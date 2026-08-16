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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class EventDetails extends Application {

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
    private static final String GREEN_TEXT = "#137333";
    private static final String RED_TEXT = "#C5221F";
    private static final String YELLOW_BG = "#FEF7E0";
    private static final String YELLOW_TEXT = "#B06000";

    // Dynamic UI components for state management
    private Label summaryStatusLabel;
    private VBox actionAreaContainer;
    private VBox timelineContainer;
    private String currentStatus = "Pending Review";

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;");

        // Top Navigation Bar (Shiftable Search Bar)
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Full Sidebar Navigation (No Inner Scrollbar)
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Main Center Content Area (Scrollable)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1420, 880);
        primaryStage.setTitle("EventXpert - TechVision Annual Summit");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================
    // TOP BAR (With Flexible / Shiftable Search Bar)
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        // Flexible / Shiftable Search Bar
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS); // Allows search bar to flex and shift smoothly

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");
        
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search events, customers, vendors...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS); // Expand input field inside searchBox

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side icons
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        // User Profile Section
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(18, Color.web(PRIMARY_BLUE));
        Label avatarText = new Label("AU");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        avatar.getChildren().addAll(circle, avatarText);

        VBox userInfo = new VBox(1);
        Label userName = new Label("Admin User");
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        Label userRole = new Label("Event Director");
        userRole.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);

        profileBox.getChildren().addAll(avatar, userInfo);

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, helpBtn, profileBox);
        return topBar;
    }

    // ==========================================
    // SIDEBAR (Full view, no cutoffs)
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setSpacing(6);

        // Brand Logo
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
        sidebar.getChildren().add(brand);
        sidebar.getChildren().add(new Separator());

        // Full Menu List
        sidebar.getChildren().addAll(
            createNavHeader("EVENT MANAGEMENT"),
            createNavItem("Dashboard", false),
            createNavItem("Event Requests", true), // Active item
            createNavItem("My Events", false),

            createNavHeader("EVENT PLANNING"),
            createNavItem("AI Planner", false),
            createNavItem("Budget", false),
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

        // Bottom Menu
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
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 10 0 2 6;");
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
    // MAIN CONTENT
    // ==========================================
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumb
        HBox breadcrumbRow = new HBox();
        breadcrumbRow.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("Event Requests");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> showInfoAlert("Navigation", "Returning to Event Requests list..."));

        Label bcSep = new Label("  /  ");
        bcSep.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bc2 = new Label("TechVision Annual Summit");
        bc2.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpacer = new Region();
        HBox.setHgrow(bcSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Requests");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> showInfoAlert("Navigation", "Going back to Event Requests list."));

        breadcrumbRow.getChildren().addAll(bc1, bcSep, bc2, bcSpacer, backBtn);

        // Header Title Area
        HBox pageHeader = new HBox(15);
        pageHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("TechVision Annual Summit");
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label newBadge = new Label("New Request");
        newBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");

        titleRow.getChildren().addAll(pageTitle, newBadge);

        Label pageSubtitle = new Label("Corporate Conference · Event Request");
        pageSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(titleRow, pageSubtitle);
        pageHeader.getChildren().add(titleBox);

        // 2-Column Grid Layout
        HBox columns = new HBox(24);

        // Left Column (Wide)
        VBox leftColumn = new VBox(20);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        leftColumn.getChildren().addAll(
            createEventOverviewCard(),
            createEventRequirementsCard(),
            createPlanningInsightsCard()
        );

        // Right Column (Sidebar Summary)
        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(360);
        rightColumn.setMinWidth(340);

        rightColumn.getChildren().addAll(
            createRequestSummaryCard(),
            createTimelineCard(),
            createActionCard()
        );

        columns.getChildren().addAll(leftColumn, rightColumn);

        content.getChildren().addAll(breadcrumbRow, pageHeader, columns);
        return content;
    }

    // ==========================================
    // CARDS & COMPONENTS
    // ==========================================
    private VBox createEventOverviewCard() {
        VBox card = createCardContainer();

        Label cardTitle = new Label("Event Overview");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(16);
        grid.setPadding(new Insets(10, 0, 15, 0));

        grid.add(createDetailItem("Event Name", "TechVision Annual Summit"), 0, 0);
        grid.add(createDetailItem("Event Type", "Corporate Conference"), 1, 0);
        grid.add(createDetailItem("Date", "📅 18 May 2026"), 0, 1);
        grid.add(createDetailItem("Time", "⏰ 09:00 AM – 05:00 PM"), 1, 1);
        grid.add(createDetailItem("Expected Guests", "👥 250 Guests"), 0, 2);
        grid.add(createDetailItem("Requested Location", "📍 Pune, Maharashtra"), 1, 2);

        HBox customerBanner = new HBox(12);
        customerBanner.setAlignment(Pos.CENTER_LEFT);
        customerBanner.setPadding(new Insets(14, 16, 14, 16));
        customerBanner.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8;");

        StackPane custAvatar = new StackPane();
        Circle c = new Circle(18, Color.web(LIGHT_BLUE));
        Label cTxt = new Label("AT");
        cTxt.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        custAvatar.getChildren().addAll(c, cTxt);

        VBox custInfo = new VBox(2);
        Label custLabel = new Label("Customer");
        custLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label custName = new Label("Acme Technologies");
        custName.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px;");
        Label custContact = new Label("Contact: Sarah Jenkins (sarah.j@acme.com)");
        custContact.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        custInfo.getChildren().addAll(custLabel, custName, custContact);

        customerBanner.getChildren().addAll(custAvatar, custInfo);

        card.getChildren().addAll(cardTitle, grid, customerBanner);
        return card;
    }

    private VBox createEventRequirementsCard() {
        VBox card = createCardContainer();

        Label cardTitle = new Label("Event Requirements");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        FlowPane chipsPane = new FlowPane();
        chipsPane.setHgap(8);
        chipsPane.setVgap(8);
        chipsPane.setPadding(new Insets(8, 0, 12, 0));

        chipsPane.getChildren().addAll(
            createRequirementChip("🏛️ Auditorium"),
            createRequirementChip("🎭 Stage"),
            createRequirementChip("🔊 Sound"),
            createRequirementChip("📹 Projector"),
            createRequirementChip("🍽️ Catering"),
            createRequirementChip("📷 Photography"),
            createRequirementChip("📋 Registration"),
            createRequirementChip("🪑 Seating")
        );

        Label addReqLabel = new Label("Additional Requirements");
        addReqLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 6 0 2 0;");

        VBox addReqBox = new VBox();
        addReqBox.setPadding(new Insets(12));
        addReqBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        Text addReqText = new Text("Requires a dedicated VIP lounge area for keynote speakers. Fast and reliable Wi-Fi is critical for live demonstrations. Need dietary options including vegan, gluten-free, and nut-free for catering.");
        addReqText.setStyle("-fx-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        addReqText.setWrappingWidth(580);

        addReqBox.getChildren().add(addReqText);

        card.getChildren().addAll(cardTitle, chipsPane, addReqLabel, addReqBox);
        return card;
    }

    private VBox createPlanningInsightsCard() {
        VBox card = createCardContainer();

        Label cardTitle = new Label("Planning Insights");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane insightsGrid = new GridPane();
        insightsGrid.setHgap(12);
        insightsGrid.setVgap(12);
        insightsGrid.setPadding(new Insets(8, 0, 0, 0));

        insightsGrid.add(createInsightBox("🌧️ Weather Forecast", "Rain expected. Ensure indoor contingencies.", "#E0E0E0", WHITE), 0, 0);
        insightsGrid.add(createInsightBox("📅 Schedule Conflict", "Clear. No overlapping major events.", "#C8E6C9", "#E8F5E9"), 1, 0);
        insightsGrid.add(createInsightBox("📊 Budget Analysis", "Estimated costs within requested limits.", "#C8E6C9", "#E8F5E9"), 0, 1);
        insightsGrid.add(createInsightBox("📍 Venue Availability", "Multiple suitable alternatives available in Pune.", "#FFE0B2", "#FFF3E0"), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        insightsGrid.getColumnConstraints().addAll(col1, col2);

        card.getChildren().addAll(cardTitle, insightsGrid);
        return card;
    }

    private VBox createRequestSummaryCard() {
        VBox card = createCardContainer();

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label cardTitle = new Label("Request Summary");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priorityBadge = new Label(" High Priority ");
        priorityBadge.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        headerRow.getChildren().addAll(cardTitle, spacer, priorityBadge);

        VBox rows = new VBox(10);
        rows.setPadding(new Insets(12, 0, 12, 0));

        summaryStatusLabel = new Label("Pending Review");
        summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        rows.getChildren().addAll(
            createSummaryRow("Status", summaryStatusLabel),
            createSummaryRow("Date", new Label("18 May 2026")),
            createSummaryRow("Guests", new Label("250")),
            createSummaryRow("Location", new Label("Pune, MH")),
            createSummaryRow("Est. Budget", createBoldLabel("₹5,00,000"))
        );

        VBox sysRecBox = new VBox(6);
        sysRecBox.setPadding(new Insets(12));
        sysRecBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8;");

        HBox sysTitle = new HBox(6);
        sysTitle.setAlignment(Pos.CENTER_LEFT);
        Label sysIcon = new Label("⚙️");
        Label sysHeader = new Label("System Recommendation");
        sysHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
        sysTitle.getChildren().addAll(sysIcon, sysHeader);

        Label sysDesc = new Label("Highly feasible based on requested parameters and budget.");
        sysDesc.setWrapText(true);
        sysDesc.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px;");

        sysRecBox.getChildren().addAll(sysTitle, sysDesc);

        card.getChildren().addAll(headerRow, rows, sysRecBox);
        return card;
    }

    private VBox createTimelineCard() {
        VBox card = createCardContainer();

        Label cardTitle = new Label("Request Timeline");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        timelineContainer = new VBox(12);
        timelineContainer.setPadding(new Insets(10, 0, 0, 0));

        renderTimeline();

        card.getChildren().addAll(cardTitle, timelineContainer);
        return card;
    }

    private void renderTimeline() {
        timelineContainer.getChildren().clear();

        timelineContainer.getChildren().addAll(
            createTimelineItem("Submitted", "12 May 2026, 10:30 AM", true, false),
            createTimelineItem("Assigned", "13 May 2026, 09:15 AM", true, false),
            createTimelineItem("In Review", "Current Status", currentStatus.equals("Pending Review"), false),
            createTimelineItem("Decision", currentStatus.equals("Pending Review") ? "Pending" : currentStatus, !currentStatus.equals("Pending Review"), true)
        );
    }

    private VBox createActionCard() {
        actionAreaContainer = createCardContainer();
        renderActionBox();
        return actionAreaContainer;
    }

    private void renderActionBox() {
        actionAreaContainer.getChildren().clear();

        if (currentStatus.equals("Pending Review")) {
            Label actionTitle = new Label("Ready to take this event?");
            actionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

            HBox btnBox = new HBox(10);
            btnBox.setPadding(new Insets(8, 0, 0, 0));

            Button rejectBtn = new Button("Reject");
            rejectBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rejectBtn, Priority.ALWAYS);
            rejectBtn.setStyle("-fx-background-color: white; -fx-border-color: " + RED_TEXT + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 0 8 0;");
            rejectBtn.setOnAction(e -> handleReject());

            Button acceptBtn = new Button("Accept Request");
            acceptBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(acceptBtn, Priority.ALWAYS);
            acceptBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 0 8 0;");
            acceptBtn.setOnAction(e -> handleApprove());

            btnBox.getChildren().addAll(rejectBtn, acceptBtn);
            actionAreaContainer.getChildren().addAll(actionTitle, btnBox);

        } else if (currentStatus.equals("APPROVED")) {
            VBox approvedBox = new VBox(8);
            approvedBox.setAlignment(Pos.CENTER);

            Label successTitle = new Label("✓ Event Approved");
            successTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");

            Label msg = new Label("You can now continue planning this event.");
            msg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            Button planBtn = new Button("Continue Planning →");
            planBtn.setMaxWidth(Double.MAX_VALUE);
            planBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 10 0 10 0;");
            planBtn.setOnAction(e -> showInfoAlert("Planning Flow", "Navigating to AI Event Planner module..."));

            approvedBox.getChildren().addAll(successTitle, msg, planBtn);
            actionAreaContainer.getChildren().add(approvedBox);

        } else if (currentStatus.equals("REJECTED")) {
            VBox rejectedBox = new VBox(6);
            rejectedBox.setAlignment(Pos.CENTER);

            Label rejTitle = new Label("✕ Event Request Rejected");
            rejTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

            Label msg = new Label("This request has been archived.");
            msg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            rejectedBox.getChildren().addAll(rejTitle, msg);
            actionAreaContainer.getChildren().add(rejectedBox);
        }
    }

    // ==========================================
    // ACTION HANDLERS
    // ==========================================
    private void handleApprove() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Approve Event Request");
        confirmDialog.setHeaderText("Approve TechVision Annual Summit?");
        confirmDialog.setContentText("Are you sure you want to approve this request and move it to active planning?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentStatus = "APPROVED";
            summaryStatusLabel.setText("APPROVED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");

            renderTimeline();
            renderActionBox();
            showInfoAlert("Success", "Event request approved successfully!");
        }
    }

    private void handleReject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Event Request");
        dialog.setHeaderText("Reason for Rejection");
        dialog.setContentText("Please enter reason for rejecting this request:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            currentStatus = "REJECTED";
            summaryStatusLabel.setText("REJECTED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

            renderTimeline();
            renderActionBox();
            showInfoAlert("Rejected", "Event request rejected.");
        }
    }

    // ==========================================
    // UI HELPERS
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        return card;
    }

    private VBox createDetailItem(String label, String value) {
        VBox box = new VBox(3);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().addAll(l, v);
        return box;
    }

    private HBox createRequirementChip(String text) {
        HBox chip = new HBox();
        chip.setPadding(new Insets(6, 12, 6, 12));
        chip.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16; -fx-background-radius: 16;");
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        chip.getChildren().add(l);
        return chip;
    }

    private VBox createInsightBox(String title, String desc, String borderHex, String bgHex) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + bgHex + "; -fx-border-color: " + borderHex + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(t, d);
        return box;
    }

    private HBox createSummaryRow(String labelText, Label valueLabel) {
        HBox row = new HBox();
        Label l = new Label(labelText);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        valueLabel.setStyle(valueLabel.getStyle() + "; -fx-font-size: 12px;");

        row.getChildren().addAll(l, spacer, valueLabel);
        return row;
    }

    private Label createBoldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        return l;
    }

    private HBox createTimelineItem(String title, String date, boolean isDone, boolean isLast) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.TOP_LEFT);

        VBox dotLine = new VBox(0);
        dotLine.setAlignment(Pos.TOP_CENTER);

        Circle dot = new Circle(6);
        if (isDone) {
            dot.setFill(Color.web(PRIMARY_BLUE));
        } else {
            dot.setFill(Color.TRANSPARENT);
            dot.setStroke(Color.web(SECONDARY_TEXT));
            dot.setStrokeWidth(2);
        }

        dotLine.getChildren().add(dot);

        if (!isLast) {
            Region line = new Region();
            line.setPrefWidth(2);
            line.setPrefHeight(24);
            line.setStyle("-fx-background-color: " + BORDER_COLOR + ";");
            dotLine.getChildren().add(line);
        }

        VBox textContent = new VBox(1);
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + (isDone ? TEXT_MAIN : SECONDARY_TEXT) + ";");

        Label d = new Label(date);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textContent.getChildren().addAll(t, d);

        item.getChildren().addAll(dotLine, textContent);
        return item;
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}