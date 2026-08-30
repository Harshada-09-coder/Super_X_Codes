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

import com.techtrio.session.UserSession;

public class ScheduleO {

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Conflict Alert Colors
    private static final String CONFLICT_BG = "#FEF2F2";
    private static final String CONFLICT_TEXT = "#991B1B";
    private static final String CONFLICT_BORDER = "#FCA5A5";

    private String uid;
    private String idToken;
    private Runnable backAction;

    private static class ScheduleItem {
        String time;
        String title;
        String location;
        String assigned;
        String status; // PENDING, CONFLICT, COMPLETED, UPCOMING
        boolean isConflict;

        ScheduleItem(String time, String title, String location, String assigned, String status, boolean isConflict) {
            this.time = time;
            this.title = title;
            this.location = location;
            this.assigned = assigned;
            this.status = status;
            this.isConflict = isConflict;
        }
    }

    private final List<ScheduleItem> scheduleItems = new ArrayList<>();
    private VBox timelineContainer;
    private VBox conflictAlertBanner;
    private Label conflictCountLabel;
    private int conflictCount = 1;

    public ScheduleO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ScheduleO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        initSampleSchedule();
    }

    private void initSampleSchedule() {
        if (!scheduleItems.isEmpty()) return;
        scheduleItems.add(new ScheduleItem("08:00 AM", "Venue Final Setup", "Main Hall", "Venue Team", "PENDING", false));
        scheduleItems.add(new ScheduleItem("08:30 AM", "AV & Sound Setup", "Main Hall", "VisionPro AV", "CONFLICT", true));
        scheduleItems.add(new ScheduleItem("09:00 AM", "Guest Registration & Welcome", "Main Lobby", "Registration Staff", "COMPLETED", false));
        scheduleItems.add(new ScheduleItem("10:00 AM", "Keynote Session: Tech Trends 2026", "Main Hall", "Rahul Sharma", "UPCOMING", false));
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

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 30, 30, 30));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(stage),
                createSummaryCardsRow(),
                createConflictAlertBanner(stage),
                createTimelineSection(stage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        return new Scene(root, 1550, 785);
    }

    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Schedule");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Event Schedule");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label tag = new Label("● TechVision Annual Summit • In Planning");
        tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button aiGenerateBtn = createSecondaryButton("✨ AI Generate Schedule");
        Button addActivityBtn = createPrimaryButton("+ Add Activity");

        Button backBtn = createSecondaryButton("← Back");
        backBtn.setOnAction(e -> handleBack(stage));

        titleRow.getChildren().addAll(title, tag, spacer, aiGenerateBtn, addActivityBtn, backBtn);

        Label subtitle = new Label("Plan the event timeline, coordinate activities and detect scheduling conflicts automatically.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    private HBox createSummaryCardsRow() {
        HBox row = new HBox(16);
        row.getChildren().addAll(
                createSummaryCard("Event Duration", "8 Hours", "09:00 AM - 05:00 PM", PRIMARY_BLUE, "⏰"),
                createSummaryCard("Activities", "12 Scheduled", "Across 3 locations", NAVY, "📋"),
                createConflictCardWidget(),
                createProgressCardWidget()
        );
        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String color, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox head = new HBox();
        Label t = new Label(title); t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr); head.getChildren().addAll(t, sp, ic);

        Label v = new Label(val); v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(head, v, s);
        return card;
    }

    private VBox createConflictCardWidget() {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + CONFLICT_BORDER + "; -fx-border-radius: 12px; -fx-border-width: 1.5;");

        HBox head = new HBox();
        Label t = new Label("Conflicts"); t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + CONFLICT_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label("⚠️"); head.getChildren().addAll(t, sp, ic);

        conflictCountLabel = new Label(conflictCount + " Needs Attention");
        conflictCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + CONFLICT_TEXT + ";");

        Label s = new Label("Review required"); s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(head, conflictCountLabel, s);
        return card;
    }

    private VBox createProgressCardWidget() {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox head = new HBox();
        Label t = new Label("Completion"); t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label("☑"); head.getChildren().addAll(t, sp, ic);

        Label v = new Label("65% Progress"); v.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(head, v, pb);
        return card;
    }

    private VBox createConflictAlertBanner(Stage stage) {
        conflictAlertBanner = new VBox(10);
        conflictAlertBanner.setPadding(new Insets(16));
        conflictAlertBanner.setStyle("-fx-background-color: " + CONFLICT_BG + "; -fx-background-radius: 12px; -fx-border-color: " + CONFLICT_BORDER + "; -fx-border-radius: 12px;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label alertIcon = new Label("🚨");
        Label title = new Label("Schedule Conflict Detected"); title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + CONFLICT_TEXT + ";");
        Label priorityBadge = new Label("MEDIUM PRIORITY"); priorityBadge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: " + CONFLICT_TEXT + "; -fx-font-weight: bold; -fx-font-size: 9px; -fx-padding: 2 6; -fx-background-radius: 8px;");

        header.getChildren().addAll(alertIcon, title, priorityBadge);

        Label desc = new Label("AV Setup overlaps with Guest Registration.");
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox conflictBox = new HBox(20);
        conflictBox.setPadding(new Insets(10));
        conflictBox.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 8px;");

        VBox c1 = new VBox(2, new Label("AV Setup"), new Label("08:30 - 09:30 AM"));
        c1.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        c1.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox c2 = new VBox(2, new Label("Guest Registration"), new Label("09:00 - 09:30 AM"));
        c2.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        c2.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        conflictBox.getChildren().addAll(c1, new Separator(), c2);

        HBox solutionBox = new HBox(8);
        solutionBox.setAlignment(Pos.CENTER_LEFT);
        solutionBox.setPadding(new Insets(8, 12, 8, 12));
        solutionBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px;");

        Label solText = new Label("💡 Solution: Move AV final setup to 08:00 AM - 09:00 AM.");
        solText.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        solutionBox.getChildren().add(solText);

        HBox btnRow = new HBox(10);
        Button reviewBtn = createSecondaryButton("Review Conflict");

        Button applyBtn = createPrimaryButton("Apply Suggestion");
        applyBtn.setStyle("-fx-background-color: " + CONFLICT_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        applyBtn.setOnAction(e -> resolveConflictSuggestion(stage));

        btnRow.getChildren().addAll(reviewBtn, applyBtn);

        conflictAlertBanner.getChildren().addAll(header, desc, conflictBox, solutionBox, btnRow);
        return conflictAlertBanner;
    }

    private void resolveConflictSuggestion(Stage stage) {
        for (ScheduleItem item : scheduleItems) {
            if (item.title.contains("AV & Sound Setup")) {
                item.time = "08:00 AM";
                item.isConflict = false;
                item.status = "PENDING";
            }
        }
        conflictCount = 0;
        if (conflictCountLabel != null) {
            conflictCountLabel.setText("0 Resolved");
            conflictCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #137333;");
        }
        if (conflictAlertBanner != null) {
            conflictAlertBanner.setVisible(false);
            conflictAlertBanner.setManaged(false);
        }
        renderTimeline(stage);
    }

    private VBox createTimelineSection(Stage stage) {
        VBox card = createCardContainer();

        HBox timelineHeader = new HBox();
        timelineHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Friday, Oct 24 Timeline");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        ComboBox<String> viewCB = new ComboBox<>();
        viewCB.getItems().addAll("Day View", "Week View"); viewCB.setValue("Day View");

        timelineHeader.getChildren().addAll(title, sp, viewCB);

        timelineContainer = new VBox(10);
        renderTimeline(stage);

        card.getChildren().addAll(timelineHeader, new Separator(), timelineContainer);
        return card;
    }

    private void renderTimeline(Stage stage) {
        timelineContainer.getChildren().clear();

        for (ScheduleItem item : scheduleItems) {
            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 12, 8, 12));

            Label timeLbl = new Label(item.time);
            timeLbl.setPrefWidth(80);
            timeLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + (item.isConflict ? CONFLICT_TEXT : TEXT_MAIN) + ";");

            Circle nodeDot = new Circle(6);
            nodeDot.setFill(Color.web(item.isConflict ? CONFLICT_TEXT : BORDER_COLOR));

            HBox contentBox = new HBox(12);
            HBox.setHgrow(contentBox, Priority.ALWAYS);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.setPadding(new Insets(10, 14, 10, 14));

            if (item.isConflict) {
                contentBox.setStyle("-fx-background-color: " + CONFLICT_BG + "; -fx-border-color: " + CONFLICT_BORDER + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            } else {
                contentBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            }

            VBox textGroup = new VBox(2);
            Label itemTitle = new Label(item.title + (item.isConflict ? " ⚠️ CONFLICT" : ""));
            itemTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + (item.isConflict ? CONFLICT_TEXT : NAVY) + ";");

            Label details = new Label("📍 " + item.location + "  •  👤 " + item.assigned);
            details.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            textGroup.getChildren().addAll(itemTitle, details);

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

            Label statusBadge = new Label(item.status);
            statusBadge.setStyle("-fx-background-color: " + (item.isConflict ? CONFLICT_BG : BACKGROUND) + "; -fx-text-fill: " + (item.isConflict ? CONFLICT_TEXT : SECONDARY_TEXT) + "; -fx-padding: 3 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 10px;");

            contentBox.getChildren().addAll(textGroup, sp, statusBadge);

            row.getChildren().addAll(timeLbl, nodeDot, contentBox);
            timelineContainer.getChildren().add(row);
        }
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            goDashboard(stage);
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
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
        menu.getChildren().add(createNavBtn("Schedule", true, () -> {}));

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
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label label = new Label("Event Schedule & Timeline");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        topBar.getChildren().add(label);
        return topBar;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
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