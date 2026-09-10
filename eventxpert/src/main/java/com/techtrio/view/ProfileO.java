package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.controller.OrganizerController;
import com.techtrio.controller.OrganizerState;
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
import javafx.stage.Stage;

public class ProfileO {

    private Scene profileOScene;
    private OrganizerDashboardO activeDashboard;
    private InactiveOrgDash inactiveDashboard;
    private final String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String BORDER_COLOR = "#DCE5F0";

    private StackPane rootLayer;

    private Label heroAvatarText;
    private Label heroNameLabel;
    private Label heroRoleLabel;
    private Label heroOrgLabel;

    private TextField nameInput;
    private TextField phoneInput;
    private TextField emailInput;
    private TextField locationInput;
    private TextField orgNameInput;

    public ProfileO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ProfileO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public void setOrganizerDashboard(OrganizerDashboardO dashboard) {
        this.activeDashboard = dashboard;
    }

    public void setInactiveOrgDash(InactiveOrgDash dashboard) {
        this.inactiveDashboard = dashboard;
    }

    public Scene getProfileScene() {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Matched Dashboard Sidebar (No TopBar)
        root.setLeft(createCommonOrganizerSidebar("Profile"));

        // 2. Main Scroll Content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createHeroProfileCard(),
                createPersonalInfoCard()
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        root.setCenter(scrollPane);
        rootLayer.getChildren().add(root);

        loadOrganizerProfileAsync();

        profileOScene = new Scene(rootLayer, 1550, 785);
        return profileOScene;
    }

    private void loadOrganizerProfileAsync() {
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
                    String name = p.getName() != null && !p.getName().isBlank() ? p.getName() : "Organizer";
                    String org = p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : "Independent Organizer";
                    String phone = p.getMobileNo() != null && !p.getMobileNo().isBlank() ? p.getMobileNo() : "";
                    String email = p.getEmail() != null && !p.getEmail().isBlank() ? p.getEmail() : "";
                    String loc = p.getAddress() != null && !p.getAddress().isBlank() ? p.getAddress() : "Pune, Maharashtra";

                    if (heroNameLabel != null) heroNameLabel.setText(name);
                    if (heroAvatarText != null) heroAvatarText.setText(getInitials(name));
                    if (heroRoleLabel != null) heroRoleLabel.setText("Event Organizer • " + org);
                    if (heroOrgLabel != null) heroOrgLabel.setText("📍 " + loc + "  |  ✉ " + email + "  |  📞 " + (phone.isEmpty() ? "N/A" : phone));

                    if (nameInput != null) nameInput.setText(name);
                    if (phoneInput != null) phoneInput.setText(phone);
                    if (emailInput != null) emailInput.setText(email);
                    if (locationInput != null) locationInput.setText(loc);
                    if (orgNameInput != null) orgNameInput.setText(org);
                });
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createPageHeader() {
        VBox headerBox = new VBox(4);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Organizer Profile");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Dashboard");
        backBtn.setOnAction(e -> navigateBackToDashboard());

        titleRow.getChildren().addAll(title, spacer, backBtn);

        Label subtitle = new Label("View and update your personal credentials, organizational branding and contact details.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headerBox.getChildren().addAll(titleRow, subtitle);
        return headerBox;
    }

    private VBox createHeroProfileCard() {
        VBox card = createCardContainer();
        HBox heroRow = new HBox(18);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        heroAvatarText = new Label("OR");
        heroAvatarText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        heroAvatarText.setAlignment(Pos.CENTER);
        heroAvatarText.setPrefSize(72, 72);
        heroAvatarText.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 50; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; -fx-font-size: 22px;");

        VBox infoBox = new VBox(4);
        heroNameLabel = new Label("Loading Name...");
        heroNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        heroRoleLabel = new Label("Event Organizer");
        heroRoleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        heroOrgLabel = new Label("Loading live profile records...");
        heroOrgLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        infoBox.getChildren().addAll(heroNameLabel, heroRoleLabel, heroOrgLabel);
        heroRow.getChildren().addAll(heroAvatarText, infoBox);
        card.getChildren().add(heroRow);
        return card;
    }

    private VBox createPersonalInfoCard() {
        VBox card = createCardContainer();
        Label title = new Label("Personal & Organization Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(12);

        nameInput = new TextField();
        phoneInput = new TextField();
        emailInput = new TextField();
        emailInput.setDisable(true); // Locked for security
        locationInput = new TextField();
        orgNameInput = new TextField();

        form.add(createFormField("Full Name", nameInput), 0, 0);
        form.add(createFormField("Email Address 🔒", emailInput), 1, 0);
        form.add(createFormField("Phone Number", phoneInput), 0, 1);
        form.add(createFormField("Organization Name", orgNameInput), 1, 1);
        form.add(createFormField("Location / Address", locationInput), 0, 2);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        form.getColumnConstraints().addAll(col1, col2);

        Button saveBtn = createPrimaryButton("Save Profile Changes");
        saveBtn.setOnAction(e -> {
            String newName = nameInput.getText().trim();
            String newPhone = phoneInput.getText().trim();
            String newLoc = locationInput.getText().trim();
            String newOrg = orgNameInput.getText().trim();

            saveBtn.setDisable(true);
            saveBtn.setText("Saving...");

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("mobileNo", newPhone);
            updates.put("address", newLoc);
            updates.put("organizationName", newOrg);

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    OrganizerController controller = new OrganizerController();
                    boolean updated = controller.updateOrganizerProfileFields(uid, updates);
                    if (updated) {
                        controller.markOrganizerAsExisting(uid);
                    }
                    return updated;
                }
            };

            task.setOnSucceeded(evt -> {
                boolean success = task.getValue();
                saveBtn.setDisable(false);
                saveBtn.setText("Save Profile Changes");

                if (success) {
                    heroNameLabel.setText(newName);
                    heroAvatarText.setText(getInitials(newName));
                    heroRoleLabel.setText("Event Organizer • " + newOrg);
                    heroOrgLabel.setText("📍 " + newLoc + "  |  ✉ " + emailInput.getText() + "  |  📞 " + newPhone);
                    showInAppModal("Profile Updated", "Your changes have been successfully saved to Firestore.");
                }
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        HBox btnRow = new HBox(saveBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, new Separator(), form, new Separator(), btnRow);
        return card;
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

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "OR";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private VBox createFormField(String labelStr, Control control) {
        VBox box = new VBox(4);
        Label lbl = new Label(labelStr);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        control.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 10;");
        box.getChildren().addAll(lbl, control);
        return box;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
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

    private void navigateBackToDashboard() {
        if (activeDashboard != null) {
            activeDashboard.backToOrganizerDashboard();
        } else if (inactiveDashboard != null) {
            inactiveDashboard.backToInactiveOrganizerDashboard();
        } else {
            OrganizerController controller = new OrganizerController();
            OrganizerState state = controller.getOrganizerState(uid, idToken);
            if (state != null && state.isProfileCompleted()) {
                OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
            } else {
                InactiveOrgDash inactDash = new InactiveOrgDash(uid, idToken);
                LandingPage.landingPageStage.setScene(inactDash.getinactiveOrganizerScene());
            }
        }
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
                createSidebarButton("Profile", "fas-user", activeTab.equals("Profile"), () -> handleNav("Profile")),
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
            case "Dashboard" -> navigateBackToDashboard();
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
            case "Profile" -> {}
            case "Settings" -> {
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
            }
            case "Support" -> {
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
            }
        }
    }
}