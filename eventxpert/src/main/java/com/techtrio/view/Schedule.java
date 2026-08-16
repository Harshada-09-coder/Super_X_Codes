package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Schedule extends Application {

    // Color Palette Constants
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";
    private static final String CONFLICT_BG = "#FEF2F2";
    private static final String CONFLICT_TEXT = "#991B1B";
    private static final String CONFLICT_BORDER = "#FCA5A5";

    // Dynamic State Variables
    private static class ScheduleItem {
        String time;
        String title;
        String location;
        String assigned;
        String status; // PENDING, CONFLICT, COMPLETED, LIVE, UPCOMING
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

    @Override
    public void start(Stage primaryStage) {
        initSampleSchedule();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Scrollable Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Assemble Layout Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createSummaryCardsRow(),
                createConflictAlertBanner(primaryStage),
                createTimelineSection(primaryStage),
                createBottomInsightsAndAiRow(primaryStage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Event Schedule");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleSchedule() {
        scheduleItems.clear();
        scheduleItems.add(new ScheduleItem("08:00 AM", "Venue Final Setup", "Main Hall", "Venue Team", "PENDING", false));
        scheduleItems.add(new ScheduleItem("08:30 AM", "AV & Sound Setup", "Main Hall", "VisionPro AV", "CONFLICT", true));
        scheduleItems.add(new ScheduleItem("09:00 AM", "Guest Registration & Welcome", "Main Lobby", "Registration Staff", "COMPLETED", false));
        scheduleItems.add(new ScheduleItem("10:00 AM", "Keynote Session: Tech Trends 2026", "Main Hall", "Rahul Sharma (Director)", "LIVE", false));
        scheduleItems.add(new ScheduleItem("11:00 AM", "Coffee & Networking Break", "Networking Lounge", "FreshBite Catering", "UPCOMING", false));
        scheduleItems.add(new ScheduleItem("11:45 AM", "Technology Panel Discussion", "Conference Room A", "Amit Patel & Panelists", "UPCOMING", false));
        scheduleItems.add(new ScheduleItem("01:00 PM", "Corporate Executive Lunch", "Dining Hall", "FreshBite Catering", "UPCOMING", false));
        scheduleItems.add(new ScheduleItem("02:00 PM", "AI & Business Strategy Session", "Main Hall", "Sneha Joshi", "UPCOMING", false));
        scheduleItems.add(new ScheduleItem("05:00 PM", "Closing Ceremony & Wrap Up", "Main Hall", "Event Director", "UPCOMING", false));
    }

    // ==========================================
    // 1. SIDEBAR NAVIGATION
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(6);

        Label brandLabel = new Label("EventXpert");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Corporate Management");
        brandSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox brandBox = new VBox(2, brandLabel, brandSub);
        brandBox.setPadding(new Insets(0, 8, 16, 8));

        sidebar.getChildren().add(brandBox);

        sidebar.getChildren().add(createSidebarCategory("MAIN"));
        sidebar.getChildren().add(createSidebarItem("Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));
        sidebar.getChildren().add(createSidebarItem("Event Requests", false, e -> showToast(stage, "Opening Event Requests...")));
        sidebar.getChildren().add(createSidebarItem("My Events", false, e -> showToast(stage, "Opening My Events...")));

        sidebar.getChildren().add(createSidebarCategory("EVENT PLANNING"));
        sidebar.getChildren().add(createSidebarItem("AI Planner", false, e -> showToast(stage, "Opening AI Planner...")));
        sidebar.getChildren().add(createSidebarItem("Budget", false, e -> showToast(stage, "Opening Budget...")));
        sidebar.getChildren().add(createSidebarItem("Venue", false, e -> showToast(stage, "Opening Venue Management...")));
        sidebar.getChildren().add(createSidebarItem("Vendors", false, e -> showToast(stage, "Opening Vendors...")));
        sidebar.getChildren().add(createSidebarItem("Schedule", true, e -> {})); // Active Menu

        sidebar.getChildren().add(createSidebarCategory("EXECUTION"));
        sidebar.getChildren().add(createSidebarItem("Guest Management", false, e -> showToast(stage, "Opening Guest Management...")));
        sidebar.getChildren().add(createSidebarItem("QR Check-In", false, e -> showToast(stage, "Opening QR Check-In...")));
        sidebar.getChildren().add(createSidebarItem("Live Event", false, e -> showToast(stage, "Opening Live Event...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(10));
        profileBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        Text avatarText = new Text("OP");
        avatarText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Admin User");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label userEmail = new Label("admin@eventxpert.com");
        userEmail.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        userDetails.getChildren().addAll(userName, userEmail);

        profileBox.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profileBox);
        return sidebar;
    }

    private Label createSidebarCategory(String title) {
        Label cat = new Label(title);
        cat.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 4 8;");
        return cat;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        return btn;
    }

    // ==========================================
    // 2. TOP BAR
    // ==========================================
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search sessions, speakers, vendors...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "1 Conflict Alert pending review."));

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, notifBtn, helpBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Schedule");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Event Schedule");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label tag = new Label("TechVision Annual Summit • In Planning");
        tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 4 12; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button aiGenerateBtn = createSecondaryButton("✨ AI Generate Schedule");
        aiGenerateBtn.setOnAction(e -> openAiGenerateModal(stage));

        Button addActivityBtn = createPrimaryButton("+ Add Activity");
        addActivityBtn.setOnAction(e -> openAddActivityModal(stage));

        titleRow.getChildren().addAll(title, tag, spacer, aiGenerateBtn, addActivityBtn);

        Label subtitle = new Label("Plan the event timeline, coordinate activities and detect scheduling conflicts automatically.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW
    // ==========================================
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
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        head.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(head, v, s);
        return card;
    }

    private VBox createConflictCardWidget() {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + CONFLICT_BORDER + "; -fx-border-radius: 12px; -fx-border-width: 1.5;");

        HBox head = new HBox();
        Label t = new Label("Conflicts");
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + CONFLICT_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label("⚠️");
        head.getChildren().addAll(t, sp, ic);

        conflictCountLabel = new Label(conflictCount + " Needs Attention");
        conflictCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + CONFLICT_TEXT + ";");

        Label s = new Label("Review required");
        s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(head, conflictCountLabel, s);
        return card;
    }

    private VBox createProgressCardWidget() {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox head = new HBox();
        Label t = new Label("Completion");
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label("☑");
        head.getChildren().addAll(t, sp, ic);

        Label v = new Label("65% Progress");
        v.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        ProgressBar pb = new ProgressBar(0.65);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        card.getChildren().addAll(head, v, pb);
        return card;
    }

    // ==========================================
    // 5. CONFLICT ALERT BANNER
    // ==========================================
    private VBox createConflictAlertBanner(Stage stage) {
        conflictAlertBanner = new VBox(10);
        conflictAlertBanner.setPadding(new Insets(16));
        conflictAlertBanner.setStyle("-fx-background-color: " + CONFLICT_BG + "; -fx-background-radius: 12px; -fx-border-color: " + CONFLICT_BORDER + "; -fx-border-radius: 12px;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label alertIcon = new Label("🚨");
        alertIcon.setStyle("-fx-font-size: 16px;");

        Label title = new Label("Schedule Conflict Detected");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + CONFLICT_TEXT + ";");

        Label priorityBadge = new Label("MEDIUM PRIORITY");
        priorityBadge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: " + CONFLICT_TEXT + "; -fx-font-weight: bold; -fx-font-size: 9px; -fx-padding: 2 6; -fx-background-radius: 8px;");

        header.getChildren().addAll(alertIcon, title, priorityBadge);

        Label desc = new Label("AV Setup overlaps with Guest Registration in the Main Hall area.");
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

        // Solution Bar
        HBox solutionBox = new HBox(8);
        solutionBox.setAlignment(Pos.CENTER_LEFT);
        solutionBox.setPadding(new Insets(8, 12, 8, 12));
        solutionBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px;");

        Label solText = new Label("💡 Solution: Move AV final setup to 08:00 AM - 09:00 AM.");
        solText.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        solutionBox.getChildren().add(solText);

        HBox btnRow = new HBox(10);
        Button reviewBtn = createSecondaryButton("Review Conflict");
        reviewBtn.setOnAction(e -> openReviewConflictModal(stage));

        Button applyBtn = createPrimaryButton("Apply Suggestion");
        applyBtn.setStyle("-fx-background-color: " + CONFLICT_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        applyBtn.setOnAction(e -> {
            resolveConflictSuggestion();
            showToast(stage, "✓ Suggestion applied! Conflict resolved.");
        });

        btnRow.getChildren().addAll(reviewBtn, applyBtn);

        conflictAlertBanner.getChildren().addAll(header, desc, conflictBox, solutionBox, btnRow);
        return conflictAlertBanner;
    }

    private void resolveConflictSuggestion() {
        for (ScheduleItem item : scheduleItems) {
            if (item.title.contains("AV & Sound Setup")) {
                item.time = "08:00 AM";
                item.isConflict = false;
                item.status = "PENDING";
            }
        }
        conflictCount = 0;
        conflictCountLabel.setText("0 Resolved");
        conflictCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        conflictAlertBanner.setVisible(false);
        conflictAlertBanner.setManaged(false);
        renderTimeline((Stage) timelineContainer.getScene().getWindow());
    }

    // ==========================================
    // 6. MAIN TIMELINE SECTION
    // ==========================================
    private VBox createTimelineSection(Stage stage) {
        VBox card = createCardContainer();

        HBox timelineHeader = new HBox();
        timelineHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Friday, Oct 24 Timeline");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        ComboBox<String> viewCB = new ComboBox<>();
        viewCB.getItems().addAll("Day View", "Week View", "Timeline");
        viewCB.setValue("Day View");

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

            // Time Label Column
            Label timeLbl = new Label(item.time);
            timeLbl.setPrefWidth(80);
            if (item.isConflict) {
                timeLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + CONFLICT_TEXT + ";");
            } else {
                timeLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
            }

            // Timeline Node Circle
            Circle nodeDot = new Circle(6);
            if (item.isConflict) {
                nodeDot.setFill(Color.web(CONFLICT_TEXT));
            } else if (item.status.equals("LIVE")) {
                nodeDot.setFill(Color.web(PRIMARY_BLUE));
            } else {
                nodeDot.setFill(Color.web(BORDER_COLOR));
            }

            // Item Content Box
            HBox contentBox = new HBox(12);
            HBox.setHgrow(contentBox, Priority.ALWAYS);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.setPadding(new Insets(10, 14, 10, 14));

            if (item.isConflict) {
                contentBox.setStyle("-fx-background-color: " + CONFLICT_BG + "; -fx-border-color: " + CONFLICT_BORDER + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            } else if (item.status.equals("LIVE")) {
                contentBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            } else {
                contentBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            }

            VBox textGroup = new VBox(2);
            Label itemTitle = new Label(item.title + (item.isConflict ? " ⚠️ CONFLICT" : ""));
            itemTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + (item.isConflict ? CONFLICT_TEXT : NAVY) + ";");

            Label details = new Label("📍 " + item.location + "  •  👤 " + item.assigned);
            details.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            textGroup.getChildren().addAll(itemTitle, details);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label statusBadge = new Label(item.status);
            statusBadge.setStyle(getStatusStyle(item.status));

            Button editBtn = createSecondaryButton("Edit");
            editBtn.setOnAction(e -> openEditActivityModal(stage, item));

            contentBox.getChildren().addAll(textGroup, sp, statusBadge, editBtn);

            row.getChildren().addAll(timeLbl, nodeDot, contentBox);
            timelineContainer.getChildren().add(row);
        }
    }

    private String getStatusStyle(String status) {
        switch (status) {
            case "COMPLETED":
                return "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 3 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 10px;";
            case "LIVE":
                return "-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 10px;";
            case "CONFLICT":
                return "-fx-background-color: " + CONFLICT_BG + "; -fx-text-fill: " + CONFLICT_TEXT + "; -fx-padding: 3 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 10px;";
            default:
                return "-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 3 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 10px;";
        }
    }

    // ==========================================
    // 7. BOTTOM AI & INSIGHTS ROW
    // ==========================================
    private HBox createBottomInsightsAndAiRow(Stage stage) {
        HBox row = new HBox(16);

        // AI Optimization Card (Left)
        VBox aiCard = createCardContainer();
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        aiCard.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");

        Label aiTitle = new Label("✨ AI Schedule Optimization");
        aiTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label aiMsg = new Label("Schedule Efficiency: 92%. AI detected smooth flow with optimal break buffer times.");
        aiMsg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox bulletPoints = new VBox(4);
        bulletPoints.getChildren().addAll(
                new Label("✓ 15-min setup buffer allocated for Keynote Session"),
                new Label("✓ Catering arrival synchronized with 11:00 AM Coffee Break"),
                new Label("✓ Hall transition paths verified for 250 guests")
        );
        bulletPoints.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        Button optimizeBtn = createPrimaryButton("Optimize Schedule →");
        optimizeBtn.setOnAction(e -> showToast(stage, "AI Schedule re-optimized for maximum efficiency!"));

        aiCard.getChildren().addAll(aiTitle, aiMsg, bulletPoints, optimizeBtn);

        // Resource Availability Status Card (Right)
        VBox resCard = createCardContainer();
        resCard.setPrefWidth(400);

        Label resTitle = new Label("Resource Readiness");
        resTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane resGrid = new GridPane();
        resGrid.setHgap(16); resGrid.setVgap(8);

        resGrid.add(createResourceRow("Speakers", "✓ 8 Ready", GREEN_TEXT), 0, 0);
        resGrid.add(createResourceRow("Vendors", "✓ 6 Confirmed", GREEN_TEXT), 0, 1);
        resGrid.add(createResourceRow("Halls/Rooms", "⚠️ 1 Overlap", WARNING_TEXT), 1, 0);
        resGrid.add(createResourceRow("AV Gear", "✓ Checked", GREEN_TEXT), 1, 1);

        resCard.getChildren().addAll(resTitle, new Separator(), resGrid);

        row.getChildren().addAll(aiCard, resCard);
        return row;
    }

    private VBox createResourceRow(String name, String status, String color) {
        VBox b = new VBox(2);
        Label n = new Label(name); n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label s = new Label(status); s.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + color + ";");
        b.getChildren().addAll(n, s);
        return b;
    }

    // ==========================================
    // MODAL DIALOGS
    // ==========================================
    private void openAddActivityModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add Schedule Activity");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Add New Schedule Item");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField timeInput = new TextField(); timeInput.setPromptText("Time (e.g. 03:00 PM)");
        TextField titleInput = new TextField(); titleInput.setPromptText("Activity Title");
        TextField locInput = new TextField(); locInput.setPromptText("Venue / Room");
        TextField assignInput = new TextField(); assignInput.setPromptText("Speaker or Vendor");

        Button saveBtn = createPrimaryButton("Add to Schedule");
        saveBtn.setOnAction(e -> {
            if (!titleInput.getText().isEmpty()) {
                scheduleItems.add(new ScheduleItem(
                        timeInput.getText().isEmpty() ? "03:30 PM" : timeInput.getText(),
                        titleInput.getText(),
                        locInput.getText().isEmpty() ? "Main Hall" : locInput.getText(),
                        assignInput.getText().isEmpty() ? "Event Staff" : assignInput.getText(),
                        "UPCOMING", false
                ));
                renderTimeline(parentStage);
                showToast(parentStage, "✓ Added activity '" + titleInput.getText() + "'");
                modal.close();
            } else {
                showToast(parentStage, "Please enter an activity title.");
            }
        });

        layout.getChildren().addAll(title, new Label("Time:"), timeInput, new Label("Title:"), titleInput,
                new Label("Venue:"), locInput, new Label("Assigned:"), assignInput, saveBtn);

        Scene scene = new Scene(layout, 360, 380);
        modal.setScene(scene);
        modal.show();
    }

    private void openEditActivityModal(Stage parentStage, ScheduleItem item) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Edit Activity - " + item.title);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Edit Activity Details");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField timeInput = new TextField(item.time);
        TextField titleInput = new TextField(item.title);
        TextField locInput = new TextField(item.location);

        Button saveBtn = createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            item.time = timeInput.getText();
            item.title = titleInput.getText();
            item.location = locInput.getText();
            renderTimeline(parentStage);
            showToast(parentStage, "Updated " + item.title);
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Time:"), timeInput, new Label("Title:"), titleInput,
                new Label("Venue:"), locInput, saveBtn);

        Scene scene = new Scene(layout, 340, 320);
        modal.setScene(scene);
        modal.show();
    }

    private void openAiGenerateModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("AI Schedule Generator");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("✨ Auto-Generate Event Timeline");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label desc = new Label("AI will auto-arrange sessions, break slots and buffer times based on 250 guest count and venue capacity.");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button genBtn = createPrimaryButton("Generate & Apply Timeline");
        genBtn.setOnAction(e -> {
            resolveConflictSuggestion();
            showToast(parentStage, "✓ Timeline regenerated with 0 conflicts!");
            modal.close();
        });

        layout.getChildren().addAll(title, desc, genBtn);

        Scene scene = new Scene(layout, 380, 200);
        modal.setScene(scene);
        modal.show();
    }

    private void openReviewConflictModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Review Conflict");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("⚠️ Conflict Review");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + CONFLICT_TEXT + ";");

        Label details = new Label("AV & Sound Setup at 08:30 AM overlaps with Guest Registration setup at 09:00 AM.");
        details.setWrapText(true);

        Button fixBtn = createPrimaryButton("Apply Recommended Solution");
        fixBtn.setOnAction(e -> {
            resolveConflictSuggestion();
            showToast(parentStage, "✓ Conflict resolved!");
            modal.close();
        });

        layout.getChildren().addAll(title, details, fixBtn);

        Scene scene = new Scene(layout, 380, 220);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 7 15; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}