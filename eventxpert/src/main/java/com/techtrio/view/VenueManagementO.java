package com.techtrio.view;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
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

    // Dynamic State Variables (Loaded from Customer's Input)
    private String selectedVenueName = (ReviewPageC.EventData.confirmedVenueName != null && !ReviewPageC.EventData.confirmedVenueName.isBlank())
            ? ReviewPageC.EventData.confirmedVenueName
            : "Grand Convention Center";

    private String selectedVenueAddress = (ReviewPageC.EventData.confirmedVenueAddress != null && !ReviewPageC.EventData.confirmedVenueAddress.isBlank())
            ? ReviewPageC.EventData.confirmedVenueAddress
            : "Senapati Bapat Rd, Pune";

    private String selectedVenueCost = "₹1,50,000";

    // UI elements to update dynamically
    private Label venueNameLabel;
    private Label venueLocationSubLabel;
    private Label venueCostLabel;
    private Label mapCalloutName;
    private Label mapCalloutAddr;

    private String uid;
    private String idToken;
    private Runnable backAction;

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

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 30, 30, 30));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build All Sections
        mainContent.getChildren().addAll(
                createPageHeader(stage),
                createTopSummaryAndMatchRow(),
                createMapAndRiskOverviewRow(),
                createAlternativeVenuesSection()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        return new Scene(root, 1550, 785);
    }

    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("My Events  >  Event Setup  >  Venue Management");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Venue Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // BUTTON 1: CONFIRM & ASSIGN TO VENDOR
        Button assignToVendorBtn = createPrimaryButton("✓ Confirm & Send to Vendor");
        assignToVendorBtn.setOnAction(e -> {
            ReviewPageC.EventData.confirmedVenueName = selectedVenueName;
            ReviewPageC.EventData.confirmedVenueAddress = selectedVenueAddress;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venue Assigned");
            alert.setHeaderText("Success!");
            alert.setContentText("Venue details and Map location sent to Customer & Assigned Vendors successfully.");
            alert.showAndWait();
        });

        // BUTTON 2: BACK
        Button backBtn = createSecondaryButton("← Back to Planning");
        backBtn.setOnAction(e -> handleBack(stage));

        titleRow.getChildren().addAll(title, spacer, assignToVendorBtn, backBtn);

        Label subtitle = new Label("Manage the event venue, location, availability and backup options.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // TOP SUMMARY & SMART VENUE MATCH ROW
    // ==========================================
    private HBox createTopSummaryAndMatchRow() {
        HBox row = new HBox(20);

        // Card 1: Selected Venue Main Card
        VBox mainVenueCard = createCardContainer();
        HBox.setHgrow(mainVenueCard, Priority.ALWAYS);

        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label statusBadge = new Label("✓ Confirmed");
        statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        VBox costBox = new VBox(2);
        costBox.setAlignment(Pos.TOP_RIGHT);
        Label costTitle = new Label("Total Cost");
        costTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        venueCostLabel = new Label(selectedVenueCost);
        venueCostLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        costBox.getChildren().addAll(costTitle, venueCostLabel);

        cardHeader.getChildren().addAll(statusBadge, sp, costBox);

        venueNameLabel = new Label(selectedVenueName);
        venueNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        venueLocationSubLabel = new Label("📍 " + selectedVenueAddress);
        venueLocationSubLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox metricsGrid = new HBox(16);
        metricsGrid.setPadding(new Insets(12, 0, 0, 0));
        metricsGrid.getChildren().addAll(
                createMiniSpecBox("Capacity", "500 (Req: 250)", "✓ Fits Guests"),
                createMiniSpecBox("Availability", "Dates secured", "✓ Confirmed"),
                createMiniSpecBox("Budget", "Within Limit", "✓ Approved")
        );

        mainVenueCard.getChildren().addAll(cardHeader, venueNameLabel, venueLocationSubLabel, new Separator(), metricsGrid);

        // Card 2: Smart Venue Match Gauge Card
        VBox matchCard = createCardContainer();
        matchCard.setPrefWidth(350);

        Label matchTitle = new Label("Smart Venue Match");
        matchTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox gaugeBox = new HBox(16);
        gaugeBox.setAlignment(Pos.CENTER_LEFT);

        StackPane circleGauge = createCircularGauge(95);

        VBox gaugeDetails = new VBox(4);
        Label matchRating = new Label("Excellent fit for the event requirements.");
        matchRating.setWrapText(true);
        matchRating.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        gaugeDetails.getChildren().addAll(matchRating);

        gaugeBox.getChildren().addAll(circleGauge, gaugeDetails);

        VBox checklist = new VBox(4);
        checklist.getChildren().addAll(
                new Label("✓ Capacity Match"),
                new Label("✓ Central Location"),
                new Label("✓ Complete Logistics Setup")
        );
        for (javafx.scene.Node n : checklist.getChildren()) {
            n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + ";");
        }

        VBox aiInsightBox = new VBox(4);
        aiInsightBox.setPadding(new Insets(10));
        aiInsightBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");
        Label aiHeader = new Label("💡 AI Insight:");
        aiHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label aiText = new Label("Venue fits capacity & logistics smoothly. Weather patterns recommend confirming air-conditioned banquet halls.");
        aiText.setWrapText(true);
        aiText.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        aiInsightBox.getChildren().addAll(aiHeader, aiText);

        matchCard.getChildren().addAll(matchTitle, gaugeBox, checklist, aiInsightBox);

        row.getChildren().addAll(mainVenueCard, matchCard);
        return row;
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

    private StackPane createCircularGauge(int percentage) {
        Circle bgCircle = new Circle(28, Color.TRANSPARENT);
        bgCircle.setStroke(Color.web(BORDER_COLOR));
        bgCircle.setStrokeWidth(5);

        Arc arc = new Arc(0, 0, 28, 28, 90, -(360 * percentage / 100.0));
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.web(PRIMARY_BLUE));
        arc.setStrokeWidth(5);
        arc.setFill(Color.TRANSPARENT);

        Label percentageText = new Label(percentage + "%");
        percentageText.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");

        return new StackPane(bgCircle, arc, percentageText);
    }

    // ==========================================
    // MAP LOCATION DETAILS & RISK OVERVIEW
    // ==========================================
    private HBox createMapAndRiskOverviewRow() {
        HBox row = new HBox(20);

        // Left Map Location Card
        VBox mapCard = createCardContainer();
        HBox.setHgrow(mapCard, Priority.ALWAYS);

        HBox mapHeader = new HBox();
        Label mapTitle = new Label("🗺 Location Details");
        mapTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // DIRECTIONS BUTTON - OPENS GOOGLE MAPS
        Button directionsBtn = createSecondaryButton("📍 Open in Google Maps");
        directionsBtn.setOnAction(e -> openInGoogleMaps(selectedVenueName + " " + selectedVenueAddress));

        mapHeader.getChildren().addAll(mapTitle, sp, directionsBtn);

        // Map Visual Panel
        StackPane mapVisual = new StackPane();
        mapVisual.setPrefHeight(220);
        mapVisual.setStyle("-fx-background-color: #E5EEF8; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        try {
            Image mapImage = new Image(getClass().getResource("/assets/images/map img.jpg").toExternalForm());
            ImageView mapView = new ImageView(mapImage);
            mapView.setFitWidth(550);
            mapView.setFitHeight(210);
            mapView.setPreserveRatio(false);
            mapView.setSmooth(true);
            mapVisual.getChildren().add(mapView);
        } catch (Exception ex) {
            Label noImg = new Label("🗺 Interactive Map Preview");
            noImg.setStyle("-fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
            mapVisual.getChildren().add(noImg);
        }

        // Floating Callout Info
        VBox callout = new VBox(4);
        callout.setMaxSize(340, 85);
        callout.setPadding(new Insets(10));
        callout.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 2);");

        mapCalloutName = new Label(selectedVenueName);
        mapCalloutName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");

        mapCalloutAddr = new Label(selectedVenueAddress);
        mapCalloutAddr.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label cDist = new Label("🚗 Logistics Route Ready   [Click 'Open in Google Maps']");
        cDist.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        callout.getChildren().addAll(mapCalloutName, mapCalloutAddr, cDist);
        StackPane.setAlignment(callout, Pos.BOTTOM_LEFT);
        StackPane.setMargin(callout, new Insets(0, 0, 10, 10));

        mapVisual.getChildren().add(callout);
        mapCard.getChildren().addAll(mapHeader, mapVisual);

        // Right Risk Overview Card
        VBox riskCard = createCardContainer();
        riskCard.setPrefWidth(350);

        Label riskTitle = new Label("Risk Overview");
        riskTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        VBox riskList = new VBox(10);
        riskList.getChildren().addAll(
                createRiskRow("☁ Weather Risk", "MEDIUM", WARNING_BG, WARNING_TEXT),
                createRiskRow("👥 Capacity Limits", "SAFE", GREEN_BG, GREEN_TEXT)
        );

        riskCard.getChildren().addAll(riskTitle, new Separator(), riskList);

        row.getChildren().addAll(mapCard, riskCard);
        return row;
    }

    private HBox createRiskRow(String labelText, String badgeText, String bgHex, String textHex) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label l = new Label(labelText); l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label b = new Label(badgeText);
        b.setStyle("-fx-background-color: " + bgHex + "; -fx-text-fill: " + textHex + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 4px;");

        row.getChildren().addAll(l, sp, b);
        return row;
    }

    // ==========================================
    // ALTERNATIVE VENUES SECTION
    // ==========================================
    private VBox createAlternativeVenuesSection() {
        VBox section = new VBox(12);

        HBox sectionHeader = new HBox();
        Label title = new Label("Alternative Venues (Click to select & preview location)");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewAll = new Label("Reset to Customer Location");
        viewAll.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        viewAll.setOnMouseClicked(e -> {
            String defaultAddr = (ReviewPageC.EventData.confirmedVenueAddress != null && !ReviewPageC.EventData.confirmedVenueAddress.isBlank())
                    ? ReviewPageC.EventData.confirmedVenueAddress
                    : "Senapati Bapat Rd, Pune";

            String defaultName = (ReviewPageC.EventData.confirmedVenueName != null && !ReviewPageC.EventData.confirmedVenueName.isBlank())
                    ? ReviewPageC.EventData.confirmedVenueName
                    : "Grand Convention Center";

            updateSelectedVenue(defaultName, defaultAddr, "₹1,50,000");
        });

        sectionHeader.getChildren().addAll(title, sp, viewAll);

        String currentCity = (ReviewPageC.EventData.city != null && !ReviewPageC.EventData.city.isBlank())
                ? ReviewPageC.EventData.city : "Pune";

        HBox cardsRow = new HBox(16);
        cardsRow.getChildren().addAll(
                createVenueOptionCard("Blue Horizon Hall", "Downtown, " + currentCity, "88% Match", "👥 Cap: 300", "📍 12 km away", "₹1,35,000"),
                createVenueOptionCard("Royal Business Center", "Central Avenue, " + currentCity, "82% Match", "👥 Cap: 250", "📍 5 km away", "₹1,60,000"),
                createVenueOptionCard("Riverside Palace", "Riverside Road, " + currentCity, "75% Match", "👥 Cap: 400", "📍 18 km away", "₹1,20,000")
        );

        section.getChildren().addAll(sectionHeader, cardsRow);
        return section;
    }

    private VBox createVenueOptionCard(String name, String address, String match, String cap, String dist, String price) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-cursor: hand;");

        // Hover Effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #FAFCFF; -fx-background-radius: 12px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 12px; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-cursor: hand;"));

        // Click to dynamically select venue
        card.setOnMouseClicked(e -> updateSelectedVenue(name, address, price));

        HBox topRow = new HBox();
        Label matchBadge = new Label(match);
        matchBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        topRow.getChildren().addAll(sp, matchBadge);

        Label vName = new Label(name); vName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Label vAddr = new Label("📍 " + address); vAddr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label vCap = new Label(cap); vCap.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label vDist = new Label(dist); vDist.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label costLbl = new Label("Est. Cost"); costLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region bSp = new Region(); HBox.setHgrow(bSp, Priority.ALWAYS);
        Label costVal = new Label(price); costVal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        bottomRow.getChildren().addAll(costLbl, bSp, costVal);

        card.getChildren().addAll(topRow, vName, vAddr, vCap, vDist, new Separator(), bottomRow);
        return card;
    }

    // ==========================================
    // HELPER METHODS: DYNAMIC UPDATE & GOOGLE MAPS
    // ==========================================
    private void updateSelectedVenue(String name, String address, String cost) {
        this.selectedVenueName = name;
        this.selectedVenueAddress = address;
        this.selectedVenueCost = cost;

        if (venueNameLabel != null) venueNameLabel.setText(name);
        if (venueLocationSubLabel != null) venueLocationSubLabel.setText("📍 " + address);
        if (venueCostLabel != null) venueCostLabel.setText(cost);
        if (mapCalloutName != null) mapCalloutName.setText(name);
        if (mapCalloutAddr != null) mapCalloutAddr.setText(address);
    }

    private void openInGoogleMaps(String locationQuery) {
        try {
            String encodedQuery = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8);
            String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedQuery;

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(mapUrl));
            } else {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", mapUrl});
            }
        } catch (Exception ex) {
            System.err.println("Error opening map: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Location Info");
            alert.setHeaderText(null);
            alert.setContentText("Could not open Google Maps.");
            alert.showAndWait();
        }
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            goDashboard(stage);
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, () -> goDashboard(stage)));
        menu.getChildren().add(createNavBtn("Event Requests", false, () -> {
            EventRequestsO view = new EventRequestsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(() -> goDashboard(stage)));
        }));
        menu.getChildren().add(createNavBtn("My Events", false, () -> {
            MyEventsO view = new MyEventsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getMyEventsOScene(() -> goDashboard(stage)));
        }));
        menu.getChildren().add(createNavBtn("Venue", true, () -> {}));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer);
        return sidebar;
    }

    private Button createNavBtn(String title, boolean active, Runnable action) {
        Button b = new Button(title);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(active ? "-fx-background-color: #EAF3FF; -fx-text-fill: #0B4EA2; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6px;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) b.setOnAction(e -> action.run());
        return b;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setPadding(new Insets(8, 12, 8, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        topBar.getChildren().addAll(searchBox, spacer, avatar);
        return topBar;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        return btn;
    }
}