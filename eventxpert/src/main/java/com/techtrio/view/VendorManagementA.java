package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.VendorProfile;

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

public class VendorManagementA {

    private Scene vendorScene;
    final String PRIMARY = "#002A60", SECONDARY = "#8CAFC6", LIGHT = "#C2E0F4", BG = "#F6F9FC";

    TableView<VendorProfile> table;
    ObservableList<VendorProfile> data = FXCollections.observableArrayList();
    TextField search;

    private Label totalVendorsVal;
    private Label activeVendorsVal;
    private Label newVendorsVal;
    private Label inactiveVendorsVal;

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

        Label logo = label("EventXpert", 30, true);
        logo.setTextFill(Color.WHITE);

        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head = new VBox(2, logo, admin);
        head.setPadding(new Insets(0, 12, 18, 12));

        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar",
                "Customers", "Organizers", "Vendors", "Conflict Detection",
                "Reports & Analytics"};

        VBox menu = new VBox(4);

        for (String x : items) {
            Label l = new Label(x);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial", 17));
            l.setPadding(new Insets(9, 14, 9, 14));
            l.setMaxWidth(Double.MAX_VALUE);

            if (x.equals("Vendors"))
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

        TableColumn<VendorProfile, String> nameCol = new TableColumn<>("Vendor Business");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getBusinessName() != null && !d.getValue().getBusinessName().isBlank()
                        ? d.getValue().getBusinessName()
                        : (d.getValue().getFullName() != null ? d.getValue().getFullName() : d.getValue().getName())
        ));

        TableColumn<VendorProfile, String> typeCol = new TableColumn<>("Type / Category");
        typeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getCategory() != null ? d.getValue().getCategory() : "General"
        ));

        TableColumn<VendorProfile, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : "N/A"
        ));

        TableColumn<VendorProfile, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getPhone() != null && !d.getValue().getPhone().isBlank()
                        ? d.getValue().getPhone()
                        : (d.getValue().getMobileNo() != null ? d.getValue().getMobileNo() : "N/A")
        ));

        TableColumn<VendorProfile, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getAddress() != null ? d.getValue().getAddress() : "N/A"
        ));

        TableColumn<VendorProfile, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOnboardingStatus() != null ? d.getValue().getOnboardingStatus() : "NEW"
        ));

        TableColumn<VendorProfile, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(c -> new TableCell<>() {
            Button b = button("View");

            {
                b.setOnAction(e -> {
                    VendorProfile selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedVendorId(selected.getVendorId());
                    Stage stage = (Stage) b.getScene().getWindow();
                    NavigationManager.navigate("Vendor Details", stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(nameCol, typeCol, emailCol, phoneCol, locationCol, statusCol, actionCol);
    }

    public Scene getvendorScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setLeft(sidebar());

        VBox content = new VBox(18);
        content.setPadding(new Insets(25));

        Label title = label("Vendor Management", 32, true);
        Label sub = label("Manage and monitor all registered vendors on EventXpert.", 18, false);
        VBox heading = new VBox(5, title, sub);

        totalVendorsVal = new Label("0");
        activeVendorsVal = new Label("0");
        newVendorsVal = new Label("0");
        inactiveVendorsVal = new Label("0");

        HBox stats = new HBox(
                15,
                stat("Total Vendors", totalVendorsVal, "Registered"),
                stat("Active Vendors", activeVendorsVal, "Currently active"),
                stat("New Vendors", newVendorsVal, "Recent onboarding"),
                stat("Inactive Vendors", inactiveVendorsVal, "Need attention")
        );
        stats.setCursor(javafx.scene.Cursor.HAND);
        stats.setStyle("-fx-font-size:18px;");

        for (javafx.scene.Node n : stats.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        VBox vendorCard = card();

        HBox tableHead = new HBox();
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all = label("All Vendors", 21, true);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        TextField tableSearch = new TextField();
        tableSearch.setPromptText("Search vendors...");
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

        Label showing = label("Showing Vendors from Firestore", 14, false);

        footer.getChildren().addAll(showing, fspace, pagination);

        vendorCard.getChildren().addAll(tableHead, table, new Separator(), footer);
        content.getChildren().addAll(heading, stats, vendorCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        root.setCenter(scroll);

        loadVendorsDataAsync();

        Scene scene = new Scene(root, 1550, 750);
        vendorScene = scene;
        return vendorScene;
    }

    private void loadVendorsDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<VendorProfile> vendors = controller.getAllVendors();

                Platform.runLater(() -> {
                    data.clear();
                    int activeCount = 0;
                    int newCount = 0;
                    int inactiveCount = 0;

                    for (VendorProfile v : vendors) {
                        String st = v.getOnboardingStatus() != null ? v.getOnboardingStatus().toUpperCase() : "NEW";
                        if ("ACTIVE".equals(st) || "EXISTING".equals(st) || v.isProfileCompleted() || v.isHasProvidedService()) {
                            activeCount++;
                        } else if ("NEW".equals(st)) {
                            newCount++;
                        } else {
                            inactiveCount++;
                        }
                        data.add(v);
                    }

                    totalVendorsVal.setText(String.valueOf(vendors.size()));
                    activeVendorsVal.setText(String.valueOf(activeCount));
                    newVendorsVal.setText(String.valueOf(newCount));
                    inactiveVendorsVal.setText(String.valueOf(inactiveCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}