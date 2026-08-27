package com.techtrio.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LocationC {

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String LIGHT_BLUE = "#C7E5FA";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    public Scene getLocationScene(
        Runnable backAction,
        Runnable nextAction
) {

    

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        Label logo = label(
                "EventXpert",
                21,
                FontWeight.BOLD,
                "white"
        );

        Label logoSub = label(
                "EVENT MANAGEMENT",
                8,
                FontWeight.NORMAL,
                "#BDE3FF"
        );

        VBox brand = new VBox(3, logo, logoSub);

        brand.setPadding(
                new Insets(24, 20, 28, 25)
        );

        Label create = label(
                "Create Event",
                19,
                FontWeight.BOLD,
                "white"
        );

        Label step = label(
                "Step-by-step setup",
                10,
                FontWeight.NORMAL,
                "#BDE3FF"
        );

        VBox title = new VBox(2, create, step);

        title.setPadding(
                new Insets(0, 20, 20, 25)
        );

        VBox menu = new VBox();

        menu.getChildren().addAll(
                sidebarItem("ⓘ", "Event Details", false),
                sidebarItem("▣", "Requirements", false),
                sidebarItem("⌖", "Location", true),
                sidebarItem("▣", "Budget", false),
                sidebarItem("✦", "AI Planning", false),
                sidebarItem("▤", "Review", false)
        );

        sidebar.getChildren().addAll(
                brand,
                title,
                menu
        );

        
        HBox topBar = new HBox();

        topBar.setPrefHeight(60);
        topBar.setMinHeight(60);

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(0, 30, 0, 30)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E4E7EC;" +
                "-fx-border-width:0 0 1 0;"
        );

        Label topLogo = label(
                "EventXpert",
                14,
                FontWeight.BOLD,
                NAVY
        );

        Label bell = label(
                "♧",
                20,
                FontWeight.NORMAL,
                NAVY
        );

        Label help = label(
                "?",
                13,
                FontWeight.BOLD,
                NAVY
        );

        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);

        help.setStyle(
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:50;" +
                "-fx-background-radius:50;"
        );

        Label avatar = label(
                "●",
                22,
                FontWeight.BOLD,
                "#555555"
        );

        Region topSpace = new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        topBar.getChildren().addAll(
                topLogo,
                topSpace,
                bell,
                help,
                avatar
        );

       

        VBox content = new VBox(12);

        content.setPadding(
                new Insets(25, 32, 18, 32)
        );

        content.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        Label stepText = label(
                "STEP 3 OF 6",
                9,
                FontWeight.BOLD,
                BLUE
        );

        Label heading = label(
                "Where is your event happening?",
                25,
                FontWeight.NORMAL,
                "#050B16"
        );

        Label description = label(
                "Provide location details to help us find the best logistical routes and venues.",
                12,
                FontWeight.NORMAL,
                TEXT
        );

        

        VBox locationCard = new VBox(11);

        locationCard.setPadding(
                new Insets(18)
        );

        locationCard.setPrefWidth(360);

        locationCard.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:7;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;"
        );

        TextField city = new TextField();

        city.setPromptText(
                "e.g. San Francisco"
        );

        styleField(city);

        TextField locality = new TextField();

        locality.setPromptText(
                "e.g. Downtown, SOMA"
        );

        styleField(locality);

        ComboBox<String> venueType =
                new ComboBox<>();

        venueType.getItems().addAll(
                "Hotel",
                "Banquet Hall",
                "Conference Center",
                "Outdoor Venue",
                "Restaurant",
                "Resort",
                "Other"
        );

        venueType.setPromptText(
                "Select venue type"
        );

        venueType.setMaxWidth(
                Double.MAX_VALUE
        );

        styleCombo(venueType);

        locationCard.getChildren().addAll(
                formField("City", city),
                formField("Area / Locality", locality),
                formField("Venue Preference", venueType)
        );

        Separator separator = new Separator();

        
        Button currentLocation =
                new Button("◎   Use my current location");

        currentLocation.setAlignment(
                Pos.CENTER_LEFT
        );

        currentLocation.setMaxWidth(
                Double.MAX_VALUE
        );

        currentLocation.setPrefHeight(34);

        currentLocation.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-alignment:center-left;" +
                "-fx-cursor:hand;"
        );

        currentLocation.setOnAction(e -> {

            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setTitle("Location");
            alert.setHeaderText(
                    "Current Location"
            );

            alert.setContentText(
                    "Current location will be connected using API / Google Maps."
            );

            alert.showAndWait();
        });

        locationCard.getChildren().addAll(
                separator,
                currentLocation
        );

        

        HBox helpBox = new HBox(12);

        helpBox.setPrefHeight(62);

        helpBox.setAlignment(
                Pos.CENTER_LEFT
        );

        helpBox.setPadding(
                new Insets(10, 14, 10, 14)
        );

        helpBox.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;"
        );

        Label helpIcon = label(
                "⌕",
                21,
                FontWeight.BOLD,
                BLUE
        );

        StackPane iconCircle =
                new StackPane(helpIcon);

        iconCircle.setPrefSize(34, 34);

        iconCircle.setStyle(
                "-fx-background-color:" + LIGHT_BLUE + ";" +
                "-fx-background-radius:50;"
        );

        Label helpTitle = label(
                "Need help finding a venue?",
                13,
                FontWeight.BOLD,
                TEXT
        );

        Label helpSub = label(
                "Connect with our Venue Finder.",
                10,
                FontWeight.NORMAL,
                GREY
        );

        VBox helpText =
                new VBox(1, helpTitle, helpSub);

        Region helpSpace =
                new Region();

        HBox.setHgrow(
                helpSpace,
                Priority.ALWAYS
        );

        Label arrow = label(
                "→",
                22,
                FontWeight.NORMAL,
                GREY
        );

        helpBox.getChildren().addAll(
                iconCircle,
                helpText,
                helpSpace,
                arrow
        );

       
        Image mapImage = new Image("assets\\images\\map img.jpg");

        ImageView mapView = new ImageView(mapImage);

        mapView.setFitWidth(440);
        mapView.setFitHeight(335);

        mapView.setPreserveRatio(false);
        mapView.setSmooth(true);

        StackPane mapPanel = new StackPane(
                mapView
        );

        mapPanel.setPrefSize(
                520,
                360
        );

        mapPanel.setMinSize(
                520,
                360
        );

        mapPanel.setStyle(
                "-fx-background-color:#DCE2E8;" +
                "-fx-background-radius:6;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;"
        );

        
        VBox leftColumn = new VBox(18);

        leftColumn.setPrefWidth(360);
        leftColumn.setMaxWidth(Double.MAX_VALUE);

        leftColumn.setPrefWidth(370);

        leftColumn.getChildren().addAll(
                locationCard,
                helpBox
        );

        HBox mainRow =
                new HBox(45);

        mainRow.setAlignment(
                Pos.TOP_LEFT
        );

        mainRow.getChildren().addAll(
                leftColumn,
                mapPanel
        );

        content.getChildren().addAll(
                stepText,
                heading,
                description,
                mainRow
        );

        

        HBox footer = new HBox(15);

        footer.setPrefHeight(60);
        footer.setMinHeight(60);

        footer.setAlignment(
                Pos.CENTER_RIGHT
        );

        footer.setPadding(
                new Insets(0, 30, 0, 30)
        );

        footer.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E4E7EC;" +
                "-fx-border-width:1 0 0 0;"
        );

        Label copyright = label(
                "© 2024 EventXpert Logistics",
                10,
                FontWeight.NORMAL,
                GREY
        );

        Region footerSpace =
                new Region();

        HBox.setHgrow(
                footerSpace,
                Priority.ALWAYS
        );

        Button save =
                new Button("Save Draft");

        Button back =
                new Button("Back");

        Button next =
                new Button("Next");

        save.setPrefSize(100, 38);
        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        save.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        back.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-border-color:" + BLUE + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        next.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        save.setOnAction(e ->
                showMessage(
                        "Draft Saved",
                        "Your location details have been saved."
                )
        );

        back.setOnAction(e -> {

            if (backAction != null) {
                backAction.run();
            }

        });

        // LOCATION → BUDGET
        next.setOnAction(e -> {

            BudgetC budgetPage = new BudgetC();

            LandingPage.landingPageStage.setScene(
                    budgetPage.getBudgetScene(
                            backAction,
                            nextAction
                    )
            );

        });

        footer.getChildren().addAll(
                copyright,
                footerSpace,
                save,
                back,
                next
        );

        

        VBox rightSide =
                new VBox();

        rightSide.setFillWidth(true);

        rightSide.getChildren().addAll(
                topBar,
                content,
                footer
        );

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        

        HBox fullPage =
                new HBox();

        fullPage.setFillHeight(true);

        fullPage.getChildren().addAll(
                sidebar,
                rightSide
        );

        HBox.setHgrow(
                rightSide,
                Priority.ALWAYS
        );

        root.setCenter(fullPage);

      

        Scene scene =
                new Scene(
                        root,
                        1365,
                        850
                );

        return scene;
    }

    

    private HBox sidebarItem(
            String icon,
            String text,
            boolean active
    ) {

        HBox item =
                new HBox(12);

        item.setPrefHeight(44);
        item.setMinHeight(44);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(0, 18, 0, 25)
        );

        item.setStyle(
                active
                        ? "-fx-background-color:" + BLUE + ";" +
                          "-fx-border-color:#9DD8FF;" +
                          "-fx-border-width:0 0 0 3;"
                        : "-fx-background-color:transparent;"
        );

        item.getChildren().addAll(
                label(
                        icon,
                        16,
                        FontWeight.NORMAL,
                        "white"
                ),
                label(
                        text,
                        11,
                        FontWeight.BOLD,
                        active
                                ? "white"
                                : "#B7C4D8"
                )
        );

        return item;
    }

    

    private VBox formField(
            String name,
            Control control
    ) {

        VBox box =
                new VBox(5);

        box.getChildren().addAll(
                label(
                        name,
                        10,
                        FontWeight.NORMAL,
                        TEXT
                ),
                control
        );

        return box;
    }

   

    private void styleField(
            TextField field
    ) {

        field.setPrefHeight(31);

        field.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-padding:0 10;" +
                "-fx-font-size:11px;"
        );
    }

    

    private void styleCombo(
            ComboBox<String> combo
    ) {

        combo.setPrefHeight(31);

        combo.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:11px;"
        );
    }

    
    private Label label(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label l =
                new Label(text);

        l.setFont(
                Font.font(
                        "Arial",
                        weight,
                        size
                )
        );

        l.setTextFill(
                Color.web(color)
        );

        return l;
    }

    

    private void showMessage(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("EventXpert");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    

     
}