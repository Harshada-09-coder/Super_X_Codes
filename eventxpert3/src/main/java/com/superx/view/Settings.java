package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Settings extends Application {


  
    final String NAVY = "#062B63";
    final String BLUE = "#BFE3FA";
    final String BG = "#F5F7FA";
    final String BORDER = "#D5DDE6";

    @Override
    public void start(Stage stage) {

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
        HBox logoRow = new HBox(13,brand);
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
            side.getChildren().add(item);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        side.getChildren().add(spacer);

        side.getChildren().add(sideItem("🔔   Notifications", false));
        side.getChildren().add(sideItem("⚙   Settings", true));
        side.getChildren().add(sideItem("👤   Profile", false));
        side.getChildren().add(sideItem("?   Support", false));

        /* ================= MAIN AREA ================= */

        VBox content = new VBox(15);
        content.setPadding(new Insets(25, 25, 25, 25));
        content.setStyle("-fx-background-color:" + BG + ";");

        /* Header */

        HBox crumb = new HBox(8);
       
      
        Button settingsCrumb = new Button("Back");
        settingsCrumb.setStyle("-fx-background-color:NAVY;" +
        "-fx-text-fill:#F5F7FA;"+
        "-fx-font-size:15px;" +
        "-fx-font-weight:bold;"
        );

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

        TextField systemName = new TextField("EventXpert");
        TextField email = new TextField("admin@eventxpert.com");

        ComboBox<String> currency = new ComboBox<>();
        currency.getItems().addAll("INR ₹", "USD $", "EUR €");
        currency.setValue("INR ₹");

        ComboBox<String> date = new ComboBox<>();
        date.getItems().addAll("DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD");
        date.setValue("DD/MM/YYYY");

        ComboBox<String> timezone = new ComboBox<>();
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

        ToggleButton light = new ToggleButton("☼  Light");
        ToggleButton dark = new ToggleButton("☾  Dark");

        light.setSelected(true);

        styleTheme(light, true);
        styleTheme(dark, false);

        light.setOnAction(e -> {
            light.setSelected(true);
            dark.setSelected(false);
            styleTheme(light, true);
            styleTheme(dark, false);
        });

        dark.setOnAction(e -> {
            dark.setSelected(true);
            light.setSelected(false);
            styleTheme(dark, true);
            styleTheme(light, false);
        });

        HBox themes = new HBox(8, light, dark);

        ComboBox<String> language = new ComboBox<>();
        language.getItems().addAll("English (US)", "English (UK)", "Marathi");
        language.setValue("English (US)");
        language.setPrefWidth(240);

        HBox prefRow = new HBox(35,
                field("Theme", themes),
                field("Language", language));
        HBox.setHgrow(prefRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(prefRow.getChildren().get(1), Priority.ALWAYS);

        CheckBox autoRefresh = new CheckBox();
        autoRefresh.setSelected(true);

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

        VBox notifications = card();
        notifications.getChildren().add(sectionTitle("♧", "Notification Rules"));

        notifications.getChildren().addAll(
                rule("Event Request Notifications", true),
                rule("Payment Notifications", true),
                rule("Conflict Detection Alerts", true),
                rule("New User Registration Alerts", true),
                rule("Email Notifications (Admin)", true)
        );

        /* Security */

        VBox security = card();
        security.getChildren().add(sectionTitle("⬟", "Security"));

        CheckBox twoFactor = new CheckBox();

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

        ComboBox<String> timeout = new ComboBox<>();
        timeout.getItems().addAll("15 Minutes", "30 Minutes", "60 Minutes");
        timeout.setValue("30 Minutes");
        timeout.setMaxWidth(Double.MAX_VALUE);

        HBox securityButtons = new HBox(10,
                outlineButton("Change Password"),
                outlineButton("Login Activity"));

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
                "-fx-padding:9px;"
        );

        exportData.setMaxWidth(Double.MAX_VALUE);
        backup.setMaxWidth(Double.MAX_VALUE);
        restore.setMaxWidth(Double.MAX_VALUE);

        data.getChildren().addAll(exportData, backup, restore);

        right.getChildren().addAll(notifications, security, data);

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

        saveGeneral.setOnAction(e ->
                info("Saved", "General settings saved successfully."));

        saveAll.setOnAction(e ->
                info("Saved", "All settings have been saved successfully."));

        reset.setOnAction(e -> {
            systemName.setText("EventXpert");
            email.setText("admin@eventxpert.com");
            currency.setValue("INR ₹");
            date.setValue("DD/MM/YYYY");
            timezone.setValue("Asia/Kolkata (IST)");
            language.setValue("English (US)");
            light.setSelected(true);
            dark.setSelected(false);
            autoRefresh.setSelected(true);
            twoFactor.setSelected(false);
            info("Reset", "Settings restored to default values.");
        });

        exportData.setOnAction(e ->
                info("Export", "System data export started."));

        backup.setOnAction(e ->
                info("Backup", "System backup created successfully."));

        restore.setOnAction(e ->
                info("Restore", "Backup restore option selected."));

        /* ================= ROOT ================= */

        BorderPane root = new BorderPane();
        root.setLeft(side);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#F5F7FA;-fx-background-color:#F5F7FA;");

        root.setCenter(scroll);

        Scene scene = new Scene(root, 1550, 750);
        stage.setTitle("EventXpert - Settings");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(650);
        stage.show();
    }

    /* ================= HELPERS ================= */

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

    HBox rule(String text, boolean selected) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:14px;-fx-text-fill:#202735;");

        CheckBox check = new CheckBox();
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
                "-fx-background-radius:6;"
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
                "-fx-padding:9px 14px;"
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
                "-fx-text-fill:#172033;"
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