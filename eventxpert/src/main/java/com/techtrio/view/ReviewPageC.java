package com.techtrio.view;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReviewPageC {

    private Scene scene;
    private String uid;
    private String idToken;

    private final String BLUE = "#002A60";
    private final String BG = "#F7F9FC";
    private final String BORDER = "#D7E0E8";
    private final String TEXT = "#111111";
    private final String GREY = "#667085";

    public static class EventData {
        public static String category = "Formal";
        public static String eventName = "";
        public static String eventType = "";
        public static int guests = 0;
        public static String eventDate = "";
        public static String time = "";
        public static String requirements = "";
        public static String notes = "";
        public static String city = "";
        public static String area = "";
        public static String venue = "";
        public static String budget = "₹500,000";
        public static boolean aiEnabled = true;

        public static void reset() {
            category = "Formal";
            eventName = "";
            eventType = "";
            guests = 0;
            eventDate = "";
            time = "";
            requirements = "";
            notes = "";
            city = "";
            area = "";
            venue = "";
            budget = "₹500,000";
            aiEnabled = true;
        }
    }

    public ReviewPageC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getReviewScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        VBox rightSide = new VBox();
        rightSide.setStyle("-fx-background-color:" + BG + ";");

        HBox topBar = createTopBar();
        VBox page = createPage();
        HBox footer = createFooter();

        rightSide.getChildren().addAll(topBar, page, footer);
        VBox.setVgrow(page, Priority.ALWAYS);

        root.setCenter(rightSide);
        scene = new Scene(root, 1250, 750);
        return scene;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);
        sidebar.setPadding(new Insets(35, 18, 25, 18));
        sidebar.setStyle("-fx-background-color:#002A60;");

        Label logo = new Label("EventXpert");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        logo.setTextFill(Color.WHITE);

        Label createEvent = new Label("Create Event");
        createEvent.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        createEvent.setTextFill(Color.WHITE);

        Label stepText = new Label("Step-by-step setup");
        stepText.setFont(Font.font("Arial", 12));
        stepText.setTextFill(Color.web("#D6E5F4"));

        VBox heading = new VBox(3, createEvent, stepText);
        heading.setPadding(new Insets(8, 0, 20, 0));

        VBox navigation = new VBox(8);
        navigation.getChildren().addAll(
                navItem("ⓘ", "Event Details", false),
                navItem("▤", "Requirements", false),
                navItem("⌖", "Location", false),
                navItem("▣", "Budget", false),
                navItem("✦", "AI Planning", false),
                navItem("✓", "Review", true)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, heading, navigation, spacer);
        return sidebar;
    }

    private HBox navItem(String icon, String text, boolean active) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(17));
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        HBox item = new HBox(12, iconLabel, textLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 12, 11, 12));

        if (active) {
            item.setStyle("-fx-background-color:#123A67;-fx-background-radius:8;-fx-border-color:#C2F0F4;-fx-border-width:0 0 0 3;-fx-border-radius:8;");
            iconLabel.setTextFill(Color.WHITE);
            textLabel.setTextFill(Color.WHITE);
        } else {
            item.setStyle("-fx-background-color:transparent;");
            iconLabel.setTextFill(Color.web("#D6E5F4"));
            textLabel.setTextFill(Color.web("#D6E5F4"));
        }
        return item;
    }

    private HBox createTopBar() {
        HBox bar = new HBox();
        bar.setPrefHeight(65);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(12, 30, 10, 25));
        bar.setSpacing(25);
        bar.setStyle("-fx-background-color:white;-fx-border-color:#E1E7ED;-fx-border-width:0 0 1 0;");

        Label notification = new Label("♧"); notification.setFont(Font.font(20)); notification.setTextFill(Color.web(BLUE));
        Label help = new Label("?"); help.setFont(Font.font("Arial", FontWeight.BOLD, 18)); help.setTextFill(Color.web(BLUE));
        bar.getChildren().addAll(notification, help);
        return bar;
    }

    private VBox createPage() {
        VBox page = new VBox(18);
        page.setPadding(new Insets(25, 30, 10, 30));
        page.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Review Your Event");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTextFill(Color.web(TEXT));

        Label subtitle = new Label("Please review all details before submitting your request.");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web(GREY));

        VBox titleBox = new VBox(4, title, subtitle);

        HBox row1 = new HBox(18, eventDetailsCard(), locationCard());
        HBox.setHgrow(row1.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row1.getChildren().get(1), Priority.ALWAYS);

        HBox row2 = new HBox(18, requirementsCard(), budgetCard(), aiCard());
        HBox.setHgrow(row2.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row2.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(row2.getChildren().get(2), Priority.ALWAYS);

        page.getChildren().addAll(titleBox, row1, row2);
        return page;
    }

    private VBox eventDetailsCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("ⓘ", "Event Details"));
        addDetail(card, "Name", EventData.eventName.isBlank() ? "Not specified" : EventData.eventName);
        addDetail(card, "Type", EventData.eventType.isBlank() ? "General" : EventData.eventType);
        addDetail(card, "Category", EventData.category);
        addDetail(card, "Date", EventData.eventDate.isBlank() ? "TBD" : EventData.eventDate);
        return card;
    }

    private VBox locationCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("⌖", "Location"));
        addDetail(card, "City", EventData.city.isBlank() ? "Not specified" : EventData.city);
        addDetail(card, "Area", EventData.area.isBlank() ? "Not specified" : EventData.area);
        addDetail(card, "Venue", EventData.venue.isBlank() ? "Banquet Hall" : EventData.venue);
        return card;
    }

    private VBox requirementsCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("▤", "Requirements"));
        addDetail(card, "Services", EventData.requirements.isBlank() ? "General Event Services" : EventData.requirements);
        if (!EventData.notes.isBlank()) {
            addDetail(card, "Notes", EventData.notes);
        }
        return card;
    }

    private VBox budgetCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("▣", "Budget"));
        addDetail(card, "Total Allocation", EventData.budget);
        return card;
    }

    private VBox aiCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("✦", "AI Planning"));
        addDetail(card, "Status", EventData.aiEnabled ? "Enabled" : "Disabled");
        return card;
    }

    private VBox createCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        return card;
    }

    private HBox cardTitle(String icon, String title) {
        Label iconLabel = new Label(icon); iconLabel.setFont(Font.font(18)); iconLabel.setTextFill(Color.web(BLUE));
        Label titleLabel = new Label(title); titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16)); titleLabel.setTextFill(Color.web(TEXT));
        HBox box = new HBox(8, iconLabel, titleLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void addDetail(VBox card, String labelText, String valueText) {
        Label label = new Label(labelText); label.setFont(Font.font("Arial", 12)); label.setTextFill(Color.web(GREY));
        Label value = new Label(valueText); value.setFont(Font.font("Arial", FontWeight.BOLD, 13)); value.setTextFill(Color.web(TEXT)); value.setWrapText(true);
        Region space = new Region(); HBox.setHgrow(space, Priority.ALWAYS);
        HBox row = new HBox(10, label, space, value); row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(row);
    }

    private HBox createFooter() {
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 30, 18, 30));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#DCE3E9;-fx-border-width:1 0 0 0;");

        Button back = new Button("Back"); back.setPrefSize(95, 44);
        back.setStyle("-fx-background-color:white;-fx-border-color:#9EABB8;-fx-border-radius:7;-fx-background-radius:7;-fx-text-fill:#002A60;-fx-cursor:hand;");

        Button create = new Button("Create Event"); create.setPrefSize(140, 44);
        create.setStyle("-fx-background-color:#002A60;-fx-text-fill:white;-fx-font-weight:bold;-fx-border-radius:7;-fx-background-radius:7;-fx-cursor:hand;");

        back.setOnAction(e -> {
            AiPlanningC aiView = new AiPlanningC(uid, idToken);
            LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
        });

        create.setOnAction(e -> {
            String customerUid = uid;
            if (customerUid == null || customerUid.isBlank()) {
                customerUid = UserSession.getUid();
            }
            String customerToken = idToken;
            if (customerToken == null || customerToken.isBlank()) {
                customerToken = UserSession.getIdToken();
            }

            System.out.println("========== EVENT AUTH DEBUG ==========");
            System.out.println("Session Authenticated: " + UserSession.isAuthenticated());
            System.out.println("Current Customer UID: " + customerUid);
            System.out.println("Current Customer Email: " + UserSession.getUserEmail());
            System.out.println("======================================");

            if (customerUid == null || customerUid.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "User session not found. Please log in again.");
                alert.showAndWait();
                return;
            }

            if (EventData.eventName == null || EventData.eventName.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Event Name is missing. Please go back and enter an Event Name.");
                alert.showAndWait();
                return;
            }

            create.setDisable(true);
            create.setText("Creating...");

            final String finalUid = customerUid;
            final String finalToken = customerToken;

            Event event = new Event();
            event.setEventId("EVT_" + System.currentTimeMillis());
            event.setCustomerId(finalUid);
            event.setEventName(EventData.eventName);
            event.setEventType(EventData.eventType);
            event.setCategory(EventData.category);
            event.setGuests(EventData.guests);
            event.setEventDate(EventData.eventDate);
            event.setTime(EventData.time);
            event.setRequirements(EventData.requirements);
            event.setNotes(EventData.notes);
            event.setCity(EventData.city);
            event.setLocality(EventData.area);
            event.setVenueType(EventData.venue);
            event.setBudget(EventData.budget);
            event.setAiEnabled(EventData.aiEnabled);
            event.setStatus("PENDING");

            Task<Boolean> saveTask = new Task<>() {
                @Override
                protected Boolean call() {
                    CustomerController controller = new CustomerController();
                    boolean saved = controller.createEvent(event);
                    if (saved) {
                        controller.markCustomerAsExisting(finalUid);
                    }
                    return saved;
                }
            };

            saveTask.setOnSucceeded(evt -> {
                boolean success = saveTask.getValue();
                create.setDisable(false);
                create.setText("Create Event");

                if (success) {
                    System.out.println("========== EVENT CREATION ==========");
                    System.out.println("Current authenticated customer: " + finalUid);
                    System.out.println("Customer email: " + UserSession.getUserEmail());
                    System.out.println("Creating Event...");
                    System.out.println("Event customerId: " + finalUid);
                    System.out.println("Event created successfully");
                    System.out.println("Customer status updated: EXISTING");
                    System.out.println("Opening Existing Customer Dashboard");
                    System.out.println("=====================================");

                    EventData.reset();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event Created Successfully! Your request is now PENDING approval.");
                    alert.setHeaderText("Success");
                    alert.showAndWait();

                    ActiveCustomerDashboardC activeDash = new ActiveCustomerDashboardC(finalUid, finalToken);
                    LandingPage.landingPageStage.setScene(activeDash.getactiveCustomerScene(null));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save event to Firestore. Please try again.");
                    alert.showAndWait();
                }
            });

            saveTask.setOnFailed(evt -> {
                Throwable ex = saveTask.getException();
                if (ex != null) ex.printStackTrace();
                create.setDisable(false);
                create.setText("Create Event");
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while creating the event. Please check your connection.");
                alert.showAndWait();
            });

            Thread thread = new Thread(saveTask);
            thread.setDaemon(true);
            thread.start();
        });

        footer.getChildren().addAll(back, create);
        return footer;
    }
}