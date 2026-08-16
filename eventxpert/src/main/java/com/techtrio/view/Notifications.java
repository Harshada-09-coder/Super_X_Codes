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
import java.util.stream.Collectors;

public class Notifications extends Application {

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
    private static final String URGENT_BG = "#FEF2F2";
    private static final String URGENT_TEXT = "#991B1B";
    private static final String URGENT_BORDER = "#FCA5A5";

    // Data Structure for Notification Item
    private static class NotificationData {
        String id;
        String title;
        String category;
        String message;
        String timeAgo;
        String priority; // URGENT, IMPORTANT, NORMAL
        boolean isUnread;
        String actionBtnText;
        String iconSymbol;

        NotificationData(String id, String title, String category, String message, String timeAgo,
                         String priority, boolean isUnread, String actionBtnText, String iconSymbol) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.message = message;
            this.timeAgo = timeAgo;
            this.priority = priority;
            this.isUnread = isUnread;
            this.actionBtnText = actionBtnText;
            this.iconSymbol = iconSymbol;
        }
    }

    private final List<NotificationData> notificationsList = new ArrayList<>();
    private VBox notificationListContainer;
    private Label sidebarBadgeLabel;
    private Label overviewUnreadLabel;
    private Label overviewActionRequiredLabel;
    private Label pageUnreadCountLabel;

    private String activeCategoryFilter = "All";

    @Override
    public void start(Stage primaryStage) {
        initSampleNotifications();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Content inside ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Header & Chips
        VBox headerBox = createPageHeader(primaryStage);
        HBox filterChipsRow = createFilterChipsRow(primaryStage);

        // Main Layout: Left Stream + Right Focus Action Card
        HBox centerSplit = new HBox(20);

        VBox streamCard = createNotificationStreamCard(primaryStage);
        HBox.setHgrow(streamCard, Priority.ALWAYS);

        VBox rightSideCards = createRightFocusPanel(primaryStage);
        rightSideCards.setPrefWidth(380);

        centerSplit.getChildren().addAll(streamCard, rightSideCards);

        mainContent.getChildren().addAll(headerBox, filterChipsRow, centerSplit);

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Notification Center");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleNotifications() {
        notificationsList.clear();
        notificationsList.add(new NotificationData("1", "New Event Request", "Event Requests", "TechVision Annual Summit submitted for organizer review.", "10m ago", "NORMAL", true, "Review Request", "📋"));
        notificationsList.add(new NotificationData("2", "AI Planner Optimization", "Planning", "Resource overlap detected in the main hall schedule.", "32m ago", "IMPORTANT", true, "View Recommendation", "✨"));
        notificationsList.add(new NotificationData("3", "Budget Alert", "Budget", "Catering for TechVision is 8% above estimate.", "1h ago", "URGENT", true, "Review Budget", "💰"));
        notificationsList.add(new NotificationData("4", "Schedule Conflict Detected", "Schedule", "AV Setup overlap detected in Room B with Registration.", "Today", "URGENT", true, "Resolve Conflict", "⚠️"));
        notificationsList.add(new NotificationData("5", "Vendor Quote Received", "Vendors", "VisionPro AV has submitted their initial quote.", "2h ago", "NORMAL", true, "View Quote", "📄"));
        notificationsList.add(new NotificationData("6", "Weather Alert", "Weather", "Rain probability 40% on event date. Indoor backup ready.", "2h ago", "IMPORTANT", true, "View Weather", "☁"));
        notificationsList.add(new NotificationData("7", "Guest Check-In Update", "Guests", "125 of 250 expected guests have checked in.", "Today", "NORMAL", true, "View Check-Ins", "🎟"));
        notificationsList.add(new NotificationData("8", "Venue Confirmed", "Venue", "Grand Convention Center confirmed for TechVision Summit.", "Yesterday", "NORMAL", false, "View Venue", "🏛"));
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
        sidebar.getChildren().add(createSidebarItem("Venue", false, e -> showToast(stage, "Opening Venue...")));
        sidebar.getChildren().add(createSidebarItem("Vendors", false, e -> showToast(stage, "Opening Vendors...")));
        sidebar.getChildren().add(createSidebarItem("Schedule", false, e -> showToast(stage, "Opening Schedule...")));

        sidebar.getChildren().add(createSidebarCategory("EXECUTION"));
        sidebar.getChildren().add(createSidebarItem("Guest Management", false, e -> showToast(stage, "Opening Guest Management...")));
        sidebar.getChildren().add(createSidebarItem("QR Check-In", false, e -> showToast(stage, "Opening QR Check-In...")));
        sidebar.getChildren().add(createSidebarItem("Live Event", false, e -> showToast(stage, "Opening Live Event...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Notifications Menu Item (Active Highlight)
        HBox notifMenuRow = new HBox(8);
        notifMenuRow.setAlignment(Pos.CENTER_LEFT);
        notifMenuRow.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px; -fx-padding: 8 12;");

        Label notifText = new Label("Notifications");
        notifText.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        sidebarBadgeLabel = new Label(String.valueOf(getUnreadCount()));
        sidebarBadgeLabel.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");

        notifMenuRow.getChildren().addAll(notifText, sp, sidebarBadgeLabel);

        // User profile box
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

        sidebar.getChildren().addAll(spacer, notifMenuRow, profileBox);
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

        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12;"));
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
        searchField.setPromptText("🔍 Search events, vendors, attendees, alerts...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Button signOutBtn = new Button("Sign Out");
        signOutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px; -fx-cursor: hand;");
        signOutBtn.setOnAction(e -> showToast(stage, "Signing out..."));

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, profilePill, signOutBtn);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button settingsBtn = createSecondaryButton("⚙ Settings");
        settingsBtn.setOnAction(e -> openNotificationSettingsModal(stage));

        Button markAllBtn = createSecondaryButton("✓ Mark all as read");
        markAllBtn.setOnAction(e -> markAllAsRead(stage));

        titleRow.getChildren().addAll(title, spacer, settingsBtn, markAllBtn);

        Label subtitle = new Label("Stay updated with your events, vendors, guests, schedules and important alerts.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. FILTER CHIPS ROW
    // ==========================================
    private HBox createFilterChipsRow(Stage stage) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        String[] categories = {"All", "Unread", "Event Requests", "Planning", "Vendors", "Guests", "Alerts"};

        for (String cat : categories) {
            Button chip = new Button();
            if (cat.equals("Unread")) {
                chip.setText("Unread (" + getUnreadCount() + ")");
            } else {
                chip.setText(cat);
            }

            if (cat.equals(activeCategoryFilter)) {
                chip.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 16px; -fx-padding: 6 16; -fx-cursor: hand;");
            } else {
                chip.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 6 16; -fx-cursor: hand;");
            }

            chip.setOnAction(e -> {
                activeCategoryFilter = cat;
                renderNotificationStream(stage);
            });

            row.getChildren().add(chip);
        }

        return row;
    }

    // ==========================================
    // 5. NOTIFICATION STREAM CARD
    // ==========================================
    private VBox createNotificationStreamCard(Stage stage) {
        VBox card = createCardContainer();
        card.setPadding(new Insets(0));

        notificationListContainer = new VBox();
        renderNotificationStream(stage);

        card.getChildren().add(notificationListContainer);
        return card;
    }

    private void renderNotificationStream(Stage stage) {
        notificationListContainer.getChildren().clear();

        List<NotificationData> filtered = notificationsList.stream().filter(n -> {
            if (activeCategoryFilter.equals("All")) return true;
            if (activeCategoryFilter.equals("Unread")) return n.isUnread;
            if (activeCategoryFilter.equals("Alerts")) return n.priority.equals("URGENT") || n.priority.equals("IMPORTANT");
            return n.category.equalsIgnoreCase(activeCategoryFilter);
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            Label ic = new Label("🎉"); ic.setStyle("-fx-font-size: 32px;");
            Label t = new Label("You're all caught up!"); t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");
            Label s = new Label("No new notifications match your filter."); s.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            emptyBox.getChildren().addAll(ic, t, s);
            notificationListContainer.getChildren().add(emptyBox);
            return;
        }

        for (NotificationData n : filtered) {
            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(16, 20, 16, 20));

            // Background & Border styling
            if (n.priority.equals("URGENT") && n.isUnread) {
                row.setStyle("-fx-background-color: " + URGENT_BG + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
            } else if (n.isUnread) {
                row.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
            } else {
                row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
            }

            // Category Icon Circle
            Label iconLbl = new Label(n.iconSymbol);
            iconLbl.setStyle("-fx-font-size: 16px; -fx-background-color: " + WHITE + "; -fx-padding: 8; -fx-background-radius: 50%; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 50%;");

            // Text Content
            VBox textGroup = new VBox(4);
            HBox titleRow = new HBox(8);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label(n.title);
            title.setStyle("-fx-font-weight: " + (n.isUnread ? "bold" : "normal") + "; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

            titleRow.getChildren().add(title);

            if (n.isUnread) {
                Circle blueDot = new Circle(4, Color.web(PRIMARY_BLUE));
                titleRow.getChildren().add(blueDot);
            }

            Label msg = new Label(n.message);
            msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

            Button actionBtn = createSecondaryButton(n.actionBtnText);
            actionBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2 0; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> {
                n.isUnread = false;
                renderNotificationStream(stage);
                updateCounts();
                showToast(stage, "Opening details for " + n.title);
            });

            textGroup.getChildren().addAll(titleRow, msg, actionBtn);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            // Time & Priority
            VBox metaBox = new VBox(4);
            metaBox.setAlignment(Pos.TOP_RIGHT);

            Label timeLbl = new Label(n.timeAgo);
            timeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            metaBox.getChildren().add(timeLbl);

            row.getChildren().addAll(iconLbl, textGroup, sp, metaBox);

            // Click to mark as read
            row.setOnMouseClicked(e -> {
                n.isUnread = false;
                renderNotificationStream(stage);
                updateCounts();
            });

            notificationListContainer.getChildren().add(row);
        }
    }

    // ==========================================
    // 6. RIGHT FOCUS & OVERVIEW PANEL
    // ==========================================
    private VBox createRightFocusPanel(Stage stage) {
        VBox panel = new VBox(16);

        // Top Urgent Focus Card (Matching Screenshot Alert Box)
        VBox alertFocusCard = createCardContainer();
        alertFocusCard.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + WARNING_TEXT + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-border-width: 1.5;");

        HBox topHead = new HBox();
        Label calIcon = new Label("📅"); calIcon.setStyle("-fx-font-size: 20px; -fx-background-color: " + WARNING_BG + "; -fx-padding: 8; -fx-background-radius: 8px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label closeBtn = new Label("✕"); closeBtn.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");
        closeBtn.setOnMouseClicked(e -> alertFocusCard.setVisible(false));
        topHead.getChildren().addAll(calIcon, sp, closeBtn);

        Label fTitle = new Label("Schedule Conflict Detected");
        fTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        Label fSub = new Label("The system identified an overlap in physical resources for TechVision Annual Summit.");
        fSub.setWrapText(true);
        fSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox detailBox = new VBox(4);
        detailBox.setPadding(new Insets(10));
        detailBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        Label locLbl = new Label("📍 Grand Ballroom - Section A"); locLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + NAVY + ";");
        Label t1 = new Label("VisionPro AV Setup:  1:00 PM - 3:00 PM"); t1.setStyle("-fx-font-size: 10px; -fx-text-fill: " + URGENT_TEXT + ";");
        Label t2 = new Label("Catering Delivery:      2:30 PM - 3:30 PM"); t2.setStyle("-fx-font-size: 10px; -fx-text-fill: " + URGENT_TEXT + ";");
        detailBox.getChildren().addAll(locLbl, t1, t2);

        Button adjustBtn = createPrimaryButton("Adjust Schedule");
        adjustBtn.setMaxWidth(Double.MAX_VALUE);
        adjustBtn.setOnAction(e -> showToast(stage, "Opening schedule editor..."));

        Button ignoreBtn = createSecondaryButton("Ignore for now");
        ignoreBtn.setMaxWidth(Double.MAX_VALUE);
        ignoreBtn.setOnAction(e -> alertFocusCard.setVisible(false));

        alertFocusCard.getChildren().addAll(topHead, fTitle, fSub, detailBox, adjustBtn, ignoreBtn);

        // Overview Summary Card
        VBox overviewCard = createCardContainer();

        Label oTitle = new Label("Notification Overview");
        oTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + NAVY + ";");

        HBox r1 = createStatRow("Unread", String.valueOf(getUnreadCount()), PRIMARY_BLUE);
        overviewUnreadLabel = (Label) r1.getChildren().get(2);

        HBox r2 = createStatRow("Action Required", "3", URGENT_TEXT);
        overviewActionRequiredLabel = (Label) r2.getChildren().get(2);

        ProgressBar pbUnread = new ProgressBar(0.70); pbUnread.setMaxWidth(Double.MAX_VALUE); pbUnread.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");
        ProgressBar pbAction = new ProgressBar(0.35); pbAction.setMaxWidth(Double.MAX_VALUE); pbAction.setStyle("-fx-accent: " + URGENT_TEXT + ";");

        overviewCard.getChildren().addAll(oTitle, r1, pbUnread, new Separator(), r2, pbAction);

        panel.getChildren().addAll(alertFocusCard, overviewCard);
        return panel;
    }

    private HBox createStatRow(String label, String val, String color) {
        HBox r = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val); v.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + color + ";");
        r.getChildren().addAll(l, sp, v);
        return r;
    }

    // ==========================================
    // ACTIONS & MODALS
    // ==========================================
    private long getUnreadCount() {
        return notificationsList.stream().filter(n -> n.isUnread).count();
    }

    private void updateCounts() {
        long unread = getUnreadCount();
        sidebarBadgeLabel.setText(String.valueOf(unread));
        overviewUnreadLabel.setText(String.valueOf(unread));
    }

    private void markAllAsRead(Stage parentStage) {
        for (NotificationData n : notificationsList) {
            n.isUnread = false;
        }
        renderNotificationStream(parentStage);
        updateCounts();
        showToast(parentStage, "✓ All notifications marked as read.");
    }

    private void openNotificationSettingsModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Notification Preferences");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("⚙ Notification Preferences");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        CheckBox c1 = new CheckBox("Schedule Conflicts & Overlaps"); c1.setSelected(true);
        CheckBox c2 = new CheckBox("Vendor Quotes & Contract Updates"); c2.setSelected(true);
        CheckBox c3 = new CheckBox("Budget & Payment Alerts"); c3.setSelected(true);
        CheckBox c4 = new CheckBox("Guest & QR Check-In Updates"); c4.setSelected(true);
        CheckBox c5 = new CheckBox("Weather Alerts"); c5.setSelected(true);

        Button saveBtn = createPrimaryButton("Save Preferences");
        saveBtn.setOnAction(e -> {
            showToast(parentStage, "✓ Notification preferences saved.");
            modal.close();
        });

        layout.getChildren().addAll(title, new Separator(), c1, c2, c3, c4, c5, new Separator(), saveBtn);

        Scene scene = new Scene(layout, 360, 300);
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