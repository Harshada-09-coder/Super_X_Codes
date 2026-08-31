package com.techtrio.view;

import java.time.LocalDate;
import java.util.List;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.WeatherData;
import com.techtrio.services.WeatherService;
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

    private Label welcomeNameLabel;
    private Label avatarLetterLabel;

    // Weather Dynamic Labels (Matching Blue Theme)
    private Label weatherLocationLabel;
    private Label weatherTempLabel;
    private Label weatherConditionLabel;
    private Label weatherHumidityLabel;
    private Label weatherWindLabel;
    private Label weatherFeelsLikeLabel;
    private Label weatherAlertTitleLabel;
    private Label weatherAlertSubLabel;
    private HBox weatherAlertBox;

    public ActiveCustomerDashboardC() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ActiveCustomerDashboardC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank())
                ? uid
                : UserSession.getUid();

        this.idToken = (idToken != null && !idToken.isBlank())
                ? idToken
                : UserSession.getIdToken();

        UserSession.setSession(
                this.uid,
                this.idToken,
                null,
                "Customer"
        );
    }

    public Scene getactiveCustomerScene(Runnable callBackAction1) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // =====================================
        // SIDEBAR
        // =====================================
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(24, 20, 20, 20));
        logoBox.getChildren().addAll(
                styledLabel("EventXpert", 23, FontWeight.BOLD, "#FFFFFF"),
                styledLabel("CUSTOMER PANEL", 9, FontWeight.NORMAL, "#B8C9E2")
        );

        VBox menu = new VBox(5);
        menu.setPadding(new Insets(8, 12, 8, 12));

        HBox dashboardMenu = menuItem("▦", "Dashboard", true);
        HBox createEventMenu = menuItem("⊕", "Create Event", false);
        createEventMenu.setOnMouseClicked(e -> openCreateEvent());
        
        // ==========================================
        // फक्त येथे My Events चे नेव्हिगेशन जोडले आहे
        // ==========================================
        HBox myEventsMenu = menuItem("▣", "My Events", false);
        myEventsMenu.setOnMouseClicked(e -> openMyEvents());

        HBox notificationMenu = menuItem("♧", "Notifications", false);

        notificationMenu.setOnMouseClicked(e -> {
            Notification notificationPage = new Notification();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(notificationPage.getNotificationScene());
            }
        });

        menu.getChildren().addAll(dashboardMenu, createEventMenu, myEventsMenu, notificationMenu);

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(5);
        bottomMenu.setPadding(new Insets(8, 12, 18, 12));

        HBox profileMenu = menuItem("◉", "Profile", false);
        profileMenu.setOnMouseClicked(e -> {
            ProfilePageC profilePage = new ProfilePageC();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
            }
        });

        HBox settingsMenu = menuItem("⚙", "Settings", false);
        settingsMenu.setOnMouseClicked(e -> {
            SettingC settingPage = new SettingC();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
            }
        });

        HBox supportMenu = menuItem("?", "Support", false);
        bottomMenu.getChildren().addAll(profileMenu, settingsMenu, supportMenu);

        sidebar.getChildren().addAll(logoBox, menu, sidebarSpace, bottomMenu);

        // =====================================
        // RIGHT AREA
        // =====================================
        BorderPane rightArea = new BorderPane();

        // TOP BAR
        HBox topBar = new HBox(12);
        topBar.setPrefHeight(64);
        topBar.setMinHeight(64);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color:white; -fx-border-color:#E0E6EF; -fx-border-width:0 0 1 0;");

        TextField search = new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(330);
        search.setPrefHeight(36);
        search.setStyle("-fx-background-color:#F7F9FC; -fx-border-color:#D2DAE5; -fx-border-radius:18; -fx-background-radius:18; -fx-padding:0 15; -fx-font-size:12px;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Label notification = styledLabel("♧", 19, FontWeight.NORMAL, NAVY);
        Circle avatarCircle = new Circle(18, Color.web("#C9E8FA"));
        avatarLetterLabel = styledLabel("C", 12, FontWeight.BOLD, NAVY);
        StackPane avatar = new StackPane(avatarCircle, avatarLetterLabel);

        topBar.getChildren().addAll(search, topSpace, notification, avatar);

        // MAIN CONTENT
        VBox main = new VBox(12);
        main.setPadding(new Insets(15, 20, 15, 20));
        main.setStyle("-fx-background-color:" + BG + ";");

        // WELCOME
        HBox welcome = new HBox();
        welcome.setAlignment(Pos.CENTER_LEFT);

        welcomeNameLabel = styledLabel("Welcome back!", 23, FontWeight.BOLD, NAVY);
        VBox welcomeText = new VBox(3, welcomeNameLabel, styledLabel("Here's a look at your events.", 11, FontWeight.NORMAL, "#596273"));

        Region welcomeSpace = new Region();
        HBox.setHgrow(welcomeSpace, Priority.ALWAYS);

        Button createEvent = new Button("+  Create New Event");
        createEvent.setPrefSize(175, 36);
        createEvent.setStyle("-fx-background-color:" + NAVY + "; -fx-text-fill:white; -fx-background-radius:7; -fx-font-family:Arial; -fx-font-size:11px; -fx-font-weight:bold; -fx-cursor:hand;");
        createEvent.setOnAction(e -> openCreateEvent());

        welcome.getChildren().addAll(welcomeText, welcomeSpace, createEvent);

        // STATS
        HBox stats = new HBox(12);
        stats.setPrefHeight(70);

        totalEventsValLabel = new Label("0");
        upcomingEventsValLabel = new Label("0");
        pendingEventsValLabel = new Label("0");

        stats.getChildren().addAll(
                statCard("▣", "Total Events Created", totalEventsValLabel),
                statCard("→", "Upcoming / Active", upcomingEventsValLabel),
                statCard("⏳", "Pending Approval", pendingEventsValLabel)
        );

        // LOWER CONTENT
        HBox lower = new HBox(14);
        VBox.setVgrow(lower, Priority.ALWAYS);

        VBox previous = new VBox(12);
        previous.setPadding(new Insets(16));
        previous.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");
        VBox.setVgrow(previous, Priority.ALWAYS);

        HBox previousHeader = new HBox();
        previousHeader.setAlignment(Pos.CENTER_LEFT);

        VBox previousTitle = new VBox(4,
                styledLabel("Your Events", 23, FontWeight.BOLD, TEXT),
                styledLabel("Your event history and status loaded from Firestore.", 14, FontWeight.NORMAL, "#667085")
        );
        previousHeader.getChildren().add(previousTitle);

        eventListContainer = new VBox(12);
        eventListContainer.getChildren().add(styledLabel("Loading your events...", 14, FontWeight.NORMAL, "#667085"));

        previous.getChildren().addAll(previousHeader, eventListContainer);
        HBox.setHgrow(previous, Priority.ALWAYS);

        // SIDE CARDS
        VBox sideCards = new VBox(14);
        sideCards.setPrefWidth(330);
        sideCards.setMinWidth(330);
        sideCards.setMaxWidth(330);

        sideCards.getChildren().addAll(
                aiPlanningCard(),
                calendarCard(),
                weatherCard() // प्रीमियम ब्लू थीम वेदर कार्ड
        );

        lower.getChildren().addAll(previous, sideCards);
        main.getChildren().addAll(welcome, stats, lower);
        VBox.setVgrow(lower, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background:" + BG + "; -fx-background-color:" + BG + ";");

        rightArea.setTop(topBar);
        rightArea.setCenter(scrollPane);

        root.setLeft(sidebar);
        root.setCenter(rightArea);

        loadCustomerEventsAsync();

        activeCustomerScene = new Scene(root, 1550, 785);
        return activeCustomerScene;
    }

    private void returnToDashboard() {
        ActiveCustomerDashboardC dashboardPage = new ActiveCustomerDashboardC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(dashboardPage.getactiveCustomerScene(null));
        }
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene(this::returnToDashboard));
        }
    }

    // ==========================================
    // MyEvents पेज उघडण्यासाठीची पद्धत
    // ==========================================
    private void openMyEvents() {
        MyEvents myEventsPage = new MyEvents(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(myEventsPage.getMyEventsScene(this::returnToDashboard));
        }
    }

    private VBox aiPlanningCard() {
        VBox card = new VBox(10);
        card.setPrefHeight(185);
        card.setMinHeight(185);
        card.setMaxHeight(185);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color:#0D3979; -fx-background-radius:12;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3,
                styledLabel("AI Planning", 20, FontWeight.BOLD, "#FFFFFF"),
                styledLabel("Smart event planning", 14, FontWeight.NORMAL, "#D9ECFF")
        );

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        Label aiIcon = styledLabel("✦", 27, FontWeight.BOLD, "#FFFFFF");

        header.getChildren().addAll(titleBox, space, aiIcon);

        Label description = styledLabel("Let AI help you plan your perfect event.", 14, FontWeight.NORMAL, "#E3F2FF");
        description.setWrapText(true);

        Button startPlanning = new Button("Start Planning  →");
        startPlanning.setPrefHeight(32);
        startPlanning.setMaxWidth(Double.MAX_VALUE);
        startPlanning.setStyle("-fx-background-color:#FFFFFF; -fx-text-fill:#0D3979; -fx-background-radius:7; -fx-font-family:Arial; -fx-font-size:10px; -fx-font-weight:bold; -fx-cursor:hand;");

        startPlanning.setOnAction(e -> {
            AiPlanningC aiPlanningPage = new AiPlanningC(uid, idToken);
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(aiPlanningPage.getAiPlanningScene());
            }
        });

        Region buttonSpace = new Region();
        VBox.setVgrow(buttonSpace, Priority.ALWAYS);

        card.getChildren().addAll(header, description, buttonSpace, startPlanning);
        return card;
    }

    private VBox calendarCard() {
        VBox card = new VBox(8);
        card.setPrefHeight(205);
        card.setMinHeight(205);
        card.setMaxHeight(205);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color:white; -fx-border-color:#D7DEE8; -fx-border-radius:12; -fx-background-radius:12;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        header.getChildren().addAll(
                styledLabel("Event Calendar", 16, FontWeight.BOLD, TEXT),
                space,
                styledLabel("August 2026", 10, FontWeight.NORMAL, "#667085")
        );

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER);

        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < days.length; i++) {
            Label day = styledLabel(days[i], 9, FontWeight.BOLD, "#667085");
            day.setPrefWidth(34);
            day.setPrefHeight(18);
            day.setAlignment(Pos.CENTER);
            grid.add(day, i, 0);
        }

        int[][] dates = {
                {0, 0, 0, 1, 2, 3, 4},
                {5, 6, 7, 8, 9, 10, 11},
                {12, 13, 14, 15, 16, 17, 18},
                {19, 20, 21, 22, 23, 24, 25},
                {26, 27, 28, 29, 30, 31, 0}
        };

        for (int row = 0; row < dates.length; row++) {
            for (int col = 0; col < dates[row].length; col++) {
                int value = dates[row][col];
                if (value == 0) continue;
                Label date = styledLabel(String.valueOf(value), 9, FontWeight.NORMAL, TEXT);
                date.setPrefWidth(34);
                date.setPrefHeight(22);
                date.setAlignment(Pos.CENTER);
                grid.add(date, col, row + 1);
            }
        }

        card.getChildren().addAll(header, grid);
        return card;
    }

    // =========================================================================
    // PREMIUM ATTRACTIVE BLUE THEME WEATHER CARD (इमेज लेआउट + रॉयल ब्लू थीम)
    // =========================================================================
    private VBox weatherCard() {
        VBox card = new VBox(10);
        card.setPrefHeight(200);
        card.setMinHeight(200);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(14));
        
        // Deep Royal Blue Gradient Styling
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #0D3979 0%, #154D9E 50%, #1F6AC8 100%); "
                + "-fx-background-radius: 12; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(13, 57, 121, 0.25), 10, 0, 0, 4);");

        // १. टॉप हेडर (Weather Alert + Location + Sun Icon)
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label weatherIcon = new Label("⛅");
        weatherIcon.setStyle("-fx-font-size: 20px;");

        VBox titleBox = new VBox(1);
        Label title = styledLabel("Weather Alert", 13, FontWeight.BOLD, "#FFFFFF");
        weatherLocationLabel = styledLabel("📍 Pune, Maharashtra", 10, FontWeight.NORMAL, "#D0E4FF");
        titleBox.getChildren().addAll(title, weatherLocationLabel);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Label sunDeco = new Label("☀️");
        sunDeco.setStyle("-fx-font-size: 16px; -fx-opacity: 0.9;");

        header.getChildren().addAll(weatherIcon, titleBox, headerSpace, sunDeco);

        // २. तापमान + स्थिती (उदा. 32°C Partly Cloudy)
        HBox tempConditionRow = new HBox(10);
        tempConditionRow.setAlignment(Pos.BASELINE_LEFT);
        tempConditionRow.setPadding(new Insets(2, 0, 2, 0));

        weatherTempLabel = new Label("28°C");
        weatherTempLabel.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        weatherTempLabel.setTextFill(Color.web("#FFFFFF"));

        weatherConditionLabel = styledLabel("Checking weather...", 12, FontWeight.NORMAL, "#E0F0FF");

        tempConditionRow.getChildren().addAll(weatherTempLabel, weatherConditionLabel);

        // ३. मेट्रिक्स (Humidity, Wind, Feels like - Translucent Blue Pills)
        HBox details = new HBox(8);
        details.setAlignment(Pos.CENTER_LEFT);

        weatherHumidityLabel = new Label("64%");
        weatherWindLabel = new Label("12 km/h");
        weatherFeelsLikeLabel = new Label("29°C");

        details.getChildren().addAll(
                createBlueWeatherMetric("💧", "Humidity", weatherHumidityLabel),
                createBlueWeatherMetric("💨", "Wind", weatherWindLabel),
                createBlueWeatherMetric("🌡️", "Feels like", weatherFeelsLikeLabel)
        );

        // ४. बॉटम ग्लास अलर्ट बॉक्स (Frosted Glass Container)
        weatherAlertBox = new HBox(8);
        weatherAlertBox.setAlignment(Pos.CENTER_LEFT);
        weatherAlertBox.setPadding(new Insets(6, 10, 6, 10));
        weatherAlertBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.16); -fx-background-radius: 8px;");

        Label alertIcon = new Label("ℹ️");
        alertIcon.setStyle("-fx-font-size: 12px;");

        VBox alertTextBox = new VBox(1);
        weatherAlertTitleLabel = styledLabel("Checking event forecast...", 10, FontWeight.BOLD, "#FFFFFF");
        weatherAlertSubLabel = styledLabel("Weather for your nearest active event", 9, FontWeight.NORMAL, "#CDE4FF");
        alertTextBox.getChildren().addAll(weatherAlertTitleLabel, weatherAlertSubLabel);

        weatherAlertBox.getChildren().addAll(alertIcon, alertTextBox);

        card.getChildren().addAll(header, tempConditionRow, details, weatherAlertBox);
        return card;
    }

    private HBox createBlueWeatherMetric(String icon, String label, Label valLabel) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(3, 7, 3, 7));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.13); -fx-background-radius: 6px;");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 10px;");

        VBox textBox = new VBox(0);
        Label titleLbl = styledLabel(label, 8, FontWeight.NORMAL, "#B8DCFF");
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        valLabel.setTextFill(Color.web("#FFFFFF"));

        textBox.getChildren().addAll(titleLbl, valLabel);
        box.getChildren().addAll(iconLbl, textBox);
        return box;
    }

    // =========================================================================
    // DYNAMIC WEATHER FETCHING FOR CUSTOMER UPCOMING EVENT
    // =========================================================================
    private void fetchCustomerWeatherAsync(String city, String eventDate) {
        new Thread(() -> {
            WeatherService service = new WeatherService();
            WeatherData data = service.getWeatherForEventDate(city, eventDate);

            Platform.runLater(() -> {
                if (data != null && weatherTempLabel != null) {
                    String cleanCity = (city != null && !city.isBlank()) ? city : "Pune";
                    String dateSuffix = (eventDate != null && !eventDate.isBlank()) ? " (" + eventDate + ")" : "";
                    
                    weatherLocationLabel.setText("📍 " + cleanCity + ", Maharashtra" + dateSuffix);

                    if (data.getTemperature() != 0) {
                        weatherTempLabel.setText(data.getTemperature() + "°C");
                    } else {
                        weatherTempLabel.setText("--°C");
                    }

                    weatherConditionLabel.setText(data.getAlertMessage());

                    if ("WARNING".equalsIgnoreCase(data.getAlertType())) {
                        weatherAlertBox.setStyle("-fx-background-color: rgba(255, 152, 0, 0.3); -fx-border-color: #FFA726; -fx-border-radius: 8px; -fx-background-radius: 8px;");
                        weatherAlertTitleLabel.setText("⚠️ Weather Alert for Event");
                        weatherAlertTitleLabel.setStyle("-fx-text-fill: #FFF2DF; -fx-font-weight: bold; -fx-font-size: 10px;");
                        weatherAlertSubLabel.setText("Rain or storm possible on event date.");
                        weatherAlertSubLabel.setStyle("-fx-text-fill: #FFE0B2; -fx-font-size: 9px;");
                    } else {
                        weatherAlertBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.16); -fx-background-radius: 8px;");
                        weatherAlertTitleLabel.setText("✓ Good weather expected");
                        weatherAlertTitleLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 10px;");
                        weatherAlertSubLabel.setText("Great day for your event!");
                        weatherAlertSubLabel.setStyle("-fx-text-fill: #D4EBFF; -fx-font-size: 9px;");
                    }
                }
            });
        }).start();
    }

    private void loadCustomerEventsAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                CustomerController controller = new CustomerController();
                CustomerProfile profile = controller.getCustomerProfile(uid);

                if (profile != null) {
                    Platform.runLater(() -> {
                        String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Customer";
                        welcomeNameLabel.setText("Welcome back, " + name + "!");
                        avatarLetterLabel.setText(name.substring(0, 1).toUpperCase());
                    });
                }

                return controller.getEventsByCustomerId(uid);
            }
        };

        task.setOnSucceeded(e -> {
            List<Event> events = task.getValue();

            Platform.runLater(() -> {
                if (eventListContainer == null) return;
                eventListContainer.getChildren().clear();

                String nearestEventCity = "Pune";
                String nearestEventDate = null;
                LocalDate nearestDate = null;
                LocalDate today = LocalDate.now();

                if (events == null || events.isEmpty()) {
                    eventListContainer.getChildren().add(styledLabel("No events found. Click '+ Create New Event' to plan one!", 14, FontWeight.NORMAL, "#667085"));
                    totalEventsValLabel.setText("0");
                    upcomingEventsValLabel.setText("0");
                    pendingEventsValLabel.setText("0");
                } else {
                    totalEventsValLabel.setText(String.valueOf(events.size()));

                    int pendingCount = 0;
                    int upcomingCount = 0;

                    for (Event ev : events) {
                        if ("PENDING".equalsIgnoreCase(ev.getStatus())) {
                            pendingCount++;
                        } else {
                            upcomingCount++;

                            // आगामी सर्वात जवळचा इव्हेंट शोधणे
                            if (ev.getEventDate() != null && !ev.getEventDate().isBlank()) {
                                try {
                                    LocalDate parsedDate = LocalDate.parse(ev.getEventDate().trim());
                                    if (!parsedDate.isBefore(today)) {
                                        if (nearestDate == null || parsedDate.isBefore(nearestDate)) {
                                            nearestDate = parsedDate;
                                            nearestEventDate = ev.getEventDate().trim();
                                            if (ev.getCity() != null && !ev.getCity().isBlank()) {
                                                nearestEventCity = ev.getCity().trim();
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    if (nearestEventDate == null) {
                                        nearestEventDate = ev.getEventDate().trim();
                                        if (ev.getCity() != null && !ev.getCity().isBlank()) {
                                            nearestEventCity = ev.getCity().trim();
                                        }
                                    }
                                }
                            }
                        }

                        String loc = ev.getCity();
                        if (ev.getVenueType() != null && !ev.getVenueType().isBlank()) {
                            loc = ev.getVenueType() + (loc != null && !loc.isBlank() ? ", " + loc : "");
                        }
                        if (loc == null || loc.isBlank()) loc = "Venue Pending";

                        String eventName = ev.getEventName() != null && !ev.getEventName().isBlank() ? ev.getEventName() : "Unnamed Event";
                        String eventDate = ev.getEventDate() != null && !ev.getEventDate().isBlank() ? ev.getEventDate() : "TBD";

                        eventListContainer.getChildren().add(eventCard(eventName, eventDate, loc, ev.getStatus()));
                    }

                    upcomingEventsValLabel.setText(String.valueOf(upcomingCount));
                    pendingEventsValLabel.setText(String.valueOf(pendingCount));
                }

                // जर जुनेच इव्हेंट्स असतील तर आजची तारीख वापरणे
                if (nearestEventDate == null) {
                    nearestEventDate = today.toString();
                }

                // आगामी इव्हेंटचे हवामान आणणे
                fetchCustomerWeatherAsync(nearestEventCity, nearestEventDate);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                if (eventListContainer == null) return;
                eventListContainer.getChildren().clear();
                eventListContainer.getChildren().add(styledLabel("Unable to load events.", 14, FontWeight.NORMAL, "#B42318"));
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox eventCard(String name, String date, String location, String status) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(112);
        card.setMinHeight(112);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:#F9FBFD; -fx-border-color:" + BORDER + "; -fx-border-radius:9; -fx-background-radius:9;");

        Circle circle = new Circle(27, Color.web("#E1F3FF"));
        StackPane icon = new StackPane(circle, styledLabel("✦", 18, FontWeight.BOLD, "#1D5274"));

        VBox details = new VBox(5);
        details.getChildren().addAll(
                styledLabel(name, 17, FontWeight.BOLD, TEXT),
                styledLabel("Date: " + date, 12, FontWeight.NORMAL, "#596273"),
                styledLabel("Location: " + location, 12, FontWeight.NORMAL, "#596273")
        );

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        String finalStatus = (status != null && !status.isBlank()) ? status : "PENDING";
        String badgeColor = "PENDING".equalsIgnoreCase(finalStatus) ? "#D97706" : "#16A34A";

        Label statusLbl = styledLabel("● " + finalStatus, 14, FontWeight.BOLD, badgeColor);
        card.getChildren().addAll(icon, details, space, statusLbl);
        return card;
    }

    private VBox statCard(String icon, String title, Label valueLabel) {
        VBox card = new VBox();
        card.setPrefHeight(70);
        card.setPadding(new Insets(9));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:9; -fx-background-radius:9;");

        HBox row = new HBox(9);
        row.setAlignment(Pos.CENTER_LEFT);

        Circle circle = new Circle(17, Color.web("#E0F2FF"));
        StackPane iconBox = new StackPane(circle, styledLabel(icon, 13, FontWeight.BOLD, NAVY));

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web(NAVY));

        VBox text = new VBox(1);
        text.getChildren().addAll(styledLabel(title, 12, FontWeight.NORMAL, "#667085"), valueLabel);

        row.getChildren().addAll(iconBox, text);
        card.getChildren().add(row);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox menuItem(String icon, String text, boolean active) {
        return menuItem(icon, text, active, null);
    }

    private HBox menuItem(String icon, String text, boolean active, Runnable action) {
        HBox item = new HBox(11);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPrefHeight(40);
        item.setPadding(new Insets(0, 11, 0, 13));

        if (active) {
            item.setStyle("-fx-background-color:" + LIGHT_BLUE + "; -fx-background-radius:7;");
        } else {
            item.setStyle("-fx-background-color:transparent;");
        }

        if (action != null) {
            item.setStyle(item.getStyle() + "-fx-cursor:hand;");
            item.setOnMouseClicked(e -> action.run());
        }

        item.getChildren().addAll(
                styledLabel(icon, 16, FontWeight.NORMAL, active ? NAVY : "#D1D9E8"),
                styledLabel(text, 12, active ? FontWeight.BOLD : FontWeight.NORMAL, active ? "#35556E" : "#D1D9E8")
        );
        return item;
    }

    private Label styledLabel(String text, double size, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }
}