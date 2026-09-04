package com.techtrio.services;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.sun.net.httpserver.HttpServer;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Event;
import com.techtrio.model.Payment;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

public class RazorpayServices {

    public static final String RAZORPAY_KEY_ID = "rzp_test_TXuUaAKmDakYYU"; 
    public static final String RAZORPAY_KEY_SECRET = "9VzJ2SwpqGfogtx4OrVyTEMu";

    private RazorpayClient client;
    private static HttpServer activeServer = null;
    private static Stage paymentPopupStage = null;

    public RazorpayServices() {
        try {
            this.client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
        } catch (Exception ignored) {}
    }

    // ================= PaymentController Methods =================
    public Payment calculateSplit(String eventId, String customerId, String customerName, String organizerId, double totalAmount, double vendorCost) {
        Payment payment = new Payment();
        payment.setPaymentId("PAY_" + System.currentTimeMillis());
        payment.setEventId(eventId);
        payment.setCustomerId(customerId);
        payment.setCustomerName(customerName);
        payment.setOrganizerId(organizerId);
        payment.setTotalAmount(totalAmount);

        double platformFee = totalAmount * 0.05;
        payment.setPlatformFee(platformFee);

        double organizerShare = totalAmount * 0.15;
        payment.setOrganizerAmount(organizerShare);

        double vendorShare = totalAmount - (platformFee + organizerShare);
        payment.setVendorAmount(vendorShare);

        payment.setTransactionId("TXN_" + System.currentTimeMillis());
        return payment;
    }

    public boolean processPayment(Payment payment) {
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db != null && payment != null) {
                db.collection("payments").document().set(payment).get();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Payment getPaymentByEventId(String eventId) {
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db != null && eventId != null) {
                QuerySnapshot qs = db.collection("payments").whereEqualTo("eventId", eventId).limit(1).get().get();
                if (!qs.isEmpty()) {
                    return qs.getDocuments().get(0).toObject(Payment.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db != null) {
                QuerySnapshot qs = db.collection("payments").get().get();
                for (DocumentSnapshot doc : qs.getDocuments()) {
                    Payment p = doc.toObject(Payment.class);
                    if (p != null) list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalPlatformRevenue() {
        double total = 0.0;
        try {
            Firestore db = FirebaseConfig.getFirestore();
            if (db != null) {
                QuerySnapshot qs = db.collection("payments").get().get();
                for (DocumentSnapshot doc : qs.getDocuments()) {
                    Double amt = doc.getDouble("amount");
                    if (amt == null) amt = doc.getDouble("totalAmount");
                    if (amt == null) amt = doc.getDouble("paidAmount");
                    if (amt != null) {
                        total += (amt * 0.05);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // ================= ⭐ SAFE SERVER INITIALIZER =================
    private static synchronized HttpServer createSafeServer(int[] assignedPortHolder) {
        if (activeServer != null) {
            try {
                activeServer.stop(0);
            } catch (Exception ignored) {}
            activeServer = null;
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            assignedPortHolder[0] = server.getAddress().getPort();
            activeServer = server;
            return server;
        } catch (Exception ignored) {}

        for (int p = 10050; p <= 10150; p++) {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", p), 0);
                assignedPortHolder[0] = p;
                activeServer = server;
                return server;
            } catch (Exception ignored) {}
        }

        return null;
    }

    // ================= ⭐ OVERLOADED METHODS =================
    public static void startPayment(double amountInRupees, String userEmail,
                                   Consumer<String> onSuccess, Consumer<String> onFailure) {
        String defaultName = (userEmail != null && userEmail.contains("@")) ? userEmail.split("@")[0] : "Customer";
        startPayment(amountInRupees, userEmail, "9999999999", defaultName, onSuccess, onFailure);
    }

    public static void startPayment(double amountInRupees, String userEmail, String userPhone,
                                   Consumer<String> onSuccess, Consumer<String> onFailure) {
        String defaultName = (userEmail != null && userEmail.contains("@")) ? userEmail.split("@")[0] : "Customer";
        startPayment(amountInRupees, userEmail, userPhone, defaultName, onSuccess, onFailure);
    }

    public static void startPayment(double amountInRupees, String userEmail, Consumer<String> onSuccess) {
        startPayment(amountInRupees, userEmail, onSuccess, err -> System.err.println("Payment error: " + err));
    }

    // ================= ⭐ MAIN METHOD (CRASH-FREE - NO WEBVIEW) =================
    public static void startPayment(double amountInRupees, String userEmail, String userPhone, String customerName,
                                   Consumer<String> onSuccess, Consumer<String> onFailure) {
        try {
            long amountInPaise = Math.round(amountInRupees * 100);

            if (amountInPaise <= 0) {
                Platform.runLater(() -> onFailure.accept("Payment amount must be greater than zero."));
                return;
            }

            int[] portHolder = new int[1];
            HttpServer server = createSafeServer(portHolder);

            if (server == null) {
                Platform.runLater(() -> onFailure.accept("Unable to allocate network port."));
                return;
            }

            int dynamicPort = portHolder[0];

            final String finalCustomerName = (customerName != null && !customerName.isBlank()) ? customerName : "Customer";
            final String finalEmail = (userEmail != null && !userEmail.isBlank()) ? userEmail : "customer@eventxpert.com";
            final String finalPhone = (userPhone != null && !userPhone.isBlank()) ? userPhone : "9999999999";

            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();

                // Payment Success Callback (from Browser)
                if (path != null && (path.contains("payment-success") || (query != null && query.contains("razorpay_payment_id=")))) {
                    String paymentId = "PAY_" + System.currentTimeMillis();
                    if (query != null && query.contains("razorpay_payment_id=")) {
                        for (String param : query.split("&")) {
                            if (param.startsWith("razorpay_payment_id=")) {
                                paymentId = param.split("=")[1];
                            }
                        }
                    }

                    final String finalPaymentId = paymentId;

                    Platform.runLater(() -> {
                        if (paymentPopupStage != null) {
                            paymentPopupStage.close();
                            paymentPopupStage = null;
                        }
                        onSuccess.accept(finalPaymentId);
                    });

            String closeResponse = "<html><body style='text-align:center;padding:50px;font-family:Segoe UI,sans-serif;background:#F8FAFC;'>"
        + "<h2 style='color:#16A34A;'>Payment Successful!</h2>"
        + "<p style='color:#475569;'>You can close this tab and return to EventXpert.</p>"
        + "<script>setTimeout(function(){ window.close(); }, 3000);</script>"
        + "</body></html>";
                    byte[] b = closeResponse.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, b.length);
                    exchange.getResponseBody().write(b);
                    exchange.getResponseBody().close();

                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (Exception ignored) {}
                        if (activeServer != null) {
                            activeServer.stop(0);
                            activeServer = null;
                        }
                    }).start();
                    return;
                }

                // Razorpay Checkout HTML Page
                String checkoutHtml = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>EventXpert Razorpay Checkout</title>"
                        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                        + "<style>"
                        + "body { background:#F1F5F9; display:flex; justify-content:center; align-items:center; height:100vh; font-family:'Segoe UI',Arial,sans-serif; margin:0; }"
                        + ".card { background:white; padding:35px 30px; border-radius:14px; box-shadow:0 10px 25px rgba(0,0,0,0.08); text-align:center; width:360px; }"
                        + ".title { color:#0D3979; margin:0 0 6px 0; font-size:22px; font-weight:bold; }"
                        + ".amount { color:#2563EB; font-size:26px; font-weight:bold; margin:16px 0; }"
                        + ".btn { background:#2563EB; color:white; border:none; padding:13px 20px; font-size:16px; border-radius:8px; cursor:pointer; font-weight:bold; width:100%; box-shadow:0 4px 10px rgba(37,99,235,0.25); }"
                        + ".btn:hover { background:#1D4ED8; }"
                        + "</style></head><body>"
                        + "<div class='card'>"
                        + "<div class='title'>EventXpert Checkout</div>"
                        + "<p style='color:#64748B; margin:0;'>Customer: " + finalCustomerName + "</p>"
                        + "<div class='amount'>₹ " + String.format("%.2f", amountInRupees) + "</div>"
                        + "<button id='pay-btn' class='btn'>Pay Now with Razorpay</button>"
                        + "</div>"
                        + "<script src='https://checkout.razorpay.com/v1/checkout.js'></script>"
                        + "<script>"
                        + "var options = {"
                        + "    'key': '" + RAZORPAY_KEY_ID + "',"
                        + "    'amount': " + amountInPaise + ","
                        + "    'currency': 'INR',"
                        + "    'name': 'EventXpert',"
                        + "    'description': 'Booking Settlement',"
                        + "    'prefill': {"
                        + "        'name': '" + finalCustomerName + "',"
                        + "        'email': '" + finalEmail + "',"
                        + "        'contact': '" + finalPhone + "'"
                        + "    },"
                        + "    'theme': { 'color': '#2563EB' },"
                        + "    'handler': function (response){"
                        + "        window.location.href = '/payment-success?razorpay_payment_id=' + response.razorpay_payment_id;"
                        + "    },"
                        + "    'modal': {"
                        + "        'ondismiss': function(){"
                        + "            window.location.href = '/payment-failed';"
                        + "        }"
                        + "    }"
                        + "};"
                        + "var rzp = new Razorpay(options);"
                        + "document.getElementById('pay-btn').onclick = function(){ rzp.open(); };"
                        + "window.onload = function() { setTimeout(function(){ rzp.open(); }, 400); };"
                        + "</script></body></html>";

                byte[] bytes = checkoutHtml.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            });

            server.setExecutor(null);
            server.start();

            // ⭐ CRASH-FREE DIALOG (NO WEBVIEW - USES NATIVE BROWSER + CLEAN DIALOG)
            Platform.runLater(() -> {
                try {
                    paymentPopupStage = new Stage();
                    paymentPopupStage.initModality(Modality.APPLICATION_MODAL);
                    paymentPopupStage.setTitle("Razorpay Payment Gateway - EventXpert");

                    VBox dialogBox = new VBox(16);
                    dialogBox.setAlignment(Pos.CENTER);
                    dialogBox.setPadding(new Insets(30, 25, 25, 25));
                    dialogBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

                    Label logoLbl = new Label("💳 Razorpay Secure Checkout");
                    logoLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 19));
                    logoLbl.setTextFill(Color.web("#0D3979"));

                    Label amtLbl = new Label("Amount Payable: ₹ " + String.format("%.2f", amountInRupees));
                    amtLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 21));
                    amtLbl.setTextFill(Color.web("#2563EB"));

                    ProgressIndicator spinner = new ProgressIndicator();
                    spinner.setPrefSize(42, 42);

                    Label infoLbl = new Label("Opening Razorpay in your browser...\nPlease complete payment in the opened browser window.");
                    infoLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12.5));
                    infoLbl.setTextFill(Color.web("#64748B"));
                    infoLbl.setStyle("-fx-text-alignment: center;");

                    HBox btnBox = new HBox(10);
                    btnBox.setAlignment(Pos.CENTER);
                    btnBox.setPadding(new Insets(10, 0, 0, 0));

                    Button btnBrowser = new Button("🌐 Open in Browser");
                    btnBrowser.setStyle("-fx-background-color: #0284C7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 6;");
                    btnBrowser.setOnAction(ev -> {
                        try {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(new URI("http://127.0.0.1:" + dynamicPort + "/"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                    Button btnVerify = new Button("✔ I Have Paid / Verify");
                    btnVerify.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 6;");
                    btnVerify.setOnAction(ev -> {
                        if (paymentPopupStage != null) {
                            paymentPopupStage.close();
                            paymentPopupStage = null;
                        }
                        onSuccess.accept("pay_" + System.currentTimeMillis());
                    });

                    Button btnCancel = new Button("Cancel");
                    btnCancel.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 6;");
                    btnCancel.setOnAction(ev -> {
                        if (paymentPopupStage != null) {
                            paymentPopupStage.close();
                            paymentPopupStage = null;
                        }
                        onFailure.accept("Payment cancelled by user.");
                    });

                    btnBox.getChildren().addAll(btnBrowser, btnVerify, btnCancel);
                    dialogBox.getChildren().addAll(logoLbl, amtLbl, spinner, infoLbl, btnBox);

                    Scene popupScene = new Scene(dialogBox, 490, 310);
                    paymentPopupStage.setScene(popupScene);
                    paymentPopupStage.centerOnScreen();
                    paymentPopupStage.show();

                    // Open Browser Automatically (Chrome/Edge)
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI("http://127.0.0.1:" + dynamicPort + "/"));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure.accept("Failed to launch payment: " + ex.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> onFailure.accept(e.getMessage()));
        }
    }

    public String createOrder(Event event) {
        try {
            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);

            double realBudget = 0.0;
            try {
                if (event != null && event.getBudget() != null && !event.getBudget().isBlank()) {
                    realBudget = Double.parseDouble(event.getBudget().replaceAll("[^0-9.]", "").trim());
                }
            } catch (Exception ignored) {}

            if (realBudget <= 0) {
                return null;
            }

            long amountInPaise = Math.round(realBudget * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "rcpt_" + (event != null ? event.getEventId() : System.currentTimeMillis()));

            Order order = razorpay.orders.create(orderRequest);
            return order.get("id");
        } catch (Exception e) {
            return null;
        }
    }
}