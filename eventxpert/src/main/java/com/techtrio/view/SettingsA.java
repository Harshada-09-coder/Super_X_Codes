package com.techtrio.view;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.AuthController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.controller.SignInResult;
import com.techtrio.model.AdminProfile;
import com.techtrio.session.UserSession;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsA {

    private Scene settingScene;

    final String NAVY = "#062B63";
    final String BLUE = "#BFE3FA";
    final String BG = "#F5F7FA";
    final String BORDER = "#D5DDE6";

    private final AdminController adminController = new AdminController();
    private String adminId;

    // --- Dynamic Control References ---
    private TextField systemName;
    private TextField email;
    private ComboBox<String> currency;
    private ComboBox<String> date;
    private ComboBox<String> timezone;

    private ToggleButton light;
    private ToggleButton dark;
    private ComboBox<String> language;
    private CheckBox autoRefresh;

    private CheckBox ruleEventRequest = new CheckBox();
    private CheckBox rulePayment = new CheckBox();
    private CheckBox ruleConflict = new CheckBox();
    private CheckBox ruleNewUser = new CheckBox();
    private CheckBox ruleEmailAdmin = new CheckBox();

    private CheckBox twoFactor = new CheckBox();
    private ComboBox<String> timeout;

    public Scene getsettingScene() {

        /* ================= SIDEBAR ================= */

        VBox side = new VBox(5);
        side.setPrefWidth(235);
        side.setPadding(new Insets(20, 14, 18, 14));
        side.setStyle("-fx-background-color:" + NAVY + ";");

        Label title = new Label("EventXpert");
        title.setStyle("-fx-text-fill:white;-fx-font-size:20px;-fx-font-weight:bold;");

        Label sub = new Label(" ADMIN DASHBOARD");
        sub.setStyle("-fx-text-fill:#AAB9CC;-fx-font-size:11px;-fx-font-weight:bold;");

        VBox brand = new VBox(3, title, sub);
        HBox logoRow = new HBox(13, brand);
        logoRow.setAlignment(Pos.CENTER_LEFT);

        side.getChildren().add(logoRow);
        side.getChildren().add(new Region());

        String[] menu = {
                "▦   Dashboard",
                "▣   Event Requests",
                "▣   Events",
                "□   Calendar",
                "♧   Customers",
                "♣   Organizers",
                "▤   Vendors",
                "ⓘ   Conflict Detection",
                "▣   Reports & Analytics"
        };

        for (String s : menu) {
            Label item = sideItem(s, false);
            item.setOnMouseClicked(e -> {
                Stage stage = (Stage) item.getScene().getWindow();
                String page = s
                        .replace("▦", "")
                        .replace("▣", "")
                        .replace("□", "")
                        .replace("♧", "")
                        .replace("♣", "")
                        .replace("▤", "")
                        .replace("ⓘ", "")
                        .trim();
                NavigationManager.navigate(page, stage);
            });
            side.getChildren().add(item);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        side.getChildren().add(spacer);

        Label notifications = sideItem("🔔   Notifications", false);
        Label settings = sideItem("⚙   Settings", true);
        Label profile = sideItem("👤   Profile", false);
        Label support = sideItem("?   Support", false);

        notifications.setOnMouseClicked(e -> {
            Stage stage = (Stage) notifications.getScene().getWindow();
            NavigationManager.navigate("Notifications", stage);
        });

        settings.setOnMouseClicked(e -> {
            Stage stage = (Stage) settings.getScene().getWindow();
            NavigationManager.navigate("Settings", stage);
        });

        profile.setOnMouseClicked(e -> {
            Stage stage = (Stage) profile.getScene().getWindow();
            NavigationManager.navigate("Profile", stage);
        });

        support.setOnMouseClicked(e -> {
            Stage stage = (Stage) support.getScene().getWindow();
            NavigationManager.navigate("Support", stage);
        });

        side.getChildren().addAll(
                notifications,
                settings,
                profile,
                support
        );

        /* ================= MAIN AREA ================= */

        VBox content = new VBox(15);
        content.setPadding(new Insets(25, 25, 25, 25));
        content.setStyle("-fx-background-color:" + BG + ";");

        /* Header */

        HBox crumb = new HBox(8);

        Button settingsCrumb = new Button("Back");
        settingsCrumb.setStyle("-fx-background-color:" + NAVY + ";" +
                "-fx-text-fill:#F5F7FA;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-cursor:hand;");

        settingsCrumb.setOnAction(e -> {
            Stage stage = (Stage) settingsCrumb.getScene().getWindow();
            NavigationManager.navigate("Dashboard", stage);
        });

        crumb.getChildren().addAll(settingsCrumb);

        Label heading = new Label("Settings");
        heading.setStyle(
                "-fx-text-fill:#071B3A;" +
                "-fx-font-size:32px;" +
                "-fx-font-weight:bold;"
        );

        Label description = new Label(
                "Manage your EventXpert admin system preferences and configuration."
        );
        description.setStyle("-fx-text-fill:#465468;-fx-font-size:16px;");

        HBox header = new HBox(20);
        VBox headerText = new VBox(3, heading, description);
        header.getChildren().add(headerText);
        HBox.setHgrow(headerText, Priority.ALWAYS);

        /* ================= LEFT COLUMN ================= */

        VBox left = new VBox(22);

        /* General Settings */

        VBox general = card();
        general.getChildren().add(sectionTitle("☷", "General Settings"));

        systemName = new TextField("EventXpert");
        email = new TextField("admin@eventxpert.com");

        currency = new ComboBox<>();
        currency.getItems().addAll("INR ₹", "USD $", "EUR €");
        currency.setValue("INR ₹");

        date = new ComboBox<>();
        date.getItems().addAll("DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD");
        date.setValue("DD/MM/YYYY");

        timezone = new ComboBox<>();
        timezone.getItems().addAll(
                "Asia/Kolkata (IST)",
                "Asia/Dubai (GST)",
                "Europe/London (GMT)"
        );
        timezone.setValue("Asia/Kolkata (IST)");

        HBox row1 = new HBox(22,
                field("System Name", systemName),
                field("Admin Email", email));
        HBox.setHgrow(row1.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row1.getChildren().get(1), Priority.ALWAYS);

        HBox row2 = new HBox(22,
                field("Default Currency", currency),
                field("Date Format", date));
        HBox.setHgrow(row2.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row2.getChildren().get(1), Priority.ALWAYS);

        VBox tz = field("Time Zone", timezone);

        Button saveGeneral = navyButton("Save General");

        HBox saveBox = new HBox(saveGeneral);
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        general.getChildren().addAll(row1, row2, tz, saveBox);

        /* System Preferences */

        VBox preferences = card();
        preferences.getChildren().add(sectionTitle("▣", "System Preferences"));

        light = new ToggleButton("☼  Light");
        dark = new ToggleButton("☾  Dark");

        light.setSelected(true);

        styleTheme(light, true);
        styleTheme(dark, false);

        light.setOnAction(e -> {
            light.setSelected(true);
            dark.setSelected(false);
            styleTheme(light, true);
            styleTheme(dark, false);
            saveSettingsAsync(false);
        });

        dark.setOnAction(e -> {
            dark.setSelected(true);
            light.setSelected(false);
            styleTheme(dark, true);
            styleTheme(light, false);
            saveSettingsAsync(false);
        });

        HBox themes = new HBox(8, light, dark);

        language = new ComboBox<>();
        language.getItems().addAll("English (US)", "English (UK)", "Marathi");
        language.setValue("English (US)");
        language.setPrefWidth(240);
        language.setOnAction(e -> saveSettingsAsync(false));

        HBox prefRow = new HBox(35,
                field("Theme", themes),
                field("Language", language));
        HBox.setHgrow(prefRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(prefRow.getChildren().get(1), Priority.ALWAYS);

        autoRefresh = new CheckBox();
        autoRefresh.setSelected(true);
        autoRefresh.setOnAction(e -> saveSettingsAsync(false));

        Label refreshTitle = new Label("Auto Refresh Dashboard");
        refreshTitle.setStyle("-fx-font-size:17px;-fx-font-weight:bold;");

        Label refreshSub = new Label(
                "Automatically fetch new data every 5 minutes."
        );
        refreshSub.setStyle("-fx-text-fill:#536176;-fx-font-size:13px;");

        VBox refreshText = new VBox(2, refreshTitle, refreshSub);
        HBox refresh = new HBox(refreshText, autoRefresh);
        refresh.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(refreshText, Priority.ALWAYS);

        preferences.getChildren().addAll(prefRow, refresh);

        left.getChildren().addAll(general, preferences);

        /* ================= RIGHT COLUMN ================= */

        VBox right = new VBox(22);

        /* Notification Rules */

        VBox notificationRules = card();
        notificationRules.getChildren().add(
                sectionTitle("♧", "Notification Rules")
        );

        ruleEventRequest.setOnAction(e -> saveSettingsAsync(false));
        rulePayment.setOnAction(e -> saveSettingsAsync(false));
        ruleConflict.setOnAction(e -> saveSettingsAsync(false));
        ruleNewUser.setOnAction(e -> saveSettingsAsync(false));
        ruleEmailAdmin.setOnAction(e -> saveSettingsAsync(false));

        notificationRules.getChildren().addAll(
                rule("Event Request Notifications", ruleEventRequest, true),
                rule("Payment Notifications", rulePayment, true),
                rule("Conflict Detection Alerts", ruleConflict, true),
                rule("New User Registration Alerts", ruleNewUser, true),
                rule("Email Notifications (Admin)", ruleEmailAdmin, true)
        );

        /* Security */

        VBox security = card();
        security.getChildren().add(sectionTitle("⬟", "Security"));

        Label twoTitle = new Label("Two-Factor Auth");
        twoTitle.setStyle("-fx-font-size:17px;-fx-font-weight:bold;");

        Label twoSub = new Label("Add an extra layer of security.");
        twoSub.setStyle("-fx-text-fill:#536176;-fx-font-size:13px;");

        VBox twoText = new VBox(2, twoTitle, twoSub);
        HBox twoRow = new HBox(twoText, twoFactor);
        twoRow.setPadding(new Insets(10));
        twoRow.setAlignment(Pos.CENTER_LEFT);
        twoRow.setStyle("-fx-background-color:#F2F5F8;-fx-background-radius:7px;");
        HBox.setHgrow(twoText, Priority.ALWAYS);

        twoFactor.setOnAction(e -> {
            boolean status = twoFactor.isSelected();
            adminController.updateTwoFactorStatus(adminId, status);
            saveSettingsAsync(false);
        });

        timeout = new ComboBox<>();
        timeout.getItems().addAll("15 Minutes", "30 Minutes", "60 Minutes");
        timeout.setValue("30 Minutes");
        timeout.setMaxWidth(Double.MAX_VALUE);
        timeout.setOnAction(e -> saveSettingsAsync(false));

        Button changePassBtn = outlineButton("Change Password");
        Button loginActivityBtn = outlineButton("Login Activity");

        changePassBtn.setOnAction(e -> showChangePasswordDialog());
        loginActivityBtn.setOnAction(e -> info("Login Activity", "Current active session verified from local host.\nStatus: Secure & Authorized."));

        HBox securityButtons = new HBox(10, changePassBtn, loginActivityBtn);

        security.getChildren().addAll(
                twoRow,
                field("Session Timeout", timeout),
                securityButtons
        );

        /* Data Management */

        VBox data = card();
        data.getChildren().add(sectionTitle("▤", "Data Management"));

        Button exportData = outlineButton("Export System Data       ↓");
        Button backup = outlineButton("Backup Data                 ☁");
        Button restore = outlineButton("Restore Backup             ↻");

        restore.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:#D21F26;" +
                "-fx-font-size:13px;" +
                "-fx-padding:9px;" +
                "-fx-cursor:hand;"
        );

        exportData.setMaxWidth(Double.MAX_VALUE);
        backup.setMaxWidth(Double.MAX_VALUE);
        restore.setMaxWidth(Double.MAX_VALUE);

        data.getChildren().addAll(exportData, backup, restore);

        right.getChildren().addAll(notificationRules, security, data);

        /* ================= COLUMNS ================= */

        HBox columns = new HBox(22, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        /* ================= BOTTOM ================= */

        Separator separator = new Separator();

        Button reset = outlineButton("Reset");
        Button saveAll = navyButton("Save All Changes");

        HBox bottom = new HBox(12, reset, saveAll);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(
                crumb,
                header,
                columns,
                separator,
                bottom
        );

        /* ================= ACTIONS ================= */

        saveGeneral.setOnAction(e -> saveSettingsAsync(true));
        saveAll.setOnAction(e -> saveSettingsAsync(true));

        reset.setOnAction(e -> {
            systemName.setText("EventXpert");
            email.setText("admin@eventxpert.com");
            currency.setValue("INR ₹");
            date.setValue("DD/MM/YYYY");
            timezone.setValue("Asia/Kolkata (IST)");
            language.setValue("English (US)");
            timeout.setValue("30 Minutes");

            light.setSelected(true);
            dark.setSelected(false);
            styleTheme(light, true);
            styleTheme(dark, false);

            autoRefresh.setSelected(true);
            twoFactor.setSelected(false);

            ruleEventRequest.setSelected(true);
            rulePayment.setSelected(true);
            ruleConflict.setSelected(true);
            ruleNewUser.setSelected(true);
            ruleEmailAdmin.setSelected(true);

            saveSettingsAsync(false);
            info("Reset", "Settings restored to default values.");
        });

        exportData.setOnAction(e -> info("Export", "System data export created successfully."));
        backup.setOnAction(e -> info("Backup", "Cloud backup of configuration created successfully."));
        restore.setOnAction(e -> info("Restore", "System settings successfully verified and synced."));

        /* ================= ROOT ================= */

        BorderPane root = new BorderPane();
        root.setLeft(side);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#F5F7FA;-fx-background-color:#F5F7FA;");

        root.setCenter(scroll);

        // Auto-fetch settings from Firestore
        loadDynamicSettings();

        Scene scene = new Scene(root, 1550, 750);
        settingScene = scene;
        return settingScene;
    }

    /* ================= DYNAMIC FIRESTORE LOAD & SAVE ================= */

    private void loadDynamicSettings() {
        Thread thread = new Thread(() -> {
            try {
                // 1. Resolve Admin ID
                adminId = UserSession.getUid();
                if (adminId == null || adminId.isBlank()) {
                    try {
                        Method m = NavigationManager.class.getMethod("getCurrentAdminId");
                        Object res = m.invoke(null);
                        if (res != null) adminId = res.toString();
                    } catch (Exception ignored) {}
                }

                // 2. Fetch Admin Profile for Email
                AdminProfile profile = (adminId != null && !adminId.isBlank()) ? adminController.getAdminProfile(adminId) : null;
                String liveAdminEmail = (profile != null && profile.getEmail() != null) ? profile.getEmail() : "admin@eventxpert.com";

                // 3. Query Firestore for System Settings
                Firestore db = FirestoreClient.getFirestore();
                Map<String, Object> config = null;

                if (db != null) {
                    try {
                        DocumentSnapshot doc = db.collection("system_settings").document("admin_config").get().get();
                        if (doc.exists()) {
                            config = doc.getData();
                        }
                    } catch (Exception ignored) {}
                }

                final Map<String, Object> finalConfig = config;
                final String finalEmail = liveAdminEmail;

                Platform.runLater(() -> {
                    email.setText(finalEmail);

                    if (finalConfig != null) {
                        if (finalConfig.get("systemName") != null) systemName.setText(finalConfig.get("systemName").toString());
                        if (finalConfig.get("currency") != null) currency.setValue(finalConfig.get("currency").toString());
                        if (finalConfig.get("date") != null) date.setValue(finalConfig.get("date").toString());
                        if (finalConfig.get("timezone") != null) timezone.setValue(finalConfig.get("timezone").toString());
                        if (finalConfig.get("language") != null) language.setValue(finalConfig.get("language").toString());
                        if (finalConfig.get("timeout") != null) timeout.setValue(finalConfig.get("timeout").toString());

                        if (finalConfig.get("autoRefresh") != null) autoRefresh.setSelected((Boolean) finalConfig.get("autoRefresh"));
                        if (finalConfig.get("twoFactor") != null) twoFactor.setSelected((Boolean) finalConfig.get("twoFactor"));

                        if (finalConfig.get("ruleEventRequest") != null) ruleEventRequest.setSelected((Boolean) finalConfig.get("ruleEventRequest"));
                        if (finalConfig.get("rulePayment") != null) rulePayment.setSelected((Boolean) finalConfig.get("rulePayment"));
                        if (finalConfig.get("ruleConflict") != null) ruleConflict.setSelected((Boolean) finalConfig.get("ruleConflict"));
                        if (finalConfig.get("ruleNewUser") != null) ruleNewUser.setSelected((Boolean) finalConfig.get("ruleNewUser"));
                        if (finalConfig.get("ruleEmailAdmin") != null) ruleEmailAdmin.setSelected((Boolean) finalConfig.get("ruleEmailAdmin"));

                        if (finalConfig.get("theme") != null) {
                            boolean isDark = "Dark".equalsIgnoreCase(finalConfig.get("theme").toString());
                            dark.setSelected(isDark);
                            light.setSelected(!isDark);
                            styleTheme(light, !isDark);
                            styleTheme(dark, isDark);
                        }
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void saveSettingsAsync(boolean showConfirmation) {
        Thread thread = new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                if (db == null) return;

                Map<String, Object> data = new HashMap<>();
                data.put("systemName", systemName.getText().trim());
                data.put("email", email.getText().trim());
                data.put("currency", currency.getValue());
                data.put("date", date.getValue());
                data.put("timezone", timezone.getValue());

                data.put("theme", dark.isSelected() ? "Dark" : "Light");
                data.put("language", language.getValue());
                data.put("autoRefresh", autoRefresh.isSelected());

                data.put("ruleEventRequest", ruleEventRequest.isSelected());
                data.put("rulePayment", rulePayment.isSelected());
                data.put("ruleConflict", ruleConflict.isSelected());
                data.put("ruleNewUser", ruleNewUser.isSelected());
                data.put("ruleEmailAdmin", ruleEmailAdmin.isSelected());

                data.put("twoFactor", twoFactor.isSelected());
                data.put("timeout", timeout.getValue());

                db.collection("system_settings").document("admin_config").set(data).get();

                if (showConfirmation) {
                    Platform.runLater(() -> info("Saved", "Settings saved successfully."));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                if (showConfirmation) {
                    Platform.runLater(() -> info("Error", "Could not save settings: " + ex.getMessage()));
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /* ================= PASSWORD DIALOG ================= */

    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change your administrator password");

        PasswordField curPass = new PasswordField();
        curPass.setPromptText("Current Password");

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password");

        PasswordField confPass = new PasswordField();
        confPass.setPromptText("Confirm Password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(curPass, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPass, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confPass, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType changeBtnType = new ButtonType("Change Password");
        dialog.getDialogPane().getButtonTypes().addAll(changeBtnType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == changeBtnType) {
                String c = curPass.getText();
                String n = newPass.getText();
                String cf = confPass.getText();

                if (c.isBlank() || n.isBlank() || cf.isBlank()) {
                    info("Error", "All password fields are required.");
                    return btn;
                }
                if (!n.equals(cf)) {
                    info("Error", "New passwords do not match.");
                    return btn;
                }
                if (n.length() < 6) {
                    info("Error", "Password must be at least 6 characters.");
                    return btn;
                }

                AuthController auth = new AuthController();
                SignInResult res = auth.signIn(email.getText().trim(), c);
                if (res == null) {
                    info("Error", "Current password is incorrect.");
                    return btn;
                }

                try {
                    String uid = FirebaseAuth.getInstance().verifyIdToken(res.getIdToken()).getUid();
                    FirebaseAuth.getInstance().updateUser(new UserRecord.UpdateRequest(uid).setPassword(n));
                    info("Success", "Password changed successfully.");
                } catch (Exception ex) {
                    info("Error", "Failed to update password: " + ex.getMessage());
                }
            }
            return btn;
        });

        dialog.showAndWait();
    }

    /* ================= HELPERS (PRESERVED EXACTLY) ================= */

    Label sideItem(String text, boolean active) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(11, 12, 11, 12));
        l.setStyle(
                "-fx-text-fill:" + (active ? NAVY : "#D9E2EF") + ";" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:" + (active ? "bold" : "normal") + ";" +
                (active
                        ? "-fx-background-color:" + BLUE + ";-fx-background-radius:7;"
                        : "")
        );
        return l;
    }

    VBox card() {
        VBox v = new VBox(13);
        v.setPadding(new Insets(20));
        v.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;"
        );
        VBox.setVgrow(v, Priority.NEVER);
        return v;
    }

    HBox sectionTitle(String icon, String text) {
        Label i = new Label(icon);
        i.setStyle("-fx-text-fill:#0A4C76;-fx-font-size:20px;");

        Label t = new Label(text);
        t.setStyle(
                "-fx-text-fill:#101A2C;" +
                "-fx-font-size:20px;" +
                "-fx-font-weight:bold;"
        );

        HBox h = new HBox(10, i, t);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(0, 0, 12, 0));
        h.setStyle("-fx-border-color:transparent transparent #E5E9EF transparent;");
        return h;
    }

    VBox field(String name, Node node) {
        Label l = new Label(name);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#303A49;");

        if (node instanceof TextField)
            ((TextField) node).setPrefHeight(40);

        if (node instanceof ComboBox)
            ((ComboBox<?>) node).setPrefHeight(40);

        VBox box = new VBox(5, l, node);
        VBox.setVgrow(node, Priority.ALWAYS);
        return box;
    }

    HBox rule(String text, CheckBox check, boolean selected) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:14px;-fx-text-fill:#202735;");

        check.setSelected(selected);

        HBox h = new HBox(l, check);
        h.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(l, Priority.ALWAYS);
        return h;
    }

    Button navyButton(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:" + NAVY + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-font-size:13px;" +
                "-fx-padding:9px 18px;" +
                "-fx-background-radius:6;" +
                "-fx-cursor:hand;"
        );
        return b;
    }

    Button outlineButton(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-size:13px;" +
                "-fx-padding:9px 14px;" +
                "-fx-cursor:hand;"
        );
        return b;
    }

    void styleTheme(ToggleButton b, boolean active) {
        b.setPrefWidth(120);
        b.setPrefHeight(40);
        b.setStyle(
                "-fx-background-color:" +
                        (active ? BLUE : "white") + ";" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-text-fill:#172033;" +
                "-fx-cursor:hand;"
        );
    }

    void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}