package com.techtrio.view;

import java.util.List;
import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.VendorProfile;
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

public class VendorManagementA {

    private Scene vendorScene;
    private final String PRIMARY = "#002A60", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";

    private TableView<VendorProfile> table;
    private ObservableList<VendorProfile> data = FXCollections.observableArrayList();

    private Label totalVendorsVal;
    private Label activeVendorsVal;
    private Label newVendorsVal;
    private Label inactiveVendorsVal;

    public Scene getvendorScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Dashboard Sidebar
        root.setLeft(createAdminSidebar("Vendors"));

        // 2. Main Scroll Content (No TopBar)
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Vendor Directory");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Manage verified service vendors, categories, and track active bookings live.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, sub);

        totalVendorsVal = new Label("0");
        activeVendorsVal = new Label("0");
        newVendorsVal = new Label("0");
        inactiveVendorsVal = new Label("0");

        HBox stats = new HBox(12,
                createKpiCard("TOTAL VENDORS", totalVendorsVal, "Registered in Firestore", "fas-truck", PRIMARY),
                createKpiCard("ACTIVE VENDORS", activeVendorsVal, "Currently on duty", "fas-check-circle", "#16A34A"),
                createKpiCard("NEW ONBOARDED", newVendorsVal, "Recent registrations", "fas-user-plus", "#0284C7"),
                createKpiCard("NEEDS ATTENTION", inactiveVendorsVal, "Inactive / Incomplete", "fas-exclamation-circle", "#D97706")
        );

        // Table Card
        VBox vendorCard = new VBox(12);
        vendorCard.setPadding(new Insets(18));
        vendorCard.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        HBox tableHead = new HBox(12);
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all = new Label("Verified Vendor Directory");
        all.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        all.setTextFill(Color.web(PRIMARY));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        TextField tableSearch = new TextField();
        tableSearch.setPromptText("Search by business or category...");
        tableSearch.setPrefWidth(280);
        tableSearch.setStyle("-fx-padding: 6 10; -fx-border-color:" + BORDER + "; -fx-border-radius:6;");

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> tableSearch.clear());

        tableHead.getChildren().addAll(all, space, tableSearch, clearBtn);

        createTable();

        vendorCard.getChildren().addAll(tableHead, table);
        content.getChildren().addAll(heading, stats, vendorCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadVendorsDataAsync();

        vendorScene = new Scene(root, 1550, 785);
        return vendorScene;
    }

    private void createTable() {
        table = new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(340);

        TableColumn<VendorProfile, String> nameCol = new TableColumn<>("Vendor Business");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getBusinessName() != null && !d.getValue().getBusinessName().isBlank()
                        ? d.getValue().getBusinessName()
                        : (d.getValue().getFullName() != null ? d.getValue().getFullName() : d.getValue().getName())
        ));

        TableColumn<VendorProfile, String> typeCol = new TableColumn<>("Category");
        typeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getCategory() != null ? d.getValue().getCategory() : "General"
        ));

        TableColumn<VendorProfile, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : "N/A"
        ));

        TableColumn<VendorProfile, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getPhone() != null && !d.getValue().getPhone().isBlank()
                        ? d.getValue().getPhone()
                        : (d.getValue().getMobileNo() != null ? d.getValue().getMobileNo() : "N/A")
        ));

        TableColumn<VendorProfile, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getAddress() != null ? d.getValue().getAddress() : "Pune"
        ));

        TableColumn<VendorProfile, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOnboardingStatus() != null ? d.getValue().getOnboardingStatus() : "Active"
        ));

        TableColumn<VendorProfile, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(90);
        actionCol.setCellFactory(c -> new TableCell<>() {
            final Button b = new Button("View Profile →");
            {
                b.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
                b.setOnAction(e -> {
                    VendorProfile selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedVendorId(selected.getVendorId());
                    Stage stage = (Stage) b.getScene().getWindow();
                    NavigationManager.navigate("Vendor Details", stage);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(nameCol, typeCol, emailCol, phoneCol, locationCol, statusCol, actionCol);
    }

    private void loadVendorsDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<VendorProfile> vendors = controller.getAllVendors();

                Platform.runLater(() -> {
                    data.clear();
                    int activeCount = 0;
                    int newCount = 0;
                    int inactiveCount = 0;

                    if (vendors != null) {
                        for (VendorProfile v : vendors) {
                            String st = v.getOnboardingStatus() != null ? v.getOnboardingStatus().toUpperCase() : "ACTIVE";
                            if ("ACTIVE".equals(st) || "EXISTING".equals(st) || v.isProfileCompleted()) {
                                activeCount++;
                            } else if ("NEW".equals(st)) {
                                newCount++;
                            } else {
                                inactiveCount++;
                            }
                            data.add(v);
                        }
                        totalVendorsVal.setText(String.valueOf(vendors.size()));
                    }

                    activeVendorsVal.setText(String.valueOf(activeCount));
                    newVendorsVal.setText(String.valueOf(newCount));
                    inactiveVendorsVal.setText(String.valueOf(inactiveCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
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