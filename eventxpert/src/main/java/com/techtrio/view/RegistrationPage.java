package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegistrationPage extends Application {

    private final String NAVY = "#082E6B";

    @Override
    public void start(Stage stage) {

            HBox root = new HBox();

            root.setPrefSize(1365, 900);

            VBox leftSide = new VBox();

            leftSide.setPrefWidth(500);
            leftSide.setPrefHeight(900);

            leftSide.setAlignment(Pos.TOP_LEFT);

            leftSide.setPadding(
                            new Insets(25, 35, 30, 35));

            leftSide.setStyle("-fx-background-color:  #082E6B");

            Button backToLogin = new Button("←  Back to Login");

            backToLogin.setFont(
                            Font.font(
                                            "Arial",
                                            15));

            backToLogin.setTextFill(
                            Color.WHITE);

            backToLogin.setStyle(
                            "-fx-background-color: transparent;" +
                                            "-fx-border-color: transparent;" +
                                            "-fx-padding: 5 0 5 0;" +
                                            "-fx-cursor: hand;");

            Image logoImage = new Image("assets\\images\\logo .png");
            ImageView logoView = new ImageView(logoImage);

            logoView.setFitWidth(70);
            logoView.setFitHeight(70);

            logoView.setPreserveRatio(true);

            logoView.setSmooth(true);

            Label eventXpertText = new Label("EventXpert");

            eventXpertText.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            25));

            eventXpertText.setTextFill(
                            Color.WHITE);

            HBox logoBox = new HBox();

            logoBox.setAlignment(
                            Pos.CENTER_LEFT);

            logoBox.setSpacing(1);

            logoBox.getChildren().addAll(
                            logoView,
                            eventXpertText);

            VBox.setMargin(
                            logoBox,
                            new Insets( 30, 0, 20, 40));

            Image registrationImage = new Image("assets\\images\\reg image.png");

            ImageView registrationImageView = new ImageView(
                            registrationImage);

            registrationImageView.setFitWidth(420);
            registrationImageView.setFitHeight(400);

            registrationImageView.setPreserveRatio(true);

            registrationImageView.setSmooth(true);

            StackPane imageBox = new StackPane();

            imageBox.setPrefWidth(420);
            imageBox.setPrefHeight(400);

            imageBox.setAlignment(
                            Pos.CENTER);

            imageBox.getChildren().add(
                            registrationImageView);

            VBox.setMargin(
                            imageBox,
                            new Insets( 5, 0,10,0));

            Label joinCustomer = new Label(
                            "Join as Customer");

            joinCustomer.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            24));

            joinCustomer.setTextFill(
                            Color.WHITE);

            VBox.setMargin(
                            joinCustomer,
                            new Insets(5,0,8,0));

            Label customerDescription = new Label(
                            "Book events, explore services,\nand manage your bookings easily.");

            customerDescription.setFont(
                            Font.font(
                                            "Arial",
                                            15));

            customerDescription.setTextFill(
                            Color.web("#D9E2F2"));

            customerDescription.setLineSpacing(5);

            leftSide.getChildren().addAll(

                            backToLogin,

                            logoBox,

                            imageBox,

                            joinCustomer,

                            customerDescription

            );

            VBox rightSide = new VBox();

            rightSide.setPrefWidth(865);

            rightSide.setPadding(
                            new Insets(55,85,40,85));

            rightSide.setStyle(
                            "-fx-background-color: white;");

            Label rightLogo = new Label("EventXpert");

            rightLogo.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            27));

            rightLogo.setTextFill(
                            Color.web(NAVY));

            Label heading = new Label(
                            "Create your EventXpert\naccount");

            heading.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            32));

            heading.setTextFill(
                            Color.web("#111111"));

            VBox.setMargin(
                            heading,
                            new Insets(28,0,7,0));

            Label subtitle = new Label(
                            "Plan, manage and organize your events effortlessly.");

            subtitle.setFont(
                            Font.font(
                                            "Arial",
                                            17));

            subtitle.setTextFill(
                            Color.web("#303746"));

            VBox.setMargin(
                            subtitle,
                            new Insets(0,0,22,0));

            Label fullNameLabel = createLabel("Full Name");

            TextField fullNameField = createTextField("Harshada Kurumkar");

            VBox.setMargin(
                            fullNameField,
                            new Insets(6,0,12,0));

            Label emailLabel = createLabel("Email Address");

            TextField emailField = createTextField("harshada@example.com");

            VBox.setMargin(
                            emailField,
                            new Insets(6,0,12,0));

            Label mobileLabel = createLabel("Mobile Number");

            TextField mobileField = createTextField(
                            "+1 (555) 000-0000");

            VBox.setMargin(
                            mobileField,
                            new Insets(6,0,12,0));

            Label passwordLabel = createLabel("Password");

            PasswordField passwordField = createPasswordField(
                            "Enter your password");

            Button passwordEye = createEyeButton();

            StackPane passwordBox = createPasswordBox(
                            passwordField,
                            passwordEye);

            VBox.setMargin(
                            passwordBox,
                            new Insets(6,0,12,0));

            Label confirmPasswordLabel = createLabel(
                            "Confirm Password");

            PasswordField confirmPasswordField = createPasswordField(
                            "Confirm your password");

            Button confirmEye = createEyeButton();

            StackPane confirmPasswordBox = createPasswordBox(
                            confirmPasswordField,
                            confirmEye);

            VBox.setMargin(
                            confirmPasswordBox,
                            new Insets(6,0,20,0));

            CheckBox termsCheckBox = new CheckBox();

            Label termsLabel = new Label(
                            "I agree to the Terms of Service and Privacy Policy");

            termsLabel.setFont(
                            Font.font(
                                            "Arial",
                                            15));

            termsLabel.setTextFill(
                            Color.web("#20242B"));

            HBox termsBox = new HBox();

            termsBox.setAlignment(
                            Pos.CENTER_LEFT);

            termsBox.setSpacing(10);

            termsBox.getChildren().addAll(
                            termsCheckBox,
                            termsLabel);

            VBox.setMargin(
                            termsBox,
                            new Insets( 0, 0,22, 0));

            Button createAccountButton = new Button(
                            "Create Account");

            createAccountButton.setPrefHeight(45);

            createAccountButton.setMaxWidth(
                            Double.MAX_VALUE);

            createAccountButton.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            16));

            createAccountButton.setTextFill(
                            Color.WHITE);

            createAccountButton.setStyle(
                            "-fx-background-color: #082E6B;" +
                                            "-fx-background-radius: 6;" +
                                            "-fx-cursor: hand;");

            createAccountButton.setOnMouseEntered(
                            event -> {

                                    createAccountButton.setStyle(
                                                    "-fx-background-color: #0D3E87;" +
                                                                    "-fx-background-radius: 6;" +
                                                                    "-fx-cursor: hand;");

                            });

            createAccountButton.setOnMouseExited(
                            event -> {

                                    createAccountButton.setStyle(
                                                    "-fx-background-color: #082E6B;" +
                                                                    "-fx-background-radius: 6;" +
                                                                    "-fx-cursor: hand;");

                            });

            createAccountButton.setOnAction(
                            event -> {

                                    String name = fullNameField
                                                    .getText()
                                                    .trim();

                                    String email = emailField
                                                    .getText()
                                                    .trim();

                                    String mobile = mobileField
                                                    .getText()
                                                    .trim();

                                    String password = passwordField
                                                    .getText();

                                    String confirmPasswordText = confirmPasswordField
                                                    .getText();

                                    if (name.isEmpty()
                                                    || email.isEmpty()
                                                    || mobile.isEmpty()
                                                    || password.isEmpty()
                                                    || confirmPasswordText.isEmpty()) {

                                            showAlert(
                                                            "Please fill all the fields.");

                                            return;
                                    }

                                    if (!password.equals(
                                                    confirmPasswordText)) {

                                            showAlert(
                                                            "Passwords do not match.");

                                            return;
                                    }

                                    if (!termsCheckBox.isSelected()) {

                                            showAlert(
                                                            "Please accept Terms of Service and Privacy Policy.");

                                            return;
                                    }

                                

                                    showAlert(
                                                    "Account created successfully!");

                            });

            Label alreadyAccount = new Label(
                            "Already have an account?");

            alreadyAccount.setFont(
                            Font.font("Arial",16));

            alreadyAccount.setTextFill(
                            Color.web("#303746"));

            Hyperlink signIn = new Hyperlink(
                            "Sign In");

            signIn.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            16));

            signIn.setTextFill(
                            Color.web(NAVY));

            signIn.setStyle(
                            "-fx-background-color: transparent;" +
                                            "-fx-border-color: transparent;" +
                                            "-fx-cursor: hand;");

            HBox signInBox = new HBox();

            signInBox.setAlignment(
                            Pos.CENTER);

            signInBox.setSpacing(5);

            signInBox.getChildren().addAll(
                            alreadyAccount,
                            signIn);

            VBox.setMargin(
                            signInBox,
                            new Insets( 25, 0, 0, 0));

            rightSide.getChildren().addAll(

                            rightLogo,

                            heading,

                            subtitle,

                            fullNameLabel,
                            fullNameField,

                            emailLabel,
                            emailField,

                            mobileLabel,
                            mobileField,

                            passwordLabel,
                            passwordBox,

                            confirmPasswordLabel,
                            confirmPasswordBox,

                            termsBox,

                            createAccountButton,

                            signInBox

            );

            root.getChildren().addAll(
                            leftSide,
                            rightSide);

            Scene scene = new Scene(
                            root,
                            1365,
                            900);

            stage.setTitle(
                            "EventXpert - Registration");

            stage.setScene(scene);

            stage.setMinWidth(1100);

            stage.setMinHeight(750);

            stage.show();
    }

    private Label createLabel(
                    String text) {

            Label label = new Label(text);

            label.setFont(
                            Font.font(
                                            "Arial",
                                            FontWeight.BOLD,
                                            15));

            label.setTextFill(
                            Color.web("#111111"));

            return label;
    }

    private TextField createTextField(
                    String prompt) {

            TextField field = new TextField();

            field.setPromptText(
                            prompt);

            field.setPrefHeight(48);

            field.setFont(
                            Font.font(
                                            "Arial", 15));

            field.setStyle(
                            "-fx-background-color: white;" +
                                            "-fx-border-color: #C7CED8;" +
                                            "-fx-border-width: 1;" +
                                            "-fx-border-radius: 7;" +
                                            "-fx-background-radius: 7;" +
                                            "-fx-padding: 0 15;");

            return field;
    }

    private PasswordField createPasswordField(
                    String prompt) {

            PasswordField field = new PasswordField();

            field.setPromptText(
                            prompt);

            field.setPrefHeight(48);

            field.setFont(
                            Font.font(
                                            "Arial",15));

            field.setStyle(
                            "-fx-background-color: white;" +
                                            "-fx-border-color: #C7CED8;" +
                                            "-fx-border-width: 1;" +
                                            "-fx-border-radius: 7;" +
                                            "-fx-background-radius: 7;" +
                                            "-fx-padding: 0 45 0 15;");

            return field;
    }

    private Button createEyeButton() {

            Button eye = new Button("◉");

            eye.setPrefWidth(40);
            eye.setPrefHeight(40);

            eye.setStyle(
                            "-fx-background-color: transparent;" +
                                            "-fx-border-color: transparent;" +
                                            "-fx-text-fill: #687080;" +
                                            "-fx-font-size: 16;" +
                                            "-fx-cursor: hand;");

            return eye;
    }

    private StackPane createPasswordBox(
                    PasswordField passwordField,
                    Button eyeButton) {

            StackPane box = new StackPane();

            box.setPrefHeight(48);

            box.getChildren().addAll(
                            passwordField,
                            eyeButton);

            StackPane.setAlignment(
                            eyeButton,
                            Pos.CENTER_RIGHT);

            StackPane.setMargin(
                            eyeButton,
                            new Insets( 0, 3, 0, 0));

            return box;
    }

    private void showAlert(
                    String message) {

            Alert alert = new Alert(
                            Alert.AlertType.INFORMATION);

            alert.setTitle(
                            "EventXpert");

            alert.setHeaderText(
                            null);

            alert.setContentText(
                            message);

            alert.showAndWait();
    }

    public static void main(
                    String[] args) {

            launch(args);
    }
}