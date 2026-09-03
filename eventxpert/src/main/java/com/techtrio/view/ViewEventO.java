package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

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

public class ViewEventO {

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
    private Label custPhoneLabel;

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

        // 1. Sidebar (Matched with Organizer Dashboard)
        root.setLeft(createCommonOrganizerSidebar("My Events"));

        // 2. Main Content inside ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadSelectedEventFromFirestoreAsync();

        return new Scene(root, 1550, 785);
    }

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
                        if (metaTypeLabel != null) metaTypeLabel.setText(event.getEventType() != null ? event.getEventType() : "General");

                        String dateRange = (event.getStartDate() != null && !event.getStartDate().isBlank())
                                ? event.getStartDate() + (event.getEndDate() != null && !event.getEndDate().isBlank() && !event.getStartDate().equals(event.getEndDate()) ? " to " + event.getEndDate() : "")
                                : (event.getEventDate() != null ? event.getEventDate() : "TBD");
                        if (metaDateLabel != null) metaDateLabel.setText(dateRange);

                        String timeRange = (event.getStartTime() != null && !event.getStartTime().isBlank())
                                ? event.getStartTime() + (event.getEndTime() != null && !event.getEndTime().isBlank() ? " - " + event.getEndTime() : "")
                                : (event.getTime() != null ? event.getTime() : "09:00 AM – 05:00 PM");
                        if (metaTimeLabel != null) metaTimeLabel.setText(timeRange);

                        if (metaCityLabel != null) metaCityLabel.setText(event.getCity() != null ? event.getCity() : "India");
                        if (metaGuestsLabel != null) metaGuestsLabel.setText(event.getGuests() + " Guests");

                        if (statusBadgeLabel != null) {
                            String st = event.getStatus() != null ? event.getStatus() : "CONFIRMED";
                            statusBadgeLabel.setText("● " + ("CONFIRMED".equalsIgnoreCase(st) ? "Confirmed" : "In Planning"));
                            statusBadgeLabel.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-size: 11px;");
                        }

                        if (cust != null) {
                            if (custClientLabel != null) custClientLabel.setText("Client: " + (cust.getName() != null ? cust.getName() : "Customer"));
                            if (custEmailLabel != null) custEmailLabel.setText("Email: " + (cust.getEmail() != null ? cust.getEmail() : "N/A"));
                            if (custPhoneLabel != null) custPhoneLabel.setText("Phone: " + (cust.getMobileNo() != null ? cust.getMobileNo() : "N/A"));
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
            case "Venue":
                openVenue(LandingPage.landingPageStage);
                break;
            case "Vendors":
                openVendors(LandingPage.landingPageStage);
                break;
            case "Schedule":
                openSchedule(LandingPage.landingPageStage);
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

        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events  /  ");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));

        Label bc2 = new Label("Event Planning Hub");
        bc2.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpacer = new Region();
        HBox.setHgrow(bcSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to My Events");
        backBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-border-radius: 6px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> handleBack(stage));

        breadcrumbs.getChildren().addAll(bc1, bc2, bcSpacer, backBtn);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        pageTitleLabel = new Label("Loading Event...");
        pageTitleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        metaTypeLabel = new Label("General");
        metaDateLabel = new Label("Date TBD");
        metaTimeLabel = new Label("Time TBD");
        metaCityLabel = new Label("Pune");
        metaGuestsLabel = new Label("0 Guests");

        HBox metaBox = new HBox(16);
        metaBox.getChildren().addAll(
                createMetaChip("fas-bookmark", metaTypeLabel),
                createMetaChip("fas-calendar-alt", metaDateLabel),
                createMetaChip("fas-clock", metaTimeLabel),
                createMetaChip("fas-map-marker-alt", metaCityLabel),
                createMetaChip("fas-users", metaGuestsLabel)
        );
        titleBox.getChildren().addAll(pageTitleLabel, metaBox);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        statusBadgeLabel = new Label("● Confirmed");
        statusBadgeLabel.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 12px; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, statusBadgeLabel);

        HBox bodyGrid = new HBox(20);

        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label modulesTitle = new Label("Planning & Execution Modules");
        modulesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane modulesGrid = createModulesGrid(stage);
        leftCol.getChildren().addAll(modulesTitle, modulesGrid);

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
                createCustomerAndChecklistCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, headerRow, bodyGrid);
        return mainBox;
    }

    private HBox createMetaChip(String iconLit, Label textLbl) {
        HBox chip = new HBox(5);
        chip.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(12);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        textLbl.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");
        chip.getChildren().addAll(icon, textLbl);
        return chip;
    }

    private GridPane createModulesGrid(Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        grid.add(createModuleCard("fas-brain", "AI Event Planner", "Timefold Synced", "View and regenerate optimized timeline, tasks, and vendor picks.", "Open AI Planner →", () -> openAiPlanner(stage)), 0, 0);
        grid.add(createModuleCard("fas-wallet", "Budget & Expenses", "On Track", "Track and finalize approved budget across all vendor categories.", "View Budget →", () -> openBudget(stage)), 1, 0);
        grid.add(createModuleCard("fas-hotel", "Venue Management", "Venue Selected", "Manage venue floor plans, layout arrangements, and reservations.", "View Venue →", () -> openVenue(stage)), 0, 1);
        grid.add(createModuleCard("fas-truck", "Vendor Management", "Active", "Coordinate required service vendors, quotes, and contract status.", "Manage Vendors →", () -> openVendors(stage)), 1, 1);
        grid.add(createModuleCard("fas-calendar-alt", "Schedule & Agenda", "Timefold Slots", "Monitor real-time agenda slots and resolve scheduling conflicts.", "Open Schedule →", () -> openSchedule(stage)), 0, 2);
        grid.add(createModuleCard("fas-qrcode", "Guest & QR Check-In", "In Progress", "Manage attendee list, invitations, and live QR gate check-in.", "Manage Guests →", () -> openGuests(stage)), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createModuleCard(String iconLit, String title, String badgeText, String desc, String linkText, Runnable action) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(16, Color.web(LIGHT_BLUE));
        FontIcon ic = new FontIcon(iconLit);
        ic.setIconSize(14);
        ic.setIconColor(Color.web(PRIMARY_BLUE));
        iconBox.getChildren().addAll(iconBg, ic);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 4px;");

        topRow.getChildren().addAll(iconBox, spacer, badge);

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label cardDesc = new Label(desc);
        cardDesc.setWrapText(true);
        cardDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spaceBottom = new Region();
        VBox.setVgrow(spaceBottom, Priority.ALWAYS);

        Button linkBtn = new Button(linkText);
        linkBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0;");
        if (action != null) linkBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(topRow, cardTitle, cardDesc, spaceBottom, linkBtn);
        return card;
    }

    private VBox createCustomerAndChecklistCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label custHeader = new Label("Customer Information");
        custHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        custClientLabel = new Label("Client: Loading...");
        custEmailLabel = new Label("Email: Loading...");
        custPhoneLabel = new Label("Phone: Loading...");

        VBox custDetails = new VBox(6);
        custDetails.getChildren().addAll(custClientLabel, custEmailLabel, custPhoneLabel);
        for (javafx.scene.Node n : custDetails.getChildren()) {
            n.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        }

        card.getChildren().addAll(custHeader, new Separator(), custDetails);
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
        AIEventPlannerO view = new AIEventPlannerO(uid, idToken);
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
        String selectedEvtId = AdminContext.getSelectedEventId();
        GuestManagementO view = new GuestManagementO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getScene(stage, selectedEvtId, () -> LandingPage.landingPageStage.setScene(getScene(stage, backAction))));
    }
}