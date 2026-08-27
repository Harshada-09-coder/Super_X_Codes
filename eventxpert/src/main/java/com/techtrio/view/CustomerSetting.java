
package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CustomerSetting extends Application {

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String TEXT = "#17233C";
    private static final String BORDER = "#D7DEE8";
    private static final String LIGHT_BLUE = "#BFE5FF";

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // =========================
        // LEFT SIDEBAR
        // =========================

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        // Logo
        VBox logoBox = new VBox(3);
        logoBox.setPadding(new Insets(25, 20, 25, 20));

        Label logo = styledLabel(
                "EventXpert",
                23,
                FontWeight.BOLD,
                "#FFFFFF"
        );

        Label panel = styledLabel(
                "CUSTOMER PANEL",
                9,
                FontWeight.NORMAL,
                "#B8C9E2"
        );

        logoBox.getChildren().addAll(
                logo,
                panel
        );

        // Menu
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(8, 12, 8, 12));

        HBox dashboard = menuItem(
                "▦",
                "Dashboard",
                false
        );

        HBox createEvent = menuItem(
                "⊕",
                "Create Event",
                false
        );

        HBox myEvents = menuItem(
                "▣",
                "My Events",
                false
        );

        HBox calendar = menuItem(
                "□",
                "Calendar",
                false
        );

        HBox notifications = menuItem(
                "♧",
                "Notifications",
                false
        );

        menu.getChildren().addAll(
                dashboard,
                createEvent,
                myEvents,
                calendar,
                notifications
        );

        Region sidebarSpace = new Region();
        VBox.setVgrow(
                sidebarSpace,
                Priority.ALWAYS
        );

        // Bottom Menu
        VBox bottomMenu = new VBox(5);
        bottomMenu.setPadding(
                new Insets(8, 12, 18, 12)
        );

        HBox profile = menuItem(
                "◉",
                "Profile",
                false
        );

        HBox settings = menuItem(
                "⚙",
                "Settings",
                true
        );

        HBox support = menuItem(
                "?",
                "Support",
                false
        );

        bottomMenu.getChildren().addAll(
                profile,
                settings,
                support
        );

        sidebar.getChildren().addAll(
                logoBox,
                menu,
                sidebarSpace,
                bottomMenu
        );

        // =========================
        // RIGHT AREA
        // =========================

        BorderPane rightArea = new BorderPane();

        // =========================
        // TOP BAR
        // =========================

        HBox topBar = new HBox(15);

        topBar.setPrefHeight(64);
        topBar.setMinHeight(64);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(10, 25, 10, 25)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#E0E6EF;" +
                "-fx-border-width:0 0 1 0;"
        );

        TextField search = new TextField();

        search.setPromptText(
                "Search"
        );

        search.setPrefWidth(330);
        search.setPrefHeight(36);

        search.setStyle(
                "-fx-background-color:#F7F9FC;" +
                "-fx-border-color:#D2DAE5;" +
                "-fx-border-radius:18;" +
                "-fx-background-radius:18;" +
                "-fx-padding:0 15;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:12px;"
        );

        Region topSpace = new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        Label notification = styledLabel(
                "♧",
                20,
                FontWeight.NORMAL,
                NAVY
        );

        Label user = styledLabel(
                "S",
                13,
                FontWeight.BOLD,
                NAVY
        );

        StackPane avatar = new StackPane();

        avatar.setPrefSize(
                36,
                36
        );

        avatar.setStyle(
                "-fx-background-color:#C9E8FA;" +
                "-fx-background-radius:50;"
        );

        avatar.getChildren().add(
                user
        );

        topBar.getChildren().addAll(
                search,
                topSpace,
                notification,
                avatar
        );

        // =========================
        // MAIN CONTENT
        // =========================

        VBox main = new VBox(18);

        main.setPadding(
                new Insets(25, 35, 25, 35)
        );

        main.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        // =========================
        // HEADER
        // =========================

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headerText = new VBox(5);

        Label title = styledLabel(
                "Settings",
                28,
                FontWeight.BOLD,
                NAVY
        );

        Label subtitle = styledLabel(
                "Manage your account preferences and settings.",
                13,
                FontWeight.NORMAL,
                "#667085"
        );

        headerText.getChildren().addAll(
                title,
                subtitle
        );

        Region headerSpace = new Region();

        HBox.setHgrow(
                headerSpace,
                Priority.ALWAYS
        );

        // Back Button
        Button backButton = new Button(
                "←  Back"
        );

        backButton.setPrefSize(
                95,
                36
        );

        backButton.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        header.getChildren().addAll(
                headerText,
                headerSpace,
                backButton
        );

        // =========================
        // SETTINGS CONTENT
        // =========================

        HBox content = new HBox(20);

        VBox leftColumn = new VBox(18);

        VBox rightColumn = new VBox(18);

        HBox.setHgrow(
                leftColumn,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                rightColumn,
                Priority.ALWAYS
        );

        // =========================
        // ACCOUNT SETTINGS
        // =========================

        VBox accountCard = createCard();

        accountCard.getChildren().addAll(

                cardTitle(
                        "Account Settings",
                        "Manage your account information."
                ),

                settingRow(
                        "Profile Information",
                        "Update your name, email and personal details.",
                        "Edit"
                ),

                separator(),

                settingRow(
                        "Password",
                        "Change your account password.",
                        "Change"
                )
        );

        // =========================
        // PRIVACY & SECURITY
        // =========================

        VBox securityCard = createCard();

        securityCard.getChildren().addAll(

                cardTitle(
                        "Privacy & Security",
                        "Control your account security and privacy."
                ),

                toggleRow(
                        "Two-Factor Authentication",
                        "Add an extra layer of security to your account.",
                        true
                ),

                separator(),

                toggleRow(
                        "Login Alerts",
                        "Receive notifications when a new login occurs.",
                        true
                ),

                separator(),

                settingRow(
                        "Active Sessions",
                        "Manage devices currently signed in to your account.",
                        "Manage"
                )
        );

        // =========================
        // NOTIFICATION SETTINGS
        // =========================

        VBox notificationCard = createCard();

        notificationCard.getChildren().addAll(

                cardTitle(
                        "Notifications",
                        "Choose how you want to receive notifications."
                ),

                toggleRow(
                        "Event Updates",
                        "Receive updates about your events.",
                        true
                ),

                separator(),

                toggleRow(
                        "Email Notifications",
                        "Receive important updates through email.",
                        true
                ),

                separator(),

                toggleRow(
                        "Promotional Notifications",
                        "Receive offers and EventXpert promotions.",
                        false
                )
        );

        // =========================
        // APPEARANCE
        // =========================

        VBox appearanceCard = createCard();

        appearanceCard.getChildren().addAll(

                cardTitle(
                        "Appearance",
                        "Customize how EventXpert looks."
                ),

                settingRow(
                        "Theme",
                        "Choose your preferred application theme.",
                        "Light"
                ),

                separator(),

                settingRow(
                        "Language",
                        "Select your preferred language.",
                        "English"
                )
        );

        // Add Cards
        leftColumn.getChildren().addAll(
                accountCard,
                securityCard
        );

        rightColumn.getChildren().addAll(
                notificationCard,
                appearanceCard
        );

        content.getChildren().addAll(
                leftColumn,
                rightColumn
        );

        // =========================
        // SAVE BUTTON
        // =========================

        HBox bottom = new HBox();

        bottom.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button saveButton = new Button(
                "Save Changes"
        );

        saveButton.setPrefSize(
                140,
                40
        );

        saveButton.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:7;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        bottom.getChildren().add(
                saveButton
        );

        main.getChildren().addAll(
                header,
                content,
                bottom
        );

        // =========================
        // ROOT
        // =========================

        rightArea.setTop(
                topBar
        );

        rightArea.setCenter(
                main
        );

        root.setLeft(
                sidebar
        );

        root.setCenter(
                rightArea
        );

        // =========================
        // SCENE
        // =========================

        Scene scene = new Scene(
                root,
                1365,
                800
        );

        stage.setTitle(
                "EventXpert - Customer Settings"
        );

        stage.setScene(
                scene
        );

        stage.setMinWidth(
                1100
        );

        stage.setMinHeight(
                700
        );

        stage.setMaximized(
                true
        );

        stage.show();
    }

    // ==========================================
    // CARD
    // ==========================================

    private VBox createCard() {

        VBox card = new VBox(14);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        VBox.setVgrow(
                card,
                Priority.ALWAYS
        );

        return card;
    }

    // ==========================================
    // CARD TITLE
    // ==========================================

    private VBox cardTitle(
            String title,
            String subtitle
    ) {

        VBox box = new VBox(4);

        Label titleLabel = styledLabel(
                title,
                18,
                FontWeight.BOLD,
                TEXT
        );

        Label subtitleLabel = styledLabel(
                subtitle,
                11,
                FontWeight.NORMAL,
                "#667085"
        );

        box.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        return box;
    }

    // ==========================================
    // SETTING ROW
    // ==========================================

    private HBox settingRow(
            String title,
            String description,
            String buttonText
    ) {

        HBox row = new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox text = new VBox(4);

        Label titleLabel = styledLabel(
                title,
                13,
                FontWeight.BOLD,
                TEXT
        );

        Label descriptionLabel = styledLabel(
                description,
                10,
                FontWeight.NORMAL,
                "#667085"
        );

        descriptionLabel.setWrapText(
                true
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Button button = new Button(
                buttonText
        );

        button.setPrefSize(
                80,
                30
        );

        button.setStyle(
                "-fx-background-color:#F3F6FA;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-family:Arial;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );

        row.getChildren().addAll(
                text,
                space,
                button
        );

        return row;
    }

    // ==========================================
    // TOGGLE ROW
    // ==========================================

    private HBox toggleRow(
            String title,
            String description,
            boolean selected
    ) {

        HBox row = new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox text = new VBox(4);

        Label titleLabel = styledLabel(
                title,
                13,
                FontWeight.BOLD,
                TEXT
        );

        Label descriptionLabel = styledLabel(
                description,
                10,
                FontWeight.NORMAL,
                "#667085"
        );

        descriptionLabel.setWrapText(
                true
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        CheckBox toggle = new CheckBox();

        toggle.setSelected(
                selected
        );

        toggle.setStyle(
                "-fx-font-size:12px;"
        );

        row.getChildren().addAll(
                text,
                space,
                toggle
        );

        return row;
    }

    // ==========================================
    // SEPARATOR
    // ==========================================

    private Separator separator() {

        Separator separator = new Separator();

        separator.setStyle(
                "-fx-background-color:#E5EAF0;"
        );

        return separator;
    }

    // ==========================================
    // SIDEBAR MENU
    // ==========================================

    private HBox menuItem(
            String icon,
            String text,
            boolean active
    ) {

        HBox item = new HBox(11);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPrefHeight(
                40
        );

        item.setPadding(
                new Insets(0, 11, 0, 13)
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" +
                    LIGHT_BLUE +
                    ";" +
                    "-fx-background-radius:7;"
            );
        }

        Label iconLabel = styledLabel(
                icon,
                16,
                FontWeight.NORMAL,
                active
                        ? NAVY
                        : "#D1D9E8"
        );

        Label textLabel = styledLabel(
                text,
                12,
                active
                        ? FontWeight.BOLD
                        : FontWeight.NORMAL,
                active
                        ? "#35556E"
                        : "#D1D9E8"
        );

        item.getChildren().addAll(
                iconLabel,
                textLabel
        );

        return item;
    }

    // ==========================================
    // LABEL
    // ==========================================

    private Label styledLabel(
            String text,
            double size,
            FontWeight weight,
            String color
    ) {

        Label label = new Label(
                text
        );

        label.setFont(
                Font.font(
                        "Arial",
                        weight,
                        size
                )
        );

        label.setTextFill(
                Color.web(color)
        );

        return label;
    }

    // ==========================================
    // MAIN
    // ==========================================

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}


