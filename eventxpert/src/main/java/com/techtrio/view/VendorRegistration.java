package com.techtrio.view;

import com.techtrio.controller.AuthController;
import com.techtrio.controller.SignUpResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class VendorRegistration {
    private Scene vendorRegistrationScene;

    // --- COLOR PALETTE ---
    private static final String COLOR_NAVY = "#0B2E63";
    private static final String COLOR_BLUE = "#2563D9";
    private static final String COLOR_BLUE_HOVER = "#1D4ED8";
    private static final String COLOR_LIGHT_BLUE = "#EAF2FF";
    private static final String COLOR_VERY_LIGHT_BLUE = "#F7F9FC";
    private static final String COLOR_MAIN_TEXT = "#172B4D";
    private static final String COLOR_SECONDARY_TEXT = "#64748B";
    private static final String COLOR_BORDER = "#D9E2F0";
    private static final String COLOR_ERROR = "#DC2626";
    private static final String COLOR_WHITE = "#FFFFFF";

    // --- FORM FIELDS ---
    private TextField txtFullName;
    private TextField txtEmailAddress;
    private TextField txtPhone;
    private ComboBox<String> cbCategory;
    private PasswordField txtPassword;
    private TextField txtPasswordVisible;
    private PasswordField txtConfirmPassword;
    private TextField txtConfirmPasswordVisible;
    private TextField txtAddress;
    private TextField txtExperience;
    private TextField txtGstNumber;
    private CheckBox chkTerms;
    private Button btnRegister;

    // --- VALIDATION LABELS ---
    private Label lblFullNameError;
    private Label lblEmailError;
    private Label lblPhoneError;
    private Label lblCategoryError;
    private Label lblPasswordError;
    private Label lblConfirmPasswordError;
    private Label lblAddressError;
    private Label lblExperienceError;
    private Label lblTermsError;
    private VBox statusBanner;
    Scene getvendorRegistrationScene(Runnable callBackAction){

        Scene scene = new Scene(createRootPane(), 1550, 750);
        vendorRegistrationScene = scene;
        return vendorRegistrationScene;
    }

    /**
     * Root container builder - Embeddable in existing JavaFX navigation layouts.
     */
       public Parent createRootPane() {
        HBox root = new HBox();
        root.setStyle("-fx-background-color: " + COLOR_WHITE + ";");

        // 1. Left Branding Panel (35% width ratio)
        VBox leftPanel = createLeftPanel();
        leftPanel.prefWidthProperty().bind(root.widthProperty().multiply(0.36));
        leftPanel.setMinWidth(320);

        // 2. Right Form Panel (64% width ratio)
        ScrollPane rightPanelScroll = createRightPanelScroll();
        HBox.setHgrow(rightPanelScroll, Priority.ALWAYS);

        root.getChildren().addAll(leftPanel, rightPanelScroll);
        return root;
    }

    // ==========================================
    // LEFT BRANDING & INFORMATION PANEL
    // ==========================================
    private VBox createLeftPanel() {
        VBox panel = new VBox();
        panel.setPadding(new Insets(32, 28, 32, 28));
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + COLOR_NAVY + ", " + COLOR_BLUE + ");"
        );

        // Top Navigation
        Hyperlink backLink = new Hyperlink("←  Back to Login");
        backLink.setStyle(
            "-fx-text-fill: " + COLOR_WHITE + ";" +
            "-fx-font-size: 13px;" +
            "-fx-underline: false;" +
            "-fx-opacity: 0.9;" +
            "-fx-border-color: transparent;"
        );
        backLink.setOnAction(e -> {
            System.out.println("Back To Login Page");
            LoginPage backToLoginPage = new LoginPage();
            Runnable callBackAction = new Runnable() {
                        public void run(){
                                backToVendorRegistration();
                        }
                };

                LandingPage.landingPageStage.setScene(backToLoginPage.getloginPageScene(callBackAction));
      
        });

        // Illustration Area Container
        VBox illustrationBox = createIllustrationContainer();

        // Descriptive Copy
        Label heading = new Label("Join as a Vendor");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_WHITE + ";");

        Label subtext = new Label("Offer your services, get bookings, and increase your visibility");
        subtext.setWrapText(true);
        subtext.setStyle("-fx-font-size: 13px; -fx-text-fill: #D1E0FF; -fx-text-alignment: center;");

        VBox textContainer = new VBox(8, heading, subtext);
        textContainer.setAlignment(Pos.CENTER);

        // Pagination Dots (3rd dot active for Vendor)
        HBox dotsBox = new HBox(8);
        dotsBox.setAlignment(Pos.CENTER);
        Circle dot1 = new Circle(4, Color.web("#FFFFFF", 0.35));
        Circle dot2 = new Circle(4, Color.web("#FFFFFF", 0.35));
        Rectangle dot3 = new Rectangle(20, 8, Color.web(COLOR_WHITE));
        dot3.setArcWidth(8);
        dot3.setArcHeight(8);
        dotsBox.getChildren().addAll(dot1, dot2, dot3);

        // Spacer to push illustration and content
        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        panel.getChildren().addAll(
            backLink,
            topSpacer,
            illustrationBox,
            textContainer,
            dotsBox,
            bottomSpacer
        );

        VBox.setMargin(illustrationBox, new Insets(0, 0, 24, 0));
        VBox.setMargin(dotsBox, new Insets(20, 0, 0, 0));

        return panel;
    }

    /**
     * Self-contained Vector Illustration Component
     */
        private VBox createIllustrationContainer() {
        VBox box = new VBox();
        box.setPrefSize(280, 240);
        box.setMaxSize(300, 260);
        box.setAlignment(Pos.CENTER);
        box.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.12);" +
            "-fx-background-radius: 16px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-radius: 16px;" +
            "-fx-padding: 20px;"
        );
        Image vendor = new Image("assets\\images\\Vendor.png");
        ImageView vendorImageView = new ImageView(vendor);
        vendorImageView.setFitHeight(220);
        vendorImageView.setFitWidth(280);

        // Rendered Vendor Booth / Equipment Scene via JavaFX Graphic Primitives
        StackPane canvas = new StackPane(vendorImageView);
        canvas.setPrefSize(240, 180);
        box.getChildren().add(canvas);
        return box;
    }

    private ScrollPane createRightPanelScroll() {
        VBox formContainer = new VBox(16);
        formContainer.setPadding(new Insets(36, 48, 36, 48));
        formContainer.setMaxWidth(680);
        formContainer.setAlignment(Pos.TOP_LEFT);

        // Header Title
        Label title = new Label("Vendor Registration");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");

        Label subtitle = new Label("Create your vendor account");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        VBox headerBox = new VBox(4, title, subtitle);
        headerBox.setPadding(new Insets(0, 0, 8, 0));

        // Form Status Banner (Success/Error summary)
        statusBanner = new VBox();
        statusBanner.setManaged(false);
        statusBanner.setVisible(false);

        // --- ROW 1: Full Name | Business Name ---
        lblFullNameError = createErrorLabel();
        lblEmailError = createErrorLabel();
        txtFullName = createTextField("Enter full name");
        txtEmailAddress = createTextField("Enter business name");
        HBox row1 = createTwoColumnRow(createFieldGroup("Full Name", txtFullName, lblFullNameError),
            createFieldGroup("Email Address", txtEmailAddress, lblEmailError)
        );

        // --- ROW 2: Phone | Service Category ---
        lblPhoneError = createErrorLabel();
        lblCategoryError = createErrorLabel();
        txtPhone = createTextField("Enter phone number");
        cbCategory = new ComboBox<>();
        cbCategory.setPromptText("Select category");
        cbCategory.setMaxWidth(Double.MAX_VALUE);
        cbCategory.getItems().addAll(
            "Decoration",
            "Sound System",
            "Photography and Videography",
            "Rental Equipment",
            "Invitation and Printing",
            "Catering",
            "Anchor",
            "Cake and Dessert Services"
        );
        cbCategory.setStyle(getControlStyle());

        HBox row2 = createTwoColumnRow(
            createFieldGroup("Phone Number", txtPhone, lblPhoneError),
            createFieldGroup("Service Category", cbCategory, lblCategoryError)
        );

        // --- ROW 3: Password | Confirm Password ---
        lblPasswordError = createErrorLabel();
        lblConfirmPasswordError = createErrorLabel();

        StackPane passPane = createPasswordControl(true);
        StackPane confirmPassPane = createPasswordControl(false);

        HBox row3 = createTwoColumnRow(
            createFieldGroup("Password", passPane, lblPasswordError),
            createFieldGroup("Confirm Password", confirmPassPane, lblConfirmPasswordError)
        );

        // --- ROW 4: Address (Full Width) ---
        lblAddressError = createErrorLabel();
        txtAddress = createTextField("Enter business address");
        VBox row4 = createFieldGroup("Address", txtAddress, lblAddressError);

        // --- ROW 5: Experience | GST Number ---
        lblExperienceError = createErrorLabel();
        txtExperience = createTextField("Enter experience");
        txtGstNumber = createTextField("Enter GST number");
        HBox row5 = createTwoColumnRow(
            createFieldGroup("Experience (Years)", txtExperience, lblExperienceError),
            createFieldGroup("GST Number (Optional)", txtGstNumber, new Label())
        );

        // Helper Info Box
        HBox infoBox = createHelperInfoBox();

        // Terms Agreement Checkbox
        lblTermsError = createErrorLabel();
        chkTerms = new CheckBox();

        TextFlow termsFlow = new TextFlow();
        Text text1 = new Text("I agree to the ");
        text1.setFill(Color.web(COLOR_MAIN_TEXT));
        Hyperlink termsLink = new Hyperlink("Terms & Conditions");
        styleInlineLink(termsLink);
        Text text2 = new Text(" and ");
        text2.setFill(Color.web(COLOR_MAIN_TEXT));
        Hyperlink privacyLink = new Hyperlink("Privacy Policy");
        styleInlineLink(privacyLink);
        termsFlow.getChildren().addAll(text1, termsLink, text2, privacyLink);

        HBox termsRow = new HBox(8, chkTerms, termsFlow);
        termsRow.setAlignment(Pos.CENTER_LEFT);
        VBox termsContainer = new VBox(4, termsRow, lblTermsError);

        // Register Button
        Text output = new Text();
        output.setStyle("-fx-fill: white");
        AuthController controller = new AuthController();
        btnRegister = new Button("Register as Vendor  →");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setPrefHeight(44);
        btnRegister.setStyle(
            "-fx-background-color: " + COLOR_BLUE + ";" +
            "-fx-text-fill: " + COLOR_WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        );
        
        // Hover & Pressed Effects
        btnRegister.setOnMouseEntered(e -> btnRegister.setStyle(
            "-fx-background-color: " + COLOR_BLUE_HOVER + ";" +
            "-fx-text-fill: " + COLOR_WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        ));
        btnRegister.setOnMouseExited(e -> btnRegister.setStyle(
            "-fx-background-color: " + COLOR_BLUE + ";" +
            "-fx-text-fill: " + COLOR_WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;"
        ));
        btnRegister.setOnAction(e -> {

            handleRegistration();
            SignUpResult result = controller.signUp(
            txtFullName.getText().trim(),
            txtEmailAddress.getText().trim(),
            txtPhone.getText().trim(),
            txtPassword.getText(),
            "vendor"
         );

    if (result != null) {

        System.out.println("Registration successful");
    } else {
        System.out.println("Registration Failed");
    }
        });
        
        // Footer Login Link
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER);
        Label lblAlready = new Label("Already have an account? ");
        lblAlready.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 13px;");
        Hyperlink loginLink = new Hyperlink("Login");
        loginLink.setStyle(
            "-fx-text-fill: " + COLOR_BLUE + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-underline: false;" +
            "-fx-border-color: transparent;"
        );
        loginLink.setOnAction(e -> {
            handleNavigateLogin();
            LoginPage backToLoginPage = new LoginPage();
            Runnable callBackAction1 = new Runnable() {
                        public void run(){
                                backToVendorRegistration();
                        }
                };

                LandingPage.landingPageStage.setScene(backToLoginPage.getloginPageScene(callBackAction1));
      
        });
        footerBox.getChildren().addAll(lblAlready, loginLink);

        // Assemble Form Container
        formContainer.getChildren().addAll(
            headerBox,
            statusBanner,
            row1,
            row2,
            row3,
            row4,
            row5,
            infoBox,
            termsContainer,
            btnRegister,
            footerBox
        );

        ScrollPane scrollPane = new ScrollPane(formContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + COLOR_WHITE + ";");
        return scrollPane;
    }

    // ==========================================
    // UI BUILDER HELPERS
    // ==========================================
    private VBox createFieldGroup(String labelText, Node inputControl, Label errorLabel) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_MAIN_TEXT + ";");
        VBox group = new VBox(4, label, inputControl, errorLabel);
        HBox.setHgrow(group, Priority.ALWAYS);
        return group;
    }

    private HBox createTwoColumnRow(VBox leftGroup, VBox rightGroup) {
        HBox row = new HBox(16, leftGroup, rightGroup);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    private TextField createTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefHeight(38);
        field.setStyle(getControlStyle());
        return field;
    }

    private StackPane createPasswordControl(boolean isPrimaryPassword) {
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText(isPrimaryPassword ? "Create password" : "Confirm password");
        pwdField.setPrefHeight(38);
        pwdField.setStyle(getControlStyle());

        TextField visibleField = new TextField();
        visibleField.setPromptText(pwdField.getPromptText());
        visibleField.setPrefHeight(38);
        visibleField.setStyle(getControlStyle());
        visibleField.setVisible(false);

        if (isPrimaryPassword) {
            txtPassword = pwdField;
            txtPasswordVisible = visibleField;
        } else {
            txtConfirmPassword = pwdField;
            txtConfirmPasswordVisible = visibleField;
        }

        // Eye Toggle Button
        Button toggleBtn = new Button("👁");
        toggleBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + COLOR_SECONDARY_TEXT + ";" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        );

        // Bind text fields
        pwdField.textProperty().bindBidirectional(visibleField.textProperty());

        toggleBtn.setOnAction(e -> {
            if (pwdField.isVisible()) {
                pwdField.setVisible(false);
                visibleField.setVisible(true);
                toggleBtn.setText("🙈");
            } else {
                visibleField.setVisible(false);
                pwdField.setVisible(true);
                toggleBtn.setText("👁");
            }
        });

        StackPane pane = new StackPane();
        pane.getChildren().addAll(pwdField, visibleField, toggleBtn);
        StackPane.setAlignment(toggleBtn, Pos.CENTER);
        StackPane.setMargin(toggleBtn, new Insets(0, 6, 0, 0));
        return pane;
    }

    private HBox createHelperInfoBox() {
        HBox box = new HBox(8);
        box.setPadding(new Insets(10, 12, 10, 12));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
            "-fx-background-color: " + COLOR_VERY_LIGHT_BLUE + ";" +
            "-fx-border-color: " + COLOR_LIGHT_BLUE + ";" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;"
        );

        Label icon = new Label("ⓘ");
        icon.setStyle("-fx-text-fill: " + COLOR_BLUE + "; -fx-font-weight: bold;");

        Label infoText = new Label("You can add and manage more services after registration.");
        infoText.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_SECONDARY_TEXT + ";");

        box.getChildren().addAll(icon, infoText);
        return box;
    }

    private Label createErrorLabel() {
        Label lbl = new Label();
        lbl.setStyle("-fx-text-fill: " + COLOR_ERROR + "; -fx-font-size: 11px;");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    private void styleInlineLink(Hyperlink link) {
        link.setStyle(
            "-fx-text-fill: " + COLOR_BLUE + ";" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 0px;"
        );
    }

    private String getControlStyle() {
        return "-fx-background-color: " + COLOR_WHITE + ";" +
               "-fx-border-color: " + COLOR_BORDER + ";" +
               "-fx-border-radius: 6px;" +
               "-fx-background-radius: 6px;" +
               "-fx-padding: 6 10 6 10;";
    }

    private String getErrorControlStyle() {
        return "-fx-background-color: " + COLOR_WHITE + ";" +
               "-fx-border-color: " + COLOR_ERROR + ";" +
               "-fx-border-radius: 6px;" +
               "-fx-background-radius: 6px;" +
               "-fx-padding: 6 10 6 10;";
    }

    // ==========================================
    // VALIDATION & LOGIC HANDLING
    // ==========================================
    private void handleRegistration() {
        clearErrors();
        boolean isValid = true;

        // Full Name Validation
        if (txtFullName.getText().trim().isEmpty()) {
            showFieldError(txtFullName, lblFullNameError, "Full name is required");
            isValid = false;
        }

        // Business Name Validation
        if (txtEmailAddress.getText().trim().isEmpty()) {
            showFieldError(txtEmailAddress, lblEmailError, "Business name is required");
            isValid = false;
        }

        // Phone Validation
        String phoneStr = txtPhone.getText().trim();
        if (phoneStr.isEmpty()) {
            showFieldError(txtPhone, lblPhoneError, "Phone number is required");
            isValid = false;
        } else if (!phoneStr.matches("^[0-9\\-\\+\\s\\(\\)]{7,15}$")) {
            showFieldError(txtPhone, lblPhoneError, "Enter a valid phone number");
            isValid = false;
        }

        // Category Validation
        if (cbCategory.getValue() == null) {
            cbCategory.setStyle(getErrorControlStyle());
            lblCategoryError.setText("Select a service category");
            lblCategoryError.setVisible(true);
            lblCategoryError.setManaged(true);
            isValid = false;
        }

        // Password Validation
        String password = txtPassword.getText();
        if (password.isEmpty()) {
            txtPassword.setStyle(getErrorControlStyle());
            txtPasswordVisible.setStyle(getErrorControlStyle());
            lblPasswordError.setText("Password is required");
            lblPasswordError.setVisible(true);
            lblPasswordError.setManaged(true);
            isValid = false;
        } else if (password.length() < 6) {
            txtPassword.setStyle(getErrorControlStyle());
            txtPasswordVisible.setStyle(getErrorControlStyle());
            lblPasswordError.setText("Password must be at least 6 characters");
            lblPasswordError.setVisible(true);
            lblPasswordError.setManaged(true);
            isValid = false;
        }

        // Confirm Password Validation
        String confirmPassword = txtConfirmPassword.getText();
        if (!confirmPassword.equals(password) || confirmPassword.isEmpty()) {
            txtConfirmPassword.setStyle(getErrorControlStyle());
            txtConfirmPasswordVisible.setStyle(getErrorControlStyle());
            lblConfirmPasswordError.setText("Passwords do not match");
            lblConfirmPasswordError.setVisible(true);
            lblConfirmPasswordError.setManaged(true);
            isValid = false;
        }

        // Address Validation
        if (txtAddress.getText().trim().isEmpty()) {
            showFieldError(txtAddress, lblAddressError, "Address is required");
            isValid = false;
        }

        // Experience Validation
        String expStr = txtExperience.getText().trim();
        if (expStr.isEmpty()) {
            showFieldError(txtExperience, lblExperienceError, "Experience is required");
            isValid = false;
        } else {
            try {
                double exp = Double.parseDouble(expStr);
                if (exp < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showFieldError(txtExperience, lblExperienceError, "Enter numeric years");
                isValid = false;
            }
        }

        // Terms Checkbox Validation
        if (!chkTerms.isSelected()) {
            lblTermsError.setText("You must accept the terms and conditions");
            lblTermsError.setVisible(true);
            lblTermsError.setManaged(true);
            isValid = false;
        }

        // Form Process Result
        if (isValid) {
            showStatusBanner(
                "Vendor registration successful! Redirecting to your dashboard...",
                "#10B981", "#E6F4EA"
            );
            btnRegister.setDisable(true);
        } else {
            showStatusBanner(
                "Please fix the errors below to complete your registration.",
                COLOR_ERROR, "#FEE2E2"
            );
        }
    }

    private void showFieldError(Control field, Label errorLabel, String message) {
        field.setStyle(getErrorControlStyle());
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearErrors() {
        statusBanner.setVisible(false);
        statusBanner.setManaged(false);

        Control[] controls = {
            txtFullName, txtEmailAddress, txtPhone, cbCategory, 
            txtPassword, txtPasswordVisible, txtConfirmPassword, 
            txtConfirmPasswordVisible, txtAddress, txtExperience, txtGstNumber
        };

        for (Control c : controls) {
            c.setStyle(getControlStyle());
        }

        Label[] labels = {
            lblFullNameError, lblEmailError, lblPhoneError,
            lblCategoryError, lblPasswordError, lblConfirmPasswordError,
            lblAddressError, lblExperienceError, lblTermsError
        };

        for (Label lbl : labels) {
            lbl.setVisible(false);
            lbl.setManaged(false);
        }
    }

    private void showStatusBanner(String message, String textColor, String bgColor) {
        statusBanner.getChildren().clear();
        Label msg = new Label(message);
        msg.setStyle("-fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        statusBanner.getChildren().add(msg);
        statusBanner.setPadding(new Insets(10, 14, 10, 14));
        statusBanner.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + textColor + ";" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;"
        );
        statusBanner.setVisible(true);
        statusBanner.setManaged(true);
    }

    private void handleNavigateLogin() {
        System.out.println("Navigating back to Login Screen...");
    }

    public void backToVendorRegistration(){

        LandingPage.landingPageStage.setScene(vendorRegistrationScene);
    }
}