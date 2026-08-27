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

public class BudgetC {

    private Scene scene;
    private String uid;
    private String idToken;

    private final String NAVY = "#002A60";
    private final String BLUE = "#0057B8";
    private final String LIGHT = "#C2F0F4";
    private final String BG = "#F5F8FA";
    private final String BORDER = "#D5DEE7";
    private final String TEXT = "#102A43";
    private final String GREY = "#667085";

    public BudgetC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getBudgetScene() {
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
                navItem("▣", "Budget", true),
                navItem("✦", "AI Planning", false),
                navItem("▤", "Review", false)
        );

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(logo, eventTitle, navigation, sidebarSpace);

        HBox topBar = new HBox(24);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setPrefHeight(58);
        topBar.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#DCE5EC;-fx-border-width:0 0 1 0;");
        topBar.getChildren().addAll(
                label("♧", 19, false, Color.web(NAVY)),
                label("?", 16, true, Color.web(NAVY))
        );

        VBox title = new VBox(4,
                label("Set Your Event Budget", 28, true, Color.web("#111111")),
                label("Define your total spend and let our system optimize distribution.", 13, false, Color.web(GREY))
        );
        title.setPadding(new Insets(22, 32, 18, 32));

        VBox estimated = card();
        estimated.getChildren().add(label("Estimated Total Budget", 13, true, Color.web(TEXT)));

        TextField amount = new TextField(ReviewPageC.EventData.budget != null ? ReviewPageC.EventData.budget : "₹ 500,000");
        amount.setPrefHeight(48);
        amount.setFont(Font.font("Arial", 16));
        amount.setStyle("-fx-border-color:#C9D3DC;-fx-border-radius:7;-fx-background-radius:7;-fx-padding:8 12;");

        estimated.getChildren().addAll(amount, label("ⓘ  Recommended minimum: ₹100,000", 10, false, Color.web(GREY)));

        HBox content = new HBox(22, estimated);
        content.setPadding(new Insets(0, 32, 20, 32));

        Button back = footerButton("Back", 80, false);
        Button next = footerButton("Next", 82, true);

        back.setOnAction(e -> {
            LocationC locView = new LocationC(uid, idToken);
            LandingPage.landingPageStage.setScene(locView.getLocationScene());
        });

        next.setOnAction(e -> {
            ReviewPageC.EventData.budget = amount.getText().trim();
            AiPlanningC aiView = new AiPlanningC(uid, idToken);
            LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
        });

        HBox buttons = new HBox(10, back, next);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        HBox footer = new HBox(footerSpace, buttons);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 12, 30));
        footer.setPrefHeight(64);
        footer.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:#DCE5EC;-fx-border-width:1 0 0 0;");

        VBox center = new VBox(topBar, title, content, footer);
        center.setStyle("-fx-background-color:" + BG + ";");
        VBox.setVgrow(content, Priority.ALWAYS);

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

    private VBox card() {
        VBox v = new VBox(12);
        v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");
        return v;
    }

    private HBox navItem(String icon, String text, boolean active) {
        Label i = label(icon, 16, false, active ? Color.WHITE : Color.web("#D0DFEC"));
        Label t = label(text, 12, true, active ? Color.WHITE : Color.web("#D0DFEC"));
        HBox h = new HBox(12, i, t);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPrefHeight(38);
        h.setPadding(new Insets(7, 10, 7, 12));
        if (active) {
            h.setStyle("-fx-background-color:#07396E;-fx-border-color:" + LIGHT + ";-fx-border-width:0 0 0 3;-fx-background-radius:7;");
        } else {
            h.setStyle("-fx-background-color:transparent;");
        }
        return h;
    }

    private Button footerButton(String text, double width, boolean primary) {
        Button b = new Button(text);
        b.setPrefSize(width, 42);
        b.setStyle(primary
                ? "-fx-background-color:" + BLUE + ";-fx-background-radius:7;-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;"
                : "-fx-background-color:white;-fx-border-color:#A7B9C8;-fx-border-radius:7;-fx-background-radius:7;-fx-text-fill:" + NAVY + ";-fx-cursor:hand;");
        return b;
    }
}