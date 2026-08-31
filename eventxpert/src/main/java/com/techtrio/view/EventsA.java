package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;

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
    final String P = "#002A60", S = "#8CAFC6", L = "#C2E0F4";

    private TableView<Event> table;
    private ObservableList<Event> eventDataList = FXCollections.observableArrayList();

    private Label totalEventsVal;
    private Label upcomingEventsVal;
    private Label ongoingEventsVal;
    private Label completedEventsVal;

    Label label(String t, int size, boolean bold) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        return l;
    }

    Button button(String t, boolean primary) {
        Button b = new Button(t);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setPrefHeight(38);
        b.setStyle(primary
                ? "-fx-background-color:" + P + ";-fx-text-fill:white;-fx-background-radius:7;-fx-cursor:hand;"
                : "-fx-background-color:white;-fx-text-fill:" + P + ";-fx-border-color:#B8C7D8;-fx-border-radius:7;-fx-cursor:hand;");
        return b;
    }

    VBox card(String title, Label valLabel, String icon) {
        Label t = label(title, 14, false);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        valLabel.setTextFill(Color.web(P));

        Label i = label(icon, 18, true);
        i.setTextFill(Color.web(P));
        StackPane sp = new StackPane(i);
        sp.setPrefSize(38, 38);
        sp.setStyle("-fx-background-color:#EAF0F5;-fx-background-radius:20;");

        HBox top = new HBox(t, new Region(), sp);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);

        VBox box = new VBox(15, top, valLabel);
        box.setPadding(new Insets(18, 20, 15, 20));
        box.setPrefWidth(245);
        box.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;-fx-background-radius:8;-fx-border-radius:8;");
        return box;
    }

    void createTable() {
        table = new TableView<>();
        table.setItems(eventDataList);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(390);

        TableColumn<Event, String> customerCol = col("Customer", Event::getCustomerId);
        TableColumn<Event, String> eventNameCol = col("Event Name", Event::getEventName);
        TableColumn<Event, String> typeCol = col("Event Type", Event::getEventType);
        TableColumn<Event, String> dateCol = col("Event Date", Event::getEventDate);
        TableColumn<Event, String> guestsCol = col("Guests", ev -> String.valueOf(ev.getGuests()));
        TableColumn<Event, String> locationCol = col("Location", ev -> ev.getCity() != null ? ev.getCity() : "N/A");

        // SHOW ORGANIZER ORGANIZATION NAME INSTEAD OF ID
        TableColumn<Event, String> organizerCol = col("Organizer", ev ->
                ev.getOrganizerName() != null && !ev.getOrganizerName().isBlank()
                        ? ev.getOrganizerName() : "Not Assigned"
        );

        TableColumn<Event, String> budgetCol = col("Budget", Event::getBudget);
        TableColumn<Event, String> statusCol = col("Status", Event::getStatus);

        TableColumn<Event, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button b = button("View", false);

            {
                b.setPrefHeight(30);
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

    TableColumn<Event, String> col(String title, java.util.function.Function<Event, String> f) {
        TableColumn<Event, String> c = new TableColumn<>(title);
        c.setCellValueFactory(x -> new SimpleStringProperty(f.apply(x.getValue())));
        return c;
    }

    public Scene geteventsScene() {
        VBox sidebar = new VBox(7);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25, 18, 18, 18));
        sidebar.setStyle("-fx-background-color:" + P + ";");

        Label logo = label("EventXpert", 26, true);
        Label admin = label(" ADMIN DASHBOARD", 12, true);
        logo.setTextFill(Color.WHITE); admin.setTextFill(Color.WHITE);

        VBox menu = new VBox(7);
        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"};

        for (String s : items) {
            Label m = label(s, 14, true);
            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9, 13, 9, 13));

            if (s.equals("Events"))
                m.setStyle("-fx-background-color:#07539A;-fx-background-radius:5;");

            m.setOnMouseClicked(e -> {
                Stage stage = (Stage) m.getScene().getWindow();
                NavigationManager.navigate(s, stage);
            });

            menu.getChildren().add(m);
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        Label n = label("🔔  Notifications", 13, true);
        Label se = label("⚙  Settings", 13, true);
        Label pr = label("👤  Profile", 13, true);
        Label sp = label("?  Support", 13, true);

        for (Label x : new Label[]{n, se, pr, sp}) {
            x.setTextFill(Color.WHITE);
            x.setPadding(new Insets(7, 10, 7, 10));
        }

        sidebar.getChildren().addAll(new VBox(1, logo, admin), menu, space, n, se, pr, sp);

        Region hs = new Region();
        HBox.setHgrow(hs, Priority.ALWAYS);

        HBox header = new HBox(18, hs,
                label("🔔", 18, false),
                label("?", 18, true),
                label("⚙", 18, false),
                label("👤", 18, false));

        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(10, 25, 10, 25));
        header.setPrefHeight(60);
        header.setStyle("-fx-background-color:white;-fx-border-color:#E5EAF0;");

        Label title = label("Events", 30, true);
        title.setTextFill(Color.web(P));

        Label sub = label("Manage and monitor all scheduled events.", 15, false);
        VBox heading = new VBox(3, title, sub);

        Button create = button("+  Create Event", true);
        create.setPrefWidth(145);

        Region hspace = new Region();
        HBox.setHgrow(hspace, Priority.ALWAYS);

        HBox titleRow = new HBox(heading, hspace, create);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        totalEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        ongoingEventsVal = new Label("0");
        completedEventsVal = new Label("0");

        HBox cards = new HBox(20,
                card("Total Events", totalEventsVal, "▣"),
                card("Upcoming Events", upcomingEventsVal, "◷"),
                card("Ongoing Events", ongoingEventsVal, "●"),
                card("Completed Events", completedEventsVal, "✓"));

        cards.setCursor(javafx.scene.Cursor.HAND);

        TextField search = new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(220);

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Status", "Upcoming", "Ongoing", "Completed", "Cancelled");
        status.setValue("Status");

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Event Types", "Corporate", "Birthday", "Educational", "Family");
        type.setValue("Event Types");

        Button clear = button("Clear Filters", false);
        clear.setOnAction(e -> {
            search.clear();
            status.setValue("Status");
            type.setValue("Event Types");
        });

        HBox filters = new HBox(12, search, status, type, clear);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(15));
        filters.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;-fx-background-radius:8;-fx-border-radius:8;");

        Label tableTitle = label("Events", 18, true);
        createTable();

        VBox tableBox = new VBox(8, tableTitle, table);
        tableBox.setPadding(new Insets(15));
        tableBox.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;-fx-background-radius:8;-fx-border-radius:8;");

        VBox content = new VBox(14, titleRow, cards, filters, tableBox);
        content.setPadding(new Insets(15, 25, 20, 25));
        content.setStyle("-fx-background-color:#F7F9FC;");

        VBox center = new VBox(header, content);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(center);

        loadEventsDataAsync();

        Scene scene = new Scene(root, 1550, 750);
        eventsScene = scene;
        return eventsScene;
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
}