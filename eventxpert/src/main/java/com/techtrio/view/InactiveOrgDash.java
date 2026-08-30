package com.techtrio.view;

import com.techtrio.controller.OrganizerController;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class InactiveOrgDash {

    private Scene inactiveOrganizerScene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String BLUE = "#082F70";
    private static final String BG = "#F6F8FC";
    private static final String BORDER = "#DDE2EA";
    private static final String TEXT = "#17191D";
    private static final String MUTED = "#737985";

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
        root.setStyle("-fx-background-color:" + BG + ";");

        // SIDEBAR
        VBox side = new VBox(7);
        side.setPrefWidth(235);
        side.setPadding(new Insets(20, 9, 16, 9));
        side.setStyle("-fx-background-color:" + NAVY + ";");

        HBox logo = new HBox(11);
        logo.setAlignment(Pos.CENTER_LEFT);

        Label ex = new Label("EX");
        ex.setMinSize(42, 42);
        ex.setAlignment(Pos.CENTER);
        ex.setStyle("-fx-background-color:white;-fx-text-fill:" + NAVY + ";-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8px;");

        sidebarNameLabel = txt("EventXpert", 19, Color.WHITE, true);

        VBox brand = new VBox(1);
        brand.getChildren().addAll(
                sidebarNameLabel,
                txt("ORGANIZER PORTAL", 10, Color.web("#A9B7D0"), false)
        );
        logo.getChildren().addAll(ex, brand);

        side.getChildren().addAll(
                logo, gap(22),
                nav("▦   Dashboard", true, () -> {}),
                nav("▣   Event Requests", false, this::openProfile),
                nav("▣   My Events", false, this::openProfile),
                nav("⌖   Venue", false, this::openProfile),
                nav("♧   Vendors", false, this::openProfile),
                nav("▤   Schedule", false, this::openProfile),
                gap(8),
                section("ACCOUNT"),
                nav("♧   Notifications", false, this::openNotifications),
                nav("♙   Profile & Settings", false, this::openProfile),
                nav("?   Settings", false, this::openSettings)
        );

        Region sideGrow = new Region();
        VBox.setVgrow(sideGrow, Priority.ALWAYS);

        Button newEvent = new Button("＋   Activate Account");
        newEvent.setMaxWidth(Double.MAX_VALUE);
        newEvent.setPrefHeight(47);
        newEvent.setStyle("-fx-background-color:#0B4EA2;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8px;-fx-cursor:hand;");
        newEvent.setOnAction(e -> openProfile());

        side.getChildren().addAll(sideGrow, newEvent);
        root.setLeft(side);

        // TOP BAR
        VBox main = new VBox();
        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(8, 20, 8, 22));
        top.setPrefHeight(58);
        top.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");

        TextField search = new TextField();
        search.setPromptText("Search events, vendors, guests...");
        search.setPrefWidth(500);
        search.setPrefHeight(40);
        search.setStyle("-fx-background-color:white;-fx-border-color:#D9DEE7;-fx-border-radius:8px;-fx-background-radius:8px;-fx-font-size:14px;");

        Region topGrow = new Region();
        HBox.setHgrow(topGrow, Priority.ALWAYS);

        Button bell = topBtn("♧");
        bell.setOnAction(e -> openNotifications());
        Button settings = topBtn("⚙");
        settings.setOnAction(e -> openSettings());

        Label person = txt("♙  Profile", 14, Color.web(TEXT), true);
        person.setStyle("-fx-cursor:hand;");
        person.setOnMouseClicked(e -> openProfile());

        top.getChildren().addAll(search, topGrow, bell, settings, person);
        main.getChildren().add(top);

        // CONTENT
        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 22, 22, 22));

        HBox welcome = new HBox(12);
        welcome.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(3);
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        welcomeHeaderLabel = txt("Good Morning 👋", 31, Color.web(TEXT), true);

        titleRow.getChildren().addAll(
                welcomeHeaderLabel,
                badge("●  INACTIVE")
        );

        welcomeText.getChildren().addAll(
                titleRow,
                txt("Your organizer account is currently inactive. Activate your account to start managing events.", 14, Color.web("#343841"), false)
        );
        welcome.getChildren().add(welcomeText);

        // INACTIVE CARD
        HBox inactive = new HBox(22);
        inactive.setAlignment(Pos.CENTER_LEFT);
        inactive.setPadding(new Insets(22));
        inactive.setMinHeight(140);
        inactive.setStyle("-fx-background-color:white;-fx-border-color:#FFD0CC;-fx-border-radius:11px;-fx-background-radius:11px;");

        StackPane warning = new StackPane(txt("⚠", 27, Color.web("#B51D1D"), true));
        warning.setMinSize(62, 62);
        warning.setStyle("-fx-background-color:#FFE1DE;-fx-background-radius:50%;");

        VBox inactiveInfo = new VBox(7);
        inactiveInfo.getChildren().addAll(
                txt("Organizer Account Inactive", 24, Color.web(TEXT), true),
                txt("Your organizer account is currently inactive. Activate your account to create events, manage vendors, handle event requests, and access organizer services.", 14, Color.web("#343841"), false)
        );

        HBox inBtns = new HBox(10);
        inBtns.setPadding(new Insets(5, 0, 0, 0));

        Button actBtn = action("Activate Organizer Account  →", true);
        actBtn.setOnAction(e -> openProfile());

        Button profBtn = action("View Profile", false);
        profBtn.setOnAction(e -> openProfile());

        inBtns.getChildren().addAll(actBtn, profBtn);
        inactiveInfo.getChildren().add(inBtns);
        inactive.getChildren().addAll(warning, inactiveInfo);

        // STATS
        HBox stats = new HBox(14);
        stats.getChildren().addAll(
                stat("Event Requests", "00", "No pending requests", "▣"),
                stat("My Events", "00", "No active events", "▣"),
                stat("Upcoming Events", "00", "No upcoming events", "⌄"),
                stat("Vendors", "00", "No active vendors", "♧"),
                stat("Tasks", "00", "No tasks due", "✓")
        );

        content.getChildren().addAll(welcome, inactive, stats);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color:" + BG + ";-fx-border-color:transparent;");

        VBox.setVgrow(scroll, Priority.ALWAYS);
        main.getChildren().add(scroll);
        root.setCenter(main);

        loadOrganizerProfileAsync();

        inactiveOrganizerScene = new Scene(root, 1550, 750);
        return inactiveOrganizerScene;
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

    private Label txt(String s, double size, Color c, boolean bold) {
        Label l = new Label(s);
        l.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        l.setTextFill(c);
        l.setWrapText(true);
        return l;
    }

    private Label section(String s) {
        Label l = txt(s, 11, Color.web("#8396B7"), true);
        l.setPadding(new Insets(10, 12, 3, 12));
        return l;
    }

    private Button nav(String s, boolean active, Runnable action) {
        Button b = new Button(s);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(43);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(0, 12, 0, 12));
        b.setStyle("-fx-background-color:" + (active ? "#0B4EA2" : "transparent") + ";-fx-text-fill:" + (active ? "white" : "#AAB6CC") + ";-fx-font-size:14px;-fx-background-radius:5px;-fx-cursor:hand;");
        if (action != null) b.setOnAction(e -> action.run());
        return b;
    }

    private Button topBtn(String s) {
        Button b = new Button(s);
        b.setPrefHeight(40);
        b.setStyle("-fx-background-color:#F8F9FB;-fx-border-color:#DDE2EA;-fx-border-radius:8px;-fx-background-radius:8px;-fx-font-size:14px;");
        return b;
    }

    private Label badge(String s) {
        Label l = txt(s, 11, Color.web("#626872"), true);
        l.setStyle("-fx-background-color:#E2E5E8;-fx-background-radius:14px;-fx-padding:5 12 5 12;");
        return l;
    }

    private Button action(String s, boolean primary) {
        Button b = new Button(s);
        b.setPrefHeight(40);
        b.setPadding(new Insets(0, 17, 0, 17));
        b.setStyle(primary ? "-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:14px;-fx-background-radius:7px;-fx-cursor:hand;"
                : "-fx-background-color:white;-fx-text-fill:" + NAVY + ";-fx-border-color:#AAB2C0;-fx-border-radius:7px;-fx-background-radius:7px;-fx-font-size:14px;-fx-cursor:hand;");
        return b;
    }

    private VBox stat(String title, String value, String sub, String icon) {
        VBox b = new VBox(6); b.setPadding(new Insets(16)); b.setPrefHeight(135); b.setStyle(card());
        HBox.setHgrow(b, Priority.ALWAYS);
        HBox top = new HBox(); top.setAlignment(Pos.CENTER_LEFT);
        Label t = txt(title, 14, Color.web(MUTED), false); Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        top.getChildren().addAll(t, r, txt(icon, 18, Color.web("#9DA4B0"), false));
        b.getChildren().addAll(top, txt(value, 42, Color.web("#6F7378"), true), txt(sub, 12, Color.web("#777D87"), false));
        return b;
    }

    private String card() {
        return "-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:11px;-fx-background-radius:11px;";
    }

    private Region gap(double h) {
        Region r = new Region(); r.setMinHeight(h); return r;
    }

    public void backToInactiveOrganizerDashboard() {
        if (inactiveOrganizerScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(inactiveOrganizerScene);
        }
    }
}