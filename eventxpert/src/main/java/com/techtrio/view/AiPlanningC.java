package com.techtrio.view;

import com.techtrio.model.AiEventPlan;
import com.techtrio.services.GeminiAiService;
import com.techtrio.session.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AiPlanningC {

    private Scene scene;
    private String uid;
    private String idToken;

    // High Contrast Brand Colors
    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String MAIN_BLUE = "#002A60";
    private static final String MAIN_STRONG_BLUE = "#0D3979";
    private static final String BG = "#F4F7FC";
    private static final String BORDER = "#CBD5E1";
    private static final String DARK_TEXT = "#0F172A";
    private static final String MUTED_TEXT = "#334155";
    private static final String RATING_TEXT = "#92400E";

    private GeminiAiService aiService = new GeminiAiService();
    private AiEventPlan currentPlan;

    private StackPane dynamicContentArea;
    private VBox activeTabContent;
    private ProgressIndicator loadingIndicator;
    private TextField searchField;
    private VBox pill5;
    private VBox pill6;

    public AiPlanningC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getAiPlanningScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "#FFFFFF");
        Label logoSub = label("Plan. Organize. Celebrate.", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label createTitle = label("Create Event", 20, FontWeight.BOLD, "#FFFFFF");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("ⓘ", "Event Details", false),
                sidebarItem("▣", "Requirements", false),
                sidebarItem("⌖", "Location", false),
                sidebarItem("▣", "Budget", false),
                sidebarItem("✦", "AI Planning", true),
                sidebarItem("▤", "Review", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brand, titleBox, menu, sideSpace);

        // TOP BAR
        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setMinHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E2E8F0;-fx-border-width:0 0 1 0;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        Label notification = label("♧", 20, FontWeight.NORMAL, MAIN_BLUE);
        Label help = label("?", 13, FontWeight.BOLD, MAIN_BLUE);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + MAIN_BLUE + ";-fx-border-radius:50;-fx-background-radius:50;-fx-text-fill:" + MAIN_BLUE + ";");
        topBar.getChildren().addAll(topSpace, notification, help);

        // SCROLLABLE BODY
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox content = new VBox(18);
        content.setPadding(new Insets(20, 40, 25, 40));

        // 1. HEADER SECTION
        Label mainHeading = label("AI Event Planning", 26, FontWeight.BOLD, MAIN_BLUE);
        Label mainSubtitle = label("Get a smart, personalized plan for your event in seconds.", 13, FontWeight.NORMAL, MUTED_TEXT);
        VBox headerBox = new VBox(4, mainHeading, mainSubtitle);

        // SEARCH BAR
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(6, 8, 6, 16));
        searchBar.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");

        searchField = new TextField();
        searchField.setPromptText("Search by event type (e.g. Wedding, Conference, Birthday)...");
        searchField.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;-fx-font-size:13px;-fx-text-fill:" + DARK_TEXT + ";");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search →");
        searchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        searchBtn.setStyle("-fx-background-color:" + MAIN_BLUE + ";-fx-text-fill:white;-fx-background-radius:6;-fx-padding:8 18;-fx-cursor:hand;");
        searchBtn.setOnAction(e -> triggerPlanGeneration(searchField.getText().trim()));
        searchField.setOnAction(e -> triggerPlanGeneration(searchField.getText().trim()));

        searchBar.getChildren().addAll(label("🔍", 14, FontWeight.NORMAL, MUTED_TEXT), searchField, searchBtn);

        // POPULAR SEARCHES
        HBox popularSearches = new HBox(8);
        popularSearches.setAlignment(Pos.CENTER_LEFT);
        popularSearches.getChildren().add(label("Popular searches:", 11, FontWeight.BOLD, MUTED_TEXT));
        for (String tag : new String[]{"Wedding", "Corporate Event", "Birthday Party", "Exhibition", "Annual Meet"}) {
            Button tagBtn = new Button(tag);
            tagBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + DARK_TEXT + ";-fx-border-color:" + BORDER + ";-fx-border-radius:12;-fx-background-radius:12;-fx-font-size:11px;-fx-font-weight:bold;-fx-padding:4 12;-fx-cursor:hand;");
            tagBtn.setOnAction(e -> {
                searchField.setText(tag);
                triggerPlanGeneration(tag);
            });
            popularSearches.getChildren().add(tagBtn);
        }

        // 2. FEATURE BANNER
        HBox featureBanner = createFeatureBanner();

        // 3. AI PLAN RESULT CARD
        VBox planResultCard = new VBox(16);
        planResultCard.setPadding(new Insets(22));
        planResultCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        // Plan Header with Regenerate & Edit Inputs
        HBox planHeader = new HBox(12);
        planHeader.setAlignment(Pos.CENTER_LEFT);

        Label planTitle = label("Your AI Generated Plan", 18, FontWeight.BOLD, MAIN_BLUE);
        Label draftBadge = label("Draft", 10, FontWeight.BOLD, MAIN_STRONG_BLUE);
        draftBadge.setStyle("-fx-background-color:#E0EDFF;-fx-text-fill:" + MAIN_STRONG_BLUE + ";-fx-padding:3 10;-fx-background-radius:10;");

        Region phSpace = new Region();
        HBox.setHgrow(phSpace, Priority.ALWAYS);

        Button regenBtn = new Button("↺  Regenerate Plan");
        regenBtn.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        regenBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + DARK_TEXT + ";-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:7 14;-fx-cursor:hand;");
        regenBtn.setOnAction(e -> triggerPlanGeneration(searchField.getText().trim()));

        Button editInputsBtn = new Button("✎  Edit Inputs");
        editInputsBtn.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        editInputsBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + DARK_TEXT + ";-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:7 14;-fx-cursor:hand;");
        editInputsBtn.setOnAction(e -> {
            CreateEventC createView = new CreateEventC(uid, idToken);
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene());
        });

        planHeader.getChildren().addAll(planTitle, draftBadge, phSpace, regenBtn, editInputsBtn);

        // Stats Summary Pills Row
        HBox statsRow = new HBox(12);
        statsRow.setFillHeight(true);

        String dynamicType = ReviewPageC.EventData.eventType.isBlank() ? "Wedding" : ReviewPageC.EventData.eventType;
        String dynamicDate = ReviewPageC.EventData.eventDate.isBlank() ? "18 May 2024" : ReviewPageC.EventData.eventDate;
        String dynamicBudget = ReviewPageC.EventData.budget.isBlank() ? "₹ 8,45,000" : ReviewPageC.EventData.budget;

        VBox pill1 = statPill("Event Type", dynamicType, "📅");
        VBox pill2 = statPill("Event Date", dynamicDate, "🕒");
        VBox pill3 = statPill("Duration", "1 Day", "⏱");
        VBox pill4 = statPill("Estimated Budget", dynamicBudget, "💰");
        pill5 = statPill("Tasks", "42", "📋");
        pill6 = statPill("Vendors", "18", "👥");

        for (VBox p : new VBox[]{pill1, pill2, pill3, pill4, pill5, pill6}) {
            HBox.setHgrow(p, Priority.ALWAYS);
            statsRow.getChildren().add(p);
        }

        // TABS
        HBox tabHeader = new HBox(16);
        tabHeader.setStyle("-fx-border-color:#E2E8F0;-fx-border-width:0 0 1.5 0;");
        tabHeader.setPadding(new Insets(10, 0, 4, 0));

        String[] tabs = {"Overview", "Timeline", "Tasks", "Vendors", "Budget Breakdown", "Checklist"};
        Button[] tabButtons = new Button[tabs.length];

        activeTabContent = new VBox(15);
        activeTabContent.setPadding(new Insets(12, 0, 0, 0));

        for (int i = 0; i < tabs.length; i++) {
            final String tabName = tabs[i];
            Button tb = new Button(tabName);
            tb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            tb.setStyle(i == 0
                    ? "-fx-background-color:transparent;-fx-text-fill:" + MAIN_BLUE + ";-fx-border-color:" + MAIN_BLUE + ";-fx-border-width:0 0 2.5 0;-fx-padding:4 8;-fx-cursor:hand;"
                    : "-fx-background-color:transparent;-fx-text-fill:" + MUTED_TEXT + ";-fx-padding:4 8;-fx-cursor:hand;");

            tb.setOnAction(e -> {
                for (Button b : tabButtons) {
                    b.setStyle("-fx-background-color:transparent;-fx-text-fill:" + MUTED_TEXT + ";-fx-padding:4 8;-fx-cursor:hand;");
                }
                tb.setStyle("-fx-background-color:transparent;-fx-text-fill:" + MAIN_BLUE + ";-fx-border-color:" + MAIN_BLUE + ";-fx-border-width:0 0 2.5 0;-fx-padding:4 8;-fx-cursor:hand;");
                renderTabContent(tabName);
            });
            tabButtons[i] = tb;
            tabHeader.getChildren().add(tb);
        }

        dynamicContentArea = new StackPane();
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);
        dynamicContentArea.getChildren().addAll(activeTabContent, loadingIndicator);

        planResultCard.getChildren().addAll(planHeader, statsRow, tabHeader, dynamicContentArea);

        content.getChildren().addAll(headerBox, searchBar, popularSearches, featureBanner, planResultCard);
        scrollPane.setContent(content);

        // FOOTER
        HBox footer = new HBox(14);
        footer.setPrefHeight(65);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E2E8F0;-fx-border-width:1 0 0 0;");

        Button backBtn = new Button("← Back");
        backBtn.setPrefSize(90, 38);
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        backBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + MAIN_BLUE + ";-fx-border-color:" + MAIN_BLUE + ";-fx-border-radius:6;-fx-background-radius:6;-fx-cursor:hand;");
        backBtn.setOnAction(e -> {
            BudgetC budgetView = new BudgetC(uid, idToken);
            LandingPage.landingPageStage.setScene(budgetView.getBudgetScene());
        });

        Button saveDraftBtn = new Button("💾 Save Draft");
        saveDraftBtn.setPrefSize(115, 38);
        saveDraftBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        saveDraftBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + DARK_TEXT + ";-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-cursor:hand;");
        saveDraftBtn.setOnAction(e -> {
            savePlanToMemory();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "AI Plan successfully saved as Draft!");
            alert.setHeaderText("Draft Saved");
            alert.showAndWait();
        });

        Region fSpace = new Region();
        HBox.setHgrow(fSpace, Priority.ALWAYS);

        Button reviewBtn = new Button("Review →");
        reviewBtn.setPrefSize(115, 38);
        reviewBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        reviewBtn.setStyle("-fx-background-color:" + NAVY + ";-fx-text-fill:white;-fx-background-radius:6;-fx-cursor:hand;");
        reviewBtn.setOnAction(e -> {
            savePlanToMemory();
            ReviewPageC reviewView = new ReviewPageC(uid, idToken);
            LandingPage.landingPageStage.setScene(reviewView.getReviewScene());
        });

        footer.getChildren().addAll(backBtn, saveDraftBtn, fSpace, reviewBtn);

        VBox rightSide = new VBox(topBar, scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1365, 850);

        Platform.runLater(() -> triggerPlanGeneration(""));

        return scene;
    }

    private void triggerPlanGeneration(String query) {
        loadingIndicator.setVisible(true);
        activeTabContent.setOpacity(0.35);

        Task<AiEventPlan> planTask = new Task<>() {
            @Override
            protected AiEventPlan call() {
                return aiService.generatePlanFromEventData(query);
            }
        };

        planTask.setOnSucceeded(e -> {
            currentPlan = planTask.getValue();
            loadingIndicator.setVisible(false);
            activeTabContent.setOpacity(1.0);

            if (pill5 != null && pill6 != null) {
                Label taskCountLbl = (Label) pill5.getChildren().get(1);
                taskCountLbl.setText(String.valueOf(currentPlan.getEstimatedTasks()));

                Label vendorCountLbl = (Label) pill6.getChildren().get(1);
                vendorCountLbl.setText(String.valueOf(currentPlan.getRecommendedVendorsCount()));
            }

            renderTabContent("Overview");
        });

        planTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            activeTabContent.setOpacity(1.0);
            currentPlan = aiService.generateIntelligentFallback(query);
            renderTabContent("Overview");
        });

        Thread th = new Thread(planTask);
        th.setDaemon(true);
        th.start();
    }

    private void renderTabContent(String tabName) {
        activeTabContent.getChildren().clear();
        if (currentPlan == null) return;

        switch (tabName) {
            case "Overview" -> renderOverviewTab();
            case "Timeline" -> renderTimelineTab();
            case "Tasks", "Checklist" -> renderTasksTab();
            case "Vendors" -> renderVendorsTab();
            case "Budget Breakdown" -> renderBudgetTab();
        }
    }

    private void renderOverviewTab() {
        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(16));
        summaryBox.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");

        Label sumTitle = label("Event Summary", 15, FontWeight.BOLD, MAIN_BLUE);
        Label sumText = label(currentPlan.getSummary(), 13, FontWeight.NORMAL, DARK_TEXT);
        sumText.setWrapText(true);

        HBox statsChips = new HBox(12);
        statsChips.getChildren().addAll(
                statChip("Estimated Guests", String.valueOf(ReviewPageC.EventData.guests > 0 ? ReviewPageC.EventData.guests : 250)),
                statChip("Venue Type", ReviewPageC.EventData.venue.isBlank() ? "Banquet Hall" : ReviewPageC.EventData.venue),
                statChip("Theme", currentPlan.getTheme()),
                statChip("Location", ReviewPageC.EventData.city.isBlank() ? "Pune, Maharashtra" : ReviewPageC.EventData.city)
        );

        summaryBox.getChildren().addAll(sumTitle, sumText, statsChips);

        HBox splitRow = new HBox(18);

        VBox vendorsBox = new VBox(10);
        vendorsBox.setPadding(new Insets(16));
        vendorsBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");
        HBox.setHgrow(vendorsBox, Priority.ALWAYS);

        HBox vbHeader = new HBox(label("Recommended Vendors (Top Picks)", 14, FontWeight.BOLD, MAIN_BLUE));
        vendorsBox.getChildren().add(vbHeader);

        for (AiEventPlan.VendorRecommendation vr : currentPlan.getVendorRecommendations()) {
            HBox vrRow = new HBox(10);
            vrRow.setAlignment(Pos.CENTER_LEFT);
            vrRow.setPadding(new Insets(10));
            vrRow.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#E2E8F0;-fx-border-radius:6;-fx-background-radius:6;");

            Label catIcon = label("🏢", 15, FontWeight.NORMAL, MAIN_STRONG_BLUE);
            VBox nameBox = new VBox(2,
                    label(vr.getCategory(), 11, FontWeight.BOLD, MAIN_STRONG_BLUE),
                    label(vr.getName(), 13, FontWeight.BOLD, DARK_TEXT)
            );
            Region vSp = new Region();
            HBox.setHgrow(vSp, Priority.ALWAYS);
            Label rating = label(vr.getRating() + " ★", 12, FontWeight.BOLD, RATING_TEXT);

            vrRow.getChildren().addAll(catIcon, nameBox, vSp, rating);
            vendorsBox.getChildren().add(vrRow);
        }

        VBox budgetBox = new VBox(10);
        budgetBox.setPadding(new Insets(16));
        budgetBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");
        HBox.setHgrow(budgetBox, Priority.ALWAYS);

        Label bTitle = label("Budget Overview", 14, FontWeight.BOLD, MAIN_BLUE);
        Label totalB = label(ReviewPageC.EventData.budget.isBlank() ? "₹ 8,45,000" : ReviewPageC.EventData.budget, 20, FontWeight.BOLD, MAIN_BLUE);

        ProgressBar pb = new ProgressBar(0.70);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(10);
        pb.setStyle("-fx-accent: " + MAIN_STRONG_BLUE + ";");

        VBox bList = new VBox(7);
        for (AiEventPlan.BudgetItem bi : currentPlan.getBudgetBreakdown()) {
            HBox bRow = new HBox(8);
            bRow.setAlignment(Pos.CENTER_LEFT);
            Label bDot = label("■", 10, FontWeight.NORMAL, MAIN_STRONG_BLUE);
            Label bCat = label(bi.getCategory(), 12, FontWeight.BOLD, MUTED_TEXT);
            Region bsp = new Region();
            HBox.setHgrow(bsp, Priority.ALWAYS);
            Label bVal = label(bi.getAmount() + " (" + bi.getPercentage() + "%)", 12, FontWeight.BOLD, DARK_TEXT);
            bRow.getChildren().addAll(bDot, bCat, bsp, bVal);
            bList.getChildren().add(bRow);
        }

        budgetBox.getChildren().addAll(bTitle, totalB, pb, bList);

        splitRow.getChildren().addAll(vendorsBox, budgetBox);
        activeTabContent.getChildren().addAll(summaryBox, splitRow);
    }

    private void renderTimelineTab() {
        VBox timelineBox = new VBox(10);
        for (AiEventPlan.ScheduleItem item : currentPlan.getTimeline()) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

            Label timeLbl = label(item.getTime(), 13, FontWeight.BOLD, MAIN_STRONG_BLUE);
            timeLbl.setPrefWidth(100);
            Label actLbl = label(item.getActivity(), 13, FontWeight.BOLD, DARK_TEXT);

            row.getChildren().addAll(timeLbl, actLbl);
            timelineBox.getChildren().add(row);
        }
        activeTabContent.getChildren().add(timelineBox);
    }

    private void renderTasksTab() {
        VBox tasksBox = new VBox(10);
        for (String task : currentPlan.getTaskChecklist()) {
            CheckBox cb = new CheckBox(task);
            cb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            cb.setStyle("-fx-text-fill:" + DARK_TEXT + ";-fx-padding:8 12;");
            tasksBox.getChildren().add(cb);
        }
        activeTabContent.getChildren().add(tasksBox);
    }

    private void renderVendorsTab() {
        VBox vendorsList = new VBox(10);
        for (AiEventPlan.VendorRecommendation vr : currentPlan.getVendorRecommendations()) {
            HBox card = new HBox(14);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(14));
            card.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

            VBox vInfo = new VBox(3,
                    label(vr.getCategory(), 11, FontWeight.BOLD, MAIN_STRONG_BLUE),
                    label(vr.getName(), 14, FontWeight.BOLD, DARK_TEXT)
            );
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label rating = label("Rating: " + vr.getRating() + " ★", 13, FontWeight.BOLD, RATING_TEXT);

            card.getChildren().addAll(vInfo, sp, rating);
            vendorsList.getChildren().add(card);
        }
        activeTabContent.getChildren().add(vendorsList);
    }

    private void renderBudgetTab() {
        VBox bBox = new VBox(12);
        for (AiEventPlan.BudgetItem bi : currentPlan.getBudgetBreakdown()) {
            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

            Label cat = label(bi.getCategory(), 13, FontWeight.BOLD, DARK_TEXT);
            cat.setPrefWidth(180);
            ProgressBar pb = new ProgressBar(bi.getPercentage() / 100.0);
            HBox.setHgrow(pb, Priority.ALWAYS);
            pb.setStyle("-fx-accent:" + MAIN_STRONG_BLUE + ";");
            Label amt = label(bi.getAmount(), 13, FontWeight.BOLD, MAIN_BLUE);

            row.getChildren().addAll(cat, pb, amt);
            bBox.getChildren().add(row);
        }
        activeTabContent.getChildren().add(bBox);
    }

    // --- SAVES COMPLETE DYNAMIC AI PLAN AS JSON FOR REVIEW & ORGANIZER ---
    private void savePlanToMemory() {
        if (currentPlan != null) {
            ReviewPageC.EventData.aiPlanJson = currentPlan.toJson();
            ReviewPageC.EventData.theme = currentPlan.getTheme();
            ReviewPageC.EventData.notes = currentPlan.getSummary();
        }
    }

    private HBox createFeatureBanner() {
        HBox banner = new HBox(20);
        banner.setPadding(new Insets(18));
        banner.setStyle("-fx-background-color:#F0F6FE;-fx-border-color:#CCE3FE;-fx-border-radius:10;-fx-background-radius:10;");

        VBox leftInfo = new VBox(8);
        leftInfo.setPrefWidth(300);
        Label bIcon = label("✨", 24, FontWeight.NORMAL, MAIN_STRONG_BLUE);
        Label bTitle = label("AI-Powered Event Planning", 16, FontWeight.BOLD, MAIN_BLUE);
        Label bDesc = label("Our AI analyzes your inputs and generates a complete event plan including timeline, tasks, vendor suggestions, and budget estimate.", 12, FontWeight.NORMAL, MUTED_TEXT);
        bDesc.setWrapText(true);
        leftInfo.getChildren().addAll(bIcon, bTitle, bDesc);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        grid.add(featureTile("Smart Timeline", "Optimized schedule based on event type & duration", "🕒"), 0, 0);
        grid.add(featureTile("Vendor Suggestions", "AI-recommended vendors matching your requirements", "👥"), 1, 0);
        grid.add(featureTile("Task Checklist", "Customized tasks and deadlines for your event", "📋"), 0, 1);
        grid.add(featureTile("Budget Insights", "Estimated budget breakdown and cost optimization tips", "💰"), 1, 1);

        HBox.setHgrow(grid, Priority.ALWAYS);
        banner.getChildren().addAll(leftInfo, grid);
        return banner;
    }

    private VBox featureTile(String title, String desc, String icon) {
        VBox tile = new VBox(3);
        tile.getChildren().addAll(
                label(icon + " " + title, 13, FontWeight.BOLD, DARK_TEXT),
                label(desc, 11, FontWeight.NORMAL, MUTED_TEXT)
        );
        return tile;
    }

    private VBox statPill(String title, String val, String icon) {
        VBox pill = new VBox(3);
        pill.setPadding(new Insets(10));
        pill.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");
        pill.getChildren().addAll(
                label(icon + " " + title, 11, FontWeight.BOLD, MUTED_TEXT),
                label(val, 14, FontWeight.BOLD, MAIN_BLUE)
        );
        return pill;
    }

    private VBox statChip(String key, String val) {
        VBox chip = new VBox(2);
        chip.setPadding(new Insets(6, 12, 6, 12));
        chip.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");
        chip.getChildren().addAll(label(key, 10, FontWeight.BOLD, MUTED_TEXT), label(val, 12, FontWeight.BOLD, DARK_TEXT));
        return chip;
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 16, FontWeight.NORMAL, "white"), label(text, 13, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + ";");
        l.setTextFill(Color.web(color));
        return l;
    }
}