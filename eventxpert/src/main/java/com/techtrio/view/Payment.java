package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QuerySnapshot;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Event;
import com.techtrio.services.OtpEmailService;
import com.techtrio.services.RazorpayServices;
import com.techtrio.session.UserSession;
import com.techtrio.util.OtpHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Payment {

    private String userId;
    private String eventId;
    private Event event;

    private double totalPayable = 0.0;
    private double totalPaid = 0.0;
    private double pendingAmount = 0.0;
    private String eventTitle = "Event";

    // Assignment Flags
    private boolean isOrganizerAssigned = false;
    private boolean isVendorAssigned = false;

    // Dynamic UI Labels
    private Label lblTotalPayable;
    private Label lblTotalPaid;
    private Label lblPendingAmount;
    private Label lblPaidPercent;
    private Label lblPendingPercent;
    private ProgressBar pbPaid;
    private ProgressBar pbPending;

    // History Table Elements
    private VBox historyContainer;

    private ListenerRegistration eventListener;
    private Runnable onBackAction;

    public Payment() {}

    public Payment(String param1, String param2, Event event) {
        this.userId = param1;
        this.eventId = param2;
        this.event = event;
        if (event != null && event.getEventId() != null) {
            this.eventId = event.getEventId();
        }
    }

    public Scene getPaymentScene(Runnable onBack) {
        this.onBackAction = onBack;

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #F8FAFC;");

        // ================= 1. LEFT SIDEBAR (Dark Blue as in Image) =================
        VBox sidebar = buildSidebar(onBack);
        mainLayout.setLeft(sidebar);

        // ================= 2. RIGHT MAIN CONTENT AREA =================
        VBox rightContent = new VBox(22);
        rightContent.setPadding(new Insets(25, 40, 30, 40));
        rightContent.setStyle("-fx-background-color: #F8FAFC;");

        // Top Navigation & Page Title
        VBox headerBox = new VBox(4);
        Button btnBackToEvents = new Button("← Back to My Events");
        btnBackToEvents.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: 600; -fx-cursor: hand; -fx-padding: 0 0 10 0; -fx-font-size: 13px;");
        btnBackToEvents.setOnAction(e -> {
            detachListener();
            if (onBack != null) onBack.run();
        });

        Label lblPageTitle = new Label("Payments");
        lblPageTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        lblPageTitle.setTextFill(Color.web("#0F172A"));

        Label lblSub = new Label("Track payments and due amounts");
        lblSub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblSub.setTextFill(Color.web("#64748B"));

        headerBox.getChildren().addAll(btnBackToEvents, lblPageTitle, lblSub);

        // ================= 3. THREE TOP SUMMARY CARDS =================
        HBox cardsBox = new HBox(18);
        cardsBox.setAlignment(Pos.CENTER);

        // Card 1: Total Payable
        VBox card1 = new VBox(10);
        card1.setPadding(new Insets(20));
        card1.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");
        HBox.setHgrow(card1, Priority.ALWAYS);

        Label c1Title = new Label("Total Payable");
        c1Title.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: bold;");
        lblTotalPayable = new Label("₹ 0.00");
        lblTotalPayable.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTotalPayable.setTextFill(Color.web("#0F172A"));
        Region c1Spacer = new Region();
        c1Spacer.setPrefHeight(12);
        card1.getChildren().addAll(c1Title, lblTotalPayable, c1Spacer);

        // Card 2: Paid Amount (with Blue Progress Bar)
        VBox card2 = new VBox(10);
        card2.setPadding(new Insets(20));
        card2.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");
        HBox.setHgrow(card2, Priority.ALWAYS);

        Label c2Title = new Label("Paid Amount");
        c2Title.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: bold;");
        lblTotalPaid = new Label("₹ 0.00");
        lblTotalPaid.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTotalPaid.setTextFill(Color.web("#0F172A"));

        HBox c2ProgressBox = new HBox(8);
        c2ProgressBox.setAlignment(Pos.CENTER_LEFT);
        pbPaid = new ProgressBar(0.0);
        pbPaid.setPrefHeight(6);
        HBox.setHgrow(pbPaid, Priority.ALWAYS);
        pbPaid.setStyle("-fx-accent: #2563EB; -fx-control-inner-background: #E2E8F0;");
        lblPaidPercent = new Label("0%");
        lblPaidPercent.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        c2ProgressBox.getChildren().addAll(pbPaid, lblPaidPercent);
        card2.getChildren().addAll(c2Title, lblTotalPaid, c2ProgressBox);

        // Card 3: Pending Amount (with Red Progress Bar)
        VBox card3 = new VBox(10);
        card3.setPadding(new Insets(20));
        card3.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");
        HBox.setHgrow(card3, Priority.ALWAYS);

        Label c3Title = new Label("Pending Amount");
        c3Title.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: bold;");
        lblPendingAmount = new Label("₹ 0.00");
        lblPendingAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblPendingAmount.setTextFill(Color.web("#0F172A"));

        HBox c3ProgressBox = new HBox(8);
        c3ProgressBox.setAlignment(Pos.CENTER_LEFT);
        pbPending = new ProgressBar(0.0);
        pbPending.setPrefHeight(6);
        HBox.setHgrow(pbPending, Priority.ALWAYS);
        pbPending.setStyle("-fx-accent: #EF4444; -fx-control-inner-background: #E2E8F0;");
        lblPendingPercent = new Label("0%");
        lblPendingPercent.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
        c3ProgressBox.getChildren().addAll(pbPending, lblPendingPercent);
        card3.getChildren().addAll(c3Title, lblPendingAmount, c3ProgressBox);

        cardsBox.getChildren().addAll(card1, card2, card3);

        // ================= 4. PAYMENT HISTORY SECTION =================
        VBox historySection = new VBox(16);
        historySection.setPadding(new Insets(24));
        historySection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        Label lblHistoryHeader = new Label("Payment History");
        lblHistoryHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        lblHistoryHeader.setTextFill(Color.web("#0F172A"));

        historyContainer = new VBox(14);
        historySection.getChildren().addAll(lblHistoryHeader, historyContainer);

        // ================= 5. BOTTOM PAYMENT INFORMATION BANNER =================
        HBox infoBanner = new HBox(20);
        infoBanner.setAlignment(Pos.CENTER_LEFT);
        infoBanner.setPadding(new Insets(20, 25, 20, 25));
        infoBanner.setStyle("-fx-background-color: #F0F6FF; -fx-background-radius: 12; -fx-border-color: #DBEAFE; -fx-border-radius: 12; -fx-border-width: 1;");

        VBox infoText = new VBox(6);
        HBox.setHgrow(infoText, Priority.ALWAYS);

        Label lblInfoTitle = new Label("Payment Information");
        lblInfoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblInfoTitle.setTextFill(Color.web("#1E293B"));

        Label lblInfoDesc1 = new Label("Make sure to complete the pending payment before the event date.");
        lblInfoDesc1.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #475569;");

        Label lblInfoDesc2 = new Label("Contact your organizer for any payment related queries.");
        lblInfoDesc2.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #475569;");

        infoText.getChildren().addAll(lblInfoTitle, lblInfoDesc1, lblInfoDesc2);

        // Card Graphic
        StackPane cardGraphic = new StackPane();
        cardGraphic.setPrefSize(95, 58);
        cardGraphic.setStyle("-fx-background-color: #1D4ED8; -fx-background-radius: 8;");
        Label cardChip = new Label("💳");
        cardChip.setTextFill(Color.WHITE);
        cardGraphic.getChildren().add(cardChip);

        infoBanner.getChildren().addAll(infoText, cardGraphic);

        rightContent.getChildren().addAll(headerBox, cardsBox, historySection, infoBanner);

        ScrollPane scroll = new ScrollPane(rightContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #F8FAFC;");
        mainLayout.setCenter(scroll);

        // Start Firebase real-time sync
        String effectiveEventId = (eventId != null && !eventId.isBlank()) ? eventId : (event != null ? event.getEventId() : null);
        if (effectiveEventId != null) {
            setupRealTimeListener(effectiveEventId);
        }

        // Exact Size 1550 x 750
        return new Scene(mainLayout, 1550, 750);
    }

    // ================= SIDEBAR COMPONENT =================
    private VBox buildSidebar(Runnable onBack) {
        VBox sb = new VBox(15);
        sb.setPrefWidth(240);
        sb.setPadding(new Insets(25, 15, 25, 15));
        sb.setStyle("-fx-background-color: #031B38;");

        // App Logo
        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 10, 10));
        Label logoIcon = new Label("✨");
        logoIcon.setStyle("-fx-font-size: 18px;");
        Label logoText = new Label("EventXpert");
        logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        logoText.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(logoIcon, logoText);

        // User Profile Info
        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(5, 10, 15, 10));

        Circle avatar = new Circle(18, Color.web("#3B82F6"));
        VBox userDetails = new VBox(2);
        String name = UserSession.getUserEmail() != null ? UserSession.getUserEmail().split("@")[0] : "Customer";
        Label uName = new Label(name);
        uName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        uName.setTextFill(Color.WHITE);
        Label uEmail = new Label(UserSession.getUserEmail() != null ? UserSession.getUserEmail() : "user@eventxpert.com");
        uEmail.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        uEmail.setTextFill(Color.web("#94A3B8"));
        userDetails.getChildren().addAll(uName, uEmail);
        userBox.getChildren().addAll(avatar, userDetails);

        // Navigation Items
        VBox navMenu = new VBox(4);
        navMenu.getChildren().addAll(
                createNavItem("📊  Dashboard", false, onBack),
                createNavItem("➕  Create Event", false, onBack),
                createNavItem("📅  My Events", false, onBack)
        );

        Label secHeader = new Label("EVENT DETAILS");
        secHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        secHeader.setTextFill(Color.web("#64748B"));
        secHeader.setPadding(new Insets(15, 0, 5, 12));

        VBox subMenu = new VBox(4);
        subMenu.getChildren().addAll(
                createNavItem("📑  Overview", false, null),
                createNavItem("📍  Venue", false, null),
                createNavItem("💰  Budget", false, null),
                createNavItem("👥  Guest Management", false, null),
                createNavItem("💳  Payments", true, null),
                createNavItem("🤖  AI Planning", false, null),
                createNavItem("📋  Requirements", false, null),
                createNavItem("⚙  Tracker", false, null)
        );

        sb.getChildren().addAll(logoBox, userBox, navMenu, secHeader, subMenu);
        return sb;
    }

    private Button createNavItem(String text, boolean isActive, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 15, 10, 15));
        btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        btn.setCursor(javafx.scene.Cursor.HAND);

        if (isActive) {
            btn.setStyle("-fx-background-color: #1D4ED8; -fx-text-fill: white; -fx-background-radius: 8;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-text-fill: white; -fx-background-radius: 8;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8;"));
        }

        if (action != null) {
            btn.setOnAction(e -> {
                detachListener();
                action.run();
            });
        }
        return btn;
    }

    // ================= 6. REAL-TIME FIRESTORE LISTENER =================
    private void setupRealTimeListener(String eId) {
        this.eventId = eId;
        Firestore db = FirebaseConfig.getFirestore();
        if (db == null) return;

        detachListener();

        DocumentReference docRef = db.collection("events").document(eId);
        eventListener = docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null || !snapshot.exists()) return;

            Platform.runLater(() -> {
                Double budget = null;
                try {
                    Object bObj = snapshot.get("budget");
                    if (bObj == null) bObj = snapshot.get("totalBudget");
                    if (bObj instanceof Number) {
                        budget = ((Number) bObj).doubleValue();
                    } else if (bObj != null) {
                        String s = bObj.toString().replaceAll("[^0-9.]", "").trim();
                        if (!s.isEmpty()) budget = Double.parseDouble(s);
                    }
                } catch (Exception ignored) {}

                if (budget == null && event != null && event.getBudget() != null) {
                    try {
                        String s = event.getBudget().replaceAll("[^0-9.]", "").trim();
                        if (!s.isEmpty()) budget = Double.parseDouble(s);
                    } catch (Exception ignored) {}
                }

                if (budget == null || budget <= 0) {
                    budget = (event != null && event.getAllocatedBudget() > 0) ? event.getAllocatedBudget() : 50000.0;
                }

                Double paid = snapshot.getDouble("paidAmount");
                if (paid == null) paid = 0.0;

                String title = snapshot.getString("eventName");
                if (title == null) title = snapshot.getString("title");
                if (title != null) this.eventTitle = title;

                this.totalPayable = budget;
                this.totalPaid = paid;
                this.pendingAmount = Math.max(0, this.totalPayable - this.totalPaid);

                String status = snapshot.getString("status");
                boolean isConfirmed = "CONFIRMED".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status);

                String orgId = snapshot.getString("organizerId");
                String orgStatus = snapshot.getString("organizerStatus");
                isOrganizerAssigned = isConfirmed || (orgId != null && !orgId.trim().isEmpty() && !"PENDING".equalsIgnoreCase(orgId) && !"UNASSIGNED".equalsIgnoreCase(orgId))
                        || "ASSIGNED".equalsIgnoreCase(orgStatus) || "ACCEPTED".equalsIgnoreCase(orgStatus);

                String venId = snapshot.getString("vendorId");
                String venStatus = snapshot.getString("vendorStatus");
                isVendorAssigned = isConfirmed || (venId != null && !venId.trim().isEmpty() && !"PENDING".equalsIgnoreCase(venId))
                        || "ASSIGNED".equalsIgnoreCase(venStatus) || "CONFIRMED".equalsIgnoreCase(venStatus) || "ACCEPTED".equalsIgnoreCase(venStatus);

                // Update Cards UI
                lblTotalPayable.setText("₹ " + String.format("%.2f", totalPayable));
                lblTotalPaid.setText("₹ " + String.format("%.2f", totalPaid));
                lblPendingAmount.setText("₹ " + String.format("%.2f", pendingAmount));

                double paidPct = (totalPayable > 0) ? (totalPaid / totalPayable) : 0.0;
                double pendPct = (totalPayable > 0) ? (pendingAmount / totalPayable) : 0.0;

                pbPaid.setProgress(paidPct);
                lblPaidPercent.setText(String.format("%.0f%%", paidPct * 100));

                pbPending.setProgress(pendPct);
                lblPendingPercent.setText(String.format("%.0f%%", pendPct * 100));

                // Update Payment History Table Rows
                renderHistoryRows(snapshot.getString("lastPaymentId"));
            });
        });
    }

    // ================= 7. PAYMENT HISTORY ROWS =================
    private void renderHistoryRows(String lastTxnId) {
        historyContainer.getChildren().clear();

        String txn = (lastTxnId != null && !lastTxnId.isBlank()) ? lastTxnId : "PAY-" + System.currentTimeMillis() % 10000;
        String dateStr = new SimpleDateFormat("dd MMM yyyy").format(new Date());

        // जर पेमेंट झालेले असेल तर 'Paid' Row दाखवा
        if (totalPaid > 0) {
            historyContainer.getChildren().add(
                    createHistoryRow("✔", "#10B981", txn, "Event Settlement", "₹ " + String.format("%.2f", totalPaid), "Paid", "#DCFCE7", "#15803D", dateStr, true)
            );
        }

        // जर काही Pending असेल तर 'Pending' Row दाखवा (Pay Now बटणासह)
        if (pendingAmount > 0) {
            historyContainer.getChildren().add(
                    createHistoryRow("⏳", "#F59E0B", "PAY-DUE", "Final Balance", "₹ " + String.format("%.2f", pendingAmount), "Pending", "#FEF3C7", "#D97706", "Due Soon", false)
            );
        }
    }

    private HBox createHistoryRow(String icon, String iconColor, String code, String label, String amount, String status, String statusBg, String statusFg, String date, boolean isPaid) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #FAFAFA; -fx-background-radius: 8; -fx-border-color: #F1F5F9; -fx-border-width: 1; -fx-border-radius: 8;");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: " + iconColor + "; -fx-font-weight: bold; -fx-font-size: 15px;");

        VBox codeBox = new VBox(2);
        codeBox.setPrefWidth(120);
        Label lblCode = new Label(code);
        lblCode.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1E293B;");
        Label lblDesc = new Label(label);
        lblDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        codeBox.getChildren().addAll(lblCode, lblDesc);

        Label lblAmount = new Label(amount);
        lblAmount.setPrefWidth(110);
        lblAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblAmount.setTextFill(Color.web("#0F172A"));

        Label lblStatusBadge = new Label(status);
        lblStatusBadge.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusFg + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");

        Label lblDate = new Label(date);
        lblDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        lblDate.setPrefWidth(100);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionBtn;
        if (isPaid) {
            actionBtn = new Button("Receipt 📥");
            actionBtn.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border-color: #BFDBFE; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> generateReceipt());
        } else {
            actionBtn = new Button("Pay Now");
            actionBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 16;");
            actionBtn.setOnAction(e -> handlePendingPayment());
        }

        row.getChildren().addAll(iconLbl, codeBox, lblAmount, lblStatusBadge, lblDate, spacer, actionBtn);
        return row;
    }

// ================= 8. PAYMENT INITIATION (ACTUAL USER BUDGET) =================
    private void handlePendingPayment() {
        System.out.println("Pay Now button clicked!");

        // १. युझरने टाकलेली खरी देय रक्कम (Pending Balance किंवा Total Budget)
        double payNowAmt = 0.0;

        if (this.pendingAmount > 0) {
            payNowAmt = this.pendingAmount;
        } else if (this.totalPayable > 0) {
            payNowAmt = this.totalPayable;
        } else if (event != null && event.getBudget() != null) {
            try {
                payNowAmt = Double.parseDouble(event.getBudget().replaceAll("[^0-9.]", "").trim());
            } catch (Exception ignored) {}
        } else if (event != null && event.getAllocatedBudget() > 0) {
            payNowAmt = event.getAllocatedBudget();
        }

        if (payNowAmt <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("No Payable Amount");
            alert.setContentText("The payable amount for this event is ₹ 0. Please check event budget.");
            alert.showAndWait();
            return;
        }

        // २. ग्राहक माहिती
        String userEmail = UserSession.getUserEmail();
        if (userEmail == null || userEmail.isBlank()) {
            userEmail = "customer@eventxpert.com";
        }

        String customerName = "Customer";
        if (userEmail.contains("@")) {
            customerName = userEmail.split("@")[0];
        }

        // ३. OTP जनरेट करणे
        String generatedOtp = String.valueOf((int) (Math.random() * 900000) + 100000);
        final String finalOtp = generatedOtp;
        final String finalEmail = userEmail;
        final String finalCustName = customerName;
        final double finalPayAmount = payNowAmt;

        // Email वर OTP पाठवणे
        new Thread(() -> {
            try {
                   OtpEmailService otpService = new OtpEmailService();
                otpService.sendOtpEmail(finalEmail, finalOtp);
            } catch (Throwable t1) {
                try {
                    new com.techtrio.services.OtpEmailService().sendOtpEmail(finalEmail, finalOtp);
                } catch (Throwable t2) {
                    System.out.println("Payment OTP Note: OTP is " + finalOtp);
                }
            }
        }).start();

        Stage parentStage = (Stage) Stage.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .filter(w -> w instanceof Stage)
                .findFirst().orElse(null);

        // ४. OTP डायलॉग
        Stage otpDialog = new Stage();
        otpDialog.initModality(Modality.APPLICATION_MODAL);
        if (parentStage != null) otpDialog.initOwner(parentStage);
        otpDialog.setTitle("Payment Security - OTP Verification");

        VBox otpBox = new VBox(15);
        otpBox.setPadding(new Insets(25));
        otpBox.setAlignment(Pos.CENTER);
        otpBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label lblTitle = new Label("Verify Payment OTP");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.web("#0F172A"));

        Label lblSubtitle = new Label("Paying: ₹ " + String.format("%.2f", finalPayAmount) + "\nOTP sent to: " + finalEmail + "\n(Test OTP: " + finalOtp + " or 123456)");
        lblSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-text-alignment: center;");

        TextField txtOtp = new TextField();
        txtOtp.setPromptText("Enter 6-digit OTP");
        txtOtp.setMaxWidth(200);
        txtOtp.setAlignment(Pos.CENTER);
        txtOtp.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        txtOtp.setStyle("-fx-padding: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");

        Label lblErr = new Label();
        lblErr.setTextFill(Color.RED);
        lblErr.setVisible(false);

        Button btnVerify = new Button("Verify & Open Razorpay");
        btnVerify.setPrefWidth(200);
        btnVerify.setPrefHeight(38);
        btnVerify.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");

        btnVerify.setOnAction(ev -> {
            String entered = txtOtp.getText() != null ? txtOtp.getText().trim() : "";
            if (entered.equals(finalOtp) || entered.equals("123456")) {
                otpDialog.close();

                // ५. युझरने टाकलेल्या खऱ्या रकमेसह Razorpay सुरू करणे
                System.out.println("Opening Razorpay gateway for actual amount: ₹" + finalPayAmount);
                RazorpayServices.startPayment(finalPayAmount, finalEmail, "9876543210", finalCustName,
                    paymentId -> {
                        System.out.println("Payment Successful with ID: " + paymentId);
                        processAndDistributePayment(paymentId, finalPayAmount);
                        Platform.runLater(() -> {
                            showPaymentSuccessDialog(parentStage, paymentId, finalPayAmount);
                        });
                    },
                    err -> {
                        System.err.println("Payment error: " + err);
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Payment Error");
                            alert.setHeaderText("Unable to Process Payment");
                            alert.setContentText("Error: " + err);
                            alert.showAndWait();
                        });
                    }
                );
            } else {
                lblErr.setText("Invalid OTP! Try 123456 or check email.");
                lblErr.setVisible(true);
            }
        });

        otpBox.getChildren().addAll(lblTitle, lblSubtitle, txtOtp, lblErr, btnVerify);
        Scene otpScene = new Scene(otpBox, 390, 270);
        otpDialog.setScene(otpScene);
        otpDialog.centerOnScreen();
        otpDialog.show();
    }

    // ================= 9. PAYMENT SUCCESS "OK" DIALOG =================
    private void showPaymentSuccessDialog(Stage parentStage, String paymentId, double amountPaid) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (parentStage != null) {
            alert.initOwner(parentStage);
        }
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Payment Done Successfully! ✅");
        alert.setContentText("Transaction ID: " + paymentId + "\n"
                + "Amount Paid: ₹ " + String.format("%.2f", amountPaid) + "\n\n"
                + "Your event booking is officially confirmed.\n"
                + "Click OK to download your official payment receipt.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 'OK' दाबल्यावर थेट पावती उघडणे
                generateReceipt();
            }
        });
    }

    // ================= 10. REVENUE DISTRIBUTION & FIRESTORE UPDATE =================
    private void processAndDistributePayment(String txnId, double amountPaid) {
        new Thread(() -> {
            try {
                Firestore db = FirebaseConfig.getFirestore();
                if (db == null || eventId == null) return;

                double platformFee = amountPaid * 0.05;
                double organizerShare = amountPaid * 0.15;
                double vendorPool = amountPaid * 0.80;

                double newTotalPaid = this.totalPaid + amountPaid;
                String newStatus = (newTotalPaid >= this.totalPayable) ? "PAID" : "PARTIAL";

                Map<String, Object> eventUpdates = new HashMap<>();
                eventUpdates.put("paidAmount", newTotalPaid);
                eventUpdates.put("paymentStatus", newStatus);
                eventUpdates.put("status", "CONFIRMED");
                eventUpdates.put("lastPaymentId", txnId);
                db.collection("events").document(eventId).update(eventUpdates);

                Map<String, Object> paymentDoc = new HashMap<>();
                paymentDoc.put("paymentId", txnId);
                paymentDoc.put("eventId", eventId);
                paymentDoc.put("customerId", userId != null ? userId : UserSession.getUid());
                paymentDoc.put("amount", amountPaid);
                paymentDoc.put("totalAmount", this.totalPayable);
                paymentDoc.put("paidAmount", newTotalPaid);
                paymentDoc.put("platformFee", platformFee);
                paymentDoc.put("organizerAmount", organizerShare);
                paymentDoc.put("vendorAmount", vendorPool);
                paymentDoc.put("status", "PAID");
                paymentDoc.put("timestamp", System.currentTimeMillis());
                db.collection("payments").document(txnId).set(paymentDoc);

                QuerySnapshot qs = db.collection("vendor_bookings").whereEqualTo("eventId", eventId).get().get();
                if (qs != null && !qs.isEmpty()) {
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        doc.getReference().update("status", "PAID");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ================= 11. RECEIPT GENERATION =================
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

    private void detachListener() {
        if (eventListener != null) {
            eventListener.remove();
            eventListener = null;
        }
    }
}