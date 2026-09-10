package com.techtrio.view;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.AdminProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SupportA {

    private Scene supportScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private final AdminController adminController = new AdminController();
    private String adminId;

    private TextField nameField;
    private TextField emailField;
    private ComboBox<String> issueBox;
    private TextField subjectField;
    private TextArea descriptionArea;

    private StackPane rootLayer;

    public Scene getsupportScene() {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        root.setLeft(createAdminSidebar("Support"));

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Admin Help & Support Center");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Submit technical tickets, browse system documentation or get direct developer assistance.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, subtitle);

        HBox helpCards = new HBox(12);
        helpCards.getChildren().addAll(
                createDocCard("User Guide", "Learn full EventXpert workflows", "fas-book-open"),
                createDocCard("Knowledge Base", "Frequently asked technical questions", "fas-question-circle"),
                createDocCard("System Architecture", "Timefold solver and Firebase rules", "fas-layer-group"),
                createDocCard("Developer Contact", "Direct technical escalation channel", "fas-headset")
        );

        HBox lower = new HBox(18);

        VBox formCard = createCard();
        Label fTitle = new Label("Submit Support / Bug Ticket");
        fTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        fTitle.setTextFill(Color.web(PRIMARY));

        nameField = new TextField("Administrator");
        emailField = new TextField("admin@eventxpert.com");

        issueBox = new ComboBox<>();
        issueBox.getItems().addAll("Timefold Constraint Error", "Payment Webhook Issue", "Authentication / 2FA", "Database Connection", "General Query");
        issueBox.setValue("Timefold Constraint Error");
        issueBox.setMaxWidth(Double.MAX_VALUE);

        subjectField = new TextField();
        subjectField.setPromptText("Enter brief ticket summary...");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Explain the incident, error stack or requirement in detail...");
        descriptionArea.setPrefRowCount(3);

        GridPane form = new GridPane();
        form.setHgap(14); form.setVgap(10);
        form.add(new Label("Admin Name:"), 0, 0); form.add(nameField, 0, 1);
        form.add(new Label("Contact Email:"), 1, 0); form.add(emailField, 1, 1);
        form.add(new Label("Issue Classification:"), 0, 2); form.add(issueBox, 0, 3);
        form.add(new Label("Subject:"), 1, 2); form.add(subjectField, 1, 3);

        Button submitBtn = new Button("Submit Support Ticket");
        submitBtn.setStyle("-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 18; -fx-background-radius:6; -fx-cursor:hand;");
        submitBtn.setOnAction(e -> submitSupportTicket());

        HBox btnRow = new HBox(submitBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        formCard.getChildren().addAll(fTitle, new Separator(), form, new Label("Description / Error Trace:"), descriptionArea, btnRow);
        HBox.setHgrow(formCard, Priority.ALWAYS);

        VBox infoCard = createCard();
        infoCard.setPrefWidth(320);
        Label iTitle = new Label("Platform Health & SLA");
        iTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        iTitle.setTextFill(Color.web(PRIMARY));

        infoCard.getChildren().addAll(
                iTitle,
                new Separator(),
                infoRow("Version", "EventXpert v1.0.0"),
                infoRow("Environment", "Production (Cloud)"),
                infoRow("Backend", "Google Cloud Firestore"),
                infoRow("Response SLA", "< 2 Hours Priority")
        );

        lower.getChildren().addAll(formCard, infoCard);
        content.getChildren().addAll(heading, helpCards, lower);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);
        rootLayer.getChildren().add(root);

        loadAdminProfileData();

        supportScene = new Scene(rootLayer, 1550, 785);
        return supportScene;
    }

    private void submitSupportTicket() {
        String sub = subjectField.getText().trim();
        String desc = descriptionArea.getText().trim();

        if (sub.isEmpty() || desc.isEmpty()) {
            showInAppModal("Validation Error", "Please provide a subject and detailed description before submitting.", false);
            return;
        }

        String ticketId = "TICK-" + (System.currentTimeMillis() % 100000);

        Thread thread = new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                if (db != null) {
                    Map<String, Object> ticket = new HashMap<>();
                    ticket.put("ticketId", ticketId);
                    ticket.put("name", nameField.getText().trim());
                    ticket.put("email", emailField.getText().trim());
                    ticket.put("issueType", issueBox.getValue());
                    ticket.put("subject", sub);
                    ticket.put("description", desc);
                    ticket.put("status", "Open");
                    ticket.put("createdAt", Instant.now().toString());

                    db.collection("support_tickets").document(ticketId).set(ticket).get();
                }

                Platform.runLater(() -> {
                    subjectField.clear();
                    descriptionArea.clear();
                    showInAppModal("Ticket Logged: #" + ticketId, "Your request has been logged successfully to Firestore. Our team will review it shortly.", true);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void loadAdminProfileData() {
        Thread thread = new Thread(() -> {
            try {
                adminId = UserSession.getUid();
                AdminProfile profile = (adminId != null && !adminId.isBlank()) ? adminController.getAdminProfile(adminId) : null;
                if (profile != null) {
                    Platform.runLater(() -> {
                        if (profile.getName() != null) nameField.setText(profile.getName());
                        if (profile.getEmail() != null) emailField.setText(profile.getEmail());
                    });
                }
            } catch (Exception ignored) {}
        });
        thread.setDaemon(true);
        thread.start();
    }

    /* ================= FIXED SLENDER RECTANGULAR POPUP ================= */
    private void showInAppModal(String title, String message, boolean success) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web(success ? "#DCFCE7" : "#FEE2E2"));
        Label ic = new Label(success ? "✓" : "⚠");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: " + (success ? "#16A34A" : "#DC2626") + "; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(PRIMARY));

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private VBox createDocCard(String title, String sub, String iconLit) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10; -fx-cursor:hand;");
        HBox.setHgrow(card, Priority.ALWAYS);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(PRIMARY));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        t.setTextFill(Color.web(PRIMARY));

        Label s = new Label(sub);
        s.setFont(Font.font("Arial", 11));
        s.setTextFill(Color.web("#64748B"));
        s.setWrapText(true);

        card.getChildren().addAll(icon, t, s);
        return card;
    }

    private HBox infoRow(String leftText, String rightText) {
        Label l = new Label(leftText); l.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label r = new Label(rightText); r.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");
        return new HBox(l, sp, r);
    }

    private VBox createCard() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        return v;
    }

    private VBox createAdminSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label adminLabel = new Label("ADMIN DASHBOARD");
        adminLabel.setTextFill(Color.web(LIGHT));
        adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        brandTexts.getChildren().addAll(logo, adminLabel);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"Events", "fas-calendar-check"},
                {"Calendar", "fas-calendar"},
                {"Customers", "fas-users"},
                {"Organizers", "fas-user-tie"},
                {"Vendors", "fas-truck"},
                {"Conflict Detection", "fas-shield-alt"},
                {"Reports & Analytics", "fas-chart-pie"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab)));
        }

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false),
                createSidebarButton("Profile", "fas-user", false),
                createSidebarButton("Settings", "fas-cog", false),
                createSidebarButton("Support", "fas-question-circle", activeTab.equals("Support")),
                createSidebarButton("About Us", "fas-info-circle", activeTab.equals("About Us"))
        );

        sidebar.getChildren().addAll(brandBox, menu, sidebarSpace, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(PRIMARY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT + "; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-font-size:12px; -fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px; -fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08); -fx-text-fill:white; -fx-font-size:12px; -fx-background-radius:6; -fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }
}
