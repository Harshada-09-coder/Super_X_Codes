package com.techtrio.view;

import com.techtrio.controller.AuthController;
import com.techtrio.controller.SignInResult;
import com.techtrio.controller.VendorController;
import com.techtrio.controller.VendorState;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginPage {

        private Scene loginPageScene;
        Scene getloginPageScene(Runnable callBackAction){
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

        Text output = new Text();
        output.setStyle("-fx-fill: white");

        AuthController controller = new AuthController();

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

        SignInResult result = controller.signIn(email.trim(),password);
        if (result != null) {

    System.out.println("Login Successful");

    String uid = result.getUid();
    String idToken = result.getIdToken();

    System.out.println("UID: " + uid);
    System.out.println("ID Token received");

    String role =
            controller.getUserRole(uid, idToken);

    System.out.println(
            "User Role: " + role
    );

    if (role == null) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText(
                "User role not found."
        );
        alert.showAndWait();
        return;
    }
    if (role.equalsIgnoreCase("admin")) {

    System.out.println("Opening Admin Dashboard");

    AdminDashboardA adminDashboard = new AdminDashboardA();

    LandingPage.landingPageStage.setScene(adminDashboard.getadminDashboardScene()
      );
      } else if (role.equalsIgnoreCase("customer")) {
        System.out.println("Opening Customer Dashboard");
        ActiveCustomerDashboardC customerDashboardC = new ActiveCustomerDashboardC();

        Runnable callBackAction1 = new Runnable() {
                public void run(){
                        backToLoginPage();
                }
        };
       LandingPage.landingPageStage.setScene( customerDashboardC.getactiveCustomerScene(callBackAction1));

     } else if (role.equalsIgnoreCase("organizer")) {
         System.out.println( "Opening Organizer Dashboard");
         OrganizerDashboardO organizerDashboardO = new OrganizerDashboardO();

         LandingPage.landingPageStage.setScene(organizerDashboardO.getorganizerDashboardScene(callBackAction));

} else if (role.equalsIgnoreCase("vendor")) {

    System.out.println("Opening Vendor Dashboard");

    VendorController vendorController = new VendorController();
    VendorState vendorState = vendorController.getVendorState(uid, idToken);

    if (vendorState == null) {
        vendorState = new VendorState(false, false, "NEW", "NONE");
    }

    System.out.println("========== VENDOR STATE ==========");
    System.out.println("Profile Completed: " + vendorState.isProfileCompleted());
    System.out.println("Has Provided Service: " + vendorState.hasProvidedService());
    System.out.println("Onboarding Status: " + vendorState.getOnboardingStatus());
    System.out.println("Current Process: " + vendorState.getCurrentProcess());
    System.out.println("==================================");

    if (!vendorState.hasProvidedService()) {
        System.out.println("Opening NEW Vendor Dashboard");
        InactiveVendorDash newVendorDashboard = new InactiveVendorDash(uid, idToken);
        LandingPage.landingPageStage.setScene(newVendorDashboard.getinactiveVendorScene());
    } else {
        System.out.println("Opening ACTIVE Vendor Dashboard");
        VendorDashboardV vendorDashboardV = new VendorDashboardV(uid, idToken);
        LandingPage.landingPageStage.setScene(vendorDashboardV.getvendorDashboardScene());
    }
}
}});
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
            CommonRegistration commonRegistrationPage = new CommonRegistration();

                Runnable callBackAction1 = new Runnable() {
                        public void run(){
                               backToLoginPage(); 
                        }
                };
                LandingPage.landingPageStage.setScene(commonRegistrationPage.getcommonRegistration(callBackAction1));  
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
                createAccountBox,
                output
        );

       
        root.getChildren().addAll(logo, card);
       
        Scene scene = new Scene(root, 1550, 750);
        loginPageScene = scene;
        return loginPageScene;
        }

        public void backToLoginPage(){
                LandingPage.landingPageStage.setScene(loginPageScene);
        }
    }

