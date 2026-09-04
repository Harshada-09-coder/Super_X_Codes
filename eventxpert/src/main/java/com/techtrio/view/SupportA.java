package com.techtrio.view;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.AdminProfile;
import com.techtrio.session.UserSession;

import java.io.File;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SupportA {

    private Scene supportScene;

    // Colors
    private final String NAVY = "#062B63";
    private final String LIGHT_BLUE = "#BFE2FA";
    private final String BG = "#F5F8FC";
    private final String BORDER = "#D6DEE8";
    private final String TEXT = "#14213D";
    private final String GRAY = "#596579";

    private final AdminController adminController = new AdminController();
    private String adminId;

    // Dynamic Form Controls
    private TextField nameField;
    private TextField emailField;
    private ComboBox<String> issueBox;
    private TextField subjectField;
    private TextArea descriptionArea;
    private Label topProfileAvatar;
    private Button attachBtn;

    private File attachedFile = null;

    public Scene getsupportScene() {

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

            nav.setOnMouseEntered(e -> nav.setStyle("-fx-background-color:#0C3975;-fx-background-radius:8;"));
            nav.setOnMouseExited(e -> nav.setStyle("-fx-background-color:transparent;-fx-background-radius:8;"));

            nav.setOnMouseClicked(e -> {
                Stage stage = (Stage) nav.getScene().getWindow();
                String page = item
                        .replace("▦", "")
                        .replace("▣", "")
                        .replace("♙", "")
                        .replace("▤", "")
                        .replace("△", "")
                        .replace("▥", "")
                        .replace("♧", "")
                        .trim();
                NavigationManager.navigate(page, stage);
            });

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

            nav.setOnMouseClicked(e -> {
                Stage stage = (Stage) nav.getScene().getWindow();
                String page = item.replace("⚙", "").replace("●", "").trim();
                NavigationManager.navigate(page, stage);
            });

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
                "-fx-background-radius:7;" +
                "-fx-cursor:hand;"
        );

        searchBtn.setOnAction(e -> {
            String query = search.getText().trim();
            if (!query.isEmpty()) {
                showAlert("Search Results", "Search completed for '" + query + "'.\nFound relevant guides in User Guide & FAQs.");
            }
        });

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
        bell.setStyle("-fx-cursor:hand;");
        bell.setOnMouseClicked(e -> NavigationManager.navigate("Notifications", (Stage) bell.getScene().getWindow()));

        Label question = new Label("?");
        question.setPrefSize(25, 25);
        question.setAlignment(Pos.CENTER);
        question.setStyle(
                "-fx-border-color:#26354A;" +
                "-fx-border-radius:50%;" +
                "-fx-text-fill:#26354A;" +
                "-fx-font-weight:bold;"
        );

        topProfileAvatar = new Label("AD");
        topProfileAvatar.setPrefSize(38, 38);
        topProfileAvatar.setAlignment(Pos.CENTER);
        topProfileAvatar.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:50%;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;"
        );
        topProfileAvatar.setOnMouseClicked(e -> NavigationManager.navigate("Profile", (Stage) topProfileAvatar.getScene().getWindow()));

        topBar.getChildren().addAll(
                searchArea,
                topSpace,
                bell,
                question,
                topProfileAvatar
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
        helpCards.setCursor(javafx.scene.Cursor.HAND);

        VBox guide = helpCard("▤", "User Guide", "Learn how to use\nEventXpert");
        guide.setOnMouseClicked(e -> showAlert("User Guide", "• Dashboard: Manage all key metrics.\n• Event Requests: Assign organizers to new events.\n• Vendors & Venues: Monitor verified profiles.\n• Analytics: Real-time revenue insights."));

        VBox faq = helpCard("▣", "FAQs", "Find answers to common\nquestions");
        faq.setOnMouseClicked(e -> showAlert("Frequently Asked Questions", "Q: How to approve an event?\nA: Navigate to Event Requests, select the event and assign an organizer.\n\nQ: How to update profile?\nA: Go to Profile page and click Edit Profile."));

        VBox tutorial = helpCard("▥", "System Tutorial", "Learn the main features");
        tutorial.setOnMouseClicked(e -> showAlert("System Tutorial", "Welcome to EventXpert Console!\n1. Check Notifications for pending requests.\n2. Review system status on Dashboard.\n3. Track revenue in Reports & Analytics."));

        VBox contact = helpCard("♧", "Contact Support", "Get assistance from our\nsupport team");
        contact.setOnMouseClicked(e -> {
            if (subjectField != null) subjectField.requestFocus();
        });

        HBox.setHgrow(guide, Priority.ALWAYS);
        HBox.setHgrow(faq, Priority.ALWAYS);
        HBox.setHgrow(tutorial, Priority.ALWAYS);
        HBox.setHgrow(contact, Priority.ALWAYS);

        helpCards.getChildren().addAll(guide, faq, tutorial, contact);

        // ================= LOWER SECTION =================
        HBox lower = new HBox(26);

        // ----- Support Request Form -----
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
        nameField = input("Administrator");

        // Email
        Label emailLabel = formLabel("Email");
        emailField = input("admin@eventxpert.com");

        // Issue
        Label issueLabel = formLabel("Issue Type");
        issueBox = new ComboBox<>();
        issueBox.getItems().addAll(
                "Technical Issue",
                "Account Issue",
                "Payment Issue",
                "Event Issue",
                "Other"
        );
        issueBox.setValue("Technical Issue");
        issueBox.setPrefHeight(44);
        issueBox.setMaxWidth(Double.MAX_VALUE);
        issueBox.setStyle(
                "-fx-border-color:#C9D2DE;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:15px;"
        );

        // Subject
        Label subjectLabel = formLabel("Subject");
        subjectField = input("");
        subjectField.setPromptText("Enter brief subject...");

        VBox nameBox = new VBox(6, nameLabel, nameField);
        VBox emailBox = new VBox(6, emailLabel, emailField);
        VBox issueBoxCont = new VBox(6, issueLabel, issueBox);
        VBox subjectBox = new VBox(6, subjectLabel, subjectField);

        form.add(nameBox, 0, 0);
        form.add(emailBox, 1, 0);
        form.add(issueBoxCont, 0, 1);
        form.add(subjectBox, 1, 1);

        GridPane.setHgrow(nameBox, Priority.ALWAYS);
        GridPane.setHgrow(emailBox, Priority.ALWAYS);
        GridPane.setHgrow(issueBoxCont, Priority.ALWAYS);
        GridPane.setHgrow(subjectBox, Priority.ALWAYS);

        // Description
        Label descLabel = formLabel("Description");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Describe the issue or assistance required in detail...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        descriptionArea.setStyle(
                "-fx-border-color:#C9D2DE;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-font-size:15px;"
        );

        VBox descBox = new VBox(6, descLabel, descriptionArea);
        descBox.setPadding(new Insets(0, 28, 0, 28));

        // Buttons
        attachBtn = new Button("📎   Attach\n      File");
        attachBtn.setPrefSize(138, 42);
        attachBtn.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-weight:bold;" +
                "-fx-font-size:14px;" +
                "-fx-cursor:hand;"
        );

        attachBtn.setOnAction(e -> {
            Stage stage = (Stage) attachBtn.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Attach File to Support Request");
            attachedFile = fileChooser.showOpenDialog(stage);
            if (attachedFile != null) {
                String fname = attachedFile.getName();
                if (fname.length() > 10) fname = fname.substring(0, 9) + "..";
                attachBtn.setText("📎  " + fname);
            }
        });

        Button submit = new Button("Submit Request");
        submit.setPrefSize(170, 42);
        submit.setTextFill(Color.WHITE);
        submit.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        submit.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-background-radius:6;" +
                "-fx-cursor:hand;"
        );

        submit.setOnAction(e -> submitSupportRequest());

        Region buttonSpace = new Region();
        HBox.setHgrow(buttonSpace, Priority.ALWAYS);

        HBox buttons = new HBox(10, attachBtn, buttonSpace, submit);
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

        // Load Admin's actual profile data dynamically
        loadAdminProfileData();

        Scene scene = new Scene(root, 1550, 750);
        supportScene = scene;
        return supportScene;
    }

    /* ================= DYNAMIC DATA SYNC ================= */

    private void loadAdminProfileData() {
        Thread thread = new Thread(() -> {
            try {
                adminId = UserSession.getUid();
                if (adminId == null || adminId.isBlank()) {
                    try {
                        Method m = NavigationManager.class.getMethod("getCurrentAdminId");
                        Object res = m.invoke(null);
                        if (res != null) adminId = res.toString();
                    } catch (Exception ignored) {}
                }

                AdminProfile profile = (adminId != null && !adminId.isBlank()) ? adminController.getAdminProfile(adminId) : null;

                String liveName = "System Administrator";
                String liveEmail = "admin@eventxpert.com";

                if (profile != null) {
                    if (profile.getName() != null && !profile.getName().isBlank()) liveName = profile.getName();
                    if (profile.getEmail() != null && !profile.getEmail().isBlank()) liveEmail = profile.getEmail();
                }

                final String fName = liveName;
                final String fEmail = liveEmail;

                Platform.runLater(() -> {
                    nameField.setText(fName);
                    emailField.setText(fEmail);
                    topProfileAvatar.setText(getInitials(fName));
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void submitSupportRequest() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String issueType = issueBox.getValue();
        String subject = subjectField.getText().trim();
        String desc = descriptionArea.getText().trim();

        if (subject.isEmpty()) {
            showAlert("Required", "Please enter a subject for your request.");
            return;
        }

        if (desc.isEmpty()) {
            showAlert("Required", "Please provide a detailed description of the issue.");
            return;
        }

        String ticketId = "TICK-" + (System.currentTimeMillis() % 100000);

        Thread thread = new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                if (db != null) {
                    Map<String, Object> ticket = new HashMap<>();
                    ticket.put("ticketId", ticketId);
                    ticket.put("name", name);
                    ticket.put("email", email);
                    ticket.put("issueType", issueType);
                    ticket.put("subject", subject);
                    ticket.put("description", desc);
                    ticket.put("attachedFile", (attachedFile != null) ? attachedFile.getName() : "None");
                    ticket.put("status", "Open");
                    ticket.put("createdAt", Instant.now().toString());

                    db.collection("support_tickets").document(ticketId).set(ticket).get();
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(
                            Alert.AlertType.INFORMATION,
                            "Your support request #" + ticketId + " has been submitted successfully.\nOur technical team will review and respond within 24 hours."
                    );
                    alert.setTitle("Support Request");
                    alert.setHeaderText("Ticket Created: #" + ticketId);
                    alert.showAndWait();

                    subjectField.clear();
                    descriptionArea.clear();
                    attachedFile = null;
                    attachBtn.setText("📎   Attach\n      File");
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> showAlert("Error", "Could not submit ticket to database: " + ex.getMessage()));
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "AD";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private HBox infoRow(String icon, String title, String value) {
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