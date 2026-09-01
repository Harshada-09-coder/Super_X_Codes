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

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.model.AiEventPlan;
import com.techtrio.model.Event;
import com.techtrio.services.GeminiAiService;
import com.techtrio.session.UserSession;

public class AIEventPlannerO {

    private Scene AIPageScene;
    private String uid;
    private String idToken;
    private Runnable backAction;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    
    private static final String RED_TEXT = "#C5221F";
    private static final String RED_BG = "#FCE8E6";
    private static final String ORANGE_TEXT = "#B06000";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String GREEN_TEXT = "#137333";
    private static final String RATING_COLOR = "#92400E";

    private TextField aiPromptInput;

    // Dynamic UI Controls
    private Label summaryTitleLabel;
    private Label summaryDescLabel;
    private VBox vendorsContainer;
    private VBox timelineContainer;
    private Label budgetInsightValueLabel;
    private Label userTopNameLabel;

    private Event currentEvent;
    private AiEventPlan currentAiPlan;
    private final GeminiAiService aiService = new GeminiAiService();

    public AIEventPlannerO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public AIEventPlannerO(String uid, String idToken) {
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

        loadDynamicEventAIPlanAsync();

        AIPageScene = new Scene(root, 1550, 785);
        return AIPageScene;  
    }

    public Scene getAIPageScene(Runnable callBackAction) {
        return getScene(LandingPage.landingPageStage, callBackAction);
    }

    private void loadDynamicEventAIPlanAsync() {
        String selectedEvtId = AdminContext.getSelectedEventId();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController adminController = new AdminController();
                if (selectedEvtId != null && !selectedEvtId.isBlank()) {
                    currentEvent = adminController.getEventById(selectedEvtId);
                }

                String eventTypeQuery = (currentEvent != null && currentEvent.getEventType() != null)
                        ? currentEvent.getEventType() : "Event Planning";

                try {
                    currentAiPlan = aiService.generatePlanFromEventData(eventTypeQuery);
                } catch (Exception e) {
                    currentAiPlan = aiService.generateIntelligentFallback(eventTypeQuery);
                }

                Platform.runLater(() -> renderDynamicAIContent());
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderDynamicAIContent() {
        String eventName = (currentEvent != null && currentEvent.getEventName() != null) ? currentEvent.getEventName() : "Event Planning";
        String eventBudget = (currentEvent != null && currentEvent.getBudget() != null) ? currentEvent.getBudget() : "₹5,00,000";
        int guests = (currentEvent != null && currentEvent.getGuests() > 0) ? currentEvent.getGuests() : 250;
        String date = (currentEvent != null && currentEvent.getEventDate() != null) ? currentEvent.getEventDate() : "Upcoming Date";
        String city = (currentEvent != null && currentEvent.getCity() != null) ? currentEvent.getCity() : "Pune";

        if (summaryTitleLabel != null) {
            summaryTitleLabel.setText("Analysis Complete for " + eventName);
        }

        if (summaryDescLabel != null) {
            String desc = (currentAiPlan != null && currentAiPlan.getSummary() != null && !currentAiPlan.getSummary().isBlank())
                    ? currentAiPlan.getSummary()
                    : "AI analyzed your event parameters: " + guests + " guests, " + eventBudget + " budget in " + city + " for " + date + ". Plan is fully optimized.";
            summaryDescLabel.setText(desc);
        }

        if (budgetInsightValueLabel != null) {
            budgetInsightValueLabel.setText("Total Approved: " + eventBudget);
        }

        // Render Recommended Vendors Card
        if (vendorsContainer != null) {
            vendorsContainer.getChildren().clear();
            if (currentAiPlan != null && currentAiPlan.getVendorRecommendations() != null && !currentAiPlan.getVendorRecommendations().isEmpty()) {
                for (AiEventPlan.VendorRecommendation vr : currentAiPlan.getVendorRecommendations()) {
                    vendorsContainer.getChildren().add(createVendorItemRow(vr.getCategory(), vr.getName(), vr.getRating()));
                }
            } else {
                vendorsContainer.getChildren().addAll(
                    createVendorItemRow("Venue", "Grand Royale Banquet Hall", 4.8),
                    createVendorItemRow("Catering", "Pune Feast Gourmet Caterers", 4.7),
                    createVendorItemRow("Decoration", "Opulent Events & Decor", 4.6),
                    createVendorItemRow("Photography", "FocusFrame Media Studios", 4.8)
                );
            }
        }

        // Render Timeline Card
        if (timelineContainer != null) {
            timelineContainer.getChildren().clear();
            if (currentAiPlan != null && currentAiPlan.getTimeline() != null && !currentAiPlan.getTimeline().isEmpty()) {
                for (AiEventPlan.ScheduleItem item : currentAiPlan.getTimeline()) {
                    timelineContainer.getChildren().add(createTimelineItemRow(item.getTime(), item.getActivity()));
                }
            } else {
                timelineContainer.getChildren().addAll(
                    createTimelineItemRow("08:00 AM", "Vendor Arrival & Stage Setup"),
                    createTimelineItemRow("09:15 AM", "Sound Check & AV Alignment"),
                    createTimelineItemRow("10:00 AM", "Guest Arrival & Welcome Drinks"),
                    createTimelineItemRow("11:00 AM", "Main Event Proceedings & Keynote"),
                    createTimelineItemRow("01:00 PM", "Buffet Lunch & Networking Session")
                );
            }
        }
    }

    private HBox createVendorItemRow(String category, String name, double rating) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label icon = new Label("🏢");
        icon.setStyle("-fx-font-size: 16px;");

        VBox info = new VBox(2);
        Label cat = new Label(category);
        cat.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label nm = new Label(name);
        nm.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        info.getChildren().addAll(cat, nm);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label ratingLbl = new Label(rating + " ★");
        ratingLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + RATING_COLOR + ";");

        row.getChildren().addAll(icon, info, spacer, ratingLbl);
        return row;
    }

    private HBox createTimelineItemRow(String time, String activity) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label timeLbl = new Label(time);
        timeLbl.setMinWidth(80);
        timeLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label actLbl = new Label(activity);
        actLbl.setWrapText(true);
        actLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        row.getChildren().addAll(timeLbl, actLbl);
        return row;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search events, vendors, or tasks...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(18, Color.web(PRIMARY_BLUE));
        Label avatarText = new Label("AS");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        avatar.getChildren().addAll(circle, avatarText);

        VBox userInfo = new VBox(1);
        userTopNameLabel = new Label("Organizer");
        userTopNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        Label userRole = new Label("Lead Organizer");
        userRole.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userTopNameLabel, userRole);

        profileBox.getChildren().addAll(avatar, userInfo);

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, profileBox);
        return topBar;
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setSpacing(6);

        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: white;");

        sidebar.getChildren().addAll(brandName, new Separator(),
            createNavItem("Dashboard", false, () -> goDashboard(stage)),
            createNavItem("Event Requests", false, () -> {
                EventRequestsO view = new EventRequestsO();
                stage.setScene(view.geteventRequestsOScene(() -> goDashboard(stage)));
            }),
            createNavItem("My Events", false, () -> {
                MyEventsO view = new MyEventsO();
                stage.setScene(view.getMyEventsOScene(() -> goDashboard(stage)));
            }),
            createNavItem("AI Planner", true, () -> {}),
            createNavItem("Budget", false, () -> {
                BudgetExpenseTrackerO view = new BudgetExpenseTrackerO();
                stage.setScene(view.getScene(stage, backAction));
            }),
            createNavItem("Venue", false, () -> {
                VenueManagementO view = new VenueManagementO();
                stage.setScene(view.getScene(stage, backAction));
            }),
            createNavItem("Vendors", false, () -> {
                VendorManagementO view = new VendorManagementO();
                stage.setScene(view.getvendorManagementOScene(() -> goDashboard(stage)));
            }),
            createNavItem("Schedule", false, () -> {
                ScheduleO view = new ScheduleO();
                stage.setScene(view.getScene(stage, backAction));
            })
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(4);
        bottomMenu.getChildren().addAll(
            new Separator(),
            createNavItem("Settings", false, () -> {
                SettingsO view = new SettingsO();
                stage.setScene(view.getSettingScene(() -> goDashboard(stage)));
            }),
            createNavItem("Profile", false, () -> {
                ProfileO view = new ProfileO();
                stage.setScene(view.getProfileScene());
            })
        );

        sidebar.getChildren().addAll(spacer, bottomMenu);
        return sidebar;
    }

    private Button createNavItem(String text, boolean isActive, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(isActive ? "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("Back to Event Planning");
        bc1.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));

        breadcrumbs.getChildren().add(bc1);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("AI Event Planner");
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label betaBadge = new Label(" AI OPTIMIZED ");
        betaBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        titleRow.getChildren().addAll(pageTitle, betaBadge);

        Label pageSubtitle = new Label("Plan smarter with intelligent recommendations tailored to your event parameters and current progress.");
        pageSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox headerGroup = new VBox(4);
        headerGroup.getChildren().addAll(breadcrumbs, titleRow, pageSubtitle);

        VBox aiSummaryCard = createAISummaryCard();

        // 50-50 SIDE-BY-SIDE EQUAL FIT: VENDORS CARD & TIMELINE CARD
        GridPane centerGrid = new GridPane();
        centerGrid.setHgap(20);
        centerGrid.setVgap(20);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);

        centerGrid.getColumnConstraints().addAll(col1, col2);

        VBox vendorsCard = createRecommendedVendorsCard(stage);
        VBox timelineCard = createTimelineCard();

        centerGrid.add(vendorsCard, 0, 0);
        centerGrid.add(timelineCard, 1, 0);

        // LOWER SPLIT: Recommended Steps & Risk/Budget Insights
        HBox columns = new HBox(24);

        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label recTitle = new Label("! Recommended Next Steps");
        recTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane stepsGrid = createStepsGrid(stage);
        leftCol.getChildren().addAll(recTitle, stepsGrid);

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
            createRiskAnalysisCard(),
            createBudgetInsightCard()
        );

        columns.getChildren().addAll(leftCol, rightCol);

        VBox aiChatBar = createAIChatBar();

        mainBox.getChildren().addAll(headerGroup, aiSummaryCard, centerGrid, columns, aiChatBar);
        return mainBox;
    }

    private VBox createAISummaryCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Circle c = new Circle(22, Color.web(PRIMARY_BLUE));
        Label ic = new Label("✨");
        ic.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        iconBox.getChildren().addAll(c, ic);

        VBox textGroup = new VBox(4);
        HBox.setHgrow(textGroup, Priority.ALWAYS);

        summaryTitleLabel = new Label("Analysis Complete for Event");
        summaryTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        summaryDescLabel = new Label("AI has analyzed your event parameters and requirements. Recommendations and timeline are optimized.");
        summaryDescLabel.setWrapText(true);
        summaryDescLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textGroup.getChildren().addAll(summaryTitleLabel, summaryDescLabel);

        row.getChildren().addAll(iconBox, textGroup);
        card.getChildren().add(row);
        return card;
    }

    private VBox createRecommendedVendorsCard(Stage stage) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("👥 Recommended Vendors (AI Top Picks)");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewAll = new Button("View All Vendors →");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
        viewAll.setOnAction(e -> {
            VendorManagementO view = new VendorManagementO();
            stage.setScene(view.getvendorManagementOScene(() -> stage.setScene(AIPageScene)));
        });

        header.getChildren().addAll(title, spacer, viewAll);

        vendorsContainer = new VBox(8);
        renderDynamicAIContent();

        card.getChildren().addAll(header, vendorsContainer);
        return card;
    }

    private VBox createTimelineCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("🕒 AI Optimized Event Timeline");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label("Live Schedule");
        badge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 10px;");

        header.getChildren().addAll(title, spacer, badge);

        timelineContainer = new VBox(8);
        renderDynamicAIContent();

        card.getChildren().addAll(header, timelineContainer);
        return card;
    }

    private GridPane createStepsGrid(Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(16);

        grid.add(createStepCard("HIGH PRIORITY", RED_TEXT, RED_BG, RED_TEXT, "Finalize AV & Sound", "Presentation equipment & high-fidelity audio alignment.", "Review Vendors", () -> {
            VendorManagementO view = new VendorManagementO();
            stage.setScene(view.getvendorManagementOScene(() -> stage.setScene(AIPageScene)));
        }), 0, 0);

        grid.add(createStepCard("MEDIUM PRIORITY", ORANGE_TEXT, ORANGE_BG, ORANGE_TEXT, "Confirm Catering Capacity", "Ensure menu packages match exact expected guest count.", "View Vendors", () -> {
            VendorManagementO view = new VendorManagementO();
            stage.setScene(view.getvendorManagementOScene(() -> stage.setScene(AIPageScene)));
        }), 1, 0);

        grid.add(createStepCard("MEDIUM PRIORITY", ORANGE_TEXT, ORANGE_BG, ORANGE_TEXT, "Review Venue Capacity", "Confirm seating layout and stage setup with venue.", "Check Venue", () -> {
            VenueManagementO view = new VenueManagementO();
            stage.setScene(view.getScene(stage, () -> stage.setScene(AIPageScene)));
        }), 0, 1);

        grid.add(createStepCard("TASK", PRIMARY_BLUE, LIGHT_BLUE, PRIMARY_BLUE, "Complete Guest Check-In", "Manage attendees and registration passes.", "Manage Schedule", () -> {
            ScheduleO view = new ScheduleO();
            stage.setScene(view.getScene(stage, () -> stage.setScene(AIPageScene)));
        }), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createStepCard(String tagText, String tagColor, String tagBg, String borderColor, String title, String desc, String btnText, Runnable action) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + borderColor + "; -fx-border-width: 1 1 1 4; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label tag = new Label(tagText);
        tag.setStyle("-fx-background-color: " + tagBg + "; -fx-text-fill: " + tagColor + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 4px;");

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label cardDesc = new Label(desc);
        cardDesc.setWrapText(true);
        cardDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button(btnText);
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");
        if (action != null) actionBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(tag, cardTitle, cardDesc, spacer, actionBtn);
        return card;
    }

    private VBox createRiskAnalysisCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label title = new Label("🎯 Risk Analysis");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox rows = new VBox(10);
        rows.getChildren().addAll(
            createRiskRow("⏰ Scheduling", "SAFE", GREEN_TEXT, "#E6F4EA"),
            createRiskRow("☁️ Weather", "CLEAR", GREEN_TEXT, "#E6F4EA")
        );

        card.getChildren().addAll(title, new Separator(), rows);
        return card;
    }

    private HBox createRiskRow(String labelText, String badgeText, String textCol, String bgCol) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(labelText); l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + bgCol + "; -fx-text-fill: " + textCol + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 4px;");
        row.getChildren().addAll(l, spacer, badge);
        return row;
    }

    private VBox createBudgetInsightCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label title = new Label("📊 Budget Insight");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        budgetInsightValueLabel = new Label("Total Approved: ₹5,00,000");
        budgetInsightValueLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        card.getChildren().addAll(title, budgetInsightValueLabel);
        return card;
    }

    private VBox createAIChatBar() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setPadding(new Insets(8, 14, 8, 14));
        inputBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px;");

        aiPromptInput = new TextField();
        aiPromptInput.setPromptText("Ask EventXpert AI to analyze, generate, or recommend...");
        aiPromptInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(aiPromptInput, Priority.ALWAYS);

        Button sendBtn = new Button("➢");
        sendBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-cursor: hand;");

        inputBox.getChildren().addAll(aiPromptInput, sendBtn);
        container.getChildren().add(inputBox);
        return container;
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