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

public class EventRequestA {
    private Scene eventRequstScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BACKGROUND = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private Label pendingCardVal;
    private Label underReviewCardVal;
    private Label assignmentCardVal;
    private Label assignedCardVal;

    private TableView<Event> table = new TableView<>();

    public Scene geteventRequestScene() {

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
        String[][] items = {
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

        for (String[] item : items) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals("Event Requests")));
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

        // MAIN CONTENT
        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(24, 32, 28, 32));
        contentBox.setStyle("-fx-background-color:" + BACKGROUND + ";");

        Label title = new Label("Event Requests");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Review live customer event requests and assign the right organizer.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, subtitle);

        // STATS CARDS
        HBox cards = new HBox(16);
        pendingCardVal = new Label("0");
        underReviewCardVal = new Label("0");
        assignmentCardVal = new Label("0");
        assignedCardVal = new Label("0");

        cards.getChildren().addAll(
                card("Pending Requests", pendingCardVal, "fas-clock", "#DC2626"),
                card("Under Review", underReviewCardVal, "fas-search", "#D97706"),
                card("Ready to Assign", assignmentCardVal, "fas-user-check", "#0284C7"),
                card("Assigned & Done", assignedCardVal, "fas-check-circle", "#16A34A")
        );

        // TABLE
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> customer = col("Customer", Event::getCustomerId);
        TableColumn<Event, String> eventCol = col("Event Name", Event::getEventName);
        TableColumn<Event, String> eventTypeCol = col("Type", Event::getEventType);

        // Live Start - End Date
        TableColumn<Event, String> dateCol = col("Event Duration", ev -> {
            if (ev.getStartDate() != null && !ev.getStartDate().isBlank()) {
                return ev.getStartDate() + (ev.getEndDate() != null && !ev.getEndDate().isBlank() && !ev.getStartDate().equals(ev.getEndDate()) ? " to " + ev.getEndDate() : "");
            }
            return ev.getEventDate() != null ? ev.getEventDate() : "TBD";
        });

        // Live Start - End Time
        TableColumn<Event, String> timeCol = col("Time", ev -> {
            if (ev.getStartTime() != null && !ev.getStartTime().isBlank()) {
                return ev.getStartTime() + (ev.getEndTime() != null && !ev.getEndTime().isBlank() ? " - " + ev.getEndTime() : "");
            }
            return ev.getTime() != null ? ev.getTime() : "TBD";
        });

        TableColumn<Event, String> guestsCol = col("Guests", ev -> String.valueOf(ev.getGuests()));
        TableColumn<Event, String> locationCol = col("Location", ev -> ev.getCity() != null ? ev.getCity() : "N/A");
        TableColumn<Event, String> budgetCol = col("Budget", Event::getBudget);
        
        // Status Column with color coding
        TableColumn<Event, String> statusCol = col("Status", Event::getStatus);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("COMPLETED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                    } else if ("CONFIRMED".equalsIgnoreCase(status) || "ASSIGNED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #0284C7; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<Event, Void> action = new TableColumn<>("Action");
        action.setPrefWidth(100);

        action.setCellFactory(c -> new TableCell<>() {
            Button review = new Button("Review →");
            {
                review.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:11px;-fx-padding:5 12;-fx-background-radius:6;-fx-cursor:hand;");
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

        table.getColumns().addAll(customer, eventCol, eventTypeCol, dateCol, timeCol, guestsCol, locationCol, budgetCol, statusCol, action);

        VBox tableBox = new VBox(table);
        tableBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;-fx-padding:14;");
        VBox.setVgrow(table, Priority.ALWAYS);

        contentBox.getChildren().addAll(heading, cards, tableBox);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BACKGROUND + ";-fx-border-color:transparent;");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scroll);

        loadEventsDataAsync();

        eventRequstScene = new Scene(root, 1550, 785);
        return eventRequstScene;
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

    // ⭐ Event Completed झाल्यावर "Assigned & Done" मध्ये मोजणे
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
                    int assignedAndDone = 0;

                    if (events != null) {
                        for (Event ev : events) {
                            String st = ev.getStatus() != null ? ev.getStatus().toUpperCase().trim() : "PENDING";

                            if ("PENDING".equals(st)) {
                                pending++;
                            } else if ("UNDER_REVIEW".equals(st)) {
                                underReview++;
                            } else if ("ASSIGNMENT_PENDING".equals(st)) {
                                assignment++;
                            } else if ("ASSIGNED".equals(st) || "CONFIRMED".equals(st) || "COMPLETED".equals(st) || "PAID".equals(st)) {
                                // ⭐ Event Complete झाल्यावर किंवा Confirmed असल्यास Assigned & Done कार्डमध्ये मोजले जाईल!
                                assignedAndDone++;
                            }

                            if (customers != null) {
                                CustomerProfile cust = customers.stream()
                                        .filter(c -> c.getCustomerId() != null && c.getCustomerId().equals(ev.getCustomerId()))
                                        .findFirst().orElse(null);

                                if (cust != null && cust.getName() != null) {
                                    ev.setCustomerId(cust.getName());
                                }
                            }

                            table.getItems().add(ev);
                        }
                    }

                    pendingCardVal.setText(String.valueOf(pending));
                    underReviewCardVal.setText(String.valueOf(underReview));
                    assignmentCardVal.setText(String.valueOf(assignment));
                    assignedCardVal.setText(String.valueOf(assignedAndDone));
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

    private TableColumn<Event, String> col(String name, java.util.function.Function<Event, String> value) {
        TableColumn<Event, String> column = new TableColumn<>(name);
        column.setCellValueFactory(data -> new SimpleStringProperty(value.apply(data.getValue())));
        return column;
    }
}