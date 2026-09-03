package com.techtrio.view;

import com.google.cloud.firestore.Firestore;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Event;
import com.techtrio.services.PaymentService;
import com.techtrio.services.RazorpayServices;
import com.techtrio.session.UserSession;
import com.techtrio.util.OtpHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment {

    private String userId;
    private String eventId;
    private Event event;

    private double totalPayable = 0.0;
    private double totalPaid = 0.0;
    private double pendingAmount = 0.0;
    private String eventTitle = "Event";

    private Label lblTotalPayable;
    private Label lblTotalPaid;
    private Label lblPendingAmount;
    private Label lblStatus;
    private Button btnPendingPayNow;
    private Button btnDownloadReceipt;

    public Payment() {
    }

    // Constructor required by Budget.java, Guest.java and MyEventDetails.java
    public Payment(String param1, String param2, Event event) {
        this.userId = param1;
        this.eventId = param2;
        this.event = event;
        if (event != null && event.getEventId() != null) {
            this.eventId = event.getEventId();
        }
    }

    public Scene getPaymentScene(Runnable onBack) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Top Bar
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button btnBack = new Button("← Back");
        btnBack.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Label lblHeader = new Label("Event Payment & Invoicing");
        lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblHeader.setTextFill(Color.web("#2c3e50"));
        topBar.getChildren().addAll(btnBack, lblHeader);
        root.setTop(topBar);

        // Center Content Cards
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(25, 0, 0, 0));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Event Info Card
        VBox infoCard = new VBox(10);
        infoCard.setPadding(new Insets(15));
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        String titleStr = (event != null && event.getEventName() != null) ? event.getEventName() : "Event ID: " + eventId;
        Label lblEventName = new Label("Event: " + titleStr);
        lblEventName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        this.lblStatus = new Label("Status: Loading...");
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        infoCard.getChildren().addAll(lblEventName, lblStatus);

        // Financials Grid
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        lblTotalPayable = new Label("₹ 0.00");
        lblTotalPayable.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTotalPayable.setTextFill(Color.web("#2980b9"));

        lblTotalPaid = new Label("₹ 0.00");
        lblTotalPaid.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTotalPaid.setTextFill(Color.web("#27ae60"));

        lblPendingAmount = new Label("₹ 0.00");
        lblPendingAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblPendingAmount.setTextFill(Color.web("#c0392b"));

        grid.add(new Label("Total Payable:"), 0, 0);
        grid.add(lblTotalPayable, 1, 0);
        grid.add(new Label("Total Amount Paid:"), 0, 1);
        grid.add(lblTotalPaid, 1, 1);
        grid.add(new Label("Pending Balance:"), 0, 2);
        grid.add(lblPendingAmount, 1, 2);

        // Action Buttons Box
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        btnPendingPayNow = new Button("Pay Remaining Balance");
        btnPendingPayNow.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 6; -fx-cursor: hand;");
        btnPendingPayNow.setVisible(false);
        btnPendingPayNow.setManaged(false);
        btnPendingPayNow.setOnAction(e -> handlePendingPayment());

        btnDownloadReceipt = new Button("Download Receipt");
        btnDownloadReceipt.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 6; -fx-cursor: hand;");
        btnDownloadReceipt.setDisable(true);
        btnDownloadReceipt.setOnAction(e -> generateReceipt());

        buttonBox.getChildren().addAll(btnPendingPayNow, btnDownloadReceipt);
        contentBox.getChildren().addAll(infoCard, grid, buttonBox);
        root.setCenter(contentBox);

        // Start Real-time Data Sync with Firestore
        String effectiveEventId = (eventId != null && !eventId.isBlank()) ? eventId : (event != null ? event.getEventId() : null);
        if (effectiveEventId != null) {
            setupRealTimeListener(effectiveEventId);
        }

        return new Scene(root, 850, 600);
    }

    private void setupRealTimeListener(String eId) {
        this.eventId = eId;
        Firestore db = FirebaseConfig.getFirestore();
        if (db == null) return;

        db.collection("events").document(eId).addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null || !snapshot.exists()) return;

            Platform.runLater(() -> {
                Double budget = snapshot.getDouble("totalBudget");
                Double paid = snapshot.getDouble("paidAmount");
                String title = snapshot.getString("eventName");
                if (title == null) title = snapshot.getString("title");
                String status = snapshot.getString("status");

                this.totalPayable = (budget != null) ? budget : (event != null ? event.getAllocatedBudget() : 0.0);
                this.totalPaid = (paid != null) ? paid : 0.0;
                this.pendingAmount = Math.max(0, this.totalPayable - this.totalPaid);
                if (title != null) this.eventTitle = title;

                lblTotalPayable.setText("₹ " + String.format("%.2f", totalPayable));
                lblTotalPaid.setText("₹ " + String.format("%.2f", totalPaid));
                lblPendingAmount.setText("₹ " + String.format("%.2f", pendingAmount));
                lblStatus.setText("Status: " + (status != null ? status : "APPROVED"));

                // Show Pay Now if Pending > 0
                if (pendingAmount > 0) {
                    btnPendingPayNow.setVisible(true);
                    btnPendingPayNow.setManaged(true);
                } else {
                    btnPendingPayNow.setVisible(false);
                    btnPendingPayNow.setManaged(false);
                }

                // Enable receipt if Paid > 0
                btnDownloadReceipt.setDisable(totalPaid <= 0);
            });
        });
    }

    private void handlePendingPayment() {
        if (pendingAmount <= 0) return;

        String userEmail = UserSession.getUserEmail();
        if (userEmail == null || userEmail.isBlank()) {
            userEmail = "customer@eventxpert.com";
        }

        boolean verified = OtpHelper.sendAndVerifyOtp(userEmail);
        if (!verified) return;

        double payNowAmt = pendingAmount;
        RazorpayServices.startPayment(payNowAmt, userEmail, "9999999999",
            paymentId -> {
                try {
                    // ⭐ FIX: थेट PaymentService द्वारे रेकॉर्ड सेव्ह व वाटप केले
                    new PaymentService().processPaymentAndDistribute(null, eventId, payNowAmt);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Payment Successful!\nTransaction ID: " + paymentId);
                    success.showAndWait();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            },
            err -> {
                Alert fail = new Alert(Alert.AlertType.ERROR);
                fail.setTitle("Payment Failed");
                fail.setContentText("Error: " + err);
                fail.showAndWait();
            }
        );
    }

    // Zero-dependency clean Printable Receipt generator (HTML/Browser Print)
    private void generateReceipt() {
        try {
            String userHome = System.getProperty("user.home");
            String fileName = "EventXpert_Receipt_" + eventId + ".html";
            File receiptFile = new File(userHome + "/Downloads/" + fileName);

            String formattedDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date());
            String email = UserSession.getUserEmail() != null ? UserSession.getUserEmail() : "Customer";

            FileWriter writer = new FileWriter(receiptFile);
            writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Payment Receipt</title>"
                    + "<style>"
                    + "body { font-family: 'Segoe UI', Arial, sans-serif; background:#f4f6f9; padding: 40px; }"
                    + ".receipt-card { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }"
                    + ".header { text-align: center; border-bottom: 2px solid #e2e8f0; padding-bottom: 20px; }"
                    + ".header h1 { color: #0D3979; margin: 0; font-size: 26px; }"
                    + ".row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f1f5f9; }"
                    + ".total-row { display: flex; justify-content: space-between; padding: 15px 0; font-weight: bold; font-size: 18px; color: #16a34a; }"
                    + ".footer { text-align: center; margin-top: 25px; color: #64748b; font-size: 13px; }"
                    + ".btn-print { background: #0D3979; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; margin-top: 15px; }"
                    + "</style></head><body>"
                    + "<div class='receipt-card'>"
                    + "<div class='header'><h1>EventXpert Official Receipt</h1><p>Event Planning & Management Platform</p></div>"
                    + "<div class='row'><span>Receipt Date:</span><b>" + formattedDate + "</b></div>"
                    + "<div class='row'><span>Event Name:</span><b>" + eventTitle + "</b></div>"
                    + "<div class='row'><span>Event ID:</span><b>" + eventId + "</b></div>"
                    + "<div class='row'><span>Customer Email:</span><b>" + email + "</b></div>"
                    + "<div class='row'><span>Total Event Budget:</span><b>₹ " + String.format("%.2f", totalPayable) + "</b></div>"
                    + "<div class='total-row'><span>Total Amount Paid:</span><span>₹ " + String.format("%.2f", totalPaid) + "</span></div>"
                    + "<div class='row'><span>Pending Balance:</span><b>₹ " + String.format("%.2f", pendingAmount) + "</b></div>"
                    + "<div class='row'><span>Payment Status:</span><b>" + (pendingAmount <= 0 ? "PAID IN FULL" : "PARTIALLY PAID") + "</b></div>"
                    + "<div class='footer'><p>Thank you for choosing EventXpert!</p>"
                    + "<button class='btn-print' onclick='window.print()'>Print / Save as PDF</button></div>"
                    + "</div></body></html>");
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Receipt Ready");
            alert.setHeaderText(null);
            alert.setContentText("Receipt saved successfully in your Downloads folder!\nOpening receipt...");
            alert.showAndWait();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(receiptFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}