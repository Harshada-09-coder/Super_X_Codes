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

public class SettingsO extends Application {

    // Color Palette Constants
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String DANGER_TEXT = "#C5221F";

    // Dynamic Navigation State
    private VBox contentHolder;
    private Button btnProfileMenu;
    private Button btnSecurityMenu;
    private Button btnNotifMenu;
    private Button btnPrefMenu;
    private Button btnAppearanceMenu;
    private Button btnSystemMenu;

    private HBox unsavedBar;
    private boolean hasUnsavedChanges = false;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Content inside ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Header & Search
        VBox headerBox = createPageHeader(primaryStage);

        // Settings Split Layout: Left Nav Card + Right Form Card
        HBox settingsSplit = new HBox(20);

        VBox navCard = createSettingsNavCard(primaryStage);
        navCard.setPrefWidth(240);

        contentHolder = new VBox();
        HBox.setHgrow(contentHolder, Priority.ALWAYS);

        // Default Active View: Profile
        showProfileSettings(primaryStage);

        settingsSplit.getChildren().addAll(navCard, contentHolder);

        // Bottom Unsaved Changes Bar (Matching Stitch Design)
        unsavedBar = createUnsavedChangesBar(primaryStage);

        mainContent.getChildren().addAll(headerBox, settingsSplit, unsavedBar);

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Settings & Preferences");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // ==========================================
    // 1. SIDEBAR NAVIGATION
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(6);

        Label brandLabel = new Label("EventXpert");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Enterprise Suite");
        brandSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox brandBox = new VBox(2, brandLabel, brandSub);
        brandBox.setPadding(new Insets(0, 8, 16, 8));

        sidebar.getChildren().add(brandBox);

        sidebar.getChildren().add(createSidebarCategory("MAIN"));
        sidebar.getChildren().add(createSidebarItem("Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));
        sidebar.getChildren().add(createSidebarItem("Event Requests", false, e -> showToast(stage, "Opening Event Requests...")));
        sidebar.getChildren().add(createSidebarItem("My Events", false, e -> showToast(stage, "Opening My Events...")));

        sidebar.getChildren().add(createSidebarCategory("EVENT PLANNING"));
        sidebar.getChildren().add(createSidebarItem("AI Planner", false, e -> showToast(stage, "Opening AI Planner...")));
        sidebar.getChildren().add(createSidebarItem("Budget", false, e -> showToast(stage, "Opening Budget...")));
        sidebar.getChildren().add(createSidebarItem("Venue", false, e -> showToast(stage, "Opening Venue...")));
        sidebar.getChildren().add(createSidebarItem("Vendors", false, e -> showToast(stage, "Opening Vendors...")));
        sidebar.getChildren().add(createSidebarItem("Schedule", false, e -> showToast(stage, "Opening Schedule...")));

        sidebar.getChildren().add(createSidebarCategory("EXECUTION"));
        sidebar.getChildren().add(createSidebarItem("Guest Management", false, e -> showToast(stage, "Opening Guest Management...")));
        sidebar.getChildren().add(createSidebarItem("QR Check-In", false, e -> showToast(stage, "Opening QR Check-In...")));
        sidebar.getChildren().add(createSidebarItem("Live Event", false, e -> showToast(stage, "Opening Live Event...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Highlighted Settings Menu
        HBox settingsRow = new HBox(8);
        settingsRow.setAlignment(Pos.CENTER_LEFT);
        settingsRow.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px; -fx-padding: 8 12;");
        Label settingsText = new Label("⚙ Settings");
        settingsText.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        settingsRow.getChildren().add(settingsText);

        // Profile Box
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(10));
        profileBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        Text avatarText = new Text("OP");
        avatarText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Admin User");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label userEmail = new Label("admin@eventxpert.com");
        userEmail.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        userDetails.getChildren().addAll(userName, userEmail);

        profileBox.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, settingsRow, profileBox);
        return sidebar;
    }

    private Label createSidebarCategory(String title) {
        Label cat = new Label(title);
        cat.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 4 8;");
        return cat;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12;"));
        return btn;
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
        searchField.setPromptText("🔍 Search event data, settings...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "No unread alerts"));

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        Label userLabel = new Label("Admin User\nLead Organizer");
        userLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        topBar.getChildren().addAll(searchField, spacer, notifBtn, helpBtn, profilePill, userLabel);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("Home  >  Settings");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Manage your account, preferences, notifications and organizer portal settings to tailor EventXpert to your workflow.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, title, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. SETTINGS NAVIGATION CARD
    // ==========================================
    private VBox createSettingsNavCard(Stage stage) {
        VBox card = createCardContainer();
        card.setPadding(new Insets(12));
        card.setSpacing(4);

        btnProfileMenu = createNavButton("👤  Profile", true);
        btnSecurityMenu = createNavButton("🛡  Security", false);
        btnNotifMenu = createNavButton("🔔  Notifications", false);
        btnPrefMenu = createNavButton("⚙  Preferences", false);
        btnAppearanceMenu = createNavButton("🎨  Appearance", false);
        btnSystemMenu = createNavButton("💻  System & Data", false);

        btnProfileMenu.setOnAction(e -> { setActiveNav(btnProfileMenu); showProfileSettings(stage); });
        btnSecurityMenu.setOnAction(e -> { setActiveNav(btnSecurityMenu); showSecuritySettings(stage); });
        btnNotifMenu.setOnAction(e -> { setActiveNav(btnNotifMenu); showNotificationSettings(stage); });
        btnPrefMenu.setOnAction(e -> { setActiveNav(btnPrefMenu); showPreferencesSettings(stage); });
        btnAppearanceMenu.setOnAction(e -> { setActiveNav(btnAppearanceMenu); showAppearanceSettings(stage); });
        btnSystemMenu.setOnAction(e -> { setActiveNav(btnSystemMenu); showSystemSettings(stage); });

        card.getChildren().addAll(btnProfileMenu, btnSecurityMenu, btnNotifMenu, btnPrefMenu, btnAppearanceMenu, btnSystemMenu);
        return card;
    }

    private Button createNavButton(String title, boolean isActive) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 10 14;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");
        }
        return btn;
    }

    private void setActiveNav(Button activeBtn) {
        Button[] all = {btnProfileMenu, btnSecurityMenu, btnNotifMenu, btnPrefMenu, btnAppearanceMenu, btnSystemMenu};
        for (Button b : all) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");
        }
        activeBtn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 10 14;");
    }

    // ==========================================
    // 5. PROFILE SETTINGS (Matching Visual Reference)
    // ==========================================
    private void showProfileSettings(Stage stage) {
        contentHolder.getChildren().clear();

        VBox card = createCardContainer();

        Label title = new Label("👤 Profile Information");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Update your photo and personal details here.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox headerGroup = new VBox(2, title, subtitle);

        // Photo Upload Box
        HBox photoBox = new HBox(16);
        photoBox.setAlignment(Pos.CENTER_LEFT);
        photoBox.setPadding(new Insets(12, 0, 12, 0));

        Circle avatarCircle = new Circle(32, Color.web(PRIMARY_BLUE));
        Text avatarText = new Text("OP");
        avatarText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        StackPane avatarPane = new StackPane(avatarCircle, avatarText);

        VBox photoInfo = new VBox(6);
        Button changePhotoBtn = createSecondaryButton("Change Photo");
        changePhotoBtn.setOnAction(e -> showToast(stage, "Select image file (JPG, PNG max 800KB)"));

        Label photoHint = new Label("JPG, GIF or PNG. Max size of 800K");
        photoHint.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        photoInfo.getChildren().addAll(changePhotoBtn, photoHint);

        photoBox.getChildren().addAll(avatarPane, photoInfo);

        // Form Fields Grid
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);

        TextField nameInput = createFormInput("Admin User");
        TextField emailInput = createFormInput("admin@eventxpert.com"); emailInput.setDisable(true);
        TextField phoneInput = createFormInput("+91 98765 43210");
        TextField orgInput = createFormInput("EventXpert");

        ComboBox<String> roleCB = new ComboBox<>();
        roleCB.getItems().addAll("Lead Organizer", "Event Manager", "Coordinator");
        roleCB.setValue("Lead Organizer");
        roleCB.setMaxWidth(Double.MAX_VALUE);

        TextField locInput = createFormInput("Pune, India");

        // Mark unsaved on edit
        nameInput.textProperty().addListener((o, oldVal, newVal) -> setUnsaved(true));
        phoneInput.textProperty().addListener((o, oldVal, newVal) -> setUnsaved(true));

        grid.add(createFieldBox("Full Name", nameInput), 0, 0);
        grid.add(createFieldBox("Email Address 🔒", emailInput), 1, 0);
        grid.add(createFieldBox("Phone Number", phoneInput), 0, 1);
        grid.add(createFieldBox("Organization", orgInput), 1, 1);
        grid.add(createFieldBox("Role", roleCB), 0, 2);
        grid.add(createFieldBox("Location", locInput), 1, 2);

        // Action Buttons
        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> setUnsaved(false));

        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            setUnsaved(false);
            showToast(stage, "✓ Profile information updated successfully!");
        });

        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(headerGroup, new Separator(), photoBox, new Separator(), grid, new Separator(), btnRow);
        contentHolder.getChildren().add(card);
    }

    private VBox createFieldBox(String labelStr, Control inputControl) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelStr);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        inputControl.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 12;");
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private TextField createFormInput(String text) {
        return new TextField(text);
    }

    // ==========================================
    // 6. OTHER SETTINGS SECTIONS
    // ==========================================
    private void showSecuritySettings(Stage stage) {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("🛡 Security Settings");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Button passBtn = createPrimaryButton("Change Password");
        passBtn.setOnAction(e -> openPasswordModal(stage));

        CheckBox twoFA = new CheckBox("Enable Two-Factor Authentication (2FA)");
        CheckBox loginNotif = new CheckBox("Send Email Alerts on New Login"); loginNotif.setSelected(true);

        card.getChildren().addAll(title, new Separator(), passBtn, twoFA, loginNotif);
        contentHolder.getChildren().add(card);
    }

    private void showNotificationSettings(Stage stage) {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("🔔 Notification Preferences");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        CheckBox c1 = new CheckBox("Event Requests & Registrations"); c1.setSelected(true);
        CheckBox c2 = new CheckBox("Schedule Conflicts & Resource Overlaps"); c2.setSelected(true);
        CheckBox c3 = new CheckBox("Vendor Contract & Payment Alerts"); c3.setSelected(true);
        CheckBox c4 = new CheckBox("Weather Warning Alerts"); c4.setSelected(true);

        Button saveBtn = createPrimaryButton("Save Preferences");
        saveBtn.setOnAction(e -> showToast(stage, "✓ Notification preferences updated."));

        card.getChildren().addAll(title, new Separator(), c1, c2, c3, c4, new Separator(), saveBtn);
        contentHolder.getChildren().add(card);
    }

    private void showPreferencesSettings(Stage stage) {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("⚙ Event Defaults");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        ComboBox<String> currCB = new ComboBox<>();
        currCB.getItems().addAll("INR (₹)", "USD ($)", "EUR (€)");
        currCB.setValue("INR (₹)");

        CheckBox aiToggle = new CheckBox("Enable AI Venue & Vendor Matching Recommendations");
        aiToggle.setSelected(true);

        Button saveBtn = createPrimaryButton("Save Preferences");
        saveBtn.setOnAction(e -> showToast(stage, "✓ Event preferences saved."));

        card.getChildren().addAll(title, new Separator(), new Label("Default Currency:"), currCB, aiToggle, new Separator(), saveBtn);
        contentHolder.getChildren().add(card);
    }

    private void showAppearanceSettings(Stage stage) {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("🎨 Appearance Settings");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        RadioButton rLight = new RadioButton("Light Theme (Default)"); rLight.setSelected(true);
        RadioButton rDark = new RadioButton("Dark Theme");
        ToggleGroup tg = new ToggleGroup();
        rLight.setToggleGroup(tg); rDark.setToggleGroup(tg);

        CheckBox compact = new CheckBox("Compact Dashboard Layout");

        Button saveBtn = createPrimaryButton("Apply Theme");
        saveBtn.setOnAction(e -> showToast(stage, "Theme preference saved."));

        card.getChildren().addAll(title, new Separator(), rLight, rDark, compact, new Separator(), saveBtn);
        contentHolder.getChildren().add(card);
    }

    private void showSystemSettings(Stage stage) {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("💻 System & Data");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        CheckBox autoSave = new CheckBox("Auto-Save Draft Changes (5 min)"); autoSave.setSelected(true);

        VBox dangerZone = new VBox(8);
        dangerZone.setPadding(new Insets(12));
        dangerZone.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 8px; -fx-border-color: #FCA5A5; -fx-border-radius: 8px;");

        Label dTitle = new Label("🚨 Danger Zone"); dTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: " + DANGER_TEXT + ";");
        Button delBtn = new Button("Delete Organizer Account");
        delBtn.setStyle("-fx-background-color: " + DANGER_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        delBtn.setOnAction(e -> openDeleteModal(stage));

        dangerZone.getChildren().addAll(dTitle, delBtn);

        card.getChildren().addAll(title, autoSave, new Separator(), dangerZone);
        contentHolder.getChildren().add(card);
    }

    // ==========================================
    // 7. UNSAVED CHANGES BOTTOM BAR
    // ==========================================
    private HBox createUnsavedChangesBar(Stage stage) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Circle dot = new Circle(5, Color.web(PRIMARY_BLUE));
        Label msg = new Label("You have unsaved changes");
        msg.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button discardBtn = createSecondaryButton("Discard");
        discardBtn.setOnAction(e -> setUnsaved(false));

        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            setUnsaved(false);
            showToast(stage, "✓ All settings saved successfully!");
        });

        bar.getChildren().addAll(dot, msg, sp, discardBtn, saveBtn);
        bar.setVisible(false);
        bar.setManaged(false);
        return bar;
    }

    private void setUnsaved(boolean value) {
        hasUnsavedChanges = value;
        unsavedBar.setVisible(value);
        unsavedBar.setManaged(value);
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openPasswordModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Change Password");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("🔒 Change Password");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        PasswordField oldP = new PasswordField(); oldP.setPromptText("Current Password");
        PasswordField newP = new PasswordField(); newP.setPromptText("New Password");

        Button saveBtn = createPrimaryButton("Update Password");
        saveBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Password updated successfully!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Current:"), oldP, new Label("New:"), newP, saveBtn);

        Scene scene = new Scene(layout, 340, 260);
        modal.setScene(scene);
        modal.show();
    }

    private void openDeleteModal(Stage parentStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Delete Organizer Account?");
        alert.setContentText("Are you sure? This action cannot be undone.");
        alert.initOwner(parentStage);
        alert.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 7 16; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Settings");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}