package com.techtrio.view;

import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AiPlanningC {

    private Scene scene;
    private String uid;
    private String idToken;

    private final String NAVY = "#002A60";
    private final String BLUE = "#0057B8";
    private final String BG = "#F5F8FA";
    private final String BORDER = "#D5DEE7";
    private final String TEXT = "#102A43";
    private final String GREY = "#667085";

    public AiPlanningC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getAiPlanningScene() {
        VBox sidebar = new VBox(7);
        sidebar.setPrefWidth(205);
        sidebar.setMinWidth(205);
        sidebar.setMaxWidth(205);
        sidebar.setPadding(new Insets(24, 8, 20, 8));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 21, true, Color.WHITE);
        VBox eventTitle = new VBox(2,
                label("Create Event", 16, true, Color.WHITE),
                label("Step-by-step setup", 10, false, Color.web("#D1DFEB"))
        );
        eventTitle.setPadding(new Insets(7, 12, 15, 12));

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
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(logo, eventTitle, navigation, sideSpace);

        HBox topBar = new HBox(24);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setPrefHeight(58);
        topBar.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#DCE5EC;-fx-border-width:0 0 1 0;");
        topBar.getChildren().addAll(
                label("♧", 19, false, Color.web(NAVY)),
                label("?", 16, true, Color.web(NAVY))
        );

        VBox heading = new VBox(7);
        heading.setAlignment(Pos.CENTER);
        heading.setPadding(new Insets(20));

        Label aiIcon = label("✦", 22, true, Color.web(NAVY));
        aiIcon.setPrefSize(42, 42);
        aiIcon.setAlignment(Pos.CENTER);
        aiIcon.setStyle("-fx-background-color:#C5E9FF;-fx-background-radius:50;");

        Label title = label("Plan Smarter with AI", 27, true, Color.web("#111111"));
        Label subtitle = label("Let our AI assistant optimize your timeline and create smart checklists.", 13, false, Color.web(GREY));
        heading.getChildren().addAll(aiIcon, title, subtitle);

        ToggleButton aiToggle = new ToggleButton(ReviewPageC.EventData.aiEnabled ? "✓" : "○");
        aiToggle.setSelected(ReviewPageC.EventData.aiEnabled);
        aiToggle.setPrefSize(50, 28);
        aiToggle.setStyle("-fx-background-color:" + NAVY + ";-fx-background-radius:20;-fx-text-fill:white;");

        aiToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            aiToggle.setText(newVal ? "✓" : "○");
            ReviewPageC.EventData.aiEnabled = newVal;
        });

        HBox toggleBox = new HBox(10, label("Enable AI Planning Assistant:", 14, true, Color.web(TEXT)), aiToggle);
        toggleBox.setAlignment(Pos.CENTER);

        VBox mainContent = new VBox(15, heading, toggleBox);
        mainContent.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Button back = footerButton("← Back", 100, false);
        Button next = footerButton("Next →", 90, true);

        back.setOnAction(e -> {
            BudgetC budgetView = new BudgetC(uid, idToken);
            LandingPage.landingPageStage.setScene(budgetView.getBudgetScene());
        });

        next.setOnAction(e -> {
            ReviewPageC.EventData.aiEnabled = aiToggle.isSelected();
            ReviewPageC reviewView = new ReviewPageC(uid, idToken);
            LandingPage.landingPageStage.setScene(reviewView.getReviewScene());
        });

        HBox footer = new HBox(10, back, new Region(), next);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 30, 12, 30));
        footer.setPrefHeight(68);
        footer.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#DCE5EC;-fx-border-width:1 0 0 0;");

        VBox center = new VBox(topBar, mainContent, footer);
        center.setStyle("-fx-background-color:" + BG + ";");
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + NAVY + ";");
        root.setLeft(sidebar);
        root.setCenter(center);

        scene = new Scene(root, 1365, 850);
        return scene;
    }

    private Label label(String text, double size, boolean bold, Color color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        l.setTextFill(color);
        return l;
    }

    private HBox navItem(String icon, String text, boolean active) {
        Label iconLabel = label(icon, 16, false, active ? Color.WHITE : Color.web("#D0DFEC"));
        Label textLabel = label(text, 12, true, active ? Color.WHITE : Color.web("#D0DFEC"));
        HBox item = new HBox(12, iconLabel, textLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPrefHeight(38);
        item.setPadding(new Insets(7, 10, 7, 12));
        if (active) {
            item.setStyle("-fx-background-color:#07396E;-fx-border-color:#C2F0F4;-fx-border-width:0 0 0 3;-fx-background-radius:7;");
        } else {
            item.setStyle("-fx-background-color:transparent;");
        }
        return item;
    }

    private Button footerButton(String text, double width, boolean primary) {
        Button b = new Button(text);
        b.setPrefSize(width, 42);
        b.setStyle(primary
                ? "-fx-background-color:" + NAVY + ";-fx-background-radius:7;-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;"
                : "-fx-background-color:white;-fx-border-color:#9EB1C1;-fx-border-radius:7;-fx-background-radius:7;-fx-text-fill:" + NAVY + ";-fx-cursor:hand;");
        return b;
    }
}