package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class Notification  {

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String TEXT = "#17233C";
    private static final String BORDER = "#D7DEE8";
    private static final String LIGHT_BLUE = "#BFE5FF";

    public Scene getNotificationScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

       
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        VBox logoBox = new VBox(2);
        logoBox.setPadding(
                new Insets(24, 20, 20, 20)
        );

        logoBox.getChildren().addAll(
                styledLabel(
                        "EventXpert",
                        23,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),
                styledLabel(
                        "CUSTOMER PANEL",
                        9,
                        FontWeight.NORMAL,
                        "#B8C9E2"
                )
        );

        VBox menu = new VBox(5);
        menu.setPadding(
                new Insets(8, 12, 8, 12)
        );

        menu.getChildren().addAll(
                menuItem("▦", "Dashboard", false),
                menuItem("⊕", "Create Event", false),
                menuItem("▣", "My Events", false),
                menuItem("□", "Calendar", false),

                
                menuItem("♧", "Notifications", true)
        );

        Region sidebarSpace = new Region();

        VBox.setVgrow(
                sidebarSpace,
                Priority.ALWAYS
        );

        VBox bottomMenu = new VBox(5);

        bottomMenu.setPadding(
                new Insets(8, 12, 18, 12)
        );

        bottomMenu.getChildren().addAll(
                menuItem("◉", "Profile", false),
                menuItem("⚙", "Settings", false),
                menuItem("?", "Support", false)
        );

        sidebar.getChildren().addAll(
                logoBox,
                menu,
                sidebarSpace,
                bottomMenu
        );

    

        BorderPane rightArea = new BorderPane();

        HBox topBar = new HBox(12);

        topBar.setPrefHeight(64);
        topBar.setMinHeight(64);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(10, 20, 10, 20)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E0E6EF;" +
                "-fx-border-width:0 0 1 0;"
        );

        Button backButton = new Button("←  Back");

        backButton.setOnAction(e -> {

    EventDetailsC eventDetailsPage =
            new EventDetailsC();

    LandingPage.landingPageStage.setScene(
            eventDetailsPage.getEventDetailsScene()
    );

});

        backButton.setPrefSize(
                105,
                36
        );


        backButton.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#0D3979;" +
                "-fx-border-color:#0D3979;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );
       

   
        TextField search = new TextField();

        search.setPromptText("Search");

        search.setPrefWidth(560);
        search.setPrefHeight(36);

        search.setStyle(
                "-fx-background-color:#F7F9FC;" +
                "-fx-border-color:#D2DAE5;" +
                "-fx-border-radius:18;" +
                "-fx-background-radius:18;" +
                "-fx-padding:0 15;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:12px;"
        );

        Region topSpace = new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        Label notificationIcon = styledLabel(
                "♧",
                21,
                FontWeight.NORMAL,
                NAVY
        );

        Circle avatarCircle = new Circle(
                18,
                Color.web("#C9E8FA")
        );

        Label avatarLetter = styledLabel(
                "S",
                12,
                FontWeight.BOLD,
                NAVY
        );

        StackPane avatar = new StackPane(
                avatarCircle,
                avatarLetter
        );

        topBar.getChildren().addAll(
                backButton,
                search,
                topSpace,
                notificationIcon,
                avatar
        );

        

        VBox main = new VBox(18);

        main.setPadding(
                new Insets(25, 30, 25, 30)
        );

        main.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        // Heading

        HBox heading = new HBox();

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headingText = new VBox(4);

        headingText.getChildren().addAll(
                styledLabel(
                        "Notifications",
                        28,
                        FontWeight.BOLD,
                        NAVY
                ),

                styledLabel(
                        "Manage how you receive notifications and stay updated.",
                        13,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        Region headingSpace = new Region();

        HBox.setHgrow(
                headingSpace,
                Priority.ALWAYS
        );

        Button markAll = new Button(
                "✉  Mark all as read"
        );

        markAll.setPrefHeight(38);

        markAll.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-border-color:#D5DDE7;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        Button clearAll = new Button(
                "▥  Clear All"
        );

        clearAll.setPrefHeight(38);

        clearAll.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#D62828;" +
                "-fx-border-color:#E7B7B7;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        heading.getChildren().addAll(
                headingText,
                headingSpace,
                markAll,
                clearAll
        );

        

        HBox content = new HBox(20);

        VBox preferences = notificationPreferences();

        VBox recent = recentNotifications();

        HBox.setHgrow(
                preferences,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                recent,
                Priority.ALWAYS
        );

        content.getChildren().addAll(
                preferences,
                recent
        );

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        main.getChildren().addAll(
                heading,
                content
        );

        rightArea.setTop(topBar);
        rightArea.setCenter(main);

        root.setLeft(sidebar);
        root.setCenter(rightArea);

        

        Scene scene = new Scene(
                root,
                1365,
                800
        );

     return scene;
    }

    

    private VBox notificationPreferences() {

        VBox card = new VBox(12);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefWidth(560);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        HBox titleRow = new HBox(12);

        titleRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle iconCircle = new Circle(
                22,
                Color.web("#E4F2FF")
        );

        Label icon = styledLabel(
                "♧",
                18,
                FontWeight.BOLD,
                "#1769D1"
        );

        StackPane iconBox = new StackPane(
                iconCircle,
                icon
        );

        VBox titleBox = new VBox(3);

        titleBox.getChildren().addAll(
                styledLabel(
                        "Notification Preferences",
                        18,
                        FontWeight.BOLD,
                        NAVY
                ),

                styledLabel(
                        "Choose the types of notifications you want to receive.",
                        11,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        titleRow.getChildren().addAll(
                iconBox,
                titleBox
        );

        VBox options = new VBox(0);

        options.getChildren().addAll(

                notificationOption(
                        "▣",
                        "Event Reminders",
                        "Receive reminders for your upcoming events.",
                        true
                ),

                notificationOption(
                        "♧",
                        "Event Updates",
                        "Get notified about event changes and important updates.",
                        true
                ),

                notificationOption(
                        "✦",
                        "AI Planning Updates",
                        "Receive updates and suggestions from AI planning assistant.",
                        true
                ),

                notificationOption(
                        "✉",
                        "Email Notifications",
                        "Receive important alerts and updates via email.",
                        true
                ),

                notificationOption(
                        "♢",
                        "Security Alerts",
                        "Get notified about login activity and security updates.",
                        true
                ),

                notificationOption(
                        "◇",
                        "Marketing & Promotions",
                        "Receive offers, tips and promotional updates.",
                        false
                ),

                notificationOption(
                        "◉",
                        "Feedback & Surveys",
                        "Receive invitations for feedback and surveys.",
                        false
                )
        );

        Button save = new Button(
                "▣  Save Changes"
        );

        save.setPrefHeight(40);

        save.setMaxWidth(
                Double.MAX_VALUE
        );

        save.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        card.getChildren().addAll(
                titleRow,
                options,
                save
        );

        return card;
    }

    

    private HBox notificationOption(
            String iconText,
            String title,
            String description,
            boolean selected
    ) {

        HBox row = new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(12, 2, 12, 2)
        );

        row.setStyle(
                "-fx-border-color:#E8EDF3;" +
                "-fx-border-width:0 0 1 0;"
        );

        Circle circle = new Circle(
                21,
                Color.web("#EAF5FF")
        );

        Label icon = styledLabel(
                iconText,
                16,
                FontWeight.BOLD,
                "#1769D1"
        );

        StackPane iconBox = new StackPane(
                circle,
                icon
        );

        VBox textBox = new VBox(3);

        textBox.getChildren().addAll(
                styledLabel(
                        title,
                        13,
                        FontWeight.BOLD,
                        NAVY
                ),

                styledLabel(
                        description,
                        10,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        CheckBox checkBox = new CheckBox();

        checkBox.setSelected(
                selected
        );

        row.getChildren().addAll(
                iconBox,
                textBox,
                space,
                checkBox
        );

        return row;
    }


    private VBox recentNotifications() {

        VBox card = new VBox(8);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefWidth(560);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox = new VBox(3);

        titleBox.getChildren().addAll(
                styledLabel(
                        "Recent Notifications",
                        18,
                        FontWeight.BOLD,
                        NAVY
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Button viewAll = new Button(
                "View All"
        );

        viewAll.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#1769D1;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        header.getChildren().addAll(
                titleBox,
                space,
                viewAll
        );

        VBox list = new VBox(0);

        list.getChildren().addAll(

                recentNotification(
                        "▣",
                        "Event Reminder",
                        "Your Corporate Seminar is starting tomorrow.",
                        "10:30 AM",
                        true
                ),

                recentNotification(
                        "♧",
                        "Event Updated",
                        "Birthday Celebration event details have been updated.",
                        "Yesterday",
                        true
                ),

                recentNotification(
                        "✦",
                        "AI Suggestion",
                        "New vendor suggestions available for your upcoming event.",
                        "Yesterday",
                        true
                ),

                recentNotification(
                        "✉",
                        "Email Notification",
                        "Your invoice for Family Gathering has been sent.",
                        "2 Days Ago",
                        true
                ),

                recentNotification(
                        "♢",
                        "Security Alert",
                        "New login detected on Windows - Chrome.",
                        "3 Days Ago",
                        true
                ),

                recentNotification(
                        "◇",
                        "Special Offer",
                        "Get 20% off on premium decoration packages!",
                        "5 Days Ago",
                        false
                ),

                recentNotification(
                        "◉",
                        "Feedback Request",
                        "We value your feedback! Please take a moment to review.",
                        "6 Days Ago",
                        false
                )
        );

        card.getChildren().addAll(
                header,
                list
        );

        return card;
    }

    

    private HBox recentNotification(
            String iconText,
            String title,
            String message,
            String time,
            boolean unread
    ) {

        HBox row = new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(12, 2, 12, 2)
        );

        row.setMinHeight(68);

        row.setStyle(
                "-fx-border-color:#E8EDF3;" +
                "-fx-border-width:0 0 1 0;"
        );

        Circle circle = new Circle(
                21,
                Color.web("#EAF5FF")
        );

        Label icon = styledLabel(
                iconText,
                16,
                FontWeight.BOLD,
                "#1769D1"
        );

        StackPane iconBox = new StackPane(
                circle,
                icon
        );

        VBox text = new VBox(4);

        text.getChildren().addAll(
                styledLabel(
                        title,
                        13,
                        FontWeight.BOLD,
                        NAVY
                ),

                styledLabel(
                        message,
                        10,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        VBox right = new VBox(5);

        right.setAlignment(
                Pos.CENTER_RIGHT
        );

        right.getChildren().add(
                styledLabel(
                        time,
                        9,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        if (unread) {

            Circle dot = new Circle(
                    5,
                    Color.web("#1769D1")
            );

            right.getChildren().add(dot);
        }

        row.getChildren().addAll(
                iconBox,
                text,
                space,
                right
        );

        return row;
    }

   
    private HBox menuItem(
            String icon,
            String text,
            boolean active
    ) {

        HBox item = new HBox(11);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPrefHeight(40);

        item.setPadding(
                new Insets(0, 11, 0, 13)
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" +
                    "#1769D1;" +
                    "-fx-background-radius:7;"
            );
        }

        item.getChildren().addAll(

                styledLabel(
                        icon,
                        16,
                        FontWeight.NORMAL,
                        active
                                ? "#FFFFFF"
                                : "#D1D9E8"
                ),

                styledLabel(
                        text,
                        12,
                        active
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        active
                                ? "#FFFFFF"
                                : "#D1D9E8"
                )
        );

        return item;
    }

    

    private Label styledLabel(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label label = new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        weight,
                        size
                )
        );

        label.setTextFill(
                Color.web(color)
        );

        return label;
    }


    

    
}
