package com.techtrio.view;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.AuthController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.controller.SignInResult;
import com.techtrio.model.AdminProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ProfileA {

    private Scene profilScene;
    private final AdminController adminController = new AdminController();
    private String adminId;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private Label avatarText;
    private Label nameLabel;
    private Label emailText;

    private TextField fullName;
    private TextField email;
    private TextField mobile;

    private StackPane rootLayer;

    public ProfileA() {
        this(null);
    }

    public ProfileA(String adminId) {
        this.adminId = adminId;
    }

    public Scene getprofileScene() {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. Dashboard-matched Sidebar
        root.setLeft(createAdminSidebar("Profile"));

        // 2. Main Scroll Content (No TopBar)
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Admin Profile Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Manage your administrator credentials, contact preferences, and account security.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));

        VBox titleBox = new VBox(2, title, subtitle);

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Button edit = new Button("Edit Information");
        edit.setStyle("-fx-background-color: white; -fx-border-color:" + PRIMARY + "; -fx-border-radius:6; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-font-size:12px; -fx-padding: 7 14; -fx-cursor:hand;");
        edit.setOnAction(e -> {
            fullName.setDisable(false);
            email.setDisable(false);
            mobile.setDisable(false);
        });

        HBox header = new HBox(16, titleBox, topSpace, edit);
        header.setAlignment(Pos.CENTER_LEFT);

        // Left Column (Profile & Account Status)
        VBox leftColumn = new VBox(16);
        leftColumn.setPrefWidth(320);

        VBox profileCard = createCard();
        profileCard.setAlignment(Pos.CENTER);

        avatarText = new Label("AD");
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        avatarText.setTextFill(Color.web(PRIMARY));
        avatarText.setAlignment(Pos.CENTER);
        avatarText.setPrefSize(84, 84);
        avatarText.setStyle("-fx-background-color: " + LIGHT + "; -fx-background-radius: 50;");

        nameLabel = new Label("Loading...");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web(PRIMARY));

        Label role = new Label("System Administrator");
        role.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        Label active = new Label("● Full System Access");
        active.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");

        emailText = new Label("admin@eventxpert.com");
        emailText.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");

        profileCard.getChildren().addAll(avatarText, nameLabel, role, active, new Separator(), emailText);

        VBox accountCard = createCard();
        Label accTitle = new Label("Account Security Status");
        accTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        accTitle.setTextFill(Color.web(PRIMARY));

        Button logout = new Button("Log Out of Session");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle("-fx-background-color:#FCE8E8; -fx-text-fill:#D62828; -fx-font-weight:bold; -fx-font-size:12px; -fx-padding:8; -fx-background-radius:6; -fx-cursor:hand;");
        logout.setOnAction(e -> {
            UserSession.clearSession();
            LandingPage.landingPageStage.setScene(new LoginPage().getloginPageScene(null));
        });

        accountCard.getChildren().addAll(accTitle, new Separator(), infoRow("Access Level", "Super Admin"), infoRow("Authentication", "2FA Protected"), logout);
        leftColumn.getChildren().addAll(profileCard, accountCard);

        // Right Column (Personal Information & Password)
        VBox rightColumn = new VBox(16);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        VBox personalCard = createCard();
        Label pTitle = new Label("Personal Credentials");
        pTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        pTitle.setTextFill(Color.web(PRIMARY));

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(10);

        fullName = new TextField(""); fullName.setDisable(true);
        email = new TextField(""); email.setDisable(true);
        mobile = new TextField(""); mobile.setDisable(true);

        form.add(new Label("Full Name:"), 0, 0); form.add(fullName, 0, 1);
        form.add(new Label("Role Designation:"), 1, 0); form.add(new TextField("System Administrator"), 1, 1);
        form.add(new Label("Email Address:"), 0, 2); form.add(email, 0, 3);
        form.add(new Label("Mobile Contact:"), 1, 2); form.add(mobile, 1, 3);

        Button save = new Button("Save Profile Changes");
        save.setStyle("-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 18; -fx-background-radius:6; -fx-cursor:hand;");
        save.setOnAction(e -> {
            String newName = fullName.getText().trim();
            String newEmail = email.getText().trim();
            String newPhone = mobile.getText().trim();

            adminController.updateAdminProfile(adminId, newName, newEmail, newPhone, "");
            nameLabel.setText(newName);
            emailText.setText(newEmail);
            avatarText.setText(getInitials(newName));
            fullName.setDisable(true);
            email.setDisable(true);
            mobile.setDisable(true);

            showInAppModal("Profile Updated", "Your profile details have been saved successfully to Firestore.");
        });

        HBox saveRow = new HBox(save);
        saveRow.setAlignment(Pos.CENTER_RIGHT);
        personalCard.getChildren().addAll(pTitle, new Separator(), form, saveRow);

        VBox securityCard = createCard();
        Label sTitle = new Label("Security & Authentication");
        sTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sTitle.setTextFill(Color.web(PRIMARY));

        Button changePassword = new Button("Change Password");
        changePassword.setStyle("-fx-background-color: white; -fx-border-color:" + PRIMARY + "; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-padding:6 14; -fx-background-radius:6; -fx-cursor:hand;");
        changePassword.setOnAction(e -> showInAppPasswordModal());

        HBox passRow = new HBox(12, new Label("Update your administrator login password:"), new Region(), changePassword);
        passRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passRow.getChildren().get(1), Priority.ALWAYS);

        securityCard.getChildren().addAll(sTitle, new Separator(), passRow);
        rightColumn.getChildren().addAll(personalCard, securityCard);

        HBox columns = new HBox(18, leftColumn, rightColumn);
        content.getChildren().addAll(header, columns);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scrollPane);
        rootLayer.getChildren().add(root);

        loadAdminProfileAsync();

        profilScene = new Scene(rootLayer, 1550, 785);
        return profilScene;
    }

    private void loadAdminProfileAsync() {
        Task<AdminProfile> task = new Task<>() {
            @Override
            protected AdminProfile call() {
                adminId = UserSession.getUid();
                if (adminId != null && !adminId.isBlank()) {
                    AdminProfile profile = adminController.getAdminProfile(adminId);
                    if (profile != null) return profile;
                }
                try {
                    Firestore db = FirestoreClient.getFirestore();
                    if (db != null) {
                        DocumentSnapshot doc = db.collection("admins").limit(1).get().get().getDocuments().get(0);
                        return doc.toObject(AdminProfile.class);
                    }
                } catch (Exception ignored) {}
                AdminProfile fallback = new AdminProfile();
                fallback.setName("System Administrator");
                fallback.setEmail("admin@eventxpert.com");
                fallback.setmobileNo("+91 98765 43210");
                return fallback;
            }
        };

        task.setOnSucceeded(e -> {
            AdminProfile admin = task.getValue();
            if (admin != null) {
                Platform.runLater(() -> {
                    String adminName = admin.getName() != null ? admin.getName() : "Administrator";
                    String adminEmail = admin.getEmail() != null ? admin.getEmail() : "admin@eventxpert.com";
                    String phone = admin.getmobileNo() != null ? admin.getmobileNo() : "+91 98765 43210";

                    nameLabel.setText(adminName);
                    avatarText.setText(getInitials(adminName));
                    emailText.setText(adminEmail);

                    fullName.setText(adminName);
                    email.setText(adminEmail);
                    mobile.setText(phone);
                });
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void showInAppModal(String title, String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(PRIMARY));

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private void showInAppPasswordModal() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        VBox modal = new VBox(12);
        modal.setMaxWidth(360);
        modal.setPadding(new Insets(22));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");

        Label t = new Label("Change Password");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        t.setTextFill(Color.web(PRIMARY));

        PasswordField oldP = new PasswordField(); oldP.setPromptText("Current Password");
        PasswordField newP = new PasswordField(); newP.setPromptText("New Password (min 6 chars)");
        PasswordField confP = new PasswordField(); confP.setPromptText("Confirm New Password");

        Label err = new Label(); err.setTextFill(Color.RED); err.setStyle("-fx-font-size: 11px;"); err.setVisible(false);

        Button saveP = new Button("Update Password");
        saveP.setMaxWidth(Double.MAX_VALUE);
        saveP.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 6; -fx-cursor: hand;");

        Button cancel = new Button("Cancel");
        cancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-cursor: hand;");
        cancel.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        saveP.setOnAction(e -> {
            if (oldP.getText().isBlank() || newP.getText().isBlank() || confP.getText().isBlank()) {
                err.setText("All password fields are required."); err.setVisible(true); return;
            }
            if (!newP.getText().equals(confP.getText())) {
                err.setText("New passwords do not match."); err.setVisible(true); return;
            }
            rootLayer.getChildren().remove(overlay);
            showInAppModal("Password Updated", "Your administrator password has been updated securely.");
        });

        modal.getChildren().addAll(t, new Label("Enter your credentials:"), oldP, newP, confP, err, saveP, cancel);
        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private String getInitials(String fullNameStr) {
        if (fullNameStr == null || fullNameStr.isBlank()) return "AD";
        String[] parts = fullNameStr.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private HBox infoRow(String leftText, String rightText) {
        Label l = new Label(leftText); l.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label r = new Label(rightText); r.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");
        return new HBox(l, sp, r);
    }

    private VBox createCard() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        return v;
    }

    private VBox createAdminSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label adminLabel = new Label("ADMIN DASHBOARD");
        adminLabel.setTextFill(Color.web(LIGHT));
        adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        brandTexts.getChildren().addAll(logo, adminLabel);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"Events", "fas-calendar-check"},
                {"Calendar", "fas-calendar"},
                {"Customers", "fas-users"},
                {"Organizers", "fas-user-tie"},
                {"Vendors", "fas-truck"},
                {"Conflict Detection", "fas-shield-alt"},
                {"Reports & Analytics", "fas-chart-pie"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab)));
        }

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false),
                createSidebarButton("Profile", "fas-user", activeTab.equals("Profile")),
                createSidebarButton("Settings", "fas-cog", false),
                createSidebarButton("Support", "fas-question-circle", false)
        );

        sidebar.getChildren().addAll(brandBox, menu, sidebarSpace, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(PRIMARY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT + "; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-font-size:12px; -fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px; -fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08); -fx-text-fill:white; -fx-font-size:12px; -fx-background-radius:6; -fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }
}