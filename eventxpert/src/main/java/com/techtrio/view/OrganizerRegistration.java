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

public class OrganizerRegistration{
    private Scene OrganizerRegistrationScene;

    // --- EVENTXPERT COLOR PALETTE CONSTANTS ---
    private static final String COLOR_NAVY = "#0B2E63";
    private static final String COLOR_PRIMARY_BLUE = "#2563D9";
    private static final String COLOR_MAIN_TEXT = "#172B4D";
    private static final String COLOR_SECONDARY_TEXT = "#64748B";
    private static final String COLOR_WHITE = "#FFFFFF";

    // --- FORM CONTROLS ---
    private TextField txtFullName;
    private TextField txtEmail;
    private TextField txtPhone;
    private TextField txtOrganizationName;

    private PasswordField txtPassword;
    private TextField txtPasswordVisible;

    private PasswordField txtConfirmPassword;
    private TextField txtConfirmPasswordVisible;

    private ComboBox<String> cbBusinessType;
    private TextArea txtAddress;
    private TextField txtGstNumber;

    private CheckBox chkTerms;
    private Button btnRegister;

    // --- ERROR LABELS ---
    private Label lblFullNameError;
    private Label lblEmailError;
    private Label lblPhoneError;
    private Label lblOrganizationNameError;
    private Label lblPasswordError;
    private Label lblConfirmPasswordError;
    private Label lblBusinessTypeError;
    private Label lblAddressError;
    private Label lblTermsError;
    private VBox statusBanner;

    
    Scene getOrganizerRegistrationScene(Runnable callBackAction) {
        Scene scene = new Scene(createRootPane(), 1150, 750);
        OrganizerRegistrationScene = scene;
        return OrganizerRegistrationScene;

    }

    /**
     * Root Pane Factory - Integrates cleanly with navigation managers.
     */
       public Parent createRootPane() {
        HBox root = new HBox();
        root.getStyleClass().add("root");

        // 1. LEFT PANEL - Spans FULL height top to bottom (40% width)
        VBox leftPanel = createLeftPanel();
        leftPanel.prefWidthProperty().bind(root.widthProperty().multiply(0.40));
        leftPanel.setMinWidth(340);

        // 2. RIGHT PANEL - Form area (60% width)
        ScrollPane rightPanelScroll = createRightPanelScroll();
        HBox.setHgrow(rightPanelScroll, Priority.ALWAYS);

        root.getChildren().addAll(leftPanel, rightPanelScroll);
        return root;
    }

    // ==========================================
    // LEFT PANEL (NAVY BRANDING & ORGANIZER IMAGE)
    // ==========================================
    private VBox createLeftPanel() {
        VBox panel = new VBox();
        panel.getStyleClass().add("left-panel");
        panel.setPadding(new Insets(32, 28, 32, 28));
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setStyle("-fx-background-color: #0B2E63");

        // Top Navigation Link
        Button backLink = new Button("←  Back to Login");
        backLink.setStyle("-fx-background-color: #ffffff");
        backLink.getStyleClass().add("back-link");
        backLink.setOnAction(e -> {
            System.out.println("Back To Login Page");
            LoginPage backToLoginPage = new LoginPage();
            Runnable callBackAction1 = new Runnable() {
                        public void run(){
                                backToOrganizerRegistration();
                        }
                };

                LandingPage.landingPageStage.setScene(backToLoginPage.getloginPageScene(callBackAction1));
      
        });

        // Center Image Container displaying assets/images/Organizer.png
        VBox imageContainer = createOrganizerImageContainer();

        // Titles and Text
        Label heading = new Label("Join as an Organizer");
        heading.getStyleClass().add("panel-title");
        heading.setStyle("-fx-font-size: 22; -fx-text-fill: #ffffff; -fx-font-weight: BOLD");

        Label subtitle = new Label("Create events, manage bookings, and grow your business.");
        subtitle.getStyleClass().add("panel-subtitle");
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-font-size: 16; -fx-text-fill: #ffffff; ");

        VBox textContainer = new VBox(8, heading, subtitle);
        textContainer.setAlignment(Pos.CENTER);

        // PAGINATION INDICATORS: ● ━ ● (Second indicator ACTIVE)
        HBox dotsBox = new HBox(8);
        dotsBox.setAlignment(Pos.CENTER);

        Circle dot1 = new Circle(4, Color.web("#FFFFFF", 0.35));

        Rectangle dot2Active = new Rectangle(22, 8, Color.web(COLOR_WHITE));
        dot2Active.setArcWidth(8);
        dot2Active.setArcHeight(8);

        Circle dot3 = new Circle(4, Color.web("#FFFFFF", 0.35));

        dotsBox.getChildren().addAll(dot1, dot2Active, dot3);

        // Spacers to keep layout centered vertically
        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        panel.getChildren().addAll(
            backLink,
            topSpacer,
            imageContainer,
            textContainer,
            dotsBox,
            bottomSpacer
        );

        VBox.setMargin(imageContainer, new Insets(0, 0, 20, 0));
        VBox.setMargin(dotsBox, new Insets(20, 0, 0, 0));

        return panel;
    }

    /**
     * Loads and displays assets/images/Organizer.png cleanly
     */
       private VBox createOrganizerImageContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("image-container");
        container.setPrefSize(290, 230);
        container.setMaxSize(320, 250);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(12));
       
        Image organizer = new Image("assets\\images\\Oragnizer.png");
        ImageView imageView = new ImageView(organizer);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(270);
        imageView.setFitHeight(210);
        imageView.setTranslateX(45);


        container.getChildren().add(imageView);
        return container;
    }

    // ==========================================
    // RIGHT PANEL (REGISTRATION FORM)
    // ==========================================
    private ScrollPane createRightPanelScroll() {
        VBox formContainer = new VBox(14);
        formContainer.setPadding(new Insets(32, 48, 32, 48));
        formContainer.setMaxWidth(680);
        formContainer.setAlignment(Pos.TOP_LEFT);

        // Header Title
        Label title = new Label("Organizer Registration");
        title.getStyleClass().add("form-heading");

        Label subtitle = new Label("Create your organizer account");
        subtitle.getStyleClass().add("form-subtitle");

        VBox headerBox = new VBox(4, title, subtitle);
        headerBox.setPadding(new Insets(0, 0, 4, 0));

        // Dynamic Status Banner
        statusBanner = new VBox();
        statusBanner.setManaged(false);
        statusBanner.setVisible(false);

        // ROW 1: Full Name | Email Address
        lblFullNameError = createErrorLabel();
        lblEmailError = createErrorLabel();
        txtFullName = createTextField("Enter full name");
        txtEmail = createTextField("Enter email address");

        HBox row1 = createTwoColumnRow(
            createFieldGroup("Full Name", txtFullName, lblFullNameError),
            createFieldGroup("Email Address", txtEmail, lblEmailError)
        );

        // ROW 2: Phone Number | Organization Name
        lblPhoneError = createErrorLabel();
        lblOrganizationNameError = createErrorLabel();
        txtPhone = createTextField("Enter phone number");
        txtOrganizationName = createTextField("Enter organization name");

        HBox row2 = createTwoColumnRow(
            createFieldGroup("Phone Number", txtPhone, lblPhoneError),
            createFieldGroup("Organization Name", txtOrganizationName, lblOrganizationNameError)
        );

        // ROW 3: Password | Confirm Password (with Eye Toggle)
        lblPasswordError = createErrorLabel();
        lblConfirmPasswordError = createErrorLabel();
        StackPane passPane = createPasswordControl(true);
        StackPane confirmPassPane = createPasswordControl(false);

        HBox row3 = createTwoColumnRow(
            createFieldGroup("Password", passPane, lblPasswordError),
            createFieldGroup("Confirm Password", confirmPassPane, lblConfirmPasswordError)
        );

        // ROW 4: Business Type (Full Width Dropdown)
        lblBusinessTypeError = createErrorLabel();
        cbBusinessType = new ComboBox<>();
        cbBusinessType.setPromptText("Select business type");
        cbBusinessType.setMaxWidth(Double.MAX_VALUE);
        cbBusinessType.setPrefHeight(38);
        cbBusinessType.getItems().addAll(
            "Event Management Company",
            "Wedding Planner",
            "Corporate Event Organizer",
            "Event Agency",
            "Individual Organizer",
            "Other"
        );

        VBox row4 = createFieldGroup("Business Type", cbBusinessType, lblBusinessTypeError);

        // ROW 5: Address (Full Width Multi-line TextArea)
        lblAddressError = createErrorLabel();
        txtAddress = new TextArea();
        txtAddress.setPromptText("Enter complete address");
        txtAddress.setPrefRowCount(3);
        txtAddress.setPrefHeight(75);
        txtAddress.setWrapText(true);

        VBox row5 = createFieldGroup("Address", txtAddress, lblAddressError);

        // ROW 6: GST Number (Optional)
        txtGstNumber = createTextField("ENTER GST NUMBER");
        VBox row6 = createFieldGroup("GST Number (Optional)", txtGstNumber, new Label());

        // TERMS AND CONDITIONS CHECKBOX
        lblTermsError = createErrorLabel();
        chkTerms = new CheckBox();

        TextFlow termsFlow = new TextFlow();
        Text text1 = new Text("I agree to the ");
        text1.setFill(Color.web(COLOR_MAIN_TEXT));

        Hyperlink termsLink = new Hyperlink("Terms & Conditions");
        termsLink.getStyleClass().add("inline-link");

        Text text2 = new Text(" and ");
        text2.setFill(Color.web(COLOR_MAIN_TEXT));

        Hyperlink privacyLink = new Hyperlink("Privacy Policy");
        privacyLink.getStyleClass().add("inline-link");

        termsFlow.getChildren().addAll(text1, termsLink, text2, privacyLink);

        HBox termsRow = new HBox(8, chkTerms, termsFlow);
        termsRow.setAlignment(Pos.CENTER_LEFT);
        VBox termsContainer = new VBox(4, termsRow, lblTermsError);

        // REGISTER BUTTON - EVENTXPERT PRIMARY BLUE
        Text output = new Text();
            output.setStyle("-fx-fill: white");
            AuthController controller = new AuthController();
        btnRegister = new Button("Register as Organizer  →");
        btnRegister.getStyleClass().add("btn-primary");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setPrefHeight(44);
        btnRegister.setOnAction(e -> {
              handleRegistration();
             SignUpResult result = controller.signUp(
             txtFullName.getText().trim(),
             txtEmail.getText().trim(),
             txtPhone.getText().trim(),
             txtPassword.getText(),
             "organizer"
            );

    if (result != null) {

        System.out.println("Registration successful");
         InactiveOrgDash organizerDashboardO = new InactiveOrgDash();
         Runnable callBackAction = new Runnable() {
          public void run(){
            backToOrganizerRegistration();
          }
        };

        LandingPage.landingPageStage.setScene(organizerDashboardO.getinactiveOrganizerScene());
     
    } else {
        System.out.println("Registration Failed");
    }

         });
       
        // FOOTER LOGIN LINK
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER);
        Label lblAlready = new Label("Already have an account? ");
        lblAlready.setStyle("-fx-text-fill: " + COLOR_SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Hyperlink loginLink = new Hyperlink("Login");
        loginLink.getStyleClass().add("inline-link");
        loginLink.setOnAction(e -> {handleNavigateLogin();
            LoginPage backToLoginPage = new LoginPage();
            Runnable callBackAction1 = new Runnable() {
                        public void run(){
                                backToOrganizerRegistration();
                        }
                };

                LandingPage.landingPageStage.setScene(backToLoginPage.getloginPageScene(callBackAction1));
      
        });

        footerBox.getChildren().addAll(lblAlready, loginLink);

        // Assemble Right Form Panel
        formContainer.getChildren().addAll(
            headerBox,
            statusBanner,
            row1,
            row2,
            row3,
            row4,
            row5,
            row6,
            termsContainer,
            btnRegister,
            footerBox
        );

        ScrollPane scrollPane = new ScrollPane(formContainer);
        scrollPane.getStyleClass().add("right-panel-scroll");
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    // ==========================================
    // UI HELPER METHODS
    // ==========================================
    private VBox createFieldGroup(String labelText, Node inputControl, Label errorLabel) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
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
        return field;
    }

    private StackPane createPasswordControl(boolean isPrimaryPassword) {
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText(isPrimaryPassword ? "Enter password" : "Confirm password");
        pwdField.setPrefHeight(38);

        TextField visibleField = new TextField();
        visibleField.setPromptText(pwdField.getPromptText());
        visibleField.setPrefHeight(38);
        visibleField.setVisible(false);

        if (isPrimaryPassword) {
            txtPassword = pwdField;
            txtPasswordVisible = visibleField;
        } else {
            txtConfirmPassword = pwdField;
            txtConfirmPasswordVisible = visibleField;
        }

        pwdField.textProperty().bindBidirectional(visibleField.textProperty());

        Button toggleBtn = new Button("👁");
        toggleBtn.getStyleClass().add("eye-toggle-btn");

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
        StackPane.setAlignment(toggleBtn, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleBtn, new Insets(0, 4, 0, 0));
        return pane;
    }

    private Label createErrorLabel() {
        Label lbl = new Label();
        lbl.getStyleClass().add("error-label");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    // ==========================================
    // FORM VALIDATION & ACTIONS
    // ==========================================
    private void handleRegistration() {
        clearErrors();
        boolean isValid = true;

        // 1. Full Name
        if (txtFullName.getText().trim().isEmpty()) {
            showFieldError(txtFullName, lblFullNameError, "Please enter your full name");
            isValid = false;
        }

        // 2. Email Address
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            showFieldError(txtEmail, lblEmailError, "Please enter your email address");
            isValid = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showFieldError(txtEmail, lblEmailError, "Please enter a valid email address");
            isValid = false;
        }

        // 3. Phone Number
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) {
            showFieldError(txtPhone, lblPhoneError, "Please enter your phone number");
            isValid = false;
        } else if (!phone.matches("^[0-9\\-\\+\\s\\(\\)]{7,15}$")) {
            showFieldError(txtPhone, lblPhoneError, "Please enter a valid phone number");
            isValid = false;
        }

        // 4. Organization Name
        if (txtOrganizationName.getText().trim().isEmpty()) {
            showFieldError(txtOrganizationName, lblOrganizationNameError, "Please enter organization name");
            isValid = false;
        }

        // 5. Password (Minimum 8 chars)
        String password = txtPassword.getText();
        if (password.isEmpty()) {
            showFieldError(txtPassword, lblPasswordError, "Please enter a password");
            txtPasswordVisible.getStyleClass().add("field-error");
            isValid = false;
        } else if (password.length() < 8) {
            showFieldError(txtPassword, lblPasswordError, "Password must be at least 8 characters");
            txtPasswordVisible.getStyleClass().add("field-error");
            isValid = false;
        }

        // 6. Confirm Password
        String confirmPassword = txtConfirmPassword.getText();
        if (confirmPassword.isEmpty()) {
            showFieldError(txtConfirmPassword, lblConfirmPasswordError, "Please confirm your password");
            txtConfirmPasswordVisible.getStyleClass().add("field-error");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            showFieldError(txtConfirmPassword, lblConfirmPasswordError, "Passwords do not match");
            txtConfirmPasswordVisible.getStyleClass().add("field-error");
            isValid = false;
        }

        // 7. Business Type
        if (cbBusinessType.getValue() == null) {
            cbBusinessType.getStyleClass().add("field-error");
            lblBusinessTypeError.setText("Please select a business type");
            lblBusinessTypeError.setVisible(true);
            lblBusinessTypeError.setManaged(true);
            isValid = false;
        }

        // 8. Address
        if (txtAddress.getText().trim().isEmpty()) {
            showFieldError(txtAddress, lblAddressError, "Please enter complete address");
            isValid = false;
        }

        // 9. Terms and Conditions
        if (!chkTerms.isSelected()) {
            lblTermsError.setText("You must accept the terms & conditions");
            lblTermsError.setVisible(true);
            lblTermsError.setManaged(true);
            isValid = false;
        }

        // Registration Execution
        if (isValid) {
            showStatusBanner(
                "Organizer registration successful!",
                "#10B981", "#E6F4EA"
            );
            btnRegister.setDisable(true);
        } else {
            showStatusBanner(
                "Please fix the highlighted errors below.",
                "#DC2626", "#FEE2E2"
            );
        }
    }

    private void showFieldError(Control control, Label errorLabel, String message) {
        control.getStyleClass().add("field-error");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearErrors() {
        statusBanner.setVisible(false);
        statusBanner.setManaged(false);

        Control[] controls = {
            txtFullName, txtEmail, txtPhone, txtOrganizationName,
            txtPassword, txtPasswordVisible, txtConfirmPassword,
            txtConfirmPasswordVisible, cbBusinessType, txtAddress, txtGstNumber
        };

        for (Control c : controls) {
            c.getStyleClass().remove("field-error");
        }

        Label[] labels = {
            lblFullNameError, lblEmailError, lblPhoneError,
            lblOrganizationNameError, lblPasswordError, lblConfirmPasswordError,
            lblBusinessTypeError, lblAddressError, lblTermsError
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

    public void backToOrganizerRegistration(){

        LandingPage.landingPageStage.setScene(OrganizerRegistrationScene);
    }
}