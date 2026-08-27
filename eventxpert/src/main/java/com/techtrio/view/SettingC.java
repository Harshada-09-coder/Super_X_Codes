
package com.techtrio.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SettingC  {

    // ==============================
    // COLOR PALETTE
    // ==============================

    private static final String NAVY = "#082E6B";
    private static final String BLUE = "#174A88";
    private static final String LIGHT_BLUE = "#C7E7FA";
    private static final String LIGHT = "#F5F8FC";
    private static final String TEXT = "#082E6B";
    private static final String GRAY = "#526071";
    private static final String BORDER = "#DCE3EC";
    private static final String WHITE = "#FFFFFF";

    public Scene getSettingScene(Runnable backAction) {

        // ==========================================
        // LEFT SIDEBAR
        // ==========================================

        VBox sidebar = new VBox(18);

        sidebar.setPrefWidth(285);
        sidebar.setPadding(
                new Insets(28, 0, 20, 0)
        );

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        // Profile Circle

        Circle profileCircle = new Circle(33);
        profileCircle.setFill(
                Color.web("#E7E7E7")
        );

        Label initial = new Label("S");

        initial.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        25
                )
        );

        initial.setTextFill(
                Color.web(NAVY)
        );

        StackPane profile =
                new StackPane(
                        profileCircle,
                        initial
                );

        // Logo

        Label logo =
                new Label("EventXpert");

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        29
                )
        );

        logo.setTextFill(Color.WHITE);

        // Dashboard Text

        Label dashboardText =
                new Label("Customer Dashboard");

        dashboardText.setFont(
                Font.font("Arial", 17)
        );

        dashboardText.setTextFill(
                Color.web("#B8D0EE")
        );

        VBox brand =
                new VBox(
                        5,
                        profile,
                        logo,
                        dashboardText
                );

        brand.setAlignment(
                Pos.CENTER
        );

        brand.setPadding(
                new Insets(0, 0, 20, 0)
        );

        sidebar.getChildren().add(brand);

        // Main Navigation
        // My Events आणि Calendar intentionally removed

        sidebar.getChildren().addAll(

                navItem(
                        "▦",
                        "Dashboard",
                        false
                ),

                navItem(
                        "⊕",
                        "Create Event",
                        false
                ),

                navItem(
                        "♧",
                        "Notifications",
                        false
                )
        );

        // Space

        Region sideSpace =
                new Region();

        VBox.setVgrow(
                sideSpace,
                Priority.ALWAYS
        );

        // Bottom Navigation

        sidebar.getChildren().addAll(

                navItem(
                        "♙",
                        "Profile",
                        false
                ),

                navItem(
                        "⚙",
                        "Settings",
                        true
                ),

                navItem(
                        "?",
                        "Support",
                        false
                )
        );

        // ==========================================
        // TOP BAR
        // ==========================================

        HBox topBar =
                new HBox(18);

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(14, 25, 14, 25)
        );

        topBar.setPrefHeight(72);

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E0E5EC;" +
                "-fx-border-width:0 0 1 0;"
        );

        Region topSpace =
                new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        // Search



        // Notification

        Label notification =
                new Label("♧");

        notification.setFont(
                Font.font(
                        "Arial",
                        22
                )
        );

        notification.setTextFill(
                Color.web(GRAY)
        );

        // Top Profile

        Circle topProfile =
                new Circle(20);

        topProfile.setFill(
                Color.web("#D7EAF8")
        );

        Label topInitial =
                new Label("S");

        topInitial.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        topInitial.setTextFill(
                Color.web(NAVY)
        );

        StackPane topUser =
                new StackPane(
                        topProfile,
                        topInitial
                );

        // Create Event button removed

        topBar.getChildren().addAll(
                topSpace,
                notification,
                topUser
        );

        // ==========================================
        // MAIN CONTENT
        // ==========================================

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(
                        25,
                        32,
                        35,
                        32
                )
        );

        content.setStyle(
                "-fx-background-color:" + LIGHT + ";"
        );

        // Page Heading

        Label heading =
                new Label("Settings");

        heading.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        30
                )
        );

        heading.setTextFill(
                Color.web(TEXT)
        );

        Label subtitle =
                new Label(
                        "Manage your account, preferences and notification settings."
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        15
                )
        );

        subtitle.setTextFill(
                Color.web(GRAY)
        );

        VBox headingBox =
                new VBox(
                        3,
                        heading,
                        subtitle
                );

        // ==========================================
        // SETTINGS LAYOUT
        // ==========================================

        HBox settingsArea =
                new HBox(20);

        // ==========================================
        // LEFT SETTINGS MENU
        // ==========================================

        VBox settingsMenu =
                new VBox(5);

        settingsMenu.setPrefWidth(245);

        settingsMenu.setPadding(
                new Insets(18)
        );

        settingsMenu.setStyle(
                cardStyle()
        );

        Label menuTitle =
                new Label("Settings");

        menuTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        menuTitle.setTextFill(
                Color.web(TEXT)
        );

        Button profileBtn =
                settingButton(
                        "👤  Profile",
                        true
                );

        Button accountBtn =
                settingButton(
                        "▣  Account",
                        false
                );

        Button notificationBtn =
                settingButton(
                        "♧  Notifications",
                        false
                );

        Button preferenceBtn =
                settingButton(
                        "⚙  Preferences",
                        false
                );

        Button privacyBtn =
                settingButton(
                        "🔒  Privacy",
                        false
                );

        settingsMenu.getChildren().addAll(
                menuTitle,
                new Separator(),
                profileBtn,
                accountBtn,
                notificationBtn,
                preferenceBtn,
                privacyBtn
        );

        // ==========================================
        // RIGHT SETTINGS CARD
        // ==========================================

        VBox settingsCard =
                new VBox(18);

        settingsCard.setPadding(
                new Insets(25)
        );

        settingsCard.setStyle(
                cardStyle()
        );

        HBox.setHgrow(
                settingsCard,
                Priority.ALWAYS
        );

        // Profile Heading

        Label profileTitle =
                new Label(
                        "Profile Information"
                );

        profileTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        21
                )
        );

        profileTitle.setTextFill(
                Color.web(TEXT)
        );

        Label profileDescription =
                new Label(
                        "Update your personal information and account details."
                );

        profileDescription.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        profileDescription.setTextFill(
                Color.web(GRAY)
        );

        VBox profileHeading =
                new VBox(
                        3,
                        profileTitle,
                        profileDescription
                );

        // ==========================================
        // PROFILE SECTION
        // ==========================================

        HBox profileSection =
                new HBox(20);

        profileSection.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle largeProfile =
                new Circle(42);

        largeProfile.setFill(
                Color.web("#D7EAF8")
        );

        Label largeInitial =
                new Label("S");

        largeInitial.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        30
                )
        );

        largeInitial.setTextFill(
                Color.web(NAVY)
        );

        StackPane largeProfilePane =
                new StackPane(
                        largeProfile,
                        largeInitial
                );

        VBox profileInfo =
                new VBox(7);

        Label profileName =
                new Label("Sarah");

        profileName.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        17
                )
        );

        profileName.setTextFill(
                Color.web(TEXT)
        );

        Label profileEmail =
                new Label(
                        "sarah@example.com"
                );

        profileEmail.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        profileEmail.setTextFill(
                Color.web(GRAY)
        );

        Button changePhoto =
                new Button("Change Photo");

        changePhoto.setStyle(
                secondaryButtonStyle()
        );

        profileInfo.getChildren().addAll(
                profileName,
                profileEmail,
                changePhoto
        );

        profileSection.getChildren().addAll(
                largeProfilePane,
                profileInfo
        );

        // ==========================================
        // FORM
        // ==========================================

        GridPane form =
                new GridPane();

        form.setHgap(18);
        form.setVgap(15);

        TextField fullName =
                formInput("Sarah");

        TextField email =
                formInput("sarah@example.com");

        TextField phone =
                formInput("+91 98765 43210");

        TextField location =
                formInput("Pune, India");

        form.add(
                fieldBox(
                        "Full Name",
                        fullName
                ),
                0,
                0
        );

        form.add(
                fieldBox(
                        "Email Address",
                        email
                ),
                1,
                0
        );

        form.add(
                fieldBox(
                        "Phone Number",
                        phone
                ),
                0,
                1
        );

        form.add(
                fieldBox(
                        "Location",
                        location
                ),
                1,
                1
        );

        // ==========================================
        // NOTIFICATION SECTION
        // ==========================================

        VBox notificationSection =
                new VBox(10);

        Label notificationTitle =
                new Label(
                        "Notification Settings"
                );

        notificationTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        notificationTitle.setTextFill(
                Color.web(TEXT)
        );

        CheckBox eventNotification =
                new CheckBox(
                        "Receive notifications about my events"
                );

        eventNotification.setSelected(true);

        CheckBox reminderNotification =
                new CheckBox(
                        "Receive event reminders"
                );

        reminderNotification.setSelected(true);

        CheckBox promotionalNotification =
                new CheckBox(
                        "Receive EventXpert updates and offers"
                );

        notificationSection.getChildren().addAll(
                notificationTitle,
                eventNotification,
                reminderNotification,
                promotionalNotification
        );

        // ==========================================
        // BUTTONS
        // ==========================================

        HBox buttonRow =
                new HBox(12);

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

          

        Button backButton =
        new Button("Back");

backButton.setPrefHeight(40);

backButton.setPrefWidth(85);

backButton.setStyle(
        secondaryButtonStyle()
);

// BACK → ACTIVE CUSTOMER DASHBOARD

backButton.setOnAction(e -> {

    if (backAction != null) {
        backAction.run();
    }
});

buttonRow.getChildren().addAll(
        backButton
);

        // ==========================================
        // ADD EVERYTHING TO SETTINGS CARD
        // ==========================================

        settingsCard.getChildren().addAll(
                profileHeading,
                new Separator(),
                profileSection,
                new Separator(),
                form,
                new Separator(),
                notificationSection,
                new Separator(),
                buttonRow
        );

        settingsArea.getChildren().addAll(
                settingsMenu,
                settingsCard
        );

        // ==========================================
        // ADD CONTENT
        // ==========================================

        content.getChildren().addAll(
                headingBox,
                settingsArea
        );

        // ==========================================
        // MAIN AREA
        // ==========================================

        BorderPane mainArea =
                new BorderPane();

        mainArea.setTop(topBar);
        mainArea.setCenter(content);

        // ==========================================
        // ROOT
        // ==========================================

        HBox root =
                new HBox(
                        sidebar,
                        mainArea
                );

        HBox.setHgrow(
                mainArea,
                Priority.ALWAYS
        );

        // ==========================================
        // SCENE
        // ==========================================

        Scene scene =
                new Scene(
                        root,
                        1500,
                        900
                );

      return scene;
    }

    // ==========================================
    // SIDEBAR ITEM
    // ==========================================

    private HBox navItem(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font(
                        "Arial",
                        17
                )
        );

        iconLabel.setTextFill(
                Color.WHITE
        );

        Label textLabel =
                new Label(text);

        textLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        textLabel.setTextFill(
                active
                        ? Color.WHITE
                        : Color.web("#B8D0EE")
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

        item.setPadding(
                new Insets(
                        12,
                        25,
                        12,
                        35
                )
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" +
                    BLUE + ";"
            );
        }

        return item;
    }

    // ==========================================
    // SETTINGS BUTTON
    // ==========================================

    private Button settingButton(
            String text,
            boolean active
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        if (active) {

            button.setStyle(
                    "-fx-background-color:" +
                    LIGHT_BLUE + ";" +
                    "-fx-text-fill:" +
                    NAVY + ";" +
                    "-fx-font-weight:bold;" +
                    "-fx-font-size:14;" +
                    "-fx-background-radius:7;" +
                    "-fx-padding:11 14;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color:transparent;" +
                    "-fx-text-fill:" +
                    TEXT + ";" +
                    "-fx-font-size:14;" +
                    "-fx-padding:11 14;"
            );
        }

        return button;
    }

    // ==========================================
    // FORM INPUT
    // ==========================================

    private TextField formInput(
            String text
    ) {

        TextField field =
                new TextField(text);

        field.setPrefHeight(40);

        field.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" +
                BORDER + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-padding:0 12;" +
                "-fx-font-size:13;"
        );

        return field;
    }

    // ==========================================
    // FIELD BOX
    // ==========================================

    private VBox fieldBox(
            String title,
            Control control
    ) {

        VBox box =
                new VBox(6);

        Label label =
                new Label(title);

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        label.setTextFill(
                Color.web(GRAY)
        );

        box.getChildren().addAll(
                label,
                control
        );

        GridPane.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }

    // ==========================================
    // CARD STYLE
    // ==========================================

    private String cardStyle() {

        return
                "-fx-background-color:white;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:" +
                BORDER + ";" +
                "-fx-border-radius:10;" +
                "-fx-effect:" +
                "dropshadow(gaussian," +
                "rgba(0,0,0,0.08)," +
                "5,0,0,2);";
    }

    // ==========================================
    // SECONDARY BUTTON
    // ==========================================

    private String secondaryButtonStyle() {

        return
                "-fx-background-color:white;" +
                "-fx-text-fill:" +
                NAVY + ";" +
                "-fx-border-color:" +
                BORDER + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:13;" +
                "-fx-padding:0 18;";
    }

    // ==========================================
    // MAIN METHOD
    // ==========================================

    
}


