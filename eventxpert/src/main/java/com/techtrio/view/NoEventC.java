package com.techtrio.view;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class NoEventC extends Application {

    private final String BLUE = "#002A60";
    private final String BG = "#F6F8FB";

    private Label noEventsLabel;
    private Label descriptionLabel;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

       

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setMaxWidth(260);

        sidebar.setPadding(
                new Insets(28, 17, 20, 17)
        );

        sidebar.setStyle(
                "-fx-background-color:" + BLUE + ";"
        );

        // Logo
        Label logo = new Label("EventXpert");

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        42
                )
        );

        logo.setTextFill(Color.WHITE);

        Label premium =
                new Label("Premium Management");

        premium.setFont(
                Font.font("Arial", 14)
        );

        premium.setTextFill(
                Color.web("#B8C9E0")
        );

        VBox logoBox =
                new VBox(2, logo, premium);

        // New Project
        Button newProject =
                new Button("+   New Project");

        newProject.setPrefHeight(48);
        newProject.setMaxWidth(
                Double.MAX_VALUE
        );

        newProject.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#182230;" +
                "-fx-font-size:16;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:8;"
        );

        VBox.setMargin(
                newProject,
                new Insets(35, 0, 30, 0)
        );

        // Navigation
        VBox navigation =
                new VBox(8);

        navigation.getChildren().addAll(

                navItem(
                        "▦",
                        "Dashboard",
                        false
                ),

                navItem(
                        "▣",
                        "My Events",
                        true
                ),

                navItem(
                        "⊕",
                        "Create Event",
                        false
                ),

                navItem(
                        "✦",
                        "AI Event Planner",
                        false
                )
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        
        VBox bottomNav =
                new VBox(8);

        Region line =
                new Region();

        line.setPrefHeight(1);
        line.setMaxWidth(
                Double.MAX_VALUE
        );

        line.setStyle(
                "-fx-background-color:#17477D;"
        );

        bottomNav.getChildren().addAll(

                line,

                navItem(
                        "⚙",
                        "Settings",
                        false
                ),

                navItem(
                        "?",
                        "Help",
                        false
                )
        );

        sidebar.getChildren().addAll(

                logoBox,
                newProject,
                navigation,
                spacer,
                bottomNav
        );

        

        VBox right =
                new VBox();

        HBox topBar =
                createTopBar();

        StackPane content =
                createMainContent();

        HBox footer =
                createFooter(stage);

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        right.getChildren().addAll(
                topBar,
                content,
                footer
        );

        root.setLeft(sidebar);
        root.setCenter(right);

        

        Scene scene =
                new Scene(
                        root,
                        1280,
                        760
                );

        stage.setTitle(
                "EventXpert - My Events"
        );

        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
    }

    
    private HBox navItem(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font("Arial", 22)
        );

        Label textLabel =
                new Label(text);

        textLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        HBox item =
                new HBox(
                        15,
                        iconLabel,
                        textLabel
                );

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(
                        12,
                        14,
                        12,
                        14
                )
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:#073B78;" +
                    "-fx-background-radius:9;" +
                    "-fx-border-color:#BFE9FF;" +
                    "-fx-border-width:0 0 0 3;" +
                    "-fx-border-radius:9;"
            );

            iconLabel.setTextFill(
                    Color.WHITE
            );

            textLabel.setTextFill(
                    Color.WHITE
            );

        } else {

            item.setStyle(
                    "-fx-background-color:transparent;"
            );

            iconLabel.setTextFill(
                    Color.web("#91A6C1")
            );

            textLabel.setTextFill(
                    Color.web("#91A6C1")
            );
        }

        return item;
    }


    

    private HBox createTopBar() {

        HBox top =
                new HBox();

        top.setPrefHeight(68);

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        top.setPadding(
                new Insets(
                        10,
                        28,
                        10,
                        25
                )
        );

        top.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#DCE2E9;" +
                "-fx-border-width:0 0 1 0;"
        );

        
        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search..."
        );

        searchField.setPrefWidth(275);
        searchField.setPrefHeight(42);

        searchField.setFont(
                Font.font(
                        "Arial",
                        15
                )
        );

        searchField.setStyle(
                "-fx-background-color:#F8F9FB;" +
                "-fx-border-color:#C7D0DB;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-padding:0 12 0 12;"
        );

        
        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    if (newValue == null ||
                            newValue.trim().isEmpty()) {

                        noEventsLabel.setText(
                                "No events yet"
                        );

                        descriptionLabel.setText(
                                "Start planning your first event with EventXpert.\n" +
                                "Create detailed itineraries, manage guests, and track budgets\n" +
                                "seamlessly."
                        );

                    } else {

                        noEventsLabel.setText(
                                "No matching events"
                        );

                        descriptionLabel.setText(
                                "No events found for \"" +
                                newValue.trim() +
                                "\".\n" +
                                "Create a new event to get started."
                        );
                    }
                }
        );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label notification =
                new Label("♧");

        notification.setFont(
                Font.font(24)
        );

        Label apps =
                new Label("⠿");

        apps.setFont(
                Font.font(28)
        );

        Label profile =
                new Label("●");

        profile.setFont(
                Font.font(26)
        );

        notification.setTextFill(
                Color.web("#111827")
        );

        apps.setTextFill(
                Color.web("#111827")
        );

        profile.setTextFill(
                Color.web(BLUE)
        );

        top.getChildren().addAll(

                searchField,
                space,
                notification,
                apps,
                profile
        );

        return top;
    }

   

    private StackPane createMainContent() {

        StackPane main =
                new StackPane();

        main.setStyle(
                "-fx-background-color:" + BG + ";"
        );

       
        Label watermark =
                new Label("EVENTXPERT");

        watermark.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        120
                )
        );

        watermark.setTextFill(
                Color.web("#E9EEF5")
        );

        watermark.setRotate(-8);

        StackPane.setAlignment(
                watermark,
                Pos.CENTER
        );

        

        VBox page =
                new VBox();

        page.setPadding(
                new Insets(
                        42,
                        50,
                        35,
                        50
                )
        );

        page.setSpacing(10);

    
        Label title =
                new Label("My Events");

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        36
                )
        );

        title.setTextFill(
                Color.web("#111111")
        );

        Label subtitle =
                new Label(
                        "Manage and track all your events in one place."
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        17
                )
        );

        subtitle.setTextFill(
                Color.web("#344054")
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        

        VBox card =
                new VBox();

        card.setAlignment(
                Pos.CENTER
        );

        card.setSpacing(18);

        card.setPrefWidth(820);
        card.setMaxWidth(820);

        card.setPrefHeight(520);
        card.setMaxHeight(520);

        card.setPadding(
                new Insets(35)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E2E7ED;" +
                "-fx-border-radius:14;" +
                "-fx-background-radius:14;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),18,0,0,5);"
        );

    
        StackPane calendarCircle =
                new StackPane();

        calendarCircle.setPrefSize(
                150,
                150
        );

        calendarCircle.setMaxSize(
                150,
                150
        );

        calendarCircle.setStyle(
                "-fx-background-color:#F0F3F7;" +
                "-fx-background-radius:100;"
        );

        Label calendar =
                new Label("▣");

        calendar.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        70
                )
        );

        calendar.setTextFill(
                Color.web("#66758A")
        );

        calendarCircle.getChildren().add(
                calendar
        );

        
        noEventsLabel =
                new Label("No events yet");

        noEventsLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        28
                )
        );

        noEventsLabel.setTextFill(
                Color.web("#111111")
        );

        
        descriptionLabel =
                new Label(
                        "Start planning your first event with EventXpert.\n" +
                        "Create detailed itineraries, manage guests, and track budgets\n" +
                        "seamlessly."
                );

        descriptionLabel.setAlignment(
                Pos.CENTER
        );

        descriptionLabel.setTextAlignment(
                javafx.scene.text.TextAlignment.CENTER
        );

        descriptionLabel.setFont(
                Font.font(
                        "Arial",
                        17
                )
        );

        descriptionLabel.setTextFill(
                Color.web("#344054")
        );

        // Create button
        Button createButton =
                new Button(
                        "+   Create Your First Event"
                );

        createButton.setPrefSize(
                250,
                50
        );

        createButton.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        createButton.setTextFill(
                Color.WHITE
        );

        createButton.setStyle(
                "-fx-background-color:#635BFF;" +
                "-fx-background-radius:9;"
        );

        createButton.setOnMouseEntered(
                e -> createButton.setStyle(
                        "-fx-background-color:#5148E8;" +
                        "-fx-background-radius:9;"
                )
        );

        createButton.setOnMouseExited(
                e -> createButton.setStyle(
                        "-fx-background-color:#635BFF;" +
                        "-fx-background-radius:9;"
                )
        );

        card.getChildren().addAll(

                calendarCircle,
                noEventsLabel,
                descriptionLabel,
                createButton
        );

        VBox wrapper =
                new VBox();

        wrapper.setSpacing(45);

        wrapper.setAlignment(
                Pos.TOP_CENTER
        );

        wrapper.getChildren().addAll(
                heading,
                card
        );

        StackPane.setAlignment(
                wrapper,
                Pos.TOP_CENTER
        );

        main.getChildren().addAll(
                watermark,
                wrapper
        );

        return main;
    }

    
    private HBox createFooter(
            Stage stage
    ) {

        HBox footer =
                new HBox();

        footer.setPrefHeight(65);

        footer.setAlignment(
                Pos.CENTER
        );

        footer.setPadding(
                new Insets(
                        10,
                        30,
                        10,
                        30
                )
        );

        footer.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:#D6DEE8;" +
                "-fx-border-width:1 0 0 0;"
        );

        
        Button backButton =
                new Button("Back");

        backButton.setPrefSize(
                100,
                42
        );

        backButton.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        backButton.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#002A60;" +
                "-fx-border-color:#9FB0C5;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;"
        );

        backButton.setOnAction(
                e -> stage.close()
        );

        

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

       
        Button nextButton =
                new Button("Next");

        nextButton.setPrefSize(
                100,
                42
        );

        nextButton.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        nextButton.setTextFill(
                Color.WHITE
        );

        nextButton.setStyle(
                "-fx-background-color:#002A60;" +
                "-fx-background-radius:7;"
        );

        nextButton.setOnMouseEntered(
                e -> nextButton.setStyle(
                        "-fx-background-color:#073B78;" +
                        "-fx-background-radius:7;"
                )
        );

        nextButton.setOnMouseExited(
                e -> nextButton.setStyle(
                        "-fx-background-color:#002A60;" +
                        "-fx-background-radius:7;"
                )
        );

        nextButton.setOnAction(
                e -> {

                    System.out.println(
                            "Next button clicked"
                    );

                    
                }
        );

        footer.getChildren().addAll(
                backButton,
                space,
                nextButton
        );

        return footer;
        
    }

    

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}


