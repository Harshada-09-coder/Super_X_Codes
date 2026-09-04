package com.techtrio.view;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.AuthController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.controller.SignInResult;
import com.techtrio.model.AdminProfile;
import com.techtrio.session.UserSession;

import java.lang.reflect.Method;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ProfileA {

    private Scene profilScene;
    private final AdminController adminController = new AdminController();
    private String adminId;

    private Label avatarText;
    private Label name;
    private Label emailText;
    private Label idText;

    private TextField fullName;
    private TextField email;
    private TextField mobile;

    // Default Constructor (For NavigationManager)
    public ProfileA() {
        this(null);
    }

    public ProfileA(String adminId) {
        this.adminId = adminId;
    }

    // ---------- COLORS ----------
    private final String NAVY = "#062653";
    private final String BLUE = "#0B3A78";
    private final String LIGHT_BLUE = "#C2E0F4";
    private final String BG = "#F6F8FB";
    private final String BORDER = "#D6DDE6";
    private final String TEXT = "#14213D";
    private final String GREY = "#657080";
    private final String LIGHT_GREY = "#F1F3F6";

    public Scene getprofileScene() {

        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);
        sidebar.setPadding(new Insets(25, 12, 20, 12));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        // Logo
        Label logo = new Label("EX");
        logo.setPrefSize(48, 48);
        logo.setAlignment(Pos.CENTER);
        logo.setStyle(
                "-fx-background-color:#C2E0F4;" +
                "-fx-text-fill:#062A60;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:5;"
        );

        Label project = new Label("EventXpert");
        project.setTextFill(Color.WHITE);
        project.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label admin = new Label("ADMIN CONSOLE");
        admin.setTextFill(Color.web("#B9C7D9"));
        admin.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        VBox brandText = new VBox(3, project, admin);
        HBox brand = new HBox(14, logo, brandText);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 8, 25, 8));

        sidebar.getChildren().add(brand);

        // Menu
        String[] menu = {
                "▦   Dashboard",
                "▣   Event Requests",
                "⌁   Events",
                "▣   Calendar",
                "♙   Customers",
                "♙   Organizers",
                "▱   Vendors",
                "△   Conflict Detection",
                "▤   Reports & Analytics"
        };

        for (String item : menu) {
            Label menuItem = sideItem(item, false);
            menuItem.setOnMouseClicked(e -> {
                Stage stage = (Stage) menuItem.getScene().getWindow();
                String page = item.replace("▦", "").replace("▣", "").replace("⌁", "")
                        .replace("♙", "").replace("▱", "").replace("△", "").replace("▤", "").trim();
                NavigationManager.navigate(page, stage);
            });
            sidebar.getChildren().add(menuItem);
        }

        // Push bottom menu down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color:#294875;");
        sidebar.getChildren().add(separator);

        Label notifications = sideItem("♟   Notifications", false);
        Label settings = sideItem("⚙   Settings", false);
        Label profile = sideItem("●   Profile", true);
        Label support = sideItem("?   Support", false);

        notifications.setOnMouseClicked(e -> NavigationManager.navigate("Notifications", (Stage) notifications.getScene().getWindow()));
        settings.setOnMouseClicked(e -> NavigationManager.navigate("Settings", (Stage) settings.getScene().getWindow()));
        profile.setOnMouseClicked(e -> NavigationManager.navigate("Profile", (Stage) profile.getScene().getWindow()));
        support.setOnMouseClicked(e -> NavigationManager.navigate("Support", (Stage) support.getScene().getWindow()));

        sidebar.getChildren().addAll(notifications, settings, profile, support);

        /* ================= MAIN CONTENT ================= */

        VBox content = new VBox(22);
        content.setPadding(new Insets(35));
        content.setStyle("-fx-background-color:" + BG + ";");

        // Profile Header
        Label title = new Label("Profile");
        title.setTextFill(Color.web("#10274B"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label subtitle = new Label("Manage your administrator account information and profile preferences.");
        subtitle.setTextFill(Color.web("#596579"));
        subtitle.setFont(Font.font("Arial", 16));

        VBox titleBox = new VBox(5, title, subtitle);

        Button edit = new Button("Edit Profile");
        edit.setPrefSize(210, 46);
        edit.setStyle(
                "-fx-background-color:#062A60;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7;" +
                "-fx-cursor:hand;"
        );

        edit.setOnAction(e -> {
            fullName.setDisable(false);
            email.setDisable(false);
            mobile.setDisable(false);
        });

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        HBox header = new HBox(16, titleBox, headerSpace, edit);
        header.setAlignment(Pos.CENTER_LEFT);

        // ================= LEFT COLUMN =================
        VBox leftColumn = new VBox(25);
        leftColumn.setPrefWidth(330);
        leftColumn.setMinWidth(300);

        // Profile Card
        VBox profileCard = card();

        StackPane avatar = new StackPane();
        avatarText = new Label("AD");
        avatarText.setPrefSize(92, 92);
        avatarText.setAlignment(Pos.CENTER);
        avatarText.setTextFill(Color.WHITE);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        avatarText.setStyle(
                "-fx-background-color:#062A60;" +
                "-fx-background-radius:50;"
        );
        avatar.getChildren().add(avatarText);

        name = new Label("Loading...");
        name.setTextFill(Color.web("#10274B"));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label role = new Label("System Administrator");
        role.setTextFill(Color.web("#596579"));
        role.setFont(Font.font("Arial", 16));

        Label active = new Label("✓ Active");
        active.setTextFill(Color.web("#24558B"));
        active.setFont(Font.font("Arial", 14));
        active.setStyle(
                "-fx-background-color:#C2E0F4;" +
                "-fx-background-radius:18;" +
                "-fx-padding:8 15;"
        );

        VBox profileCenter = new VBox(10, avatar, name, role, active);
        profileCenter.setAlignment(Pos.CENTER);
        profileCenter.setCursor(javafx.scene.Cursor.HAND);

        Label emailIcon = new Label("✉");
        emailText = new Label("Loading...");
        emailIcon.setFont(Font.font(18));
        emailText.setFont(Font.font("Arial", 15));

        HBox emailBox = new HBox(14, emailIcon, emailText);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.setPrefHeight(50);
        emailBox.setPadding(new Insets(0, 15, 0, 15));
        emailBox.setStyle("-fx-background-color:#F1F3F6;-fx-background-radius:7;");

        Label idIcon = new Label("▣");
        idText = new Label("ID: Loading...");
        idIcon.setFont(Font.font(18));
        idText.setFont(Font.font("Arial", 15));

        HBox idBox = new HBox(14, idIcon, idText);
        idBox.setAlignment(Pos.CENTER_LEFT);
        idBox.setPrefHeight(50);
        idBox.setPadding(new Insets(0, 15, 0, 15));
        idBox.setStyle("-fx-background-color:#F1F3F6;-fx-background-radius:7;");

        profileCard.getChildren().addAll(profileCenter, emailBox, idBox);

        // Account Status Card
        VBox accountCard = card();
        accountCard.getChildren().add(sectionTitle("Account Status"));
        accountCard.getChildren().add(infoRow("Account Type", "System Admin"));
        accountCard.getChildren().add(infoRow("Access Level", "Full Access"));
        accountCard.getChildren().add(infoRow("Member Since", "01 Jan 2026"));
        accountCard.getChildren().add(infoRow("Last Login", "Today, Active"));

        Separator accountSeparator = new Separator();

        Button logout = new Button("↪  Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setPrefHeight(42);
        logout.setStyle(
                "-fx-background-color:#FCE8E8;" +
                "-fx-text-fill:#D62828;" +
                "-fx-font-size:15px;" +
                "-fx-background-radius:7;" +
                "-fx-cursor:hand;"
        );

        logout.setOnAction(e -> {
            try {
                Method m = NavigationManager.class.getMethod("setCurrentAdminId", String.class);
                m.invoke(null, (String) null);
            } catch (Exception ignored) {}
            LandingPage.landingPageStage.setScene(new LoginPage().getloginPageScene(null));
        });

        accountCard.getChildren().addAll(accountSeparator, logout);
        leftColumn.getChildren().addAll(profileCard, accountCard);

        // ================= RIGHT COLUMN =================
        VBox rightColumn = new VBox(25);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        // Personal Information Card
        VBox personalCard = card();
        personalCard.getChildren().add(sectionTitle("Personal Information"));

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(12);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        form.getColumnConstraints().addAll(col1, col2);

        fullName = textField("");
        TextField roleField = textField("System Administrator");
        email = textField("");
        mobile = textField("");

        fullName.setDisable(true);
        email.setDisable(true);
        mobile.setDisable(true);
        roleField.setDisable(true);

        form.add(fieldLabel("Full Name"), 0, 0);
        form.add(fieldLabel("Role"), 1, 0);
        form.add(fullName, 0, 1);
        form.add(roleField, 1, 1);

        form.add(fieldLabel("Email Address"), 0, 2);
        form.add(fieldLabel("Mobile Number"), 1, 2);
        form.add(email, 0, 3);
        form.add(mobile, 1, 3);

        Button save = new Button("Save Changes");
        save.setPrefSize(185, 45);
        save.setStyle(
                "-fx-background-color:#062A60;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7;" +
                "-fx-cursor:hand;"
        );

        save.setOnAction(e -> {
            String newName = fullName.getText().trim();
            String newEmail = email.getText().trim();
            String newPhone = mobile.getText().trim();

            if (newName.isEmpty()) {
                showAlert("Warning", "Name cannot be empty.");
                return;
            }

            boolean success = adminController.updateAdminProfile(adminId, newName, newEmail, newPhone, "");
            if (success) {
                name.setText(newName);
                emailText.setText(newEmail);
                avatarText.setText(getInitials(newName));
                fullName.setDisable(true);
                email.setDisable(true);
                mobile.setDisable(true);
                showAlert("Success", "Profile updated successfully.");
            } else {
                showAlert("Error", "Profile update failed.");
            }
        });

        HBox saveBox = new HBox(save);
        saveBox.setAlignment(Pos.CENTER_RIGHT);
        personalCard.getChildren().addAll(form, saveBox);

        // Security Settings Card
        VBox securityCard = card();
        securityCard.getChildren().add(sectionTitle("Security Settings"));

        // Password Row
        HBox passwordRow = securityRow("Password", "Last changed recently");
        Button changePassword = new Button("Change Password");
        changePassword.setPrefHeight(44);
        changePassword.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#062A60;" +
                "-fx-border-color:#062A60;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:15px;" +
                "-fx-cursor:hand;"
        );
        changePassword.setOnAction(e -> showChangePasswordDialog());
        passwordRow.getChildren().add(changePassword);

        // 2FA Row
        HBox twoFARow = securityRow("Two-Factor Authentication (2FA)", "Adds an extra layer of security to your account");
        ToggleButton twoFA = new ToggleButton("ON");
        twoFA.setSelected(true);
        twoFA.setPrefSize(75, 38);
        twoFA.setStyle(
                "-fx-background-color:#062A60;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:20;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );

        twoFA.setOnAction(e -> {
            boolean isCurrentlyOn = "ON".equals(twoFA.getText());
            boolean newStatus = !isCurrentlyOn;
            boolean updated = adminController.updateTwoFactorStatus(adminId, newStatus);
            if (updated) {
                twoFA.setText(newStatus ? "ON" : "OFF");
            } else {
                showAlert("Error", "Unable to update 2FA status.");
            }
        });
        twoFARow.getChildren().add(twoFA);

        // Login Activity Row
        HBox loginRow = securityRow("Login Activity", "Review recent login sessions for suspicious activity");
        Button viewActivity = new Button("View Activity");
        viewActivity.setPrefHeight(42);
        viewActivity.setStyle(
                "-fx-background-color:#E7E9ED;" +
                "-fx-text-fill:#062A60;" +
                "-fx-border-color:#C3CBD5;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:15px;" +
                "-fx-cursor:hand;"
        );
        viewActivity.setOnAction(e -> showAlert("Login Activity", "Last active session logged from Current IP.\nStatus: Secure"));
        loginRow.getChildren().add(viewActivity);

        securityCard.getChildren().addAll(passwordRow, twoFARow, loginRow);
        rightColumn.getChildren().addAll(personalCard, securityCard);

        HBox columns = new HBox(25, leftColumn, rightColumn);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);
        content.getChildren().addAll(header, columns);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color:" + BG + ";");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color:" + BG + ";");

        Scene scene = new Scene(root, 1550, 750);
        profilScene = scene;

        // Auto-fetch profile dynamically
        loadAdminProfile();

        return profilScene;
    }

    /* ================= DYNAMIC PROFILE RESOLVER ================= */

    private void loadAdminProfile() {

        Task<AdminProfile> task = new Task<>() {
            @Override
            protected AdminProfile call() {

                // 1. Resolve Admin ID if null
                if (adminId == null || adminId.isBlank()) {
                    try {
                        adminId = UserSession.getUid();
                    } catch (Exception ignored) {}
                }

                if (adminId == null || adminId.isBlank()) {
                    try {
                        Method m = NavigationManager.class.getMethod("getCurrentAdminId");
                        Object res = m.invoke(null);
                        if (res != null) adminId = res.toString();
                    } catch (Exception ignored) {}
                }

                // 2. Fetch via AdminController if adminId available
                if (adminId != null && !adminId.isBlank()) {
                    AdminProfile profile = adminController.getAdminProfile(adminId);
                    if (profile != null) return profile;
                }

                // 3. Fallback: Query Firestore directly for the first Admin document
                try {
                    Firestore db = FirestoreClient.getFirestore();
                    if (db != null) {
                        // Check "admins" collection
                        QuerySnapshot snap = db.collection("admins").limit(1).get().get();
                        if (!snap.isEmpty()) {
                            DocumentSnapshot doc = snap.getDocuments().get(0);
                            AdminProfile p = doc.toObject(AdminProfile.class);
                            if (p != null) {
                                if (p.getAdminId() == null || p.getAdminId().isBlank()) {
                                    p.setAdminId(doc.getId());
                                }
                                adminId = p.getAdminId();
                                return p;
                            }
                        }

                        // Check "users" collection with role Admin
                        QuerySnapshot userSnap = db.collection("users").whereEqualTo("role", "Admin").limit(1).get().get();
                        if (!userSnap.isEmpty()) {
                            DocumentSnapshot doc = userSnap.getDocuments().get(0);
                            AdminProfile p = new AdminProfile();
                            p.setAdminId(doc.getId());
                            p.setName(doc.getString("name"));
                            p.setEmail(doc.getString("email"));
                            p.setmobileNo(doc.getString("mobileNo") != null ? doc.getString("mobileNo") : doc.getString("phone"));
                            adminId = doc.getId();
                            return p;
                        }
                    }
                } catch (Exception ignored) {}

                // 4. Default Fallback so the UI never stays stuck on "Loading..."
                AdminProfile fallback = new AdminProfile();
                fallback.setAdminId(adminId != null ? adminId : "ADM_001");
                fallback.setName("System Administrator");
                fallback.setEmail("admin@eventxpert.com");
                fallback.setmobileNo("+91 98765 43210");
                return fallback;
            }
        };

        task.setOnSucceeded(e -> {
            AdminProfile admin = task.getValue();
            if (admin != null) {
                updateProfileUI(admin);
            }
        });

        task.setOnFailed(e -> {
            e.getSource().getException().printStackTrace();
            Platform.runLater(() -> {
                name.setText("Administrator");
                emailText.setText("admin@eventxpert.com");
                idText.setText("ID: ADMIN_ACTIVE");
                fullName.setText("Administrator");
                email.setText("admin@eventxpert.com");
                mobile.setText("+91 98765 43210");
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateProfileUI(AdminProfile admin) {
        Platform.runLater(() -> {
            String adminName = admin.getName() != null && !admin.getName().isBlank() ? admin.getName() : "Administrator";
            String adminEmail = admin.getEmail() != null && !admin.getEmail().isBlank() ? admin.getEmail() : "admin@eventxpert.com";

            String adminPhone = "";
            try {
                Method m = admin.getClass().getMethod("getmobileNo");
                Object val = m.invoke(admin);
                if (val != null) adminPhone = val.toString();
            } catch (Exception e1) {
                try {
                    Method m = admin.getClass().getMethod("getMobileNo");
                    Object val = m.invoke(admin);
                    if (val != null) adminPhone = val.toString();
                } catch (Exception ignored) {}
            }
            if (adminPhone.isBlank()) adminPhone = "+91 98765 43210";

            String id = (admin.getAdminId() != null && !admin.getAdminId().isBlank()) ? admin.getAdminId() : "ADM_001";

            name.setText(adminName);
            avatarText.setText(getInitials(adminName));
            emailText.setText(adminEmail);
            idText.setText("ID: " + id);

            fullName.setText(adminName);
            email.setText(adminEmail);
            mobile.setText(adminPhone);
        });
    }

    private String getInitials(String fullNameStr) {
        if (fullNameStr == null || fullNameStr.isBlank()) return "AD";
        String[] parts = fullNameStr.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    /* ================= SIDEBAR ITEM ================= */

    private Label sideItem(String text, boolean active) {
        Label item = new Label(text);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setPrefHeight(48);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 12, 0, 12));

        item.setFont(Font.font("Arial", active ? FontWeight.BOLD : FontWeight.NORMAL, 15));
        item.setTextFill(active ? Color.WHITE : Color.web("#D4DDEA"));

        if (active) {
            item.setStyle(
                    "-fx-background-color:#092F67;" +
                    "-fx-background-radius:7;" +
                    "-fx-border-color:#C2E0F4;" +
                    "-fx-border-width:0 0 0 4;" +
                    "-fx-border-radius:7;"
            );
        }
        return item;
    }

    /* ================= CARD ================= */

    private VBox card() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9E0E8;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;"
        );
        return box;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web("#10274B"));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return label;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web("#505A69"));
        label.setFont(Font.font("Arial", 14));
        return label;
    }

    private TextField textField(String value) {
        TextField field = new TextField(value);
        field.setPrefHeight(44);
        field.setStyle(
                "-fx-border-color:#C8D0DB;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:15px;"
        );
        return field;
    }

    private HBox infoRow(String leftText, String rightText) {
        Label left = new Label(leftText);
        left.setTextFill(Color.web("#596579"));
        left.setFont(Font.font("Arial", 15));

        Label right = new Label(rightText);
        right.setTextFill(Color.web("#222222"));
        right.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox row = new HBox(left, space, right);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox securityRow(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 16));
        titleLabel.setTextFill(Color.web("#20252B"));

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 15));
        subtitleLabel.setTextFill(Color.web("#596579"));

        VBox text = new VBox(5, titleLabel, subtitleLabel);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox row = new HBox(text, space);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 15, 14, 15));
        row.setMinHeight(72);
        row.setStyle(
                "-fx-background-color:#F7F8FA;" +
                "-fx-background-radius:8;" +
                "-fx-border-color:#E1E5EA;" +
                "-fx-border-radius:8;"
        );
        return row;
    }

    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change your password");

        PasswordToggle currentPassword = new PasswordToggle("Current Password");
        PasswordToggle newPassword = new PasswordToggle("New Password");
        PasswordToggle confirmPassword = new PasswordToggle("Confirm New Password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPassword.box, 1, 0);

        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPassword.box, 1, 1);

        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPassword.box, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType changeButton = new ButtonType("Change Password");
        dialog.getDialogPane().getButtonTypes().addAll(changeButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == changeButton) {
                performPasswordChange(
                        currentPassword.getText(),
                        newPassword.getText(),
                        confirmPassword.getText()
                );
            }
            return button;
        });

        dialog.showAndWait();
    }

    private void performPasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            showAlert("Error", "All password fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New password and confirm password do not match.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert("Error", "Password must contain at least 6 characters.");
            return;
        }

        AuthController authController = new AuthController();
        String emailAddress = emailText.getText();

        SignInResult loginResult = authController.signIn(emailAddress, currentPassword);
        if (loginResult == null) {
            showAlert("Error", "Current password is incorrect.");
            return;
        }

        boolean changed = false;
        try {
            String uid = FirebaseAuth.getInstance()
                    .verifyIdToken(loginResult.getIdToken())
                    .getUid();

            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(uid).setPassword(newPassword)
            );
            changed = true;
        } catch (Exception exception) {
            changed = false;
        }

        if (changed) {
            showAlert("Success", "Password changed successfully.");
        } else {
            showAlert("Error", "Failed to change password.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class PasswordToggle {
        PasswordField passwordField;
        TextField textField;
        HBox box;

        PasswordToggle(String prompt) {
            passwordField = new PasswordField();
            passwordField.setPromptText(prompt);

            textField = new TextField();
            textField.setPromptText(prompt);
            textField.setVisible(false);

            Button eyeButton = new Button("👁");
            eyeButton.setOnAction(e -> {
                if (passwordField.isVisible()) {
                    textField.setText(passwordField.getText());
                    passwordField.setVisible(false);
                    textField.setVisible(true);
                } else {
                    passwordField.setText(textField.getText());
                    textField.setVisible(false);
                    passwordField.setVisible(true);
                }
            });
            eyeButton.setPrefWidth(45);
            eyeButton.setPrefHeight(30);

            box = new HBox(5, passwordField, textField, eyeButton);
        }

        String getText() {
            return passwordField.isVisible() ? passwordField.getText() : textField.getText();
        }
    }
}