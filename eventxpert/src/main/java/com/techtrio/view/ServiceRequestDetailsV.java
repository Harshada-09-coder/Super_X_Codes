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

public class ServiceRequestDetailsV extends Application {

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

    // Dynamic State Labels
    private Label statusBadgeHeader;
    private Label summaryStatusLabel;
    private Button topAcceptBtn;
    private Button rightAcceptBtn;

    @Override
    public void start(Stage primaryStage) {
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
                createStatusOverviewHeaderCard(),
                createConflictAlertBanner(primaryStage),
                createTwoColumnMainLayout(primaryStage),
                createBottomActionBar(primaryStage)
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Service Request Details");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
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
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", true, e -> {})); // Active
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Availability Calendar", false, e -> showToast(stage, "Opening Calendar...")));

        sidebar.getChildren().add(createSidebarCategory("FINANCE"));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Finance...")));

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
    // 3. BREADCRUMB & HEADER
    // ==========================================
    private VBox createHeaderAndBreadcrumb(Stage stage) {
        VBox header = new VBox(6);

        Label breadcrumb = new Label("Service Requests  /  TechVision Annual Summit");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Service Request Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Requests");
        backBtn.setOnAction(e -> showToast(stage, "Navigating back to Service Requests..."));

        topAcceptBtn = createPrimaryButton("Accept Request");
        topAcceptBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        titleRow.getChildren().addAll(title, spacer, backBtn, topAcceptBtn);

        Label subtitle = new Label("Review the event requirements and decide how you want to respond.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. REQUEST STATUS HEADER CARD
    // ==========================================
    private VBox createStatusOverviewHeaderCard() {
        VBox card = createCardContainer();

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        statusBadgeHeader = new Label("NEW REQUEST");
        statusBadgeHeader.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Label priorityBadge = new Label("Priority: Important");
        priorityBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label metaInfo = new Label("Received: 10 minutes ago  •  ID: SR-2026-00248");
        metaInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        topRow.getChildren().addAll(statusBadgeHeader, priorityBadge, spacer, metaInfo);

        HBox mainInfoRow = new HBox(20);
        mainInfoRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label eventName = new Label("TechVision Annual Summit");
        eventName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label orgName = new Label("Organizer: TechVision Events  •  📞 +91 98765 43210");
        orgName.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(eventName, orgName);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label eventTypeTag = new Label("Corporate Conference");
        eventTypeTag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        mainInfoRow.getChildren().addAll(titleBox, spacer2, eventTypeTag);

        card.getChildren().addAll(topRow, new Separator(), mainInfoRow);
        return card;
    }

    // ==========================================
    // 5. CONFLICT ALERT BANNER
    // ==========================================
    private HBox createConflictAlertBanner(Stage stage) {
        HBox alertBox = new HBox(12);
        alertBox.setAlignment(Pos.CENTER_LEFT);
        alertBox.setPadding(new Insets(12, 16, 12, 16));
        alertBox.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-border-color: #FCD34D; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label icon = new Label("⚠️"); icon.setStyle("-fx-font-size: 16px;");
        VBox text = new VBox(2);
        Label title = new Label("Potential Scheduling Notice"); title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + WARNING_TEXT + ";");
        Label desc = new Label("You have another booking close to this event date. Please review your availability calendar."); desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WARNING_TEXT + ";");
        text.getChildren().addAll(title, desc);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button calBtn = createSecondaryButton("View Calendar");
        calBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + WARNING_TEXT + "; -fx-border-color: " + WARNING_TEXT + "; -fx-border-radius: 6px; -fx-padding: 4 10; -fx-font-size: 11px;");
        calBtn.setOnAction(e -> showToast(stage, "Opening Availability Calendar..."));

        alertBox.getChildren().addAll(icon, text, sp, calBtn);
        return alertBox;
    }

    // ==========================================
    // 6. TWO-COLUMN MAIN LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column (Detailed Specs & Requirements)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEventInformationCard(),
                createRequestedServiceCard(),
                createEventRequirementsCard(),
                createBudgetAndPaymentCard(),
                createAttachmentsCard(stage),
                createTimelineCard(),
                createVendorResponseCard(stage)
        );

        // Right Column (Sticky Summary & Organizer Card)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createRightSummaryCard(stage),
                createOrganizerCard(stage)
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createEventInformationCard() {
        VBox card = createCardContainer();

        Label title = new Label("Event Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);

        grid.add(createSpecItem("Event Name", "TechVision Annual Summit"), 0, 0);
        grid.add(createSpecItem("Event Type", "Corporate Conference"), 1, 0);
        grid.add(createSpecItem("Event Date", "📅 18 May 2026"), 0, 1);
        grid.add(createSpecItem("Event Time", "⏰ 09:00 AM – 06:00 PM (9 Hours)"), 1, 1);
        grid.add(createSpecItem("Venue", "🏢 Grand Convention Center"), 0, 2);
        grid.add(createSpecItem("Location", "📍 Pune, Maharashtra"), 1, 2);
        grid.add(createSpecItem("Expected Guests", "👥 250 Guests"), 0, 3);
        grid.add(createSpecItem("Setup Time", "⏰ 07:30 AM (Pre-event)"), 1, 3);

        card.getChildren().addAll(title, new Separator(), grid);
        return card;
    }

    private VBox createRequestedServiceCard() {
        VBox card = createCardContainer();

        Label title = new Label("Requested Service");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox top = new HBox(16);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox b1 = new VBox(2, new Label("SERVICE CATEGORY"), new Label("Audio & Visual"));
        b1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        b1.getChildren().get(1).setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox b2 = new VBox(2, new Label("OFFERED BUDGET"), new Label("₹85,000"));
        b2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        b2.getChildren().get(1).setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        top.getChildren().addAll(b1, new Separator(), b2);

        Label reqHeader = new Label("Package Inclusions Required:");
        reqHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        FlowPane tags = new FlowPane();
        tags.setHgap(8); tags.setVgap(8);

        String[] items = {"🔊 Sound System (250 Pax)", "🎙 4 Wireless Mics", "📽 HD Projector", "📺 2 LED Displays", "💡 Stage Lighting", "👨‍💻 2 Technicians"};
        for (String item : items) {
            Label tag = new Label(item);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");
            tags.getChildren().add(tag);
        }

        card.getChildren().addAll(title, new Separator(), top, reqHeader, tags);
        return card;
    }

    private VBox createEventRequirementsCard() {
        VBox card = createCardContainer();

        Label title = new Label("Detailed Requirements");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox reqList = new VBox(8);
        reqList.getChildren().addAll(
                createReqRow("Audio Setup:", "High-clarity speaker setup suitable for 250 corporate attendees in Hall A."),
                createReqRow("Visual Setup:", "Dual LED screens on stage with high-definition presentation support."),
                createReqRow("Microphones:", "4 handheld wireless microphones + 2 lapel mics for keynote speakers."),
                createReqRow("Technical Support:", "2 dedicated technicians required on site from 07:30 AM to 06:30 PM.")
        );

        card.getChildren().addAll(title, new Separator(), reqList);
        return card;
    }

    private VBox createBudgetAndPaymentCard() {
        VBox card = createCardContainer();

        Label title = new Label("Budget & Payment Terms");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox pGrid = new HBox(24);
        VBox p1 = new VBox(2, new Label("Requested Budget"), new Label("₹85,000"));
        p1.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        p1.getChildren().get(1).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox p2 = new VBox(2, new Label("50% Advance"), new Label("₹42,500"));
        p2.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        p2.getChildren().get(1).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + GREEN_TEXT + ";");

        VBox p3 = new VBox(2, new Label("50% Post-Event"), new Label("₹42,500"));
        p3.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        p3.getChildren().get(1).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");

        pGrid.getChildren().addAll(p1, new Separator(), p2, new Separator(), p3);

        Label terms = new Label("💳 Payment Method: EventXpert Escrow Payment. Advance released upon vendor confirmation.");
        terms.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(title, new Separator(), pGrid, terms);
        return card;
    }

    private VBox createAttachmentsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Event Attachments");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox fileList = new VBox(8);
        fileList.getChildren().addAll(
                createFileRow(stage, "📄 Event Brief.pdf", "1.2 MB"),
                createFileRow(stage, "📄 Venue Layout Plan.pdf", "2.4 MB"),
                createFileRow(stage, "📄 Technical Requirements.pdf", "850 KB")
        );

        card.getChildren().addAll(title, new Separator(), fileList);
        return card;
    }

    private HBox createFileRow(Stage stage, String filename, String size) {
        HBox r = new HBox(12);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(8));
        r.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label fn = new Label(filename); fn.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label sz = new Label("(" + size + ")"); sz.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button viewBtn = createSecondaryButton("View");
        viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
        viewBtn.setOnAction(e -> showToast(stage, "Opening " + filename));

        r.getChildren().addAll(fn, sz, sp, viewBtn);
        return r;
    }

    private VBox createTimelineCard() {
        VBox card = createCardContainer();

        Label title = new Label("Request Timeline");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox timeline = new VBox(6);
        timeline.getChildren().addAll(
                createTimelineStep("✓ Request Received", "10 minutes ago", true),
                createTimelineStep("○ Request Reviewed by Vendor", "In Progress", false),
                createTimelineStep("○ Vendor Response Sent", "Pending", false),
                createTimelineStep("○ Booking Confirmed", "Pending", false)
        );

        card.getChildren().addAll(title, new Separator(), timeline);
        return card;
    }

    private HBox createTimelineStep(String step, String time, boolean isDone) {
        HBox r = new HBox(10);
        r.setAlignment(Pos.CENTER_LEFT);

        Label s = new Label(step);
        s.setStyle("-fx-font-weight: " + (isDone ? "bold" : "normal") + "; -fx-font-size: 12px; -fx-text-fill: " + (isDone ? GREEN_TEXT : SECONDARY_TEXT) + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label t = new Label(time); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        r.getChildren().addAll(s, sp, t);
        return r;
    }

    private VBox createVendorResponseCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Your Response");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label desc = new Label("Decide how you want to respond to this request from TechVision Events:");
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btnRow = new HBox(12);
        Button acceptBtn = createPrimaryButton("Accept Request");
        acceptBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        Button rejectBtn = createSecondaryButton("Reject Request");
        rejectBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px; -fx-padding: 8 16;");
        rejectBtn.setOnAction(e -> openRejectModal(stage));

        Button infoBtn = createSecondaryButton("Request More Info");
        infoBtn.setOnAction(e -> openRequestInfoModal(stage));

        btnRow.getChildren().addAll(acceptBtn, rejectBtn, infoBtn);

        card.getChildren().addAll(title, desc, new Separator(), btnRow);
        return card;
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createRightSummaryCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Request Summary");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox statusRow = new HBox();
        summaryStatusLabel = new Label("Status: NEW");
        summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label prio = new Label("Important"); prio.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 8px;");
        statusRow.getChildren().addAll(summaryStatusLabel, sp, prio);

        VBox summaryGrid = new VBox(6);
        summaryGrid.getChildren().addAll(
                new Label("• Event: TechVision Summit"),
                new Label("• Date: 18 May 2026"),
                new Label("• Service: Audio & Visual"),
                new Label("• Guests: 250 Pax"),
                new Label("• Budget: ₹85,000")
        );
        summaryGrid.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";"));

        rightAcceptBtn = createPrimaryButton("Accept Request");
        rightAcceptBtn.setMaxWidth(Double.MAX_VALUE);
        rightAcceptBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        Button rightRejectBtn = createSecondaryButton("Reject Request");
        rightRejectBtn.setMaxWidth(Double.MAX_VALUE);
        rightRejectBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + BORDER_COLOR + ";");
        rightRejectBtn.setOnAction(e -> openRejectModal(stage));

        card.getChildren().addAll(title, statusRow, new Separator(), summaryGrid, new Separator(), rightAcceptBtn, rightRejectBtn);
        return card;
    }

    private VBox createOrganizerCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Organizer Information");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label orgName = new Label("TechVision Events");
        orgName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox details = new VBox(4);
        details.getChildren().addAll(
                new Label("👤 Role: Corporate Event Organizer"),
                new Label("📞 Phone: +91 98765 43210"),
                new Label("✉ Email: events@techvision.com"),
                new Label("⭐ Rating: 4.8 / 5.0 (12 Events)")
        );
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        Button contactBtn = createSecondaryButton("Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setOnAction(e -> showToast(stage, "Contact details: +91 98765 43210 | events@techvision.com"));

        card.getChildren().addAll(title, orgName, details, new Separator(), contactBtn);
        return card;
    }

    // ==========================================
    // 7. BOTTOM ACTION BAR
    // ==========================================
    private HBox createBottomActionBar(Stage stage) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");

        Button backBtn = createSecondaryButton("← Back to Requests");
        backBtn.setOnAction(e -> showToast(stage, "Navigating back..."));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button rejBtn = createSecondaryButton("Reject Request");
        rejBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + BORDER_COLOR + ";");
        rejBtn.setOnAction(e -> openRejectModal(stage));

        Button infoBtn = createSecondaryButton("Request More Info");
        infoBtn.setOnAction(e -> openRequestInfoModal(stage));

        Button accBtn = createPrimaryButton("Accept Request");
        accBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        bar.getChildren().addAll(backBtn, sp, rejBtn, infoBtn, accBtn);
        return bar;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openAcceptConfirmationModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Accept Service Request");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Accept Service Request?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label msg = new Label("Are you sure you want to accept the Audio & Visual request for TechVision Annual Summit (18 May 2026)?");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button confirmBtn = createPrimaryButton("Confirm & Accept");
        confirmBtn.setOnAction(e -> {
            statusBadgeHeader.setText("ACCEPTED");
            statusBadgeHeader.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
            summaryStatusLabel.setText("Status: ACCEPTED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + GREEN_TEXT + ";");
            topAcceptBtn.setDisable(true);
            rightAcceptBtn.setDisable(true);
            showToast(parentStage, "✓ Service request accepted successfully!");
            modal.close();
        });

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> modal.close());

        btns.getChildren().addAll(confirmBtn, cancelBtn);
        layout.getChildren().addAll(title, msg, new Separator(), btns);

        Scene scene = new Scene(layout, 380, 200);
        modal.setScene(scene);
        modal.show();
    }

    private void openRejectModal(Stage parentStage) {
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
            statusBadgeHeader.setText("REJECTED");
            statusBadgeHeader.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
            summaryStatusLabel.setText("Status: REJECTED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + RED_TEXT + ";");
            showToast(parentStage, "Request rejected.");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Reason for Rejection:"), reasonCB, new Label("Additional Note:"), msgInput, submitBtn);

        Scene scene = new Scene(layout, 360, 320);
        modal.setScene(scene);
        modal.show();
    }

    private void openRequestInfoModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Request More Information");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Request More Information");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        TextArea msgArea = new TextArea("Please share the detailed venue layout and stage dimensions.");
        msgArea.setPrefRowCount(3);

        Button sendBtn = createPrimaryButton("Send Message");
        sendBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Message sent to TechVision Events!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Message to Organizer:"), msgArea, sendBtn);

        Scene scene = new Scene(layout, 360, 240);
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

    private VBox createSpecItem(String label, String value) {
        VBox b = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(value); v.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        b.getChildren().addAll(l, v);
        return b;
    }

    private HBox createReqRow(String label, String val) {
        HBox r = new HBox(8);
        Label l = new Label(label); l.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().addAll(l, v);
        return r;
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