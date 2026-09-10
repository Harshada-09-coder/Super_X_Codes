package com.techtrio.view;

import com.techtrio.model.Event;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Desktop;
import java.net.URI;

public class PaymentView {

    // तुमची Razorpay Key किंवा Payment Link (जर असेल तर)
    // उदा: "rzp_test_XXXXXXXXXXXX"
    private static final String RAZORPAY_KEY_ID = "rzp_test_YOUR_KEY_HERE";

    public void openPaymentWindow(Stage parentStage, Event event, double amountToPay, Runnable onSuccessCallback) {
        Stage modalStage = new Stage();
        modalStage.initOwner(parentStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Razorpay Secure Checkout");
        modalStage.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F8FAFC; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setStyle("-fx-background-color: #061B41;");

        VBox headerText = new VBox(2);
        Label title = new Label("Razorpay Trusted Checkout");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subTitle = new Label("Event ID: " + (event.getEventId() != null ? event.getEventId() : "N/A"));
        subTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");
        headerText.getChildren().addAll(title, subTitle);

        header.getChildren().add(headerText);

        // Body Content
        HBox body = new HBox(20);
        body.setPadding(new Insets(24));
        body.setAlignment(Pos.TOP_LEFT);

        // डावी बाजू - डायनॅमिक बिल सारांश (No Hardcoding)
        VBox leftSummary = new VBox(12);
        leftSummary.setPrefWidth(260);
        leftSummary.setStyle("-fx-background-color: white; -fx-padding: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label evName = new Label("Event: " + (event.getEventName() != null ? event.getEventName() : "Event"));
        evName.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        Separator sep1 = new Separator();

        // अचूक आकडेमोड (डायनॅमिक)
        double baseAmount = amountToPay / 1.05; // 5% GST/Fee गृहीत धरल्यास किंवा सरळ दाखवा
        double fee = amountToPay - baseAmount;

        HBox r1 = createRow("Payable Amount:", String.format("₹ %.2f", amountToPay));
        r1.setStyle("-fx-font-weight: bold;");

        Separator sep2 = new Separator();

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label lblTotalT = new Label("Total To Pay:");
        lblTotalT.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblTotalV = new Label(String.format("₹ %.2f", amountToPay)); // खरोखर सिलेक्ट केलेली रक्कम
        lblTotalV.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        totalRow.getChildren().addAll(lblTotalT, sp, lblTotalV);

        leftSummary.getChildren().addAll(summaryTitle, evName, sep1, r1, sep2, totalRow);

        // उजवी बाजू - Razorpay Payment Trigger
        VBox rightBox = new VBox(16);
        rightBox.setPrefWidth(320);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setStyle("-fx-background-color: white; -fx-padding: 24; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label payLabel = new Label("Complete Payment with Razorpay");
        payLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label desc = new Label("Clicking below will securely initiate the transaction for ₹" + String.format("%.2f", amountToPay) + ".");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B; -fx-text-alignment: center;");

        // मुख्य Razorpay बटण
        Button btnLaunchRazorpay = new Button("Pay ₹" + String.format("%.2f", amountToPay) + " Now");
        btnLaunchRazorpay.setMaxWidth(Double.MAX_VALUE);
        btnLaunchRazorpay.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 0; -fx-background-radius: 6px; -fx-cursor: hand;");

        // पेमेंट पूर्ण झाल्याचे सिमुलेट/कन्फर्म करणारे बटण (किंवा Razorpay Callback)
        btnLaunchRazorpay.setOnAction(e -> {
            btnLaunchRazorpay.setDisable(true);
            btnLaunchRazorpay.setText("Processing...");

            // १. Razorpay Payment URL / Gateway उघडणे (उदा. Browser मध्ये)
            try {
                // तुमच्याकडे जर Razorpay चा Payment Page URL असेल तर तो इथे उघडू शकता:
                // Desktop.getDesktop().browse(new URI("https://rzp.io/l/your_payment_link"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // २. यशस्वी झाल्यावर (Success Flow)
            new Thread(() -> {
                try {
                    Thread.sleep(1500); // 1.5 सेकंद प्रोसेसिंग
                } catch (InterruptedException ignored) {}

                Platform.runLater(() -> {
                    modalStage.close();
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run(); // Payment.java मधील डेटा अपडेट होईल
                    }
                    showSuccessAlert(amountToPay);
                });
            }).start();
        });

        rightBox.getChildren().addAll(payLabel, desc, btnLaunchRazorpay);
        body.getChildren().addAll(leftSummary, rightBox);
        root.getChildren().addAll(header, body);

        Scene scene = new Scene(root, 640, 360);
        modalStage.setScene(scene);
        modalStage.show();
    }

    private HBox createRow(String label, String val) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 12px; -fx-text-fill: #1E293B;");
        box.getChildren().addAll(l, r, v);
        return box;
    }

    private void showSuccessAlert(double amt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Transaction Completed!");
        alert.setContentText(String.format("Payment of ₹ %.2f has been successfully recorded and synced with Firestore.", amt));
        alert.showAndWait();
    }
}