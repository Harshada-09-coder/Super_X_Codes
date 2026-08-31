package com.techtrio.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
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

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.VendorController;
import com.techtrio.model.AiEventPlan;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.services.EventService;
import com.techtrio.services.GeminiAiService;
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
    
    private Label approvedBudgetValLabel;
    private Label allocatedBudgetValLabel;
    private Label spentValLabel;
    private Label remainingValLabel;

    private Label breadcrumbLabel;
    private Label eventBadgeLabel;

    private Label budgetOverviewTotalLabel;
    private ProgressBar budgetOverviewProgressBar;
    private VBox budgetOverviewListContainer;
    private VBox budgetBreakdownItemsContainer;

    private Label eventDateDetailLabel;
    private Label eventGuestsDetailLabel;
    private Label eventVenueDetailLabel;

    private VBox expensesTableContainer;

    private String uid;
    private String idToken;
    private Runnable backAction;

    private Event currentEvent;
    private AiEventPlan currentAiPlan;
    private final GeminiAiService aiService = new GeminiAiService();

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

        loadDynamicEventBudgetDataAsync();

        return new Scene(root, 1550, 785);
    }

    // =========================================================================
    // DYNAMIC EVENT & ACCEPTED VENDOR EXPENSES LOADER
    // =========================================================================
    private void loadDynamicEventBudgetDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController adminController = new AdminController();
                String selectedEvtId = AdminContext.getSelectedEventId();

                // 1. Resolve Active Event
                if (selectedEvtId != null && !selectedEvtId.isBlank()) {
                    currentEvent = adminController.getEventById(selectedEvtId);
                }

                if (currentEvent == null) {
                    EventService eventService = new EventService();
                    List<Event> assigned = eventService.getEventsByOrganizerId(uid);
                    if (assigned != null && !assigned.isEmpty()) {
                        currentEvent = assigned.get(0);
                        selectedEvtId = currentEvent.getEventId();
                        AdminContext.setSelectedEventId(selectedEvtId);
                    }
                }

                // 2. Parse Event AI Plan from Firestore JSON
                if (currentEvent != null && currentEvent.getAiPlanJson() != null && !currentEvent.getAiPlanJson().isBlank()) {
                    currentAiPlan = AiEventPlan.fromJson(currentEvent.getAiPlanJson());
                }

                if (currentAiPlan == null && currentEvent != null) {
                    String eventTypeQuery = (currentEvent.getEventType() != null) ? currentEvent.getEventType() : "Corporate Event";
                    try {
                        currentAiPlan = aiService.generatePlanFromEventData(eventTypeQuery);
                    } catch (Exception e) {
                        currentAiPlan = aiService.generateIntelligentFallback(eventTypeQuery);
                    }
                }

                // 3. Fetch Real Vendor Bookings from Firestore via VendorController ("vendorBookings")
                if (selectedEvtId != null && !selectedEvtId.isBlank()) {
                    fetchAcceptedVendorExpenses(selectedEvtId);
                }

                Platform.runLater(() -> renderDynamicBudgetData());
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void fetchAcceptedVendorExpenses(String eventId) {
        if (eventId == null || eventId.isBlank()) return;

        try {
            VendorController vendorController = new VendorController();
            List<VendorBooking> bookings = vendorController.getEventVendorBookings(eventId.trim());
            expenseList.clear();

            if (bookings != null && !bookings.isEmpty()) {
                for (VendorBooking vb : bookings) {
                    double amt = 0.0;
                    try {
                        String clean = vb.getAmount() != null ? vb.getAmount().replaceAll("[^0-9.]", "") : "0";
                        if (!clean.isEmpty()) amt = Double.parseDouble(clean);
                    } catch (Exception ignored) {}

                    String vName = vb.getVendorName() != null && !vb.getVendorName().isBlank() ? vb.getVendorName() : "Vendor Partner";
                    String cat = vb.getServiceCategory() != null ? vb.getServiceCategory() : "Service";
                    String dt = vb.getEventDate() != null ? vb.getEventDate() : "Upcoming";
                    
                    String rawStatus = vb.getStatus() != null ? vb.getStatus().toUpperCase() : "PENDING";
                    String st = ("CONFIRMED".equals(rawStatus) || "ACCEPTED".equals(rawStatus)) ? "Paid" : "Pending";

                    expenseList.add(new ExpenseItem(cat, vName, dt, amt, st));
                }
                System.out.println("✅ Loaded " + expenseList.size() + " vendor bookings from Firestore collection 'vendorBookings' for event: " + eventId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderDynamicBudgetData() {
        String eventName = (currentEvent != null && currentEvent.getEventName() != null) ? currentEvent.getEventName() : "Annual Tech Summit";
        String eventDate = (currentEvent != null && currentEvent.getEventDate() != null) ? currentEvent.getEventDate() : "18 May 2026";
        int guests = (currentEvent != null && currentEvent.getGuests() > 0) ? currentEvent.getGuests() : 250;
        String venue = (currentEvent != null && currentEvent.getVenueType() != null) ? currentEvent.getVenueType() : "Conference Center";
        String budgetStr = (currentEvent != null && currentEvent.getBudget() != null) ? currentEvent.getBudget() : "₹ 20,000";

        // Parse numerical budget
        try {
            String clean = budgetStr.replaceAll("[^0-9]", "");
            if (!clean.isEmpty()) {
                approvedBudget = Double.parseDouble(clean);
                allocatedBudget = approvedBudget * 0.85;
            }
        } catch (Exception ignored) {}

        if (breadcrumbLabel != null) breadcrumbLabel.setText("My Events  >  " + eventName + "  >  Budget");
        if (eventBadgeLabel != null) eventBadgeLabel.setText("● " + eventName + " (In Planning)");

        if (approvedBudgetValLabel != null) approvedBudgetValLabel.setText(budgetStr);
        if (allocatedBudgetValLabel != null) allocatedBudgetValLabel.setText("₹ " + String.format("%,.0f", allocatedBudget));

        if (eventDateDetailLabel != null) eventDateDetailLabel.setText(eventDate);
        if (eventGuestsDetailLabel != null) eventGuestsDetailLabel.setText(guests + " Expected");
        if (eventVenueDetailLabel != null) eventVenueDetailLabel.setText(venue);

        // 1. RENDER BUDGET OVERVIEW CARD (DYNAMICALLY PROPORTIONED TO ACTUAL APPROVED BUDGET)
        if (budgetOverviewTotalLabel != null) budgetOverviewTotalLabel.setText(budgetStr);
        if (budgetOverviewProgressBar != null) budgetOverviewProgressBar.setProgress(0.70);

        if (budgetOverviewListContainer != null) {
            budgetOverviewListContainer.getChildren().clear();
            if (currentAiPlan != null && currentAiPlan.getBudgetBreakdown() != null && !currentAiPlan.getBudgetBreakdown().isEmpty()) {
                for (AiEventPlan.BudgetItem bi : currentAiPlan.getBudgetBreakdown()) {
                    double catAmount = (bi.getPercentage() / 100.0) * approvedBudget;
                    String formattedAmt = "₹ " + String.format("%,.0f", catAmount);

                    HBox row = new HBox(8);
                    row.setAlignment(Pos.CENTER_LEFT);
                    Label dot = new Label("■");
                    dot.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px;");
                    Label cat = new Label(bi.getCategory());
                    cat.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);
                    Label val = new Label(formattedAmt + " (" + (int)bi.getPercentage() + "%)");
                    val.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
                    row.getChildren().addAll(dot, cat, sp, val);
                    budgetOverviewListContainer.getChildren().add(row);
                }
            } else {
                renderDefaultProportionedOverview(approvedBudget);
            }
        }

        // 2. RENDER BUDGET BREAKDOWN CARD (WITH DYNAMIC PERCENTAGE PROGRESS BARS)
        if (budgetBreakdownItemsContainer != null) {
            budgetBreakdownItemsContainer.getChildren().clear();
            if (currentAiPlan != null && currentAiPlan.getBudgetBreakdown() != null && !currentAiPlan.getBudgetBreakdown().isEmpty()) {
                for (AiEventPlan.BudgetItem bi : currentAiPlan.getBudgetBreakdown()) {
                    double catAmount = (bi.getPercentage() / 100.0) * approvedBudget;
                    String formattedAmt = "₹ " + String.format("%,.0f", catAmount);

                    HBox row = new HBox(12);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(7, 10, 7, 10));
                    row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

                    Label cat = new Label(bi.getCategory());
                    cat.setMinWidth(130);
                    cat.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

                    ProgressBar pb = new ProgressBar(bi.getPercentage() / 100.0);
                    pb.setMaxWidth(Double.MAX_VALUE);
                    pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");
                    HBox.setHgrow(pb, Priority.ALWAYS);

                    Label amt = new Label(formattedAmt);
                    amt.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

                    row.getChildren().addAll(cat, pb, amt);
                    budgetBreakdownItemsContainer.getChildren().add(row);
                }
            } else {
                renderDefaultProportionedBreakdown(approvedBudget);
            }
        }

        renderExpenseRows();
        recalculateBudget();
    }

    private void renderDefaultProportionedOverview(double total) {
        String[][] defaults = {{"Venue", "30"}, {"Catering", "35"}, {"Decoration", "15"}, {"Sound & Photography", "10"}, {"Others & Contingency", "10"}};
        for (String[] d : defaults) {
            double pct = Double.parseDouble(d[1]);
            double catAmount = (pct / 100.0) * total;
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Label dot = new Label("■"); dot.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px;");
            Label cat = new Label(d[0]); cat.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label val = new Label("₹ " + String.format("%,.0f", catAmount) + " (" + (int)pct + "%)");
            val.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            row.getChildren().addAll(dot, cat, sp, val);
            budgetOverviewListContainer.getChildren().add(row);
        }
    }

    private void renderDefaultProportionedBreakdown(double total) {
        String[][] defaults = {{"Venue", "30"}, {"Catering", "35"}, {"Decoration", "15"}, {"Sound & Photography", "10"}, {"Others & Contingency", "10"}};
        for (String[] d : defaults) {
            double pct = Double.parseDouble(d[1]);
            double catAmount = (pct / 100.0) * total;
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(7, 10, 7, 10));
            row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

            Label cat = new Label(d[0]); cat.setMinWidth(130); cat.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            ProgressBar pb = new ProgressBar(pct / 100.0); pb.setMaxWidth(Double.MAX_VALUE); pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;"); HBox.setHgrow(pb, Priority.ALWAYS);
            Label amt = new Label("₹ " + String.format("%,.0f", catAmount)); amt.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

            row.getChildren().addAll(cat, pb, amt);
            budgetBreakdownItemsContainer.getChildren().add(row);
        }
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
        breadcrumbLabel = new Label("My Events  >  Annual Tech Summit  >  Budget");
        breadcrumbLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Budget & Expense Tracker");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        eventBadgeLabel = new Label("● Annual Tech Summit (In Planning)");
        eventBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16px; -fx-font-size: 12px;");

        Button addExpenseBtn = new Button("+ Add Expense");
        addExpenseBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        addExpenseBtn.setOnAction(e -> handleAddExpenseDialog());

        headerRow.getChildren().addAll(title, headerSpacer, eventBadgeLabel, addExpenseBtn);

        Label subtitle = new Label("Monitor event spending, optimize allocations and stay within the approved budget.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(breadcrumbLabel, headerRow, subtitle);

        // Top Summary KPI Cards (4 Cards)
        approvedBudgetValLabel = new Label("₹ 20,000");
        allocatedBudgetValLabel = new Label("₹ 17,000");

        HBox summaryRow = new HBox(16);
        summaryRow.getChildren().addAll(
            createCustomSummaryCard("Approved Budget 🔒", approvedBudgetValLabel, TEXT_MAIN),
            createCustomSummaryCard("Allocated", allocatedBudgetValLabel, TEXT_MAIN),
            createSummaryCardSpent(),
            createSummaryCardRemaining()
        );

        // Middle Grid Section (Equal 50-50 Split)
        HBox middleGrid = new HBox(20);

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        HBox middleCards = new HBox(16);
        middleCards.getChildren().addAll(
            createBudgetOverviewCard(),
            createBudgetBreakdownCard()
        );
        HBox.setHgrow(middleCards.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(middleCards.getChildren().get(1), Priority.ALWAYS);

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

    private VBox createCustomSummaryCard(String title, Label valLbl, String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(t, valLbl);
        return card;
    }

    private VBox createSummaryCardSpent() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Spent"); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        spentValLabel = new Label("₹ 0"); spentValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");

        card.getChildren().addAll(t, spentValLabel);
        return card;
    }

    private VBox createSummaryCardRemaining() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label("Remaining"); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        remainingValLabel = new Label("₹ 20,000"); remainingValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(t, remainingValLabel);
        return card;
    }

    private VBox createBudgetOverviewCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("💰 Budget Overview");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusTag = new Label("✓ AI Validated");
        statusTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        header.getChildren().addAll(title, spacer, statusTag);

        budgetOverviewTotalLabel = new Label("₹ 20,000");
        budgetOverviewTotalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        budgetOverviewProgressBar = new ProgressBar(0.70);
        budgetOverviewProgressBar.setMaxWidth(Double.MAX_VALUE);
        budgetOverviewProgressBar.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        budgetOverviewListContainer = new VBox(7);

        card.getChildren().addAll(header, budgetOverviewTotalLabel, budgetOverviewProgressBar, budgetOverviewListContainer);
        return card;
    }

    private VBox createBudgetBreakdownCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📊 AI Budget Breakdown");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label("Allocated");
        badge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        header.getChildren().addAll(title, spacer, badge);

        budgetBreakdownItemsContainer = new VBox(8);

        card.getChildren().addAll(header, budgetBreakdownItemsContainer);
        return card;
    }

    private VBox createEventDetailsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Event Details");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        eventDateDetailLabel = new Label("18 May 2026");
        eventGuestsDetailLabel = new Label("250 Expected");
        eventVenueDetailLabel = new Label("Conference Center");

        VBox details = new VBox(6);
        details.getChildren().addAll(
            createCustomDetailItem("📅 Date", eventDateDetailLabel),
            createCustomDetailItem("👥 Guests", eventGuestsDetailLabel),
            createCustomDetailItem("📍 Venue", eventVenueDetailLabel)
        );

        card.getChildren().addAll(title, details);
        return card;
    }

    private VBox createCustomDetailItem(String label, Label valLbl) {
        VBox item = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        item.getChildren().addAll(l, valLbl);
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

        Label title = new Label("Recent Expenses (Accepted Vendors)");
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
        if (expensesTableContainer == null) return;
        expensesTableContainer.getChildren().clear();

        if (expenseList.isEmpty()) {
            VBox empty = new VBox(8);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(24));
            Label l = new Label("No accepted vendor bookings or expenses recorded yet for this event.");
            l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            empty.getChildren().add(l);
            expensesTableContainer.getChildren().add(empty);
            return;
        }

        for (ExpenseItem item : expenseList) {
            GridPane row = new GridPane();
            row.setPadding(new Insets(12));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            row.add(new Label(item.category), 0, 0);
            row.add(new Label(item.vendor), 1, 0);
            row.add(new Label(item.date), 2, 0);
            
            Label amtLbl = new Label("₹ " + String.format("%,.0f", item.amount));
            amtLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            row.add(amtLbl, 3, 0);

            Label statusBadge = new Label(item.status);
            if (item.status.equalsIgnoreCase("Paid") || item.status.equalsIgnoreCase("Confirmed") || item.status.equalsIgnoreCase("Accepted")) {
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
            if ("Paid".equalsIgnoreCase(item.status) || "Confirmed".equalsIgnoreCase(item.status) || "Accepted".equalsIgnoreCase(item.status)) {
                totalSpent += item.amount;
            }
        }
        double remaining = approvedBudget - totalSpent;
        if (remaining < 0) remaining = 0;

        if (spentValLabel != null) spentValLabel.setText("₹ " + String.format("%,.0f", totalSpent));
        if (remainingValLabel != null) remainingValLabel.setText("₹ " + String.format("%,.0f", remaining));
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
            ViewEventO view = new ViewEventO();
            stage.setScene(view.getScene(stage, () -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO();
        stage.setScene(dash.getorganizerDashboardScene(null));
    }
}