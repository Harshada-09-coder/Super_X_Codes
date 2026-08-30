package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.techtrio.session.UserSession;

public class BudgetExpenseTrackerO {

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
    private static final String PENDING_BG = "#FEF7E0";
    private static final String PENDING_TEXT = "#B06000";

    private double approvedBudget = 500000.0;
    private double allocatedBudget = 420000.0;
    
    private Label spentValLabel;
    private Label remainingValLabel;
    private Label utilizationPercentLabel;
    private ProgressBar utilizationBar;
    private VBox expensesTableContainer;

    private String uid;
    private String idToken;
    private Runnable backAction;

    private static class ExpenseItem {
        String category;
        String vendor;
        String date;
        double amount;
        String status;

        ExpenseItem(String category, String vendor, String date, double amount, String status) {
            this.category = category;
            this.vendor = vendor;
            this.date = date;
            this.amount = amount;
            this.status = status;
        }
    }

    private final List<ExpenseItem> expenseList = new ArrayList<>();

    public BudgetExpenseTrackerO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public BudgetExpenseTrackerO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        if (expenseList.isEmpty()) {
            expenseList.add(new ExpenseItem("AV Equipment", "SoundMasters Inc.", "10 May 2026", 15000, "Paid"));
            expenseList.add(new ExpenseItem("Catering Advance", "Gourmet Bites", "12 May 2026", 20000, "Paid"));
            expenseList.add(new ExpenseItem("Photography", "Lens & Light", "14 May 2026", 7000, "Paid"));
            expenseList.add(new ExpenseItem("Decoration", "Flora Designs", "Pending", 25000, "Pending"));
        }
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
        recalculateBudget();

        return new Scene(root, 1550, 785);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍");
        TextField topSearch = new TextField();
        topSearch.setPromptText("Search events, vendors, attendees...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        topBar.getChildren().addAll(searchBox, spacer, avatar);
        return topBar;
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, () -> goDashboard(stage)));
        menu.getChildren().add(createNavBtn("Event Requests", false, () -> {
            EventRequestsO view = new EventRequestsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(() -> goDashboard(stage)));
        }));
        menu.getChildren().add(createNavBtn("My Events", false, () -> {
            MyEventsO view = new MyEventsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getMyEventsOScene(() -> goDashboard(stage)));
        }));
        menu.getChildren().add(createNavBtn("Budget", true, () -> {}));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer);
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

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumbs & Title
        VBox titleBox = new VBox(4);
        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Budget");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Budget & Expense Tracker");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label badge = new Label("● TechVision 2026 (In Planning)");
        badge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16px; -fx-font-size: 12px;");

        Button addExpenseBtn = new Button("+ Add Expense");
        addExpenseBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        addExpenseBtn.setOnAction(e -> handleAddExpenseDialog());

        headerRow.getChildren().addAll(title, headerSpacer, badge, addExpenseBtn);

        Label subtitle = new Label("Monitor event spending, optimize allocations and stay within the approved budget.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(breadcrumb, headerRow, subtitle);

        // Top Summary KPI Cards (4 Cards)
        HBox summaryRow = new HBox(16);
        summaryRow.getChildren().addAll(
            createSummaryCard("Approved Budget 🔒", "₹5,00,000", TEXT_MAIN),
            createSummaryCard("Allocated", "₹4,20,000", TEXT_MAIN),
            createSummaryCardSpent(),
            createSummaryCardRemaining()
        );

        // Middle Grid Section
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

        // Bottom Expenses Table
        VBox recentExpensesTable = createRecentExpensesSection();

        mainBox.getChildren().addAll(titleBox, summaryRow, middleGrid, recentExpensesTable);
        return mainBox;
    }

    private VBox createSummaryCard(String title, String val, String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(t, v);
        return card;
    }

    private VBox createSummaryCardSpent() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Spent"); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        spentValLabel = new Label("₹42,000"); spentValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");

        card.getChildren().addAll(t, spentValLabel);
        return card;
    }

    private VBox createSummaryCardRemaining() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Remaining"); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        remainingValLabel = new Label("₹4,58,000"); remainingValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(t, remainingValLabel);
        return card;
    }

    private VBox createBudgetUtilizationCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Budget Utilization");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusTag = new Label("✓ On Track");
        statusTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        header.getChildren().addAll(title, spacer, statusTag);

        utilizationPercentLabel = new Label("8.4% spent");
        utilizationPercentLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        utilizationBar = new ProgressBar(0.084);
        utilizationBar.setMaxWidth(Double.MAX_VALUE);
        utilizationBar.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        HBox rangeRow = new HBox();
        Label minL = new Label("₹0"); minL.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region rSpacer = new Region(); HBox.setHgrow(rSpacer, Priority.ALWAYS);
        Label maxL = new Label("₹5,00,000"); maxL.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        rangeRow.getChildren().addAll(minL, rSpacer, maxL);

        card.getChildren().addAll(header, utilizationPercentLabel, utilizationBar, rangeRow);
        return card;
    }

    private VBox createAIOptimizerCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("✨ AI Optimizer");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewAll = new Button("View All");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-cursor: hand;");

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

        VBox textGroup = new VBox(2);
        Label cat = new Label(category); cat.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label desc = new Label(text); desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        textGroup.getChildren().addAll(cat, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button(btnText);
        actionBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-border-radius: 4px; -fx-cursor: hand;");

        row.getChildren().addAll(textGroup, spacer, actionBtn);
        return row;
    }

    private VBox createEventDetailsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Event Details");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox details = new VBox(6);
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
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        item.getChildren().addAll(l, v);
        return item;
    }

    private VBox createBudgetAlertsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("🔔 Budget Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox alerts = new VBox(8);
        alerts.getChildren().addAll(
            createAlertItem("● Overall budget is currently on track.", GREEN_TEXT),
            createAlertItem("● Catering is nearing 80% of allocated limit.", PENDING_TEXT),
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

    private VBox createRecentExpensesSection() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recent Expenses");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button fullLedgerBtn = new Button("View Full Ledger →");
        fullLedgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px; -fx-cursor: hand;");

        header.getChildren().addAll(title, spacer, fullLedgerBtn);

        GridPane tableHeader = new GridPane();
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

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

        for (ExpenseItem item : expenseList) {
            GridPane row = new GridPane();
            row.setPadding(new Insets(12));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            row.add(new Label(item.category), 0, 0);
            row.add(new Label(item.vendor), 1, 0);
            row.add(new Label(item.date), 2, 0);
            
            Label amtLbl = new Label("₹" + String.format("%,.0f", item.amount));
            amtLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            row.add(amtLbl, 3, 0);

            Label statusBadge = new Label(item.status);
            if (item.status.equalsIgnoreCase("Paid")) {
                statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            } else {
                statusBadge.setStyle("-fx-background-color: " + PENDING_BG + "; -fx-text-fill: " + PENDING_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            }
            row.add(statusBadge, 4, 0);

            Label actionBtn = new Label("⋮");
            actionBtn.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");
            row.add(actionBtn, 5, 0);

            setGridConstraints(row);
            expensesTableContainer.getChildren().add(row);
        }
    }

    private void setGridConstraints(GridPane grid) {
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(25);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(18);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(14);
        ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(10);
        ColumnConstraints c6 = new ColumnConstraints(); c6.setPercentWidth(8);
        grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5, c6);
    }

    private void recalculateBudget() {
        double totalSpent = 0;
        for (ExpenseItem item : expenseList) {
            totalSpent += item.amount;
        }
        double remaining = approvedBudget - totalSpent;

        if (spentValLabel != null) spentValLabel.setText("₹" + String.format("%,.0f", totalSpent));
        if (remainingValLabel != null) remainingValLabel.setText("₹" + String.format("%,.0f", remaining));
        if (utilizationPercentLabel != null) utilizationPercentLabel.setText(String.format("%.1f%% spent", (totalSpent / approvedBudget) * 100));
        if (utilizationBar != null) utilizationBar.setProgress(totalSpent / approvedBudget);
    }

    private void handleAddExpenseDialog() {
        Dialog<ExpenseItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Expense");
        dialog.setHeaderText("Enter details for new expense");

        ButtonType addBtnType = new ButtonType("Add Expense", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField catInput = new TextField(); catInput.setPromptText("Category");
        TextField vendorInput = new TextField(); vendorInput.setPromptText("Vendor");
        TextField amtInput = new TextField(); amtInput.setPromptText("Amount");

        grid.add(new Label("Category:"), 0, 0); grid.add(catInput, 1, 0);
        grid.add(new Label("Vendor:"), 0, 1); grid.add(vendorInput, 1, 1);
        grid.add(new Label("Amount:"), 0, 2); grid.add(amtInput, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                try {
                    double amt = Double.parseDouble(amtInput.getText().trim());
                    return new ExpenseItem(catInput.getText().trim(), vendorInput.getText().trim(), "18 May 2026", amt, "Paid");
                } catch (Exception e) {}
            }
            return null;
        });

        Optional<ExpenseItem> result = dialog.showAndWait();
        result.ifPresent(expense -> {
            expenseList.add(expense);
            renderExpenseRows();
            recalculateBudget();
        });
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