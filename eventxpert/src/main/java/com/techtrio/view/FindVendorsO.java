package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FindVendorsO extends Application {

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

    // Data Structure for Vendor Card
    private static class VendorData {
        String name;
        String category;
        String location;
        double rating;
        String priceRange;
        String matchScore;
        String expYears;
        String availability;
        String shortDesc;
        boolean isBestMatch;

        VendorData(String name, String category, String location, double rating, String priceRange,
                   String matchScore, String expYears, String availability, String shortDesc, boolean isBestMatch) {
            this.name = name;
            this.category = category;
            this.location = location;
            this.rating = rating;
            this.priceRange = priceRange;
            this.matchScore = matchScore;
            this.expYears = expYears;
            this.availability = availability;
            this.shortDesc = shortDesc;
            this.isBestMatch = isBestMatch;
        }
    }

    private final List<VendorData> vendorList = new ArrayList<>();
    private final List<VendorData> shortlistedVendors = new ArrayList<>();
    
    private FlowPane bestMatchesPane;
    private FlowPane allVendorsPane;
    private Label resultsCountLabel;
    private VBox shortlistDrawer;
    private VBox shortlistItemsBox;

    private String activeCategoryFilter = "All Categories";

    @Override
    public void start(Stage primaryStage) {
        initVendorData();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Content inside ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build Header & Top Search Bar
        VBox headerBox = createPageHeader(primaryStage);
        VBox searchCard = createSearchAndChipsCard(primaryStage);

        // Layout containing Left Filter Sidebar + Right Results Grid
        HBox discoveryArea = new HBox(20);

        VBox filterSidebar = createFilterSidebar();
        VBox resultsContainer = createResultsContainer(primaryStage);
        HBox.setHgrow(resultsContainer, Priority.ALWAYS);

        discoveryArea.getChildren().addAll(filterSidebar, resultsContainer);

        // Shortlisted Drawer Floating Widget
        shortlistDrawer = createShortlistDrawer(primaryStage);

        StackPane centerStack = new StackPane();
        centerStack.setAlignment(Pos.BOTTOM_RIGHT);
        
        mainContent.getChildren().addAll(headerBox, searchCard, discoveryArea, createPaginationRow());
        centerStack.getChildren().addAll(mainContent, shortlistDrawer);

        scrollPane.setContent(centerStack);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Find Vendors");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initVendorData() {
        vendorList.add(new VendorData("VisionPro AV", "AV & Sound", "Baner, Pune", 4.8, "₹70k - 80k", "96% Match", "8 Years Exp", "Available", "Professional audio, visual & stage technology for corporate conferences.", true));
        vendorList.add(new VendorData("FreshBite Corporate Catering", "Catering", "Hinjewadi, Pune", 4.7, "₹450-550/guest", "93% Match", "7 Years Exp", "Available", "Corporate catering with customizable menus and professional service staff.", true));
        vendorList.add(new VendorData("PixelFrame Studios", "Photography", "Kharadi, Pune", 4.8, "₹30k - 35k", "91% Match", "9 Years Exp", "Available", "High-end corporate photography and 4K video coverage.", true));
        
        vendorList.add(new VendorData("SoundSphere Events", "AV & Sound", "Viman Nagar, Pune", 4.5, "₹60k - 90k", "88% Match", "5 Years Exp", "Available", "Stage sound reinforcement and corporate LED wall setups.", false));
        vendorList.add(new VendorData("Corporate Feast Catering", "Catering", "Kothrud, Pune", 4.6, "₹400-600/guest", "85% Match", "6 Years Exp", "Available", "Premium buffet catering for large corporate summits.", false));
        vendorList.add(new VendorData("CreativeWorks Decoration", "Decoration", "Aundh, Pune", 4.9, "₹100k - 150k", "84% Match", "10 Years Exp", "Limited", "Corporate stage designs, backdrop framing and lighting setup.", false));
        vendorList.add(new VendorData("SafeFleet Transportation", "Transportation", "Shivajinagar, Pune", 4.4, "₹25k - 40k", "82% Match", "4 Years Exp", "Available", "Executive coach rentals and airport shuttle services.", false));
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

        Label brandLabel = new Label("EventXpert");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Enterprise Suite");
        brandSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox brandBox = new VBox(2, brandLabel, brandSub);
        brandBox.setPadding(new Insets(0, 8, 16, 8));

        sidebar.getChildren().add(brandBox);

        sidebar.getChildren().add(createSidebarCategory("MAIN"));
        sidebar.getChildren().add(createSidebarItem("Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));
        sidebar.getChildren().add(createSidebarItem("Event Requests", false, e -> showToast(stage, "Opening Event Requests...")));
        sidebar.getChildren().add(createSidebarItem("My Events", false, e -> showToast(stage, "Opening My Events...")));

        sidebar.getChildren().add(createSidebarCategory("EVENT PLANNING"));
        sidebar.getChildren().add(createSidebarItem("AI Planner", false, e -> showToast(stage, "Opening AI Planner...")));
        sidebar.getChildren().add(createSidebarItem("Budget", false, e -> showToast(stage, "Opening Budget...")));
        sidebar.getChildren().add(createSidebarItem("Venue", false, e -> showToast(stage, "Opening Venue...")));
        sidebar.getChildren().add(createSidebarItem("Vendors", true, e -> {})); // Active Menu
        sidebar.getChildren().add(createSidebarItem("Schedule", false, e -> showToast(stage, "Opening Schedule...")));

        sidebar.getChildren().add(createSidebarCategory("EXECUTION"));
        sidebar.getChildren().add(createSidebarItem("Guest Management", false, e -> showToast(stage, "Opening Guest Management...")));
        sidebar.getChildren().add(createSidebarItem("QR Check-In", false, e -> showToast(stage, "Opening QR Check-In...")));
        sidebar.getChildren().add(createSidebarItem("Live Event", false, e -> showToast(stage, "Opening Live Event...")));

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
        searchField.setPromptText("🔍 Search vendors, services, locations...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "No new notifications"));

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        HBox topNavRow = new HBox(12);
        topNavRow.setAlignment(Pos.CENTER_LEFT);

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Vendors  >  Find Vendors");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button backBtn = createSecondaryButton("← Back to Vendor Management");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 0; -fx-cursor: hand;");
        backBtn.setOnAction(e -> showToast(stage, "Returning to Vendor Management..."));

        topNavRow.getChildren().addAll(breadcrumb);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Find Vendors");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label tag = new Label("TechVision Annual Summit • Pune • 18 May 2026 • 250 Guests");
        tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleRow.getChildren().addAll(title, backBtn, spacer, tag);

        Label subtitle = new Label("Discover trusted vendors that match your event requirements, budget and location.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(topNavRow, titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. SEARCH & CHIPS CARD
    // ==========================================
    private VBox createSearchAndChipsCard(Stage stage) {
        VBox card = createCardContainer();

        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search vendors or services...");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 14; -fx-font-size: 13px;");

        Button searchBtn = createPrimaryButton("Search");
        searchBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8px; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> applyFilters());

        searchRow.getChildren().addAll(searchInput, searchBtn);

        // Category Chips
        HBox chipsRow = new HBox(8);
        chipsRow.setAlignment(Pos.CENTER_LEFT);
        chipsRow.setPadding(new Insets(6, 0, 0, 0));

        String[] categories = {"All Categories", "AV & Sound", "Catering", "Photography", "Decoration", "Transportation", "Security"};
        for (String cat : categories) {
            Button chip = new Button(cat);
            if (cat.equals(activeCategoryFilter)) {
                chip.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
            } else {
                chip.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
            }

            chip.setOnAction(e -> {
                activeCategoryFilter = cat;
                applyFilters();
                // Refresh chips styling
                createSearchAndChipsCard(stage); 
            });

            chipsRow.getChildren().add(chip);
        }

        card.getChildren().addAll(searchRow, chipsRow);
        return card;
    }

    // ==========================================
    // 5. LEFT FILTER SIDEBAR
    // ==========================================
    private VBox createFilterSidebar() {
        VBox filterCard = createCardContainer();
        filterCard.setPrefWidth(280);

        HBox filterHeader = new HBox();
        Label fTitle = new Label("Filters");
        fTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label clearLink = new Label("Clear all");
        clearLink.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        clearLink.setOnMouseClicked(e -> {
            activeCategoryFilter = "All Categories";
            applyFilters();
        });
        filterHeader.getChildren().addAll(fTitle, sp, clearLink);

        // Filters Group
        VBox catGroup = createFilterGroupLabel("Category");
        ComboBox<String> catCB = new ComboBox<>();
        catCB.getItems().addAll("All Categories", "AV & Sound", "Catering", "Photography", "Decoration", "Transportation");
        catCB.setValue("All Categories");
        catCB.setMaxWidth(Double.MAX_VALUE);
        catCB.setOnAction(e -> {
            activeCategoryFilter = catCB.getValue();
            applyFilters();
        });
        catGroup.getChildren().add(catCB);

        VBox locGroup = createFilterGroupLabel("Location");
        TextField locInput = new TextField("Pune");
        locInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 10;");
        locGroup.getChildren().add(locInput);

        CheckBox availCB = new CheckBox("Available on Event Date");
        availCB.setSelected(true);
        availCB.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label availDate = new Label("18 May 2026");
        availDate.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 0 0 0 22;");
        VBox availGroup = new VBox(2, availCB, availDate);

        VBox budgetGroup = createFilterGroupLabel("Budget Range");
        CheckBox b1 = new CheckBox("₹0 - 50k");
        CheckBox b2 = new CheckBox("₹50k - 100k"); b2.setSelected(true);
        CheckBox b3 = new CheckBox("₹100k+");
        budgetGroup.getChildren().addAll(b1, b2, b3);

        VBox ratingGroup = createFilterGroupLabel("Rating");
        CheckBox r1 = new CheckBox("4.0 & above ⭐"); r1.setSelected(true);
        ratingGroup.getChildren().add(r1);

        VBox matchGroup = createFilterGroupLabel("AI Match Score");
        CheckBox m1 = new CheckBox("80% & above"); m1.setSelected(true);
        matchGroup.getChildren().add(m1);

        filterCard.getChildren().addAll(filterHeader, new Separator(), catGroup, locGroup, availGroup, new Separator(), budgetGroup, ratingGroup, matchGroup);
        return filterCard;
    }

    private VBox createFilterGroupLabel(String title) {
        VBox box = new VBox(6);
        Label l = new Label(title);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().add(l);
        return box;
    }

    // ==========================================
    // 6. MAIN RESULTS CONTAINER
    // ==========================================
    private VBox createResultsContainer(Stage stage) {
        VBox box = new VBox(16);

        // Results Header Row
        HBox resHeader = new HBox();
        resHeader.setAlignment(Pos.CENTER_LEFT);

        resultsCountLabel = new Label("48 Vendors Found");
        resultsCountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label sortLbl = new Label("Sort by: ");
        sortLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        ComboBox<String> sortCB = new ComboBox<>();
        sortCB.getItems().addAll("AI Match Score (High to Low)", "Rating (High to Low)", "Price (Low to High)");
        sortCB.setValue("AI Match Score (High to Low)");

        resHeader.getChildren().addAll(resultsCountLabel, sp, sortLbl, sortCB);

        // Section 1: Best Matches (AI Highlighted)
        VBox bestMatchesSection = new VBox(8);
        Label bestTitle = new Label("✨ Best Matches for Your Event");
        bestTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        bestMatchesPane = new FlowPane();
        bestMatchesPane.setHgap(16);
        bestMatchesPane.setVgap(16);

        bestMatchesSection.getChildren().addAll(bestTitle, bestMatchesPane);

        // Section 2: All Vendors Grid
        VBox allVendorsSection = new VBox(8);
        Label allTitle = new Label("All Vendors");
        allTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        allVendorsPane = new FlowPane();
        allVendorsPane.setHgap(16);
        allVendorsPane.setVgap(16);

        allVendorsSection.getChildren().addAll(allTitle, allVendorsPane);

        renderVendorCards(stage);

        box.getChildren().addAll(resHeader, bestMatchesSection, allVendorsSection);
        return box;
    }

    private void renderVendorCards(Stage stage) {
        bestMatchesPane.getChildren().clear();
        allVendorsPane.getChildren().clear();

        List<VendorData> filtered = vendorList.stream()
                .filter(v -> activeCategoryFilter.equals("All Categories") || v.category.equalsIgnoreCase(activeCategoryFilter))
                .collect(Collectors.toList());

        resultsCountLabel.setText(filtered.size() + " Vendors Found");

        for (VendorData v : filtered) {
            VBox card = createVendorCard(stage, v);
            if (v.isBestMatch) {
                bestMatchesPane.getChildren().add(card);
            } else {
                allVendorsPane.getChildren().add(card);
            }
        }
    }

    private VBox createVendorCard(Stage stage, VendorData v) {
        VBox card = createCardContainer();
        card.setPrefWidth(280);

        // Card Top Badge Header
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconBox = new Label(getCategoryIcon(v.category));
        iconBox.setStyle("-fx-font-size: 18px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 8; -fx-background-radius: 8px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label matchBadge = new Label("✨ " + v.matchScore);
        matchBadge.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 12px;");

        topRow.getChildren().addAll(iconBox, sp, matchBadge);

        // Name & Category
        Label name = new Label(v.name);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        HBox catRatingRow = new HBox(8);
        Label catTag = new Label(v.category);
        catTag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 4px;");

        Label rating = new Label("⭐ " + v.rating);
        rating.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        catRatingRow.getChildren().addAll(catTag, rating);

        Label loc = new Label("📍 " + v.location);
        loc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label price = new Label("💵 " + v.priceRange);
        price.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label desc = new Label("\"" + v.shortDesc + "\"");
        desc.setWrapText(true);
        desc.setPrefHeight(36);
        desc.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-style: italic;");

        // Action Buttons
        HBox actionBox = new HBox(8);
        Button addBtn = createPrimaryButton("+ Add");
        addBtn.setOnAction(e -> {
            if (!shortlistedVendors.contains(v)) {
                shortlistedVendors.add(v);
                updateShortlistDrawer(stage);
                showToast(stage, "Added " + v.name + " to shortlist!");
            } else {
                showToast(stage, v.name + " is already in your shortlist.");
            }
        });

        Button detailsBtn = createSecondaryButton("Details");
        detailsBtn.setOnAction(e -> openVendorDetailsModal(stage, v));

        actionBox.getChildren().addAll(addBtn, detailsBtn);

        card.getChildren().addAll(topRow, name, catRatingRow, loc, price, desc, new Separator(), actionBox);
        return card;
    }

    private String getCategoryIcon(String category) {
        switch (category) {
            case "AV & Sound": return "🎙";
            case "Catering": return "🍽";
            case "Photography": return "📷";
            case "Decoration": return "🎨";
            case "Transportation": return "🚌";
            default: return "📦";
        }
    }

    private void applyFilters() {
        renderVendorCards((Stage) bestMatchesPane.getScene().getWindow());
    }

    // ==========================================
    // 7. SHORTLIST FLOATING DRAWER
    // ==========================================
    private VBox createShortlistDrawer(Stage stage) {
        VBox drawer = new VBox(10);
        drawer.setMaxWidth(300);
        drawer.setPadding(new Insets(12));
        drawer.setStyle("-fx-background-color: " + NAVY + "; -fx-background-radius: 12px 12px 0 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 10, 0, 0, -2);");

        HBox drawerHeader = new HBox();
        Label title = new Label("🛒 Shortlisted Vendors");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        drawerHeader.getChildren().addAll(title, sp);

        shortlistItemsBox = new VBox(6);
        updateShortlistDrawer(stage);

        Button compareBtn = new Button("Compare Selected");
        compareBtn.setMaxWidth(Double.MAX_VALUE);
        compareBtn.setStyle("-fx-background-color: " + BRIGHT_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");
        compareBtn.setOnAction(e -> openCompareModal(stage));

        drawer.getChildren().addAll(drawerHeader, shortlistItemsBox, compareBtn);
        return drawer;
    }

    private void updateShortlistDrawer(Stage stage) {
        shortlistItemsBox.getChildren().clear();

        if (shortlistedVendors.isEmpty()) {
            Label empty = new Label("No vendors shortlisted yet.");
            empty.setStyle("-fx-font-size: 10px; -fx-text-fill: #A0AEC0;");
            shortlistItemsBox.getChildren().add(empty);
            return;
        }

        for (VendorData v : shortlistedVendors) {
            HBox item = new HBox(8);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(4, 8, 4, 8));
            item.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 6px;");

            Label name = new Label(v.name);
            name.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-font-weight: bold;");

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

            Label removeBtn = new Label("✕");
            removeBtn.setStyle("-fx-text-fill: #E2E8F0; -fx-cursor: hand; -fx-font-size: 10px;");
            removeBtn.setOnMouseClicked(e -> {
                shortlistedVendors.remove(v);
                updateShortlistDrawer(stage);
            });

            item.getChildren().addAll(name, sp, removeBtn);
            shortlistItemsBox.getChildren().add(item);
        }
    }

    // ==========================================
    // 8. PAGINATION
    // ==========================================
    private HBox createPaginationRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 0, 0, 0));

        Label count = new Label("Showing 1–8 of 48 vendors");
        count.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button prev = createSecondaryButton("← Previous");
        Button page1 = createPrimaryButton("1");
        Button page2 = createSecondaryButton("2");
        Button page3 = createSecondaryButton("3");
        Button next = createSecondaryButton("Next →");

        HBox pBox = new HBox(4, prev, page1, page2, page3, next);

        row.getChildren().addAll(count, sp, pBox);
        return row;
    }

    // ==========================================
    // MODAL DIALOGS
    // ==========================================
    private void openVendorDetailsModal(Stage parentStage, VendorData v) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Vendor Profile - " + v.name);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label(v.name + " (" + v.category + ")");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label mTag = new Label("✨ " + v.matchScore + " Match for TechVision Summit");
        mTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8px; -fx-font-size: 11px;");

        VBox specs = new VBox(4);
        specs.getChildren().addAll(
                new Label("• Rating: ⭐ " + v.rating + " / 5.0"),
                new Label("• Experience: " + v.expYears),
                new Label("• Pricing: " + v.priceRange),
                new Label("• Location: " + v.location),
                new Label("• Status: " + v.availability + " on 18 May 2026")
        );

        Label desc = new Label(v.shortDesc);
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button addBtn = createPrimaryButton("Add to Event");
        addBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Vendor " + v.name + " added to TechVision Summit!");
            modal.close();
        });

        Button closeBtn = createSecondaryButton("Close");
        closeBtn.setOnAction(e -> modal.close());

        btns.getChildren().addAll(addBtn, closeBtn);

        layout.getChildren().addAll(title, mTag, new Separator(), specs, desc, new Separator(), btns);

        Scene scene = new Scene(layout, 380, 320);
        modal.setScene(scene);
        modal.show();
    }

    private void openCompareModal(Stage parentStage) {
        if (shortlistedVendors.size() < 2) {
            showToast(parentStage, "Please shortlist at least 2 vendors to compare.");
            return;
        }

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Compare Selected Vendors");

        VBox layout = new VBox(14);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Vendor Comparison");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane table = new GridPane();
        table.setHgap(16); table.setVgap(10);

        table.add(new Label("Vendor"), 0, 0);
        table.add(new Label("Rating"), 1, 0);
        table.add(new Label("Price"), 2, 0);
        table.add(new Label("AI Match"), 3, 0);

        int r = 1;
        for (VendorData v : shortlistedVendors) {
            table.add(new Label(v.name), 0, r);
            table.add(new Label("⭐ " + v.rating), 1, r);
            table.add(new Label(v.priceRange), 2, r);
            table.add(new Label(v.matchScore), 3, r);
            r++;
        }

        Button closeBtn = createSecondaryButton("Close Comparison");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, new Separator(), table, new Separator(), closeBtn);

        Scene scene = new Scene(layout, 480, 260);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 5 11; -fx-cursor: hand;");
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