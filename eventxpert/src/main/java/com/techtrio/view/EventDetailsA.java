package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class EventDetailsA {

    private Scene eventDetailsScene;

    private final String P = "#002A60";
    private final String BG = "#F7F9FC";
    private final String BORDER = "#CBD5E1";

    private Label eventTitleLabel;
    private Label eventStatusBadge;

    private Label custNameVal;
    private Label custTypeVal;
    private Label custEmailVal;
    private Label custPhoneVal;

    private Label orgNameVal;
    private Label orgStatusVal;
    private Label orgEmailVal;
    private Label orgContactVal;

    private Label infoCustomerVal;
    private Label infoEventNameVal;
    private Label infoTypeVal;
    private Label infoDateVal;
    private Label infoGuestsVal;
    private Label infoLocationVal;
    private Label infoOrganizerVal;
    private Label infoBudgetVal;
    private Label infoStatusVal;

    private Label venueNameVal;
    private Label venueCityVal;

    private Label label(String text, int size, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        return l;
    }

    private VBox info(String title, Label valLabel) {
        Label t = label(title, 12, false);
        t.setTextFill(Color.web("#596273"));
        valLabel.setFont(Font.font("Arial", 15));
        return new VBox(4, t, valLabel);
    }

    private VBox card(String icon, String title, Node content) {
        Label i = label(icon, 19, true);
        i.setTextFill(Color.web(P));

        Label t = label(title, 19, true);
        t.setTextFill(Color.web(P));

        HBox head = new HBox(9, i, t);
        head.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(12, head, content);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");
        return box;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(7);
        sidebar.setPrefWidth(260); sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25, 18, 18, 18));
        sidebar.setStyle("-fx-background-color:" + P + ";");

        Label logo = label("EventXpert", 26, true);
        Label admin = label("ADMIN DASHBOARD", 12, true);
        logo.setTextFill(Color.WHITE); admin.setTextFill(Color.WHITE);

        VBox logoBox = new VBox(1, logo, admin);
        VBox menu = new VBox(7);

        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"};

        for (String item : items) {
            Label m = label(item, 14, true);
            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9, 13, 9, 13));

            if (item.equals("Events")) {
                m.setStyle("-fx-background-color:#07539A;-fx-background-radius:5;");
            }

            menu.getChildren().add(m);
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        Label n = label("🔔  Notifications", 13, true);
        Label s = label("⚙  Settings", 13, true);
        Label p = label("👤  Profile", 13, true);
        Label sp = label("?  Support", 13, true);

        for (Label x : new Label[]{n, s, p, sp}) {
            x.setTextFill(Color.WHITE);
            x.setPadding(new Insets(7, 10, 7, 10));
        }

        sidebar.getChildren().addAll(logoBox, menu, space, n, s, p, sp);
        return sidebar;
    }

    public Scene geteventDetailsScene() {

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        HBox header = new HBox(20, headerSpace, label("🔔", 18, false), label("?", 18, true), label("⚙", 18, false), label("👤", 18, false));
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(8, 25, 8, 20));
        header.setPrefHeight(55);
        header.setStyle("-fx-background-color:white;-fx-border-color:#E5EAF0;");

        eventTitleLabel = label("Event Details", 30, true);
        eventTitleLabel.setTextFill(Color.web(P));

        Label subtitle = label("View complete information about the selected event.", 15, false);
        VBox titleBox = new VBox(4, eventTitleLabel, subtitle);

        eventStatusBadge = label("Upcoming", 12, true);
        eventStatusBadge.setTextFill(Color.web("#237A45"));
        eventStatusBadge.setPadding(new Insets(6, 14, 6, 14));
        eventStatusBadge.setStyle("-fx-background-color:#DDF7E7;-fx-background-radius:15;");

        Button back = new Button("←  Back to Events");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Events", stage);
        });

        back.setPrefHeight(38);
        back.setStyle("-fx-background-color:white;-fx-border-color:" + P + ";-fx-text-fill:" + P + ";-fx-border-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");

        Region titleSpace = new Region();
        HBox.setHgrow(titleSpace, Priority.ALWAYS);

        HBox titleRow = new HBox(12, titleBox, eventStatusBadge, titleSpace, back);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        // GRID EVENT INFO
        GridPane eventGrid = new GridPane();
        eventGrid.setHgap(40); eventGrid.setVgap(18);

        infoCustomerVal = label("Loading...", 15, false);
        infoEventNameVal = label("Loading...", 15, false);
        infoTypeVal = label("Loading...", 15, false);
        infoDateVal = label("Loading...", 15, false);
        infoGuestsVal = label("Loading...", 15, false);
        infoLocationVal = label("Loading...", 15, false);
        infoOrganizerVal = label("Loading...", 15, false);
        infoBudgetVal = label("Loading...", 15, false);
        infoStatusVal = label("Loading...", 15, false);

        eventGrid.add(info("Customer", infoCustomerVal), 0, 0);
        eventGrid.add(info("Event Name", infoEventNameVal), 1, 0);
        eventGrid.add(info("Event Type", infoTypeVal), 2, 0);
        eventGrid.add(info("Event Date", infoDateVal), 0, 1);
        eventGrid.add(info("Guests", infoGuestsVal), 1, 1);
        eventGrid.add(info("Location", infoLocationVal), 2, 1);
        eventGrid.add(info("Organizer", infoOrganizerVal), 0, 2);
        eventGrid.add(info("Budget", infoBudgetVal), 1, 2);
        eventGrid.add(info("Status", infoStatusVal), 2, 2);

        VBox eventCard = card("▣", "Event Information", eventGrid);

        venueNameVal = label("Loading...", 15, false);
        venueCityVal = label("Loading...", 15, false);

        VBox locationContent = new VBox(10, info("Venue", venueNameVal), info("City", venueCityVal));
        VBox locationCard = card("⌖", "Location", locationContent);

        HBox row1 = new HBox(25, eventCard, locationCard);
        HBox.setHgrow(eventCard, Priority.ALWAYS);

        // CUSTOMER CARD
        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(35); customerGrid.setVgap(14);

        custNameVal = label("Loading...", 15, false);
        custTypeVal = label("Registered Client", 15, false);
        custEmailVal = label("Loading...", 15, false);
        custPhoneVal = label("Loading...", 15, false);

        customerGrid.add(info("Customer Name", custNameVal), 0, 0);
        customerGrid.add(info("Customer Type", custTypeVal), 1, 0);
        customerGrid.add(info("Email", custEmailVal), 0, 1);
        customerGrid.add(info("Phone", custPhoneVal), 1, 1);

        VBox customerCard = card("♙", "Customer Information", customerGrid);

        // ORGANIZER CARD
        GridPane organizerGrid = new GridPane();
        organizerGrid.setHgap(35); organizerGrid.setVgap(14);

        orgNameVal = label("Loading...", 15, false);
        orgStatusVal = label("Assigned", 15, false);
        orgEmailVal = label("Loading...", 15, false);
        orgContactVal = label("Loading...", 15, false);

        organizerGrid.add(info("Organization Name", orgNameVal), 0, 0);
        organizerGrid.add(info("Status", orgStatusVal), 1, 0);
        organizerGrid.add(info("Email", orgEmailVal), 0, 1);
        organizerGrid.add(info("Contact", orgContactVal), 1, 1);

        VBox organizerCard = card("♙", "Assigned Organizer Information", organizerGrid);

        HBox row2 = new HBox(25, customerCard, organizerCard);
        HBox.setHgrow(customerCard, Priority.ALWAYS);
        HBox.setHgrow(organizerCard, Priority.ALWAYS);

        VBox content = new VBox(18, titleRow, row1, row2);
        content.setPadding(new Insets(18, 25, 25, 25));
        content.setStyle("-fx-background-color:" + BG + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        VBox center = new VBox(header, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(center);

        loadEventDetailsAsync();

        Scene scene = new Scene(root, 1550, 750);
        eventDetailsScene = scene;
        return eventDetailsScene;
    }

    private void loadEventDetailsAsync() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                Event event = controller.getEventById(selectedEvtId);

                if (event != null) {
                    CustomerProfile cust = controller.getCustomerProfile(event.getCustomerId());
                    OrganizerProfile org = (event.getOrganizerId() != null && !event.getOrganizerId().isBlank())
                            ? controller.getOrganizerProfile(event.getOrganizerId()) : null;

                    Platform.runLater(() -> {
                        eventTitleLabel.setText(event.getEventName() != null ? event.getEventName() : "Event Details");
                        eventStatusBadge.setText(event.getStatus() != null ? event.getStatus() : "Upcoming");

                        infoCustomerVal.setText(cust != null && cust.getName() != null ? cust.getName() : "Customer");
                        infoEventNameVal.setText(event.getEventName() != null ? event.getEventName() : "N/A");
                        infoTypeVal.setText(event.getEventType() != null ? event.getEventType() : "General");
                        infoDateVal.setText(event.getEventDate() != null ? event.getEventDate() : "TBD");
                        infoGuestsVal.setText(String.valueOf(event.getGuests()));
                        infoLocationVal.setText(event.getCity() != null ? event.getCity() : "N/A");

                        String orgDisplayName = event.getOrganizerName() != null && !event.getOrganizerName().isBlank()
                                ? event.getOrganizerName() : "Not Assigned";
                        infoOrganizerVal.setText(orgDisplayName);
                        infoBudgetVal.setText(event.getBudget() != null ? event.getBudget() : "N/A");
                        infoStatusVal.setText(event.getStatus() != null ? event.getStatus() : "PENDING");

                        venueNameVal.setText(event.getVenueType() != null ? event.getVenueType() : "Banquet Hall");
                        venueCityVal.setText(event.getCity() != null ? event.getCity() : "N/A");

                        if (cust != null) {
                            custNameVal.setText(cust.getName() != null ? cust.getName() : "Customer");
                            custEmailVal.setText(cust.getEmail() != null ? cust.getEmail() : "N/A");
                            custPhoneVal.setText(cust.getMobileNo() != null ? cust.getMobileNo() : "N/A");
                        }

                        if (org != null) {
                            orgNameVal.setText(org.getOrganizationName() != null && !org.getOrganizationName().isBlank()
                                    ? org.getOrganizationName() : org.getName());
                            orgStatusVal.setText(event.getOrganizerStatus() != null ? event.getOrganizerStatus() : "Assigned");
                            orgEmailVal.setText(org.getEmail() != null ? org.getEmail() : "N/A");
                            orgContactVal.setText(org.getMobileNo() != null ? org.getMobileNo() : "N/A");
                        } else {
                            orgNameVal.setText(orgDisplayName);
                            orgStatusVal.setText("Not Assigned");
                            orgEmailVal.setText("N/A");
                            orgContactVal.setText("N/A");
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