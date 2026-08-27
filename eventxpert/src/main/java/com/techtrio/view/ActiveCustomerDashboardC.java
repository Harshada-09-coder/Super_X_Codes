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

    public ActiveCustomerDashboardC() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ActiveCustomerDashboardC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }

    public Scene getactiveCustomerScene(Runnable callBackAction1) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

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
        menu.getChildren().addAll(
                menuItem("▦", "Dashboard", true),
                menuItem("⊕", "Create Event", false, this::openCreateEvent),
                menuItem("▣", "My Events", false),
                menuItem("□", "Calendar", false),
                menuItem("♧", "Notifications", false)
        );

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(5);
        bottomMenu.setPadding(new Insets(8, 12, 18, 12));
        bottomMenu.getChildren().addAll(
                menuItem("◉", "Profile", false),
                menuItem("⚙", "Settings", false),
                menuItem("?", "Support", false)
        );

        sidebar.getChildren().addAll(logoBox, menu, sidebarSpace, bottomMenu);

        BorderPane rightArea = new BorderPane();

        HBox topBar = new HBox(12);
        topBar.setPrefHeight(64);
        topBar.setMinHeight(64);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E0E6EF;-fx-border-width:0 0 1 0;");

        TextField search = new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(330);
        search.setPrefHeight(36);
        search.setStyle("-fx-background-color:#F7F9FC;-fx-border-color:#D2DAE5;-fx-border-radius:18;-fx-background-radius:18;-fx-padding:0 15;-fx-font-size:12px;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Label notification = styledLabel("♧", 19, FontWeight.NORMAL, NAVY);
        Circle avatarCircle = new Circle(18, Color.web("#C9E8FA"));
        Label avatarLetter = styledLabel("C", 12, FontWeight.BOLD, NAVY);
        StackPane avatar = new StackPane(avatarCircle, avatarLetter);

        topBar.getChildren().addAll(search, topSpace, notification, avatar);

        VBox main = new VBox(12);
        main.setPadding(new Insets(15, 20, 15, 20));
        main.setStyle("-fx-background-color:" + BG + ";");

        HBox welcome = new HBox();
        welcome.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(3);
        welcomeText.getChildren().addAll(
                styledLabel("Welcome back!", 23, FontWeight.BOLD, NAVY),
                styledLabel("Here's a look at your events.", 11, FontWeight.NORMAL, "#596273")
        );

        Region welcomeSpace = new Region();
        HBox.setHgrow(welcomeSpace, Priority.ALWAYS);

        Button createEvent = new Button("+  Create New Event");
        createEvent.setPrefSize(175, 36);
        createEvent.setStyle("-fx-background-color:" + NAVY + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");
        createEvent.setOnAction(e -> openCreateEvent());

        welcome.getChildren().addAll(welcomeText, welcomeSpace, createEvent);

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

        HBox lower = new HBox(14);
        VBox.setVgrow(lower, Priority.ALWAYS);

        VBox previous = new VBox(12);
        previous.setPadding(new Insets(16));
        previous.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:12;-fx-background-radius:12;");
        VBox.setVgrow(previous, Priority.ALWAYS);

        HBox previousHeader = new HBox();
        previousHeader.setAlignment(Pos.CENTER_LEFT);

        VBox previousTitle = new VBox(4);
        previousTitle.getChildren().addAll(
                styledLabel("Your Events", 23, FontWeight.BOLD, TEXT),
                styledLabel("Your event history and status loaded from Firestore.", 14, FontWeight.NORMAL, "#667085")
        );

        previousHeader.getChildren().addAll(previousTitle);

        eventListContainer = new VBox(12);
        eventListContainer.getChildren().add(styledLabel("Loading your events...", 14, FontWeight.NORMAL, "#667085"));

        previous.getChildren().addAll(previousHeader, eventListContainer);
        HBox.setHgrow(previous, Priority.ALWAYS);

        lower.getChildren().add(previous);

        main.getChildren().addAll(welcome, stats, lower);
        rightArea.setTop(topBar);
        rightArea.setCenter(main);

        root.setLeft(sidebar);
        root.setCenter(rightArea);

        loadCustomerEventsAsync();

        Scene scene = new Scene(root, 1550, 785);
        activeCustomerScene = scene;
        return activeCustomerScene;
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene());
        }
    }

    private void loadCustomerEventsAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                CustomerController controller = new CustomerController();
                return controller.getEventsByCustomerId(uid);
            }
        };

        task.setOnSucceeded(e -> {
            List<Event> events = task.getValue();
            Platform.runLater(() -> {
                eventListContainer.getChildren().clear();

                if (events == null || events.isEmpty()) {
                    eventListContainer.getChildren().add(styledLabel("No events found. Click '+ Create New Event' to plan one!", 14, FontWeight.NORMAL, "#667085"));
                    totalEventsValLabel.setText("0");
                    upcomingEventsValLabel.setText("0");
                    pendingEventsValLabel.setText("0");
                    return;
                }

                totalEventsValLabel.setText(String.valueOf(events.size()));
                int pendingCount = 0;
                int upcomingCount = 0;

                for (Event ev : events) {
                    if ("PENDING".equalsIgnoreCase(ev.getStatus())) {
                        pendingCount++;
                    } else {
                        upcomingCount++;
                    }

                    String loc = ev.getCity();
                    if (ev.getVenueType() != null && !ev.getVenueType().isBlank()) {
                        loc = ev.getVenueType() + (loc != null && !loc.isBlank() ? ", " + loc : "");
                    }

                    eventListContainer.getChildren().add(
                            eventCard(
                                    ev.getEventName(),
                                    ev.getEventDate() != null && !ev.getEventDate().isBlank() ? ev.getEventDate() : "TBD",
                                    loc != null && !loc.isBlank() ? loc : "Venue Pending",
                                    ev.getStatus()
                            )
                    );
                }

                upcomingEventsValLabel.setText(String.valueOf(upcomingCount));
                pendingEventsValLabel.setText(String.valueOf(pendingCount));
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
        card.setStyle("-fx-background-color:#F9FBFD;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");

        Circle circle = new Circle(27, Color.web("#E1F3FF"));
        StackPane icon = new StackPane(circle, styledLabel("✦", 18, FontWeight.BOLD, "#1D5274"));

        VBox details = new VBox(5);
        details.getChildren().addAll(
                styledLabel(name, 17, FontWeight.BOLD, TEXT),
                styledLabel("📅  " + date, 12, FontWeight.NORMAL, "#596273"),
                styledLabel("📍  " + location, 12, FontWeight.NORMAL, "#596273")
        );

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        String badgeColor = "PENDING".equalsIgnoreCase(status) ? "#D97706" : "#16A34A";
        Label statusLbl = styledLabel("● " + (status != null ? status : "PENDING"), 14, FontWeight.BOLD, badgeColor);

        card.getChildren().addAll(icon, details, space, statusLbl);
        return card;
    }

    private VBox statCard(String icon, String title, Label valueLabel) {
        VBox card = new VBox();
        card.setPrefHeight(70);
        card.setPadding(new Insets(9));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");

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
            item.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-background-radius:7;");
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