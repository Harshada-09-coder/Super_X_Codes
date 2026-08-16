package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceRequestsV extends Application {

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

    // Data Structure for Request Card
    private static class RequestData {
        String id;
        String organizer;
        String phone;
        String eventName;
        String eventType;
        String date;
        String time;
        String location;
        String guests;
        String service;
        String budget;
        String priority;
        String receivedTime;
        String status; // New, Pending, Accepted, Conflict

        RequestData(String id, String organizer, String phone, String eventName, String eventType,
                    String date, String time, String location, String guests, String service,
                    String budget, String priority, String receivedTime, String status) {
            this.id = id;
            this.organizer = organizer;
            this.phone = phone;
            this.eventName = eventName;
            this.eventType = eventType;
            this.date = date;
            this.time = time;
            this.location = location;
            this.guests = guests;
            this.service = service;
            this.budget = budget;
            this.priority = priority;
            this.receivedTime = receivedTime;
            this.status = status;
        }
    }

    private final List<RequestData> requestList = new ArrayList<>();
    private VBox cardsContainer;
    private String activeTabFilter = "All";

    @Override
    public void start(Stage primaryStage) {
        initSampleRequests();

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
                createPageHeader(primaryStage),
                createSummaryCardsRow(),
                createFilterSection(primaryStage),
                createTabsRow(primaryStage),
                createMainSplitSection(primaryStage),
                createPaginationRow()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Service Requests");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleRequests() {
        requestList.add(new RequestData("1", "TechVision Events", "+91 98765 43210", "TechVision Annual Summit", "Corporate Conference", "18 May 2026", "09:00 AM - 06:00 PM", "Grand Convention Center, Pune", "250", "Audio & Visual", "₹85,000", "Important", "10 minutes ago", "New"));
        requestList.add(new RequestData("2", "Corporate Connect", "+91 91234 56789", "Leadership Summit 2026", "Corporate Event", "24 May 2026", "10:00 AM - 05:00 PM", "Business Bay Convention Hall", "180", "Sound System", "₹55,000", "Normal", "1 hour ago", "New"));
        requestList.add(new RequestData("3", "Startup India Network", "+91 99887 66554", "Startup Connect 2026", "Networking Event", "02 Jun 2026", "09:30 AM - 06:00 PM", "Pune International Center", "300", "Rental Equipment", "₹70,000", "Important", "3 hours ago", "Pending"));
        requestList.add(new RequestData("4", "Royal Events", "+91 88990 11223", "Annual Gala Night", "Corporate Gala", "15 Jun 2026", "07:00 PM - 11:00 PM", "Royal Orchid Banquet", "200", "Decoration", "₹60,000", "Urgent", "Yesterday", "Conflict"));
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
        addServiceBtn.setOnAction(e -> showToast(stage, "Add Service Clicked"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        // Menu Sections
        sidebar.getChildren().add(createSidebarCategory("OVERVIEW"));
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));

        sidebar.getChildren().add(createSidebarCategory("SERVICE MANAGEMENT"));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", true, e -> {})); // Active Menu
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Availability Calendar", false, e -> showToast(stage, "Opening Calendar...")));

        sidebar.getChildren().add(createSidebarCategory("FINANCE"));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Earnings...")));

        sidebar.getChildren().add(createSidebarCategory("COMMUNICATION"));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));

        sidebar.getChildren().add(createSidebarCategory("ACCOUNT"));
        sidebar.getChildren().add(createSidebarItem("👤 Profile & Settings", false, e -> showToast(stage, "Opening Settings...")));
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
        Label userSub = new Label("Sound System • ● Available");
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
        searchField.setPromptText("🔍 Search requests, events, organizers...");
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
    private VBox createPageHeader(Stage stage) {
        VBox header = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Service Requests");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = createSecondaryButton("↻ Refresh");
        refreshBtn.setOnAction(e -> {
            renderRequestCards(stage);
            showToast(stage, "Requests list refreshed.");
        });

        Button calendarBtn = createPrimaryButton("📅 View Calendar");
        calendarBtn.setOnAction(e -> showToast(stage, "Opening Availability Calendar..."));

        titleRow.getChildren().addAll(title, spacer, refreshBtn, calendarBtn);

        Label subtitle = new Label("Review and manage service requests received from event organizers.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createSummaryCard("Total Requests", "24", "All Requests", PRIMARY_BLUE, "👥"),
                createSummaryCard("New Requests", "8", "Needs Review (+3 this week)", GREEN_TEXT, "✉"),
                createSummaryCard("Pending", "5", "Awaiting Response", WARNING_TEXT, "⏱"),
                createSummaryCard("Accepted", "9", "Confirmed Services", GREEN_TEXT, "✓"),
                createSummaryCard("Urgent", "2", "Requires Attention", RED_TEXT, "⚠️")
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, v, s);
        return card;
    }

    // ==========================================
    // 5. FILTER SECTION
    // ==========================================
    private VBox createFilterSection(Stage stage) {
        VBox card = createCardContainer();

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search event, organizer, service...");

        ComboBox<String> statusCB = new ComboBox<>();
        statusCB.getItems().addAll("All Status", "New", "Pending", "Accepted", "Rejected");
        statusCB.setValue("All Status");

        ComboBox<String> serviceCB = new ComboBox<>();
        serviceCB.getItems().addAll("All Services", "Decoration", "Sound System", "Photography & Videography", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services");
        serviceCB.setValue("All Services");

        ComboBox<String> eventTypeCB = new ComboBox<>();
        eventTypeCB.getItems().addAll("All Event Types", "Corporate Conference", "Corporate Event", "Networking Event", "Corporate Gala");
        eventTypeCB.setValue("All Event Types");

        ComboBox<String> locationCB = new ComboBox<>();
        locationCB.getItems().addAll("All Locations", "Pune", "Mumbai", "Bangalore");
        locationCB.setValue("All Locations");

        ComboBox<String> dateCB = new ComboBox<>();
        dateCB.getItems().addAll("All Dates", "Next 7 Days", "Next 30 Days", "May 2026");
        dateCB.setValue("All Dates");

        ComboBox<String> priorityCB = new ComboBox<>();
        priorityCB.getItems().addAll("All Priorities", "Normal", "Important", "Urgent");
        priorityCB.setValue("All Priorities");

        Button applyBtn = createPrimaryButton("Apply Filters");
        applyBtn.setOnAction(e -> {
            showToast(stage, "Filters applied.");
            renderRequestCards(stage);
        });

        Button clearBtn = createSecondaryButton("Clear");
        clearBtn.setOnAction(e -> {
            searchInput.clear();
            statusCB.setValue("All Status");
            serviceCB.setValue("All Services");
            eventTypeCB.setValue("All Event Types");
            renderRequestCards(stage);
        });

        grid.add(searchInput, 0, 0);
        grid.add(statusCB, 1, 0);
        grid.add(serviceCB, 2, 0);
        grid.add(eventTypeCB, 3, 0);
        grid.add(locationCB, 4, 0);

        grid.add(dateCB, 0, 1);
        grid.add(priorityCB, 1, 1);

        HBox btns = new HBox(8, applyBtn, clearBtn);
        grid.add(btns, 4, 1);

        card.getChildren().add(grid);
        return card;
    }

    // ==========================================
    // 6. REQUEST TABS ROW
    // ==========================================
    private HBox createTabsRow(Stage stage) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        String[] tabs = {"All 24", "New 8", "Pending 5", "Accepted 9", "Rejected 2", "Expired 0"};

        for (String tab : tabs) {
            Button tabBtn = new Button(tab);
            String tabName = tab.split(" ")[0];

            if (tabName.equals(activeTabFilter)) {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 0 0 2 0; -fx-padding: 6 12; -fx-cursor: hand;");
            } else {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-padding: 6 12; -fx-cursor: hand;");
            }

            tabBtn.setOnAction(e -> {
                activeTabFilter = tabName;
                createTabsRow(stage);
                renderRequestCards(stage);
            });

            row.getChildren().add(tabBtn);
        }

        return row;
    }

    // ==========================================
    // 7. MAIN SPLIT SECTION (REQUEST FEED & INSIGHTS)
    // ==========================================
    private HBox createMainSplitSection(Stage stage) {
        HBox row = new HBox(20);

        // Left Column: Request Cards Feed
        cardsContainer = new VBox(16);
        HBox.setHgrow(cardsContainer, Priority.ALWAYS);

        renderRequestCards(stage);

        // Right Column: Insights & Alerts Panel
        VBox rightPanel = new VBox(16);
        rightPanel.setPrefWidth(380);

        rightPanel.getChildren().addAll(
                createQuickInsightsCard(stage),
                createStatusOverviewCard(),
                createImportantAlertsCard(stage)
        );

        row.getChildren().addAll(cardsContainer, rightPanel);
        return row;
    }

    private void renderRequestCards(Stage stage) {
        cardsContainer.getChildren().clear();

        List<RequestData> filtered = requestList.stream().filter(r -> {
            if (activeTabFilter.equals("All")) return true;
            return r.status.equalsIgnoreCase(activeTabFilter);
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = createCardContainer();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            emptyBox.getChildren().add(new Label("No service requests found for tab: " + activeTabFilter));
            cardsContainer.getChildren().add(emptyBox);
            return;
        }

        for (RequestData req : filtered) {
            cardsContainer.getChildren().add(createRequestCard(stage, req));
        }
    }

    private VBox createRequestCard(Stage stage, RequestData req) {
        VBox card = createCardContainer();

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Organizer Info
        VBox orgBox = new VBox(2);
        Label orgName = new Label("👤 " + req.organizer);
        orgName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label orgPhone = new Label(req.phone);
        orgPhone.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgBox.getChildren().addAll(orgName, orgPhone);
        orgBox.setPrefWidth(160);

        // Event Specs
        VBox eventBox = new VBox(2);
        Label eName = new Label(req.eventName);
        eName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label eDetails = new Label("📅 " + req.date + " • ⏰ " + req.time + " • 📍 " + req.location + " • 👥 " + req.guests + " Pax");
        eDetails.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        eventBox.getChildren().addAll(eName, eDetails);
        HBox.setHgrow(eventBox, Priority.ALWAYS);

        // Service & Budget
        VBox serviceBox = new VBox(2);
        Label sName = new Label("Service: " + req.service);
        sName.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label budget = new Label("Budget: " + req.budget);
        budget.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        serviceBox.getChildren().addAll(sName, budget);
        serviceBox.setPrefWidth(140);

        // Meta & Status
        VBox metaBox = new VBox(4);
        metaBox.setAlignment(Pos.TOP_RIGHT);
        Label prioLbl = new Label("● " + req.priority);
        prioLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + (req.priority.equals("Urgent") ? RED_TEXT : WARNING_TEXT) + ";");
        Label recLbl = new Label("⏱ " + req.receivedTime);
        recLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label statusBadge = new Label(req.status);
        if (req.status.equals("New")) {
            statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else if (req.status.equals("Conflict")) {
            statusBadge.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        }

        metaBox.getChildren().addAll(prioLbl, recLbl, statusBadge);

        topRow.getChildren().addAll(orgBox, new Separator(), eventBox, new Separator(), serviceBox, metaBox);

        // Bottom Action Buttons
        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button detailsBtn = createSecondaryButton("View Details");
        detailsBtn.setOnAction(e -> showToast(stage, "Opening Service Request Details for " + req.eventName));

        if (req.status.equals("New")) {
            Button acceptBtn = createPrimaryButton("Accept");
            acceptBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 14; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> openAcceptConfirmationModal(stage, req));

            Button rejectBtn = createSecondaryButton("Reject");
            rejectBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + BORDER_COLOR + ";");
            rejectBtn.setOnAction(e -> openRejectModal(stage, req));

            btnRow.getChildren().addAll(detailsBtn, acceptBtn, rejectBtn);
        } else if (req.status.equals("Conflict")) {
            Button conflictBtn = createSecondaryButton("View Conflict");
            conflictBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + ";");
            conflictBtn.setOnAction(e -> showToast(stage, "Checking calendar conflicts for " + req.date));

            btnRow.getChildren().addAll(conflictBtn, detailsBtn);
        } else {
            Button reviewBtn = createPrimaryButton("Continue Review");
            reviewBtn.setOnAction(e -> showToast(stage, "Continuing review for " + req.eventName));
            btnRow.getChildren().addAll(reviewBtn, detailsBtn);
        }

        card.getChildren().addAll(topRow, new Separator(), btnRow);
        return card;
    }

    // ==========================================
    // RIGHT PANEL CARDS
    // ==========================================
    private VBox createQuickInsightsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Quick Insights");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createInsightRow("📈 Your Sound System service received 35% more requests this month."),
                createInsightRow("📅 Weekend dates between 18–30 May have high demand."),
                createInsightRow("⏱ Your average response time is excellent (under 15 mins).")
        );

        Button analysisBtn = createSecondaryButton("View Analysis →");
        analysisBtn.setOnAction(e -> showToast(stage, "Opening Vendor Analytics..."));

        card.getChildren().addAll(title, new Separator(), list, analysisBtn);
        return card;
    }

    private HBox createInsightRow(String text) {
        HBox r = new HBox(6);
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().add(l);
        return r;
    }

    private VBox createStatusOverviewCard() {
        VBox card = createCardContainer();

        Label title = new Label("Request Status Overview");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox legend = new VBox(4);
        legend.getChildren().addAll(
                createLegendRow("● New", "8 (33%)", PRIMARY_BLUE),
                createLegendRow("● Pending", "5 (21%)", WARNING_TEXT),
                createLegendRow("● Accepted", "9 (37%)", GREEN_TEXT),
                createLegendRow("● Rejected", "2 (8%)", RED_TEXT)
        );

        card.getChildren().addAll(title, new Separator(), legend);
        return card;
    }

    private HBox createLegendRow(String label, String val, String color) {
        HBox r = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        r.getChildren().addAll(l, sp, v);
        return r;
    }

    private VBox createImportantAlertsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Important Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox alerts = new VBox(8);
        alerts.getChildren().addAll(
                createAlertRow(stage, "⚠️ 3 requests require your response.", "Review now →"),
                createAlertRow(stage, "⚠️ 15 June has a booking conflict.", "Check calendar →"),
                createAlertRow(stage, "✓ ₹42,000 payment pending.", "View payments →")
        );

        card.getChildren().addAll(title, new Separator(), alerts);
        return card;
    }

    private VBox createAlertRow(Stage stage, String msg, String linkText) {
        VBox box = new VBox(2);
        Label m = new Label(msg); m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label l = new Label(linkText); l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        l.setOnMouseClicked(e -> showToast(stage, "Action: " + linkText));
        box.getChildren().addAll(m, l);
        return box;
    }

    // ==========================================
    // 8. PAGINATION
    // ==========================================
    private HBox createPaginationRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 0, 0, 0));

        Label count = new Label("Showing 1 to 10 of 24 requests");
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
    private void openAcceptConfirmationModal(Stage parentStage, RequestData req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Accept Service Request");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Accept Service Request?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label msg = new Label("Are you sure you want to accept the " + req.service + " request for " + req.eventName + "?");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button confirmBtn = createPrimaryButton("Accept Request");
        confirmBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            req.status = "Accepted";
            renderRequestCards(parentStage);
            showToast(parentStage, "✓ Request accepted successfully!");
            modal.close();
        });

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> modal.close());

        btns.getChildren().addAll(confirmBtn, cancelBtn);
        layout.getChildren().addAll(title, msg, new Separator(), btns);

        Scene scene = new Scene(layout, 380, 180);
        modal.setScene(scene);
        modal.show();
    }

    private void openRejectModal(Stage parentStage, RequestData req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Reject Service Request");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Reject Service Request");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        ComboBox<String> reasonCB = new ComboBox<>();
        reasonCB.getItems().addAll("Schedule Conflict", "Budget Too Low", "Service Not Available", "Location Not Supported", "Other");
        reasonCB.setValue("Schedule Conflict");

        TextArea msgInput = new TextArea();
        msgInput.setPromptText("Tell the organizer why you are rejecting this request...");
        msgInput.setPrefRowCount(3);

        Button submitBtn = createPrimaryButton("Reject Request");
        submitBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        submitBtn.setOnAction(e -> {
            req.status = "Rejected";
            renderRequestCards(parentStage);
            showToast(parentStage, "Request rejected.");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Reason for Rejection:"), reasonCB, new Label("Additional Note:"), msgInput, submitBtn);

        Scene scene = new Scene(layout, 360, 320);
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