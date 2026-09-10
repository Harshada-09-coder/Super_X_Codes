package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ProfilePageC {

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String BG = "#F4F7FC";
    private static final String BORDER = "#E2E8F0";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";

    private Label profileHeaderNameLabel;
    private Label profileHeaderEmailLabel;

    private TextField nameField;
    private TextField emailField;
    private TextField mobileField;
    private Button editButton;

    private StackPane toastOverlayContainer;

    private String uid;
    private String idToken;

    public ProfilePageC() {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
    }

    public Scene getProfileScene(Runnable callBackAction1) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Sidebar
        mainLayout.setLeft(createSidebar());

        // 2. Main Profile Content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 35, 30, 35));
        mainContent.setStyle("-fx-background-color:" + BG + ";");

        // Header with Back Button
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 7px; -fx-background-radius: 7px; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (callBackAction1 != null) callBackAction1.run();
            else returnToDashboard();
        });

        VBox headingText = new VBox(2);
        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        Label subtitle = new Label("Manage your personal information and account details.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill:" + TEXT_MUTED + ";");
        headingText.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(backBtn, headingText);

        HBox mainGrid = new HBox(24);

        VBox leftColumn = new VBox(20);
        leftColumn.setPrefWidth(340);
        leftColumn.getChildren().addAll(createProfileCard(), createAccountCard());

        VBox rightColumn = new VBox(20);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);
        rightColumn.getChildren().addAll(createPersonalInformation(), createSecurityCard(), createLogoutButton());

        mainGrid.getChildren().addAll(leftColumn, rightColumn);

        mainContent.getChildren().addAll(header, mainGrid);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG + "; -fx-border-color: transparent;");

        mainLayout.setCenter(scrollPane);

        // 3. In-Scene Toast Notification Overlay (No separate stage spawned!)
        toastOverlayContainer = new StackPane();
        toastOverlayContainer.setAlignment(Pos.TOP_CENTER);
        toastOverlayContainer.setPadding(new Insets(18, 0, 0, 0));
        toastOverlayContainer.setPickOnBounds(false); // Allows clicks to pass through

        StackPane rootStack = new StackPane(mainLayout, toastOverlayContainer);

        loadCustomerProfileAsync();

        return new Scene(rootStack, 1550, 785);
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
                createSidebarItem("Profile", "fas-user", true, null),
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

    private void loadCustomerProfileAsync() {
        String currentUid = uid != null && !uid.isBlank() ? uid : UserSession.getUid();
        if (currentUid == null || currentUid.isBlank()) return;

        Task<CustomerProfile> task = new Task<>() {
            @Override
            protected CustomerProfile call() {
                CustomerController controller = new CustomerController();
                return controller.getCustomerProfile(currentUid);
            }
        };

        task.setOnSucceeded(e -> {
            CustomerProfile profile = task.getValue();
            if (profile != null) {
                Platform.runLater(() -> {
                    String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Customer";
                    String email = profile.getEmail() != null && !profile.getEmail().isBlank() ? profile.getEmail() : UserSession.getUserEmail();
                    String mobile = profile.getMobileNo() != null && !profile.getMobileNo().isBlank() ? profile.getMobileNo() : "Not Provided";

                    if (profileHeaderNameLabel != null) profileHeaderNameLabel.setText(name);
                    if (profileHeaderEmailLabel != null) profileHeaderEmailLabel.setText(email);

                    if (nameField != null) nameField.setText(name);
                    if (emailField != null) emailField.setText(email);
                    if (mobileField != null) mobileField.setText(mobile);
                });
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox createProfileCard() {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");

        StackPane photo = new StackPane();
        Circle photoCircle = new Circle(44, Color.web("#E0F2FE"));
        FontIcon userIcon = new FontIcon("fas-user");
        userIcon.setIconSize(36);
        userIcon.setIconColor(Color.web(PRIMARY_BLUE));
        photo.getChildren().addAll(photoCircle, userIcon);

        profileHeaderNameLabel = new Label("Loading...");
        profileHeaderNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        profileHeaderEmailLabel = new Label("Loading...");
        profileHeaderEmailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");

        Label role = new Label("Customer");
        role.setStyle("-fx-background-color:#E0F2FE; -fx-text-fill:" + PRIMARY_BLUE + "; -fx-font-weight:bold; -fx-font-size:11px; -fx-padding:4 14; -fx-background-radius:12;");

        card.getChildren().addAll(photo, profileHeaderNameLabel, profileHeaderEmailLabel, role);
        return card;
    }

    private VBox createAccountCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");

        Label title = new Label("Account Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        card.getChildren().addAll(
                title,
                new Separator(),
                accountRow("Account Role", "Customer"),
                accountRow("Status", "Active"),
                accountRow("Platform", "EventXpert Desktop")
        );
        return card;
    }

    private HBox accountRow(String left, String right) {
        HBox row = new HBox();
        Label l = new Label(left);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        Label r = new Label(right);
        r.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        row.getChildren().addAll(l, space, r);
        return row;
    }

    private VBox createPersonalInformation() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Personal Details");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");
        editButton.setOnAction(e -> handleEditOrSave());

        header.getChildren().addAll(title, space, editButton);

        GridPane fields = new GridPane();
        fields.setHgap(20);
        fields.setVgap(14);

        nameField = new TextField("Loading...");
        emailField = new TextField("Loading...");
        mobileField = new TextField("Loading...");

        styleField(nameField);
        styleField(emailField);
        styleField(mobileField);

        fields.add(fieldBox("Full Name", nameField), 0, 0);
        fields.add(fieldBox("Email Address", emailField), 1, 0);
        fields.add(fieldBox("Mobile Number", mobileField), 0, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        fields.getColumnConstraints().addAll(col1, col2);

        card.getChildren().addAll(header, new Separator(), fields);
        setFieldsEditable(false);
        return card;
    }

    private VBox fieldBox(String title, Control control) {
        VBox box = new VBox(4);
        Label l = new Label(title);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");
        box.getChildren().addAll(l, control);
        return box;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(36);
        field.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
    }

    private void handleEditOrSave() {
        if (!nameField.isEditable()) {
            setFieldsEditable(true);
            editButton.setText("Save Changes");
            editButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 5 12; -fx-cursor: hand;");
        } else {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newMobile = mobileField.getText().trim();

            if (newName.isEmpty() || newEmail.isEmpty() || newMobile.isEmpty()) {
                showToast("Please fill in all required fields.", false);
                return;
            }

            editButton.setDisable(true);
            editButton.setText("Saving...");

            String currentUid = uid != null && !uid.isBlank() ? uid : UserSession.getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("email", newEmail);
            updates.put("mobileNo", newMobile);

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    CustomerController controller = new CustomerController();
                    return controller.updateCustomerProfileFields(currentUid, updates);
                }
            };

            task.setOnSucceeded(e -> {
                boolean success = task.getValue();
                editButton.setDisable(false);

                if (success) {
                    setFieldsEditable(false);
                    editButton.setText("Edit");
                    editButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");

                    Platform.runLater(() -> {
                        if (profileHeaderNameLabel != null) profileHeaderNameLabel.setText(newName);
                        if (profileHeaderEmailLabel != null) profileHeaderEmailLabel.setText(newEmail);
                    });

                    // IN-SCENE RECTANGLE TOAST POPUP (NO SEPARATE WINDOW/STAGE!)
                    showToast("Profile details updated successfully!", true);
                } else {
                    editButton.setText("Save Changes");
                    showToast("Unable to save profile to database.", false);
                }
            });

            task.setOnFailed(e -> {
                editButton.setDisable(false);
                editButton.setText("Save Changes");
                showToast("An error occurred while updating profile.", false);
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        mobileField.setEditable(editable);

        String style = editable
                ? "-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";"
                : "-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";";

        nameField.setStyle(style);
        emailField.setStyle(style);
        mobileField.setStyle(style);
    }

    // =========================================================================
    // IN-SCENE RECTANGLE TOAST POPUP (Same Stage Overlay)
    // =========================================================================
    private void showToast(String message, boolean isSuccess) {
        Platform.runLater(() -> {
            if (toastOverlayContainer == null) return;
            toastOverlayContainer.getChildren().clear();

            HBox toast = new HBox(10);
            toast.setAlignment(Pos.CENTER_LEFT);
            toast.setPadding(new Insets(10, 20, 10, 16));
            toast.setMaxWidth(380);

            String bg = isSuccess ? "#ECFDF5" : "#FEF2F2";
            String border = isSuccess ? "#6EE7B7" : "#FCA5A5";
            String textCol = isSuccess ? "#065F46" : "#991B1B";
            String iconLit = isSuccess ? "fas-check-circle" : "fas-exclamation-circle";

            toast.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border + "; -fx-border-width: 1.5; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            toast.setEffect(new DropShadow(14, Color.rgb(0, 0, 0, 0.14)));

            FontIcon icon = new FontIcon(iconLit);
            icon.setIconSize(16);
            icon.setIconColor(Color.web(textCol));

            Label lbl = new Label(message);
            lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + textCol + ";");

            toast.getChildren().addAll(icon, lbl);
            toastOverlayContainer.getChildren().add(toast);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toast);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition pause = new PauseTransition(Duration.seconds(2.8));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(350), toast);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> toastOverlayContainer.getChildren().remove(toast));

            SequentialTransition seq = new SequentialTransition(fadeIn, pause, fadeOut);
            seq.play();
        });
    }

    private VBox createSecurityCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-background-radius:12;");

        Label title = new Label("Account Security");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox passBox = new VBox(2);
        Label passLbl = new Label("Password");
        passLbl.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_MUTED + ";");
        Label passDots = new Label("••••••••••••");
        passDots.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        passBox.getChildren().addAll(passLbl, passDots);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Button changePassBtn = new Button("Change Password");
        changePassBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 7 14; -fx-cursor: hand;");
        changePassBtn.setOnAction(e -> showChangePasswordDialog());

        row.getChildren().addAll(passBox, space, changePassBtn);
        card.getChildren().addAll(title, new Separator(), row);
        return card;
    }

    private void showChangePasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update your account password");

        PasswordField oldPassword = new PasswordField(); oldPassword.setPromptText("Current password");
        PasswordField newPassword = new PasswordField(); newPassword.setPromptText("New password");
        PasswordField confirmPassword = new PasswordField(); confirmPassword.setPromptText("Confirm password");

        VBox box = new VBox(10, oldPassword, newPassword, confirmPassword);
        box.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(box);

        ButtonType save = new ButtonType("Update Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        dialog.showAndWait();
    }

    private Button createLogoutButton() {
        Button logout = new Button("Logout of Account");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        logout.setOnAction(e -> {
            UserSession.clearSession();
            LandingPage landing = new LandingPage();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(landing.landingPageScene);
            }
        });
        return logout;
    }

    private void returnToDashboard() {
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
        }
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene(this::returnToDashboard));
        }
    }

    private void openMyEvents() {
        MyEvents myEventsPage = new MyEvents(uid, idToken);
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

    private void openSettings() {
        SettingC settingPage = new SettingC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
        }
    }
} 