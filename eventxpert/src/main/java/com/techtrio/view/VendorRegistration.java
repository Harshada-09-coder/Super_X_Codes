package com.techtrio.view;

import com.techtrio.controller.AuthController;
import com.techtrio.controller.SignUpResult;
import com.techtrio.controller.VendorController;
import com.techtrio.model.VendorProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VendorRegistration {

    private Scene vendorRegistrationScene;
    private StackPane rootLayer;

    private static final String COLOR_NAVY = "#062A60";
    private static final String COLOR_PRIMARY_BLUE = "#0B4EA2";
    private static final String BORDER_COLOR = "#DCE5F0";

    private TextField txtFullName;
    private TextField txtEmailAddress;
    private TextField txtBusinessName;
    private TextField txtPhone;
    private ComboBox<String> cbCategory;
    private TextField txtAddress;
    private TextField txtExperience;
    private CheckBox chkTerms;

    Scene getvendorRegistrationScene(Runnable callBackAction) {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Parent content = createRootPane();
        rootLayer.getChildren().add(content);

        vendorRegistrationScene = new Scene(rootLayer, 1550, 785);
        return vendorRegistrationScene;
    }

    public Parent createRootPane() {
        HBox root = new HBox();
        root.setStyle("-fx-background-color: white;");

        // 1. LEFT PANEL
        VBox leftPanel = new VBox(20);
        leftPanel.setPrefWidth(450);
        leftPanel.setPadding(new Insets(40));
        leftPanel.setAlignment(Pos.TOP_LEFT);
        leftPanel.setStyle("-fx-background-color: " + COLOR_NAVY + ";");

        Button backLink = new Button("← Back to Login");
        backLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");
        backLink.setOnAction(e -> {
            LoginPage backToLoginPage = new LoginPage();
            LandingPage.landingPageStage.setScene(backToLoginPage.getloginPageScene(this::backToVendorRegistration));
        });

        Label brand = new Label("EventXpert");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        brand.setTextFill(Color.WHITE);

        Image vendor = new Image("assets\\images\\Vendor.png");
        ImageView vendorImageView = new ImageView(vendor);
        vendorImageView.setFitWidth(320);
        vendorImageView.setFitHeight(260);
        vendorImageView.setPreserveRatio(true);

        Label heading = new Label("Join as a Verified Vendor");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heading.setTextFill(Color.WHITE);

        Label subtext = new Label("Offer your event services, receive direct assignment bookings, and get secure Escrow payouts.");
        subtext.setWrapText(true);
        subtext.setStyle("-fx-font-size: 13.5px; -fx-text-fill: #CBD5E1;");

        leftPanel.getChildren().addAll(backLink, brand, vendorImageView, heading, subtext);

        // 2. RIGHT FORM PANEL
        VBox formContainer = new VBox(12);
        formContainer.setPadding(new Insets(35, 60, 35, 60));
        HBox.setHgrow(formContainer, Priority.ALWAYS);

        Label title = new Label("Vendor Registration");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web(COLOR_NAVY));

        Label subtitle = new Label("Create your business profile to connect with event organizers.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        txtFullName = createInput("Enter full name");
        txtEmailAddress = createInput("Enter email address");
        txtBusinessName = createInput("Enter registered business name");
        txtPhone = createInput("Enter contact phone number");

        cbCategory = new ComboBox<>();
        cbCategory.setPromptText("Select primary service category");
        cbCategory.setMaxWidth(Double.MAX_VALUE);
        cbCategory.setPrefHeight(42);
        cbCategory.getItems().addAll("Decoration", "Sound System", "Photography and Videography", "Rental Equipment", "Invitation and Printing", "Catering", "Anchor", "Cake and Dessert Services");

        PasswordEyeBox passBox = new PasswordEyeBox("Create password (min 6 characters)");
        PasswordEyeBox confPassBox = new PasswordEyeBox("Confirm password");

        txtAddress = createInput("Enter business address / coverage area");
        txtExperience = createInput("Experience in years (e.g. 5)");

        chkTerms = new CheckBox("I agree to the Terms & Conditions and Privacy Policy");
        chkTerms.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #334155;");

        Button btnRegister = new Button("Register as Vendor →");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setPrefHeight(46);
        btnRegister.setStyle("-fx-background-color: " + COLOR_PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-cursor: hand;");

        AuthController controller = new AuthController();

        btnRegister.setOnAction(e -> {
            String p = passBox.getText();
            String cp = confPassBox.getText();

            if (txtFullName.getText().isBlank() || txtEmailAddress.getText().isBlank() || cbCategory.getValue() == null || p.isBlank() || !chkTerms.isSelected()) {
                showCompactModal("Incomplete Form", "Please fill in all required fields and select a category.", false);
                return;
            }

            if (p.length() < 6) {
                showCompactModal("Weak Password", "Password must be at least 6 characters long.", false);
                return;
            }

            if (!p.equals(cp)) {
                showCompactModal("Password Mismatch", "Passwords do not match. Please verify them.", false);
                return;
            }

            SignUpResult result = controller.signUp(
                    txtFullName.getText().trim(),
                    txtEmailAddress.getText().trim(),
                    txtPhone.getText().trim(),
                    p,
                    "vendor"
            );

            if (result != null) {
                String uid = result.getUid();
                String idToken = result.getIdToken();

                VendorProfile profile = new VendorProfile(
                        uid,
                        txtFullName.getText().trim(),
                        txtEmailAddress.getText().trim(),
                        txtBusinessName.getText().trim(),
                        txtPhone.getText().trim(),
                        cbCategory.getValue(),
                        txtAddress.getText().trim(),
                        txtExperience.getText().trim(),
                        "",
                        "Available"
                );

                VendorController vendorController = new VendorController();
                vendorController.createVendorProfile(profile, idToken);

                InactiveVendorDash vendorDash = new InactiveVendorDash(uid, idToken);
                LandingPage.landingPageStage.setScene(vendorDash.getinactiveVendorScene());
            } else {
                showCompactModal("Registration Failed (400)", "Email may already exist or request was rejected by auth.", false);
            }
        });

        formContainer.getChildren().addAll(
                title, subtitle,
                new Label("Full Name:"), txtFullName,
                new Label("Business Name:"), txtBusinessName,
                new Label("Email Address:"), txtEmailAddress,
                new Label("Phone Number:"), txtPhone,
                new Label("Service Category:"), cbCategory,
                new Label("Password:"), passBox.getContainer(),
                new Label("Confirm Password:"), confPassBox.getContainer(),
                new Label("Operating Location:"), txtAddress,
                new Label("Experience (Years):"), txtExperience,
                chkTerms,
                btnRegister
        );

        ScrollPane rightScroll = new ScrollPane(formContainer);
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");
        HBox.setHgrow(rightScroll, Priority.ALWAYS);

        root.getChildren().addAll(leftPanel, rightScroll);
        return root;
    }

    private TextField createInput(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefHeight(42);
        f.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12;");
        return f;
    }

    private static class PasswordEyeBox {
        private final PasswordField pwdField = new PasswordField();
        private final TextField txtField = new TextField();
        private final StackPane pane = new StackPane();

        public PasswordEyeBox(String prompt) {
            pwdField.setPromptText(prompt);
            txtField.setPromptText(prompt);
            txtField.setVisible(false);

            String style = "-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 38 0 12;";
            pwdField.setStyle(style);
            txtField.setStyle(style);
            pwdField.setPrefHeight(42);
            txtField.setPrefHeight(42);

            pwdField.textProperty().bindBidirectional(txtField.textProperty());

            Button eyeBtn = new Button("👁");
            eyeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 14px; -fx-cursor: hand;");

            eyeBtn.setOnAction(e -> {
                if (pwdField.isVisible()) {
                    pwdField.setVisible(false);
                    txtField.setVisible(true);
                    eyeBtn.setText("🙈");
                } else {
                    txtField.setVisible(false);
                    pwdField.setVisible(true);
                    eyeBtn.setText("👁");
                }
            });

            pane.getChildren().addAll(pwdField, txtField, eyeBtn);
            StackPane.setAlignment(eyeBtn, Pos.CENTER_RIGHT);
            StackPane.setMargin(eyeBtn, new Insets(0, 8, 0, 0));
        }

        public StackPane getContainer() { return pane; }
        public String getText() { return pwdField.getText(); }
    }

    // ⭐ COMPACT RECTANGULAR MODAL POPUP
    private void showCompactModal(String title, String message, boolean success) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.40);");

        VBox modal = new VBox(10);
        modal.setPrefWidth(400);
        modal.setMaxWidth(420);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPadding(new Insets(16, 20, 16, 20));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + (success ? "#86EFAC" : "#FCA5A5") + "; -fx-border-width: 1px; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.20)));

        HBox topH = new HBox(10);
        topH.setAlignment(Pos.CENTER_LEFT);

        Label ic = new Label(success ? "✓" : "⚠");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: " + (success ? "#16A34A" : "#DC2626") + "; -fx-font-weight: bold;");

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(COLOR_NAVY));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button closeX = new Button("✕");
        closeX.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 0;");
        closeX.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        topH.getChildren().addAll(ic, t, sp, closeX);

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        HBox bRow = new HBox();
        bRow.setAlignment(Pos.CENTER_RIGHT);
        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-background-color: " + (success ? "#16A34A" : COLOR_PRIMARY_BLUE) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11.5px; -fx-padding: 5 18; -fx-background-radius: 4px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));
        bRow.getChildren().add(okBtn);

        modal.getChildren().addAll(topH, m, bRow);
        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    public void backToVendorRegistration() {
        if (vendorRegistrationScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(vendorRegistrationScene);
        }
    }
}