package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.Event;
import com.techtrio.services.EventService;
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

public class MyEventsO {

    private Scene MyEventsOScene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String BORDER_COLOR = "#DCE5F0";

    private Label lblActiveEvents;
    private Label lblUpcomingEvents;
    private Label lblCompletedEvents;
    private TableView<Event> confirmedEventsTable;

    public MyEventsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public MyEventsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getMyEventsOScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Sidebar on Left
        root.setLeft(createCommonOrganizerSidebar("My Events"));

        // 2. Main Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 32, 28, 32));

        Label title = new Label("My Events (Planning & Execution)");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Manage confirmed customer events, monitor Timefold timelines, and execute live services.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        VBox titleBox = new VBox(2, title, subtitle);

        // 3 STAT CARDS
        HBox metricCardsRow = new HBox(14);
        lblActiveEvents = new Label("00");
        lblUpcomingEvents = new Label("00");
        lblCompletedEvents = new Label("00");

        metricCardsRow.getChildren().addAll(
                createCard("Confirmed Events", lblActiveEvents, "fas-calendar-check", "#16A34A"),
                createCard("In Planning", lblUpcomingEvents, "fas-tasks", "#0284C7"),
                createCard("Completed", lblCompletedEvents, "fas-award", "#7C3AED")
        );

        // CONFIRMED EVENTS TABLE VIEW
        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label tableTitle = new Label("Active Confirmed & Completed Events");
        tableTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        confirmedEventsTable = new TableView<>();
        confirmedEventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
            final Button planBtn = new Button("Open Plan →");
            {
                planBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-cursor: hand;");
                planBtn.setOnAction(e -> {
                    Event ev = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedEventId(ev.getEventId());
                    ViewEventO view = new ViewEventO(uid, idToken);
                    LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, () -> {
                        LandingPage.landingPageStage.setScene(MyEventsOScene);
                    }));
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : planBtn);
            }
        });

        confirmedEventsTable.getColumns().addAll(nameCol, typeCol, dateCol, timeCol, guestsCol, budgetCol, statusCol, actionCol);
        confirmedEventsTable.setPrefHeight(340);
        tableCard.getChildren().addAll(tableTitle, confirmedEventsTable);

        content.getChildren().addAll(titleBox, metricCardsRow, tableCard);
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        loadAcceptedEventsFromFirestoreAsync();

        MyEventsOScene = new Scene(root, 1550, 785);
        return MyEventsOScene;
    }

    // ⭐ ACCURATE EVENT COUNT CALCULATION
    private void loadAcceptedEventsFromFirestoreAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                EventService eventService = new EventService();
                return eventService.getEventsByOrganizerId(uid);
            }
        };

        task.setOnSucceeded(evt -> {
            List<Event> assignedList = task.getValue();
            Platform.runLater(() -> {
                int activeCount = 0, upcomingCount = 0, completedCount = 0;
                if (confirmedEventsTable != null) confirmedEventsTable.getItems().clear();

                if (assignedList != null) {
                    for (Event ev : assignedList) {
                        String oStatus = ev.getOrganizerStatus() != null ? ev.getOrganizerStatus().trim().toUpperCase() : "";
                        String status = ev.getStatus() != null ? ev.getStatus().trim().toUpperCase() : "";

                        if ("ACCEPTED".equals(oStatus) || "CONFIRMED".equals(status) || "COMPLETED".equals(status)) {
                            if ("COMPLETED".equals(status)) {
                                completedCount++;
                            } else {
                                activeCount++;
                                upcomingCount++;
                            }
                            if (confirmedEventsTable != null) confirmedEventsTable.getItems().add(ev);
                        }
                    }
                }

                lblActiveEvents.setText(String.format("%02d", activeCount));
                lblUpcomingEvents.setText(String.format("%02d", upcomingCount));
                lblCompletedEvents.setText(String.format("%02d", completedCount));
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
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
            case "Dashboard" -> {
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
            }
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> {}
            case "Venue" -> {
                VenueManagementO venue = new VenueManagementO();
                LandingPage.landingPageStage.setScene(venue.getScene(LandingPage.landingPageStage, null));
            }
            case "Vendors" -> {
                VendorManagementO vendor = new VendorManagementO(uid, idToken);
                LandingPage.landingPageStage.setScene(vendor.getvendorManagementOScene(null));
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

    private VBox createCard(String title, Label valueLabel, String iconLit, String iconColorHex) {
        VBox card = new VBox(6);
        card.setPrefWidth(220);
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

        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: " + NAVY + ";");
        card.getChildren().addAll(top, valueLabel);
        return card;
    }

    private TableColumn<Event, String> col(String name, java.util.function.Function<Event, String> value) {
        TableColumn<Event, String> column = new TableColumn<>(name);
        column.setCellValueFactory(data -> new SimpleStringProperty(value.apply(data.getValue())));
        return column;
    }
}