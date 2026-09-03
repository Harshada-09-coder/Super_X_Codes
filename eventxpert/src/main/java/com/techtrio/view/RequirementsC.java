package com.techtrio.view;

import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RequirementsC {

    private Scene scene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    // 8 Core Service Card holders
    private ServiceCardBox cardDecoration;
    private ServiceCardBox cardCatering;
    private ServiceCardBox cardPhotography;
    private ServiceCardBox cardSoundSystem;
    private ServiceCardBox cardAnchor;
    private ServiceCardBox cardRentalEquipment;
    private ServiceCardBox cardInvitationPrinting;
    private ServiceCardBox cardCakeDessert;

    public RequirementsC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getRequirementsScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 10, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label create = label("Create Event", 20, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox title = new VBox(2, create, step);
        title.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", false),
                sidebarItem("[2]", "Requirements", true),
                sidebarItem("[3]", "Venue", false),
                sidebarItem("[4]", "Budget", false),
                sidebarItem("[5]", "Guest", false),
                sidebarItem("[6]", "AI Planning", false),
                sidebarItem("[7]", "Review", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brand, title, menu, sideSpace);

        // TOP BAR
        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:0 0 1 0;");

        Label topLogo = label("EventXpert", 14, FontWeight.BOLD, NAVY);
        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Label bell = label("Notifications", 11, FontWeight.NORMAL, NAVY);
        Label help = label("?", 13, FontWeight.BOLD, NAVY);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + NAVY + ";-fx-border-radius:50;-fx-background-radius:50;");

        topBar.getChildren().addAll(topLogo, topSpace, bell, help);

        // CONTENT
        VBox content = new VBox(14);
        content.setPadding(new Insets(20, 35, 18, 35));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label stepText = label("STEP 2 OF 7", 10, FontWeight.BOLD, BLUE);
        Label heading = label("What do you need for your event?", 25, FontWeight.BOLD, NAVY);
        Label description = label("Select the core services and logistical requirements necessary for a successful execution.", 12, FontWeight.NORMAL, GREY);

        // CORE SERVICES CONTAINER
        VBox servicesCard = new VBox(14);
        servicesCard.setPadding(new Insets(18));
        servicesCard.setStyle("-fx-background-color:white;-fx-background-radius:9;-fx-border-color:" + BORDER + ";-fx-border-radius:9;");

        Label servicesTitle = label("Core Services", 16, FontWeight.BOLD, TEXT);

        String reqs = ReviewPageC.EventData.requirements != null ? ReviewPageC.EventData.requirements : "";

        // 8 Service Cards with Ikonli FontIcon
        cardDecoration = new ServiceCardBox("Decoration", "fas-archway", "#F3E8FF", "#7E22CE", reqs.contains("Decoration"));
        cardCatering = new ServiceCardBox("Catering", "fas-concierge-bell", "#FEF3C7", "#D97706", reqs.contains("Catering"));
        cardPhotography = new ServiceCardBox("Photography", "fas-camera", "#E0F2FE", "#0284C7", reqs.contains("Photography"));
        cardSoundSystem = new ServiceCardBox("Sound System", "fas-volume-up", "#FEE2E2", "#DC2626", reqs.contains("Sound System"));

        cardAnchor = new ServiceCardBox("Anchor", "fas-microphone", "#DCFCE7", "#16A34A", reqs.contains("Anchor"));
        cardRentalEquipment = new ServiceCardBox("Rental Equipment", "fas-toolbox", "#E0E7FF", "#4338CA", reqs.contains("Rental Equipment"));
        cardInvitationPrinting = new ServiceCardBox("Invitation & Printing", "fas-envelope-open-text", "#FCE7F3", "#DB2777", reqs.contains("Invitation & Printing"));
        cardCakeDessert = new ServiceCardBox("Cake & Dessert Services", "fas-birthday-cake", "#FFEDD5", "#EA580C", reqs.contains("Cake & Dessert Services"));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        grid.add(cardDecoration.getCard(), 0, 0);
        grid.add(cardCatering.getCard(), 1, 0);
        grid.add(cardPhotography.getCard(), 2, 0);
        grid.add(cardSoundSystem.getCard(), 3, 0);

        grid.add(cardAnchor.getCard(), 0, 1);
        grid.add(cardRentalEquipment.getCard(), 1, 1);
        grid.add(cardInvitationPrinting.getCard(), 2, 1);
        grid.add(cardCakeDessert.getCard(), 3, 1);

        servicesCard.getChildren().addAll(servicesTitle, grid);

        // ADDITIONAL REQUIREMENTS CONTAINER
        VBox additionalCard = new VBox(8);
        additionalCard.setPadding(new Insets(16));
        additionalCard.setStyle("-fx-background-color:white;-fx-background-radius:9;-fx-border-color:" + BORDER + ";-fx-border-radius:9;");

        Label additionalTitle = label("Additional Requirements", 15, FontWeight.BOLD, TEXT);
        Label additionalDescription = label("Specify any unique logistical needs, accessibility requirements, or custom requests.", 11, FontWeight.NORMAL, BLUE);
        Label customLabel = label("Custom Notes", 11, FontWeight.BOLD, TEXT);

        TextArea notes = new TextArea(ReviewPageC.EventData.notes != null ? ReviewPageC.EventData.notes : "");
        notes.setPromptText("Tell us anything else you need for your event...");
        notes.setPrefRowCount(3);
        notes.setWrapText(true);
        notes.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:12px;-fx-padding:8;");

        additionalCard.getChildren().addAll(additionalTitle, additionalDescription, customLabel, notes);

        content.getChildren().addAll(stepText, heading, description, servicesCard, additionalCard);

        // FOOTER
        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.NORMAL, GREY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            CreateEventC createView = new CreateEventC(uid, idToken);
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene());
        });

        next.setOnAction(e -> {
            StringBuilder selectedReqs = new StringBuilder();
            if (cardDecoration.isSelected()) selectedReqs.append("Decoration, ");
            if (cardCatering.isSelected()) selectedReqs.append("Catering, ");
            if (cardPhotography.isSelected()) selectedReqs.append("Photography, ");
            if (cardSoundSystem.isSelected()) selectedReqs.append("Sound System, ");
            if (cardAnchor.isSelected()) selectedReqs.append("Anchor, ");
            if (cardRentalEquipment.isSelected()) selectedReqs.append("Rental Equipment, ");
            if (cardInvitationPrinting.isSelected()) selectedReqs.append("Invitation & Printing, ");
            if (cardCakeDessert.isSelected()) selectedReqs.append("Cake & Dessert Services, ");

            String result = selectedReqs.toString();
            if (result.endsWith(", ")) {
                result = result.substring(0, result.length() - 2);
            }

            ReviewPageC.EventData.requirements = result;
            ReviewPageC.EventData.notes = notes.getText().trim();

            LocationC locationPage = new LocationC(uid, idToken);
            LandingPage.landingPageStage.setScene(locationPage.getLocationScene());
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox();
        rightSide.getChildren().addAll(topBar, content, footer);
        VBox.setVgrow(content, Priority.ALWAYS);

        HBox fullPage = new HBox();
        fullPage.getChildren().addAll(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        root.setCenter(fullPage);
        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private static class ServiceCardBox {
        private final VBox card = new VBox(6);
        private final CheckBox checkBox = new CheckBox();
        private final String title;

        public ServiceCardBox(String title, String iconLiteral, String circleBgColor, String iconColor, boolean initialSelected) {
            this.title = title;
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(10, 10, 12, 10));
            card.setPrefHeight(135);
            card.setMinHeight(135);
            card.setCursor(javafx.scene.Cursor.HAND);

            // Checkbox Header
            HBox topRow = new HBox();
            topRow.setAlignment(Pos.CENTER_RIGHT);
            topRow.setPadding(new Insets(0, 4, 0, 0));
            checkBox.setSelected(initialSelected);
            topRow.getChildren().add(checkBox);

            // Circular Icon Badge
            StackPane iconCircle = new StackPane();
            Circle circle = new Circle(26, Color.web(circleBgColor));
            FontIcon fontIcon = new FontIcon(iconLiteral);
            fontIcon.setIconSize(24);
            fontIcon.setIconColor(Color.web(iconColor));
            iconCircle.getChildren().addAll(circle, fontIcon);

            // Service Title
            Label titleLabel = new Label(title);
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            titleLabel.setTextFill(Color.web(TEXT));
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            titleLabel.setWrapText(true);

            card.getChildren().addAll(topRow, iconCircle, titleLabel);

            updateStyle();

            card.setOnMouseClicked(e -> {
                checkBox.setSelected(!checkBox.isSelected());
                updateStyle();
            });

            checkBox.setOnAction(e -> updateStyle());
        }

        private void updateStyle() {
            if (checkBox.isSelected()) {
                card.setStyle("-fx-background-color:#F0F6FE;-fx-border-color:" + BLUE + ";-fx-border-width:1.5;-fx-border-radius:8;-fx-background-radius:8;");
            } else {
                card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");
            }
        }

        public boolean isSelected() {
            return checkBox.isSelected();
        }

        public VBox getCard() {
            return card;
        }
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setMinHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");

        item.getChildren().addAll(
                label(icon, 13, FontWeight.NORMAL, "white"),
                label(text, 13, FontWeight.BOLD, active ? "white" : "#B7C4D8")
        );
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setTextFill(Color.web(color));
        return l;
    }
}