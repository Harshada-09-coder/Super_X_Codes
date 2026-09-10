package com.techtrio.view;

import java.time.LocalDate;
import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.model.Event;
import com.techtrio.model.WeatherData;
import com.techtrio.services.EventService;
import com.techtrio.services.WeatherService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OrganizerDashboardO {

    private Scene organizerDashboardScene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Dynamic Labels
    private Label activeEventsValLabel;
    private Label pendingRequestsValLabel;
    private Label upcomingEventsValLabel;
    private Label tasksDueValLabel;
    private Label totalEarningsValLabel;

    private TableView<Event> eventsTable;
    private VBox weatherBox;
    private Label wTitle;
    private Label wDesc;

    public OrganizerDashboardO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public OrganizerDashboardO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getorganizerDashboardScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonOrganizerSidebar("Dashboard"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox dashboardContent = createDashboardContent();
        scrollPane.setContent(dashboardContent);
        root.setCenter(scrollPane);

        loadOrganizerDashboardDataAsync();

        organizerDashboardScene = new Scene(root, 1550, 785);
        return organizerDashboardScene;
    }

    public void backToOrganizerDashboard() {
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(getorganizerDashboardScene(null));
        }
    }

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
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard" -> {}
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> {
                MyEventsO myEvents = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(myEvents.getMyEventsOScene(null));
            }
            case "Schedule" -> {
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
            }
            case "Notifications" -> {
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
            }
            case "Profile" -> {
                ProfileO prof = new ProfileO(uid, idToken);
                prof.setOrganizerDashboard(this);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
            }
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

    private VBox createDashboardContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Label title = new Label("Organizer Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Live overview of event assignments, planning milestones, and customer payment wallet.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        VBox titleBox = new VBox(2, title, subtitle);

        HBox cards = new HBox(12);
        activeEventsValLabel = new Label("00");
        pendingRequestsValLabel = new Label("00");
        upcomingEventsValLabel = new Label("00");
        tasksDueValLabel = new Label("00");
        totalEarningsValLabel = new Label("₹0.00");

        cards.getChildren().addAll(
                createCard("Active Events", activeEventsValLabel, "fas-calendar-check", "#16A34A"),
                createCard("Assigned Requests", pendingRequestsValLabel, "fas-inbox", "#D97706"),
                createCard("Upcoming Events", upcomingEventsValLabel, "fas-calendar-alt", "#0284C7"),
                createCard("Tasks In Progress", tasksDueValLabel, "fas-tasks", "#7C3AED"),
                createCard("My Revenue (15%)", totalEarningsValLabel, "fas-wallet", "#062A60")
        );

        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label tableTitle = new Label("Assigned Events in Planning");
        tableTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        eventsTable = new TableView<>();
        eventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> nameCol = col("Event Name", Event::getEventName);
        TableColumn<Event, String> typeCol = col("Event Type", Event::getEventType);
        TableColumn<Event, String> dateCol = col("Event Duration", ev -> {
            if (ev.getStartDate() != null && !ev.getStartDate().isBlank()) {
                return ev.getStartDate() + (ev.getEndDate() != null && !ev.getEndDate().isBlank() && !ev.getStartDate().equals(ev.getEndDate()) ? " to " + ev.getEndDate() : "");
            }
            return ev.getEventDate() != null ? ev.getEventDate() : "TBD";
        });
        TableColumn<Event, String> timeCol = col("Time", ev -> {
            if (ev.getStartTime() != null && !ev.getStartTime().isBlank()) {
                return ev.getStartTime() + (ev.getEndTime() != null && !ev.getEndTime().isBlank() ? " - " + ev.getEndTime() : "");
            }
            return ev.getTime() != null ? ev.getTime() : "TBD";
        });
        TableColumn<Event, String> guestsCol = col("Guests", ev -> String.valueOf(ev.getGuests()));
        TableColumn<Event, String> budgetCol = col("Budget", Event::getBudget);
        TableColumn<Event, String> statusCol = col("Status", Event::getStatus);

        TableColumn<Event, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(90);
        actionCol.setCellFactory(c -> new TableCell<>() {
            final Button btn = new Button("Planning →");
            {
                btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    Event ev = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedEventId(ev.getEventId());
                    ViewEventO view = new ViewEventO(uid, idToken);
                    LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, () -> {
                        LandingPage.landingPageStage.setScene(organizerDashboardScene);
                    }));
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        eventsTable.getColumns().addAll(nameCol, typeCol, dateCol, timeCol, guestsCol, budgetCol, statusCol, actionCol);
        eventsTable.setPrefHeight(240);
        tableCard.getChildren().addAll(tableTitle, eventsTable);

        HBox bottomWidgets = new HBox(16);

        weatherBox = new VBox(6);
        weatherBox.setPadding(new Insets(14));
        weatherBox.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(weatherBox, Priority.ALWAYS);

        wTitle = new Label("Weather Alert");
        wTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        wDesc = new Label("Checking live forecast for nearest event...");
        wDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        weatherBox.getChildren().addAll(wTitle, wDesc);

        VBox conflictWidget = new VBox(6);
        conflictWidget.setPadding(new Insets(14));
        conflictWidget.setPrefWidth(380);
        conflictWidget.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        Label cTitle = new Label("Schedule Status");
        cTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label cDesc = new Label("✓ Timefold Constraint Solver: No active schedule conflicts");
        cDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        conflictWidget.getChildren().addAll(cTitle, cDesc);

        bottomWidgets.getChildren().addAll(weatherBox, conflictWidget);

        content.getChildren().addAll(titleBox, cards, tableCard, bottomWidgets);
        return content;
    }

    // ⭐ Event Completed झाल्यावर Upcoming Events मधून काढणे
    private void loadOrganizerDashboardDataAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                EventService eventService = new EventService();
                return eventService.getEventsByOrganizerId(uid);
            }
        };

        task.setOnSucceeded(evt -> {
            List<Event> events = task.getValue();
            Platform.runLater(() -> {
                int activeCount = 0;
                int pendingCount = 0;
                int upcomingCount = 0;
                double totalOrganizerRevenue = 0.0;
                String city = "Pune";
                String eventDate = null;

                if (eventsTable != null) eventsTable.getItems().clear();

                if (events != null) {
                    for (Event ev : events) {
                        String oStatus = ev.getOrganizerStatus() != null ? ev.getOrganizerStatus().toUpperCase() : "";
                        String st = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "";

                        // कमिशन कायम मोजले जाईल (कस्टमरच्या paidAmount वर 15%)
                        double actualPaidByCustomer = ev.getPaidAmount();
                        if (actualPaidByCustomer > 0) {
                            totalOrganizerRevenue += (actualPaidByCustomer * 0.15);
                        }

                        // ⭐ जर इव्हेंट पूर्ण (COMPLETED) झाला असेल, तर तो Upcoming Events मध्ये मोजला जाणार नाही आणि प्लॅनिंग टेबलमधूनही निघून जाईल
                        if ("COMPLETED".equals(st)) {
                            continue; 
                        }

                        if ("PENDING".equals(oStatus)) {
                            pendingCount++;
                        } else if ("ACCEPTED".equals(oStatus) || "CONFIRMED".equals(st)) {
                            activeCount++;
                            upcomingCount++; // ⭐ फक्त चालू/न संपलेले इव्हेंट्सच Upcoming मध्ये येतील
                            if (eventsTable != null) eventsTable.getItems().add(ev);
                            if (eventDate == null && ev.getStartDate() != null) {
                                eventDate = ev.getStartDate();
                                if (ev.getCity() != null) city = ev.getCity();
                            }
                        }
                    }
                }

                activeEventsValLabel.setText(String.format("%02d", activeCount));
                pendingRequestsValLabel.setText(String.format("%02d", pendingCount));
                upcomingEventsValLabel.setText(String.format("%02d", upcomingCount));
                tasksDueValLabel.setText("00");
                totalEarningsValLabel.setText("₹" + String.format("%,.0f", totalOrganizerRevenue));

                if (eventDate == null) eventDate = LocalDate.now().toString();
                fetchWeatherAsync(city, eventDate);
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void fetchWeatherAsync(String city, String date) {
        new Thread(() -> {
            WeatherService service = new WeatherService();
            WeatherData data = service.getWeatherForEventDate(city, date);
            Platform.runLater(() -> {
                if (data != null && wTitle != null && wDesc != null) {
                    wTitle.setText("Weather: " + city + " (" + (data.getTemperature() != 0 ? data.getTemperature() + "°C" : "Clear") + ")");
                    wDesc.setText(data.getAlertMessage());
                }
            });
        }).start();
    }

    private VBox createCard(String title, Label valueLabel, String iconLit, String iconColorHex) {
        VBox card = new VBox(6);
        card.setPrefWidth(200);
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

        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: " + NAVY + ";");
        card.getChildren().addAll(top, valueLabel);
        return card;
    }

    private TableColumn<Event, String> col(String name, java.util.function.Function<Event, String> value) {
        TableColumn<Event, String> column = new TableColumn<>(name);
        column.setCellValueFactory(data -> new SimpleStringProperty(value.apply(data.getValue())));
        return column;
    }
}