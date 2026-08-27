package com.techtrio.view;

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

public class ServicesPackagesV {
    private Scene servicesPackagesVScene;
    private VendorDashboardV vendorDashboard;
    // Color Palette Constants
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#D97706";
    private static final String GRAY_BG = "#F1F5F9";
    private static final String GRAY_TEXT = "#64748B";

    // Data Structure for Service
    private static class ServiceData {
        String name;
        String desc;
        String price;
        int packages;
        int bookings;
        double rating;
        boolean isActive;
        String iconSymbol;

        ServiceData(String name, String desc, String price, int packages, int bookings, double rating, boolean isActive, String iconSymbol) {
            this.name = name;
            this.desc = desc;
            this.price = price;
            this.packages = packages;
            this.bookings = bookings;
            this.rating = rating;
            this.isActive = isActive;
            this.iconSymbol = iconSymbol;
        }
    }

    private final List<ServiceData> serviceList = new ArrayList<>();
    private FlowPane servicesGridContainer;
    private String selectedCategoryFilter = "All Services";

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getservicesPackagesVScene(Stage primaryStage, Runnable callBackAction) {
        initSampleServices();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left - Fixed 250px Full Height)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Main Application Container
        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Top Bar
        HBox topBar = createTopBar(primaryStage);
        mainArea.setTop(topBar);

        // Scrollable Center Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createSummaryCardsRow(),
                createSmartInsightCard(primaryStage),
                createFilterSection(primaryStage),
                createServicesGridSection(primaryStage),
                createFooter()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        servicesPackagesVScene = scene;
        return servicesPackagesVScene;
    }

    private void initSampleServices() {
        serviceList.clear();
        serviceList.add(new ServiceData("Decoration", "Professional event decoration including stage, floral, theme and entrance setups.", "₹15,000", 3, 12, 4.9, true, "🎉"));
        serviceList.add(new ServiceData("Sound System", "Professional speakers, microphones, DJ and complete event audio setup.", "₹8,000", 3, 18, 4.9, true, "🔊"));
        serviceList.add(new ServiceData("Photography & Videography", "Professional event photography, cinematography and video coverage.", "₹20,000", 3, 15, 4.8, true, "📷"));
        serviceList.add(new ServiceData("Rental Equipment", "Event equipment rentals including chairs, tables, projectors and LED lights.", "₹2,000", 4, 21, 4.8, true, "🪑"));
        serviceList.add(new ServiceData("Invitation & Printing", "Custom invitations, banners, posters, event cards and professional printing.", "₹1,500", 2, 8, 4.7, true, "✉"));
        serviceList.add(new ServiceData("Catering", "Flexible catering services with customized menus for different event sizes.", "₹350 / guest", 3, 14, 4.9, true, "🍽"));
        serviceList.add(new ServiceData("Anchor", "Professional event hosting for corporate events, weddings and celebrations.", "₹5,000", 2, 7, 4.8, true, "🎤"));
        serviceList.add(new ServiceData("Cake & Dessert Services", "Custom cakes, desserts, pastries and celebration dessert arrangements.", "₹2,500", 2, 9, 4.9, true, "🍰"));
    }

    // ==========================================
    // 1. SIDEBAR (FULL HEIGHT)
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

        // Logo / Branding Area
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("E");
        logoIcon.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 4 10; -fx-background-radius: 6px;");
        
        VBox brandBox = new VBox(0);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label brandSub = new Label("VENDOR PORTAL");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(brandTitle, brandSub);
        logoBox.getChildren().addAll(logoIcon, brandBox);

        // Top Action Button
        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> openAddServiceModal(stage));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        // Menu Sections
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> {showToast(stage, "Opening Dashboard...");
         if (vendorDashboard != null) {
                vendorDashboard.backToVendorDashboard();
            } else {
                VendorDashboardV dashboard = new VendorDashboardV();
                Scene dashboardScene = dashboard.getvendorDashboardScene();
                if (LandingPage.landingPageStage != null) {
                    LandingPage.landingPageStage.setScene(dashboardScene);
                }
            }
    }));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", true, e -> {})); // Active
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(stage, "Opening Calendar...")));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Earnings...")));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));
        sidebar.getChildren().add(createSidebarItem("👤 Settings", false, e -> showToast(stage, "Opening Settings...")));
        sidebar.getChildren().add(createSidebarItem("❓ Support", false, e -> showToast(stage, "Opening Support...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom Vendor Profile Pill
        HBox profilePill = new HBox(10);
        profilePill.setAlignment(Pos.CENTER_LEFT);
        profilePill.setPadding(new Insets(10));
        profilePill.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(WHITE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("VisionPro Events");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("Sound System • ● Available");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        if (isActive) {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        return btn;
    }

    private HBox createSidebarItemWithBadge(String title, String badgeCount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Button btn = createSidebarItem(title, false, e -> {});
        HBox.setHgrow(btn, Priority.ALWAYS);

        Label badge = new Label(badgeCount);
        badge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");

        row.getChildren().addAll(btn, badge);
        return row;
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
        searchField.setPromptText("🔍 Search services, packages...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓"); helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button notifBtn = new Button("🔔"); notifBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button settingsBtn = new Button("⚙"); settingsBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("VP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifBtn, settingsBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox header = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Services & Packages");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewReqBtn = createSecondaryButton("View Requests");
        viewReqBtn.setOnAction(e -> showToast(stage, "Opening Service Requests..."));

        Button addServiceBtn = createPrimaryButton("+ Add Service");
        addServiceBtn.setOnAction(e -> openAddServiceModal(stage));

        titleRow.getChildren().addAll(title, spacer, viewReqBtn, addServiceBtn);

        Label subtitle = new Label("Manage the services you offer, create packages and control your pricing.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createSummaryCard("ACTIVE SERVICES", "8", "Currently Listed", PRIMARY_BLUE, "📋"),
                createSummaryCard("ACTIVE PACKAGES", "16", "Across all services", BRIGHT_BLUE, "📦"),
                createSummaryCard("TOTAL BOOKINGS", "38", "📈 +12% this month", GREEN_TEXT, "📅"),
                createSummaryCard("SERVICE REVENUE", "₹2,48,500", "This month", PRIMARY_BLUE, "💳")
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 4 8; -fx-background-radius: 50%;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label s = new Label(sub);
        if (sub.contains("+")) {
            s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold;");
        } else {
            s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        }

        card.getChildren().addAll(top, v, s);
        return card;
    }

    // ==========================================
    // 5. SMART INSIGHT CARD
    // ==========================================
    private HBox createSmartInsightCard(Stage stage) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label icon = new Label("💡"); icon.setStyle("-fx-font-size: 16px;");
        VBox text = new VBox(2);
        Label t = new Label("Smart Insight:"); t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label d = new Label("Sound System is currently your most requested service. Consider creating a premium package to increase revenue."); d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        text.getChildren().addAll(t, d);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button trendsBtn = createSecondaryButton("View Trends →");
        trendsBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-font-size: 11px;");
        trendsBtn.setOnAction(e -> showToast(stage, "Opening Service Trends..."));

        card.getChildren().addAll(icon, text, sp, trendsBtn);
        return card;
    }

    // ==========================================
    // 6. FILTER SECTION
    // ==========================================
    private VBox createFilterSection(Stage stage) {
        VBox card = createCardContainer();

        HBox grid = new HBox(12);
        grid.setAlignment(Pos.CENTER_LEFT);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Find a service...");
        searchInput.setPrefWidth(240);

        ComboBox<String> categoryCB = new ComboBox<>();
        categoryCB.getItems().addAll("All Services", "Decoration", "Sound System", "Photography & Videography", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services");
        categoryCB.setValue("All Services");

        ComboBox<String> statusCB = new ComboBox<>();
        statusCB.getItems().addAll("Status: Active", "Status: All");
        statusCB.setValue("Status: Active");

        ComboBox<String> priceCB = new ComboBox<>();
        priceCB.getItems().addAll("All Prices", "Under ₹10,000", "₹10,000 - ₹20,000", "Above ₹20,000");
        priceCB.setValue("All Prices");

        ComboBox<String> sortCB = new ComboBox<>();
        sortCB.getItems().addAll("Most Booked", "Highest Rated", "Price: Low to High");
        sortCB.setValue("Most Booked");

        Button filterBtn = createSecondaryButton("More Filters");
        filterBtn.setOnAction(e -> {
            selectedCategoryFilter = categoryCB.getValue();
            renderServicesGrid(stage);
        });

        grid.getChildren().addAll(searchInput, categoryCB, statusCB, priceCB, sortCB, filterBtn);
        card.getChildren().add(grid);
        return card;
    }

    // ==========================================
    // 7. SERVICES GRID SECTION (MATCHING STITCH CARDS)
    // ==========================================
    private VBox createServicesGridSection(Stage stage) {
        VBox container = new VBox(16);

        servicesGridContainer = new FlowPane();
        servicesGridContainer.setHgap(16);
        servicesGridContainer.setVgap(16);

        renderServicesGrid(stage);

        container.getChildren().add(servicesGridContainer);
        return container;
    }

    private void renderServicesGrid(Stage stage) {
        servicesGridContainer.getChildren().clear();

        List<ServiceData> filtered = serviceList.stream().filter(s -> {
            if (selectedCategoryFilter.equals("All Services")) return true;
            return s.name.equalsIgnoreCase(selectedCategoryFilter);
        }).collect(Collectors.toList());

        for (ServiceData s : filtered) {
            servicesGridContainer.getChildren().add(createServiceCard(stage, s));
        }
    }

    private VBox createServiceCard(Stage stage, ServiceData s) {
        VBox card = createCardContainer();
        card.setPrefWidth(610); // 2 cards per row on desktop

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLbl = new Label(s.iconSymbol);
        iconLbl.setStyle("-fx-font-size: 20px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 10; -fx-background-radius: 8px;");

        VBox titleBox = new VBox(2);
        Label title = new Label(s.name);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label desc = new Label(s.desc);
        desc.setWrapText(true);
        desc.setPrefWidth(380);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, desc);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label statusBadge = new Label(s.isActive ? "● Active" : "Inactive");
        if (s.isActive) {
            statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + GRAY_BG + "; -fx-text-fill: " + GRAY_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        }

        topRow.getChildren().addAll(iconLbl, titleBox, sp, statusBadge);

        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        VBox pBox = new VBox(2);
        Label pHead = new Label("STARTING AT"); pHead.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label pVal = new Label(s.price); pVal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        pBox.getChildren().addAll(pHead, pVal);

        Label pkgTag = new Label("📦 " + s.packages + " Packages"); pkgTag.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label bkTag = new Label("✓ " + s.bookings + " Bookings"); bkTag.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label ratingTag = new Label("⭐ " + s.rating); ratingTag.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        statsRow.getChildren().addAll(pBox, new Separator(), pkgTag, bkTag, ratingTag);

        HBox btnsRow = new HBox(8);
        Button viewPkgsBtn = createSecondaryButton("View Packages");
        viewPkgsBtn.setOnAction(e -> openPackagesModal(stage, s));

        Button editBtn = createSecondaryButton("Edit");
        editBtn.setOnAction(e -> openEditServiceModal(stage, s));

        btnsRow.getChildren().addAll(viewPkgsBtn, editBtn);

        card.getChildren().addAll(topRow, new Separator(), statsRow, new Separator(), btnsRow);
        return card;
    }

    // ==========================================
    // 8. FOOTER
    // ==========================================
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 10, 0));

        Label copy = new Label("© 2026 EventXpert Management System. All rights reserved.  •  Privacy Policy  •  Terms of Service");
        copy.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        footer.getChildren().add(copy);
        return footer;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openAddServiceModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add New Service");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Add New Service");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        ComboBox<String> nameCB = new ComboBox<>();
        nameCB.getItems().addAll("Decoration", "Sound System", "Photography & Videography", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services");
        nameCB.setValue("Sound System");

        TextArea descArea = new TextArea(); descArea.setPromptText("Service description..."); descArea.setPrefRowCount(3);
        TextField priceInput = new TextField(); priceInput.setPromptText("Starting Price (e.g. ₹10,000)");

        Button saveBtn = createPrimaryButton("Add Service");
        saveBtn.setOnAction(e -> {
            serviceList.add(new ServiceData(nameCB.getValue(), descArea.getText().isEmpty() ? "Professional vendor service package." : descArea.getText(), priceInput.getText().isEmpty() ? "₹10,000" : priceInput.getText(), 2, 0, 5.0, true, "📦"));
            renderServicesGrid(parentStage);
            showToast(parentStage, "✓ Service added successfully!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Category:"), nameCB, new Label("Description:"), descArea, new Label("Price:"), priceInput, saveBtn);

        Scene scene = new Scene(layout, 360, 360);
        modal.setScene(scene);
        modal.show();
    }

    private void openEditServiceModal(Stage parentStage, ServiceData s) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Edit Service - " + s.name);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Edit " + s.name);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        TextField priceIn = new TextField(s.price);
        TextArea descIn = new TextArea(s.desc); descIn.setPrefRowCount(3);

        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            s.price = priceIn.getText();
            s.desc = descIn.getText();
            renderServicesGrid(parentStage);
            showToast(parentStage, "✓ Service updated!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Starting Price:"), priceIn, new Label("Description:"), descIn, saveBtn);

        Scene scene = new Scene(layout, 340, 280);
        modal.setScene(scene);
        modal.show();
    }

    private void openPackagesModal(Stage parentStage, ServiceData s) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle(s.name + " Packages");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("📦 Packages for " + s.name);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox p1 = createPackageBox("Basic Tier", s.price, "Standard setup with basic equipment.");
        VBox p2 = createPackageBox("Premium Tier", "₹25,000", "Complete setup with premium equipment and staff.");

        Button closeBtn = createSecondaryButton("Close");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, p1, p2, new Separator(), closeBtn);

        Scene scene = new Scene(layout, 380, 320);
        modal.setScene(scene);
        modal.show();
    }

    private VBox createPackageBox(String name, String price, String desc) {
        VBox b = new VBox(4);
        b.setPadding(new Insets(10));
        b.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        HBox head = new HBox();
        Label n = new Label(name); n.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label p = new Label(price); p.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        head.getChildren().addAll(n, sp, p);

        Label d = new Label(desc); d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        b.getChildren().addAll(head, d);
        return b;
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 7 15; -fx-cursor: hand;");
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