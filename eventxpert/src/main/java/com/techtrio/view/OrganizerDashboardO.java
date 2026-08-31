package com.techtrio.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
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
import javafx.scene.shape.SVGPath;

public class OrganizerDashboardO {

    private Scene organizerDashboardScene;
    private String uid;
    private String idToken;

    // COLOR PALETTE
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String AMBER_TEXT = "#B06000";

    private static final String FONT_FAMILY = "-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;";

    // SVG Icons
    private static final String SVG_DASHBOARD = "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z";
    private static final String SVG_CALENDAR = "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z";
    private static final String SVG_USERS = "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z";
    private static final String SVG_BELL = "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z";
    private static final String SVG_SEARCH = "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z";
    private static final String SVG_CHECK = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z";
    private static final String SVG_ALERT = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";

    // Dynamic Labels
    private Label greetingLabel;
    private Label userTopNameLabel;
    private Label sidebarOrgNameLabel;

    private Label activeEventsValLabel;
    private Label pendingRequestsValLabel;
    private Label upcomingEventsValLabel;
    private Label tasksDueValLabel;

    private VBox upcomingEventsTableBox;
    private VBox tasksContainerBox;

    public OrganizerDashboardO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public OrganizerDashboardO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getorganizerDashboardScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; " + FONT_FAMILY);

        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        VBox rightContainer = new VBox();
        HBox topBar = createTopBar();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox dashboardContent = createDashboardContent();
        scrollPane.setContent(dashboardContent);

        rightContainer.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setCenter(rightContainer);

        loadOrganizerDashboardDataAsync();

        organizerDashboardScene = new Scene(root, 1550, 785);
        return organizerDashboardScene;
    }

    // =========================================================================
    // DYNAMIC FIRESTORE DATA LOADER
    // =========================================================================
    private void loadOrganizerDashboardDataAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                // 1. Fetch Organizer Profile Info
                OrganizerController controller = new OrganizerController();
                OrganizerProfile profile = controller.getOrganizerProfile(uid);

                Platform.runLater(() -> {
                    if (profile != null) {
                        String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Organizer";
                        if (greetingLabel != null) greetingLabel.setText("Good Morning, " + name + " 👋");
                        if (userTopNameLabel != null) userTopNameLabel.setText(name);
                        if (sidebarOrgNameLabel != null && profile.getOrganizationName() != null && !profile.getOrganizationName().isBlank()) {
                            sidebarOrgNameLabel.setText(profile.getOrganizationName());
                        }
                    }
                });

                // 2. Fetch Events Assigned specifically to this Organizer
                EventService eventService = new EventService();
                List<Event> assignedEvents = eventService.getEventsByOrganizerId(uid);

                // 3. Fetch All Pending Customer Event Requests from Firestore
                List<Event> pendingIncomingRequests = controller.getPendingEventRequests();

                Platform.runLater(() -> {
                    int activeCount = 0;
                    int upcomingCount = 0;
                    int pendingCount = (pendingIncomingRequests != null) ? pendingIncomingRequests.size() : 0;

                    if (upcomingEventsTableBox != null) {
                        upcomingEventsTableBox.getChildren().clear();
                        upcomingEventsTableBox.getChildren().add(createTableHeaderRow());
                    }

                    boolean hasConfirmedEvents = false;

                    if (assignedEvents != null && !assignedEvents.isEmpty()) {
                        for (Event ev : assignedEvents) {
                            String oStatus = ev.getOrganizerStatus() != null ? ev.getOrganizerStatus().toUpperCase() : "";
                            String evStatus = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "";

                            // FAKTA ACCEPTED / CONFIRMED ZALELICH EVENTS UPCOMING MADHYE DAKHVA
                            if ("ACCEPTED".equals(oStatus) || "CONFIRMED".equals(evStatus)) {
                                activeCount++;
                                upcomingCount++;
                                hasConfirmedEvents = true;

                                if (upcomingEventsTableBox != null) {
                                    String dateLoc = (ev.getEventDate() != null ? ev.getEventDate() : "TBD") +
                                            "\n" + (ev.getCity() != null ? ev.getCity() : "India");

                                    upcomingEventsTableBox.getChildren().add(
                                            createEventRow(
                                                    ev.getEventId(),
                                                    ev.getEventName() != null ? ev.getEventName() : "Unnamed Event",
                                                    dateLoc,
                                                    String.valueOf(ev.getGuests()),
                                                    "In Planning",
                                                    LIGHT_BLUE,
                                                    PRIMARY_BLUE
                                            )
                                    );
                                }
                            }
                        }
                    }

                    if (!hasConfirmedEvents) {
                        renderEmptyEventsState();
                    }

                    activeEventsValLabel.setText(String.format("%02d", activeCount));
                    pendingRequestsValLabel.setText(String.format("%02d", pendingCount));
                    upcomingEventsValLabel.setText(String.format("%02d", upcomingCount));
                    tasksDueValLabel.setText("00");

                    renderEmptyTasksState();
                });

                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderEmptyEventsState() {
        if (upcomingEventsTableBox == null) return;
        upcomingEventsTableBox.getChildren().clear();

        VBox emptyBox = new VBox(8);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(30));

        Label icon = new Label("📅"); icon.setStyle("-fx-font-size: 28px;");
        Label title = new Label("No active events in planning");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label sub = new Label("Accept new requests from 'Event Requests' to start planning.");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        emptyBox.getChildren().addAll(icon, title, sub);
        upcomingEventsTableBox.getChildren().add(emptyBox);
    }

    private void renderEmptyTasksState() {
        if (tasksContainerBox == null) return;
        tasksContainerBox.getChildren().clear();

        VBox emptyBox = new VBox(6);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(20));

        Label icon = new Label("✓"); icon.setStyle("-fx-font-size: 24px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label title = new Label("No tasks due today");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label sub = new Label("You're all caught up on event tasks.");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        emptyBox.getChildren().addAll(icon, title, sub);
        tasksContainerBox.getChildren().add(emptyBox);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(12);

        VBox brandBox = new VBox(2);
        brandBox.setPadding(new Insets(0, 0, 10, 8));

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        SVGPath logoIcon = createSVGIcon(SVG_CALENDAR, WHITE, 1.2);
        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");
        logoBox.getChildren().addAll(logoIcon, brandName);

        Label catHeader = new Label("EVENT MANAGEMENT");
        catHeader.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #94A3B8; -fx-padding: 8 0 0 0;");

        sidebarOrgNameLabel = new Label("Organizer Portal");
        sidebarOrgNameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D1E0FF;");

        brandBox.getChildren().addAll(logoBox, catHeader, sidebarOrgNameLabel);

        VBox navBox = new VBox(6);
        navBox.getChildren().add(createSidebarItem("Dashboard", SVG_DASHBOARD, true, "", () -> {}));
        navBox.getChildren().add(createSidebarItem("Event Requests", SVG_CALENDAR, false, "", this::openEventRequests));
        navBox.getChildren().add(createSidebarItem("My Events", SVG_CALENDAR, false, "", this::openMyEvents));
        navBox.getChildren().add(createSidebarItem("Venue", SVG_DASHBOARD, false, "", this::openVenue));
        navBox.getChildren().add(createSidebarItem("Vendors", SVG_USERS, false, "", this::openVendors));
        navBox.getChildren().add(createSidebarItem("Schedule", SVG_CALENDAR, false, "", this::openSchedule));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomBox = new VBox(6);
        bottomBox.getChildren().add(createSidebarItem("Notifications", SVG_BELL, false, "", this::openNotifications));
        bottomBox.getChildren().add(createSidebarItem("Settings", SVG_DASHBOARD, false, "", this::openSettings));
        bottomBox.getChildren().add(createSidebarItem("Profile", SVG_USERS, false, "", this::openProfile));

        sidebar.getChildren().addAll(brandBox, navBox, spacer, bottomBox);
        return sidebar;
    }

    private HBox createSidebarItem(String title, String svgData, boolean isActive, String badgeCount, Runnable action) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setCursor(javafx.scene.Cursor.HAND);

        SVGPath icon = createSVGIcon(svgData, isActive ? PRIMARY_BLUE : "#94A3B8", 1.0);
        Label label = new Label(title);

        if (isActive) {
            item.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px;");
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        } else {
            item.setStyle("-fx-background-color: transparent;");
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: " + WHITE + ";");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        item.getChildren().addAll(icon, label, spacer);

        if (!badgeCount.isEmpty()) {
            Label badge = new Label(badgeCount);
            badge.setStyle("-fx-background-color: " + BRIGHT_BLUE + "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 2 6;");
            item.getChildren().add(badge);
        }

        if (action != null) {
            item.setOnMouseClicked(e -> action.run());
        }

        return item;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPrefHeight(65);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setPadding(new Insets(8, 12, 8, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        SVGPath searchIcon = createSVGIcon(SVG_SEARCH, SECONDARY_TEXT, 0.9);
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search events, vendors, guests...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightNav = new HBox(15);
        rightNav.setAlignment(Pos.CENTER);

        Button bellBtn = createIconButton(SVG_BELL);
        bellBtn.setOnAction(e -> openNotifications());

        Button helpBtn = createIconButton(SVG_ALERT);

        VBox userBox = new VBox(2);
        userBox.setAlignment(Pos.CENTER_RIGHT);

        userTopNameLabel = new Label("Organizer");
        userTopNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label userRole = new Label("Organizer Portal");
        userRole.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        userBox.getChildren().addAll(userTopNameLabel, userRole);

        Circle userAvatar = new Circle(16, Color.web(BRIGHT_BLUE));
        userAvatar.setStyle("-fx-cursor: hand;");
        userAvatar.setOnMouseClicked(e -> openProfile());

        rightNav.getChildren().addAll(bellBtn, helpBtn, userBox, userAvatar);

        topBar.getChildren().addAll(searchBox, spacer, rightNav);
        return topBar;
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        greetingLabel = new Label("Good Morning 👋");
        greetingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subGreeting = new Label("Here's what's happening with your events today.");
        subGreeting.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(greetingLabel, subGreeting);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox dateBox = new VBox(2);
        dateBox.setAlignment(Pos.TOP_RIGHT);
        Label dayLbl = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE")));
        dayLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");
        Label fullDateLbl = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        fullDateLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        dateBox.getChildren().addAll(dayLbl, fullDateLbl);

        header.getChildren().addAll(titleBox, spacer, dateBox);

        HBox topCardsRow = new HBox(15);

        activeEventsValLabel = new Label("00");
        pendingRequestsValLabel = new Label("00");
        upcomingEventsValLabel = new Label("00");
        tasksDueValLabel = new Label("00");

        HBox kpiGroup = new HBox(12);
        HBox.setHgrow(kpiGroup, Priority.ALWAYS);
        kpiGroup.getChildren().addAll(
                createSummaryCard(activeEventsValLabel, "Active Events", SVG_CALENDAR, PRIMARY_BLUE),
                createSummaryCard(pendingRequestsValLabel, "Pending Requests", SVG_ALERT, AMBER_TEXT),
                createSummaryCard(upcomingEventsValLabel, "Upcoming Events", SVG_CALENDAR, BRIGHT_BLUE),
                createSummaryCard(tasksDueValLabel, "Tasks Due Today", SVG_CHECK, GREEN_TEXT)
        );

        VBox smartAlertsCard = createSmartAlertsCard();
        smartAlertsCard.setPrefWidth(280);

        topCardsRow.getChildren().addAll(kpiGroup, smartAlertsCard);

        HBox mainGrid = new HBox(20);

        VBox leftColumn = new VBox(20);
        leftColumn.prefWidthProperty().bind(mainGrid.widthProperty().multiply(0.65));

        leftColumn.getChildren().addAll(
                createUpcomingEventsTableCard(),
                createGlobalHealthCard()
        );

        VBox rightColumn = new VBox(20);
        rightColumn.prefWidthProperty().bind(mainGrid.widthProperty().multiply(0.35));

        rightColumn.getChildren().addAll(
                createTasksCard(),
                createQuickActionsCard()
        );

        mainGrid.getChildren().addAll(leftColumn, rightColumn);

        content.getChildren().addAll(header, topCardsRow, mainGrid);
        return content;
    }

    private VBox createSummaryCard(Label valLabel, String label, String svgPath, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconPane = new StackPane();
        Circle bg = new Circle(18, Color.web(color).deriveColor(0, 1, 1, 0.12));
        SVGPath icon = createSVGIcon(svgPath, color, 0.9);
        iconPane.getChildren().addAll(bg, icon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(iconPane, spacer);

        valLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label descLabel = new Label(label);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, valLabel, descLabel);
        return card;
    }

    private VBox createSmartAlertsCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox header = new HBox(6);
        header.setAlignment(Pos.CENTER_LEFT);
        SVGPath alertIcon = createSVGIcon(SVG_ALERT, PRIMARY_BLUE, 0.85);
        Label title = new Label("Smart Alerts");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        header.getChildren().addAll(alertIcon, title);

        VBox weatherBox = new VBox(2);
        weatherBox.setPadding(new Insets(6, 8, 6, 8));
        weatherBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        Label wTitle = new Label("Weather Update");
        wTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label wDesc = new Label("No urgent weather risks detected.");
        wDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        weatherBox.getChildren().addAll(wTitle, wDesc);

        HBox conflictBox = new HBox(6);
        conflictBox.setAlignment(Pos.CENTER_LEFT);
        conflictBox.setPadding(new Insets(6, 8, 6, 8));
        conflictBox.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-background-radius: 6px;");
        Label cTitle = new Label("✓ No Active Conflicts");
        cTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        conflictBox.getChildren().add(cTitle);

        card.getChildren().addAll(header, weatherBox, conflictBox);
        return card;
    }

    private VBox createUpcomingEventsTableCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Upcoming Events");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewAll = new Button("View All");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        viewAll.setOnAction(e -> openMyEvents());

        header.getChildren().addAll(title, spacer, viewAll);

        upcomingEventsTableBox = new VBox(0);
        renderEmptyEventsState();

        card.getChildren().addAll(header, upcomingEventsTableBox);
        return card;
    }

    private HBox createTableHeaderRow() {
        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 4px;");

        Label col1 = createColumnHeader("EVENT NAME", 0.32);
        Label col2 = createColumnHeader("DATE & LOCATION", 0.28);
        Label col3 = createColumnHeader("GUESTS", 0.12);
        Label col4 = createColumnHeader("STATUS", 0.18);
        Label col5 = createColumnHeader("ACTION", 0.10);

        tableHeader.getChildren().addAll(col1, col2, col3, col4, col5);
        return tableHeader;
    }

    private Label createColumnHeader(String text, double widthPercent) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return label;
    }

    private HBox createEventRow(String eventId, String name, String dateLoc, String guests, String status, String statusBg, String statusFg) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label dateLbl = new Label(dateLoc);
        dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label guestsLbl = new Label(guests);
        guestsLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label statusLbl = new Label("• " + status);
        statusLbl.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusFg + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 12px;");

        Button actionBtn = new Button("View");
        actionBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
        actionBtn.setOnAction(e -> {
            AdminContext.setSelectedEventId(eventId);
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::backToOrganizerDashboard));
        });

        HBox c1 = new HBox(nameLbl); HBox c2 = new HBox(dateLbl); HBox c3 = new HBox(guestsLbl); HBox c4 = new HBox(statusLbl); HBox c5 = new HBox(actionBtn);

        c1.prefWidthProperty().bind(row.widthProperty().multiply(0.32));
        c2.prefWidthProperty().bind(row.widthProperty().multiply(0.28));
        c3.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
        c4.prefWidthProperty().bind(row.widthProperty().multiply(0.18));
        c5.prefWidthProperty().bind(row.widthProperty().multiply(0.10));

        row.getChildren().addAll(c1, c2, c3, c4, c5);
        return row;
    }

    private VBox createGlobalHealthCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label title = new Label("Global Event Health");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox metrics = new HBox(20);

        VBox budgetBox = createHealthMetric("Budget Utilization", "0%", "₹0 / ₹0", 0.0, PRIMARY_BLUE);
        VBox vendorBox = createHealthMetric("Vendor Status", "0%", "0/0 Confirmed", 0.0, BRIGHT_BLUE);
        VBox guestBox = createHealthMetric("Guest Check-In", "0%", "0/0 Arrived", 0.0, NAVY);

        budgetBox.prefWidthProperty().bind(metrics.widthProperty().multiply(0.33));
        vendorBox.prefWidthProperty().bind(metrics.widthProperty().multiply(0.33));
        guestBox.prefWidthProperty().bind(metrics.widthProperty().multiply(0.33));

        metrics.getChildren().addAll(budgetBox, vendorBox, guestBox);
        card.getChildren().addAll(title, metrics);
        return card;
    }

    private VBox createHealthMetric(String label, String percentStr, String subtext, double progress, String color) {
        VBox box = new VBox(6);

        HBox topRow = new HBox();
        Label titleLbl = new Label(label);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label percentLbl = new Label(percentStr);
        percentLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        topRow.getChildren().addAll(titleLbl, spacer, percentLbl);

        ProgressBar bar = new ProgressBar(progress);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + color + "; -fx-control-inner-background: " + BACKGROUND + ";");

        Label subLbl = new Label(subtext);
        subLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(topRow, bar, subLbl);
        return box;
    }

    private VBox createTasksCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label title = new Label("Today's Tasks");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        tasksContainerBox = new VBox(8);
        renderEmptyTasksState();

        card.getChildren().addAll(title, tasksContainerBox);
        return card;
    }

    private VBox createQuickActionsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 8px;");

        Label title = new Label("⚡ Quick Actions");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(createActionButton("Review Requests", this::openEventRequests), 0, 0);
        grid.add(createActionButton("Open My Events", this::openMyEvents), 1, 0);
        grid.add(createActionButton("Add Expense", this::openBudget), 0, 1);
        grid.add(createActionButton("Check Vendors", this::openVendors), 1, 1);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            grid.getColumnConstraints().add(cc);
        }

        card.getChildren().addAll(title, grid);
        return card;
    }

    private Button createActionButton(String label, Runnable action) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 6px; -fx-cursor: hand;");
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    // NAVIGATION HANDLERS
    private void openEventRequests() {
        EventRequestsO view = new EventRequestsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(this::backToOrganizerDashboard));
    }

    private void openMyEvents() {
        MyEventsO view = new MyEventsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getMyEventsOScene(this::backToOrganizerDashboard));
    }

    private void openVenue() {
        VenueManagementO view = new VenueManagementO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::backToOrganizerDashboard));
    }

    private void openVendors() {
        VendorManagementO view = new VendorManagementO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(this::backToOrganizerDashboard));
    }

    private void openSchedule() {
        ScheduleO view = new ScheduleO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::backToOrganizerDashboard));
    }

    private void openBudget() {
        BudgetExpenseTrackerO view = new BudgetExpenseTrackerO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::backToOrganizerDashboard));
    }

    private void openNotifications() {
        NotificationsO view = new NotificationsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getNotificationScene());
    }

    private void openSettings() {
        SettingsO view = new SettingsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getSettingScene(this::backToOrganizerDashboard));
    }

    private void openProfile() {
        ProfileO view = new ProfileO(uid, idToken);
        view.setOrganizerDashboard(this);
        LandingPage.landingPageStage.setScene(view.getProfileScene());
    }

    private Button createIconButton(String svgPath) {
        Button btn = new Button();
        btn.setGraphic(createSVGIcon(svgPath, SECONDARY_TEXT, 0.9));
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
        return btn;
    }

    private SVGPath createSVGIcon(String content, String colorHex, double scale) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(Color.web(colorHex));
        svgPath.setScaleX(scale);
        svgPath.setScaleY(scale);
        return svgPath;
    }

    public void backToOrganizerDashboard() {
        if (organizerDashboardScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(organizerDashboardScene);
        }
    }
}