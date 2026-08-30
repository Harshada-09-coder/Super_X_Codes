package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyBookingsV {
    private Scene myBookingScene;
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
    private static final String BLUE_BG = "#EFF6FF";
    private static final String BLUE_TEXT = "#2563C7";
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";
    private static final String GRAY_BG = "#F1F5F9";
    private static final String GRAY_TEXT = "#64748B";

    // Data Structure for Booking Record
    private static class BookingData {
        String id;
        String organizer;
        String eventName;
        String venue;
        String dateTime;
        String service;
        String guests;
        String amount;
        String status; // Confirmed, Pending Payment, In Progress, Completed, Cancelled
        String payment; // Paid, Partial, Pending, Refunded

        BookingData(String id, String organizer, String eventName, String venue, String dateTime,
                    String service, String guests, String amount, String status, String payment) {
            this.id = id;
            this.organizer = organizer;
            this.eventName = eventName;
            this.venue = venue;
            this.dateTime = dateTime;
            this.service = service;
            this.guests = guests;
            this.amount = amount;
            this.status = status;
            this.payment = payment;
        }
    }

    private final List<BookingData> bookingList = new ArrayList<>();
    private VBox tableRowsContainer;
    private String activeTabFilter = "All Bookings";

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }


    public Scene getmyBookingScene() {
        return getmyBookingScene(null);
    }

    public Scene getmyBookingScene(Stage primaryStage) {
        initSampleBookings();

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
                createFilterSection(primaryStage),
                createTabsRow(primaryStage),
                createMainSplitSection(primaryStage),
                createPaginationRow()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        myBookingScene = scene;
        return myBookingScene;
    }

    private void initSampleBookings() {
        bookingList.add(new BookingData("1", "TechVision Events", "TechVision Annual Summit", "Grand Convention Center, Pune", "18 May 2026\n09:00 AM - 06:00 PM", "Audio & Visual", "250", "₹85,000", "Confirmed", "Paid"));
        bookingList.add(new BookingData("2", "Corporate Connect", "Leadership Summit 2026", "Business Bay Convention Hall", "22 May 2026\n10:00 AM - 05:00 PM", "Sound System", "180", "₹45,000", "Confirmed", "Partial"));
        bookingList.add(new BookingData("3", "Startup India Network", "Startup Connect 2026", "Pune International Center", "05 Jun 2026\n09:30 AM - 06:00 PM", "Stage & Lighting", "300", "₹72,000", "Pending Payment", "Pending"));
        bookingList.add(new BookingData("4", "Royal Events", "Annual Gala Night", "Royal Orchid Banquet", "12 Jun 2026\n07:00 PM - 11:00 PM", "Lighting & Stage", "200", "₹60,000", "Confirmed", "Paid"));
        bookingList.add(new BookingData("5", "Global Business Forum", "Business Growth Conference", "Westin Hotel, Pune", "20 Jun 2026\n09:00 AM - 05:00 PM", "Audio & Visual", "220", "₹50,000", "In Progress", "Paid"));
        bookingList.add(new BookingData("6", "Dream Wedding Planners", "Sangeet Night", "Lakeside Garden, Pune", "28 Jun 2026\n06:00 PM - 10:00 PM", "Sound System", "150", "₹35,000", "Completed", "Paid"));
        bookingList.add(new BookingData("7", "Event Masters", "Product Launch Event", "Hyatt Regency, Pune", "02 Jul 2026\n10:00 AM - 04:00 PM", "Stage & AV", "180", "₹48,000", "Cancelled", "Refunded"));
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
        addServiceBtn.setOnAction(e -> showToast(stage, "Add Service Clicked"));

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
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", true, e -> {})); // Active Menu
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
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
        Label userSub = new Label("Audio & Visual • ● Available");
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
        searchField.setPromptText("🔍 Search bookings, events, organizers...");
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

        Label title = new Label("My Bookings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = createSecondaryButton("📥 Export");
        exportBtn.setOnAction(e -> showToast(stage, "Exporting bookings data..."));

        Button calendarBtn = createPrimaryButton("📅 Calendar View");
        calendarBtn.setOnAction(e -> showToast(stage, "Calendar View clicked."));

        titleRow.getChildren().addAll(title, spacer, exportBtn, calendarBtn);

        Label subtitle = new Label("Manage all your confirmed, upcoming and completed bookings.");
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
                createSummaryCard("Total Bookings", "32", "All Time", PRIMARY_BLUE, "📱"),
                createSummaryCard("Upcoming", "12", "Next: 18 May 2026", GREEN_TEXT, "📅"),
                createSummaryCard("In Progress", "4", "Currently Live", BLUE_TEXT, "⌛"),
                createSummaryCard("Completed", "16", "This Year", GREEN_TEXT, "✓"),
                createSummaryCard("Cancelled", "2", "This Year", RED_TEXT, "🚫")
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, v, s);
        return card;
    }

    // ==========================================
    // 5. FILTER SECTION
    // ==========================================
    private VBox createFilterSection(Stage stage) {
        VBox card = createCardContainer();

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search event, organizer, service...");

        ComboBox<String> statusCB = new ComboBox<>();
        statusCB.getItems().addAll("All Status", "Confirmed", "In Progress", "Pending Payment", "Completed", "Cancelled");
        statusCB.setValue("All Status");

        ComboBox<String> serviceCB = new ComboBox<>();
        serviceCB.getItems().addAll("All Services", "Sound System", "Decoration", "Photography & Videography", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services");
        serviceCB.setValue("All Services");

        ComboBox<String> eventTypeCB = new ComboBox<>();
        eventTypeCB.getItems().addAll("All Event Types", "Corporate Conference", "Corporate Event", "Networking Event", "Corporate Gala");
        eventTypeCB.setValue("All Event Types");

        ComboBox<String> locationCB = new ComboBox<>();
        locationCB.getItems().addAll("All Locations", "Pune", "Mumbai", "Bangalore");
        locationCB.setValue("All Locations");

        ComboBox<String> dateCB = new ComboBox<>();
        dateCB.getItems().addAll("All Dates", "May 2026", "June 2026", "July 2026");
        dateCB.setValue("All Dates");

        Button applyBtn = createPrimaryButton("Apply Filters");
        applyBtn.setOnAction(e -> {
            showToast(stage, "Filters applied.");
            renderBookingsTable(stage);
        });

        Button clearBtn = createSecondaryButton("Clear");
        clearBtn.setOnAction(e -> {
            searchInput.clear();
            statusCB.setValue("All Status");
            serviceCB.setValue("All Services");
            renderBookingsTable(stage);
        });

        grid.add(searchInput, 0, 0);
        grid.add(statusCB, 1, 0);
        grid.add(serviceCB, 2, 0);
        grid.add(eventTypeCB, 3, 0);
        grid.add(locationCB, 4, 0);

        grid.add(dateCB, 0, 1);

        HBox btns = new HBox(8, applyBtn, clearBtn);
        grid.add(btns, 4, 1);

        card.getChildren().add(grid);
        return card;
    }

    // ==========================================
    // 6. TABS ROW
    // ==========================================
    private HBox createTabsRow(Stage stage) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        String[] tabs = {"All Bookings 32", "Upcoming 12", "In Progress 4", "Completed 16", "Cancelled 2"};

        for (String tab : tabs) {
            String tabName = tab.substring(0, tab.lastIndexOf(" "));
            Button tabBtn = new Button(tab);

            if (tabName.equals(activeTabFilter)) {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 0 0 2 0; -fx-padding: 6 12; -fx-cursor: hand;");
            } else {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-padding: 6 12; -fx-cursor: hand;");
            }

            tabBtn.setOnAction(e -> {
                activeTabFilter = tabName;
                createTabsRow(stage);
                renderBookingsTable(stage);
            });

            row.getChildren().add(tabBtn);
        }

        return row;
    }

    // ==========================================
    // 7. MAIN SPLIT SECTION (TABLE & CALENDAR WIDGETS)
    // ==========================================
    private HBox createMainSplitSection(Stage stage) {
        HBox row = new HBox(20);

        // Left Main Bookings Table Card
        VBox tableCard = createCardContainer();
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(12);
        tableHeader.setPadding(new Insets(10, 12, 10, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addTableHeaderLabel(tableHeader, "EVENT & ORGANIZER", 0, 200);
        addTableHeaderLabel(tableHeader, "DATE & TIME", 1, 140);
        addTableHeaderLabel(tableHeader, "SERVICE", 2, 120);
        addTableHeaderLabel(tableHeader, "GUESTS", 3, 60);
        addTableHeaderLabel(tableHeader, "AMOUNT", 4, 80);
        addTableHeaderLabel(tableHeader, "STATUS", 5, 100);
        addTableHeaderLabel(tableHeader, "PAYMENT", 6, 80);
        addTableHeaderLabel(tableHeader, "ACTION", 7, 90);

        tableRowsContainer = new VBox(4);
        renderBookingsTable(stage);

        tableCard.getChildren().addAll(tableHeader, tableRowsContainer);

        // Right Side Column: Calendar Widget + Status Chart + Reminders
        VBox rightSideWidgets = new VBox(16);
        rightSideWidgets.setPrefWidth(380);

        rightSideWidgets.getChildren().addAll(
                createBookingCalendarCard(),
                createBookingsByStatusCard(),
                createUpcomingRemindersCard(stage)
        );

        row.getChildren().addAll(tableCard, rightSideWidgets);
        return row;
    }

    private void addTableHeaderLabel(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void renderBookingsTable(Stage stage) {
        tableRowsContainer.getChildren().clear();

        List<BookingData> filtered = bookingList.stream().filter(b -> {
            if (activeTabFilter.equals("All Bookings")) return true;
            return b.status.equalsIgnoreCase(activeTabFilter);
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Label empty = new Label("No bookings found for selected tab: " + activeTabFilter);
            empty.setStyle("-fx-padding: 20; -fx-text-fill: " + SECONDARY_TEXT + ";");
            tableRowsContainer.getChildren().add(empty);
            return;
        }

        for (BookingData b : filtered) {
            GridPane row = new GridPane();
            row.setHgap(12);
            row.setPadding(new Insets(10, 12, 10, 12));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            // Event & Organizer Column
            VBox eventBox = new VBox(2);
            eventBox.setPrefWidth(200);
            Label eventName = new Label(b.eventName);
            eventName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label orgVenue = new Label(b.organizer + " • " + b.venue);
            orgVenue.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            eventBox.getChildren().addAll(eventName, orgVenue);

            // Date & Time
            Label dtLbl = new Label(b.dateTime); dtLbl.setPrefWidth(140);
            dtLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            // Service
            Label sLbl = new Label(b.service); sLbl.setPrefWidth(120);
            sLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            // Guests
            Label gLbl = new Label(b.guests); gLbl.setPrefWidth(60);
            gLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            // Amount
            Label aLbl = new Label(b.amount); aLbl.setPrefWidth(80);
            aLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            // Status Badge
            Label statusBadge = new Label(b.status); statusBadge.setPrefWidth(100);
            statusBadge.setStyle(getStatusBadgeStyle(b.status));

            // Payment Badge
            Label paymentBadge = new Label(b.payment); paymentBadge.setPrefWidth(80);
            paymentBadge.setStyle(getPaymentBadgeStyle(b.payment));

            // Action
            Button viewBtn = createSecondaryButton("View Details");
            viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 4 8;");
            viewBtn.setOnAction(e -> {
                showToast(stage, "Opening Booking Details for " + b.eventName);
                BookingDetailsV bookingDetailsV = new BookingDetailsV();

                Runnable callBackAction = new Runnable() {
                    public void run(){
                       backToMyBookingPage();
                    }
                };
                LandingPage.landingPageStage.setScene(bookingDetailsV.getbookingDetailsScene(null , callBackAction));
            });

            row.add(eventBox, 0, 0);
            row.add(dtLbl, 1, 0);
            row.add(sLbl, 2, 0);
            row.add(gLbl, 3, 0);
            row.add(aLbl, 4, 0);
            row.add(statusBadge, 5, 0);
            row.add(paymentBadge, 6, 0);
            row.add(viewBtn, 7, 0);

            tableRowsContainer.getChildren().add(row);
        }
    }

    private String getStatusBadgeStyle(String status) {
        switch (status) {
            case "Confirmed":
                return "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;";
            case "Pending Payment":
                return "-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;";
            case "In Progress":
                return "-fx-background-color: " + BLUE_BG + "; -fx-text-fill: " + BLUE_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;";
            case "Completed":
                return "-fx-background-color: " + GRAY_BG + "; -fx-text-fill: " + GRAY_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;";
            case "Cancelled":
                return "-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;";
            default:
                return "-fx-background-color: " + GRAY_BG + "; -fx-text-fill: " + GRAY_TEXT + "; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;";
        }
    }

    private String getPaymentBadgeStyle(String payment) {
        switch (payment) {
            case "Paid":
                return "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8px;";
            case "Partial":
            case "Pending":
                return "-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8px;";
            default:
                return "-fx-background-color: " + GRAY_BG + "; -fx-text-fill: " + GRAY_TEXT + "; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8px;";
        }
    }

    // ==========================================
    // 8. RIGHT SIDE WIDGETS (CALENDAR, CHART, REMINDERS)
    // ==========================================
    private VBox createBookingCalendarCard() {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("Booking Calendar");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label month = new Label("<   May 2026   >"); month.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        head.getChildren().addAll(title, sp, month);

        GridPane calGrid = new GridPane();
        calGrid.setHgap(8); calGrid.setVgap(6);
        calGrid.setAlignment(Pos.CENTER);

        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (int c = 0; c < days.length; c++) {
            Label d = new Label(days[c]);
            d.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
            calGrid.add(d, c, 0);
        }

        int dayVal = 1;
        for (int r = 1; r <= 4; r++) {
            for (int c = 0; c < 7; c++) {
                if (dayVal <= 28) {
                    Label dayCell = new Label(String.valueOf(dayVal));
                    dayCell.setPrefSize(24, 24);
                    dayCell.setAlignment(Pos.CENTER);

                    if (dayVal == 18) {
                        dayCell.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-background-radius: 50%; -fx-font-weight: bold;");
                    } else if (dayVal == 22) {
                        dayCell.setStyle("-fx-background-color: " + BLUE_TEXT + "; -fx-text-fill: white; -fx-background-radius: 50%; -fx-font-weight: bold;");
                    } else if (dayVal == 21) {
                        dayCell.setStyle("-fx-background-color: " + WARNING_TEXT + "; -fx-text-fill: white; -fx-background-radius: 50%; -fx-font-weight: bold;");
                    } else if (dayVal == 28) {
                        dayCell.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-background-radius: 50%; -fx-font-weight: bold;");
                    } else {
                        dayCell.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MAIN + ";");
                    }

                    calGrid.add(dayCell, c, r);
                    dayVal++;
                }
            }
        }

        HBox legend = new HBox(8);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
                createLegendDot("Booked", GREEN_TEXT),
                createLegendDot("In Progress", BLUE_TEXT),
                createLegendDot("Pending", WARNING_TEXT),
                createLegendDot("Conflict", RED_TEXT)
        );

        card.getChildren().addAll(head, new Separator(), calGrid, legend);
        return card;
    }

    private HBox createLegendDot(String text, String color) {
        HBox r = new HBox(4);
        r.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(3, Color.web(color));
        Label l = new Label(text); l.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().addAll(dot, l);
        return r;
    }

    private VBox createBookingsByStatusCard() {
        VBox card = createCardContainer();

        Label title = new Label("Bookings by Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox legend = new VBox(4);
        legend.getChildren().addAll(
                createStatusLegendRow("● Confirmed", "12 (37%)", GREEN_TEXT),
                createStatusLegendRow("● In Progress", "4 (12%)", BLUE_TEXT),
                createStatusLegendRow("● Pending", "6 (19%)", WARNING_TEXT),
                createStatusLegendRow("● Completed", "8 (25%)", GRAY_TEXT),
                createStatusLegendRow("● Cancelled", "2 (6%)", RED_TEXT)
        );

        card.getChildren().addAll(title, new Separator(), legend);
        return card;
    }

    private HBox createStatusLegendRow(String label, String val, String color) {
        HBox r = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        r.getChildren().addAll(l, sp, v);
        return r;
    }

    private VBox createUpcomingRemindersCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Upcoming Reminders");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createReminderRow("TechVision Annual Summit", "18 May 2026 • 09:00 AM", "Tomorrow", LIGHT_BLUE, PRIMARY_BLUE),
                createReminderRow("Leadership Summit 2026", "22 May 2026 • 10:00 AM", "In 4 Days", BACKGROUND, SECONDARY_TEXT),
                createReminderRow("Startup Connect 2026", "05 Jun 2026 • 09:30 AM", "In 18 Days", BACKGROUND, SECONDARY_TEXT)
        );

        Button viewRemBtn = createSecondaryButton("View All Reminders →");
        viewRemBtn.setMaxWidth(Double.MAX_VALUE);
        viewRemBtn.setOnAction(e -> showToast(stage, "Opening Reminders List..."));

        card.getChildren().addAll(title, new Separator(), list, viewRemBtn);
        return card;
    }

    private HBox createReminderRow(String event, String dt, String badgeText, String bgHex, String textHex) {
        HBox r = new HBox(8);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(6));
        r.setStyle("-fx-background-color: " + bgHex + "; -fx-background-radius: 6px;");

        VBox t = new VBox(2);
        Label eName = new Label(event); eName.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label eDt = new Label(dt); eDt.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        t.getChildren().addAll(eName, eDt);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: white; -fx-text-fill: " + textHex + "; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        r.getChildren().addAll(t, sp, badge);
        return r;
    }

    // ==========================================
    // 9. PAGINATION
    // ==========================================
    private HBox createPaginationRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 0, 0, 0));

        Label count = new Label("Showing 1 to 7 of 32 bookings");
        count.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button prev = createSecondaryButton("← Previous");
        Button page1 = createPrimaryButton("1");
        Button page2 = createSecondaryButton("2");
        Button page3 = createSecondaryButton("3");
        Button page4 = createSecondaryButton("4");
        Button page5 = createSecondaryButton("5");
        Button next = createSecondaryButton("Next →");

        HBox pBox = new HBox(4, prev, page1, page2, page3, page4, page5, next);

        row.getChildren().addAll(count, sp, pBox);
        return row;
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
        if (stage != null) {
            alert.initOwner(stage);
        }
        alert.show();
    }
    public void backToMyBookingPage(){
        LandingPage.landingPageStage.setScene(myBookingScene);
    }
}