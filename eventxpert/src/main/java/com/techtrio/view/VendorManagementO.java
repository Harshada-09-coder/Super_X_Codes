package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VendorManagementO extends Application {

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
    private static final String DANGER_BG = "#FCE8E6";
    private static final String DANGER_TEXT = "#C5221F";

    // In-Memory Data Models & State
    private static class VendorItem {
        String name;
        String category;
        String contact;
        String phone;
        String email;
        String contractStatus;
        String payment;
        String paymentStatus;
        String status;
        String rating;

        VendorItem(String name, String category, String contact, String phone, String email, 
                   String contractStatus, String payment, String paymentStatus, String status, String rating) {
            this.name = name;
            this.category = category;
            this.contact = contact;
            this.phone = phone;
            this.email = email;
            this.contractStatus = contractStatus;
            this.payment = payment;
            this.paymentStatus = paymentStatus;
            this.status = status;
            this.rating = rating;
        }
    }

    private final List<VendorItem> masterVendorList = new ArrayList<>();
    private VBox tableRowsContainer;

    @Override
    public void start(Stage primaryStage) {
        initSampleData();

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

        // Assemble Layout Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createSummaryCardsRow(),
                createRequirementsAndBudgetRow(primaryStage),
                createVendorTableSection(primaryStage),
                createAiRecommendationAndPipelineRow(primaryStage),
                createPerformanceAndTasksRow()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Vendor Management");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleData() {
        masterVendorList.add(new VendorItem("VisionPro AV", "AV & Sound", "Amit Sharma", "+91 98220 11223", "amit@visionpro.in", "✓ Signed", "₹1,60,000 Paid", "Paid", "Confirmed", "★★★★★ 4.8"));
        masterVendorList.add(new VendorItem("FreshBite Catering", "Catering", "Neha Mehta", "+91 98190 44321", "neha@freshbite.com", "✓ Signed", "₹1,40,000 Paid", "Paid", "Confirmed", "★★★★★ 4.7"));
        masterVendorList.add(new VendorItem("Elegant Events", "Decoration", "Rohan Patil", "+91 97650 88210", "rohan@elegantevents.in", "⚠ Pending", "₹1,20,000 Partial", "Partially Paid", "At Risk", "★★★★☆ 4.5"));
        masterVendorList.add(new VendorItem("PixelFrame Studios", "Photography", "Sneha Joshi", "+91 99300 55441", "sneha@pixelframe.com", "✓ Signed", "₹70,000 Paid", "Paid", "Confirmed", "★★★★★ 4.9"));
        masterVendorList.add(new VendorItem("SafeGuard Security", "Security", "Rajesh Kumar", "+91 98811 22334", "info@safeguard.co.in", "✓ Signed", "₹40,000 Pending", "Pending", "Pending", "★★★★☆ 4.4"));
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

        // Menu Sections
        sidebar.getChildren().add(createSidebarCategory("MAIN"));
        sidebar.getChildren().add(createSidebarItem("Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));
        sidebar.getChildren().add(createSidebarItem("Event Requests", false, e -> showToast(stage, "Opening Event Requests...")));
        sidebar.getChildren().add(createSidebarItem("My Events", false, e -> showToast(stage, "Opening My Events...")));

        sidebar.getChildren().add(createSidebarCategory("EVENT PLANNING"));
        sidebar.getChildren().add(createSidebarItem("AI Planner", false, e -> showToast(stage, "Opening AI Planner...")));
        sidebar.getChildren().add(createSidebarItem("Budget", false, e -> showToast(stage, "Opening Budget...")));
        sidebar.getChildren().add(createSidebarItem("Venue", false, e -> showToast(stage, "Opening Venue Management...")));
        sidebar.getChildren().add(createSidebarItem("Vendors", true, e -> {})); // Active Menu
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
        searchField.setPromptText("🔍 Search vendors, services, contacts...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "No new vendor alerts"));

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, notifBtn, helpBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Vendors");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Vendor Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label tag = new Label("TechVision Annual Summit (In Planning)");
        tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button findVendorsBtn = createSecondaryButton("Find Vendors");
        findVendorsBtn.setOnAction(e -> openFindVendorsModal(stage));

        Button addVendorBtn = createPrimaryButton("+ Add Vendor");
        addVendorBtn.setOnAction(e -> openAddVendorModal(stage));

        titleRow.getChildren().addAll(title, tag, spacer, findVendorsBtn, addVendorBtn);

        Label subtitle = new Label("Find, compare and manage trusted vendors for your event.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(16);

        row.getChildren().addAll(
                createSummaryCard("REQUIRED VENDORS", "6", "Total categories", PRIMARY_BLUE),
                createSummaryCard("CONFIRMED", "3", "Vendors confirmed", GREEN_TEXT),
                createSummaryCard("PENDING", "2", "Awaiting confirmation", WARNING_TEXT),
                createSummaryCard("BUDGET ALLOCATED", "₹2,45,000", "For vendors", NAVY)
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String color) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(t, v, s);
        return card;
    }

    // ==========================================
    // 5. VENDOR REQUIREMENTS & BUDGET ROW
    // ==========================================
    private HBox createRequirementsAndBudgetRow(Stage stage) {
        HBox row = new HBox(16);

        // Vendor Requirements Grid Section (Left - matching Stitch layout)
        VBox reqSection = new VBox(12);
        HBox.setHgrow(reqSection, Priority.ALWAYS);

        Label reqTitle = new Label("Vendor Requirements");
        reqTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane reqGrid = new GridPane();
        reqGrid.setHgap(16);
        reqGrid.setVgap(16);

        reqGrid.add(createReqCard(stage, "🎙 AV & Sound", "VisionPro AV", "Budget: ₹80,000", "Confirmed", true), 0, 0);
        reqGrid.add(createReqCard(stage, "🍽 Catering", "Awaiting Quotes", "Budget: ₹1,25,000", "Pending", false), 1, 0);
        reqGrid.add(createReqCard(stage, "📷 Photography", "PixelFrame Studios", "Budget: ₹35,000", "Confirmed", true), 0, 1);
        reqGrid.add(createReqCard(stage, "🎋 Decoration & Setup", "Shortlisting", "Budget: ₹45,000", "Pending", false), 1, 1);

        reqSection.getChildren().addAll(reqTitle, reqGrid);

        // Budget & Alerts Panel (Right)
        VBox rightPanel = new VBox(16);
        rightPanel.setPrefWidth(380);

        // Budget Tracker Card
        VBox budgetCard = createCardContainer();
        Label bTitle = new Label("📊 Vendor Budget Tracker");
        bTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox bStats = new HBox();
        VBox allocBox = new VBox(2, new Label("ALLOCATED"), createBoldLabel("₹2,45,000", TEXT_MAIN));
        VBox commitBox = new VBox(2, new Label("COMMITTED"), createBoldLabel("₹1,45,000", BRIGHT_BLUE));
        commitBox.setAlignment(Pos.TOP_RIGHT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        bStats.getChildren().addAll(allocBox, sp, commitBox);

        ProgressBar pb = new ProgressBar(0.60);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        HBox remBox = new HBox();
        Label remLbl = new Label("Remaining");
        remLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label remVal = new Label("₹1,00,000");
        remVal.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + GREEN_TEXT + ";");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        remBox.getChildren().addAll(remLbl, sp2, remVal);

        budgetCard.getChildren().addAll(bTitle, bStats, pb, remBox);

        // Alerts Card
        VBox alertsCard = createCardContainer();
        Label aTitle = new Label("🔔 Vendor Alerts");
        aTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox alert1 = createAlertBox("⚠ Catering Pending", "Secure a caterer soon to guarantee availability for 250 pax.", WARNING_BG, WARNING_TEXT);
        VBox alert2 = createAlertBox("✓ AV Setup Confirmed", "VisionPro AV has acknowledged the layout plan.", GREEN_BG, GREEN_TEXT);

        alertsCard.getChildren().addAll(aTitle, alert1, alert2);

        rightPanel.getChildren().addAll(budgetCard, alertsCard);

        row.getChildren().addAll(reqSection, rightPanel);
        return row;
    }

    private VBox createReqCard(Stage stage, String category, String details, String budget, String status, boolean isConfirmed) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        Label catLbl = new Label(category);
        catLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label statusBadge = new Label(status);
        if (isConfirmed) {
            statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 2 8; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-font-size: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 2 8; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-font-size: 10px;");
        }
        topRow.getChildren().addAll(catLbl, sp, statusBadge);

        Label detLbl = new Label(isConfirmed ? "Vendor: " + details : "Status: " + details);
        detLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label budLbl = new Label(budget);
        budLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Button actionBtn;
        if (isConfirmed) {
            actionBtn = createSecondaryButton("View Vendor");
            actionBtn.setMaxWidth(Double.MAX_VALUE);
            actionBtn.setOnAction(e -> openVendorDetailsModal(stage, details));
        } else {
            actionBtn = createPrimaryButton("Find Vendors");
            actionBtn.setMaxWidth(Double.MAX_VALUE);
            actionBtn.setOnAction(e -> openFindVendorsModal(stage));
        }

        card.getChildren().addAll(topRow, detLbl, budLbl, new Separator(), actionBtn);
        return card;
    }

    private VBox createAlertBox(String title, String desc, String bg, String textClr) {
        VBox box = new VBox(2);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8px;");

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + textClr + ";");

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + textClr + ";");

        box.getChildren().addAll(t, d);
        return box;
    }

    // ==========================================
    // 6. MAIN VENDOR TABLE & FILTERS SECTION
    // ==========================================
    private VBox createVendorTableSection(Stage stage) {
        VBox card = createCardContainer();

        Label sectionTitle = new Label("Event Vendors List");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        // Filter Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search vendors...");
        searchField.setPrefWidth(220);

        ComboBox<String> catCB = new ComboBox<>();
        catCB.getItems().addAll("All Categories", "AV & Sound", "Catering", "Decoration", "Photography", "Security");
        catCB.setValue("All Categories");

        ComboBox<String> statusCB = new ComboBox<>();
        statusCB.getItems().addAll("Any Status", "Confirmed", "Pending", "At Risk");
        statusCB.setValue("Any Status");

        Button clearBtn = createSecondaryButton("Clear Filters");

        // Dynamic Filtering Event
        Runnable applyFilters = () -> {
            String query = searchField.getText().toLowerCase();
            String cat = catCB.getValue();
            String stat = statusCB.getValue();

            List<VendorItem> filtered = masterVendorList.stream().filter(v -> {
                boolean matchQ = query.isEmpty() || v.name.toLowerCase().contains(query) || v.category.toLowerCase().contains(query);
                boolean matchC = cat.equals("All Categories") || v.category.equalsIgnoreCase(cat);
                boolean matchS = stat.equals("Any Status") || v.status.equalsIgnoreCase(stat);
                return matchQ && matchC && matchS;
            }).collect(Collectors.toList());

            renderTableRows(stage, filtered);
        };

        searchField.textProperty().addListener((obs, old, val) -> applyFilters.run());
        catCB.setOnAction(e -> applyFilters.run());
        statusCB.setOnAction(e -> applyFilters.run());
        clearBtn.setOnAction(e -> {
            searchField.clear();
            catCB.setValue("All Categories");
            statusCB.setValue("Any Status");
            renderTableRows(stage, masterVendorList);
        });

        filterBar.getChildren().addAll(searchField, catCB, statusCB, clearBtn);

        // Table Header Row
        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(16);
        tableHeader.setPadding(new Insets(10, 12, 10, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addHeaderLabel(tableHeader, "Vendor Name", 0, 200);
        addHeaderLabel(tableHeader, "Category", 1, 130);
        addHeaderLabel(tableHeader, "Contact Person", 2, 140);
        addHeaderLabel(tableHeader, "Contract", 3, 110);
        addHeaderLabel(tableHeader, "Payment", 4, 130);
        addHeaderLabel(tableHeader, "Status", 5, 110);
        addHeaderLabel(tableHeader, "Performance", 6, 120);
        addHeaderLabel(tableHeader, "Action", 7, 100);

        tableRowsContainer = new VBox(6);
        renderTableRows(stage, masterVendorList);

        card.getChildren().addAll(sectionTitle, filterBar, tableHeader, tableRowsContainer);
        return card;
    }

    private void addHeaderLabel(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void renderTableRows(Stage stage, List<VendorItem> vendors) {
        tableRowsContainer.getChildren().clear();

        if (vendors.isEmpty()) {
            Label empty = new Label("No matching vendors found.");
            empty.setStyle("-fx-padding: 16; -fx-text-fill: " + SECONDARY_TEXT + ";");
            tableRowsContainer.getChildren().add(empty);
            return;
        }

        for (VendorItem v : vendors) {
            GridPane row = new GridPane();
            row.setHgap(16);
            row.setPadding(new Insets(10, 12, 10, 12));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            Label nameLbl = new Label(v.name);
            nameLbl.setPrefWidth(200);
            nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");

            Label catLbl = new Label(v.category); catLbl.setPrefWidth(130);
            catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            Label contactLbl = new Label(v.contact); contactLbl.setPrefWidth(140);
            contactLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            Label contractLbl = new Label(v.contractStatus); contractLbl.setPrefWidth(110);
            contractLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            Label payLbl = new Label(v.payment); payLbl.setPrefWidth(130);
            payLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            Label statusBadge = new Label(v.status);
            statusBadge.setPrefWidth(110);
            if (v.status.equals("Confirmed")) {
                statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 2 8; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-font-size: 10px;");
            } else if (v.status.equals("At Risk")) {
                statusBadge.setStyle("-fx-background-color: " + DANGER_BG + "; -fx-text-fill: " + DANGER_TEXT + "; -fx-padding: 2 8; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-font-size: 10px;");
            } else {
                statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 2 8; -fx-background-radius: 10px; -fx-font-weight: bold; -fx-font-size: 10px;");
            }

            Label ratingLbl = new Label(v.rating); ratingLbl.setPrefWidth(120);
            ratingLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");

            Button viewBtn = createSecondaryButton("View");
            viewBtn.setOnAction(e -> openVendorDetailsModal(stage, v.name));

            row.add(nameLbl, 0, 0);
            row.add(catLbl, 1, 0);
            row.add(contactLbl, 2, 0);
            row.add(contractLbl, 3, 0);
            row.add(payLbl, 4, 0);
            row.add(statusBadge, 5, 0);
            row.add(ratingLbl, 6, 0);
            row.add(viewBtn, 7, 0);

            tableRowsContainer.getChildren().add(row);
        }
    }

    // ==========================================
    // 7. AI RECOMMENDATIONS & WORKFLOW PIPELINE
    // ==========================================
    private HBox createAiRecommendationAndPipelineRow(Stage stage) {
        HBox row = new HBox(16);

        // AI Recommendation Card (Left)
        VBox aiCard = createCardContainer();
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        aiCard.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-padding: 16;");

        Label aiTitle = new Label("✨ AI Vendor Recommendations");
        aiTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label aiMsg = new Label("Based on your budget, date and guest capacity, these preferred partners are optimal matches:");
        aiMsg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox recsBox = new HBox(12);
        recsBox.getChildren().addAll(
                createAiMiniBadge("VisionPro AV", "96% Match"),
                createAiMiniBadge("FreshBite Catering", "94% Match"),
                createAiMiniBadge("PixelFrame Studios", "92% Match")
        );

        Button findBtn = createPrimaryButton("Find Best Vendors →");
        findBtn.setOnAction(e -> openFindVendorsModal(stage));

        aiCard.getChildren().addAll(aiTitle, aiMsg, recsBox, findBtn);

        // Vendor Workflow Pipeline Card (Right)
        VBox pipeCard = createCardContainer();
        pipeCard.setPrefWidth(420);

        Label pipeTitle = new Label("Vendor Pipeline Status");
        pipeTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox stepsBox = new HBox(8);
        stepsBox.setAlignment(Pos.CENTER);

        stepsBox.getChildren().addAll(
                createPipeStep("Shortlisted", "12"),
                createPipeStep("Contacted", "10"),
                createPipeStep("Proposal", "8"),
                createPipeStep("Confirmed", "6"),
                createPipeStep("Ready", "5")
        );

        pipeCard.getChildren().addAll(pipeTitle, new Separator(), stepsBox);

        row.getChildren().addAll(aiCard, pipeCard);
        return row;
    }

    private VBox createAiMiniBadge(String name, String match) {
        VBox b = new VBox(2);
        b.setPadding(new Insets(8, 12, 8, 12));
        b.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 8px;");
        Label n = new Label(name); n.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + NAVY + ";");
        Label m = new Label("✓ " + match); m.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GREEN_TEXT + ";");
        b.getChildren().addAll(n, m);
        return b;
    }

    private VBox createPipeStep(String step, String count) {
        VBox b = new VBox(4);
        b.setAlignment(Pos.CENTER);
        b.setPadding(new Insets(8));
        b.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        Label c = new Label(count);
        c.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label s = new Label(step);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        b.getChildren().addAll(c, s);
        return b;
    }

    // ==========================================
    // 8. PERFORMANCE & DAY-OF TASKS
    // ==========================================
    private HBox createPerformanceAndTasksRow() {
        HBox row = new HBox(16);

        // Performance Overview Card
        VBox perfCard = createCardContainer();
        HBox.setHgrow(perfCard, Priority.ALWAYS);

        Label pTitle = new Label("Vendor Performance Index");
        pTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane pGrid = new GridPane();
        pGrid.setHgap(16); pGrid.setVgap(8);

        pGrid.add(createMetricRow("Reliability Score", 0.96, "96%"), 0, 0);
        pGrid.add(createMetricRow("Communication", 0.94, "94%"), 0, 1);
        pGrid.add(createMetricRow("Service Quality", 0.97, "97%"), 1, 0);
        pGrid.add(createMetricRow("On-Time Setup", 0.92, "92%"), 1, 1);

        perfCard.getChildren().addAll(pTitle, pGrid);

        // Day-of Timeline Tasks
        VBox taskCard = createCardContainer();
        taskCard.setPrefWidth(420);

        Label tTitle = new Label("Event-Day Vendor Schedule");
        tTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox tasksList = new VBox(6);
        tasksList.getChildren().addAll(
                createTaskRow("08:00 AM", "AV Team", "Equipment Setup"),
                createTaskRow("08:30 AM", "Decoration", "Stage & Hall Layout"),
                createTaskRow("09:00 AM", "Catering", "Breakfast Setup"),
                createTaskRow("09:30 AM", "Photography", "Pre-event Briefing")
        );

        taskCard.getChildren().addAll(tTitle, tasksList);

        row.getChildren().addAll(perfCard, taskCard);
        return row;
    }

    private VBox createMetricRow(String label, double val, String strVal) {
        VBox box = new VBox(2);
        Label l = new Label(label + " (" + strVal + ")");
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        ProgressBar pb = new ProgressBar(val);
        pb.setPrefWidth(200);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");
        box.getChildren().addAll(l, pb);
        return box;
    }

    private HBox createTaskRow(String time, String team, String task) {
        HBox r = new HBox(12);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(4, 0, 4, 0));

        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label teamLbl = new Label(team + ":");
        teamLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + NAVY + ";");

        Label taskLbl = new Label(task);
        taskLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        r.getChildren().addAll(timeLbl, teamLbl, taskLbl);
        return r;
    }

    // ==========================================
    // MODAL DIALOGS
    // ==========================================
    private void openAddVendorModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add New Vendor");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label modalTitle = new Label("Add New Vendor");
        modalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField nameInput = new TextField(); nameInput.setPromptText("Vendor Name");
        TextField catInput = new TextField(); catInput.setPromptText("Category (e.g. Catering, AV)");
        TextField contactInput = new TextField(); contactInput.setPromptText("Contact Person Name");
        TextField phoneInput = new TextField(); phoneInput.setPromptText("Phone Number");
        TextField emailInput = new TextField(); emailInput.setPromptText("Email Address");

        Button saveBtn = createPrimaryButton("Add Vendor");
        saveBtn.setOnAction(e -> {
            if (!nameInput.getText().isEmpty()) {
                masterVendorList.add(new VendorItem(
                        nameInput.getText(),
                        catInput.getText().isEmpty() ? "General" : catInput.getText(),
                        contactInput.getText().isEmpty() ? "Manager" : contactInput.getText(),
                        phoneInput.getText().isEmpty() ? "+91 90000 00000" : phoneInput.getText(),
                        emailInput.getText().isEmpty() ? "vendor@eventxpert.com" : emailInput.getText(),
                        "✓ Signed", "₹50,000 Pending", "Pending", "Confirmed", "★★★★☆ 4.5"
                ));
                renderTableRows(parentStage, masterVendorList);
                showToast(parentStage, "✓ Vendor '" + nameInput.getText() + "' added successfully!");
                modal.close();
            } else {
                showToast(parentStage, "Please enter a vendor name.");
            }
        });

        layout.getChildren().addAll(modalTitle, new Label("Vendor Name:"), nameInput, new Label("Category:"), catInput,
                new Label("Contact Person:"), contactInput, new Label("Phone:"), phoneInput, saveBtn);

        Scene scene = new Scene(layout, 380, 420);
        modal.setScene(scene);
        modal.show();
    }

    private void openFindVendorsModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Find Preferred Vendors");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("🔍 Find Recommended Vendors");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox v1 = createSearchOptionCard(modal, parentStage, "ProSound Events", "AV & Sound", "₹1,45,000", "91% Match");
        VBox v2 = createSearchOptionCard(modal, parentStage, "Gourmet Delights", "Catering", "₹1,20,000", "89% Match");

        layout.getChildren().addAll(title, v1, v2);

        Scene scene = new Scene(layout, 420, 280);
        modal.setScene(scene);
        modal.show();
    }

    private VBox createSearchOptionCard(Stage modal, Stage parentStage, String name, String cat, String price, String match) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        HBox top = new HBox();
        Label n = new Label(name + " (" + cat + ")");
        n.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label m = new Label(match);
        m.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-font-size: 10px; -fx-font-weight: bold;");
        top.getChildren().addAll(n, sp, m);

        HBox bot = new HBox();
        Label p = new Label("Est. Cost: " + price);
        p.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);

        Button addBtn = createPrimaryButton("Add to Event");
        addBtn.setOnAction(e -> {
            masterVendorList.add(new VendorItem(name, cat, "Contact Manager", "+91 98765 43210", "contact@vendor.com", "✓ Signed", price + " Paid", "Paid", "Confirmed", "★★★★★ 4.8"));
            renderTableRows(parentStage, masterVendorList);
            showToast(parentStage, "Added " + name + " to event vendors!");
            modal.close();
        });

        bot.getChildren().addAll(p, sp2, addBtn);
        box.getChildren().addAll(top, bot);
        return box;
    }

    private void openVendorDetailsModal(Stage parentStage, String vendorName) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Vendor Details - " + vendorName);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Vendor Profile: " + vendorName);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label d1 = new Label("• Contract Status: Signed & Verified");
        Label d2 = new Label("• Performance Rating: 4.8 / 5.0 (Preferred Partner)");
        Label d3 = new Label("• Setup Arrival Time: 08:00 AM (Event Day)");
        Label d4 = new Label("• Contact Availability: Verified 24/7");

        Button closeBtn = createSecondaryButton("Close");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, d1, d2, d3, d4, new Separator(), closeBtn);

        Scene scene = new Scene(layout, 380, 260);
        modal.setScene(scene);
        modal.show();
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

    private Label createBoldLabel(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + color + ";");
        return l;
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