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
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.CustomerController;
import com.techtrio.controller.OrganizerController;
import com.techtrio.controller.VendorController;
import com.techtrio.model.AiEventPlan;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.model.budget.BudgetAllocation;
import com.techtrio.model.budget.BudgetOptimizationRequest;
import com.techtrio.model.budget.BudgetOptimizationResult;
import com.techtrio.services.BudgetOptimizerService;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;

public class BudgetExpenseTrackerO {

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String PENDING_BG = "#FEF7E0";
    private static final String PENDING_TEXT = "#B06000";

    private double approvedBudget = 500000.0;
    private double allocatedBudget = 450000.0;
    
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
    private final BudgetOptimizerService optimizerService = new BudgetOptimizerService();
    private List<BudgetAllocation> currentAllocations = new ArrayList<>();

    private static final Map<String, List<BudgetAllocation>> savedEventAllocationsCache = new HashMap<>();

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

        // 1. Sidebar (Matched with AI Planner and Dashboard)
        root.setLeft(createCommonOrganizerSidebar("My Events"));

        // 2. Main Content inside ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadDynamicEventBudgetDataAsync();

        return new Scene(root, 1550, 785);
    }

    private void loadDynamicEventBudgetDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController adminController = new AdminController();
                String selectedEvtId = AdminContext.getSelectedEventId();

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

                if (selectedEvtId != null && !selectedEvtId.isBlank()) {
                    fetchAcceptedVendorExpenses(selectedEvtId);
                }

                Platform.runLater(() -> {
                    loadAllocationsOrOptimize();
                    renderDynamicBudgetData();
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void loadAllocationsOrOptimize() {
        String budgetStr = (currentEvent != null && currentEvent.getBudget() != null) ? currentEvent.getBudget() : "₹ 5,00,000";
        try {
            String clean = budgetStr.replaceAll("[^0-9.]", "");
            if (!clean.isEmpty()) approvedBudget = Double.parseDouble(clean);
        } catch (Exception ignored) {}

        String eventId = (currentEvent != null && currentEvent.getEventId() != null) ? currentEvent.getEventId() : "DEFAULT";

        if (savedEventAllocationsCache.containsKey(eventId) && !savedEventAllocationsCache.get(eventId).isEmpty()) {
            currentAllocations = new ArrayList<>(savedEventAllocationsCache.get(eventId));
        } else if (currentEvent != null && currentEvent.getBudgetRecommendation() != null && !currentEvent.getBudgetRecommendation().isEmpty()) {
            // Load live recommendation from Firestore
            currentAllocations.clear();
            for (Map.Entry<String, Object> entry : currentEvent.getBudgetRecommendation().entrySet()) {
                double amt = 0.0;
                try { amt = Double.parseDouble(entry.getValue().toString()); } catch (Exception ignored) {}
                double pct = approvedBudget > 0 ? (amt / approvedBudget) * 100.0 : 10.0;
                currentAllocations.add(new BudgetAllocation(entry.getKey(), amt, Math.round(pct * 10.0) / 10.0, "fas-coins"));
            }
        } else {
            forceFreshOptimization();
        }
    }

    private void forceFreshOptimization() {
        String budgetStr = (currentEvent != null && currentEvent.getBudget() != null) ? currentEvent.getBudget() : "₹ 5,00,000";
        try {
            String clean = budgetStr.replaceAll("[^0-9.]", "");
            if (!clean.isEmpty()) approvedBudget = Double.parseDouble(clean);
        } catch (Exception ignored) {}

        List<String> selectedReqs = new ArrayList<>();
        if (currentEvent != null && currentEvent.getRequirements() != null && !currentEvent.getRequirements().isBlank()) {
            selectedReqs.addAll(Arrays.asList(currentEvent.getRequirements().split(",\\s*")));
        }

        int guests = (currentEvent != null && currentEvent.getGuests() > 0) ? currentEvent.getGuests() : 250;

        BudgetOptimizationRequest req = new BudgetOptimizationRequest(
                approvedBudget,
                currentEvent != null ? currentEvent.getEventType() : "Corporate",
                guests,
                selectedReqs,
                currentEvent != null ? currentEvent.getCity() : "Pune"
        );

        BudgetOptimizationResult optResult = optimizerService.optimizeBudget(req);
        if (optResult != null && optResult.getAllocations() != null) {
            currentAllocations = new ArrayList<>(optResult.getAllocations());
            if (currentEvent != null && currentEvent.getEventId() != null) {
                savedEventAllocationsCache.put(currentEvent.getEventId(), new ArrayList<>(currentAllocations));
            }
        }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderDynamicBudgetData() {
        String eventName = (currentEvent != null && currentEvent.getEventName() != null) ? currentEvent.getEventName() : "Annual Event";
        String eventDate = (currentEvent != null && currentEvent.getEventDate() != null) ? currentEvent.getEventDate() : "18 May 2026";
        int guests = (currentEvent != null && currentEvent.getGuests() > 0) ? currentEvent.getGuests() : 250;
        String venue = (currentEvent != null && currentEvent.getVenueType() != null) ? currentEvent.getVenueType() : "Conference Center";
        String budgetStr = "₹ " + String.format("%,.0f", approvedBudget);

        allocatedBudget = 0.0;
        for (BudgetAllocation a : currentAllocations) {
            allocatedBudget += a.getRecommendedAmount();
        }

        if (breadcrumbLabel != null) breadcrumbLabel.setText("My Events  /  " + eventName + "  /  Budget & Expenses");
        if (eventBadgeLabel != null) eventBadgeLabel.setText("● Live Synced Workspace");

        if (approvedBudgetValLabel != null) approvedBudgetValLabel.setText(budgetStr);
        if (allocatedBudgetValLabel != null) allocatedBudgetValLabel.setText("₹ " + String.format("%,.0f", allocatedBudget));

        if (eventDateDetailLabel != null) eventDateDetailLabel.setText(eventDate);
        if (eventGuestsDetailLabel != null) eventGuestsDetailLabel.setText(guests + " Expected");
        if (eventVenueDetailLabel != null) eventVenueDetailLabel.setText(venue);

        // 1. Overview Card
        if (budgetOverviewTotalLabel != null) budgetOverviewTotalLabel.setText(budgetStr);
        if (budgetOverviewProgressBar != null) budgetOverviewProgressBar.setProgress(Math.min(1.0, allocatedBudget / (approvedBudget > 0 ? approvedBudget : 1)));

        if (budgetOverviewListContainer != null) {
            budgetOverviewListContainer.getChildren().clear();
            for (BudgetAllocation a : currentAllocations) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                Label dot = new Label("■");
                dot.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px;");
                Label cat = new Label(a.getCategory());
                cat.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                Label val = new Label("₹ " + String.format("%,.0f", a.getRecommendedAmount()) + " (" + a.getPercentage() + "%)");
                val.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
                row.getChildren().addAll(dot, cat, sp, val);
                budgetOverviewListContainer.getChildren().add(row);
            }
        }

        // 2. Interactive Editable Breakdown Card
        if (budgetBreakdownItemsContainer != null) {
            budgetBreakdownItemsContainer.getChildren().clear();
            for (int i = 0; i < currentAllocations.size(); i++) {
                final int idx = i;
                BudgetAllocation a = currentAllocations.get(idx);

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(7, 10, 7, 10));
                row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

                Label cat = new Label(a.getCategory());
                cat.setMinWidth(130);
                cat.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

                ProgressBar pb = new ProgressBar(a.getPercentage() / 100.0);
                pb.setMaxWidth(Double.MAX_VALUE);
                pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");
                HBox.setHgrow(pb, Priority.ALWAYS);

                Label amt = new Label("₹ " + String.format("%,.0f", a.getRecommendedAmount()));
                amt.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

                Button editBtn = new Button();
                FontIcon editIcon = new FontIcon("fas-pencil-alt");
                editIcon.setIconSize(11);
                editIcon.setIconColor(Color.web(PRIMARY_BLUE));
                editBtn.setGraphic(editIcon);
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 3 6;");
                editBtn.setTooltip(new Tooltip("Edit allocated amount"));
                editBtn.setOnAction(e -> handleEditAllocationDialog(idx));

                row.getChildren().addAll(cat, pb, amt, editBtn);
                budgetBreakdownItemsContainer.getChildren().add(row);
            }
        }

        renderExpenseRows();
        recalculateBudget();
    }

    private void handleEditAllocationDialog(int index) {
        if (index < 0 || index >= currentAllocations.size()) return;
        BudgetAllocation target = currentAllocations.get(index);

        TextInputDialog dialog = new TextInputDialog(String.format("%.0f", target.getRecommendedAmount()));
        dialog.setTitle("Edit Allocation");
        dialog.setHeaderText("Adjust allocation for: " + target.getCategory());
        dialog.setContentText("Enter new amount (₹):");
        if (LandingPage.landingPageStage != null) dialog.initOwner(LandingPage.landingPageStage);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newVal -> {
            try {
                double parsed = Double.parseDouble(newVal.replaceAll("[^0-9.]", "").trim());
                double newPct = approvedBudget > 0 ? (parsed / approvedBudget) * 100.0 : target.getPercentage();
                
                currentAllocations.set(index, new BudgetAllocation(
                        target.getCategory(),
                        parsed,
                        Math.round(newPct * 10.0) / 10.0,
                        target.getIcon()
                ));

                if (currentEvent != null && currentEvent.getEventId() != null) {
                    savedEventAllocationsCache.put(currentEvent.getEventId(), new ArrayList<>(currentAllocations));
                }

                renderDynamicBudgetData();
            } catch (Exception ignored) {}
        });
    }

    private void handleSaveAndPublishBudget(Button saveBtn) {
        if (currentEvent == null || currentEvent.getEventId() == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "No active event loaded to save budget.");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
            return;
        }

        saveBtn.setDisable(true);
        saveBtn.setText("Saving...");

        Map<String, Object> budgetMap = new LinkedHashMap<>();
        List<AiEventPlan.BudgetItem> budgetItems = new ArrayList<>();

        for (BudgetAllocation a : currentAllocations) {
            budgetMap.put(a.getCategory(), a.getRecommendedAmount());
            budgetItems.add(new AiEventPlan.BudgetItem(a.getCategory(), String.format("₹ %,.0f", a.getRecommendedAmount()), a.getPercentage()));
        }

        // Keep Timefold AI Plan budget in sync
        AiEventPlan aiPlan = (currentEvent.getAiPlanJson() != null && !currentEvent.getAiPlanJson().isBlank())
                ? AiEventPlan.fromJson(currentEvent.getAiPlanJson()) : new AiEventPlan();
        aiPlan.setBudgetBreakdown(budgetItems);

        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() {
                OrganizerController controller = new OrganizerController();
                boolean success = controller.updateEventBudgetBreakdown(currentEvent.getEventId(), allocatedBudget, budgetMap);
                
                CustomerController custController = new CustomerController();
                custController.updateEventBudgetAndPlan(currentEvent.getEventId(), currentEvent.getBudget(), aiPlan.toJson());

                if (success) {
                    savedEventAllocationsCache.put(currentEvent.getEventId(), new ArrayList<>(currentAllocations));
                }
                return success;
            }
        };

        saveTask.setOnSucceeded(e -> {
            saveBtn.setDisable(false);
            saveBtn.setText(" Save & Publish Budget");
            if (saveTask.getValue()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Budget breakdown has been saved to Firestore and live-synced with Customer Dashboard & Budget View!");
                alert.setHeaderText("Budget Saved & Published");
                if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save budget in database. Please check Firestore connection.");
                if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
                alert.showAndWait();
            }
        });

        saveTask.setOnFailed(e -> {
            saveBtn.setDisable(false);
            saveBtn.setText(" Save & Publish Budget");
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while saving budget.");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        });

        Thread t = new Thread(saveTask);
        t.setDaemon(true);
        t.start();
    }

    private VBox createCommonOrganizerSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard":
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
                break;
            case "Event Requests":
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
                break;
            case "My Events":
                handleBack(LandingPage.landingPageStage);
                break;
            case "Schedule":
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
                break;
            case "Notifications":
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
                break;
            case "Profile":
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
                break;
            case "Settings":
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
                break;
            case "Support":
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
                break;
        }
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(18);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        VBox titleBox = new VBox(4);
        breadcrumbLabel = new Label("My Events  /  Budget & Expenses");
        breadcrumbLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Budget & Expense Tracker");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        eventBadgeLabel = new Label("● Live Synced Workspace");
        eventBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16px; -fx-font-size: 12px;");

        Button reOptimizeBtn = new Button(" Re-Optimize");
        FontIcon magicIcon = new FontIcon("fas-magic");
        magicIcon.setIconSize(11);
        magicIcon.setIconColor(Color.web(PRIMARY_BLUE));
        reOptimizeBtn.setGraphic(magicIcon);
        reOptimizeBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 7 12; -fx-background-radius: 6px; -fx-border-radius: 6px; -fx-cursor: hand;");
        reOptimizeBtn.setOnAction(e -> {
            forceFreshOptimization();
            renderDynamicBudgetData();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Budget breakdown re-calculated using constraint optimization engine!");
            a.setHeaderText("Re-Optimization Complete");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
        });

        Button savePublishBtn = new Button(" Save & Publish Budget");
        FontIcon saveIcon = new FontIcon("fas-save");
        saveIcon.setIconSize(12);
        saveIcon.setIconColor(Color.WHITE);
        savePublishBtn.setGraphic(saveIcon);
        savePublishBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        savePublishBtn.setOnAction(e -> handleSaveAndPublishBudget(savePublishBtn));

        headerRow.getChildren().addAll(title, headerSpacer, eventBadgeLabel, reOptimizeBtn, savePublishBtn);

        Label subtitle = new Label("Modify allocations based on requirements and save to publish live to the Customer's Dashboard.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(breadcrumbLabel, headerRow, subtitle);

        approvedBudgetValLabel = new Label("₹ 5,00,000");
        allocatedBudgetValLabel = new Label("₹ 5,00,000");

        HBox summaryRow = new HBox(16);
        summaryRow.getChildren().addAll(
            createCustomSummaryCard("Approved Budget", approvedBudgetValLabel, TEXT_MAIN, "fas-lock"),
            createCustomSummaryCard("Allocated", allocatedBudgetValLabel, TEXT_MAIN, "fas-coins"),
            createSummaryCardSpent(),
            createSummaryCardRemaining()
        );

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

        VBox recentExpensesTable = createRecentExpensesSection();

        mainBox.getChildren().addAll(titleBox, summaryRow, middleGrid, recentExpensesTable);
        return mainBox;
    }

    private VBox createCustomSummaryCard(String title, Label valLbl, String valColor, String iconLiteral) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(11);
        icon.setIconColor(Color.web(SECONDARY_TEXT));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        top.getChildren().addAll(icon, t);

        valLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(top, valLbl);
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
        remainingValLabel = new Label("₹ 5,00,000"); remainingValLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(t, remainingValLabel);
        return card;
    }

    private VBox createBudgetOverviewCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon moneyIcon = new FontIcon("fas-money-bill-wave");
        moneyIcon.setIconSize(14);
        moneyIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label title = new Label("Budget Overview");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusTag = new Label("Live Synced");
        statusTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        header.getChildren().addAll(moneyIcon, title, spacer, statusTag);

        budgetOverviewTotalLabel = new Label("₹ 5,00,000");
        budgetOverviewTotalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        budgetOverviewProgressBar = new ProgressBar(0.75);
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

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon chartIcon = new FontIcon("fas-chart-pie");
        chartIcon.setIconSize(14);
        chartIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label title = new Label("Allocations (Editable by Organizer)");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label("✎ Editable");
        badge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        header.getChildren().addAll(chartIcon, title, spacer, badge);

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
            createCustomDetailItem("Date", eventDateDetailLabel, "fas-calendar-alt"),
            createCustomDetailItem("Guests", eventGuestsDetailLabel, "fas-users"),
            createCustomDetailItem("Venue", eventVenueDetailLabel, "fas-hotel")
        );

        card.getChildren().addAll(title, details);
        return card;
    }

    private VBox createCustomDetailItem(String label, Label valLbl, String iconLiteral) {
        VBox item = new VBox(2);
        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(11);
        icon.setIconColor(Color.web(SECONDARY_TEXT));

        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        top.getChildren().addAll(icon, l);

        valLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        item.getChildren().addAll(top, valLbl);
        return item;
    }

    private VBox createBudgetAlertsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox titleBox = new HBox(6);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon bellIcon = new FontIcon("fas-bell");
        bellIcon.setIconSize(13);
        bellIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label title = new Label("Budget Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        titleBox.getChildren().addAll(bellIcon, title);

        VBox alerts = new VBox(8);
        alerts.getChildren().addAll(
            createAlertItem("● Overall budget is currently on track.", GREEN_TEXT),
            createAlertItem("● Click ✎ to adjust allocation for any category.", PRIMARY_BLUE),
            createAlertItem("● Click 'Save & Publish' to sync live with Customer Dashboard.", PRIMARY_BLUE)
        );

        card.getChildren().addAll(titleBox, alerts);
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

        Label title = new Label("Recent Expenses (Accepted Vendor Bookings)");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer);

        GridPane tableHeader = new GridPane();
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        tableHeader.add(createHeaderLabel("Category"), 0, 0);
        tableHeader.add(createHeaderLabel("Vendor"), 1, 0);
        tableHeader.add(createHeaderLabel("Date"), 2, 0);
        tableHeader.add(createHeaderLabel("Amount"), 3, 0);
        tableHeader.add(createHeaderLabel("Status"), 4, 0);

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
            Label l = new Label("No accepted vendor bookings recorded yet for this event.");
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

            setGridConstraints(row);
            expensesTableContainer.getChildren().add(row);
        }
    }

    private void setGridConstraints(GridPane grid) {
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(28);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(28);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(18);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(14);
        ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(12);
        grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5);
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