package com.techtrio.view;

import com.techtrio.controller.OrganizerController;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class NotificationsO {

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Alert Callout Colors
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";

    private final String uid;
    private final String idToken;

    public NotificationsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public NotificationsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getNotificationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 30, 30, 30));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Page Title Row
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Stay updated with your events, vendors, guests, schedules and important alerts.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button markReadBtn = createSecondaryButton("✓ Mark all as read");
        titleRow.getChildren().addAll(titleBox, headerSpacer, markReadBtn);

        // Filter Pills Row
        HBox filterPillsRow = createFilterPillsRow();

        // Main Content Split Grid
        HBox mainGrid = new HBox(20);

        VBox leftColumn = new VBox(12);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // Notification Cards List
        leftColumn.getChildren().addAll(
                createNotifCard("📋", "New Event Request", "TechVision Annual Summit submitted for review.", "10m ago", true, false, "Review Request", () -> {
                    EventRequestsO view = new EventRequestsO(uid, idToken);
                    LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(this::returnToDashboard));
                }),
                createNotifCard("⚙️", "AI Planner Optimization", "Resource overlap detected in the main hall schedule.", "32m ago", true, false, "View Recommendation", () -> {
                    AIEventPlannerO view = new AIEventPlannerO();
                    LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::returnToDashboard));
                }),
                createNotifCard("💳", "Budget Alert", "Catering for TechVision is 8% above estimate.", "1h ago", true, true, "Review Budget", () -> {
                    BudgetExpenseTrackerO view = new BudgetExpenseTrackerO();
                    LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::returnToDashboard));
                }),
                createNotifCard("⚠️", "Schedule Conflict", "AV Setup overlap detected in Room B.", "Today", true, false, "Resolve Conflict", () -> {
                    ScheduleO view = new ScheduleO();
                    LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::returnToDashboard));
                }),
                createNotifCard("📄", "Vendor Quote Received", "VisionPro AV has submitted their initial quote.", "2h ago", false, false, "View Quote", () -> {
                    VendorManagementO view = new VendorManagementO(uid, idToken);
                    LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(this::returnToDashboard));
                })
        );

        // Right Column Side Widgets
        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(330);
        rightColumn.setMinWidth(310);

        rightColumn.getChildren().addAll(
                createConflictDetailWidget(),
                createOverviewWidget()
        );

        mainGrid.getChildren().addAll(leftColumn, rightColumn);

        mainContent.getChildren().addAll(titleRow, filterPillsRow, mainGrid);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        return new Scene(root, 1550, 785);
    }

    private HBox createFilterPillsRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Button pAll = createPillButton("All", true);
        Button pUnread = createPillButton("Unread (7)", false);
        Button pRequests = createPillButton("Event Requests", false);
        Button pPlanning = createPillButton("Planning", false);
        Button pVendors = createPillButton("Vendors", false);
        Button pGuests = createPillButton("Guests", false);
        Button pAlerts = createPillButton("Alerts", false);

        row.getChildren().addAll(pAll, pUnread, pRequests, pPlanning, pVendors, pGuests, pAlerts);
        return row;
    }

    private Button createPillButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(active ? "-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 14; -fx-background-radius: 16px;"
                : "-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
        return btn;
    }

    private VBox createNotifCard(String iconStr, String title, String desc, String time, boolean isUnread, boolean isAlert, String actionText, Runnable action) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + (isAlert ? RED_BG : (isUnread ? LIGHT_BLUE : WHITE)) + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 16px;");

        VBox textGroup = new VBox(2);
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + (isAlert ? RED_TEXT : NAVY) + ";");
        Label d = new Label(desc);
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        textGroup.getChildren().addAll(t, d);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        topRow.getChildren().addAll(icon, textGroup, sp, timeLbl);

        Button linkBtn = new Button(actionText);
        linkBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 0; -fx-cursor: hand;");
        if (action != null) linkBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(topRow, linkBtn);
        return card;
    }

    private VBox createConflictDetailWidget() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("📅 Schedule Conflict Detected");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label sub = new Label("The system identified an overlap in physical resources for TechVision Annual Summit.");
        sub.setWrapText(true);
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox detailsBox = new VBox(4);
        detailsBox.setPadding(new Insets(10));
        detailsBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label room = new Label("📍 Grand Ballroom - Section A");
        room.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label v1 = new Label("VisionPro AV Setup: 1:00 PM - 3:00 PM");
        v1.setStyle("-fx-font-size: 10px; -fx-text-fill: " + RED_TEXT + ";");

        Label v2 = new Label("Catering Delivery: 2:30 PM - 3:30 PM");
        v2.setStyle("-fx-font-size: 10px; -fx-text-fill: " + RED_TEXT + ";");

        detailsBox.getChildren().addAll(room, v1, v2);

        Button adjustBtn = createPrimaryButton("Adjust Schedule");
        adjustBtn.setMaxWidth(Double.MAX_VALUE);
        adjustBtn.setOnAction(e -> {
            ScheduleO view = new ScheduleO();
            LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::returnToDashboard));
        });

        card.getChildren().addAll(title, sub, detailsBox, adjustBtn);
        return card;
    }

    private VBox createOverviewWidget() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Overview");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createMetricProgressRow("Unread", "7", 0.70, PRIMARY_BLUE),
                createMetricProgressRow("Action Required", "3", 0.30, RED_TEXT)
        );

        card.getChildren().addAll(title, rows);
        return card;
    }

    private VBox createMetricProgressRow(String label, String val, double progress, String colorHex) {
        VBox box = new VBox(4);

        HBox topRow = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        topRow.getChildren().addAll(l, sp, v);

        ProgressBar pb = new ProgressBar(progress);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + colorHex + "; -fx-pref-height: 6;");

        box.getChildren().addAll(topRow, pb);
        return box;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, this::returnToDashboard));
        menu.getChildren().add(createNavBtn("Notifications", true, () -> {}));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer);
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

    private HBox createTopBar() {
        HBox bar = new HBox();
        bar.setPrefHeight(65);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 30, 0, 30));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label l = new Label("Notification Center");
        l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        bar.getChildren().add(l);
        return bar;
    }

    private void returnToDashboard() {
        OrganizerController controller = new OrganizerController();
        OrganizerProfile profile = controller.getOrganizerProfile(uid);

        if (profile != null && profile.isProfileCompleted()) {
            OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
            LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
        } else {
            InactiveOrgDash inactiveDash = new InactiveOrgDash(uid, idToken);
            LandingPage.landingPageStage.setScene(inactiveDash.getinactiveOrganizerScene());
        }
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        return btn;
    }
}