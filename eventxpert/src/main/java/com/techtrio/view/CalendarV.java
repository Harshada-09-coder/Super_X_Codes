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


public class CalendarV extends Application {

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
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";
    private static final String GRAY_BG = "#F1F5F9";
    private static final String GRAY_TEXT = "#64748B";

    // Dynamic Calendar State
    private String currentViewMode = "Month"; // Month, Week, Day
    private String currentMonthStr = "August 2026";
    private Label monthTitleLabel;
    private VBox calendarViewContainer;

    // Right Side Selected Event Details Focus Panel Controls
    private Label selectedTitleLabel;
    private Label selectedOrgLabel;
    private Label selectedTimeLabel;
    private Label selectedVenueLabel;
    private Label selectedServiceLabel;
    private Label selectedAmountLabel;

    @Override
    public void start(Stage primaryStage) {
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
                createTwoColumnMainLayout(primaryStage),
                createFooter()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Vendor Availability Calendar");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
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
        sidebar.getChildren().add(createSidebarCategory("OVERVIEW"));
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));

        sidebar.getChildren().add(createSidebarCategory("SERVICE MANAGEMENT"));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", true, e -> {})); // Active Menu

        sidebar.getChildren().add(createSidebarCategory("FINANCE"));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Finance...")));

        sidebar.getChildren().add(createSidebarCategory("COMMUNICATION"));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "3"));

        sidebar.getChildren().add(createSidebarCategory("ACCOUNT"));
        sidebar.getChildren().add(createSidebarItem("👤 Profile & Settings", false, e -> showToast(stage, "Opening Profile...")));
        sidebar.getChildren().add(createSidebarItem("❓ Help & Support", false, e -> showToast(stage, "Opening Support...")));

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

    private Label createSidebarCategory(String title) {
        Label cat = new Label(title);
        cat.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-padding: 14 8 4 8;");
        return cat;
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
        searchField.setPromptText("🔍 Search bookings, services, dates...");
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

        Label title = new Label("Calendar");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button todayBtn = createSecondaryButton("Today");
        todayBtn.setOnAction(e -> showToast(stage, "Focusing Today's Date..."));

        Button blockBtn = createSecondaryButton("📅 Add Unavailable Date");
        blockBtn.setOnAction(e -> openBlockDateModal(stage));

        Button addBookingBtn = createPrimaryButton("+ Add Booking");
        addBookingBtn.setOnAction(e -> openAddAvailabilityModal(stage));

        titleRow.getChildren().addAll(title, spacer, todayBtn, blockBtn, addBookingBtn);

        Label subtitle = new Label("Manage your bookings, availability and schedule in one place.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW (MATCHING STITCH)
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createSummaryCard("Available", "18", "Dates", GREEN_TEXT, "✅", false),
                createSummaryCard("Booked", "9", "Dates", PRIMARY_BLUE, "📅", false),
                createSummaryCard("Pending", "4", "Requests", WARNING_TEXT, "⏳", false),
                createSummaryCard("Conflicts", "1", "Issue detected", RED_TEXT, "⚠️", true)
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String colorHex, String iconStr, boolean isConflict) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        if (isConflict) {
            card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 12px; -fx-border-width: 1.5;");
        }

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (isConflict ? RED_TEXT : SECONDARY_TEXT) + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + (isConflict ? RED_TEXT : TEXT_MAIN) + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (isConflict ? RED_TEXT : SECONDARY_TEXT) + ";");

        card.getChildren().addAll(top, v, s);
        return card;
    }

    // ==========================================
    // 5. TWO-COLUMN MAIN LAYOUT (CALENDAR & FOCUS DETAILS)
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column: Calendar Card & Toolbar (~2/3 width)
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createCalendarToolbarCard(stage),
                createCalendarContainer(stage)
        );

        // Right Column: Focus Event Details & Schedules (~1/3 width)
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createSelectedBookingFocusCard(stage),
                createWorkingHoursCard(),
                createUpcomingNextCard(),
                createSmartInsightCard()
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createCalendarToolbarCard(Stage stage) {
        VBox card = createCardContainer();

        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button prevBtn = createSecondaryButton("<");
        Button nextBtn = createSecondaryButton(">");

        monthTitleLabel = new Label(currentMonthStr);
        monthTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);

        // View Mode Toggles
        Button btnMonth = createSecondaryButton("Month");
        Button btnWeek = createSecondaryButton("Week");
        Button btnDay = createSecondaryButton("Day");

        if (currentViewMode.equals("Month")) btnMonth.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px;");

        btnMonth.setOnAction(e -> switchViewMode(stage, "Month", btnMonth, btnWeek, btnDay));
        btnWeek.setOnAction(e -> switchViewMode(stage, "Week", btnWeek, btnMonth, btnDay));
        btnDay.setOnAction(e -> switchViewMode(stage, "Day", btnDay, btnMonth, btnWeek));

        ComboBox<String> serviceFilter = new ComboBox<>();
        serviceFilter.getItems().addAll("All Services", "Sound System", "Decoration", "Photo & Video");
        serviceFilter.setValue("All Services");

        toolbar.getChildren().addAll(prevBtn, nextBtn, monthTitleLabel, sp1, btnMonth, btnWeek, btnDay, serviceFilter);

        // Legend Row
        HBox legendRow = new HBox(16);
        legendRow.setAlignment(Pos.CENTER_LEFT);
        legendRow.setPadding(new Insets(4, 0, 0, 0));

        legendRow.getChildren().addAll(
                createLegendDot("Available", "white", BORDER_COLOR),
                createLegendDot("Confirmed", PRIMARY_BLUE, PRIMARY_BLUE),
                createLegendDot("Pending", WARNING_TEXT, WARNING_TEXT),
                createLegendDot("Unavailable", GRAY_TEXT, GRAY_TEXT),
                createLegendDot("Conflict", RED_TEXT, RED_TEXT)
        );

        card.getChildren().addAll(toolbar, new Separator(), legendRow);
        return card;
    }

    private void switchViewMode(Stage stage, String mode, Button active, Button b1, Button b2) {
        currentViewMode = mode;
        active.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px;");
        b1.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px;");
        b2.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px;");
        renderCalendarGrid(stage);
    }

    private HBox createLegendDot(String text, String fillHex, String borderHex) {
        HBox r = new HBox(6);
        r.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, Color.web(fillHex));
        dot.setStroke(Color.web(borderHex));
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().addAll(dot, l);
        return r;
    }

    private VBox createCalendarContainer(Stage stage) {
        VBox card = createCardContainer();
        card.setPadding(new Insets(0));

        calendarViewContainer = new VBox();
        renderCalendarGrid(stage);

        card.getChildren().add(calendarViewContainer);
        return card;
    }

    private void renderCalendarGrid(Stage stage) {
        calendarViewContainer.getChildren().clear();

        if (currentViewMode.equals("Week")) {
            calendarViewContainer.getChildren().add(createSimulatedWeekView());
            return;
        } else if (currentViewMode.equals("Day")) {
            calendarViewContainer.getChildren().add(createSimulatedDayView());
            return;
        }

        // Default Month Grid
        GridPane monthGrid = new GridPane();
        monthGrid.setGridLinesVisible(true);
        monthGrid.setStyle("-fx-border-color: " + BORDER_COLOR + ";");

        String[] headers = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (int c = 0; c < headers.length; c++) {
            Label h = new Label(headers[c]);
            h.setPrefWidth(120);
            h.setAlignment(Pos.CENTER);
            h.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 8 0; -fx-background-color: " + BACKGROUND + ";");
            monthGrid.add(h, c, 0);
        }

        // Render Calendar Days
        int dayCounter = 1;
        for (int r = 1; r <= 5; r++) {
            for (int c = 0; c < 7; c++) {
                if (r == 1 && c < 4) { // Previous Month Cells
                    VBox prevCell = new VBox(); prevCell.setPrefSize(120, 95);
                    prevCell.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-padding: 6;");
                    monthGrid.add(prevCell, c, r);
                } else if (dayCounter <= 31) {
                    VBox dayCell = createMonthDayCell(stage, dayCounter);
                    monthGrid.add(dayCell, c, r);
                    dayCounter++;
                } else {
                    VBox nextCell = new VBox(); nextCell.setPrefSize(120, 95);
                    nextCell.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-padding: 6;");
                    monthGrid.add(nextCell, c, r);
                }
            }
        }

        calendarViewContainer.getChildren().add(monthGrid);
    }

    private VBox createMonthDayCell(Stage stage, int dayNum) {
        VBox cell = new VBox(4);
        cell.setPrefSize(120, 95);
        cell.setPadding(new Insets(6));
        cell.setStyle("-fx-background-color: white; -fx-cursor: hand;");

        Label numLbl = new Label(String.valueOf(dayNum));
        numLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        if (dayNum == 18) { // Today / Selected Confirmed Event
            cell.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 1.5;");
            numLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
            VBox eventChip = createEventChip("TechVisio...", "Sound Sys •", PRIMARY_BLUE);
            cell.getChildren().addAll(numLbl, eventChip);
        } else if (dayNum == 12) { // Conflict Event Cell
            cell.setStyle("-fx-background-color: " + RED_BG + "; -fx-border-color: " + RED_TEXT + "; -fx-border-style: dashed;");
            VBox eventChip1 = createEventChip("Annual Ga...", "Decoration", RED_TEXT);
            VBox eventChip2 = createEventChip("Corporat...", "Rental Eq •", PRIMARY_BLUE);
            cell.getChildren().addAll(numLbl, eventChip1, eventChip2);
        } else if (dayNum == 20) {
            VBox eventChip = createEventChip("Corp Lead...", "Decoration •", BRIGHT_BLUE);
            cell.getChildren().addAll(numLbl, eventChip);
        } else if (dayNum == 22) {
            VBox eventChip = createEventChip("Wedding C...", "Decoration •", WARNING_TEXT);
            cell.getChildren().addAll(numLbl, eventChip);
        } else if (dayNum == 27) { // Unavailable / Blocked Cell
            cell.setStyle("-fx-background-color: " + GRAY_BG + ";");
            Label unAvail = new Label("Annual Ga...\nUnavailable");
            unAvail.setStyle("-fx-font-size: 9px; -fx-text-fill: " + GRAY_TEXT + ";");
            cell.getChildren().addAll(numLbl, unAvail);
        } else {
            cell.getChildren().add(numLbl);
        }

        cell.setOnMouseClicked(e -> updateSelectedFocusEvent(dayNum));
        return cell;
    }

    private VBox createEventChip(String title, String sub, String colorHex) {
        VBox b = new VBox(1);
        b.setPadding(new Insets(2, 4, 2, 4));
        b.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 4px; -fx-border-color: " + colorHex + "; -fx-border-radius: 4px;");

        Label t = new Label(title); t.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 8px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        b.getChildren().addAll(t, s);
        return b;
    }

    private VBox createSimulatedWeekView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));
        Label l = new Label("📅 Week View Schedule (August 17 - August 23, 2026)");
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + SIDEBAR_NAVY + ";");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("18 Aug (Tue): TechVision Annual Summit (09:00 AM - 06:00 PM)"), 0, 0);
        grid.add(new Label("20 Aug (Thu): Corp Leadership Meet (10:00 AM - 05:00 PM)"), 0, 1);
        grid.add(new Label("22 Aug (Sat): Wedding Celebration (06:00 PM - 10:00 PM)"), 0, 2);
        box.getChildren().addAll(l, new Separator(), grid);
        return box;
    }

    private VBox createSimulatedDayView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));
        Label l = new Label("📅 Day View Timeline - 18 August 2026");
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        VBox timeline = new VBox(8);
        timeline.getChildren().addAll(
                new Label("08:00 AM - Setup Arrival & Equipment Check"),
                new Label("09:00 AM - TechVision Annual Summit Begins (Sound System)"),
                new Label("01:00 PM - Afternoon Break & Audio Monitoring"),
                new Label("06:00 PM - Session Wrap-Up & Gear Tear-Down")
        );
        box.getChildren().addAll(l, new Separator(), timeline);
        return box;
    }

    // ==========================================
    // RIGHT COLUMN FOCUS & WIDGET CARDS
    // ==========================================
    private VBox createSelectedBookingFocusCard(Stage stage) {
        VBox card = createCardContainer();

        HBox topHead = new HBox();
        Label st = new Label("● Confirmed"); st.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label close = new Label("✕"); close.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");
        topHead.getChildren().addAll(st, sp, close);

        selectedTitleLabel = new Label("TechVision Annual Summit");
        selectedTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        selectedOrgLabel = new Label("Organizer: TechVision Events");
        selectedOrgLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        selectedTimeLabel = new Label("📅 18 May 2026\n⏰ 09:00 AM - 06:00 PM (9h)");
        selectedTimeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        selectedVenueLabel = new Label("📍 Grand Convention Center\nHall A, North Wing");
        selectedVenueLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        selectedServiceLabel = new Label("🔊 Sound System\nPackage: Premium Sound Package");
        selectedServiceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox amtBox = new VBox(2);
        amtBox.setPadding(new Insets(8));
        amtBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");
        Label aHead = new Label("Estimated Amount"); aHead.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        selectedAmountLabel = new Label("₹85,000"); selectedAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        amtBox.getChildren().addAll(aHead, selectedAmountLabel);

        Button viewBookingBtn = createPrimaryButton("View Booking Details");
        viewBookingBtn.setMaxWidth(Double.MAX_VALUE);
        viewBookingBtn.setOnAction(e -> showToast(stage, "Opening Booking Details for TechVision Summit..."));

        Button contactBtn = createSecondaryButton("✉ Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setOnAction(e -> showToast(stage, "Contacting TechVision Events..."));

        card.getChildren().addAll(topHead, selectedTitleLabel, selectedOrgLabel, new Separator(), selectedTimeLabel, selectedVenueLabel, selectedServiceLabel, amtBox, viewBookingBtn, contactBtn);
        return card;
    }

    private void updateSelectedFocusEvent(int dayNum) {
        if (dayNum == 20) {
            selectedTitleLabel.setText("Corp Leadership Meet");
            selectedOrgLabel.setText("Organizer: Corporate Connect");
            selectedTimeLabel.setText("📅 20 August 2026\n⏰ 10:00 AM - 05:00 PM");
            selectedServiceLabel.setText("🎨 Decoration\nPackage: Premium Stage Setup");
            selectedAmountLabel.setText("₹45,000");
        } else {
            selectedTitleLabel.setText("TechVision Annual Summit");
            selectedOrgLabel.setText("Organizer: TechVision Events");
            selectedTimeLabel.setText("📅 18 August 2026\n⏰ 09:00 AM - 06:00 PM (9h)");
            selectedServiceLabel.setText("🔊 Sound System\nPackage: Premium Sound Package");
            selectedAmountLabel.setText("₹85,000");
        }
    }

    private VBox createWorkingHoursCard() {
        VBox card = createCardContainer();

        Label title = new Label("⏰ Working Hours");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        VBox grid = new VBox(4);
        grid.getChildren().addAll(
                createHourRow("Mon - Fri", "08:00 AM - 08:00 PM"),
                createHourRow("Saturday", "09:00 AM - 05:00 PM"),
                createHourRow("Sunday", "Unavailable")
        );

        Label editLink = new Label("Edit Availability");
        editLink.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");

        card.getChildren().addAll(title, new Separator(), grid, editLink);
        return card;
    }

    private HBox createHourRow(String day, String hours) {
        HBox r = new HBox();
        Label d = new Label(day); d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label h = new Label(hours);
        if (hours.equals("Unavailable")) {
            h.setStyle("-fx-font-size: 10px; -fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 1 6; -fx-background-radius: 4px;");
        } else {
            h.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        }
        r.getChildren().addAll(d, sp, h);
        return r;
    }

    private VBox createUpcomingNextCard() {
        VBox card = createCardContainer();

        Label title = new Label("Upcoming Next");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createUpcomingRow("MAY 18", "TechVision Annual...", "Sound System"),
                createUpcomingRow("MAY 22", "Corp Leadership Meet", "Decoration"),
                createUpcomingRow("JUN 02", "Startup Connect 2026", "Photo & Video")
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private HBox createUpcomingRow(String dateBadge, String title, String service) {
        HBox r = new HBox(10);
        r.setAlignment(Pos.CENTER_LEFT);

        Label b = new Label(dateBadge);
        b.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 9px; -fx-padding: 4; -fx-background-radius: 4px; -fx-alignment: center;");

        VBox text = new VBox(1);
        Label t = new Label(title); t.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label s = new Label(service); s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        text.getChildren().addAll(t, s);

        r.getChildren().addAll(b, text);
        return r;
    }

    private VBox createSmartInsightCard() {
        VBox card = createCardContainer();
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");

        HBox head = new HBox(6);
        Label icon = new Label("💡"); icon.setStyle("-fx-font-size: 16px;");
        Label title = new Label("Smart Insight"); title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        head.getChildren().addAll(icon, title);

        Label msg = new Label("Your Sound System service has the highest number of upcoming bookings this quarter.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        card.getChildren().addAll(head, msg);
        return card;
    }

    // ==========================================
    // 8. FOOTER
    // ==========================================
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 0, 10, 0));

        Label copy = new Label("© 2026 EventXpert Management System. All rights reserved.  •  Privacy Policy  •  Terms of Service");
        copy.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        footer.getChildren().add(copy);
        return footer;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openBlockDateModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Block Unavailable Date");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("📅 Block Unavailable Date");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        DatePicker datePicker = new DatePicker();
        TextField reasonInput = new TextField(); reasonInput.setPromptText("Reason (e.g. Equipment Maintenance)");

        ComboBox<String> durCB = new ComboBox<>();
        durCB.getItems().addAll("Full Day", "Morning Only", "Afternoon Only", "Evening Only");
        durCB.setValue("Full Day");

        Button blockBtn = createPrimaryButton("Block Date");
        blockBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Date blocked successfully!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Select Date:"), datePicker, new Label("Duration:"), durCB, new Label("Reason:"), reasonInput, blockBtn);

        Scene scene = new Scene(layout, 360, 320);
        modal.setScene(scene);
        modal.show();
    }

    private void openAddAvailabilityModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add Booking");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("➕ Add Manual Booking / Availability");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        TextField nameInput = new TextField(); nameInput.setPromptText("Event Name");
        DatePicker datePicker = new DatePicker();

        ComboBox<String> serviceCB = new ComboBox<>();
        serviceCB.getItems().addAll("Sound System", "Decoration", "Photography & Videography", "Catering");
        serviceCB.setValue("Sound System");

        Button saveBtn = createPrimaryButton("Save Availability");
        saveBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Booking availability saved!");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Event Name:"), nameInput, new Label("Date:"), datePicker, new Label("Service:"), serviceCB, saveBtn);

        Scene scene = new Scene(layout, 360, 320);
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