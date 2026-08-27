package com.techtrio.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class AIPage  {

    // ================= COLORS =================

    private final String NAVY = "#061B41";
    private final String BLUE = "#0D3979";
    private final String LIGHT_BLUE = "#BFE5FF";
    private final String BG = "#F5F8FC";
    private final String WHITE = "#FFFFFF";
    private final String TEXT = "#17233C";
    private final String BORDER = "#D7DEE8";
    private final String MUTED = "#5E6878";

    // =====================================================
    // START
    // =====================================================

   public Scene getAIPageScene(Runnable backAction) {

        // =====================================================
        // LEFT SIDEBAR
        // =====================================================

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);

        sidebar.setPadding(
                new Insets(25, 0, 20, 0)
        );

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        // ---------- Logo ----------

        Label logo = label(
                "EventXpert",
                30,
                true,
                Color.WHITE
        );

        Label adminDashboard = label(
                "Admin Dashboard",
                16,
                false,
                Color.WHITE
        );

        VBox logoBox = new VBox(
                2,
                logo,
                adminDashboard
        );

        logoBox.setPadding(
                new Insets(0, 0, 25, 30)
        );

        // ---------- Separator ----------

        Region separator = new Region();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color:#17345E;"
        );

        // ---------- Navigation ----------

        VBox navigation = new VBox();

        navigation.getChildren().addAll(

                navItem(
                        "☷",
                        "Event Details",
                        false
                ),

                navItem(
                        "▣",
                        "Requirements",
                        false
                ),

                navItem(
                        "⌖",
                        "Location",
                        false
                ),

                navItem(
                        "▣",
                        "Budget",
                        false
                ),

                navItem(
                        "♙",
                        "AI Planning",
                        true
                ),

                navItem(
                        "▤",
                        "Review",
                        false
                )
        );

        // ---------- Bottom Navigation ----------

       



sidebar.getChildren().addAll(
        logoBox,
        separator,
        navigation
);

        // =====================================================
        // TOP BAR
        // =====================================================

        BorderPane topBar = new BorderPane();

        topBar.setPrefHeight(78);

        topBar.setPadding(
                new Insets(17, 20, 17, 35)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D4DCE6;" +
                "-fx-border-width:0 0 1 0;"
        );

        // ---------- Search ----------

        HBox searchBox = new HBox();

        searchBox.setPrefWidth(415);
        searchBox.setPrefHeight(42);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPadding(
                new Insets(0, 14, 0, 15)
        );

        searchBox.setStyle(
                "-fx-background-color:#FFFFFF;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        Label searchIcon = label(
                "⌕",
                25,
                true,
                Color.web(TEXT)
        );

        Label searchText = label(
                "Search events...",
                15,
                false,
                Color.web(TEXT)
        );

        Region searchSpacer = new Region();

        searchBox.getChildren().addAll(
                searchIcon,
                searchSpacer,
                searchText
        );

        topBar.setLeft(searchBox);

        // ---------- Top Right ----------

        HBox topRight = new HBox(30);

        topRight.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label notification = label(
                "♧",
                27,
                true,
                Color.web(TEXT)
        );

        Label help = label(
                "?",
                15,
                true,
                Color.web(TEXT)
        );

        help.setPrefSize(24, 24);

        help.setAlignment(
                Pos.CENTER
        );

        help.setStyle(
                "-fx-border-color:" + TEXT + ";" +
                "-fx-border-radius:50;"
        );

        Circle avatarCircle =
                new Circle(
                        18,
                        Color.web(NAVY)
                );

        Label avatarText = label(
                "A",
                15,
                true,
                Color.WHITE
        );

        StackPane avatar = new StackPane();

        avatar.getChildren().addAll(
                avatarCircle,
                avatarText
        );

        topRight.getChildren().addAll(
                notification,
                help,
                avatar
        );

        topBar.setRight(topRight);

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content = new VBox();

        content.setPadding(
                new Insets(30, 35, 15, 35)
        );

        content.setSpacing(25);

        // =====================================================
        // HEADING
        // =====================================================

        HBox headingRow = new HBox();

        headingRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headingBox = new VBox(5);

        Label heading = label(
                "AI Planning ✨",
                34,
                true,
                Color.web("#101828")
        );

        Label subtitle = label(
                "Your intelligent event planning assistant",
                18,
                false,
                Color.web("#303746")
        );

        headingBox.getChildren().addAll(
                heading,
                subtitle
        );

        Region headingSpacer = new Region();

        HBox.setHgrow(
                headingSpacer,
                Priority.ALWAYS
        );

        // ---------- AI Status ----------


        // =====================================================
        // MAIN CARDS
        // =====================================================

        HBox mainCards = new HBox(25);

        VBox eventSummary =
                createEventSummaryCard();

        HBox.setHgrow(
                eventSummary,
                Priority.ALWAYS
        );

        VBox aiPlanner =
                createAiPlannerCard();

        aiPlanner.setPrefWidth(320);
        aiPlanner.setMinWidth(320);

        mainCards.getChildren().addAll(
                eventSummary,
                aiPlanner
        );

        // =====================================================
        // PREFERENCES
        // =====================================================

        VBox preferences =
                createPreferencesCard();

        // =====================================================
        // AI INSIGHTS
        // =====================================================

        HBox insights =
                createInsightsCard();

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                headingRow,
                mainCards,
                preferences,
                insights
        );

        // =====================================================
        // BOTTOM BAR
        // =====================================================

        HBox bottomBar = new HBox();

        bottomBar.setPrefHeight(70);

        bottomBar.setPadding(
                new Insets(10, 20, 10, 35)
        );

        bottomBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        bottomBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D4DCE6;" +
                "-fx-border-width:1 0 0 0;"
        );

        Label copyright = label(
                "© 2024 EventXpert Admin Portal",
                13,
                false,
                Color.web("#303746")
        );

        Region bottomSpacer = new Region();

        HBox.setHgrow(
                bottomSpacer,
                Priority.ALWAYS
        );

        Button backButton =
                createBottomButton(
                        "Back",
                        false
                );

                backButton.setOnAction(e -> {

    if (backAction != null) {
        backAction.run();
    }

});

       
        Button reviewButton =
                createBottomButton(
                        "Next ",
                        true
                );
 
                reviewButton.setOnAction(e -> {

    ReviewPageC reviewPage = new ReviewPageC();

    Scene reviewScene = reviewPage.getReviewScene();

    LandingPage.landingPageStage.setScene(reviewScene);
});
        bottomBar.getChildren().addAll(
                copyright,
                bottomSpacer,
                backButton,
                reviewButton
        );

        HBox.setMargin(
                backButton,
                new Insets(0, 15, 0, 0)
        );

        // =====================================================
// SCROLL PANE
// =====================================================

ScrollPane scrollPane = new ScrollPane();

scrollPane.setContent(content);

scrollPane.setFitToWidth(true);

scrollPane.setHbarPolicy(
        ScrollPane.ScrollBarPolicy.NEVER
);

scrollPane.setVbarPolicy(
        ScrollPane.ScrollBarPolicy.AS_NEEDED
);

scrollPane.setStyle(
        "-fx-background-color:" + BG + ";" +
        "-fx-border-color:transparent;"
);

        // =====================================================
        // CENTER
        // =====================================================

        BorderPane center = new BorderPane();

        center.setTop(topBar);
        center.setCenter(scrollPane);
        center.setBottom(bottomBar);

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        root.setLeft(sidebar);
        root.setCenter(center);

        // =====================================================
        // SCENE
        // =====================================================

        Scene scene = new Scene(
                root,
                1365,
                800
        );

return scene;    }

    // =====================================================
    // EVENT SUMMARY CARD
    // =====================================================

    private VBox createEventSummaryCard() {

        VBox card = new VBox();

        card.setPadding(
                new Insets(20, 20, 18, 20)
        );

        card.setSpacing(14);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        Label title =
                createCardTitle(
                        "▣",
                        "Event Summary"
                );

        Region line = new Region();

        line.setPrefHeight(1);

        line.setStyle(
                "-fx-background-color:#D9DEE6;"
        );

        GridPane grid = new GridPane();

        grid.setHgap(80);
        grid.setVgap(15);

        addSummary(
                grid,
                "TYPE",
                "Corporate Event",
                0,
                0
        );

        addSummary(
                grid,
                "NAME",
                "Annual Business Conference",
                1,
                0
        );

        addSummary(
                grid,
                "DATE & TIME",
                "25 May 2025, 10:00 AM",
                0,
                1
        );

        addSummary(
                grid,
                "LOCATION",
                "Pune, Maharashtra",
                1,
                1
        );

        addSummary(
                grid,
                "GUESTS",
                "150",
                0,
                2
        );

        addSummary(
                grid,
                "BUDGET",
                "₹2,00,000",
                1,
                2
        );

        addSummary(
                grid,
                "PURPOSE",
                "Annual Business Meeting",
                0,
                3
        );

        addSummary(
                grid,
                "THEME",
                "Professional & Modern",
                1,
                3
        );

        card.getChildren().addAll(
                title,
                line,
                grid
        );

        return card;
    }

    // =====================================================
    // SUMMARY ITEM
    // =====================================================

    private void addSummary(
            GridPane grid,
            String labelTextValue,
            String value,
            int column,
            int row) {

        VBox box = new VBox(4);

        Label labelText = label(
                labelTextValue,
                12,
                true,
                Color.web("#283449")
        );

        Label valueText = label(
                value,
                16,
                false,
                Color.web("#171717")
        );

        if (labelTextValue.equals("BUDGET")) {

            valueText.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            17
                    )
            );
        }

        box.getChildren().addAll(
                labelText,
                valueText
        );

        grid.add(
                box,
                column,
                row
        );
    }

    // =====================================================
    // AI PLANNER CARD
    // =====================================================

    private VBox createAiPlannerCard() {

        VBox card = new VBox();

        card.setPadding(
                new Insets(25, 25, 25, 25)
        );

        card.setSpacing(22);

        card.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:12;"
        );

        Circle aiCircle =
                new Circle(
                        35,
                        Color.web("#173B70")
                );

        Label robot = label(
                "♙",
                28,
                true,
                Color.WHITE
        );

        StackPane robotPane =
                new StackPane();

        robotPane.getChildren().addAll(
                aiCircle,
                robot
        );

        Label title = label(
                "AI Planner Ready",
                20,
                true,
                Color.WHITE
        );

        HBox aiTitle =
                new HBox(12);

        aiTitle.setAlignment(
                Pos.CENTER_LEFT
        );

        aiTitle.getChildren().addAll(
                robotPane,
                title
        );

        Label description = label(
                "\"I’ll analyze your event\n" +
                "requirements and create the best\n" +
                "plan for your event.\"",
                16,
                false,
                Color.web("#D4E1F5")
        );

        description.setWrapText(true);

        VBox checks = new VBox(16);

        checks.getChildren().addAll(
                createCheck(
                        "Analyzing event requirements",
                        true
                ),
                createCheck(
                        "Checking preferences",
                        true
                ),
                createCheck(
                        "Optimizing budget",
                        true
                ),
                createCheck(
                        "Finding suitable options",
                        false
                ),
                createCheck(
                        "Preparing recommendations",
                        false
                )
        );

        Button generate =
                new Button(
                        "Generate AI Plan  ✨"
                );

        generate.setMaxWidth(
                Double.MAX_VALUE
        );

        generate.setPrefHeight(60);

        generate.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        generate.setTextFill(
                Color.web(NAVY)
        );

        generate.setStyle(
                "-fx-background-color:" +
                LIGHT_BLUE + ";" +
                "-fx-background-radius:8;" +
                "-fx-cursor:hand;"
        );

        card.getChildren().addAll(
                aiTitle,
                description,
                checks,
                generate
        );

        return card;
    }

    // =====================================================
    // CHECK ITEM
    // =====================================================

    private HBox createCheck(
            String text,
            boolean active) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label icon = label(
                active ? "✓" : "◉",
                17,
                true,
                active
                        ? Color.WHITE
                        : Color.web("#7185A4")
        );

        Label checkText = label(
                text,
                15,
                false,
                active
                        ? Color.WHITE
                        : Color.web("#8294B0")
        );

        row.getChildren().addAll(
                icon,
                checkText
        );

        return row;
    }

    // =====================================================
    // PREFERENCES CARD
    // =====================================================

    private VBox createPreferencesCard() {

        VBox card = new VBox();

        card.setPadding(
                new Insets(18, 20, 18, 20)
        );

        card.setSpacing(15);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        Label title =
                createCardTitle(
                        "☷",
                        "Your Preferences"
                );

        Region line = new Region();

        line.setPrefHeight(1);

        line.setStyle(
                "-fx-background-color:#D9DEE6;"
        );

        FlowPane tags =
                new FlowPane();

        tags.setHgap(12);
        tags.setVgap(12);

        tags.getChildren().addAll(

                createTag(
                        "▱",
                        "Conference Setup"
                ),

                createTag(
                        "♜",
                        "High Tea + Lunch"
                ),

                createTag(
                        "▣",
                        "Projector, Mic, Sound, Wi-Fi"
                ),

                createTag(
                        "◈",
                        "Professional Décor, Branding"
                ),

                createTag(
                        "P",
                        "Parking Required"
                ),

                createTag(
                        "♙",
                        "Registration Desk"
                )
        );

        card.getChildren().addAll(
                title,
                line,
                tags
        );

        return card;
    }

    // =====================================================
    // PREFERENCE TAG
    // =====================================================

    private HBox createTag(
            String icon,
            String text) {

        HBox tag =
                new HBox(8);

        tag.setAlignment(
                Pos.CENTER
        );

        tag.setPadding(
                new Insets(8, 14, 8, 12)
        );

        tag.setStyle(
                "-fx-background-color:#F8FAFD;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        Label iconLabel = label(
                icon,
                15,
                true,
                Color.web("#24557D")
        );

        Label textLabel = label(
                text,
                14,
                false,
                Color.web("#252B35")
        );

        tag.getChildren().addAll(
                iconLabel,
                textLabel
        );

        return tag;
    }

    // =====================================================
    // AI INSIGHTS
    // =====================================================

    private HBox createInsightsCard() {

        HBox card =
                new HBox(20);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(18, 20, 18, 20)
        );

        card.setStyle(
                "-fx-background-color:#DFF2FF;" +
                "-fx-border-color:#B7E1FF;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        Circle bulbCircle =
                new Circle(
                        25,
                        Color.web(NAVY)
                );

        Label bulb = label(
                "♧",
                20,
                true,
                Color.WHITE
        );

        StackPane bulbPane =
                new StackPane();

        bulbPane.getChildren().addAll(
                bulbCircle,
                bulb
        );

        VBox textBox =
                new VBox(8);

        Label title = label(
                "AI Insights",
                20,
                true,
                Color.web("#17233C")
        );

        Label insight1 =
                createInsight(
                        "Book early for better venue pricing in Pune"
                );

        Label insight2 =
                createInsight(
                        "High tea + lunch is ideal for the 10:00 AM start time"
                );

        Label insight3 =
                createInsight(
                        "Confirm vendors early for potential 10-15% savings"
                );

        Label insight4 =
                createInsight(
                        "Consider adding a 30-min networking session post-lunch"
                );

        Label insight5 =
                createInsight(
                        "Confirm AV setup strictly 1 day before the event"
                );

        GridPane insights =
                new GridPane();

        insights.setHgap(40);
        insights.setVgap(8);

        insights.add(
                insight1,
                0,
                0
        );

        insights.add(
                insight2,
                1,
                0
        );

        insights.add(
                insight3,
                0,
                1
        );

        insights.add(
                insight4,
                1,
                1
        );

        insights.add(
                insight5,
                0,
                2
        );

        textBox.getChildren().addAll(
                title,
                insights
        );

        card.getChildren().addAll(
                bulbPane,
                textBox
        );

        return card;
    }

    // =====================================================
    // INSIGHT ITEM
    // =====================================================

    private Label createInsight(
            String text) {

        return label(
                "•   " + text,
                14,
                false,
                Color.web("#303746")
        );
    }

    // =====================================================
    // CARD TITLE
    // =====================================================

    private Label createCardTitle(
            String icon,
            String title) {

        return label(
                icon + "   " + title,
                20,
                true,
                Color.web(TEXT)
        );
    }

    // =====================================================
    // SIDEBAR NAV ITEM
    // =====================================================

    private HBox navItem(
            String icon,
            String text,
            boolean active) {

        Label iconLabel = label(
                icon,
                17,
                false,
                active
                        ? Color.WHITE
                        : Color.web("#A6B5CA")
        );

        Label textLabel = label(
                text,
                16,
                active,
                active
                        ? Color.WHITE
                        : Color.web("#A6B5CA")
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

        item.setPrefHeight(56);

        item.setMinHeight(56);

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setPadding(
                new Insets(0, 0, 0, 30)
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:#2F5593;" +
                    "-fx-border-color:#BFE5FF;" +
                    "-fx-border-width:0 0 0 4;"
            );

        } else {

            item.setStyle(
                    "-fx-background-color:transparent;"
            );
        }

        return item;
    }

    // =====================================================
    // BOTTOM BUTTON
    // =====================================================

    private Button createBottomButton(
            String text,
            boolean primary) {

        Button button =
                new Button(text);

        button.setPrefHeight(46);

        button.setPrefWidth(
                primary ? 190 : 130
        );

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        if (primary) {

            button.setTextFill(
                    Color.WHITE
            );

            button.setStyle(
                    "-fx-background-color:" +
                    NAVY + ";" +
                    "-fx-background-radius:8;" +
                    "-fx-cursor:hand;"
            );

        } else {

            button.setTextFill(
                    Color.web("#285879")
            );

            button.setStyle(
                    "-fx-background-color:white;" +
                    "-fx-border-color:#7D91A8;" +
                    "-fx-border-radius:8;" +
                    "-fx-background-radius:8;" +
                    "-fx-cursor:hand;"
            );
        }

        return button;
    }

    // =====================================================
    // COMMON LABEL METHOD
    // =====================================================

    private Label label(
            String text,
            double size,
            boolean bold,
            Color color) {

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

    // =====================================================
    // MAIN
    // =====================================================

    
}
