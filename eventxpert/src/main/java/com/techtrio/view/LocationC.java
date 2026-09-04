package com.techtrio.view;

import com.techtrio.session.UserSession;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    private static final String LIGHT_BLUE = "#DFF3FF";

    public LocationC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getLocationScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
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
                sidebarItem("[2]", "Requirements", false),
                sidebarItem("[3]", "Venue", true),
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
        topBar.setMinHeight(60);
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

        Label stepText = label("STEP 3 OF 7", 10, FontWeight.BOLD, BLUE);
        Label heading = label("Where will your event take place?", 25, FontWeight.BOLD, NAVY);
        Label description = label("Provide your venue and location details for a smooth event experience.", 12, FontWeight.NORMAL, GREY);

        // LEFT COLUMN - VENUE DETAILS & INFORMATION
        VBox venueDetailsCard = new VBox(10);
        venueDetailsCard.setPadding(new Insets(16));
        venueDetailsCard.setStyle("-fx-background-color:white;-fx-background-radius:9;-fx-border-color:" + BORDER + ";-fx-border-radius:9;");

        Label vDetailsTitle = label("Venue Details", 15, FontWeight.BOLD, TEXT);

        ComboBox<String> venueType = new ComboBox<>();
        venueType.getItems().addAll("Hotel", "Banquet Hall", "Conference Center", "Outdoor Venue", "Restaurant", "Resort", "Other");
        if (ReviewPageC.EventData.venue != null && !ReviewPageC.EventData.venue.isBlank()) {
            venueType.setValue(ReviewPageC.EventData.venue);
        } else {
            venueType.setPromptText("Select venue type");
        }
        venueType.setMaxWidth(Double.MAX_VALUE);
        styleCombo(venueType);

        TextField city = new TextField(ReviewPageC.EventData.city != null ? ReviewPageC.EventData.city : "");
        city.setPromptText("e.g. Pune");
        styleField(city);

        TextField locality = new TextField(ReviewPageC.EventData.area != null ? ReviewPageC.EventData.area : "");
        locality.setPromptText("e.g. Baner");
        styleField(locality);

        HBox cityAreaRow = new HBox(12, formField("City", city), formField("Area / Locality", locality));
        HBox.setHgrow(cityAreaRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(cityAreaRow.getChildren().get(1), Priority.ALWAYS);

        // Venue Name आणि Full Address काढून टाकले आहेत
        venueDetailsCard.getChildren().addAll(vDetailsTitle, formField("Venue Type", venueType), cityAreaRow);

        // VENUE INFORMATION CARD
        VBox venueInfoCard = new VBox(10);
        venueInfoCard.setPadding(new Insets(16));
        venueInfoCard.setStyle("-fx-background-color:white;-fx-background-radius:9;-fx-border-color:" + BORDER + ";-fx-border-radius:9;");

        Label vInfoTitle = label("Venue Information", 15, FontWeight.BOLD, TEXT);

        TextField guestCap = new TextField(ReviewPageC.EventData.guests > 0 ? String.valueOf(ReviewPageC.EventData.guests) : "250");
        guestCap.setPromptText("e.g. 250");
        styleField(guestCap);

        TextField estCost = new TextField("₹ ");
        styleField(estCost);

        HBox capCostRow = new HBox(12, formField("Guest Capacity", guestCap), formField("Estimated Venue Cost", estCost));
        HBox.setHgrow(capCostRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(capCostRow.getChildren().get(1), Priority.ALWAYS);

        // Radio Rows
        HBox parkRow = radioOptionRow("Parking Available");
        HBox catRow = radioOptionRow("Catering Allowed");
        HBox decRow = radioOptionRow("Decoration Allowed");

        TextField addNotesField = new TextField();
        addNotesField.setPromptText("Any special information about the venue...");
        styleField(addNotesField);

        venueInfoCard.getChildren().addAll(vInfoTitle, capCostRow, parkRow, catRow, decRow, formField("Additional Notes", addNotesField));

        VBox leftColumn = new VBox(14, venueDetailsCard, venueInfoCard);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // RIGHT COLUMN - MAP & VENUE FINDER
        VBox rightColumn = new VBox(14);
        rightColumn.setPrefWidth(420);
        rightColumn.setMinWidth(400);

        StackPane mapPanel = createMapPanel(city, locality);

        // VENUE FINDER HELPER BOX
        HBox helpBox = new HBox(12);
        helpBox.setPrefHeight(68);
        helpBox.setAlignment(Pos.CENTER_LEFT);
        helpBox.setPadding(new Insets(10, 16, 10, 16));
        helpBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");

        Label helpIcon = label("🔍", 18, FontWeight.NORMAL, BLUE);
        StackPane iconCircle = new StackPane(helpIcon);
        iconCircle.setPrefSize(36, 36);
        iconCircle.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-background-radius:50;");

        Label helpTitle = label("Need help finding a venue?", 13, FontWeight.BOLD, TEXT);
        Label helpSub = label("Connect with our Venue Finder.", 11, FontWeight.NORMAL, GREY);
        VBox helpText = new VBox(1, helpTitle, helpSub);

        Region helpSpace = new Region();
        HBox.setHgrow(helpSpace, Priority.ALWAYS);
        Label arrow = label("→", 20, FontWeight.NORMAL, GREY);
        helpBox.getChildren().addAll(iconCircle, helpText, helpSpace, arrow);

        rightColumn.getChildren().addAll(mapPanel, helpBox);

        HBox mainRow = new HBox(20, leftColumn, rightColumn);
        content.getChildren().addAll(stepText, heading, description, mainRow);

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
            RequirementsC reqView = new RequirementsC(uid, idToken);
            LandingPage.landingPageStage.setScene(reqView.getRequirementsScene());
        });

        next.setOnAction(e -> {
            String enteredCity = city.getText().trim();
            String enteredArea = locality.getText().trim();
            String selectedVenue = venueType.getValue() != null ? venueType.getValue() : "Banquet Hall";

            ReviewPageC.EventData.city = enteredCity;
            ReviewPageC.EventData.area = enteredArea;
            ReviewPageC.EventData.venue = selectedVenue;

            // ऑटो-कॅल्क्युलेटेड Venue Name आणि Address पुढील Review Page साठी सेट राहतील
            if (!enteredCity.isEmpty() || !enteredArea.isEmpty()) {
                ReviewPageC.EventData.confirmedVenueName = selectedVenue + " (" + (!enteredArea.isEmpty() ? enteredArea : enteredCity) + ")";
                ReviewPageC.EventData.confirmedVenueAddress = (!enteredArea.isEmpty() ? enteredArea + ", " : "") + enteredCity;
            }

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
        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private HBox radioOptionRow(String title) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelTitle = label(title, 11, FontWeight.BOLD, TEXT);
        labelTitle.setPrefWidth(160);

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbYes = new RadioButton("Yes");
        rbYes.setToggleGroup(tg);
        rbYes.setSelected(true);
        rbYes.setStyle("-fx-font-size:11px;");

        RadioButton rbNo = new RadioButton("No");
        rbNo.setToggleGroup(tg);
        rbNo.setStyle("-fx-font-size:11px;");

        row.getChildren().addAll(labelTitle, rbYes, rbNo);
        return row;
    }

    private StackPane createMapPanel(TextField cityField, TextField areaField) {
        StackPane mapPanel = new StackPane();
        mapPanel.setPrefHeight(320);
        mapPanel.setStyle("-fx-background-color:#E9EEF4;-fx-background-radius:9;-fx-border-color:" + BORDER + ";-fx-border-radius:9;");

        try {
            Image mapImage = new Image(getClass().getResource("/assets/images/map img.jpg").toExternalForm());
            ImageView mapView = new ImageView(mapImage);
            mapView.setFitWidth(400);
            mapView.setFitHeight(300);
            mapView.setPreserveRatio(false);
            mapPanel.getChildren().add(mapView);
        } catch (Exception ex) {
            Label mapLabel = label("Map Location Preview", 16, FontWeight.BOLD, GREY);
            mapPanel.getChildren().add(mapLabel);
        }

        mapPanel.setCursor(javafx.scene.Cursor.HAND);
        mapPanel.setOnMouseClicked(e -> {
            String cityText = cityField.getText().trim();
            String areaText = areaField.getText().trim();
            String fullAddress = (!areaText.isEmpty() ? areaText + ", " : "") + (!cityText.isEmpty() ? cityText : "Pune");
            try {
                String encodedAddress = URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);
                String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(mapUrl));
                }
            } catch (Exception ignored) {}
        });

        return mapPanel;
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

    private VBox formField(String name, Control control) {
        VBox box = new VBox(4);
        box.getChildren().addAll(label(name, 11, FontWeight.BOLD, TEXT), control);
        return box;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(32);
        field.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:0 10;-fx-font-size:11px;");
    }

    private void styleCombo(ComboBox<String> combo) {
        combo.setPrefHeight(32);
        combo.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:11px;");
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setTextFill(Color.web(color));
        return l;
    }
}