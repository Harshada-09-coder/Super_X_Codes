package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
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

public class EventDetailsA {

    private Scene eventDetailsScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BACKGROUND = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

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
    private Label infoStartDateVal;
    private Label infoEndDateVal;
    private Label infoTimeVal;
    private Label infoGuestsVal;
    private Label infoLocationVal;
    private Label infoOrganizerVal;
    private Label infoBudgetVal;
    private Label infoStatusVal;

    private Label venueNameVal;
    private Label venueCityVal;

    public Scene geteventDetailsScene() {

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

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        eventTitleLabel = new Label("Event Details");
        eventTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        eventTitleLabel.setTextFill(Color.web(PRIMARY));
        Label subtitle = new Label("View complete information about the selected customer event.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));
        titleBox.getChildren().addAll(eventTitleLabel, subtitle);

        eventStatusBadge = new Label("Upcoming");
        eventStatusBadge.setTextFill(Color.web("#16A34A"));
        eventStatusBadge.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        eventStatusBadge.setPadding(new Insets(4, 10, 4, 10));
        eventStatusBadge.setStyle("-fx-background-color:#DCFCE7;-fx-background-radius:6;");

        Region titleSpace = new Region();
        HBox.setHgrow(titleSpace, Priority.ALWAYS);

        Button back = new Button("← Back to Events");
        back.setStyle("-fx-background-color:white;-fx-border-color:" + PRIMARY + ";-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-font-size:11px;-fx-padding:6 14;-fx-border-radius:6;-fx-cursor:hand;");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Events", stage);
        });

        titleRow.getChildren().addAll(titleBox, eventStatusBadge, titleSpace, back);

        // EVENT GRID
        GridPane eventGrid = new GridPane();
        eventGrid.setHgap(30); eventGrid.setVgap(14);

        infoCustomerVal = new Label("Loading...");
        infoEventNameVal = new Label("Loading...");
        infoTypeVal = new Label("Loading...");
        infoStartDateVal = new Label("Loading...");
        infoEndDateVal = new Label("Loading...");
        infoTimeVal = new Label("Loading...");
        infoGuestsVal = new Label("Loading...");
        infoLocationVal = new Label("Loading...");
        infoOrganizerVal = new Label("Loading...");
        infoBudgetVal = new Label("Loading...");
        infoStatusVal = new Label("Loading...");

        eventGrid.add(info("Customer", infoCustomerVal), 0, 0);
        eventGrid.add(info("Event Name", infoEventNameVal), 1, 0);
        eventGrid.add(info("Event Type", infoTypeVal), 2, 0);

        eventGrid.add(info("Start Date", infoStartDateVal), 0, 1);
        eventGrid.add(info("End Date", infoEndDateVal), 1, 1);
        eventGrid.add(info("Start - End Time", infoTimeVal), 2, 1);

        eventGrid.add(info("Guests", infoGuestsVal), 0, 2);
        eventGrid.add(info("Location", infoLocationVal), 1, 2);
        eventGrid.add(info("Assigned Organizer", infoOrganizerVal), 2, 2);

        eventGrid.add(info("Budget", infoBudgetVal), 0, 3);
        eventGrid.add(info("Status", infoStatusVal), 1, 3);

        VBox eventCard = card("fas-calendar-check", "Event Details", eventGrid);

        venueNameVal = new Label("Loading...");
        venueCityVal = new Label("Loading...");
        VBox locationContent = new VBox(8, info("Venue Type", venueNameVal), info("City / Locality", venueCityVal));
        VBox locationCard = card("fas-map-marker-alt", "Location & Venue", locationContent);
        locationCard.setPrefWidth(320);

        HBox row1 = new HBox(16, eventCard, locationCard);
        HBox.setHgrow(eventCard, Priority.ALWAYS);

        // CUSTOMER INFO
        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(30); customerGrid.setVgap(12);

        custNameVal = new Label("Loading...");
        custTypeVal = new Label("Registered Client");
        custEmailVal = new Label("Loading...");
        custPhoneVal = new Label("Loading...");

        customerGrid.add(info("Customer Name", custNameVal), 0, 0);
        customerGrid.add(info("Email", custEmailVal), 1, 0);
        customerGrid.add(info("Phone", custPhoneVal), 0, 1);
        customerGrid.add(info("Client Type", custTypeVal), 1, 1);

        VBox customerCard = card("fas-user", "Customer Information", customerGrid);

        // ORGANIZER INFO
        GridPane organizerGrid = new GridPane();
        organizerGrid.setHgap(30); organizerGrid.setVgap(12);

        orgNameVal = new Label("Loading...");
        orgStatusVal = new Label("Assigned");
        orgEmailVal = new Label("Loading...");
        orgContactVal = new Label("Loading...");

        organizerGrid.add(info("Organization Name", orgNameVal), 0, 0);
        organizerGrid.add(info("Status", orgStatusVal), 1, 0);
        organizerGrid.add(info("Email", orgEmailVal), 0, 1);
        organizerGrid.add(info("Contact", orgContactVal), 1, 1);

        VBox organizerCard = card("fas-user-tie", "Assigned Organizer", organizerGrid);

        HBox row2 = new HBox(16, customerCard, organizerCard);
        HBox.setHgrow(customerCard, Priority.ALWAYS);
        HBox.setHgrow(organizerCard, Priority.ALWAYS);

        content.getChildren().addAll(titleRow, row1, row2);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BACKGROUND + ";-fx-border-color:transparent;");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scroll);

        loadEventDetailsAsync();

        eventDetailsScene = new Scene(root, 1550, 785);
        return eventDetailsScene;
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

                        infoStartDateVal.setText(event.getStartDate() != null && !event.getStartDate().isBlank() ? event.getStartDate() : (event.getEventDate() != null ? event.getEventDate() : "TBD"));
                        infoEndDateVal.setText(event.getEndDate() != null && !event.getEndDate().isBlank() ? event.getEndDate() : infoStartDateVal.getText());

                        String timeDisplay = (event.getStartTime() != null && !event.getStartTime().isBlank())
                                ? event.getStartTime() + (event.getEndTime() != null && !event.getEndTime().isBlank() ? " - " + event.getEndTime() : "")
                                : (event.getTime() != null ? event.getTime() : "TBD");
                        infoTimeVal.setText(timeDisplay);

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

    private VBox info(String title, Label valLabel) {
        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        t.setTextFill(Color.web("#64748B"));
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        valLabel.setTextFill(Color.web(PRIMARY));
        return new VBox(2, t, valLabel);
    }

    private VBox card(String iconLit, String title, Node content) {
        HBox head = new HBox(8);
        head.setAlignment(Pos.CENTER_LEFT);

        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(14);
        fontIcon.setIconColor(Color.web(PRIMARY));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(PRIMARY));

        head.getChildren().addAll(fontIcon, t);

        VBox box = new VBox(10, head, content);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        return box;
    }
}