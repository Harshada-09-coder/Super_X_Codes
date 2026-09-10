package com.techtrio.view;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.AdminProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

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

public class SettingsA {

    private Scene settingScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private final AdminController adminController = new AdminController();
    private String adminId;

    private TextField systemName;
    private TextField email;
    private ComboBox<String> currency;
    private ComboBox<String> timezone;
    private CheckBox autoRefresh;
    private CheckBox twoFactor;
    private StackPane rootLayer;

    public Scene getsettingScene() {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        root.setLeft(createAdminSidebar("Settings"));

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("System Settings & Preferences");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label description = new Label("Manage EventXpert platform defaults, currency, email rules and automated sync.");
        description.setFont(Font.font("Arial", 13));
        description.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, description);

        HBox columns = new HBox(18);

        VBox general = createCard();
        Label gTitle = new Label("General Configuration");
        gTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        gTitle.setTextFill(Color.web(PRIMARY));

        systemName = new TextField("EventXpert");
        email = new TextField("admin@eventxpert.com");

        currency = new ComboBox<>();
        currency.getItems().addAll("INR ₹", "USD $", "EUR €");
        currency.setValue("INR ₹");

        timezone = new ComboBox<>();
        timezone.getItems().addAll("Asia/Kolkata (IST)", "Asia/Dubai (GST)", "UTC");
        timezone.setValue("Asia/Kolkata (IST)");

        GridPane gGrid = new GridPane();
        gGrid.setHgap(16); gGrid.setVgap(10);
        gGrid.add(new Label("Platform Brand Name:"), 0, 0); gGrid.add(systemName, 0, 1);
        gGrid.add(new Label("Notification Admin Email:"), 1, 0); gGrid.add(email, 1, 1);
        gGrid.add(new Label("Base Currency:"), 0, 2); gGrid.add(currency, 0, 3);
        gGrid.add(new Label("System Timezone:"), 1, 2); gGrid.add(timezone, 1, 3);

        general.getChildren().addAll(gTitle, new Separator(), gGrid);
        HBox.setHgrow(general, Priority.ALWAYS);

        VBox security = createCard();
        Label sTitle = new Label("Security & Automation Rules");
        sTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sTitle.setTextFill(Color.web(PRIMARY));

        autoRefresh = new CheckBox("Real-time Live Sync (Active)");
        autoRefresh.setSelected(true);

        twoFactor = new CheckBox("Enforce 2FA for Super Admin Login");
        twoFactor.setSelected(true);

        VBox rulesBox = new VBox(10, autoRefresh, twoFactor);
        rulesBox.setPadding(new Insets(6, 0, 6, 0));

        Button saveAll = new Button("Save Configuration Changes");
        saveAll.setStyle("-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:9 18; -fx-background-radius:6; -fx-cursor:hand;");
        saveAll.setOnAction(e -> saveSettingsAsync(true));

        HBox btnRow = new HBox(saveAll);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        security.getChildren().addAll(sTitle, new Separator(), rulesBox, btnRow);
        HBox.setHgrow(security, Priority.ALWAYS);

        columns.getChildren().addAll(general, security);
        content.getChildren().addAll(heading, columns);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);
        rootLayer.getChildren().add(root);

        loadDynamicSettings();

        settingScene = new Scene(rootLayer, 1550, 785);
        return settingScene;
    }

    private void loadDynamicSettings() {
        Thread thread = new Thread(() -> {
            try {
                adminId = UserSession.getUid();
                AdminProfile profile = (adminId != null && !adminId.isBlank()) ? adminController.getAdminProfile(adminId) : null;
                String liveAdminEmail = (profile != null && profile.getEmail() != null) ? profile.getEmail() : "admin@eventxpert.com";

                Platform.runLater(() -> email.setText(liveAdminEmail));
            } catch (Exception ignored) {}
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void saveSettingsAsync(boolean notify) {
        Thread thread = new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                if (db != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("systemName", systemName.getText().trim());
                    data.put("email", email.getText().trim());
                    data.put("currency", currency.getValue());
                    data.put("timezone", timezone.getValue());
                    data.put("autoRefresh", autoRefresh.isSelected());
                    data.put("twoFactor", twoFactor.isSelected());
                    db.collection("system_settings").document("admin_config").set(data).get();
                }

                if (notify) {
                    Platform.runLater(() -> showInAppModal("Settings Saved", "System settings successfully synchronized with Firestore."));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /* ================= FIXED SLENDER RECTANGULAR POPUP ================= */
    private void showInAppModal(String title, String message) {
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

        Circle c = new Circle(18, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
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

    private VBox createCard() {
        VBox v = new VBox(12);
        v.setPadding(new Insets(18));
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
                createSidebarButton("Settings", "fas-cog", activeTab.equals("Settings")),
                createSidebarButton("Support", "fas-question-circle", false)
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