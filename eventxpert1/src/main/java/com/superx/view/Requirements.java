package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Requirements extends Application {

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String SELECTED = "#C7E5FA";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label(
                "EventXpert",
                22,
                FontWeight.BOLD,
                "white"
        );

        Label logoSub = label(
                "EVENT MANAGEMENT",
                12,
                FontWeight.NORMAL,
                "#BDE3FF"
        );

        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(
                new Insets(24, 20, 28, 25)
        );

        Label create = label(
                "Create Event",
                22,
                FontWeight.BOLD,
                "white"
        );

        Label step = label(
                "Step-by-step setup",
                14,
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
                sidebarItem("▣", "Requirements", true),
                sidebarItem("⌖", "Location", false),
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
        topBar.setAlignment(Pos.CENTER_RIGHT);
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
                20,
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
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        topBar.getChildren().addAll(
                topLogo,
                topSpace,
                bell,
                new Region(),
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

       

        Label heading = label(
                "What do you need for your event?",
                25,
                FontWeight.NORMAL,
                "#050B16"
        );

        Label description = label(
                "Select the core services and logistical requirements necessary for a successful\n" +
                "execution. You can refine these details later in the planning process.",
                15,
                FontWeight.NORMAL,
                TEXT
        );

        

        VBox servicesCard = new VBox(12);

        servicesCard.setPadding(
                new Insets(18)
        );

        servicesCard.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:7;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;"
        );

        Label servicesTitle = label(
                "Core Services",
                18,
                FontWeight.BOLD,
                TEXT
        );

        

        ToggleButton venue =
                serviceCard("▦", "Venue", true);

        ToggleButton catering =
                serviceCard("♜", "Catering", false);

        ToggleButton decoration =
                serviceCard("♨", "Decoration", false);

        ToggleButton photography =
                serviceCard("▣", "Photography", false);

        ToggleButton sound =
                serviceCard("▣", "Sound System", true);

        ToggleButton seating =
                serviceCard("▤", "Seating", false);

        ToggleButton other =
                serviceCard("•••", "Other", false);

        HBox row1 = new HBox(
                15,
                venue,
                catering,
                decoration,
                photography
        );

        HBox row2 = new HBox(
                15,
                sound,
                seating,
                other
        );

        for (ToggleButton b : new ToggleButton[]{
                venue, catering, decoration,
                photography, sound, seating, other
        }) {
            HBox.setHgrow(b, Priority.ALWAYS);
        }

        servicesCard.getChildren().addAll(
                servicesTitle,
                row1,
                row2
        );

        

        VBox additionalCard = new VBox(8);

        additionalCard.setPadding(
                new Insets(18)
        );

        additionalCard.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:7;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;"
        );

        Label additionalTitle = label(
                "Additional Requirements",
                18,
                FontWeight.BOLD,
                TEXT
        );

        Label additionalDescription = label(
                "Specify any unique logistical needs, accessibility requirements, or custom requests.",
                14,
                FontWeight.NORMAL,
                BLUE
        );

        Label customLabel = label(
                "Custom Notes",
                12,
                FontWeight.NORMAL,
                TEXT
        );

        TextArea notes = new TextArea();

        notes.setPromptText(
                "Tell us anything else you need for your event..."
        );

        notes.setPrefRowCount(10);
        notes.setWrapText(true);

        notes.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:11px;" +
                "-fx-padding:10;"
        );

        additionalCard.getChildren().addAll(
                additionalTitle,
                additionalDescription,
                customLabel,
                notes
        );

        content.getChildren().addAll(
                
                heading,
                description,
                servicesCard,
                additionalCard
        );

        

        HBox footer = new HBox(15);

        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);

        footer.setPadding(
                new Insets(0, 30, 0, 30)
        );

        footer.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E4E7EC;" +
                "-fx-border-width:1 0 0 0;"
        );

        Label copyright = label(
                "© 2026 EventXpert ",
                12,
                FontWeight.NORMAL,
                GREY
        );

        Region footerSpace = new Region();
        HBox.setHgrow(
                footerSpace,
                Priority.ALWAYS
        );

        Button save = new Button("Save Draft");
        Button back = new Button("Back");
        Button next = new Button("Next");

        save.setPrefSize(110, 38);
        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        save.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-border-color:" + BLUE + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
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

        save.setOnAction(e -> showMessage(
                "Draft Saved",
                "Your requirements have been saved."
        ));

        back.setOnAction(e -> showMessage(
                "Back",
                "Returning to Event Details."
        ));

        next.setOnAction(e -> showMessage(
                "Next Step",
                "Moving to Location."
        ));

        footer.getChildren().addAll(
                copyright,
                footerSpace,
                save,
                back,
                next
        );

        

        VBox rightSide = new VBox();

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

        

        HBox fullPage = new HBox();

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

        Scene scene = new Scene(
                root,
                1365,
                850
        );

        stage.setTitle(
                "EventXpert - Requirements"
        );

        stage.setScene(scene);

        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        stage.show();
    }

    
    private HBox sidebarItem(
            String icon,
            String text,
            boolean active
    ) {

        HBox item = new HBox(12);

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
                        12,
                        FontWeight.BOLD,
                        active
                                ? "white"
                                : "#B7C4D8"
                )
        );

        return item;
    }

    

    private ToggleButton serviceCard(
            String icon,
            String title,
            boolean selected
    ) {

        ToggleButton button =
                new ToggleButton();

        button.setSelected(selected);

        button.setPrefHeight(92);
        button.setMinHeight(92);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox box = new VBox(8);

        box.setAlignment(
                Pos.CENTER
        );

        Label iconLabel = label(
                icon,
                21,
                FontWeight.BOLD,
                NAVY
        );

        Label titleLabel = label(
                title,
                14,
                FontWeight.NORMAL,
                "#050B16"
        );

        Label check = label(
                "●",
                12,
                FontWeight.BOLD,
                NAVY
        );

        StackPane iconPane = new StackPane(
                iconLabel
        );

        iconPane.setPrefHeight(28);

        StackPane.setAlignment(
                check,
                Pos.TOP_RIGHT
        );

        check.setVisible(selected);

        iconPane.getChildren().add(check);

        box.getChildren().addAll(
                iconPane,
                titleLabel
        );

        button.setGraphic(box);

        updateServiceStyle(
                button,
                selected
        );

        button.selectedProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            updateServiceStyle(
                                    button,
                                    newValue
                            );

                            check.setVisible(
                                    newValue
                            );
                        }
                );

        return button;
    }

    
    private void updateServiceStyle(
            ToggleButton button,
            boolean selected
    ) {

        button.setStyle(
                selected
                        ? "-fx-background-color:" + SELECTED + ";" +
                          "-fx-border-color:" + BLUE + ";" +
                          "-fx-border-width:1.5;" +
                          "-fx-border-radius:6;" +
                          "-fx-background-radius:6;"
                        : "-fx-background-color:white;" +
                          "-fx-border-color:" + BORDER + ";" +
                          "-fx-border-width:1;" +
                          "-fx-border-radius:6;" +
                          "-fx-background-radius:6;"
        );
    }

    

    private Label label(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label l = new Label(text);

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

    
    public static void main(
            String[] args
    ) {

        launch(args);
    }
}
