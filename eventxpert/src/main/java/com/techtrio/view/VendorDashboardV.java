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
import javafx.stage.Stage;

public class VendorDashboardV extends Application {

    // Color Palette Constants
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
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
    private static final String ORANGE_BTN = "#EA580C";

    @Override
    public void start(Stage primaryStage) {
        // Root BorderPane
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // =========================================================================
        // 1. LEFT SIDEBAR (Full Window Height, Fixed 250px Width)
        // =========================================================================
        VBox sidebar = createSidebar();
        root.setLeft(sidebar); // Attached to root LEFT so it spans 100% height

        // =========================================================================
        // 2. MAIN APPLICATION AREA (Center of Root)
        // =========================================================================
        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Top Bar (Starts AFTER the sidebar, inside mainArea TOP)
        HBox topBar = createTopBar();
        mainArea.setTop(topBar);

        // Scrollable Dashboard Content (Inside mainArea CENTER)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Assemble Dashboard Sections
        mainContent.getChildren().addAll(
                createPageHeader(),
                createTopHeroCardsRow(),
                createSummaryCardsRow(),
                createMainSplitSection(),
                createServicesPreviewSection()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        // Attach mainArea to Root Center
        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Vendor Portal Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // ==========================================
    // 1. SIDEBAR (STARTS FROM ABSOLUTE TOP-LEFT)
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

        // Logo / Branding Area at the Very Top
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("E");
        logoIcon.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 4 10; -fx-background-radius: 6px;");
        
        VBox brandBox = new VBox(0);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label brandSub = new Label("Plan.Manage.Celebrate");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(brandTitle, brandSub);
        logoBox.getChildren().addAll(logoIcon, brandBox);

        // Top Action Button
        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> System.out.println("Add Service Clicked"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", true));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false));       
        HBox notifItem = createSidebarItemWithBadge("🔔 Notifications", "4");
        sidebar.getChildren().add(notifItem);
        sidebar.getChildren().add(createSidebarItem("👤 Settings", false));
        sidebar.getChildren().add(createSidebarItem("❓ Support", false));

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
        Label userName = new Label("VisionPro ...");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("Audio & Visu...  • ● Available");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
    }


    private Button createSidebarItem(String title, boolean isActive) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        if (isActive) {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        btn.setOnAction(e -> System.out.println(title + " Clicked"));
        return btn;
    }

    private HBox createSidebarItemWithBadge(String title, String badgeCount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Button btn = createSidebarItem(title, false);
        HBox.setHgrow(btn, Priority.ALWAYS);

        Label badge = new Label(badgeCount);
        badge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");

        row.getChildren().addAll(btn, badge);
        return row;
    }

    // ==========================================
    // 2. TOP BAR (STARTS AFTER SIDEBAR)
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search bookings, requests...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");

        Button settingsBtn = new Button("⚙");
        settingsBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");

        Button viewCalBtn = createSecondaryButton("📅 View Calendar");
        viewCalBtn.setOnAction(e -> System.out.println("View Calendar Clicked"));

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("VP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifBtn, settingsBtn, viewCalBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader() {
        VBox header = new VBox(4);

        Label greeting = new Label("Good Morning, VisionPro Events 👋");
        greeting.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Here's what's happening with your bookings, requests and earnings.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(greeting, subtitle);
        return header;
    }

    // ==========================================
    // 4. TOP HERO CARDS ROW
    // ==========================================
    private HBox createTopHeroCardsRow() {
        HBox row = new HBox(16);

        // Left Card: Next Booking
        VBox nextBookingCard = createCardContainer();
        HBox.setHgrow(nextBookingCard, Priority.ALWAYS);

        HBox topRow1 = new HBox();
        topRow1.setAlignment(Pos.CENTER_LEFT);

        Label statusBadge = new Label("Confirmed");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Label categoryTag = new Label("NEXT BOOKING");
        categoryTag.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 0 0 0 10;");

        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);

        Label iconHome = new Label("🏛");
        iconHome.setStyle("-fx-font-size: 18px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 6 10; -fx-background-radius: 50%;");

        topRow1.getChildren().addAll(statusBadge, categoryTag, sp1, iconHome);

        Label eventTitle = new Label("TechVision Annual Summit");
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox detailsGrid = new HBox(24);
        VBox d1 = new VBox(2, new Label("📅 18 May 2026"), new Label("09:00 AM - 06:00 PM"));
        d1.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        d1.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox d2 = new VBox(2, new Label("📍 Grand Convention Center, Pune"), new Label("Hall A, Tech Park"));
        d2.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        d2.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        detailsGrid.getChildren().addAll(d1, d2);

        HBox bottomMeta = new HBox();
        bottomMeta.setAlignment(Pos.CENTER_LEFT);

        Label serviceGuests = new Label("Service & Guests: Audio & Visual • 250 Guests");
        serviceGuests.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Label amount = new Label("Amount: ₹85,000");
        amount.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        bottomMeta.getChildren().addAll(serviceGuests, sp2, amount);

        Button viewBookingBtn = createLinkButton("View Booking →");
        viewBookingBtn.setOnAction(e -> System.out.println("View Booking Clicked"));

        nextBookingCard.getChildren().addAll(topRow1, eventTitle, detailsGrid, new Separator(), bottomMeta, viewBookingBtn);

        // Right Card: Action Required
        VBox actionCard = createCardContainer();
        actionCard.setPrefWidth(420);
        actionCard.setStyle("-fx-background-color: #FFFBEB; -fx-border-color: #FCD34D; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 18;");

        HBox actionHeader = new HBox(10);
        actionHeader.setAlignment(Pos.CENTER_LEFT);

        Label warnIcon = new Label("⚠️");
        warnIcon.setStyle("-fx-font-size: 20px;");

        Label actionTitle = new Label("Action Required");
        actionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #92400E;");

        actionHeader.getChildren().addAll(warnIcon, actionTitle);

        Label actionText = new Label("You have 8 new service requests waiting for your response.");
        actionText.setWrapText(true);
        actionText.setStyle("-fx-font-size: 12px; -fx-text-fill: #78350F;");

        HBox tagsRow = new HBox(8);
        Label tag1 = new Label("2 High Priority");
        tag1.setStyle("-fx-background-color: white; -fx-text-fill: #92400E; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6px; -fx-border-color: #FCD34D; -fx-border-radius: 6px;");

        Label tag2 = new Label("3 This Week");
        tag2.setStyle("-fx-background-color: white; -fx-text-fill: #92400E; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6px; -fx-border-color: #FCD34D; -fx-border-radius: 6px;");

        tagsRow.getChildren().addAll(tag1, tag2);

        Region flexSpace = new Region();
        VBox.setVgrow(flexSpace, Priority.ALWAYS);

        Button reviewBtn = new Button("Review Service Requests →");
        reviewBtn.setStyle("-fx-background-color: " + ORANGE_BTN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 10 18; -fx-background-radius: 8px; -fx-cursor: hand;");
        reviewBtn.setOnAction(e -> System.out.println("Review Requests Clicked"));

        actionCard.getChildren().addAll(actionHeader, actionText, tagsRow, flexSpace, reviewBtn);

        row.getChildren().addAll(nextBookingCard, actionCard);
        return row;
    }

    // ==========================================
    // 5. SUMMARY CARDS ROW
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createStatCard("New Service Requests", "8", "+3 this week", "✉"),
                createStatCard("Upcoming Bookings", "12", "Next: 18 May", "📱"),
                createStatCard("Active Services", "6", "Currently Listed", "📦"),
                createStatCard("Total Earnings", "₹2.48L", "+12.5% vs last month", "💼"),
                createStatCard("Pending Payments", "₹42k", "Across 3 payments", "⏳")
        );

        return row;
    }

    private VBox createStatCard(String title, String val, String sub, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");
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
    // 6. MAIN SPLIT SECTION (TABLE & REQUESTS)
    // ==========================================
    private HBox createMainSplitSection() {
        HBox row = new HBox(16);

        // Left Table Card
        VBox tableCard = createCardContainer();
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        Label tTitle = new Label("Upcoming Bookings");
        tTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewAll = new Label("View All");
        viewAll.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");

        tableHeader.getChildren().addAll(tTitle, sp, viewAll);

        // Upcoming Bookings Table Grid
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        // Table Headers
        addTableHeader(grid, "EVENT", 0);
        addTableHeader(grid, "DATE", 1);
        addTableHeader(grid, "SERVICE", 2);
        addTableHeader(grid, "AMOUNT", 3);
        addTableHeader(grid, "STATUS", 4);

        // Table Rows
        addTableRow(grid, 1, "TechVision Annual Summit", "18 May 2026", "Audio & Visual", "₹85,000", "Confirmed");
        addTableRow(grid, 2, "Corporate Leadership Meet", "22 May 2026", "Stage Lighting", "₹45,000", "Confirmed");
        addTableRow(grid, 3, "Startup Connect 2026", "05 Jun 2026", "Sound System", "₹72,000", "Pending Payment");
        addTableRow(grid, 4, "Annual Gala Night", "12 Jun 2026", "Full A/V Setup", "₹60,000", "Confirmed");

        tableCard.getChildren().addAll(tableHeader, grid);

        // Right Recent Service Requests Column
        VBox requestsCard = createCardContainer();
        requestsCard.setPrefWidth(380);

        HBox reqHeader = new HBox(8);
        reqHeader.setAlignment(Pos.CENTER_LEFT);

        Label rTitle = new Label("Recent Service Requests");
        rTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label newBadge = new Label("New");
        newBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 8px;");

        reqHeader.getChildren().addAll(rTitle, newBadge);

        // Request Item 1
        VBox req1 = createRequestBox("Global Marketing Expo", "₹1.2L", "📱 Audio & Visual Setup", "📅 25 Aug 2026");
        // Request Item 2
        VBox req2 = createRequestBox("Sharma Wedding", "₹85k", "📍 Stage Lighting", "📅 10 Sep 2026");

        requestsCard.getChildren().addAll(reqHeader, req1, req2);

        row.getChildren().addAll(tableCard, requestsCard);
        return row;
    }

    private void addTableHeader(GridPane grid, String title, int col) {
        Label l = new Label(title);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void addTableRow(GridPane grid, int row, String event, String date, String service, String amount, String status) {
        Label eLbl = new Label(event); eLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label dLbl = new Label(date); dLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label sLbl = new Label(service); sLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label aLbl = new Label(amount); aLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label statusBadge = new Label(status);
        if (status.equals("Confirmed")) {
            statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        }

        grid.add(eLbl, 0, row);
        grid.add(dLbl, 1, row);
        grid.add(sLbl, 2, row);
        grid.add(aLbl, 3, row);
        grid.add(statusBadge, 4, row);
    }

    private VBox createRequestBox(String title, String price, String service, String date) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label p = new Label(price);
        p.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        top.getChildren().addAll(t, sp, p);

        Label s = new Label(service);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label d = new Label(date);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button acceptBtn = new Button("Accept");
        acceptBtn.setStyle("-fx-background-color: " + SIDEBAR_NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        acceptBtn.setOnAction(e -> System.out.println("Accepted " + title));

        Button rejectBtn = new Button("Reject");
        rejectBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-padding: 5 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        rejectBtn.setOnAction(e -> System.out.println("Rejected " + title));

        btns.getChildren().addAll(acceptBtn, rejectBtn);

        box.getChildren().addAll(top, s, d, btns);
        return box;
    }

    // ==========================================
    // 7. SERVICES & PACKAGES PREVIEW SECTION
    // ==========================================
    private VBox createServicesPreviewSection() {
        VBox card = createCardContainer();

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Services & Packages Preview");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button manageBtn = createLinkButton("Manage All Services →");
        manageBtn.setOnAction(e -> System.out.println("Manage Services Clicked"));

        header.getChildren().addAll(title, sp, manageBtn);

        FlowPane servicesGrid = new FlowPane();
        servicesGrid.setHgap(10);
        servicesGrid.setVgap(10);

        String[] categories = {
                "🎨 Decoration", "🔊 Sound System", "📷 Photography & Videography",
                "🎪 Rental Equipment", "🖨 Invitation & Printing", "🍽 Catering",
                "🎤 Anchor", "🍰 Cake & Dessert Services"
        };

        for (String cat : categories) {
            Label tag = new Label(cat);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px; -fx-padding: 8 14; -fx-background-radius: 20px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px;");
            servicesGrid.getChildren().add(tag);
        }

        card.getChildren().addAll(header, new Separator(), servicesGrid);
        return card;
    }

    // ==========================================
    // HELPER UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 6, 0, 0, 2);");
        return card;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-font-weight: bold; -fx-cursor: hand;");
        return btn;
    }

    private Button createLinkButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 0; -fx-cursor: hand;");
        return btn;
    }
}