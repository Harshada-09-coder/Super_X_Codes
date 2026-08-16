package com.superx.view;


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

public class ProfilePage extends Application {

    private final String BLUE = "#002653";
    private final String LIGHT_BLUE = "#0B3B78";
    private final String BG = "#F5F7FA";

    private TextField nameField;
    private TextField emailField;
    private TextField mobileField;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(300);
        sidebar.setMinWidth(300);
        sidebar.setMaxWidth(300);

        sidebar.setPadding(
                new Insets(25, 18, 20, 18)
        );

        sidebar.setStyle(
                "-fx-background-color:" + BLUE + ";"
        );

        

        Label logo =
                new Label("EventXpert");

        logo.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        32
                )
        );

        logo.setTextFill(Color.WHITE);

        Label admin =
                new Label("Customer Console");

        admin.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        16
                )
        );

        admin.setTextFill(
                Color.web("#B9C9DF")
        );

        VBox logoBox =
                new VBox(
                        3,
                        logo,
                        admin
                );

        

        Button createEvent =
                new Button("+   Create Event");

        createEvent.setPrefHeight(48);
        createEvent.setMaxWidth(
                Double.MAX_VALUE
        );

        createEvent.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        createEvent.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#16233A;" +
                "-fx-background-radius:9;"
        );

        VBox.setMargin(
                createEvent,
                new Insets(
                        35,
                        0,
                        28,
                        0
                )
        );

    

        VBox navigation =
                new VBox(6);

        navigation.getChildren().addAll(

                navigationItem(
                        "▦",
                        "Dashboard",
                        false
                ),

                navigationItem(
                        "▣",
                        "Event Requests",
                        false
                ),

                navigationItem(
                        "□",
                        "Events",
                        false
                ),

                navigationItem(
                        "▤",
                        "Calendar",
                        false
                ),

                navigationItem(
                        "♙",
                        "Customers",
                        true
                ),

                navigationItem(
                        "▣",
                        "Organizers",
                        false
                ),

                navigationItem(
                        "▥",
                        "Vendors",
                        false
                ),

                navigationItem(
                        "!",
                        "Conflict Detection",
                        false
                ),

                navigationItem(
                        "▤",
                        "Reports",
                        false
                )
        );

        

        Region sidebarSpacer =
                new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        

        VBox bottom =
                new VBox(6);

        bottom.getChildren().addAll(

                navigationItem(
                        "⚙",
                        "Settings",
                        false
                ),

                navigationItem(
                        "?",
                        "Help",
                        false
                )
        );

        sidebar.getChildren().addAll(
                logoBox,
                createEvent,
                navigation,
                sidebarSpacer,
                bottom
        );

        
        VBox rightSide =
                new VBox();

        HBox topBar =
                createTopBar();

        VBox mainContent =
                createProfileContent();

        VBox.setVgrow(
                mainContent,
                Priority.ALWAYS
        );

        rightSide.getChildren().addAll(
                topBar,
                mainContent
        );

        root.setLeft(sidebar);
        root.setCenter(rightSide);

        
        Scene scene =
                new Scene(
                        root,
                        1400,
                        850
                );

        stage.setTitle(
                "EventXpert - Customer Profile"
        );

        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
    }

    
    private HBox navigationItem(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        21
                )
        );

        Label textLabel =
                new Label(text);

        textLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
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

        item.setPrefHeight(48);

        item.setPadding(
                new Insets(
                        5,
                        15,
                        5,
                        15
                )
        );

        if (active) {

            item.setStyle(
                    "-fx-background-color:" +
                    LIGHT_BLUE +
                    ";" +
                    "-fx-background-radius:8;" +
                    "-fx-border-color:#B9E7FF;" +
                    "-fx-border-width:0 0 0 4;" +
                    "-fx-border-radius:8;"
            );

            iconLabel.setTextFill(
                    Color.WHITE
            );

            textLabel.setTextFill(
                    Color.WHITE
            );

        } else {

            item.setStyle(
                    "-fx-background-color:transparent;"
            );

            iconLabel.setTextFill(
                    Color.web("#8BA7CF")
            );

            textLabel.setTextFill(
                    Color.web("#8BA7CF")
            );
        }

        return item;
    }

    
    private HBox createTopBar() {

        HBox top =
                new HBox();

        top.setPrefHeight(78);

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        top.setPadding(
                new Insets(
                        12,
                        30,
                        12,
                        35
                )
        );

        top.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D8DEE7;" +
                "-fx-border-width:0 0 1 0;"
        );

        
        TextField search =
                new TextField();

        search.setPromptText(
                "Search..."
        );

        search.setPrefWidth(280);
        search.setPrefHeight(42);

        search.setFont(
                Font.font(
                        "Arial",
                        15
                )
        );

        search.setStyle(
                "-fx-background-color:#F7F9FC;" +
                "-fx-border-color:#C8D0DB;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-padding:0 12 0 12;"
        );

        Label searchIcon =
                new Label("⌕");

        searchIcon.setFont(
                Font.font(
                        "Arial",
                        28
                )
        );

        HBox searchBox =
                new HBox(
                        5,
                        searchIcon,
                        search
                );

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        
        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notification =
                new Label("♧");

        notification.setFont(
                Font.font(
                        "Arial",
                        27
                )
        );

        Label settings =
                new Label("⚙");

        settings.setFont(
                Font.font(
                        "Arial",
                        24
                )
        );

        Label help =
                new Label("?");

        help.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        Label profile =
                new Label("●");

        profile.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        31
                )
        );

        notification.setTextFill(
                Color.web("#182233")
        );

        settings.setTextFill(
                Color.web("#182233")
        );

        help.setTextFill(
                Color.web("#182233")
        );

        profile.setTextFill(
                Color.web("#173D6B")
        );

        top.getChildren().addAll(

                searchBox,
                spacer,
                notification,
                settings,
                help,
                profile
        );

        HBox.setMargin(
                notification,
                new Insets(
                        0,
                        25,
                        0,
                        0
                )
        );

        HBox.setMargin(
                settings,
                new Insets(
                        0,
                        25,
                        0,
                        0
                )
        );

        HBox.setMargin(
                help,
                new Insets(
                        0,
                        25,
                        0,
                        0
                )
        );

        return top;
    }

    

    private VBox createProfileContent() {

        VBox content =
                new VBox();

        content.setPadding(
                new Insets(
                        35,
                        45,
                        35,
                        45
                )
        );

        content.setSpacing(25);

        content.setStyle(
                "-fx-background-color:" + BG + ";"
        );

    

        Label title =
                new Label("My Profile");

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        36
                )
        );

        title.setTextFill(
                Color.web("#071D41")
        );

        Label subtitle =
                new Label(
                        "Manage your personal information and account settings."
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        18
                )
        );

        subtitle.setTextFill(
                Color.web("#4B5563")
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        

        HBox mainArea =
                new HBox(28);

        // LEFT COLUMN
        VBox leftColumn =
                new VBox(25);

        VBox profileCard =
                createProfileCard();

        VBox accountCard =
                createAccountCard();

        leftColumn.getChildren().addAll(
                profileCard,
                accountCard
        );

        
        VBox rightColumn =
                new VBox(25);

        VBox personalCard =
                createPersonalInformation();

        VBox securityCard =
                createSecurityCard();

        Button logout =
                createLogoutButton();

        rightColumn.getChildren().addAll(
                personalCard,
                securityCard,
                logout
        );

        HBox.setHgrow(
                rightColumn,
                Priority.ALWAYS
        );

        mainArea.getChildren().addAll(
                leftColumn,
                rightColumn
        );

        content.getChildren().addAll(
                heading,
                mainArea
        );

        return content;
    }

    

    private VBox createProfileCard() {

        VBox card =
                new VBox();

        card.setAlignment(
                Pos.CENTER
        );

        card.setSpacing(13);

        card.setPadding(
                new Insets(25)
        );

        card.setPrefWidth(340);
        card.setMinWidth(340);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#CBD4DF;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        
        StackPane photo =
                new StackPane();

        photo.setPrefSize(
                130,
                130
        );

        photo.setMaxSize(
                130,
                130
        );

        photo.setStyle(
                "-fx-background-color:#E8EDF2;" +
                "-fx-background-radius:100;" +
                "-fx-border-color:#E2E8F0;" +
                "-fx-border-width:6;" +
                "-fx-border-radius:100;"
        );

        Label person =
                new Label("👤");

        person.setFont(
                Font.font(
                        "Arial",
                        65
                )
        );

        photo.getChildren().add(
                person
        );

        Label name =
                new Label("Sarah Johnson");

        name.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        27
                )
        );

        name.setTextFill(
                Color.web("#071D41")
        );

        Label email =
                new Label(
                        "sarah.johnson@email.com"
                );

        email.setFont(
                Font.font(
                        "Arial",
                        15
                )
        );

        email.setTextFill(
                Color.web("#596273")
        );

        Label role =
                new Label("Customer");

        role.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        role.setTextFill(
                Color.web("#071D41")
        );

        role.setPadding(
                new Insets(
                        6,
                        18,
                        6,
                        18
                )
        );

        role.setStyle(
                "-fx-background-color:#C9E9FF;" +
                "-fx-background-radius:20;"
        );

        Label member =
                new Label(
                        "Member since 2026"
                );

        member.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        member.setTextFill(
                Color.web("#596273")
        );

        Button changePhoto =
                new Button(
                        "▣   Change Profile Photo"
                );

        changePhoto.setPrefWidth(275);
        changePhoto.setPrefHeight(44);

        changePhoto.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        changePhoto.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#071D41;" +
                "-fx-border-color:#52728D;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;"
        );

        changePhoto.setOnAction(
                e -> {

                    Alert alert =
                            new Alert(
                                    Alert.AlertType.INFORMATION
                            );

                    alert.setTitle(
                            "Profile Photo"
                    );

                    alert.setHeaderText(
                            "Change Profile Photo"
                    );

                    alert.setContentText(
                            "Photo upload can be connected here."
                    );

                    alert.showAndWait();
                }
        );

        card.getChildren().addAll(
                photo,
                name,
                email,
                role,
                member,
                changePhoto
        );

        return card;
    }

    
    private VBox createAccountCard() {

        VBox card =
                new VBox();

        card.setSpacing(17);

        card.setPadding(
                new Insets(25)
        );

        card.setPrefWidth(340);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#CBD4DF;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        Label title =
                new Label("Account Information");

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        title.setTextFill(
                Color.web("#071D41")
        );

        HBox role =
                accountRow(
                        "Account Role",
                        "Customer"
                );

        HBox status =
                accountRow(
                        "Account Status",
                        "● Active"
                );

        status.getChildren()
                .get(1)
                .setStyle(
                        "-fx-text-fill:#12B981;" +
                        "-fx-font-weight:bold;"
                );

        HBox member =
                accountRow(
                        "Member Since",
                        "2026"
                );

        card.getChildren().addAll(
                title,
                role,
                status,
                member
        );

        return card;
    }

   

    private HBox accountRow(
            String left,
            String right
    ) {

        Label l =
                new Label(left);

        l.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        l.setTextFill(
                Color.web("#5B6472")
        );

        Label r =
                new Label(right);

        r.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        r.setTextFill(
                Color.web("#071D41")
        );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        HBox row =
                new HBox(
                        l,
                        space,
                        r
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        return row;
    }

    

    private VBox createPersonalInformation() {

        VBox card =
                new VBox();

        card.setPadding(
                new Insets(0)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#CBD4DF;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        HBox header =
                new HBox();

        header.setPadding(
                new Insets(
                        25
                )
        );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        "Personal Information"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        21
                )
        );

        title.setTextFill(
                Color.web("#071D41")
        );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Button edit =
                new Button("Edit");

        edit.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        edit.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#52728D;"
        );

        edit.setOnAction(
                e -> toggleEditing()
        );

        header.getChildren().addAll(
                title,
                space,
                edit
        );

        // Fields
        GridPane fields =
                new GridPane();

        fields.setHgap(70);
        fields.setVgap(25);

        fields.setPadding(
                new Insets(
                        28
                )
        );

        ColumnConstraints c1 =
                new ColumnConstraints();

        c1.setPercentWidth(50);

        ColumnConstraints c2 =
                new ColumnConstraints();

        c2.setPercentWidth(50);

        fields.getColumnConstraints()
                .addAll(c1, c2);

        
        Label nameLabel =
                fieldLabel("Full Name");

        nameField =
                new TextField(
                        "Sarah Johnson"
                );

        styleField(
                nameField
        );

    
        Label emailLabel =
                fieldLabel("Email Address");

        emailField =
                new TextField(
                        "sarah.johnson@email.com"
                );

        styleField(
                emailField
        );

        // Mobile
        Label mobileLabel =
                fieldLabel("Mobile Number");

        mobileField =
                new TextField(
                        "+91 XXXXX XXXXX"
                );

        styleField(
                mobileField
        );

        VBox nameBox =
                new VBox(
                        8,
                        nameLabel,
                        nameField
                );

        VBox emailBox =
                new VBox(
                        8,
                        emailLabel,
                        emailField
                );

        VBox mobileBox =
                new VBox(
                        8,
                        mobileLabel,
                        mobileField
                );

        fields.add(
                nameBox,
                0,
                0
        );

        fields.add(
                emailBox,
                1,
                0
        );

        fields.add(
                mobileBox,
                0,
                1
        );

        card.getChildren().addAll(
                header,
                fields
        );

        // Start read-only
        setFieldsEditable(false);

        return card;
    }

    

    private Label fieldLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        label.setTextFill(
                Color.web("#5B6472")
        );

        return label;
    }

    

    private void styleField(
            TextField field
    ) {

        field.setPrefHeight(42);

        field.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        field.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-border-color:transparent;" +
                "-fx-text-fill:#071D41;" +
                "-fx-padding:0;"
        );
    }

    
    private void toggleEditing() {

        boolean editable =
                !nameField.isEditable();

        setFieldsEditable(
                editable
        );
    }

    private void setFieldsEditable(
            boolean editable
    ) {

        nameField.setEditable(
                editable
        );

        emailField.setEditable(
                editable
        );

        mobileField.setEditable(
                editable
        );

        if (editable) {

            nameField.setStyle(
                    "-fx-background-color:#F8FAFC;" +
                    "-fx-border-color:#5CAFE3;" +
                    "-fx-border-radius:6;" +
                    "-fx-background-radius:6;" +
                    "-fx-text-fill:#071D41;" +
                    "-fx-padding:0 10 0 10;"
            );

            emailField.setStyle(
                    "-fx-background-color:#F8FAFC;" +
                    "-fx-border-color:#5CAFE3;" +
                    "-fx-border-radius:6;" +
                    "-fx-background-radius:6;" +
                    "-fx-text-fill:#071D41;" +
                    "-fx-padding:0 10 0 10;"
            );

            mobileField.setStyle(
                    "-fx-background-color:#F8FAFC;" +
                    "-fx-border-color:#5CAFE3;" +
                    "-fx-border-radius:6;" +
                    "-fx-background-radius:6;" +
                    "-fx-text-fill:#071D41;" +
                    "-fx-padding:0 10 0 10;"
            );

        } else {

            styleField(nameField);
            styleField(emailField);
            styleField(mobileField);
        }
    }

    

    private VBox createSecurityCard() {

        VBox card =
                new VBox();

        card.setPadding(
                new Insets(0)
        );

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#CBD4DF;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        HBox header =
                new HBox();

        header.setPadding(
                new Insets(
                        25
                )
        );

        Label title =
                new Label(
                        "Account Security"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        21
                )
        );

        title.setTextFill(
                Color.web("#071D41")
        );

        header.getChildren().add(
                title
        );

        VBox body =
                new VBox();

        body.setPadding(
                new Insets(
                        25
                )
        );

        Label passwordLabel =
                new Label("Password");

        passwordLabel.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        passwordLabel.setTextFill(
                Color.web("#5B6472")
        );

        Label password =
                new Label(
                        "••••••••••••"
                );

        password.setFont(
                Font.font(
                        "Arial",
                        20
                )
        );

        password.setTextFill(
                Color.web("#071D41")
        );

        VBox passwordBox =
                new VBox(
                        8,
                        passwordLabel,
                        password
                );

        Region space =
                new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Button changePassword =
                new Button(
                        "Change\nPassword"
                );

        changePassword.setPrefSize(
                190,
                50
        );

        changePassword.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        changePassword.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#071D41;" +
                "-fx-border-color:#52728D;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        changePassword.setOnAction(
                e -> showChangePassword()
        );

        HBox row =
                new HBox(
                        passwordBox,
                        space,
                        changePassword
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        body.getChildren().add(
                row
        );

        card.getChildren().addAll(
                header,
                body
        );

        return card;
    }

    

    private void showChangePassword() {

        Dialog<String> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Change Password"
        );

        dialog.setHeaderText(
                "Update your account password"
        );

        PasswordField oldPassword =
                new PasswordField();

        oldPassword.setPromptText(
                "Current password"
        );

        PasswordField newPassword =
                new PasswordField();

        newPassword.setPromptText(
                "New password"
        );

        PasswordField confirmPassword =
                new PasswordField();

        confirmPassword.setPromptText(
                "Confirm password"
        );

        VBox box =
                new VBox(
                        12,
                        oldPassword,
                        newPassword,
                        confirmPassword
                );

        box.setPadding(
                new Insets(15)
        );

        dialog.getDialogPane()
                .setContent(box);

        ButtonType save =
                new ButtonType(
                        "Save",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        save,
                        ButtonType.CANCEL
                );

        dialog.showAndWait();
    }

    

    private Button createLogoutButton() {

        Button logout =
                new Button(
                        "⇥   Logout"
                );

        logout.setPrefSize(
                150,
                45
        );

        logout.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        logout.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#071D41;" +
                "-fx-border-color:#CBD4DF;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        logout.setOnAction(
                e -> {

                    Alert alert =
                            new Alert(
                                    Alert.AlertType.CONFIRMATION
                            );

                    alert.setTitle(
                            "Logout"
                    );

                    alert.setHeaderText(
                            "Are you sure you want to logout?"
                    );

                    alert.setContentText(
                            "You will be returned to the login page."
                    );

                    alert.showAndWait();
                }
        );

        return logout;
    }

    

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}


