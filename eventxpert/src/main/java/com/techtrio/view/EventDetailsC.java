package com.techtrio.view;

import com.techtrio.controller.CustomerController;
import com.techtrio.controller.CustomerState;
import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EventDetailsC {

    private final String BLUE = "#031D49";
    private final String ACTIVE = "#073E82";
    private final String TEXT = "#102653";

    private final String uid;
    private final String idToken;

    public EventDetailsC() {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
    }

    public Scene getEventDetailsScene() {
        HBox root = new HBox();
        root.setStyle("-fx-background-color:#F5F8FC;");

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

        BorderPane right = new BorderPane();
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setStyle("-fx-background-color:#F5F8FC;");

        Button back = outlineButton("Back");
        back.setOnAction(e -> returnToDashboard());

        HBox footerSpace = new HBox();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        HBox footer = new HBox(10, footerSpace, back);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(7, 20, 7, 20));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#D8E1EC;-fx-border-width:1 0 0 0;");

        right.setBottom(footer);

        Label title = new Label("Event Details");
        title.setStyle("-fx-text-fill:" + TEXT + ";-fx-font-size:24px;-fx-font-weight:bold;");

        VBox content = new VBox(15, title);
        content.setPadding(new Insets(20));

        right.setCenter(content);

        root.getChildren().addAll(sidebar, right);
        return new Scene(root, 1365, 850);
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