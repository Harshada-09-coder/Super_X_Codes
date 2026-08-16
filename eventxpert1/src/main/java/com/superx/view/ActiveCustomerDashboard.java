package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ActiveCustomerDashboard  {

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String TEXT = "#17233C";
    private static final String BORDER = "#D7DEE8";
    private static final String LIGHT_BLUE = "#BFE5FF";

    public Scene getActiveCustomerDashboardScene() {

        
        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

       

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

                menuItem(
                        "▦",
                        "Dashboard",
                        true
                ),

                menuItem(
                        "⊕",
                        "Create Event",
                        false
                ),

                menuItem(
                        "▣",
                        "My Events",
                        false
                ),

                menuItem(
                        "□",
                        "Calendar",
                        false
                ),

                menuItem(
                        "♧",
                        "Notifications",
                        false
                )

               
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

                menuItem(
                        "◉",
                        "Profile",
                        false
                ),

                menuItem(
                        "⚙",
                        "Settings",
                        false
                ),
                menuItem(
                        "?",
                        "Support",
                        false
                )
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

        

        TextField search = new TextField();

        search.setPromptText(
                "Search"
        );

        search.setPrefWidth(330);
        search.setMinWidth(270);
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

        Label notification = styledLabel(
                "♧",
                19,
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
                search,
                topSpace,
                notification,
                avatar
                
        );

        

        VBox main = new VBox(12);

        main.setPadding(
                new Insets(15, 20, 15, 20)
        );

        main.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        

        HBox welcome = new HBox();

        welcome.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox welcomeText = new VBox(3);

        welcomeText.getChildren().addAll(

                styledLabel(
                        "Welcome back!",
                        23,
                        FontWeight.BOLD,
                        NAVY
                ),

                styledLabel(
                        "Here's a look at your previous events.",
                        11,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        Region welcomeSpace = new Region();

        HBox.setHgrow(
                welcomeSpace,
                Priority.ALWAYS
        );

        Button createEvent = new Button(
                "+  Create New Event"
        );

        createEvent.setPrefSize(
                175,
                36
        );

        createEvent.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        welcome.getChildren().addAll(
                welcomeText,
                welcomeSpace,
                createEvent
        );

       
        HBox stats = new HBox(12);

        stats.setPrefHeight(70);

        stats.getChildren().addAll(

                statCard(
                        "✓",
                        "Completed Events",
                        "3"
                ),

                statCard(
                        "▣",
                        "Total Events Created",
                        "3"
                ),

                statCard(
                        "→",
                        "Upcoming Events",
                        "0"
                )
        );

        
        HBox lower = new HBox(14);

        VBox.setVgrow(
                lower,
                Priority.ALWAYS
        );

        

        VBox previous = new VBox(12);

        previous.setPadding(
                new Insets(16)
        );

        previous.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        VBox.setVgrow(
                previous,
                Priority.ALWAYS
        );

        HBox previousHeader = new HBox();

        previousHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox previousTitle = new VBox(4);

        previousTitle.getChildren().addAll(

                styledLabel(
                        "Previous Events",
                        23,
                        FontWeight.BOLD,
                        TEXT
                ),

                styledLabel(
                        "Your completed events and past event history.",
                        14,
                        FontWeight.NORMAL,
                        "#667085"
                )
        );

        Region previousSpace = new Region();

        HBox.setHgrow(
                previousSpace,
                Priority.ALWAYS
        );

        Button allButton = smallButton(
                "All",
                "#F3F6FA"
        );

        Button completedButton = smallButton(
                "Completed",
                "#EAF6FF"
        );

        previousHeader.getChildren().addAll(
                previousTitle,
                previousSpace,
                allButton,
                completedButton
        );

        VBox eventList = new VBox(12);

        eventList.getChildren().addAll(

                eventCard(
                        "Birthday Celebration",
                        "15 July 2026",
                        "Sunshine Banquet Hall"
                ),

                eventCard(
                        "Corporate Seminar",
                        "28 June 2026",
                        "Grand Conference Hall"
                ),

                eventCard(
                        "Family Gathering",
                        "10 June 2026",
                        "Green Valley Resort"
                )
        );

        previous.getChildren().addAll(
                previousHeader,
                eventList
        );

        HBox.setHgrow(
                previous,
                Priority.ALWAYS
        );

       
        VBox rightColumn = new VBox(12);

        rightColumn.setPrefWidth(330);
        rightColumn.setMinWidth(310);
        rightColumn.setMaxWidth(340);

        rightColumn.setFillWidth(true);

        VBox aiPlanning = aiPlanningCard();

        VBox calendar = calendarCard();

        VBox weather = weatherCard();

        rightColumn.getChildren().addAll(
                aiPlanning,
                calendar,
                weather
        );

        

        lower.getChildren().addAll(
                previous,
                rightColumn
        );

       

        main.getChildren().addAll(
                welcome,
                stats,
                lower
        );

        

        rightArea.setTop(
                topBar
        );

        rightArea.setCenter(
                main
        );

        root.setLeft(
                sidebar
        );

        root.setCenter(
                rightArea
        );

        

        Scene scene = new Scene(
                root,
                1365,
                800
        );

        return scene;
    }

   
    

    private VBox aiPlanningCard() {

        VBox card = new VBox(10);

        card.setPrefHeight(185);
        card.setMinHeight(185);
        card.setMaxHeight(185);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPadding(
                new Insets(16)
        );

        card.setStyle(
                "-fx-background-color:#0D3979;" +
                "-fx-background-radius:12;"
        );

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox = new VBox(3);

        titleBox.getChildren().addAll(

                styledLabel(
                        "AI Planning",
                        20,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Smart event planning",
                        14,
                        FontWeight.NORMAL,
                        "#D9ECFF"
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label aiIcon = styledLabel(
                "✦",
                27,
                FontWeight.BOLD,
                "#FFFFFF"
        );

        header.getChildren().addAll(
                titleBox,
                space,
                aiIcon
        );

        Label description = styledLabel(
                "Let AI help you plan your perfect event.",
                14,
                FontWeight.NORMAL,
                "#E3F2FF"
        );

        description.setWrapText(
                true
        );

        Button startPlanning = new Button(
                "Start Planning  →"
        );

        startPlanning.setPrefHeight(
                32
        );

        startPlanning.setMaxWidth(
                Double.MAX_VALUE
        );

        startPlanning.setStyle(
                "-fx-background-color:#FFFFFF;" +
                "-fx-text-fill:#0D3979;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );

        Region buttonSpace = new Region();

VBox.setVgrow(
        buttonSpace,
        Priority.ALWAYS
);

        card.getChildren().addAll(
        header,
        description,
        buttonSpace,
        startPlanning
);
        return card;
    }

   

    private VBox calendarCard() {

        VBox card = new VBox(8);

        card.setPrefHeight(205);
        card.setMinHeight(205);
        card.setMaxHeight(205);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPadding(
                new Insets(15)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D7DEE8;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        header.getChildren().addAll(

                styledLabel(
                        "Event Calendar",
                        16,
                        FontWeight.BOLD,
                        TEXT
                ),

                space,

                styledLabel(
                        "July 2026",
                        10,
                        FontWeight.NORMAL,
                        "#667085"
                )
        );

        GridPane grid = new GridPane();

        grid.setHgap(5);
        grid.setVgap(4);

        grid.setAlignment(
                Pos.CENTER
        );

        String[] days = {"S","M","T","W","T","F","S" };

        for (int i = 0; i < 7; i++) {

            Label day = styledLabel(
                    days[i],
                    9,
                    FontWeight.BOLD,
                    "#667085"
            );

            day.setPrefWidth(34);

            day.setAlignment(
                    Pos.CENTER
            );

            grid.add(
                    day,
                    i,
                    0
            );
        }

        int number = 1;

        for (int row = 1; row <= 5; row++) {

            for (int col = 0; col < 7; col++) {

                if (
                        row == 1 &&
                        col < 3
                ) {
                    continue;
                }

                if (number > 31) {
                    break;
                }

                Label numberLabel = styledLabel(
                        String.valueOf(number),
                        9,
                        FontWeight.NORMAL,
                        TEXT
                );

                StackPane cell = new StackPane(
                        numberLabel
                );

                cell.setPrefSize(
                        34,
                        23
                );

                if (
                        number == 10 ||
                        number == 15 ||
                        number == 28
                ) {

                    cell.setStyle(
                            "-fx-background-color:#BFE5FF;" +
                            "-fx-background-radius:5;"
                    );
                }

                grid.add(
                        cell,
                        col,
                        row
                );

                number++;
            }
        }

        Label note = styledLabel(
                "●  Highlighted dates are event dates",
                12,
                FontWeight.NORMAL,
                "#667085"
        );

        card.getChildren().addAll(
                header,
                grid,
                note
        );

        return card;
    }

        private VBox weatherCard() {

        VBox card = new VBox(9);

        card.setPrefHeight(185);
        card.setMinHeight(185);
        card.setMaxHeight(185);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPadding(
                new Insets(15)
        );

        card.setStyle(
                "-fx-background-color:linear-gradient(" +
                "to bottom right,#0D3979,#2670B8);" +
                "-fx-background-radius:12;"
        );



        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox title = new VBox(2);

        title.getChildren().addAll(

                styledLabel(
                        "Weather Forecast",
                        17,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Pune, Maharashtra",
                        11,
                        FontWeight.NORMAL,
                        "#D9ECFF"
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label today = styledLabel(
                "TODAY",
                11,
                FontWeight.BOLD,
                "#D9ECFF"
        );

        header.getChildren().addAll(
                title,
                space,
                today
        );

        

        HBox weather = new HBox(14);

        weather.setAlignment(
                Pos.CENTER_LEFT
        );

        Label sun = styledLabel(
                "☀",
                42,
                FontWeight.NORMAL,
                "#FFFFFF"
        );

        VBox temperature = new VBox(2);

        temperature.getChildren().addAll(

                styledLabel(
                        "28°C",
                        29,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Partly Cloudy",
                        12,
                        FontWeight.NORMAL,
                        "#D9ECFF"
                )
        );

        weather.getChildren().addAll(
                sun,
                temperature
        );

        

        HBox details = new HBox(20);

        details.getChildren().addAll(

                weatherDetail(
                        "Humidity",
                        "64%"
                ),

                weatherDetail(
                        "Wind",
                        "12 km/h"
                ),

                weatherDetail(
                        "Visibility",
                        "8 km"
                )
        );

        

        HBox message = new HBox(7);

        message.setAlignment(
                Pos.CENTER_LEFT
        );

        message.setPadding(
                new Insets(7, 9, 7, 9)
        );

        message.setStyle(
                "-fx-background-color:rgba(255,255,255,0.13);" +
                "-fx-background-radius:7;"
        );

        message.getChildren().addAll(

                styledLabel(
                        "✓",
                        11,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        "Good weather for outdoor events",
                        11,
                        FontWeight.NORMAL,
                        "#E5F2FF"
                )
        );

        card.getChildren().addAll(
                header,
                weather,
                details,
                message
        );

        return card;
    }

    

    private VBox weatherDetail(
            String title,
            String value
    ) {

        VBox box = new VBox(2);

        box.getChildren().addAll(

                styledLabel(
                        value,
                        9,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),

                styledLabel(
                        title,
                        8,
                        FontWeight.NORMAL,
                        "#C8E3FF"
                )
        );

        return box;
    }

    private HBox eventCard(
            String name,
            String date,
            String location
    ) {

        HBox card = new HBox(14);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPrefHeight(
                112
        );

        card.setMinHeight(
                112
        );

        card.setPadding(
                new Insets(14)
        );

        card.setStyle(
                "-fx-background-color:#F9FBFD;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        Circle circle = new Circle(
                27,
                Color.web("#E1F3FF")
        );

        StackPane icon = new StackPane(
                circle,
                styledLabel(
                        "✦",
                        18,
                        FontWeight.BOLD,
                        "#1D5274"
                )
        );

        VBox details = new VBox(5);

        details.getChildren().addAll(

                styledLabel(
                        name,
                        17,
                        FontWeight.BOLD,
                        TEXT
                ),

                styledLabel(
                        "▣  " + date,
                        12,
                        FontWeight.NORMAL,
                        "#596273"
                ),

                styledLabel(
                        "⌖  " + location,
                        12,
                        FontWeight.NORMAL,
                        "#596273"
                )
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label completed = styledLabel(
                "● Completed",
                14,
                FontWeight.BOLD,
                "#536174"
        );

        card.getChildren().addAll(
                icon,
                details,
                space,
                completed
        );

        return card;
    }

    
    private VBox statCard(
            String icon,
            String title,
            String value
    ) {

        VBox card = new VBox();

        card.setPrefHeight(
                70
        );

        card.setPadding(
                new Insets(9)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        HBox row = new HBox(9);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle circle = new Circle(
                17,
                Color.web("#E0F2FF")
        );

        StackPane iconBox = new StackPane(
                circle,
                styledLabel(
                        icon,
                        13,
                        FontWeight.BOLD,
                        NAVY
                )
        );

        VBox text = new VBox(1);

        text.getChildren().addAll(

                styledLabel(
                        title,
                        20,
                        FontWeight.NORMAL,
                        "#667085"
                ),

                styledLabel(
                        value,
                        20,
                        FontWeight.BOLD,
                        NAVY
                )
        );

        row.getChildren().addAll(
                iconBox,
                text
        );

        card.getChildren().add(
                row
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        return card;
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

        item.setPrefHeight(
                40
        );

        item.setPadding(
                new Insets(0, 11, 0, 13)
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" +
                    LIGHT_BLUE + ";" +
                    "-fx-background-radius:7;"
            );
        }

        item.getChildren().addAll(

                styledLabel(
                        icon,
                        16,
                        FontWeight.NORMAL,
                        active
                                ? NAVY
                                : "#D1D9E8"
                ),

                styledLabel(
                        text,
                        12,
                        active
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        active
                                ? "#35556E"
                                : "#D1D9E8"
                )
        );

        return item;
    }

    
    private Button smallButton(
            String text,
            String background
    ) {

        Button button = new Button(
                text
        );

        button.setPrefHeight(
                27
        );

        button.setStyle(
                "-fx-background-color:" +
                background + ";" +
                "-fx-border-color:#D5DDE7;" +
                "-fx-border-radius:5;" +
                "-fx-background-radius:5;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:9px;"
        );

        return button;
    }
    private Label styledLabel(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label label = new Label(
                text
        );

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