package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.OrganizerProfile;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class OrganizerManagementA {

    private Scene organizerScene;

    final String PRIMARY = "#002A60", SECONDARY = "#8CAFC6", LIGHT = "#C2E0F4", BG = "#F6F9FC";

    TableView<OrganizerProfile> table;
    ObservableList<OrganizerProfile> data = FXCollections.observableArrayList();

    private Label totalOrganizersVal;
    private Label activeOrganizersVal;
    private Label newOrganizersVal;
    private Label inactiveOrganizersVal;

    Label label(String text, int size, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", size));
        l.setTextFill(Color.web(PRIMARY));
        if (bold) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    Button button(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:white;-fx-text-fill:" + PRIMARY +
                ";-fx-border-color:" + SECONDARY + ";-fx-border-radius:6;" +
                "-fx-background-radius:6;-fx-font-weight:bold;-fx-cursor:hand;");
        b.setPadding(new Insets(8, 14, 8, 14));
        return b;
    }

    VBox card() {
        VBox v = new VBox(8);
        v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;" +
                "-fx-border-radius:8;-fx-background-radius:8;");
        return v;
    }

    VBox sidebar() {
        VBox side = new VBox(7);
        side.setPrefWidth(285);
        side.setPadding(new Insets(24, 12, 15, 12));
        side.setStyle("-fx-background-color:" + PRIMARY + ";");
        side.setCursor(javafx.scene.Cursor.HAND);

        Label logo = label("EventXpert", 30, true);
        logo.setTextFill(Color.WHITE);

        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head = new VBox(2, logo, admin);
        head.setPadding(new Insets(0, 12, 18, 12));

        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"};

        VBox menu = new VBox(4);

        for (String x : items) {
            Label l = new Label(x);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial", 17));
            l.setPadding(new Insets(9, 14, 9, 14));
            l.setMaxWidth(Double.MAX_VALUE);

            if (x.equals("Organizers"))
                l.setStyle("-fx-background-color:#1264A3;" +
                        "-fx-background-radius:6;-fx-font-weight:bold;");

            l.setOnMouseClicked(e -> {
                Stage stage = (Stage) l.getScene().getWindow();
                NavigationManager.navigate(x, stage);
            });

            menu.getChildren().add(l);
        }

        Region gap = new Region();
        VBox.setVgrow(gap, Priority.ALWAYS);

        Label notifications = new Label("🔔  Notifications");
        Label settings = new Label("⚙  Settings");
        Label profile = new Label("👤  Profile");
        Label support = new Label("?  Support");

        for (Label l : new Label[]{notifications, settings, profile, support}) {
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial", 17));
            l.setPadding(new Insets(7, 14, 7, 14));
        }

        side.getChildren().addAll(head, menu, gap, notifications, settings, profile, support);
        return side;
    }

    VBox stat(String title, Label valLabel, String sub) {
        VBox v = card();
        v.setPrefHeight(125);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 34));
        valLabel.setTextFill(Color.web(PRIMARY));

        v.getChildren().addAll(
                label(title.toUpperCase(), 14, false),
                valLabel,
                label(sub, 14, false)
        );
        return v;
    }

    void createTable() {
        table = new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<OrganizerProfile, String> orgNameCol = new TableColumn<>("Organization Name");
        orgNameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrganizationName() != null && !d.getValue().getOrganizationName().isBlank()
                        ? d.getValue().getOrganizationName()
                        : d.getValue().getName()
        ));

        TableColumn<OrganizerProfile, String> nameCol = new TableColumn<>("Contact Person");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getName() != null ? d.getValue().getName() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getMobileNo() != null ? d.getValue().getMobileNo() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getAddress() != null ? d.getValue().getAddress() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOnboardingStatus() != null ? d.getValue().getOnboardingStatus() : "NEW"
        ));

        TableColumn<OrganizerProfile, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(c -> new TableCell<>() {
            Button b = button("View");

            {
                b.setOnAction(e -> {
                    OrganizerProfile selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedOrganizerId(selected.getOrganizerId());
                    Stage stage = (Stage) b.getScene().getWindow();
                    NavigationManager.navigate("Organizer Details", stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(orgNameCol, nameCol, emailCol, phoneCol, locationCol, statusCol, actionCol);
    }

    public Scene getorganizerScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setLeft(sidebar());

        VBox content = new VBox(18);
        content.setPadding(new Insets(25));

        Label title = label("Organizer Management", 32, true);
        Label sub = label("Manage and monitor all registered organizers on EventXpert.", 18, false);
        VBox heading = new VBox(5, title, sub);

        totalOrganizersVal = new Label("0");
        activeOrganizersVal = new Label("0");
        newOrganizersVal = new Label("0");
        inactiveOrganizersVal = new Label("0");

        HBox stats = new HBox(15,
                stat("Total Organizers", totalOrganizersVal, "Registered"),
                stat("Active Organizers", activeOrganizersVal, "Currently active"),
                stat("New Organizers", newOrganizersVal, "Recent onboarding"),
                stat("Inactive Organizers", inactiveOrganizersVal, "Need attention"));
        stats.setStyle("-fx-font-size:18;");
        stats.setCursor(javafx.scene.Cursor.HAND);

        for (javafx.scene.Node n : stats.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        VBox organizerCard = card();

        HBox tableHead = new HBox();
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all = label("All Organizers", 21, true);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        TextField tableSearch = new TextField();
        tableSearch.setPromptText("Search organizers...");
        tableSearch.setPrefWidth(280);

        Button filter = button("☰  Filter");
        filter.setOnAction(e -> tableSearch.clear());

        tableHead.getChildren().addAll(all, space, tableSearch, filter);

        createTable();

        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        Button previous = button("Previous");
        Button next = button("Next");

        pagination.getChildren().addAll(previous, next);

        HBox footer = new HBox();
        Region fspace = new Region();
        HBox.setHgrow(fspace, Priority.ALWAYS);
        footer.getChildren().addAll(fspace, pagination);

        organizerCard.getChildren().addAll(tableHead, table, new Separator(), footer);
        content.getChildren().addAll(heading, stats, organizerCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        root.setCenter(scroll);

        loadOrganizersDataAsync();

        Scene scene = new Scene(root, 1550, 750);
        organizerScene = scene;
        return organizerScene;
    }

    private void loadOrganizersDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<OrganizerProfile> organizers = controller.getAllOrganizers();

                Platform.runLater(() -> {
                    data.clear();
                    int activeCount = 0;
                    int newCount = 0;
                    int inactiveCount = 0;

                    for (OrganizerProfile org : organizers) {
                        String st = org.getOnboardingStatus() != null ? org.getOnboardingStatus().toUpperCase() : "NEW";
                        if ("EXISTING".equals(st) || "ACTIVE".equals(st) || org.isProfileCompleted()) {
                            activeCount++;
                        } else if ("NEW".equals(st)) {
                            newCount++;
                        } else {
                            inactiveCount++;
                        }
                        data.add(org);
                    }

                    totalOrganizersVal.setText(String.valueOf(organizers.size()));
                    activeOrganizersVal.setText(String.valueOf(activeCount));
                    newOrganizersVal.setText(String.valueOf(newCount));
                    inactiveOrganizersVal.setText(String.valueOf(inactiveCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}