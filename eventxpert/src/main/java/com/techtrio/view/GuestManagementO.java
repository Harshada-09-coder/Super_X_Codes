package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuestManagementO extends Application {

    // Color Palette
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Badges & Alerts
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String YELLOW_BG = "#FEF7E0";
    private static final String YELLOW_TEXT = "#B06000";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String PURPLE_BG = "#F3E8FF";
    private static final String PURPLE_TEXT = "#6B21A8";

    // Dynamic UI Stats Labels
    private Label lblTotalGuests;
    private Label lblConfirmed;
    private Label lblPending;
    private Label lblDeclined;
    private Label lblCheckedIn;
    private ProgressBar pbRsvpRate;
    private Label lblRsvpPercent;
    private VBox rosterTableContainer;

    // Guest Data Model
    private static class GuestItem {
        String initials;
        String name;
        String email;
        String company;
        String type; // VIP, Speaker, Employee, Partner
        String rsvpStatus; // Confirmed, Pending, Declined
        String qrStatus; // Generated, Pending RSVP
        boolean isCheckedIn;

        GuestItem(String initials, String name, String email, String company, String type, String rsvpStatus, String qrStatus, boolean isCheckedIn) {
            this.initials = initials;
            this.name = name;
            this.email = email;
            this.company = company;
            this.type = type;
            this.rsvpStatus = rsvpStatus;
            this.qrStatus = qrStatus;
            this.isCheckedIn = isCheckedIn;
        }
    }

    private final List<GuestItem> guestList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Sample Guest Data
        guestList.add(new GuestItem("RS", "Rahul Sharma", "rahul.s@techcorp.com", "TechVision Solutions", "VIP", "Confirmed", "Generated", true));
        guestList.add(new GuestItem("PD", "Priya Deshmukh", "priya.d@innovate.io", "GreenTech Pvt Ltd", "Speaker", "Confirmed", "Generated", false));
        guestList.add(new GuestItem("AP", "Amit Patil", "amit.p@techvision.com", "TechVision Solutions", "Employee", "Pending", "Pending RSVP", false));
        guestList.add(new GuestItem("NK", "Neha Kulkarni", "neha.k@startup.connect", "Startup Connect", "Partner", "Confirmed", "Generated", true));
        guestList.add(new GuestItem("VJ", "Vikram Joshi", "vikram.j@globaltech.org", "GlobalTech", "VIP", "Declined", "N/A", false));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;");

        // Top Navigation Bar
        root.setTop(createTopBar());

        // Sidebar Navigation
        root.setLeft(createSidebar());

        // Main Content Area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1440, 900);
        primaryStage.setTitle("EventXpert - Guest Management & QR Check-In");
        primaryStage.setScene(scene);
        primaryStage.show();

        recalculateStats();
    }

    // ==========================================
    // TOP BAR
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        // Search Bar
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search guests, events, or actions...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        // Profile Box
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(18, Color.web(PRIMARY_BLUE));
        Label avatarText = new Label("AS");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        avatar.getChildren().addAll(circle, avatarText);

        VBox userInfo = new VBox(1);
        Label userName = new Label("Alex Sterling");
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        Label userRole = new Label("Lead Planner");
        userRole.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);

        profileBox.getChildren().addAll(avatar, userInfo);

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, helpBtn, profileBox);
        return topBar;
    }

    // ==========================================
    // SIDEBAR
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setSpacing(6);

        // Logo
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 10, 6));

        StackPane logoBg = new StackPane();
        Rectangle rect = new Rectangle(34, 34);
        rect.setArcWidth(8);
        rect.setArcHeight(8);
        rect.setFill(Color.web(PRIMARY_BLUE));
        Label logoText = new Label("EX");
        logoText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        logoBg.getChildren().addAll(rect, logoText);

        VBox brandTitles = new VBox(1);
        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Corporate Suite");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        brandTitles.getChildren().addAll(brandName, brandSub);

        brand.getChildren().addAll(logoBg, brandTitles);
        sidebar.getChildren().addAll(brand, new Separator());

        // Nav Menu
        sidebar.getChildren().addAll(
            createNavHeader("EVENT MANAGEMENT"),
            createNavItem("Dashboard", false),
            createNavItem("Event Requests", false),
            createNavItem("My Events", false),

            createNavHeader("EVENT PLANNING"),
            createNavItem("AI Planner", false),
            createNavItem("Budget", false),
            createNavItem("Venue", false),
            createNavItem("Vendors", false),
            createNavItem("Schedule", false),

            createNavHeader("EVENT EXECUTION"),
            createNavItem("Guest Management", true), // Active
            createNavItem("QR Check-In", false),
            createNavItem("Live Event", false)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(4);
        bottomMenu.getChildren().addAll(
            new Separator(),
            createNavItem("Settings", false),
            createNavItem("Support", false)
        );

        sidebar.getChildren().addAll(spacer, bottomMenu);
        return sidebar;
    }

    private Label createNavHeader(String title) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 8 0 2 6;");
        return header;
    }

    private Button createNavItem(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(7, 10, 7, 10));

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-background-radius: 6; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-background-radius: 6; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-background-radius: 6; -fx-cursor: hand;"));
        }

        btn.setOnAction(e -> {
            if (!isActive) {
                showInfoAlert("Navigation", "Navigating to " + text + "...");
            }
        });

        return btn;
    }

    // ==========================================
    // MAIN CONTENT
    // ==========================================
    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumbs
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> showInfoAlert("Navigation", "Navigating to My Events..."));

        Label bcSep1 = new Label(">");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("TechVision Annual Summit");
        bc2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bcSep2 = new Label(">");
        bcSep2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc3 = new Label("Guest Management");
        bc3.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSep2, bc3);

        // Header Title Row
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox tRow = new HBox(10);
        tRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Guest Management & QR Check-In");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label statusBadge = new Label("In Planning");
        statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 12;");

        tRow.getChildren().addAll(title, statusBadge);

        Label subtitle = new Label("Manage guest attendance, invitations and seamless QR-based event check-in for the TechVision Annual Summit.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(tRow, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button exportBtn = new Button("📥  Export List");
        exportBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6; -fx-border-radius: 6; -fx-cursor: hand;");
        exportBtn.setOnAction(e -> showInfoAlert("Export", "Exporting guest list to CSV/Excel..."));

        Button addGuestBtn = new Button("+ Add Guest");
        addGuestBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        addGuestBtn.setOnAction(e -> handleAddGuestDialog());

        headerRow.getChildren().addAll(titleBox, headerSpacer, exportBtn, addGuestBtn);

        // Summary Cards Row (5 Cards)
        HBox summaryRow = new HBox(14);

        lblTotalGuests = new Label("250");
        lblConfirmed = new Label("180");
        lblPending = new Label("45");
        lblDeclined = new Label("25");
        lblCheckedIn = new Label("0/250");

        summaryRow.getChildren().addAll(
            createStatCard("TOTAL GUESTS 👥", lblTotalGuests, TEXT_MAIN),
            createStatCard("CONFIRMED ✓", lblConfirmed, BRIGHT_BLUE),
            createStatCard("PENDING RSVP ⌛", lblPending, YELLOW_TEXT),
            createStatCard("DECLINED ✕", lblDeclined, RED_TEXT),
            createStatCard("CHECKED IN 🎟️", lblCheckedIn, PRIMARY_BLUE)
        );

        // 2-Column Main Layout
        HBox bodyGrid = new HBox(20);

        // Left Column: Guest Roster Table
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().add(createGuestRosterCard());

        // Right Column: Side Widgets
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(320);
        rightCol.setMinWidth(300);

        rightCol.getChildren().addAll(
            createRsvpRateCard(),
            createAttentionNeededCard(),
            createQrProvisioningCard(),
            createLiveScannerCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, headerRow, summaryRow, bodyGrid);
        return mainBox;
    }

    // ==========================================
    // STATS CARDS
    // ==========================================
    private VBox createStatCard(String title, Label valLabel, String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        valLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(t, valLabel);
        return card;
    }

    // ==========================================
    // GUEST ROSTER TABLE CARD
    // ==========================================
    private VBox createGuestRosterCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox topHeader = new HBox();
        topHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Guest Roster");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBox = new HBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        TextField filterInput = new TextField();
        filterInput.setPromptText("Search guest by name...");
        filterInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-pref-width: 180px;");
        searchBox.getChildren().addAll(new Label("🔍"), filterInput);

        Button filterBtn = new Button("🎛️");
        filterBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-cursor: hand;");

        topHeader.getChildren().addAll(title, spacer, searchBox, filterBtn);

        // Table Header Row
        GridPane tableHeader = new GridPane();
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6;");

        tableHeader.add(createHeaderLabel("GUEST"), 0, 0);
        tableHeader.add(createHeaderLabel("TYPE"), 1, 0);
        tableHeader.add(createHeaderLabel("RSVP STATUS"), 2, 0);
        tableHeader.add(createHeaderLabel("QR STATUS"), 3, 0);
        tableHeader.add(createHeaderLabel("CHECK IN"), 4, 0);

        setGridConstraints(tableHeader);

        rosterTableContainer = new VBox(2);
        renderRosterRows();

        // Table Pagination Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Label pageInfo = new Label("Showing 1 to 5 of 250 entries");
        pageInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        HBox pageBtns = new HBox(4);
        pageBtns.getChildren().addAll(
            createPageBtn("Prev", false),
            createPageBtn("1", true),
            createPageBtn("2", false),
            createPageBtn("3", false),
            createPageBtn("Next", false)
        );

        footer.getChildren().addAll(pageInfo, footerSpacer, pageBtns);

        card.getChildren().addAll(topHeader, tableHeader, rosterTableContainer, footer);
        return card;
    }

    private Label createHeaderLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return l;
    }

    private void renderRosterRows() {
        rosterTableContainer.getChildren().clear();

        for (int i = 0; i < guestList.size(); i++) {
            GuestItem item = guestList.get(i);
            GridPane row = new GridPane();
            row.setPadding(new Insets(12, 12, 12, 12));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            // Guest Name + Avatar
            HBox guestCell = new HBox(10);
            guestCell.setAlignment(Pos.CENTER_LEFT);

            StackPane avatar = new StackPane();
            Circle c = new Circle(16, Color.web(LIGHT_BLUE));
            Label avTxt = new Label(item.initials);
            avTxt.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            avatar.getChildren().addAll(c, avTxt);

            VBox textGroup = new VBox(2);
            Label name = new Label(item.name);
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            Label email = new Label(item.email);
            email.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            textGroup.getChildren().addAll(name, email);

            guestCell.getChildren().addAll(avatar, textGroup);
            row.add(guestCell, 0, 0);

            // Type Badge
            Label typeBadge = new Label(item.type);
            if (item.type.equalsIgnoreCase("VIP")) {
                typeBadge.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            } else if (item.type.equalsIgnoreCase("Speaker")) {
                typeBadge.setStyle("-fx-background-color: " + PURPLE_BG + "; -fx-text-fill: " + PURPLE_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            } else {
                typeBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            row.add(typeBadge, 1, 0);

            // RSVP Status
            Label rsvpBadge = new Label("● " + item.rsvpStatus);
            if (item.rsvpStatus.equalsIgnoreCase("Confirmed")) {
                rsvpBadge.setStyle("-fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            } else if (item.rsvpStatus.equalsIgnoreCase("Pending")) {
                rsvpBadge.setStyle("-fx-text-fill: " + YELLOW_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            } else {
                rsvpBadge.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            }
            row.add(rsvpBadge, 2, 0);

            // QR Status
            Label qrLbl = new Label("🏁 " + item.qrStatus);
            qrLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            row.add(qrLbl, 3, 0);

            // Check-In Toggle Action Button
            int index = i;
            Button checkInBtn = new Button(item.isCheckedIn ? "✓ Checked In" : "Check In");
            if (item.isCheckedIn) {
                checkInBtn.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4; -fx-cursor: hand;");
            } else {
                checkInBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4; -fx-border-radius: 4; -fx-cursor: hand;");
            }

            checkInBtn.setOnAction(e -> {
                item.isCheckedIn = !item.isCheckedIn;
                renderRosterRows();
                recalculateStats();
            });

            row.add(checkInBtn, 4, 0);

            setGridConstraints(row);
            rosterTableContainer.getChildren().add(row);
        }
    }

    private void setGridConstraints(GridPane grid) {
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(35);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(15);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(18);
        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPercentWidth(18);
        ColumnConstraints c5 = new ColumnConstraints();
        c5.setPercentWidth(14);

        grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5);
    }

    private Button createPageBtn(String text, boolean active) {
        Button btn = new Button(text);
        if (active) {
            btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4;");
        } else {
            btn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
        }
        return btn;
    }

    // ==========================================
    // RIGHT COLUMN WIDGETS
    // ==========================================
    private VBox createRsvpRateCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("RSVP Confirmation Rate");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Target: 85% before event day");
        subtitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox rateRow = new HBox();
        rateRow.setAlignment(Pos.CENTER_LEFT);

        lblRsvpPercent = new Label("72%");
        lblRsvpPercent.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLbl = new Label("180/250");
        countLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        rateRow.getChildren().addAll(lblRsvpPercent, spacer, countLbl);

        pbRsvpRate = new ProgressBar(0.72);
        pbRsvpRate.setMaxWidth(Double.MAX_VALUE);
        pbRsvpRate.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-control-inner-background: " + LIGHT_BLUE + "; -fx-pref-height: 8;");

        card.getChildren().addAll(title, subtitle, rateRow, pbRsvpRate);
        return card;
    }

    private VBox createAttentionNeededCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-border-color: " + YELLOW_TEXT + "; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label header = new Label("⚠️ Attention Needed");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + YELLOW_TEXT + ";");

        Label l1 = new Label("• 45 guests have Pending RSVPs.");
        l1.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label l2 = new Label("• 70 confirmed guests are missing QR Codes.");
        l2.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Button reminderBtn = new Button("Send Reminders →");
        reminderBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 4 0 0 0;");
        reminderBtn.setOnAction(e -> showInfoAlert("Reminders Sent", "Follow-up RSVP reminders emailed to 45 guests."));

        card.getChildren().addAll(header, l1, l2, reminderBtn);
        return card;
    }

    private VBox createQrProvisioningCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("🏁 QR Provisioning");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox pRow = new HBox();
        Label pLabel = new Label("Generated:");
        pLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pVal = new Label("180 / 180 (Confirmed)");
        pVal.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        pRow.getChildren().addAll(pLabel, spacer, pVal);

        Button genBtn = new Button("🔄 Generate Missing Codes");
        genBtn.setMaxWidth(Double.MAX_VALUE);
        genBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
        genBtn.setOnAction(e -> showInfoAlert("QR Provisioning", "All confirmed guest QR passes generated."));

        card.getChildren().addAll(title, pRow, genBtn);
        return card;
    }

    private VBox createLiveScannerCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + NAVY + "; -fx-background-radius: 10;");

        HBox header = new HBox();
        Label title = new Label("((•)) Live QR Scanner");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label liveDot = new Label("●");
        liveDot.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-font-size: 12px;");
        header.getChildren().addAll(title, spacer, liveDot);

        Button scanBtn = new Button("Start Scanner / Simulate Scan");
        scanBtn.setMaxWidth(Double.MAX_VALUE);
        scanBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 8; -fx-background-radius: 6; -fx-cursor: hand;");
        scanBtn.setOnAction(e -> handleSimulateScan());

        card.getChildren().addAll(header, scanBtn);
        return card;
    }

    // ==========================================
    // ACTION HANDLERS
    // ==========================================
    private void recalculateStats() {
        int total = guestList.size();
        int confirmed = 0;
        int pending = 0;
        int declined = 0;
        int checkedIn = 0;

        for (GuestItem item : guestList) {
            if (item.rsvpStatus.equalsIgnoreCase("Confirmed")) confirmed++;
            else if (item.rsvpStatus.equalsIgnoreCase("Pending")) pending++;
            else if (item.rsvpStatus.equalsIgnoreCase("Declined")) declined++;

            if (item.isCheckedIn) checkedIn++;
        }

        lblTotalGuests.setText(String.valueOf(total));
        lblConfirmed.setText(String.valueOf(confirmed));
        lblPending.setText(String.valueOf(pending));
        lblDeclined.setText(String.valueOf(declined));
        lblCheckedIn.setText(checkedIn + "/" + total);

        double rate = total > 0 ? ((double) confirmed / total) : 0;
        lblRsvpPercent.setText(String.format("%.0f%%", rate * 100));
        pbRsvpRate.setProgress(rate);
    }

    private void handleAddGuestDialog() {
        Dialog<GuestItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Guest");
        dialog.setHeaderText("Register a new guest for TechVision Annual Summit");

        ButtonType addBtnType = new ButtonType("Add Guest", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Full Name");

        TextField emailInput = new TextField();
        emailInput.setPromptText("Email Address");

        TextField companyInput = new TextField();
        companyInput.setPromptText("Organization / Company");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("VIP", "Speaker", "Employee", "Partner");
        typeCombo.setValue("Employee");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameInput, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailInput, 1, 1);
        grid.add(new Label("Company:"), 0, 2);
        grid.add(companyInput, 1, 2);
        grid.add(new Label("Guest Type:"), 0, 3);
        grid.add(typeCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                String n = nameInput.getText().trim();
                String[] parts = n.split(" ");
                String init = parts.length > 1 ? (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase() : "G";

                return new GuestItem(
                    init,
                    n,
                    emailInput.getText().trim(),
                    companyInput.getText().trim(),
                    typeCombo.getValue(),
                    "Confirmed",
                    "Generated",
                    false
                );
            }
            return null;
        });

        Optional<GuestItem> result = dialog.showAndWait();
        result.ifPresent(guest -> {
            guestList.add(guest);
            renderRosterRows();
            recalculateStats();
            showInfoAlert("Success", "Guest added successfully!");
        });
    }

    private void handleSimulateScan() {
        for (GuestItem item : guestList) {
            if (!item.isCheckedIn && item.rsvpStatus.equalsIgnoreCase("Confirmed")) {
                item.isCheckedIn = true;
                renderRosterRows();
                recalculateStats();
                showInfoAlert("✓ Check-In Successful", "Simulated QR Scan Match:\n\nGuest: " + item.name + "\nType: " + item.type + "\nStatus: Checked In Successfully!");
                return;
            }
        }
        showInfoAlert("QR Scanner", "All confirmed guests are already checked in.");
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}