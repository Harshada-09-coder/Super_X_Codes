package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProfileV {

    private Scene profileVScene;
    private VendorDashboardV vendorDashboard;
    private InactiveVendorDash inactVendorDashboard;
    private final String vendorId;
    private String idToken;

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

    // Dynamic State Labels
    private Label busNameLabel;
    private Label busCategoryLabel;
    private Label busLocationLabel;
    private Label busPhoneLabel;
    private Label busEmailLabel;
    private Label busBioLabel;
    private Label businessNameInfoLabel;
    private Label businessTypeInfoLabel;
    private Label vendorIdInfoLabel;
    private Label primaryLocationInfoLabel;
    private Label availabilityInfoLabel;

    public ProfileV(String vendorId) {
        this(vendorId, null);
    }

    public ProfileV(String vendorId, String idToken) {
        this.vendorId = vendorId;
        this.idToken = idToken;
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public void setInactiveVendorDash(InactiveVendorDash inactVendorDashboard) {
        this.inactVendorDashboard = inactVendorDashboard;
    }

    public Scene getprofileVScene() {
        Stage primaryStage = LandingPage.landingPageStage;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Main Application Container
        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        HBox topBar = createTopBar(primaryStage);
        mainArea.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createHeroProfileCard(primaryStage),
                createVerificationBadgesRow(),
                createProfileStatsRow(),
                createTwoColumnMainLayout(primaryStage),
                createFooter()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);
        root.setCenter(mainArea);

        if (vendorId != null && !vendorId.isBlank()) {
            loadVendorProfile(vendorId);
        }

        profileVScene = new Scene(root, 1550, 785);
        return profileVScene;
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

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

        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);
        sidebar.getChildren().add(createSidebarCategory("OVERVIEW"));

        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> navigateBackToDashboard()));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(stage, "Opening Calendar...")));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Earnings...")));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));
        sidebar.getChildren().add(createSidebarItem("👤 Profile", true, e -> {}));
        sidebar.getChildren().add(createSidebarItem("⚙ Settings", false, e -> showToast(stage, "Opening Settings...")));
        sidebar.getChildren().add(createSidebarItem("❓ Support", false, e -> showToast(stage, "Opening Support...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profilePill = new HBox(10);
        profilePill.setAlignment(Pos.CENTER_LEFT);
        profilePill.setPadding(new Insets(10));
        profilePill.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(WHITE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Vendor Profile");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("● Session Active");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);
        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
    }

    private void navigateBackToDashboard() {
        if (vendorDashboard != null) {
            vendorDashboard.backToVendorDashboard();
        } else if (inactVendorDashboard != null) {
            inactVendorDashboard.backToInactVendorDashboard();
        } else if (vendorId != null && !vendorId.isBlank()) {
            InactiveVendorDash dash = new InactiveVendorDash(vendorId, idToken);
            LandingPage.landingPageStage.setScene(dash.getinactiveVendorScene());
        }
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

    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search profile...");
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

    private VBox createPageHeader(Stage stage) {
        VBox header = new VBox(6);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button previewBtn = createSecondaryButton("👁 Preview Profile");
        Button editBtn = createPrimaryButton("Edit Profile");
        editBtn.setOnAction(e -> openEditProfileModal(stage));

        titleRow.getChildren().addAll(title, spacer, previewBtn, editBtn);

        Label subtitle = new Label("Manage your professional vendor profile and business information.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    private VBox createHeroProfileCard(Stage stage) {
        VBox card = createCardContainer();
        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(40, Color.web(LIGHT_BLUE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 22px;");
        StackPane avatarPane = new StackPane(avatarCircle, avatarText);

        VBox infoBox = new VBox(4);
        busNameLabel = new Label("Loading Vendor...");
        busNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        busCategoryLabel = new Label("Loading Category...");
        busCategoryLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox metaRow = new HBox(12);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        busLocationLabel = new Label("📍 Location loading...");
        busLocationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label verifiedBadge = new Label("✓ Verified Vendor");
        verifiedBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 10px;");

        metaRow.getChildren().addAll(busLocationLabel, verifiedBadge);
        infoBox.getChildren().addAll(busNameLabel, busCategoryLabel, metaRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox completionBox = new VBox(6);
        completionBox.setPadding(new Insets(12));
        completionBox.setPrefWidth(220);
        completionBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        HBox compHead = new HBox();
        Label compTitle = new Label("Profile Completion"); compTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region compSp = new Region(); HBox.setHgrow(compSp, Priority.ALWAYS);
        Label compVal = new Label("100%"); compVal.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        compHead.getChildren().addAll(compTitle, compSp, compVal);

        ProgressBar pb = new ProgressBar(1.0);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        completionBox.getChildren().addAll(compHead, pb);
        heroRow.getChildren().addAll(avatarPane, infoBox, spacer, completionBox);
        card.getChildren().add(heroRow);
        return card;
    }

    private HBox createVerificationBadgesRow() {
        HBox row = new HBox(12);
        row.getChildren().addAll(
                createBadgePill("✓ Business Verified"),
                createBadgePill("✓ Contact Verified"),
                createBadgePill("✓ Identity Verified"),
                createBadgePill("✓ Payment Verified")
        );
        return row;
    }

    private Label createBadgePill(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;");
        return l;
    }

    private HBox createProfileStatsRow() {
        HBox row = new HBox(14);
        row.getChildren().addAll(
                createStatCard("Total Bookings", "0", "Completed bookings", PRIMARY_BLUE),
                createStatCard("Services Offered", "1+", "Active services", BRIGHT_BLUE),
                createStatCard("Reviews & Rating", "5.0 ★", "New Vendor", GREEN_TEXT),
                createStatCard("Industry Experience", "Active", "Verified", SIDEBAR_NAVY)
        );
        return row;
    }

    private VBox createStatCard(String title, String val, String sub, String colorHex) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(t, v, s);
        return card;
    }

    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createBusinessInformationCard(stage),
                createAboutBusinessCard(stage),
                createServicesOfferedCard(stage)
        );

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createProfileStatusCard(),
                createVerificationStatusCard()
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createBusinessInformationCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("Business Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label editLink = new Label("✏ Edit");
        editLink.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        editLink.setOnMouseClicked(e -> openEditProfileModal(stage));
        head.getChildren().addAll(title, sp, editLink);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);

        businessNameInfoLabel = createValueLabel("Loading...");
        businessTypeInfoLabel = createValueLabel("Loading...");
        vendorIdInfoLabel = createValueLabel("Loading...");
        primaryLocationInfoLabel = createValueLabel("Loading...");
        availabilityInfoLabel = createValueLabel("Loading...");

        grid.add(createSpecItem("BUSINESS NAME", businessNameInfoLabel), 0, 0);
        grid.add(createSpecItem("BUSINESS TYPE", businessTypeInfoLabel), 1, 0);
        grid.add(createSpecItem("VENDOR ID", vendorIdInfoLabel), 0, 1);
        grid.add(createSpecItem("PRIMARY LOCATION", primaryLocationInfoLabel), 0, 2);
        grid.add(createSpecItem("AVAILABILITY", availabilityInfoLabel), 1, 2);

        Label contactHead = new Label("Contact Details");
        contactHead.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox contactGrid = new HBox(20);
        busEmailLabel = new Label("✉ Loading..."); busEmailLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        busPhoneLabel = new Label("📞 Loading..."); busPhoneLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        contactGrid.getChildren().addAll(busEmailLabel, busPhoneLabel);

        card.getChildren().addAll(head, new Separator(), grid, new Separator(), contactHead, contactGrid);
        return card;
    }

    private Label createValueLabel(String value) {
        Label label = new Label(value);
        label.setFont(Font.font("Arial", 14));
        label.setTextFill(Color.web("#15171B"));
        label.setWrapText(true);
        return label;
    }

    private VBox createAboutBusinessCard(Stage stage) {
        VBox card = createCardContainer();
        HBox head = new HBox();
        Label title = new Label("About Business");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        head.getChildren().addAll(title, sp);

        busBioLabel = new Label("Vendor bio and information loaded from Firestore profile.");
        busBioLabel.setWrapText(true);
        busBioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(head, new Separator(), busBioLabel);
        return card;
    }

    private VBox createServicesOfferedCard(Stage stage) {
        VBox card = createCardContainer();
        HBox head = new HBox();
        Label title = new Label("Services Offered");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        head.getChildren().add(title);

        FlowPane tags = new FlowPane();
        tags.setHgap(8); tags.setVgap(8);

        String[] officialServices = {
                "Decoration", "Sound System", "Photography & Videography",
                "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services"
        };

        for (String s : officialServices) {
            Label tag = new Label(s);
            tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 12px;");
            tags.getChildren().add(tag);
        }

        card.getChildren().addAll(head, new Separator(), tags);
        return card;
    }

    private VBox createSpecItem(String label, Label valueLabel) {
        VBox box = new VBox(2);
        Label title = new Label(label);
        title.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().addAll(title, valueLabel);
        return box;
    }

    private VBox createProfileStatusCard() {
        VBox card = createCardContainer();
        Label title = new Label("Profile Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox r1 = new HBox();
        VBox t1 = new VBox(2, new Label("Public Visibility"), new Label("Profile is active"));
        t1.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        t1.getChildren().get(1).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        CheckBox tog1 = new CheckBox(); tog1.setSelected(true);
        r1.getChildren().addAll(t1, sp1, tog1);

        card.getChildren().addAll(title, new Separator(), r1);
        return card;
    }

    private VBox createVerificationStatusCard() {
        VBox card = createCardContainer();
        Label title = new Label("Verification Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createVerificationRow("Business Registration", "Verified", true),
                createVerificationRow("Contact Information", "Phone & Email verified", true)
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private HBox createVerificationRow(String title, String desc, boolean isDone) {
        HBox r = new HBox(8);
        Label icon = new Label(isDone ? "✓" : "⏳");
        icon.setStyle("-fx-text-fill: " + (isDone ? GREEN_TEXT : WARNING_TEXT) + "; -fx-font-weight: bold;");

        VBox t = new VBox(1);
        Label head = new Label(title); head.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label sub = new Label(desc); sub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        t.getChildren().addAll(head, sub);

        r.getChildren().addAll(icon, t);
        return r;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 0, 10, 0));

        Label copy = new Label("© 2026 EventXpert Management System. All rights reserved.");
        copy.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        footer.getChildren().add(copy);
        return footer;
    }

    private void openEditProfileModal(Stage parentStage) {
        if (vendorId == null || vendorId.isBlank()) {
            showToast(parentStage, "User session not found. Cannot edit profile.");
            return;
        }

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Edit Vendor Profile");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("✏ Edit Business Profile");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        String currentBusName = busNameLabel != null ? busNameLabel.getText() : "";
        String currentPhone = busPhoneLabel != null ? busPhoneLabel.getText().replace("📞 ", "") : "";
        String currentEmail = busEmailLabel != null ? busEmailLabel.getText().replace("✉ ", "") : "";
        String currentBio = busBioLabel != null ? busBioLabel.getText() : "";

        TextField nameIn = new TextField(currentBusName);
        nameIn.setPromptText("Enter Business Name");

        TextField phoneIn = new TextField(currentPhone);
        phoneIn.setPromptText("Enter Phone Number");

        TextField emailIn = new TextField(currentEmail);
        emailIn.setPromptText("Enter Email Address");

        TextArea bioIn = new TextArea(currentBio);
        bioIn.setPromptText("Enter About Business / Bio");
        bioIn.setPrefRowCount(3);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button saveBtn = createPrimaryButton("Save Changes");

        saveBtn.setOnAction(e -> {
            String newBusName = nameIn.getText().trim();
            String newPhone = phoneIn.getText().trim();
            String newEmail = emailIn.getText().trim();
            String newBio = bioIn.getText().trim();

            // Validation
            if (newBusName.isEmpty()) {
                showModalError(errorLabel, "Business name cannot be empty.");
                return;
            }
            if (newPhone.isEmpty()) {
                showModalError(errorLabel, "Phone number cannot be empty.");
                return;
            }
            if (newEmail.isEmpty() || !newEmail.contains("@") || !newEmail.contains(".")) {
                showModalError(errorLabel, "Please enter a valid email address.");
                return;
            }

            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            saveBtn.setDisable(true);
            saveBtn.setText("Saving...");

            Map<String, Object> updates = new HashMap<>();
            updates.put("businessName", newBusName);
            updates.put("phone", newPhone);
            updates.put("email", newEmail);
            updates.put("bio", newBio);

            Task<Boolean> updateTask = new Task<>() {
                @Override
                protected Boolean call() {
                    VendorProfileService service = new VendorProfileService();
                    return service.updateVendorProfileFields(vendorId, updates);
                }
            };

            updateTask.setOnSucceeded(evt -> {
                boolean success = updateTask.getValue();
                saveBtn.setDisable(false);
                saveBtn.setText("Save Changes");

                if (success) {
                    // Update JavaFX UI Controls
                    Platform.runLater(() -> {
                        if (busNameLabel != null) busNameLabel.setText(newBusName);
                        if (busPhoneLabel != null) busPhoneLabel.setText("📞 " + newPhone);
                        if (busEmailLabel != null) busEmailLabel.setText("✉ " + newEmail);
                        if (busBioLabel != null) busBioLabel.setText(newBio);
                        if (businessNameInfoLabel != null) businessNameInfoLabel.setText(newBusName);
                    });

                    showToast(parentStage, "Profile updated successfully!");
                    modal.close();
                } else {
                    showModalError(errorLabel, "Unable to update vendor profile. Please try again.");
                }
            });

            updateTask.setOnFailed(evt -> {
                Throwable ex = updateTask.getException();
                if (ex != null) ex.printStackTrace();
                saveBtn.setDisable(false);
                saveBtn.setText("Save Changes");
                showModalError(errorLabel, "Unable to update vendor profile. Please try again.");
            });

            Thread thread = new Thread(updateTask);
            thread.setDaemon(true);
            thread.start();
        });

        layout.getChildren().addAll(
                title,
                new Label("Business Name:"), nameIn,
                new Label("Phone:"), phoneIn,
                new Label("Email:"), emailIn,
                new Label("About Business:"), bioIn,
                errorLabel,
                saveBtn
        );

        Scene scene = new Scene(layout, 380, 420);
        modal.setScene(scene);
        modal.show();
    }

    private void showModalError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

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
        if (stage != null) alert.initOwner(stage);
        alert.show();
    }

    public Scene getVendorProfileScene() {
        return getprofileVScene();
    }

    private void updateProfileUI(VendorProfile vendor) {
        if (vendor == null) return;

        Platform.runLater(() -> {
            String bName = safeText(vendor.getBusinessName(), safeText(vendor.getFullName(), safeText(vendor.getName(), "Vendor Name")));
            String cat = safeText(vendor.getCategory(), "Category Not Set");
            String addr = safeText(vendor.getAddress(), "Location not available");
            String ph = safeText(vendor.getPhone(), safeText(vendor.getMobileNo(), "Phone not available"));
            String em = safeText(vendor.getEmail(), "Email not available");
            String avail = safeText(vendor.getAvailability(), "Available");
            String vId = safeText(vendor.getVendorId(), vendorId);
            String bio = safeText(vendor.getBio(), bName + " provides high-quality " + cat.toLowerCase() + " services for events across " + addr + ". Registered as an active EventXpert vendor.");

            busNameLabel.setText(bName);
            busCategoryLabel.setText(cat);
            busLocationLabel.setText("📍 " + addr);
            busPhoneLabel.setText("📞 " + ph);
            busEmailLabel.setText("✉ " + em);

            if (businessNameInfoLabel != null) businessNameInfoLabel.setText(bName);
            if (businessTypeInfoLabel != null) businessTypeInfoLabel.setText(cat);
            if (vendorIdInfoLabel != null) vendorIdInfoLabel.setText(vId);
            if (primaryLocationInfoLabel != null) primaryLocationInfoLabel.setText(addr);
            if (availabilityInfoLabel != null) availabilityInfoLabel.setText(avail);

            if (busBioLabel != null) {
                busBioLabel.setText(bio);
            }
        });
    }

    public void loadVendorProfile(String vendorId) {
        if (vendorId == null || vendorId.trim().isEmpty()) {
            return;
        }

        Task<VendorProfile> task = new Task<>() {
            @Override
            protected VendorProfile call() {
                VendorProfileService service = new VendorProfileService();
                return service.getVendorProfile(vendorId);
            }
        };

        task.setOnSucceeded(e -> {
            VendorProfile vendor = task.getValue();
            if (vendor != null) {
                updateProfileUI(vendor);
            }
        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            if (error != null) error.printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String safeText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}