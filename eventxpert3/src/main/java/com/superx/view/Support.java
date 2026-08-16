package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Support extends Application{

    // Colors
    private final String NAVY = "#062B63";
    private final String LIGHT_BLUE = "#BFE2FA";
    private final String BG = "#F5F8FC";
    private final String BORDER = "#D6DEE8";
    private final String TEXT = "#14213D";
    private final String GRAY = "#596579";   
   
   
   
    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // ================= SIDEBAR =================
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(258);
        sidebar.setMinWidth(258);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        // Logo
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(25, 20, 25, 25));

        Label logo = new Label("▣");
        logo.setPrefSize(45, 45);
        logo.setAlignment(Pos.CENTER);
        logo.setStyle(
                "-fx-background-color:" + LIGHT_BLUE + ";" +
                "-fx-background-radius:50%;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-size:22px;"
        );

        Label brand = new Label("EventXpert");
        brand.setTextFill(Color.WHITE);
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label admin = new Label("Admin Console");
        admin.setTextFill(Color.web("#B8C7DC"));
        admin.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

        HBox brandRow = new HBox(12, logo, new VBox(2, brand, admin));
        brandRow.setAlignment(Pos.CENTER_LEFT);

        logoBox.getChildren().add(brandRow);

        // Main navigation
        VBox mainNav = new VBox(7);
        mainNav.setPadding(new Insets(5, 16, 10, 16));

        String[] menu = {
                "▦   Dashboard",
                "▣   Event Requests",
                "▣   Events",
                "▦   Calendar",
                "♙   Customers",
                "♙   Organizers",
                "▤   Vendors",
                "△   Conflict Detection",
                "▥   Reports & Analytics",
                "♧   Notifications"
        };

        for (String item : menu) {
            Label nav = new Label(item);
            nav.setMaxWidth(Double.MAX_VALUE);
            nav.setPrefHeight(48);
            nav.setAlignment(Pos.CENTER_LEFT);
            nav.setPadding(new Insets(0, 12, 0, 10));
            nav.setTextFill(Color.web("#9FB3CE"));
            nav.setFont(Font.font("Arial", FontWeight.BOLD, 15));

            nav.setStyle("-fx-background-radius:8;");

            nav.setOnMouseEntered(e ->
                    nav.setStyle("-fx-background-color:#0C3975;-fx-background-radius:8;")
            );

            nav.setOnMouseExited(e ->
                    nav.setStyle("-fx-background-color:transparent;-fx-background-radius:8;")
            );

            mainNav.getChildren().add(nav);
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomNav = new VBox(7);
        bottomNav.setPadding(new Insets(10, 16, 18, 16));

        String[] bottom = {
                "⚙   Settings",
                "●   Profile"
        };

        for (String item : bottom) {
            Label nav = new Label(item);
            nav.setMaxWidth(Double.MAX_VALUE);
            nav.setPrefHeight(48);
            nav.setAlignment(Pos.CENTER_LEFT);
            nav.setPadding(new Insets(0, 12, 0, 10));
            nav.setTextFill(Color.web("#9FB3CE"));
            nav.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            bottomNav.getChildren().add(nav);
        }

        // Support active
        Label supportNav = new Label("❔   Support");
        supportNav.setMaxWidth(Double.MAX_VALUE);
        supportNav.setPrefHeight(50);
        supportNav.setAlignment(Pos.CENTER_LEFT);
        supportNav.setPadding(new Insets(0, 12, 0, 10));
        supportNav.setTextFill(Color.web(NAVY));
        supportNav.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        supportNav.setStyle(
                "-fx-background-color:" + LIGHT_BLUE + ";" +
                "-fx-background-radius:8;"
        );

        bottomNav.getChildren().add(supportNav);

        sidebar.getChildren().addAll(
                logoBox,
                mainNav,
                space,
                bottomNav
        );

        // ================= TOP BAR =================
        BorderPane main = new BorderPane();

        HBox topBar = new HBox();
        topBar.setPrefHeight(70);
        topBar.setPadding(new Insets(12, 25, 12, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-width:0 0 1 0;"
        );

        TextField search = new TextField();
        search.setPromptText("Search for help...");
        search.setPrefWidth(410);
        search.setPrefHeight(44);
        search.setStyle(
                "-fx-border-color:#C9D2DE;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-font-size:14px;" +
                "-fx-padding:0 14;"
        );

        Button searchBtn = new Button("Search");
        searchBtn.setPrefSize(88, 38);
        searchBtn.setTextFill(Color.WHITE);
        searchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        searchBtn.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:7;"
        );

        StackPane searchBox = new StackPane(search);
        searchBox.setPrefWidth(410);

        HBox searchArea = new HBox(searchBox, searchBtn);
        searchArea.setAlignment(Pos.CENTER_LEFT);
        searchArea.setSpacing(5);

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Label bell = new Label("♧");
        bell.setFont(Font.font(25));
        bell.setTextFill(Color.web("#26354A"));

        Label question = new Label("?");
        question.setPrefSize(25, 25);
        question.setAlignment(Pos.CENTER);
        question.setStyle(
                "-fx-border-color:#26354A;" +
                "-fx-border-radius:50%;" +
                "-fx-text-fill:#26354A;" +
                "-fx-font-weight:bold;"
        );

        Label profile = new Label("AD");
        profile.setPrefSize(38, 38);
        profile.setAlignment(Pos.CENTER);
        profile.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:50%;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;"
        );

        topBar.getChildren().addAll(
                searchArea,
                topSpace,
                bell,
                question,
                profile
        );

        main.setTop(topBar);

        // ================= CONTENT =================
        VBox content = new VBox(28);
        content.setPadding(new Insets(28, 26, 35, 26));
        content.setStyle("-fx-background-color:" + BG + ";");

        // Heading
        Label title = new Label("Support");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setTextFill(Color.web(TEXT));

        Label subtitle = new Label(
                "Get help, find answers, or contact our support team."
        );
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.web(GRAY));

        VBox heading = new VBox(5, title, subtitle);

        // ================= HELP CARDS =================
        HBox helpCards = new HBox(25);
        helpCards.setFillHeight(true);

        VBox guide = helpCard(
                "▤",
                "User Guide",
                "Learn how to use\nEventXpert"
        );

        VBox faq = helpCard(
                "▣",
                "FAQs",
                "Find answers to common\nquestions"
        );

        VBox tutorial = helpCard(
                "▥",
                "System Tutorial",
                "Learn the main features"
        );

        VBox contact = helpCard(
                "♧",
                "Contact Support",
                "Get assistance from our\nsupport team"
        );

        HBox.setHgrow(guide, Priority.ALWAYS);
        HBox.setHgrow(faq, Priority.ALWAYS);
        HBox.setHgrow(tutorial, Priority.ALWAYS);
        HBox.setHgrow(contact, Priority.ALWAYS);

        helpCards.getChildren().addAll(
                guide, faq, tutorial, contact
        );

        // ================= LOWER SECTION =================
        HBox lower = new HBox(26);

        // ----- Support Request -----
        VBox request = new VBox();
        request.setStyle(cardStyle());
        request.setPadding(new Insets(0));
        HBox.setHgrow(request, Priority.ALWAYS);

        Label requestTitle = new Label("Submit a Support Request");
        requestTitle.setFont(Font.font("Arial", FontWeight.BOLD, 27));
        requestTitle.setTextFill(Color.web(TEXT));

        HBox titleBox = new HBox(requestTitle);
        titleBox.setPadding(new Insets(26, 28, 22, 28));

        Separator sep = new Separator();

        GridPane form = new GridPane();
        form.setHgap(25);
        form.setVgap(18);
        form.setPadding(new Insets(25, 28, 25, 28));

        // Name
        Label nameLabel = formLabel("Name");
        TextField name = input("");

        // Email
        Label emailLabel = formLabel("Email");
        TextField email = input("");

        // Issue
        Label issueLabel = formLabel("Issue Type");
        ComboBox<String> issue = new ComboBox<>();
        issue.getItems().addAll(
                "Technical Issue",
                "Account Issue",
                "Payment Issue",
                "Event Issue",
                "Other"
        );
        issue.setValue("Technical Issue");
        issue.setPrefHeight(44);
        issue.setMaxWidth(Double.MAX_VALUE);
        issue.setStyle(
                "-fx-border-color:#C9D2DE;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:15px;"
        );

        // Subject
        Label subjectLabel = formLabel("Subject");
        TextField subject = input("");

        VBox nameBox = new VBox(6, nameLabel, name);
        VBox emailBox = new VBox(6, emailLabel, email);
        VBox issueBox = new VBox(6, issueLabel, issue);
        VBox subjectBox = new VBox(6, subjectLabel, subject);

        form.add(nameBox, 0, 0);
        form.add(emailBox, 1, 0);
        form.add(issueBox, 0, 1);
        form.add(subjectBox, 1, 1);

        GridPane.setHgrow(nameBox, Priority.ALWAYS);
        GridPane.setHgrow(emailBox, Priority.ALWAYS);
        GridPane.setHgrow(issueBox, Priority.ALWAYS);
        GridPane.setHgrow(subjectBox, Priority.ALWAYS);

        // Description
        Label descLabel = formLabel("Description");
        TextArea description = new TextArea();
        description.setPrefRowCount(5);
        description.setWrapText(true);
        description.setStyle(
                "-fx-border-color:#C9D2DE;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:15px;"
        );

        VBox descBox = new VBox(6, descLabel, description);
        descBox.setPadding(new Insets(0, 28, 0, 28));

        // Buttons
        Button attach = new Button("📎   Attach\n      File");
        attach.setPrefSize(138, 42);
        attach.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-weight:bold;" +
                "-fx-font-size:14px;"
        );

        Button submit = new Button("Submit Request");
        submit.setPrefSize(170, 42);
        submit.setTextFill(Color.WHITE);
        submit.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        submit.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:6;"
        );

        submit.setOnAction(e -> {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Your support request has been submitted."
            );
            alert.setHeaderText("Support Request");
            alert.showAndWait();
        });

        Region buttonSpace = new Region();
        HBox.setHgrow(buttonSpace, Priority.ALWAYS);

        HBox buttons = new HBox(10, attach, buttonSpace, submit);
        buttons.setPadding(new Insets(25, 28, 28, 28));
        buttons.setAlignment(Pos.CENTER_LEFT);

        request.getChildren().addAll(
                titleBox,
                sep,
                form,
                descBox,
                buttons
        );

        // ----- Right Side -----
        VBox right = new VBox(25);
        right.setPrefWidth(340);
        right.setMinWidth(320);

        // Contact Information
        VBox contactInfo = new VBox(17);
        contactInfo.setPadding(new Insets(25));
        contactInfo.setStyle(cardStyle());

        Label contactTitle = sectionTitle("Contact Information");

        contactInfo.getChildren().addAll(
                contactTitle,
                infoRow("✉", "Support Email", "support@eventxpert.com"),
                infoRow("☎", "Phone", "+91 98765 43210"),
                infoRow("◷", "Support Hours", "Mon – Sat, 9:00 AM – 6:00 PM"),
                infoRow("ϟ", "Response Time", "Within 24 hours")
        );

        // System Information
        VBox systemInfo = new VBox(17);
        systemInfo.setPadding(new Insets(25));
        systemInfo.setStyle(cardStyle());

        Label systemTitle = sectionTitle("ⓘ  System Information");

        HBox appRow = dataRow("Application", "EventXpert");
        HBox versionRow = dataRow("Version", "1.0.0");
        HBox envRow = dataRow("Environment", "Production");
        HBox updatedRow = dataRow("Last Updated", "15 Aug 2026");

        systemInfo.getChildren().addAll(
                systemTitle,
                appRow,
                versionRow,
                envRow,
                updatedRow
        );

        right.getChildren().addAll(contactInfo, systemInfo);

        lower.getChildren().addAll(request, right);

        VBox.setVgrow(lower, Priority.ALWAYS);

        content.getChildren().addAll(
                heading,
                helpCards,
                lower
        );

        // ================= SCROLLPANE =================
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle(
                "-fx-background-color:" + BG + ";" +
                "-fx-border-color:transparent;"
        );

        main.setCenter(scroll);

        root.setLeft(sidebar);
        root.setCenter(main);

        // ================= SCENE =================
        Scene scene = new Scene(root, 1550, 750);

        stage.setTitle("EventXpert - Support");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.show();
    }

    // ================= HELPER METHODS =================

    private VBox helpCard(String icon, String title, String text) {

        Label iconLabel = new Label(icon);
        iconLabel.setPrefSize(52, 52);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(
                "-fx-background-color:#DFF1FC;" +
                "-fx-background-radius:8;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-size:24px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(TEXT));

        Label desc = new Label(text);
        desc.setFont(Font.font("Arial", 15));
        desc.setTextFill(Color.web(GRAY));
        desc.setWrapText(true);

        VBox box = new VBox(17, iconLabel, titleLabel, desc);
        box.setPadding(new Insets(25));
        box.setPrefHeight(190);
        box.setMinWidth(210);
        box.setStyle(cardStyle());

        return box;
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 14));
        label.setTextFill(Color.web("#374151"));
        return label;
    }

    private TextField input(String text) {
        TextField field = new TextField(text);
        field.setPrefHeight(44);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-border-color:#C9D2DE;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:15px;" +
                "-fx-padding:0 12;"
        );
        return field;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 21));
        label.setTextFill(Color.web(TEXT));
        return label;
    }

    private HBox infoRow(
            String icon,
            String title,
            String value
    ) {

        Label iconLabel = new Label(icon);
        iconLabel.setPrefWidth(28);
        iconLabel.setFont(Font.font(20));
        iconLabel.setTextFill(Color.web("#4B5563"));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 13));
        titleLabel.setTextFill(Color.web(GRAY));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valueLabel.setTextFill(Color.web(TEXT));
        valueLabel.setWrapText(true);

        VBox text = new VBox(2, titleLabel, valueLabel);

        HBox row = new HBox(10, iconLabel, text);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

    private HBox dataRow(String left, String right) {

        Label l = new Label(left);
        l.setFont(Font.font("Arial", 14));
        l.setTextFill(Color.web(GRAY));

        Label r = new Label(right);
        r.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        r.setTextFill(Color.web(TEXT));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox row = new HBox(l, space, r);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

    private String cardStyle() {
        return
                "-fx-background-color:white;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:#E0E5EC;" +
                "-fx-border-radius:10;";
    }

}