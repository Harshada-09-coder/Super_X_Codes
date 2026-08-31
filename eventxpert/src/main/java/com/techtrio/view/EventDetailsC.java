package com.techtrio.view;

import com.techtrio.controller.CustomerController;
import com.techtrio.controller.CustomerState;
import com.techtrio.model.Event;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EventDetailsC {

    private final String BLUE = "#031D49";
    private final String ACTIVE = "#073E82";
    private final String TEXT = "#102653";

    private final String uid;
    private final String idToken;
    private Event currentEvent;

    // Default Constructor
    public EventDetailsC() {
        this(null);
    }

    // Parameterized Constructor (Event Object स्वीकारण्यासाठी)
    public EventDetailsC(Event event) {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
        this.currentEvent = event;
    }

    public Scene getEventDetailsScene() {
        HBox root = new HBox();
        root.setStyle("-fx-background-color:#F5F8FC;");

        // --- SIDEBAR (असाच ठेवला आहे) ---
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setPadding(new Insets(24, 12, 15, 12));
        sidebar.setStyle("-fx-background-color:" + BLUE + ";");

        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill:white;-fx-font-size:27px;-fx-font-weight:bold;");

        Label console = new Label("Customer Console");
        console.setStyle("-fx-text-fill:#B9CCE8;-fx-font-size:12px;");

        Button createEvent = sideButton("+   Create Event", false);
        createEvent.setStyle("-fx-background-color:white;-fx-text-fill:#102653;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:7;");
        createEvent.setPadding(new Insets(10));
        createEvent.setOnAction(e -> openCreateEvent());

        Button dashboard = sideButton("▦   Dashboard", false);
        dashboard.setOnAction(e -> returnToDashboard());

        Button events = sideButton("▣   Events", true);
        Button calendar = sideButton("▦   Calendar", false);

        VBox menu = new VBox(5, dashboard, events, calendar);
        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, console, createEvent, menu, sidebarSpace);

        // --- RIGHT CONTENT AREA ---
        BorderPane right = new BorderPane();
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setStyle("-fx-background-color:#F5F8FC;");

        // Footer Buttons
        Button back = outlineButton("Back");
        back.setOnAction(e -> returnToDashboard());

        HBox footerSpace = new HBox();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        HBox footer = new HBox(12, footerSpace, back);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 25, 10, 25));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#D8E1EC;-fx-border-width:1 0 0 0;");

        // Title
        Label title = new Label("Event Details & Summary");
        title.setStyle("-fx-text-fill:" + TEXT + ";-fx-font-size:22px;-fx-font-weight:bold;");

        // Main Details Card
        VBox content = new VBox(15, title);
        content.setPadding(new Insets(25));

        // Event Card UI
        VBox card = createEventDetailsCard(footer);
        content.getChildren().add(card);

        right.setCenter(content);
        right.setBottom(footer);

        root.getChildren().addAll(sidebar, right);
        return new Scene(root, 1365, 850);
    }

    /**
     * Event Details Display Card & Pay Button Integration
     */
    private VBox createEventDetailsCard(HBox footer) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white;-fx-border-color:#D8E1EC;-fx-border-radius:8;-fx-background-radius:8;");

        String eventName = (currentEvent != null && currentEvent.getEventName() != null) ? currentEvent.getEventName() : "Annual Celebration";
        String eventDate = (currentEvent != null && currentEvent.getEventDate() != null) ? currentEvent.getEventDate() : "Upcoming";
        String venue = (currentEvent != null && currentEvent.getCity() != null) ? currentEvent.getCity() + ", " + currentEvent.getLocality() : "City Center";
        String budget = (currentEvent != null && currentEvent.getBudget() != null) ? currentEvent.getBudget() : "10000";
        String status = (currentEvent != null && currentEvent.getStatus() != null) ? currentEvent.getStatus() : "APPROVED";
        String organizer = (currentEvent != null && currentEvent.getOrganizerName() != null) ? currentEvent.getOrganizerName() : "Assigned Organizer";

        Label nameLbl = new Label(eventName);
        nameLbl.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";");

        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(12);

        grid.add(new Label("📅 Date: " + eventDate), 0, 0);
        grid.add(new Label("📍 Venue: " + venue), 1, 0);
        grid.add(new Label("👤 Organizer: " + organizer), 0, 1);
        grid.add(new Label("💰 Estimated Budget: ₹" + budget), 1, 1);

        Label statusBadge = new Label(" Status: " + status + " ");
        if ("PAID".equalsIgnoreCase(status)) {
            statusBadge.setStyle("-fx-background-color:#DCFCE7;-fx-text-fill:#166534;-fx-font-weight:bold;-fx-padding:5 10;-fx-background-radius:5;");
        } else {
            statusBadge.setStyle("-fx-background-color:#FEF3C7;-fx-text-fill:#92400E;-fx-font-weight:bold;-fx-padding:5 10;-fx-background-radius:5;");
        }
        grid.add(statusBadge, 0, 2);

        card.getChildren().addAll(nameLbl, new Separator(), grid);

        // --- 💳 PAY NOW BUTTON INTEGRATION ---
        if (!"PAID".equalsIgnoreCase(status)) {
            Button payNowBtn = new Button("💳 Pay Now");
            payNowBtn.setStyle("-fx-background-color:#2563EB;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-padding:8 18;-fx-background-radius:5;-fx-cursor:hand;");
            
            payNowBtn.setOnAction(e -> {
                Stage stage = (Stage) payNowBtn.getScene().getWindow();
                
                // If currentEvent is null, create a mock event for test
                if (currentEvent == null) {
                    currentEvent = new Event();
                    currentEvent.setEventId("EVT_TEST");
                    currentEvent.setEventName(eventName);
                    currentEvent.setBudget(budget);
                    currentEvent.setCustomerId(uid);
                    currentEvent.setOrganizerId("ORG_01");
                }

                PaymentView paymentView = new PaymentView();
                paymentView.openPaymentWindow(stage, currentEvent, 2500.0, () -> {
                    // On Success: Update Badge & Remove Pay Button
                    statusBadge.setText(" Status: PAID ");
                    statusBadge.setStyle("-fx-background-color:#DCFCE7;-fx-text-fill:#166534;-fx-font-weight:bold;-fx-padding:5 10;-fx-background-radius:5;");
                    footer.getChildren().remove(payNowBtn);
                });
            });

            // Add Pay Now button to footer next to Back button
            footer.getChildren().add(0, payNowBtn);
        }

        return card;
    }

    private void returnToDashboard() {
        CustomerController controller = new CustomerController();
        CustomerState state = controller.getCustomerState(uid, idToken);

        if (state != null && state.hasCreatedEvent()) {
            ActiveCustomerDashboardC activeDash = new ActiveCustomerDashboardC(uid, idToken);
            LandingPage.landingPageStage.setScene(activeDash.getactiveCustomerScene(null));
        } else {
            NewCustomerDashboard newDash = new NewCustomerDashboard(uid, idToken);
            LandingPage.landingPageStage.setScene(newDash.getnewCustomerScene(null));
        }
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene(this::returnToDashboard));
        }
    }

    private Button sideButton(String text, boolean active) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(10, 12, 10, 12));
        b.setStyle("-fx-background-color:" + (active ? ACTIVE : "transparent") + ";-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;");
        return b;
    }

    private Button outlineButton(String text) {
        Button b = new Button(text);
        b.setPadding(new Insets(7, 14, 7, 14));
        b.setStyle("-fx-background-color:white;-fx-text-fill:#173360;-fx-border-color:#B8C7DA;-fx-border-radius:5;-fx-background-radius:5;-fx-font-size:10px;-fx-cursor:hand;");
        return b;
    }
}