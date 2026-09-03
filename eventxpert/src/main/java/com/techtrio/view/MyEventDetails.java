package com.techtrio.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.techtrio.controller.CustomerController;
import com.techtrio.controller.CustomerEventController;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.services.PaymentService;
import com.techtrio.services.RazorpayServices;
import com.techtrio.session.UserSession;
import com.techtrio.util.OtpHelper;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MyEventDetails {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backCallback;

    // BRAND PALETTE
    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    // Dynamic UI Labels
    private Label eventTitleLabel;
    private Label eventTypeBadge;
    private Label eventIdLabel;
    private Label eventDateKpiVal;
    private Label eventTimeKpiVal;
    private Label guestCountKpiVal;
    private Label organizerKpiVal;
    private Label eventTypeVal;
    private Label locationVal;
    private Label notesVal;
    private Label userNameLabel;
    private Label userEmailLabel;
    private Label statusBadgeLabel;
    
    // ⭐ Pay Now Button
    private Button btnPayNow;
    
    // ⭐ Event Paid Amount लोकल व्हेरिएबल (Event मॉडेलवर अवलंबून न राहण्यासाठी)
    private double eventPaidAmount = 0.0;

    private BorderPane rootPane;

    public MyEventDetails() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public MyEventDetails(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getMyEventDetailsScene() {
        return getMyEventDetailsScene(null);
    }

    public Scene getMyEventDetailsScene(Runnable backCallback) {
        this.backCallback = backCallback;

        rootPane = new BorderPane();
        rootPane.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Sidebar
        rootPane.setLeft(createCommonSidebar("Overview"));

        // 2. Main Scrollable Overview Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createOverviewContent();
        scrollPane.setContent(mainContent);
        rootPane.setCenter(scrollPane);

        loadRealtimeEventDataAsync();

        return new Scene(rootPane, 1550, 785);
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(16, 12, 16, 12));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 8, 8, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(18);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        HBox userBox = new HBox(8);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(4, 8, 10, 8));

        Circle userAvatar = new Circle(14, Color.web("#DBEAFE"));
        VBox userDetails = new VBox(0);
        userNameLabel = new Label("Customer");
        userNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        userEmailLabel = new Label("user@email.com");
        userEmailLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 9px;");
        userDetails.getChildren().addAll(userNameLabel, userEmailLabel);
        userBox.getChildren().addAll(userAvatar, userDetails);

        VBox mainNav = new VBox(2);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::navigateBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 8 3 8;");

        VBox eventNav = new VBox(2);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), () -> {}),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(2);
        bottomNav.getChildren().addAll(
                createNavItem("Support", "fas-life-ring", false, null),
                createNavItem("Settings", "fas-cog", false, this::goSettings)
        );

        sidebar.getChildren().addAll(brandBox, userBox, mainNav, sectionLabel, eventNav, spacer, bottomNav);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(32);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 11px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 6px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 11px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    // ================= 2. OVERVIEW MAIN CONTENT =================
    private VBox createOverviewContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        HBox headerRow = new HBox(14);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false; -fx-cursor: hand;");
        backLink.setOnAction(e -> navigateBack());

        VBox titleCol = new VBox(2);
        HBox tRow = new HBox(8);
        tRow.setAlignment(Pos.CENTER_LEFT);

        eventTitleLabel = new Label("Loading Event...");
        eventTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        eventTypeBadge = new Label("General");
        eventTypeBadge.setStyle("-fx-background-color: #E0E7FF; -fx-text-fill: #4338CA; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 12px;");
        tRow.getChildren().addAll(eventTitleLabel, eventTypeBadge);

        eventIdLabel = new Label("Event ID: EVT12345");
        eventIdLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleCol.getChildren().addAll(tRow, eventIdLabel);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        // ⭐ PAY NOW BUTTON
        btnPayNow = new Button("Pay Now");
        FontIcon payIcon = new FontIcon("fas-credit-card");
        payIcon.setIconSize(14);
        payIcon.setIconColor(Color.WHITE);
        btnPayNow.setGraphic(payIcon);
        btnPayNow.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 20; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnPayNow.setOnMouseEntered(e -> btnPayNow.setStyle("-fx-background-color: #15803D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 20; -fx-background-radius: 6px; -fx-cursor: hand;"));
        btnPayNow.setOnMouseExited(e -> btnPayNow.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 20; -fx-background-radius: 6px; -fx-cursor: hand;"));
        btnPayNow.setOnAction(e -> handlePayNowAction());

        // Approved तपासणे
        String currentEvtStatus = (event != null && event.getStatus() != null) ? event.getStatus() : "";
        boolean isInitialApproved = isStatusApproved(currentEvtStatus);
        btnPayNow.setVisible(isInitialApproved);
        btnPayNow.setManaged(isInitialApproved);

        statusBadgeLabel = new Label(isInitialApproved ? "APPROVED" : "Planning");
        statusBadgeLabel.setStyle(isInitialApproved 
                ? "-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px;"
                : "-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px;");

        headerRow.getChildren().addAll(backLink, new Label("|"), titleCol, sp, btnPayNow, statusBadgeLabel);

        // 5 Top Cards
        HBox kpiRow = new HBox(14);
        eventDateKpiVal = new Label("Date TBD");
        eventTimeKpiVal = new Label("10:00 AM");
        guestCountKpiVal = new Label("100");
        organizerKpiVal = new Label("Aman Events");

        kpiRow.getChildren().addAll(
                createKpiPill("Event Date", eventDateKpiVal, "Saturday", "fas-calendar-alt"),
                createKpiPill("Event Time", eventTimeKpiVal, "Onwards", "fas-clock"),
                createKpiPill("Guest Count", guestCountKpiVal, "Expected", "fas-users"),
                createKpiPill("Organizer", organizerKpiVal, "Assigned Team", "fas-user-tie"),
                createProgressDialCard("68%", "Progress")
        );

        HBox bodyRow = new HBox(20);

        VBox summaryCard = createWhiteCard();
        HBox.setHgrow(summaryCard, Priority.ALWAYS);
        Label sumTitle = new Label("Event Summary");
        sumTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        eventTypeVal = new Label("General");
        locationVal = new Label("Pune, Maharashtra");

        VBox sumRows = new VBox(10);
        sumRows.setPadding(new Insets(10, 0, 10, 0));
        sumRows.getChildren().addAll(
                createKeyValueRow("Event Type", eventTypeVal),
                createKeyValueRow("Location", locationVal),
                createKeyValueRow("Created On", new Label("Recently")),
                createKeyValueRow("Last Updated", new Label("Live Updated")),
                createKeyValueRow("AI Planning", new Label("Enabled")),
                createKeyValueRow("Payment Status", new Label("Partially Paid"))
        );
        summaryCard.getChildren().addAll(sumTitle, sumRows);

        VBox timelineCard = createWhiteCard();
        timelineCard.setPrefWidth(420);
        Label tlTitle = new Label("Timeline");
        tlTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        VBox tlList = new VBox(12);
        tlList.setPadding(new Insets(10, 0, 0, 0));
        tlList.getChildren().addAll(
                createTimelineRow("Event Created", "Completed", true),
                createTimelineRow("Requirements Submitted", "Completed", true),
                createTimelineRow("Organizer Assigned", "Active", true),
                createTimelineRow("Venue Confirmed", "Confirmed", true),
                createTimelineRow("Payments In Progress", "In Progress", false),
                createTimelineRow("Event Completion", "Pending", false)
        );
        timelineCard.getChildren().addAll(tlTitle, tlList);

        bodyRow.getChildren().addAll(summaryCard, timelineCard);

        VBox notesCard = createWhiteCard();
        Label notesTitle = new Label("Event Description / Notes");
        notesTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        notesVal = new Label("Standard event requirements with quality catering and venue coordination.");
        notesVal.setWrapText(true);
        notesVal.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
        notesCard.getChildren().addAll(notesTitle, notesVal);

        page.getChildren().addAll(headerRow, kpiRow, bodyRow, notesCard);
        return page;
    }

    // ================= REAL-TIME FIRESTORE DATA =================
    private void loadRealtimeEventDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                CustomerController customerController = new CustomerController();
                CustomerProfile profile = customerController.getCustomerProfile(uid);

                CustomerEventController eventController = new CustomerEventController();
                String eventId = (event != null) ? event.getEventId() : null;
                Map<String, String> orgDetails = eventController.getOrganizerDetailsForEvent(eventId);

                // Firestore मधून ताजी व्हॅल्यू मिळवा
                if (eventId != null) {
                    try {
                        Firestore db = FirebaseConfig.getFirestore();
                        if (db != null) {
                            DocumentSnapshot doc = db.collection("events").document(eventId).get().get();
                            if (doc.exists()) {
                                String dbStatus = doc.getString("status");
                                Double dbBudget = doc.getDouble("totalBudget");
                                Double dbPaid = doc.getDouble("paidAmount");
                                if (dbStatus != null && event != null) event.setStatus(dbStatus);
                                if (dbBudget != null && event != null) event.setAllocatedBudget(dbBudget);
                                
                                // ⭐ FIX: event.setPaidAmount ऐवजी लोकल व्हेरिएबल अपडेट केले
                                if (dbPaid != null) {
                                    eventPaidAmount = dbPaid;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                Platform.runLater(() -> {
                    if (profile != null && profile.getName() != null) {
                        userNameLabel.setText(profile.getName());
                        userEmailLabel.setText(profile.getEmail() != null ? profile.getEmail() : "user@email.com");
                    }

                    if (event != null) {
                        String name = (event.getEventName() != null) ? event.getEventName() : "Event Overview";
                        String type = (event.getEventType() != null) ? event.getEventType() : "General";
                        String date = (event.getEventDate() != null) ? event.getEventDate() : "TBD";
                        String time = (event.getTime() != null) ? event.getTime() : "10:00 AM";
                        String loc = (event.getCity() != null) ? event.getCity() + ", Maharashtra" : "Pune, Maharashtra";
                        String status = (event.getStatus() != null) ? event.getStatus() : "PLANNING";
                        int guests = event.getGuests() > 0 ? event.getGuests() : 100;

                        eventTitleLabel.setText(name);
                        eventTypeBadge.setText(type);
                        eventIdLabel.setText("Event ID: " + (event.getEventId() != null ? event.getEventId() : "EVT12345"));
                        eventDateKpiVal.setText(date);
                        eventTimeKpiVal.setText(time);
                        guestCountKpiVal.setText(String.valueOf(guests));
                        eventTypeVal.setText(type);
                        locationVal.setText(loc);

                        // ⭐ APPROVED चेक
                        boolean isApproved = isStatusApproved(status);

                        if (isApproved) {
                            statusBadgeLabel.setText("APPROVED");
                            statusBadgeLabel.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px;");
                            
                            btnPayNow.setVisible(true);
                            btnPayNow.setManaged(true);
                        } else {
                            statusBadgeLabel.setText(status.toUpperCase());
                            statusBadgeLabel.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px;");
                            
                            btnPayNow.setVisible(false);
                            btnPayNow.setManaged(false);
                        }

                        if (event.getNotes() != null && !event.getNotes().isBlank()) {
                            notesVal.setText(event.getNotes());
                        }
                    }

                    if (orgDetails != null && orgDetails.containsKey("name")) {
                        organizerKpiVal.setText(orgDetails.get("name"));
                    } else if (event != null && event.getOrganizerName() != null) {
                        organizerKpiVal.setText(event.getOrganizerName());
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private boolean isStatusApproved(String status) {
        if (status == null) return false;
        String s = status.trim().toUpperCase();
        return s.contains("APPROV") || s.contains("CONFIRM") || s.contains("ACCEPT");
    }

    // ================= ⭐ PAY NOW ACTION =================
    // ================= ⭐ PAY NOW ACTION =================
    private void handlePayNowAction() {
        if (event == null) return;

        // १. युजर ईमेल मिळवा
        String userEmail = (userEmailLabel != null && !userEmailLabel.getText().equals("user@email.com"))
                ? userEmailLabel.getText() : UserSession.getUserEmail();

        if (userEmail == null || userEmail.isBlank()) {
            userEmail = "customer@eventxpert.com";
        }

        // २. OTP Verification
        boolean isOtpValid = OtpHelper.sendAndVerifyOtp(userEmail);
        if (!isOtpValid) {
            return;
        }

        // ३. भरावयाची रक्कम
        double totalBudget = (event.getAllocatedBudget() > 0) ? event.getAllocatedBudget() : 50000.0;
        double alreadyPaid = this.eventPaidAmount;
        double amountToPay = Math.max(0, totalBudget - alreadyPaid);

        if (amountToPay <= 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Payment Info");
            alert.setHeaderText(null);
            alert.setContentText("This event is already fully paid!");
            alert.showAndWait();
            return;
        }

        final double finalAmount = amountToPay;
        final String finalEmail = userEmail;

        // ४. Razorpay Live Payment
        RazorpayServices.startPayment(finalAmount, finalEmail, "9999999999",
            paymentId -> {
                try {
                    // ५. Database मध्ये सेव्ह आणि Organizer व Vendor ला वाटप
                    new PaymentService().processPaymentAndDistribute(null, event.getEventId(), finalAmount);

                    // ६. ॲप विंडो पुन्हा समोर आणणे (Bring to Front)
                    Stage stage = null;
                    if (LandingPage.landingPageStage != null) {
                        stage = LandingPage.landingPageStage;
                    } else if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() instanceof Stage) {
                        stage = (Stage) rootPane.getScene().getWindow();
                    }
                    if (stage != null) {
                        stage.setIconified(false);
                        stage.toFront();
                        stage.requestFocus();
                    }

                    // ७. स्वच्छ पॉपअप मेसेज (कोणतीही अडगळ नाही)
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Payment Successful!\nAmount: ₹ " + String.format("%.2f", finalAmount));
                    successAlert.showAndWait();

                    // ⭐ ८. पॉपअप नंतर थेट Payment.java ची स्क्रीन उघडेल!
                    goPayment();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            },
            errorMsg -> {
                Alert failAlert = new Alert(Alert.AlertType.ERROR);
                failAlert.setTitle("Payment Failed");
                failAlert.setHeaderText(null);
                failAlert.setContentText("Payment was not completed: " + errorMsg);
                failAlert.showAndWait();
            }
        );
    }

    private VBox createWhiteCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        return card;
    }

    private HBox createKpiPill(String title, Label valLbl, String subVal, String iconLit) {
        HBox pill = new HBox(10);
        pill.setAlignment(Pos.CENTER_LEFT);
        pill.setPadding(new Insets(12, 14, 12, 14));
        pill.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(pill, Priority.ALWAYS);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(PRIMARY_BLUE));

        VBox texts = new VBox(1);
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        valLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label s = new Label(subVal);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
        texts.getChildren().addAll(t, valLbl, s);

        pill.getChildren().addAll(icon, texts);
        return pill;
    }

    private VBox createProgressDialCard(String percent, String title) {
        VBox card = new VBox(2);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10, 16, 10, 16));
        card.setPrefWidth(160);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold;");

        Label pLbl = new Label(percent);
        pLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label sLbl = new Label("Completed");
        sLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #16A34A;");

        card.getChildren().addAll(tLbl, pLbl, sLbl);
        return card;
    }

    private HBox createKeyValueRow(String key, Label valLabel) {
        HBox row = new HBox();
        Label k = new Label(key);
        k.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        valLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        row.getChildren().addAll(k, sp, valLabel);
        return row;
    }

    private HBox createTimelineRow(String title, String date, boolean isDone) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(5, isDone ? Color.web("#16A34A") : Color.web("#CBD5E1"));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (isDone ? TEXT_DARK : TEXT_MUTED) + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label d = new Label(date);
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        row.getChildren().addAll(dot, t, sp, d);
        return row;
    }

    private void setAppScene(Scene newScene) {
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(newScene);
        } else if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() instanceof Stage) {
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.setScene(newScene);
        }
    }

    private void navigateBack() {
        if (backCallback != null) {
            backCallback.run();
        } else {
            goMyEvents();
        }
    }

    private void goDashboard() {
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        setAppScene(dash.getactiveCustomerScene(null));
    }

    private void goCreateEvent() {
        CreateEventC create = new CreateEventC(uid, idToken);
        setAppScene(create.getCreateEventScene(this::goDashboard));
    }

    private void goMyEvents() {
        MyEvents myEvents = new MyEvents(uid, idToken);
        setAppScene(myEvents.getMyEventsScene(this::goDashboard));
    }

    private void goVenue() {
        Venue view = new Venue(uid, idToken, event);
        setAppScene(view.getVenueScene(this::navigateBack));
    }

    private void goBudget() {
        Budget view = new Budget(uid, idToken, event);
        setAppScene(view.getBudgetScene(this::navigateBack));
    }

    private void goGuest() {
        Guest view = new Guest(uid, idToken, event);
        setAppScene(view.getGuestScene(this::navigateBack));
    }

    private void goPayment() {
        Payment view = new Payment(uid, idToken, event);
        setAppScene(view.getPaymentScene(this::navigateBack));
    }

    private void goRequirement() {
        Requirement view = new Requirement(uid, idToken, event);
        setAppScene(view.getRequirementScene(this::navigateBack));
    }

    private void goSettings() {
        SettingC s = new SettingC();
        setAppScene(s.getSettingScene(this::navigateBack));
    }
}