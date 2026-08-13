package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * EventXpert - Organizer Dashboard
 * <p>
 * Main landing screen of the Organizer Portal. Gives the organizer an
 * at-a-glance overview of events, requests, budget, vendors, schedule,
 * weather and recent activity.
 * <p>
 * Layout: BorderPane
 *   LEFT   -> Sidebar (fixed navigation)
 *   TOP    -> TopBar (search + notifications + profile)
 *   CENTER -> ScrollPane -> dashboardContent (VBox)
 */
public class OrganizerDashboard extends Application {

    // ======================================================
    // COLOR PALETTE (EventXpert)
    // ======================================================
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String MAIN_TEXT = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER = "#DCE5F0";
    private static final String GREEN = "#1E9E5A";
    private static final String ORANGE = "#E9A23B";
    private static final String RED = "#D9342B";

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        root.setLeft(buildSidebar());
        root.setTop(buildTopBar());
        root.setCenter(buildCenterScrollPane());

        Scene scene = new Scene(root, 1440, 900);
        java.net.URL cssUrl = getClass().getResource("/OrganizerDashboard.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("EventXpert - Organizer Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.show();
    }

    // ======================================================
    // SIDEBAR
    // ======================================================
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);

        // ---- Brand ----
        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(22, 20, 18, 20));

        SVGPath brandIcon = createIcon(IconPaths.CALENDAR, PRIMARY_BLUE, 24);
        VBox brandText = new VBox(1);
        Label brandTitle = new Label("EventXpert");
        brandTitle.getStyleClass().add("sidebar-brand-title");
        Label brandSubtitle = new Label("Corporate Event Management");
        brandSubtitle.getStyleClass().add("sidebar-brand-subtitle");
        brandText.getChildren().addAll(brandTitle, brandSubtitle);
        brandBox.getChildren().addAll(brandIcon, brandText);

        // ---- Navigation (scrollable middle section) ----
        VBox navBox = new VBox(4);
        navBox.setPadding(new Insets(6, 14, 6, 14));

        navBox.getChildren().add(sectionLabel("EVENT MANAGEMENT"));
        navBox.getChildren().add(sidebarItemActive("Dashboard", IconPaths.GRID));
        navBox.getChildren().add(sidebarItem("Event Requests", IconPaths.INBOX, () -> navigateTo("EventRequests")));
        navBox.getChildren().add(sidebarItem("My Events", IconPaths.CALENDAR, () -> navigateTo("MyEvents")));

        navBox.getChildren().add(spacer(10));
        navBox.getChildren().add(sectionLabel("EVENT PLANNING"));
        navBox.getChildren().add(sidebarItem("AI Planner", IconPaths.SPARKLE, () -> navigateTo("AIEventPlanner")));
        navBox.getChildren().add(sidebarItem("Budget", IconPaths.WALLET, () -> navigateTo("BudgetExpenseTracker")));
        navBox.getChildren().add(sidebarItem("Venue", IconPaths.PIN, () -> navigateTo("VenueManagement")));
        navBox.getChildren().add(sidebarItem("Vendors", IconPaths.STORE, () -> navigateTo("VendorManagement")));
        navBox.getChildren().add(sidebarItem("Schedule", IconPaths.CLOCK, () -> navigateTo("Schedule")));

        navBox.getChildren().add(spacer(10));
        navBox.getChildren().add(sectionLabel("EVENT EXECUTION"));
        navBox.getChildren().add(sidebarItem("Guest Management", IconPaths.USERS, () -> navigateTo("GuestManagement")));
        navBox.getChildren().add(sidebarItem("QR Check-In", IconPaths.QR, () -> navigateTo("QRCheckIn")));
        navBox.getChildren().add(sidebarItem("Live Event", IconPaths.PULSE, () -> navigateTo("LiveEventDashboard")));

        ScrollPane navScroll = new ScrollPane(navBox);
        navScroll.setFitToWidth(true);
        navScroll.getStyleClass().add("scroll-pane-transparent");
        navScroll.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        VBox.setVgrow(navScroll, Priority.ALWAYS);

        // ---- Bottom section ----
        VBox bottomBox = new VBox(4);
        bottomBox.setPadding(new Insets(10, 14, 14, 14));

        Separator sep = new Separator();
        sep.getStyleClass().add("sidebar-separator");

        bottomBox.getChildren().add(sep);
        bottomBox.getChildren().add(spacer(6));
        bottomBox.getChildren().add(sidebarItem("Notifications", IconPaths.BELL, () -> navigateTo("Notifications")));
        bottomBox.getChildren().add(sidebarItem("Settings", IconPaths.GEAR, () -> navigateTo("Settings")));
        bottomBox.getChildren().add(sidebarItem("Help Center", IconPaths.HELP, () -> navigateTo("Support")));
        bottomBox.getChildren().add(spacer(8));
        bottomBox.getChildren().add(buildProfileBox());

        sidebar.getChildren().addAll(brandBox, navScroll, bottomBox);
        return sidebar;
    }

    private HBox buildProfileBox() {
        HBox box = new HBox(10);
        box.getStyleClass().add("sidebar-profile-box");
        box.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(16);
        avatar.getStyleClass().add("sidebar-avatar");

        VBox textBox = new VBox(1);
        Label name = new Label("Admin User");
        name.getStyleClass().add("sidebar-profile-name");
        Label email = new Label("admin@eventxpert.com");
        email.getStyleClass().add("sidebar-profile-email");
        textBox.getChildren().addAll(name, email);

        box.getChildren().addAll(avatar, textBox);
        return box;
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("sidebar-section-label");
        label.setPadding(new Insets(10, 4, 6, 4));
        return label;
    }

    /** Currently-active sidebar item ("Dashboard") with left indicator bar. */
    private HBox sidebarItemActive(String text, String iconPath) {
        HBox row = new HBox(10);
        row.getStyleClass().add("sidebar-item-active");
        row.setAlignment(Pos.CENTER_LEFT);

        Region indicator = new Region();
        indicator.getStyleClass().add("sidebar-active-indicator");
        indicator.setPrefWidth(3);
        indicator.setPrefHeight(18);

        SVGPath icon = createIcon(iconPath, PRIMARY_BLUE, 16);
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        row.getChildren().addAll(indicator, icon, label);
        return row;
    }

    private HBox sidebarItem(String text, String iconPath, Runnable onClick) {
        HBox row = new HBox(10);
        row.getStyleClass().add("sidebar-item");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 12, 9, 15));

        SVGPath icon = createIcon(iconPath, SECONDARY_TEXT, 16);
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + MAIN_TEXT + "; -fx-font-size: 13px;");

        row.getChildren().addAll(icon, label);
        row.setOnMouseClicked(e -> onClick.run());
        return row;
    }

    private Region spacer(double height) {
        Region r = new Region();
        r.setPrefHeight(height);
        return r;
    }

    // ======================================================
    // TOP BAR
    // ======================================================
    private HBox buildTopBar() {
        HBox topBar = new HBox(16);
        topBar.getStyleClass().add("topbar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Search field
        HBox searchBox = new HBox(8);
        searchBox.getStyleClass().add("search-field");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        SVGPath searchIcon = createIcon(IconPaths.SEARCH, SECONDARY_TEXT, 15);
        TextField searchField = new TextField();
        searchField.setPromptText("Search events, guests, vendors...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 13px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        // Notification bell with badge
        StackPane bellBtn = iconButtonWithBadge(IconPaths.BELL, () -> navigateTo("Notifications"));

        // Help icon
        StackPane helpBtn = new StackPane(createIcon(IconPaths.HELP, MAIN_TEXT, 17));
        helpBtn.getStyleClass().add("topbar-icon-btn");
        helpBtn.setOnMouseClicked(e -> navigateTo("Support"));

        // Profile
        HBox profileBox = new HBox(8);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(16);
        avatar.getStyleClass().add("sidebar-avatar");
        Label profileName = new Label("Admin User");
        profileName.getStyleClass().add("topbar-user-name");
        profileBox.getChildren().addAll(avatar, profileName);
        profileBox.setOnMouseClicked(e -> navigateTo("Profile"));
        profileBox.setStyle("-fx-cursor: hand;");

        topBar.getChildren().addAll(searchBox, gap, bellBtn, helpBtn, profileBox);
        return topBar;
    }

    private StackPane iconButtonWithBadge(String iconPath, Runnable onClick) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("topbar-icon-btn");
        SVGPath icon = createIcon(iconPath, MAIN_TEXT, 17);
        Circle badge = new Circle(4);
        badge.getStyleClass().add("badge-dot");
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        badge.setTranslateX(-4);
        badge.setTranslateY(4);
        pane.getChildren().addAll(icon, badge);
        pane.setOnMouseClicked(e -> onClick.run());
        return pane;
    }

    // ======================================================
    // CENTER CONTENT
    // ======================================================
    private ScrollPane buildCenterScrollPane() {
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(24));
        dashboardContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        dashboardContent.getChildren().add(buildHeaderRow());
        dashboardContent.getChildren().add(buildSummaryCardsRow());

        // Two-column body: left (main) + right (side panels)
        HBox bodyRow = new HBox(20);

        VBox leftColumn = new VBox(20);
        leftColumn.getChildren().add(buildUpcomingEventsCard());
        leftColumn.getChildren().add(buildEventRequestsCard());
        leftColumn.getChildren().add(buildEventPerformanceCard());
        leftColumn.getChildren().add(buildRecentActivityCard());
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(340);
        rightColumn.setMinWidth(300);
        rightColumn.getChildren().add(buildBudgetOverviewCard());
        rightColumn.getChildren().add(buildVendorStatusCard());
        rightColumn.getChildren().add(buildScheduleStatusCard());
        rightColumn.getChildren().add(buildWeatherAlertCard());
        rightColumn.getChildren().add(buildQuickActionsCard());

        bodyRow.getChildren().addAll(leftColumn, rightColumn);
        dashboardContent.getChildren().add(bodyRow);

        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane-transparent");
        scrollPane.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-width: 0;");
        return scrollPane;
    }

    // ---------------- HEADER ----------------
    private HBox buildHeaderRow() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Good Morning, Admin \uD83D\uDC4B");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Here's what's happening with your events today.");
        subtitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        VBox rightBox = new VBox(8);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        Label date = new Label("13 August 2026");
        date.getStyleClass().add("header-date");

        Button createEventBtn = new Button("+  Create Event");
        createEventBtn.getStyleClass().add("primary-button");
        createEventBtn.setOnAction(e -> navigateTo("CreateEvent"));

        rightBox.getChildren().addAll(date, createEventBtn);

        header.getChildren().addAll(titleBox, gap, rightBox);
        return header;
    }

    // ---------------- SUMMARY CARDS ----------------
    private HBox buildSummaryCardsRow() {
        HBox row = new HBox(16);

        VBox c1 = summaryCard(IconPaths.CALENDAR, "TOTAL EVENTS", "24", "+12% from last month", true);
        VBox c2 = summaryCard(IconPaths.INBOX, "PENDING REQUESTS", "06", "Requires attention", false);
        VBox c3 = summaryCard(IconPaths.CALENDAR_CHECK, "UPCOMING EVENTS", "08", "Next 30 days", true);
        VBox c4 = summaryCard(IconPaths.CHECK_CIRCLE, "ACTIVE EVENTS", "03", "Currently running", true);

        for (VBox c : List.of(c1, c2, c3, c4)) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox summaryCard(String iconPath, String label, String value, String trendText, boolean positive) {
        VBox card = new VBox(10);
        card.getStyleClass().add("summary-card");

        StackPane iconBox = new StackPane(createIcon(iconPath, PRIMARY_BLUE, 18));
        iconBox.getStyleClass().add("summary-icon-box");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("summary-value");

        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("summary-label");

        Label trendLabel = new Label(trendText);
        trendLabel.getStyleClass().add(positive ? "summary-trend-positive" : "summary-trend-warning");

        card.getChildren().addAll(iconBox, valueLabel, nameLabel, trendLabel);
        return card;
    }

    // ---------------- UPCOMING EVENTS ----------------
    private VBox buildUpcomingEventsCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox headerRow = cardHeaderWithLink("Upcoming Events", "View All \u2192", () -> navigateTo("MyEvents"));

        // Column headers
        HBox colHeaders = new HBox();
        colHeaders.setPadding(new Insets(6, 0, 6, 0));
        Label hName = tableHeader("EVENT NAME", 220);
        Label hDate = tableHeader("DATE & VENUE", 190);
        Label hGuests = tableHeader("GUESTS", 90);
        Label hStatus = tableHeader("STATUS", 110);
        Label hAction = tableHeader("ACTION", 80);
        colHeaders.getChildren().addAll(hName, hDate, hGuests, hStatus, hAction);

        VBox rowsBox = new VBox();

        List<EventItem> events = MockData.upcomingEvents();
        for (int i = 0; i < events.size(); i++) {
            rowsBox.getChildren().add(buildEventRow(events.get(i)));
            if (i < events.size() - 1) {
                Region sep = new Region();
                sep.getStyleClass().add("row-separator");
                sep.setPrefHeight(1);
                rowsBox.getChildren().add(sep);
            }
        }

        card.getChildren().addAll(headerRow, colHeaders, rowsBox);
        return card;
    }

    private Label tableHeader(String text, double width) {
        Label l = new Label(text);
        l.getStyleClass().add("table-header-label");
        l.setPrefWidth(width);
        return l;
    }

    private HBox buildEventRow(EventItem event) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));

        VBox nameBox = new VBox(2);
        nameBox.setPrefWidth(220);
        Label name = new Label(event.name);
        name.getStyleClass().add("event-name");
        nameBox.getChildren().add(name);

        VBox dateBox = new VBox(2);
        dateBox.setPrefWidth(190);
        Label date = new Label(event.date);
        date.getStyleClass().add("event-meta");
        Label venue = new Label(event.venue);
        venue.getStyleClass().add("event-meta");
        dateBox.getChildren().addAll(date, venue);

        Label guests = new Label(event.guests + " Guests");
        guests.getStyleClass().add("event-meta");
        guests.setPrefWidth(90);

        Label status = statusBadge(event.status);
        HBox statusBox = new HBox(status);
        statusBox.setPrefWidth(110);

        Button viewBtn = new Button("View");
        viewBtn.getStyleClass().add("secondary-button");
        viewBtn.setStyle(viewBtn.getStyle() + "-fx-padding: 5 14 5 14; -fx-font-size: 12px;");
        viewBtn.setOnAction(e -> navigateTo("EventDetails"));
        HBox actionBox = new HBox(viewBtn);
        actionBox.setPrefWidth(80);

        row.getChildren().addAll(nameBox, dateBox, guests, statusBox, actionBox);
        return row;
    }

    private Label statusBadge(String status) {
        Label badge = new Label(status);
        badge.getStyleClass().add("status-badge");
        switch (status) {
            case "Confirmed":
                badge.getStyleClass().add("status-confirmed");
                break;
            case "Pending":
                badge.getStyleClass().add("status-pending");
                break;
            case "Active":
                badge.getStyleClass().add("status-active");
                break;
            case "Cancelled":
                badge.getStyleClass().add("status-cancelled");
                break;
            default:
                badge.getStyleClass().add("status-planning");
        }
        return badge;
    }

    // ---------------- EVENT REQUESTS ----------------
    private VBox buildEventRequestsCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox headerRow = cardHeaderWithLink("Event Requests", "View All Requests \u2192",
                () -> navigateTo("EventRequests"));

        VBox rowsBox = new VBox();
        List<RequestItem> requests = MockData.eventRequests();
        for (int i = 0; i < requests.size(); i++) {
            rowsBox.getChildren().add(buildRequestRow(requests.get(i)));
            if (i < requests.size() - 1) {
                Region sep = new Region();
                sep.getStyleClass().add("row-separator");
                sep.setPrefHeight(1);
                rowsBox.getChildren().add(sep);
            }
        }

        card.getChildren().addAll(headerRow, rowsBox);
        return card;
    }

    private HBox buildRequestRow(RequestItem request) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));

        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label name = new Label(request.name);
        name.getStyleClass().add("event-name");
        Label meta = new Label(request.type + "  \u2022  " + request.guests + " Guests");
        meta.getStyleClass().add("event-meta");
        infoBox.getChildren().addAll(name, meta);

        Label status = statusBadge(request.status);

        row.getChildren().addAll(infoBox, status);
        return row;
    }

    // ---------------- EVENT PERFORMANCE ----------------
    private VBox buildEventPerformanceCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("Event Overview");
        title.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.setPadding(new Insets(6, 0, 0, 0));

        grid.add(performanceStat("Events Completed", "18", GREEN), 0, 0);
        grid.add(performanceStat("Upcoming", "08", PRIMARY_BLUE), 1, 0);
        grid.add(performanceStat("Active", "03", BRIGHT_BLUE), 2, 0);
        grid.add(performanceStat("Cancelled", "01", RED), 3, 0);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        card.getChildren().addAll(title, grid);
        return card;
    }

    private VBox performanceStat(String label, String value, String colorHex) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER_LEFT);

        Region bar = new Region();
        bar.setPrefHeight(6);
        bar.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 6;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + MAIN_TEXT + ";");

        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("event-meta");

        box.getChildren().addAll(bar, valueLabel, nameLabel);
        return box;
    }

    // ---------------- RECENT ACTIVITY ----------------
    private VBox buildRecentActivityCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("Recent Activity");
        title.getStyleClass().add("section-title");

        VBox list = new VBox(14);
        for (ActivityItem item : MockData.recentActivity()) {
            list.getChildren().add(buildActivityRow(item));
        }

        card.getChildren().addAll(title, list);
        return card;
    }

    private HBox buildActivityRow(ActivityItem item) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);

        StackPane iconBox = new StackPane(createIcon(item.icon, PRIMARY_BLUE, 14));
        iconBox.getStyleClass().add("summary-icon-box");
        iconBox.setMinSize(30, 30);
        iconBox.setMaxSize(30, 30);

        VBox textBox = new VBox(2);
        Label time = new Label(item.time);
        time.getStyleClass().add("activity-time");
        Label text = new Label(item.text);
        text.getStyleClass().add("activity-text");
        text.setWrapText(true);
        textBox.getChildren().addAll(time, text);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        row.getChildren().addAll(iconBox, textBox);
        return row;
    }

    // ---------------- BUDGET OVERVIEW ----------------
    private VBox buildBudgetOverviewCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox headerRow = cardHeaderWithLink("Budget Overview", "View Budget \u2192",
                () -> navigateTo("BudgetExpenseTracker"));

        VBox statsBox = new VBox(8);
        statsBox.getChildren().add(budgetLine("Total Planned", "\u20B912,50,000"));
        statsBox.getChildren().add(budgetLine("Spent", "\u20B98,40,000"));
        statsBox.getChildren().add(budgetLine("Remaining", "\u20B94,10,000"));

        // Progress bar (spent / planned = 67.2%)
        StackPane track = new StackPane();
        track.getStyleClass().add("progress-bar-track");
        track.setPrefHeight(10);
        Region fill = new Region();
        fill.getStyleClass().add("progress-bar-fill-blue");
        fill.setPrefHeight(10);
        fill.setMaxWidth(Region.USE_PREF_SIZE);
        fill.prefWidthProperty().bind(track.widthProperty().multiply(0.672));
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        track.getChildren().add(fill);

        Label statusLabel = new Label("\u25CF  Within Budget");
        statusLabel.setStyle("-fx-text-fill: " + GREEN + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        card.getChildren().addAll(headerRow, statsBox, track, statusLabel);
        return card;
    }

    private HBox budgetLine(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.getStyleClass().add("event-meta");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + MAIN_TEXT + ";");
        row.getChildren().addAll(l, gap, v);
        return row;
    }

    // ---------------- VENDOR STATUS ----------------
    private VBox buildVendorStatusCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox headerRow = cardHeaderWithLink("Vendor Status", "Manage Vendors \u2192",
                () -> navigateTo("VendorManagement"));

        VBox list = new VBox(12);
        for (VendorItem vendor : MockData.vendors()) {
            list.getChildren().add(buildVendorRow(vendor));
        }

        card.getChildren().addAll(headerRow, list);
        return card;
    }

    private HBox buildVendorRow(VendorItem vendor) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(vendor.name);
        name.getStyleClass().add("vendor-name");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label status = vendor.confirmed
                ? new Label("\u2713 Confirmed")
                : new Label("Pending");
        status.getStyleClass().add("status-badge");
        status.getStyleClass().add(vendor.confirmed ? "status-confirmed" : "status-pending");

        row.getChildren().addAll(name, gap, status);
        return row;
    }

    // ---------------- SCHEDULE STATUS ----------------
    private VBox buildScheduleStatusCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("Schedule Status");
        title.getStyleClass().add("section-title");

        Label todayLabel = new Label("Today's Schedule");
        todayLabel.getStyleClass().add("event-meta");
        Label activityCount = new Label("08 Activities");
        activityCount.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + MAIN_TEXT + ";");

        HBox statsRow = new HBox(24);
        statsRow.getChildren().add(smallStat("Completed", "05", GREEN));
        statsRow.getChildren().add(smallStat("Upcoming", "03", PRIMARY_BLUE));

        HBox conflictBox = new HBox(8);
        conflictBox.setAlignment(Pos.CENTER_LEFT);
        conflictBox.setPadding(new Insets(10));
        conflictBox.setStyle("-fx-background-color: #FFF4E5; -fx-background-radius: 8;");
        SVGPath warnIcon = createIcon(IconPaths.WARNING, ORANGE, 15);
        Label conflictText = new Label("One schedule conflict detected.");
        conflictText.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A5A00; -fx-wrap-text: true;");
        conflictBox.getChildren().addAll(warnIcon, conflictText);

        Button resolveBtn = new Button("Resolve Conflict \u2192");
        resolveBtn.getStyleClass().add("link-button");
        resolveBtn.setOnAction(e -> navigateTo("Schedule"));

        card.getChildren().addAll(title, todayLabel, activityCount, statsRow, conflictBox, resolveBtn);
        return card;
    }

    private VBox smallStat(String label, String value, String colorHex) {
        VBox box = new VBox(2);
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        Label l = new Label(label);
        l.getStyleClass().add("event-meta");
        box.getChildren().addAll(v, l);
        return box;
    }

    // ---------------- WEATHER ALERT ----------------
    private VBox buildWeatherAlertCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("weather-card");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Weather Alert \u2022 Pune");
        title.getStyleClass().add("weather-title");
        topRow.getChildren().add(title);

        HBox tempRow = new HBox(14);
        tempRow.setAlignment(Pos.CENTER_LEFT);
        Label temp = new Label("28\u00B0C");
        temp.getStyleClass().add("weather-temp");
        VBox descBox = new VBox(2);
        Label desc = new Label("Partly Cloudy");
        desc.getStyleClass().add("weather-desc");
        Label rain = new Label("Rain Probability: 40%");
        rain.getStyleClass().add("weather-desc");
        descBox.getChildren().addAll(desc, rain);
        tempRow.getChildren().addAll(temp, descBox);

        Label message = new Label("Keep a covered entry route available for the evening session.");
        message.getStyleClass().add("weather-message");
        message.setMaxWidth(280);

        Button viewWeatherBtn = new Button("View Weather \u2192");
        viewWeatherBtn.getStyleClass().add("weather-button");
        viewWeatherBtn.setOnAction(e -> navigateTo("VenueManagement"));

        card.getChildren().addAll(topRow, tempRow, message, viewWeatherBtn);
        return card;
    }

    // ---------------- QUICK ACTIONS ----------------
    private VBox buildQuickActionsCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("quick-actions-card");

        Label title = new Label("\u26A1 Quick Actions");
        title.getStyleClass().add("quick-actions-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(quickActionButton("Create Event", () -> navigateTo("CreateEvent")), 0, 0);
        grid.add(quickActionButton("AI Plan Event", () -> navigateTo("AIEventPlanner")), 1, 0);
        grid.add(quickActionButton("Add Vendor", () -> navigateTo("VendorManagement")), 0, 1);
        grid.add(quickActionButton("Manage Guests", () -> navigateTo("GuestManagement")), 1, 1);
        grid.add(quickActionButton("Scan QR", () -> navigateTo("QRCheckIn")), 0, 2);
        grid.add(quickActionButton("View Schedule", () -> navigateTo("Schedule")), 1, 2);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(50);
            grid.getColumnConstraints().add(col);
        }

        card.getChildren().addAll(title, grid);
        return card;
    }

    private Button quickActionButton(String text, Runnable onClick) {
        Button btn = new Button(text);
        btn.getStyleClass().add("quick-action-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setWrapText(true);
        GridPane.setHgrow(btn, Priority.ALWAYS);
        btn.setOnAction(e -> onClick.run());
        return btn;
    }

    // ======================================================
    // SHARED HELPERS
    // ======================================================
    private HBox cardHeaderWithLink(String titleText, String linkText, Runnable onClick) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(titleText);
        title.getStyleClass().add("section-title");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Button link = new Button(linkText);
        link.getStyleClass().add("link-button");
        link.setOnAction(e -> onClick.run());
        row.getChildren().addAll(title, gap, link);
        return row;
    }

    /** Creates a colored SVG icon of the given size from path data. */
    private SVGPath createIcon(String pathData, String colorHex, double size) {
        SVGPath icon = new SVGPath();
        icon.setContent(pathData);
        icon.setFill(Color.web(colorHex));
        // Icons are authored on a 24x24 grid; scale to requested size.
        double scale = size / 24.0;
        icon.setScaleX(scale);
        icon.setScaleY(scale);
        return icon;
    }

    // ======================================================
    // NAVIGATION
    // ======================================================
    /**
     * Central navigation handler. Each sidebar / card action calls this with
     * a logical screen name. Wire this up to your real view classes as they
     * become available (EventRequests.java, MyEvents.java, etc.). Currently
     * this is a safe placeholder that will not crash the application if a
     * target screen has not been implemented yet.
     */
    private void navigateTo(String screenName) {
        System.out.println("Navigate -> " + screenName);
        // TODO: Replace with real navigation once the target view class exists, e.g.:
        //
        // switch (screenName) {
        //     case "EventRequests" -> new EventRequests().start(primaryStage);
        //     case "MyEvents"      -> new MyEvents().start(primaryStage);
        //     case "AIEventPlanner"-> new AIEventPlanner().start(primaryStage);
        //     case "BudgetExpenseTracker" -> new BudgetExpenseTracker().start(primaryStage);
        //     case "VenueManagement"      -> new VenueManagement().start(primaryStage);
        //     case "VendorManagement"     -> new VendorManagement().start(primaryStage);
        //     case "Schedule"             -> new Schedule().start(primaryStage);
        //     case "GuestManagement"      -> new GuestManagement().start(primaryStage);
        //     case "QRCheckIn"            -> new QRCheckIn().start(primaryStage);
        //     case "LiveEventDashboard"   -> new LiveEventDashboard().start(primaryStage);
        //     case "Notifications"        -> new Notifications().start(primaryStage);
        //     case "Settings"             -> new Settings().start(primaryStage);
        //     case "Support"              -> new Support().start(primaryStage);
        //     case "Profile"              -> new Profile().start(primaryStage);
        //     case "EventDetails"         -> new EventDetails().start(primaryStage);
        //     default -> System.out.println("No screen wired up for: " + screenName);
        // }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ======================================================
    // MOCK DATA MODELS
    // ======================================================
    private static class EventItem {
        final String name, date, venue, status;
        final int guests;

        EventItem(String name, String date, String venue, int guests, String status) {
            this.name = name;
            this.date = date;
            this.venue = venue;
            this.guests = guests;
            this.status = status;
        }
    }

    private static class RequestItem {
        final String name, type, status;
        final int guests;

        RequestItem(String name, String type, int guests, String status) {
            this.name = name;
            this.type = type;
            this.guests = guests;
            this.status = status;
        }
    }

    private static class VendorItem {
        final String name;
        final boolean confirmed;

        VendorItem(String name, boolean confirmed) {
            this.name = name;
            this.confirmed = confirmed;
        }
    }

    private static class ActivityItem {
        final String time, text, icon;

        ActivityItem(String time, String text, String icon) {
            this.time = time;
            this.text = text;
            this.icon = icon;
        }
    }

    /** Centralized static mock data. Replace with a service/DB layer later. */
    private static class MockData {

        static List<EventItem> upcomingEvents() {
            List<EventItem> list = new ArrayList<>();
            list.add(new EventItem("TechVision Annual Summit", "18 May 2026",
                    "Grand Convention Center", 250, "Confirmed"));
            list.add(new EventItem("Corporate Leadership Meet", "25 May 2026",
                    "Pune Business Hub", 180, "In Planning"));
            list.add(new EventItem("Product Launch 2026", "02 June 2026",
                    "Skyline Convention Hall", 320, "In Planning"));
            return list;
        }

        static List<RequestItem> eventRequests() {
            List<RequestItem> list = new ArrayList<>();
            list.add(new RequestItem("TechVision Annual Summit", "Corporate Conference", 250, "Pending"));
            list.add(new RequestItem("GreenTech Business Meet", "Corporate Event", 150, "Pending"));
            list.add(new RequestItem("Startup Connect 2026", "Networking Event", 200, "Pending"));
            return list;
        }

        static List<VendorItem> vendors() {
            List<VendorItem> list = new ArrayList<>();
            list.add(new VendorItem("VisionPro AV", true));
            list.add(new VendorItem("FreshBite Catering", true));
            list.add(new VendorItem("PixelFrame Studios", false));
            list.add(new VendorItem("Security Services", true));
            return list;
        }

        static List<ActivityItem> recentActivity() {
            List<ActivityItem> list = new ArrayList<>();
            list.add(new ActivityItem("10:42 AM", "Rahul Sharma submitted a new event request.", IconPaths.INBOX));
            list.add(new ActivityItem("10:15 AM", "FreshBite Catering confirmed the vendor booking.", IconPaths.CHECK_CIRCLE));
            list.add(new ActivityItem("09:48 AM", "AI Event Planner generated a schedule recommendation.", IconPaths.SPARKLE));
            list.add(new ActivityItem("09:20 AM", "18 guests completed QR registration.", IconPaths.QR));
            list.add(new ActivityItem("Yesterday", "Budget updated for TechVision Annual Summit.", IconPaths.WALLET));
            return list;
        }
    }

    /**
     * Reusable line-icon path data (24x24 viewbox), used with createIcon().
     * Simple, consistent, professional line-style icons.
     */
    private static class IconPaths {
        static final String CALENDAR =
                "M7 2v3M17 2v3M3.5 8h17M5 4h14a2 2 0 0 1 2 2v13a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2Z";
        static final String CALENDAR_CHECK =
                "M7 2v3M17 2v3M3.5 8h17M5 4h14a2 2 0 0 1 2 2v13a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2Z M8 14l2.5 2.5L16 11";
        static final String GRID =
                "M4 4h6v6H4V4Zm10 0h6v6h-6V4ZM4 14h6v6H4v-6Zm10 0h6v6h-6v-6Z";
        static final String INBOX =
                "M3 12l2.5-7A2 2 0 0 1 7.4 3.5h9.2a2 2 0 0 1 1.9 1.5L21 12v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-6Z M3 12h5l1.5 3h5L16 12h5";
        static final String SPARKLE =
                "M12 2l1.8 4.9L19 8.5l-4.9 1.8L12 15l-1.8-4.7L5 8.5l5.2-1.6L12 2Z M18 15l.9 2.4L21 18l-2.1.6L18 21l-.9-2.4L15 18l2.1-.6L18 15Z";
        static final String WALLET =
                "M3 7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7Z M16 12h3v2h-3a1.5 1.5 0 0 1 0-2Z";
        static final String PIN =
                "M12 22s7-7.2 7-12A7 7 0 0 0 5 10c0 4.8 7 12 7 12Z M12 12.5A2.5 2.5 0 1 0 12 7.5a2.5 2.5 0 0 0 0 5Z";
        static final String STORE =
                "M4 9l1-5h14l1 5M4 9h16M4 9v10a1 1 0 0 0 1 1h3v-6h8v6h3a1 1 0 0 0 1-1V9";
        static final String CLOCK =
                "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Z M12 7v5l3.5 2";
        static final String USERS =
                "M9 11a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z M3 21v-1a5 5 0 0 1 5-5h2a5 5 0 0 1 5 5v1 M17 11a3 3 0 1 0 0-6 M17 21v-1a5 5 0 0 0-3-4.6";
        static final String QR =
                "M3 3h7v7H3V3Zm11 0h7v7h-7V3ZM3 14h7v7H3v-7Zm14 0h1v1h-1zm3 0h1v3h-3v-1h2zm-4 3h1v4h-4v-1h3z";
        static final String PULSE =
                "M3 12h4l2-7 4 14 2-7h6";
        static final String BELL =
                "M6 9a6 6 0 1 1 12 0v5l1.8 2.6a1 1 0 0 1-.8 1.6H5a1 1 0 0 1-.8-1.6L6 14V9Z M9.5 19a2.5 2.5 0 0 0 5 0";
        static final String GEAR =
                "M12 15.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z M19.4 13.5a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.9 2.9l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.6V20a2 2 0 1 1-4 0v-.2a1.7 1.7 0 0 0-1-1.6 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.9-2.9l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.6-1H4a2 2 0 1 1 0-4h.2a1.7 1.7 0 0 0 1.6-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.9-2.9l.1.1a1.7 1.7 0 0 0 1.9.3H10a1.7 1.7 0 0 0 1-1.6V4a2 2 0 1 1 4 0v.2a1.7 1.7 0 0 0 1 1.6 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.9 2.9l-.1.1a1.7 1.7 0 0 0-.3 1.9V10a1.7 1.7 0 0 0 1.6 1H20a2 2 0 1 1 0 4h-.2a1.7 1.7 0 0 0-1.6 1Z";
        static final String HELP =
                "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Z M9.5 9a2.5 2.5 0 1 1 3.7 2.2c-.8.5-1.2.9-1.2 1.8v.5 M12 17h.01";
        static final String SEARCH =
                "M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16Z M21 21l-4.3-4.3";
        static final String CHECK_CIRCLE =
                "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Z M8 12l2.5 2.5L16 9";
        static final String WARNING =
                "M12 3 2 20h20L12 3Z M12 10v4 M12 17h.01";
    }
}
