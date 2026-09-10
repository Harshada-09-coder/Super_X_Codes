package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.context.AdminContext;
import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class LiveEventDashboardO {

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String ORANGE_TEXT = "#B06000";

    private String uid;
    private String idToken;
    private Runnable backAction;

    private Event currentEvent;
    private List<VendorBooking> activeBookings = new ArrayList<>();
    private ListenerRegistration eventListener;
    private ListenerRegistration bookingsListener;

    private StackPane rootLayer;

    // Dynamic UI Controls
    private Label eventTitleLabel;
    private Label liveBadge;
    private Label checkedInValLabel;
    private Label activeVendorsValLabel;
    private ProgressBar mainProgressBar;
    private Label progressPctLabel;
    private VBox timelineListContainer;
    private VBox vendorStatusContainer;
    private Button btnCompleteFullEvent;

    public LiveEventDashboardO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public LiveEventDashboardO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getScene(Runnable backAction) {
        this.backAction = backAction;

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        root.setLeft(createCommonOrganizerSidebar("My Events"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);
        rootLayer.getChildren().add(root);

        initRealtimeLiveOperationsListener();

        return new Scene(rootLayer, 1550, 785);
    }

    private void initRealtimeLiveOperationsListener() {
        new Thread(() -> {
            try {
                String eventId = null;
                try {
                    eventId = AdminContext.getSelectedEventId();
                } catch (Throwable ignored) {}

                if (eventId == null || eventId.isBlank()) {
                    EventService eventService = new EventService();
                    List<Event> assigned = eventService.getEventsByOrganizerId(uid);
                    if (assigned != null && !assigned.isEmpty()) {
                        eventId = assigned.get(0).getEventId();
                        try {
                            AdminContext.setSelectedEventId(eventId);
                        } catch (Throwable ignored) {}
                    }
                }

                if (eventId == null || eventId.isBlank()) return;

                Firestore db = FirestoreClient.getFirestore();
                if (db == null) return;

                final String finalEventId = eventId.trim();

                // १. Event Document Listener
                if (eventListener != null) eventListener.remove();
                eventListener = db.collection("events").document(finalEventId)
                        .addSnapshotListener((snapshot, error) -> {
                            if (error != null || snapshot == null || !snapshot.exists()) return;
                            Event ev = snapshot.toObject(Event.class);
                            if (ev != null) {
                                this.currentEvent = ev;
                                Platform.runLater(this::renderLiveEventDashboardUI);
                            }
                        });

                // ⭐ २. अचूक 'vendorBookings' कलेक्शनवर Real-Time Listener
                if (bookingsListener != null) bookingsListener.remove();
                bookingsListener = db.collection("vendorBookings")
                        .whereEqualTo("eventId", finalEventId)
                        .addSnapshotListener((snapshots, error) -> {
                            if (error != null || snapshots == null) return;

                            List<VendorBooking> list = new ArrayList<>();
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                VendorBooking vb = doc.toObject(VendorBooking.class);
                                if (vb != null) {
                                    if (vb.getBookingId() == null || vb.getBookingId().isBlank()) {
                                        vb.setBookingId(doc.getId());
                                    }
                                    list.add(vb);
                                }
                            }
                            this.activeBookings = list;
                            Platform.runLater(this::renderLiveEventDashboardUI);
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderLiveEventDashboardUI() {
        if (currentEvent == null) return;

        try {
            boolean isAlreadyCompleted = "COMPLETED".equalsIgnoreCase(currentEvent.getStatus());
            boolean isPaid = currentEvent.getPaidAmount() > 0 || "PAID".equalsIgnoreCase(currentEvent.getStatus()) || "CONFIRMED".equalsIgnoreCase(currentEvent.getStatus());

            if (eventTitleLabel != null) {
                eventTitleLabel.setText((currentEvent.getEventName() != null ? currentEvent.getEventName() : "Event") + " — Live Operations");
            }

            // Guest metrics
            int totalLimit = currentEvent.getGuestLimit() > 0 ? currentEvent.getGuestLimit() : (currentEvent.getGuests() > 0 ? currentEvent.getGuests() : 50);
            int checkedIn = currentEvent.getCheckedInCount();
            if (checkedInValLabel != null) {
                checkedInValLabel.setText(checkedIn + " / " + totalLimit);
            }

            // Vendor Milestones
            int totalVendors = activeBookings != null ? activeBookings.size() : 0;
            int completedVendors = 0;
            int inProgressVendors = 0;

            if (vendorStatusContainer != null) vendorStatusContainer.getChildren().clear();

            if (activeBookings != null && !activeBookings.isEmpty()) {
                for (VendorBooking vb : activeBookings) {
                    if (vb == null) continue;
                    String st = vb.getStatus() != null ? vb.getStatus().trim().toUpperCase() : "ASSIGNED";

                    if ("COMPLETED".equals(st)) completedVendors++;
                    else if ("IN_PROGRESS".equals(st) || "IN PROGRESS".equals(st)) inProgressVendors++;

                    String bg = "COMPLETED".equals(st) ? GREEN_BG : (st.contains("PROGRESS") ? ORANGE_BG : LIGHT_BLUE);
                    String txt = "COMPLETED".equals(st) ? GREEN_TEXT : (st.contains("PROGRESS") ? ORANGE_TEXT : PRIMARY_BLUE);

                    if (vendorStatusContainer != null) {
                        String vName = (vb.getVendorName() != null && !vb.getVendorName().isBlank()) ? vb.getVendorName() : "Partner";
                        String sCat = (vb.getServiceCategory() != null) ? vb.getServiceCategory() : "Service";
                        vendorStatusContainer.getChildren().add(createVendorRow(vName + " (" + sCat + ")", st, bg, txt));
                    }
                }
            }

            if (activeVendorsValLabel != null) {
                activeVendorsValLabel.setText(completedVendors + " / " + totalVendors + " Done");
            }

            // Progress Calculation
            double progressVal = isAlreadyCompleted ? 1.0 : (totalVendors > 0 ? ((double) completedVendors / totalVendors) : 0.0);
            if (mainProgressBar != null) mainProgressBar.setProgress(progressVal);
            if (progressPctLabel != null) progressPctLabel.setText(Math.round(progressVal * 100) + "% Executed");

            // Conclude Button Activation
            boolean canConclude = (totalVendors > 0 && completedVendors == totalVendors) || isAlreadyCompleted;
            if (btnCompleteFullEvent != null) {
                btnCompleteFullEvent.setDisable(!canConclude || isAlreadyCompleted);
                if (isAlreadyCompleted) {
                    btnCompleteFullEvent.setText("✓ Event Fully Concluded & Closed");
                    btnCompleteFullEvent.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 6px;");
                } else if (canConclude) {
                    btnCompleteFullEvent.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9; -fx-background-radius: 6; -fx-cursor: hand;");
                } else {
                    btnCompleteFullEvent.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9; -fx-background-radius: 6;");
                }
            }

            if (liveBadge != null) {
                if (isAlreadyCompleted) {
                    liveBadge.setText("🎉 EVENT FULLY CONCLUDED");
                    liveBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16;");
                } else if (!isPaid) {
                    liveBadge.setText("🔒 AWAITING CUSTOMER PAYMENT");
                    liveBadge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16;");
                } else {
                    liveBadge.setText("● LIVE FLOOR OPERATIONS");
                    liveBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16;");
                }
            }

            // Timeline Schedule Rows
            if (timelineListContainer != null) {
                timelineListContainer.getChildren().clear();
                String startTime = (currentEvent.getStartTime() != null && !currentEvent.getStartTime().isBlank()) ? currentEvent.getStartTime() : "10:00";
                String endTime = (currentEvent.getEndTime() != null && !currentEvent.getEndTime().isBlank()) ? currentEvent.getEndTime() : "12:00";

                String guestEntryStatus = checkedIn == 0 ? "Upcoming" : (checkedIn >= totalLimit ? "Completed" : "Arrival In Progress");
                String guestEntryColor = checkedIn == 0 ? SECONDARY_TEXT : (checkedIn >= totalLimit ? GREEN_TEXT : ORANGE_TEXT);

                String vendorServicesStatus = isAlreadyCompleted ? "Completed" : (inProgressVendors > 0 ? "In Progress" : (totalVendors > 0 && completedVendors == totalVendors ? "Completed" : "Active"));
                String vendorServicesColor = isAlreadyCompleted || (totalVendors > 0 && completedVendors == totalVendors) ? GREEN_TEXT : (inProgressVendors > 0 ? ORANGE_TEXT : PRIMARY_BLUE);

                timelineListContainer.getChildren().addAll(
                        createScheduleRow(startTime, "● Event Kickoff & Guest Registration", guestEntryStatus, guestEntryColor, checkedIn > 0 && checkedIn < totalLimit),
                        createScheduleRow("Mid-Event", "● Vendor Deliveries & Main Program", vendorServicesStatus, vendorServicesColor, inProgressVendors > 0),
                        createScheduleRow(endTime, "● Event Wrap-up & Floor Handover", isAlreadyCompleted ? "Completed" : "Upcoming", isAlreadyCompleted ? GREEN_TEXT : SECONDARY_TEXT, false)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void concludeEntireEvent() {
        if (currentEvent == null || currentEvent.getEventId() == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Firestore db = FirestoreClient.getFirestore();
                    if (db != null) {
                        db.collection("events").document(currentEvent.getEventId()).update("status", "COMPLETED").get();
                        currentEvent.setStatus("COMPLETED");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                renderLiveEventDashboardUI();
                showInAppModal("Event Concluded Successfully", "The event is now officially marked as COMPLETED. Admin dashboard analytics, reports and customer view are synchronized!");
            });
        });

        new Thread(task).start();
    }

    private void showInAppModal(String title, String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private VBox createCommonOrganizerSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void detachListeners() {
        if (eventListener != null) {
            eventListener.remove();
            eventListener = null;
        }
        if (bookingsListener != null) {
            bookingsListener.remove();
            bookingsListener = null;
        }
    }

    private void handleNav(String screen) {
        detachListeners();
        switch (screen) {
            case "Dashboard" -> {
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
            }
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> handleBack();
            case "Schedule" -> {
                ScheduleO view = new ScheduleO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::handleBack));
            }
            case "Notifications" -> {
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
            }
            case "Profile" -> {
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
            }
            case "Settings" -> {
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
            }
            case "Support" -> {
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
            }
        }
    }

    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack());

        Label bcSep1 = new Label(" / ");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("Event Hub");
        bc2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc2.setOnMouseClicked(e -> handleBack());

        Label bcSep2 = new Label(" / ");
        bcSep2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc3 = new Label("Live Event Operations");
        bc3.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpace = new Region();
        HBox.setHgrow(bcSpace, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Hub");
        backBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-border-radius: 6px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> handleBack());

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSep2, bc3, bcSpace, backBtn);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        eventTitleLabel = new Label("Live Event Operations Dashboard");
        eventTitleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        liveBadge = new Label("● LIVE OPERATIONS");
        liveBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(eventTitleLabel, headerSpacer, liveBadge);

        // KPI Row
        HBox summaryRow = new HBox(14);
        checkedInValLabel = new Label("0/0");
        activeVendorsValLabel = new Label("0/0");

        summaryRow.getChildren().addAll(
                createKpiPill("Guests Attending", checkedInValLabel, "Live Check-In Counter"),
                createKpiPill("Vendor Milestones", activeVendorsValLabel, "On-Floor Execution"),
                createKpiPill("Venue Access", new Label("READY"), "Verified Logistics"),
                createKpiPill("Timeline Execution", new Label("ON TIME"), "Real-Time Schedule Flow")
        );

        // Body 2-Column Grid
        HBox bodyGrid = new HBox(20);

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createHappeningNowCard(),
                createTodayScheduleCard()
        );

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(360);
        rightCol.getChildren().addAll(
                createVendorStatusCard(),
                createLiveExecutionSupportCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, headerRow, summaryRow, bodyGrid);
        return mainBox;
    }

    private VBox createKpiPill(String title, Label val, String sub) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title); t.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(t, val, s);
        return card;
    }

    private VBox createHappeningNowCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Overall Event Execution Status");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        progressPctLabel = new Label("0% Executed");
        progressPctLabel.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 4;");

        topRow.getChildren().addAll(header, spacer, progressPctLabel);

        mainProgressBar = new ProgressBar(0.0);
        mainProgressBar.setMaxWidth(Double.MAX_VALUE);
        mainProgressBar.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        card.getChildren().addAll(topRow, mainProgressBar);
        return card;
    }

    private VBox createTodayScheduleCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Live Schedule & Execution Timeline");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        timelineListContainer = new VBox(8);
        card.getChildren().addAll(title, timelineListContainer);
        return card;
    }

    private HBox createScheduleRow(String time, String name, String statusText, String statusColor, boolean highlight) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));

        row.setStyle(highlight
                ? "-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6; -fx-background-radius: 6;"
                : "-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6;");

        Label t = new Label(time);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label n = new Label(name);
        n.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label s = new Label(statusText);
        s.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

        row.getChildren().addAll(t, n, spacer, s);
        return row;
    }

    private VBox createVendorStatusCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Live Vendor Delivery Milestones");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        vendorStatusContainer = new VBox(6);
        card.getChildren().addAll(title, new Separator(), vendorStatusContainer);
        return card;
    }

    private HBox createVendorRow(String name, String status, String bgCol, String textCol) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label n = new Label(name); n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label s = new Label(status);
        s.setStyle("-fx-background-color: " + bgCol + "; -fx-text-fill: " + textCol + "; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
        row.getChildren().addAll(n, spacer, s);
        return row;
    }

    private VBox createLiveExecutionSupportCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + NAVY + "; -fx-background-radius: 10;");

        Label title = new Label("⚡ Coordinator Controls");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label msg = new Label("Once all vendors complete their services, click below to officially conclude the event. This instantly closes the event across Admin and Customer portals.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 11px; -fx-text-fill: #DCE5F0;");

        btnCompleteFullEvent = new Button("🎉 Finish & Conclude Event");
        btnCompleteFullEvent.setMaxWidth(Double.MAX_VALUE);
        btnCompleteFullEvent.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9; -fx-background-radius: 6;");
        btnCompleteFullEvent.setOnAction(e -> concludeEntireEvent());

        card.getChildren().addAll(title, msg, btnCompleteFullEvent);
        return card;
    }

    private void handleBack() {
        detachListeners();
        if (backAction != null) {
            backAction.run();
        } else {
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(this::goDashboard));
        }
    }

    private void goDashboard() {
        detachListeners();
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }
}