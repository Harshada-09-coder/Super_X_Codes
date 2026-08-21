
package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CommonRegistration {

         String BLUE = "#062B63";
         String HOVER_BLUE = "#0D3D7D";
         String TEXT = "#102A43";
        
        private Scene commonRegistrationScene;
        Scene getcommonRegistration(Runnable callBackAction1){
     
                Label heading = new Label("Welcome to EventXpert");

        heading.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label headingText = new Label("Choose your registration type and start your event journey.");

        headingText.setStyle(
                "-fx-text-fill:#64748B;" +
                "-fx-font-size:15px;"
        );

        VBox headingBox = new VBox(7,heading,headingText);

        headingBox.setAlignment(Pos.CENTER_LEFT);

        Label chooseTitle = new Label("Choose Registration Type");

        chooseTitle.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:21px;" +
                "-fx-font-weight:bold;"
        );

        Label chooseText = new Label(
                "Select the role that best describes you."
        );

        chooseText.setStyle(
                "-fx-text-fill:#64748B;" +
                "-fx-font-size:13px;"
        );


        VBox customer = registrationCard(
                "👤",
                "Customer Registration",
                "Plan and manage your events, guests, " +
                "requirements and budgets.",
                "Register as Customer →",
                () -> {
            CustomerRegistration customerRegistration = new CustomerRegistration();

            Runnable callBackAction = () -> {
                backToCommonRegistration();
            };

            LandingPage.landingPageStage.setScene(
                    customerRegistration.getCustomerRegistrationScene(callBackAction)
            );
        }        
);

        VBox organizer = registrationCard(
                "🏢",
                "Organizer Registration",
                "Manage corporate events, schedules, " +
                "clients and complete event operations.",
                "Register as Organizer →",
                () -> {
            OrganizerRegistration organizerRegistration = new OrganizerRegistration();

            Runnable callBackAction = () -> {
                backToCommonRegistration();
            };

            LandingPage.landingPageStage.setScene(
                    organizerRegistration.getOrganizerRegistrationScene(callBackAction)
            );
        }
        );

        VBox vendor = registrationCard(
                "🛍",
                "Vendor Registration",
                "Join EventXpert as a vendor and offer " +
                "your services for corporate events.",
                "Register as Vendor →",
                () -> {
            VendorRegistration vendorRegistration = new VendorRegistration();

            Runnable callBackAction = () -> {
                backToCommonRegistration();
            };

            LandingPage.landingPageStage.setScene(
                    vendorRegistration.getvendorRegistrationScene(callBackAction)
            );
        }
        );


        HBox cards = new HBox(20,customer,organizer,vendor);
        cards.setTranslateY(50);

        VBox rightContent = new VBox(14,chooseTitle, chooseText, cards);

        rightContent.setPrefWidth(900);

        rightContent.setMaxWidth(900);

        rightContent.setAlignment(Pos.CENTER);


        HBox mainArea = new HBox(35, rightContent);

       mainArea.setAlignment(Pos.TOP_CENTER);

        mainArea.setPadding(new Insets(30, 0, 0, 0));

        VBox content = new VBox(22,headingBox,mainArea);

        content.setPadding(new Insets( 28, 40, 25, 40 ));

        content.setAlignment(Pos.CENTER);

        content.setStyle("-fx-background-color:#F5F8FC;");


           Button back = new Button("← Back");
            back.setPrefWidth(110); 
            back.setPrefHeight(38);
             back.setStyle( "-fx-background-color:white;" + "-fx-text-fill:" + BLUE + ";" + "-fx-border-color:#AFC0D5;" + "-fx-border-radius:7px;" + "-fx-background-radius:7px;" + "-fx-padding:9px;" + "-fx-font-size:13px;" + "-fx-cursor:hand;" );
             back.setOnAction(e -> {
                System.out.println("Back To Landing Page");
                callBackAction1.run();
             });
              HBox footer = new HBox(back); 
              footer.setAlignment(Pos.CENTER_RIGHT);
               footer.setPadding( new Insets(12, 28, 12, 28) ); 
               footer.setStyle( "-fx-background-color:white;" + "-fx-border-color:#DCE3EC;" + "-fx-border-width:1 0 0 0;" );
        
        BorderPane rightPanel = new BorderPane();


        rightPanel.setBottom(footer);

        VBox root = new VBox(250,content, rightPanel);

        HBox.setHgrow(rightPanel,Priority.ALWAYS);

        Scene scene = new Scene(root, 1550, 750);
        commonRegistrationScene = scene;
        return commonRegistrationScene;
    }

    private VBox registrationCard(
            String icon,
            String title,
            String description,
            String buttonText,
            Runnable action
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle("-fx-font-size:24px;");


        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        Label descriptionLabel = new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill:#64748B;" +
                "-fx-font-size:12px;"
        );


        Button button = new Button(buttonText);

        button.setMaxWidth(Double.MAX_VALUE);

        button.setPrefHeight(38);


        button.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7px;" +
                "-fx-cursor:hand;"
        );


        button.setOnMouseEntered(
                e -> {button.setStyle(
                        "-fx-background-color:" + HOVER_BLUE + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:7px;" +
                        "-fx-cursor:hand;"
                );}
        );
        button.setOnMouseExited(
                e ->{ button.setStyle(
                        "-fx-background-color:" + BLUE + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:7px;" +
                        "-fx-cursor:hand;"
                );}
        );
        
        button.setOnAction(e -> action.run());
        VBox card = new VBox( 7,iconLabel,titleLabel,descriptionLabel,button);

        card.setPadding(
                new Insets( 12, 17, 12, 17 )
        );

        card.setPrefWidth(500);

        card.setMaxWidth(500);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D5DFEC;" +
                "-fx-border-radius:10px;" +
                "-fx-background-radius:10px;"
        );

        return card;
    }

   public void backToCommonRegistration(){
        LandingPage.landingPageStage.setScene(commonRegistrationScene);
   }
}