package com.techtrio.view;

import com.techtrio.model.Event;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Venue {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    public Venue() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Venue(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getVenueScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Venue"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createVenueContent();
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        return new Scene(root, 1550, 785);
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(4, 8, 12, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(6, 8, 14, 8));

        Circle userAvatar = new Circle(16, Color.web("#DBEAFE"));
        VBox userDetails = new VBox(1);
        Label userName = new Label("Priya Sharma");
        userName.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label userEmail = new Label("priya.sharma@email.com");
        userEmail.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px;");
        userDetails.getChildren().addAll(userName, userEmail);
        userBox.getChildren().addAll(userAvatar, userDetails);

        VBox mainNav = new VBox(3);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 12 8 4 8;");

        VBox eventNav = new VBox(3);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), this::goOverview),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), () -> {}),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(3);
        bottomNav.getChildren().addAll(
                createNavItem("Support", "fas-life-ring", false, null),
                createNavItem("Settings", "fas-cog", false, this::goSettings)
        );

        sidebar.getChildren().addAll(brandBox, userBox, mainNav, sectionLabel, eventNav, spacer, bottomNav);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createVenueContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(3);
        Label heading = new Label("Venue");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Manage your venue details and status");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        HBox bodyRow = new HBox(20);

        // 1. Venue Status Card
        VBox statusCard = new VBox(12);
        statusCard.setPadding(new Insets(18));
        statusCard.setPrefWidth(260);
        statusCard.setAlignment(Pos.CENTER);
        statusCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label stHeader = new Label("Venue Status");
        stHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        StackPane checkCircle = new StackPane();
        Circle c = new Circle(28, Color.web("#DCFCE7"));
        FontIcon icon = new FontIcon("fas-check");
        icon.setIconSize(20);
        icon.setIconColor(Color.web("#16A34A"));
        checkCircle.getChildren().addAll(c, icon);

        Label confBadge = new Label("Confirmed");
        confBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

        Label confDate = new Label("Confirmed on\n15 Aug 2026\nby Aman Events");
        confDate.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-text-alignment: center;");

        statusCard.getChildren().addAll(stHeader, checkCircle, confBadge, confDate);

        // 2. Venue Details Card
        VBox detailsCard = new VBox(14);
        detailsCard.setPadding(new Insets(18));
        detailsCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(detailsCard, Priority.ALWAYS);

        Label detTitle = new Label("Venue Details");
        detTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox bannerRow = new HBox(16);
        bannerRow.setAlignment(Pos.CENTER_LEFT);

        StackPane imgPlaceholder = new StackPane();
        imgPlaceholder.setPrefSize(140, 90);
        imgPlaceholder.setStyle("-fx-background-color: #334155; -fx-background-radius: 8px;");
        FontIcon bIcon = new FontIcon("fas-hotel");
        bIcon.setIconSize(32);
        bIcon.setIconColor(Color.web("#94A3B8"));
        imgPlaceholder.getChildren().add(bIcon);

        VBox venueDesc = new VBox(4);
        Label vName = new Label("Royal Heritage Banquet");
        vName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label vAddr = new Label("Baner Road, Pune, Maharashtra 411045");
        vAddr.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
        Hyperlink mapLink = new Hyperlink("📍 View on Map");
        mapLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-padding: 0;");
        venueDesc.getChildren().addAll(vName, vAddr, mapLink);

        bannerRow.getChildren().addAll(imgPlaceholder, venueDesc);

        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(14);
        grid.setPadding(new Insets(10, 0, 0, 0));

        grid.add(createDetailItem("Venue Type", "Indoor"), 0, 0);
        grid.add(createDetailItem("Area", "Baner, Pune"), 1, 0);
        grid.add(createDetailItem("Capacity", "200 Guests"), 0, 1);
        grid.add(createDetailItem("Venue Cost", "₹1,20,000"), 1, 1);
        grid.add(createDetailItem("Selected Date", "25 Dec 2026"), 0, 2);
        grid.add(createDetailItem("Status", "Partially Paid"), 1, 2);

        detailsCard.getChildren().addAll(detTitle, bannerRow, grid);
        bodyRow.getChildren().addAll(statusCard, detailsCard);

        // Help footer banner
        HBox helpBanner = new HBox(12);
        helpBanner.setAlignment(Pos.CENTER_LEFT);
        helpBanner.setPadding(new Insets(12, 16, 12, 16));
        helpBanner.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #BFDBFE; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        FontIcon infoIco = new FontIcon("fas-info-circle");
        infoIco.setIconSize(16);
        infoIco.setIconColor(Color.web(PRIMARY_BLUE));

        Label helpTxt = new Label("Need to change venue?\nContact your organizer if you want to request a venue alteration.");
        helpTxt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button contactOrgBtn = new Button("Contact Organizer");
        contactOrgBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-border-radius: 6px; -fx-cursor: hand;");

        helpBanner.getChildren().addAll(infoIco, helpTxt, sp, contactOrgBtn);

        page.getChildren().addAll(backLink, titleBox, bodyRow, helpBanner);
        return page;
    }

    private VBox createDetailItem(String label, String value) {
        VBox box = new VBox(2);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        box.getChildren().addAll(l, v);
        return box;
    }

    private void handleBack() {
        if (backAction != null) backAction.run();
        else goOverview();
    }
    private void goDashboard() {
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }
    private void goCreateEvent() {
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }
    private void goOverview() {
        MyEventDetails view = new MyEventDetails(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getMyEventDetailsScene(this::handleBack));
    }
    private void goBudget() {
        Budget view = new Budget(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getBudgetScene(this::handleBack));
    }
    private void goGuest() {
        Guest view = new Guest(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getGuestScene(this::handleBack));
    }
    private void goPayment() {
        Payment view = new Payment(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getPaymentScene(this::handleBack));
    }
    private void goRequirement() {
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }
    private void goSettings() {
        SettingC s = new SettingC();
        LandingPage.landingPageStage.setScene(s.getSettingScene(this::handleBack));
    }
}