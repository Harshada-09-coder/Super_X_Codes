package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.techtrio.session.UserSession;

public class GuestManagementO {

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

    // Status Badges & Callout Colors
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String YELLOW_BG = "#FEF7E0";
    private static final String YELLOW_TEXT = "#B06000";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String PURPLE_BG = "#F3E8FF";
    private static final String PURPLE_TEXT = "#6B21A8";

    private String uid;
    private String idToken;
    private Runnable backAction;

    // Dynamic UI Stats
    private Label lblTotalGuests;
    private Label lblConfirmed;
    private Label lblPending;
    private Label lblDeclined;
    private Label lblCheckedIn;
    private ProgressBar pbRsvpRate;
    private Label lblRsvpPercent;
    private VBox rosterTableContainer;

    private static class GuestItem {
        String initials;
        String name;
        String email;
        String type; // VIP, Speaker, Employee, Partner
        String rsvpStatus; // Confirmed, Pending, Declined
        String qrStatus; // Generated, Pending RSVP
        boolean isCheckedIn;

        GuestItem(String initials, String name, String email, String type, String rsvpStatus, String qrStatus, boolean isCheckedIn) {
            this.initials = initials;
            this.name = name;
            this.email = email;
            this.type = type;
            this.rsvpStatus = rsvpStatus;
            this.qrStatus = qrStatus;
            this.isCheckedIn = isCheckedIn;
        }
    }

    private final List<GuestItem> guestList = new ArrayList<>();

    public GuestManagementO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public GuestManagementO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        if (guestList.isEmpty()) {
            guestList.add(new GuestItem("RS", "Rahul Sharma", "rahul.s@techcorp.com", "VIP", "Confirmed", "Generated", false));
            guestList.add(new GuestItem("PD", "Priya Deshmukh", "priya.d@innovate.io", "Speaker", "Confirmed", "Generated", false));
            guestList.add(new GuestItem("AP", "Amit Patil", "amit.p@techvision.com", "Employee", "Pending", "Pending RSVP", false));
        }
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar(stage));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        recalculateStats();
        return new Scene(root, 1550, 785);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search guests, events, or actions...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button bellBtn = new Button("🔔");
        bellBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));

        topBar.getChildren().addAll(searchBox, spacer, bellBtn, helpBtn, avatar);
        return topBar;
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
        menu.getChildren().add(createNavBtn("Guest Management", true, () -> {}));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottom = new VBox(6);
        bottom.getChildren().add(createNavBtn("Notifications", false, () -> {
            NotificationsO view = new NotificationsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getNotificationScene());
        }));
        bottom.getChildren().add(createNavBtn("Settings", false, () -> {
            SettingsO view = new SettingsO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getSettingScene(() -> goDashboard(stage)));
        }));
        bottom.getChildren().add(createNavBtn("Profile", false, () -> {
            ProfileO view = new ProfileO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getProfileScene());
        }));

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer, bottom);
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

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumbs & Title Row
        VBox titleBox = new VBox(4);
        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Guest Management");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox tRow = new HBox(10);
        tRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Guest Management & QR Check-In");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label statusBadge = new Label("In Planning");
        statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12px;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button exportBtn = new Button("📥  Export List");
        exportBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");

        Button addGuestBtn = new Button("+ Add Guest");
        addGuestBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        addGuestBtn.setOnAction(e -> handleAddGuestDialog());

        tRow.getChildren().addAll(title, statusBadge, headerSpacer, exportBtn, addGuestBtn);

        Label subtitle = new Label("Manage guest attendance, invitations and seamless QR-based event check-in for the TechVision Annual Summit.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(breadcrumb, tRow, subtitle);

        // Top 5 Metric Cards Row
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

        // Main 2-Column Split Grid
        HBox bodyGrid = new HBox(20);

        // Left Column: Guest Roster Card
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.getChildren().add(createGuestRosterCard());

        // Right Column: Side Widgets
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(330);
        rightCol.setMinWidth(310);

        rightCol.getChildren().addAll(
            createRsvpRateCard(),
            createAttentionNeededCard(),
            createQrProvisioningCard(),
            createLiveScannerCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(titleBox, summaryRow, bodyGrid);
        return mainBox;
    }

    private VBox createStatCard(String title, Label valLabel, String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        valLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(t, valLabel);
        return card;
    }

    private VBox createGuestRosterCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topHeader = new HBox();
        topHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Guest Roster");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBox = new HBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        TextField filterInput = new TextField();
        filterInput.setPromptText("Search guest by name...");
        filterInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-pref-width: 180px;");
        searchBox.getChildren().addAll(new Label("🔍"), filterInput);

        topHeader.getChildren().addAll(title, spacer, searchBox);

        // Table Header Row
        GridPane tableHeader = new GridPane();
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        tableHeader.add(createHeaderLabel("GUEST"), 0, 0);
        tableHeader.add(createHeaderLabel("TYPE"), 1, 0);
        tableHeader.add(createHeaderLabel("RSVP STATUS"), 2, 0);
        tableHeader.add(createHeaderLabel("QR STATUS"), 3, 0);
        tableHeader.add(createHeaderLabel("CHECK IN"), 4, 0);

        setGridConstraints(tableHeader);

        rosterTableContainer = new VBox(2);
        renderRosterRows();

        // Footer Pagination
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Label pageInfo = new Label("Showing 1 to 3 of 250 entries");
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
            row.setPadding(new Insets(12));
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
                typeBadge.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            } else if (item.type.equalsIgnoreCase("Speaker")) {
                typeBadge.setStyle("-fx-background-color: " + PURPLE_BG + "; -fx-text-fill: " + PURPLE_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            } else {
                typeBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            }
            row.add(typeBadge, 1, 0);

            // RSVP Status
            Label rsvpBadge = new Label("● " + item.rsvpStatus);
            if (item.rsvpStatus.equalsIgnoreCase("Confirmed")) {
                rsvpBadge.setStyle("-fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            } else {
                rsvpBadge.setStyle("-fx-text-fill: " + YELLOW_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            }
            row.add(rsvpBadge, 2, 0);

            // QR Status
            Label qrLbl = new Label("🏁 " + item.qrStatus);
            qrLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            row.add(qrLbl, 3, 0);

            // Check-In Status
            Label checkInLbl = new Label(item.isCheckedIn ? "✓ Checked In" : "Not Checked In");
            checkInLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (item.isCheckedIn ? GREEN_TEXT : SECONDARY_TEXT) + ";");
            row.add(checkInLbl, 4, 0);

            setGridConstraints(row);
            rosterTableContainer.getChildren().add(row);
        }
    }

    private void setGridConstraints(GridPane grid) {
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(35);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(15);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(18);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(18);
        ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(14);
        grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5);
    }

    private Button createPageBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(active ? "-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4px;"
                : "-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-cursor: hand;");
        return btn;
    }

    // ==========================================
    // RIGHT COLUMN SIDE WIDGETS
    // ==========================================
    private VBox createRsvpRateCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

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
        pbRsvpRate.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        card.getChildren().addAll(title, subtitle, rateRow, pbRsvpRate);
        return card;
    }

    private VBox createAttentionNeededCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-border-color: #FCD34D; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label header = new Label("⚠️ Attention Needed");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + YELLOW_TEXT + ";");

        Label l1 = new Label("• 45 guests have Pending RSVPs.");
        l1.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label l2 = new Label("• 70 confirmed guests are missing QR Codes.");
        l2.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Button reminderBtn = new Button("Send Reminders");
        reminderBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 4 0 0 0;");

        card.getChildren().addAll(header, l1, l2, reminderBtn);
        return card;
    }

    private VBox createQrProvisioningCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("🏁 QR Provisioning");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox pRow = new HBox();
        Label pLabel = new Label("Generated:"); pLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pVal = new Label("180 / 180 (Confirmed)"); pVal.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        pRow.getChildren().addAll(pLabel, spacer, pVal);

        Button genBtn = new Button("🔄 Generate Missing Codes");
        genBtn.setMaxWidth(Double.MAX_VALUE);
        genBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8; -fx-cursor: hand;");

        card.getChildren().addAll(title, pRow, genBtn);
        return card;
    }

    private VBox createLiveScannerCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + BRIGHT_BLUE + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox header = new HBox();
        Label title = new Label("((•)) Live Scanner");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label liveDot = new Label("●"); liveDot.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-font-size: 12px;");
        header.getChildren().addAll(title, spacer, liveDot);

        card.getChildren().addAll(header);
        return card;
    }

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

        if (lblTotalGuests != null) lblTotalGuests.setText(String.valueOf(total));
        if (lblConfirmed != null) lblConfirmed.setText(String.valueOf(confirmed));
        if (lblPending != null) lblPending.setText(String.valueOf(pending));
        if (lblDeclined != null) lblDeclined.setText(String.valueOf(declined));
        if (lblCheckedIn != null) lblCheckedIn.setText(checkedIn + "/" + total);

        double rate = total > 0 ? ((double) confirmed / total) : 0;
        if (lblRsvpPercent != null) lblRsvpPercent.setText(String.format("%.0f%%", rate * 100));
        if (pbRsvpRate != null) pbRsvpRate.setProgress(rate);
    }

    private void handleAddGuestDialog() {
        Dialog<GuestItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Guest");
        dialog.setHeaderText("Register a new guest for TechVision Annual Summit");

        ButtonType addBtnType = new ButtonType("Add Guest", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField nameInput = new TextField(); nameInput.setPromptText("Full Name");
        TextField emailInput = new TextField(); emailInput.setPromptText("Email Address");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("VIP", "Speaker", "Employee", "Partner");
        typeCombo.setValue("Employee");

        grid.add(new Label("Full Name:"), 0, 0); grid.add(nameInput, 1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(emailInput, 1, 1);
        grid.add(new Label("Guest Type:"), 0, 2); grid.add(typeCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                String n = nameInput.getText().trim();
                String[] parts = n.split(" ");
                String init = parts.length > 1 ? (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase() : "G";

                return new GuestItem(init, n, emailInput.getText().trim(), typeCombo.getValue(), "Confirmed", "Generated", false);
            }
            return null;
        });

        Optional<GuestItem> result = dialog.showAndWait();
        result.ifPresent(guest -> {
            guestList.add(guest);
            renderRosterRows();
            recalculateStats();
        });
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }
}