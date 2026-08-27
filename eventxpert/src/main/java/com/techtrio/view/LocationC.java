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

public class LocationC {

    private Scene scene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    public LocationC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getLocationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 21, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 8, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label create = label("Create Event", 19, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 10, FontWeight.NORMAL, "#BDE3FF");
        VBox title = new VBox(2, create, step);
        title.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("ⓘ", "Event Details", false),
                sidebarItem("▣", "Requirements", false),
                sidebarItem("⌖", "Location", true),
                sidebarItem("▣", "Budget", false),
                sidebarItem("✦", "AI Planning", false),
                sidebarItem("▤", "Review", false)
        );

        sidebar.getChildren().addAll(brand, title, menu);

        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setMinHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:0 0 1 0;");

        Label topLogo = label("EventXpert", 14, FontWeight.BOLD, NAVY);
        Label bell = label("♧", 20, FontWeight.NORMAL, NAVY);
        Label help = label("?", 13, FontWeight.BOLD, NAVY);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + NAVY + ";-fx-border-radius:50;-fx-background-radius:50;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        topBar.getChildren().addAll(topLogo, topSpace, bell, help);

        VBox content = new VBox(12);
        content.setPadding(new Insets(25, 32, 18, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label stepText = label("STEP 3 OF 6", 9, FontWeight.BOLD, BLUE);
        Label heading = label("Where is your event happening?", 25, FontWeight.NORMAL, "#050B16");
        Label description = label("Provide location details to help us find the best logistical routes and venues.", 12, FontWeight.NORMAL, TEXT);

        VBox locationCard = new VBox(11);
        locationCard.setPadding(new Insets(18));
        locationCard.setPrefWidth(360);
        locationCard.setStyle("-fx-background-color:white;-fx-background-radius:7;-fx-border-color:" + BORDER + ";-fx-border-radius:7;");

        TextField city = new TextField(ReviewPageC.EventData.city != null ? ReviewPageC.EventData.city : "");
        city.setPromptText("e.g. Pune / Mumbai");
        styleField(city);

        TextField locality = new TextField(ReviewPageC.EventData.area != null ? ReviewPageC.EventData.area : "");
        locality.setPromptText("e.g. Downtown, Baner");
        styleField(locality);

        ComboBox<String> venueType = new ComboBox<>();
        venueType.getItems().addAll("Hotel", "Banquet Hall", "Conference Center", "Outdoor Venue", "Restaurant", "Resort", "Other");
        if (ReviewPageC.EventData.venue != null && !ReviewPageC.EventData.venue.isBlank()) {
            venueType.setValue(ReviewPageC.EventData.venue);
        } else {
            venueType.setPromptText("Select venue type");
        }
        venueType.setMaxWidth(Double.MAX_VALUE);
        styleCombo(venueType);

        locationCard.getChildren().addAll(
                formField("City", city),
                formField("Area / Locality", locality),
                formField("Venue Preference", venueType)
        );

        content.getChildren().addAll(stepText, heading, description, locationCard);

        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 30, 0, 30));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("© 2026 EventXpert", 10, FontWeight.NORMAL, GREY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        Button next = new Button("Next");
        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:11px;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:11px;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            RequirementsC reqView = new RequirementsC(uid, idToken);
            LandingPage.landingPageStage.setScene(reqView.getRequirementsScene());
        });

        next.setOnAction(e -> {
            ReviewPageC.EventData.city = city.getText().trim();
            ReviewPageC.EventData.area = locality.getText().trim();
            ReviewPageC.EventData.venue = venueType.getValue() != null ? venueType.getValue() : "Banquet Hall";

            BudgetC budgetView = new BudgetC(uid, idToken);
            LandingPage.landingPageStage.setScene(budgetView.getBudgetScene());
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox();
        rightSide.setFillWidth(true);
        rightSide.getChildren().addAll(topBar, content, footer);
        VBox.setVgrow(content, Priority.ALWAYS);

        HBox fullPage = new HBox();
        fullPage.setFillHeight(true);
        fullPage.getChildren().addAll(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        root.setCenter(fullPage);
        scene = new Scene(root, 1365, 850);
        return scene;
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setMinHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 16, FontWeight.NORMAL, "white"), label(text, 11, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private VBox formField(String name, Control control) {
        VBox box = new VBox(5);
        box.getChildren().addAll(label(name, 10, FontWeight.NORMAL, TEXT), control);
        return box;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(31);
        field.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:0 10;-fx-font-size:11px;");
    }

    private void styleCombo(ComboBox<String> combo) {
        combo.setPrefHeight(31);
        combo.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:11px;");
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setTextFill(Color.web(color));
        return l;
    }
}