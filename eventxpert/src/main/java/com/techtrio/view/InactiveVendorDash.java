package com.techtrio.view;

import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;
import org.kordamp.ikonli.javafx.FontIcon;

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
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class InactiveVendorDash {
    private Scene inactiveVendorScene;
    private final String uid;
    private final String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String SECONDARY_TEXT = "#64748B";

    private Label vendorBusinessNameLabel;
    private Label vendorCategoryLabel;

    public InactiveVendorDash(String uid, String idToken) {
        this.uid = uid;
        this.idToken = idToken;
    }

    public Scene getinactiveVendorScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Sidebar
        root.setLeft(createVendorSidebar());

        // Main Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Welcome Header
        Label welcomeTitle = new Label("Welcome Back, Partner 👋");
        welcomeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        welcomeTitle.setTextFill(Color.web(NAVY));

        Label badge = new Label("● PENDING VERIFICATION");
        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 12px;");

        HBox titleRow = new HBox(10, welcomeTitle, badge);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label sub = new Label("Your vendor account is pending profile activation. Complete your business information to receive customer service bookings.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox titleBox = new VBox(4, titleRow, sub);

        // Activation Card
        VBox activateCard = new VBox(14);
        activateCard.setPadding(new Insets(20));
        activateCard.setStyle("-fx-background-color: white; -fx-border-color: #FCD34D; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topH = new HBox(14);
        topH.setAlignment(Pos.CENTER_LEFT);

        StackPane warnCircle = new StackPane();
        Circle c = new Circle(22, Color.web("#FEF3C7"));
        Label wIcon = new Label("⚡");
        wIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: #D97706;");
        warnCircle.getChildren().addAll(c, wIcon);

        VBox info = new VBox(3);
        Label h = new Label("Complete Your Vendor Portfolio");
        h.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        h.setTextFill(Color.web(NAVY));
        Label desc = new Label("List your pricing, service categories and operational areas to be visible in the Organizer Find Vendors directory.");
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
        info.getChildren().addAll(h, desc);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button actBtn = new Button("Activate Vendor Profile →");
        actBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 9 18; -fx-background-radius: 6px; -fx-cursor: hand;");
        actBtn.setOnAction(e -> openProfileView());

        topH.getChildren().addAll(warnCircle, info, sp, actBtn);
        activateCard.getChildren().add(topH);

        // KPI Summary Cards
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
                createCard("Active Bookings", "00", "fas-calendar-check", "#16A34A"),
                createCard("Service Requests", "00", "fas-inbox", "#D97706"),
                createCard("Scheduled Events", "00", "fas-calendar-alt", PRIMARY_BLUE),
                createCard("Total Released Revenue", "₹0.00", "fas-wallet", NAVY)
        );

        content.getChildren().addAll(titleBox, activateCard, stats);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        root.setCenter(scroll);

        loadVendorDataAsync();

        inactiveVendorScene = new Scene(root, 1550, 785);
        return inactiveVendorScene;
    }

    private VBox createCard(String title, String val, String iconLit, String iconColorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER_COLOR + ";-fx-border-radius:10;-fx-background-radius:10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = new StackPane();
        Circle c = new Circle(14, Color.web(iconColorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(13);
        fontIcon.setIconColor(Color.web(iconColorHex));
        iconCircle.getChildren().addAll(c, fontIcon);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #64748B;");
        top.getChildren().addAll(iconCircle, titleLabel);

        Label valueLabel = new Label(val);
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: " + NAVY + ";");

        card.getChildren().addAll(top, valueLabel);
        return card;
    }

    private VBox createVendorSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-truck");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        vendorBusinessNameLabel = new Label("EventXpert");
        vendorBusinessNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        vendorCategoryLabel = new Label("VENDOR PORTAL (INACTIVE)");
        vendorCategoryLabel.setStyle("-fx-text-fill: #FDE68A; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(vendorBusinessNameLabel, vendorCategoryLabel);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        menu.getChildren().addAll(
                createSideBtn("Dashboard", "fas-tachometer-alt", true, this::backToInactVendorDashboard),
                createSideBtn("Service Requests", "fas-inbox", false, this::openServiceRequests),
                createSideBtn("My Bookings", "fas-calendar-check", false, this::openMyBookings),
                createSideBtn("Services & Packages", "fas-boxes", false, this::openServicesPackages),
                createSideBtn("Calendar", "fas-calendar-alt", false, this::openCalenderV),
                createSideBtn("Earnings & Payments", "fas-wallet", false, this::openEarningsPaymentsV)
        );

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        Button actBtn = new Button("⚡ Activate Portfolio");
        actBtn.setMaxWidth(Double.MAX_VALUE);
        actBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9; -fx-background-radius: 6; -fx-cursor: hand;");
        actBtn.setOnAction(e -> openProfileView());

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                actBtn,
                new Region(),
                createSideBtn("Notifications", "fas-bell", false, this::openNotificationsV),
                createSideBtn("Profile", "fas-user", false, this::openProfileView),
                createSideBtn("Settings", "fas-cog", false, this::openSettingsV),
                createSideBtn("Support", "fas-question-circle", false, this::openSupportV)
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSideBtn(String name, String iconLit, boolean active, Runnable action) {
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
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void loadVendorDataAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<VendorProfile> task = new Task<>() {
            @Override
            protected VendorProfile call() {
                VendorProfileService service = new VendorProfileService();
                return service.getVendorProfile(uid);
            }
        };

        task.setOnSucceeded(e -> {
            VendorProfile profileV = task.getValue();
            if (profileV != null) {
                Platform.runLater(() -> {
                    if (vendorBusinessNameLabel != null) {
                        String bName = profileV.getBusinessName();
                        if (bName == null || bName.isBlank()) bName = profileV.getFullName();
                        if (bName == null || bName.isBlank()) bName = "Vendor";
                        vendorBusinessNameLabel.setText(bName);
                    }
                    if (vendorCategoryLabel != null && profileV.getCategory() != null) {
                        vendorCategoryLabel.setText(profileV.getCategory().toUpperCase());
                    }
                });
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void openProfileView() {
        ProfileV profilePage = new ProfileV(uid, idToken);
        profilePage.setInactiveVendorDash(this);
        LandingPage.landingPageStage.setScene(profilePage.getprofileVScene());
    }

    private void openServiceRequests() {
        ServiceRequestsV serviceRequestsV = new ServiceRequestsV();
        LandingPage.landingPageStage.setScene(serviceRequestsV.getserviceRequestVScene());
    }

    private void openMyBookings() {
        MyBookingsV myBookingsV = new MyBookingsV();
        LandingPage.landingPageStage.setScene(myBookingsV.getmyBookingScene());
    }

    private void openServicesPackages() {
        ServicesPackagesV servicesPackagesV = new ServicesPackagesV();
        LandingPage.landingPageStage.setScene(servicesPackagesV.getservicesPackagesVScene(null, null));
    }

    private void openCalenderV() {
        CalendarV calendarV = new CalendarV();
        LandingPage.landingPageStage.setScene(calendarV.getcalendarVScene(null, null));
    }

    private void openEarningsPaymentsV() {
        EarningsPaymentsV earningsPaymentsV = new EarningsPaymentsV();
        LandingPage.landingPageStage.setScene(earningsPaymentsV.getearningsPaymentsScene(null));
    }

    private void openNotificationsV() {
        NotificationsV notificationsV = new NotificationsV();
        LandingPage.landingPageStage.setScene(notificationsV.getnotificationsScene(null));
    }

    private void openSettingsV() {
        SettingsV settingsV = new SettingsV();
        LandingPage.landingPageStage.setScene(settingsV.getsettingsVScene(null));
    }

    private void openSupportV() {
        SupportV supportV = new SupportV();
        LandingPage.landingPageStage.setScene(supportV.getsupportVScene(null));
    }

    public void backToInactVendorDashboard() {
        if (inactiveVendorScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(inactiveVendorScene);
        }
    }
}