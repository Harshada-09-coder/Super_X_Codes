package com.techtrio.view;

import com.razorpay.PaymentLink;
import com.techtrio.model.Event;
import com.techtrio.services.RazorpayService;
import com.techtrio.util.QRCodeUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.UUID;

public class PaymentView {

    private final RazorpayService razorpayService = new RazorpayService();

    /**
     * थेट ही एकच मेथड कॉल करून Razorpay विंडो उघडायची
     */
    public void openPaymentWindow(Stage parentStage, Event event, double amount, Runnable onSuccessCallback) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Razorpay Secure Payment");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Pay to Admin Account");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblAmount = new Label("Amount to Pay: ₹ " + amount);
        lblAmount.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");

        ImageView qrView = new ImageView();
        qrView.setFitWidth(180);
        qrView.setFitHeight(180);

        ProgressIndicator spinner = new ProgressIndicator();
        Label statusLbl = new Label("Generating Razorpay UPI QR & OTP Link...");

        Button btnOtpPay = new Button("Pay with Card / NetBanking (Bank OTP)");
        btnOtpPay.setDisable(true);
        btnOtpPay.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 6;");

        layout.getChildren().addAll(title, lblAmount, qrView, spinner, statusLbl, btnOtpPay);

        // बॅकग्राउंड टास्क: Razorpay लिंक बनवणे व दर २ सेकंदांनी पैसे जमा झाले का ते पाहणे
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String pId = "PAY_" + UUID.randomUUID().toString().substring(0, 8);
                
                // Razorpay API कॉल
                PaymentLink link = razorpayService.createPaymentLink(pId, amount, "Customer", "customer@event.com", "9876543210");
                String payUrl = link.get("short_url");
                String linkId = link.get("id");

                // QR कोड दाखवणे
                Platform.runLater(() -> {
                    qrView.setImage(QRCodeUtil.generateQRCodeImage(payUrl, 180, 180));
                    statusLbl.setText("Scan UPI QR via PhonePe/GPay OR click below for OTP");
                    btnOtpPay.setDisable(false);
                    btnOtpPay.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().browse(new URI(payUrl));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                });

                // Real-time Polling: युझरने पेमेंट केले का ते तपासणे
                boolean isPaid = false;
                for (int i = 0; i < 150; i++) { // 5 मिनिटे वेळ
                    Thread.sleep(2000);
                    isPaid = razorpayService.checkPaymentStatus(linkId);
                    if (isPaid) break;
                }

                boolean success = isPaid;
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    if (success) {
                        statusLbl.setText("✅ Payment Received by Admin!");
                        statusLbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment of ₹" + amount + " Successful!", ButtonType.OK);
                        alert.showAndWait();

                        dialog.close();

                        if (onSuccessCallback != null) {
                            onSuccessCallback.run();
                        }
                    } else {
                        statusLbl.setText("❌ Payment Incomplete / Timeout.");
                        statusLbl.setStyle("-fx-text-fill: red;");
                    }
                });
                return null;
            }
        };

        new Thread(task).start();

        dialog.setScene(new Scene(layout, 380, 440));
        dialog.show();
    }
}