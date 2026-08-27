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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsV {
    private Scene notificationsScene;
    private VendorDashboardV vendorDashboard;

    // Color Palette Constants
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#D97706";
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";

    // Data Structure for Vendor Notification
    private static class NotificationItem {
        String id;
        String title;
        String desc;
        String timeAgo;
        String category; // Service Requests, Bookings, Payments, Schedule, System
        boolean isUnread;
        String eventTag;
        String budgetTag;
        String iconSymbol;
        String fullMessage;

        NotificationItem(String id, String title, String desc, String timeAgo, String category,
                         boolean isUnread, String eventTag, String budgetTag, String iconSymbol, String fullMessage) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.timeAgo = timeAgo;
            this.category = category;
            this.isUnread = isUnread;
            this.eventTag = eventTag;
            this.budgetTag = budgetTag;
            this.iconSymbol = iconSymbol;
            this.fullMessage = fullMessage;
        }
    }

    private final List<NotificationItem> notificationsList = new ArrayList<>();
    private VBox notificationStreamContainer;
    private Label sidebarBadgeLabel;
    private Label topBarBadgeLabel;
    private Label unreadCountCardLabel;

    // Right Side Notification Details Focus Controls
    private Label detailTitleLabel;
    private Label detailTimeLabel;
    private Label detailMsgLabel;
    private Label detailEventLabel;
    private Label detailServiceLabel;
    private Label detailBudgetLabel;

    private String activeCategoryFilter = "All";
    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getnotificationsScene(Stage primaryStage){
        initSampleNotifications();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left - Fixed 250px Full Height)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Main Application Container
        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Top Bar
        HBox topBar = createTopBar(primaryStage);
        mainArea.setTop(topBar);

        // Scrollable Center Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createSummaryCardsRow(),
                createTwoColumnMainLayout(primaryStage),
                createPaginationRow()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        notificationsScene = scene;
        return notificationsScene;
    }

    private void initSampleNotifications() {
        notificationsList.clear();
        notificationsList.add(new NotificationItem("1", "TechVision Events requested your Sound System service", "They are requesting the Premium Audio Package for their upcoming annual summit. Review the requirements...", "10m ago", "Service Requests", true, "TechVision Annual Summit", "₹85,000", "🎙", "Hello VisionPro Team, We are organizing our Annual Tech Summit and were very impressed with your Sound System portfolio. We need a comprehensive audio setup for our main keynote hall (capacity 500) and two smaller breakout rooms. Could you review our budget and let us know if you can accommodate this request?"));
        notificationsList.add(new NotificationItem("2", "Your booking for Corporate Leadership Meet is confirmed", "The organizer has approved your quote and finalized the booking for Decoration services.", "1h ago", "Bookings", true, "Decoration", "22 May 2026", "📅", "Great news! Your quote for the Leadership Summit 2026 has been officially accepted. Advance payment of ₹22,500 has been credited to your wallet. Setup time is confirmed for 08:00 AM on 22 May 2026."));
        notificationsList.add(new NotificationItem("3", "⚠️ Scheduling Conflict: Two bookings overlap", "You have overlapping bookings for Rental Equipment on 12 June 2026. Please resolve this to avoid service issues.", "3h ago", "Schedule", true, "Conflict Alert", "12 June 2026", "⚠️", "Notice: Annual Gala Night and Corporate Meet are scheduled close to each other on 12 June 2026. Please check your availability calendar to ensure adequate crew and transportation."));
        notificationsList.add(new NotificationItem("4", "Payment of ₹85,000 has been received", "Advance payment for Sound System services (Invoice #INV-2026-042) has been successfully credited to your wallet.", "2h ago", "Payments", false, "Invoice #INV-2026-042", "₹85,000", "💳", "Payment Confirmation: ₹85,000 transferred via EventXpert Escrow for TechVision Annual Summit. Available for payout immediately."));
        notificationsList.add(new NotificationItem("5", "₹17,000 payment for Startup Connect 2026 is due", "Final settlement was due on 02 June. You can send an automatic reminder to the organizer.", "Yesterday", "Payments", false, "Startup Connect 2026", "₹17,000", "💰", "Payment Reminder: Final balance of ₹17,000 for Startup Connect 2026 is pending. Send a reminder to Startup India Network."));
    }

    // ==========================================
    // 1. SIDEBAR (FULL HEIGHT)
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

        // Logo / Branding Area
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

        // Top Action Button
        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> showToast(stage, "Add Service Clicked"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        // Menu Sections
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> {showToast(stage, "Opening Dashboard...");
          if (vendorDashboard != null) {
                vendorDashboard.backToVendorDashboard();
            } else {
                VendorDashboardV dashboard = new VendorDashboardV();
                Scene dashboardScene = dashboard.getvendorDashboardScene();
                if (LandingPage.landingPageStage != null) {
                    LandingPage.landingPageStage.setScene(dashboardScene);
                }
            }
    }));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(stage, "Opening Calendar...")));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Earnings...")));

        // Active Notifications Item
        HBox notifRow = new HBox();
        notifRow.setAlignment(Pos.CENTER_LEFT);
        Button notifBtn = createSidebarItem("🔔 Notifications", true, e -> {
            if (vendorDashboard != null) {
                vendorDashboard.backToVendorDashboard();
            } else {
                VendorDashboardV dashboard = new VendorDashboardV();
                Scene dashboardScene = dashboard.getvendorDashboardScene();
                if (LandingPage.landingPageStage != null) {
                    LandingPage.landingPageStage.setScene(dashboardScene);
                }
            }
        });
        HBox.setHgrow(notifBtn, Priority.ALWAYS);
        sidebarBadgeLabel = new Label(String.valueOf(getUnreadCount()));
        sidebarBadgeLabel.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");
        notifRow.getChildren().addAll(notifBtn, sidebarBadgeLabel);
        sidebar.getChildren().add(notifRow);
        sidebar.getChildren().add(createSidebarItem("👤 Settings", false, e -> showToast(stage, "Opening Profile...")));
        sidebar.getChildren().add(createSidebarItem("❓ Support", false, e -> showToast(stage, "Opening Support...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom Vendor Profile Pill
        HBox profilePill = new HBox(10);
        profilePill.setAlignment(Pos.CENTER_LEFT);
        profilePill.setPadding(new Insets(10));
        profilePill.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(WHITE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("VisionPro Events");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("Audio & Visual • ● Available");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
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
        searchField.setPromptText("🔍 Search everywhere...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓"); helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        StackPane notifPane = new StackPane();
        Button notifBtn = new Button("🔔"); notifBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        topBarBadgeLabel = new Label(String.valueOf(getUnreadCount()));
        topBarBadgeLabel.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 8px; -fx-padding: 1 4; -fx-background-radius: 8px;");
        StackPane.setAlignment(topBarBadgeLabel, Pos.TOP_RIGHT);
        notifPane.getChildren().addAll(notifBtn, topBarBadgeLabel);

        Button settingsBtn = new Button("⚙"); settingsBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("VP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifPane, settingsBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox header = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button markAllBtn = createSecondaryButton("✓ Mark All as Read");
        markAllBtn.setOnAction(e -> markAllAsRead(stage));

        titleRow.getChildren().addAll(title, spacer, markAllBtn);

        Label subtitle = new Label("Stay updated with your service requests, bookings, payments and account activity.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW (MATCHING STITCH)
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createSummaryCard("UNREAD", String.valueOf(getUnreadCount()), PRIMARY_BLUE, "📩"),
                createSummaryCard("TODAY", "6", TEXT_MAIN, "📅"),
                createSummaryCard("THIS WEEK", "18", TEXT_MAIN, "📆")
        );

        return row;
    }

    private VBox createSummaryCard(String title, String val, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 4 8; -fx-background-radius: 50%;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        if (title.equals("UNREAD")) unreadCountCardLabel = v;
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        card.getChildren().addAll(top, v);
        return card;
    }

    // ==========================================
    // 5. TWO-COLUMN MAIN LAYOUT (FEED & FOCUS DETAILS)
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column: Notification Feed Stream (~2/3 width)
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createFilterTabsRow(stage),
                createSearchFilterRow(stage),
                createNotificationStreamContainer(stage)
        );

        // Right Column: Sticky Notification Details Card (~1/3 width)
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().add(createNotificationDetailsCard(stage));

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private HBox createFilterTabsRow(Stage stage) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        String[] tabs = {"All", "Unread", "Service Requests", "Bookings", "Payments"};

        for (String tab : tabs) {
            Button tabBtn = new Button();
            if (tab.equals("Unread")) {
                tabBtn.setText("Unread (" + getUnreadCount() + ")");
            } else {
                tabBtn.setText(tab);
            }

            if (tab.equals(activeCategoryFilter)) {
                tabBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
            } else {
                tabBtn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
            }

            tabBtn.setOnAction(e -> {
                activeCategoryFilter = tab;
                renderNotificationFeed(stage);
            });

            row.getChildren().add(tabBtn);
        }

        return row;
    }

    private HBox createSearchFilterRow(Stage stage) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search notifications...");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchInput.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 12;");

        ComboBox<String> typeCB = new ComboBox<>();
        typeCB.getItems().addAll("Type: All", "Type: Requests", "Type: Payments");
        typeCB.setValue("Type: All");

        ComboBox<String> dateCB = new ComboBox<>();
        dateCB.getItems().addAll("Date: All", "Date: Today", "Date: This Week");
        dateCB.setValue("Date: All");

        row.getChildren().addAll(searchInput, typeCB, dateCB);
        return row;
    }

    private VBox createNotificationStreamContainer(Stage stage) {
        VBox card = createCardContainer();
        card.setPadding(new Insets(0));

        notificationStreamContainer = new VBox();
        renderNotificationFeed(stage);

        card.getChildren().add(notificationStreamContainer);
        return card;
    }

    private void renderNotificationFeed(Stage stage) {
        notificationStreamContainer.getChildren().clear();

        List<NotificationItem> filtered = notificationsList.stream().filter(n -> {
            if (activeCategoryFilter.equals("All")) return true;
            if (activeCategoryFilter.startsWith("Unread")) return n.isUnread;
            return n.category.equalsIgnoreCase(activeCategoryFilter);
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox empty = new VBox(10); empty.setAlignment(Pos.CENTER); empty.setPadding(new Insets(30));
            empty.getChildren().add(new Label("No notifications match your current filter."));
            notificationStreamContainer.getChildren().add(empty);
            return;
        }

        for (NotificationItem n : filtered) {
            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14, 18, 14, 18));

            if (n.category.equals("Schedule")) {
                row.setStyle("-fx-background-color: " + RED_BG + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
            } else if (n.isUnread) {
                row.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
            } else {
                row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
            }

            Label iconLbl = new Label(n.iconSymbol);
            iconLbl.setStyle("-fx-font-size: 16px; -fx-background-color: " + WHITE + "; -fx-padding: 8; -fx-background-radius: 50%; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 50%;");

            VBox textGroup = new VBox(4);
            HBox titleRow = new HBox(6);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label(n.title);
            title.setStyle("-fx-font-weight: " + (n.isUnread ? "bold" : "normal") + "; -fx-font-size: 13px; -fx-text-fill: " + (n.category.equals("Schedule") ? RED_TEXT : SIDEBAR_NAVY) + ";");
            titleRow.getChildren().add(title);

            if (n.isUnread) {
                Circle blueDot = new Circle(3, Color.web(PRIMARY_BLUE));
                titleRow.getChildren().add(blueDot);
            }

            Label desc = new Label(n.desc);
            desc.setWrapText(true);
            desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            HBox tagsRow = new HBox(6);
            if (!n.eventTag.isEmpty()) {
                Label t1 = new Label("📋 " + n.eventTag); t1.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 4px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4px;");
                tagsRow.getChildren().add(t1);
            }
            if (!n.budgetTag.isEmpty()) {
                Label t2 = new Label("💰 " + n.budgetTag); t2.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 4px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4px;");
                tagsRow.getChildren().add(t2);
            }

            HBox btnsRow = new HBox(8);
            if (n.category.equals("Schedule")) {
                Button resolveBtn = createPrimaryButton("Resolve Conflict");
                resolveBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10;");
                resolveBtn.setOnAction(e -> showToast(stage, "Resolving schedule conflict..."));
                btnsRow.getChildren().add(resolveBtn);
            } else if (n.category.equals("Service Requests")) {
                Button viewBtn = createPrimaryButton("View Request");
                viewBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 10;");
                viewBtn.setOnAction(e -> showToast(stage, "Opening Service Request..."));

                Button decBtn = createSecondaryButton("Decline");
                decBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 9;");
                decBtn.setOnAction(e -> showToast(stage, "Declining request..."));

                btnsRow.getChildren().addAll(viewBtn, decBtn);
            } else {
                Button viewBtn = createSecondaryButton("View Details");
                viewBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 9;");
                viewBtn.setOnAction(e -> showToast(stage, "Opening details for " + n.title));
                btnsRow.getChildren().add(viewBtn);
            }

            textGroup.getChildren().addAll(titleRow, desc, tagsRow, btnsRow);

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

            Label timeLbl = new Label(n.timeAgo);
            timeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            row.getChildren().addAll(iconLbl, textGroup, sp, timeLbl);

            row.setOnMouseClicked(e -> {
                n.isUnread = false;
                updateFocusDetails(n);
                renderNotificationFeed(stage);
                updateCounts();
            });

            notificationStreamContainer.getChildren().add(row);
        }
    }

    private VBox createNotificationDetailsCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label icon = new Label("🎙"); icon.setStyle("-fx-font-size: 18px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 8; -fx-background-radius: 50%;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label close = new Label("✕"); close.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");
        head.getChildren().addAll(icon, sp, close);

        detailTitleLabel = new Label("TechVision Events requested your Sound System service");
        detailTitleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");
        detailTitleLabel.setWrapText(true);

        detailTimeLabel = new Label("Today at 10:45 AM");
        detailTimeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label msgHead = new Label("MESSAGE"); msgHead.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox msgBox = new VBox();
        msgBox.setPadding(new Insets(10));
        msgBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        detailMsgLabel = new Label("Hello VisionPro Team, We are organizing our Annual Tech Summit and were very impressed with your Sound System portfolio. We need a comprehensive audio setup for our main keynote hall (capacity 500) and two smaller breakout rooms.");
        detailMsgLabel.setWrapText(true);
        detailMsgLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        msgBox.getChildren().add(detailMsgLabel);

        Label summaryHead = new Label("Request Summary"); summaryHead.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        VBox summaryGrid = new VBox(6);
        summaryGrid.setPadding(new Insets(10));
        summaryGrid.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px;");

        detailEventLabel = new Label("• Event: TechVision Annual Summit");
        detailServiceLabel = new Label("• Category: Audio & Visual");
        Label dt = new Label("• Date: 15-16 Oct 2026");
        Label loc = new Label("• Location: Grand Hyatt, Mumbai");
        detailBudgetLabel = new Label("• Budget: ₹85,000");

        Label actionTag = new Label("ACTION REQUIRED");
        actionTag.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 9px; -fx-padding: 2 6; -fx-background-radius: 6px;");

        summaryGrid.getChildren().addAll(detailEventLabel, detailServiceLabel, dt, loc, detailBudgetLabel, actionTag);

        Button acceptBtn = createPrimaryButton("Review & Accept Request");
        acceptBtn.setMaxWidth(Double.MAX_VALUE);
        acceptBtn.setStyle("-fx-background-color: " + SIDEBAR_NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8;");
        acceptBtn.setOnAction(e -> showToast(stage, "Opening Request Review..."));

        Button declineBtn = createSecondaryButton("Decline");
        declineBtn.setMaxWidth(Double.MAX_VALUE);
        declineBtn.setOnAction(e -> showToast(stage, "Declining request..."));

        Button msgOrgBtn = new Button("💬 Message Organizer");
        msgOrgBtn.setMaxWidth(Double.MAX_VALUE);
        msgOrgBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        msgOrgBtn.setOnAction(e -> showToast(stage, "Messaging TechVision Events..."));

        card.getChildren().addAll(head, detailTitleLabel, detailTimeLabel, new Separator(), msgHead, msgBox, summaryHead, summaryGrid, acceptBtn, declineBtn, msgOrgBtn);
        return card;
    }

    private void updateFocusDetails(NotificationItem n) {
        detailTitleLabel.setText(n.title);
        detailTimeLabel.setText("Today at " + n.timeAgo);
        detailMsgLabel.setText(n.fullMessage);
        detailEventLabel.setText("• Event: " + n.eventTag);
        detailBudgetLabel.setText("• Budget: " + n.budgetTag);
    }

    // ==========================================
    // 6. PAGINATION
    // ==========================================
    private HBox createPaginationRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 0, 0, 0));

        Label count = new Label("Showing 1-10 of 24 notifications");
        count.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button prev = createSecondaryButton("Previous");
        Button next = createSecondaryButton("Next");

        row.getChildren().addAll(count, sp, prev, next);
        return row;
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
        topBarBadgeLabel.setText(String.valueOf(unread));
        if (unreadCountCardLabel != null) unreadCountCardLabel.setText(String.valueOf(unread));
    }

    private void markAllAsRead(Stage parentStage) {
        for (NotificationItem n : notificationsList) {
            n.isUnread = false;
        }
        renderNotificationFeed(parentStage);
        updateCounts();
        showToast(parentStage, "✓ All notifications marked as read.");
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
        alert.initOwner(stage);
        alert.show();
    }
}