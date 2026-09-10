package com.techtrio.view;

import com.techtrio.controller.CustomerController;
import com.techtrio.controller.CustomerState;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Notification {

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String BG = "#F4F7FC";
    private static final String BORDER = "#E2E8F0";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";

    public Scene getNotificationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Sidebar
        root.setLeft(createSidebar());

        // 2. Main Content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 35, 30, 35));
        mainContent.setStyle("-fx-background-color:" + BG + ";");

        // Header with Back Button
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 7px; -fx-background-radius: 7px; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        backBtn.setOnAction(e -> returnToDashboard());

        VBox headingText = new VBox(2);
        Label title = new Label("Notifications Center");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        Label subtitle = new Label("Manage how you receive alerts and stay updated with your events.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill:" + TEXT_MUTED + ";");
        headingText.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button markAll = new Button("Mark all as read");
        markAll.setStyle("-fx-background-color: white; -fx-text-fill:" + NAVY + "; -fx-border-color:" + BORDER + "; -fx-border-radius: 7px; -fx-background-radius: 7px; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 14; -fx-cursor: hand;");

        Button clearAll = new Button("Clear All");
        clearAll.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-border-radius: 7px; -fx-background-radius: 7px; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 14; -fx-cursor: hand;");

        header.getChildren().addAll(backBtn, headingText, headerSpacer, markAll, clearAll);

        HBox contentGrid = new HBox(20);
        VBox preferences = notificationPreferences();
        VBox recent = recentNotifications();

        HBox.setHgrow(preferences, Priority.ALWAYS);
        HBox.setHgrow(recent, Priority.ALWAYS);

        contentGrid.getChildren().addAll(preferences, recent);

        mainContent.getChildren().addAll(header, contentGrid);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG + "; -fx-border-color: transparent;");

        root.setCenter(scrollPane);
        return new Scene(root, 1550, 785);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(6, 10, 14, 10));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label brandSub = new Label("Plan. Manage. Celebrate.");
        brandSub.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px;");
        brandTexts.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox navList = new VBox(5);
        navList.getChildren().addAll(
                createSidebarItem("Dashboard", "fas-tachometer-alt", false, this::returnToDashboard),
                createSidebarItem("Create Event", "fas-plus-circle", false, this::openCreateEvent),
                createSidebarItem("My Events", "fas-calendar-check", false, this::openMyEvents),
                createSidebarItem("Notifications", "fas-bell", true, null),
                createSidebarItem("Profile", "fas-user", false, this::openProfile),
                createSidebarItem("Settings", "fas-cog", false, this::openSettings)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, navList, spacer);
        return sidebar;
    }

    private Button createSidebarItem(String title, String iconLiteral, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(38);
        btn.setPadding(new Insets(0, 12, 0, 12));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill:" + NAVY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 7px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 7px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 12px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox notificationPreferences() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Circle iconCircle = new Circle(20, Color.web("#E0F2FE"));
        FontIcon icon = new FontIcon("fas-sliders-h");
        icon.setIconSize(16);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        iconBox.getChildren().addAll(iconCircle, icon);

        VBox titleBox = new VBox(2);
        Label t = new Label("Notification Preferences");
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        Label d = new Label("Choose the types of updates and reminders you receive.");
        d.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(t, d);

        titleRow.getChildren().addAll(iconBox, titleBox);

        VBox options = new VBox(0);
        options.getChildren().addAll(
                notificationOption("fas-calendar-day", "Event Reminders", "Receive reminders for upcoming milestone dates.", true),
                notificationOption("fas-sync-alt", "Event Updates", "Get notified about status changes and organizer actions.", true),
                notificationOption("fas-magic", "AI Planning Updates", "Receive suggestions from our constraint optimization engine.", true),
                notificationOption("fas-envelope", "Email Notifications", "Receive copies of key alerts directly to your email.", true),
                notificationOption("fas-shield-alt", "Security Alerts", "Get notified about login activity on your account.", true)
        );

        Button save = new Button("Save Preferences");
        save.setPrefHeight(38);
        save.setMaxWidth(Double.MAX_VALUE);
        save.setStyle("-fx-background-color:" + NAVY + "; -fx-text-fill:white; -fx-background-radius:7; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;");
        save.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Notification preferences updated successfully!");
            a.setHeaderText("Saved");
            a.showAndWait();
        });

        card.getChildren().addAll(titleRow, new Separator(), options, save);
        return card;
    }

    private HBox notificationOption(String iconLiteral, String title, String description, boolean selected) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 4, 12, 4));
        row.setStyle("-fx-border-color:#F1F5F9; -fx-border-width:0 0 1 0;");

        StackPane iconBox = new StackPane();
        Circle circle = new Circle(18, Color.web("#F8FAFC"));
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        iconBox.getChildren().addAll(circle, icon);

        VBox textBox = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        Label descLbl = new Label(description);
        descLbl.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_MUTED + ";");
        textBox.getChildren().addAll(titleLbl, descLbl);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(selected);

        row.getChildren().addAll(iconBox, textBox, space, checkBox);
        return row;
    }

    private VBox recentNotifications() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recent Notifications");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        header.getChildren().addAll(title, space);

        VBox list = new VBox(0);
        list.getChildren().addAll(
                recentNotification("fas-calendar-check", "Event Planning Active", "Your latest event is in planning and assigned to organizer.", "Just now", true),
                recentNotification("fas-wallet", "Budget Optimized", "Smart budget breakdown generated and ready for review.", "2 hrs ago", true),
                recentNotification("fas-cloud-sun", "Weather Update", "Live forecast loaded for your event location.", "Yesterday", false)
        );

        card.getChildren().addAll(header, new Separator(), list);
        return card;
    }

    private HBox recentNotification(String iconLiteral, String title, String message, String time, boolean unread) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 4, 12, 4));
        row.setStyle("-fx-border-color:#F1F5F9; -fx-border-width:0 0 1 0;");

        StackPane iconBox = new StackPane();
        Circle circle = new Circle(18, Color.web(unread ? "#E0F2FE" : "#F8FAFC"));
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        iconBox.getChildren().addAll(circle, icon);

        VBox text = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        Label msgLbl = new Label(message);
        msgLbl.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_MUTED + ";");
        text.getChildren().addAll(titleLbl, msgLbl);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        VBox right = new VBox(4);
        right.setAlignment(Pos.CENTER_RIGHT);
        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill:" + TEXT_MUTED + ";");
        right.getChildren().add(timeLbl);

        if (unread) {
            Circle dot = new Circle(4, Color.web(PRIMARY_BLUE));
            right.getChildren().add(dot);
        }

        row.getChildren().addAll(iconBox, text, space, right);
        return row;
    }

    private void returnToDashboard() {
        String uid = UserSession.getUid();
        String token = UserSession.getIdToken();
        CustomerController controller = new CustomerController();
        CustomerState state = controller.getCustomerState(uid, token);

        if (state != null && state.hasCreatedEvent()) {
            ActiveCustomerDashboardC activeDash = new ActiveCustomerDashboardC(uid, token);
            LandingPage.landingPageStage.setScene(activeDash.getactiveCustomerScene(null));
        } else {
            NewCustomerDashboard newDash = new NewCustomerDashboard(uid, token);
            LandingPage.landingPageStage.setScene(newDash.getnewCustomerScene(null));
        }
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(UserSession.getUid(), UserSession.getIdToken());
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene(this::returnToDashboard));
        }
    }

    private void openMyEvents() {
        MyEvents myEventsPage = new MyEvents(UserSession.getUid(), UserSession.getIdToken());
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(myEventsPage.getMyEventsScene(this::returnToDashboard));
        }
    }

    private void openProfile() {
        ProfilePageC profilePage = new ProfilePageC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
        }
    }

    private void openSettings() {
        SettingC settingPage = new SettingC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
        }
    }
}