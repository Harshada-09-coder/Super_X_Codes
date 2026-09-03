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

public class Payment {

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

    public Payment() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Payment(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getPaymentScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Payments"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createPaymentContent();
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
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), () -> {}),
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

    private VBox createPaymentContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(3);
        Label heading = new Label("Payments");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Track payments and due amounts");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        // 3 KPI Cards
        HBox metricsRow = new HBox(16);
        metricsRow.getChildren().addAll(
                createMetricCard("Total Payable", "₹5,00,000", ""),
                createMetricCard("Paid Amount", "₹2,80,000", "56%"),
                createMetricCard("Pending Amount", "₹2,20,000", "44%")
        );

        // Payment History Card
        VBox histCard = new VBox(12);
        histCard.setPadding(new Insets(18));
        histCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label histTitle = new Label("Payment History");
        histTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        VBox histRows = new VBox(6);
        histRows.setPadding(new Insets(6, 0, 0, 0));
        histRows.getChildren().addAll(
                createPaymentHistoryRow("PAY-001", "Advance Payment", "₹1,00,000", "Paid", "12 Aug 2026", true),
                createPaymentHistoryRow("PAY-002", "Second Payment", "₹1,80,000", "Paid", "20 Aug 2026", true),
                createPaymentHistoryRow("PAY-003", "Final Payment", "₹2,20,000", "Pending", "Due on 10 Dec 2026", false)
        );
        histCard.getChildren().addAll(histTitle, histRows);

        // Payment Information Card
        HBox infoCard = new HBox(16);
        infoCard.setAlignment(Pos.CENTER_LEFT);
        infoCard.setPadding(new Insets(14, 18, 14, 18));
        infoCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        VBox infoTexts = new VBox(2);
        Label infoHead = new Label("Payment Information");
        infoHead.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label infoDesc = new Label("Make sure to complete the pending payment before the event date.\nContact your organizer for any payment related queries.");
        infoDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        infoTexts.getChildren().addAll(infoHead, infoDesc);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        FontIcon cardIcon = new FontIcon("fas-credit-card");
        cardIcon.setIconSize(36);
        cardIcon.setIconColor(Color.web(PRIMARY_BLUE));

        infoCard.getChildren().addAll(infoTexts, sp, cardIcon);

        page.getChildren().addAll(backLink, titleBox, metricsRow, histCard, infoCard);
        return page;
    }

    private VBox createMetricCard(String title, String val, String percent) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        HBox valRow = new HBox(8);
        valRow.setAlignment(Pos.BASELINE_LEFT);
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        valRow.getChildren().add(v);

        if (!percent.isEmpty()) {
            Label p = new Label(percent);
            p.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
            valRow.getChildren().add(p);
        }

        card.getChildren().addAll(t, valRow);
        return card;
    }

    private HBox createPaymentHistoryRow(String id, String label, String amount, String status, String date, boolean isPaid) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        FontIcon ico = new FontIcon("fas-receipt");
        ico.setIconSize(16);
        ico.setIconColor(Color.web(isPaid ? "#16A34A" : "#F59E0B"));

        VBox desc = new VBox(1);
        Label t = new Label(label + " (" + id + ")");
        t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label d = new Label(date);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
        desc.getChildren().addAll(t, d);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label amt = new Label(amount);
        amt.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Label st = new Label(status);
        st.setStyle(isPaid ? "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;"
                : "-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");

        Button actionBtn = new Button(isPaid ? "Receipt ⬇" : "Pay Now");
        actionBtn.setStyle(isPaid ? "-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-cursor: hand;"
                : "-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 4px; -fx-cursor: hand;");

        row.getChildren().addAll(ico, desc, sp, amt, st, actionBtn);
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
    private void goRequirement() {
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }
    private void goSettings() {
        SettingC s = new SettingC();
        LandingPage.landingPageStage.setScene(s.getSettingScene(this::handleBack));
    }
}