package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginPage extends Application {

    @Override
    public void start(Stage stage) {

        
        String navy = "#082E6B";
        String lightBlue = "#7EAED4";
        String background = "#F6F8FA";
        String borderColor = "#C9CED6";

       
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(0);
        root.setPadding(new Insets(0, 0, 0, 0));
        root.setStyle("-fx-background-color: " + background + ";");

       
        Label logo = new Label("EventXpert");

        logo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        logo.setTextFill(Color.web(navy));

        VBox.setMargin(logo, new Insets(100, 0, 36, 0));

        VBox card = new VBox();
        card.setPrefWidth(480);
        card.setMaxWidth(480);

        card.setPadding(new Insets(28, 26, 24, 26));
        card.setSpacing(0);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #7EAED4;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setOffsetY(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));

        card.setEffect(shadow);

        
        Label welcome = new Label("Welcome Back");

        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        welcome.setTextFill(Color.web(navy));

        
        Label subtitle = new Label("Sign in to continue to EventXpert");

        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web(lightBlue));

        VBox.setMargin(subtitle, new Insets(10, 0, 32, 0));

       
        Label emailLabel = new Label("Email Address");

        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        emailLabel.setTextFill(Color.BLACK);

        
        TextField emailField = new TextField();

        emailField.setPromptText("you@company.com");
        emailField.setPrefHeight(42);

        emailField.setFont(Font.font("Arial", 15));

        emailField.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 0 13;"
        );

        VBox.setMargin(emailField, new Insets(7, 0, 16, 0));

        
        Label passwordLabel = new Label("Password");

        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        passwordLabel.setTextFill(Color.BLACK);

       
        PasswordField passwordField = new PasswordField();

        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(42);

        passwordField.setFont(Font.font("Arial", 15));

        passwordField.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 0 42 0 13;"
        );

        
        TextField visiblePasswordField = new TextField();

        visiblePasswordField.setPromptText("Enter your password");
        visiblePasswordField.setPrefHeight(42);

        visiblePasswordField.setFont(Font.font("Arial", 15));

        visiblePasswordField.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 0 42 0 13;"
        );

        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

       
        Button eyeButton = new Button("◉");

        eyeButton.setPrefWidth(40);
        eyeButton.setPrefHeight(38);

        eyeButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #687080;" +
                "-fx-font-size: 16;" +
                "-fx-cursor: hand;"
        );

        StackPane passwordContainer = new StackPane();

        passwordContainer.setPrefHeight(42);

        passwordContainer.getChildren().addAll(
                passwordField,
                visiblePasswordField,
                eyeButton
        );

        StackPane.setAlignment(eyeButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(eyeButton, new Insets(0, 3, 0, 0));

        eyeButton.setOnAction(e -> {

            if (passwordField.isVisible()) {

                visiblePasswordField.setText(passwordField.getText());

                passwordField.setVisible(false);
                passwordField.setManaged(false);

                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);

            } else {

                passwordField.setText(visiblePasswordField.getText());

                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);

                passwordField.setVisible(true);
                passwordField.setManaged(true);
            }
        });


        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");

        forgotPassword.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        forgotPassword.setTextFill(Color.web(navy));

        forgotPassword.setBorder(Border.EMPTY);
        forgotPassword.setPadding(new Insets(0));

        forgotPassword.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-underline: false;" +
                "-fx-cursor: hand;"
        );

        HBox forgotBox = new HBox();

        forgotBox.setAlignment(Pos.CENTER_RIGHT);
        forgotBox.getChildren().add(forgotPassword);

        HBox.setMargin(forgotBox, new Insets(22, 0, 36, 0));

        forgotPassword.setOnAction(e -> {
            System.out.println("Forgot Password clicked");
        });

        Button signInButton = new Button("Sign In");

        signInButton.setPrefWidth(280);
        signInButton.setPrefHeight(50);
        VBox.setMargin(signInButton, new Insets(25, 0, 0, 0));

       HBox signInBox = new HBox();
       signInBox.setAlignment(Pos.CENTER);
       signInBox.getChildren().add(signInButton);


        
        
        signInButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        signInButton.setTextFill(Color.WHITE);

        signInButton.setStyle(
                "-fx-background-color: " + navy + ";" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );

        signInButton.setOnMouseEntered(e ->
                signInButton.setStyle(
                        "-fx-background-color: #0D3E87;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
                )
        );

        signInButton.setOnMouseExited(e ->
                signInButton.setStyle(
                        "-fx-background-color: " + navy + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
                )
        );

        signInButton.setOnAction(e -> {

            String email = emailField.getText();

            String password = passwordField.isVisible()
                    ? passwordField.getText()
                    : visiblePasswordField.getText();

            if (email.isEmpty() || password.isEmpty()) {

                Alert alert = new Alert(
                        Alert.AlertType.WARNING,
                        "Please enter email and password."
                );

                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.showAndWait();

            } else {

                System.out.println("Email: " + email);
                System.out.println("Password: " + password);

            
            }
        });

        
        Separator separator = new Separator();

        separator.setMaxWidth(Double.MAX_VALUE);

        VBox.setMargin(separator, new Insets(34, 0, 28, 0));

       
        Label accountText = new Label("Don't have an account?");

        accountText.setFont(Font.font("Arial", 15));
        accountText.setTextFill(Color.web("#20242B"));

       
        Hyperlink createAccount = new Hyperlink("Create Account");

        createAccount.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        createAccount.setTextFill(Color.web(navy));

        createAccount.setPadding(new Insets(0));
        createAccount.setBorder(Border.EMPTY);

        createAccount.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-underline: false;" +
                "-fx-cursor: hand;"
        );

        createAccount.setOnAction(e -> {
            System.out.println("Create Account clicked");

            
        });

        HBox createAccountBox = new HBox();

        createAccountBox.setAlignment(Pos.CENTER);
        createAccountBox.setSpacing(7);

        createAccountBox.getChildren().addAll(
                accountText,
                createAccount
        );

        
        card.getChildren().addAll(
                welcome,
                subtitle,

                emailLabel,
                emailField,

                passwordLabel,
                passwordContainer,

                forgotBox,

                signInBox,

                separator,

                createAccountBox
        );

       
        root.getChildren().addAll(
                logo,
                card
        );

       
        Scene scene = new Scene(root, 1530, 900);

        stage.setTitle("EventXpert - Login");
        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(700);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
