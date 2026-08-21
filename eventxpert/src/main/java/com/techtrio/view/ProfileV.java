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

public class ProfileV extends Application {

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

    // Dynamic State Labels
    private Label busNameLabel;
    private Label busCategoryLabel;
    private Label busLocationLabel;
    private Label busPhoneLabel;
    private Label busEmailLabel;
    private Label busWebLabel;
    private Label busBioLabel;

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
                createPageHeader(primaryStage),
                createHeroProfileCard(primaryStage),
                createVerificationBadgesRow(),
                createProfileStatsRow(),
                createTwoColumnMainLayout(primaryStage),
                createFooter()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Vendor Profile");
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
        addServiceBtn.setOnAction(e -> showToast(stage, "Add Service Clicked"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        // Menu Sections
        sidebar.getChildren().add(createSidebarCategory("OVERVIEW"));
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));

        sidebar.getChildren().add(createSidebarCategory("SERVICE MANAGEMENT"));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(stage, "Opening Calendar...")));

        sidebar.getChildren().add(createSidebarCategory("FINANCE"));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Earnings...")));

        sidebar.getChildren().add(createSidebarCategory("COMMUNICATION"));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));

        sidebar.getChildren().add(createSidebarCategory("ACCOUNT"));
        sidebar.getChildren().add(createSidebarItem("👤 Profile", true, e -> {})); // Active Menu
        sidebar.getChildren().add(createSidebarItem("⚙ Settings", false, e -> showToast(stage, "Opening Settings...")));
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
        searchField.setPromptText("🔍 Search profile...");
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

        Label title = new Label("Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button previewBtn = createSecondaryButton("👁 Preview Profile");
        previewBtn.setOnAction(e -> showToast(stage, "Opening public vendor preview..."));

        Button editBtn = createPrimaryButton("Edit Profile");
        editBtn.setOnAction(e -> openEditProfileModal(stage));

        titleRow.getChildren().addAll(title, spacer, previewBtn, editBtn);

        Label subtitle = new Label("Manage your professional vendor profile and business information.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. PROFILE HERO CARD (MATCHING STITCH)
    // ==========================================
    private VBox createHeroProfileCard(Stage stage) {
        VBox card = createCardContainer();

        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        // Avatar Stack Pane
        Circle avatarCircle = new Circle(40, Color.web(LIGHT_BLUE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 22px;");
        StackPane avatarPane = new StackPane(avatarCircle, avatarText);

        // Info Column
        VBox infoBox = new VBox(4);
        busNameLabel = new Label("VisionPro Events");
        busNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        busCategoryLabel = new Label("Audio & Visual Services");
        busCategoryLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox metaRow = new HBox(12);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        busLocationLabel = new Label("📍 Pune, Maharashtra");
        busLocationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label verifiedBadge = new Label("✓ Verified Vendor");
        verifiedBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 10px;");

        Label memberSince = new Label("EventXpert Vendor since 2026");
        memberSince.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        metaRow.getChildren().addAll(busLocationLabel, verifiedBadge, memberSince);
        infoBox.getChildren().addAll(busNameLabel, busCategoryLabel, metaRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right Completion Box
        VBox completionBox = new VBox(6);
        completionBox.setPadding(new Insets(12));
        completionBox.setPrefWidth(220);
        completionBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        HBox compHead = new HBox();
        Label compTitle = new Label("Profile Completion"); compTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region compSp = new Region(); HBox.setHgrow(compSp, Priority.ALWAYS);
        Label compVal = new Label("92%"); compVal.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        compHead.getChildren().addAll(compTitle, compSp, compVal);

        ProgressBar pb = new ProgressBar(0.92);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        Button completeBtn = new Button("Complete Profile");
        completeBtn.setMaxWidth(Double.MAX_VALUE);
        completeBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 4; -fx-background-radius: 6px; -fx-cursor: hand;");
        completeBtn.setOnAction(e -> showToast(stage, "Add portfolio photos to complete 100%!"));

        completionBox.getChildren().addAll(compHead, pb, completeBtn);

        heroRow.getChildren().addAll(avatarPane, infoBox, spacer, completionBox);
        card.getChildren().add(heroRow);
        return card;
    }

    // ==========================================
    // 5. VERIFICATION BADGES ROW
    // ==========================================
    private HBox createVerificationBadgesRow() {
        HBox row = new HBox(12);

        row.getChildren().addAll(
                createBadgePill("✓ Business Verified"),
                createBadgePill("✓ Contact Verified"),
                createBadgePill("✓ Identity Verified"),
                createBadgePill("✓ Payment Verified")
        );

        return row;
    }

    private Label createBadgePill(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;");
        return l;
    }

    // ==========================================
    // 6. PROFILE STATS ROW
    // ==========================================
    private HBox createProfileStatsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createStatCard("Total Bookings", "38", "Completed bookings", PRIMARY_BLUE),
                createStatCard("Services Offered", "8", "Active services", BRIGHT_BLUE),
                createStatCard("Reviews & Rating", "4.9 ★", "Based on 38 reviews", GREEN_TEXT),
                createStatCard("Industry Experience", "6+ Years", "Established 2022", SIDEBAR_NAVY)
        );

        return row;
    }

    private VBox createStatCard(String title, String val, String sub, String colorHex) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(t, v, s);
        return card;
    }

    // ==========================================
    // 7. TWO-COLUMN MAIN LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column (~2/3 width)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createBusinessInformationCard(stage),
                createAboutBusinessCard(stage),
                createServicesOfferedCard(stage),
                createPortfolioHighlightsCard(stage)
        );

        // Right Column (~1/3 width)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createProfileStatusCard(),
                createVerificationStatusCard(),
                createPerformanceCard()
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createBusinessInformationCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("Business Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label editLink = new Label("✏ Edit");
        editLink.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        editLink.setOnMouseClicked(e -> openEditProfileModal(stage));
        head.getChildren().addAll(title, sp, editLink);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);

        grid.add(createSpecItem("BUSINESS NAME", "VisionPro Events"), 0, 0);
        grid.add(createSpecItem("BUSINESS TYPE", "Event Service Provider"), 1, 0);
        grid.add(createSpecItem("VENDOR ID", "EVX-VND-10284"), 0, 1);
        grid.add(createSpecItem("YEAR ESTABLISHED", "2022"), 1, 1);
        grid.add(createSpecItem("PRIMARY LOCATION", "Pune, Maharashtra"), 0, 2);
        grid.add(createSpecItem("SERVICE AREA", "Pune • Mumbai • Maharashtra"), 1, 2);

        Label contactHead = new Label("Contact Details");
        contactHead.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox contactGrid = new HBox(20);
        busEmailLabel = new Label("✉ visionpro@example.com"); busEmailLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        busPhoneLabel = new Label("📞 +91 98765 43210"); busPhoneLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        busWebLabel = new Label("🌐 visionproevents.com"); busWebLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        contactGrid.getChildren().addAll(busEmailLabel, busPhoneLabel, busWebLabel);

        card.getChildren().addAll(head, new Separator(), grid, new Separator(), contactHead, contactGrid);
        return card;
    }

    private VBox createAboutBusinessCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("About Business");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label editLink = new Label("✏ Edit");
        editLink.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        editLink.setOnMouseClicked(e -> openEditProfileModal(stage));
        head.getChildren().addAll(title, sp, editLink);

        busBioLabel = new Label("VisionPro Events provides professional audio and visual services for corporate events, weddings, and large-scale concerts across Maharashtra. We specialize in high-end sound systems and atmospheric lighting.");
        busBioLabel.setWrapText(true);
        busBioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(head, new Separator(), busBioLabel);
        return card;
    }

    private VBox createServicesOfferedCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("Services Offered");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label manageLink = new Label("Manage");
        manageLink.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        manageLink.setOnMouseClicked(e -> showToast(stage, "Opening Services & Packages..."));
        head.getChildren().addAll(title, sp, manageLink);

        FlowPane tags = new FlowPane();
        tags.setHgap(8); tags.setVgap(8);

        String[] officialServices = {
                "Decoration", "Sound System", "Photography & Videography",
                "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services"
        };

        for (String s : officialServices) {
            Label tag = new Label(s);
            tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 12px;");
            tags.getChildren().add(tag);
        }

        card.getChildren().addAll(head, new Separator(), tags);
        return card;
    }

    private VBox createPortfolioHighlightsCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("Portfolio Highlights");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label viewAll = new Label("View All →");
        viewAll.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        head.getChildren().addAll(title, sp, viewAll);

        HBox portfolioGrid = new HBox(12);

        VBox item1 = createPortfolioBox("Corporate Conference", "Stage & LED Setup");
        VBox item2 = createPortfolioBox("Wedding Gala", "Lighting & Sound");

        VBox addPhotoBox = new VBox(6);
        addPhotoBox.setAlignment(Pos.CENTER);
        addPhotoBox.setPrefSize(140, 100);
        addPhotoBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-style: dashed; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        Label addIcon = new Label("🖼"); addIcon.setStyle("-fx-font-size: 18px;");
        Label addTxt = new Label("Add Photo"); addTxt.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        addPhotoBox.getChildren().addAll(addIcon, addTxt);
        addPhotoBox.setOnMouseClicked(e -> showToast(stage, "Select photo file to add to portfolio..."));

        portfolioGrid.getChildren().addAll(item1, item2, addPhotoBox);

        card.getChildren().addAll(head, new Separator(), portfolioGrid);
        return card;
    }

    private VBox createPortfolioBox(String title, String category) {
        VBox box = new VBox(4);
        box.setPrefSize(140, 100);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: " + SIDEBAR_NAVY + "; -fx-background-radius: 8px;");

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Label t = new Label(title); t.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: white;");
        Label c = new Label(category); c.setStyle("-fx-font-size: 9px; -fx-text-fill: #94A3B8;");

        box.getChildren().addAll(sp, t, c);
        return box;
    }

    // ==========================================
    // RIGHT COLUMN CARDS (MATCHING STITCH)
    // ==========================================
    private VBox createProfileStatusCard() {
        VBox card = createCardContainer();

        Label title = new Label("Profile Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox r1 = new HBox();
        VBox t1 = new VBox(2, new Label("Public Visibility"), new Label("Profile is visible to clients"));
        t1.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        t1.getChildren().get(1).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        CheckBox tog1 = new CheckBox(); tog1.setSelected(true);
        r1.getChildren().addAll(t1, sp1, tog1);

        HBox r2 = new HBox();
        VBox t2 = new VBox(2, new Label("Accepting Bookings"), new Label("Available for new requests"));
        t2.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        t2.getChildren().get(1).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        CheckBox tog2 = new CheckBox(); tog2.setSelected(true);
        r2.getChildren().addAll(t2, sp2, tog2);

        VBox hours = new VBox(4);
        Label hHead = new Label("Business Hours"); hHead.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        Label h1 = new Label("Mon - Fri: 09:00 AM - 07:00 PM"); h1.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label h2 = new Label("Saturday: 10:00 AM - 08:00 PM"); h2.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label h3 = new Label("Sunday: Closed"); h3.setStyle("-fx-font-size: 10px; -fx-text-fill: " + WARNING_TEXT + ";");
        hours.getChildren().addAll(hHead, h1, h2, h3);

        card.getChildren().addAll(title, new Separator(), r1, r2, new Separator(), hours);
        return card;
    }

    private VBox createVerificationStatusCard() {
        VBox card = createCardContainer();

        Label title = new Label("Verification Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createVerificationRow("Identity Verified", "Govt. ID approved on Jan 12, 2026", true),
                createVerificationRow("Business Registration", "GST/Tax ID verified", true),
                createVerificationRow("Contact Information", "Phone & Email verified", true),
                createVerificationRow("Bank Account", "Verification pending", false)
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private HBox createVerificationRow(String title, String desc, boolean isDone) {
        HBox r = new HBox(8);
        Label icon = new Label(isDone ? "✓" : "⏳");
        icon.setStyle("-fx-text-fill: " + (isDone ? GREEN_TEXT : WARNING_TEXT) + "; -fx-font-weight: bold;");

        VBox t = new VBox(1);
        Label head = new Label(title); head.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label sub = new Label(desc); sub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        t.getChildren().addAll(head, sub);

        r.getChildren().addAll(icon, t);
        return r;
    }

    private VBox createPerformanceCard() {
        VBox card = createCardContainer();

        Label title = new Label("Performance");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox ratingRow = new HBox(8);
        ratingRow.setAlignment(Pos.CENTER_LEFT);
        Label num = new Label("4.9"); num.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        VBox stars = new VBox(2);
        Label s1 = new Label("⭐⭐⭐⭐⭐"); s1.setStyle("-fx-font-size: 12px;");
        Label s2 = new Label("Based on 38 reviews"); s2.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        stars.getChildren().addAll(s1, s2);
        ratingRow.getChildren().addAll(num, stars);

        HBox metrics = new HBox(20);
        VBox m1 = new VBox(2, new Label("Total Bookings"), new Label("124"));
        m1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        m1.getChildren().get(1).setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox m2 = new VBox(2, new Label("Response Rate"), new Label("98%"));
        m2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        m2.getChildren().get(1).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + GREEN_TEXT + ";");

        metrics.getChildren().addAll(m1, new Separator(), m2);

        card.getChildren().addAll(title, ratingRow, new Separator(), metrics);
        return card;
    }

    // ==========================================
    // 8. FOOTER
    // ==========================================
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 0, 10, 0));

        Label copy = new Label("© 2026 EventXpert Management System. All rights reserved.  •  Privacy Policy  •  Terms of Service");
        copy.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        footer.getChildren().add(copy);
        return footer;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openEditProfileModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Edit Vendor Profile");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("✏ Edit Business Profile");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        TextField nameIn = new TextField(busNameLabel.getText());
        TextField phoneIn = new TextField("+91 98765 43210");
        TextField emailIn = new TextField("visionpro@example.com");
        TextArea bioIn = new TextArea(busBioLabel.getText()); bioIn.setPrefRowCount(3);

        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            busNameLabel.setText(nameIn.getText());
            busPhoneLabel.setText("📞 " + phoneIn.getText());
            busEmailLabel.setText("✉ " + emailIn.getText());
            busBioLabel.setText(bioIn.getText());
            showToast(parentStage, "✓ Profile updated successfully!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Business Name:"), nameIn, new Label("Phone:"), phoneIn, new Label("Email:"), emailIn, new Label("About Business:"), bioIn, saveBtn);

        Scene scene = new Scene(layout, 360, 380);
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
        Label l = new Label(label); l.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(value); v.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        b.getChildren().addAll(l, v);
        return b;
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