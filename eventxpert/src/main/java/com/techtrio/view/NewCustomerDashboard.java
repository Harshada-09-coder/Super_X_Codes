package com.techtrio.view;

import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NewCustomerDashboard {

    private Scene newCustomerScene;
    private String uid;
    private String idToken;

    private final String NAVY = "#082E6B";
    private final String BLUE = "#174A88";
    private final String LIGHT = "#F5F8FC";
    private final String TEXT = "#082E6B";
    private final String GRAY = "#526071";

    public NewCustomerDashboard() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public NewCustomerDashboard(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }

    public Scene getnewCustomerScene(Runnable callBackAction1) {

        VBox sidebar = new VBox(18);
        sidebar.setPrefWidth(285);
        sidebar.setPadding(new Insets(28, 0, 20, 0));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Circle profileCircle = new Circle(33);
        profileCircle.setFill(Color.web("#E7E7E7"));

        Label initial = new Label("C");
        initial.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        initial.setTextFill(Color.web(NAVY));

        StackPane profile = new StackPane(profileCircle, initial);

        Label logo = new Label("EventXpert");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 29));
        logo.setTextFill(Color.WHITE);

        Label dashboardText = new Label("Customer Dashboard");
        dashboardText.setFont(Font.font("Arial", 17));
        dashboardText.setTextFill(Color.web("#B8D0EE"));

        VBox brand = new VBox(5, profile, logo, dashboardText);
        brand.setAlignment(Pos.CENTER);
        brand.setPadding(new Insets(0, 0, 20, 0));

        sidebar.getChildren().add(brand);

        HBox dashNav = navItem("▦", "Dashboard", true, () -> {});
        HBox createEventNav = navItem("⊕", "Create Event", false, this::openCreateEvent);
        HBox myEventsNav = navItem("□", "My Events", false, this::openCreateEvent);
        HBox calendarNav = navItem("□", "Calendar", false, () -> {});
        HBox notificationsNav = navItem("♧", "Notifications", false, this::openNotifications);

        sidebar.getChildren().addAll(
                dashNav,
                createEventNav,
                myEventsNav,
                calendarNav,
                notificationsNav
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);

        HBox profileNav = navItem("♙", "Profile", false, this::openProfile);
        HBox settingsNav = navItem("⚙", "Settings", false, this::openSettings);
        HBox supportNav = navItem("?", "Support", false, () -> {});

        sidebar.getChildren().addAll(
                sideSpace,
                profileNav,
                settingsNav,
                supportNav
        );

        HBox topBar = new HBox(18);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(14, 25, 14, 25));
        topBar.setPrefHeight(72);
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E0E5EC;-fx-border-width:0 0 1 0;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(325);
        search.setPrefHeight(42);
        search.setStyle("-fx-background-color:#F8FAFD;-fx-border-color:#C8D2DF;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:0 15;-fx-font-size:14;");

        Label notification = new Label("♧");
        notification.setFont(Font.font("Arial", 22));
        notification.setTextFill(Color.web(GRAY));
        notification.setStyle("-fx-cursor:hand;");
        notification.setOnMouseClicked(e -> openNotifications());

        Button createTop = new Button("Create Event");
        createTop.setPrefHeight(42);
        createTop.setStyle("-fx-background-color:" + NAVY + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:15;-fx-background-radius:8;-fx-padding:0 22;-fx-cursor:hand;");
        createTop.setOnAction(e -> openCreateEvent());

        Circle topProfile = new Circle(20);
        topProfile.setFill(Color.web("#D7EAF8"));

        Label topInitial = new Label("C");
        topInitial.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        topInitial.setTextFill(Color.web(NAVY));

        StackPane topUser = new StackPane(topProfile, topInitial);
        topUser.setStyle("-fx-cursor:hand;");
        topUser.setOnMouseClicked(e -> openProfile());

        topBar.getChildren().addAll(topSpace, search, notification, createTop, topUser);

        VBox content = new VBox(18);
        content.setPadding(new Insets(22, 32, 35, 32));
        content.setStyle("-fx-background-color:" + LIGHT + ";");

        Label welcome = new Label("Welcome to EventXpert!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        welcome.setTextFill(Color.web(TEXT));

        Label subtitle = new Label("Let's plan your first event and make it memorable.");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.web(GRAY));

        VBox heading = new VBox(2, welcome, subtitle);

        VBox firstEvent = new VBox(13);
        firstEvent.setPadding(new Insets(22));
        firstEvent.setStyle(cardStyle());

        Label firstTitle = new Label("Plan Your First Event");
        firstTitle.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        firstTitle.setTextFill(Color.web(TEXT));

        Label firstDescription = new Label(
                "Whether you're planning a birthday celebration, corporate event, "
              + "or any special occasion, EventXpert helps you manage everything in one place."
        );
        firstDescription.setWrapText(true);
        firstDescription.setFont(Font.font("Arial", 14));
        firstDescription.setTextFill(Color.web(GRAY));

        Button firstButton = new Button("+ Create Your First Event");
        firstButton.setPrefHeight(42);
        firstButton.setStyle(buttonStyle());
        firstButton.setOnAction(e -> openCreateEvent());

        // Added ONCE to prevent duplicate-children exception
        firstEvent.getChildren().addAll(firstTitle, firstDescription, firstButton);

        VBox aiCard = new VBox(12);
        aiCard.setPadding(new Insets(20));
        aiCard.setPrefWidth(305);
        aiCard.setStyle("-fx-background-color:#174A88;-fx-background-radius:12;");

        Label aiIcon = new Label("✦");
        aiIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        aiIcon.setTextFill(Color.WHITE);

        Label aiTitle = new Label("Plan Smarter with AI");
        aiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 21));
        aiTitle.setTextFill(Color.WHITE);

        Label aiText = new Label(
                "Let EventXpert's AI Event Planner create a personalized "
              + "event plan based on your event type, guest count, preferences and budget."
        );
        aiText.setWrapText(true);
        aiText.setFont(Font.font("Arial", 13));
        aiText.setTextFill(Color.WHITE);

        Button aiButton = new Button("Try AI Event Planner  →");
        aiButton.setMaxWidth(Double.MAX_VALUE);
        aiButton.setPrefHeight(40);
        aiButton.setStyle("-fx-background-color:#BFE0FA;-fx-text-fill:#082E6B;-fx-font-weight:bold;-fx-font-size:14;-fx-background-radius:7;-fx-cursor:hand;");
        aiButton.setOnAction(e -> openCreateEvent());

        aiCard.getChildren().addAll(aiIcon, aiTitle, aiText, aiButton);

        HBox topCards = new HBox(22, firstEvent, aiCard);
        HBox.setHgrow(firstEvent, Priority.ALWAYS);

        HBox stats = new HBox(15);
        stats.getChildren().addAll(
                statCard("Upcoming Events", "0"),
                statCard("Active Events", "0"),
                statCard("Total Events", "0"),
                statCard("Pending Requests", "0")
        );

        Label exploreTitle = new Label("Explore Event Ideas");
        exploreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        exploreTitle.setTextFill(Color.web(TEXT));

        Label exploreSubtitle = new Label("Choose an event category to get started with your planning.");
        exploreSubtitle.setFont(Font.font("Arial", 14));
        exploreSubtitle.setTextFill(Color.web(GRAY));

        VBox exploreHeading = new VBox(2, exploreTitle, exploreSubtitle);

        HBox row1 = new HBox(15);
        row1.getChildren().addAll(
                eventCard("▣", "Birthday Celebration", "Plan a memorable birthday with venue, catering, entertainment and more.", "Plan Birthday  →", this::openCreateEvent),
                eventCard("▣", "Corporate Event", "Organize a professional seminar, conference, meeting or corporate gathering.", "Plan Corporate Event  →", this::openCreateEvent)
        );

        HBox row2 = new HBox(15);
        row2.getChildren().addAll(
                eventCard("◇", "Educational Event", "Plan seminars, workshops, conferences, training sessions and educational programs.", "Plan Educational Event  →", this::openCreateEvent),
                eventCard("♧", "Family Event", "Create memorable family gatherings, reunions, celebrations and special occasions.", "Plan Family Event  →", this::openCreateEvent)
        );

        content.getChildren().addAll(heading, topCards, stats, exploreHeading, row1, row2);

        BorderPane mainArea = new BorderPane();
        mainArea.setTop(topBar);
        mainArea.setCenter(content);

        HBox root = new HBox(sidebar, mainArea);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 1550, 750);
        newCustomerScene = scene;
        return newCustomerScene;
    }

    private void openCreateEvent() {
        CreateEventC createEventView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createEventView.getCreateEventScene(this::returnToDashboard));
        }
    }

    private void openProfile() {
        ProfilePageC profilePage = new ProfilePageC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
        }
    }

    private void openSettings() {
        SettingC settingPage = new SettingC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
        }
    }

    private void openNotifications() {
        Notification notificationPage = new Notification();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(notificationPage.getNotificationScene());
        }
    }

    private void returnToDashboard() {
        NewCustomerDashboard dash = new NewCustomerDashboard(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(dash.getnewCustomerScene(null));
        }
    }

    private HBox navItem(String icon, String text, boolean active) {
        return navItem(icon, text, active, null);
    }

    private HBox navItem(String icon, String text, boolean active, Runnable action) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 17));
        iconLabel.setTextFill(Color.WHITE);

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        textLabel.setTextFill(active ? Color.WHITE : Color.web("#B8D0EE"));

        HBox item = new HBox(15, iconLabel, textLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12, 25, 12, 35));
        item.setMaxWidth(Double.MAX_VALUE);

        if (active) {
            item.setStyle("-fx-background-color:#174A88;");
        }
        if (action != null) {
            item.setStyle(item.getStyle() + "-fx-cursor:hand;");
            item.setOnMouseClicked(e -> action.run());
        }
        return item;
    }

    private VBox statCard(String title, String value) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setPrefHeight(100);
        card.setStyle(cardStyle());

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.setTextFill(Color.web(GRAY));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        valueLabel.setTextFill(Color.web(TEXT));

        card.getChildren().addAll(titleLabel, valueLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox eventCard(String icon, String title, String description, String action, Runnable onClick) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setMinHeight(145);
        card.setStyle(cardStyle() + "-fx-cursor:hand;");
        if (onClick != null) {
            card.setOnMouseClicked(e -> onClick.run());
        }

        Label iconLabel = new Label(icon);
        iconLabel.setPrefSize(45, 35);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setFont(Font.font("Arial", 17));
        iconLabel.setTextFill(Color.web(NAVY));
        iconLabel.setStyle("-fx-background-color:#C7E7FA;-fx-background-radius:20;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 19));
        titleLabel.setTextFill(Color.web(TEXT));

        HBox titleRow = new HBox(12, iconLabel, titleLabel);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setFont(Font.font("Arial", 13));
        descriptionLabel.setTextFill(Color.web(GRAY));

        Label actionLabel = new Label(action);
        actionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        actionLabel.setTextFill(Color.web(NAVY));

        card.getChildren().addAll(titleRow, descriptionLabel, actionLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private String cardStyle() {
        return "-fx-background-color:white;-fx-background-radius:10;-fx-border-color:#DCE3EC;-fx-border-radius:10;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),5,0,0,2);";
    }

    private String buttonStyle() {
        return "-fx-background-color:#082E6B;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:14;-fx-background-radius:7;-fx-padding:0 22;-fx-cursor:hand;";
    }
}