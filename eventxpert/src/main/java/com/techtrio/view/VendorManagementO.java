package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VendorManagementO {

    // --- COLOR PALETTE CONSTANTS ---
    private static final String COLOR_PRIMARY = "#2447B8";
    private static final String COLOR_DARK_BLUE = "#17368F";
    private static final String COLOR_LIGHT_BLUE = "#EAF1FF";
    private static final String COLOR_BG = "#F7F9FC";
    private static final String COLOR_WHITE = "#FFFFFF";
    private static final String COLOR_MAIN_TEXT = "#172033";
    private static final String COLOR_SECONDARY_TEXT = "#64748B";
    private static final String COLOR_BORDER = "#DCE3EC";
    private static final String COLOR_GREEN = "#16A34A";
    private static final String COLOR_GREEN_BG = "#F0FDF4";
    private static final String COLOR_ORANGE = "#D97706";
    private static final String COLOR_ORANGE_BG = "#FFFBEB";
    private static final String COLOR_RED = "#DC2626";

    // --- DATA MODELS ---
    public static class Vendor {
        private String name;
        private String category;
        private String contact;
        private String phone;
        private String email;
        private String status; // Confirmed, Pending, At Risk
        private double rating;

        public Vendor(String name, String category, String contact, String phone, String email, String status, double rating) {
            this.name = name;
            this.category = category;
            this.contact = contact;
            this.phone = phone;
            this.email = email;
            this.status = status;
            this.rating = rating;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getContact() { return contact; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getStatus() { return status; }
        public double getRating() { return rating; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class Requirement {
        private String category;
        private String icon;
        private String vendorName;
        private String budget;
        private String status; // Confirmed, Pending
        private String subStatus; // e.g. "Awaiting Quotes", "Shortlisting"

        public Requirement(String category, String icon, String vendorName, String budget, String status, String subStatus) {
            this.category = category;
            this.icon = icon;
            this.vendorName = vendorName;
            this.budget = budget;
            this.status = status;
            this.subStatus = subStatus;
        }

        public String getCategory() { return category; }
        public String getIcon() { return icon; }
        public String getVendorName() { return vendorName; }
        public void setVendorName(String vendorName) { this.vendorName = vendorName; }
        public String getBudget() { return budget; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getSubStatus() { return subStatus; }
        public void setSubStatus(String subStatus) { this.subStatus = subStatus; }
    }

    // --- STATE ---
    private List<Requirement> requirementsList = new ArrayList<>();
    private List<Vendor> masterVendorList = new ArrayList<>();
    private GridPane requirementsGrid;

    // Filters
    private TextField txtSearch;
    private ComboBox<String> cbCategory;
    private ComboBox<String> cbLocation;
    private ComboBox<String> cbStatus;

    /**
     * Main entry point required by navigation. Returns the Vendor Management Scene.
     */
    public Scene getvendorManagementOScene(Runnable callBackAction) {
        initSampleData();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // 1. LEFT SIDEBAR
        VBox sidebar = createSidebar(callBackAction);
        root.setLeft(sidebar);

        // 2. MAIN CONTAINER (TOP BAR + CONTENT)
        VBox mainContainer = new VBox();
        HBox topBar = createTopBar();

        ScrollPane scrollContent = createScrollableContent();
        VBox.setVgrow(scrollContent, Priority.ALWAYS);

        mainContainer.getChildren().addAll(topBar, scrollContent);
        root.setCenter(mainContainer);

        return new Scene(root, 1550, 750);
    }

    private void initSampleData() {
        masterVendorList.clear();
        masterVendorList.add(new Vendor("VisionPro AV", "AV & Sound", "Amit Sharma", "+91 98220 11223", "amit@visionpro.in", "Confirmed", 4.8));
        masterVendorList.add(new Vendor("FreshBite Catering", "Catering", "Neha Mehta", "+91 98190 44321", "neha@freshbite.in", "Confirmed", 4.7));
        masterVendorList.add(new Vendor("Elegant Events", "Decoration", "Rohan Patil", "+91 97654 32109", "rohan@elegantevents.in", "At Risk", 4.5));
        masterVendorList.add(new Vendor("PixelFrame Studios", "Photography", "Sneha Joshi", "+91 99887 66554", "sneha@pixelframe.in", "Confirmed", 4.9));
        masterVendorList.add(new Vendor("SafeGuard Security", "Security", "Rajesh Kumar", "+91 91234 56789", "rajesh@safeguard.in", "Pending", 4.4));

        requirementsList.clear();
        requirementsList.add(new Requirement("AV & Sound", "🎙", "VisionPro AV", "₹80,000", "Confirmed", "Vendor Confirmed"));
        requirementsList.add(new Requirement("Catering", "🍴", "", "₹1,25,000", "Pending", "Awaiting Quotes"));
        requirementsList.add(new Requirement("Photography", "📷", "PixelFrame Studios", "₹35,000", "Confirmed", "Vendor Confirmed"));
        requirementsList.add(new Requirement("Decoration & Setup", "✨", "", "₹45,000", "Pending", "Shortlisting"));
    }

    // ==========================================
    // SIDEBAR NAVIGATION
    // ==========================================
    private VBox createSidebar(Runnable callBackAction) {
        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(20, 16, 20, 16));
        sidebar.setStyle("-fx-background-color: " + COLOR_WHITE + "; -fx-border-color: transparent " + COLOR_BORDER + " transparent transparent;");

        // Brand Logo
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 10, 8));

        SVGPath logoIcon = new SVGPath();
        logoIcon.setContent("M19 3H5C3.89 3 3 3.89 3 5V19C3 20.1 3.89 21 5 21H19C20.1 21 21 20.1 21 19V5C21 3.89 20.1 3 19 3ZM10 17L5 12L6.41 10.59L10 14.17L17.59 6.58L19 8L10 17Z");
        logoIcon.setFill(Color.web(COLOR_PRIMARY));
        logoIcon.setScaleX(0.8);
        logoIcon.setScaleY(0.8);

        Label logoText = new Label("EventXpert");
        logoText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");
        brand.getChildren().addAll(logoIcon, logoText);

        // Create New Event Button
        Button btnCreateEvent = new Button("+ Create New Event");
        btnCreateEvent.setMaxWidth(Double.MAX_VALUE);
        btnCreateEvent.setPrefHeight(38);
        btnCreateEvent.setStyle(
            "-fx-background-color: " + COLOR_PRIMARY + ";" +
            "-fx-text-fill: " + COLOR_WHITE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );

        // Navigation Sections
        VBox navItems = new VBox(4);
        
        navItems.getChildren().add(createNavSectionHeader("PLANNING HUB"));
        navItems.getChildren().add(createNavItem("Dashboard", false, callBackAction));
        navItems.getChildren().add(createNavItem("Event Requests", false, callBackAction));
        navItems.getChildren().add(createNavItem("My Events", false, callBackAction));
        navItems.getChildren().add(createNavItem("AI Planner", false, callBackAction));
        navItems.getChildren().add(createNavItem("Budget", false, callBackAction));
        navItems.getChildren().add(createNavItem("Venue", false, callBackAction));
        navItems.getChildren().add(createNavItem("Vendors", true, callBackAction)); // ACTIVE ITEM
        navItems.getChildren().add(createNavItem("Schedule", false, callBackAction));
        navItems.getChildren().add(createNavItem("Guest Management", false, callBackAction));

        navItems.getChildren().add(createNavSectionHeader("EXECUTION"));
        navItems.getChildren().add(createNavItem("QR Check-In", false, callBackAction));
        navItems.getChildren().add(createNavItem("Live Event", false, callBackAction));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(4);
        bottomNav.getChildren().add(createNavItem("Settings", false, callBackAction));
        bottomNav.getChildren().add(createNavItem("Support", false, callBackAction));

        sidebar.getChildren().addAll(brand, btnCreateEvent, navItems, spacer, bottomNav);
        return sidebar;
    }

    private Label createNavSectionHeader(String title) {
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");
        lbl.setPadding(new Insets(12, 0, 4, 8));
        return lbl;
    }

    private HBox createNavItem(String title, boolean isActive, Runnable callBackAction) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setPrefHeight(34);

        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: " + (isActive ? "bold;" : "normal;") +
                     "-fx-text-fill: " + (isActive ? COLOR_PRIMARY : COLOR_MAIN_TEXT) + ";");

        if (isActive) {
            item.setStyle("-fx-background-color: " + COLOR_LIGHT_BLUE + "; -fx-background-radius: 6px;");
        } else {
            item.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-background-radius: 6px;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent;"));
            item.setOnMouseClicked(e -> {
                if (callBackAction != null) callBackAction.run();
            });
        }

        item.getChildren().add(lbl);
        return item;
    }

    // ==========================================
    // TOP NAVIGATION BAR
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + COLOR_WHITE + "; -fx-border-color: transparent transparent " + COLOR_BORDER + " transparent;");

        // Search Field
        TextField globalSearch = new TextField();
        globalSearch.setPromptText("🔍  Search events, vendors, attendees...");
        globalSearch.setPrefWidth(420);
        globalSearch.setPrefHeight(36);
        globalSearch.setStyle(
            "-fx-background-color: " + COLOR_BG + ";" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;" +
            "-fx-font-size: 12px;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Notification & User Controls
        Button btnNotify = new Button("🔔");
        btnNotify.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");

        Button btnHelp = new Button("❓");
        btnHelp.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");

        Circle avatar = new Circle(16, Color.web(COLOR_PRIMARY));
        Label avatarText = new Label("A");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);
        avatarPane.setStyle("-fx-cursor: hand;");

        topBar.getChildren().addAll(globalSearch, spacer, btnNotify, btnHelp, avatarPane);
        return topBar;
    }

    // ==========================================
    // MAIN SCROLLABLE CONTENT AREA
    // ==========================================
    private ScrollPane createScrollableContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 28, 32, 28));

        // 1. Breadcrumb & Header
        VBox headerArea = createHeaderArea();

        // 2. Summary Metrics Cards (4 Horizontal Cards)
        HBox summaryCards = createSummaryCards();

        // 3. Search & Filter Bar
        VBox filterSection = createFilterBar();

        // 4. Vendor Requirements Grid + Right Side Cards
        HBox mainGrid = createMainLayoutGrid();

        content.getChildren().addAll(headerArea, summaryCards, filterSection, mainGrid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + COLOR_BG + ";");
        return scrollPane;
    }

    private VBox createHeaderArea() {
        HBox breadcrumb = new HBox(6);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);
        Label b1 = new Label("My Events");
        b1.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 12px;");
        Label sep1 = new Label(">");
        sep1.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 11px;");
        Label b2 = new Label("TechVision Annual Summit");
        b2.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 12px;");
        Label sep2 = new Label(">");
        sep2.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 11px;");
        Label b3 = new Label("Vendors");
        b3.setStyle("-fx-text-fill: " + COLOR_MAIN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        breadcrumb.getChildren().addAll(b1, sep1, b2, sep2, b3);

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(2);
        Label mainTitle = new Label("Vendor Management");
        mainTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");
        Label subTitle = new Label("Find, compare and manage trusted vendors for your event.");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");
        titles.getChildren().addAll(mainTitle, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status Badge
        Label badge = new Label("●  TechVision Annual Summit (In Planning)");
        badge.setStyle(
            "-fx-background-color: " + COLOR_LIGHT_BLUE + ";" +
            "-fx-text-fill: " + COLOR_PRIMARY + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 16px;"
        );

        titleRow.getChildren().addAll(titles, spacer, badge);

        VBox header = new VBox(6, breadcrumb, titleRow);
        return header;
    }

    // ==========================================
    // SUMMARY CARDS
    // ==========================================
    private HBox createSummaryCards() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        HBox c1 = createMetricCard("REQUIRED VENDORS", "6", "Total categories", COLOR_MAIN_TEXT);
        HBox c2 = createMetricCard("CONFIRMED", "3", "Vendors confirmed", COLOR_GREEN);
        HBox c3 = createMetricCard("PENDING", "2", "Awaiting confirmation", COLOR_ORANGE);
        HBox c4 = createMetricCard("BUDGET ALLOCATED", "₹2,45,000", "For vendors", COLOR_MAIN_TEXT);

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private HBox createMetricCard(String title, String value, String subtitle, String valueColor) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + COLOR_WHITE + ";" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        Label lblVal = new Label(value);
        lblVal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + valueColor + ";");

        Label lblSub = new Label(subtitle);
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        card.getChildren().addAll(lblTitle, lblVal, lblSub);
        HBox container = new HBox(card);
        HBox.setHgrow(card, Priority.ALWAYS);
        return container;
    }

    // ==========================================
    // FILTER BAR
    // ==========================================
    private VBox createFilterBar() {
        HBox filterCard = new HBox(12);
        filterCard.setAlignment(Pos.CENTER_LEFT);
        filterCard.setPadding(new Insets(12, 16, 12, 16));
        filterCard.setStyle(
            "-fx-background-color: " + COLOR_WHITE + ";" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );

        txtSearch = new TextField();
        txtSearch.setPromptText("Search vendors...");
        txtSearch.setPrefWidth(200);
        txtSearch.setPrefHeight(34);
        txtSearch.setStyle(getFilterControlStyle());
        txtSearch.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        cbCategory = new ComboBox<>();
        cbCategory.getItems().addAll("All Categories", "AV & Sound", "Catering", "Decoration", "Photography", "Security");
        cbCategory.setValue("All Categories");
        cbCategory.setPrefHeight(34);
        cbCategory.setStyle(getFilterControlStyle());
        cbCategory.setOnAction(e -> applyFilters());

        cbLocation = new ComboBox<>();
        cbLocation.getItems().addAll("Pune (Default)", "Mumbai", "Delhi", "Bangalore");
        cbLocation.setValue("Pune (Default)");
        cbLocation.setPrefHeight(34);
        cbLocation.setStyle(getFilterControlStyle());
        cbLocation.setOnAction(e -> applyFilters());

        cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Any Status", "Confirmed", "Pending", "At Risk");
        cbStatus.setValue("Any Status");
        cbStatus.setPrefHeight(34);
        cbStatus.setStyle(getFilterControlStyle());
        cbStatus.setOnAction(e -> applyFilters());

        Button btnClear = new Button("Clear");
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        btnClear.setOnAction(e -> {
            txtSearch.clear();
            cbCategory.setValue("All Categories");
            cbLocation.setValue("Pune (Default)");
            cbStatus.setValue("Any Status");
            applyFilters();
        });

        filterCard.getChildren().addAll(txtSearch, cbCategory, cbLocation, cbStatus, btnClear);
        return new VBox(filterCard);
    }

    private String getFilterControlStyle() {
        return "-fx-background-color: " + COLOR_WHITE + ";" +
               "-fx-border-color: " + COLOR_BORDER + ";" +
               "-fx-border-radius: 6px;" +
               "-fx-background-radius: 6px;" +
               "-fx-font-size: 12px;";
    }

    // ==========================================
    // MAIN LAYOUT GRID (LEFT: REQUIREMENTS, RIGHT: TRACKER/ALERTS)
    // ==========================================
    private HBox createMainLayoutGrid() {
        HBox layout = new HBox(20);
        layout.setAlignment(Pos.TOP_LEFT);

        // LEFT COLUMN: Requirements Grid
        VBox leftCol = new VBox(12);
        Label lblReqTitle = new Label("Vendor Requirements");
        lblReqTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        requirementsGrid = new GridPane();
        requirementsGrid.setHgap(16);
        requirementsGrid.setVgap(16);

        renderRequirementsGrid(requirementsList);

        leftCol.getChildren().addAll(lblReqTitle, requirementsGrid);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // RIGHT COLUMN: Tracker & Alerts Cards
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(320);
        rightCol.setMinWidth(320);

        VBox budgetTrackerCard = createBudgetTrackerCard();
        VBox alertsCard = createAlertsCard();

        rightCol.getChildren().addAll(budgetTrackerCard, alertsCard);

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private void renderRequirementsGrid(List<Requirement> reqs) {
        requirementsGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        if (reqs.isEmpty()) {
            Label noMatch = new Label("No vendor requirements match your active filters.");
            noMatch.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 13px;");
            requirementsGrid.add(noMatch, 0, 0);
            return;
        }

        for (Requirement req : reqs) {
            VBox card = createRequirementCard(req);
            GridPane.setHgrow(card, Priority.ALWAYS);
            requirementsGrid.add(card, col, row);

            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createRequirementCard(Requirement req) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + COLOR_WHITE + ";" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );

        // Top Row: Category + Status Badge
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label catLabel = new Label(req.getIcon() + "  " + req.getCategory());
        catLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean isConfirmed = "Confirmed".equalsIgnoreCase(req.getStatus());
        Label badge = new Label(isConfirmed ? "Confirmed" : "Pending");
        badge.setStyle(
            "-fx-background-color: " + (isConfirmed ? COLOR_GREEN_BG : COLOR_ORANGE_BG) + ";" +
            "-fx-text-fill: " + (isConfirmed ? COLOR_GREEN : COLOR_ORANGE) + ";" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 4 8 4 8;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-color: " + (isConfirmed ? "#BBF7D0" : "#FDE68A") + ";" +
            "-fx-border-radius: 4px;"
        );

        topRow.getChildren().addAll(catLabel, spacer, badge);

        // Middle Row: Vendor / Status Detail
        VBox midBox = new VBox(4);
        String detailText = isConfirmed ? "Vendor: " + req.getVendorName() : "Status: " + req.getSubStatus();
        Label lblDetail = new Label(detailText);
        lblDetail.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        Label lblBudget = new Label("Budget: " + req.getBudget());
        lblBudget.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        midBox.getChildren().addAll(lblDetail, lblBudget);

        // Bottom Action Button
        Button btnAction = new Button();
        btnAction.setMaxWidth(Double.MAX_VALUE);
        btnAction.setPrefHeight(34);

        if (isConfirmed) {
            btnAction.setText("View Vendor");
            btnAction.setStyle(
                "-fx-background-color: " + COLOR_WHITE + ";" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-text-fill: " + COLOR_MAIN_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;"
            );
            btnAction.setOnAction(e -> openViewVendorDialog(req.getVendorName(), req.getCategory()));
        } else {
            btnAction.setText("Find Vendors");
            btnAction.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                "-fx-text-fill: " + COLOR_WHITE + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;"
            );
            btnAction.setOnAction(e -> openFindVendorsDialog(req));
        }

        card.getChildren().addAll(topRow, midBox, btnAction);
        return card;
    }

    // ==========================================
    // RIGHT PANEL CARDS (BUDGET TRACKER & ALERTS)
    // ==========================================
    private VBox createBudgetTrackerCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + COLOR_WHITE + ";" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );

        Label title = new Label("📊  Vendor Budget Tracker");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        // Values
        HBox statsRow = new HBox();
        VBox allocBox = new VBox(2, new Label("ALLOCATED"), new Label("₹2,45,000"));
        ((Label) allocBox.getChildren().get(0)).setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");
        ((Label) allocBox.getChildren().get(1)).setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox commitBox = new VBox(2, new Label("COMMITTED"), new Label("₹1,45,000"));
        ((Label) commitBox.getChildren().get(0)).setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");
        ((Label) commitBox.getChildren().get(1)).setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");

        statsRow.getChildren().addAll(allocBox, spacer, commitBox);

        // Progress Bar
        ProgressBar progress = new ProgressBar(145000.0 / 245000.0);
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setPrefHeight(8);
        progress.setStyle("-fx-accent: " + COLOR_PRIMARY + ";");

        // Remaining
        HBox remainRow = new HBox();
        Label lblRemainText = new Label("Remaining");
        lblRemainText.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label lblRemainVal = new Label("₹1,00,000");
        lblRemainVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_GREEN + ";");

        remainRow.getChildren().addAll(lblRemainText, spacer2, lblRemainVal);

        card.getChildren().addAll(title, statsRow, progress, remainRow);
        return card;
    }

    private VBox createAlertsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + COLOR_WHITE + ";" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );

        Label title = new Label("🔔  Vendor Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        // Alert Box 1 (Orange/Warning)
        VBox alert1 = createAlertItem("⚠  Catering Pending", "Secure a caterer soon to guarantee availability for 250 pax.", COLOR_ORANGE, COLOR_ORANGE_BG, "#FDE68A");

        // Alert Box 2 (Green/Success)
        VBox alert2 = createAlertItem("✓  AV Setup Confirmed", "VisionPro AV has acknowledged the layout plan.", COLOR_GREEN, COLOR_GREEN_BG, "#BBF7D0");

        card.getChildren().addAll(title, alert1, alert2);
        return card;
    }

    private VBox createAlertItem(String alertTitle, String message, String textColor, String bgColor, String borderColor) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10, 12, 10, 12));
        box.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;"
        );

        Label t = new Label(alertTitle);
        t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        box.getChildren().addAll(t, m);
        return box;
    }

    // ==========================================
    // FILTERING LOGIC
    // ==========================================
    private void applyFilters() {
        String query = txtSearch.getText().toLowerCase().trim();
        String selCat = cbCategory.getValue();
        String selStat = cbStatus.getValue();

        List<Requirement> filtered = requirementsList.stream().filter(req -> {
            // Text Search
            boolean matchesSearch = query.isEmpty() ||
                req.getCategory().toLowerCase().contains(query) ||
                req.getVendorName().toLowerCase().contains(query) ||
                req.getSubStatus().toLowerCase().contains(query);

            // Category Filter
            boolean matchesCat = selCat == null || "All Categories".equals(selCat) ||
                req.getCategory().equalsIgnoreCase(selCat);

            // Status Filter
            boolean matchesStatus = selStat == null || "Any Status".equals(selStat) ||
                req.getStatus().equalsIgnoreCase(selStat);

            return matchesSearch && matchesCat && matchesStatus;
        }).collect(Collectors.toList());

        renderRequirementsGrid(filtered);
    }

    // ==========================================
    // INTERACTIVE DIALOGS
    // ==========================================
    private void openViewVendorDialog(String vendorName, String category) {
        Vendor target = masterVendorList.stream()
            .filter(v -> v.getName().equalsIgnoreCase(vendorName))
            .findFirst()
            .orElse(new Vendor(vendorName, category, "Contact N/A", "+91 90000 00000", "contact@vendor.in", "Confirmed", 4.5));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Vendor Details - " + target.getName());
        dialog.setHeaderText(null);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        Label name = new Label(target.getName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        Label cat = new Label("Category: " + target.getCategory());
        cat.setStyle("-fx-font-size: 13px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        Separator sep = new Separator();

        GridPane details = new GridPane();
        details.setHgap(10);
        details.setVgap(8);

        addDetailRow(details, 0, "Contact Person:", target.getContact());
        addDetailRow(details, 1, "Phone:", target.getPhone());
        addDetailRow(details, 2, "Email:", target.getEmail());
        addDetailRow(details, 3, "Contract Status:", target.getStatus());
        addDetailRow(details, 4, "Payment Status:", "50% Advance Paid");
        addDetailRow(details, 5, "Rating:", "★ " + target.getRating() + " / 5.0");

        content.getChildren().addAll(name, cat, sep, details);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void addDetailRow(GridPane grid, int row, String label, String val) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");
        Label value = new Label(val);
        value.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");
        grid.add(lbl, 0, row);
        grid.add(value, 1, row);
    }

    private void openFindVendorsDialog(Requirement req) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Find Vendors - " + req.getCategory());

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.setPrefWidth(420);

        Label title = new Label("Available Vendors for " + req.getCategory());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        VBox vendorList = new VBox(8);

        // Filter master vendors for this category
        List<Vendor> matching = masterVendorList.stream()
            .filter(v -> v.getCategory().toLowerCase().contains(req.getCategory().toLowerCase()) || req.getCategory().toLowerCase().contains(v.getCategory().toLowerCase()))
            .collect(Collectors.toList());

        if (matching.isEmpty()) {
            // Add a default vendor option if none in master list
            matching.add(new Vendor("Elite " + req.getCategory() + " Services", req.getCategory(), "Vikram Singh", "+91 98330 22110", "info@elite.in", "Available", 4.6));
        }

        for (Vendor v : matching) {
            HBox vRow = new HBox(12);
            vRow.setAlignment(Pos.CENTER_LEFT);
            vRow.setPadding(new Insets(10));
            vRow.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6px;");

            VBox vInfo = new VBox(2);
            Label vName = new Label(v.getName());
            vName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            Label vSub = new Label("★ " + v.getRating() + "  •  " + v.getContact());
            vSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");
            vInfo.getChildren().addAll(vName, vSub);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Button btnSelect = new Button("Add to Event");
            btnSelect.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
            btnSelect.setOnAction(e -> {
                req.setVendorName(v.getName());
                req.setStatus("Confirmed");
                req.setSubStatus("Vendor Confirmed");
                applyFilters();
                dialog.setResult(ButtonType.CLOSE);
                dialog.close();
            });

            vRow.getChildren().addAll(vInfo, sp, btnSelect);
            vendorList.getChildren().add(vRow);
        }

        content.getChildren().addAll(title, vendorList);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }
}