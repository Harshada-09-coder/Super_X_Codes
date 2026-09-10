package com.techtrio.view;

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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class InactiveOrgDash {

    private Scene inactiveOrganizerScene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#64748B";

    private Label welcomeHeaderLabel;
    private Label sidebarNameLabel;

    public InactiveOrgDash() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public InactiveOrgDash(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getinactiveOrganizerScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Sidebar (Standard Theme)
        VBox side = createSidebar();
        root.setLeft(side);

        // 2. Main Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Welcome Box
        welcomeHeaderLabel = new Label("Good Morning, Organizer 👋");
        welcomeHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        welcomeHeaderLabel.setTextFill(Color.web(NAVY));

        Label badge = new Label("● INACTIVE ACCOUNT");
        badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 12px;");

        HBox titleRow = new HBox(10, welcomeHeaderLabel, badge);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label subtitle = new Label("Your organizer account is pending profile activation. Complete activation to unlock event requests and booking management.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox welcomeText = new VBox(4, titleRow, subtitle);

        // Attention Banner Card
        VBox inactiveCard = new VBox(14);
        inactiveCard.setPadding(new Insets(20));
        inactiveCard.setStyle("-fx-background-color: white; -fx-border-color: #FCA5A5; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox bannerTop = new HBox(14);
        bannerTop.setAlignment(Pos.CENTER_LEFT);

        StackPane warningCircle = new StackPane();
        Circle c = new Circle(22, Color.web("#FEE2E2"));
        Label wIcon = new Label("⚠");
        wIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: #DC2626; -fx-font-weight: bold;");
        warningCircle.getChildren().addAll(c, wIcon);

        VBox bannerInfo = new VBox(4);
        Label bannerHeading = new Label("Organizer Profile Activation Required");
        bannerHeading.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        bannerHeading.setTextFill(Color.web("#B91C1C"));

        Label bannerDesc = new Label("Your profile needs business information before customer event requests and schedule tools can be assigned to you.");
        bannerDesc.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + TEXT_MAIN + ";");
        bannerInfo.getChildren().addAll(bannerHeading, bannerDesc);

        Region bSp = new Region();
        HBox.setHgrow(bSp, Priority.ALWAYS);

        Button actBtn = new Button("Complete Profile & Activate →");
        actBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9 18; -fx-background-radius: 6px; -fx-cursor: hand;");
        actBtn.setOnAction(e -> openProfile());

        bannerTop.getChildren().addAll(warningCircle, bannerInfo, bSp, actBtn);
        inactiveCard.getChildren().add(bannerTop);

        // Summary Cards (Locked / 0)
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
                createCard("Assigned Requests", "00", "fas-inbox", "#D97706"),
                createCard("My Active Events", "00", "fas-calendar-check", "#16A34A"),
                createCard("Upcoming Events", "00", "fas-calendar-alt", PRIMARY_BLUE),
                createCard("Tasks & Milestones", "00", "fas-tasks", "#7C3AED")
        );

        content.getChildren().addAll(welcomeText, inactiveCard, stats);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        root.setCenter(scroll);

        loadOrganizerProfileAsync();

        inactiveOrganizerScene = new Scene(root, 1550, 785);
        return inactiveOrganizerScene;
    }

    private VBox createCard(String title, String val, String iconLit, String iconColorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER_COLOR + ";-fx-border-radius:10;-fx-background-radius:10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = new StackPane();
        Circle c = new Circle(14, Color.web(iconColorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(13);
        fontIcon.setIconColor(Color.web(iconColorHex));
        iconCircle.getChildren().addAll(c, fontIcon);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #64748B;");
        top.getChildren().addAll(iconCircle, titleLabel);

        Label valueLabel = new Label(val);
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: " + NAVY + ";");

        card.getChildren().addAll(top, valueLabel);
        return card;
    }

    private VBox createSidebar() {
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
        sidebarNameLabel = new Label("EventXpert");
        sidebarNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL (INACTIVE)");
        portal.setStyle("-fx-text-fill: #FCA5A5; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(sidebarNameLabel, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        menu.getChildren().addAll(
                createSidebarButton("Dashboard", "fas-tachometer-alt", true, () -> {}),
                createSidebarButton("Event Requests", "fas-inbox", false, this::openProfile),
                createSidebarButton("My Events", "fas-calendar-check", false, this::openProfile),
                createSidebarButton("Schedule", "fas-calendar-alt", false, this::openProfile)
        );

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        Button activateBottom = new Button("⚡ Activate Profile");
        activateBottom.setMaxWidth(Double.MAX_VALUE);
        activateBottom.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9; -fx-background-radius: 6; -fx-cursor: hand;");
        activateBottom.setOnAction(e -> openProfile());

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                activateBottom,
                new Region(),
                createSidebarButton("Notifications", "fas-bell", false, this::openNotifications),
                createSidebarButton("Profile", "fas-user", false, this::openProfile),
                createSidebarButton("Settings", "fas-cog", false, this::openSettings)
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
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
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
            OrganizerProfile profile = task.getValue();
            if (profile != null) {
                Platform.runLater(() -> {
                    String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Organizer";
                    if (welcomeHeaderLabel != null) welcomeHeaderLabel.setText("Good Morning, " + name + " 👋");
                    if (sidebarNameLabel != null && profile.getOrganizationName() != null && !profile.getOrganizationName().isBlank()) {
                        sidebarNameLabel.setText(profile.getOrganizationName());
                    }
                });
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void openProfile() {
        ProfileO profilePage = new ProfileO(uid, idToken);
        profilePage.setInactiveOrgDash(this);
        LandingPage.landingPageStage.setScene(profilePage.getProfileScene());
    }

    private void openNotifications() {
        NotificationsO view = new NotificationsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getNotificationScene());
    }

    private void openSettings() {
        SettingsO view = new SettingsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getSettingScene(this::backToInactiveOrganizerDashboard));
    }

    public void backToInactiveOrganizerDashboard() {
        if (inactiveOrganizerScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(inactiveOrganizerScene);
        }
    }
}