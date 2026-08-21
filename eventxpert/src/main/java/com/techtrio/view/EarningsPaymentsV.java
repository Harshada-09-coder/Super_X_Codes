package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EarningsPaymentsV extends Application {

    // Color Palette Constants
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#D97706";
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";

    // Data Structure for Financial Transaction
    private static class TransactionData {
        String date;
        String type;
        String eventName;
        String amount;
        String fee;
        String net;
        String status;

        TransactionData(String date, String type, String eventName, String amount, String fee, String net, String status) {
            this.date = date;
            this.type = type;
            this.eventName = eventName;
            this.amount = amount;
            this.fee = fee;
            this.net = net;
            this.status = status;
        }
    }

    private final List<TransactionData> transactions = new ArrayList<>();
    private VBox tableRowsContainer;

    @Override
    public void start(Stage primaryStage) {
        initSampleTransactions();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left - Fixed 250px Full Height)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Main Application Container
        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Top Bar
        HBox topBar = createTopBar(primaryStage);
        mainArea.setTop(topBar);

        // Scrollable Center Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build Sections
        mainContent.getChildren().addAll(
                createHeaderAndBreadcrumb(primaryStage),
                createSummaryCardsRow(),
                createTwoColumnMainLayout(primaryStage),
                createPaginationRow()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Earnings & Payments");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleTransactions() {
        transactions.add(new TransactionData("18 May 2026", "Booking Payment", "TechVision Annual Summit", "₹85,000", "-₹4,250", "₹80,750", "Paid"));
        transactions.add(new TransactionData("22 May 2026", "Booking Payment", "Leadership Summit 2026", "₹45,000", "-₹2,250", "₹42,750", "Paid"));
        transactions.add(new TransactionData("05 Jun 2026", "Booking Payment", "Startup Connect 2026", "₹72,000", "-₹3,600", "₹68,400", "Pending"));
        transactions.add(new TransactionData("12 Jun 2026", "Booking Payment", "Annual Gala Night", "₹60,000", "-₹3,000", "₹57,000", "Paid"));
    }

    // ==========================================
    // 1. SIDEBAR (FULL HEIGHT)
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

        // Logo / Branding Area
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("E");
        logoIcon.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 4 10; -fx-background-radius: 6px;");
        
        VBox brandBox = new VBox(0);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label brandSub = new Label("VENDOR PORTAL");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(brandTitle, brandSub);
        logoBox.getChildren().addAll(logoIcon, brandBox);

        // Top Action Button
        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> showToast(stage, "Add Service feature"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        // Menu Sections
        sidebar.getChildren().add(createSidebarCategory("OVERVIEW"));
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));

        sidebar.getChildren().add(createSidebarCategory("SERVICE MANAGEMENT"));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Availability Calendar", false, e -> showToast(stage, "Opening Calendar...")));

        sidebar.getChildren().add(createSidebarCategory("FINANCE"));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", true, e -> {})); // Active Menu

        sidebar.getChildren().add(createSidebarCategory("COMMUNICATION"));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));

        sidebar.getChildren().add(createSidebarCategory("ACCOUNT"));
        sidebar.getChildren().add(createSidebarItem("👤 Profile & Settings", false, e -> showToast(stage, "Opening Profile...")));
        sidebar.getChildren().add(createSidebarItem("❓ Help & Support", false, e -> showToast(stage, "Opening Support...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom Vendor Profile Pill
        HBox profilePill = new HBox(10);
        profilePill.setAlignment(Pos.CENTER_LEFT);
        profilePill.setPadding(new Insets(10));
        profilePill.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(WHITE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("VisionPro Events");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("Audio & Visual • ● Available");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
    }

    private Label createSidebarCategory(String title) {
        Label cat = new Label(title);
        cat.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-padding: 14 8 4 8;");
        return cat;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        if (isActive) {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        return btn;
    }

    private HBox createSidebarItemWithBadge(String title, String badgeCount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Button btn = createSidebarItem(title, false, e -> {});
        HBox.setHgrow(btn, Priority.ALWAYS);

        Label badge = new Label(badgeCount);
        badge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");

        row.getChildren().addAll(btn, badge);
        return row;
    }

    // ==========================================
    // 2. TOP BAR
    // ==========================================
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search transactions, events, or services...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓"); helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button notifBtn = new Button("🔔"); notifBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button settingsBtn = new Button("⚙"); settingsBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("VP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifBtn, settingsBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createHeaderAndBreadcrumb(Stage stage) {
        VBox header = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Earnings & Payments");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = createSecondaryButton("📥 Export Report");
        exportBtn.setOnAction(e -> showToast(stage, "Exporting financial report PDF..."));

        Button dlBtn = createPrimaryButton("📄 Download Statement");
        dlBtn.setOnAction(e -> showToast(stage, "Downloading statement..."));

        titleRow.getChildren().addAll(title, spacer, exportBtn, dlBtn);

        Label subtitle = new Label("Track your revenue, payments and financial performance.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW (MATCHING STITCH)
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createSummaryCard("TOTAL EARNINGS", "₹8,42,500", "📈 +12.5% vs last year", PRIMARY_BLUE, "💳"),
                createSummaryCard("THIS MONTH", "₹2,48,500", "📈 +8.2% vs last month", PRIMARY_BLUE, "📅"),
                createSummaryCard("PENDING PAYMENTS", "₹42,000", "3 invoices pending", WARNING_TEXT, "⏱"),
                createSummaryCard("RECEIVED", "₹2,06,500", "83% of monthly revenue", GREEN_TEXT, "✓")
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 4 8; -fx-background-radius: 50%;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label s = new Label(sub);
        if (sub.contains("+")) {
            s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold;");
        } else {
            s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        }

        card.getChildren().addAll(top, v, s);
        return card;
    }

    // ==========================================
    // 5. TWO-COLUMN MAIN LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column (Analytics & Transactions)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEarningsChartCard(),
                createRevenueByServiceCard(),
                createTransactionsTableCard(stage)
        );

        // Right Column (Insights & Payout Breakdown)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createFinancialInsightCard(),
                createMonthlyBreakdownCard(stage),
                createPayoutAccountCard(stage)
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createEarningsChartCard() {
        VBox card = createCardContainer();

        HBox head = new HBox();
        head.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Earnings Overview");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        ComboBox<String> filterCB = new ComboBox<>();
        filterCB.getItems().addAll("Current Month", "Last 30 Days", "This Year");
        filterCB.setValue("Current Month");

        head.getChildren().addAll(title, sp, filterCB);

        // Custom JavaFX Visual Line Path (Weekly Trend Chart)
        Pane chartArea = new Pane();
        chartArea.setPrefHeight(160);
        chartArea.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        Path linePath = new Path();
        linePath.getElements().addAll(
                new MoveTo(20, 130),
                new LineTo(140, 90),
                new LineTo(280, 80),
                new LineTo(420, 30)
        );
        linePath.setStroke(Color.web(PRIMARY_BLUE));
        linePath.setStrokeWidth(3);

        chartArea.getChildren().add(linePath);

        HBox xLabels = new HBox();
        xLabels.setAlignment(Pos.CENTER);
        xLabels.setSpacing(100);
        xLabels.setPadding(new Insets(6, 0, 0, 20));
        xLabels.getChildren().addAll(
                new Label("Week 1"), new Label("Week 2"), new Label("Week 3"), new Label("Week 4")
        );
        xLabels.getChildren().forEach(n -> n.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        card.getChildren().addAll(head, new Separator(), chartArea, xLabels);
        return card;
    }

    private VBox createRevenueByServiceCard() {
        VBox card = createCardContainer();

        Label title = new Label("Revenue by Service");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createServiceBarRow("Sound System", "₹1,45,000", 0.85),
                createServiceBarRow("Photography & Videography", "₹92,000", 0.60),
                createServiceBarRow("Catering", "₹85,000", 0.50),
                createServiceBarRow("Decoration", "₹78,000", 0.42)
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private VBox createServiceBarRow(String service, String amount, double progress) {
        VBox b = new VBox(4);
        HBox head = new HBox();
        Label s = new Label(service); s.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label a = new Label(amount); a.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        head.getChildren().addAll(s, sp, a);

        ProgressBar pb = new ProgressBar(progress);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        b.getChildren().addAll(head, pb);
        return b;
    }

    private VBox createTransactionsTableCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Transaction History");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(12);
        tableHeader.setPadding(new Insets(8, 10, 8, 10));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addTableHeader(tableHeader, "DATE", 0, 90);
        addTableHeader(tableHeader, "TRANSACTION", 1, 120);
        addTableHeader(tableHeader, "EVENT", 2, 160);
        addTableHeader(tableHeader, "AMOUNT", 3, 75);
        addTableHeader(tableHeader, "FEE", 4, 60);
        addTableHeader(tableHeader, "NET", 5, 75);
        addTableHeader(tableHeader, "STATUS", 6, 70);
        addTableHeader(tableHeader, "ACTION", 7, 50);

        tableRowsContainer = new VBox(4);
        renderTransactionsTable(stage);

        card.getChildren().addAll(title, tableHeader, tableRowsContainer);
        return card;
    }

    private void addTableHeader(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void renderTransactionsTable(Stage stage) {
        tableRowsContainer.getChildren().clear();

        for (TransactionData t : transactions) {
            GridPane row = new GridPane();
            row.setHgap(12);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            Label d = new Label(t.date); d.setPrefWidth(90); d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label tp = new Label(t.type); tp.setPrefWidth(120); tp.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            Label ev = new Label(t.eventName); ev.setPrefWidth(160); ev.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label am = new Label(t.amount); am.setPrefWidth(75); am.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label fe = new Label(t.fee); fe.setPrefWidth(60); fe.setStyle("-fx-font-size: 11px; -fx-text-fill: " + RED_TEXT + ";");
            Label nt = new Label(t.net); nt.setPrefWidth(75); nt.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + ";");

            Label st = new Label(t.status); st.setPrefWidth(70);
            if (t.status.equals("Paid")) {
                st.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 8px;");
            } else {
                st.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 8px;");
            }

            Button viewBtn = createSecondaryButton("View");
            viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 2 6;");
            viewBtn.setOnAction(e -> openTransactionDetailsModal(stage, t));

            row.add(d, 0, 0);
            row.add(tp, 1, 0);
            row.add(ev, 2, 0);
            row.add(am, 3, 0);
            row.add(fe, 4, 0);
            row.add(nt, 5, 0);
            row.add(st, 6, 0);
            row.add(viewBtn, 7, 0);

            tableRowsContainer.getChildren().add(row);
        }
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createFinancialInsightCard() {
        VBox card = createCardContainer();
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");

        HBox head = new HBox(8);
        Label icon = new Label("💡"); icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("Financial Insight"); title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        head.getChildren().addAll(icon, title);

        Label desc = new Label("Your revenue has grown by 12% compared to last quarter. Focus on collecting the ₹42,000 in pending payments to improve cash flow.");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        card.getChildren().addAll(head, desc);
        return card;
    }

    private VBox createMonthlyBreakdownCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Monthly Breakdown");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox grid = new VBox(8);
        grid.getChildren().addAll(
                createBreakdownRow("Gross Earnings", "₹2,48,500", TEXT_MAIN),
                createBreakdownRow("Platform Fees (3%)", "-₹7,455", RED_TEXT),
                createBreakdownRow("Taxes (TDS)", "Pending", WARNING_TEXT)
        );

        VBox netBox = new VBox(4);
        netBox.setPadding(new Insets(10));
        netBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        Label netLbl = new Label("Net Earnings"); netLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label netVal = new Label("₹2,41,045"); netVal.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        netBox.getChildren().addAll(netLbl, netVal);

        Button withdrawBtn = createPrimaryButton("🏦 Withdraw Funds / Request Payout");
        withdrawBtn.setMaxWidth(Double.MAX_VALUE);
        withdrawBtn.setOnAction(e -> openPayoutModal(stage));

        card.getChildren().addAll(title, new Separator(), grid, netBox, withdrawBtn);
        return card;
    }

    private HBox createBreakdownRow(String label, String val, String color) {
        HBox r = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        r.getChildren().addAll(l, sp, v);
        return r;
    }

    private VBox createPayoutAccountCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Payout Account");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label bank = new Label("🏦 HDFC Bank •••• 4821");
        bank.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label holder = new Label("Account Holder: VisionPro Events (Verified ✓)");
        holder.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button manageBtn = createSecondaryButton("Manage Account");
        manageBtn.setMaxWidth(Double.MAX_VALUE);
        manageBtn.setOnAction(e -> showToast(stage, "Opening Payout Account Settings..."));

        card.getChildren().addAll(title, bank, holder, manageBtn);
        return card;
    }

    // ==========================================
    // 6. PAGINATION
    // ==========================================
    private HBox createPaginationRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 0, 0, 0));

        Label count = new Label("Showing 1 to 4 of 32 transactions");
        count.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button prev = createSecondaryButton("← Previous");
        Button page1 = createPrimaryButton("1");
        Button page2 = createSecondaryButton("2");
        Button page3 = createSecondaryButton("3");
        Button next = createSecondaryButton("Next →");

        HBox pBox = new HBox(4, prev, page1, page2, page3, next);

        row.getChildren().addAll(count, sp, pBox);
        return row;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openPayoutModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Request Payout");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("🏦 Request Payout");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label bal = new Label("Available Balance: ₹86,500");
        bal.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextField amountInput = new TextField("42500");
        amountInput.setPromptText("Enter amount in ₹");

        Label dest = new Label("Destination: HDFC Bank •••• 4821");
        dest.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Button submitBtn = createPrimaryButton("Submit Payout Request");
        submitBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Payout request of ₹" + amountInput.getText() + " submitted successfully!");
            modal.close();
        });

        layout.getChildren().addAll(title, bal, new Label("Withdraw Amount:"), amountInput, dest, new Separator(), submitBtn);

        Scene scene = new Scene(layout, 360, 260);
        modal.setScene(scene);
        modal.show();
    }

    private void openTransactionDetailsModal(Stage parentStage, TransactionData t) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Transaction Breakdown");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Transaction Breakdown");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox details = new VBox(6);
        details.getChildren().addAll(
                new Label("• Date: " + t.date),
                new Label("• Event: " + t.eventName),
                new Label("• Gross Amount: " + t.amount),
                new Label("• Platform Fee: " + t.fee),
                new Label("• Net Earnings: " + t.net),
                new Label("• Status: " + t.status)
        );
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";"));

        Button closeBtn = createSecondaryButton("Close");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, new Separator(), details, new Separator(), closeBtn);

        Scene scene = new Scene(layout, 340, 260);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 7 15; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}