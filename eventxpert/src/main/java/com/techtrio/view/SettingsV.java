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

public class SettingsV extends Application {

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

    // Dynamic Navigation State
    private VBox settingsContentHolder;
    private Button btnGeneralNav;
    private Button btnNotifNav;
    private Button btnAppearanceNav;
    private Button btnLangNav;
    private Button btnSecurityNav;
    private Button btnPrivacyNav;
    private Button btnPaymentNav;

    private HBox unsavedSaveBar;

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
                createTwoColumnMainLayout(primaryStage),
                createDangerZoneCard(primaryStage)
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        // Sticky Bottom Save Bar
        unsavedSaveBar = createSaveBar(primaryStage);
        mainArea.setBottom(unsavedSaveBar);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Vendor Settings");
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
        sidebar.getChildren().add(createSidebarItem("👤 Profile & Settings", false, e -> showToast(stage, "Opening Profile...")));
        sidebar.getChildren().add(createSidebarItem("⚙ Settings", true, e -> {})); // Active Menu
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
        searchField.setPromptText("🔍 Search settings...");
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

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button saveHeaderBtn = createPrimaryButton("Save Changes");
        saveHeaderBtn.setOnAction(e -> showToast(stage, "✓ Settings saved successfully!"));

        titleRow.getChildren().addAll(title, spacer, saveHeaderBtn);

        Label subtitle = new Label("Manage your account preferences and EventXpert experience.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. TWO-COLUMN SETTINGS LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Navigation Card (~230px)
        VBox navCard = createCardContainer();
        navCard.setPrefWidth(230);
        navCard.setPadding(new Insets(12));
        navCard.setSpacing(4);

        Label cat1 = new Label("ACCOUNT"); cat1.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 8 8 2 8;");
        btnGeneralNav = createNavBtn("⚙  General", true);
        btnNotifNav = createNavBtn("🔔  Notifications", false);

        Label cat2 = new Label("PREFERENCES"); cat2.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 2 8;");
        btnAppearanceNav = createNavBtn("🎨  Appearance", false);
        btnLangNav = createNavBtn("🌐  Language & Region", false);

        Label cat3 = new Label("SECURITY"); cat3.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 2 8;");
        btnSecurityNav = createNavBtn("🛡  Password & Security", false);

        Label cat4 = new Label("PRIVACY & PAYMENTS"); cat4.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 2 8;");
        btnPrivacyNav = createNavBtn("🔒  Privacy & Permissions", false);
        btnPaymentNav = createNavBtn("💳  Payment Preferences", false);

        btnGeneralNav.setOnAction(e -> switchSettingsPane(btnGeneralNav, createGeneralSettingsPane()));
        btnNotifNav.setOnAction(e -> switchSettingsPane(btnNotifNav, createNotificationSettingsPane()));
        btnAppearanceNav.setOnAction(e -> switchSettingsPane(btnAppearanceNav, createAppearanceSettingsPane()));
        btnLangNav.setOnAction(e -> switchSettingsPane(btnLangNav, createLanguageSettingsPane()));
        btnSecurityNav.setOnAction(e -> switchSettingsPane(btnSecurityNav, createSecuritySettingsPane(stage)));
        btnPrivacyNav.setOnAction(e -> switchSettingsPane(btnPrivacyNav, createPrivacySettingsPane(stage)));
        btnPaymentNav.setOnAction(e -> switchSettingsPane(btnPaymentNav, createPaymentSettingsPane(stage)));

        navCard.getChildren().addAll(cat1, btnGeneralNav, btnNotifNav, cat2, btnAppearanceNav, btnLangNav, cat3, btnSecurityNav, cat4, btnPrivacyNav, btnPaymentNav);

        // Right Content Container
        settingsContentHolder = new VBox();
        HBox.setHgrow(settingsContentHolder, Priority.ALWAYS);

        // Default Active View
        settingsContentHolder.getChildren().add(createGeneralSettingsPane());

        layout.getChildren().addAll(navCard, settingsContentHolder);
        return layout;
    }

    private Button createNavBtn(String title, boolean isActive) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px; -fx-padding: 8 12; -fx-cursor: hand;");
        }
        return btn;
    }

    private void switchSettingsPane(Button activeBtn, VBox pane) {
        Button[] all = {btnGeneralNav, btnNotifNav, btnAppearanceNav, btnLangNav, btnSecurityNav, btnPrivacyNav, btnPaymentNav};
        for (Button b : all) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px; -fx-padding: 8 12; -fx-cursor: hand;");
        }
        activeBtn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 8 12;");

        settingsContentHolder.getChildren().clear();
        settingsContentHolder.getChildren().add(pane);
    }

    // ==========================================
    // 5. SETTINGS PANES CONTENT
    // ==========================================
    private VBox createGeneralSettingsPane() {
        VBox card = createCardContainer();

        Label title = new Label("General Settings");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label desc = new Label("Manage your basic application preferences.");
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox list = new VBox(12);
        list.getChildren().addAll(
                createToggleRow("Auto Save", "Automatically save changes while editing.", true),
                createToggleRow("Confirm Before Actions", "Ask for confirmation before cancelling bookings or rejecting requests.", true),
                createToggleRow("Compact View", "Use a more compact layout for tables and lists.", false),
                createToggleRow("Show Availability Status", "Display your current availability status across the vendor portal.", true)
        );

        card.getChildren().addAll(title, desc, new Separator(), list);
        return card;
    }

    private VBox createNotificationSettingsPane() {
        VBox card = createCardContainer();

        Label title = new Label("Notification Preferences");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createToggleRow("New Service Requests", "Notify me when an organizer sends a new service request.", true),
                createToggleRow("Booking Confirmations", "Notify me when a booking is confirmed.", true),
                createToggleRow("Payment Updates", "Notify me about payments, payouts and transactions.", true),
                createToggleRow("Booking Reminders", "Receive reminders before upcoming events.", true),
                createToggleRow("Schedule Conflicts", "Alert me when a potential booking conflict is detected.", true),
                createToggleRow("Promotional Updates", "Receive product updates and promotional messages.", false)
        );

        Label chTitle = new Label("Notification Channels");
        chTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox chList = new VBox(8);
        chList.getChildren().addAll(
                createToggleRow("In-App Notifications", "Display badges inside vendor portal.", true),
                createToggleRow("Email Notifications", "Send instant emails for urgent requests.", true),
                createToggleRow("SMS Notifications", "Send mobile SMS alerts.", false)
        );

        card.getChildren().addAll(title, new Separator(), list, new Separator(), chTitle, chList);
        return card;
    }

    private VBox createAppearanceSettingsPane() {
        VBox card = createCardContainer();

        Label title = new Label("Appearance Settings");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label tLabel = new Label("Theme Preference"); tLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        RadioButton rLight = new RadioButton("Light Theme (Default)"); rLight.setSelected(true);
        RadioButton rDark = new RadioButton("Dark Theme");
        RadioButton rSys = new RadioButton("System Default");
        ToggleGroup tg = new ToggleGroup();
        rLight.setToggleGroup(tg); rDark.setToggleGroup(tg); rSys.setToggleGroup(tg);

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createToggleRow("Keep Sidebar Expanded", "Always display full sidebar menu labels.", true),
                createToggleRow("Enable Animations", "Use smooth transition animations.", true)
        );

        card.getChildren().addAll(title, new Separator(), tLabel, rLight, rDark, rSys, new Separator(), list);
        return card;
    }

    private VBox createLanguageSettingsPane() {
        VBox card = createCardContainer();

        Label title = new Label("Language & Region");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(12);

        ComboBox<String> langCB = new ComboBox<>(); langCB.getItems().addAll("English", "Hindi", "Marathi"); langCB.setValue("English");
        ComboBox<String> regCB = new ComboBox<>(); regCB.getItems().addAll("India"); regCB.setValue("India");
        ComboBox<String> tzCB = new ComboBox<>(); tzCB.getItems().addAll("India Standard Time (IST)"); tzCB.setValue("India Standard Time (IST)");
        ComboBox<String> dfCB = new ComboBox<>(); dfCB.getItems().addAll("DD/MM/YYYY", "MM/DD/YYYY"); dfCB.setValue("DD/MM/YYYY");

        grid.add(new Label("Language:"), 0, 0); grid.add(langCB, 1, 0);
        grid.add(new Label("Region:"), 0, 1); grid.add(regCB, 1, 1);
        grid.add(new Label("Timezone:"), 0, 2); grid.add(tzCB, 1, 2);
        grid.add(new Label("Date Format:"), 0, 3); grid.add(dfCB, 1, 3);

        card.getChildren().addAll(title, new Separator(), grid);
        return card;
    }

    private VBox createSecuritySettingsPane(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Password & Security");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        PasswordField oldP = new PasswordField(); oldP.setPromptText("Current Password");
        PasswordField newP = new PasswordField(); newP.setPromptText("New Password");
        PasswordField confirmP = new PasswordField(); confirmP.setPromptText("Confirm New Password");

        Button updatePBtn = createPrimaryButton("Update Password");
        updatePBtn.setOnAction(e -> showToast(stage, "✓ Password updated successfully!"));

        VBox twoFA = new VBox(6);
        Label twoTitle = new Label("Two-Factor Authentication (2FA)"); twoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label twoStatus = new Label("Status: Enabled ✓"); twoStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + ";");
        Button manage2FABtn = createSecondaryButton("Manage 2FA");
        manage2FABtn.setOnAction(e -> showToast(stage, "2FA Settings opened."));
        twoFA.getChildren().addAll(twoTitle, twoStatus, manage2FABtn);

        card.getChildren().addAll(title, new Separator(), new Label("Change Password:"), oldP, newP, confirmP, updatePBtn, new Separator(), twoFA);
        return card;
    }

    private VBox createPrivacySettingsPane(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Privacy & Permissions");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createToggleRow("Show Contact Information", "Display phone and email on public vendor profile.", true),
                createToggleRow("Show Business Rating", "Display customer ratings and reviews.", true),
                createToggleRow("Show Service Pricing", "Display starting package prices.", true)
        );

        Button requestDataBtn = createSecondaryButton("Download My Account Data");
        requestDataBtn.setOnAction(e -> showToast(stage, "Preparing data export ZIP file..."));

        card.getChildren().addAll(title, new Separator(), list, new Separator(), requestDataBtn);
        return card;
    }

    private VBox createPaymentSettingsPane(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Payment Preferences");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label bank = new Label("🏦 Default Payout Method: HDFC Bank •••• 4821 (Verified ✓)");
        bank.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createToggleRow("Automatic Payouts", "Automatically transfer available earnings weekly.", true),
                createToggleRow("Payout Notifications", "Receive email confirmation on payout release.", true)
        );

        Button manageBankBtn = createSecondaryButton("Manage Bank Account");
        manageBankBtn.setOnAction(e -> showToast(stage, "Opening Bank Account Settings..."));

        card.getChildren().addAll(title, bank, new Separator(), list, manageBankBtn);
        return card;
    }

    private VBox createDangerZoneCard(Stage stage) {
        VBox card = createCardContainer();
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label title = new Label("🚨 Danger Zone");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        HBox r1 = new HBox();
        Label l1 = new Label("Sign Out Everywhere\nSign out of EventXpert on all active devices."); l1.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        Button signOutBtn = createSecondaryButton("Sign Out All");
        signOutBtn.setOnAction(e -> showToast(stage, "Signed out of all devices."));
        r1.getChildren().addAll(l1, sp1, signOutBtn);

        HBox r2 = new HBox();
        Label l2 = new Label("Delete Account\nPermanently delete your vendor profile and data."); l2.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        Button delBtn = createPrimaryButton("Delete Account");
        delBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        delBtn.setOnAction(e -> openDeleteModal(stage));
        r2.getChildren().addAll(l2, sp2, delBtn);

        card.getChildren().addAll(title, r1, new Separator(), r2);
        return card;
    }

    // ==========================================
    // 6. STICKY SAVE BAR & MODALS
    // ==========================================
    private HBox createSaveBar(Stage stage) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1 0 0 0;");

        Label msg = new Label("● Unsaved changes detected");
        msg.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + WARNING_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button cancelBtn = createSecondaryButton("Cancel");
        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> showToast(stage, "✓ Settings saved successfully!"));

        bar.getChildren().addAll(msg, sp, cancelBtn, saveBtn);
        return bar;
    }

    private void openDeleteModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Delete Account?");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Delete Vendor Account?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        Label msg = new Label("Are you sure? This action cannot be undone and all active bookings will be cancelled.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button confirmBtn = createPrimaryButton("Delete Account");
        confirmBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            showToast(parentStage, "Account deletion initiated.");
            modal.close();
        });

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, msg, new Separator(), confirmBtn, cancelBtn);

        Scene scene = new Scene(layout, 380, 220);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private HBox createToggleRow(String title, String desc, boolean isChecked) {
        HBox r = new HBox();
        r.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(2);
        Label t = new Label(title); t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label d = new Label(desc); d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        text.getChildren().addAll(t, d);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        CheckBox toggle = new CheckBox();
        toggle.setSelected(isChecked);

        r.getChildren().addAll(text, sp, toggle);
        return r;
    }

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