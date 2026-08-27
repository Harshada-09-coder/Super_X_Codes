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

public class CreateEventC {

    private Scene scene;
    private String uid;
    private String idToken;


public class CreateEventC  {

        

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    public CreateEventC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }
    public Scene getCreateEventScene(
        Runnable backAction
        
) {

       

    public Scene getCreateEventScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setMinHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:0 0 1 0;");

        Label notification = label("♧", 20, FontWeight.NORMAL, NAVY);
        Label help = label("?", 13, FontWeight.BOLD, NAVY);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + NAVY + ";-fx-border-radius:50;-fx-background-radius:50;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        topBar.getChildren().addAll(topSpace, notification, help);

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label create = label("Create Event", 20, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox title = new VBox(2, create, step);
        title.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("ⓘ", "Event Details", true),
                sidebarItem("▣", "Requirements", false),
                sidebarItem("⌖", "Location", false),
                sidebarItem("▣", "Budget", false),
                sidebarItem("✦", "AI Planning", false),
                sidebarItem("▤", "Review", false)
        );

        sidebar.getChildren().addAll(brand, title, menu);

        VBox content = new VBox(12);
        content.setPadding(new Insets(22, 45, 18, 45));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label heading = label("Create Your Event", 26, FontWeight.NORMAL, NAVY);
        Label subtitle = label("Tell us a little about the event you are planning.", 14, FontWeight.NORMAL, GREY);

        VBox card = new VBox(15);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:white;-fx-background-radius:9;-fx-border-color:" + BORDER + ";-fx-border-radius:9;");

        Label categoryTitle = label("Event Category", 16, FontWeight.BOLD, TEXT);
        ToggleGroup group = new ToggleGroup();

        boolean isInformal = "Informal".equalsIgnoreCase(ReviewPageC.EventData.category);
        ToggleButton formal = categoryCard("▣", "Formal", "Conferences, Corporate, Galas", !isInformal, group);
        ToggleButton informal = categoryCard("♨", "Informal", "Parties, Retreats, Socials", isInformal, group);

        HBox categories = new HBox(14, formal, informal);
        HBox.setHgrow(formal, Priority.ALWAYS);
        HBox.setHgrow(informal, Priority.ALWAYS);

        Separator separator = new Separator();

        TextField eventName = new TextField(ReviewPageC.EventData.eventName != null ? ReviewPageC.EventData.eventName : "");
        eventName.setPromptText("e.g., Annual Tech Summit 2024");
        styleField(eventName);

        ComboBox<String> eventType = new ComboBox<>();
        eventType.getItems().addAll("Conference", "Corporate Event", "Birthday", "Family Event", "Educational Event", "Other");
        if (ReviewPageC.EventData.eventType != null && !ReviewPageC.EventData.eventType.isBlank()) {
            eventType.setValue(ReviewPageC.EventData.eventType);
        } else {
            eventType.setPromptText("Select a type...");
        }
        eventType.setMaxWidth(Double.MAX_VALUE);
        styleControl(eventType);

        TextField guests = new TextField(ReviewPageC.EventData.guests > 0 ? String.valueOf(ReviewPageC.EventData.guests) : "0");
        styleField(guests);

        HBox row1 = new HBox(17, formField("Event Type", eventType), formField("Expected Guests", guests));
        HBox.setHgrow(row1.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row1.getChildren().get(1), Priority.ALWAYS);

        DatePicker date = new DatePicker();
        date.setPromptText("dd-mm-yyyy");
        if (ReviewPageC.EventData.eventDate != null && !ReviewPageC.EventData.eventDate.isBlank()) {
            try {
                date.setValue(java.time.LocalDate.parse(ReviewPageC.EventData.eventDate));
            } catch (Exception ignored) {}
        }
        date.setMaxWidth(Double.MAX_VALUE);
        styleControl(date);

        TextField time = new TextField(ReviewPageC.EventData.time != null ? ReviewPageC.EventData.time : "");
        time.setPromptText("e.g., 10:00 AM");
        styleField(time);

        HBox row2 = new HBox(17, formField("Date", date), formField("Time", time));
        HBox.setHgrow(row2.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row2.getChildren().get(1), Priority.ALWAYS);

        card.getChildren().addAll(categoryTitle, categories, separator, formField("Event Name", eventName), row1, row2);
        content.getChildren().addAll(heading, subtitle, card);

        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("© 2026 EventXpert", 14, FontWeight.NORMAL, GREY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("← Back");
        back.setPrefSize(82, 38);
        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");
        back.setOnAction(e -> {
            NewCustomerDashboard dash = new NewCustomerDashboard(uid, idToken);
            LandingPage.landingPageStage.setScene(dash.getnewCustomerScene(null));
        });

        back.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-border-color:" + BLUE + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );

back.setOnAction(e -> {
    System.out.println("Back to Dashboard");
    backAction.run();
});     

        Button next = new Button("Next");
        next.setPrefSize(82, 38);
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setOnAction(e -> {
            if (eventName.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter an Event Name.");
                alert.showAndWait();
                return;
            }

            ReviewPageC.EventData.category = formal.isSelected() ? "Formal" : "Informal";
            ReviewPageC.EventData.eventName = eventName.getText().trim();
            ReviewPageC.EventData.eventType = eventType.getValue() != null ? eventType.getValue() : "Other";
            try {
                ReviewPageC.EventData.guests = Integer.parseInt(guests.getText().trim());
            } catch (Exception ex) {
                ReviewPageC.EventData.guests = 0;
            }
            ReviewPageC.EventData.eventDate = date.getValue() != null ? date.getValue().toString() : "";
            ReviewPageC.EventData.time = time.getText().trim();

            RequirementsC reqView = new RequirementsC(uid, idToken);
            LandingPage.landingPageStage.setScene(reqView.getRequirementsScene());
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        next.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );
next.setOnAction(e -> {

    RequirementsC requirementsPage =
            new RequirementsC();

    LandingPage.landingPageStage.setScene(
            requirementsPage.getRequirementsScene(

                    () -> {

                        CreateEventC createEventPage =
                                new CreateEventC();

                        LandingPage.landingPageStage.setScene(
                                createEventPage.getCreateEventScene(
                                        backAction
                                )
                        );

                    },

                    null
            )
    );

});        

        footer.getChildren().addAll(
                copyright,
                footerSpace,
                back,
                next
        );

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

       

        Scene scene =
                new Scene(
                        root,
                        1365,
                        850
                );

        return scene;
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setMinHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 18, FontWeight.NORMAL, "white"), label(text, 14, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private ToggleButton categoryCard(String icon, String title, String description, boolean selected, ToggleGroup group) {
        ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setSelected(selected);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(130);
        button.setMinHeight(130);

        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        Label iconLabel = label(icon, 22, FontWeight.NORMAL, BLUE);
        Label titleLabel = label(title, 14, FontWeight.BOLD, BLUE);
        Label descriptionLabel = label(description, 12, FontWeight.NORMAL, TEXT);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setAlignment(Pos.CENTER);

        Label selectedLabel = label("Selected", 11, FontWeight.BOLD, BLUE);
        selectedLabel.setStyle("-fx-background-color:#C6E9FF;-fx-background-radius:12;-fx-padding:4 8;");

        box.getChildren().addAll(iconLabel, titleLabel, descriptionLabel);
        if (selected) box.getChildren().add(selectedLabel);

        button.setGraphic(box);
        updateCategory(button, selected);

        button.selectedProperty().addListener((obs, oldValue, newValue) -> {
            updateCategory(button, newValue);
            if (newValue) {
                if (!box.getChildren().contains(selectedLabel)) box.getChildren().add(selectedLabel);
            } else {
                box.getChildren().remove(selectedLabel);
            }
        });
        return button;
    }

    private void updateCategory(ToggleButton button, boolean selected) {
        button.setStyle(selected ? "-fx-background-color:#F8FAFC;-fx-border-color:" + BLUE + ";-fx-border-width:1.5;-fx-border-radius:7;-fx-background-radius:7;" : "-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:7;-fx-background-radius:7;");
    }

    private VBox formField(String name, Control control) {
        VBox box = new VBox(5);
        box.getChildren().addAll(label(name, 12, FontWeight.NORMAL, TEXT), control);
        return box;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(36);
        field.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:0 12;-fx-font-size:11px;");
    }

    private void styleControl(Control control) {
        control.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:11px;");
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }

    

   
}