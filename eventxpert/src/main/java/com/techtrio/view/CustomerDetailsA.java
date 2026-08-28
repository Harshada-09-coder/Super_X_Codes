package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;

import javafx.application.Platform;
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

public class CustomerDetailsA {

    private Scene customerDetailsScene;
    final String PRIMARY = "#002A60", SECONDARY = "#8CAFC6", LIGHT = "#C2E0F4", BG = "#F6F9FC";

    private Label nameLabel;
    private Label idLabel;
    private Label emailLabel;
    private Label mobileLabel;
    private Label totalEventsVal;
    private Label upcomingEventsVal;
    private Label completedEventsVal;
    private GridPane historyGrid;
    private VBox requirementsCardBox;

    public Scene getcustomerDetailsScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setLeft(sidebar());

        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 25, 25, 25));

        Button back = btn("←  Back to Customers");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Customers", stage);
        });

        Label title = label("Customer Details", 34, true);
        Label sub = label("View customer information, events and event requirements.", 18, false);
        VBox heading = new VBox(8, back, title, sub);

        VBox left = new VBox(16, profileCard(), contactCard());
        left.setPrefWidth(330);

        totalEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        completedEventsVal = new Label("0");

        HBox stats = new HBox(
                14,
                stat("Total Events", totalEventsVal),
                stat("Upcoming", upcomingEventsVal),
                stat("Completed", completedEventsVal),
                stat("Cancelled", new Label("0"))
        );
        stats.setCursor(javafx.scene.Cursor.HAND);

        for (javafx.scene.Node n : stats.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        VBox right = new VBox(16);
        right.getChildren().addAll(stats, eventHistory());

        requirementsCardBox = requirements();
        HBox bottom = new HBox(16, requirementsCardBox, activity());

        for (javafx.scene.Node n : bottom.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        right.getChildren().add(bottom);

        HBox main = new HBox(20, left, right);
        HBox.setHgrow(right, Priority.ALWAYS);

        content.getChildren().addAll(heading, main);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        root.setCenter(scroll);

        loadCustomerDetailsAsync();

        Scene scene = new Scene(root, 1550, 750);
        customerDetailsScene = scene;
        return customerDetailsScene;
    }

    private void loadCustomerDetailsAsync() {
        String selectedCustId = AdminContext.getSelectedCustomerId();
        if (selectedCustId == null || selectedCustId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                CustomerProfile profile = controller.getCustomerProfile(selectedCustId);
                List<Event> events = controller.getEventsByCustomerId(selectedCustId);

                Platform.runLater(() -> {
                    if (profile != null) {
                        String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Customer";
                        nameLabel.setText(name);
                        idLabel.setText("ID: " + profile.getCustomerId());
                        emailLabel.setText("✉  Email\n    " + (profile.getEmail() != null ? profile.getEmail() : "N/A"));
                        mobileLabel.setText("☎  Mobile\n    " + (profile.getMobileNo() != null ? profile.getMobileNo() : "N/A"));
                    }

                    totalEventsVal.setText(String.valueOf(events.size()));
                    int pendingUpcoming = 0;
                    int completed = 0;

                    historyGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

                    int rowIdx = 1;
                    for (Event ev : events) {
                        if ("COMPLETED".equalsIgnoreCase(ev.getStatus())) {
                            completed++;
                        } else {
                            pendingUpcoming++;
                        }

                        historyGrid.add(label(ev.getEventName() != null ? ev.getEventName() : "Unnamed", 15, false), 0, rowIdx);
                        historyGrid.add(label(ev.getEventType() != null ? ev.getEventType() : "General", 15, false), 1, rowIdx);
                        historyGrid.add(label(ev.getEventDate() != null ? ev.getEventDate() : "TBD", 15, false), 2, rowIdx);
                        historyGrid.add(label("Not Assigned", 15, false), 3, rowIdx);
                        historyGrid.add(status(ev.getStatus() != null ? ev.getStatus() : "PENDING", true), 4, rowIdx);
                        rowIdx++;
                    }

                    upcomingEventsVal.setText(String.valueOf(pendingUpcoming));
                    completedEventsVal.setText(String.valueOf(completed));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    Label label(String t, int s, boolean b) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", s));
        l.setTextFill(Color.web(PRIMARY));
        if (b) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    Button btn(String t) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;");
        b.setPadding(new Insets(10, 16, 10, 16));
        return b;
    }

    VBox card() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;-fx-border-radius:8;-fx-background-radius:8;");
        return v;
    }

    VBox sidebar() {
        VBox side = new VBox(7);
        side.setPadding(new Insets(24, 12, 15, 12)); side.setPrefWidth(285);
        side.setStyle("-fx-background-color:" + PRIMARY + ";");

        Label logo = label("EventXpert", 30, true); logo.setTextFill(Color.WHITE);
        Label admin = new Label("ADMIN DASHBOARD"); admin.setTextFill(Color.WHITE); admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");
        VBox head = new VBox(2, logo, admin); head.setPadding(new Insets(0, 12, 18, 12));

        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"};
        VBox menu = new VBox(5);

        for (String x : items) {
            Label l = new Label(x); l.setTextFill(Color.WHITE); l.setFont(Font.font("Arial", 17));
            l.setPadding(new Insets(9, 14, 9, 14)); l.setMaxWidth(Double.MAX_VALUE);
            if (x.equals("Customers")) l.setStyle("-fx-background-color:#1264A3;-fx-background-radius:6;-fx-font-weight:bold;");
            menu.getChildren().add(l);
        }

        Region gap = new Region(); VBox.setVgrow(gap, Priority.ALWAYS);
        Label notifications = new Label("🔔 Notifications"); Label settings = new Label("⚙  Settings");
        Label profile = new Label("👤  Profile"); Label support = new Label("?  Support");

        for (Label l : new Label[]{notifications, settings, profile, support}) {
            l.setTextFill(Color.WHITE); l.setFont(Font.font("Arial", 17)); l.setPadding(new Insets(7, 14, 7, 14));
        }

        side.getChildren().addAll(head, menu, gap, notifications, settings, profile, support);
        return side;
    }

    VBox profileCard() {
        VBox v = card();
        v.setAlignment(Pos.CENTER);

        Label avatar = label("C", 27, true);
        avatar.setAlignment(Pos.CENTER); avatar.setPrefSize(95, 95);
        avatar.setStyle("-fx-background-color:" + LIGHT + ";-fx-background-radius:60;-fx-font-weight:bold;");

        nameLabel = label("Loading...", 22, true);
        idLabel = label("ID: Loading...", 14, false);

        Label status = new Label("Active");
        status.setTextFill(Color.web(PRIMARY));
        status.setStyle("-fx-background-color:" + LIGHT + ";-fx-background-radius:20;-fx-font-weight:bold;");
        status.setPadding(new Insets(8, 20, 8, 20));

        Label reg = label("Registered Customer", 14, false);
        v.getChildren().addAll(avatar, nameLabel, idLabel, status, reg);
        return v;
    }

    VBox contactCard() {
        VBox v = card();
        emailLabel = label("✉  Email\n    Loading...", 15, false);
        mobileLabel = label("☎  Mobile\n    Loading...", 15, false);

        v.getChildren().addAll(
                label("Contact Information", 20, true),
                new Separator(),
                emailLabel,
                mobileLabel,
                label("⌖  Location\n    Registered Location", 15, false)
        );
        return v;
    }

    VBox stat(String t, Label valLabel) {
        VBox v = card(); v.setPrefHeight(105);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valLabel.setTextFill(Color.web(PRIMARY));
        v.getChildren().addAll(label(t.toUpperCase(), 14, true), valLabel);
        return v;
    }

    VBox eventHistory() {
        VBox v = card();
        v.getChildren().add(label("Event History", 21, true));

        historyGrid = new GridPane();
        historyGrid.setHgap(35); historyGrid.setVgap(20);

        String[] h = {"EVENT NAME", "TYPE", "DATE", "ORGANIZER", "STATUS"};
        for (int i = 0; i < h.length; i++)
            historyGrid.add(label(h[i], 13, true), i, 0);

        v.getChildren().add(historyGrid);
        return v;
    }

    Label status(String t, boolean upcoming) {
        Label l = new Label(t);
        l.setPadding(new Insets(7, 14, 7, 14));
        l.setTextFill(Color.web(PRIMARY));
        l.setStyle("-fx-background-color:" + (upcoming ? LIGHT : "#E2E6EA") + ";-fx-background-radius:20;");
        return l;
    }

    VBox requirements() {
        VBox v = card();
        v.getChildren().add(label("Customer Event Requirements", 20, true));

        Label special = label("ⓘ Loaded from Firestore event records.", 14, false);
        special.setPadding(new Insets(10));
        special.setStyle("-fx-background-color:#EEF2F5;-fx-background-radius:6;");

        v.getChildren().add(special);
        return v;
    }

    VBox activity() {
        VBox v = card();
        v.getChildren().add(label("Recent Activities", 20, true));
        v.getChildren().addAll(
                label("✓  Event Created", 14, true),
                label("□  Requirements Submitted", 14, true),
                label("✓  Customer Profile Synchronized", 14, true)
        );
        return v;
    }
}