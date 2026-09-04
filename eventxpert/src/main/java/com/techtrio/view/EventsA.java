package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class EventsA {

    private Scene eventsScene;
    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BACKGROUND = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private TableView<Event> table;
    private ObservableList<Event> eventDataList = FXCollections.observableArrayList();

    private Label totalEventsVal;
    private Label upcomingEventsVal;
    private Label ongoingEventsVal;
    private Label completedEventsVal;

    public Scene geteventsScene() {
        // SIDEBAR
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.web(LIGHT));
        admin.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        brandTexts.getChildren().addAll(logo, admin);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"Events", "fas-calendar-check"},
                {"Calendar", "fas-calendar"},
                {"Customers", "fas-users"},
                {"Organizers", "fas-user-tie"},
                {"Vendors", "fas-truck"},
                {"Conflict Detection", "fas-shield-alt"},
                {"Reports & Analytics", "fas-chart-pie"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals("Events")));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        // SIDEBAR BOTTOM ITEMS WITH NAVIGATION
        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false),
                createSidebarButton("Profile", "fas-user", false),
                createSidebarButton("Settings", "fas-cog", false),
                createSidebarButton("Support", "fas-question-circle", false)
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);

        // MAIN CONTENT (TOPBAR REMOVED)
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BACKGROUND + ";");

        Label title = new Label("Events");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Manage and monitor all scheduled customer events across organizers.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));
        VBox heading = new VBox(2, title, sub);

        // STAT CARDS
        totalEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        ongoingEventsVal = new Label("0");
        completedEventsVal = new Label("0");

        HBox cards = new HBox(16,
                card("Total Events", totalEventsVal, "fas-calendar-alt", "#0284C7"),
                card("Confirmed", upcomingEventsVal, "fas-check-circle", "#16A34A"),
                card("In Progress", ongoingEventsVal, "fas-clock", "#D97706"),
                card("Completed", completedEventsVal, "fas-award", "#7C3AED"));

        // FILTERS
        TextField search = new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(220);
        search.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Status", "Upcoming", "Ongoing", "Completed", "Cancelled");
        status.setValue("Status");
        status.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;");

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Event Types", "Corporate", "Birthday", "Educational", "Family", "Wedding");
        type.setValue("Event Types");
        type.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;");

        Button clear = new Button("Clear Filters");
        clear.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-font-weight:bold;-fx-cursor:hand;");
        clear.setOnAction(e -> {
            search.clear();
            status.setValue("Status");
            type.setValue("Event Types");
        });

        HBox filters = new HBox(12, search, status, type, clear);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(12, 16, 12, 16));
        filters.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-background-radius:10;-fx-border-radius:10;");

        createTable();

        VBox tableBox = new VBox(table);
        tableBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;-fx-padding:14;");
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(heading, cards, filters, tableBox);
        VBox.setVgrow(content, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BACKGROUND + ";-fx-border-color:transparent;");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scroll);

        loadEventsDataAsync();

        eventsScene = new Scene(root, 1550, 785);
        return eventsScene;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(PRIMARY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT + ";-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }

    private void createTable() {
        table = new TableView<>();
        table.setItems(eventDataList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> customerCol = col("Customer", Event::getCustomerId);
        TableColumn<Event, String> eventNameCol = col("Event Name", Event::getEventName);
        TableColumn<Event, String> typeCol = col("Event Type", Event::getEventType);

        TableColumn<Event, String> dateCol = col("Event Duration", ev -> {
            if (ev.getStartDate() != null && !ev.getStartDate().isBlank()) {
                return ev.getStartDate() + (ev.getEndDate() != null && !ev.getEndDate().isBlank() && !ev.getStartDate().equals(ev.getEndDate()) ? " to " + ev.getEndDate() : "");
            }
            return ev.getEventDate() != null ? ev.getEventDate() : "TBD";
        });

        TableColumn<Event, String> guestsCol = col("Guests", ev -> String.valueOf(ev.getGuests()));
        TableColumn<Event, String> locationCol = col("Location", ev -> ev.getCity() != null ? ev.getCity() : "N/A");

        TableColumn<Event, String> organizerCol = col("Assigned Organizer", ev ->
                ev.getOrganizerName() != null && !ev.getOrganizerName().isBlank()
                        ? ev.getOrganizerName() : "Not Assigned"
        );

        TableColumn<Event, String> budgetCol = col("Budget", Event::getBudget);
        TableColumn<Event, String> statusCol = col("Status", Event::getStatus);

        TableColumn<Event, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(90);
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button b = new Button("View →");
            {
                b.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:11px;-fx-padding:5 12;-fx-background-radius:6;-fx-cursor:hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                b.setOnAction(e -> {
                    Event selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedEventId(selected.getEventId());
                    Stage stage = (Stage) b.getScene().getWindow();
                    NavigationManager.navigate("Event Details", stage);
                });
                setGraphic(b);
            }
        });

        table.getColumns().addAll(customerCol, eventNameCol, typeCol, dateCol, guestsCol, locationCol, organizerCol, budgetCol, statusCol, actionCol);
    }

    private TableColumn<Event, String> col(String title, java.util.function.Function<Event, String> f) {
        TableColumn<Event, String> c = new TableColumn<>(title);
        c.setCellValueFactory(x -> new SimpleStringProperty(f.apply(x.getValue())));
        return c;
    }

    private void loadEventsDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<Event> events = controller.getAllEvents();
                List<CustomerProfile> customers = controller.getAllCustomerProfiles();

                Platform.runLater(() -> {
                    eventDataList.clear();
                    int upcoming = 0;
                    int ongoing = 0;
                    int completed = 0;

                    for (Event ev : events) {
                        String st = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "PENDING";
                        if ("COMPLETED".equals(st)) {
                            completed++;
                        } else if ("CONFIRMED".equals(st) || "ASSIGNED".equals(st)) {
                            upcoming++;
                        } else {
                            ongoing++;
                        }

                        CustomerProfile cust = customers.stream()
                                .filter(c -> c.getCustomerId() != null && c.getCustomerId().equals(ev.getCustomerId()))
                                .findFirst().orElse(null);

                        if (cust != null && cust.getName() != null && !cust.getName().isBlank()) {
                            ev.setCustomerId(cust.getName());
                        }

                        eventDataList.add(ev);
                    }

                    totalEventsVal.setText(String.valueOf(events.size()));
                    upcomingEventsVal.setText(String.valueOf(upcoming));
                    ongoingEventsVal.setText(String.valueOf(ongoing));
                    completedEventsVal.setText(String.valueOf(completed));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox card(String title, Label valueLabel, String iconLit, String iconColorHex) {
        VBox card = new VBox(6);
        card.setPrefWidth(220);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
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
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        titleLabel.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(iconCircle, titleLabel);

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web(PRIMARY));

        card.getChildren().addAll(top, valueLabel);
        return card;
    }
}