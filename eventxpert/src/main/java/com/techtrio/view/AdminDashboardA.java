package com.techtrio.view;

import java.util.List;

import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorProfile;

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

public class AdminDashboardA {
    private Scene adminDashboardScene;

    private final String PRIMARY = "#002A60";
    private final String SECONDARY = "#8CAFC6";
    private final String LIGHT = "#C2E0F4";
    private final String BACKGROUND = "#F8FAFC";

    private Label totalCustomersVal;
    private Label totalOrganizersVal;
    private Label totalVendorsVal;
    private Label activeEventsVal;
    private Label pendingRequestsVal;
    private TableView<String[]> table;

    public Scene getadminDashboardScene() {

        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(25, 15, 20, 15));
        sidebar.setPrefWidth(235);
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        Label logo = new Label("EventXpert   ");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label adminLabel = new Label("ADMIN DASHBOARD");
        adminLabel.setTextFill(Color.web(LIGHT));
        adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        VBox logoBox = new VBox(2, logo, adminLabel);
        VBox menu = new VBox(7);

        String[] menuItems = {
            "Dashboard", "Event Requests", "Events", "Calendar",
            "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"
        };

        for (String item : menuItems) {
            Label menuLabel = new Label(item);
            menuLabel.setMaxWidth(Double.MAX_VALUE);
            menuLabel.setPadding(new Insets(10, 12, 10, 12));
            menuLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            menuLabel.setTextFill(Color.WHITE);

            if (item.equals("Dashboard")) {
                menuLabel.setStyle("-fx-background-color:" + LIGHT + ";-fx-text-fill:" + PRIMARY + ";-fx-background-radius:7;");
            }

            menuLabel.setOnMouseClicked(e -> {
                Stage stage = (Stage) menuLabel.getScene().getWindow();
                NavigationManager.navigate(item, stage);
            });

            menu.getChildren().add(menuLabel);
        }

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        Label notification = new Label("🔔 Notifications");
        Label settings = new Label("⚙ Settings");
        Label profile = new Label("👤 Profile");
        Label support = new Label("? Support");

        Label[] bottomItems = {notification, settings, profile, support};
        for (Label label : bottomItems) {
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            label.setPadding(new Insets(7, 10, 7, 10));
        }

        sidebar.getChildren().addAll(logoBox, menu, sidebarSpace, notification, settings, profile, support);

        HBox header = new HBox(18);
        header.setPadding(new Insets(12, 25, 12, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(65);
        header.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#E5EAF0;-fx-border-width:0 0 1 0;");

        TextField search = new TextField();
        search.setPromptText("Search events, organizers, vendors...");
        search.setPrefWidth(400);
        search.setPrefHeight(38);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Label bell = new Label("🔔");
        Label question = new Label("?");
        Label settingIcon = new Label("⚙");
        Label user = new Label("👤");

        for (Label label : new Label[]{bell, question, settingIcon, user})
            label.setFont(Font.font("Arial", 18));

        header.getChildren().addAll(search, headerSpace, bell, question, settingIcon, user);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setPrefWidth(1250);
        content.setStyle("-fx-background-color:" + BACKGROUND + ";");

        Label title = new Label("Admin System");
        title.setTextFill(Color.web(PRIMARY));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 27));

        Label subtitle = new Label("Here's what's happening across EventXpert today.");
        subtitle.setTextFill(Color.web("#555555"));
        subtitle.setFont(Font.font("Arial", 14));

        VBox titleBox = new VBox(4, title, subtitle);
        Region titleSpace = new Region();
        HBox.setHgrow(titleSpace, Priority.ALWAYS);

        HBox titleRow = new HBox(15, titleBox, titleSpace);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        // KPI CARDS
        HBox cards = new HBox(12);

        totalCustomersVal = new Label("0");
        totalOrganizersVal = new Label("0");
        totalVendorsVal = new Label("0");
        activeEventsVal = new Label("0");
        pendingRequestsVal = new Label("0");

        VBox customerCard = createCard("Total Customers", totalCustomersVal, "Registered");
        VBox organizerCard = createCard("Total Organizers", totalOrganizersVal, "Active");
        VBox vendorCard = createCard("Total Vendors", totalVendorsVal, "Active");
        VBox activeEventsCard = createCard("Active Events", activeEventsVal, "Planning");
        VBox pendingRequestsCard = createCard("Pending Requests", pendingRequestsVal, "Need review");

        cards.getChildren().addAll(customerCard, organizerCard, vendorCard, activeEventsCard, pendingRequestsCard);

        customerCard.setOnMouseClicked(e -> {
            Stage stage = (Stage) customerCard.getScene().getWindow();
            NavigationManager.navigate("Customers", stage);
        });

        organizerCard.setOnMouseClicked(e -> {
            Stage stage = (Stage) organizerCard.getScene().getWindow();
            NavigationManager.navigate("Organizers", stage);
        });

        vendorCard.setOnMouseClicked(e -> {
            Stage stage = (Stage) vendorCard.getScene().getWindow();
            NavigationManager.navigate("Vendors", stage);
        });

        activeEventsCard.setOnMouseClicked(e -> {
            Stage stage = (Stage) activeEventsCard.getScene().getWindow();
            NavigationManager.navigate("Events", stage);
        });

        pendingRequestsCard.setOnMouseClicked(e -> {
            Stage stage = (Stage) pendingRequestsCard.getScene().getWindow();
            NavigationManager.navigate("Event Requests", stage);
        });

        // PENDING EVENT REQUESTS
        VBox requestBox = new VBox(10);
        requestBox.setPadding(new Insets(16));
        requestBox.setPrefWidth(850);
        requestBox.setStyle("-fx-background-color:white;-fx-border-color:#D5DDE5;-fx-border-radius:10;-fx-background-radius:10;");

        Label requestTitle = new Label(" Recent Events Status");
        requestTitle.setTextFill(Color.web(PRIMARY));
        requestTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        table = new TableView<>();
        String[] columns = {"Customer", "Event Type", "Date", "Guests", "Budget", "Status"};

        for (int i = 0; i < columns.length; i++) {
            final int index = i;
            TableColumn<String[], String> column = new TableColumn<>(columns[i]);
            column.setPrefWidth(125);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[index]));
            table.getColumns().add(column);
        }
        table.setPrefHeight(210);
        requestBox.getChildren().addAll(requestTitle, table);

        // QUICK ACTIONS
        VBox quickBox = new VBox(12);
        quickBox.setPadding(new Insets(16));
        quickBox.setPrefWidth(300);
        quickBox.setStyle("-fx-background-color:white;-fx-border-color:#D5DDE5;-fx-border-radius:10;-fx-background-radius:10;");

        Label quickTitle = new Label("Quick Actions");
        quickTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        quickTitle.setTextFill(Color.web(PRIMARY));

        Button review = actionButton("Review Requests");
        Button customers = actionButton("Manage Customers");
        Button organizers = actionButton("Manage Organizers");
        Button calendar = actionButton("View Calendar");

        review.setOnAction(e -> {
            Stage stage = (Stage) review.getScene().getWindow();
            NavigationManager.navigate("Event Requests", stage);
        });

        customers.setOnAction(e -> {
            Stage stage = (Stage) customers.getScene().getWindow();
            NavigationManager.navigate("Customers", stage);
        });

        organizers.setOnAction(e -> {
            Stage stage = (Stage) organizers.getScene().getWindow();
            NavigationManager.navigate("Organizers", stage);
        });

        calendar.setOnAction(e -> {
            Stage stage = (Stage) calendar.getScene().getWindow();
            NavigationManager.navigate("Calendar", stage);
        });

        GridPane actionGrid = new GridPane();
        actionGrid.setHgap(8); actionGrid.setVgap(8);
        actionGrid.add(review, 0, 0); actionGrid.add(customers, 1, 0);
        actionGrid.add(organizers, 0, 1); actionGrid.add(calendar, 1, 1);

        quickBox.getChildren().addAll(quickTitle, actionGrid);

        HBox middle = new HBox(15, requestBox, quickBox);

        VBox statusBox = new VBox(12);
        statusBox.setPadding(new Insets(16));
        statusBox.setPrefWidth(600);
        statusBox.setStyle("-fx-background-color:white;-fx-border-color:#D5DDE5;-fx-border-radius:10;-fx-background-radius:10;");

        Label statusTitle = new Label("Event Status");
        statusTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        statusTitle.setTextFill(Color.web(PRIMARY));

        statusBox.getChildren().addAll(
                statusTitle,
                progress("Planning", 45),
                progress("Upcoming", 30),
                progress("Completed", 15),
                progress("Pending", 10)
        );

        // RECENT ACTIVITY
        VBox activityBox = new VBox(12);
        activityBox.setPadding(new Insets(16));
        activityBox.setPrefWidth(600);
        activityBox.setStyle("-fx-background-color:white;-fx-border-color:#D5DDE5;-fx-border-radius:10;-fx-background-radius:10;");

        Label activityTitle = new Label("Recent Activities");
        activityTitle.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        activityTitle.setTextFill(Color.web(PRIMARY));

        Label activity1 = new Label("●  New Event Created by Customer");
        Label activity2 = new Label("●  Event Request Submitted for Review");
        Label activity3 = new Label("●  Customer Registration Completed");
        Label activity4 = new Label("●  System Audit Log Updated");

        activityBox.getChildren().addAll(activityTitle, activity1, activity2, activity3, activity4);

        HBox bottom = new HBox(15, statusBox, activityBox);
        content.getChildren().addAll(titleRow, cards, middle, bottom);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color:" + BACKGROUND + ";-fx-border-color:transparent;");

        VBox center = new VBox(header, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(center);

        loadDashboardDataAsync();

        Scene scene = new Scene(root, 1550, 750);
        adminDashboardScene = scene;
        return adminDashboardScene;
    }

    private void loadDashboardDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<CustomerProfile> customers = controller.getAllCustomerProfiles();
                List<Event> events = controller.getAllEvents();
                List<OrganizerProfile> organizers = controller.getAllOrganizers();
                List<VendorProfile> vendors = controller.getAllVendors();

                Platform.runLater(() -> {
                    totalCustomersVal.setText(String.valueOf(customers.size()));
                    totalOrganizersVal.setText(String.valueOf(organizers.size()));
                    totalVendorsVal.setText(String.valueOf(vendors.size()));

                    int pendingCount = 0;
                    int activeCount = 0;

                    table.getItems().clear();
                    for (Event ev : events) {
                        if ("PENDING".equalsIgnoreCase(ev.getStatus())) {
                            pendingCount++;
                        } else {
                            activeCount++;
                        }

                        CustomerProfile cust = customers.stream()
                                .filter(c -> c.getCustomerId() != null && c.getCustomerId().equals(ev.getCustomerId()))
                                .findFirst().orElse(null);

                        String custName = cust != null ? cust.getName() : "Customer (" + (ev.getCustomerId() != null && ev.getCustomerId().length() > 6 ? ev.getCustomerId().substring(0, 6) : "N/A") + ")";
                        table.getItems().add(new String[]{
                                custName,
                                ev.getEventType() != null ? ev.getEventType() : "General",
                                ev.getEventDate() != null ? ev.getEventDate() : "TBD",
                                String.valueOf(ev.getGuests()),
                                ev.getBudget() != null ? ev.getBudget() : "N/A",
                                ev.getStatus() != null ? ev.getStatus() : "PENDING"
                        });
                    }

                    activeEventsVal.setText(String.valueOf(activeCount));
                    pendingRequestsVal.setText(String.valueOf(pendingCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createCard(String title, Label valueLabel, String sub) {
        VBox card = new VBox(6);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setPrefWidth(220); card.setPrefHeight(105); card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white;-fx-border-color:#C8D2DC;-fx-border-radius:10;-fx-background-radius:10;");

        Label titleLabel = new Label(title); titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 23)); valueLabel.setTextFill(Color.web(PRIMARY));
        Label subLabel = new Label(sub); subLabel.setTextFill(Color.web(SECONDARY)); subLabel.setFont(Font.font("Arial", 11));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private Button actionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(125); button.setPrefHeight(60); button.setWrapText(true);
        button.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#C8D2DC;-fx-border-radius:8;-fx-background-radius:8;-fx-font-weight:bold;-fx-text-fill:#002A60;");
        return button;
    }

    private VBox progress(String name, double value) {
        Label nameLabel = new Label(name); nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13)); nameLabel.setTextFill(Color.web("#333333"));
        Label percentageLabel = new Label((int) value + "%"); percentageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13)); percentageLabel.setTextFill(Color.web(PRIMARY));

        Region space = new Region(); HBox.setHgrow(space, Priority.ALWAYS);
        HBox top = new HBox(nameLabel, space, percentageLabel); top.setAlignment(Pos.CENTER_LEFT);

        HBox progressContainer = new HBox();
        double totalWidth = 500;
        double filledWidth = totalWidth * value / 100;
        double remainingWidth = totalWidth - filledWidth;

        Region filled = new Region();
        filled.setPrefWidth(filledWidth); filled.setMinWidth(filledWidth); filled.setMaxWidth(filledWidth); filled.setPrefHeight(12);
        filled.setStyle("-fx-background-color:#002A60;-fx-background-radius:6 0 0 6;");

        Region remaining = new Region();
        remaining.setPrefWidth(remainingWidth); remaining.setMinWidth(remainingWidth); remaining.setMaxWidth(remainingWidth); remaining.setPrefHeight(12);
        remaining.setStyle("-fx-background-color:#E5EDF3;-fx-background-radius:0 6 6 0;");

        progressContainer.getChildren().addAll(filled, remaining);
        VBox result = new VBox(6, top, progressContainer);
        result.setPrefWidth(totalWidth);
        return result;
    }
}