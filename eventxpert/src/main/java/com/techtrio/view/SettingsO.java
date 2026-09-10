package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.controller.OrganizerController;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SettingsO {

    private final String uid;
    private final String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String BORDER_COLOR = "#DCE5F0";

    private StackPane rootLayer;

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

    public SettingsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public SettingsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getSettingScene(Runnable backAction) {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Matched Dashboard Sidebar (No TopBar)
        root.setLeft(createCommonOrganizerSidebar("Settings"));

        // 2. Main Scroll Content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        VBox headerBox = createPageHeader();

        // Split Layout: Nav Card + Content Card
        HBox settingsSplit = new HBox(20);

        VBox navCard = createSettingsNavCard();
        navCard.setPrefWidth(220);

        contentHolder = new VBox();
        HBox.setHgrow(contentHolder, Priority.ALWAYS);

        showProfileSettings();

        settingsSplit.getChildren().addAll(navCard, contentHolder);
        mainContent.getChildren().addAll(headerBox, settingsSplit);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        root.setCenter(scrollPane);
        rootLayer.getChildren().add(root);

        loadOrganizerDataAsync();

        return new Scene(rootLayer, 1550, 785);
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
                    if (nameInput != null) nameInput.setText(p.getName() != null ? p.getName() : "");
                    if (emailInput != null) emailInput.setText(p.getEmail() != null ? p.getEmail() : "");
                    if (phoneInput != null) phoneInput.setText(p.getMobileNo() != null ? p.getMobileNo() : "");
                    if (orgInput != null) orgInput.setText(p.getOrganizationName() != null ? p.getOrganizationName() : "");
                    if (locationInput != null) locationInput.setText(p.getAddress() != null ? p.getAddress() : "");
                });
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createPageHeader() {
        VBox headerBox = new VBox(4);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Portal Settings & Preferences");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Dashboard");
        backBtn.setOnAction(e -> returnToDashboard());

        topRow.getChildren().addAll(title, spacer, backBtn);

        Label subtitle = new Label("Manage your organizer configurations, notification channels, security and preferences.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headerBox.getChildren().addAll(topRow, subtitle);
        return headerBox;
    }

    private VBox createSettingsNavCard() {
        VBox card = createCardContainer();
        card.setPadding(new Insets(12));
        card.setSpacing(4);

        btnProfileMenu = createNavButton("👤  Profile Details", true);
        btnSecurityMenu = createNavButton("🛡  Security & 2FA", false);
        btnNotifMenu = createNavButton("🔔  Notifications", false);
        btnPrefMenu = createNavButton("⚙  System Preferences", false);

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

        Label title = new Label("Profile Information");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label sub = new Label("Update your personal identity, contact numbers and registered business name.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);

        nameInput = new TextField();
        emailInput = new TextField(); emailInput.setDisable(true);
        phoneInput = new TextField();
        orgInput = new TextField();

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Lead Organizer", "Event Planner", "Coordinator");
        roleCombo.setValue("Lead Organizer");
        roleCombo.setMaxWidth(Double.MAX_VALUE);

        locationInput = new TextField();

        grid.add(createFieldBox("Full Name", nameInput), 0, 0);
        grid.add(createFieldBox("Registered Email 🔒", emailInput), 1, 0);
        grid.add(createFieldBox("Mobile Contact", phoneInput), 0, 1);
        grid.add(createFieldBox("Organization", orgInput), 1, 1);
        grid.add(createFieldBox("Designation", roleCombo), 0, 2);
        grid.add(createFieldBox("Location / Address", locationInput), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = createPrimaryButton("Save Configuration");
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
                saveBtn.setText("Save Configuration");
                showInAppModal("Settings Saved", "Your portal preferences have been successfully updated.");
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        btnRow.getChildren().add(saveBtn);
        card.getChildren().addAll(title, sub, new Separator(), grid, new Separator(), btnRow);
        contentHolder.getChildren().add(card);
    }

    private void showSecuritySettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("Security & Authentication");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        CheckBox twoFa = new CheckBox("Enforce Two-Factor Authentication (2FA)");
        twoFa.setSelected(true);

        CheckBox emailAlert = new CheckBox("Enable New Device Login Email Alerts");
        emailAlert.setSelected(true);

        VBox c = new VBox(12, twoFa, emailAlert);
        card.getChildren().addAll(title, new Separator(), c);
        contentHolder.getChildren().add(card);
    }

    private void showNotificationSettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("Notification Preferences");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        CheckBox notif1 = new CheckBox("Instant alert on Customer Event Request Assignment");
        notif1.setSelected(true);

        CheckBox notif2 = new CheckBox("Receive SMS notifications for schedule updates");
        notif2.setSelected(false);

        VBox c = new VBox(12, notif1, notif2);
        card.getChildren().addAll(title, new Separator(), c);
        contentHolder.getChildren().add(card);
    }

    private void showPreferencesSettings() {
        contentHolder.getChildren().clear();
        VBox card = createCardContainer();

        Label title = new Label("System Preferences");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label cur = new Label("Default Currency: Indian Rupee (INR ₹)");
        cur.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");

        Label tz = new Label("System Timezone: Asia/Kolkata (IST)");
        tz.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");

        VBox c = new VBox(8, cur, tz);
        card.getChildren().addAll(title, new Separator(), c);
        contentHolder.getChildren().add(card);
    }

    private void showInAppModal(String title, String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private VBox createFieldBox(String labelStr, Control inputControl) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelStr);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        inputControl.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 10;");
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private void returnToDashboard() {
        OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
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

    /* ================= MATCHED ORGANIZER SIDEBAR ================= */
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
                createSidebarButton("Settings", "fas-cog", activeTab.equals("Settings"), () -> handleNav("Settings")),
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
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6px;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6px;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard" -> returnToDashboard();
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> {
                MyEventsO myEvents = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(myEvents.getMyEventsOScene(null));
            }
            case "Schedule" -> {
                ScheduleO sched = new ScheduleO(uid, idToken);
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
            }
            case "Notifications" -> {
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
            }
            case "Profile" -> {
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
            }
            case "Settings" -> {}
            case "Support" -> {
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
            }
        }
    }
}