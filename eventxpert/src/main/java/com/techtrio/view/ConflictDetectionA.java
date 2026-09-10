package com.techtrio.view;

import java.util.List;
import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Conflict;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ConflictDetectionA {

    private Scene conflictDetectionScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private final AdminController adminController = new AdminController();
    private final ObservableList<Conflict> conflictData = FXCollections.observableArrayList();

    private TableView<Conflict> table = new TableView<>();
    private TextField search = new TextField();

    private Label totalConflictsValue;
    private Label criticalConflictsValue;
    private Label pendingResolutionValue;
    private Label resolvedValue;

    public Scene getconflictDetectionScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Dashboard-matched Sidebar
        root.setLeft(createAdminSidebar("Conflict Detection"));

        // 2. Main Scroll Content (No TopBar)
        VBox main = new VBox(18);
        main.setPadding(new Insets(24, 32, 28, 32));
        main.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Conflict Detection & Resolution");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Identify and resolve venue, scheduling, vendor and organizer overlaps in real-time.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, sub);

        // 4 KPI Cards
        totalConflictsValue = new Label("0");
        criticalConflictsValue = new Label("0");
        pendingResolutionValue = new Label("0");
        resolvedValue = new Label("0");

        HBox stats = new HBox(12,
                createKpiCard("TOTAL CONFLICTS", totalConflictsValue, "All detected issues", "fas-exclamation-triangle", PRIMARY),
                createKpiCard("CRITICAL OVERLAPS", criticalConflictsValue, "Immediate action", "fas-fire", "#DC2626"),
                createKpiCard("PENDING REVIEW", pendingResolutionValue, "Needs admin fix", "fas-clock", "#D97706"),
                createKpiCard("RESOLVED", resolvedValue, "Successfully cleared", "fas-check-circle", "#16A34A")
        );

        // Filter Bar & Table Card
        VBox conflictCard = new VBox(12);
        conflictCard.setPadding(new Insets(18));
        conflictCard.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        search.setPromptText("Search by resource, event ID or type...");
        search.setPrefWidth(280);
        search.setStyle("-fx-padding: 6 10; -fx-border-color:" + BORDER + "; -fx-border-radius:6;");
        search.textProperty().addListener((a, b, c) -> searchData());

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("All Types", "Organizer", "Vendor", "Venue", "Schedule");
        type.setValue("All Types");
        type.setStyle("-fx-border-color:" + BORDER + "; -fx-border-radius:6;");

        ComboBox<String> severity = new ComboBox<>();
        severity.getItems().addAll("All Severity", "Critical", "High", "Medium");
        severity.setValue("All Severity");
        severity.setStyle("-fx-border-color:" + BORDER + "; -fx-border-radius:6;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button clearBtn = new Button("Clear Filters");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            search.clear();
            type.setValue("All Types");
            severity.setValue("All Severity");
            table.setItems(conflictData);
        });

        filterBar.getChildren().addAll(search, type, severity, sp, clearBtn);

        createTable();

        conflictCard.getChildren().addAll(filterBar, table);
        main.getChildren().addAll(heading, stats, conflictCard);

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadConflictsAsync();

        conflictDetectionScene = new Scene(root, 1550, 785);
        return conflictDetectionScene;
    }

    private void createTable() {
        table.getColumns().clear();
        table.setItems(conflictData);
        table.setPrefHeight(360);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Conflict, String> cType = col("Conflict Type", Conflict::getType);
        TableColumn<Conflict, String> cEvents = col("Conflicting Events", c -> safe(c.getEventId()) + " / " + safe(c.getConflictingEventId()));
        TableColumn<Conflict, String> cDate = col("Date", Conflict::getDate);
        TableColumn<Conflict, String> cRes = col("Resource / Asset", Conflict::getResource);
        TableColumn<Conflict, String> cSev = col("Severity", Conflict::getSeverity);
        TableColumn<Conflict, String> cStat = col("Status", Conflict::getStatus);

        TableColumn<Conflict, Void> action = new TableColumn<>("Action");
        action.setPrefWidth(95);
        action.setCellFactory(c -> new TableCell<>() {
            final Button review = new Button("Review →");
            {
                review.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
                review.setOnAction(e -> {
                    Conflict selected = getTableView().getItems().get(getIndex());
                    if (selected != null) {
                        AdminContext.setSelectedEventId(selected.getEventId());
                        AdminContext.setSelectedConflictId(selected.getConflictId());
                        Stage stage = (Stage) review.getScene().getWindow();
                        NavigationManager.navigate("Conflict Detection Review", stage);
                    }
                });
            }

            @Override
            protected void updateItem(Void x, boolean empty) {
                super.updateItem(x, empty);
                setGraphic(empty ? null : review);
            }
        });

        table.getColumns().addAll(cType, cEvents, cDate, cRes, cSev, cStat, action);
    }

    private void loadConflictsAsync() {
        Task<List<Conflict>> task = new Task<>() {
            @Override
            protected List<Conflict> call() {
                return adminController.getAllConflicts();
            }
        };

        task.setOnSucceeded(e -> {
            List<Conflict> conflicts = task.getValue();
            Platform.runLater(() -> {
                conflictData.clear();
                if (conflicts != null) {
                    conflictData.addAll(conflicts);
                }
                updateStatistics(conflicts);
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void updateStatistics(List<Conflict> conflicts) {
        int total = 0, critical = 0, pending = 0, resolved = 0;
        if (conflicts != null) {
            total = conflicts.size();
            for (Conflict c : conflicts) {
                if (c == null) continue;
                if ("Critical".equalsIgnoreCase(c.getSeverity())) critical++;
                if ("Resolved".equalsIgnoreCase(c.getStatus())) resolved++;
                else pending++;
            }
        }
        totalConflictsValue.setText(String.valueOf(total));
        criticalConflictsValue.setText(String.valueOf(critical));
        pendingResolutionValue.setText(String.valueOf(pending));
        resolvedValue.setText(String.valueOf(resolved));
    }

    private void searchData() {
        String q = search.getText().toLowerCase().trim();
        table.setItems(conflictData.filtered(x ->
                q.isEmpty()
                || (x.getType() != null && x.getType().toLowerCase().contains(q))
                || (x.getResource() != null && x.getResource().toLowerCase().contains(q))
                || (x.getEventId() != null && x.getEventId().toLowerCase().contains(q))
                || (x.getConflictingEventId() != null && x.getConflictingEventId().toLowerCase().contains(q))
        ));
    }

    private TableColumn<Conflict, String> col(String title, java.util.function.Function<Conflict, String> f) {
        TableColumn<Conflict, String> c = new TableColumn<>(title);
        c.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(safe(f.apply(x.getValue()))));
        return c;
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s.trim();
    }

    private VBox createKpiCard(String title, Label valLbl, String sub, String iconLit, String colorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(colorHex));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        t.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(icon, t);

        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valLbl.setTextFill(Color.web(PRIMARY));

        Label s = new Label(sub);
        s.setFont(Font.font("Arial", 10));
        s.setTextFill(Color.web("#94A3B8"));

        card.getChildren().addAll(top, valLbl, s);
        return card;
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