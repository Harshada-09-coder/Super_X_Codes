package com.superx.view;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class AiPlanning extends Application {

    private final String NAVY = "#002A60";
    private final String BLUE = "#0057B8";
    private final String BG = "#F5F8FA";
    private final String BORDER = "#D5DEE7";
    private final String TEXT = "#102A43";
    private final String GREY = "#667085";
    private final String LIGHT = "#C2F0F4";

    @Override
    public void start(Stage stage) {



        VBox sidebar = new VBox(7);

        sidebar.setPrefWidth(205);
        sidebar.setMinWidth(205);
        sidebar.setMaxWidth(205);

        sidebar.setPadding(
                new Insets(24, 8, 20, 8)
        );

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        Label logo = label(
                "EventXpert",
                21,
                true,
                Color.WHITE
        );

        VBox eventTitle = new VBox(
                2,
                label(
                        "Create Event",
                        16,
                        true,
                        Color.WHITE
                ),
                label(
                        "Step-by-step setup",
                        10,
                        false,
                        Color.web("#D1DFEB")
                )
        );

        eventTitle.setPadding(
                new Insets(7, 12, 15, 12)
        );

        VBox navigation = new VBox(2);

        navigation.getChildren().addAll(
                navItem("ⓘ", "Event Details", false),
                navItem("▤", "Requirements", false),
                navItem("⌖", "Location", false),
                navItem("▣", "Budget", false),
                navItem("✦", "AI Planning", true),
                navItem("▤", "Review", false)
        );

        Region sideSpace = new Region();

        VBox.setVgrow(
                sideSpace,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                logo,
                eventTitle,
                navigation,
                sideSpace
        );

       

        HBox topBar = new HBox(24);

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(12, 28, 12, 28)
        );

        topBar.setPrefHeight(58);

        topBar.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:#DCE5EC;" +
                "-fx-border-width:0 0 1 0;"
        );

        topBar.getChildren().addAll(
                label("♧", 19, false, Color.web(NAVY)),
                label("?", 16, true, Color.web(NAVY)),
                label("●", 21, false, Color.web(NAVY))
        );

       

        VBox heading = new VBox(7);

        heading.setAlignment(
                Pos.CENTER
        );

        heading.setPadding(
                new Insets(38, 20, 20, 20)
        );

        Label aiIcon = label(
                "✦",
                22,
                true,
                Color.web(NAVY)
        );

        aiIcon.setPrefSize(42, 42);
        aiIcon.setAlignment(Pos.CENTER);

        aiIcon.setStyle(
                "-fx-background-color:#C5E9FF;" +
                "-fx-background-radius:50;"
        );

        Label title = label(
                "Plan Smarter with AI",
                27,
                true,
                Color.web("#111111")
        );

        Label subtitle = label(
                "Let our intelligent assistant optimize your timeline, predict potential bottlenecks, and\n" +
                "generate comprehensive checklists based on your event parameters.",
                13,
                false,
                Color.web(GREY)
        );

        subtitle.setTextAlignment(
                TextAlignment.CENTER
        );

        heading.getChildren().addAll(
                aiIcon,
                title,
                subtitle
        );

       
        VBox plannerCard = new VBox(14);

        plannerCard.setMaxWidth(680);
        plannerCard.setPadding(
                new Insets(20)
        );

        plannerCard.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

       
        HBox plannerHeader = new HBox();

        plannerHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox plannerText = new VBox(
                3,
                label(
                        "Enable AI Event ",
                        15,
                        true,
                        Color.web("#222222")
                ),
                label(
                        "Unlock automated insights and structural planning.",
                        10,
                        false,
                        Color.web(GREY)
                )
        );

        Region headerSpace = new Region();

        HBox.setHgrow(
                headerSpace,
                Priority.ALWAYS
        );

        

        ToggleButton aiToggle =
                new ToggleButton("✓");

        aiToggle.setSelected(true);

        aiToggle.setPrefSize(
                45,
                25
        );

        aiToggle.setTextFill(
                Color.WHITE
        );

        aiToggle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        aiToggle.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:20;"
        );

        aiToggle.selectedProperty().addListener(
                (obs, oldValue, newValue) -> {

                    aiToggle.setText(
                            newValue ? "✓" : "○"
                    );

                    aiToggle.setStyle(
                            "-fx-background-color:" +
                            (newValue ? NAVY : "#9AA5B1") +
                            ";" +
                            "-fx-background-radius:20;"
                    );
                }
        );

        plannerHeader.getChildren().addAll(
                plannerText,
                headerSpace,
                aiToggle
        );

        Separator separator = new Separator();

        

        GridPane features = new GridPane();

        features.setHgap(35);
        features.setVgap(20);

        features.add(
                feature(
                        "▣",
                        "Smart Checklists",
                        "Auto-generated tasks tailored to your specific event\n" +
                        "type and scope."
                ),
                0, 0
        );

        features.add(
                feature(
                        "⌁",
                        "Dynamic Timeline",
                        "Optimized scheduling that automatically adjusts to\n" +
                        "constraint changes."
                ),
                1, 0
        );

        features.add(
                feature(
                        "♙",
                        "Proactive Suggestions",
                        "Intelligent recommendations for vendors, layouts, and\n" +
                        "logistics."
                ),
                0, 1
        );

        features.add(
                feature(
                        "≡",
                        "Risk Priorities",
                        "Highlights critical path items and potential planning\n" +
                        "bottlenecks instantly."
                ),
                1, 1
        );

        ColumnConstraints c1 =
                new ColumnConstraints();

        c1.setPercentWidth(50);

        ColumnConstraints c2 =
                new ColumnConstraints();

        c2.setPercentWidth(50);

        features.getColumnConstraints().addAll(
                c1, c2
        );

        plannerCard.getChildren().addAll(
                plannerHeader,
                separator,
                features
        );

        
        Button enableAI =
        new Button("◉  Enable AI Planning");

enableAI.setPrefSize(170, 42);


enableAI.setStyle(
        "-fx-background-color:white;" +
        "-fx-border-color:#9EB1C1;" +
        "-fx-border-radius:7;" +
        "-fx-background-radius:7;" +
        "-fx-text-fill:" + NAVY + ";" +
        "-fx-font-weight:bold;"
);


enableAI.setOnAction(e -> {

    enableAI.setStyle(
            "-fx-background-color:" + NAVY + ";" +
            "-fx-background-radius:7;" +
            "-fx-text-fill:white;" +
            "-fx-font-weight:bold;"
    );

    enableAI.setText("✓  AI Planning Enabled");
});

        Button skip =
                new Button("Skip for Now");

        skip.setPrefSize(
                125,
                42
        );

        skip.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#9EB1C1;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-text-fill:" + NAVY + ";"
        );

        HBox actions =
                new HBox(
                        10,
                        enableAI,
                        skip
                );

        actions.setAlignment(
                Pos.CENTER
        );

        actions.setPadding(
                new Insets(5, 0, 15, 0)
        );

        VBox mainContent =
                new VBox(
                        heading,
                        plannerCard,
                        actions
                );

        mainContent.setAlignment(
                Pos.TOP_CENTER
        );

        VBox.setVgrow(
                mainContent,
                Priority.ALWAYS
        );


        Button back =
                footerButton(
                        "← Back",
                        100,
                        false
                );

        Button save =
                footerButton(
                        "Save Draft",
                        110,
                        false
                );

        Button next =
                footerButton(
                        "Next",
                        90,
                        true
                );

        HBox leftButtons =
                new HBox(
                        10,
                        back,
                        save
                );

        leftButtons.setAlignment(
                Pos.CENTER_LEFT
        );

        Region footerSpace =
                new Region();

        HBox.setHgrow(
                footerSpace,
                Priority.ALWAYS
        );

        HBox footer =
                new HBox(
                        leftButtons,
                        footerSpace,
                        next
                );

        footer.setAlignment(
                Pos.CENTER
        );

        footer.setPadding(
                new Insets(10, 30, 12, 30)
        );

        footer.setPrefHeight(68);

        footer.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:#DCE5EC;" +
                "-fx-border-width:1 0 0 0;"
        );

        

        VBox center =
                new VBox(
                        topBar,
                        mainContent,
                        footer
                );

        center.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        VBox.setVgrow(
                mainContent,
                Priority.ALWAYS
        );

        

        BorderPane root =
                new BorderPane();

       

        root.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        root.setLeft(sidebar);
        root.setCenter(center);



        Scene scene =
                new Scene(root);

        stage.setTitle(
                "EventXpert - AI Planning"
        );

        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
    }

    

    private Label label(
            String text,
            double size,
            boolean bold,
            Color color
    ) {

        Label l =
                new Label(text);

        l.setFont(
                Font.font(
                        "Arial",
                        bold
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        size
                )
        );

        l.setTextFill(color);

        return l;
    }

    

    private HBox navItem(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel =
                label(
                        icon,
                        16,
                        false,
                        active
                                ? Color.WHITE
                                : Color.web("#D0DFEC")
                );

        Label textLabel =
                label(
                        text,
                        12,
                        true,
                        active
                                ? Color.WHITE
                                : Color.web("#D0DFEC")
                );

        HBox item =
                new HBox(
                        12,
                        iconLabel,
                        textLabel
                );

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPrefHeight(38);

        item.setPadding(
                new Insets(7, 10, 7, 12)
        );

        item.setStyle(
                active
                        ? "-fx-background-color:#07396E;" +
                          "-fx-border-color:" + LIGHT + ";" +
                          "-fx-border-width:0 0 0 3;" +
                          "-fx-background-radius:7;"
                        : "-fx-background-color:transparent;"
        );

        return item;
    }

    

    private VBox feature(
            String icon,
            String title,
            String description
    ) {

        Label iconLabel =
                label(
                        icon,
                        16,
                        true,
                        Color.web(NAVY)
                );

        iconLabel.setPrefSize(
                28,
                28
        );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setStyle(
                "-fx-background-color:#C9E9FF;" +
                "-fx-background-radius:50;"
        );

        Label titleLabel =
                label(
                        title,
                        12,
                        true,
                        Color.web(TEXT)
                );

        Label descriptionLabel =
                label(
                        description,
                        9,
                        false,
                        Color.web(GREY)
                );

        descriptionLabel.setWrapText(true);

        VBox text =
                new VBox(
                        3,
                        titleLabel,
                        descriptionLabel
                );

        HBox row =
                new HBox(
                        10,
                        iconLabel,
                        text
                );

        row.setAlignment(
                Pos.TOP_LEFT
        );

        return new VBox(row);
    }

    

    private Button footerButton(
            String text,
            double width,
            boolean primary
    ) {

        Button b =
                new Button(text);

        b.setPrefSize(
                width,
                42
        );

        b.setStyle(
                primary
                        ? "-fx-background-color:" + NAVY + ";" +
                          "-fx-background-radius:7;" +
                          "-fx-text-fill:white;" +
                          "-fx-font-weight:bold;"
                        : "-fx-background-color:white;" +
                          "-fx-border-color:#9EB1C1;" +
                          "-fx-border-radius:7;" +
                          "-fx-background-radius:7;" +
                          "-fx-text-fill:" + NAVY + ";"
        );

        return b;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
