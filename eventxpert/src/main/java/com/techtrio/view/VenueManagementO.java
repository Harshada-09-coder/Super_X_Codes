package com.techtrio.view;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    private final List<VenueModel> nearbyVenues = new ArrayList<>();
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

        // 1. Sidebar (Matched with AI Planner & Guest Management -> "My Events" Active)
        root.setLeft(createCommonOrganizerSidebar("My Events"));

        // 2. Main Content inside ScrollPane (Top Bar Removed)
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

                String city = (currentEvent != null && currentEvent.getCity() != null) ? currentEvent.getCity() : "Pune";
                String venueType = (currentEvent != null && currentEvent.getVenueType() != null) ? currentEvent.getVenueType() : "Banquet Hall";
                int capacity = (currentEvent != null && currentEvent.getGuests() > 0) ? currentEvent.getGuests() : 250;

                List<VenueModel> venues = venueController.getNearbyVenues(city, venueType, capacity);
                nearbyVenues.clear();
                if (venues != null) {
                    nearbyVenues.addAll(venues);
                }

                Platform.runLater(() -> renderDynamicVenueUI());
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderDynamicVenueUI() {
        if (currentEvent == null) return;

        String vStatus = currentEvent.getVenueStatus() != null ? currentEvent.getVenueStatus().toUpperCase() : "PENDING_SUGGESTION";
        String cityArea = (currentEvent.getArea() != null && !currentEvent.getArea().isBlank() ? currentEvent.getArea() + ", " : "") + (currentEvent.getCity() != null ? currentEvent.getCity() : "Pune");

        if (lblCustomerPref != null) {
            lblCustomerPref.setText("Customer Preferred: " + cityArea + " | Type: " + (currentEvent.getVenueType() != null ? currentEvent.getVenueType() : "Banquet Hall") + " | Guests: " + currentEvent.getGuests());
        }

        // Change Request Alert
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

        // Default or Confirmed Venue setup
        if (currentEvent.getConfirmedVenueName() != null && !currentEvent.getConfirmedVenueName().startsWith("Pending")) {
            venueNameLabel.setText(currentEvent.getConfirmedVenueName());
            venueLocationSubLabel.setText("📍 " + (currentEvent.getConfirmedVenueAddress() != null ? currentEvent.getConfirmedVenueAddress() : cityArea));
            venueCostLabel.setText(currentEvent.getConfirmedVenueCost() != null ? currentEvent.getConfirmedVenueCost() : "₹1,50,000");
            mapCalloutName.setText(currentEvent.getConfirmedVenueName());
            mapCalloutAddr.setText(venueLocationSubLabel.getText());
        } else if (!nearbyVenues.isEmpty()) {
            VenueModel top = nearbyVenues.get(0);
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

        for (VenueModel vm : nearbyVenues) {
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
        Label costLbl = new Label("Est. Cost"); costLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
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

        // Change Request Banner Container
        changeRequestContainer = new HBox(10);
        changeRequestContainer.setAlignment(Pos.CENTER_LEFT);
        changeRequestContainer.setPadding(new Insets(10, 16, 10, 16));
        changeRequestContainer.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-border-color: #FCD34D; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        lblChangeRequestBanner = new Label("⚠️ Customer requested venue alteration.");
        lblChangeRequestBanner.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + WARNING_TEXT + ";");
        changeRequestContainer.getChildren().add(lblChangeRequestBanner);
        changeRequestContainer.setVisible(false);
        changeRequestContainer.setManaged(false);

        // Top Summary & Match Row
        HBox summaryRow = createTopSummaryAndMatchRow();

        // Map & Risk Row
        HBox mapRow = createMapAndRiskOverviewRow();

        // Alternative Venues Section
        VBox altSection = createAlternativeVenuesSection();

        mainBox.getChildren().addAll(breadcrumbs, titleRow, lblCustomerPref, changeRequestContainer, summaryRow, mapRow, altSection);
        return mainBox;
    }

    private HBox createTopSummaryAndMatchRow() {
        HBox row = new HBox(20);

        // Card 1: Selected Venue Main Card
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
        Label costTitle = new Label("Estimated Cost");
        costTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        venueCostLabel = new Label("₹ 1,50,000");
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
                createMiniSpecBox("Location Proximity", "Nearby Customer City", "✓ City Matched"),
                createMiniSpecBox("Guest Capacity", "Fits Expected Guests", "✓ Capacity Verified"),
                createMiniSpecBox("Logistics & Parking", "Verified Setup", "✓ Logistics Ready")
        );

        mainVenueCard.getChildren().addAll(cardHeader, venueNameLabel, venueLocationSubLabel, new Separator(), metricsGrid);

        // Card 2: AI Venue Match
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
        Label aiText = new Label("Recommended venues fit customer guest limits & caterer access seamlessly.");
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

    private HBox createMapAndRiskOverviewRow() {
        HBox row = new HBox(20);

        // Map Card
        VBox mapCard = createCardContainer();
        HBox.setHgrow(mapCard, Priority.ALWAYS);

        HBox mapHeader = new HBox();
        Label mapTitle = new Label("🗺 Interactive Map & Route Preview");
        mapTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button directionsBtn = new Button("📍 Open in Google Maps");
        directionsBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 6px; -fx-border-radius: 6px; -fx-cursor: hand;");
        directionsBtn.setOnAction(e -> openInGoogleMaps(venueNameLabel.getText() + " " + venueLocationSubLabel.getText()));

        mapHeader.getChildren().addAll(mapTitle, sp, directionsBtn);

        StackPane mapVisual = new StackPane();
        mapVisual.setPrefHeight(200);
        mapVisual.setStyle("-fx-background-color: #E5EEF8; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

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

        // Risk Overview Card
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
        if (currentEvent == null || nearbyVenues.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "No active event or venue suggestions available to send.");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
            return;
        }

        boolean success = venueController.sendVenueSuggestions(currentEvent.getEventId(), nearbyVenues);
        if (success) {
            statusBadgeLabel.setText("⏳ SUGGESTIONS SENT");
            statusBadgeLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Venue suggestions have been live-synced to Customer's Venue Screen!");
            alert.setHeaderText("Suggestions Dispatched");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update Firestore.");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        }
    }

    private void openInGoogleMaps(String locationQuery) {
        try {
            String encodedQuery = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8);
            String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedQuery;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(mapUrl));
            }
        } catch (Exception ex) {
            System.err.println("Error opening map: " + ex.getMessage());
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