package com.techtrio.view;


import com.techtrio.controller.NavigationManager;
import com.techtrio.controller.SignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.AuthController;
import com.techtrio.model.AdminProfile;
import com.techtrio.view.LoginPage;
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

public class ProfileA  {

        private Scene profilScene;

        private final AdminController adminController = new AdminController();

        private String adminId;

      
        private Label name;
        private Label emailText;
        private Label idText;

        private TextField fullName;
        private TextField email;
        private TextField mobile;

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

    public Scene getprofileScene(){

        
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

        Stage stage =
                (Stage) menuItem.getScene().getWindow();

        String page = item
                .replace("▦", "")
                .replace("▣", "")
                .replace("⌁", "")
                .replace("♙", "")
                .replace("▱", "")
                .replace("△", "")
                .replace("▤", "")
                .trim();

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

        Label notifications =
        sideItem("♟   Notifications", false);

Label settings =
        sideItem("⚙   Settings", false);

Label profile =
        sideItem("●   Profile", true);

Label support =
        sideItem("?   Support", false);


notifications.setOnMouseClicked(e -> {

    Stage stage =
            (Stage) notifications.getScene().getWindow();

    NavigationManager.navigate("Notifications", stage);
});


settings.setOnMouseClicked(e -> {

    Stage stage =
            (Stage) settings.getScene().getWindow();

    NavigationManager.navigate("Settings", stage);
});


profile.setOnMouseClicked(e -> {

    Stage stage =
            (Stage) profile.getScene().getWindow();

    NavigationManager.navigate("Profile", stage);
});


support.setOnMouseClicked(e -> {

    Stage stage =
            (Stage) support.getScene().getWindow();

    NavigationManager.navigate("Support", stage);
});


sidebar.getChildren().addAll(
        notifications,
        settings,
        profile,
        support
);

       
        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content = new VBox(22);
        content.setPadding(new Insets(35));
        content.setStyle("-fx-background-color:" + BG + ";");


        // =====================================================
        // PROFILE HEADER
        // =====================================================

        Label title = new Label("Profile");
        title.setTextFill(Color.web("#10274B"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label subtitle = new Label(
                "Manage your administrator account information and profile preferences."
        );
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
                "-fx-background-radius:7;"
        );

        edit.setOnAction(e -> {

                fullName.setDisable(false);
                email.setDisable(false);
                mobile.setDisable(false);

                System.out.println("Edit Profile enabled");

        });

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        HBox header = new HBox(
                16,
                titleBox,
                headerSpace,
                edit
        );

        header.setAlignment(Pos.CENTER_LEFT);


        // =====================================================
        // LEFT COLUMN
        // =====================================================

        VBox leftColumn = new VBox(25);
        leftColumn.setPrefWidth(330);
        leftColumn.setMinWidth(300);


        // ---------------- PROFILE CARD ----------------

        VBox profileCard = card();

        StackPane avatar = new StackPane();

        Label avatarText = new Label("AD");
        avatarText.setPrefSize(92, 92);
        avatarText.setAlignment(Pos.CENTER);
        avatarText.setTextFill(Color.WHITE);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
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

        VBox profileCenter = new VBox(
                10,
                avatar,
                name,
                role,
                active
        );

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
        emailBox.setStyle(
                "-fx-background-color:#F1F3F6;" +
                "-fx-background-radius:7;"
        );

        Label idIcon = new Label("▣");
        idText = new Label("ID: Loading...");

        idIcon.setFont(Font.font(18));
        idText.setFont(Font.font("Arial", 15));

        HBox idBox = new HBox(14, idIcon, idText);
        idBox.setAlignment(Pos.CENTER_LEFT);
        idBox.setPrefHeight(50);
        idBox.setPadding(new Insets(0, 15, 0, 15));
        idBox.setStyle(
                "-fx-background-color:#F1F3F6;" +
                "-fx-background-radius:7;"
        );

        profileCard.getChildren().addAll(
                profileCenter,
                emailBox,
                idBox
        );


        // ---------------- ACCOUNT STATUS ----------------

        VBox accountCard = card();

        accountCard.getChildren().add(
                sectionTitle("Account Status")
        );

        accountCard.getChildren().add(
                infoRow("Account Type", "System Admin")
        );

        accountCard.getChildren().add(
                infoRow("Access Level", "Full Access")
        );

        accountCard.getChildren().add(
                infoRow("Member Since", "01 Jan 2026")
        );

        accountCard.getChildren().add(
                infoRow("Last Login", "Today, 10:30 AM")
        );

        Separator accountSeparator = new Separator();

        Button logout = new Button("↪  Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setPrefHeight(42);
        logout.setStyle(
                "-fx-background-color:#FCE8E8;" +
                "-fx-text-fill:#D62828;" +
                "-fx-font-size:15px;" +
                "-fx-background-radius:7;"
        );
        logout.setOnAction(e -> {

    // Clear current admin session
    NavigationManager.setCurrentAdminId(null);

    // Go to Login Page
    LandingPage.landingPageStage.setScene(
          new LoginPage().getloginPageScene(null)
    );
});
        


        

        accountCard.getChildren().addAll(
                accountSeparator,
                logout
        );

        leftColumn.getChildren().addAll(
                profileCard,
                accountCard
        );


        // =====================================================
        // RIGHT COLUMN
        // =====================================================

        VBox rightColumn = new VBox(25);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);


        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        VBox personalCard = card();

        personalCard.getChildren().add(
                sectionTitle("Personal Information")
        );

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
                "-fx-background-radius:7;"
        );
        save.setOnAction(e -> {

    String newName =
            fullName.getText().trim();

    String newEmail =
            email.getText().trim();

    String newPhone =
            mobile.getText().trim();

    if (newName.isEmpty()) {

        System.out.println(
                "Name cannot be empty."
        );

        return;
    }

    boolean success =
            adminController.updateAdminProfile(
                    adminId,
                    newName,
                    newEmail,
                    newPhone,
                    ""
            );

    if (success) {

        name.setText(newName);

                        emailText.setText(newEmail);
                        fullName.setDisable(true);
                        email.setDisable(true);
                        mobile.setDisable(true);

        System.out.println(
                "Profile updated successfully."
        );

    } else {

        System.out.println(
                "Profile update failed."
        );
    }
});



        HBox saveBox = new HBox(save);
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        personalCard.getChildren().addAll(
                form,
                saveBox
        );


        // =====================================================
        // SECURITY SETTINGS
        // =====================================================

        VBox securityCard = card();

        securityCard.getChildren().add(
                sectionTitle("Security Settings")
        );


        // Password
        HBox passwordRow = securityRow(
                "Password",
                "Last changed 30 days ago"
        );

        Button changePassword = new Button("Change Password");
        changePassword.setPrefHeight(44);
        changePassword.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#062A60;" +
                "-fx-border-color:#062A60;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:15px;"
        );
        changePassword.setOnAction(e -> {
    showChangePasswordDialog();
});

        passwordRow.getChildren().add(changePassword);


        // 2FA
        HBox twoFARow = securityRow(
                "Two-Factor Authentication (2FA)",
                "Adds an extra layer of security to your account"
        );

        ToggleButton twoFA = new ToggleButton("ON");
        twoFA.setSelected(true);
        twoFA.setPrefSize(75, 38);
        twoFA.setStyle(
                "-fx-background-color:#062A60;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:20;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        twoFA.setOnAction(e -> {

    boolean currentlyOn =
            twoFA.getText()
                    .equals("ON");

    boolean newStatus =
            !currentlyOn;

    AdminController controller =
            new AdminController();

    boolean updated =
            controller.updateTwoFactorStatus(
                    adminId,
                    newStatus
            );

    if (updated) {

        if (newStatus) {
            twoFA.setText("ON");
        } else {
            twoFA.setText("OFF");
        }

    } else {

        showAlert(
                "Error",
                "Unable to update 2FA status."
        );
    }
});

        twoFARow.getChildren().add(twoFA);


        // Login Activity
        HBox loginRow = securityRow(
                "Login Activity",
                "Review recent login sessions for suspicious activity"
        );

        Button viewActivity = new Button("View Activity");
        viewActivity.setPrefHeight(42);
        viewActivity.setStyle(
                "-fx-background-color:#E7E9ED;" +
                "-fx-text-fill:#062A60;" +
                "-fx-border-color:#C3CBD5;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:15px;"
        );

        loginRow.getChildren().add(viewActivity);

        securityCard.getChildren().addAll(
                passwordRow,
                twoFARow,
                loginRow
        );

        rightColumn.getChildren().addAll(
                personalCard,
                securityCard
        );


        // =====================================================
        // TWO COLUMN LAYOUT
        // =====================================================

        HBox columns = new HBox(
                25,
                leftColumn,
                rightColumn
        );

        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        content.getChildren().addAll(
                header,
                columns
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color:" + BG + ";"
        );


        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        // Sidebar directly starts from TOP
        root.setLeft(sidebar);

        // No TopBar
        root.setCenter(scrollPane);

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );


        // =====================================================
        // SCENE
        // =====================================================

        Scene scene = new Scene(root,1550,750);
        profilScene =  scene;
        loadAdminProfile();
        
    return profilScene;
}


    // =====================================================
    // SIDEBAR ITEM
    // =====================================================

    private Label sideItem(String text, boolean active) {

        Label item = new Label(text);

        item.setMaxWidth(Double.MAX_VALUE);
        item.setPrefHeight(48);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 12, 0, 12));

        item.setFont(
                Font.font(
                        "Arial",
                        active
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        15
                )
        );

        item.setTextFill(
                active
                        ? Color.WHITE
                        : Color.web("#D4DDEA")
        );

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


    // =====================================================
    // CARD
    // =====================================================

    private VBox card() {

        VBox box = new VBox(15);

        box.setPadding(
                new Insets(25)
        );

        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9E0E8;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;"
        );

        return box;
    }


    // =====================================================
    // SECTION TITLE
    // =====================================================

    private Label sectionTitle(String text) {

        Label label = new Label(text);

        label.setTextFill(
                Color.web("#10274B")
        );

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        return label;
    }


    // =====================================================
    // FORM LABEL
    // =====================================================

    private Label fieldLabel(String text) {

        Label label = new Label(text);

        label.setTextFill(
                Color.web("#505A69")
        );

        label.setFont(
                Font.font("Arial", 14)
        );

        return label;
    }


    // =====================================================
    // TEXT FIELD
    // =====================================================

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


    // =====================================================
    // ACCOUNT ROW
    // =====================================================

    private HBox infoRow(
            String leftText,
            String rightText
    ) {

        Label left = new Label(leftText);
        left.setTextFill(
                Color.web("#596579")
        );
        left.setFont(
                Font.font("Arial", 15)
        );

        Label right = new Label(rightText);
        right.setTextFill(
                Color.web("#222222")
        );
        right.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        HBox row = new HBox(
                left,
                space,
                right
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        return row;
    }


    // =====================================================
    // SECURITY ROW
    // =====================================================

    private HBox securityRow(
            String title,
            String subtitle
    ) {

        Label titleLabel = new Label(title);

        titleLabel.setFont(
                Font.font("Arial", 16)
        );

        titleLabel.setTextFill(
                Color.web("#20252B")
        );

        Label subtitleLabel = new Label(subtitle);

        subtitleLabel.setFont(
                Font.font("Arial", 15)
        );

        subtitleLabel.setTextFill(
                Color.web("#596579")
        );

        VBox text = new VBox(
                5,
                titleLabel,
                subtitleLabel
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        HBox row = new HBox(
                text,
                space
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(14, 15, 14, 15)
        );

        row.setMinHeight(72);

        row.setStyle(
                "-fx-background-color:#F7F8FA;" +
                "-fx-background-radius:8;" +
                "-fx-border-color:#E1E5EA;" +
                "-fx-border-radius:8;"
        );

        return row;
    }

    private void loadAdminProfile() {

    if (adminId == null || adminId.isBlank()) {

        System.out.println(
                "Admin ID is not available."
        );

        return;
    }

    Task<AdminProfile> task =
            new Task<>() {

        @Override
        protected AdminProfile call() {

            return adminController
                    .getAdminProfile(adminId);
        }
    };

    task.setOnSucceeded(e -> {

        AdminProfile admin =
                task.getValue();

        if (admin != null) {

            updateProfileUI(admin);

        } else {

            System.out.println(
                    "Admin profile not found."
            );
        }


    });
    

    task.setOnFailed(e -> {

        e.getSource()
         .getException()
         .printStackTrace();
    });

    Thread thread = new Thread(task);

    thread.setDaemon(true);

    thread.start();
    }



    private void updateProfileUI(
        AdminProfile admin) {

    Platform.runLater(() -> {

        String adminName =
                admin.getName() != null
                        ? admin.getName()
                        : "";

        String adminEmail =
                admin.getEmail() != null
                        ? admin.getEmail()
                        : "";

        String adminPhone =
                admin.getmobileNo() != null
                        ? admin.getmobileNo()
                        : "";

        name.setText(adminName);

        emailText.setText(adminEmail);

        idText.setText(
                "ID: " +
                admin.getAdminId()
        );

        fullName.setText(adminName);

        email.setText(adminEmail);

        mobile.setText(adminPhone);
    });
}

    private void showChangePasswordDialog() {

    Dialog<ButtonType> dialog = new Dialog<>();

    dialog.setTitle("Change Password");
    dialog.setHeaderText("Change your password");

    PasswordToggle currentPassword =
            new PasswordToggle("Current Password");

    PasswordToggle newPassword =
            new PasswordToggle("New Password");

    PasswordToggle confirmPassword =
            new PasswordToggle("Confirm New Password");

    GridPane grid = new GridPane();

    grid.setHgap(10);
    grid.setVgap(15);
    grid.setPadding(new Insets(20));

    grid.add(
            new Label("Current Password:"),
            0, 0
    );

    grid.add(
            currentPassword.box,
            1, 0
    );

    grid.add(
            new Label("New Password:"),
            0, 1
    );

    grid.add(
            newPassword.box,
            1, 1
    );

    grid.add(
            new Label("Confirm Password:"),
            0, 2
    );

    grid.add(
            confirmPassword.box,
            1, 2
    );

    dialog.getDialogPane().setContent(grid);

    ButtonType changeButton =
            new ButtonType("Change Password");

    dialog.getDialogPane()
            .getButtonTypes()
            .addAll(
                    changeButton,
                    ButtonType.CANCEL
            );

    dialog.setResultConverter(button -> {

        if (button == changeButton) {

            String current =
                    currentPassword.getText();

            String newPass =
                    newPassword.getText();

            String confirm =
                    confirmPassword.getText();

            performPasswordChange(
                    current,
                    newPass,
                    confirm
            );
        }

        return button;
    });

    dialog.showAndWait();






}


private void performPasswordChange(
        String currentPassword,
        String newPassword,
        String confirmPassword) {

    if (currentPassword.isBlank()
            || newPassword.isBlank()
            || confirmPassword.isBlank()) {

        showAlert(
                "Error",
                "All password fields are required."
        );

        return;
    }

    if (!newPassword.equals(confirmPassword)) {

        showAlert(
                "Error",
                "New password and confirm password do not match."
        );

        return;
    }

    if (newPassword.length() < 6) {

        showAlert(
                "Error",
                "Password must contain at least 6 characters."
        );

        return;
    }

    AuthController authController =
            new AuthController();

    /*
     * Current logged-in user's email
     */
    String emailAddress =
            emailText.getText();

    /*
     * Verify current password
     */
    SignInResult loginResult =
            authController.signIn(
                    emailAddress,
                    currentPassword
            );

    if (loginResult == null) {

        showAlert(
                "Error",
                "Current password is incorrect."
        );

        return;
    }

    /*
     * Current password correct.
     * Now update password.
     */
    boolean changed =
            authController.changePassword(
                    loginResult.getIdToken(),
                    newPassword
            );

    if (changed) {

        showAlert(
                "Success",
                "Password changed successfully."
        );

    } else {

        showAlert(
                "Error",
                "Failed to change password."
        );
    }
}
private void showAlert(
        String title,
        String message) {

    Alert alert =
            new Alert(Alert.AlertType.INFORMATION);

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

        // सुरुवातीला password hide
        textField.setVisible(false);

        Button eyeButton = new Button("👁");

        eyeButton.setOnAction(e -> {

            if (passwordField.isVisible()) {

                // SHOW PASSWORD
                textField.setText(passwordField.getText());

                passwordField.setVisible(false);
                textField.setVisible(true);

            } else {

                // HIDE PASSWORD
                passwordField.setText(textField.getText());

                textField.setVisible(false);
                passwordField.setVisible(true);
            }
        });
        eyeButton.setPrefWidth(45);
eyeButton.setPrefHeight(30);

        box = new HBox(5);

        box.getChildren().addAll(
                passwordField,
                textField,
                eyeButton
        );
    }

    String getText() {

        if (passwordField.isVisible()) {
            return passwordField.getText();
        } else {
            return textField.getText();
        }
    }
}


}
  

   

    