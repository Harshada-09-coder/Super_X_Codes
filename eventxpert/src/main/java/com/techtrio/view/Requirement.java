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

public class Requirement {

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

    public Requirement() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Requirement(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getRequirementScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Requirements"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createRequirementContent();
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
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), () -> {})
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

    private VBox createRequirementContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(2);
        Label heading = new Label("Requirements");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Manage all your event requirements and services");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        // 4 KPI Summary Cards
        HBox kpiRow = new HBox(16);
        kpiRow.getChildren().addAll(
                createReqKpiPill("8", "Requested", "fas-lightbulb", "#8B5CF6"),
                createReqKpiPill("5", "Assigned", "fas-user-check", "#0284C7"),
                createReqKpiPill("3", "In Progress", "fas-clock", "#F59E0B"),
                createReqKpiPill("0", "Completed", "fas-check-circle", "#16A34A")
        );

        // Requested Services Table + Donut Summary Row
        HBox bodyRow = new HBox(20);

        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        Label tblTitle = new Label("Requested Services");
        tblTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        VBox rows = new VBox(6);
        rows.setPadding(new Insets(8, 0, 0, 0));

        HBox tHead = new HBox(10);
        tHead.setPadding(new Insets(6, 12, 6, 12));
        tHead.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px;");
        tHead.getChildren().addAll(
                createTableCell("Service", 160, true),
                createTableCell("Status", 110, true),
                createTableCell("Assigned To", 160, true),
                createTableCell("Priority", 90, true),
                createTableCell("Estimated Cost", 120, true)
        );

        rows.getChildren().addAll(
                tHead,
                createServiceRow("Catering", "Assigned", "Taste Buds Catering", "High", "₹1,75,000", "fas-utensils"),
                createServiceRow("Decoration", "In Progress", "Dream Decor", "High", "₹75,000", "fas-paint-brush"),
                createServiceRow("Photography", "Assigned", "Click Moments", "Medium", "₹50,000", "fas-camera"),
                createServiceRow("Sound System", "In Progress", "Beat Sound & Lights", "Medium", "₹25,000", "fas-volume-up"),
                createServiceRow("Seating Arrangement", "Requested", "—", "Low", "₹20,000", "fas-chair"),
                createServiceRow("Lighting", "Requested", "—", "Low", "₹15,000", "fas-lightbulb"),
                createServiceRow("Security", "Assigned", "Shield Security", "Medium", "₹20,000", "fas-shield-alt")
        );
        tableCard.getChildren().addAll(tblTitle, rows);

        // Right Donut Overview Card
        VBox donutCard = new VBox(12);
        donutCard.setPadding(new Insets(16));
        donutCard.setPrefWidth(380);
        donutCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label donutTitle = new Label("Requirement Status Overview");
        donutTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox donutBody = new HBox(16);
        donutBody.setAlignment(Pos.CENTER_LEFT);

        StackPane donutHolder = new StackPane();
        Circle outC = new Circle(44, Color.web("#EFF6FF"));
        Circle inC = new Circle(30, Color.WHITE);
        VBox centerTxt = new VBox(0);
        centerTxt.setAlignment(Pos.CENTER);
        Label numL = new Label("16");
        numL.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label totL = new Label("Total");
        totL.setStyle("-fx-font-size: 9px; -fx-text-fill: " + TEXT_MUTED + ";");
        centerTxt.getChildren().addAll(numL, totL);
        donutHolder.getChildren().addAll(outC, inC, centerTxt);

        VBox legend = new VBox(6);
        legend.getChildren().addAll(
                createDonutLegendItem("Requested", "8 (50%)", "#8B5CF6"),
                createDonutLegendItem("Assigned", "5 (31%)", "#0284C7"),
                createDonutLegendItem("In Progress", "3 (19%)", "#F59E0B"),
                createDonutLegendItem("Completed", "0 (0%)", "#16A34A")
        );

        donutBody.getChildren().addAll(donutHolder, legend);
        donutCard.getChildren().addAll(donutTitle, donutBody);

        bodyRow.getChildren().addAll(tableCard, donutCard);

        page.getChildren().addAll(backLink, titleBox, kpiRow, bodyRow);
        return page;
    }

    private HBox createReqKpiPill(String count, String label, String iconLit, String colorHex) {
        HBox pill = new HBox(12);
        pill.setAlignment(Pos.CENTER_LEFT);
        pill.setPadding(new Insets(14));
        pill.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(pill, Priority.ALWAYS);

        StackPane circle = new StackPane();
        Circle c = new Circle(18, Color.web(colorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(14);
        icon.setIconColor(Color.web(colorHex));
        circle.getChildren().addAll(c, icon);

        VBox texts = new VBox(1);
        Label cLbl = new Label(count);
        cLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label lLbl = new Label(label);
        lLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        texts.getChildren().addAll(cLbl, lLbl);

        pill.getChildren().addAll(circle, texts);
        return pill;
    }

    private Label createTableCell(String text, double width, boolean isHeader) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setStyle(isHeader ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";" : "-fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        return lbl;
    }

    private HBox createServiceRow(String service, String status, String assignedTo, String priority, String estCost, String iconLit) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        HBox svcBox = new HBox(8);
        svcBox.setPrefWidth(160);
        svcBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon ico = new FontIcon(iconLit);
        ico.setIconSize(13);
        ico.setIconColor(Color.web(PRIMARY_BLUE));
        Label svcLabel = new Label(service);
        svcLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        svcBox.getChildren().addAll(ico, svcLabel);

        Label st = new Label(status);
        st.setPrefWidth(110);
        st.setStyle("Assigned".equals(status) ? "-fx-text-fill: #0284C7; -fx-font-weight: bold; -fx-font-size: 11px;" : ("In Progress".equals(status) ? "-fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 11px;" : "-fx-text-fill: #8B5CF6; -fx-font-weight: bold; -fx-font-size: 11px;"));

        Label asg = createTableCell(assignedTo, 160, false);

        Label pri = new Label(priority);
        pri.setPrefWidth(90);
        pri.setStyle("High".equals(priority) ? "-fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-font-weight: bold;" : ("Medium".equals(priority) ? "-fx-text-fill: #D97706; -fx-font-size: 11px; -fx-font-weight: bold;" : "-fx-text-fill: #16A34A; -fx-font-size: 11px; -fx-font-weight: bold;"));

        Label cost = createTableCell(estCost, 120, false);
        cost.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");

        row.getChildren().addAll(svcBox, st, asg, pri, cost);
        return row;
    }

    private HBox createDonutLegendItem(String title, String val, String colorHex) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, Color.web(colorHex));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";");
        row.getChildren().addAll(dot, t, sp, v);
        return row;
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
    private void goVenue() {
        Venue view = new Venue(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
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
    private void goSettings() {
        SettingC s = new SettingC();
        LandingPage.landingPageStage.setScene(s.getSettingScene(this::handleBack));
    }
}