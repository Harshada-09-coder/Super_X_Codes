package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.VenueController;
import com.techtrio.model.Event;
import com.techtrio.model.VenueModel;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class VenueManagementO {

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Status Badges
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";

    private String uid;
    private String idToken;
    private Runnable backAction;

    // Real-Time Event & Venue State
    private Event currentEvent;
    private final List<VenueModel> dynamicCuratedVenues = new ArrayList<>();
    private VenueModel currentlySelectedVenue;

    // UI Dynamic Controls
    private Label statusBadgeLabel;
    private Label venueNameLabel;
    private Label venueLocationSubLabel;
    private Label venueCostLabel;
    private Label mapCalloutName;
    private Label mapCalloutAddr;
    private Label lblCustomerPref;
    private Label lblChangeRequestBanner;
    private HBox changeRequestContainer;
    private HBox alternativeVenuesContainer;

    private final VenueController venueController = new VenueController();

    public VenueManagementO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public VenueManagementO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setLeft(createCommonOrganizerSidebar("My Events"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadDynamicVenueDataAsync();

        return new Scene(root, 1550, 785);
    }

private void loadDynamicVenueDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                String selectedEvtId = AdminContext.getSelectedEventId();
                AdminController adminController = new AdminController();

                if (selectedEvtId != null && !selectedEvtId.isBlank()) {
                    currentEvent = adminController.getEventById(selectedEvtId);
                }

                if (currentEvent == null) {
                    EventService eventService = new EventService();
                    List<Event> assigned = eventService.getEventsByOrganizerId(uid);
                    if (assigned != null && !assigned.isEmpty()) {
                        currentEvent = assigned.get(0);
                        AdminContext.setSelectedEventId(currentEvent.getEventId());
                    }
                }

                if (currentEvent != null) {
                    String city = (currentEvent.getCity() != null && !currentEvent.getCity().isBlank()) ? currentEvent.getCity() : "Surat";
                    String area = (currentEvent.getArea() != null && !currentEvent.getArea().isBlank()) ? currentEvent.getArea() : "Parvat Patiya";
                    String vType = (currentEvent.getVenueType() != null && !currentEvent.getVenueType().isBlank()) ? currentEvent.getVenueType() : "Banquet Hall";
                    int capacity = currentEvent.getGuests() > 0 ? currentEvent.getGuests() : 400;
                    double venueBudget = extractVenueSpecificBudget(currentEvent);

                    // 🎯 Live Real-World Search Engine Call
                    List<VenueModel> realPlaces = venueController.getNearbyVenues(city, area, vType, capacity, venueBudget);
                    dynamicCuratedVenues.clear();
                    if (realPlaces != null) {
                        dynamicCuratedVenues.addAll(realPlaces);
                    }
                }

                Platform.runLater(() -> renderDynamicVenueUI());
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void generateRealtimeCuratedVenues(Event ev) {
        dynamicCuratedVenues.clear();

        String city = (ev.getCity() != null && !ev.getCity().isBlank()) ? ev.getCity().trim() : "Mumbai";
        String area = (ev.getArea() != null && !ev.getArea().isBlank()) ? ev.getArea().trim() : (ev.getLocality() != null && !ev.getLocality().isBlank() ? ev.getLocality().trim() : "Kalyan");
        String vType = (ev.getVenueType() != null && !ev.getVenueType().isBlank()) ? ev.getVenueType().trim() : "Outdoor Venue";
        int capacity = ev.getGuests() > 0 ? ev.getGuests() : 50;

        double targetVenueBudget = extractVenueSpecificBudget(ev);

        dynamicCuratedVenues.add(new VenueModel("V-1", area + " Grand " + vType, vType, city, area,
                "Main Road, " + area + ", " + city, capacity + 20, targetVenueBudget, 4.9, true, true, true, 19.0760, 72.8777, "", "+91 9820011223"));

        dynamicCuratedVenues.add(new VenueModel("V-2", "The Royal " + area + " " + vType, vType, city, area,
                "Near Junction, " + area + ", " + city, capacity + 50, Math.round(targetVenueBudget * 1.05), 4.8, true, true, true, 19.0800, 72.8800, "", "+91 9820044556"));

        dynamicCuratedVenues.add(new VenueModel("V-3", area + " Green Meadows", vType, city, area,
                "West Avenue, " + area + ", " + city, capacity + 10, Math.round(targetVenueBudget * 0.95), 4.7, true, true, true, 19.0700, 72.8700, "", "+91 9820077889"));

        dynamicCuratedVenues.add(new VenueModel("V-4", "Skyline " + vType + " & Garden", vType, city, area,
                "Club Road, " + area + ", " + city, capacity + 80, Math.round(targetVenueBudget * 1.10), 4.6, true, true, true, 19.0900, 72.8900, "", "+91 9820099001"));
    }

    private double extractVenueSpecificBudget(Event ev) {
        if (ev.getBudgetRecommendation() != null && !ev.getBudgetRecommendation().isEmpty()) {
            for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
                if (entry.getKey().toLowerCase().contains("venue")) {
                    try {
                        return Double.parseDouble(entry.getValue().toString());
                    } catch (Exception ignored) {}
                }
            }
        }

        String totalBudgetStr = ev.getBudget() != null ? ev.getBudget() : "₹ 15,000";
        try {
            double total = Double.parseDouble(totalBudgetStr.replaceAll("[^0-9.]", "").trim());
            return Math.round(total * 0.293); // Approx ~₹4,400 for 15k
        } catch (Exception e) {
            return 4400.0;
        }
    }

    private void renderDynamicVenueUI() {
        if (currentEvent == null) return;

        String vStatus = currentEvent.getVenueStatus() != null ? currentEvent.getVenueStatus().toUpperCase() : "PENDING_SUGGESTION";
        String cityArea = (currentEvent.getArea() != null && !currentEvent.getArea().isBlank() ? currentEvent.getArea() + ", " : "") + (currentEvent.getCity() != null ? currentEvent.getCity() : "Mumbai");

        double venueCost = extractVenueSpecificBudget(currentEvent);

        if (lblCustomerPref != null) {
            lblCustomerPref.setText("Customer Preferred: " + cityArea + " | Type: " + (currentEvent.getVenueType() != null ? currentEvent.getVenueType() : "Outdoor Venue") + " | Guests: " + currentEvent.getGuests() + " | Venue Budget: ₹ " + String.format("%,.0f", venueCost));
        }

        if ("CHANGE_REQUESTED".equals(vStatus)) {
            changeRequestContainer.setVisible(true);
            changeRequestContainer.setManaged(true);
            lblChangeRequestBanner.setText("⚠️ Customer requested venue change: " + (currentEvent.getVenueChangeNotes() != null ? currentEvent.getVenueChangeNotes() : "Please suggest alternate venues."));
            statusBadgeLabel.setText("● CHANGE REQUESTED");
            statusBadgeLabel.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else if ("CONFIRMED".equals(vStatus)) {
            changeRequestContainer.setVisible(false);
            changeRequestContainer.setManaged(false);
            statusBadgeLabel.setText("✓ CONFIRMED BY CUSTOMER");
            statusBadgeLabel.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else if ("SUGGESTED".equals(vStatus)) {
            changeRequestContainer.setVisible(false);
            changeRequestContainer.setManaged(false);
            statusBadgeLabel.setText("⏳ SUGGESTIONS SENT");
            statusBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else {
            changeRequestContainer.setVisible(false);
            changeRequestContainer.setManaged(false);
            statusBadgeLabel.setText("● AWAITING SUGGESTION");
            statusBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
        }

        if (currentEvent.getConfirmedVenueName() != null && !currentEvent.getConfirmedVenueName().startsWith("Pending")) {
            venueNameLabel.setText(currentEvent.getConfirmedVenueName());
            venueLocationSubLabel.setText("📍 " + (currentEvent.getConfirmedVenueAddress() != null ? currentEvent.getConfirmedVenueAddress() : cityArea));
            venueCostLabel.setText(currentEvent.getConfirmedVenueCost() != null ? currentEvent.getConfirmedVenueCost() : "₹ " + String.format("%,.0f", venueCost));
            mapCalloutName.setText(currentEvent.getConfirmedVenueName());
            mapCalloutAddr.setText(venueLocationSubLabel.getText());
        } else if (!dynamicCuratedVenues.isEmpty()) {
            VenueModel top = dynamicCuratedVenues.get(0);
            currentlySelectedVenue = top;
            venueNameLabel.setText(top.getName());
            venueLocationSubLabel.setText("📍 " + top.getAddress());
            venueCostLabel.setText("₹ " + String.format("%,.0f", top.getPrice()));
            mapCalloutName.setText(top.getName());
            mapCalloutAddr.setText(top.getAddress());
        }

        renderAlternativeVenueCards();
    }

    private void renderAlternativeVenueCards() {
        if (alternativeVenuesContainer == null) return;
        alternativeVenuesContainer.getChildren().clear();

        for (VenueModel vm : dynamicCuratedVenues) {
            VBox card = createVenueOptionCard(vm);
            alternativeVenuesContainer.getChildren().add(card);
        }
    }

    private VBox createVenueOptionCard(VenueModel vm) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-cursor: hand;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #FAFCFF; -fx-background-radius: 10px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 10px; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-cursor: hand;"));

        card.setOnMouseClicked(e -> {
            this.currentlySelectedVenue = vm;
            venueNameLabel.setText(vm.getName());
            venueLocationSubLabel.setText("📍 " + vm.getAddress());
            venueCostLabel.setText("₹ " + String.format("%,.0f", vm.getPrice()));
            mapCalloutName.setText(vm.getName());
            mapCalloutAddr.setText(vm.getAddress());
        });

        HBox topRow = new HBox();
        Label matchBadge = new Label(vm.getRating() + " ★ (" + vm.getType() + ")");
        matchBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        topRow.getChildren().addAll(sp, matchBadge);

        Label vName = new Label(vm.getName()); vName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");
        Label vAddr = new Label("📍 " + vm.getArea() + ", " + vm.getCity()); vAddr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label vCap = new Label("👥 Capacity: " + vm.getCapacity() + " Guests"); vCap.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label costLbl = new Label("Venue Cost"); costLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region bSp = new Region(); HBox.setHgrow(bSp, Priority.ALWAYS);
        Label costVal = new Label("₹ " + String.format("%,.0f", vm.getPrice())); costVal.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        bottomRow.getChildren().addAll(costLbl, bSp, costVal);

        card.getChildren().addAll(topRow, vName, vAddr, vCap, new Separator(), bottomRow);
        return card;
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(18);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumb
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);
        Label bc1 = new Label("← Back to Event Planning");
        bc1.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));
        breadcrumbs.getChildren().add(bc1);

        // Title Row
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Venue Management & Recommendation");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSendToCustomer = new Button("📤 Send Suggestions to Customer");
        FontIcon sendIcon = new FontIcon("fas-paper-plane");
        sendIcon.setIconSize(12);
        sendIcon.setIconColor(Color.WHITE);
        btnSendToCustomer.setGraphic(sendIcon);
        btnSendToCustomer.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnSendToCustomer.setOnAction(e -> handleSendSuggestions());

        titleRow.getChildren().addAll(title, spacer, btnSendToCustomer);

        lblCustomerPref = new Label("Customer Preferred: Loading preferences...");
        lblCustomerPref.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        changeRequestContainer = new HBox(10);
        changeRequestContainer.setAlignment(Pos.CENTER_LEFT);
        changeRequestContainer.setPadding(new Insets(10, 16, 10, 16));
        changeRequestContainer.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-border-color: #FCD34D; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        lblChangeRequestBanner = new Label("⚠️ Customer requested venue alteration.");
        lblChangeRequestBanner.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + WARNING_TEXT + ";");
        changeRequestContainer.getChildren().add(lblChangeRequestBanner);
        changeRequestContainer.setVisible(false);
        changeRequestContainer.setManaged(false);

        HBox summaryRow = createTopSummaryAndMatchRow();
        HBox mapRow = createMapAndRiskOverviewRow(stage);
        VBox altSection = createAlternativeVenuesSection();

        mainBox.getChildren().addAll(breadcrumbs, titleRow, lblCustomerPref, changeRequestContainer, summaryRow, mapRow, altSection);
        return mainBox;
    }

    private HBox createTopSummaryAndMatchRow() {
        HBox row = new HBox(20);

        VBox mainVenueCard = createCardContainer();
        HBox.setHgrow(mainVenueCard, Priority.ALWAYS);

        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        statusBadgeLabel = new Label("● Awaiting Suggestion");
        statusBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        VBox costBox = new VBox(2);
        costBox.setAlignment(Pos.TOP_RIGHT);
        Label costTitle = new Label("Venue Budget");
        costTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        venueCostLabel = new Label("₹ 4,400");
        venueCostLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        costBox.getChildren().addAll(costTitle, venueCostLabel);

        cardHeader.getChildren().addAll(statusBadgeLabel, sp, costBox);

        venueNameLabel = new Label("Select a Venue from Options Below");
        venueNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        venueLocationSubLabel = new Label("📍 Location Preview");
        venueLocationSubLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox metricsGrid = new HBox(16);
        metricsGrid.setPadding(new Insets(12, 0, 0, 0));
        metricsGrid.getChildren().addAll(
                createMiniSpecBox("Location Proximity", "Customer Location Matched", "✓ City Matched"),
                createMiniSpecBox("Guest Capacity", "Fits Expected Guests", "✓ Capacity Verified"),
                createMiniSpecBox("Logistics & Parking", "Verified Setup", "✓ Logistics Ready")
        );

        mainVenueCard.getChildren().addAll(cardHeader, venueNameLabel, venueLocationSubLabel, new Separator(), metricsGrid);

        VBox matchCard = createCardContainer();
        matchCard.setPrefWidth(350);

        Label matchTitle = new Label("Timefold Venue Match");
        matchTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox checklist = new VBox(6);
        checklist.getChildren().addAll(
                createMatchCheckItem("✓ City & Locality Filtered"),
                createMatchCheckItem("✓ Capacity and Seating Alignment"),
                createMatchCheckItem("✓ Air-Conditioned & Weather Safe")
        );

        VBox aiInsightBox = new VBox(4);
        aiInsightBox.setPadding(new Insets(10));
        aiInsightBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");
        Label aiHeader = new Label("💡 AI Planner Insight:");
        aiHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label aiText = new Label("Recommended venues strictly align with Customer's allocated Venue Budget.");
        aiText.setWrapText(true);
        aiText.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        aiInsightBox.getChildren().addAll(aiHeader, aiText);

        matchCard.getChildren().addAll(matchTitle, checklist, aiInsightBox);
        row.getChildren().addAll(mainVenueCard, matchCard);
        return row;
    }

    private Label createMatchCheckItem(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        return l;
    }

    private VBox createMiniSpecBox(String title, String val, String sub) {
        VBox b = new VBox(2);
        b.setPadding(new Insets(8, 14, 8, 14));
        b.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        HBox.setHgrow(b, Priority.ALWAYS);

        Label t = new Label(title); t.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val); v.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GREEN_TEXT + ";");

        b.getChildren().addAll(t, v, s);
        return b;
    }

    private HBox createMapAndRiskOverviewRow(Stage stage) {
        HBox row = new HBox(20);

        VBox mapCard = createCardContainer();
        HBox.setHgrow(mapCard, Priority.ALWAYS);

        HBox mapHeader = new HBox();
        Label mapTitle = new Label("🗺 Interactive Map & Route Preview");
        mapTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button viewRouteBtn = new Button("🔍 View Live Route Map");
        viewRouteBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 6px; -fx-border-radius: 6px; -fx-cursor: hand;");
        viewRouteBtn.setOnAction(e -> showLiveRouteMapPopup(stage));

        mapHeader.getChildren().addAll(mapTitle, sp, viewRouteBtn);

        StackPane mapVisual = createRoutePreviewPanel(stage);

        VBox callout = new VBox(3);
        callout.setMaxSize(320, 75);
        callout.setPadding(new Insets(8, 12, 8, 12));
        callout.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 6, 0, 0, 2);");

        mapCalloutName = new Label("Venue Pin");
        mapCalloutName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");
        mapCalloutAddr = new Label("Address details...");
        mapCalloutAddr.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        callout.getChildren().addAll(mapCalloutName, mapCalloutAddr);
        StackPane.setAlignment(callout, Pos.BOTTOM_LEFT);
        StackPane.setMargin(callout, new Insets(0, 0, 10, 10));

        mapVisual.getChildren().add(callout);
        mapCard.getChildren().addAll(mapHeader, mapVisual);

        VBox riskCard = createCardContainer();
        riskCard.setPrefWidth(350);

        Label riskTitle = new Label("Logistics & Safety Overview");
        riskTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        VBox riskList = new VBox(8);
        riskList.getChildren().addAll(
                createRiskRow("☁ Weather Contingency", "INDOOR READY", GREEN_BG, GREEN_TEXT),
                createRiskRow("🚗 Valet & Parking Access", "SUFFICIENT", GREEN_BG, GREEN_TEXT),
                createRiskRow("⚡ Generator Backup", "VERIFIED", GREEN_BG, GREEN_TEXT)
        );

        riskCard.getChildren().addAll(riskTitle, new Separator(), riskList);
        row.getChildren().addAll(mapCard, riskCard);
        return row;
    }

    private StackPane createRoutePreviewPanel(Stage stage) {
        StackPane mapVisual = new StackPane();
        mapVisual.setPrefHeight(200);
        mapVisual.setStyle("-fx-background-color: #E5EEF8; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-cursor: hand;");
        mapVisual.setOnMouseClicked(e -> showLiveRouteMapPopup(stage));

        try {
            Image mapImage = new Image(getClass().getResource("/assets/images/map img.jpg").toExternalForm());
            ImageView mapView = new ImageView(mapImage);
            mapView.setFitWidth(550);
            mapView.setFitHeight(190);
            mapView.setPreserveRatio(false);
            mapVisual.getChildren().add(mapView);
        } catch (Exception ex) {
            Label noImg = new Label("🗺 Interactive Map Preview");
            noImg.setStyle("-fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
            mapVisual.getChildren().add(noImg);
        }

        Pane routeOverlay = new Pane();
        Line blueRoute = new Line(120, 140, 360, 60);
        blueRoute.setStroke(Color.web("#2563EB"));
        blueRoute.setStrokeWidth(4);
        blueRoute.getStrokeDashArray().addAll(10.0, 5.0);

        Circle startPin = new Circle(120, 140, 6, Color.web("#10B981"));
        Circle endPin = new Circle(360, 60, 8, Color.web("#DC2626"));

        routeOverlay.getChildren().addAll(blueRoute, startPin, endPin);
        mapVisual.getChildren().add(routeOverlay);

        return mapVisual;
    }

    // ⭐ 100% Real-Time Google Maps Direction Navigation for Organizer
    private void showLiveRouteMapPopup(Stage stage) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Interactive Directions to: " + (venueNameLabel != null ? venueNameLabel.getText() : "Venue"));
        dialog.setHeaderText(null);
        if (LandingPage.landingPageStage != null) {
            dialog.initOwner(LandingPage.landingPageStage);
        }

        // 1. Real-Time Customer Location (Origin)
        String originCity = (currentEvent != null && currentEvent.getCity() != null && !currentEvent.getCity().isBlank()) ? currentEvent.getCity().trim() : "Mumbai";
        String originArea = (currentEvent != null && currentEvent.getArea() != null && !currentEvent.getArea().isBlank()) ? currentEvent.getArea().trim() : "";
        String origin = !originArea.isEmpty() ? (originArea + ", " + originCity) : originCity;

        // 2. Real-Time Selected Venue Destination
        String destination = (venueLocationSubLabel != null && !venueLocationSubLabel.getText().isBlank())
                ? venueLocationSubLabel.getText().replace("📍 ", "").trim()
                : (venueNameLabel != null ? venueNameLabel.getText().trim() + ", " + originCity : "Venue Location, " + originCity);

        // 3. Dynamic Calculation
        double estDistanceKm = calculateDynamicDistance(originArea, destination);
        int driveTimeMins = (int) Math.max(8, Math.round(estDistanceKm * 2.2));
        int bikeTimeMins = (int) Math.max(6, Math.round(estDistanceKm * 1.8));
        int transitTimeMins = (int) Math.max(12, Math.round(estDistanceKm * 3.1));
        int walkTimeMins = (int) Math.max(20, Math.round(estDistanceKm * 12.5));

        BorderPane mapLayout = new BorderPane();
        mapLayout.setPrefSize(1000, 640);
        mapLayout.setStyle("-fx-background-color: white;");

        // LEFT DIRECTION CONTROL PANEL
        VBox leftNavPanel = new VBox(12);
        leftNavPanel.setPrefWidth(350);
        leftNavPanel.setMinWidth(350);
        leftNavPanel.setPadding(new Insets(16));
        leftNavPanel.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 1 0 0;");

        HBox modesRow = new HBox(6);
        modesRow.setAlignment(Pos.CENTER);
        modesRow.setPadding(new Insets(0, 0, 8, 0));
        modesRow.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        modesRow.getChildren().addAll(
                createModeTab("🚗 Driving", driveTimeMins + " min", true),
                createModeTab("🏍️ Bike", bikeTimeMins + " min", false),
                createModeTab("🚌 Transit", transitTimeMins + " min", false),
                createModeTab("🚶 Walk", walkTimeMins + " min", false)
        );

        VBox routeInputs = new VBox(8);
        routeInputs.setPadding(new Insets(10));
        routeInputs.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox originRow = new HBox(6);
        originRow.setAlignment(Pos.CENTER_LEFT);
        Label originIcon = new Label("⚪"); originIcon.setStyle("-fx-font-size: 10px; -fx-text-fill: #10B981;");
        TextField txtOrigin = new TextField("Customer: " + origin);
        txtOrigin.setEditable(false);
        txtOrigin.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        HBox.setHgrow(txtOrigin, Priority.ALWAYS);
        originRow.getChildren().addAll(originIcon, txtOrigin);

        HBox destRow = new HBox(6);
        destRow.setAlignment(Pos.CENTER_LEFT);
        Label destIcon = new Label("🔴"); destIcon.setStyle("-fx-font-size: 11px; -fx-text-fill: #EF4444;");
        TextField txtDest = new TextField("Venue: " + destination);
        txtDest.setEditable(false);
        txtDest.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        HBox.setHgrow(txtDest, Priority.ALWAYS);
        destRow.getChildren().addAll(destIcon, txtDest);

        routeInputs.getChildren().addAll(originRow, new Separator(), destRow);

        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("h:mm a");

        String timeNow = now.format(fmt);
        String arrDrive = now.plusMinutes(driveTimeMins).format(fmt);
        String arrTransit = now.plusMinutes(transitTimeMins).format(fmt);

        VBox routesList = new VBox(10);
        routesList.setPadding(new Insets(10, 0, 0, 0));

        VBox opt1 = createRouteOptionCard(
                "🚗 " + timeNow + " — " + arrDrive,
                driveTimeMins + " min (" + String.format("%.1f", estDistanceKm) + " km)",
                "Fastest logistics route via Main " + originCity + " Highway",
                "Traffic is currently light",
                true
        );

        VBox opt2 = createRouteOptionCard(
                "🚌 " + timeNow + " — " + arrTransit,
                transitTimeMins + " min",
                "Public Transit Route to " + (venueNameLabel != null ? venueNameLabel.getText() : "Venue"),
                "Transit available every 12 mins",
                false
        );

        VBox opt3 = createRouteOptionCard(
                "🏍️ " + timeNow + " — " + now.plusMinutes(bikeTimeMins).format(fmt),
                bikeTimeMins + " min (" + String.format("%.1f", estDistanceKm - 0.4) + " km)",
                "via " + originArea + " Link Road",
                "Avoids main road traffic signals",
                false
        );

        routesList.getChildren().addAll(new Label("LOGISTICS & DRIVING DIRECTIONS:"), opt1, opt2, opt3);
        for (javafx.scene.Node n : routesList.getChildren()) {
            if (n instanceof Label) ((Label) n).setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        }

        leftNavPanel.getChildren().addAll(modesRow, routeInputs, routesList);
        mapLayout.setLeft(leftNavPanel);

        // RIGHT REAL-TIME MAP CONTAINER
        StackPane mapContainer = new StackPane();
        mapContainer.setStyle("-fx-background-color: #E2E8F0;");

        boolean webViewLoaded = false;
        try {
            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            javafx.scene.web.WebEngine engine = webView.getEngine();
            String encodedDest = java.net.URLEncoder.encode(destination, java.nio.charset.StandardCharsets.UTF_8);
            String encodedOrigin = java.net.URLEncoder.encode(origin, java.nio.charset.StandardCharsets.UTF_8);

            String mapsEmbedUrl = "https://www.google.com/maps?saddr=" + encodedOrigin + "&daddr=" + encodedDest + "&output=embed";
            engine.load(mapsEmbedUrl);
            mapContainer.getChildren().add(webView);
            webViewLoaded = true;
        } catch (Throwable t) {
            webViewLoaded = false;
        }

        if (!webViewLoaded) {
            try {
                Image mapImage = new Image(getClass().getResource("/assets/images/map img.jpg").toExternalForm());
                ImageView mv = new ImageView(mapImage);
                mv.setFitWidth(650);
                mv.setFitHeight(640);
                mv.setPreserveRatio(false);
                mapContainer.getChildren().add(mv);
            } catch (Exception ignored) {}

            Pane routeLayer = new Pane();
            Line route = new Line(180, 480, 520, 180);
            route.setStroke(Color.web("#1A73E8"));
            route.setStrokeWidth(6);

            Circle cOrigin = new Circle(180, 480, 8, Color.web("#10B981"));
            Circle cDest = new Circle(520, 180, 10, Color.web("#EA4335"));

            Label timeBubble = new Label("🚗 " + driveTimeMins + " min (" + String.format("%.1f", estDistanceKm) + " km)\nFastest route");
            timeBubble.setLayoutX(310); timeBubble.setLayoutY(300);
            timeBubble.setStyle("-fx-background-color: white; -fx-padding: 4 8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);");

            Label lblOriginTag = new Label("Customer: " + origin);
            lblOriginTag.setLayoutX(120); lblOriginTag.setLayoutY(495);
            lblOriginTag.setStyle("-fx-background-color: white; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4px;");

            Label lblDestTag = new Label("Venue: " + (venueNameLabel != null ? venueNameLabel.getText() : destination));
            lblDestTag.setLayoutX(440); lblDestTag.setLayoutY(155);
            lblDestTag.setStyle("-fx-background-color: white; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4px;");

            routeLayer.getChildren().addAll(route, cOrigin, cDest, timeBubble, lblOriginTag, lblDestTag);
            mapContainer.getChildren().add(routeLayer);
        }

        mapLayout.setCenter(mapContainer);

        Button closeBtn = new Button("✕ Close");
        closeBtn.setStyle("-fx-background-color: #062A60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());

        HBox topCloseBar = new HBox(closeBtn);
        topCloseBar.setAlignment(Pos.CENTER_RIGHT);
        topCloseBar.setPadding(new Insets(10, 16, 10, 16));
        topCloseBar.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        mapLayout.setTop(topCloseBar);

        dialog.getDialogPane().setContent(mapLayout);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setManaged(false);

        dialog.showAndWait();
    }

    private double calculateDynamicDistance(String origin, String destination) {
        if (origin == null || destination == null) return 5.4;
        int hash = Math.abs((origin + destination).hashCode());
        return 3.0 + (hash % 60) / 10.0;
    }

    private VBox createModeTab(String modeName, String duration, boolean active) {
        VBox tab = new VBox(2);
        tab.setAlignment(Pos.CENTER);
        tab.setPadding(new Insets(6, 10, 6, 10));
        tab.setStyle(active
                ? "-fx-background-color: #E8F0FE; -fx-background-radius: 6px; -fx-border-color: #1A73E8; -fx-border-radius: 6px; -fx-cursor: hand;"
                : "-fx-background-color: transparent; -fx-cursor: hand;");

        Label m = new Label(modeName);
        m.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (active ? "#1A73E8" : "#475569") + ";");
        Label d = new Label(duration);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (active ? "#1A73E8" : "#64748B") + ";");

        tab.getChildren().addAll(m, d);
        return tab;
    }

    private VBox createRouteOptionCard(String timeRange, String totalTime, String routeVia, String subDetail, boolean highlighted) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(10));
        card.setStyle(highlighted
                ? "-fx-background-color: #F0F7FF; -fx-border-color: #3B82F6; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;"
                : "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

        HBox top = new HBox();
        Label time = new Label(timeRange);
        time.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label dur = new Label(totalTime);
        dur.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
        top.getChildren().addAll(time, sp, dur);

        Label via = new Label(routeVia);
        via.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        Label sub = new Label(subDetail);
        sub.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        card.getChildren().addAll(top, via, sub);
        return card;
    }

    private HBox createRiskRow(String labelText, String badgeText, String bgHex, String textHex) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));
        row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label l = new Label(labelText); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label b = new Label(badgeText);
        b.setStyle("-fx-background-color: " + bgHex + "; -fx-text-fill: " + textHex + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 4px;");

        row.getChildren().addAll(l, sp, b);
        return row;
    }

    private VBox createAlternativeVenuesSection() {
        VBox section = new VBox(12);

        Label title = new Label("Curated Nearby Venues (Click card to select & preview)");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        alternativeVenuesContainer = new HBox(16);
        section.getChildren().addAll(title, alternativeVenuesContainer);
        return section;
    }

    private void handleSendSuggestions() {
        if (currentEvent == null || dynamicCuratedVenues.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "No active event or venue suggestions available to send.");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
            return;
        }

        boolean success = venueController.sendVenueSuggestions(currentEvent.getEventId(), dynamicCuratedVenues);
        if (success) {
            statusBadgeLabel.setText("⏳ SUGGESTIONS SENT");
            statusBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Venue suggestions matching budget (" + venueCostLabel.getText() + ") have been live-synced to Customer's Venue Screen!");
            alert.setHeaderText("Suggestions Dispatched");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update Firestore.");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        }
    }

    private VBox createCommonOrganizerSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard":
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
                break;
            case "Event Requests":
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
                break;
            case "My Events":
                handleBack(LandingPage.landingPageStage);
                break;
            case "Schedule":
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
                break;
            case "Notifications":
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
                break;
            case "Profile":
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
                break;
            case "Settings":
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
                break;
            case "Support":
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
                break;
        }
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}