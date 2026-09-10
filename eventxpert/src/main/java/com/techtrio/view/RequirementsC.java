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
        ReviewPageC.EventData.currentStep = 2;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(24, 20, 20, 22));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = label("EventXpert", 20, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 9, FontWeight.NORMAL, "#BDE3FF");
        brandTexts.getChildren().addAll(logo, logoSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        Label create = label("Create Event", 18, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox title = new VBox(2, create, step);
        title.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", "fas-info-circle", false),
                sidebarItem("[2]", "Requirements", "fas-list-ul", true),
                sidebarItem("[3]", "Venue & Location", "fas-map-marker-alt", false),
                sidebarItem("[4]", "Budget", "fas-wallet", false),
                sidebarItem("[5]", "Guest", "fas-users", false),
                sidebarItem("[6]", "AI Planning", "fas-magic", false),
                sidebarItem("[7]", "Review", "fas-check-circle", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandBox, title, menu, sideSpace);

        // CONTENT
        VBox content = new VBox(14);
        content.setPadding(new Insets(28, 45, 20, 45));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label stepText = label("STEP 2 OF 7", 10, FontWeight.BOLD, BLUE);
        Label heading = label("What do you need for your event?", 25, FontWeight.BOLD, NAVY);
        Label description = label("Select the core services and logistical requirements necessary for a successful execution.", 12, FontWeight.NORMAL, GREY);

        VBox servicesCard = new VBox(14);
        servicesCard.setPadding(new Insets(18));
        servicesCard.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-border-color:" + BORDER + ";-fx-border-radius:10;");

        Label servicesTitle = label("Core Services", 15, FontWeight.BOLD, TEXT);

        String reqs = ReviewPageC.EventData.requirements != null ? ReviewPageC.EventData.requirements : "";

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

        VBox additionalCard = new VBox(8);
        additionalCard.setPadding(new Insets(16));
        additionalCard.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-border-color:" + BORDER + ";-fx-border-radius:10;");

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

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, GREY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            ReviewPageC.EventData.saveDraft(1);
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
            ReviewPageC.EventData.saveDraft(3);

            LocationC locationPage = new LocationC(uid, idToken);
            LandingPage.landingPageStage.setScene(locationPage.getLocationScene());
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox();
        rightSide.getChildren().addAll(content, footer);
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

        public ServiceCardBox(String title, String iconLiteral, String circleBgColor, String iconColor, boolean initialSelected) {
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(10, 10, 12, 10));
            card.setPrefHeight(135);
            card.setMinHeight(135);
            card.setCursor(javafx.scene.Cursor.HAND);

            HBox topRow = new HBox();
            topRow.setAlignment(Pos.CENTER_RIGHT);
            topRow.setPadding(new Insets(0, 4, 0, 0));
            checkBox.setSelected(initialSelected);
            topRow.getChildren().add(checkBox);

            StackPane iconCircle = new StackPane();
            Circle circle = new Circle(26, Color.web(circleBgColor));
            FontIcon fontIcon = new FontIcon(iconLiteral);
            fontIcon.setIconSize(24);
            fontIcon.setIconColor(Color.web(iconColor));
            iconCircle.getChildren().addAll(circle, fontIcon);

            Label titleLabel = new Label(title);
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            titleLabel.setStyle("-fx-text-fill:" + NAVY + " !important;");
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

    private HBox sidebarItem(String stepNum, String text, String iconLiteral, boolean active) {
        HBox item = new HBox(10);
        item.setPrefHeight(42);
        item.setMinHeight(42);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 16, 0, 22));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#B7C4D8"));

        Label textLbl = label(stepNum + "  " + text, 12, active ? FontWeight.BOLD : FontWeight.NORMAL, active ? NAVY : "#B7C4D8");

        if (active) {
            item.setStyle("-fx-background-color: #E0F2FE; -fx-background-radius: 7px;");
        } else {
            item.setStyle("-fx-background-color: transparent;");
        }

        item.getChildren().addAll(icon, textLbl);
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + " !important;");
        return l;
    }
}