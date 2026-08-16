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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EventRequests extends Application {

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

    // Status & Badge Colors
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String AMBER_BG = "#FEF7E0";
    private static final String AMBER_TEXT = "#B06000";

    private static final String FONT_FAMILY = "-fx-font-family: 'Poppins', 'Segoe UI', 'Helvetica Neue', sans-serif;";

    // SVG Icons
    private static final String SVG_DASHBOARD = "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z";
    private static final String SVG_CALENDAR = "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z";
    private static final String SVG_USERS = "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z";
    private static final String SVG_BELL = "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z";
    private static final String SVG_SEARCH = "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z";
    private static final String SVG_CHECK = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z";
    private static final String SVG_ALERT = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
    private static final String SVG_FILTER = "M10 18h4v-2h-4v2zM3 6v2h18V6H3zm3 7h12v-2H6v2z";

    // Simple Data Representation for Event Requests
    private static class RequestData {
        String id, title, customer, type, date, time, guests, venue, budget, priority, status, message;

        RequestData(String id, String title, String customer, String type, String date, String time, String guests, String venue, String budget, String priority, String status, String message) {
            this.id = id;
            this.title = title;
            this.customer = customer;
            this.type = type;
            this.date = date;
            this.time = time;
            this.guests = guests;
            this.venue = venue;
            this.budget = budget;
            this.priority = priority;
            this.status = status;
            this.message = message;
        }
    }

    private final List<RequestData> requestsList = new ArrayList<>();
    private VBox requestsContainer;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initSampleData();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; " + FONT_FAMILY);

        // 1. LEFT SIDEBAR
        root.setLeft(createSidebar());

        // 2. CENTER CONTENT (TopBar + Scrollable Body)
        VBox rightContainer = new VBox();
        HBox topBar = createTopBar();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        rightContainer.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setCenter(rightContainer);

        // Scene & Stage Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleData() {
        requestsList.clear();
        requestsList.add(new RequestData("REQ-101", "TechVision Annual Summit", "Rahul Sharma", "Corporate Conference", "18 May 2026", "09:00 AM", "250", "Grand Convention Center", "₹12,50,000", "High", "Pending", "Requires full AV setup and stage branding. Keynote speakers arriving at 8:30 AM."));
        requestsList.add(new RequestData("REQ-102", "GreenTech Business Meet", "Priya Mehta", "Corporate Meeting", "25 May 2026", "10:30 AM", "150", "Pune Business Hub", "₹7,50,000", "Medium", "Pending", "Eco-friendly catering preferred. Need 2 breakout rooms for discussion sessions."));
        requestsList.add(new RequestData("REQ-103", "Startup Connect 2026", "Aarav Patil", "Networking Event", "02 June 2026", "05:00 PM", "200", "Skyline Convention Hall", "₹9,20,000", "Medium", "Approved", "High-speed Wi-Fi required for demo booths. Cocktail dinner arrangement."));
        requestsList.add(new RequestData("REQ-104", "Product Launch 2026", "Neha Kulkarni", "Product Launch", "10 June 2026", "06:30 PM", "320", "Grand Convention Center", "₹15,00,000", "High", "Rejected", "Budget constraint identified during initial review. Rejection reason provided to client."));
    }

    // ==========================================
    // 1. SIDEBAR
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + NAVY + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
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

        Label subBrand = new Label("Plan. Manage. Celebrate.");
        subBrand.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WHITE + ";");
        brandBox.getChildren().addAll(logoBox, subBrand);

        // Navigation
        VBox navBox = new VBox(4);
        navBox.getChildren().add(createSidebarItem("Dashboard", SVG_DASHBOARD, false, ""));
        navBox.getChildren().add(createSidebarItem("Event Requests", SVG_CALENDAR, true, "03"));
        navBox.getChildren().add(createSidebarItem("My Events", SVG_CALENDAR, false, ""));
        navBox.getChildren().add(createSidebarItem("Venue", SVG_DASHBOARD, false, ""));
        navBox.getChildren().add(createSidebarItem("Vendors", SVG_USERS, false, ""));
        navBox.getChildren().add(createSidebarItem("Schedule", SVG_CALENDAR, false, ""));
       
        // Bottom Items
        VBox bottomBox = new VBox(10);
        bottomBox.getChildren().add(createSidebarItem("Notifications", SVG_BELL, false, ""));
        bottomBox.getChildren().add(createSidebarItem("Settings", SVG_DASHBOARD, false, ""));

        ScrollPane navScroll = new ScrollPane(navBox);
        navScroll.setFitToWidth(true);
        navScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        sidebar.getChildren().addAll(brandBox, navScroll, bottomBox);
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

        SVGPath icon = createSVGIcon(svgData, isActive ? PRIMARY_BLUE : SECONDARY_TEXT, 1.0);
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
            badge.setStyle("-fx-background-color: " + BRIGHT_BLUE + "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 2 6 2 6;");
            item.getChildren().add(badge);
        }

        item.setOnMouseClicked(e -> {
            if (title.equals("Dashboard")) {
                showToast("Opening Organizer Dashboard...");
            } else if (!isActive) {
                showToast("Navigating to: " + title + " (Screen coming soon)");
            }
        });

        return item;
    }

    // ==========================================
    // 2. TOP BAR
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPrefHeight(65);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        // Search Input
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setPadding(new Insets(8, 12, 8, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        SVGPath searchIcon = createSVGIcon(SVG_SEARCH, SECONDARY_TEXT, 0.9);
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search events, requests, customers...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightNav = new HBox(15);
        rightNav.setAlignment(Pos.CENTER);

        Button bellBtn = createIconButton(SVG_BELL);
        Button helpBtn = createIconButton(SVG_ALERT);

        VBox dateBox = new VBox(2);
        dateBox.setAlignment(Pos.CENTER_RIGHT);
        Label dateDay = new Label("Sarah Jenkins");
        dateDay.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label dateFull = new Label("Organizer");
        dateFull.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        dateBox.getChildren().addAll(dateDay, dateFull);

        Circle userAvatar = new Circle(16, Color.web(BRIGHT_BLUE));

        rightNav.getChildren().addAll(bellBtn, helpBtn, dateBox, userAvatar);

        topBar.getChildren().addAll(searchBox, spacer, rightNav);
        return topBar;
    }

    // ==========================================
    // 3. MAIN CONTENT AREA
    // ==========================================
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25, 30, 30, 30));

        // Page Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Event Requests");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Review incoming event requests and decide which events you want to manage.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer);

        // 4 Summary KPI Cards
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(15);

        summaryGrid.add(createSummaryCard("03", "NEW REQUESTS", "Needs review", PRIMARY_BLUE, SVG_CALENDAR), 0, 0);
        summaryGrid.add(createSummaryCard("05", "PENDING", "Awaiting decision", AMBER_TEXT, SVG_ALERT), 1, 0);
        summaryGrid.add(createSummaryCard("12", "ACCEPTED", "This month", GREEN_TEXT, SVG_CHECK), 2, 0);
        summaryGrid.add(createSummaryCard("02", "REJECTED", "This month", RED_TEXT, SVG_ALERT), 3, 0);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            summaryGrid.getColumnConstraints().add(cc);
        }

        // Split Main View: Left Request Table (68%) + Right Insights Sidebar (32%)
        HBox splitView = new HBox(20);

        VBox leftSection = new VBox(15);
        leftSection.prefWidthProperty().bind(splitView.widthProperty().multiply(0.68));

        // Filter Bar Box
        VBox filterCard = createFilterBar();

        // Incoming Event Requests Table Card
        VBox requestsCard = createRequestsTableCard();

        leftSection.getChildren().addAll(filterCard, requestsCard);

        // Right Insights Sidebar
        VBox rightSection = createOrganizerInsightsCard();
        rightSection.prefWidthProperty().bind(splitView.widthProperty().multiply(0.32));

        splitView.getChildren().addAll(leftSection, rightSection);

        content.getChildren().addAll(header, summaryGrid, splitView);
        return content;
    }

    // Summary KPI Card Component
    private VBox createSummaryCard(String count, String title, String subtitle, String accentColor, String svgIcon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SVGPath icon = createSVGIcon(svgIcon, accentColor, 0.85);

        topRow.getChildren().addAll(titleLbl, spacer, icon);

        Label numLbl = new Label(count);
        numLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subLbl = new Label(subtitle);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, numLbl, subLbl);
        return card;
    }

    // Filter Bar Card
    private VBox createFilterBar() {
        VBox filterBox = new VBox(10);
        filterBox.setPadding(new Insets(14));
        filterBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox topFilters = new HBox(10);
        topFilters.setAlignment(Pos.CENTER_LEFT);

        // Search Input
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search by event name or customer...");
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 8 12 8 12; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Status: All", "Pending", "Approved", "Rejected");
        statusCombo.setValue("Status: All");
        statusCombo.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Event Types: All", "Corporate Conference", "Corporate Meeting", "Networking Event", "Product Launch");
        typeCombo.setValue("Event Types: All");
        typeCombo.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        topFilters.getChildren().addAll(searchInput, statusCombo, typeCombo);

        HBox bottomFilters = new HBox(10);
        bottomFilters.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> datesCombo = new ComboBox<>();
        datesCombo.getItems().addAll("Dates: All", "Next 7 Days", "This Month", "Next Month");
        datesCombo.setValue("Dates: All");
        datesCombo.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Priority: All", "High Priority", "Medium Priority", "Low Priority");
        priorityCombo.setValue("Priority: All");
        priorityCombo.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

        Button clearBtn = new Button("Clear Filters");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            searchInput.clear();
            statusCombo.setValue("Status: All");
            typeCombo.setValue("Event Types: All");
            datesCombo.setValue("Dates: All");
            priorityCombo.setValue("Priority: All");
            renderRequestRows();
        });

        bottomFilters.getChildren().addAll(datesCombo, priorityCombo, clearBtn);

        filterBox.getChildren().addAll(topFilters, bottomFilters);
        return filterBox;
    }

    // Requests List Table Container
    private VBox createRequestsTableCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label cardTitle = new Label("Incoming Event Requests");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        // Table Column Headers Row
        HBox tableHeader = new HBox(125);
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 4px;");

        Label col1 = createColumnHeader("EVENT TYPE / NAME", 0.32);
        Label col2 = createColumnHeader("DETAILS", 0.28);
        col2.setTranslateX(50);
        Label col3 = createColumnHeader("PRIORITY", 0.12);
        col3.setTranslateX(70);
        Label col4 = createColumnHeader("STATUS", 0.14);
        Label col5 = createColumnHeader("ACTION", 0.14);

        tableHeader.getChildren().addAll(col1, col2, col3, col4, col5);

        requestsContainer = new VBox(0);
        renderRequestRows();

        // Pagination Bar
        HBox pagination = createPaginationBar();

        card.getChildren().addAll(cardTitle, tableHeader, requestsContainer, pagination);
        return card;
    }

    private Label createColumnHeader(String text, double widthPercent) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return label;
    }

    // Render list of request rows
    private void renderRequestRows() {
        requestsContainer.getChildren().clear();

        for (RequestData req : requestsList) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14, 12, 14, 12));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            // Col 1: Title & Customer
            VBox col1 = new VBox(3);
            Label nameLbl = new Label(req.title);
            nameLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
            Label customerLbl = new Label("By " + req.customer + " • " + req.type);
            customerLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            col1.getChildren().addAll(nameLbl, customerLbl);

            // Col 2: Date, Guests, Budget
            VBox col2 = new VBox(3);
            Label dateLbl = new Label("📅 " + req.date + " (" + req.time + ")");
            dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label detailsLbl = new Label("👥 " + req.guests + " Guests  |  💰 " + req.budget);
            detailsLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            col2.getChildren().addAll(dateLbl, detailsLbl);

            // Col 3: Priority Badge
            String priorityBg = req.priority.equalsIgnoreCase("High") ? RED_BG : LIGHT_BLUE;
            String priorityFg = req.priority.equalsIgnoreCase("High") ? RED_TEXT : BRIGHT_BLUE;
            Label priorityBadge = new Label(req.priority);
            priorityBadge.setStyle("-fx-background-color: " + priorityBg + "; -fx-text-fill: " + priorityFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 12px;");
            HBox col3 = new HBox(priorityBadge);
            col3.setAlignment(Pos.CENTER_LEFT);

            // Col 4: Status Badge
            String statusBg = AMBER_BG;
            String statusFg = AMBER_TEXT;
            if (req.status.equalsIgnoreCase("Approved")) {
                statusBg = GREEN_BG;
                statusFg = GREEN_TEXT;
            } else if (req.status.equalsIgnoreCase("Rejected")) {
                statusBg = RED_BG;
                statusFg = RED_TEXT;
            }

            Label statusBadge = new Label(req.status);
            statusBadge.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 12px;");
            HBox col4 = new HBox(statusBadge);
            col4.setAlignment(Pos.CENTER_LEFT);

            // Col 5: Action Button
            Button actionBtn = new Button("View Details →");
            actionBtn.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + NAVY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 10 6 10; -fx-background-radius: 6px; -fx-border-radius: 6px; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> openRequestDetailsModal(req));

            HBox col5 = new HBox(actionBtn);
            col5.setAlignment(Pos.CENTER_LEFT);

            // Set column width proportions
            col1.prefWidthProperty().bind(row.widthProperty().multiply(0.32));
            col2.prefWidthProperty().bind(row.widthProperty().multiply(0.28));
            col3.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
            col4.prefWidthProperty().bind(row.widthProperty().multiply(0.14));
            col5.prefWidthProperty().bind(row.widthProperty().multiply(0.14));

            row.getChildren().addAll(col1, col2, col3, col4, col5);
            requestsContainer.getChildren().add(row);
        }
    }

    private HBox createPaginationBar() {
        HBox pagination = new HBox();
        pagination.setAlignment(Pos.CENTER_LEFT);
        pagination.setPadding(new Insets(12, 0, 0, 0));

        Label countText = new Label("Showing 1–" + requestsList.size() + " of 24 requests");
        countText.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox pageButtons = new HBox(4);
        pageButtons.setAlignment(Pos.CENTER);

        Button prevBtn = new Button("Previous");
        prevBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px; -fx-cursor: hand;");

        Button p1 = new Button("1");
        p1.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 4px;");

        Button p2 = new Button("2");
        p2.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px;");

        Button nextBtn = new Button("Next");
        nextBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px; -fx-cursor: hand;");

        pageButtons.getChildren().addAll(prevBtn, p1, p2, nextBtn);

        pagination.getChildren().addAll(countText, spacer, pageButtons);
        return pagination;
    }

    // Right Organizer Insights Card
    private VBox createOrganizerInsightsCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 10px;");

        Label title = new Label("📈 Organizer Insights");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox bullet1 = createInsightBullet("⚠ 3 requests need attention", "Review 'New Requests' to maintain response SLA.");
        VBox bullet2 = createInsightBullet("❗ 2 high-priority events", "TechVision Summit and Nova Launch require immediate action.");
        VBox bullet3 = createInsightBullet("💰 ₹11.7L estimated value", "Total potential budget from pending and new requests.");
        VBox bullet4 = createInsightBullet("🏢 1 event requires venue", "TechVision Summit needs venue sourcing initiated.");

        card.getChildren().addAll(title, bullet1, bullet2, bullet3, bullet4);
        return card;
    }

    private VBox createInsightBullet(String header, String desc) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 6px;");

        Label headLbl = new Label(header);
        headLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #D1E3FF;");
        descLbl.setWrapText(true);

        box.getChildren().addAll(headLbl, descLbl);
        return box;
    }

    // ==========================================
    // 4. REQUEST DETAILS MODAL / DIALOG
    // ==========================================
    private void openRequestDetailsModal(RequestData req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(primaryStage);
        modal.setTitle("Request Details - " + req.title);

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + WHITE + "; " + FONT_FAMILY);

        Label header = new Label("Event Request Details");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);

        addDetailRow(detailsGrid, 0, "Event Name:", req.title);
        addDetailRow(detailsGrid, 1, "Customer Name:", req.customer);
        addDetailRow(detailsGrid, 2, "Event Type:", req.type);
        addDetailRow(detailsGrid, 3, "Date & Time:", req.date + " at " + req.time);
        addDetailRow(detailsGrid, 4, "Number of Guests:", req.guests + " Guests");
        addDetailRow(detailsGrid, 5, "Requested Venue:", req.venue);
        addDetailRow(detailsGrid, 6, "Estimated Budget:", req.budget);
        addDetailRow(detailsGrid, 7, "Current Status:", req.status);

        Label msgTitle = new Label("Customer Message / Requirements:");
        msgTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        TextArea msgArea = new TextArea(req.message);
        msgArea.setEditable(false);
        msgArea.setWrapText(true);
        msgArea.setPrefRowCount(3);
        msgArea.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + ";");

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> modal.close());

        Button rejectBtn = new Button("Reject Request");
        rejectBtn.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");

        Button approveBtn = new Button("Approve Request");
        approveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");

        approveBtn.setOnAction(e -> {
            modal.close();
            openApproveConfirmation(req);
        });

        rejectBtn.setOnAction(e -> {
            modal.close();
            openRejectModal(req);
        });

        if (req.status.equalsIgnoreCase("Pending")) {
            buttonBar.getChildren().addAll(closeBtn, rejectBtn, approveBtn);
        } else {
            buttonBar.getChildren().addAll(closeBtn);
        }

        content.getChildren().addAll(header, detailsGrid, msgTitle, msgArea, buttonBar);

        Scene modalScene = new Scene(content, 520, 520);
        modal.setScene(modalScene);
        modal.showAndWait();
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    // Approve Confirmation Dialog
    private void openApproveConfirmation(RequestData req) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Event Request");
        alert.setHeaderText("Approve Event Request?");
        alert.setContentText("Are you sure you want to approve '" + req.title + "' submitted by " + req.customer + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                req.status = "Approved";
                renderRequestRows();
                showToast("✓ Event request approved successfully.");
            }
        });
    }

    // Reject Dialog with Reason Text Box
    private void openRejectModal(RequestData req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(primaryStage);
        modal.setTitle("Reject Request - " + req.title);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + WHITE + "; " + FONT_FAMILY);

        Label title = new Label("Reject Event Request");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        Label desc = new Label("Please enter the reason for rejecting this event request:");
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for rejection...");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(4);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-padding: 8 14 8 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> modal.close());

        Button submitRejectBtn = new Button("Reject Request");
        submitRejectBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 14 8 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        submitRejectBtn.setOnAction(e -> {
            req.status = "Rejected";
            renderRequestRows();
            modal.close();
            showToast("✓ Event request rejected.");
        });

        buttons.getChildren().addAll(cancelBtn, submitRejectBtn);
        content.getChildren().addAll(title, desc, reasonArea, buttons);

        Scene scene = new Scene(content, 420, 260);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // Helper Utility: Icon Button
    private Button createIconButton(String svgPath) {
        Button btn = new Button();
        btn.setGraphic(createSVGIcon(svgPath, SECONDARY_TEXT, 0.9));
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 6;");
        return btn;
    }

    // Helper Utility: SVG Icon Path Generator
    private SVGPath createSVGIcon(String content, String colorHex, double scale) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(Color.web(colorHex));
        svgPath.setScaleX(scale);
        svgPath.setScaleY(scale);
        return svgPath;
    }

    // Console Log / Toast Simulation
    private void showToast(String message) {
        System.out.println("[EventXpert Notification]: " + message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
