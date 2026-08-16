package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ViewEventO extends Application {

    // Color Palette
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String RED_ALERT_BG = "#FFF5F5";
    private static final String RED_ALERT_BORDER = "#E53E3E";
    private static final String RED_TEXT = "#C5221F";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;");

        // Top Navigation Bar
        root.setTop(createTopBar());

        // Sidebar Navigation
        root.setLeft(createSidebar());

        // Main Scrollable Area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1440, 900);
        primaryStage.setTitle("EventXpert - View Event (TechVision Annual Summit)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================
    // TOP BAR
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        // Search Bar
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search events, vendors, venues...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button msgBtn = new Button("✉️");
        msgBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        // Profile Box
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(18, Color.web(PRIMARY_BLUE));
        Label avatarText = new Label("AS");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        avatar.getChildren().addAll(circle, avatarText);

        VBox userInfo = new VBox(1);
        Label userName = new Label("Alex Sterling");
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        Label userRole = new Label("Lead Organizer");
        userRole.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);

        profileBox.getChildren().addAll(avatar, userInfo);

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, msgBtn, profileBox);
        return topBar;
    }

    // ==========================================
    // SIDEBAR
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setSpacing(6);

        // Logo
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 10, 6));

        StackPane logoBg = new StackPane();
        Rectangle rect = new Rectangle(34, 34);
        rect.setArcWidth(8);
        rect.setArcHeight(8);
        rect.setFill(Color.web(PRIMARY_BLUE));
        Label logoText = new Label("EX");
        logoText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        logoBg.getChildren().addAll(rect, logoText);

        VBox brandTitles = new VBox(1);
        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Corporate Management");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        brandTitles.getChildren().addAll(brandName, brandSub);

        brand.getChildren().addAll(logoBg, brandTitles);
        sidebar.getChildren().addAll(brand, new Separator());

        // Nav Menu
        sidebar.getChildren().addAll(
            createNavHeader("EVENT MANAGEMENT"),
            createNavItem("Dashboard", false),
            createNavItem("Event Requests", false),
            createNavItem("My Events", true), // Active highlight

            createNavHeader("PLANNING TOOLS"),
            createNavItem("AI Planner", false),
            createNavItem("Budget", false),
            createNavItem("Venue", false),
            createNavItem("Vendors", false),
            createNavItem("Schedule", false),
            createNavItem("Guest Management", false),
            createNavItem("QR Check-In", false),
            createNavItem("Live Event", false)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(4);
        bottomMenu.getChildren().addAll(
            new Separator(),
            createNavItem("Settings", false),
            createNavItem("Support", false)
        );

        sidebar.getChildren().addAll(spacer, bottomMenu);
        return sidebar;
    }

    private Label createNavHeader(String title) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 8 0 2 6;");
        return header;
    }

    private Button createNavItem(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(7, 10, 7, 10));

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-background-radius: 6; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-background-radius: 6; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-background-radius: 6; -fx-cursor: hand;"));
        }

        btn.setOnAction(e -> {
            if (!isActive) {
                showInfoAlert("Navigation", "Navigating to " + text + "...");
            }
        });

        return btn;
    }

    // ==========================================
    // MAIN CONTENT AREA
    // ==========================================
    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumb Navigation Row
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> showInfoAlert("Navigation", "Returning to My Events list..."));

        Label bcSep1 = new Label(">");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("TechVision Annual Summit");
        bc2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bcSep2 = new Label(">");
        bcSep2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc3 = new Label("Planning");
        bc3.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpacer = new Region();
        HBox.setHgrow(bcSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to My Events");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> showInfoAlert("Navigation", "Returning to My Events..."));

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSep2, bc3, bcSpacer, backBtn);

        // Header Title Row
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("TechVision Annual Summit");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox metaBox = new HBox(16);
        metaBox.getChildren().addAll(
            new Label("🏢 Corporate Conference"),
            new Label("📅 18 May 2026"),
            new Label("⏰ 09:00 AM – 05:00 PM"),
            new Label("📍 Pune, Maharashtra"),
            new Label("👥 250 Guests")
        );
        for (javafx.scene.Node n : metaBox.getChildren()) {
            n.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        }
        titleBox.getChildren().addAll(title, metaBox);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label statusBadge = new Label("● In Planning");
        statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 16; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, statusBadge);

        // Top Planning Progress Card
        VBox progressCard = createPlanningProgressCard();

        // Main 2-Column Body Layout
        HBox bodyGrid = new HBox(24);

        // Left Column: Planning Modules Grid
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label modulesTitle = new Label("Planning Modules");
        modulesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane modulesGrid = createModulesGrid();

        leftCol.getChildren().addAll(modulesTitle, modulesGrid);

        // Right Column: Priority Task & Smart Alerts Sidebar
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
            createPriorityTaskCard(),
            createSmartAlertsCard(),
            createCustomerAndChecklistCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, headerRow, progressCard, bodyGrid);
        return mainBox;
    }

    // ==========================================
    // PLANNING PROGRESS TRACKER CARD
    // ==========================================
    private VBox createPlanningProgressCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox pHeader = new HBox();
        Label pTitle = new Label("Planning Progress");
        pTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pPercent = new Label("65% Complete");
        pPercent.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");

        pHeader.getChildren().addAll(pTitle, spacer, pPercent);

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-control-inner-background: " + LIGHT_BLUE + "; -fx-pref-height: 8;");

        // Step Tracker Timeline
        HBox stepsRow = new HBox(0);
        stepsRow.setAlignment(Pos.CENTER);
        stepsRow.setPadding(new Insets(10, 0, 0, 0));

        stepsRow.getChildren().addAll(
            createProgressStep("Requirements", true, false, false),
            createStepLine(true),
            createProgressStep("Venue", true, false, false),
            createStepLine(true),
            createProgressStep("Budget", true, false, false),
            createStepLine(true),
            createProgressStep("Vendors", false, true, true), // Active + Red Alert
            createStepLine(false),
            createProgressStep("Schedule", false, false, false),
            createStepLine(false),
            createProgressStep("Guests", false, false, false),
            createStepLine(false),
            createProgressStep("Final Review", false, false, false)
        );

        card.getChildren().addAll(pHeader, pb, stepsRow);
        return card;
    }

    private VBox createProgressStep(String title, boolean completed, boolean active, boolean hasAlert) {
        VBox stepBox = new VBox(6);
        stepBox.setAlignment(Pos.CENTER);

        StackPane iconPane = new StackPane();

        Circle c = new Circle(12);
        if (completed) {
            c.setFill(Color.web(PRIMARY_BLUE));
        } else if (active) {
            c.setFill(Color.web(WHITE));
            c.setStroke(Color.web(PRIMARY_BLUE));
            c.setStrokeWidth(2.5);
        } else {
            c.setFill(Color.web(WHITE));
            c.setStroke(Color.web(BORDER_COLOR));
            c.setStrokeWidth(2);
        }

        Label icon = new Label(completed ? "✓" : (active ? "◉" : ""));
        icon.setStyle("-fx-text-fill: " + (completed ? "white" : PRIMARY_BLUE) + "; -fx-font-size: 10px; -fx-font-weight: bold;");

        iconPane.getChildren().addAll(c, icon);

        if (hasAlert) {
            Label alertDot = new Label("🔻");
            alertDot.setStyle("-fx-font-size: 8px;");
            StackPane.setAlignment(alertDot, Pos.TOP_RIGHT);
            iconPane.getChildren().add(alertDot);
        }

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (completed || active ? TEXT_MAIN : SECONDARY_TEXT) + "; " + (active ? "-fx-font-weight: bold;" : ""));

        stepBox.getChildren().addAll(iconPane, t);
        return stepBox;
    }

    private Region createStepLine(boolean completed) {
        Region line = new Region();
        line.setPrefWidth(55);
        line.setPrefHeight(2);
        line.setStyle("-fx-background-color: " + (completed ? PRIMARY_BLUE : BORDER_COLOR) + ";");
        return line;
    }

    // ==========================================
    // PLANNING MODULES GRID
    // ==========================================
    private GridPane createModulesGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        grid.add(createModuleCard("💡", "AI Event Planner", "Available", "Get intelligent recommendations for layout, catering, and scheduling base...", "", "Open Planner →", false, LIGHT_BLUE, BRIGHT_BLUE), 0, 0);
        grid.add(createModuleCard("📊", "Budget & Expenses", "On Track", "Track approved budget across all vendor categories.", "₹5,00,000 Approved", "View Budget →", false, LIGHT_BLUE, BRIGHT_BLUE), 1, 0);
        grid.add(createModuleCard("🏢", "Venue Management", "Venue Selected", "Manage selected venue details, floor plans, and seating arrangements.", "Grand Convention Center, Pune", "View Venue →", false, LIGHT_BLUE, BRIGHT_BLUE), 0, 1);
        
        // Vendors Card with Red Action Needed Highlight
        grid.add(createModuleCard("🏪", "Vendors", "⚠️ Action Needed", "Manage required vendors, contracts, and payments.", "3 of 6 Confirmed", "Manage Vendors →", true, RED_ALERT_BG, RED_TEXT), 1, 1);

        grid.add(createModuleCard("⏰", "Schedule & Agenda", "In Progress", "Detailed event timeline, session tracking, and speaker setup.", "09:00 AM – 05:00 PM (7 Sessions)", "View Schedule →", false, LIGHT_BLUE, BRIGHT_BLUE), 0, 2);
        grid.add(createModuleCard("👥", "Guest Management & QR", "In Progress", "Guest invitations, RSVP tracking, and QR check-in readiness.", "184 / 250 Registered", "Manage Guests →", false, LIGHT_BLUE, BRIGHT_BLUE), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createModuleCard(String iconStr, String title, String badgeText, String desc, String subDetail, String linkText, boolean isAlert, String badgeBg, String badgeTextCol) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));

        if (isAlert) {
            card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + RED_ALERT_BORDER + "; -fx-border-width: 1 1 1 4; -fx-border-radius: 10; -fx-background-radius: 10;");
        } else {
            card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        }

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Rectangle iconBg = new Rectangle(36, 36);
        iconBg.setArcWidth(8);
        iconBg.setArcHeight(8);
        iconBg.setFill(Color.web(LIGHT_BLUE));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 16px;");
        iconBox.getChildren().addAll(iconBg, ic);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeTextCol + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        topRow.getChildren().addAll(iconBox, spacer, badge);

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label cardDesc = new Label(desc);
        cardDesc.setWrapText(true);
        cardDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, cardTitle, cardDesc);

        if (!subDetail.isEmpty()) {
            Label detailLbl = new Label(subDetail);
            detailLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (isAlert ? RED_TEXT : TEXT_MAIN) + ";");
            card.getChildren().add(detailLbl);
        }

        Region spaceBottom = new Region();
        VBox.setVgrow(spaceBottom, Priority.ALWAYS);

        Button linkBtn = new Button(linkText);
        linkBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + (isAlert ? RED_TEXT : PRIMARY_BLUE) + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0;");
        linkBtn.setOnAction(e -> showInfoAlert(title, "Opening module: " + title + "..."));

        card.getChildren().addAll(spaceBottom, linkBtn);
        return card;
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createPriorityTaskCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label cardTitle = new Label("! Next Priority Task");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox taskCallout = new VBox(6);
        taskCallout.setPadding(new Insets(12));
        taskCallout.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8;");

        HBox calloutTop = new HBox();
        Label priorityLabel = new Label("HIGH PRIORITY");
        priorityLabel.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label dueDate = new Label("Due 12 May");
        dueDate.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px;");
        calloutTop.getChildren().addAll(priorityLabel, spacer, dueDate);

        Label taskDesc = new Label("Finalize AV & Sound Setup");
        taskDesc.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");

        taskCallout.getChildren().addAll(calloutTop, taskDesc);

        Button continueBtn = new Button("Continue Task →");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        continueBtn.setOnAction(e -> showInfoAlert("Priority Task", "Navigating to AV & Sound Setup vendor contract..."));

        card.getChildren().addAll(cardTitle, taskCallout, continueBtn);
        return card;
    }

    private VBox createSmartAlertsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label title = new Label("Smart Planning Alerts");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        card.getChildren().addAll(
            title,
            createAlertCallout("WEATHER ALERT", "Rain expected. Consider indoor backup.", LIGHT_BLUE, BRIGHT_BLUE),
            createAlertCallout("CONFLICT DETECTION", "No major schedule conflicts.", BACKGROUND, SECONDARY_TEXT),
            createAlertCallout("BUDGET", "Within approved ₹5L budget.", BACKGROUND, SECONDARY_TEXT)
        );

        return card;
    }

    private VBox createAlertCallout(String header, String body, String bgHex, String headerCol) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + bgHex + "; -fx-background-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6;");

        Label h = new Label(header);
        h.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + headerCol + ";");

        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        box.getChildren().addAll(h, b);
        return box;
    }

    private VBox createCustomerAndChecklistCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        // Customer Info
        Label custHeader = new Label("Customer Information");
        custHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox custDetails = new VBox(4);
        custDetails.getChildren().addAll(
            new Label("Client: Rahul Sharma"),
            new Label("Organization: TechVision Solutions"),
            new Label("Email: rahul.sharma@email.com"),
            new Label("Phone: +91 98765 43210")
        );
        for (javafx.scene.Node n : custDetails.getChildren()) {
            n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        }

        Button contactBtn = new Button("Contact Customer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 0 6 0;");
        contactBtn.setOnAction(e -> showInfoAlert("Contact Client", "Emailing rahul.sharma@email.com..."));

        card.getChildren().addAll(custHeader, custDetails, contactBtn, new Separator());

        // Checklist Summary
        HBox chkHeader = new HBox();
        Label chkTitle = new Label("Checklist");
        chkTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label chkVal = new Label("4 of 7 completed");
        chkVal.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        chkHeader.getChildren().addAll(chkTitle, spacer, chkVal);

        card.getChildren().addAll(chkHeader);
        return card;
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}