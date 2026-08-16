package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class OrganizerDashboard extends Application {

    // --- EVENTXPERT COLOR PALETTE ---
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    
    // Status & Accent Colors
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String AMBER_TEXT = "#B06000";

    private static final String FONT_FAMILY = "-fx-font-family: 'Poppins', 'Segoe UI', 'Helvetica Neue', sans-serif;";

    // Common SVG Icons Data
    private static final String SVG_DASHBOARD = "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z";
    private static final String SVG_CALENDAR = "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z";
    private static final String SVG_USERS = "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z";
    private static final String SVG_BELL = "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z";
    private static final String SVG_SEARCH = "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z";
    private static final String SVG_CHECK = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z";
    private static final String SVG_ALERT = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; " + FONT_FAMILY);

        // 1. LEFT SIDEBAR
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // 2. RIGHT CONTAINER (TopBar + Main Dashboard ScrollPane)
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

        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(785);
        primaryStage.setMinWidth(1550);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // ==========================================
    // 1. SIDEBAR IMPLEMENTATION
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + NAVY + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0; ");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(15);

        // Brand Title
        VBox brandBox = new VBox(2);
        brandBox.setPadding(new Insets(0, 0, 10, 10));
        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        
        SVGPath logoIcon = createSVGIcon(SVG_CALENDAR, WHITE, 1.2);
        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");
        logoBox.getChildren().addAll(logoIcon, brandName);

        Label tagline = new Label("Plan. Manage. Celebrate.");
        tagline.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WHITE + ";");

        brandBox.getChildren().addAll(logoBox, tagline);

        // Nav Scroll / Links
        VBox navBox = new VBox(10);
        


        navBox.getChildren().add(createSidebarItem("Dashboard", SVG_DASHBOARD, true, ""));
        navBox.getChildren().add(createSidebarItem("Event Requests", SVG_CALENDAR, false, "3"));
        navBox.getChildren().add(createSidebarItem("My Events", SVG_CALENDAR, false, ""));
        navBox.getChildren().add(createSidebarItem("AI Planner", SVG_DASHBOARD, false, ""));
       // navBox.getChildren().add(createSidebarItem("Budget", SVG_DASHBOARD, false, ""));
       //navBox.getChildren().add(createSidebarItem("Venue", SVG_DASHBOARD, false, ""));
        navBox.getChildren().add(createSidebarItem("Vendors", SVG_USERS, false, ""));
        navBox.getChildren().add(createSidebarItem("Schedule", SVG_CALENDAR, false, ""));
       //navBox.getChildren().add(createSidebarItem("Guest Management", SVG_USERS, false, ""));
       //navBox.getChildren().add(createSidebarItem("QR Check-In", SVG_CHECK, false, ""));
       // navBox.getChildren().add(createSidebarItem("Live Event", SVG_ALERT, false, ""));


       
        
        ScrollPane navScroll = new ScrollPane(navBox);
        navScroll.setFitToWidth(true);
        navScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        sidebar.getChildren().addAll(brandBox, navScroll );
        VBox.setVgrow(navScroll, Priority.ALWAYS);

        return sidebar;
    }

    private Label createSidebarSectionHeader(String title) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 0 4 8;");
        return header;
    }

    private HBox createSidebarItem(String title, String svgData, boolean isActive, String badgeCount) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setCursor(javafx.scene.Cursor.HAND);

        SVGPath icon = createSVGIcon(svgData, isActive ? WHITE : SECONDARY_TEXT, 1.0);
        Label label = new Label(title);

        if (isActive) {
            item.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px;");
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        } else {
            item.setStyle("-fx-background-color: transparent;");
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: " + WHITE + ";");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        item.getChildren().addAll(icon, label, spacer);

        if (!badgeCount.isEmpty()) {
            Label badge = new Label(badgeCount);
            badge.setStyle("-fx-background-color: " + BRIGHT_BLUE + "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 2 6 2 6;");
            item.getChildren().add(badge);
        }

        item.setOnMouseClicked(e -> showToast("Clicked: " + title));

        return item;
    }

    
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPrefHeight(65);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        // Search Field Box
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setTranslateX(-32);
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

        // Right side info / Avatar
        HBox rightNav = new HBox(15);
        rightNav.setAlignment(Pos.CENTER);

        Button bellBtn = createIconButton(SVG_BELL);
        Button helpBtn = createIconButton(SVG_ALERT);

        rightNav.getChildren().addAll(bellBtn, helpBtn);

        topBar.getChildren().addAll(searchBox, spacer, rightNav);
        return topBar;
    }

    // ==========================================
    // 3. MAIN DASHBOARD CONTENT AREA
    // ==========================================
    private VBox createDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25, 30, 30, 30));

        // Header section: Greeting + Action Button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label greeting = new Label("Welcome Organizer 👋");
        greeting.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subGreeting = new Label("Here's what's happening with your events today.");
        subGreeting.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(greeting, subGreeting);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer);

        // Summary Stat Cards Row
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(15);

        summaryGrid.add(createSummaryCard("04", "Active Events", SVG_CALENDAR, PRIMARY_BLUE), 0, 0);
        summaryGrid.add(createSummaryCard("03", "Pending Requests", SVG_ALERT, AMBER_TEXT), 1, 0);
        summaryGrid.add(createSummaryCard("06", "Upcoming Events", SVG_CALENDAR, BRIGHT_BLUE), 2, 0);
        summaryGrid.add(createSummaryCard("08", "Tasks Due Today", SVG_CHECK, GREEN_TEXT), 3, 0);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            summaryGrid.getColumnConstraints().add(cc);
        }

        // Dashboard Main Split Layout (Left Column & Right Column)
        HBox mainGrid = new HBox(20);

        // LEFT COLUMN (65% Width)
        VBox leftColumn = new VBox(20);
        leftColumn.prefWidthProperty().bind(mainGrid.widthProperty().multiply(0.66));

        leftColumn.getChildren().addAll(
                createUpcomingEventsCard(),
                createGlobalHealthCard()
        );

        // RIGHT COLUMN (35% Width)
        VBox rightColumn = new VBox(20);
        rightColumn.prefWidthProperty().bind(mainGrid.widthProperty().multiply(0.34));

        rightColumn.getChildren().addAll(
                createSmartAlertsCard(),
                createTasksCard(),
                createQuickActionsCard()
        );

        mainGrid.getChildren().addAll(leftColumn, rightColumn);

        content.getChildren().addAll(header, summaryGrid, mainGrid);
        return content;
    }

    // ==========================================
    // UI CARD COMPONENTS
    // ==========================================

    // Summary Card Component
    private VBox createSummaryCard(String count, String label, String svgPath, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconPane = new StackPane();
        Circle bg = new Circle(18, Color.web(color).deriveColor(0, 1, 1, 0.12));
        SVGPath icon = createSVGIcon(svgPath, color, 0.9);
        iconPane.getChildren().addAll(bg, icon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(iconPane, spacer);

        Label numberLabel = new Label(count);
        numberLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label descLabel = new Label(label);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, numberLabel, descLabel);
        return card;
    }

    // Upcoming Events Table-style Card
    private VBox createUpcomingEventsCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Upcoming Events");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewAll = new Button("View All");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");

        header.getChildren().addAll(title, spacer, viewAll);

        // Events Table Box
        VBox table = new VBox(0);

        // Header Row
        HBox tableHeader = new HBox(150);
        tableHeader.setAlignment(Pos.CENTER);
        tableHeader.setPadding(new Insets(6, 8, 6, 8));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Label col1 = createTableLabel("EVENT NAME", 0.35, true);
        Label col2 = createTableLabel("DATE & LOCATION", 0.30, true);
        Label col3 = createTableLabel("GUESTS", 0.15, true);
        Label col4 = createTableLabel("STATUS", 0.20, true);

        tableHeader.getChildren().addAll(col1, col2, col3, col4);
        table.getChildren().add(tableHeader);

        // Event Rows
        table.getChildren().add(createEventRow( "TechVision Annual Summit", "18 May 2026\nPune, India", "250", "In Planning", LIGHT_BLUE, PRIMARY_BLUE));
        table.getChildren().add(createEventRow( " Bussiness Leadership Meet", "22 May 2026\nPune, India", "120", "Confirmed", GREEN_BG, GREEN_TEXT));
        table.getChildren().add(createEventRow( "Startup Networking Night", "28 May 2026\nPune, India", "180", "In Planning", LIGHT_BLUE, PRIMARY_BLUE));

        card.getChildren().addAll(header, table);
        return card;
    }

    private HBox createEventRow(String name, String dateLoc, String guests, String status, String statusBg, String statusFg) {
        HBox row = new HBox(100);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label nameLbl = createTableLabel(name, 0.35, false);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label dateLbl = createTableLabel(dateLoc, 0.30, false);
        dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label guestsLbl = createTableLabel(guests, 0.15, false);

        // Status Badge
        Label statusLbl = new Label("• " + status);
        statusLbl.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusFg + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 8 4 8; -fx-background-radius: 12px;");
        HBox statusBox = new HBox(statusLbl);
        statusBox.prefWidthProperty().bind(row.widthProperty().multiply(0.20));

        row.getChildren().addAll(nameLbl, dateLbl, guestsLbl, statusBox);
        return row;
    }

    private Label createTableLabel(String text, double widthPercent, boolean isHeader) {
        Label label = new Label(text);
        if (isHeader) {
            label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        }
        return label;
    }

    // Global Event Health Card
    private VBox createGlobalHealthCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label title = new Label("Global Event Health");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox metrics = new HBox(20);

        // Budget Progress
        VBox budgetBox = createHealthMetric("Budget Utilization", "77%", "₹38,500 / ₹50,000", 0.77, PRIMARY_BLUE);
        // Vendor Progress
        VBox vendorBox = createHealthMetric("Vendor Status", "80%", "8/10 Confirmed", 0.80, BRIGHT_BLUE);
        // Check-In Progress
        VBox guestBox = createHealthMetric("Guest Check-In", "62%", "156/250 Arrived", 0.62, NAVY);

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

    // Smart Alerts Widget
    private VBox createSmartAlertsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox header = new HBox(6);
        header.setAlignment(Pos.CENTER_LEFT);
        SVGPath alertIcon = createSVGIcon(SVG_ALERT, RED_TEXT, 0.9);
        Label title = new Label("Smart Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        header.getChildren().addAll(alertIcon, title);

        VBox alertBox = new VBox(4);
        alertBox.setPadding(new Insets(10));
        alertBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        Label alertTitle = new Label("Weather Update");
        alertTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label alertDesc = new Label("Rain expected for outdoor setup tomorrow.");
        alertDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        alertBox.getChildren().addAll(alertTitle, alertDesc);
        card.getChildren().addAll(header, alertBox);
        return card;
    }

    // Today's Tasks Card
    private VBox createTasksCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label title = new Label("Today's Tasks");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox taskList = new VBox(8);

        taskList.getChildren().add(createTaskItem("Confirm catering menu", "HIGH", true));
        taskList.getChildren().add(createTaskItem("Finalize seating arrangement", "MEDIUM", true));
        taskList.getChildren().add(createTaskItem("Review A/V quotation", "MEDIUM", true));
        taskList.getChildren().add(createTaskItem("Check sound & lighting", "HIGH", false));
        taskList.getChildren().add(createTaskItem("Verify guest list final count", "LOW", false));

        card.getChildren().addAll(title, taskList);
        return card;
    }

    private HBox createTaskItem(String text, String priority, boolean checked) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox cb = new CheckBox();
        cb.setSelected(checked);

        Label label = new Label(text);
        if (checked) {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-strikethrough: true;");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String tagBg = LIGHT_BLUE;
        String tagFg = BRIGHT_BLUE;
        if (priority.equals("HIGH")) {
            tagBg = RED_BG;
            tagFg = RED_TEXT;
        } else if (priority.equals("LOW")) {
            tagBg = BACKGROUND;
            tagFg = SECONDARY_TEXT;
        }

        Label pTag = new Label(priority);
        pTag.setStyle("-fx-background-color: " + tagBg + "; -fx-text-fill: " + tagFg + "; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6 2 6; -fx-background-radius: 4px;");

        row.getChildren().addAll(cb, label, spacer, pTag);
        return row;
    }

    // Quick Action Grid Card
    private VBox createQuickActionsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 8px;");

        Label title = new Label("⚡ Quick Actions");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(createActionButton("Review Requests"), 0, 0);
        grid.add(createActionButton("Open My Events"), 1, 0);
        grid.add(createActionButton("Schedule"), 0, 1);
        grid.add(createActionButton("Check Vendors"), 1, 1);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            grid.getColumnConstraints().add(cc);
        }

        card.getChildren().addAll(title, grid);
        return card;
    }

    private Button createActionButton(String label) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 6px; -fx-cursor: hand;");
        btn.setOnAction(e -> showToast("Action Executed: " + label));
        return btn;
    }

    // Helper Utility: Build Icon Buttons
    private Button createIconButton(String svgPath) {
        Button btn = new Button();
        btn.setGraphic(createSVGIcon(svgPath, SECONDARY_TEXT, 0.9));
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
        return btn;
    }

    // Helper Utility: SVG Path Generator
    private SVGPath createSVGIcon(String content, String colorHex, double scale) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(Color.web(colorHex));
        svgPath.setScaleX(scale);
        svgPath.setScaleY(scale);
        return svgPath;
    }

    // Helper Utility: Show Toast Notification on Click
    private void showToast(String message) {
        System.out.println("[EventXpert Log]: " + message);
    }
}