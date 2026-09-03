package com.techtrio.view;

import com.techtrio.services.EmailService;
import com.techtrio.services.RazorpayService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Random;

public class RazorpayPaymentDialog {

    private String generatedOtp;
    private final RazorpayService razorpayService = new RazorpayService();
    private final EmailService emailService = new EmailService();

    public void showPaymentDialog(String eventId, String customerEmail, String customerId, 
                                  String organizerId, String vendorId, double totalAmount, 
                                  double vendorAmount, Runnable onSuccessCallback) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Razorpay Secure Checkout & Verification");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 10;");

        Label title = new Label("Secure Payment Checkout");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#00d2d3"));

        Label lblAmount = new Label("Total Payable: ₹ " + String.format("%.2f", totalAmount));
        lblAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblAmount.setTextFill(Color.WHITE);

        // Split Summary
        double platformFee = totalAmount * 0.05;
        double organizerShare = totalAmount - (platformFee + vendorAmount);
        Label lblSplit = new Label(String.format("Split: Vendors: ₹%.0f | Org: ₹%.0f | Platform: ₹%.0f", 
                                  vendorAmount, organizerShare, platformFee));
        lblSplit.setFont(Font.font("Segoe UI", 11));
        lblSplit.setTextFill(Color.web("#a4b0be"));

        // Step 1: Send OTP Button
        Button btnSendOtp = new Button("Send Payment OTP to Email");
        btnSendOtp.setStyle("-fx-background-color: #5f27cd; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");

        Label lblOtpMsg = new Label();
        lblOtpMsg.setTextFill(Color.web("#1dd1a1"));

        // OTP Input
        TextField txtOtp = new TextField();
        txtOtp.setPromptText("Enter 6-Digit OTP");
        txtOtp.setMaxWidth(180);
        txtOtp.setDisable(true);
        txtOtp.setAlignment(Pos.CENTER);

        Button btnVerifyAndPay = new Button("Verify OTP & Pay Now");
        btnVerifyAndPay.setDisable(true);
        btnVerifyAndPay.setStyle("-fx-background-color: #10ac84; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        // OTP Logic
        btnSendOtp.setOnAction(e -> {
            generatedOtp = String.format("%06d", new Random().nextInt(999999));
            String emailBody = "Dear Customer,\n\nYour OTP for EventXpert Payment of Rs. " 
                             + totalAmount + " is: " + generatedOtp 
                             + "\n\nDo not share this with anyone.";
             new Thread(() -> {
            try {
                emailService.sendEmail(customerEmail, emailBody);
                System.out.println("[EventXpert] OTP sent successfully to: " + customerEmail);
            } catch (Exception ex) {
                System.err.println("[EventXpert] Email sending failed: " + ex.getMessage());
                // ईमेल नाही गेला तरी टेस्टिंगसाठी टर्मिनलवर OTP दिसेल:
                System.out.println("👉 [DEBUG/TEST OTP]: " + generatedOtp);
            }
        }).start();

            lblOtpMsg.setText("OTP sent to " + customerEmail);
            txtOtp.setDisable(false);
            btnVerifyAndPay.setDisable(false);
            btnSendOtp.setDisable(true);
        });

        // Verify & Pay Logic
        btnVerifyAndPay.setOnAction(e -> {
            String enteredOtp = txtOtp.getText().trim();
            if (!enteredOtp.equals(generatedOtp)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid OTP! Please check your email.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            btnVerifyAndPay.setDisable(true);
            btnVerifyAndPay.setText("Processing Transaction...");

            new Thread(() -> {
                try {
                    // १. Create Razorpay Order
                    String orderId = razorpayService.createOrder(totalAmount, "rcpt_" + System.currentTimeMillis());
                    String simulatedPaymentId = "pay_rzp_" + System.currentTimeMillis();

                    // २. Execute Real-Time Distribution
                    boolean success = razorpayService.processRealtimeDistribution(
                            eventId, customerId, organizerId, vendorId,
                            totalAmount, vendorAmount, simulatedPaymentId, orderId
                    );

                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                                "Payment of ₹" + totalAmount + " Successful!\nRazorpay ID: " + simulatedPaymentId 
                                + "\nFunds distributed to Organizer, Vendor & Admin.", ButtonType.OK);
                            alert.showAndWait();
                            dialog.close();
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Payment processing failed!", ButtonType.OK);
                            alert.showAndWait();
                            btnVerifyAndPay.setDisable(false);
                            btnVerifyAndPay.setText("Verify OTP & Pay Now");
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Razorpay Error: " + ex.getMessage(), ButtonType.OK);
                        alert.showAndWait();
                    });
                }
            }).start();
        });

        layout.getChildren().addAll(title, lblAmount, lblSplit, btnSendOtp, lblOtpMsg, txtOtp, btnVerifyAndPay);

        Scene scene = new Scene(layout, 420, 360);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}