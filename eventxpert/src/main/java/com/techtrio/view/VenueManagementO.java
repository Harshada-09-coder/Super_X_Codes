package com.techtrio.view;

import javafx.application.Application;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VenueManagementO extends Application {

    // Color Palette Constants
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";

    // Application State Variables
    private String selectedVenueName = "Grand Convention Center";
    private String selectedVenueCost = "₹1,50,000";
    private Label selectedVenueHeaderLabel;
    private Label selectedVenueCostLabel;
    private Label summaryVenueNameLabel;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Scrollable Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createSearchAndFilterCard(),
                createTopSummaryAndMatchRow(),
                createMapAndLocationDetailsRow(),
                createAlternativeVenuesSection(),
                createComparisonAndToolsRow()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Venue Management");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // ==========================================
    // 1. SIDEBAR NAVIGATION
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(6);

        // Brand
        Label brandLabel = new Label("EventXpert");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Organizer Portal");
        brandSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox brandBox = new VBox(2, brandLabel, brandSub);
        brandBox.setPadding(new Insets(0, 8, 16, 8));

        sidebar.getChildren().add(brandBox);

        // Section Items Helper
        sidebar.getChildren().add(createSidebarCategory("MAIN"));
        sidebar.getChildren().add(createSidebarItem("Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));
        sidebar.getChildren().add(createSidebarItem("Event Requests", false, e -> showToast(stage, "Opening Event Requests...")));
        sidebar.getChildren().add(createSidebarItem("My Events", false, e -> showToast(stage, "Opening My Events...")));

        sidebar.getChildren().add(createSidebarCategory("EVENT PLANNING"));
        sidebar.getChildren().add(createSidebarItem("AI Planner", false, e -> showToast(stage, "Opening AI Planner...")));
        sidebar.getChildren().add(createSidebarItem("Budget", false, e -> showToast(stage, "Opening Budget...")));
        sidebar.getChildren().add(createSidebarItem("Venue", true, e -> {})); // Active
        sidebar.getChildren().add(createSidebarItem("Vendors", false, e -> showToast(stage, "Opening Vendors...")));
        sidebar.getChildren().add(createSidebarItem("Schedule", false, e -> showToast(stage, "Opening Schedule...")));

        sidebar.getChildren().add(createSidebarCategory("EXECUTION"));
        sidebar.getChildren().add(createSidebarItem("Guest Management", false, e -> showToast(stage, "Opening Guest Management...")));
        sidebar.getChildren().add(createSidebarItem("QR Check-In", false, e -> showToast(stage, "Opening QR Check-In...")));
        sidebar.getChildren().add(createSidebarItem("Live Event", false, e -> showToast(stage, "Opening Live Event...")));

        // Bottom User Profile Box
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(10));
        profileBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        Text avatarText = new Text("OP");
        avatarText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Admin User");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label userEmail = new Label("admin@eventxpert.com");
        userEmail.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        userDetails.getChildren().addAll(userName, userEmail);

        profileBox.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profileBox);
        return sidebar;
    }

    private Label createSidebarCategory(String title) {
        Label cat = new Label(title);
        cat.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 4 8;");
        return cat;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        return btn;
    }

    // ==========================================
    // 2. TOP BAR
    // ==========================================
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search venues, locations, cities...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "No new notifications"));

        Button settingsBtn = new Button("⚙");
        settingsBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, notifBtn, settingsBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Venue");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Venue Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label tag = new Label("TechVision Annual Summit • 18 May 2026 • 250 Guests");
        tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button emergencyBtn = createSecondaryButton("🚨 Emergency Venue Finder");
        emergencyBtn.setOnAction(e -> openEmergencyVenueModal(stage));

        Button addVenueBtn = createPrimaryButton("+ Add Venue");
        addVenueBtn.setOnAction(e -> openAddVenueModal(stage));

        titleRow.getChildren().addAll(title, tag, spacer, emergencyBtn, addVenueBtn);

        Label subtitle = new Label("Manage the event venue, location, availability and backup options.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. SEARCH & FILTER AREA
    // ==========================================
    private VBox createSearchAndFilterCard() {
        VBox card = createCardContainer();

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);

        // Inputs
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search venue name...");

        ComboBox<String> locationCB = new ComboBox<>();
        locationCB.getItems().addAll("Pune", "Mumbai", "Bangalore", "Delhi");
        locationCB.setValue("Pune");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("18 May 2026");

        ComboBox<String> capacityCB = new ComboBox<>();
        capacityCB.getItems().addAll("100+ Guests", "250+ Guests", "500+ Guests", "1000+ Guests");
        capacityCB.setValue("250+ Guests");

        ComboBox<String> typeCB = new ComboBox<>();
        typeCB.getItems().addAll("All Types", "Convention Center", "Hotel Hall", "Resort");
        typeCB.setValue("All Types");

        Button searchBtn = createPrimaryButton("Search Venues");
        Button clearBtn = createSecondaryButton("Clear");

        grid.add(createFieldLabel("Search Venue"), 0, 0);
        grid.add(searchInput, 0, 1);

        grid.add(createFieldLabel("Location"), 1, 0);
        grid.add(locationCB, 1, 1);

        grid.add(createFieldLabel("Event Date"), 2, 0);
        grid.add(datePicker, 2, 1);

        grid.add(createFieldLabel("Capacity"), 3, 0);
        grid.add(capacityCB, 3, 1);

        grid.add(createFieldLabel("Venue Type"), 4, 0);
        grid.add(typeCB, 4, 1);

        HBox btnBox = new HBox(8, searchBtn, clearBtn);
        btnBox.setAlignment(Pos.BOTTOM_LEFT);
        grid.add(btnBox, 5, 1);

        card.getChildren().add(grid);
        return card;
    }

    private Label createFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return lbl;
    }

    // ==========================================
    // 5. SUMMARY & SMART MATCH ROW
    // ==========================================
    private HBox createTopSummaryAndMatchRow() {
        HBox row = new HBox(16);

        // Selected Venue Main Summary Card (Left)
        VBox mainVenueCard = createCardContainer();
        HBox.setHgrow(mainVenueCard, Priority.ALWAYS);

        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label statusBadge = new Label("✓ Confirmed");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 3 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label costTitle = new Label("Total Cost");
        costTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        selectedVenueCostLabel = new Label(selectedVenueCost);
        selectedVenueCostLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        VBox costBox = new VBox(2, costTitle, selectedVenueCostLabel);
        costBox.setAlignment(Pos.TOP_RIGHT);

        cardHeader.getChildren().addAll(statusBadge, sp, costBox);

        selectedVenueHeaderLabel = new Label(selectedVenueName);
        selectedVenueHeaderLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label locationSub = new Label("📍 Senapati Bapat Road, Pune, Maharashtra");
        locationSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox metricsGrid = new HBox(16);
        metricsGrid.setPadding(new Insets(12, 0, 0, 0));

        metricsGrid.getChildren().addAll(
                createMiniSpecBox("Capacity", "500 (Req: 250)", "✓ Fits Guests"),
                createMiniSpecBox("Availability", "Dates Secured", "✓ 18 May 2026"),
                createMiniSpecBox("Budget", "Within Limit", "✓ Approved")
        );

        mainVenueCard.getChildren().addAll(cardHeader, selectedVenueHeaderLabel, locationSub, new Separator(), metricsGrid);

        // Smart Venue Match Gauge Card (Right)
        VBox matchCard = createCardContainer();
        matchCard.setPrefWidth(380);

        Label matchTitle = new Label("Smart Venue Match");
        matchTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox gaugeBox = new HBox(16);
        gaugeBox.setAlignment(Pos.CENTER_LEFT);

        // Custom Circular Progress Ring
        StackPane circleGauge = createCircularGauge(95);

        VBox gaugeDetails = new VBox(4);
        Label matchRating = new Label("Excellent Fit");
        matchRating.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label matchDesc = new Label("Matches all event criteria for TechVision Summit.");
        matchDesc.setWrapText(true);
        matchDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        gaugeDetails.getChildren().addAll(matchRating, matchDesc);
        gaugeBox.getChildren().addAll(circleGauge, gaugeDetails);

        VBox aiInsightBox = new VBox(4);
        aiInsightBox.setPadding(new Insets(10));
        aiInsightBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");
        Label aiHeader = new Label("💡 AI Insight");
        aiHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label aiText = new Label("High compatibility (95%). Weather models indicate a 40% rain probability. Indoor halls recommended.");
        aiText.setWrapText(true);
        aiText.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        aiInsightBox.getChildren().addAll(aiHeader, aiText);

        matchCard.getChildren().addAll(matchTitle, gaugeBox, aiInsightBox);

        row.getChildren().addAll(mainVenueCard, matchCard);
        return row;
    }

    private VBox createMiniSpecBox(String title, String val, String sub) {
        VBox b = new VBox(2);
        b.setPadding(new Insets(8, 14, 8, 14));
        b.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label v = new Label(val);
        v.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GREEN_TEXT + ";");
        b.getChildren().addAll(t, v, s);
        return b;
    }

    private StackPane createCircularGauge(int percentage) {
        Circle bgCircle = new Circle(32, Color.TRANSPARENT);
        bgCircle.setStroke(Color.web(BORDER_COLOR));
        bgCircle.setStrokeWidth(6);

        Arc arc = new Arc(0, 0, 32, 32, 90, - (360 * percentage / 100.0));
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.web(PRIMARY_BLUE));
        arc.setStrokeWidth(6);
        arc.setFill(Color.TRANSPARENT);

        Label percentageText = new Label(percentage + "%");
        percentageText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

        return new StackPane(bgCircle, arc, percentageText);
    }

    // ==========================================
    // 6. MAP & VENUE DETAILS SECTION
    // ==========================================
    private HBox createMapAndLocationDetailsRow() {
        HBox row = new HBox(16);

        // Map Visual Placeholder Box (Left)
        VBox mapCard = createCardContainer();
        HBox.setHgrow(mapCard, Priority.ALWAYS);

        HBox mapHeader = new HBox();
        Label mapTitle = new Label("Location Details");
        mapTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button directionsBtn = createSecondaryButton("🗺 Get Directions");
        directionsBtn.setOnAction(e -> showToast((Stage) directionsBtn.getScene().getWindow(), "Opening Maps Directions..."));

        mapHeader.getChildren().addAll(mapTitle, sp, directionsBtn);

        // Visual Map Representation
        StackPane mapVisual = new StackPane();
        mapVisual.setPrefHeight(220);
        mapVisual.setStyle("-fx-background-color: #E5EEF8; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        // Map lines background
        Pane linesPane = new Pane();
        Line line1 = new Line(0, 50, 600, 120);
        line1.setStroke(Color.web("#FFFFFF")); line1.setStrokeWidth(8);
        Line line2 = new Line(200, 0, 150, 220);
        line2.setStroke(Color.web("#FFFFFF")); line2.setStrokeWidth(6);
        Line line3 = new Line(0, 180, 600, 40);
        line3.setStroke(Color.web("#FFFFFF")); line3.setStrokeWidth(5);
        linesPane.getChildren().addAll(line1, line2, line3);

        // Map Callout Card Overlay
        VBox callout = new VBox(4);
        callout.setMaxSize(280, 80);
        callout.setPadding(new Insets(10));
        callout.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);");

        summaryVenueNameLabel = new Label(selectedVenueName);
        summaryVenueNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");
        Label addr = new Label("123 Senapati Bapat Rd, Pune • 8.4 km");
        addr.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label timeEst = new Label("🚗 25-35 min from Airport");
        timeEst.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        callout.getChildren().addAll(summaryVenueNameLabel, addr, timeEst);
        mapVisual.getChildren().addAll(linesPane, callout);

        mapCard.getChildren().addAll(mapHeader, mapVisual);

        // Amenities & Weather Card (Right)
        VBox sideDetailsCard = createCardContainer();
        sideDetailsCard.setPrefWidth(380);

        Label detailsTitle = new Label("Venue Amenities & Weather");
        detailsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        GridPane amenitiesGrid = new GridPane();
        amenitiesGrid.setHgap(10);
        amenitiesGrid.setVgap(8);

        String[] amenities = {"✓ Parking (200+)", "✓ Central AC", "✓ Audio/Visual", "✓ Stage Setup", "✓ Catering Area", "✓ Backup Generator"};
        for (int i = 0; i < amenities.length; i++) {
            Label tag = new Label(amenities[i]);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-padding: 5 8; -fx-background-radius: 6px; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            amenitiesGrid.add(tag, i % 2, i / 2);
        }

        // Weather Alert Widget
        HBox weatherBox = new HBox(12);
        weatherBox.setAlignment(Pos.CENTER_LEFT);
        weatherBox.setPadding(new Insets(10));
        weatherBox.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-background-radius: 8px;");

        Label weatherIcon = new Label("⛅");
        weatherIcon.setStyle("-fx-font-size: 22px;");

        VBox weatherText = new VBox(2);
        Label wTitle = new Label("Pune Weather Forecast: 28°C");
        wTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + WARNING_TEXT + ";");
        Label wSub = new Label("Partly Cloudy • Rain Risk: Medium (40%)");
        wSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + WARNING_TEXT + ";");
        weatherText.getChildren().addAll(wTitle, wSub);

        weatherBox.getChildren().addAll(weatherIcon, weatherText);

        sideDetailsCard.getChildren().addAll(detailsTitle, amenitiesGrid, new Separator(), weatherBox);

        row.getChildren().addAll(mapCard, sideDetailsCard);
        return row;
    }

    // ==========================================
    // 7. ALTERNATIVE / RECOMMENDED VENUES
    // ==========================================
    private VBox createAlternativeVenuesSection() {
        VBox section = new VBox(12);

        HBox sectionHeader = new HBox();
        Label title = new Label("Recommended Alternative Venues");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewAll = new Label("View All Options →");
        viewAll.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");

        sectionHeader.getChildren().addAll(title, sp, viewAll);

        HBox cardsRow = new HBox(16);

        cardsRow.getChildren().addAll(
                createVenueCard("Blue Horizon Hall", "Baner, Pune", "Cap: 300", "12 km away", "₹1,35,000", "88% Match"),
                createVenueCard("Pune Business Center", "Hinjewadi, Pune", "Cap: 250", "5 km away", "₹1,60,000", "82% Match"),
                createVenueCard("Riverside Corporate Hall", "Viman Nagar, Pune", "Cap: 400", "18 km away", "₹1,20,000", "75% Match")
        );

        section.getChildren().addAll(sectionHeader, cardsRow);
        return section;
    }

    private VBox createVenueCard(String name, String loc, String cap, String dist, String price, String match) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        Label matchBadge = new Label(match);
        matchBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        topRow.getChildren().addAll(matchBadge);

        Label title = new Label(name);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        Label location = new Label("📍 " + loc + " • " + dist);
        location.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label capacity = new Label("👥 " + cap + " Guests");
        capacity.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label cost = new Label(price);
        cost.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button selectBtn = createSecondaryButton("Select");
        selectBtn.setOnAction(e -> {
            selectedVenueName = name;
            selectedVenueCost = price;
            selectedVenueHeaderLabel.setText(selectedVenueName);
            selectedVenueCostLabel.setText(selectedVenueCost);
            summaryVenueNameLabel.setText(selectedVenueName);
            showToast((Stage) selectBtn.getScene().getWindow(), "Selected venue updated to " + name);
        });

        bottomRow.getChildren().addAll(cost, sp, selectBtn);

        card.getChildren().addAll(topRow, title, location, capacity, new Separator(), bottomRow);
        return card;
    }

    // ==========================================
    // 8. COMPARISON TABLE & EMERGENCY TOOLS
    // ==========================================
    private VBox createComparisonAndToolsRow() {
        VBox card = createCardContainer();

        Label title = new Label("Venue Quick Comparison");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane table = new GridPane();
        table.setHgap(20);
        table.setVgap(10);
        table.setPadding(new Insets(8, 0, 0, 0));

        // Table Headers
        String[] headers = {"Venue Name", "Capacity", "Estimated Cost", "Distance", "Status", "AI Match"};
        for (int c = 0; c < headers.length; c++) {
            Label h = new Label(headers[c]);
            h.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            table.add(h, c, 0);
        }

        // Table Rows
        addTableRow(table, 1, "Grand Convention Center", "500 Guests", "₹1,50,000", "8.4 km", "✓ Selected", "95%");
        addTableRow(table, 2, "Blue Horizon Hall", "300 Guests", "₹1,35,000", "12.0 km", "Available", "88%");
        addTableRow(table, 3, "Pune Business Center", "250 Guests", "₹1,60,000", "5.0 km", "Available", "82%");

        card.getChildren().addAll(title, table);
        return card;
    }

    private void addTableRow(GridPane table, int row, String name, String cap, String price, String dist, String status, String match) {
        table.add(new Label(name), 0, row);
        table.add(new Label(cap), 1, row);
        table.add(new Label(price), 2, row);
        table.add(new Label(dist), 3, row);

        Label statusLbl = new Label(status);
        if (status.contains("Selected")) {
            statusLbl.setStyle("-fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold;");
        } else {
            statusLbl.setStyle("-fx-text-fill: " + SECONDARY_TEXT + ";");
        }
        table.add(statusLbl, 4, row);

        Label matchLbl = new Label(match);
        matchLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        table.add(matchLbl, 5, row);
    }

    // ==========================================
    // MODAL DIALOGS & ACTIONS
    // ==========================================
    private void openAddVenueModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add New Venue");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label modalTitle = new Label("Add New Venue");
        modalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField nameInput = new TextField(); nameInput.setPromptText("Venue Name");
        TextField locationInput = new TextField(); locationInput.setPromptText("Location / Address");
        TextField capacityInput = new TextField(); capacityInput.setPromptText("Capacity (e.g. 400)");
        TextField priceInput = new TextField(); priceInput.setPromptText("Rental Price (e.g. ₹2,00,000)");

        Button saveBtn = createPrimaryButton("Save Venue");
        saveBtn.setOnAction(e -> {
            if (!nameInput.getText().isEmpty()) {
                showToast(parentStage, "✓ Venue '" + nameInput.getText() + "' added successfully!");
                modal.close();
            } else {
                showToast(parentStage, "Please enter a venue name.");
            }
        });

        layout.getChildren().addAll(modalTitle, new Label("Name:"), nameInput, new Label("Location:"), locationInput,
                new Label("Capacity:"), capacityInput, new Label("Price:"), priceInput, saveBtn);

        Scene scene = new Scene(layout, 360, 420);
        modal.setScene(scene);
        modal.show();
    }

    private void openEmergencyVenueModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Emergency Backup Finder");

        VBox layout = new VBox(14);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label modalTitle = new Label("🚨 Emergency Venue Backup");
        modalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label info = new Label("Instantly switch to an available backup venue with guaranteed immediate confirmation.");
        info.setWrapText(true);
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox backupOption = new VBox(4);
        backupOption.setPadding(new Insets(10));
        backupOption.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        Label optTitle = new Label("Skyline Business Hotel (4.2 km away)");
        optTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label optSub = new Label("Capacity: 350 • Price: ₹1,60,000 • Status: Immediate Booking Available");
        optSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        backupOption.getChildren().addAll(optTitle, optSub);

        Button switchBtn = createPrimaryButton("Switch to Backup Venue Now");
        switchBtn.setOnAction(e -> {
            selectedVenueName = "Skyline Business Hotel";
            selectedVenueCost = "₹1,60,000";
            selectedVenueHeaderLabel.setText(selectedVenueName);
            selectedVenueCostLabel.setText(selectedVenueCost);
            summaryVenueNameLabel.setText(selectedVenueName);
            showToast(parentStage, "⚡ Emergency Switch Complete! Assigned: Skyline Business Hotel");
            modal.close();
        });

        layout.getChildren().addAll(modalTitle, info, backupOption, switchBtn);

        Scene scene = new Scene(layout, 400, 260);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI HELPER UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 7 15; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}