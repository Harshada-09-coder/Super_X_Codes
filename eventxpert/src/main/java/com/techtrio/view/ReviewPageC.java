package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.Event;
import com.techtrio.model.Guest;
import com.techtrio.session.UserSession;
import com.techtrio.util.QRCodeUtil;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ReviewPageC {

    private Scene scene;
    private String uid;
    private String idToken;

    // High Contrast Theme Colors
    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String MAIN_BLUE = "#002A60";
    private static final String BG = "#F4F7FC";
    private static final String BORDER = "#CBD5E1";
    private static final String DARK_TEXT = "#0F172A";
    private static final String MUTED_TEXT = "#334155";

    public static class EventData {
        public static String category = "Formal";
        public static String eventName = "";
        public static String eventType = "";
        public static int guests;
        public static String eventDate = "";
        public static String time = "";
        public static String requirements = "";
        public static String notes = "";
        public static String city = "";
        public static String area = "";
        public static String venue = "";
        public static String budget = "₹ 8,45,000";
        public static String guestMode = "MANUAL"; // "MANUAL" or "QR_ONLY"
        public static List<GuestManagementC.GuestRow> guestList = new ArrayList<>();
        public static boolean aiEnabled = true;
        public static String aiPlanJson = "";
        public static String theme = "Classic Elegance";

        // Shared with Organizer and Vendor
        public static String confirmedVenueName = "Grand Convention Center";
        public static String confirmedVenueAddress = "Senapati Bapat Road, Pune";

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
            budget = "₹ 8,45,000";
            guestMode = "MANUAL";
            guestList = new ArrayList<>();
            aiEnabled = true;
            aiPlanJson = "";
            theme = "Classic Elegance";

            confirmedVenueName = "Grand Convention Center";
            confirmedVenueAddress = "Senapati Bapat Road, Pune";
        }
    }

    public ReviewPageC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getReviewScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "#FFFFFF");
        Label logoSub = label("Plan. Organize. Celebrate.", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label createTitle = label("Create Event", 20, FontWeight.BOLD, "#FFFFFF");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("ⓘ", "Event Details", false),
                sidebarItem("▣", "Requirements", false),
                sidebarItem("⌖", "Location", false),
                sidebarItem("▣", "Budget", false),
                sidebarItem("👥", "Guest Management", false),
                sidebarItem("✦", "AI Planning", false),
                sidebarItem("✓", "Review", true)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brand, titleBox, menu, sideSpace);

        // 2. TOP BAR
        HBox topBar = new HBox(12);
        topBar.setPrefHeight(60);
        topBar.setMinHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E2E8F0;-fx-border-width:0 0 1 0;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Label notification = label("♧", 20, FontWeight.NORMAL, MAIN_BLUE);
        Label help = label("?", 13, FontWeight.BOLD, MAIN_BLUE);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + MAIN_BLUE + ";-fx-border-radius:50;-fx-background-radius:50;-fx-text-fill:" + MAIN_BLUE + ";");

        topBar.getChildren().addAll(topSpace, notification, help);

        // 3. MAIN CONTENT
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox page = new VBox(16);
        page.setPadding(new Insets(18, 35, 20, 35));

        Label title = label("Review Your Event & AI Plan", 24, FontWeight.BOLD, MAIN_BLUE);
        Label subtitle = label("Please double-check your event setup and AI recommendations before final submission.", 13, FontWeight.NORMAL, MUTED_TEXT);
        VBox titleBoxPage = new VBox(3, title, subtitle);

        // 50%-50% BALANCED 2-COLUMN GRID
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(eventDetailsCard(), 0, 0);
        grid.add(locationCard(), 1, 0);
        grid.add(requirementsCard(), 0, 1);
        grid.add(budgetReviewCard(), 1, 1);

        VBox aiPlanCard = aiPlanningSummaryCard();

        page.getChildren().addAll(titleBoxPage, grid, aiPlanCard);
        scrollPane.setContent(page);

        // 4. FOOTER BAR
        HBox footer = new HBox(14);
        footer.setPrefHeight(65);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E2E8F0;-fx-border-width:1 0 0 0;");

        Button back = new Button("← Back to AI Plan");
        back.setPrefSize(150, 40);
        back.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        back.setStyle("-fx-background-color:white;-fx-border-color:" + BLUE + ";-fx-border-radius:6;-fx-background-radius:6;-fx-text-fill:" + BLUE + ";-fx-cursor:hand;");
        back.setOnAction(e -> {
            AiPlanningC aiView = new AiPlanningC(uid, idToken);
            LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
        });

        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);

        Button submitBtn = new Button("Submit Plan ✓");
        submitBtn.setPrefSize(145, 40);
        submitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        submitBtn.setStyle("-fx-background-color:" + NAVY + ";-fx-text-fill:white;-fx-background-radius:6;-fx-cursor:hand;");

        submitBtn.setOnAction(e -> {
            String customerUid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
            String customerToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();

            if (customerUid == null || customerUid.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "User session not found. Please log in again.");
                alert.showAndWait();
                return;
            }

            String evName = EventData.eventName;
            if (evName == null || evName.isBlank()) {
                evName = (EventData.eventType.isBlank() ? "Event" : EventData.eventType) + " Celebration";
            }

            submitBtn.setDisable(true);
            submitBtn.setText("Submitting...");

            final String finalUid = customerUid;
            final String finalToken = customerToken;

            Event event = new Event();
            event.setEventId("EVT_" + System.currentTimeMillis());
            event.setCustomerId(finalUid);
            event.setEventName(evName);
            event.setEventType(EventData.eventType.isBlank() ? "Wedding" : EventData.eventType);
            event.setCategory(EventData.category.isBlank() ? "Formal" : EventData.category);
            event.setGuests(EventData.guests > 0 ? EventData.guests : 250);
            event.setEventDate(EventData.eventDate.isBlank() ? "18 May 2024" : EventData.eventDate);
            event.setTime(EventData.time.isBlank() ? "10:00 AM" : EventData.time);
            event.setRequirements(EventData.requirements.isBlank() ? "Venue, Catering, Decoration, Photography" : EventData.requirements);
            event.setNotes(EventData.notes);
            event.setCity(EventData.city.isBlank() ? "Pune" : EventData.city);
            event.setLocality(EventData.area.isBlank() ? "Maharashtra" : EventData.area);
            event.setVenueType(EventData.venue.isBlank() ? "Banquet Hall" : EventData.venue);
            event.setBudget(EventData.budget.isBlank() ? "₹ 8,45,000" : EventData.budget);
            event.setAiEnabled(true);
            event.setStatus("PENDING");
            event.setOrganizerStatus("NOT_ASSIGNED");

            // ATTACH COMPLETE DYNAMIC AI PLAN TO FIRESTORE EVENT DOCUMENT
            event.setAiPlanJson(EventData.aiPlanJson);
            event.setTheme(EventData.theme);

            // ================= ATTACH GUEST MANAGEMENT DATA =================
            String selectedMode = (EventData.guestMode != null && !EventData.guestMode.isBlank())
                    ? EventData.guestMode
                    : "MANUAL";

            event.setGuestMode(selectedMode);
            event.setCheckedInCount(0);
            event.setQrToken(QRCodeUtil.generateSecureQrToken());

            if ("QR_ONLY".equalsIgnoreCase(selectedMode)) {
                int limit = EventData.guests > 0 ? EventData.guests : 250;
                event.setGuestLimit(limit);
                event.setGuestList(new ArrayList<>());
            } else {
                List<Guest> convertedGuests = new ArrayList<>();
                if (EventData.guestList != null && !EventData.guestList.isEmpty()) {
                    for (GuestManagementC.GuestRow row : EventData.guestList) {
                        convertedGuests.add(new Guest(
                                row.getName(),
                                row.getMobile(),
                                row.getEmail(),
                                row.getType()
                        ));
                    }
                }
                event.setGuestList(convertedGuests);
                event.setGuestLimit(convertedGuests.size() > 0 ? convertedGuests.size() : (EventData.guests > 0 ? EventData.guests : 250));
            }

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
                submitBtn.setDisable(false);
                submitBtn.setText("Submit Plan ✓");

                boolean isSaved = saveTask.getValue();
                if (isSaved) {
                    EventData.reset();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event & AI Plan Submitted Successfully! Your request has been sent to Admin for Organizer assignment.");
                    alert.setHeaderText("Submission Successful");
                    alert.showAndWait();

                    ActiveCustomerDashboardC activeDash = new ActiveCustomerDashboardC(finalUid, finalToken);
                    LandingPage.landingPageStage.setScene(activeDash.getactiveCustomerScene(null));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save event. Please check Firestore connection.");
                    alert.setHeaderText("Submission Failed");
                    alert.showAndWait();
                }
            });

            saveTask.setOnFailed(evt -> {
                submitBtn.setDisable(false);
                submitBtn.setText("Submit Plan ✓");
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while saving the event. Please check your connection.");
                alert.showAndWait();
            });

            Thread thread = new Thread(saveTask);
            thread.setDaemon(true);
            thread.start();
        });

        footer.getChildren().addAll(back, fSpacer, submitBtn);

        VBox rightSide = new VBox(topBar, scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private VBox eventDetailsCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("ⓘ", "Event Details"));
        addDetail(card, "Event Name", EventData.eventName.isBlank() ? "Grand Event Celebration" : EventData.eventName);
        addDetail(card, "Category", EventData.category);
        addDetail(card, "Event Type", EventData.eventType.isBlank() ? "Wedding" : EventData.eventType);
        addDetail(card, "Expected Guests", String.valueOf(EventData.guests > 0 ? EventData.guests : 250));
        addDetail(card, "Date & Time", (EventData.eventDate.isBlank() ? "18 May 2024" : EventData.eventDate) + " (" + (EventData.time.isBlank() ? "10:00 AM" : EventData.time) + ")");
        return card;
    }

    private VBox locationCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("⌖", "Location & Venue"));
        addDetail(card, "City", EventData.city.isBlank() ? "Pune" : EventData.city);
        addDetail(card, "Area / Locality", EventData.area.isBlank() ? "Baner, Maharashtra" : EventData.area);
        addDetail(card, "Venue Preference", EventData.venue.isBlank() ? "Banquet Hall" : EventData.venue);
        addDetail(card, "Venue Setup", "Standard AC Setup");
        addDetail(card, "Accessibility", "Guest Parking & Elevators");
        return card;
    }

    private VBox requirementsCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("▣", "Core Requirements & Services"));
        addDetail(card, "Selected Services", EventData.requirements.isBlank() ? "Venue, Catering, Decoration, Photography" : EventData.requirements);

        Label notesLabel = label("Custom Requirements & AI Notes", 11, FontWeight.BOLD, MUTED_TEXT);
        String customText = (EventData.notes != null && !EventData.notes.isBlank())
                ? EventData.notes
                : "Standard premium service setup tailored for your guest count and location.";

        Label notesVal = label(customText, 11, FontWeight.NORMAL, DARK_TEXT);
        notesVal.setWrapText(true);
        notesVal.setStyle("-fx-text-fill:" + DARK_TEXT + ";-fx-line-spacing:2px;");

        VBox notesBox = new VBox(4, notesLabel, notesVal);
        notesBox.setPadding(new Insets(8, 10, 8, 10));
        notesBox.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#E2E8F0;-fx-border-radius:6;-fx-background-radius:6;");

        card.getChildren().add(notesBox);
        return card;
    }

    private VBox budgetReviewCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("💰", "Budget Allocation"));

        String budgetVal = EventData.budget.isBlank() ? "₹ 8,45,000" : EventData.budget;
        HBox totalRow = new HBox(8);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label lbl = label("Total Budget:", 13, FontWeight.BOLD, MUTED_TEXT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = label(budgetVal, 16, FontWeight.BOLD, MAIN_BLUE);
        totalRow.getChildren().addAll(lbl, sp, val);
        card.getChildren().add(totalRow);

        ProgressBar pb = new ProgressBar(0.75);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(7);
        pb.setStyle("-fx-accent:" + BLUE + ";");
        card.getChildren().add(pb);

        addDetail(card, "Venue & Booking (30%)", "Optimized by AI");
        addDetail(card, "Catering & Food (25%)", "Per guest count");
        addDetail(card, "Decor & Media (25%)", "Audio-Visual setup");
        addDetail(card, "Contingency (20%)", "Safety reserve buffer");

        return card;
    }

    private VBox aiPlanningSummaryCard() {
        VBox card = createCard();
        card.setStyle("-fx-background-color:#F0F6FE;-fx-border-color:#CCE3FE;-fx-border-radius:10;-fx-background-radius:10;");

        HBox topTitle = new HBox(10);
        topTitle.setAlignment(Pos.CENTER_LEFT);
        Label icon = label("✨", 18, FontWeight.NORMAL, BLUE);
        Label title = label("AI Planning & Optimization Summary", 15, FontWeight.BOLD, MAIN_BLUE);

        Region badgeSpace = new Region();
        HBox.setHgrow(badgeSpace, Priority.ALWAYS);

        Label badge = label("AI Validated", 11, FontWeight.BOLD, BLUE);
        badge.setStyle("-fx-background-color:#E0EDFF;-fx-text-fill:" + BLUE + ";-fx-padding:3 10;-fx-background-radius:10;");

        topTitle.getChildren().addAll(icon, title, badgeSpace, badge);
        card.getChildren().add(topTitle);

        String summary = (EventData.notes != null && !EventData.notes.isBlank())
                ? EventData.notes
                : "This personalized plan has been generated and validated using Google Gemini AI. The plan includes optimized schedules, verified vendor recommendations, dynamic budget distributions, and customized task checklists.";

        Label sumLbl = label(summary, 12, FontWeight.NORMAL, DARK_TEXT);
        sumLbl.setWrapText(true);
        card.getChildren().add(sumLbl);

        HBox highlightRow = new HBox(12);
        highlightRow.setFillHeight(true);
        VBox p1 = highlightPill("Milestones", "5 Schedule Phases");
        VBox p2 = highlightPill("Tasks", "42 Action Items");
        VBox p3 = highlightPill("Vendors", "18 Recommended");

        HBox.setHgrow(p1, Priority.ALWAYS);
        HBox.setHgrow(p2, Priority.ALWAYS);
        HBox.setHgrow(p3, Priority.ALWAYS);

        highlightRow.getChildren().addAll(p1, p2, p3);
        card.getChildren().add(highlightRow);

        return card;
    }

    private VBox highlightPill(String title, String val) {
        VBox pill = new VBox(2);
        pill.setAlignment(Pos.CENTER);
        pill.setPadding(new Insets(7, 10, 7, 10));
        pill.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");
        pill.getChildren().addAll(
                label(title, 10, FontWeight.BOLD, MUTED_TEXT),
                label(val, 12, FontWeight.BOLD, MAIN_BLUE)
        );
        return pill;
    }

    private VBox createCard() {
        VBox card = new VBox(9);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        return card;
    }

    private HBox cardTitle(String icon, String title) {
        Label iconLabel = label(icon, 16, FontWeight.NORMAL, BLUE);
        Label titleLabel = label(title, 14, FontWeight.BOLD, MAIN_BLUE);
        HBox box = new HBox(8, iconLabel, titleLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 4, 0));
        box.setStyle("-fx-border-color:#F1F5F9;-fx-border-width:0 0 1 0;");
        return box;
    }

    private void addDetail(VBox card, String labelText, String valueText) {
        Label label = label(labelText, 12, FontWeight.BOLD, MUTED_TEXT);
        label.setMinWidth(150);
        label.setPrefWidth(150);

        Label value = label(valueText, 12, FontWeight.BOLD, DARK_TEXT);
        value.setWrapText(true);
        value.setTextAlignment(TextAlignment.RIGHT);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox row = new HBox(6, label, space, value);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(1, 0, 1, 0));
        card.getChildren().add(row);
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 16, FontWeight.NORMAL, "white"), label(text, 13, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + ";");
        l.setTextFill(Color.web(color));
        return l;
    }
}