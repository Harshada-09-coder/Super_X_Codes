package com.techtrio.view;

import java.util.List;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ActiveCustomerDashboardC {

    private Scene activeCustomerScene;

    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String TEXT = "#17233C";
    private static final String BORDER = "#D7DEE8";
    private static final String LIGHT_BLUE = "#BFE5FF";

    private VBox eventListContainer;

    private Label totalEventsValLabel;
    private Label upcomingEventsValLabel;
    private Label pendingEventsValLabel;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public ActiveCustomerDashboardC() {

        this(
                UserSession.getUid(),
                UserSession.getIdToken()
        );
    }

    public ActiveCustomerDashboardC(
            String uid,
            String idToken
    ) {

        this.uid =
                (uid != null && !uid.isBlank())
                        ? uid
                        : UserSession.getUid();

        this.idToken =
                (idToken != null && !idToken.isBlank())
                        ? idToken
                        : UserSession.getIdToken();

        UserSession.setSession(
                this.uid,
                this.idToken,
                null,
                "Customer"
        );
    }

    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    public Scene getactiveCustomerScene(
            Runnable callBackAction1
    ) {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar =
                new VBox();

        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        VBox logoBox =
                new VBox(2);

        logoBox.setPadding(
                new Insets(
                        24,
                        20,
                        20,
                        20
                )
        );

        logoBox.getChildren().addAll(

                styledLabel(
                        "EventXpert",
                        23,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "CUSTOMER PANEL",
                        9,
                        FontWeight.NORMAL,
                        "#B8C9E2"
                )
        );

        // -----------------------------------------------------
        // MAIN MENU
        // -----------------------------------------------------

        VBox menu =
                new VBox(5);

        menu.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12
                )
        );

        HBox dashboardMenu =
                menuItem(
                        "▦",
                        "Dashboard",
                        true
                );

        HBox createEventMenu =
                menuItem(
                        "⊕",
                        "Create Event",
                        false
                );

        createEventMenu.setOnMouseClicked(
                e -> openCreateEvent()
        );

        HBox myEventsMenu =
                menuItem(
                        "▣",
                        "My Events",
                        false
                );

        HBox calendarMenu =
                menuItem(
                        "□",
                        "Calendar",
                        false
                );

        HBox notificationMenu =
                menuItem(
                        "♧",
                        "Notifications",
                        false
                );

        notificationMenu.setOnMouseClicked(e -> {

            Notification notificationPage =
                    new Notification();

            if (LandingPage.landingPageStage != null) {

                LandingPage.landingPageStage.setScene(
                        notificationPage.getNotificationScene()
                );
            }
        });

        menu.getChildren().addAll(
                dashboardMenu,
                createEventMenu,
                myEventsMenu,
                calendarMenu,
                notificationMenu
        );

        // -----------------------------------------------------
        // SIDEBAR SPACE
        // -----------------------------------------------------

        Region sidebarSpace =
                new Region();

        VBox.setVgrow(
                sidebarSpace,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // BOTTOM MENU
        // -----------------------------------------------------

        VBox bottomMenu =
                new VBox(5);

        bottomMenu.setPadding(
                new Insets(
                        8,
                        12,
                        18,
                        12
                )
        );

        // PROFILE

        HBox profileMenu =
                menuItem(
                        "◉",
                        "Profile",
                        false
                );

        profileMenu.setOnMouseClicked(e -> {

            ProfilePageC profilePage =
                    new ProfilePageC();

            if (LandingPage.landingPageStage != null) {

                LandingPage.landingPageStage.setScene(
                        profilePage.getProfileScene(
                                this::returnToDashboard
                        )
                );
            }
        });

        // SETTINGS

        HBox settingsMenu =
                menuItem(
                        "⚙",
                        "Settings",
                        false
                );

        settingsMenu.setOnMouseClicked(e -> {

            SettingC settingPage =
                    new SettingC();

            if (LandingPage.landingPageStage != null) {

                LandingPage.landingPageStage.setScene(
                        settingPage.getSettingScene(
                                this::returnToDashboard
                        )
                );
            }
        });

        // SUPPORT

        HBox supportMenu =
                menuItem(
                        "?",
                        "Support",
                        false
                );

        bottomMenu.getChildren().addAll(
                profileMenu,
                settingsMenu,
                supportMenu
        );

        sidebar.getChildren().addAll(
                logoBox,
                menu,
                sidebarSpace,
                bottomMenu
        );

        // =====================================================
        // RIGHT AREA
        // =====================================================

        BorderPane rightArea =
                new BorderPane();

        // =====================================================
        // TOP BAR
        // =====================================================

        HBox topBar =
                new HBox(12);

        topBar.setPrefHeight(64);
        topBar.setMinHeight(64);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(
                        10,
                        20,
                        10,
                        20
                )
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E0E6EF;" +
                "-fx-border-width:0 0 1 0;"
        );

        TextField search =
                new TextField();

        search.setPromptText(
                "Search events..."
        );

        search.setPrefWidth(330);
        search.setPrefHeight(36);

        search.setStyle(
                "-fx-background-color:#F7F9FC;" +
                "-fx-border-color:#D2DAE5;" +
                "-fx-border-radius:18;" +
                "-fx-background-radius:18;" +
                "-fx-padding:0 15;" +
                "-fx-font-size:12px;"
        );

        Region topSpace =
                new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        Label notification =
                styledLabel(
                        "♧",
                        19,
                        FontWeight.NORMAL,
                        NAVY
                );

        Circle avatarCircle =
                new Circle(
                        18,
                        Color.web("#C9E8FA")
                );

        Label avatarLetter =
                styledLabel(
                        "C",
                        12,
                        FontWeight.BOLD,
                        NAVY
                );

        StackPane avatar =
                new StackPane(
                        avatarCircle,
                        avatarLetter
                );

        topBar.getChildren().addAll(
                search,
                topSpace,
                notification,
                avatar
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox main =
                new VBox(12);

        main.setPadding(
                new Insets(
                        15,
                        20,
                        15,
                        20
                )
        );

        main.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        // =====================================================
        // WELCOME
        // =====================================================

        HBox welcome =
                new HBox();

        welcome.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox welcomeText =
                new VBox(3);

        welcomeText.getChildren().addAll(

                styledLabel(
                        "Welcome back!",
                        23,
                        FontWeight.BOLD,
                        NAVY
                ),

                styledLabel(
                        "Here's a look at your events.",
                        11,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        Region welcomeSpace =
                new Region();

        HBox.setHgrow(
                welcomeSpace,
                Priority.ALWAYS
        );

        Button createEvent =
                new Button(
                        "+  Create New Event"
                );

        createEvent.setPrefSize(
                175,
                36
        );

        createEvent.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );

        createEvent.setOnAction(
                e -> openCreateEvent()
        );

        welcome.getChildren().addAll(
                welcomeText,
                welcomeSpace,
                createEvent
        );

        // =====================================================
        // STAT CARDS
        // =====================================================

        HBox stats =
                new HBox(12);

        stats.setPrefHeight(70);

        totalEventsValLabel =
                new Label("0");

        upcomingEventsValLabel =
                new Label("0");

        pendingEventsValLabel =
                new Label("0");

        stats.getChildren().addAll(

                statCard(
                        "▣",
                        "Total Events Created",
                        totalEventsValLabel
                ),

                statCard(
                        "→",
                        "Upcoming / Active",
                        upcomingEventsValLabel
                ),

                statCard(
                        "⏳",
                        "Pending Approval",
                        pendingEventsValLabel
                )
        );

        // =====================================================
        // LOWER AREA
        // =====================================================

        HBox lower =
                new HBox(14);

        VBox.setVgrow(
                lower,
                Priority.ALWAYS
        );

        // =====================================================
        // EVENTS CARD
        // =====================================================

        VBox previous =
                new VBox(12);

        previous.setPadding(
                new Insets(16)
        );

        previous.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        VBox.setVgrow(
                previous,
                Priority.ALWAYS
        );

        HBox previousHeader =
                new HBox();

        previousHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox previousTitle =
                new VBox(4);

        previousTitle.getChildren().addAll(

                styledLabel(
                        "Your Events",
                        23,
                        FontWeight.BOLD,
                        TEXT
                ),

                styledLabel(
                        "Your event history and status loaded from Firestore.",
                        14,
                        FontWeight.NORMAL,
                        "#667085"
                )
        );

        previousHeader.getChildren().add(
                previousTitle
        );

        eventListContainer =
                new VBox(12);

        eventListContainer.getChildren().add(
                styledLabel(
                        "Loading your events...",
                        14,
                        FontWeight.NORMAL,
                        "#667085"
                )
        );

        previous.getChildren().addAll(
                previousHeader,
                eventListContainer
        );

        HBox.setHgrow(
                previous,
                Priority.ALWAYS
        );

        lower.getChildren().add(
                previous
        );

        // =====================================================
        // SIDE CARDS
        // =====================================================

        VBox sideCards =
                new VBox(14);

        sideCards.setPrefWidth(330);
        sideCards.setMinWidth(330);
        sideCards.setMaxWidth(330);

        sideCards.getChildren().addAll(
                aiPlanningCard(),
                calendarCard(),
                weatherCard()
        );

        lower.getChildren().add(
                sideCards
        );

        // =====================================================
        // MAIN
        // =====================================================

        main.getChildren().addAll(
                welcome,
                stats,
                lower
        );

        VBox.setVgrow(
                lower,
                Priority.ALWAYS
        );

        rightArea.setTop(
                topBar
        );

        rightArea.setCenter(
                main
        );

        root.setLeft(
                sidebar
        );

        root.setCenter(
                rightArea
        );

        // =====================================================
        // LOAD EVENTS
        // =====================================================

        loadCustomerEventsAsync();

        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(
                        root,
                        1550,
                        785
                );

        activeCustomerScene =
                scene;

        return activeCustomerScene;
    }

    // =========================================================
    // RETURN TO DASHBOARD
    // =========================================================

    private void returnToDashboard() {

        ActiveCustomerDashboardC dashboardPage =
                new ActiveCustomerDashboardC(
                        uid,
                        idToken
                );

        if (LandingPage.landingPageStage != null) {

            LandingPage.landingPageStage.setScene(
                    dashboardPage.getactiveCustomerScene(
                            null
                    )
            );
        }
    }

    // =========================================================
    // CREATE EVENT
    // =========================================================

private void openCreateEvent() {

    CreateEventC createView =
            new CreateEventC(
                    uid,
                    idToken
            );

    if (LandingPage.landingPageStage != null) {

        LandingPage.landingPageStage.setScene(
                createView.getCreateEventScene(
                        this::returnToDashboard
                )
        );
    }
}

    // =========================================================
    // AI PLANNING CARD
    // =========================================================

    private VBox aiPlanningCard() {

        VBox card =
                new VBox(10);

        card.setPrefHeight(185);
        card.setMinHeight(185);
        card.setMaxHeight(185);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPadding(
                new Insets(16)
        );

        card.setStyle(
                "-fx-background-color:#0D3979;" +
                "-fx-background-radius:12;"
        );

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(3);

        titleBox.getChildren().addAll(

                styledLabel(
                        "AI Planning",
                        20,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Smart event planning",
                        14,
                        FontWeight.NORMAL,
                        "#D9ECFF"
                )
        );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label aiIcon =
                styledLabel(
                        "✦",
                        27,
                        FontWeight.BOLD,
                        "#FFFFFF"
                );

        header.getChildren().addAll(
                titleBox,
                space,
                aiIcon
        );

        Label description =
                styledLabel(
                        "Let AI help you plan your perfect event.",
                        14,
                        FontWeight.NORMAL,
                        "#E3F2FF"
                );

        description.setWrapText(
                true
        );

        Button startPlanning =
                new Button(
                        "Start Planning  →"
                );

        startPlanning.setPrefHeight(
                32
        );

        startPlanning.setMaxWidth(
                Double.MAX_VALUE
        );

        startPlanning.setStyle(
                "-fx-background-color:#FFFFFF;" +
                "-fx-text-fill:#0D3979;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );

        startPlanning.setOnAction(e -> {

            AiPlanningC aiPlanningPage =
                    new AiPlanningC(
                            uid,
                            idToken
                    );

            if (LandingPage.landingPageStage != null) {

                LandingPage.landingPageStage.setScene(
                        aiPlanningPage.getAiPlanningScene()
                );
            }
        });

        Region buttonSpace =
                new Region();

        VBox.setVgrow(
                buttonSpace,
                Priority.ALWAYS
        );

        card.getChildren().addAll(
                header,
                description,
                buttonSpace,
                startPlanning
        );

        return card;
    }

    // =========================================================
    // CALENDAR CARD
    // =========================================================

    private VBox calendarCard() {

        VBox card =
                new VBox(8);

        card.setPrefHeight(205);
        card.setMinHeight(205);
        card.setMaxHeight(205);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPadding(
                new Insets(15)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D7DEE8;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        header.getChildren().addAll(

                styledLabel(
                        "Event Calendar",
                        16,
                        FontWeight.BOLD,
                        TEXT
                ),

                space,

                styledLabel(
                        "July 2026",
                        10,
                        FontWeight.NORMAL,
                        "#667085"
                )
        );

        // -----------------------------------------------------
        // GRID
        // -----------------------------------------------------

        GridPane grid =
                new GridPane();

        grid.setHgap(5);
        grid.setVgap(4);

        grid.setAlignment(
                Pos.CENTER
        );

        // IMPORTANT:
        // Exactly 7 elements
        String[] days = {
                "S",
                "M",
                "T",
                "W",
                "T",
                "F",
                "S"
        };

        // -----------------------------------------------------
        // DAY HEADINGS
        // -----------------------------------------------------

        for (int i = 0; i < days.length; i++) {

            Label day =
                    styledLabel(
                            days[i],
                            9,
                            FontWeight.BOLD,
                            "#667085"
                    );

            day.setPrefWidth(34);
            day.setPrefHeight(18);

            day.setAlignment(
                    Pos.CENTER
            );

            grid.add(
                    day,
                    i,
                    0
            );
        }

        // -----------------------------------------------------
        // JULY 2026
        //
        // July 1, 2026 = Wednesday
        //
        // S  M  T  W  T  F  S
        //          1  2  3  4
        // 5  6  7  8  9 10 11
        //12 13 14 15 16 17 18
        //19 20 21 22 23 24 25
        //26 27 28 29 30 31
        // -----------------------------------------------------

        int[][] dates = {

                {0, 0, 0, 1, 2, 3, 4},

                {5, 6, 7, 8, 9, 10, 11},

                {12, 13, 14, 15, 16, 17, 18},

                {19, 20, 21, 22, 23, 24, 25},

                {26, 27, 28, 29, 30, 31, 0}
        };

        // -----------------------------------------------------
        // ADD DATES
        // -----------------------------------------------------

        for (int row = 0; row < dates.length; row++) {

            for (int col = 0; col < dates[row].length; col++) {

                int value =
                        dates[row][col];

                if (value == 0) {
                    continue;
                }

                Label date =
                        styledLabel(
                                String.valueOf(value),
                                9,
                                FontWeight.NORMAL,
                                TEXT
                        );

                date.setPrefWidth(34);
                date.setPrefHeight(22);

                date.setAlignment(
                        Pos.CENTER
                );

                grid.add(
                        date,
                        col,
                        row + 1
                );
            }
        }

        card.getChildren().addAll(
                header,
                grid
        );

        return card;
    }

    // =========================================================
    // WEATHER CARD
    // =========================================================

    private VBox weatherCard() {

        VBox card =
                new VBox(9);

        card.setPrefHeight(185);
        card.setMinHeight(185);
        card.setMaxHeight(185);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPadding(
                new Insets(15)
        );

        card.setStyle(
                "-fx-background-color:linear-gradient(" +
                "to bottom right,#0D3979,#2670B8);" +
                "-fx-background-radius:12;"
        );

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox title =
                new VBox(2);

        title.getChildren().addAll(

                styledLabel(
                        "Weather Forecast",
                        17,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Pune, Maharashtra",
                        11,
                        FontWeight.NORMAL,
                        "#D9ECFF"
                )
        );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label today =
                styledLabel(
                        "TODAY",
                        11,
                        FontWeight.BOLD,
                        "#D9ECFF"
                );

        header.getChildren().addAll(
                title,
                space,
                today
        );

        // -----------------------------------------------------
        // WEATHER
        // -----------------------------------------------------

        HBox weather =
                new HBox(14);

        weather.setAlignment(
                Pos.CENTER_LEFT
        );

        Label sun =
                styledLabel(
                        "☀",
                        42,
                        FontWeight.NORMAL,
                        "#FFFFFF"
                );

        VBox temperature =
                new VBox(2);

        temperature.getChildren().addAll(

                styledLabel(
                        "28°C",
                        29,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Partly Cloudy",
                        12,
                        FontWeight.NORMAL,
                        "#D9ECFF"
                )
        );

        weather.getChildren().addAll(
                sun,
                temperature
        );

        // -----------------------------------------------------
        // DETAILS
        // -----------------------------------------------------

        HBox details =
                new HBox(20);

        details.getChildren().addAll(

                weatherDetail(
                        "Humidity",
                        "64%"
                ),

                weatherDetail(
                        "Wind",
                        "12 km/h"
                ),

                weatherDetail(
                        "Visibility",
                        "8 km"
                )
        );

        // -----------------------------------------------------
        // MESSAGE
        // -----------------------------------------------------

        HBox message =
                new HBox(7);

        message.setAlignment(
                Pos.CENTER_LEFT
        );

        message.setPadding(
                new Insets(
                        7,
                        9,
                        7,
                        9
                )
        );

        message.setStyle(
                "-fx-background-color:rgba(255,255,255,0.13);" +
                "-fx-background-radius:7;"
        );

        message.getChildren().addAll(

                styledLabel(
                        "✓",
                        11,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Good weather for outdoor events",
                        11,
                        FontWeight.NORMAL,
                        "#E5F2FF"
                )
        );

        card.getChildren().addAll(
                header,
                weather,
                details,
                message
        );

        return card;
    }

    // =========================================================
    // WEATHER DETAIL
    // =========================================================

    private VBox weatherDetail(
            String title,
            String value
    ) {

        VBox box =
                new VBox(2);

        box.getChildren().addAll(

                styledLabel(
                        value,
                        9,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        title,
                        8,
                        FontWeight.NORMAL,
                        "#C8E3FF"
                )
        );

        return box;
    }

    // =========================================================
    // LOAD CUSTOMER EVENTS
    // =========================================================

    private void loadCustomerEventsAsync() {

        if (uid == null || uid.isBlank()) {

            if (eventListContainer != null) {

                eventListContainer.getChildren().clear();

                eventListContainer.getChildren().add(
                        styledLabel(
                                "No user session found.",
                                14,
                                FontWeight.NORMAL,
                                "#667085"
                        )
                );
            }

            return;
        }

        Task<List<Event>> task =
                new Task<>() {

                    @Override
                    protected List<Event> call() {

                        CustomerController controller =
                                new CustomerController();

                        return controller.getEventsByCustomerId(
                                uid
                        );
                    }
                };

        task.setOnSucceeded(e -> {

            List<Event> events =
                    task.getValue();

            Platform.runLater(() -> {

                if (eventListContainer == null) {
                    return;
                }

                eventListContainer
                        .getChildren()
                        .clear();

                if (events == null || events.isEmpty()) {

                    eventListContainer.getChildren().add(
                            styledLabel(
                                    "No events found. Click '+ Create New Event' to plan one!",
                                    14,
                                    FontWeight.NORMAL,
                                    "#667085"
                            )
                    );

                    totalEventsValLabel.setText("0");
                    upcomingEventsValLabel.setText("0");
                    pendingEventsValLabel.setText("0");

                    return;
                }

                totalEventsValLabel.setText(
                        String.valueOf(
                                events.size()
                        )
                );

                int pendingCount = 0;
                int upcomingCount = 0;

                for (Event ev : events) {

                    if (
                            "PENDING".equalsIgnoreCase(
                                    ev.getStatus()
                            )
                    ) {

                        pendingCount++;

                    } else {

                        upcomingCount++;
                    }

                    // -------------------------------------------------
                    // LOCATION
                    // -------------------------------------------------

                    String loc =
                            ev.getCity();

                    if (
                            ev.getVenueType() != null
                                    &&
                            !ev.getVenueType().isBlank()
                    ) {

                        loc =
                                ev.getVenueType()
                                        +
                                (
                                        loc != null
                                                &&
                                        !loc.isBlank()
                                                ? ", " + loc
                                                : ""
                                );
                    }

                    if (
                            loc == null
                                    ||
                            loc.isBlank()
                    ) {

                        loc =
                                "Venue Pending";
                    }

                    // -------------------------------------------------
                    // EVENT NAME
                    // -------------------------------------------------

                    String eventName =
                            ev.getEventName();

                    if (
                            eventName == null
                                    ||
                            eventName.isBlank()
                    ) {

                        eventName =
                                "Unnamed Event";
                    }

                    // -------------------------------------------------
                    // DATE
                    // -------------------------------------------------

                    String eventDate =
                            (
                                    ev.getEventDate() != null
                                            &&
                                    !ev.getEventDate().isBlank()
                            )
                                    ? ev.getEventDate()
                                    : "TBD";

                    // -------------------------------------------------
                    // EVENT CARD
                    // -------------------------------------------------

                    eventListContainer.getChildren().add(

                            eventCard(
                                    eventName,
                                    eventDate,
                                    loc,
                                    ev.getStatus()
                            )
                    );
                }

                upcomingEventsValLabel.setText(
                        String.valueOf(
                                upcomingCount
                        )
                );

                pendingEventsValLabel.setText(
                        String.valueOf(
                                pendingCount
                        )
                );
            });
        });

        // ---------------------------------------------------------
        // FAILED
        // ---------------------------------------------------------

        task.setOnFailed(e -> {

            Platform.runLater(() -> {

                if (eventListContainer == null) {
                    return;
                }

                eventListContainer
                        .getChildren()
                        .clear();

                eventListContainer.getChildren().add(

                        styledLabel(
                                "Unable to load events.",
                                14,
                                FontWeight.NORMAL,
                                "#B42318"
                        )
                );
            });
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

    // =========================================================
    // EVENT CARD
    // =========================================================

    private HBox eventCard(
            String name,
            String date,
            String location,
            String status
    ) {

        HBox card =
                new HBox(14);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPrefHeight(
                112
        );

        card.setMinHeight(
                112
        );

        card.setPadding(
                new Insets(14)
        );

        card.setStyle(
                "-fx-background-color:#F9FBFD;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        // -----------------------------------------------------
        // ICON
        // -----------------------------------------------------

        Circle circle =
                new Circle(
                        27,
                        Color.web("#E1F3FF")
                );

        StackPane icon =
                new StackPane(
                        circle,
                        styledLabel(
                                "✦",
                                18,
                                FontWeight.BOLD,
                                "#1D5274"
                        )
                );

        // -----------------------------------------------------
        // DETAILS
        // -----------------------------------------------------

        VBox details =
                new VBox(5);

        details.getChildren().addAll(

                styledLabel(
                        name,
                        17,
                        FontWeight.BOLD,
                        TEXT
                ),

                styledLabel(
                        "Date: " + date,
                        12,
                        FontWeight.NORMAL,
                        "#596273"
                ),

                styledLabel(
                        "Location: " + location,
                        12,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        // -----------------------------------------------------
        // SPACE
        // -----------------------------------------------------

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        String finalStatus =
                (
                        status != null
                                &&
                        !status.isBlank()
                )
                        ? status
                        : "PENDING";

        String badgeColor;

        if (
                "PENDING".equalsIgnoreCase(
                        finalStatus
                )
        ) {

            badgeColor =
                    "#D97706";

        } else {

            badgeColor =
                    "#16A34A";
        }

        Label statusLbl =
                styledLabel(
                        "● " + finalStatus,
                        14,
                        FontWeight.BOLD,
                        badgeColor
                );

        card.getChildren().addAll(
                icon,
                details,
                space,
                statusLbl
        );

        return card;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox statCard(
            String icon,
            String title,
            Label valueLabel
    ) {

        VBox card =
                new VBox();

        card.setPrefHeight(
                70
        );

        card.setPadding(
                new Insets(9)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        HBox row =
                new HBox(9);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle circle =
                new Circle(
                        17,
                        Color.web("#E0F2FF")
                );

        StackPane iconBox =
                new StackPane(
                        circle,
                        styledLabel(
                                icon,
                                13,
                                FontWeight.BOLD,
                                NAVY
                        )
                );

        valueLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        valueLabel.setTextFill(
                Color.web(NAVY)
        );

        VBox text =
                new VBox(1);

        text.getChildren().addAll(

                styledLabel(
                        title,
                        12,
                        FontWeight.NORMAL,
                        "#667085"
                ),

                valueLabel
        );

        row.getChildren().addAll(
                iconBox,
                text
        );

        card.getChildren().add(
                row
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        return card;
    }

    // =========================================================
    // MENU ITEM
    // =========================================================

    private HBox menuItem(
            String icon,
            String text,
            boolean active
    ) {

        return menuItem(
                icon,
                text,
                active,
                null
        );
    }

    private HBox menuItem(
            String icon,
            String text,
            boolean active,
            Runnable action
    ) {

        HBox item =
                new HBox(11);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPrefHeight(
                40
        );

        item.setPadding(
                new Insets(
                        0,
                        11,
                        0,
                        13
                )
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" +
                    LIGHT_BLUE +
                    ";" +
                    "-fx-background-radius:7;"
            );

        } else {

            item.setStyle(
                    "-fx-background-color:transparent;"
            );
        }

        if (action != null) {

            item.setStyle(
                    item.getStyle() +
                    "-fx-cursor:hand;"
            );

            item.setOnMouseClicked(
                    e -> action.run()
            );
        }

        item.getChildren().addAll(

                styledLabel(
                        icon,
                        16,
                        FontWeight.NORMAL,
                        active
                                ? NAVY
                                : "#D1D9E8"
                ),

                styledLabel(
                        text,
                        12,
                        active
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        active
                                ? "#35556E"
                                : "#D1D9E8"
                )
        );

        return item;
    }

    // =========================================================
    // STYLED LABEL
    // =========================================================

    private Label styledLabel(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        weight,
                        size
                )
        );

        label.setTextFill(
                Color.web(color)
        );

        return label;
    }
}