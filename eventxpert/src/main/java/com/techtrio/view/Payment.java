package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.model.Event;
import com.techtrio.services.OtpEmailService;
import com.techtrio.services.PaymentService;
import com.techtrio.services.RazorpayServices;
import com.techtrio.services.TrackerService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Payment {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN_BG = "#DCFCE7";
    private static final String GREEN_TEXT = "#16A34A";

    private Label lblTotalPayable;
    private Label lblPaidAmount;
    private Label lblPendingAmount;
    private ProgressBar pbPaidRatio;
    private Label lblPaidPercent;

    private Label lblPayableOptionAmount;
    private Label lblDueRowAmount;
    private Button btnPayRazorpay;
    private Button btnPayRow;
    private Button btnDownloadHistoryReceipt;

    private double totalEventBudget = 0.0;
    private double currentPaid = 0.0;
    private double selectedPayableAmount = 0.0;
    private String lastTransactionId = "TXN_LIVE";

    private StackPane rootStack;
    private StackPane modalOverlay;

    private ListenerRegistration firestoreListener;
    private final TrackerService trackerService = new TrackerService();

    public Payment() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Payment(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getPaymentScene(Runnable backAction) {
        this.backAction = backAction;

        rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(createCommonSidebar("Payments"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createPaymentContent();
        scrollPane.setContent(mainContent);
        mainLayout.setCenter(scrollPane);

        modalOverlay = new StackPane();
        modalOverlay.setStyle("-fx-background-color: rgba(6, 27, 65, 0.55);");
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlay);

        initRealtimePaymentListener();

        return new Scene(rootStack, 1550, 785);
    }

    private void initRealtimePaymentListener() {
        if (event == null || event.getEventId() == null) return;

        new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference docRef = db.collection("events").document(event.getEventId());

                if (firestoreListener != null) firestoreListener.remove();

                firestoreListener = docRef.addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;
                    Event ev = snapshot.toObject(Event.class);
                    if (ev != null) {
                        this.event = ev;
                        Platform.runLater(() -> renderPaymentDetails(ev));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderPaymentDetails(Event ev) {
        if (ev == null) return;

        double calculatedAllocated = 0.0;
        if (ev.getBudgetRecommendation() != null && !ev.getBudgetRecommendation().isEmpty()) {
            for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
                try {
                    calculatedAllocated += Double.parseDouble(entry.getValue().toString());
                } catch (Exception ignored) {}
            }
        }

        if (calculatedAllocated > 0) {
            totalEventBudget = calculatedAllocated;
        } else if (ev.getAllocatedBudget() > 0) {
            totalEventBudget = ev.getAllocatedBudget();
        } else {
            try {
                String clean = ev.getBudget() != null ? ev.getBudget().replaceAll("[^0-9.]", "").trim() : "0";
                if (!clean.isEmpty()) totalEventBudget = Double.parseDouble(clean);
            } catch (Exception ignored) {}
        }

        currentPaid = ev.getPaidAmount();
        double pending = Math.max(0.0, totalEventBudget - currentPaid);
        selectedPayableAmount = pending;

        lblTotalPayable.setText(String.format("₹ %.2f", totalEventBudget));
        lblPaidAmount.setText(String.format("₹ %.2f", currentPaid));
        lblPendingAmount.setText(String.format("₹ %.2f", pending));

        double ratio = totalEventBudget > 0 ? (currentPaid / totalEventBudget) : 0.0;
        pbPaidRatio.setProgress(Math.min(1.0, ratio));
        lblPaidPercent.setText((int) Math.round(ratio * 100) + "%");

        lblPayableOptionAmount.setText(String.format("Payable: ₹ %.2f", selectedPayableAmount));
        lblDueRowAmount.setText(String.format("₹ %.2f", selectedPayableAmount));

        TrackerService.EventMilestoneProgress progress = trackerService.calculateProgress(ev);
        boolean isFullyPaid = currentPaid > 0 && currentPaid >= totalEventBudget;

        boolean isVenueAndVendorsLocked = (progress.isVenueConfirmed && progress.areVendorsLocked);
        boolean canPayNow = isVenueAndVendorsLocked && !isFullyPaid && pending > 0;

        btnPayRazorpay.setDisable(!canPayNow);
        btnPayRow.setDisable(!canPayNow);

        if (btnDownloadHistoryReceipt != null) {
            btnDownloadHistoryReceipt.setVisible(currentPaid > 0);
            btnDownloadHistoryReceipt.setManaged(currentPaid > 0);
        }

        if (isFullyPaid) {
            btnPayRazorpay.setText("Fully Paid ✓");
            btnPayRazorpay.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6px;");
            btnPayRow.setText("Paid ✓");
            btnPayRow.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 6px;");
        } else if (!isVenueAndVendorsLocked) {
            Tooltip tip = new Tooltip("Locked: Payment unlocks only after Venue AND all Vendors are finalized by Organizer.");
            btnPayRazorpay.setTooltip(tip);
            btnPayRow.setTooltip(tip);
            btnPayRazorpay.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 18; -fx-background-radius: 6px;");
            btnPayRow.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px;");
        } else {
            btnPayRazorpay.setTooltip(null);
            btnPayRow.setTooltip(null);
            btnPayRazorpay.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 18; -fx-background-radius: 6px; -fx-cursor: hand;");
            btnPayRow.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        }
    }

    private VBox createPaymentContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(2);
        Label heading = new Label("Payments");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Track payments, itemized budget receipts and due amounts synced in real-time.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        HBox topCards = new HBox(16);

        lblTotalPayable = new Label("₹ 0.00");
        lblPaidAmount = new Label("₹ 0.00");
        lblPendingAmount = new Label("₹ 0.00");

        pbPaidRatio = new ProgressBar(0.0);
        pbPaidRatio.setMaxWidth(Double.MAX_VALUE);
        pbPaidRatio.setStyle("-fx-accent: #16A34A; -fx-pref-height: 4;");

        lblPaidPercent = new Label("0%");
        lblPaidPercent.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");

        topCards.getChildren().addAll(
                createCard("Total Payable", lblTotalPayable, "fas-wallet", PRIMARY_BLUE, null, null),
                createCard("Paid Amount", lblPaidAmount, "fas-check-circle", "#16A34A", pbPaidRatio, lblPaidPercent),
                createCard("Pending Amount", lblPendingAmount, "fas-clock", "#DC2626", null, null)
        );

        VBox paymentOptionsCard = createPaymentOptionsCard();
        VBox paymentHistoryCard = createPaymentHistoryCard();

        VBox infoBox = new VBox(4);
        infoBox.setPadding(new Insets(14));
        infoBox.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        Label iT = new Label("Payment Information & Itemized Invoice");
        iT.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        Label iD = new Label("Once your payment is completed via Razorpay, you can download your official PDF receipt with service-wise breakdown.");
        iD.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        infoBox.getChildren().addAll(iT, iD);

        page.getChildren().addAll(backLink, titleBox, topCards, paymentOptionsCard, paymentHistoryCard, infoBox);
        return page;
    }

    private VBox createCard(String title, Label valLbl, String iconLit, String colorHex, ProgressBar pb, Label pbLabel) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        FontIcon ic = new FontIcon(iconLit);
        ic.setIconSize(13);
        ic.setIconColor(Color.web(colorHex));
        top.getChildren().addAll(t, sp, ic);

        valLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        card.getChildren().addAll(top, valLbl);

        if (pb != null && pbLabel != null) {
            HBox pbRow = new HBox(8, pb, pbLabel);
            pbRow.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(pb, Priority.ALWAYS);
            card.getChildren().add(pbRow);
        }

        return card;
    }

    private VBox createPaymentOptionsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        FontIcon cardIcon = new FontIcon("fas-credit-card");
        cardIcon.setIconSize(14);
        cardIcon.setIconColor(Color.web(PRIMARY_BLUE));
        VBox tv = new VBox(2);
        Label t = new Label("Advance Token & Payment Options");
        t.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Choose to pay Advance Token (30%), Mid-Way (50%), or Full Balance directly to Admin via Razorpay.");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        tv.getChildren().addAll(t, sub);
        titleRow.getChildren().addAll(cardIcon, tv);

        HBox optionsRow = new HBox(12);
        optionsRow.setAlignment(Pos.CENTER_LEFT);

        Button btn30 = createOptionPill("Advance Token (30%)");
        Button btn50 = createOptionPill("Mid-Way (50%)");
        Button btn100 = createOptionPill("Full Due (100%)");

        btn30.setOnAction(e -> selectPaymentOption(btn30, btn50, btn100, 0.30));
        btn50.setOnAction(e -> selectPaymentOption(btn50, btn30, btn100, 0.50));
        btn100.setOnAction(e -> selectPaymentOption(btn100, btn30, btn50, 1.00));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        lblPayableOptionAmount = new Label("Payable: ₹ 0.00");
        lblPayableOptionAmount.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        btnPayRazorpay = new Button("Pay via Razorpay →");
        btnPayRazorpay.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 18; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnPayRazorpay.setOnAction(e -> executeRazorpayPayment(selectedPayableAmount));

        optionsRow.getChildren().addAll(btn30, btn50, btn100, sp, lblPayableOptionAmount, btnPayRazorpay);
        card.getChildren().addAll(titleRow, new Separator(), optionsRow);

        return card;
    }

    private Button createOptionPill(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-padding: 6 14; -fx-cursor: hand;");
        return b;
    }

    private void selectPaymentOption(Button active, Button b2, Button b3, double ratio) {
        active.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: " + ACTIVE_NAV_BLUE + "; -fx-font-weight: bold; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-padding: 6 14;");
        b2.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-padding: 6 14; -fx-cursor: hand;");
        b3.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-font-size: 11px; -fx-padding: 6 14; -fx-cursor: hand;");

        selectedPayableAmount = Math.round(totalEventBudget * ratio);
        lblPayableOptionAmount.setText(String.format("Payable: ₹ %.2f", selectedPayableAmount));
    }

    private VBox createPaymentHistoryCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Payment History & Receipts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        FontIcon icon = new FontIcon("fas-receipt");
        icon.setIconSize(14);
        icon.setIconColor(Color.web(PRIMARY_BLUE));

        VBox textGroup = new VBox(2);
        Label l1 = new Label("PAY-SETTLEMENT");
        l1.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
        Label l2 = new Label("Secure Payment Transaction");
        l2.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
        textGroup.getChildren().addAll(l1, l2);

        lblDueRowAmount = new Label("₹ 0.00");
        lblDueRowAmount.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        btnDownloadHistoryReceipt = new Button("📄 View / Download Receipt");
        btnDownloadHistoryReceipt.setVisible(false);
        btnDownloadHistoryReceipt.setManaged(false);
        btnDownloadHistoryReceipt.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnDownloadHistoryReceipt.setOnAction(e -> showInAppReceiptPopup(currentPaid > 0 ? currentPaid : selectedPayableAmount, lastTransactionId));

        btnPayRow = new Button("Pay Now");
        btnPayRow.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnPayRow.setOnAction(e -> executeRazorpayPayment(selectedPayableAmount));

        row.getChildren().addAll(icon, textGroup, lblDueRowAmount, sp, btnDownloadHistoryReceipt, btnPayRow);
        card.getChildren().addAll(title, row);
        return card;
    }

    private void executeRazorpayPayment(double amountToPay) {
        if (event == null || amountToPay <= 0) {
            showAlert("Invalid Amount", "Please select an amount greater than ₹ 0.");
            return;
        }

        String customerEmail = UserSession.getUserEmail();
        if (customerEmail == null || customerEmail.isBlank()) {
            showAlert("Session Error", "User email not found in active session.");
            return;
        }

        final String targetEmail = customerEmail.trim();
        final double finalAmount = amountToPay;

        String generatedOtp = String.valueOf(100000 + new Random().nextInt(900000));

        new Thread(() -> {
            OtpEmailService emailService = new OtpEmailService();
            emailService.sendOtpEmail(targetEmail, generatedOtp);
        }).start();

        showInAppOtpDialog(generatedOtp, finalAmount, targetEmail);
    }

    private void showInAppOtpDialog(String actualOtp, double amountToPay, String customerEmail) {
        modalOverlay.getChildren().clear();
        modalOverlay.setAlignment(Pos.CENTER);

        VBox card = new VBox(14);
        card.setMaxWidth(380);
        card.setPadding(new Insets(24, 28, 24, 28));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");
        card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));

        FontIcon lockIcon = new FontIcon("fas-shield-alt");
        lockIcon.setIconSize(36);
        lockIcon.setIconColor(Color.web(ACTIVE_NAV_BLUE));

        Label lblTitle = new Label("Authorize Payment");
        lblTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Label lblSubtitle = new Label("Enter the 6-digit OTP sent to your registered Gmail.");
        lblSubtitle.setStyle("-fx-font-size: 11.5px; -fx-text-fill: " + TEXT_MUTED + ";");

        Label lblAmount = new Label(String.format("Payable: ₹ %.2f", amountToPay));
        lblAmount.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ACTIVE_NAV_BLUE + "; -fx-background-color: #EFF6FF; -fx-padding: 4 14; -fx-background-radius: 20px;");

        TextField txtOtp = new TextField();
        txtOtp.setPromptText("------");
        txtOtp.setAlignment(Pos.CENTER);
        txtOtp.setMaxWidth(160);
        txtOtp.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-letter-spacing: 4px; -fx-padding: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 11px;");

        Button btnVerify = new Button("Verify & Open Razorpay →");
        btnVerify.setMaxWidth(Double.MAX_VALUE);
        btnVerify.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9 16; -fx-background-radius: 6px; -fx-cursor: hand;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> closeInAppModal());

        btnVerify.setOnAction(e -> {
            String enteredOtp = txtOtp.getText().trim();
            if (enteredOtp.isEmpty()) {
                lblError.setText("Please enter the OTP.");
                return;
            }

            if (enteredOtp.equals(actualOtp)) {
                closeInAppModal();
                launchRazorpayGateway(amountToPay, customerEmail);
            } else {
                lblError.setText("Incorrect OTP! Please re-enter.");
                txtOtp.clear();
            }
        });

        card.getChildren().addAll(lockIcon, lblTitle, lblSubtitle, lblAmount, txtOtp, lblError, btnVerify, btnCancel);
        modalOverlay.getChildren().add(card);
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    private void launchRazorpayGateway(double amountToPay, String customerEmail) {
        String customerName = UserSession.getUserName();

        RazorpayServices.startPayment(
                amountToPay,
                customerEmail,
                "9999999999",
                customerName,
                (paymentId) -> {
                    Platform.runLater(() -> {
                        try {
                            this.lastTransactionId = paymentId;

                            RazorpayServices razorServices = new RazorpayServices();
                            com.techtrio.model.Payment paymentModel = razorServices.calculateSplit(
                                    event.getEventId(),
                                    uid,
                                    customerName,
                                    event.getOrganizerId(),
                                    amountToPay,
                                    amountToPay * 0.80
                            );
                            paymentModel.setTransactionId(paymentId);

                            PaymentService paymentService = new PaymentService();
                            paymentService.processPaymentAndDistribute(paymentModel, event.getEventId(), amountToPay);

                            double updatedPaid = currentPaid + amountToPay;
                            event.setPaidAmount(updatedPaid);
                            if (updatedPaid >= totalEventBudget) {
                                event.setStatus("CONFIRMED");
                            }

                            renderPaymentDetails(event);
                            showInAppReceiptPopup(amountToPay, paymentId);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                },
                (errorMessage) -> {
                    Platform.runLater(() -> {
                        Alert failAlert = new Alert(Alert.AlertType.ERROR);
                        failAlert.setTitle("Payment Incomplete");
                        failAlert.setHeaderText(null);
                        failAlert.setContentText(errorMessage);
                        failAlert.showAndWait();
                    });
                }
        );
    }

    // ⭐ बजेट ब्रेकडाउन दाखवणारी सुंदर इन-ॲप पावती
    private void showInAppReceiptPopup(double paidAmt, String txnId) {
        modalOverlay.getChildren().clear();
        modalOverlay.setAlignment(Pos.CENTER);

        VBox card = new VBox(12);
        card.setMaxWidth(520);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");
        card.setEffect(new DropShadow(25, Color.rgb(0, 0, 0, 0.28)));

        // Header
        HBox topHead = new HBox();
        topHead.setAlignment(Pos.CENTER_LEFT);
        Label brand = new Label("EventXpert Payment Receipt");
        brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label paidBadge = new Label("PAID ✓");
        paidBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-size: 11px;");
        topHead.getChildren().addAll(brand, sp, paidBadge);

        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String clientName = UserSession.getUserName();
        String eventName = (event != null && event.getEventName() != null) ? event.getEventName() : "Event Booking";

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(6);
        grid.setPadding(new Insets(6, 0, 6, 0));

        addReceiptRow(grid, 0, "Receipt / Txn ID:", txnId);
        addReceiptRow(grid, 1, "Date & Time:", nowStr);
        addReceiptRow(grid, 2, "Billed To:", clientName);
        addReceiptRow(grid, 3, "Event Name:", eventName);
        addReceiptRow(grid, 4, "Payment Gateway:", "Razorpay Secure Online Payments");

        // ⭐ बजेट ब्रेकडाउन टेबल (कोणत्या सेवेसाठी किती खर्च झाला)
        VBox breakdownBox = new VBox(4);
        breakdownBox.setPadding(new Insets(8, 10, 8, 10));
        breakdownBox.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        Label bdTitle = new Label("Itemized Budget Allocation Breakdown:");
        bdTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        breakdownBox.getChildren().add(bdTitle);

        Map<String, Double> allocationMap = getEventBudgetBreakdown();
        for (Map.Entry<String, Double> entry : allocationMap.entrySet()) {
            HBox bRow = new HBox();
            bRow.setAlignment(Pos.CENTER_LEFT);
            Label cLbl = new Label("● " + entry.getKey());
            cLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
            Region rsp = new Region();
            HBox.setHgrow(rsp, Priority.ALWAYS);
            Label aLbl = new Label(String.format("₹ %,.2f", entry.getValue()));
            aLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
            bRow.getChildren().addAll(cLbl, rsp, aLbl);
            breakdownBox.getChildren().add(bRow);
        }

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setPadding(new Insets(6, 0, 0, 0));
        Label tLbl = new Label("Total Settled Payment:");
        tLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region tsp = new Region();
        HBox.setHgrow(tsp, Priority.ALWAYS);
        Label vLbl = new Label(String.format("₹ %.2f", paidAmt));
        vLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
        totalRow.getChildren().addAll(tLbl, tsp, vLbl);

        // Buttons
        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.setPadding(new Insets(8, 0, 0, 0));

        Button btnDownload = new Button("📥 Download PDF Receipt");
        btnDownload.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnDownload.setOnAction(e -> saveReceiptAsPdf(paidAmt, txnId, nowStr, clientName, eventName, allocationMap));

        Button btnClose = new Button("Close");
        btnClose.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnClose.setOnAction(e -> closeInAppModal());

        btnBox.getChildren().addAll(btnDownload, btnClose);
        card.getChildren().addAll(topHead, new Separator(), grid, breakdownBox, totalRow, new Separator(), btnBox);

        modalOverlay.getChildren().add(card);
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    private Map<String, Double> getEventBudgetBreakdown() {
        Map<String, Double> map = new LinkedHashMap<>();
        if (event != null && event.getBudgetRecommendation() != null && !event.getBudgetRecommendation().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getBudgetRecommendation().entrySet()) {
                try {
                    map.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
                } catch (Exception ignored) {}
            }
        }
        if (map.isEmpty()) {
            map.put("Venue", totalEventBudget * 0.45);
            map.put("Decoration", totalEventBudget * 0.40);
            map.put("Contingency & Other", totalEventBudget * 0.15);
        }
        return map;
    }

    private void closeInAppModal() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
        modalOverlay.getChildren().clear();
    }

    private void addReceiptRow(GridPane grid, int row, String title, String val) {
        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        Label vLbl = new Label(val);
        vLbl.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        grid.add(tLbl, 0, row);
        grid.add(vLbl, 1, row);
    }

    // ⭐ बजेट ब्रेकडाउनसह अधिकृत PDF Document तयार करणे
    private void saveReceiptAsPdf(double paidAmt, String txnId, String dateTime, String clientName, String eventName, Map<String, Double> breakdownMap) {
        Stage stage = (Stage) rootStack.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Payment Receipt");
        fileChooser.setInitialFileName("EventXpert_Receipt_" + txnId + ".html");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Document / Web Receipt (*.html)", "*.html"),
                new FileChooser.ExtensionFilter("PDF File (*.pdf)", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                StringBuilder breakdownHtml = new StringBuilder();
                for (Map.Entry<String, Double> entry : breakdownMap.entrySet()) {
                    breakdownHtml.append("<tr>")
                            .append("<td style='padding:8px 0; border-bottom:1px solid #F1F5F9; color:#1E293B;'>● ").append(entry.getKey()).append("</td>")
                            .append("<td style='padding:8px 0; border-bottom:1px solid #F1F5F9; text-align:right; font-weight:bold; color:#0F172A;'>₹ ").append(String.format("%,.2f", entry.getValue())).append("</td>")
                            .append("</tr>");
                }

                String pdfHtml = "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                        + "<title>Payment Receipt - " + txnId + "</title>"
                        + "<style>"
                        + "@page { size: A4; margin: 20mm; }"
                        + "body { font-family: 'Segoe UI', Arial, sans-serif; color: #1E293B; background: #FFF; margin: 0; padding: 40px; }"
                        + ".invoice-box { max-width: 650px; margin: auto; border: 1px solid #E2E8F0; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }"
                        + ".header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2563EB; padding-bottom: 15px; margin-bottom: 20px; }"
                        + ".title { color: #061B41; font-size: 26px; font-weight: bold; margin: 0; }"
                        + ".badge { background: #DCFCE7; color: #16A34A; font-weight: bold; padding: 6px 14px; border-radius: 20px; font-size: 13px; text-transform: uppercase; }"
                        + ".table { width: 100%; border-collapse: collapse; margin-top: 10px; }"
                        + ".table td { padding: 8px 0; border-bottom: 1px solid #F1F5F9; font-size: 13.5px; }"
                        + ".table td.label { color: #64748B; font-weight: 500; width: 40%; }"
                        + ".table td.val { font-weight: bold; color: #0F172A; }"
                        + ".breakdown-card { background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 6px; padding: 14px; margin-top: 20px; }"
                        + ".total-row { background: #EFF6FF; border-top: 2px solid #2563EB; margin-top: 15px; }"
                        + ".total-row td { padding: 14px 10px; font-size: 18px; color: #1D4ED8; font-weight: bold; }"
                        + ".footer { margin-top: 30px; text-align: center; font-size: 12px; color: #94A3B8; border-top: 1px solid #E2E8F0; padding-top: 15px; }"
                        + "</style></head><body>"
                        + "<div class='invoice-box'>"
                        + "<div class='header'>"
                        + "  <div><div class='title'>EventXpert</div><p style='margin:4px 0 0 0; color:#64748B; font-size:12px;'>Official Tax Invoice & Service Settlement</p></div>"
                        + "  <div class='badge'>PAID ✓</div>"
                        + "</div>"
                        + "<table class='table'>"
                        + "  <tr><td class='label'>Transaction ID:</td><td class='val'>" + txnId + "</td></tr>"
                        + "  <tr><td class='label'>Payment Date:</td><td class='val'>" + dateTime + "</td></tr>"
                        + "  <tr><td class='label'>Customer Name:</td><td class='val'>" + clientName + "</td></tr>"
                        + "  <tr><td class='label'>Registered Email:</td><td class='val'>" + UserSession.getUserEmail() + "</td></tr>"
                        + "  <tr><td class='label'>Event Name:</td><td class='val'>" + eventName + "</td></tr>"
                        + "  <tr><td class='label'>Gateway:</td><td class='val'>Razorpay Secure Online Payments</td></tr>"
                        + "</table>"
                        + "<div class='breakdown-card'>"
                        + "  <div style='font-size:12px; font-weight:bold; color:#0D3979; margin-bottom:8px;'>Itemized Service Budget Breakdown:</div>"
                        + "  <table style='width:100%; border-collapse:collapse;'>"
                        +      breakdownHtml.toString()
                        + "  </table>"
                        + "</div>"
                        + "<table class='table total-row'>"
                        + "  <tr><td class='label' style='color:#1D4ED8;'>Total Amount Settled:</td><td class='val' style='text-align:right;'>₹ " + String.format("%.2f", paidAmt) + "</td></tr>"
                        + "</table>"
                        + "<div class='footer'>"
                        + "  <p style='margin:0 0 4px 0;'>✓ This is an authentic, computer-generated receipt issued by EventXpert.</p>"
                        + "  <p style='margin:0;'>For support, contact your organizer or support@eventxpert.com</p>"
                        + "</div>"
                        + "</div>"
                        + "<script>window.onload = function(){ window.print(); };</script>"
                        + "</body></html>";

                writer.write(pdfHtml);

                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file);
                }

                Alert success = new Alert(Alert.AlertType.INFORMATION, "PDF Receipt created successfully!\nPath: " + file.getAbsolutePath());
                success.setHeaderText("PDF Downloaded");
                success.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("File Error", "Could not generate PDF receipt: " + ex.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void detachListener() {
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 8, 16, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        VBox mainNav = new VBox(3);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 12 8 4 8;");

        VBox eventNav = new VBox(3);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), this::goOverview),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), () -> {}),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, mainNav, sectionLabel, eventNav, spacer);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleBack() {
        detachListener();
        if (backAction != null) backAction.run();
        else goOverview();
    }

    private void goDashboard() {
        detachListener();
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }

    private void goCreateEvent() {
        detachListener();
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }

    private void goOverview() {
        detachListener();
        MyEventDetails view = new MyEventDetails(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getMyEventDetailsScene(this::handleBack));
    }

    private void goVenue() {
        detachListener();
        Venue view = new Venue(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
    }

    private void goBudget() {
        detachListener();
        Budget view = new Budget(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getBudgetScene(this::handleBack));
    }

    private void goGuest() {
        detachListener();
        Guest view = new Guest(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getGuestScene(this::handleBack));
    }

    private void goRequirement() {
        detachListener();
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }
}