package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MyEventDetails {

    private final String uid;
    private final String idToken;
    private Event event;
    private Runnable backAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String ORANGE_TEXT = "#B06000";

    private StackPane rootLayer;
    private ListenerRegistration eventSnapshotListener;
    private boolean completionModalShown = false;

    // UI Dynamic Controls
    private Label topStatusBadge;
    private Button topPayNowBtn;

    private Label dateValLabel;
    private Label daySubLabel;
    private Label timeValLabel;
    private Label guestCountLabel;
    private Label organizerNameLabel;
    private Label progressPercentLabel;

    private Label summaryEventType;
    private Label summaryLocation;
    private Label summaryCreatedOn;
    private Label summaryLastUpdated;
    private Label summaryAiPlanning;
    private Label summaryPaymentStatus;

    private VBox timelineContainer;
    private Label descriptionNotesLabel;

    private final TrackerService trackerService = new TrackerService();

    public MyEventDetails() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public MyEventDetails(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getMyEventDetailsScene(Runnable backAction) {
        this.backAction = backAction;

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        root.setLeft(createCommonSidebar("Overview"));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox content = createOverviewContent();
        scroll.setContent(content);
        root.setCenter(scroll);

        rootLayer.getChildren().add(root);

        initCustomerRealtimeListener();

        return new Scene(rootLayer, 1550, 785);
    }

    private void initCustomerRealtimeListener() {
        if (event == null || event.getEventId() == null) return;

        new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference docRef = db.collection("events").document(event.getEventId());

                if (eventSnapshotListener != null) eventSnapshotListener.remove();

                eventSnapshotListener = docRef.addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;
                    Event ev = snapshot.toObject(Event.class);
                    if (ev != null) {
                        this.event = ev;
                        VendorController vc = new VendorController();
                        List<VendorBooking> bookings = vc.getEventVendorBookings(ev.getEventId());
                        Platform.runLater(() -> renderDynamicOverviewUI(ev, bookings));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderDynamicOverviewUI(Event ev, List<VendorBooking> bookings) {
        if (ev == null) return;

        TrackerService.EventMilestoneProgress p = trackerService.calculateProgress(ev);

        // ⭐ १. तंतोतंत पेमेंट तपासणी: फक्त प्रत्यक्ष भरलेली रक्कम (paidAmount > 0)
        boolean isPaid = ev.getPaidAmount() > 0;
        boolean isCompleted = "COMPLETED".equalsIgnoreCase(ev.getStatus());

        if (topStatusBadge != null) {
            topStatusBadge.setText(isCompleted ? "COMPLETED" : (isPaid ? "CONFIRMED" : (ev.getStatus() != null ? ev.getStatus().toUpperCase() : "APPROVED")));
            if (isCompleted) {
                topStatusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 6px;");
            }
        }

        // ⭐ २. Pay Now फक्त तेव्हाच अनलॉक होईल जेव्हा व्हेन्यू आणि व्हेंडर्स फायनल झाले आहेत आणि पैसे भरलेले नाहीत
        boolean canPay = (p.isVenueConfirmed && p.areVendorsLocked);

        if (topPayNowBtn != null) {
            if (isPaid || isCompleted) {
                topPayNowBtn.setVisible(false);
                topPayNowBtn.setManaged(false);
            } else {
                topPayNowBtn.setVisible(true);
                topPayNowBtn.setManaged(true);
                topPayNowBtn.setDisable(!canPay);

                if (!canPay) {
                    topPayNowBtn.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6px;");
                    topPayNowBtn.setTooltip(new Tooltip("Locked: Payment unlocks only after Venue AND all Vendors are finalized."));
                } else {
                    topPayNowBtn.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6px; -fx-cursor: hand;");
                    topPayNowBtn.setTooltip(null);
                }
            }
        }

        // Top 5 Metric Values
        String dateStr = ev.getEventDate() != null ? ev.getEventDate() : (ev.getStartDate() != null ? ev.getStartDate() : "2026-09-05");
        if (dateValLabel != null) dateValLabel.setText(dateStr);

        String timeStr = (ev.getStartTime() != null && !ev.getStartTime().isBlank())
                ? ev.getStartTime() + " - " + (ev.getEndTime() != null ? ev.getEndTime() : "1:00")
                : "10:00 - 1:00";
        if (timeValLabel != null) timeValLabel.setText(timeStr);

        int guests = ev.getGuests() > 0 ? ev.getGuests() : 10;
        int checkedIn = ev.getCheckedInCount();
        if (guestCountLabel != null) {
            guestCountLabel.setText(checkedIn > 0 ? (checkedIn + "/" + guests + " Expected") : (guests + " Expected"));
        }

        String org = ev.getOrganizerName() != null && !ev.getOrganizerName().isBlank() ? ev.getOrganizerName() : "Assigned Team";
        if (organizerNameLabel != null) organizerNameLabel.setText(org);

        // Progress Calculation (पेमेंट झाल्यावरच ७५% च्या पुढे जाईल)
        int progress = isCompleted ? 100 : (isPaid ? 85 : (p.areVendorsLocked ? 68 : (p.isVenueConfirmed ? 50 : 25)));
        if (progressPercentLabel != null) progressPercentLabel.setText(progress + "%");

        // Event Summary
        if (summaryEventType != null) summaryEventType.setText(ev.getEventType() != null ? ev.getEventType() : "Corporate Event");
        String loc = (ev.getArea() != null && !ev.getArea().isBlank() ? ev.getArea() + ", " : "") + (ev.getCity() != null ? ev.getCity() : "Maharashtra");
        if (summaryLocation != null) summaryLocation.setText(loc);
        if (summaryPaymentStatus != null) {
            summaryPaymentStatus.setText(isPaid ? "Fully Paid" : (canPay ? "Payment Unlocked" : "Awaiting Venue & Vendors"));
        }

        // ⭐ ३. १००% रिअल-टाइम टाइमलाइन रेंडरिंग
        renderDynamicTimeline(p, isPaid, isCompleted, canPay);

        // Description
        if (descriptionNotesLabel != null) {
            String notes = ev.getRequirements() != null && !ev.getRequirements().isBlank()
                    ? "Timefold-optimized plan for " + ev.getEventName() + " (" + ev.getEventType() + ") covering: " + ev.getRequirements() + " for " + guests + " guests across the event duration."
                    : "Timefold-optimized event plan and schedule execution tracking.";
            descriptionNotesLabel.setText(notes);
        }

        // Completion Modal
        if (isCompleted && !completionModalShown) {
            completionModalShown = true;
            showEventCompletedSuccessModal(ev.getEventName());
        }
    }

    // ⭐ १००% रिअल-टाइम आणि अचूक ६-स्टेप्स टाइमलाइन
    private void renderDynamicTimeline(TrackerService.EventMilestoneProgress p, boolean isPaid, boolean isCompleted, boolean canPay) {
        if (timelineContainer == null) return;
        timelineContainer.getChildren().clear();

        // १. Event Created (नेहमी Completed)
        timelineContainer.getChildren().add(createTimelineStep("Event Created", "Completed", true, false));

        // २. Requirements Submitted (नेहमी Completed)
        timelineContainer.getChildren().add(createTimelineStep("Requirements Submitted", "Completed", true, false));

        // ३. Organizer Assigned
        timelineContainer.getChildren().add(createTimelineStep(
                "Organizer Assigned",
                p.isOrganizerAssigned ? "Active" : "Pending",
                p.isOrganizerAssigned,
                !p.isOrganizerAssigned
        ));

        // ४. Venue Confirmed
        timelineContainer.getChildren().add(createTimelineStep(
                "Venue Confirmed",
                p.isVenueConfirmed ? "Confirmed" : "In Progress",
                p.isVenueConfirmed,
                p.isOrganizerAssigned && !p.isVenueConfirmed
        ));

        // ५. Payments Status (केवळ प्रत्यक्ष पैसे भरल्यावरच Completed!)
        String payStatusText;
        boolean payDone = false;
        boolean payActive = false;

        if (isPaid) {
            payStatusText = "Completed";
            payDone = true;
        } else if (canPay) {
            payStatusText = "Unlocked";
            payActive = true;
        } else {
            payStatusText = "Pending";
        }
        timelineContainer.getChildren().add(createTimelineStep("Payments Status", payStatusText, payDone, payActive));

        // ६. Event Completion
        String eventCompletionStatus;
        boolean compDone = false;
        boolean compActive = false;

        if (isCompleted) {
            eventCompletionStatus = "Completed";
            compDone = true;
        } else if (isPaid) {
            eventCompletionStatus = "In Execution";
            compActive = true;
        } else {
            eventCompletionStatus = "Pending";
        }
        timelineContainer.getChildren().add(createTimelineStep("Event Completion", eventCompletionStatus, compDone, compActive));
    }

    private HBox createTimelineStep(String title, String statusText, boolean isDone, boolean isActive) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 0, 7, 0));

        FontIcon dot = new FontIcon("fas-circle");
        dot.setIconSize(8);

        String dotColor = isDone ? GREEN_TEXT : (isActive ? "#0284C7" : "#CBD5E1");
        dot.setIconColor(Color.web(dotColor));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + (isDone || isActive ? TEXT_DARK : TEXT_MUTED) + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label s = new Label(statusText);
        String textColor = isDone ? GREEN_TEXT : (isActive ? "#0284C7" : TEXT_MUTED);
        s.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");

        row.getChildren().addAll(dot, t, sp, s);
        return row;
    }

    private void showEventCompletedSuccessModal(String eventName) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(480);
        modal.setPrefWidth(480);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(80);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(20, Color.web("#DCFCE7"));
        Label ic = new Label("🎉");
        ic.setStyle("-fx-font-size: 18px;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label("Event Successfully Executed!");
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        Label m = new Label("[" + eventName + "] has concluded. All deliverables and guest check-ins completed.");
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Great!");
        okBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private VBox createOverviewContent() {
        VBox mainBox = new VBox(18);
        mainBox.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        String eventName = (event != null && event.getEventName() != null) ? event.getEventName() : "Annual Tech Summit";
        Label title = new Label(eventName);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Label typeTag = new Label(event != null && event.getEventType() != null ? event.getEventType() : "Corporate Event");
        typeTag.setStyle("-fx-background-color: #EDE9FE; -fx-text-fill: #6D28D9; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topPayNowBtn = new Button("💳 Pay Now");
        topPayNowBtn.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6px;");
        topPayNowBtn.setDisable(true); // Locked initially
        topPayNowBtn.setOnAction(e -> goPayment());

        topStatusBadge = new Label("APPROVED");
        topStatusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 6px;");

        headerRow.getChildren().addAll(title, typeTag, spacer, topPayNowBtn, topStatusBadge);

        HBox topCards = new HBox(14);
        dateValLabel = new Label("2026-09-05");
        daySubLabel = new Label("Saturday");
        timeValLabel = new Label("10:00 - 1:00");
        guestCountLabel = new Label("10 Expected");
        organizerNameLabel = new Label("Harshada Kurumkar");
        progressPercentLabel = new Label("68%");

        topCards.getChildren().addAll(
                createTopMetricCard("Event Date", dateValLabel, daySubLabel, "fas-calendar-alt"),
                createTopMetricCard("Event Time", timeValLabel, new Label("Onwards"), "fas-clock"),
                createTopMetricCard("Guest Count", guestCountLabel, new Label("Expected"), "fas-users"),
                createTopMetricCard("Organizer", organizerNameLabel, new Label("Assigned Team"), "fas-user-tie"),
                createProgressCard(progressPercentLabel)
        );

        HBox middleGrid = new HBox(20);

        VBox summaryCard = createEventSummaryCard();
        HBox.setHgrow(summaryCard, Priority.ALWAYS);

        VBox timelineCard = createTimelineCard();
        timelineCard.setPrefWidth(380);
        timelineCard.setMinWidth(360);

        middleGrid.getChildren().addAll(summaryCard, timelineCard);

        VBox descriptionCard = createDescriptionCard();

        mainBox.getChildren().addAll(backLink, headerRow, topCards, middleGrid, descriptionCard);
        return mainBox;
    }

    private VBox createTopMetricCard(String title, Label valLbl, Label subLbl, String iconLit) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        top.getChildren().addAll(icon, t);

        valLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        card.getChildren().addAll(top, valLbl, subLbl);
        return card;
    }

    private VBox createProgressCard(Label pctLbl) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Progress");
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        pctLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        Label sub = new Label("Completed");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + ";");

        card.getChildren().addAll(t, pctLbl, sub);
        return card;
    }

    private VBox createEventSummaryCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Event Summary");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        summaryEventType = new Label("Corporate Event");
        summaryLocation = new Label("Maharashtra");
        summaryCreatedOn = new Label("Recently");
        summaryLastUpdated = new Label("Live Updated");
        summaryAiPlanning = new Label("Enabled");
        summaryPaymentStatus = new Label("Pending");

        GridPane g = new GridPane();
        g.setHgap(40); g.setVgap(12);

        g.add(createSummaryRow("Event Type", summaryEventType), 0, 0);
        g.add(createSummaryRow("Location", summaryLocation), 0, 1);
        g.add(createSummaryRow("Created On", summaryCreatedOn), 0, 2);
        g.add(createSummaryRow("Last Updated", summaryLastUpdated), 0, 3);
        g.add(createSummaryRow("AI Planning", summaryAiPlanning), 0, 4);
        g.add(createSummaryRow("Payment Status", summaryPaymentStatus), 0, 5);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(100);
        g.getColumnConstraints().add(c1);

        card.getChildren().addAll(title, new Separator(), g);
        return card;
    }

    private HBox createSummaryRow(String field, Label valLbl) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label f = new Label(field);
        f.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        valLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        row.getChildren().addAll(f, sp, valLbl);
        return row;
    }

    private VBox createTimelineCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Timeline");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        timelineContainer = new VBox(4);

        card.getChildren().addAll(title, new Separator(), timelineContainer);
        return card;
    }

    private VBox createDescriptionCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Event Description / Notes");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        descriptionNotesLabel = new Label("Timefold-optimized plan for your event.");
        descriptionNotesLabel.setWrapText(true);
        descriptionNotesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        card.getChildren().addAll(title, descriptionNotesLabel);
        return card;
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
        brandBox.setPadding(new Insets(0, 8, 14, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(18);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        VBox mainNav = new VBox(2);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 8 4 8;");

        VBox eventNav = new VBox(2);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), () -> {}),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirements)
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
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 11px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleBack() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        if (backAction != null) backAction.run();
        else goDashboard();
    }
    private void goDashboard() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }
    private void goCreateEvent() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }
    private void goVenue() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        Venue view = new Venue(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
    }
    private void goBudget() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        Budget view = new Budget(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getBudgetScene(this::handleBack));
    }
    private void goGuest() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        Guest view = new Guest(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getGuestScene(this::handleBack));
    }
    private void goPayment() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        Payment view = new Payment(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getPaymentScene(this::handleBack));
    }
    private void goRequirements() {
        if (eventSnapshotListener != null) eventSnapshotListener.remove();
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }
}