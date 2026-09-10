package com.techtrio.view;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SettingC {

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String BG = "#F4F7FC";
    private static final String BORDER = "#E2E8F0";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";

    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField locationField;

    public Scene getSettingScene(Runnable backAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Sidebar
        root.setLeft(createSidebar());

        // 2. Main Content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 35, 30, 35));
        mainContent.setStyle("-fx-background-color:" + BG + ";");

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 7px; -fx-background-radius: 7px; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (backAction != null) backAction.run();
            else returnToDashboard();
        });

        VBox headingText = new VBox(2);
        Label title = new Label("Account Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        Label subtitle = new Label("Manage your personal preferences, contact details, and notifications.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill:" + TEXT_MUTED + ";");
        headingText.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(backBtn, headingText);

        VBox settingsCard = new VBox(18);
        settingsCard.setPadding(new Insets(24));
        settingsCard.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12px; -fx-background-radius:12px;");

        Label profileTitle = new Label("Profile Information");
        profileTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(14);

        fullNameField = formInput("Loading...");
        emailField = formInput("Loading...");
        phoneField = formInput("Loading...");
        locationField = formInput("India");

        form.add(fieldBox("Full Name", fullNameField), 0, 0);
        form.add(fieldBox("Email Address", emailField), 1, 0);
        form.add(fieldBox("Phone Number", phoneField), 0, 1);
        form.add(fieldBox("Location", locationField), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        form.getColumnConstraints().addAll(col1, col2);

        VBox notificationSection = new VBox(10);
        Label notificationTitle = new Label("Notification Settings");
        notificationTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        CheckBox eventNotification = new CheckBox("Receive notifications about my events");
        eventNotification.setSelected(true);
        CheckBox reminderNotification = new CheckBox("Receive event reminders");
        reminderNotification.setSelected(true);
        CheckBox promotionalNotification = new CheckBox("Receive EventXpert updates and feature recommendations");
        promotionalNotification.setSelected(true);

        notificationSection.getChildren().addAll(notificationTitle, eventNotification, reminderNotification, promotionalNotification);

        settingsCard.getChildren().addAll(profileTitle, new Separator(), form, new Separator(), notificationSection);

        mainContent.getChildren().addAll(header, settingsCard);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG + "; -fx-border-color: transparent;");

        root.setCenter(scrollPane);

        loadCustomerProfileAsync();

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
                createSidebarItem("Notifications", "fas-bell", false, this::openNotifications),
                createSidebarItem("Profile", "fas-user", false, this::openProfile),
                createSidebarItem("Settings", "fas-cog", true, null)
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

    private void loadCustomerProfileAsync() {
        Task<CustomerProfile> task = new Task<>() {
            @Override
            protected CustomerProfile call() {
                CustomerController controller = new CustomerController();
                return controller.getCustomerProfile(UserSession.getUid());
            }
        };

        task.setOnSucceeded(e -> {
            CustomerProfile p = task.getValue();
            if (p != null) {
                Platform.runLater(() -> {
                    String name = p.getName() != null && !p.getName().isBlank() ? p.getName() : "Customer";
                    String email = p.getEmail() != null && !p.getEmail().isBlank() ? p.getEmail() : UserSession.getUserEmail();
                    String mobile = p.getMobileNo() != null && !p.getMobileNo().isBlank() ? p.getMobileNo() : "Not Provided";

                    fullNameField.setText(name);
                    emailField.setText(email);
                    phoneField.setText(mobile);
                });
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private TextField formInput(String text) {
        TextField field = new TextField(text);
        field.setPrefHeight(38);
        field.setEditable(false);
        field.setStyle("-fx-background-color:#F8FAFC; -fx-border-color:" + BORDER + "; -fx-border-radius:6px; -fx-background-radius:6px; -fx-padding:0 12; -fx-font-size:12px; -fx-text-fill:" + TEXT_DARK + ";");
        return field;
    }

    private VBox fieldBox(String title, Control control) {
        VBox box = new VBox(4);
        Label label = new Label(title);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");
        box.getChildren().addAll(label, control);
        return box;
    }

    private void returnToDashboard() {
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(UserSession.getUid(), UserSession.getIdToken());
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
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

    private void openNotifications() {
        Notification notifPage = new Notification();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(notifPage.getNotificationScene());
        }
    }

    private void openProfile() {
        ProfilePageC profilePage = new ProfilePageC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
        }
    }
}