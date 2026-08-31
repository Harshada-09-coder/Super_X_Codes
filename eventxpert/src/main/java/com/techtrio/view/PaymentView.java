package com.techtrio.view;

import com.techtrio.controller.PaymentController;
import com.techtrio.model.Event;
import com.techtrio.model.Payment;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PaymentView {

    private final PaymentController controller = new PaymentController();

    /**
     * Open Payment Dialog Window
     */
    public void openPaymentWindow(Stage parentStage, Event event, double vendorCost, Runnable onPaymentSuccess) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("EventXpert - Secure Payment Gateway");

        double totalAmount = 0.0;
        try {
            if (event.getBudget() != null && !event.getBudget().isBlank()) {
                totalAmount = Double.parseDouble(event.getBudget().replaceAll("[^0-9.]", ""));
            }
        } catch (Exception ignored) {
            totalAmount = 10000.0; // Fallback default
        }

        if (totalAmount <= 0) totalAmount = 10000.0;

        // Calculate 3-Way Split
        Payment payment = controller.preparePaymentSummary(
                event.getEventId(),
                event.getCustomerId() != null ? event.getCustomerId() : "CUST_01",
                event.getEventName() != null ? event.getEventName() : "Customer",
                event.getOrganizerId() != null ? event.getOrganizerId() : "UNASSIGNED",
                totalAmount,
                vendorCost
        );

        // Root Container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F8FAFC;");

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(20, 25, 15, 25));
        headerBox.setStyle("-fx-background-color: #1E293B;");
        Label titleLabel = new Label("💳 EventXpert Checkout");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        Label subLabel = new Label("Event ID: " + (event.getEventId() != null ? event.getEventId() : "N/A"));
        subLabel.setTextFill(Color.web("#94A3B8"));
        subLabel.setFont(Font.font("Segoe UI", 12));
        headerBox.getChildren().addAll(titleLabel, subLabel);
        root.setTop(headerBox);

        // Center: Summary Card & Payment Forms
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));

        // 1. Left Side: Invoice Summary & Revenue Split Card
        VBox summaryCard = createSummaryCard(event, payment);
        summaryCard.setPrefWidth(280);

        // 2. Right Side: Tabs for UPI / Card Payment
        VBox paymentMethodBox = new VBox(15);
        paymentMethodBox.setPrefWidth(380);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        // UPI Tab
        VBox upiBox = new VBox(12);
        upiBox.setPadding(new Insets(15));
        upiBox.setAlignment(Pos.CENTER);
        Label upiTitle = new Label("Scan & Pay via any UPI App");
        upiTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        
        // Mock QR Box
        StackPane qrBox = new StackPane();
        qrBox.setPrefSize(140, 140);
        qrBox.setMaxSize(140, 140);
        qrBox.setStyle("-fx-background-color: #F1F5F9; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        Label qrText = new Label("⬛ ▄█▄ ⬛\n█ █ █ █ █\n⬛ ▀█▀ ⬛\n[ QR CODE ]");
        qrText.setStyle("-fx-font-family: monospace; -fx-text-alignment: center;");
        qrBox.getChildren().add(qrText);

        TextField upiField = new TextField("customer@upi");
        upiField.setPromptText("Enter UPI ID (e.g. name@okaxis)");
        upiField.setStyle("-fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 4;");

        upiBox.getChildren().addAll(upiTitle, qrBox, new Label("Or Enter UPI ID:"), upiField);
        Tab upiTab = new Tab("📱 UPI / QR", upiBox);

        // Card Tab
        VBox cardBox = new VBox(10);
        cardBox.setPadding(new Insets(15));
        TextField cardNumField = new TextField("4532 8921 7843 9021");
        cardNumField.setPromptText("Card Number (16 digits)");
        cardNumField.setStyle("-fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 4;");

        HBox cardRow = new HBox(10);
        TextField expiryField = new TextField("12/28");
        expiryField.setPromptText("MM/YY");
        expiryField.setPrefWidth(120);
        expiryField.setStyle("-fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 4;");

        PasswordField cvvField = new PasswordField();
        cvvField.setText("892");
        cvvField.setPromptText("CVV (3 digits)");
        cvvField.setPrefWidth(120);
        cvvField.setStyle("-fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 4;");
        cardRow.getChildren().addAll(expiryField, cvvField);

        cardBox.getChildren().addAll(
                new Label("Card Number:"), cardNumField,
                new Label("Expiry & CVV:"), cardRow
        );
        Tab cardTab = new Tab("💳 Debit / Credit Card", cardBox);

        tabPane.getTabs().addAll(upiTab, cardTab);

        // Pay Button & Loading Indicator
        Button payButton = new Button("Pay ₹" + String.format("%.2f", payment.getTotalAmount()));
        payButton.setMaxWidth(Double.MAX_VALUE);
        payButton.setPrefHeight(42);
        payButton.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setVisible(false);
        spinner.setMaxSize(28, 28);

        payButton.setOnAction(e -> {
            boolean isUpi = tabPane.getSelectionModel().getSelectedItem() == upiTab;
            String method = isUpi ? "UPI" : "CARD";

            // OTP Verification Prompt
            TextInputDialog otpDialog = new TextInputDialog("123456");
            otpDialog.setTitle("Bank OTP Verification");
            otpDialog.setHeaderText("Enter 6-Digit OTP sent to registered mobile");
            otpDialog.setContentText("OTP (Demo: 123456):");

            otpDialog.showAndWait().ifPresent(otp -> {
                if (!controller.verifyOtp(otp)) {
                    showAlert(Alert.AlertType.ERROR, "Payment Failed", "Invalid OTP entered. Please try again.");
                    return;
                }

                // Simulate Network Processing
                payButton.setDisable(true);
                spinner.setVisible(true);

                PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
                pause.setOnFinished(evt -> {
                    boolean success = controller.processPayment(payment, method);
                    spinner.setVisible(false);
                    payButton.setDisable(false);

                    if (success) {
                        showSuccessReceipt(payment, dialog, onPaymentSuccess);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Transaction Error", "Could not process payment to database.");
                    }
                });
                pause.play();
            });
        });

        HBox btnRow = new HBox(10, payButton, spinner);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        paymentMethodBox.getChildren().addAll(tabPane, btnRow);
        mainContent.getChildren().addAll(summaryCard, paymentMethodBox);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 700, 480);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    /**
     * Summary Card with 3-Way Split Breakdown
     */
    private VBox createSummaryCard(Event event, Payment payment) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        Label heading = new Label("Order Summary");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        Label eventName = new Label("Event: " + (event.getEventName() != null ? event.getEventName() : "Event Booking"));
        eventName.setStyle("-fx-text-fill: #475569;");

        Separator sep = new Separator();

        // Breakdown items
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        grid.add(new Label("Platform Fee (5%):"), 0, 0);
        grid.add(new Label("₹" + String.format("%.2f", payment.getPlatformFee())), 1, 0);

        grid.add(new Label("Vendor Cost:"), 0, 1);
        grid.add(new Label("₹" + String.format("%.2f", payment.getVendorAmount())), 1, 1);

        grid.add(new Label("Organizer Share:"), 0, 2);
        grid.add(new Label("₹" + String.format("%.2f", payment.getOrganizerAmount())), 1, 2);

        Separator sep2 = new Separator();

        HBox totalRow = new HBox();
        Label totalLbl = new Label("Total Payable:");
        totalLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label totalVal = new Label("₹" + String.format("%.2f", payment.getTotalAmount()));
        totalVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        totalVal.setTextFill(Color.web("#2563EB"));
        totalRow.getChildren().addAll(totalLbl, spacer, totalVal);

        box.getChildren().addAll(heading, eventName, sep, grid, sep2, totalRow);
        return box;
    }

    /**
     * Show Success Receipt Dialog
     */
    private void showSuccessReceipt(Payment payment, Stage currentDialog, Runnable onPaymentSuccess) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("🎉 Payment Completed Successfully!");
        alert.setContentText(
                "Transaction ID: " + payment.getTransactionId() + "\n" +
                "Payment ID: " + payment.getPaymentId() + "\n" +
                "Total Paid: ₹" + payment.getTotalAmount() + "\n" +
                "Event Status: CONFIRMED / PAID\n\n" +
                "Platform Revenue: ₹" + payment.getPlatformFee() + "\n" +
                "Organizer Revenue: ₹" + payment.getOrganizerAmount() + "\n" +
                "Vendor Earnings: ₹" + payment.getVendorAmount()
        );

        alert.showAndWait();
        currentDialog.close();

        if (onPaymentSuccess != null) {
            onPaymentSuccess.run();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
