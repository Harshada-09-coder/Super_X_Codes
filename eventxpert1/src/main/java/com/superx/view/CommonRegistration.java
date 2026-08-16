
package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CommonRegistration extends Application {

    // ===== EVENTXPERT THEME =====
    private final String BLUE = "#062B63";
    private final String HOVER_BLUE = "#0D3D7D";
    private final String TEXT = "#102A43";

    @Override
    public void start(Stage stage) {

        // =================================================
        // LEFT BLUE SIDEBAR
        // =================================================

        VBox sidebar = new VBox(18);

        sidebar.setPrefWidth(235);
        sidebar.setMinWidth(235);
        sidebar.setPadding(new Insets(28, 18, 25, 22));

        // FULL BLUE SIDEBAR
        sidebar.setStyle(
                "-fx-background-color:" + BLUE + ";"
        );

        Label logo = new Label("EventXpert");

        logo.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label sub = new Label(
                "Premium Event Management"
        );

        sub.setStyle(
                "-fx-text-fill:#BFD7F5;" +
                "-fx-font-size:13px;"
        );

        Button createEvent = new Button(
                "＋   Create Event"
        );

        createEvent.setMaxWidth(
                Double.MAX_VALUE
        );

        createEvent.setPrefHeight(42);

        createEvent.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:8px;" +
                "-fx-cursor:hand;"
        );

        Label registrationTitle = new Label(
                "Registration"
        );

        registrationTitle.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label registrationText = new Label(
                "Choose your role and\n" +
                "create your EventXpert account."
        );

        registrationText.setStyle(
                "-fx-text-fill:#C8D9F0;" +
                "-fx-font-size:13px;"
        );

        sidebar.getChildren().addAll(
                logo,
                sub,
                createEvent,
                registrationTitle,
                registrationText,

                sideItem(
                        "●   Registration",
                        true
                ),

                sideItem(
                        "○   Event Details",
                        false
                ),

                sideItem(
                        "○   Requirements",
                        false
                ),

                sideItem(
                        "○   Location",
                        false
                ),

                sideItem(
                        "○   Budget",
                        false
                ),

                sideItem(
                        "○   AI Planning",
                        false
                ),

                sideItem(
                        "○   Review",
                        false
                )
        );


        // =================================================
        // TOP SEARCH BAR
        // =================================================

        TextField search = new TextField();

        search.setPromptText(
                "Search events, venues, vendors..."
        );

        search.setPrefWidth(330);
        search.setPrefHeight(40);

        search.setStyle(
                "-fx-background-color:#F8FAFD;" +
                "-fx-border-color:#C7D2E2;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-font-size:14px;" +
                "-fx-padding:8px 14px;"
        );

        Region topSpace = new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        Label notification = new Label("🔔");
        Label help = new Label("?");
        Label profile = new Label("●");

        notification.setStyle(
                "-fx-font-size:20px;"
        );

        help.setStyle(
                "-fx-font-size:18px;" +
                "-fx-text-fill:" + BLUE + ";"
        );

        profile.setStyle(
                "-fx-font-size:22px;" +
                "-fx-text-fill:" + BLUE + ";"
        );

        HBox topBar = new HBox(
                20,
                topSpace,
                search,
                notification,
                help,
                profile
        );

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(15, 28, 15, 28)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E1E7EF;" +
                "-fx-border-width:0 0 1 0;"
        );


        // =================================================
        // PAGE HEADING
        // =================================================

        Label heading = new Label(
                "Welcome to EventXpert"
        );

        heading.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label headingText = new Label(
                "Choose your registration type and start your event journey."
        );

        headingText.setStyle(
                "-fx-text-fill:#64748B;" +
                "-fx-font-size:15px;"
        );

        VBox headingBox = new VBox(
                7,
                heading,
                headingText
        );

        headingBox.setAlignment(
                Pos.CENTER_LEFT
        );


        // =================================================
        // CORPORATE EVENT IMAGE
        // =================================================

        ImageView eventImage = new ImageView();

        try {

            Image image = new Image( "assets\\images\\event.jpg");

            eventImage.setImage(image);

        } catch (Exception e) {
        }

        eventImage.setFitWidth(650);
        eventImage.setFitHeight(500);

        eventImage.setPreserveRatio(
                false
        );
        

        StackPane imageBox = new StackPane(
                eventImage
        );
        

        imageBox.setPrefSize(
                650,
                500
        );

        imageBox.setMinSize(
                650,
                500
        );

        imageBox.setMaxSize(
                650,
                500
        );

        imageBox.setTranslateY(80);
        

        imageBox.setStyle(
                "-fx-background-color:#EAF2FC;" +
                "-fx-background-radius:15px;" +
                "-fx-border-color:#D3DFEE;" +
                "-fx-border-radius:15px;"
        );

        if (eventImage.getImage() == null) {

            Label imageText = new Label(
                    "Corporate Event Image"
            );

            imageText.setStyle(
                    "-fx-text-fill:#718096;" +
                    "-fx-font-size:16px;" +
                    "-fx-font-weight:bold;"
            );

            imageBox.getChildren().add(
                    imageText
            );
        }


        // =================================================
        // REGISTRATION HEADING
        // =================================================

        Label chooseTitle = new Label(
                "Choose Registration Type"
        );

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


        // =================================================
        // CUSTOMER
        // =================================================

        VBox customer = registrationCard(
                "👤",
                "Customer Registration",
                "Plan and manage your events, guests, " +
                "requirements and budgets.",
                "Register as Customer →"
        );


        // =================================================
        // ORGANIZER
        // =================================================

        VBox organizer = registrationCard(
                "🏢",
                "Organizer Registration",
                "Manage corporate events, schedules, " +
                "clients and complete event operations.",
                "Register as Organizer →"
        );


        // =================================================
        // VENDOR
        // =================================================

        VBox vendor = registrationCard(
                "🛍",
                "Vendor Registration",
                "Join EventXpert as a vendor and offer " +
                "your services for corporate events.",
                "Register as Vendor →"
        );


        // =================================================
        // RIGHT REGISTRATION CARDS
        // =================================================

        VBox cards = new VBox(
                12,
                customer,
                organizer,
                vendor
        );

        VBox rightContent = new VBox(
                14,
                chooseTitle,
                chooseText,
                cards
        );

        rightContent.setPrefWidth(500);

        rightContent.setMaxWidth(500);

        rightContent.setAlignment(
                Pos.TOP_LEFT
        );


        // =================================================
        // IMAGE + REGISTRATION
        // =================================================

        HBox mainArea = new HBox(
        35,
        imageBox,
        rightContent
);

       mainArea.setAlignment(
        Pos.TOP_CENTER
);

        mainArea.setPadding(
        new Insets(30, 0, 0, 0)
);


        // =================================================
        // MAIN CONTENT
        // =================================================

        VBox content = new VBox(
                22,
                headingBox,
                mainArea
        );

        content.setPadding(
                new Insets(
                        28,
                        40,
                        25,
                        40
                )
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        content.setStyle(
                "-fx-background-color:#F5F8FC;"
        );


        // =================================================
        // SCROLL
        // =================================================

        ScrollPane scroll = new ScrollPane(
                content
        );

        scroll.setFitToWidth(
                true
        );

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color:#F5F8FC;"
        );


        // =================================================
        // FOOTER BUTTONS
        // =================================================
           Button back = new Button("← Back");
            back.setPrefWidth(110); 
            back.setPrefHeight(38);
             back.setStyle( "-fx-background-color:white;" + "-fx-text-fill:" + BLUE + ";" + "-fx-border-color:#AFC0D5;" + "-fx-border-radius:7px;" + "-fx-background-radius:7px;" + "-fx-padding:9px;" + "-fx-font-size:13px;" + "-fx-cursor:hand;" );
              HBox footer = new HBox(back); 
              footer.setAlignment(Pos.CENTER_RIGHT);
               footer.setPadding( new Insets(12, 28, 12, 28) ); 
               footer.setStyle( "-fx-background-color:white;" + "-fx-border-color:#DCE3EC;" + "-fx-border-width:1 0 0 0;" );
        

        


        // =================================================
        // RIGHT PANEL
        // =================================================

        BorderPane rightPanel =
                new BorderPane();

        rightPanel.setTop(
                topBar
        );

        rightPanel.setCenter(
                scroll
        );

        rightPanel.setBottom(
                footer
        );


        // =================================================
        // COMPLETE PAGE
        // =================================================

        HBox root = new HBox(
                sidebar,
                rightPanel
        );

        HBox.setHgrow(
                rightPanel,
                Priority.ALWAYS
        );


        // =================================================
        // SCENE
        // =================================================

        Scene scene = new Scene(
                root,
                1250,
                780
        );

        stage.setTitle(
                "EventXpert - Registration"
        );

        stage.setScene(
                scene
        );

        stage.setMaximized(
                true
        );

        stage.show();
    }


    // =====================================================
    // REGISTRATION CARD METHOD
    // =====================================================

    private VBox registrationCard(
            String icon,
            String title,
            String description,
            String buttonText
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size:24px;"
        );


        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );


        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill:#64748B;" +
                "-fx-font-size:12px;"
        );


        Button button =
                new Button(buttonText);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(
                38
        );


        // EXACT SAME BLUE AS SIDEBAR
        button.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7px;" +
                "-fx-cursor:hand;"
        );


        // HOVER
        button.setOnMouseEntered(
                e -> button.setStyle(
                        "-fx-background-color:" + HOVER_BLUE + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:7px;" +
                        "-fx-cursor:hand;"
                )
        );


        // NORMAL
        button.setOnMouseExited(
                e -> button.setStyle(
                        "-fx-background-color:" + BLUE + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:7px;" +
                        "-fx-cursor:hand;"
                )
        );


        VBox card = new VBox(
                7,
                iconLabel,
                titleLabel,
                descriptionLabel,
                button
        );

        card.setPadding(
                new Insets(
                        12,
                        17,
                        12,
                        17
                )
        );

        card.setPrefWidth(
                500
        );

        card.setMaxWidth(
                500
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D5DFEC;" +
                "-fx-border-radius:10px;" +
                "-fx-background-radius:10px;"
        );

        return card;
    }


    // =====================================================
    // SIDEBAR ITEM
    // =====================================================

    private Label sideItem(
            String text,
            boolean active
    ) {

        Label item =
                new Label(text);

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setPadding(
                new Insets(
                        9,
                        8,
                        9,
                        8
                )
        );


        if (active) {

            item.setStyle(
                    "-fx-text-fill:white;" +
                    "-fx-font-size:14px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-background-color:#0D3D7D;" +
                    "-fx-background-radius:6px;"
            );

        } else {

            item.setStyle(
                    "-fx-text-fill:#DCE9FA;" +
                    "-fx-font-size:14px;"
            );
        }

        return item;
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}

