package com.techtrio.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.Event;
import com.techtrio.model.Guest;
import com.techtrio.model.budget.BudgetAllocation;
import com.techtrio.model.budget.BudgetOptimizationRequest;
import com.techtrio.model.budget.BudgetOptimizationResult;
import com.techtrio.services.BudgetOptimizerService;
import com.techtrio.session.UserSession;
import com.techtrio.util.QRCodeUtil;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ReviewPageC {

    private Scene scene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#CBD5E1";

    public static class EventData {
        public static String category = "Formal";
        public static String eventName = "";
        public static String eventType = "";
        public static int guests;
        public static String startDate = "";
        public static String endDate = "";
        public static String startTime = "";
        public static String endTime = "";
        public static String requirements = "";
        public static String notes = "";
        public static String city = "";
        public static String area = "";
        public static String venue = "";
        public static String budget = "";
        public static String guestMode = "MANUAL";
        public static List<GuestManagementC.GuestRow> guestList = new ArrayList<>();
        public static boolean aiEnabled = true;
        public static String aiPlanJson = "";
        public static String theme = "Classic Elegance";

        public static String confirmedVenueName = "";
        public static String confirmedVenueAddress = "";

        // DRAFT ENGINE
        public static boolean isDraftSaved = false;
        public static int currentStep = 1;

        public static boolean hasActiveDraft() {
            return isDraftSaved && (eventName != null && !eventName.isBlank());
        }

        public static void saveDraft(int step) {
            currentStep = step;
            isDraftSaved = true;
        }

        public static void resumeDraft(String userUid, String token) {
            switch (currentStep) {
                case 2 -> {
                    RequirementsC view = new RequirementsC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getRequirementsScene());
                }
                case 3 -> {
                    LocationC view = new LocationC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getLocationScene());
                }
                case 4 -> {
                    BudgetC view = new BudgetC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getBudgetScene());
                }
                case 5 -> {
                    GuestManagementC view = new GuestManagementC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getGuestManagementScene());
                }
                case 6 -> {
                    AiPlanningC view = new AiPlanningC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getAiPlanningScene());
                }
                case 7 -> {
                    ReviewPageC view = new ReviewPageC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getReviewScene());
                }
                default -> {
                    CreateEventC view = new CreateEventC(userUid, token);
                    LandingPage.landingPageStage.setScene(view.getCreateEventScene());
                }
            }
        }

        public static void reset() {
            category = "Formal";
            eventName = "";
            eventType = "";
            guests = 0;
            startDate = "";
            endDate = "";
            startTime = "";
            endTime = "";
            requirements = "";
            notes = "";
            city = "";
            area = "";
            venue = "";
            budget = "";
            guestMode = "MANUAL";
            guestList = new ArrayList<>();
            aiEnabled = true;
            aiPlanJson = "";
            theme = "Classic Elegance";

            confirmedVenueName = "";
            confirmedVenueAddress = "";

            isDraftSaved = false;
            currentStep = 1;
        }
    }

    public ReviewPageC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getReviewScene() {
        EventData.saveDraft(7);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(24, 20, 20, 22));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = label("EventXpert", 20, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 9, FontWeight.NORMAL, "#BDE3FF");
        brandTexts.getChildren().addAll(logo, logoSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        Label createTitle = label("Create Event", 18, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", "fas-info-circle", false),
                sidebarItem("[2]", "Requirements", "fas-list-ul", false),
                sidebarItem("[3]", "Venue & Location", "fas-map-marker-alt", false),
                sidebarItem("[4]", "Budget", "fas-wallet", false),
                sidebarItem("[5]", "Guest", "fas-users", false),
                sidebarItem("[6]", "AI Planning", "fas-magic", false),
                sidebarItem("[7]", "Review", "fas-check-circle", true)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandBox, titleBox, menu, sideSpace);

        // 2. MAIN CONTENT (TopBar Removed)
        VBox page = new VBox(14);
        page.setPadding(new Insets(28, 45, 20, 45));

        Label stepText = label("STEP 7 OF 7", 10, FontWeight.BOLD, BLUE);
        Label title = label("Review Your Event & Plan", 26, FontWeight.BOLD, NAVY);
        Label subtitle = label("Please double-check your event setup and details before final submission.", 12, FontWeight.NORMAL, "#64748B");
        VBox titleBoxPage = new VBox(3, stepText, title, subtitle);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50); col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(eventDetailsCard(), 0, 0);
        grid.add(locationCard(), 1, 0);
        grid.add(requirementsCard(), 0, 1);
        grid.add(budgetReviewCard(), 1, 1);

        VBox aiPlanCard = aiPlanningSummaryCard();
        page.getChildren().addAll(titleBoxPage, grid, aiPlanCard);

        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        // 3. FOOTER
        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, NAVY);
        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);

        Button back = new Button("Back");
        back.setPrefSize(82, 38);
        back.setStyle("-fx-background-color:white;-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-text-fill:" + BLUE + ";-fx-font-weight:bold;-fx-cursor:hand;");
        back.setOnAction(e -> {
            EventData.saveDraft(6);
            AiPlanningC aiView = new AiPlanningC(uid, idToken);
            LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
        });

        Button submitBtn = new Button("Submit Plan ✓");
        submitBtn.setPrefSize(130, 38);
        submitBtn.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");

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

            event.setStartDate(EventData.startDate.isBlank() ? "" : EventData.startDate);
            event.setEndDate(EventData.endDate.isBlank() ? "" : EventData.endDate);
            event.setStartTime(EventData.startTime.isBlank() ? "10:00 AM" : EventData.startTime);
            event.setEndTime(EventData.endTime.isBlank() ? "06:00 PM" : EventData.endTime);

            event.setEventDate(event.getStartDate());
            event.setTime(event.getStartTime() + (!event.getEndTime().isBlank() ? " - " + event.getEndTime() : ""));

            event.setRequirements(EventData.requirements.isBlank() ? "Decoration, Catering, Photography" : EventData.requirements);
            event.setNotes(EventData.notes);
            event.setCity(EventData.city.isBlank() ? "Pune" : EventData.city);
            event.setLocality(EventData.area.isBlank() ? "Maharashtra" : EventData.area);
            event.setVenueType(EventData.venue.isBlank() ? "Banquet Hall" : EventData.venue);
            event.setBudget(EventData.budget.isBlank() ? "₹ 8,45,000" : EventData.budget);
            event.setAiEnabled(true);
            event.setStatus("PENDING");
            event.setOrganizerStatus("NOT_ASSIGNED");

            event.setAiPlanJson(EventData.aiPlanJson);
            event.setTheme(EventData.theme);

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
                    EventData.reset(); // DRAFT RESET ON SUBMISSION
                    showSuccessModal(finalUid, finalToken);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save event. Please check Firestore connection.");
                    alert.setHeaderText("Submission Failed");
                    if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
                    alert.showAndWait();
                }
            });

            saveTask.setOnFailed(evt -> {
                submitBtn.setDisable(false);
                submitBtn.setText("Submit Plan ✓");
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while saving the event. Please check your connection.");
                if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
                alert.showAndWait();
            });

            Thread thread = new Thread(saveTask);
            thread.setDaemon(true);
            thread.start();
        });

        footer.getChildren().addAll(copyright, fSpacer, back, submitBtn);

        VBox rightSide = new VBox(scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private void showSuccessModal(String customerUid, String customerToken) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Event Submitted");
        dialog.setHeaderText(null);
        if (LandingPage.landingPageStage != null) {
            dialog.initOwner(LandingPage.landingPageStage);
        }

        VBox content = new VBox(14);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24, 30, 24, 30));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");

        StackPane checkCircle = new StackPane();
        Circle bgCircle = new Circle(28, Color.web("#DCFCE7"));
        Label checkIco = new Label("✓");
        checkIco.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
        checkCircle.getChildren().addAll(bgCircle, checkIco);

        Label titleLbl = new Label("Event & Plan Submitted Successfully!");
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label descLbl = new Label("Your event has been submitted to Admin.\nAn expert Organizer will be assigned shortly to begin curation and venue setup.");
        descLbl.setWrapText(true);
        descLbl.setTextAlignment(TextAlignment.CENTER);
        descLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        Button okBtn = new Button("Go to Dashboard →");
        okBtn.setStyle("-fx-background-color: " + BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 20; -fx-background-radius: 6px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
            ActiveCustomerDashboardC activeDash = new ActiveCustomerDashboardC(customerUid, customerToken);
            LandingPage.landingPageStage.setScene(activeDash.getactiveCustomerScene(null));
        });

        content.getChildren().addAll(checkCircle, titleLbl, descLbl, okBtn);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setManaged(false);

        dialog.showAndWait();
    }

    private VBox eventDetailsCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("ⓘ", "Event Details"));
        addDetail(card, "Event Name", EventData.eventName.isBlank() ? "Grand Event Celebration" : EventData.eventName);
        addDetail(card, "Category", EventData.category);
        addDetail(card, "Event Type", EventData.eventType.isBlank() ? "Wedding" : EventData.eventType);
        addDetail(card, "Expected Guests", String.valueOf(EventData.guests > 0 ? EventData.guests : 250));
        addDetail(card, "Start Date", EventData.startDate.isBlank() ? "TBD" : EventData.startDate);
        addDetail(card, "End Date", EventData.endDate.isBlank() ? "TBD" : EventData.endDate);
        addDetail(card, "Start Time", EventData.startTime.isBlank() ? "10:00 AM" : EventData.startTime);
        addDetail(card, "End Time", EventData.endTime.isBlank() ? "06:00 PM" : EventData.endTime);
        return card;
    }

    private VBox locationCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("🏢", "Venue & Location"));
        addDetail(card, "Venue Name", EventData.confirmedVenueName.isBlank() ? "Pending Organizer Suggestion" : EventData.confirmedVenueName);
        addDetail(card, "Venue Preference", EventData.venue.isBlank() ? "Banquet Hall" : EventData.venue);
        addDetail(card, "City", EventData.city.isBlank() ? "Pune" : EventData.city);
        addDetail(card, "Area / Locality", EventData.area.isBlank() ? "Baner, Maharashtra" : EventData.area);
        addDetail(card, "Address", EventData.confirmedVenueAddress.isBlank() ? "Main Street" : EventData.confirmedVenueAddress);
        return card;
    }

    private VBox requirementsCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("▣", "Core Requirements & Services"));
        addDetail(card, "Selected Services", EventData.requirements.isBlank() ? "Decoration, Catering, Photography" : EventData.requirements);

        Label notesLabel = label("Custom Requirements & AI Notes", 12, FontWeight.BOLD, NAVY);
        String customText = (EventData.notes != null && !EventData.notes.isBlank())
                ? EventData.notes
                : "Standard premium service setup tailored for your guest count and location.";

        Label notesVal = label(customText, 12, FontWeight.BOLD, NAVY);
        notesVal.setWrapText(true);

        VBox notesBox = new VBox(4, notesLabel, notesVal);
        notesBox.setPadding(new Insets(6, 8, 6, 8));
        notesBox.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#E2E8F0;-fx-border-radius:6;-fx-background-radius:6;");

        card.getChildren().add(notesBox);
        return card;
    }

    private VBox budgetReviewCard() {
        VBox card = createCard();
        card.getChildren().add(cardTitle("💰", "Smart Budget Allocation"));

        String budgetVal = EventData.budget.isBlank() ? "₹ 8,45,000" : EventData.budget;
        HBox totalRow = new HBox(8);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label lbl = label("Total Budget:", 12, FontWeight.BOLD, NAVY);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = label(budgetVal, 15, FontWeight.BOLD, NAVY);
        totalRow.getChildren().addAll(lbl, sp, val);
        card.getChildren().add(totalRow);

        ProgressBar pb = new ProgressBar(0.85);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(6);
        pb.setStyle("-fx-accent:" + BLUE + ";");
        card.getChildren().add(pb);

        double parsedAmount = 845000;
        try {
            String clean = budgetVal.replaceAll("[^0-9.]", "").trim();
            if (!clean.isEmpty()) parsedAmount = Double.parseDouble(clean);
        } catch (Exception ignored) {}

        List<String> selectedReqs = new ArrayList<>();
        if (EventData.requirements != null && !EventData.requirements.isBlank()) {
            selectedReqs.addAll(Arrays.asList(EventData.requirements.split(",\\s*")));
        }

        BudgetOptimizerService service = new BudgetOptimizerService();
        BudgetOptimizationRequest req = new BudgetOptimizationRequest(
                parsedAmount,
                EventData.eventType != null ? EventData.eventType : "Wedding",
                EventData.guests > 0 ? EventData.guests : 150,
                selectedReqs,
                EventData.city != null ? EventData.city : "Pune"
        );

        BudgetOptimizationResult optResult = service.optimizeBudget(req);

        if (optResult != null && optResult.getAllocations() != null) {
            for (BudgetAllocation a : optResult.getAllocations()) {
                addDetail(card, a.getCategory() + " (" + a.getPercentage() + "%)", String.format("₹ %,.0f", a.getRecommendedAmount()));
            }
        } else {
            addDetail(card, "Venue & Catering (55%)", "Optimized by Engine");
            addDetail(card, "Decor & Photography (35%)", "Optimized by Engine");
            addDetail(card, "Contingency (10%)", "Safety Reserve");
        }

        return card;
    }

    private VBox aiPlanningSummaryCard() {
        VBox card = createCard();
        card.setStyle("-fx-background-color:#F0F6FE;-fx-border-color:#CCE3FE;-fx-border-radius:9;-fx-background-radius:9;");

        HBox topTitle = new HBox(10);
        topTitle.setAlignment(Pos.CENTER_LEFT);
        Label icon = label("✨", 16, FontWeight.NORMAL, BLUE);
        Label title = label("AI Planning & Optimization Summary", 15, FontWeight.BOLD, NAVY);

        Region badgeSpace = new Region();
        HBox.setHgrow(badgeSpace, Priority.ALWAYS);

        Label badge = label("AI Validated", 11, FontWeight.BOLD, BLUE);
        badge.setStyle("-fx-background-color:#E0EDFF;-fx-text-fill:" + BLUE + ";-fx-padding:2 8;-fx-background-radius:10;");

        topTitle.getChildren().addAll(icon, title, badgeSpace, badge);
        card.getChildren().add(topTitle);

        String summary = (EventData.notes != null && !EventData.notes.isBlank())
                ? EventData.notes
                : "This personalized plan has been generated and validated using Timefold AI constraint solver.";

        Label sumLbl = label(summary, 12, FontWeight.BOLD, NAVY);
        sumLbl.setWrapText(true);
        card.getChildren().add(sumLbl);

        return card;
    }

    private VBox createCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");
        return card;
    }

    private HBox cardTitle(String icon, String title) {
        Label iconLabel = label(icon, 15, FontWeight.NORMAL, BLUE);
        Label titleLabel = label(title, 14, FontWeight.BOLD, NAVY);
        HBox box = new HBox(8, iconLabel, titleLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 4, 0));
        box.setStyle("-fx-border-color:#F1F5F9;-fx-border-width:0 0 1 0;");
        return box;
    }

    private void addDetail(VBox card, String labelText, String valueText) {
        Label label = label(labelText, 12, FontWeight.BOLD, NAVY);
        label.setMinWidth(160);

        Label value = label(valueText, 12, FontWeight.BOLD, NAVY);
        value.setWrapText(true);
        value.setTextAlignment(TextAlignment.RIGHT);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox row = new HBox(6, label, space, value);
        row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(row);
    }

    private HBox sidebarItem(String stepNum, String text, String iconLiteral, boolean active) {
        HBox item = new HBox(10);
        item.setPrefHeight(42);
        item.setMinHeight(42);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 16, 0, 22));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#B7C4D8"));

        Label textLbl = label(stepNum + "  " + text, 12, active ? FontWeight.BOLD : FontWeight.NORMAL, active ? NAVY : "#B7C4D8");

        if (active) {
            item.setStyle("-fx-background-color: #E0F2FE; -fx-background-radius: 7px;");
        } else {
            item.setStyle("-fx-background-color: transparent;");
        }

        item.getChildren().addAll(icon, textLbl);
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text != null ? text : "");
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + " !important;");
        return l;
    }
}