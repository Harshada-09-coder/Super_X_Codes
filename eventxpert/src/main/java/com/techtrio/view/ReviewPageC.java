package com.techtrio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ReviewPageC  {


    private final String BLUE = "#002A60";
    private final String BG = "#F7F9FC";
    private final String BORDER = "#D7E0E8";
    private final String TEXT = "#111111";
    private final String GREY = "#667085";

    

    public static class EventData {


        public static String eventName =
                "Annual Tech Symposium 2024";

        public static String eventDate =
                "Oct 15 - Oct 17, 2024";

        public static String eventType =
                "Corporate Conference";

        
        public static String requirements =
                "Venue, Catering, Decoration, Photography";

        public static String notes =
                "Special seating arrangement required.";

        
        public static String city =
                "Mumbai";

        public static String area =
                "Andheri";

        public static String venue =
                "Convention Center";

        
        public static String budget =
                "₹500,000";


        public static boolean aiEnabled =
                true;
    }

    
   public Scene getReviewScene() {
        

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        
        VBox sidebar = createSidebar();

        root.setLeft(sidebar);

        
        VBox rightSide = new VBox();

        rightSide.setStyle(
                "-fx-background-color:" + BG + ";"
        );


        HBox topBar = createTopBar();

        
        VBox page = createPage();

        
        HBox footer = createFooter();

        rightSide.getChildren().addAll(
                topBar,
                page,
                footer
        );

        VBox.setVgrow(
                page,
                Priority.ALWAYS
        );

        root.setCenter(rightSide);

        

        Scene scene = new Scene(
                root,
                1250,
                750
        );

        return scene;
    }

    
    private VBox createSidebar() {

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setMaxWidth(230);

        sidebar.setPadding(
                new Insets(
                        35,
                        18,
                        25,
                        18
                )
        );

        
        sidebar.setStyle(
                "-fx-background-color:#002A60;"
        );

        
        Label logo = new Label(
                "EventXpert"
        );

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        24
                )
        );

        logo.setTextFill(
                Color.WHITE
        );

        

        Label createEvent = new Label(
                "Create Event"
        );

        createEvent.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        createEvent.setTextFill(
                Color.WHITE
        );

        Label stepText = new Label(
                "Step-by-step setup"
        );

        stepText.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        stepText.setTextFill(
                Color.web("#D6E5F4")
        );

        VBox heading = new VBox(
                3,
                createEvent,
                stepText
        );

        heading.setPadding(
                new Insets(
                        8,
                        0,
                        20,
                        0
                )
        );

        

        VBox navigation = new VBox(8);

        navigation.getChildren().addAll(

                navItem(
                        "ⓘ",
                        "Event Details",
                        false
                ),

                navItem(
                        "▤",
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
                        "✦",
                        "AI Planning",
                        false
                ),

                navItem(
                        "✓",
                        "Review",
                        true
                )
        );

        

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        

        sidebar.getChildren().addAll(
                logo,
                heading,
                navigation,
                spacer
        );

        return sidebar;
    }

    
    private HBox navItem(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel = new Label(
                icon
        );

        iconLabel.setFont(
                Font.font(17)
        );

        Label textLabel = new Label(
                text
        );

        textLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        HBox item = new HBox(
                12,
                iconLabel,
                textLabel
        );

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(
                        11,
                        12,
                        11,
                        12
                )
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:#123A67;" +
                    "-fx-background-radius:8;" +
                    "-fx-border-color:#C2F0F4;" +
                    "-fx-border-width:0 0 0 3;" +
                    "-fx-border-radius:8;"
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
                    Color.web("#D6E5F4")
            );

            textLabel.setTextFill(
                    Color.web("#D6E5F4")
            );
        }

        return item;
    }

    private HBox createTopBar() {

        HBox bar = new HBox();

        bar.setPrefHeight(65);

        bar.setAlignment(
                Pos.CENTER_RIGHT
        );

        bar.setPadding(
                new Insets(
                        12,
                        30,
                        10,
                        25
                )
        );

        bar.setSpacing(25);

        bar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E1E7ED;" +
                "-fx-border-width:0 0 1 0;"
        );

        Label notification = new Label(
                "♧"
        );

        Label help = new Label(
                "?"
        );

        Label profile = new Label(
                "●"
        );

        notification.setFont(
                Font.font(20)
        );

        help.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        profile.setFont(
                Font.font(20)
        );

        notification.setTextFill(
                Color.web(BLUE)
        );

        help.setTextFill(
                Color.web(BLUE)
        );

        profile.setTextFill(
                Color.web(BLUE)
        );

        bar.getChildren().addAll(
                notification,
                help,
                profile
        );

        return bar;
    }

    

    private VBox createPage() {

        VBox page = new VBox();

        page.setPadding(
                new Insets(
                        25,
                        30,
                        10,
                        30
                )
        );

        page.setSpacing(18);

        page.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        

        Label title = new Label(
                "Review Your Event"
        );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        30
                )
        );

        title.setTextFill(
                Color.web(TEXT)
        );

        Label subtitle = new Label(
                "Please review all details before submitting your request."
        );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        subtitle.setTextFill(
                Color.web(GREY)
        );

        VBox titleBox = new VBox(
                4,
                title,
                subtitle
        );

        

        HBox row1 = new HBox(18);

        VBox eventCard =
                eventDetailsCard();

        VBox locationCard =
                locationCard();

        HBox.setHgrow(
                eventCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                locationCard,
                Priority.ALWAYS
        );

        row1.getChildren().addAll(
                eventCard,
                locationCard
        );

        

        HBox row2 = new HBox(18);

        VBox requirementCard =
                requirementsCard();

        VBox budgetCard =
                budgetCard();

        VBox aiCard =
                aiCard();

        HBox.setHgrow(
                requirementCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                budgetCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                aiCard,
                Priority.ALWAYS
        );

        row2.getChildren().addAll(
                requirementCard,
                budgetCard,
                aiCard
        );

       

        page.getChildren().addAll(
                titleBox,
                row1,
                row2
        );

        return page;
    }

   
    private VBox eventDetailsCard() {

        VBox card = createCard();

        card.getChildren().add(
                cardTitle(
                        "ⓘ",
                        "Event Details"
                )
        );

        addDetail(
                card,
                "Name",
                EventData.eventName
        );

        addDetail(
                card,
                "Date",
                EventData.eventDate
        );

        addDetail(
                card,
                "Type",
                EventData.eventType
        );

        return card;
    }

    

    private VBox locationCard() {

        VBox card = createCard();

        card.getChildren().add(
                cardTitle(
                        "⌖",
                        "Location"
                )
        );

        addDetail(
                card,
                "City",
                EventData.city
        );

        addDetail(
                card,
                "Area",
                EventData.area
        );

        addDetail(
                card,
                "Venue",
                EventData.venue
        );

        return card;
    }

   
    private VBox requirementsCard() {

        VBox card = createCard();

        card.getChildren().add(
                cardTitle(
                        "▤",
                        "Requirements"
                )
        );

        Label servicesTitle =
                new Label("Services");

        servicesTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        servicesTitle.setTextFill(
                Color.web(GREY)
        );

        Label services =
                new Label(
                        EventData.requirements
                );

        services.setWrapText(true);

        services.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        services.setTextFill(
                Color.web(TEXT)
        );

        card.getChildren().addAll(
                servicesTitle,
                services
        );

        if (EventData.notes != null &&
                !EventData.notes.trim().isEmpty()) {

            Label noteTitle =
                    new Label(
                            "Additional Notes"
                    );

            noteTitle.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            12
                    )
            );

            noteTitle.setTextFill(
                    Color.web(GREY)
            );

            Label note =
                    new Label(
                            EventData.notes
                    );

            note.setWrapText(true);

            note.setFont(
                    Font.font(
                            "Arial",
                            12
                    )
            );

            note.setTextFill(
                    Color.web(TEXT)
            );

            card.getChildren().addAll(
                    noteTitle,
                    note
            );
        }

        return card;
    }

    private VBox budgetCard() {

        VBox card = createCard();

        card.getChildren().add(
                cardTitle(
                        "▣",
                        "Budget"
                )
        );

        addDetail(
                card,
                "Total Allocation",
                EventData.budget
        );

        addDetail(
                card,
                "Smart Budget Optimizer",
                EventData.aiEnabled
                        ? "Enabled"
                        : "Disabled"
        );

        return card;
    }

    

    private VBox aiCard() {

        VBox card = createCard();

        card.getChildren().add(
                cardTitle(
                        "✦",
                        "AI Planning"
                )
        );

        if (EventData.aiEnabled) {

            addAIItem(
                    card,
                    "Smart Planning Enabled"
            );

            addAIItem(
                    card,
                    "Dynamic Timeline"
            );

            addAIItem(
                    card,
                    "Proactive Suggestions"
            );

        } else {

            Label disabled =
                    new Label(
                            "Skipped for Now"
                    );

            disabled.setFont(
                    Font.font(
                            "Arial",
                            13
                    )
            );

            disabled.setTextFill(
                    Color.web(GREY)
            );

            card.getChildren().add(
                    disabled
            );
        }

        return card;
    }


    private VBox createCard() {

        VBox card = new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;"
        );

        return card;
    }

    

    private HBox cardTitle(
            String icon,
            String title
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font(18)
        );

        iconLabel.setTextFill(
                Color.web(BLUE)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        titleLabel.setTextFill(
                Color.web(TEXT)
        );

        HBox box =
                new HBox(
                        8,
                        iconLabel,
                        titleLabel
                );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        return box;
    }

    
    private void addDetail(
            VBox card,
            String labelText,
            String valueText
    ) {

        Label label =
                new Label(labelText);

        label.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        label.setTextFill(
                Color.web(GREY)
        );

        Label value =
                new Label(valueText);

        value.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        value.setTextFill(
                Color.web(TEXT)
        );

        value.setWrapText(true);

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        HBox row =
                new HBox(
                        10,
                        label,
                        space,
                        value
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Separator separator =
                new Separator();

        separator.setStyle(
                "-fx-background-color:#D0D5DB;"
        );

        card.getChildren().addAll(
                row,
                separator
        );
    }

    

    private void addAIItem(
            VBox card,
            String text
    ) {

        Label item =
                new Label(
                        "✓  " + text
                );

        item.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        item.setTextFill(
                Color.web(TEXT)
        );

        card.getChildren().add(
                item
        );
    }

    

    private HBox createFooter() {

        HBox footer =
                new HBox(12);

        footer.setAlignment(
                Pos.CENTER_RIGHT
        );

        footer.setPadding(
                new Insets(
                        12,
                        30,
                        18,
                        30
                )
        );

        footer.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#DCE3E9;" +
                "-fx-border-width:1 0 0 0;"
        );

        

        Button back =
                new Button("Back");

        back.setPrefSize(
                95,
                44
        );

        back.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#9EABB8;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-text-fill:#002A60;"
        );

        
        Button save =
                new Button(
                        "Save Draft"
                );

        save.setPrefSize(
                110,
                44
        );

        save.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#9EABB8;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-text-fill:#002A60;"
        );

        

        Button create =
                new Button(
                        "Create Event"
                );

        create.setPrefSize(
                125,
                44
        );

        create.setStyle(
                "-fx-background-color:#002A60;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;"
        );

        back.setOnAction(e -> {

    AiPlanningC aiPlanningPage = new AiPlanningC();

    LandingPage.landingPageStage.setScene(
            aiPlanningPage.getAiPlanningScene(
                    null,
                    null
            )
    );
});

       
          save.setOnAction(e -> {

            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setTitle(
                    "EventXpert"
            );

            alert.setHeaderText(
                    "Draft Saved"
            );

            alert.setContentText(
                    "Your event has been saved as draft."
            );

            alert.showAndWait();
        });

        create.setOnAction(e -> {

    EventDetailsC eventDetailsPage =
            new EventDetailsC();

    LandingPage.landingPageStage.setScene(
            eventDetailsPage.getEventDetailsScene()
    );
});
        

        footer.getChildren().addAll(
                back,
                save,
                create
        );

        return footer;
    }

    
    
}

