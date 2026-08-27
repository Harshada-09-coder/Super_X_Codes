package com.techtrio.view;

import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RequirementsC {

    private Scene scene;

    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String SELECTED = "#C7E5FA";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    public RequirementsC(String uid, String idToken) {

        this.uid = (uid != null && !uid.isBlank())
                ? uid
                : UserSession.getUid();

        this.idToken = (idToken != null && !idToken.isBlank())
                ? idToken
                : UserSession.getIdToken();
    }

    public Scene getRequirementsScene() {

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        // BRAND

        Label logo = label(
                "EventXpert",
                21,
                FontWeight.BOLD,
                "white"
        );

        Label logoSub = label(
                "EVENT MANAGEMENT",
                9,
                FontWeight.NORMAL,
                "#BDE3FF"
        );

        VBox brand = new VBox(
                3,
                logo,
                logoSub
        );

        brand.setPadding(
                new Insets(24, 20, 28, 25)
        );

        // TITLE

        Label create = label(
                "Create Event",
                19,
                FontWeight.BOLD,
                "white"
        );

        Label step = label(
                "Step-by-step setup",
                10,
                FontWeight.NORMAL,
                "#BDE3FF"
        );

        VBox title = new VBox(
                2,
                create,
                step
        );

        title.setPadding(
                new Insets(0, 20, 20, 25)
        );

        // MENU

        VBox menu = new VBox();

        menu.getChildren().addAll(

                sidebarItem(
                        "[1]",
                        "Event Details",
                        false
                ),

                sidebarItem(
                        "[2]",
                        "Requirements",
                        true
                ),

                sidebarItem(
                        "[3]",
                        "Location",
                        false
                ),

                sidebarItem(
                        "[4]",
                        "Budget",
                        false
                ),

                sidebarItem(
                        "[5]",
                        "AI Planning",
                        false
                ),

                sidebarItem(
                        "[6]",
                        "Review",
                        false
                )
        );

        sidebar.getChildren().addAll(
                brand,
                title,
                menu
        );

        // =====================================================
        // TOP BAR
        // =====================================================

        HBox topBar = new HBox();

        topBar.setPrefHeight(60);

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(0, 30, 0, 30)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E4E7EC;" +
                "-fx-border-width:0 0 1 0;"
        );

        Label topLogo = label(
                "EventXpert",
                14,
                FontWeight.BOLD,
                NAVY
        );

        Label bell = label(
                "Notifications",
                11,
                FontWeight.NORMAL,
                NAVY
        );

        Label help = label(
                "?",
                13,
                FontWeight.BOLD,
                NAVY
        );

        help.setMinSize(
                28,
                28
        );

        help.setAlignment(
                Pos.CENTER
        );

        help.setStyle(
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:50;" +
                "-fx-background-radius:50;"
        );

        Region topSpace = new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        topBar.getChildren().addAll(
                topLogo,
                topSpace,
                bell,
                help
        );

        // =====================================================
        // CONTENT
        // =====================================================

        VBox content = new VBox(12);

        content.setPadding(
                new Insets(25, 32, 18, 32)
        );

        content.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        // STEP

        Label stepText = label(
                "STEP 2 OF 6",
                9,
                FontWeight.BOLD,
                BLUE
        );

        // HEADING

        Label heading = label(
                "What do you need for your event?",
                25,
                FontWeight.NORMAL,
                "#050B16"
        );

        Label description = label(
                "Select the core services and logistical requirements necessary for a successful execution.",
                12,
                FontWeight.NORMAL,
                TEXT
        );

        // =====================================================
        // SERVICES CARD
        // =====================================================

        VBox servicesCard = new VBox(12);

        servicesCard.setPadding(
                new Insets(18)
        );

        servicesCard.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:7;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;"
        );

        Label servicesTitle = label(
                "Core Services",
                16,
                FontWeight.BOLD,
                TEXT
        );

        String reqs =
                ReviewPageC.EventData.requirements != null
                        ? ReviewPageC.EventData.requirements
                        : "";

        // SERVICE BUTTONS

        ToggleButton venue =
                serviceCard(
                        "Venue",
                        reqs.contains("Venue")
                );

        ToggleButton catering =
                serviceCard(
                        "Catering",
                        reqs.contains("Catering")
                );

        ToggleButton decoration =
                serviceCard(
                        "Decoration",
                        reqs.contains("Decoration")
                );

        ToggleButton photography =
                serviceCard(
                        "Photography",
                        reqs.contains("Photography")
                );

        ToggleButton sound =
                serviceCard(
                        "Sound System",
                        reqs.contains("Sound System")
                );

        ToggleButton seating =
                serviceCard(
                        "Seating",
                        reqs.contains("Seating")
                );

        ToggleButton other =
                serviceCard(
                        "Other",
                        reqs.contains("Other")
                );

        HBox row1 = new HBox(
                15,
                venue,
                catering,
                decoration,
                photography
        );

        HBox row2 = new HBox(
                15,
                sound,
                seating,
                other
        );

        ToggleButton[] buttons = {
                venue,
                catering,
                decoration,
                photography,
                sound,
                seating,
                other
        };

        for (ToggleButton button : buttons) {

            HBox.setHgrow(
                    button,
                    Priority.ALWAYS
            );
        }

        servicesCard.getChildren().addAll(
                servicesTitle,
                row1,
                row2
        );

        // =====================================================
        // ADDITIONAL REQUIREMENTS
        // =====================================================

        VBox additionalCard = new VBox(8);

        additionalCard.setPadding(
                new Insets(18)
        );

        additionalCard.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:7;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;"
        );

        Label additionalTitle = label(
                "Additional Requirements",
                16,
                FontWeight.BOLD,
                TEXT
        );

        Label additionalDescription = label(
                "Specify any unique logistical needs, accessibility requirements, or custom requests.",
                11,
                FontWeight.NORMAL,
                BLUE
        );

        Label customLabel = label(
                "Custom Notes",
                11,
                FontWeight.NORMAL,
                TEXT
        );

        TextArea notes = new TextArea(
                ReviewPageC.EventData.notes != null
                        ? ReviewPageC.EventData.notes
                        : ""
        );

        notes.setPromptText(
                "Tell us anything else you need for your event..."
        );

        notes.setPrefRowCount(4);

        notes.setWrapText(
                true
        );

        notes.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:11px;" +
                "-fx-padding:10;"
        );

        additionalCard.getChildren().addAll(
                additionalTitle,
                additionalDescription,
                customLabel,
                notes
        );

        // CONTENT ADD

        content.getChildren().addAll(
                stepText,
                heading,
                description,
                servicesCard,
                additionalCard
        );

        // =====================================================
        // FOOTER
        // =====================================================

        HBox footer = new HBox(15);

        footer.setPrefHeight(60);

        footer.setMinHeight(60);

        footer.setAlignment(
                Pos.CENTER_RIGHT
        );

        footer.setPadding(
                new Insets(0, 30, 0, 30)
        );

        footer.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E4E7EC;" +
                "-fx-border-width:1 0 0 0;"
        );

        Label copyright = label(
                "(C) 2026 EventXpert",
                10,
                FontWeight.NORMAL,
                GREY
        );

        Region footerSpace = new Region();

        HBox.setHgrow(
                footerSpace,
                Priority.ALWAYS
        );

        Button back = new Button(
                "Back"
        );

        Button next = new Button(
                "Next"
        );

        back.setPrefSize(
                82,
                38
        );

        next.setPrefSize(
                82,
                38
        );

        // BACK STYLE

        back.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-border-color:" + BLUE + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );

        // NEXT STYLE

        next.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );

        // =====================================================
        // BACK ACTION
        // =====================================================

        back.setOnAction(e -> {

            CreateEventC createView =
                    new CreateEventC(
                            uid,
                            idToken
                    );

            LandingPage.landingPageStage.setScene(
                    createView.getCreateEventScene()
            );
        });

        // =====================================================
        // NEXT ACTION
        // =====================================================

        next.setOnAction(e -> {

            StringBuilder selectedReqs =
                    new StringBuilder();

            if (venue.isSelected()) {
                selectedReqs.append("Venue, ");
            }

            if (catering.isSelected()) {
                selectedReqs.append("Catering, ");
            }

            if (decoration.isSelected()) {
                selectedReqs.append("Decoration, ");
            }

            if (photography.isSelected()) {
                selectedReqs.append("Photography, ");
            }

            if (sound.isSelected()) {
                selectedReqs.append("Sound System, ");
            }

            if (seating.isSelected()) {
                selectedReqs.append("Seating, ");
            }

            if (other.isSelected()) {
                selectedReqs.append("Other, ");
            }

            String result =
                    selectedReqs.toString();

            if (result.endsWith(", ")) {

                result =
                        result.substring(
                                0,
                                result.length() - 2
                        );
            }

            // SAVE REQUIREMENTS

            ReviewPageC.EventData.requirements =
                    result;

            // SAVE NOTES

            ReviewPageC.EventData.notes =
                    notes.getText().trim();

            // NEXT PAGE

            LocationC locationPage =
                    new LocationC(
                            uid,
                            idToken
                    );

            LandingPage.landingPageStage.setScene(
                    locationPage.getLocationScene()
            );
        });

        footer.getChildren().addAll(
                copyright,
                footerSpace,
                back,
                next
        );

        // =====================================================
        // RIGHT SIDE
        // =====================================================

        VBox rightSide = new VBox();

        rightSide.getChildren().addAll(
                topBar,
                content,
                footer
        );

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        // =====================================================
        // FULL PAGE
        // =====================================================

        HBox fullPage = new HBox();

        fullPage.getChildren().addAll(
                sidebar,
                rightSide
        );

        HBox.setHgrow(
                rightSide,
                Priority.ALWAYS
        );

        root.setCenter(
                fullPage
        );

        // =====================================================
        // SCENE
        // =====================================================

        scene = new Scene(
                root,
                1365,
                850
        );

        return scene;
    }

    // =========================================================
    // SIDEBAR ITEM
    // =========================================================

    private HBox sidebarItem(
            String icon,
            String text,
            boolean active
    ) {

        HBox item = new HBox(12);

        item.setPrefHeight(44);

        item.setMinHeight(44);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        25
                )
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" + BLUE + ";" +
                    "-fx-border-color:#9DD8FF;" +
                    "-fx-border-width:0 0 0 3;"
            );

        } else {

            item.setStyle(
                    "-fx-background-color:transparent;"
            );
        }

        item.getChildren().addAll(

                label(
                        icon,
                        12,
                        FontWeight.NORMAL,
                        "white"
                ),

                label(
                        text,
                        11,
                        FontWeight.BOLD,
                        active
                                ? "white"
                                : "#B7C4D8"
                )
        );

        return item;
    }

    // =========================================================
    // SERVICE BUTTON
    // =========================================================

    private ToggleButton serviceCard(
            String title,
            boolean selected
    ) {

        ToggleButton button =
                new ToggleButton();

        button.setText(
                title
        );

        button.setSelected(
                selected
        );

        button.setPrefHeight(
                92
        );

        button.setMinHeight(
                92
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        applyServiceStyle(
                button,
                selected
        );

        button.selectedProperty().addListener(
                (obs, oldValue, newValue) -> {

                    applyServiceStyle(
                            button,
                            newValue
                    );
                }
        );

        return button;
    }

    // =========================================================
    // SERVICE STYLE
    // =========================================================

    private void applyServiceStyle(
            ToggleButton button,
            boolean selected
    ) {

        if (selected) {

            button.setStyle(
                    "-fx-background-color:" + SELECTED + ";" +
                    "-fx-border-color:" + BLUE + ";" +
                    "-fx-border-width:1.5;" +
                    "-fx-border-radius:6;" +
                    "-fx-background-radius:6;" +
                    "-fx-font-size:13px;" +
                    "-fx-font-weight:bold;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color:white;" +
                    "-fx-border-color:" + BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-border-radius:6;" +
                    "-fx-background-radius:6;" +
                    "-fx-font-size:13px;"
            );
        }
    }

    // =========================================================
    // LABEL
    // =========================================================

    private Label label(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label l =
                new Label(text);

        l.setFont(
                Font.font(
                        "Arial",
                        weight,
                        size
                )
        );

        l.setTextFill(
                Color.web(color)
        );

        return l;
    }
}