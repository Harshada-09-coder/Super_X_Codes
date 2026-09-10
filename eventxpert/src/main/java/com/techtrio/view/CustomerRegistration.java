package com.techtrio.view;

import com.techtrio.controller.AuthController;
import com.techtrio.controller.SignUpResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CustomerRegistration {

    private Scene CustomerRegistrationScene;
    private StackPane rootLayer;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BORDER_COLOR = "#DCE5F0";

    Scene getCustomerRegistrationScene(Runnable callBackAction) {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        HBox root = new HBox();
        root.setPrefSize(1550, 785);
        root.setStyle("-fx-background-color: white;");

        // 1. LEFT BRANDING PANEL
        VBox leftSide = new VBox(20);
        leftSide.setPrefWidth(480);
        leftSide.setAlignment(Pos.CENTER_LEFT);
        leftSide.setPadding(new Insets(40, 45, 40, 45));
        leftSide.setStyle("-fx-background-color: " + NAVY + ";");

        Label brand = new Label("EventXpert");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        brand.setTextFill(Color.WHITE);

        Image regImg = new Image("assets\\images\\reg image.png");
        ImageView imgView = new ImageView(regImg);
        imgView.setFitWidth(380);
        imgView.setFitHeight(340);
        imgView.setPreserveRatio(true);

        Label joinCustomer = new Label("Join as Customer");
        joinCustomer.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        joinCustomer.setTextFill(Color.WHITE);

        Label customerDescription = new Label("Plan events, discover venues & verified vendors, and track your bookings with live Escrow security.");
        customerDescription.setWrapText(true);
        customerDescription.setFont(Font.font("Arial", 14));
        customerDescription.setTextFill(Color.web("#CBD5E1"));

        leftSide.getChildren().addAll(brand, imgView, joinCustomer, customerDescription);

        // 2. RIGHT FORM PANEL
        VBox rightSide = new VBox(12);
        rightSide.setPadding(new Insets(40, 80, 40, 80));
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        Label heading = new Label("Create your Customer Account");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web(NAVY));

        Label subtitle = new Label("Plan, manage and organize your events effortlessly.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        TextField fullNameField = createTextField("Harshada Kurumkar");
        TextField emailField = createTextField("harshada@example.com");
        TextField mobileField = createTextField("+91 98765 43210");

        PasswordEyeBox passBox = new PasswordEyeBox("Enter your password");
        PasswordEyeBox confPassBox = new PasswordEyeBox("Confirm your password");

        CheckBox termsCheckBox = new CheckBox("I agree to the Terms of Service and Privacy Policy");
        termsCheckBox.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #334155;");

        Button createAccountButton = new Button("Create Customer Account →");
        createAccountButton.setMaxWidth(Double.MAX_VALUE);
        createAccountButton.setPrefHeight(46);
        createAccountButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-cursor: hand;");

        AuthController controller = new AuthController();

        createAccountButton.setOnAction(event -> {
            String name = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String mobile = mobileField.getText().trim();
            String password = passBox.getText();
            String confirmPasswordText = confPassBox.getText();

            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty() || confirmPasswordText.isEmpty()) {
                showCompactModal("Incomplete Form", "Please fill in all the required fields.", false);
                return;
            }

            if (!password.equals(confirmPasswordText)) {
                showCompactModal("Password Mismatch", "Passwords do not match. Please re-enter.", false);
                return;
            }

            if (password.length() < 6) {
                showCompactModal("Weak Password", "Password must contain at least 6 characters.", false);
                return;
            }

            if (!termsCheckBox.isSelected()) {
                showCompactModal("Terms Required", "Please accept the Terms of Service to register.", false);
                return;
            }

            SignUpResult result = controller.signUp(name, email, mobile, password, "customer");

            if (result != null) {
                NewCustomerDashboard customerDashboardC = new NewCustomerDashboard();
                LandingPage.landingPageStage.setScene(customerDashboardC.getnewCustomerScene(this::backToCustomerRegistration));
            } else {
                showCompactModal("Registration Failed (400)", "Email may already exist or is invalid. Please check.", false);
            }
        });

        HBox signInBox = new HBox(6);
        signInBox.setAlignment(Pos.CENTER);
        Label already = new Label("Already have an account?");
        already.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        Hyperlink signIn = new Hyperlink("Sign In");
        signIn.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: transparent;");
        signIn.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            LandingPage.landingPageStage.setScene(loginPage.getloginPageScene(this::backToCustomerRegistration));
        });
        signInBox.getChildren().addAll(already, signIn);

        rightSide.getChildren().addAll(
                heading, subtitle,
                new Label("Full Name:"), fullNameField,
                new Label("Email Address:"), emailField,
                new Label("Mobile Number:"), mobileField,
                new Label("Password:"), passBox.getContainer(),
                new Label("Confirm Password:"), confPassBox.getContainer(),
                termsCheckBox,
                createAccountButton,
                signInBox
        );

        ScrollPane rightScroll = new ScrollPane(rightSide);
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");
        HBox.setHgrow(rightScroll, Priority.ALWAYS);

        root.getChildren().addAll(leftSide, rightScroll);
        rootLayer.getChildren().add(root);

        CustomerRegistrationScene = new Scene(rootLayer, 1550, 785);
        return CustomerRegistrationScene;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(42);
        field.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12;");
        return field;
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

    // ⭐ COMPACT RECTANGULAR IN-APP POPUP (SMALL HEIGHT)
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
        t.setTextFill(Color.web(NAVY));

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
        okBtn.setStyle("-fx-background-color: " + (success ? "#16A34A" : PRIMARY_BLUE) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11.5px; -fx-padding: 5 18; -fx-background-radius: 4px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));
        bRow.getChildren().add(okBtn);

        modal.getChildren().addAll(topH, m, bRow);
        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    public void backToCustomerRegistration() {
        if (CustomerRegistrationScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(CustomerRegistrationScene);
        }
    }
}