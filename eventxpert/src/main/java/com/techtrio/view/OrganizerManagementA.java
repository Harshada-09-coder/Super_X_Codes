package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.OrganizerProfile;
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

public class OrganizerManagementA {

    private Scene organizerScene;

    final String PRIMARY = "#002A60", SECONDARY = "#8CAFC6", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";

    TableView<OrganizerProfile> table;
    ObservableList<OrganizerProfile> data = FXCollections.observableArrayList();

    private Label totalOrganizersVal;
    private Label activeOrganizersVal;
    private Label newOrganizersVal;
    private Label inactiveOrganizersVal;

    Label label(String text, int size, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", size));
        l.setTextFill(Color.web(PRIMARY));
        if (bold) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    Button button(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:white;-fx-text-fill:" + PRIMARY +
                ";-fx-border-color:" + SECONDARY + ";-fx-border-radius:6;" +
                "-fx-background-radius:6;-fx-font-weight:bold;-fx-cursor:hand;");
        b.setPadding(new Insets(8, 14, 8, 14));
        return b;
    }

    VBox card() {
        VBox v = new VBox(8);
        v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:10;-fx-background-radius:10;");
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

    VBox statWithIcon(String title, Label valLabel, String sub, String iconLit, String iconColor) {
        VBox v = card();
        v.setPrefHeight(110);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        valLabel.setTextFill(Color.web(PRIMARY));

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(iconColor));

        Label t = new Label(title.toUpperCase());
        t.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        t.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(icon, t);

        Label subLbl = new Label(sub);
        subLbl.setFont(Font.font("Arial", 11));
        subLbl.setTextFill(Color.web("#94A3B8"));

        v.getChildren().addAll(top, valLabel, subLbl);
        return v;
    }

    void createTable() {
        table = new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(320);

        TableColumn<OrganizerProfile, String> orgNameCol = new TableColumn<>("Organization Name");
        orgNameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrganizationName() != null && !d.getValue().getOrganizationName().isBlank()
                        ? d.getValue().getOrganizationName()
                        : d.getValue().getName()
        ));

        TableColumn<OrganizerProfile, String> nameCol = new TableColumn<>("Contact Person");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getName() != null ? d.getValue().getName() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getMobileNo() != null ? d.getValue().getMobileNo() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getAddress() != null ? d.getValue().getAddress() : "N/A"
        ));

        TableColumn<OrganizerProfile, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOnboardingStatus() != null ? d.getValue().getOnboardingStatus() : "NEW"
        ));

        TableColumn<OrganizerProfile, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(c -> new TableCell<>() {
            Button b = button("View");

            {
                b.setOnAction(e -> {
                    OrganizerProfile selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedOrganizerId(selected.getOrganizerId());
                    Stage stage = (Stage) b.getScene().getWindow();
                    NavigationManager.navigate("Organizer Details", stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(orgNameCol, nameCol, emailCol, phoneCol, locationCol, statusCol, actionCol);
    }

    public Scene getorganizerScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        root.setLeft(createAdminSidebar("Organizers"));

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));

        Label title = new Label("Organizer Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Manage and monitor all registered organizers on EventXpert.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, sub);

        totalOrganizersVal = new Label("0");
        activeOrganizersVal = new Label("0");
        newOrganizersVal = new Label("0");
        inactiveOrganizersVal = new Label("0");

        HBox stats = new HBox(14,
                statWithIcon("Total Organizers", totalOrganizersVal, "Registered", "fas-user-tie", PRIMARY),
                statWithIcon("Active Organizers", activeOrganizersVal, "Currently active", "fas-check-circle", "#16A34A"),
                statWithIcon("New Organizers", newOrganizersVal, "Recent onboarding", "fas-user-plus", "#0284C7"),
                statWithIcon("Inactive Organizers", inactiveOrganizersVal, "Need attention", "fas-exclamation-circle", "#DC2626"));

        for (javafx.scene.Node n : stats.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        VBox organizerCard = card();

        HBox tableHead = new HBox();
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all = new Label("All Organizers");
        all.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        all.setTextFill(Color.web(PRIMARY));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        TextField tableSearch = new TextField();
        tableSearch.setPromptText("Search organizers...");
        tableSearch.setPrefWidth(280);
        tableSearch.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        Button filter = button("☰  Filter");
        filter.setOnAction(e -> tableSearch.clear());

        tableHead.getChildren().addAll(all, space, tableSearch, filter);

        createTable();

        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        Button previous = button("Previous");
        Button next = button("Next");

        pagination.getChildren().addAll(previous, next);

        HBox footer = new HBox();
        Region fspace = new Region();
        HBox.setHgrow(fspace, Priority.ALWAYS);
        footer.getChildren().addAll(fspace, pagination);

        organizerCard.getChildren().addAll(tableHead, table, new Separator(), footer);
        content.getChildren().addAll(heading, stats, organizerCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadOrganizersDataAsync();

        Scene scene = new Scene(root, 1550, 785);
        organizerScene = scene;
        return organizerScene;
    }

    private void loadOrganizersDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<OrganizerProfile> organizers = controller.getAllOrganizers();

                Platform.runLater(() -> {
                    data.clear();
                    int activeCount = 0;
                    int newCount = 0;
                    int inactiveCount = 0;

                    for (OrganizerProfile org : organizers) {
                        String st = org.getOnboardingStatus() != null ? org.getOnboardingStatus().toUpperCase() : "NEW";
                        if ("EXISTING".equals(st) || "ACTIVE".equals(st) || org.isProfileCompleted()) {
                            activeCount++;
                        } else if ("NEW".equals(st)) {
                            newCount++;
                        } else {
                            inactiveCount++;
                        }
                        data.add(org);
                    }

                    totalOrganizersVal.setText(String.valueOf(organizers.size()));
                    activeOrganizersVal.setText(String.valueOf(activeCount));
                    newOrganizersVal.setText(String.valueOf(newCount));
                    inactiveOrganizersVal.setText(String.valueOf(inactiveCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}