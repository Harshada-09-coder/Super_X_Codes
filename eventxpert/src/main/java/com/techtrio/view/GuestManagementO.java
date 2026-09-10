package com.techtrio.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.context.AdminContext;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.Guest;
import com.techtrio.services.CustomerProfileService;
import com.techtrio.services.EmailService;
import com.techtrio.services.EventService;
import com.techtrio.services.GuestService;
import com.techtrio.session.UserSession;
import com.techtrio.util.QRCodeUtil;

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

    // Status Badges
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

    // Real-Time Event State
    private String currentEventId;
    private Event currentEvent;
    private final List<Guest> currentGuestList = new ArrayList<>();
    private final List<Guest> filteredGuestList = new ArrayList<>();
    private ListenerRegistration firestoreListener;

    private StackPane rootLayer;

    // Dynamic UI Metrics
    private Label lblEntryMode;
    private Label lblTotalLimit;
    private Label lblCheckedIn;
    private Label lblRemaining;
    private Label lblEventSubtitle;
    private Button btnSendPasses;

    // Side widgets & Live Scanner
    private ProgressBar pbEntryRate;
    private Label lblEntryPercent;
    private Label lblEntryCount;
    private TextField txtScannerInput;
    private Label lblScanResult;
    private VBox rosterTableContainer;
    private TextField filterInput;

    private final GuestService guestService = new GuestService();
    private final EventService eventService = new EventService();

    public GuestManagementO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public GuestManagementO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        return getScene(stage, AdminContext.getSelectedEventId(), backAction);
    }

    public Scene getScene(Stage stage, String eventId, Runnable backAction) {
        this.backAction = backAction;
        this.currentEventId = (eventId != null && !eventId.isBlank()) ? eventId : AdminContext.getSelectedEventId();

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Matched with My Events)
        root.setLeft(createCommonOrganizerSidebar("My Events"));

        // 2. Main Content inside ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);
        rootLayer.getChildren().add(root);

        // Load Real-time Firestore Listener
        initRealtimeFirestoreListener();

        return new Scene(rootLayer, 1550, 785);
    }

    // ==========================================
    // ORGANIZER SIDEBAR (My Events Highlighted)
    // ==========================================
    private VBox createCommonOrganizerSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        detachListener();
        switch (screen) {
            case "Dashboard":
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
                break;
            case "Event Requests":
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
                break;
            case "My Events":
                handleBack(LandingPage.landingPageStage);
                break;
            case "Schedule":
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
                break;
            case "Notifications":
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
                break;
            case "Profile":
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
                break;
            case "Settings":
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
                break;
            case "Support":
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
                break;
        }
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(18);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumbs
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("← Back to Event Planning");
        bc1.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));
        breadcrumbs.getChildren().add(bc1);

        HBox tRow = new HBox(12);
        tRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Guest Management & QR Check-In");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label statusBadge = new Label("LIVE CHECK-IN ACTIVE");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12px;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Dynamic Send Passes Button
        btnSendPasses = new Button("📧  Send QR Passes to All");
        btnSendPasses.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnSendPasses.setOnAction(e -> handleSendQrPassesToAll());

        Button addGuestBtn = new Button("+ Add Guest");
        addGuestBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        addGuestBtn.setOnAction(e -> handleAddGuestDialog());

        tRow.getChildren().addAll(title, statusBadge, headerSpacer, btnSendPasses, addGuestBtn);

        lblEventSubtitle = new Label("Manage live attendee verification and instant check-in.");
        lblEventSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        // Top 4 Entry Metric Cards Row
        HBox summaryRow = new HBox(14);
        lblEntryMode = new Label("Individual Guest List");
        lblTotalLimit = new Label("0");
        lblCheckedIn = new Label("0");
        lblRemaining = new Label("0");

        summaryRow.getChildren().addAll(
            createStatCard("ENTRY MODE", lblEntryMode, "fas-flag", PRIMARY_BLUE),
            createStatCard("GUEST LIMIT / CAPACITY", lblTotalLimit, "fas-users", TEXT_MAIN),
            createStatCard("CHECKED IN", lblCheckedIn, "fas-check-circle", GREEN_TEXT),
            createStatCard("REMAINING ENTRIES", lblRemaining, "fas-hourglass-half", YELLOW_TEXT)
        );

        // Main 2-Column Split Grid
        HBox bodyGrid = new HBox(18);

        // Left Column: Guest Roster / Live Entry Card
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.getChildren().add(createGuestRosterCard());

        // Right Column: Side Widgets
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
            createLiveScannerCard(stage),
            createEntryRateCard(),
            createQrProvisioningCard()
        );

        bodyGrid.getChildren().addAll(leftCol, rightCol);

        mainBox.getChildren().addAll(breadcrumbs, tRow, lblEventSubtitle, summaryRow, bodyGrid);
        return mainBox;
    }

    private VBox createStatCard(String title, Label valLabel, String iconLit, String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(11);
        icon.setIconColor(Color.web(SECONDARY_TEXT));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        top.getChildren().addAll(icon, t);

        valLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + valColor + ";");

        card.getChildren().addAll(top, valLabel);
        return card;
    }

    private VBox createGuestRosterCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topHeader = new HBox();
        topHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Live Guest Roster");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBox = new HBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconSize(11);
        searchIcon.setIconColor(Color.web(SECONDARY_TEXT));

        filterInput = new TextField();
        filterInput.setPromptText("Search guest by name or mobile...");
        filterInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-pref-width: 200px;");
        filterInput.textProperty().addListener((obs, oldVal, newVal) -> filterGuestList(newVal));
        searchBox.getChildren().addAll(searchIcon, filterInput);

        topHeader.getChildren().addAll(title, spacer, searchBox);

        // Table Header Row
        GridPane tableHeader = new GridPane();
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        tableHeader.add(createHeaderLabel("GUEST"), 0, 0);
        tableHeader.add(createHeaderLabel("CONTACT"), 1, 0);
        tableHeader.add(createHeaderLabel("TYPE"), 2, 0);
        tableHeader.add(createHeaderLabel("CHECK-IN STATUS"), 3, 0);
        tableHeader.add(createHeaderLabel("ACTION"), 4, 0);

        setGridConstraints(tableHeader);

        rosterTableContainer = new VBox(2);

        card.getChildren().addAll(topHeader, tableHeader, rosterTableContainer);
        return card;
    }

    private Label createHeaderLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return l;
    }

    private void renderRosterRows() {
        rosterTableContainer.getChildren().clear();

        if (currentEvent != null && "QR_ONLY".equalsIgnoreCase(currentEvent.getGuestMode())) {
            int dynamicLimit = currentEvent.getGuestLimit() > 0 ? currentEvent.getGuestLimit() : (currentEvent.getGuests() > 0 ? currentEvent.getGuests() : 250);

            VBox qrNotice = new VBox(12);
            qrNotice.setAlignment(Pos.CENTER);
            qrNotice.setPadding(new Insets(30));
            qrNotice.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-radius: 8px;");

            Label l1 = new Label("Single Event QR Entry Mode Active");
            l1.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

            Label l2 = new Label("Individual guest registration is not required. Attendees can present the Event QR Pass for entry up to " + dynamicLimit + " capacity.");
            l2.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            Button btnViewEventQr = new Button("View & Download Event QR");
            btnViewEventQr.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
            btnViewEventQr.setOnAction(e -> {
                String payload = QRCodeUtil.formatPayload(currentEvent.getEventId(), currentEvent.getQrToken());
                showInSceneQrDialog(currentEvent.getEventName(), payload);
            });

            qrNotice.getChildren().addAll(l1, l2, btnViewEventQr);
            rosterTableContainer.getChildren().add(qrNotice);
            return;
        }

        if (filteredGuestList.isEmpty()) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(30));
            empty.getChildren().add(new Label("No guests registered yet."));
            rosterTableContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < filteredGuestList.size(); i++) {
            Guest item = filteredGuestList.get(i);
            GridPane row = new GridPane();
            row.setPadding(new Insets(10, 12, 10, 12));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            // Guest Name + Avatar
            HBox guestCell = new HBox(10);
            guestCell.setAlignment(Pos.CENTER_LEFT);

            String initial = (item.getName() != null && !item.getName().isBlank())
                    ? item.getName().substring(0, 1).toUpperCase() : "G";

            StackPane avatar = new StackPane();
            Circle c = new Circle(15, Color.web(LIGHT_BLUE));
            Label avTxt = new Label(initial);
            avTxt.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            avatar.getChildren().addAll(c, avTxt);

            VBox textGroup = new VBox(2);
            Label name = new Label(item.getName() != null ? item.getName() : "Unnamed Guest");
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

            String checkTimeStr = item.isCheckedIn() && item.getCheckInTime() != null
                    ? "Checked in at " + new SimpleDateFormat("hh:mm a").format(new Date(item.getCheckInTime()))
                    : "Not yet checked in";
            Label subInfo = new Label(checkTimeStr);
            subInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (item.isCheckedIn() ? GREEN_TEXT : SECONDARY_TEXT) + ";");
            textGroup.getChildren().addAll(name, subInfo);

            guestCell.getChildren().addAll(avatar, textGroup);
            row.add(guestCell, 0, 0);

            // Contact (Email or Mobile)
            String contact = (item.getEmail() != null && !item.getEmail().isBlank())
                    ? item.getEmail() : (item.getMobile() != null ? item.getMobile() : "-");
            Label contactLbl = new Label(contact);
            contactLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            row.add(contactLbl, 1, 0);

            // Type Badge
            Label typeBadge = new Label(item.getType() != null ? item.getType() : "General");
            if ("VIP".equalsIgnoreCase(item.getType())) {
                typeBadge.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            } else if ("Speaker".equalsIgnoreCase(item.getType())) {
                typeBadge.setStyle("-fx-background-color: " + PURPLE_BG + "; -fx-text-fill: " + PURPLE_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            } else {
                typeBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 4px;");
            }
            row.add(typeBadge, 2, 0);

            // Check-In Status
            Label checkInLbl = new Label(item.isCheckedIn() ? "✓ Checked In" : "● Not Checked In");
            checkInLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (item.isCheckedIn() ? GREEN_TEXT : SECONDARY_TEXT) + ";");
            row.add(checkInLbl, 3, 0);

            // Actions (Check In + View QR + Send Email)
            HBox actions = new HBox(6);
            actions.setAlignment(Pos.CENTER_LEFT);

            Button btnCheckIn = new Button(item.isCheckedIn() ? "Checked" : "Check In");
            btnCheckIn.setDisable(item.isCheckedIn());
            btnCheckIn.setStyle(item.isCheckedIn()
                    ? "-fx-background-color: #E2E8F0; -fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 4px;"
                    : "-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 4px; -fx-cursor: hand;");
            btnCheckIn.setOnAction(e -> performManualCheckIn(item));

            Button btnViewQr = new Button();
            FontIcon eyeIco = new FontIcon("fas-eye");
            eyeIco.setIconSize(11);
            eyeIco.setIconColor(Color.web(PRIMARY_BLUE));
            btnViewQr.setGraphic(eyeIco);
            btnViewQr.setTooltip(new Tooltip("View & Download QR Pass"));
            btnViewQr.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-padding: 4 8; -fx-background-radius: 4px; -fx-cursor: hand;");
            btnViewQr.setOnAction(e -> {
                String payload = QRCodeUtil.formatPayload(currentEvent.getEventId(), item.getId());
                showInSceneQrDialog(item.getName(), payload);
            });

            Button btnEmail = new Button();
            FontIcon envIco = new FontIcon("fas-envelope");
            envIco.setIconSize(11);
            envIco.setIconColor(Color.web(PRIMARY_BLUE));
            btnEmail.setGraphic(envIco);
            btnEmail.setTooltip(new Tooltip("Send QR Pass via Email"));
            btnEmail.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-padding: 4 8; -fx-background-radius: 4px; -fx-cursor: hand;");
            btnEmail.setOnAction(e -> handleSendSingleQrPass(item));

            actions.getChildren().addAll(btnCheckIn, btnViewQr, btnEmail);
            row.add(actions, 4, 0);

            setGridConstraints(row);
            rosterTableContainer.getChildren().add(row);
        }
    }

    private void setGridConstraints(GridPane grid) {
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(30);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(24);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(14);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(16);
        ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(16);
        grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5);
    }

    // ==========================================
    // RIGHT COLUMN SIDE WIDGETS
    // ==========================================

    private VBox createLiveScannerCard(Stage stage) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + BRIGHT_BLUE + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox header = new HBox(6);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon scanIco = new FontIcon("fas-qrcode");
        scanIco.setIconSize(14);
        scanIco.setIconColor(Color.web(PRIMARY_BLUE));

        Label title = new Label("Live Entry Scanner");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label liveDot = new Label("● LIVE"); liveDot.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        header.getChildren().addAll(scanIco, title, spacer, liveDot);

        Button camBtn = new Button("  Open Camera Scanner");
        FontIcon camIco = new FontIcon("fas-camera");
        camIco.setIconSize(12);
        camIco.setIconColor(Color.WHITE);
        camBtn.setGraphic(camIco);
        camBtn.setMaxWidth(Double.MAX_VALUE);
        camBtn.setStyle("-fx-background-color: #0B4EA2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 8; -fx-background-radius: 6; -fx-cursor: hand;");
        camBtn.setOnAction(e -> {
            new WebcamScannerDialog().openCameraScanner(stage, scannedPayload -> {
                txtScannerInput.setText(scannedPayload);
                handleVerifyScan(); 
            });
        });

        Label prompt = new Label("Scan attendee QR or enter code:");
        prompt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox scanInputBox = new HBox(6);
        txtScannerInput = new TextField();
        txtScannerInput.setPromptText("Scan code or mobile...");
        txtScannerInput.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-size: 11px;");
        HBox.setHgrow(txtScannerInput, Priority.ALWAYS);

        Button scanBtn = new Button("Verify");
        scanBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 6px; -fx-cursor: hand;");
        scanBtn.setOnAction(e -> handleVerifyScan());
        txtScannerInput.setOnAction(e -> handleVerifyScan());

        scanInputBox.getChildren().addAll(txtScannerInput, scanBtn);

        lblScanResult = new Label("Ready to scan.");
        lblScanResult.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(header, camBtn, prompt, scanInputBox, lblScanResult);
        return card;
    }

    private VBox createEntryRateCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Attendance Rate");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox rateRow = new HBox();
        rateRow.setAlignment(Pos.CENTER_LEFT);

        lblEntryPercent = new Label("0%");
        lblEntryPercent.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblEntryCount = new Label("0 / 0");
        lblEntryCount.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        rateRow.getChildren().addAll(lblEntryPercent, spacer, lblEntryCount);

        pbEntryRate = new ProgressBar(0.0);
        pbEntryRate.setMaxWidth(Double.MAX_VALUE);
        pbEntryRate.setStyle("-fx-accent: " + GREEN_TEXT + "; -fx-pref-height: 8;");

        card.getChildren().addAll(title, rateRow, pbEntryRate);
        return card;
    }

    private VBox createQrProvisioningCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("QR Database Sync");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label pLabel = new Label("Real-time Firestore Token Security Active");
        pLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button genBtn = new Button("🔄 Refresh Live Roster");
        genBtn.setMaxWidth(Double.MAX_VALUE);
        genBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8; -fx-cursor: hand;");
        genBtn.setOnAction(e -> initRealtimeFirestoreListener());

        card.getChildren().addAll(title, pLabel, genBtn);
        return card;
    }

    // ==========================================
    // IN-APP RECTANGULAR COMPACT MODAL
    // ==========================================
    private void showInAppModal(String title, String message, boolean isSuccess) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web(isSuccess ? "#DCFCE7" : "#FEE2E2"));
        Label ic = new Label(isSuccess ? "✓" : "⚠");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: " + (isSuccess ? "#16A34A" : "#DC2626") + "; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private void showInSceneQrDialog(String guestName, String payload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("QR Pass");
        dialog.setHeaderText(null);
        if (LandingPage.landingPageStage != null) {
            dialog.initOwner(LandingPage.landingPageStage);
        }

        VBox content = new VBox(14);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Entry Pass QR Code");
        title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #062A60;");

        Label sub = new Label("Guest: " + guestName + "\nEvent: " + (currentEvent != null ? currentEvent.getEventName() : "Event"));
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #52677D; -fx-text-alignment: center;");

        // QR Image
        javafx.scene.image.WritableImage qrImg = QRCodeUtil.generateQrImage(payload, 220, 220);
        javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(qrImg);
        imgView.setStyle("-fx-border-color: #0B4EA2; -fx-border-width: 1px; -fx-padding: 4;");

        // Download Button
        Button btnDownload = new Button("📥 Download PNG");
        btnDownload.setStyle("-fx-background-color: #0B4EA2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        btnDownload.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save QR Code");
            chooser.setInitialFileName(guestName.replaceAll("\\s+", "_") + "_QR.png");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
            File file = chooser.showSaveDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                try {
                    BufferedImage bImage = new BufferedImage(220, 220, BufferedImage.TYPE_INT_ARGB);
                    for (int x = 0; x < 220; x++) {
                        for (int y = 0; y < 220; y++) {
                            bImage.setRGB(x, y, qrImg.getPixelReader().getArgb(x, y));
                        }
                    }
                    ImageIO.write(bImage, "png", file);
                    showInAppModal("Downloaded", "QR Code saved successfully to: " + file.getName(), true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        content.getChildren().addAll(title, sub, imgView, btnDownload);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // ==========================================
    // REAL-TIME FIRESTORE LISTENER
    // ==========================================
    private void initRealtimeFirestoreListener() {
        new Thread(() -> {
            try {
                if (currentEventId == null || currentEventId.isBlank()) {
                    List<Event> orgEvents = eventService.getEventsByOrganizerId(uid);
                    if (orgEvents != null && !orgEvents.isEmpty()) {
                        currentEventId = orgEvents.get(0).getEventId();
                    } else {
                        List<Event> all = eventService.getAllEvents();
                        if (all != null && !all.isEmpty()) currentEventId = all.get(0).getEventId();
                    }
                }

                if (currentEventId == null) return;

                Firestore db = FirestoreClient.getFirestore();
                DocumentReference docRef = db.collection("events").document(currentEventId);

                detachListener();

                firestoreListener = docRef.addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    Event ev = snapshot.toObject(Event.class);
                    if (ev != null) {
                        this.currentEvent = ev;
                        Platform.runLater(() -> updateUIWithRealtimeEvent(ev));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUIWithRealtimeEvent(Event ev) {
        int limit = ev.getGuestLimit() > 0 ? ev.getGuestLimit() : (ev.getGuests() > 0 ? ev.getGuests() : 250);
        int checked = ev.getCheckedInCount();
        int remaining = Math.max(0, limit - checked);

        lblTotalLimit.setText(String.valueOf(limit));
        lblCheckedIn.setText(String.valueOf(checked));
        lblRemaining.setText(String.valueOf(remaining));

        if ("QR_ONLY".equalsIgnoreCase(ev.getGuestMode())) {
            lblEntryMode.setText("Single Event QR");
            if (btnSendPasses != null) btnSendPasses.setText("📧  Send Event QR");
        } else {
            lblEntryMode.setText("Individual Guest List");
            if (btnSendPasses != null) btnSendPasses.setText("📧  Send QR Passes to All");
        }

        double rate = limit > 0 ? ((double) checked / limit) : 0.0;
        lblEntryPercent.setText(String.format("%.0f%%", rate * 100));
        lblEntryCount.setText(checked + " / " + limit + " Entries");
        pbEntryRate.setProgress(rate);

        currentGuestList.clear();
        if (ev.getGuestList() != null) {
            currentGuestList.addAll(ev.getGuestList());
        }
        filterGuestList(filterInput != null ? filterInput.getText() : "");
    }

    private void filterGuestList(String query) {
        filteredGuestList.clear();
        if (query == null || query.isBlank()) {
            filteredGuestList.addAll(currentGuestList);
        } else {
            String lower = query.toLowerCase();
            for (Guest g : currentGuestList) {
                if ((g.getName() != null && g.getName().toLowerCase().contains(lower)) ||
                    (g.getMobile() != null && g.getMobile().contains(lower)) ||
                    (g.getEmail() != null && g.getEmail().toLowerCase().contains(lower))) {
                    filteredGuestList.add(g);
                }
            }
        }
        renderRosterRows();
    }

    // ==========================================
    // CHECK-IN & SCANNER LOGIC
    // ==========================================
    private void handleVerifyScan() {
        String input = txtScannerInput.getText().trim();
        if (input.isEmpty() || currentEvent == null) return;
        txtScannerInput.clear();

        if ("QR_ONLY".equalsIgnoreCase(currentEvent.getGuestMode())) {
            String[] parts = input.split(":");
            String token = parts.length >= 3 ? parts[2] : input;

            new Thread(() -> {
                try {
                    GuestService.CheckInResult result = guestService.processQREntryCheckIn(currentEvent.getEventId(), token);
                    Platform.runLater(() -> {
                        if (result.isSuccess()) {
                            // Update local and firestore checkedIn count
                            int newChecked = result.getCurrentCheckedIn();
                            currentEvent.setCheckedInCount(newChecked);

                            lblScanResult.setText("Entry Allowed ✓ (" + newChecked + " / " + currentEvent.getGuestLimit() + ")");
                            lblScanResult.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
                        } else {
                            lblScanResult.setText("Entry Denied ✕ " + result.getMessage());
                            lblScanResult.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> lblScanResult.setText("Error: " + e.getMessage()));
                }
            }).start();

        } else {
            Guest target = null;
            for (Guest g : currentGuestList) {
                if (g.getId().equalsIgnoreCase(input) || 
                    input.endsWith(g.getId()) || 
                    (g.getMobile() != null && g.getMobile().equalsIgnoreCase(input)) ||
                    (g.getEmail() != null && g.getEmail().equalsIgnoreCase(input))) {
                    target = g;
                    break;
                }
            }

            if (target != null) {
                performManualCheckIn(target);
            } else {
                lblScanResult.setText("Entry Denied ✕ Guest not found on list.");
                lblScanResult.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");
            }
        }
    }

    private void performManualCheckIn(Guest guest) {
        if (currentEvent == null) return;

        new Thread(() -> {
            try {
                GuestService.CheckInResult result = guestService.checkInManualGuest(currentEvent.getEventId(), guest.getId());
                Platform.runLater(() -> {
                    if (result.isSuccess()) {
                        guest.setCheckedIn(true);
                        guest.setCheckInTime(System.currentTimeMillis());

                        // ⭐ Sync checkedInCount directly with Event Document in Firestore
                        int newCheckedCount = currentEvent.getCheckedInCount() + 1;
                        currentEvent.setCheckedInCount(newCheckedCount);

                        new Thread(() -> {
                            try {
                                FirestoreClient.getFirestore().collection("events")
                                        .document(currentEvent.getEventId())
                                        .update("checkedInCount", newCheckedCount);
                            } catch (Exception ignored) {}
                        }).start();

                        renderRosterRows();
                        lblScanResult.setText("Checked In: " + guest.getName() + " ✓");
                        lblScanResult.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
                    } else {
                        lblScanResult.setText("Denied: " + result.getMessage());
                        lblScanResult.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblScanResult.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    private void handleSendSingleQrPass(Guest guest) {
        if (guest.getEmail() == null || guest.getEmail().isBlank()) {
            showInAppModal("Missing Contact", "This guest does not have a registered email address.", false);
            return;
        }

        new Thread(() -> {
            String payload = QRCodeUtil.formatPayload(currentEvent.getEventId(), guest.getId());
            boolean sent = EmailService.sendGuestQrPass(
                    guest.getEmail(),
                    guest.getName(),
                    currentEvent.getEventName(),
                    currentEvent.getEventDate(),
                    currentEvent.getCity(),
                    payload
            );
            Platform.runLater(() -> {
                if (sent) {
                    showInAppModal("Pass Dispatched", "QR Entry Pass sent successfully to " + guest.getEmail(), true);
                } else {
                    showInAppModal("Delivery Failed", "Failed to send QR pass email.", false);
                }
            });
        }).start();
    }

    private void handleSendQrPassesToAll() {
        if (currentEvent == null) return;

        // 1. SINGLE EVENT QR MODE -> Send to Customer's Email
        if ("QR_ONLY".equalsIgnoreCase(currentEvent.getGuestMode())) {
            String customerId = currentEvent.getCustomerId();

            new Thread(() -> {
                CustomerProfile profile = new CustomerProfileService().getCustomerProfile(customerId);
                String customerEmail = (profile != null && profile.getEmail() != null) ? profile.getEmail() : UserSession.getUserEmail();

                if (customerEmail == null || customerEmail.isBlank()) {
                    Platform.runLater(() -> showInAppModal("Missing Contact", "Customer email not found for this event.", false));
                    return;
                }

                String payload = QRCodeUtil.formatPayload(currentEvent.getEventId(), currentEvent.getQrToken());
                boolean sent = EmailService.sendGuestQrPass(
                        customerEmail,
                        (profile != null && profile.getName() != null) ? profile.getName() : "Event Host",
                        currentEvent.getEventName(),
                        currentEvent.getEventDate(),
                        currentEvent.getCity(),
                        payload
                );

                Platform.runLater(() -> {
                    if (sent) {
                        showInAppModal("Pass Dispatched", "Event QR Pass sent successfully to Customer (" + customerEmail + ")!", true);
                    } else {
                        showInAppModal("Delivery Failed", "Failed to send QR email.", false);
                    }
                });
            }).start();
            return;
        }

        // 2. MANUAL GUEST LIST MODE -> Send to each individual Guest
        if (currentGuestList.isEmpty()) {
            showInAppModal("No Attendees", "No guests found on list to send passes to.", false);
            return;
        }

        new Thread(() -> {
            int count = 0;
            for (Guest g : currentGuestList) {
                if (g.getEmail() != null && !g.getEmail().isBlank()) {
                    String payload = QRCodeUtil.formatPayload(currentEvent.getEventId(), g.getId());
                    EmailService.sendGuestQrPass(
                            g.getEmail(),
                            g.getName(),
                            currentEvent.getEventName(),
                            currentEvent.getEventDate(),
                            currentEvent.getCity(),
                            payload
                    );
                    count++;
                }
            }
            final int finalCount = count;
            Platform.runLater(() -> showInAppModal("Batch Dispatched", "QR Entry Passes sent successfully to " + finalCount + " guests!", true));
        }).start();
    }

    private void handleAddGuestDialog() {
        Dialog<Guest> dialog = new Dialog<>();
        dialog.setTitle("Add New Guest");
        dialog.setHeaderText("Add attendee to live event roster");
        if (LandingPage.landingPageStage != null) dialog.initOwner(LandingPage.landingPageStage);

        ButtonType addBtnType = new ButtonType("Add Guest", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField nameInput = new TextField(); nameInput.setPromptText("Full Name");
        TextField mobileInput = new TextField(); mobileInput.setPromptText("Mobile Number");
        TextField emailInput = new TextField(); emailInput.setPromptText("Email Address");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("VIP", "Speaker", "General", "Sponsor", "Family");
        typeCombo.setValue("General");

        grid.add(new Label("Full Name:"), 0, 0); grid.add(nameInput, 1, 0);
        grid.add(new Label("Mobile:"), 0, 1); grid.add(mobileInput, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailInput, 1, 2);
        grid.add(new Label("Guest Type:"), 0, 3); grid.add(typeCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                return new Guest(nameInput.getText().trim(), mobileInput.getText().trim(), emailInput.getText().trim(), typeCombo.getValue());
            }
            return null;
        });

        Optional<Guest> result = dialog.showAndWait();
        result.ifPresent(newGuest -> {
            if (currentEvent != null) {
                currentEvent.getGuestList().add(newGuest);
                int newLimit = currentEvent.getGuestList().size();
                currentEvent.setGuestLimit(newLimit);

                currentGuestList.add(newGuest);
                filterGuestList(filterInput != null ? filterInput.getText() : "");
                lblTotalLimit.setText(String.valueOf(newLimit));
                lblRemaining.setText(String.valueOf(Math.max(0, newLimit - currentEvent.getCheckedInCount())));

                new Thread(() -> {
                    try {
                        FirestoreClient.getFirestore().collection("events")
                                .document(currentEvent.getEventId())
                                .update("guestList", currentEvent.getGuestList(), "guestLimit", newLimit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                if (newGuest.getEmail() != null && !newGuest.getEmail().isBlank()) {
                    handleSendSingleQrPass(newGuest);
                }
            }
        });
    }

    private void detachListener() {
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
    }

    private void handleBack(Stage stage) {
        detachListener();
        if (backAction != null) {
            backAction.run();
        } else {
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        detachListener();
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }
}