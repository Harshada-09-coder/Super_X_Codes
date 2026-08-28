package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EventRequestA {
    private Scene eventRequstScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";

    private Label pendingCardVal;
    private Label underReviewCardVal;
    private Label assignmentCardVal;
    private Label assignedCardVal;

    private TableView<Event> table = new TableView<>();

    public Scene geteventRequestScene() {

        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(235); sidebar.setMinWidth(235); sidebar.setMaxWidth(235);
        sidebar.setPadding(new Insets(25, 15, 20, 15));
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        Label logo = new Label("EventXpert"); logo.setTextFill(Color.WHITE); logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label admin = new Label("ADMIN DASHBOARD"); admin.setTextFill(Color.web(LIGHT)); admin.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        VBox logoBox = new VBox(2, logo, admin);
        VBox menu = new VBox(7);

        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports"};

        for (String item : items) {
            Label l = new Label(item);
            l.setMaxWidth(Double.MAX_VALUE);
            l.setPadding(new Insets(10, 12, 10, 12));
            l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            l.setTextFill(Color.WHITE);

            if (item.equals("Event Requests"))
                l.setStyle("-fx-background-color:#084A8F;-fx-text-fill:white;-fx-background-radius:4;");

            l.setOnMouseClicked(e -> {
                Stage stage = (Stage) l.getScene().getWindow();
                NavigationManager.navigate(item, stage);
            });

            menu.getChildren().add(l);
        }

        Region space = new Region(); VBox.setVgrow(space, Priority.ALWAYS);
        Label notification = new Label("🔔  Notifications"); Label settings = new Label("⚙  Settings");
        Label profile = new Label("👤  Profile"); Label support = new Label("? Support ");

        for (Label l : new Label[]{notification, settings, profile, support}) {
            l.setTextFill(Color.WHITE); l.setFont(Font.font("Arial", FontWeight.BOLD, 13)); l.setPadding(new Insets(7, 10, 7, 10));
        }

        sidebar.getChildren().addAll(logoBox, menu, space, notification, settings, profile, support);

        HBox top = new HBox();
        top.setPadding(new Insets(10, 25, 10, 35)); top.setAlignment(Pos.CENTER_LEFT); top.setPrefHeight(65);
        top.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#E5EAF0;-fx-border-width:0 0 1 0;");

        TextField searchTop = new TextField();
        searchTop.setPromptText("Search event requests, organizers, customers...");
        searchTop.setPrefWidth(430); searchTop.setPrefHeight(38);

        Region topSpace = new Region(); HBox.setHgrow(topSpace, Priority.ALWAYS);
        Label bell = new Label("🔔"); Label question = new Label("?"); Label gear = new Label("⚙"); Label user = new Label("👤");

        for (Label l : new Label[]{bell, question, gear, user}) {
            l.setFont(Font.font("Arial", 18)); l.setPadding(new Insets(0, 8, 0, 8));
        }

        top.getChildren().addAll(searchTop, topSpace, bell, question, gear, user);

        Label title = new Label("Event Requests");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30)); title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Review customer event requests and assign the right organizer.");
        subtitle.setFont(Font.font("Arial", 16)); subtitle.setTextFill(Color.web("#303746"));

        VBox heading = new VBox(5, title, subtitle);

        Region titleSpace = new Region(); HBox.setHgrow(titleSpace, Priority.ALWAYS);
        HBox titleRow = new HBox(15, heading, titleSpace); titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox headingBox = new VBox(titleRow); headingBox.setPadding(new Insets(15, 30, 15, 30));

        HBox cards = new HBox(25);
        cards.setCursor(javafx.scene.Cursor.HAND);
        cards.setPadding(new Insets(5, 30, 20, 30));

        pendingCardVal = new Label("0");
        underReviewCardVal = new Label("0");
        assignmentCardVal = new Label("0");
        assignedCardVal = new Label("0");

        cards.getChildren().addAll(
                card("", "Pending", pendingCardVal),
                card("", "Under Review", underReviewCardVal),
                card("", "Organizer Assignment", assignmentCardVal),
                card("", "Assigned", assignedCardVal)
        );

        HBox filters = new HBox(18);
        filters.setPadding(new Insets(20)); filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#D0D8E2;-fx-border-radius:10;-fx-background-radius:10;");

        TextField search = new TextField(); search.setPromptText("Search event requests..."); search.setPrefWidth(255); search.setPrefHeight(42);
        ComboBox<String> status = combo("Status", "Pending", "Under Review", "Assigned");
        ComboBox<String> type = combo("Event Types", "Educational", "Birthday", "Corporate", "Family");
        ComboBox<String> date = combo("Dates", "Today", "This Week", "This Month");
        ComboBox<String> location = combo("Locations", "Pune", "Mumbai", "Nashik");

        Button clear = new Button("Clear Filters"); clear.setPrefHeight(42);
        clear.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#C8D1DC;-fx-border-radius:7;-fx-font-weight:bold;");
        clear.setOnAction(e -> {
            search.clear(); status.getSelectionModel().selectFirst(); type.getSelectionModel().selectFirst();
            date.getSelectionModel().selectFirst(); location.getSelectionModel().selectFirst();
        });

        filters.getChildren().addAll(search, status, type, date, location, clear);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> customer = col("Customer", Event::getCustomerId);
        TableColumn<Event, String> eventCol = col("Event", Event::getEventName);
        TableColumn<Event, String> eventTypeCol = col("Type", Event::getEventType);
        TableColumn<Event, String> eventDateCol = col("Event Date", Event::getEventDate);
        TableColumn<Event, String> guestsCol = col("Guests", ev -> String.valueOf(ev.getGuests()));
        TableColumn<Event, String> locationCol = col("Location", ev -> ev.getCity() != null ? ev.getCity() : "N/A");
        TableColumn<Event, String> budgetCol = col("Budget", Event::getBudget);
        TableColumn<Event, String> statusCol = col("Status", Event::getStatus);

        TableColumn<Event, Void> action = new TableColumn<>("Action");
        action.setPrefWidth(100);

        action.setCellFactory(c -> new TableCell<>() {
            Button review = new Button("Review");
            {
                review.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:" + PRIMARY + ";-fx-text-fill:" + PRIMARY + ";-fx-border-radius:7;-fx-cursor:hand;");
                review.setOnAction(e -> {
                    Event selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedEventId(selected.getEventId());
                    Stage stage = (Stage) review.getScene().getWindow();
                    EventRequesReviewA reviewPage = new EventRequesReviewA();
                    stage.setScene(reviewPage.geteventRequestReviewScene());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : review);
            }
        });

        table.getColumns().addAll(customer, eventCol, eventTypeCol, eventDateCol, guestsCol, locationCol, budgetCol, statusCol, action);

        Button previous = new Button("Previous"); Button nextBtn = new Button("Next");
        previous.setPrefWidth(90); nextBtn.setPrefWidth(70);

        Region pageSpace = new Region(); HBox.setHgrow(pageSpace, Priority.ALWAYS);
        HBox pagination = new HBox(10, previous, pageSpace, nextBtn); pagination.setAlignment(Pos.CENTER_RIGHT);

        VBox tableBox = new VBox(5, table, pagination);
        tableBox.setPadding(new Insets(20, 30, 10, 30));
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox contentBox = new VBox(headingBox, cards, filters, tableBox);
        contentBox.setStyle("-fx-background-color:#F8FAFC;");
        VBox.setVgrow(tableBox, Priority.ALWAYS);

        BorderPane right = new BorderPane();
        right.setTop(top);
        right.setCenter(contentBox);

        HBox rootBox = new HBox(sidebar, right);
        HBox.setHgrow(right, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(rootBox);
        scroll.setFitToHeight(true); scroll.setFitToWidth(true);

        loadEventsDataAsync();

        Scene scene = new Scene(scroll, 1550, 750);
        eventRequstScene = scene;
        return eventRequstScene;
    }

    private void loadEventsDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<Event> events = controller.getAllEvents();
                List<CustomerProfile> customers = controller.getAllCustomerProfiles();

                Platform.runLater(() -> {
                    table.getItems().clear();
                    int pending = 0;
                    int underReview = 0;
                    int assignment = 0;
                    int assigned = 0;

                    for (Event ev : events) {
                        String st = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "PENDING";
                        if ("PENDING".equals(st)) pending++;
                        else if ("UNDER_REVIEW".equals(st)) underReview++;
                        else if ("ASSIGNMENT_PENDING".equals(st)) assignment++;
                        else if ("ASSIGNED".equals(st)) assigned++;

                        CustomerProfile cust = customers.stream()
                                .filter(c -> c.getCustomerId() != null && c.getCustomerId().equals(ev.getCustomerId()))
                                .findFirst().orElse(null);

                        if (cust != null && cust.getName() != null) {
                            ev.setCustomerId(cust.getName());
                        }

                        table.getItems().add(ev);
                    }

                    pendingCardVal.setText(pending + " Pending");
                    underReviewCardVal.setText(underReview + " Reviewing");
                    assignmentCardVal.setText(assignment + " Waiting");
                    assignedCardVal.setText(assigned + " Assigned");
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox card(String icon, String title, Label valueLabel) {
        Label t = new Label(title); t.setFont(Font.font("Arial", FontWeight.BOLD, 21)); t.setTextFill(Color.web("#111827"));
        valueLabel.setFont(Font.font("Arial", 15)); valueLabel.setTextFill(Color.web("#303746"));

        VBox box = new VBox(10, t, valueLabel);
        box.setPadding(new Insets(22)); box.setPrefHeight(140); box.setMinWidth(240); box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#C9D4E0;-fx-border-radius:10;-fx-background-radius:10;");

        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private ComboBox<String> combo(String first, String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().add(first); box.getItems().addAll(values); box.setValue(first);
        box.setPrefWidth(200); box.setPrefHeight(42);
        return box;
    }

    private TableColumn<Event, String> col(String name, java.util.function.Function<Event, String> value) {
        TableColumn<Event, String> column = new TableColumn<>(name);
        column.setCellValueFactory(data -> new SimpleStringProperty(value.apply(data.getValue())));
        return column;
    }
}