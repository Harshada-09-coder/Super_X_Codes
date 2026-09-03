package com.techtrio.services;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.sun.net.httpserver.HttpServer;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Payment;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;

public class RazorpayServices {

    public static final String RAZORPAY_KEY_ID = "rzp_test_TXeE7v7RXcBP3P"; 
    public static final String RAZORPAY_KEY_SECRET = "9VzJ2SwpqGfogtx4OrVyTEMu";

    private RazorpayClient client;
    
    // ⭐ जुना सर्व्हर ट्रॅक आणि क्लीनअप करण्यासाठी
    private static HttpServer activeServer = null;

    public RazorpayServices() {
        try {
            this.client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
        } catch (Exception ignored) {}
    }

    // ================= PaymentController Methods =================

    public Payment calculateSplit(String eventId, String organizerId, String vendorId, String customerId, double amount, double platformFeeRate) {
        Payment payment = new Payment();
        payment.setPaymentId("SPLIT_" + System.currentTimeMillis());
        payment.setEventId(eventId);
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

    // ================= ⭐ BULLETPROOF SERVER INITIALIZER =================

    private static synchronized HttpServer createSafeServer(int[] assignedPortHolder) {
        // १. आधीचा कोणताही जुना सर्व्हर चालू असल्यास त्याला त्वरित बंद करा
        if (activeServer != null) {
            try {
                activeServer.stop(0);
            } catch (Exception ignored) {}
            activeServer = null;
        }

        // २. 127.0.0.1 वर डायनॅमिक पोर्ट 0 ने तयार करण्याचा प्रयत्न (OS स्वतः रिकामा पोर्ट देईल)
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            assignedPortHolder[0] = server.getAddress().getPort();
            activeServer = server;
            return server;
        } catch (Exception ignored) {}

        // ३. जर पोर्ट 0 ला सिस्टिमने नकार दिला तर 10050 ते 10150 मधील रिकामा पोर्ट शोधणे
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

    // ================= ⭐ RAZORPAY CHECKOUT =================

    public static void startPayment(double amountInRupees, String userEmail, String userPhone,
                                   Consumer<String> onSuccess, Consumer<String> onFailure) {
        try {
            int amountInPaise = (int) Math.round(amountInRupees * 100);

            // Razorpay Order ID (Test mode works even if blank)
            String orderId = "";
            try {
                RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
                JSONObject orderReq = new JSONObject();
                orderReq.put("amount", amountInPaise);
                orderReq.put("currency", "INR");
                orderReq.put("receipt", "txn_" + System.currentTimeMillis());
                Order order = razorpay.orders.create(orderReq);
                orderId = order.get("id");
            } catch (Exception ex) {
                orderId = ""; 
            }

            // सुरक्षित लोकल सर्व्हर मिळवणे
            int[] portHolder = new int[1];
            HttpServer server = createSafeServer(portHolder);
            
            if (server == null) {
                javafx.application.Platform.runLater(() -> onFailure.accept("Unable to allocate free network port. Please restart the app."));
                return;
            }

            int dynamicPort = portHolder[0];

            server.createContext("/payment-success", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                String paymentId = "PAY_" + System.currentTimeMillis();
                if (query != null && query.contains("razorpay_payment_id=")) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("razorpay_payment_id=")) {
                            paymentId = param.split("=")[1];
                        }
                    }
                }

                // ⭐ ब्राऊजरवर कोणतीही ID दिसणार नाही, हा टॅब लगेच स्वतःहून बंद होईल:
                String response = "<!DOCTYPE html><html><head><title>Processing...</title>"
                        + "<script>"
                        + "window.onload = function() {"
                        + "    window.close();"
                        + "    setTimeout(function() { window.close(); }, 300);"
                        + "};"
                        + "</script></head>"
                        + "<body style='background:#f8fafc;text-align:center;padding-top:80px;font-family:Segoe UI,sans-serif;'>"
                        + "<h2 style='color:#16A34A;'>✓ Payment Successful!</h2>"
                        + "<p style='color:#64748B;'>Redirecting back to EventXpert...</p>"
                        + "<script>window.close();</script>"
                        + "</body></html>";

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

                server.stop(0);
                activeServer = null;

                final String finalPaymentId = paymentId;
                javafx.application.Platform.runLater(() -> onSuccess.accept(finalPaymentId));
            });

     

            server.setExecutor(null);
            server.start();

            // Checkout HTML फाईल तयार करून ब्राउझरमध्ये उघडणे
            File htmlFile = File.createTempFile("eventxpert_rzp", ".html");
            FileWriter writer = new FileWriter(htmlFile);

            String orderField = orderId.isEmpty() ? "" : "'order_id': '" + orderId + "',";

            writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>EventXpert Checkout</title></head>"
                    + "<body style='background:#f4f6f9;text-align:center;padding-top:80px;font-family:Segoe UI,Arial;'>"
                    + "<div style='max-width:450px;margin:auto;background:white;padding:30px;border-radius:10px;box-shadow:0 4px 15px rgba(0,0,0,0.08);'>"
                    + "<h2 style='color:#0D3979;'>Connecting to Razorpay...</h2>"
                    + "<p style='color:#64748B;'>Please complete the payment in the popup window.</p>"
                    + "</div>"
                    + "<script src='https://checkout.razorpay.com/v1/checkout.js'></script>"
                    + "<script>"
                    + "var options = {"
                    + "    'key': '" + RAZORPAY_KEY_ID + "',"
                    + "    'amount': " + amountInPaise + ","
                    + "    'currency': 'INR',"
                    + "    'name': 'EventXpert',"
                    + "    'description': 'Event Booking Payment',"
                    +      orderField
                    + "    'prefill': { 'email': '" + userEmail + "', 'contact': '" + userPhone + "' },"
                    + "    'handler': function (response){"
                    + "        window.location.href = 'http://127.0.0.1:" + dynamicPort + "/payment-success?razorpay_payment_id=' + response.razorpay_payment_id;"
                    + "    },"
                    + "    'modal': {"
                    + "        'ondismiss': function(){"
                    + "            alert('Payment window closed.');"
                    + "        }"
                    + "    }"
                    + "};"
                    + "var rzp = new Razorpay(options);"
                    + "window.onload = function() { rzp.open(); };"
                    + "</script></body></html>");
            writer.close();

            Desktop.getDesktop().browse(htmlFile.toURI());

        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> onFailure.accept(e.getMessage()));
        }
    }
}