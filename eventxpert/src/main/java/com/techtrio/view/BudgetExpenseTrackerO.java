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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BudgetExpenseTrackerO extends Application {

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

    // Status Colors
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String ORANGE_TEXT = "#B06000";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";

    // Financial State (In-Memory)
    private double approvedBudget = 500000.0;
    private double allocatedBudget = 420000.0;
    
    // UI Elements for Dynamic Updates
    private Label spentValLabel;
    private Label remainingValLabel;
    private Label utilizationPercentLabel;
    private ProgressBar utilizationBar;
    private VBox expensesTableContainer;

    // Expense Model
    private static class ExpenseItem {
        String name;
        String category;
        String vendor;
        String date;
        double amount;
        String status;

        ExpenseItem(String name, String category, String vendor, String date, double amount, String status) {
            this.name = name;
            this.category = category;
            this.vendor = vendor;
            this.date = date;
            this.amount = amount;
            this.status = status;
        }
    }

    private final List<ExpenseItem> expenseList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Initialize Sample Data
        expenseList.add(new ExpenseItem("AV Equipment", "AV & Sound", "SoundMasters Inc.", "10 May 2026", 15000, "Paid"));
        expenseList.add(new ExpenseItem("Catering Advance", "Catering", "Gourmet Bites", "12 May 2026", 20000, "Paid"));
        expenseList.add(new ExpenseItem("Photography", "Photography", "Lens & Light", "14 May 2026", 7000, "Paid"));
        expenseList.add(new ExpenseItem("Decoration", "Decoration", "Flora Designs", "Pending", 25000, "Pending"));

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
        primaryStage.setTitle("EventXpert - Budget & Expense Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial calculation
        recalculateBudget();
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
        topSearch.setPromptText("Search expenses, vendors, categories...");
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
        Label userRole = new Label("Event Director");
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

        Button createBtn = new Button("+ Create New Event");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        createBtn.setOnAction(e -> showInfoAlert("New Event", "Create Event modal coming soon."));

        sidebar.getChildren().addAll(brand, createBtn, new Separator());

        // Nav Menu
        sidebar.getChildren().addAll(
            createNavHeader("EVENT MANAGEMENT"),
            createNavItem("Dashboard", false),
            createNavItem("Event Requests", false),
            createNavItem("My Events", false),

            createNavHeader("EVENT PLANNING"),
            createNavItem("AI Planner", false),
            createNavItem("Budget", true), // Active Highlight
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

        // Breadcrumbs
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> showInfoAlert("Navigation", "Navigating to My Events..."));

        Label bcSep1 = new Label(">");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("TechVision Annual Summit");
        bc2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bcSep2 = new Label(">");
        bcSep2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc3 = new Label("Budget");
        bc3.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSep2, bc3);

        // Header Title Row
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Budget & Expense Tracker");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Monitor event spending, optimize allocations and stay within the approved budget.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label eventBadge = new Label("● TechVision 2026");
        eventBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 12 6 12; -fx-background-radius: 16; -fx-font-size: 12px;");

        Label statusBadge = new Label("In Planning");
        statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 6 12 6 12; -fx-background-radius: 6; -fx-font-size: 12px;");

        Button addExpenseBtn = new Button("+ Add Expense");
        addExpenseBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        addExpenseBtn.setOnAction(e -> handleAddExpenseDialog());

        headerRow.getChildren().addAll(titleBox, headerSpacer, eventBadge, statusBadge, addExpenseBtn);

        // Top 4 Budget Summary Cards
        HBox summaryRow = new HBox(16);
        summaryRow.getChildren().addAll(
            createSummaryCard("Approved Budget 🔒", "₹" + String.format("%,.0f", approvedBudget), TEXT_MAIN),
            createSummaryCard("Allocated", "₹" + String.format("%,.0f", allocatedBudget), TEXT_MAIN),
            createSummaryCardSpent(),
            createSummaryCardRemaining()
        );

        // Middle Section: Utilization + AI Optimizer + Sidebar Widgets
        HBox middleGrid = new HBox(20);

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        HBox middleCards = new HBox(16);
        middleCards.getChildren().addAll(
            createBudgetUtilizationCard(),
            createAIOptimizerCard()
        );

        leftCol.getChildren().add(middleCards);

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(320);
        rightCol.setMinWidth(300);

        rightCol.getChildren().addAll(
            createEventDetailsCard(),
            createBudgetAlertsCard()
        );

        middleGrid.getChildren().addAll(leftCol, rightCol);

        // Bottom Ledger / Recent Expenses Table
        VBox recentExpensesTable = createRecentExpensesSection();

        mainBox.getChildren().addAll(breadcrumbs, headerRow, summaryRow, middleGrid, recentExpensesTable);
        return mainBox;
    }

    // ==========================================
    // SUMMARY CARDS
    // ==========================================
    private VBox createSummaryCard(String title, String val, String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(t, v);
        return card;
    }

    private VBox createSummaryCardSpent() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Spent");
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        spentValLabel = new Label("₹42,000");
        spentValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");

        card.getChildren().addAll(t, spentValLabel);
        return card;
    }

    private VBox createSummaryCardRemaining() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Remaining");
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        remainingValLabel = new Label("₹4,58,000");
        remainingValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(t, remainingValLabel);
        return card;
    }

    // ==========================================
    // MIDDLE SECTION: UTILIZATION & AI OPTIMIZER
    // ==========================================
    private VBox createBudgetUtilizationCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Budget Utilization");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusTag = new Label("✓ On Track");
        statusTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 12;");

        header.getChildren().addAll(title, spacer, statusTag);

        utilizationPercentLabel = new Label("8.4% spent");
        utilizationPercentLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        utilizationBar = new ProgressBar(0.084);
        utilizationBar.setMaxWidth(Double.MAX_VALUE);
        utilizationBar.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-control-inner-background: " + LIGHT_BLUE + "; -fx-pref-height: 8;");

        HBox rangeRow = new HBox();
        Label minL = new Label("₹0");
        minL.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region rSpacer = new Region();
        HBox.setHgrow(rSpacer, Priority.ALWAYS);
        Label maxL = new Label("₹5,00,000");
        maxL.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        rangeRow.getChildren().addAll(minL, rSpacer, maxL);

        card.getChildren().addAll(header, utilizationPercentLabel, utilizationBar, rangeRow);
        return card;
    }

    private VBox createAIOptimizerCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("✨ AI Optimizer");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewAll = new Button("View All");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-cursor: hand;");
        viewAll.setOnAction(e -> showInfoAlert("AI Optimizer", "Opening all AI budget recommendations..."));

        header.getChildren().addAll(title, spacer, viewAll);

        VBox recs = new VBox(8);
        recs.getChildren().addAll(
            createAIOptionRow("AV & Sound", "Reallocate ₹5k-10k from unused buffer", "Review"),
            createAIOptionRow("Catering", "Compare premium packages", "Compare"),
            createAIOptionRow("Miscellaneous", "Move ₹15k to unexpected costs", "Apply")
        );

        card.getChildren().addAll(header, recs);
        return card;
    }

    private HBox createAIOptionRow(String category, String text, String btnText) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));

        VBox textGroup = new VBox(2);
        Label cat = new Label(category);
        cat.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label desc = new Label(text);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        textGroup.getChildren().addAll(cat, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button(btnText);
        actionBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
        actionBtn.setOnAction(e -> showInfoAlert("AI Optimizer Action", "Action performed: " + btnText + " for " + category));

        row.getChildren().addAll(textGroup, spacer, actionBtn);
        return row;
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createEventDetailsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Event Details");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox details = new VBox(8);
        details.getChildren().addAll(
            createDetailItem("📅 Date", "18 May 2026"),
            createDetailItem("👥 Guests", "250 Expected"),
            createDetailItem("📍 Venue", "Grand Horizon Center")
        );

        card.getChildren().addAll(title, details);
        return card;
    }

    private VBox createDetailItem(String label, String val) {
        VBox item = new VBox(2);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        item.getChildren().addAll(l, v);
        return item;
    }

    private VBox createBudgetAlertsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("🔔 Budget Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox alerts = new VBox(8);
        alerts.getChildren().addAll(
            createAlertItem("● Overall budget is currently on track.", GREEN_TEXT),
            createAlertItem("● Catering is nearing 80% of allocated limit.", ORANGE_TEXT),
            createAlertItem("● 3 new vendor quotes match budget constraints.", GREEN_TEXT)
        );

        card.getChildren().addAll(title, alerts);
        return card;
    }

    private Label createAlertItem(String text, String colorHex) {
        Label alert = new Label(text);
        alert.setWrapText(true);
        alert.setStyle("-fx-font-size: 11px; -fx-text-fill: " + colorHex + ";");
        return alert;
    }

    // ==========================================
    // RECENT EXPENSES SECTION & TABLE
    // ==========================================
    private VBox createRecentExpensesSection() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recent Expenses");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button fullLedgerBtn = new Button("View Full Ledger →");
        fullLedgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px; -fx-cursor: hand;");
        fullLedgerBtn.setOnAction(e -> showInfoAlert("Full Ledger", "Opening detailed general ledger report..."));

        header.getChildren().addAll(title, spacer, fullLedgerBtn);

        // Table Header
        GridPane tableHeader = new GridPane();
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6;");

        tableHeader.add(createHeaderLabel("Category"), 0, 0);
        tableHeader.add(createHeaderLabel("Vendor"), 1, 0);
        tableHeader.add(createHeaderLabel("Date"), 2, 0);
        tableHeader.add(createHeaderLabel("Amount"), 3, 0);
        tableHeader.add(createHeaderLabel("Status"), 4, 0);
        tableHeader.add(createHeaderLabel("Action"), 5, 0);

        setGridConstraints(tableHeader);

        expensesTableContainer = new VBox(2);
        renderExpenseRows();

        card.getChildren().addAll(header, tableHeader, expensesTableContainer);
        return card;
    }

    private Label createHeaderLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return l;
    }

    private void renderExpenseRows() {
        expensesTableContainer.getChildren().clear();

        for (int i = 0; i < expenseList.size(); i++) {
            ExpenseItem item = expenseList.get(i);
            GridPane row = new GridPane();
            row.setPadding(new Insets(12, 12, 12, 12));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            row.add(new Label("🏷️ " + item.name), 0, 0);
            row.add(new Label(item.vendor), 1, 0);
            row.add(new Label(item.date), 2, 0);
            
            Label amtLbl = new Label("₹" + String.format("%,.0f", item.amount));
            amtLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            row.add(amtLbl, 3, 0);

            // Status Badge
            Label statusBadge = new Label(item.status);
            if (item.status.equalsIgnoreCase("Paid")) {
                statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            } else {
                statusBadge.setStyle("-fx-background-color: " + ORANGE_BG + "; -fx-text-fill: " + ORANGE_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            row.add(statusBadge, 4, 0);

            // Action Options Menu Button
            int index = i;
            MenuButton actionBtn = new MenuButton("⋮");
            actionBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");

            MenuItem deleteItem = new MenuItem("Delete Expense");
            deleteItem.setOnAction(e -> handleDeleteExpense(index));

            actionBtn.getItems().add(deleteItem);
            row.add(actionBtn, 5, 0);

            setGridConstraints(row);
            expensesTableContainer.getChildren().add(row);
        }
    }

    private void setGridConstraints(GridPane grid) {
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(25);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(18);
        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPercentWidth(14);
        ColumnConstraints c5 = new ColumnConstraints();
        c5.setPercentWidth(10);
        ColumnConstraints c6 = new ColumnConstraints();
        c6.setPercentWidth(8);

        grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5, c6);
    }

    // ==========================================
    // FINANCIAL CALCULATIONS & HANDLERS
    // ==========================================
    private void recalculateBudget() {
        double totalSpent = 0;
        for (ExpenseItem item : expenseList) {
            totalSpent += item.amount;
        }

        double remaining = approvedBudget - totalSpent;
        double percentSpent = (totalSpent / approvedBudget);

        spentValLabel.setText("₹" + String.format("%,.0f", totalSpent));
        remainingValLabel.setText("₹" + String.format("%,.0f", remaining));
        utilizationPercentLabel.setText(String.format("%.1f%% spent", percentSpent * 100));
        utilizationBar.setProgress(percentSpent);
    }

    private void handleAddExpenseDialog() {
        Dialog<ExpenseItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Expense");
        dialog.setHeaderText("Enter details for new event expense");

        ButtonType addBtnType = new ButtonType("Add Expense", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameInput = new TextField();
        nameInput.setPromptText("e.g. AV Equipment");

        TextField vendorInput = new TextField();
        vendorInput.setPromptText("e.g. SoundMasters Inc.");

        TextField dateInput = new TextField("18 May 2026");

        TextField amtInput = new TextField();
        amtInput.setPromptText("e.g. 15000");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Paid", "Pending");
        statusCombo.setValue("Paid");

        grid.add(new Label("Expense Name:"), 0, 0);
        grid.add(nameInput, 1, 0);
        grid.add(new Label("Vendor:"), 0, 1);
        grid.add(vendorInput, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateInput, 1, 2);
        grid.add(new Label("Amount (₹):"), 0, 3);
        grid.add(amtInput, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                try {
                    double amt = Double.parseDouble(amtInput.getText().trim());
                    return new ExpenseItem(
                        nameInput.getText().trim(),
                        "General",
                        vendorInput.getText().trim(),
                        dateInput.getText().trim(),
                        amt,
                        statusCombo.getValue()
                    );
                } catch (NumberFormatException e) {
                    showInfoAlert("Invalid Input", "Please enter a valid numeric amount.");
                }
            }
            return null;
        });

        Optional<ExpenseItem> result = dialog.showAndWait();
        result.ifPresent(expense -> {
            expenseList.add(expense);
            renderExpenseRows();
            recalculateBudget();
            showInfoAlert("Success", "Expense added successfully.");
        });
    }

    private void handleDeleteExpense(int index) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Expense");
        alert.setHeaderText("Delete this expense?");
        alert.setContentText("Are you sure you want to delete this expense entry?");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            expenseList.remove(index);
            renderExpenseRows();
            recalculateBudget();
        }
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}