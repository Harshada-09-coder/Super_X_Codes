package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.Event;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MyEventsO {

    private Scene MyEventsOScene;
    private String uid;
    private String idToken;

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Status Badges
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";

    // Dynamic UI Elements
    private Label lblActiveEvents;
    private Label lblUpcomingEvents;
    private Label lblCompletedEvents;
    private Label lblPendingTasks;

    private VBox centerEventsColumn;
    private VBox nextApproachingContainer;

    public MyEventsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public MyEventsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getMyEventsOScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setLeft(createSidebar(callBackAction));
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 30, 30, 30));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Header
        VBox titleBox = new VBox(4);
        Label title = new Label("My Events");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Manage and monitor all events assigned to you.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        // Top Summary Metric Cards Row
        HBox metricCardsRow = createMetricCardsRow();

        // Filter Bar Card
        VBox filterCard = createFilterBarCard();

        // Main Content Split Grid: Dynamic Center Event Cards & Right Sidebar Widgets
        HBox mainGrid = new HBox(20);

        centerEventsColumn = new VBox(20);
        HBox.setHgrow(centerEventsColumn, Priority.ALWAYS);

        // Right Column Widgets
        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(330);
        rightColumn.setMinWidth(310);

        nextApproachingContainer = new VBox();
        rightColumn.getChildren().addAll(
                nextApproachingContainer,
                createSmartAlertsCard()
        );

        mainGrid.getChildren().addAll(centerEventsColumn, rightColumn);

        content.getChildren().addAll(titleBox, metricCardsRow, filterCard, mainGrid);
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        loadAcceptedEventsFromFirestoreAsync();

        MyEventsOScene = new Scene(root, 1550, 785);
        return MyEventsOScene;
    }

    // ==========================================
    // FIRESTORE DATA LOADER
    // ==========================================
    private void loadAcceptedEventsFromFirestoreAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                EventService eventService = new EventService();
                return eventService.getEventsByOrganizerId(uid);
            }
        };

        task.setOnSucceeded(evt -> {
            List<Event> assignedList = task.getValue();
            Platform.runLater(() -> {
                if (centerEventsColumn != null) centerEventsColumn.getChildren().clear();
                if (nextApproachingContainer != null) nextApproachingContainer.getChildren().clear();

                int activeCount = 0;
                int upcomingCount = 0;
                int completedCount = 0;

                Event nextApproachingEvent = null;

                if (assignedList != null) {
                    for (Event ev : assignedList) {
                        String oStatus = ev.getOrganizerStatus() != null ? ev.getOrganizerStatus().toUpperCase() : "";
                        String status = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "";

                        // Filter ONLY events ACCEPTED by organizer or CONFIRMED
                        if ("ACCEPTED".equals(oStatus) || "CONFIRMED".equals(status) || "COMPLETED".equals(status)) {
                            if ("COMPLETED".equals(status)) {
                                completedCount++;
                            } else {
                                activeCount++;
                                upcomingCount++;
                                if (nextApproachingEvent == null) {
                                    nextApproachingEvent = ev;
                                }
                            }

                            if (centerEventsColumn != null) {
                                centerEventsColumn.getChildren().add(createDynamicEventCard(ev));
                            }
                        }
                    }
                }

                // Update Metric Labels
                if (lblActiveEvents != null) lblActiveEvents.setText(String.format("%02d", activeCount));
                if (lblUpcomingEvents != null) lblUpcomingEvents.setText(String.format("%02d", upcomingCount));
                if (lblCompletedEvents != null) lblCompletedEvents.setText(String.format("%02d", completedCount));
                if (lblPendingTasks != null) lblPendingTasks.setText("00");

                // Render Next Approaching Widget or Empty State
                if (nextApproachingContainer != null) {
                    if (nextApproachingEvent != null) {
                        nextApproachingContainer.getChildren().add(createNextApproachingWidget(nextApproachingEvent));
                    } else {
                        nextApproachingContainer.getChildren().add(createNoApproachingWidget());
                    }
                }

                // Empty state if no accepted events exist
                if (centerEventsColumn != null && centerEventsColumn.getChildren().isEmpty()) {
                    renderEmptyEventsState();
                }
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderEmptyEventsState() {
        VBox emptyCard = new VBox(10);
        emptyCard.setAlignment(Pos.CENTER);
        emptyCard.setPadding(new Insets(40));
        emptyCard.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label icon = new Label("📅"); icon.setStyle("-fx-font-size: 32px;");
        Label title = new Label("No Confirmed Events Yet");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label sub = new Label("Accept assigned event requests to start managing them here.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        emptyCard.getChildren().addAll(icon, title, sub);
        centerEventsColumn.getChildren().add(emptyCard);
    }

    // ==========================================
    // TOP SUMMARY METRIC CARDS
    // ==========================================
    private HBox createMetricCardsRow() {
        HBox row = new HBox(16);
        lblActiveEvents = new Label("00");
        lblUpcomingEvents = new Label("00");
        lblCompletedEvents = new Label("00");
        lblPendingTasks = new Label("00");

        row.getChildren().addAll(
                createKpiCard("ACTIVE EVENTS", lblActiveEvents, "📅", PRIMARY_BLUE),
                createKpiCard("UPCOMING", lblUpcomingEvents, "⏰", BRIGHT_BLUE),
                createKpiCard("COMPLETED", lblCompletedEvents, "✓", GREEN_TEXT),
                createKpiCard("PENDING TASKS", lblPendingTasks, "📋", "#C5221F")
        );
        return row;
    }

    private VBox createKpiCard(String label, Label valLbl, String iconStr, String colorHex) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 14px; -fx-text-fill: " + colorHex + ";");

        topRow.getChildren().addAll(lbl, spacer, icon);

        valLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        card.getChildren().addAll(topRow, valLbl);
        return card;
    }

    private VBox createFilterBarCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search events by name...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchInput);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setPromptText("Status");
        statusCombo.getItems().addAll("All Statuses", "In Planning", "Confirmed", "Completed");
        statusCombo.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-font-size: 12px;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("Type");
        typeCombo.getItems().addAll("All Types", "Corporate Conference", "Corporate Event", "Networking Event");
        typeCombo.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-font-size: 12px;");

        Button clearBtn = new Button("Clear Filters");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px; -fx-cursor: hand;");

        filterRow.getChildren().addAll(searchBox, statusCombo, typeCombo, clearBtn);
        card.getChildren().add(filterRow);
        return card;
    }

    // ==========================================
    // DYNAMIC FIRESTORE EVENT CARD
    // ==========================================
    private VBox createDynamicEventCard(Event ev) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label catBadge = new Label(ev.getEventType() != null ? ev.getEventType() : "General Event");
        catBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12px;");

        String statusStr = ev.getStatus() != null ? ev.getStatus() : "CONFIRMED";
        boolean isConfirmed = "CONFIRMED".equalsIgnoreCase(statusStr);

        Label statusBadge = new Label(isConfirmed ? "Confirmed" : "In Planning");
        statusBadge.setStyle("-fx-background-color: " + (isConfirmed ? GREEN_BG : LIGHT_BLUE) + "; -fx-text-fill: " + (isConfirmed ? GREEN_TEXT : BRIGHT_BLUE) + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button("View Planning →");
        actionBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        actionBtn.setOnAction(e -> openViewEvent(ev.getEventId()));

        topRow.getChildren().addAll(catBadge, statusBadge, spacer, actionBtn);

        VBox titleBox = new VBox(4);
        Label eventTitle = new Label(ev.getEventName() != null ? ev.getEventName() : "Unnamed Event");
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox metaRow = new HBox(16);
        metaRow.getChildren().addAll(
                new Label("📅 " + (ev.getEventDate() != null ? ev.getEventDate() : "TBD")),
                new Label("🏢 " + (ev.getCity() != null ? ev.getCity() : "N/A")),
                new Label("👥 " + ev.getGuests() + " Guests")
        );
        for (javafx.scene.Node n : metaRow.getChildren()) {
            n.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        }
        titleBox.getChildren().addAll(eventTitle, metaRow);

        VBox progressSection = new VBox(8);
        HBox pHeader = new HBox();
        Label pTitle = new Label("Planning Progress");
        pTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);

        Label pVal = new Label("65%");
        pVal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");
        pHeader.getChildren().addAll(pTitle, pSpacer, pVal);

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        HBox stepsRow = new HBox(8);
        stepsRow.setAlignment(Pos.CENTER_LEFT);
        stepsRow.getChildren().addAll(
                createStepBadge("✓ Reqs", true),
                createStepBadge("✓ Venue", true),
                createStepBadge("✓ Budget", true),
                createStepBadge("◉ Vendors", true),
                createStepBadge("○ Schedule", false)
        );

        progressSection.getChildren().addAll(pHeader, pb, stepsRow);

        card.getChildren().addAll(topRow, titleBox, new Separator(), progressSection);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> openViewEvent(ev.getEventId()));
        return card;
    }

    private Label createStepBadge(String text, boolean active) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px; " +
                (active ? "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + ";"
                        : "-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + SECONDARY_TEXT + ";"));
        return l;
    }

    private VBox createNextApproachingWidget(Event ev) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 12px;");

        HBox headRow = new HBox();
        headRow.setAlignment(Pos.CENTER_LEFT);
        Label headerLabel = new Label("⚡ NEXT APPROACHING");
        headerLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #D1E0FF;");
        headRow.getChildren().add(headerLabel);

        VBox titleBox = new VBox(2);
        Label eventName = new Label(ev.getEventName() != null ? ev.getEventName() : "Event");
        eventName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label dateLabel = new Label((ev.getEventDate() != null ? ev.getEventDate() : "TBD") + " • " + ev.getGuests() + " Guests");
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #EAF3FF;");
        titleBox.getChildren().addAll(eventName, dateLabel);

        Button contBtn = new Button("Continue Planning →");
        contBtn.setMaxWidth(Double.MAX_VALUE);
        contBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");
        contBtn.setOnAction(e -> openViewEvent(ev.getEventId()));

        card.getChildren().addAll(headRow, titleBox, contBtn);
        return card;
    }

    private VBox createNoApproachingWidget() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 12px;");
        Label lbl = new Label("⚡ NEXT APPROACHING");
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #D1E0FF;");
        Label sub = new Label("No upcoming approaching events.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        card.getChildren().addAll(lbl, sub);
        return card;
    }

    private VBox createSmartAlertsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label title = new Label("✨ Smart Alerts");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createAlertCallout("Weather Alert (Rain)", "Assigned events have 60% chance of rain. Ensure indoor fallback.", LIGHT_BLUE, BRIGHT_BLUE),
                createAlertCallout("Conflict Detection: Clear", "No scheduling overlaps detected across active events.", BACKGROUND, GREEN_TEXT),
                createAlertCallout("Budget Status: On Track", "Overall spending across active events is 12% below projected estimates.", BACKGROUND, SECONDARY_TEXT)
        );

        card.getChildren().addAll(title, list);
        return card;
    }

    private VBox createAlertCallout(String header, String body, String bgHex, String headerCol) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + bgHex + "; -fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        Label h = new Label(header);
        h.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + headerCol + ";");

        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        box.getChildren().addAll(h, b);
        return box;
    }

    private void openViewEvent(String eventId) {
        if (eventId != null && !eventId.isBlank()) {
            AdminContext.setSelectedEventId(eventId);
        }
        ViewEventO eventView = new ViewEventO(uid, idToken);
        LandingPage.landingPageStage.setScene(eventView.getScene(LandingPage.landingPageStage, () -> {
            LandingPage.landingPageStage.setScene(MyEventsOScene);
        }));
    }

    private VBox createSidebar(Runnable backAction) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, () -> openDashboard(backAction)));
        menu.getChildren().add(createNavBtn("Event Requests", false, this::openEventRequests));
        menu.getChildren().add(createNavBtn("My Events", true, () -> {}));
        menu.getChildren().add(createNavBtn("Venue", false, this::openVenue));
        menu.getChildren().add(createNavBtn("Vendors", false, this::openVendors));
        menu.getChildren().add(createNavBtn("Schedule", false, this::openSchedule));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottom = new VBox(6);
        bottom.getChildren().add(createNavBtn("Notifications", false, this::openNotifications));
        bottom.getChildren().add(createNavBtn("Settings", false, this::openSettings));
        bottom.getChildren().add(createNavBtn("Profile", false, this::openProfile));

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer, bottom);
        return sidebar;
    }

    private Button createNavBtn(String title, boolean active, Runnable action) {
        Button b = new Button(title);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(active ? "-fx-background-color: #EAF3FF; -fx-text-fill: #0B4EA2; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6px;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) b.setOnAction(e -> action.run());
        return b;
    }

    private HBox createTopBar() {
        HBox bar = new HBox();
        bar.setPrefHeight(65);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 30, 0, 30));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setPadding(new Insets(8, 12, 8, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search across events...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightNav = new HBox(15);
        rightNav.setAlignment(Pos.CENTER);

        Button bellBtn = new Button("🔔");
        bellBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        bellBtn.setOnAction(e -> openNotifications());

        Button helpBtn = new Button("✉️");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle userAvatar = new Circle(16, Color.web(BRIGHT_BLUE));
        userAvatar.setStyle("-fx-cursor: hand;");
        userAvatar.setOnMouseClicked(e -> openProfile());

        rightNav.getChildren().addAll(bellBtn, helpBtn, userAvatar);

        bar.getChildren().addAll(searchBox, spacer, rightNav);
        return bar;
    }

    private void openDashboard(Runnable backAction) {
        if (backAction != null) backAction.run();
        else {
            OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
            LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
        }
    }
    private void openEventRequests() {
        EventRequestsO view = new EventRequestsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(this::openDashboardNull));
    }
    private void openVenue() {
        VenueManagementO view = new VenueManagementO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::openDashboardNull));
    }
    private void openVendors() {
        VendorManagementO view = new VendorManagementO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(this::openDashboardNull));
    }
    private void openSchedule() {
        ScheduleO view = new ScheduleO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::openDashboardNull));
    }
    private void openNotifications() {
        NotificationsO view = new NotificationsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getNotificationScene());
    }
    private void openSettings() {
        SettingsO view = new SettingsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getSettingScene(this::openDashboardNull));
    }
    private void openProfile() {
        ProfileO view = new ProfileO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getProfileScene());
    }
    private void openDashboardNull() {
        openDashboard(null);
    }
}