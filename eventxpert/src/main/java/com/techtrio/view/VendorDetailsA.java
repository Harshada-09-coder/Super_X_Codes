package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.controller.VendorController;
import com.techtrio.model.VendorBooking;
import com.techtrio.model.VendorProfile;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VendorDetailsA {

    private Scene vendorDetailScene;

    final String PRIMARY = "#002A60", SECONDARY = "#8CAFC6", LIGHT = "#C2E0F4", BG = "#F6F9FC";

    // Dynamic UI Controls
    private Label avatarLetterLabel;
    private Label vendorNameLabel;
    private Label vendorIdLabel;
    private Label vendorTypeTag;
    private Label vendorStatusTag;
    private Label registeredDateLabel;

    private Label emailValLabel;
    private Label phoneValLabel;
    private Label addressValLabel;
    private Label websiteValLabel;

    private Label totalEventsServedVal;
    private Label completedEventsVal;
    private Label upcomingEventsVal;
    private Label cancelledEventsVal;

    private Label infoTypeVal;
    private Label infoCatVal;
    private Label infoPriceRangeVal;
    private Label infoCapacityVal;
    private Label infoServiceAreaVal;
    private HBox servicesProvidedTagsBox;

    private Label currentStatusVal;
    private Label nextAvailableVal;
    private Label activeEventsCountVal;
    private Label upcomingBookingsCountVal;
    private Label vendorRatingLabel;

    private TableView<VendorBooking> historyTable;
    private ObservableList<VendorBooking> historyData = FXCollections.observableArrayList();

    Label lbl(String t, int s, boolean b) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", s));
        l.setTextFill(Color.web(PRIMARY));
        if (b) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    VBox card() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;-fx-border-radius:8;-fx-background-radius:8;");
        return v;
    }

    Button btn(String t) {
        Button b = new Button(t);
        b.setPadding(new Insets(9, 15, 9, 15));
        b.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;");
        return b;
    }

    VBox sidebar() {
        VBox side = new VBox(7);
        side.setPrefWidth(285);
        side.setPadding(new Insets(24, 12, 15, 12));
        side.setStyle("-fx-background-color:" + PRIMARY + ";");

        Label logo = lbl("EventXpert", 30, true);
        logo.setTextFill(Color.WHITE);

        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head = new VBox(2, logo, admin);
        head.setPadding(new Insets(0, 12, 18, 12));

        String[] items = {
            "Dashboard", "Event Requests", "Events", "Calendar", "Customers",
            "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"
        };

        VBox menu = new VBox(5);

        for (String s : items) {
            Label l = new Label(s);
            l.setMaxWidth(Double.MAX_VALUE);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial", 17));
            l.setPadding(new Insets(9, 14, 9, 14));

            if (s.equals("Vendors"))
                l.setStyle("-fx-background-color:#1264A3;-fx-background-radius:6;-fx-font-weight:bold;");

            l.setOnMouseClicked(e -> {
                Stage stage = (Stage) l.getScene().getWindow();
                NavigationManager.navigate(s, stage);
            });

            menu.getChildren().add(l);
        }

        Region gap = new Region();
        VBox.setVgrow(gap, Priority.ALWAYS);

        Label n = new Label("🔔  Notifications");
        Label se = new Label("⚙  Settings");
        Label p = new Label("👤  Profile");
        Label sp = new Label("?  Support");

        for (Label l : new Label[]{n, se, p, sp}) {
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial", 17));
            l.setPadding(new Insets(7, 14, 7, 14));
        }

        side.getChildren().addAll(head, menu, gap, n, se, p, sp);
        return side;
    }

    VBox vendorProfile() {
        VBox v = card();
        v.setPrefWidth(320);
        v.setAlignment(Pos.CENTER);

        avatarLetterLabel = lbl("V", 28, false);
        avatarLetterLabel.setAlignment(Pos.CENTER);
        avatarLetterLabel.setPrefSize(95, 95);
        avatarLetterLabel.setStyle("-fx-background-color:#E2E6EA;-fx-background-radius:60;");

        vendorNameLabel = lbl("Loading...", 20, true);
        vendorIdLabel = lbl("ID: Loading...", 14, false);

        vendorTypeTag = new Label("Audio-Visual");
        vendorTypeTag.setPadding(new Insets(6, 12, 6, 12));
        vendorTypeTag.setStyle("-fx-background-color:#E2E6EA;-fx-background-radius:15;");

        vendorStatusTag = new Label("● Active");
        vendorStatusTag.setTextFill(Color.web(PRIMARY));
        vendorStatusTag.setPadding(new Insets(6, 12, 6, 12));
        vendorStatusTag.setStyle("-fx-background-color:" + LIGHT + ";-fx-background-radius:15;-fx-font-weight:bold;");

        HBox tags = new HBox(8, vendorTypeTag, vendorStatusTag);
        tags.setAlignment(Pos.CENTER);

        registeredDateLabel = lbl("Aug 13, 2026", 14, false);

        v.getChildren().addAll(
                avatarLetterLabel, vendorNameLabel, vendorIdLabel, tags, new Separator(),
                lbl("REGISTERED ON", 12, true),
                registeredDateLabel
        );

        return v;
    }

    VBox contact() {
        VBox v = card();
        v.setPrefWidth(320);

        emailValLabel = lbl("✉  Email\n    Loading...", 14, false);
        phoneValLabel = lbl("☎  Mobile\n    Loading...", 14, false);
        addressValLabel = lbl("⌖  Address\n    Loading...", 14, false);
        websiteValLabel = lbl("◉  Website\n    www.eventxpert.com", 14, false);

        v.getChildren().addAll(
                lbl("Contact Information", 20, true),
                new Separator(),
                emailValLabel,
                phoneValLabel,
                addressValLabel,
                websiteValLabel
        );

        return v;
    }

    VBox stat(String title, Label valLabel) {
        VBox v = card();
        v.setPrefHeight(92);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 29));
        valLabel.setTextFill(Color.web(PRIMARY));
        v.getChildren().addAll(
                lbl(title, 12, true),
                valLabel
        );
        return v;
    }

    VBox service() {
        VBox v = card();

        v.getChildren().addAll(
                lbl("Service Details", 20, true),
                new Separator()
        );

        GridPane g = new GridPane();
        g.setHgap(55);
        g.setVgap(13);

        infoTypeVal = lbl("Audio-Visual", 14, true);
        infoCatVal = lbl("Corporate Event AV", 14, true);
        infoPriceRangeVal = lbl("₹25k – ₹80k / event", 14, true);
        infoCapacityVal = lbl("Up to 500 Guests", 14, true);
        infoServiceAreaVal = lbl("Pune, Mumbai, Nashik", 14, true);

        addInfo(g, "Type", infoTypeVal, 0, 0);
        addInfo(g, "Category", infoCatVal, 1, 0);
        addInfo(g, "Price Range", infoPriceRangeVal, 0, 1);
        addInfo(g, "Capacity", infoCapacityVal, 1, 1);
        addInfo(g, "Service Area", infoServiceAreaVal, 0, 2);

        servicesProvidedTagsBox = new HBox(6);
        servicesProvidedTagsBox.getChildren().addAll(
                tag("Projector"), tag("LED Display"), tag("Sound System"), tag("Wireless Microphones")
        );

        VBox services = new VBox(
                5,
                lbl("Services Provided", 12, false),
                servicesProvidedTagsBox
        );

        g.add(services, 1, 2);
        v.getChildren().add(g);

        return v;
    }

    void addInfo(GridPane g, String a, Label valLbl, int c, int r) {
        g.add(new VBox(2, lbl(a, 12, false), valLbl), c, r);
    }

    Label tag(String t) {
        Label l = new Label(t);
        l.setPadding(new Insets(5, 8, 5, 8));
        l.setStyle("-fx-background-color:#E9EDF0;-fx-background-radius:6;");
        return l;
    }

    VBox availability() {
        VBox v = card();

        currentStatusVal = lbl("● Available", 14, true);
        nextAvailableVal = lbl("Aug 30, 2026", 14, true);

        activeEventsCountVal = lbl("0", 25, true);
        upcomingBookingsCountVal = lbl("0", 25, true);

        v.getChildren().addAll(
                lbl("Availability & Workload", 20, true),
                new Separator(),
                infoRow("Current Status", currentStatusVal),
                infoRow("Next Available", nextAvailableVal)
        );

        HBox stats = new HBox(
                10,
                smallStat(activeEventsCountVal, "Active Events"),
                smallStat(upcomingBookingsCountVal, "Upcoming Bookings")
        );

        v.getChildren().add(stats);

        vendorRatingLabel = new Label("Vendor Rating     4.6 ★   (28 reviews)");
        vendorRatingLabel.setPadding(new Insets(8));
        vendorRatingLabel.setStyle("-fx-background-color:#EEF1F3;-fx-background-radius:6;");

        v.getChildren().add(vendorRatingLabel);
        return v;
    }

    HBox infoRow(String a, Label valLbl) {
        Label x = lbl(a, 13, false);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        HBox h = new HBox(x, gap, valLbl);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    VBox smallStat(Label valLbl, String title) {
        VBox v = card();
        v.setAlignment(Pos.CENTER);
        v.setPrefHeight(80);
        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        valLbl.setTextFill(Color.web(PRIMARY));
        v.getChildren().addAll(
                valLbl,
                lbl(title, 13, false)
        );
        return v;
    }

    VBox history() {
        VBox v = card();

        HBox head = new HBox();
        Label title = lbl("Event History", 20, true);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        head.getChildren().addAll(title, gap);
        v.getChildren().add(head);

        historyTable = new TableView<>();
        historyTable.setItems(historyData);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPrefHeight(180);

        TableColumn<VendorBooking, String> nameCol = new TableColumn<>("EVENT NAME");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEventName() != null ? d.getValue().getEventName() : "Corporate Event"
        ));

        TableColumn<VendorBooking, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEventDate() != null ? d.getValue().getEventDate() : "18 May 2026"
        ));

        TableColumn<VendorBooking, String> customerCol = new TableColumn<>("CUSTOMER");
        customerCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrganizerName() != null ? d.getValue().getOrganizerName() : "Incubators Pvt."
        ));

        TableColumn<VendorBooking, String> locationCol = new TableColumn<>("LOCATION");
        locationCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getLocation() != null ? d.getValue().getLocation() : "Pune"
        ));

        TableColumn<VendorBooking, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VendorBooking booking = getTableView().getItems().get(getIndex());
                    String st = booking.getStatus() != null ? booking.getStatus() : "Upcoming";
                    Label statusLbl = new Label(st);
                    statusLbl.setPadding(new Insets(5, 10, 5, 10));
                    statusLbl.setStyle("-fx-background-color:" + ("CONFIRMED".equalsIgnoreCase(st) ? LIGHT : "#E2E6EA") + ";-fx-background-radius:15;");
                    setGraphic(statusLbl);
                }
            }
        });

        historyTable.getColumns().addAll(nameCol, dateCol, customerCol, locationCol, statusCol);
        v.getChildren().add(historyTable);
        return v;
    }

    public Scene getvendorDetailScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setLeft(sidebar());

        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 25, 25, 25));

        Button back = btn("←  Back to Vendors");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Vendors", stage);
        });

        Label title = lbl("Vendor Details", 32, true);
        Label sub = lbl("View vendor information, services, performance and event history.", 16, false);

        VBox heading = new VBox(8, back, title, sub);

        VBox left = new VBox(16, vendorProfile(), contact());
        left.setPrefWidth(320);

        totalEventsServedVal = new Label("0");
        completedEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        cancelledEventsVal = new Label("0");

        HBox stats = new HBox(
                14,
                stat("TOTAL EVENTS SERVED", totalEventsServedVal),
                stat("COMPLETED", completedEventsVal),
                stat("UPCOMING", upcomingEventsVal),
                stat("CANCELLED", cancelledEventsVal)
        );
        stats.setCursor(javafx.scene.Cursor.HAND);

        for (javafx.scene.Node n : stats.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        HBox middle = new HBox(16, service(), availability());

        VBox right = new VBox(
                16,
                stats,
                middle,
                history()
        );

        HBox.setHgrow(right, Priority.ALWAYS);

        HBox main = new HBox(20, left, right);
        HBox.setHgrow(right, Priority.ALWAYS);

        content.getChildren().addAll(heading, main, new Separator());

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:" + BG + ";-fx-border-color:transparent;");

        root.setCenter(scroll);

        loadVendorDetailsAsync();

        Scene scene = new Scene(root, 1550, 750);
        vendorDetailScene = scene;
        return vendorDetailScene;
    }

    private void loadVendorDetailsAsync() {
        String selectedVendorId = AdminContext.getSelectedVendorId();
        if (selectedVendorId == null || selectedVendorId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                VendorProfile v = controller.getVendorProfile(selectedVendorId);
                VendorController vendorController = new VendorController();
                List<VendorBooking> bookings = vendorController.getVendorBookings(selectedVendorId);

                if (v != null) {
                    Platform.runLater(() -> {
                        String bName = v.getBusinessName() != null && !v.getBusinessName().isBlank()
                                ? v.getBusinessName()
                                : (v.getFullName() != null ? v.getFullName() : v.getName());

                        if (bName == null || bName.isBlank()) bName = "Vendor";

                        vendorNameLabel.setText(bName);
                        avatarLetterLabel.setText(bName.substring(0, 1).toUpperCase());
                        vendorIdLabel.setText("ID: " + (v.getVendorId() != null ? v.getVendorId() : "N/A"));

                        String cat = v.getCategory() != null ? v.getCategory() : "Audio-Visual";
                        vendorTypeTag.setText(cat);
                        infoTypeVal.setText(cat);
                        infoCatVal.setText(cat);

                        String st = v.getOnboardingStatus() != null ? v.getOnboardingStatus() : "Active";
                        vendorStatusTag.setText("● " + st);
                        currentStatusVal.setText("● " + st);

                        emailValLabel.setText("✉  Email\n    " + (v.getEmail() != null ? v.getEmail() : "visionav@example.com"));

                        String ph = v.getPhone() != null && !v.getPhone().isBlank() ? v.getPhone() : v.getMobileNo();
                        phoneValLabel.setText("☎  Mobile\n    " + (ph != null ? ph : "+91 98234 56789"));

                        addressValLabel.setText("⌖  Address\n    " + (v.getAddress() != null ? v.getAddress() : "Pune, Maharashtra"));
                        websiteValLabel.setText("◉  Website\n    www." + bName.toLowerCase().replaceAll("\\s+", "") + ".com");

                        if (v.getCreatedAt() != null && !v.getCreatedAt().isBlank()) {
                            registeredDateLabel.setText(v.getCreatedAt().substring(0, Math.min(10, v.getCreatedAt().length())));
                        } else {
                            registeredDateLabel.setText("Aug 13, 2026");
                        }

                        // REAL-TIME FIRESTORE BOOKINGS AGGREGATION
                        historyData.clear();
                        int completed = 0;
                        int upcoming = 0;
                        int cancelled = 0;

                        if (bookings != null && !bookings.isEmpty()) {
                            for (VendorBooking b : bookings) {
                                String status = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";
                                if ("CONFIRMED".equals(status) || "ACCEPTED".equals(status)) {
                                    upcoming++;
                                } else if ("COMPLETED".equals(status)) {
                                    completed++;
                                } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                                    cancelled++;
                                }
                                historyData.add(b);
                            }

                            int total = bookings.size();
                            totalEventsServedVal.setText(String.valueOf(total));
                            completedEventsVal.setText(String.valueOf(completed));
                            upcomingEventsVal.setText(String.valueOf(upcoming));
                            cancelledEventsVal.setText(String.valueOf(cancelled));

                            activeEventsCountVal.setText(String.valueOf(upcoming));
                            upcomingBookingsCountVal.setText(String.valueOf(upcoming));
                        } else {
                            // Default Fallback
                            totalEventsServedVal.setText("0");
                            completedEventsVal.setText("0");
                            upcomingEventsVal.setText("0");
                            cancelledEventsVal.setText("0");

                            activeEventsCountVal.setText("0");
                            upcomingBookingsCountVal.setText("0");
                        }
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}