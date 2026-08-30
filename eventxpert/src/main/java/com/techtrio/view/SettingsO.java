package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.controller.OrganizerController;
import com.techtrio.model.OrganizerProfile;
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

public class SettingsO {

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    private final String uid;
    private final String idToken;

    private VBox contentHolder;
    private Button btnProfileMenu;
    private Button btnSecurityMenu;
    private Button btnNotifMenu;
    private Button btnPrefMenu;

    private TextField nameInput;
    private TextField emailInput;
    private TextField phoneInput;
    private TextField orgInput;
    private ComboBox<String> roleCombo;
    private TextField locationInput;

    private VBox unsavedBar;

    public SettingsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public SettingsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getSettingScene(Runnable backAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setLeft(createSidebar());
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 30, 30, 30));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        VBox headerBox = createPageHeader(backAction);

        // Split Layout: Nav Menu Card + Profile Information Card
        HBox settingsSplit = new HBox(20);

        VBox navCard = createSettingsNavCard();
        navCard.setPrefWidth(220);

        contentHolder = new VBox();
        HBox.setHgrow(contentHolder, Priority.ALWAYS);

        showProfileSettings();

        settingsSplit.getChildren().addAll(navCard, contentHolder);

        // Bottom Unsaved Changes Banner
        unsavedBar = createUnsavedChangesBar();

        mainContent.getChildren().addAll(headerBox, settingsSplit, unsavedBar);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadOrganizerDataAsync();

        return new Scene(root, 1550, 785);
    }

    private void loadOrganizerDataAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<OrganizerProfile> task = new Task<>() {
            @Override
            protected OrganizerProfile call() {
                OrganizerController controller = new OrganizerController();
                return controller.getOrganizerProfile(uid);
            }
        };

        task.setOnSucceeded(e -> {
            OrganizerProfile p = task.getValue();
            if (p != null) {
                Platform.runLater(() -> {
                    if (nameInput != null) nameInput.setText(p.getName() != null ? p.getName() : "Admin User");
                    if (emailInput != null) emailInput.setText(p.getEmail() != null ? p.getEmail() : "admin@eventxpert.com");
                    if (phoneInput != null) phoneInput.setText(p.getMobileNo() != null ? p.getMobileNo() : "+91 98765 43210");
                    if (orgInput != null) orgInput.setText(p.getOrganizationName() != null ? p.getOrganizationName() : "EventXpert");
                    if (locationInput != null) locationInput.setText(p.getAddress() != null ? p.getAddress() : "Pune, India");
                });
            }
        });

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

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px;");

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search event data...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightNav = new HBox(15);
        rightNav.setAlignment(Pos.CENTER);

        Button bellBtn = new Button("🔔"); bellBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button msgBtn = new Button("✉️"); msgBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button helpBtn = new Button("❓"); helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));

        rightNav.getChildren().addAll(bellBtn, msgBtn, helpBtn, avatar);
        topBar.getChildren().addAll(searchBox, spacer, rightNav);
        return topBar;
    }

    private VBox createPageHeader(Runnable backAction) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("Home  >  Settings");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Dashboard");
        backBtn.setOnAction(e -> {
            if (backAction != null) backAction.run();
            else returnToDashboard();
        });

        topRow.getChildren().addAll(title, spacer, backBtn);

        Label subtitle = new Label("Manage your account, preferences, notifications and organizer portal settings to tailor EventXpert to your workflow.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, topRow, subtitle);
        return headerBox;
    }

    private VBox createSettingsNavCard() {
        VBox card = createCardContainer();
        card.setPadding(new Insets(12));
        card.setSpacing(4);

        btnProfileMenu = createNavButton("👤  Profile", true);
        btnSecurityMenu = createNavButton("🛡  Security", false);
        btnNotifMenu = createNavButton("🔔  Notifications", false);
        btnPrefMenu = createNavButton("⚙  Preferences", false);

        btnProfileMenu.setOnAction(e -> { setActiveNav(btnProfileMenu); showProfileSettings(); });
        btnSecurityMenu.setOnAction(e -> { setActiveNav(btnSecurityMenu); showSecuritySettings(); });
        btnNotifMenu.setOnAction(e -> { setActiveNav(btnNotifMenu); showNotificationSettings(); });
        btnPrefMenu.setOnAction(e -> { setActiveNav(btnPrefMenu); showPreferencesSettings(); });

        card.getChildren().addAll(btnProfileMenu, btnSecurityMenu, btnNotifMenu, btnPrefMenu);
        return card;
    }

    private Button createNavButton(String title, boolean isActive) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(isActive ? "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 10 14;"
                : "-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");
        return btn;
    }

    private void setActiveNav(Button activeBtn) {
        btnProfileMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");
        btnSecurityMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");
        btnNotifMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");
        btnPrefMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 10 14; -fx-cursor: hand;");

        activeBtn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 10 14;");
    }

    private void showProfileSettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("👤 Profile Information");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label sub = new Label("Update your photo and personal details here.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        // Photo Box
        HBox photoRow = new HBox(16);
        photoRow.setAlignment(Pos.CENTER_LEFT);
        photoRow.setPadding(new Insets(10, 0, 10, 0));

        Circle avatarCircle = new Circle(28, Color.web(PRIMARY_BLUE));
        VBox photoActions = new VBox(4);
        Button changePhotoBtn = createSecondaryButton("Change Photo");
        Label photoHint = new Label("JPG, GIF or PNG. Max size of 800K");
        photoHint.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        photoActions.getChildren().addAll(changePhotoBtn, photoHint);

        photoRow.getChildren().addAll(avatarCircle, photoActions);

        // Form Fields (2-Column Grid)
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);

        nameInput = new TextField("Admin User");
        emailInput = new TextField("admin@eventxpert.com"); emailInput.setDisable(true);
        phoneInput = new TextField("+91 98765 43210");
        orgInput = new TextField("EventXpert");

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Lead Organizer", "Event Manager", "Coordinator");
        roleCombo.setValue("Lead Organizer");
        roleCombo.setMaxWidth(Double.MAX_VALUE);

        locationInput = new TextField("Pune, India");

        grid.add(createFieldBox("Full Name", nameInput), 0, 0);
        grid.add(createFieldBox("Email Address 🔒", emailInput), 1, 0);
        grid.add(createFieldBox("Phone Number", phoneInput), 0, 1);
        grid.add(createFieldBox("Organization", orgInput), 1, 1);
        grid.add(createFieldBox("Role", roleCombo), 0, 2);
        grid.add(createFieldBox("Location", locationInput), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = createSecondaryButton("Cancel");
        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            String newName = nameInput.getText().trim();
            String newPhone = phoneInput.getText().trim();
            String newOrg = orgInput.getText().trim();
            String newLoc = locationInput.getText().trim();

            saveBtn.setDisable(true);
            saveBtn.setText("Saving...");

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("mobileNo", newPhone);
            updates.put("organizationName", newOrg);
            updates.put("address", newLoc);

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    OrganizerController controller = new OrganizerController();
                    return controller.updateOrganizerProfileFields(uid, updates);
                }
            };

            task.setOnSucceeded(evt -> {
                saveBtn.setDisable(false);
                saveBtn.setText("Save Changes");
                if (unsavedBar != null) {
                    unsavedBar.setVisible(false);
                    unsavedBar.setManaged(false);
                }
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        btnRow.getChildren().addAll(cancelBtn, saveBtn);
        card.getChildren().addAll(title, sub, new Separator(), photoRow, new Separator(), grid, new Separator(), btnRow);
        contentHolder.getChildren().add(card);
    }

    private void showSecuritySettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();
        card.getChildren().addAll(new Label("🛡 Security Options"), new CheckBox("Enable Email Login Alerts"));
        contentHolder.getChildren().add(card);
    }

    private void showNotificationSettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();
        card.getChildren().addAll(new Label("🔔 Notification Preferences"), new CheckBox("Email alerts for new requests"));
        contentHolder.getChildren().add(card);
    }

    private void showPreferencesSettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();
        card.getChildren().addAll(new Label("⚙ Preferences"), new Label("Default Currency: INR (₹)"));
        contentHolder.getChildren().add(card);
    }

    private VBox createUnsavedChangesBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label msg = new Label("● You have unsaved changes");
        msg.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + BRIGHT_BLUE + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button discardBtn = createSecondaryButton("Discard");
        Button saveBtn = createPrimaryButton("💾 Save");

        bar.getChildren().addAll(msg, sp, discardBtn, saveBtn);
        VBox box = new VBox(bar);
        box.setVisible(false); box.setManaged(false);
        return box;
    }

    private VBox createFieldBox(String labelStr, Control inputControl) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelStr);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        inputControl.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 10;");
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, this::returnToDashboard));
        menu.getChildren().add(createNavBtn("Settings", true, () -> {}));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer);
        return sidebar;
    }

    private Button createNavBtn(String title, boolean active, Runnable action) {
        Button b = new Button(title);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(active ? "-fx-background-color: #EAF3FF; -fx-text-fill: #0B4EA2; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6px;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) b.setOnAction(e -> action.run());
        return b;
    }

    private void returnToDashboard() {
        OrganizerController controller = new OrganizerController();
        OrganizerProfile profile = controller.getOrganizerProfile(uid);

        if (profile != null && profile.isProfileCompleted()) {
            OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
            LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
        } else {
            InactiveOrgDash inactiveDash = new InactiveOrgDash(uid, idToken);
            LandingPage.landingPageStage.setScene(inactiveDash.getinactiveOrganizerScene());
        }
    }

    private VBox createCardContainer() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        return btn;
    }
}