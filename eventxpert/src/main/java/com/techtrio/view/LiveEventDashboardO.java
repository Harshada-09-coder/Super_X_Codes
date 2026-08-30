package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.techtrio.session.UserSession;

public class LiveEventDashboardO {

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Status Colors
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String ORANGE_TEXT = "#B06000";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String RED_BTN = "#A81C1C";

    private String uid;
    private String idToken;
    private Runnable backAction;

    public LiveEventDashboardO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public LiveEventDashboardO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar(stage));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        return new Scene(root, 1550, 785);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search event data...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(18, Color.web(PRIMARY_BLUE));
        Label avatarText = new Label("JD");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        avatar.getChildren().addAll(circle, avatarText);

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, helpBtn, profileBox);
        return topBar;
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setSpacing(6);

        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: white;");

        sidebar.getChildren().addAll(brandName, new Separator(),
            createNavItem("Dashboard", false, () -> goDashboard(stage)),
            createNavItem("Event Requests", false, () -> {
                EventRequestsO view = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(() -> goDashboard(stage)));
            }),
            createNavItem("My Events", false, () -> {
                MyEventsO view = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getMyEventsOScene(() -> goDashboard(stage)));
            }),
            createNavItem("AI Planner", false, () -> {
                AIEventPlannerO view = new AIEventPlannerO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            }),
            createNavItem("Budget", false, () -> {
                BudgetExpenseTrackerO view = new BudgetExpenseTrackerO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            }),
            createNavItem("Venue", false, () -> {
                VenueManagementO view = new VenueManagementO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            }),
            createNavItem("Vendors", false, () -> {
                VendorManagementO view = new VendorManagementO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(() -> handleBack(stage)));
            }),
            createNavItem("Schedule", false, () -> {
                ScheduleO view = new ScheduleO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            }),
            createNavItem("Guest Management", false, () -> {
                GuestManagementO view = new GuestManagementO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            }),
            createNavItem("Live Event", true, () -> {})
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(4);
        bottomMenu.getChildren().addAll(
            new Separator(),
            createNavItem("Settings", false, () -> {
                SettingsO view = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getSettingScene(() -> goDashboard(stage)));
            }),
            createNavItem("Help Center", false, () -> {})
        );

        sidebar.getChildren().addAll(spacer, bottomMenu);
        return sidebar;
    }

    private Button createNavItem(String text, boolean isActive, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(isActive ? "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumbs & Title
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));

        Label bcSep1 = new Label(">");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("TechVision Annual Summit");
        bc2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bcSep2 = new Label(">");
        bcSep2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc3 = new Label("Live Event");
        bc3.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSep2, bc3);

        // Header Row
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Live Event Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label liveBadge = new Label("● LIVE");
        liveBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 6 12 6 12; -fx-background-radius: 16; -fx-font-size: 12px;");

        Label clockLabel = new Label("11:42 AM");
        clockLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        headerRow.getChildren().addAll(title, headerSpacer, liveBadge, clockLabel);

        // Top 5 Summary KPI Cards Row
        HBox summaryRow = new HBox(14);
        summaryRow.getChildren().addAll(
            createSummaryCardProgress("Guests Checked In", "142/250", "56.8% Attendance", 0.568),
            createSummaryCardText("Current Session", "Business Session", "⏰ 11:30 - 12:30"),
            createSummaryCardText("Next Session", "Lunch Break", "Starts at 01:00 PM"),
            createSummaryCardText("Vendors Active", "5/6", "Stable Connection"),
            createSummaryCardText("Open Alerts", "02", "Requires Attention")
        );

        // Main 2-Column Grid Layout
        HBox bodyGrid = new HBox(20);

        // Left Column
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
            createHappeningNowCard(),
            createTodayScheduleCard(),
            createLiveAlertsCard(),
            createVenueStatusCard(),
            createLiveActivityFeedCard()
        );

        // Right Column
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(320);
        rightCol.setMinWidth(300);

        rightCol.getChildren().addAll(
            createEventHealthCard(),
            createLiveQrCheckInCard(),
            createVendorStatusCard(),
            createQuickActionsCard(),
            createAILiveAssistantCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, headerRow, summaryRow, bodyGrid);
        return mainBox;
    }

    // ==========================================
    // TOP KPI CARDS
    // ==========================================
    private VBox createSummaryCardProgress(String title, String val, String sub, double progress) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title); t.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        ProgressBar pb = new ProgressBar(progress);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 4;");

        Label s = new Label(sub); s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(t, v, pb, s);
        return card;
    }

    private VBox createSummaryCardText(String title, String val, String sub) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title); t.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (title.contains("Vendors") ? GREEN_TEXT : SECONDARY_TEXT) + ";");

        card.getChildren().addAll(t, v, s);
        return card;
    }

    // ==========================================
    // LEFT COLUMN CARDS
    // ==========================================
    private VBox createHappeningNowCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Happening Now");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pctBadge = new Label("33% Complete");
        pctBadge.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        topRow.getChildren().addAll(header, spacer, pctBadge);

        HBox bodyRow = new HBox(16);
        bodyRow.setAlignment(Pos.CENTER_LEFT);

        VBox textGroup = new VBox(4);
        HBox.setHgrow(textGroup, Priority.ALWAYS);

        Label sessionTitle = new Label("Business Strategy Session");
        sessionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label details = new Label("Main Hall • Speaker: Sarah Jenkins");
        details.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        ProgressBar pb = new ProgressBar(0.33);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        textGroup.getChildren().addAll(sessionTitle, details, pb);

        StackPane camBox = new StackPane();
        Rectangle bg = new Rectangle(50, 50);
        bg.setArcWidth(8); bg.setArcHeight(8);
        bg.setFill(Color.web(LIGHT_BLUE));
        Label camIcon = new Label("📹");
        camIcon.setStyle("-fx-font-size: 20px;");
        camBox.getChildren().addAll(bg, camIcon);

        bodyRow.getChildren().addAll(textGroup, camBox);

        card.getChildren().addAll(topRow, bodyRow);
        return card;
    }

    private VBox createTodayScheduleCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Today's Schedule");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
            createScheduleRow("09:00 AM", "● Registration & Breakfast", "Completed", GREEN_TEXT, false),
            createScheduleRow("11:30 AM", "● Business Strategy Session", "In Progress", PRIMARY_BLUE, true),
            createScheduleRow("01:00 PM", "● Networking Lunch", "Upcoming", SECONDARY_TEXT, false)
        );

        card.getChildren().addAll(title, list);
        return card;
    }

    private HBox createScheduleRow(String time, String name, String statusText, String statusColor, boolean highlight) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));

        if (highlight) {
            row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        } else {
            row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6;");
        }

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

    private VBox createLiveAlertsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Live Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox alerts = new VBox(8);

        VBox a1 = new VBox(4);
        a1.setPadding(new Insets(10));
        a1.setStyle("-fx-background-color: " + RED_BG + "; -fx-background-radius: 6;");
        Label a1T = new Label("⚠️ Weather Alert");
        a1T.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + RED_TEXT + ";");
        Label a1D = new Label("Rain expected at 2:00 PM. Move outdoor networking to Hall B.");
        a1D.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        a1.getChildren().addAll(a1T, a1D);

        VBox a2 = new VBox(4);
        a2.setPadding(new Insets(10));
        a2.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6;");
        Label a2T = new Label("ℹ️ Catering Update");
        a2T.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label a2D = new Label("Lunch service confirmed for 1:00 PM sharp.");
        a2D.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        a2.getChildren().addAll(a2T, a2D);

        alerts.getChildren().addAll(a1, a2);
        card.getChildren().addAll(title, alerts);
        return card;
    }

    private VBox createVenueStatusCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Venue Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox grid = new HBox(12);
        grid.getChildren().addAll(
            createRoomCapCard("Main Hall", "85% Cap.", PRIMARY_BLUE),
            createRoomCapCard("Dining Hall", "Ready", GREEN_TEXT),
            createRoomCapCard("Parking", "92% Full", ORANGE_TEXT),
            createRoomCapCard("Lounge", "20% Cap.", SECONDARY_TEXT)
        );

        card.getChildren().addAll(title, grid);
        return card;
    }

    private VBox createRoomCapCard(String room, String cap, String colorHex) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6;");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label r = new Label(room);
        r.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label c = new Label(cap);
        c.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + colorHex + ";");

        box.getChildren().addAll(r, c);
        return box;
    }

    private VBox createLiveActivityFeedCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Live Activity Feed");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox feed = new VBox(8);
        feed.getChildren().addAll(
            createFeedItem("● Keynote speaker Sarah Jenkins arrived at venue.", "11:25 AM"),
            createFeedItem("● Main Hall AV check completed successfully.", "11:15 AM"),
            createFeedItem("● Parking Lot A reached 90% capacity.", "11:02 AM")
        );

        card.getChildren().addAll(title, feed);
        return card;
    }

    private VBox createFeedItem(String text, String time) {
        VBox item = new VBox(2);
        Label t = new Label(text); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label tm = new Label(time); tm.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        item.getChildren().addAll(t, tm);
        return item;
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createEventHealthCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Event Health");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox rows = new VBox(6);
        rows.getChildren().addAll(
            createHealthRow("Schedule", "On Track", GREEN_TEXT),
            createHealthRow("Guests", "On Track", GREEN_TEXT),
            createHealthRow("Venue", "Stable", GREEN_TEXT),
            createHealthRow("Vendors", "1 Delayed", RED_TEXT),
            createHealthRow("Weather", "Medium Risk", ORANGE_TEXT)
        );

        card.getChildren().addAll(title, rows);
        return card;
    }

    private HBox createHealthRow(String label, String status, String colorHex) {
        HBox row = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label s = new Label(status); s.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        row.getChildren().addAll(l, spacer, s);
        return row;
    }

    private VBox createLiveQrCheckInCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Live QR Check-In");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox stats = new VBox(4);
        stats.getChildren().addAll(
            createHealthRow("VIP Guests", "42/50", TEXT_MAIN),
            createHealthRow("General", "100/200", TEXT_MAIN)
        );

        VBox latestBox = new VBox(4);
        latestBox.setPadding(new Insets(8));
        latestBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6;");

        Label lHeader = new Label("Latest Check-In");
        lHeader.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);
        StackPane av = new StackPane();
        Circle c = new Circle(12, Color.web(PRIMARY_BLUE));
        Label avT = new Label("RS");
        avT.setStyle("-fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;");
        av.getChildren().addAll(c, avT);

        VBox uInfo = new VBox(1);
        Label uName = new Label("Rahul Sharma");
        uName.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label uTime = new Label("2 mins ago • Main Entrance");
        uTime.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        uInfo.getChildren().addAll(uName, uTime);

        userRow.getChildren().addAll(av, uInfo);
        latestBox.getChildren().addAll(lHeader, userRow);

        card.getChildren().addAll(title, stats, latestBox);
        return card;
    }

    private VBox createVendorStatusCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Live Vendor Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox rows = new VBox(6);
        rows.getChildren().addAll(
            createVendorRow("Gourmet Catering", "ACTIVE", GREEN_BG, GREEN_TEXT),
            createVendorRow("AV Solutions Pro", "ACTIVE", GREEN_BG, GREEN_TEXT),
            createVendorRow("Elite Security", "PREPARING", LIGHT_BLUE, PRIMARY_BLUE)
        );

        card.getChildren().addAll(title, rows);
        return card;
    }

    private HBox createVendorRow(String name, String status, String bgCol, String textCol) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label n = new Label(name); n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label s = new Label(status);
        s.setStyle("-fx-background-color: " + bgCol + "; -fx-text-fill: " + textCol + "; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6 2 6; -fx-background-radius: 4;");
        row.getChildren().addAll(n, spacer, s);
        return row;
    }

    private VBox createQuickActionsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Quick Actions");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);

        Button scanBtn = createActionButton("📱 Scan QR", PRIMARY_BLUE, "white");
        Button reportBtn = createActionButton("⚠️ Report Issue", RED_BTN, "white");
        Button announceBtn = createActionButton("📻 Announce", LIGHT_BLUE, PRIMARY_BLUE);
        Button aiBtn = createActionButton("🤖 Contact AI", LIGHT_BLUE, PRIMARY_BLUE);

        grid.add(scanBtn, 0, 0);
        grid.add(reportBtn, 1, 0);
        grid.add(announceBtn, 0, 1);
        grid.add(aiBtn, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        card.getChildren().addAll(title, grid);
        return card;
    }

    private Button createActionButton(String text, String bgCol, String textCol) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: " + bgCol + "; -fx-text-fill: " + textCol + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 10; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    private VBox createAILiveAssistantCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + NAVY + "; -fx-background-radius: 10;");

        Label title = new Label("⚙️ AI Live Assistant");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label msg = new Label("\"Based on current check-in rates, I recommend opening the second buffet line 10 minutes early to avoid congestion.\"");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 11px; -fx-text-fill: #DCE5F0;");

        Button applyBtn = new Button("Apply Insight");
        applyBtn.setMaxWidth(Double.MAX_VALUE);
        applyBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 8; -fx-background-radius: 6; -fx-cursor: hand;");

        card.getChildren().addAll(title, msg, applyBtn);
        return card;
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }
}