package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ViewEventO {

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String RED_ALERT_BG = "#FFF5F5";
    private static final String RED_ALERT_BORDER = "#E53E3E";
    private static final String RED_TEXT = "#C5221F";

    private String uid;
    private String idToken;
    private Runnable backAction;

    // Dynamic UI Component References
    private Label pageTitleLabel;
    private Label metaTypeLabel;
    private Label metaDateLabel;
    private Label metaTimeLabel;
    private Label metaCityLabel;
    private Label metaGuestsLabel;
    private Label statusBadgeLabel;

    private Label custClientLabel;
    private Label custEmailLabel;
    private Label checklistValLabel;

    public ViewEventO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ViewEventO(String uid, String idToken) {
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

        loadSelectedEventFromFirestoreAsync();

        return new Scene(root, 1550, 785);
    }

    // ==========================================
    // DYNAMIC FIRESTORE EVENT DATA LOADER
    // ==========================================
    private void loadSelectedEventFromFirestoreAsync() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController adminController = new AdminController();
                Event event = adminController.getEventById(selectedEvtId);

                if (event != null) {
                    CustomerProfile cust = adminController.getCustomerProfile(event.getCustomerId());

                    Platform.runLater(() -> {
                        if (pageTitleLabel != null) pageTitleLabel.setText(event.getEventName() != null ? event.getEventName() : "Event Planning");
                        if (metaTypeLabel != null) metaTypeLabel.setText("🏢 " + (event.getEventType() != null ? event.getEventType() : "General"));
                        if (metaDateLabel != null) metaDateLabel.setText("📅 " + (event.getEventDate() != null ? event.getEventDate() : "TBD"));
                        if (metaTimeLabel != null) metaTimeLabel.setText("⏰ " + (event.getTime() != null ? event.getTime() : "09:00 AM – 05:00 PM"));
                        if (metaCityLabel != null) metaCityLabel.setText("📍 " + (event.getCity() != null ? event.getCity() : "India"));
                        if (metaGuestsLabel != null) metaGuestsLabel.setText("👥 " + event.getGuests() + " Guests");

                        if (statusBadgeLabel != null) {
                            String st = event.getStatus() != null ? event.getStatus() : "CONFIRMED";
                            statusBadgeLabel.setText("● " + ("CONFIRMED".equalsIgnoreCase(st) ? "Confirmed" : "In Planning"));
                        }

                        if (cust != null) {
                            if (custClientLabel != null) custClientLabel.setText("Client: " + (cust.getName() != null ? cust.getName() : "Customer"));
                            if (custEmailLabel != null) custEmailLabel.setText("Email: " + (cust.getEmail() != null ? cust.getEmail() : "N/A"));
                        }
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
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
        topSearch.setPromptText("Search events, vendors, venues...");
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
        Label userName = new Label("Alex Sterling");
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        Label userRole = new Label("Lead Organizer");
        userRole.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);

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
                EventRequestsO view = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(() -> goDashboard(stage)));
            }),
            createNavItem("My Events", true, () -> handleBack(stage)),
            createNavItem("AI Planner", false, () -> openAiPlanner(stage)),
            createNavItem("Budget", false, () -> openBudget(stage)),
            createNavItem("Venue", false, () -> openVenue(stage)),
            createNavItem("Vendors", false, () -> openVendors(stage)),
            createNavItem("Schedule", false, () -> openSchedule(stage)),
            createNavItem("Guest Management", false, () -> openGuests(stage)),
            createNavItem("Live Event", false, () -> openLiveEvent(stage))
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(4);
        bottomMenu.getChildren().addAll(
            new Separator(),
            createNavItem("Settings", false, () -> {
                SettingsO view = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getSettingScene(() -> goDashboard(stage)));
            }),
            createNavItem("Profile", false, () -> {
                ProfileO view = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getProfileScene());
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

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));

        Label bcSep1 = new Label(">");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("Event Planning");
        bc2.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpacer = new Region();
        HBox.setHgrow(bcSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to My Events");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> handleBack(stage));

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSpacer, backBtn);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        pageTitleLabel = new Label("Loading Event...");
        pageTitleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        metaTypeLabel = new Label("🏢 Loading...");
        metaDateLabel = new Label("📅 Loading...");
        metaTimeLabel = new Label("⏰ Loading...");
        metaCityLabel = new Label("📍 Loading...");
        metaGuestsLabel = new Label("👥 Loading...");

        HBox metaBox = new HBox(16);
        metaBox.getChildren().addAll(metaTypeLabel, metaDateLabel, metaTimeLabel, metaCityLabel, metaGuestsLabel);
        for (javafx.scene.Node n : metaBox.getChildren()) {
            n.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        }
        titleBox.getChildren().addAll(pageTitleLabel, metaBox);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        statusBadgeLabel = new Label("● Loading Status");
        statusBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 16; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, statusBadgeLabel);

        VBox progressCard = createPlanningProgressCard();

        HBox bodyGrid = new HBox(24);

        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label modulesTitle = new Label("Planning Modules");
        modulesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane modulesGrid = createModulesGrid(stage);

        leftCol.getChildren().addAll(modulesTitle, modulesGrid);

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
            createPriorityTaskCard(stage),
            createSmartAlertsCard(),
            createCustomerAndChecklistCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, headerRow, progressCard, bodyGrid);
        return mainBox;
    }

    private VBox createPlanningProgressCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox pHeader = new HBox();
        Label pTitle = new Label("Planning Progress");
        pTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pPercent = new Label("65% Complete");
        pPercent.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + BRIGHT_BLUE + ";");

        pHeader.getChildren().addAll(pTitle, spacer, pPercent);

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-control-inner-background: " + LIGHT_BLUE + "; -fx-pref-height: 8;");

        card.getChildren().addAll(pHeader, pb);
        return card;
    }

    private GridPane createModulesGrid(Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(16);

        grid.add(createModuleCard("💡", "AI Event Planner", "Available", "Get intelligent recommendations for layout, catering, and scheduling...", "", "Open Planner →", false, LIGHT_BLUE, BRIGHT_BLUE, () -> openAiPlanner(stage)), 0, 0);
        grid.add(createModuleCard("📊", "Budget & Expenses", "On Track", "Track approved budget across all vendor categories.", "₹5,00,000 Approved", "View Budget →", false, LIGHT_BLUE, BRIGHT_BLUE, () -> openBudget(stage)), 1, 0);
        grid.add(createModuleCard("🏢", "Venue Management", "Venue Selected", "Manage selected venue details, floor plans, and seating arrangements.", "Grand Convention Center, Pune", "View Venue →", false, LIGHT_BLUE, BRIGHT_BLUE, () -> openVenue(stage)), 0, 1);
        grid.add(createModuleCard("🏪", "Vendors", "⚠️ Action Needed", "Manage required vendors, contracts, and payments.", "3 of 6 Confirmed", "Manage Vendors →", true, RED_ALERT_BG, RED_TEXT, () -> openVendors(stage)), 1, 1);

        grid.add(createModuleCard("🔴", "Live Event", "LIVE NOW", "Real-time event tracking, live sessions, vendor status, and emergency controls.", "11:42 AM • Session Active", "Open Live Dashboard →", false, LIGHT_BLUE, BRIGHT_BLUE, () -> openLiveEvent(stage)), 0, 2);
        grid.add(createModuleCard("👥", "Guest Management & QR", "In Progress", "Guest invitations, RSVP tracking, and QR check-in readiness.", "184 / 250 Registered", "Manage Guests →", false, LIGHT_BLUE, BRIGHT_BLUE, () -> openGuests(stage)), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createModuleCard(String iconStr, String title, String badgeText, String desc, String subDetail, String linkText, boolean isAlert, String badgeBg, String badgeTextCol, Runnable action) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + (isAlert ? RED_ALERT_BORDER : BORDER_COLOR) + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Rectangle iconBg = new Rectangle(36, 36);
        iconBg.setArcWidth(8); iconBg.setArcHeight(8);
        iconBg.setFill(Color.web(LIGHT_BLUE));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 16px;");
        iconBox.getChildren().addAll(iconBg, ic);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeTextCol + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        topRow.getChildren().addAll(iconBox, spacer, badge);

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label cardDesc = new Label(desc);
        cardDesc.setWrapText(true);
        cardDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, cardTitle, cardDesc);

        if (!subDetail.isEmpty()) {
            Label detailLbl = new Label(subDetail);
            detailLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (isAlert ? RED_TEXT : TEXT_MAIN) + ";");
            card.getChildren().add(detailLbl);
        }

        Region spaceBottom = new Region();
        VBox.setVgrow(spaceBottom, Priority.ALWAYS);

        Button linkBtn = new Button(linkText);
        linkBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + (isAlert ? RED_TEXT : PRIMARY_BLUE) + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0;");
        if (action != null) linkBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(spaceBottom, linkBtn);
        return card;
    }

    private VBox createPriorityTaskCard(Stage stage) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label cardTitle = new Label("! Next Priority Task");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox taskCallout = new VBox(6);
        taskCallout.setPadding(new Insets(12));
        taskCallout.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8;");

        Label priorityLabel = new Label("HIGH PRIORITY • Due 12 May");
        priorityLabel.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px;");

        Label taskDesc = new Label("Finalize AV & Sound Setup");
        taskDesc.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");

        taskCallout.getChildren().addAll(priorityLabel, taskDesc);

        Button continueBtn = new Button("Continue Task →");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        continueBtn.setOnAction(e -> openVendors(stage));

        card.getChildren().addAll(cardTitle, taskCallout, continueBtn);
        return card;
    }

    private VBox createSmartAlertsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label title = new Label("Smart Planning Alerts");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        card.getChildren().addAll(
            title,
            createAlertCallout("WEATHER ALERT", "Rain expected. Consider indoor backup.", LIGHT_BLUE, BRIGHT_BLUE),
            createAlertCallout("CONFLICT DETECTION", "No major schedule conflicts.", BACKGROUND, SECONDARY_TEXT),
            createAlertCallout("BUDGET", "Within approved ₹5L budget.", BACKGROUND, SECONDARY_TEXT)
        );

        return card;
    }

    private VBox createAlertCallout(String header, String body, String bgHex, String headerCol) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + bgHex + "; -fx-background-radius: 6; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6;");

        Label h = new Label(header); h.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + headerCol + ";");
        Label b = new Label(body); b.setWrapText(true); b.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        box.getChildren().addAll(h, b);
        return box;
    }

    private VBox createCustomerAndChecklistCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label custHeader = new Label("Customer Information");
        custHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        custClientLabel = new Label("Client: Loading...");
        custEmailLabel = new Label("Email: Loading...");

        VBox custDetails = new VBox(4);
        custDetails.getChildren().addAll(
            custClientLabel,
            custEmailLabel
        );
        for (javafx.scene.Node n : custDetails.getChildren()) {
            n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        }

        card.getChildren().addAll(custHeader, custDetails);
        return card;
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            MyEventsO view = new MyEventsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getMyEventsOScene(() -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }

    private void openAiPlanner(Stage stage) {
        AIEventPlannerO view = new AIEventPlannerO();
        LandingPage.landingPageStage.setScene(view.getScene(stage, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }

    private void openBudget(Stage stage) {
        BudgetExpenseTrackerO view = new BudgetExpenseTrackerO();
        LandingPage.landingPageStage.setScene(view.getScene(stage, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }

    private void openVenue(Stage stage) {
        VenueManagementO view = new VenueManagementO();
        LandingPage.landingPageStage.setScene(view.getScene(stage, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }

    private void openVendors(Stage stage) {
        VendorManagementO view = new VendorManagementO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(() -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }

    private void openSchedule(Stage stage) {
        ScheduleO view = new ScheduleO();
        LandingPage.landingPageStage.setScene(view.getScene(stage, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }

    private void openGuests(Stage stage) {
        GuestManagementO view = new GuestManagementO();
        LandingPage.landingPageStage.setScene(view.getScene(stage, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }

    private void openLiveEvent(Stage stage) {
        LiveEventDashboardO view = new LiveEventDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getScene(stage, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }
}