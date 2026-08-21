package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MyEventsO{
    private Scene MyEventsOScene;

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
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String PURPLE_BG = "#F3E8FF";
    private static final String PURPLE_TEXT = "#6B21A8";

    // Controls for filtering
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> typeFilter;
    private ComboBox<String> dateFilter;

    Scene getMyEventsOScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;");

        // Top Navigation Bar
        root.setTop(createTopBar());

        // Sidebar Navigation
        root.setLeft(createSidebar());

        // Main Content Area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1440, 900);
        MyEventsOScene = scene;
        return MyEventsOScene;
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
        topSearch.setPromptText("Search across events...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

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

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, helpBtn, profileBox);
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
        Label brandSub = new Label("Premium Management");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        brandTitles.getChildren().addAll(brandName, brandSub);

        brand.getChildren().addAll(logoBg, brandTitles);

        // + New Event Primary Button
        Button createBtn = new Button("+ New Event");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        createBtn.setOnAction(e -> showInfoAlert("New Event", "Create Event modal coming soon."));

        sidebar.getChildren().addAll(brand, createBtn, new Separator());

        // Nav Menu
        sidebar.getChildren().addAll(
            createNavHeader("EVENT MANAGEMENT"),
            createNavItem("Dashboard", false),
            createNavItem("Event Requests", false),
            createNavItem("My Events", true), // Active

            createNavHeader("EVENT PLANNING"),
            createNavItem("AI Planner", false),
            createNavItem("Budget", false),
            createNavItem("Venue", false),
            createNavItem("Vendors", false),
            createNavItem("Schedule", false),

            createNavHeader("EVENT EXECUTION"),
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

        // Header Title
        VBox headerBox = new VBox(4);
        Label title = new Label("My Events");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Manage and monitor all events assigned to you.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        headerBox.getChildren().addAll(title, subtitle);

        // Top Summary Cards Grid (4 Cards)
        HBox summaryRow = new HBox(16);
        summaryRow.getChildren().addAll(
            createSummaryCard("📅", "ACTIVE EVENTS", "04"),
            createSummaryCard("⏰", "UPCOMING", "03"),
            createSummaryCard("🎯", "COMPLETED", "08"),
            createSummaryCard("📋", "PENDING TASKS", "12")
        );

        // Main Layout Grid (Left List + Right Summary Sidebar)
        HBox bodyColumns = new HBox(24);

        // Left Column (Events List)
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
            createFilterBar(),
            createEventCardTechVision(),
            createEventCardLeadership(),
            createEventCardStartupNight(),
            createPaginationBar()
        );

        // Right Column (Next Approaching + Smart Alerts)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
            createNextApproachingCard(),
            createSmartAlertsCard()
        );

        bodyColumns.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(headerBox, summaryRow, bodyColumns);
        return mainBox;
    }

    // ==========================================
    // SUMMARY CARDS
    // ==========================================
    private HBox createSummaryCard(String icon, String title, String count) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        StackPane iconPane = new StackPane();
        Rectangle iconBg = new Rectangle(38, 38);
        iconBg.setArcWidth(8);
        iconBg.setArcHeight(8);
        iconBg.setFill(Color.web(LIGHT_BLUE));
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 16px;");
        iconPane.getChildren().addAll(iconBg, iconLbl);

        VBox textPane = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label countLbl = new Label(count);
        countLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        textPane.getChildren().addAll(titleLbl, countLbl);

        card.getChildren().addAll(iconPane, textPane);
        return card;
    }

    // ==========================================
    // FILTER BAR
    // ==========================================
    private HBox createFilterBar() {
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(12, 16, 12, 16));
        filterBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Search Input
        HBox searchBox = new HBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        
        searchField = new TextField();
        searchField.setPromptText("Search events by name...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-pref-width: 180px;");
        searchBox.getChildren().addAll(new Label("🔍"), searchField);

        // Status Dropdown
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Status", "All Status", "Upcoming", "Active", "Completed");
        statusFilter.setValue("Status");
        statusFilter.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-font-size: 12px;");

        // Type Dropdown
        typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("Type", "All Types", "Corporate Conference", "Product Launch", "Networking");
        typeFilter.setValue("Type");
        typeFilter.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-font-size: 12px;");

        // Date Dropdown
        dateFilter = new ComboBox<>();
        dateFilter.getItems().addAll("Date", "This Month", "Next Month", "All Dates");
        dateFilter.setValue("Date");
        dateFilter.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button clearBtn = new Button("Clear Filters");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("Status");
            typeFilter.setValue("Type");
            dateFilter.setValue("Date");
        });

        filterBar.getChildren().addAll(searchBox, statusFilter, typeFilter, dateFilter, spacer, clearBtn);
        return filterBar;
    }

    // ==========================================
    // EVENT CARD 1: TechVision Annual Summit
    // ==========================================
    private VBox createEventCardTechVision() {
        VBox card = createBaseCardContainer();

        // Header Row with Badges
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label catBadge = new Label("Corporate Conference");
        catBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

        Label statusBadge = new Label("In Planning");
        statusBadge.setStyle("-fx-background-color: " + PURPLE_BG + "; -fx-text-fill: " + PURPLE_TEXT + "; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button moreBtn = new Button("⋮");
        moreBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        topRow.getChildren().addAll(catBadge, statusBadge, spacer, moreBtn);

        Label title = new Label("TechVision Annual Summit");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        // Details Row
        HBox detailsRow = new HBox(20);
        detailsRow.setPadding(new Insets(4, 0, 8, 0));
        detailsRow.getChildren().addAll(
            new Label("📅 18 May 2026"),
            new Label("📍 Pune"),
            new Label("👥 250 Guests")
        );
        for (javafx.scene.Node n : detailsRow.getChildren()) {
            n.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        }

        // Planning Progress & Steps
        HBox progressHeader = new HBox();
        Label pTitle = new Label("Planning Progress");
        pTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);
        Label pPercent = new Label("65%");
        pPercent.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        progressHeader.getChildren().addAll(pTitle, pSpacer, pPercent);

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-control-inner-background: " + LIGHT_BLUE + "; -fx-pref-height: 8;");

        // Step Nodes
        HBox stepNodes = new HBox(12);
        stepNodes.setAlignment(Pos.CENTER);
        stepNodes.setPadding(new Insets(8, 0, 0, 0));

        stepNodes.getChildren().addAll(
            createStepNode("Reqs", true),
            createStepConnector(true),
            createStepNode("Venue", true),
            createStepConnector(true),
            createStepNode("Budget", true),
            createStepConnector(false),
            createStepNode("Vendors", false),
            createStepConnector(false),
            createStepNode("Schedule", false)
        );

        card.getChildren().addAll(topRow, title, detailsRow, new Separator(), progressHeader, pb, stepNodes);
        return card;
    }

    // ==========================================
    // EVENT CARD 2: Business Leadership Meet
    // ==========================================
    private VBox createEventCardLeadership() {
        VBox card = createBaseCardContainer();

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label catBadge = new Label("Corporate Event");
        catBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

        Label statusBadge = new Label("Confirmed");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button moreBtn = new Button("⋮");
        moreBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        topRow.getChildren().addAll(catBadge, statusBadge, spacer, moreBtn);

        Label title = new Label("Business Leadership Meet");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox detailsRow = new HBox(20);
        detailsRow.setPadding(new Insets(4, 0, 8, 0));
        detailsRow.getChildren().addAll(
            new Label("📅 22 May 2026"),
            new Label("📍 Pune"),
            new Label("👥 120 Guests")
        );
        for (javafx.scene.Node n : detailsRow.getChildren()) {
            n.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        }

        // Circular Progress Phase Info
        HBox phaseBox = new HBox(16);
        phaseBox.setAlignment(Pos.CENTER_LEFT);
        phaseBox.setPadding(new Insets(6, 0, 0, 0));

        StackPane circleMeter = createCircularMeter("82%", PRIMARY_BLUE);

        VBox phaseText = new VBox(2);
        Label pHeader = new Label("Execution Phase");
        pHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label pDesc = new Label("All major vendors confirmed. Awaiting final guest list.");
        pDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        phaseText.getChildren().addAll(pHeader, pDesc);

        phaseBox.getChildren().addAll(circleMeter, phaseText);

        card.getChildren().addAll(topRow, title, detailsRow, new Separator(), phaseBox);
        return card;
    }

    // ==========================================
    // EVENT CARD 3: Startup Networking Night
    // ==========================================
    private VBox createEventCardStartupNight() {
        VBox card = createBaseCardContainer();

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label catBadge = new Label("Networking Event");
        catBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

        Label statusBadge = new Label("In Planning");
        statusBadge.setStyle("-fx-background-color: " + PURPLE_BG + "; -fx-text-fill: " + PURPLE_TEXT + "; -fx-padding: 3 8 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button moreBtn = new Button("⋮");
        moreBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        topRow.getChildren().addAll(catBadge, statusBadge, spacer, moreBtn);

        Label title = new Label("Startup Networking Night");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox detailsRow = new HBox(20);
        detailsRow.setPadding(new Insets(4, 0, 8, 0));
        detailsRow.getChildren().addAll(
            new Label("📅 28 May 2026"),
            new Label("📍 Pune"),
            new Label("👥 180 Guests")
        );
        for (javafx.scene.Node n : detailsRow.getChildren()) {
            n.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        }

        HBox phaseBox = new HBox(16);
        phaseBox.setAlignment(Pos.CENTER_LEFT);
        phaseBox.setPadding(new Insets(6, 0, 0, 0));

        StackPane circleMeter = createCircularMeter("45%", BRIGHT_BLUE);

        VBox phaseText = new VBox(2);
        Label pHeader = new Label("Sponsorship Sourcing");
        pHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label pDesc = new Label("Currently securing beverage sponsors and catering options.");
        pDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        phaseText.getChildren().addAll(pHeader, pDesc);

        phaseBox.getChildren().addAll(circleMeter, phaseText);

        card.getChildren().addAll(topRow, title, detailsRow, new Separator(), phaseBox);
        return card;
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createNextApproachingCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + NAVY + "; -fx-background-radius: 12;");

        HBox topTitle = new HBox();
        topTitle.setAlignment(Pos.CENTER_LEFT);

        Label tag = new Label("NEXT APPROACHING");
        tag.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label bolt = new Label("⚡");
        bolt.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");

        topTitle.getChildren().addAll(tag, spacer, bolt);

        Label eventName = new Label("TechVision Summit");
        eventName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        Label eventDate = new Label("18 May 2026 • 250 Guests");
        eventDate.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 12px;");

        // Progress Box
        HBox pRow = new HBox();
        Label pLabel = new Label("Progress");
        pLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);
        Label pVal = new Label("65%");
        pVal.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        pRow.getChildren().addAll(pLabel, pSpacer, pVal);

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + BRIGHT_BLUE + "; -fx-pref-height: 6;");

        // Priority Task Callout Box
        VBox taskBox = new VBox(4);
        taskBox.setPadding(new Insets(10));
        taskBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8;");

        Label taskHeader = new Label("Priority Task");
        taskHeader.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px;");
        Label taskVal = new Label("! Finalize AV & Sound Setup");
        taskVal.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        taskBox.getChildren().addAll(taskHeader, taskVal);

        // Action Button
        Button planBtn = new Button("Continue Planning →");
        planBtn.setMaxWidth(Double.MAX_VALUE);
        planBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        planBtn.setOnAction(e -> showInfoAlert("Event Planning", "Navigating to TechVision Summit planning workflow..."));

        card.getChildren().addAll(topTitle, eventName, eventDate, pRow, pb, taskBox, planBtn);
        return card;
    }

    private VBox createSmartAlertsCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox titleRow = new HBox(6);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("✨");
        Label title = new Label("Smart Alerts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        titleRow.getChildren().addAll(icon, title);

        card.getChildren().addAll(
            titleRow,
            createAlertItem("☁️", "Weather Alert (Rain)", "TechVision Summit date has 60% chance of rain. Ensure indoor fallback."),
            createAlertItem("📅", "Conflict Detection: Clear", "No scheduling overlaps detected for your upcoming 3 events."),
            createAlertItem("📊", "Budget Status: On Track", "Overall spending across active events is 12% below projected estimates."),
            createAlertItem("🏢", "Venue Alert", "Suggested alternative venues available for 'Startup Networking Night'.")
        );

        return card;
    }

    private HBox createAlertItem(String iconStr, String header, String body) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.TOP_LEFT);

        StackPane circle = new StackPane();
        Circle bg = new Circle(14, Color.web(LIGHT_BLUE));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 11px;");
        circle.getChildren().addAll(bg, ic);

        VBox texts = new VBox(2);
        Label h = new Label(header);
        h.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        texts.getChildren().addAll(h, b);
        HBox.setHgrow(texts, Priority.ALWAYS);

        item.getChildren().addAll(circle, texts);
        return item;
    }

    // ==========================================
    // PAGINATION
    // ==========================================
    private HBox createPaginationBar() {
        HBox pageRow = new HBox(8);
        pageRow.setAlignment(Pos.CENTER);
        pageRow.setPadding(new Insets(10, 0, 10, 0));

        Button prev = new Button("Previous");
        prev.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4; -fx-font-size: 11px; -fx-cursor: hand;");

        Button p1 = new Button("1");
        p1.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-font-size: 11px;");

        Button p2 = new Button("2");
        p2.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4; -fx-font-size: 11px; -fx-cursor: hand;");

        Button p3 = new Button("3");
        p3.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4; -fx-font-size: 11px; -fx-cursor: hand;");

        Button next = new Button("Next");
        next.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4; -fx-font-size: 11px; -fx-cursor: hand;");

        pageRow.getChildren().addAll(prev, p1, p2, p3, next);
        return pageRow;
    }

    // ==========================================
    // UI HELPER METHODS
    // ==========================================
    private VBox createBaseCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        return card;
    }

    private HBox createStepNode(String name, boolean completed) {
        HBox step = new HBox(4);
        step.setAlignment(Pos.CENTER);

        StackPane circle = new StackPane();
        Circle c = new Circle(10, completed ? Color.web(PRIMARY_BLUE) : Color.TRANSPARENT);
        if (!completed) {
            c.setStroke(Color.web(SECONDARY_TEXT));
            c.setStrokeWidth(1.5);
        }

        Label check = new Label(completed ? "✓" : "");
        check.setStyle("-fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;");
        circle.getChildren().addAll(c, check);

        Label label = new Label(name);
        label.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (completed ? TEXT_MAIN : SECONDARY_TEXT) + ";");

        step.getChildren().addAll(circle, label);
        return step;
    }

    private Region createStepConnector(boolean completed) {
        Region line = new Region();
        line.setPrefWidth(24);
        line.setPrefHeight(2);
        line.setStyle("-fx-background-color: " + (completed ? PRIMARY_BLUE : BORDER_COLOR) + ";");
        return line;
    }

    private StackPane createCircularMeter(String percentText, String colorHex) {
        StackPane pane = new StackPane();
        Circle outer = new Circle(22);
        outer.setFill(Color.TRANSPARENT);
        outer.setStroke(Color.web(LIGHT_BLUE));
        outer.setStrokeWidth(4);

        Circle fill = new Circle(22);
        fill.setFill(Color.TRANSPARENT);
        fill.setStroke(Color.web(colorHex));
        fill.setStrokeWidth(4);

        Label txt = new Label(percentText);
        txt.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        pane.getChildren().addAll(outer, fill, txt);
        return pane;
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}