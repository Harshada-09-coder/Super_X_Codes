package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class ForgotPassword extends Application {

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

        leftSide.setStyle(
                "-fx-background-color: #082E6B;");

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

        logoView.setFitWidth(38);
        logoView.setFitHeight(38);

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

        logoBox.setSpacing(3);

        logoBox.getChildren().addAll(
                logoView,
                eventXpertText);

        // Registration page सारखी position
        VBox.setMargin(
                logoBox,
                new Insets(
                        30,
                        0,
                        20,
                        20));

        Image customerImage = new Image("assets\\images\\reg image.png");

        ImageView customerImageView = new ImageView(
                customerImage);

        customerImageView.setFitWidth(420);

        customerImageView.setFitHeight(400);

        customerImageView.setPreserveRatio(true);

        customerImageView.setSmooth(true);

        StackPane imageBox = new StackPane();

        imageBox.setPrefWidth(420);

        imageBox.setPrefHeight(400);

        imageBox.setAlignment(
                Pos.CENTER);

        imageBox.getChildren().add(
                customerImageView);

        // Image खाली text जवळ ठेवण्यासाठी
        VBox.setMargin(
                imageBox,
                new Insets(
                        5,
                        0,
                        3,
                        0));

        Label recoveryTitle = new Label(
                "Secure Account Recovery");

        recoveryTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        24));

        recoveryTitle.setTextFill(
                Color.WHITE);

        VBox.setMargin(
                recoveryTitle,
                new Insets(
                        0,
                        0,
                        8,
                        0));

        Label recoveryDescription = new Label(
                "Reset your password securely and\n"
                        + "get back to managing your events.");

        recoveryDescription.setFont(
                Font.font(
                        "Arial",
                        15));

        recoveryDescription.setTextFill(
                Color.web("#D9E2F2"));

        recoveryDescription.setLineSpacing(5);

        leftSide.getChildren().addAll(

                backToLogin,

                logoBox,

                imageBox,

                recoveryTitle,

                recoveryDescription

        );

        VBox rightSide = new VBox();

        rightSide.setPrefWidth(865);

        rightSide.setPadding(
                new Insets(
                        150,
                        100,
                        60,
                        100));

        rightSide.setStyle(
                "-fx-background-color: white;");

        Label heading = new Label(
                "Forgot Password?");

        heading.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        34));

        heading.setTextFill(
                Color.web("#111111"));

        Label subtitle = new Label(
                "Enter your registered email address and we'll\n"
                        + "help you reset your password.");

        subtitle.setFont(
                Font.font(
                        "Arial",
                        17));

        subtitle.setTextFill(
                Color.web("#303746"));

        subtitle.setLineSpacing(5);

        VBox.setMargin(
                subtitle,
                new Insets(
                        12,
                        0,
                        35,
                        0));

        Label emailLabel = new Label(
                "Email Address");

        emailLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15));

        emailLabel.setTextFill(
                Color.web("#111111"));

        TextField emailField = new TextField();

        emailField.setPromptText(
                "Enter your email address");

        emailField.setPrefHeight(50);

        emailField.setFont(
                Font.font(
                        "Arial",
                        15));

        emailField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #C7CED8;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 7;" +
                        "-fx-background-radius: 7;" +
                        "-fx-padding: 0 15;");

        VBox.setMargin(
                emailField,
                new Insets(
                        7,
                        0,
                        25,
                        0));

        Button resetButton = new Button(
                "Send Reset Link");

        resetButton.setPrefHeight(48);

        resetButton.setMaxWidth(
                Double.MAX_VALUE);

        resetButton.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16));

        resetButton.setTextFill(
                Color.WHITE);

        resetButton.setStyle(
                "-fx-background-color: #082E6B;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;");

        resetButton.setOnMouseEntered(
                event -> {

                    resetButton.setStyle(
                            "-fx-background-color: #0D3E87;" +
                                    "-fx-background-radius: 6;" +
                                    "-fx-cursor: hand;");
                });

        resetButton.setOnMouseExited(
                event -> {

                    resetButton.setStyle(
                            "-fx-background-color: #082E6B;" +
                                    "-fx-background-radius: 6;" +
                                    "-fx-cursor: hand;");
                });

        Label messageLabel = new Label();

        messageLabel.setFont(
                Font.font(
                        "Arial",
                        14));

        messageLabel.setTextFill(
                Color.web("#26734D"));

        messageLabel.setWrapText(true);

        VBox.setMargin(
                messageLabel,
                new Insets(
                        15,
                        0,
                        0,
                        0));

        resetButton.setOnAction(
                event -> {

                    String email = emailField
                            .getText()
                            .trim();

                    if (email.isEmpty()) {

                        messageLabel.setText("");

                        showAlert(
                                "Please enter your email address.");

                        return;
                    }

                    if (!email.contains("@")
                            || !email.contains(".")) {

                        messageLabel.setText("");

                        showAlert(
                                "Please enter a valid email address.");

                        return;
                    }

                    messageLabel.setText(
                            "✓ Reset link has been sent to "
                                    + email
                                    + ".");
                });

        rightSide.getChildren().addAll(

                heading,

                subtitle,

                emailLabel,

                emailField,

                resetButton,

                messageLabel

        );

        root.getChildren().addAll(
                leftSide,
                rightSide);

        Scene scene = new Scene(
                root,
                1365,
                900);

        stage.setTitle(
                "EventXpert - Forgot Password");

        stage.setScene(scene);

        stage.setMinWidth(1550);

        stage.setMinHeight(900);

        stage.show();
    }

    private void showAlert(
            String message) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION);

        alert.setTitle(
                "EventXpert");

        alert.setHeaderText(null);

        alert.setContentText(
                message);

        alert.showAndWait();
    }

    public static void main(
            String[] args) {

        launch(args);
    }
}