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

public class BookingDetailsV extends Application {

    // Color Palette Constants
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#42d16b";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String RED_TEXT = "#DC2626";

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
                createBookingHeaderCard(),
                createTwoColumnMainLayout(primaryStage)
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Booking Details");
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
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", true, e -> {})); // Active Menu
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
        searchField.setPromptText("🔍 Search bookings, events, organizers...");
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

        Label breadcrumb = new Label("My Bookings  /  Booking Details");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Booking Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to My Bookings");
        backBtn.setOnAction(e -> showToast(stage, "Returning to My Bookings..."));

        titleRow.getChildren().addAll(title, spacer, backBtn);

        Label subtitle = new Label("View complete booking information, event requirements and payment details.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. BOOKING HEADER CARD
    // ==========================================
    private VBox createBookingHeaderCard() {
        VBox card = createCardContainer();

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label statusBadge = new Label("CONFIRMED");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Label bookingIdTag = new Label("BOOKING ID: BK-2026-00124");
        bookingIdTag.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.TOP_RIGHT);
        Label amount = new Label("₹85,000");
        amount.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label payBadge = new Label("Paid");
        payBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8px;");
        priceBox.getChildren().addAll(amount, payBadge);

        topRow.getChildren().addAll(statusBadge, bookingIdTag, spacer, priceBox);

        HBox mainInfoRow = new HBox(16);
        mainInfoRow.setAlignment(Pos.CENTER_LEFT);

        Label calIcon = new Label("📅");
        calIcon.setStyle("-fx-font-size: 22px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 10; -fx-background-radius: 50%;");

        VBox titleBox = new VBox(2);
        Label eventName = new Label("TechVision Annual Summit");
        eventName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Label orgName = new Label("Organizer: TechVision Events  •  📞 +91 98765 43210");
        orgName.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(eventName, orgName);

        mainInfoRow.getChildren().addAll(calIcon, titleBox);

        card.getChildren().addAll(topRow, new Separator(), mainInfoRow);
        return card;
    }

    // ==========================================
    // 5. TWO-COLUMN MAIN LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column (Details Feed)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEventInformationCard(),
                createServiceInformationCard(),
                createRequirementsCard(),
                createPaymentInformationCard(),
                createTimelineCard(),
                createDocumentsCard(stage),
                createVenueInformationCard(stage)
        );

        // Right Column (Sticky Summary & Actions)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createSummaryCard(),
                createOrganizerCard(stage),
                createQuickActionsCard(stage)
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
        grid.add(createSpecItem("Setup Time", "⏰ 06:00 AM (3 hours prior)"), 1, 3);

        card.getChildren().addAll(title, new Separator(), grid);
        return card;
    }

    private VBox createServiceInformationCard() {
        VBox card = createCardContainer();

        Label title = new Label("Service Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox top = new HBox(16);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox b1 = new VBox(2, new Label("SERVICE"), new Label("Audio & Visual"));
        b1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        b1.getChildren().get(1).setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox b2 = new VBox(2, new Label("PACKAGE"), new Label("Premium Corporate AV Package"));
        b2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        b2.getChildren().get(1).setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        top.getChildren().addAll(b1, new Separator(), b2);

        Label reqHeader = new Label("Included Equipment & Staff:");
        reqHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        FlowPane tags = new FlowPane();
        tags.setHgap(8); tags.setVgap(8);

        String[] items = {"🔊 Sound System", "🎙 4 Wireless Mics", "📽 HD Projector", "📺 LED Display", "💡 Stage Lighting", "👨‍💻 2 Technicians"};
        for (String item : items) {
            Label tag = new Label(item);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");
            tags.getChildren().add(tag);
        }

        card.getChildren().addAll(title, new Separator(), top, reqHeader, tags);
        return card;
    }

    private VBox createRequirementsCard() {
        VBox card = createCardContainer();

        Label title = new Label("Event Requirements");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createReqRow("Audio Setup:", "Professional sound system suitable for 250 corporate guests."),
                createReqRow("Visual Setup:", "Projector and LED display for keynote presentations."),
                createReqRow("Microphones:", "4 wireless microphones + 2 lapel mics."),
                createReqRow("Technical Team:", "2 technicians required on site during the event."),
                createReqRow("Setup Time:", "Full AV setup completed by 06:00 AM on event day.")
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private VBox createPaymentInformationCard() {
        VBox card = createCardContainer();

        Label title = new Label("Payment Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox pGrid = new HBox(20);
        VBox p1 = createPayItem("Total Amount", "₹85,000", TEXT_MAIN);
        VBox p2 = createPayItem("Advance Payment", "₹42,500 (Paid)", GREEN_TEXT);
        VBox p3 = createPayItem("Final Payment", "₹42,500 (Paid)", GREEN_TEXT);

        pGrid.getChildren().addAll(p1, new Separator(), p2, new Separator(), p3);

        VBox meta = new VBox(4);
        meta.getChildren().addAll(
                new Label("💳 Payment Method: EventXpert Escrow Payment"),
                new Label("🗓 Payment Date: 15 May 2026"),
                new Label("🔢 Transaction ID: TXN-2026-45821")
        );
        meta.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        card.getChildren().addAll(title, new Separator(), pGrid, new Separator(), meta);
        return card;
    }

    private VBox createPayItem(String label, String val, String clr) {
        VBox b = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val); v.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + clr + ";");
        b.getChildren().addAll(l, v);
        return b;
    }

    private VBox createTimelineCard() {
        VBox card = createCardContainer();

        Label title = new Label("Booking Timeline");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox timeline = new VBox(6);
        timeline.getChildren().addAll(
                createTimelineStep("✓ Request Received", "12 May 2026", true),
                createTimelineStep("✓ Request Accepted by Vendor", "13 May 2026", true),
                createTimelineStep("✓ Advance Payment Received", "14 May 2026", true),
                createTimelineStep("● Event Day Scheduled", "18 May 2026", true),
                createTimelineStep("○ Final Payment Release", "Post Event Completion", false)
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

    private VBox createDocumentsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Booking Documents");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox fileList = new VBox(8);
        fileList.getChildren().addAll(
                createFileRow(stage, "📄 Event Brief.pdf", "1.2 MB"),
                createFileRow(stage, "📄 Technical Requirements.pdf", "850 KB"),
                createFileRow(stage, "📄 Invoice_BK-2026-00124.pdf", "420 KB")
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
        viewBtn.setOnAction(e -> System.out.println("View document: " + filename));

        Button dlBtn = createSecondaryButton("Download");
        dlBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
        dlBtn.setOnAction(e -> System.out.println("Download document: " + filename));

        r.getChildren().addAll(fn, sz, sp, viewBtn, dlBtn);
        return r;
    }

    private VBox createVenueInformationCard(Stage stage) {
        VBox card = createCardContainer();

        HBox top = new HBox();
        Label title = new Label("Venue Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button mapBtn = createSecondaryButton("🗺 View on Map");
        mapBtn.setOnAction(e -> System.out.println("Opening map location"));

        top.getChildren().addAll(title, sp, mapBtn);

        VBox details = new VBox(4);
        details.getChildren().addAll(
                new Label("🏢 Venue Name: Grand Convention Center"),
                new Label("🚪 Hall: Hall A, Tech Park"),
                new Label("📍 Address: Senapati Bapat Road, Pune, Maharashtra"),
                new Label("👥 Venue Capacity: 500 Guests (Expected: 250)")
        );
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        card.getChildren().addAll(top, new Separator(), details);
        return card;
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createSummaryCard() {
        VBox card = createCardContainer();

        Label title = new Label("Booking Summary");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox statusRow = new HBox();
        Label st = new Label("CONFIRMED"); st.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 8px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label id = new Label("BK-2026-00124"); id.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        statusRow.getChildren().addAll(st, sp, id);

        VBox grid = new VBox(6);
        grid.getChildren().addAll(
                new Label("• Event: TechVision Summit"),
                new Label("• Date: 18 May 2026"),
                new Label("• Service: Audio & Visual"),
                new Label("• Guests: 250 Pax"),
                new Label("• Amount: ₹85,000 (Paid)"),
                new Label("• Organizer: TechVision Events")
        );
        grid.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";"));

        card.getChildren().addAll(title, statusRow, new Separator(), grid);
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
                new Label("⭐ Rating: 4.8 / 5.0")
        );
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        Button contactBtn = createSecondaryButton("Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setOnAction(e -> showToast(stage, "Organizer Contact: +91 98765 43210"));

        card.getChildren().addAll(title, orgName, details, new Separator(), contactBtn);
        return card;
    }

    private VBox createQuickActionsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Quick Actions");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Button contactBtn = createPrimaryButton("Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setOnAction(e -> showToast(stage, "Contacting Organizer..."));

        Button calBtn = createSecondaryButton("View Calendar");
        calBtn.setMaxWidth(Double.MAX_VALUE);
        calBtn.setOnAction(e -> System.out.println("View Calendar clicked"));

        Button invoiceBtn = createSecondaryButton("Download Invoice");
        invoiceBtn.setMaxWidth(Double.MAX_VALUE);
        invoiceBtn.setOnAction(e -> System.out.println("Download Invoice clicked"));

        Button editBtn = createSecondaryButton("Edit Service");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setOnAction(e -> System.out.println("Edit service clicked"));

        Button cancelBtn = createSecondaryButton("Cancel Booking");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px;");
        cancelBtn.setOnAction(e -> openCancelModal(stage));

        card.getChildren().addAll(title, contactBtn, calBtn, invoiceBtn, editBtn, new Separator(), cancelBtn);
        return card;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openCancelModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Cancel Booking?");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Cancel Booking?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        Label msg = new Label("Are you sure you want to cancel this booking? This action may affect your payment and booking status.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button confirmBtn = createPrimaryButton("Confirm Cancellation");
        confirmBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            System.out.println("Booking cancellation confirmed");
            showToast(parentStage, "Cancellation request submitted.");
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