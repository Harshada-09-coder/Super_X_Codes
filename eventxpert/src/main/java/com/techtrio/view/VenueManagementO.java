package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.techtrio.session.UserSession;

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

    private String selectedVenueName = "Grand Convention Center";
    private String selectedVenueCost = "₹1,50,000";

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

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Venue");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Venue Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Planning");
        backBtn.setOnAction(e -> handleBack(stage));

        titleRow.getChildren().addAll(title, spacer, backBtn);

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
        Label costVal = new Label(selectedVenueCost);
        costVal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        costBox.getChildren().addAll(costTitle, costVal);

        cardHeader.getChildren().addAll(statusBadge, sp, costBox);

        Label venueName = new Label(selectedVenueName);
        venueName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label locationSub = new Label("📍 Senapati Bapat Road, Pune");
        locationSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox metricsGrid = new HBox(16);
        metricsGrid.setPadding(new Insets(12, 0, 0, 0));
        metricsGrid.getChildren().addAll(
                createMiniSpecBox("Capacity", "500 (Req: 250)", "✓ Fits Guests"),
                createMiniSpecBox("Availability", "Dates secured", "✓ 18 May 2026"),
                createMiniSpecBox("Budget", "Within Limit", "✓ Approved")
        );

        mainVenueCard.getChildren().addAll(cardHeader, venueName, locationSub, new Separator(), metricsGrid);

        // Card 2: Smart Venue Match Gauge Card
        VBox matchCard = createCardContainer();
        matchCard.setPrefWidth(350);

        Label matchTitle = new Label("Smart Venue Match");
        matchTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox gaugeBox = new HBox(16);
        gaugeBox.setAlignment(Pos.CENTER_LEFT);

        StackPane circleGauge = createCircularGauge(95);

        VBox gaugeDetails = new VBox(4);
        Label matchRating = new Label("Excellent fit for TechVision Summit.");
        matchRating.setWrapText(true);
        matchRating.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        gaugeDetails.getChildren().addAll(matchRating);

        gaugeBox.getChildren().addAll(circleGauge, gaugeDetails);

        VBox checklist = new VBox(4);
        checklist.getChildren().addAll(
                new Label("✓ Capacity (250+ req)"),
                new Label("✓ Central Location"),
                new Label("✓ Corporate Setup")
        );
        for (javafx.scene.Node n : checklist.getChildren()) {
            n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + ";");
        }

        VBox aiInsightBox = new VBox(4);
        aiInsightBox.setPadding(new Insets(10));
        aiInsightBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");
        Label aiHeader = new Label("💡 AI Insight:");
        aiHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label aiText = new Label("While this venue is a 95% match, weather patterns suggest a medium rain risk. Ensure the indoor backup area is fully prepped.");
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

        Button directionsBtn = createSecondaryButton("🗺 Directions");
        mapHeader.getChildren().addAll(mapTitle, sp, directionsBtn);

        StackPane mapVisual = new StackPane();
        mapVisual.setPrefHeight(200);
        mapVisual.setStyle("-fx-background-color: #E5EEF8; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        Pane linesPane = new Pane();
        Line line1 = new Line(0, 50, 600, 120); line1.setStroke(Color.WHITE); line1.setStrokeWidth(8);
        Line line2 = new Line(200, 0, 150, 200); line2.setStroke(Color.WHITE); line2.setStrokeWidth(6);
        linesPane.getChildren().addAll(line1, line2);

        VBox callout = new VBox(4);
        callout.setMaxSize(280, 80);
        callout.setPadding(new Insets(10));
        callout.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");

        Label cName = new Label("Grand Convention Center"); cName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");
        Label cAddr = new Label("123 Senapati Bapat Rd, Pune, Maharashtra 411016"); cAddr.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label cDist = new Label("🚗 8.4 km   ⏰ 25-35 min"); cDist.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        callout.getChildren().addAll(cName, cAddr, cDist);
        mapVisual.getChildren().addAll(linesPane, callout);

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
        Label title = new Label("Alternative Venues");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewAll = new Label("View All Options");
        viewAll.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");

        sectionHeader.getChildren().addAll(title, sp, viewAll);

        HBox cardsRow = new HBox(16);
        cardsRow.getChildren().addAll(
                createVenueOptionCard("Blue Horizon Hall", "88% Match", "👥 Cap: 300", "📍 12 km away", "₹1,35,000"),
                createVenueOptionCard("Pune Business Center", "82% Match", "👥 Cap: 250", "📍 5 km away", "₹1,60,000"),
                createVenueOptionCard("Riverside Corporate Hall", "75% Match", "👥 Cap: 400", "📍 18 km away", "₹1,20,000")
        );

        section.getChildren().addAll(sectionHeader, cardsRow);
        return section;
    }

    private VBox createVenueOptionCard(String name, String match, String cap, String dist, String price) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        Label matchBadge = new Label(match);
        matchBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        topRow.getChildren().addAll(sp, matchBadge);

        Label vName = new Label(name); vName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Label vCap = new Label(cap); vCap.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label vDist = new Label(dist); vDist.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label costLbl = new Label("Est. Cost"); costLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region bSp = new Region(); HBox.setHgrow(bSp, Priority.ALWAYS);
        Label costVal = new Label(price); costVal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        bottomRow.getChildren().addAll(costLbl, bSp, costVal);

        card.getChildren().addAll(topRow, vName, vCap, vDist, new Separator(), bottomRow);
        return card;
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