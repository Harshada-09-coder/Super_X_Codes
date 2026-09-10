package com.techtrio.view;

import java.util.List;
import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CustomerA {
    private Scene customerScene;

    private final String PRIMARY = "#002A60", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";

    private TableView<CustomerData> table = new TableView<>();
    private ObservableList<CustomerData> data = FXCollections.observableArrayList();

    private TextField search = new TextField();
    private Label totalCustomersCardVal;
    private Label activeCustomersCardVal;

    public Scene getcustomerScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Dashboard Sidebar
        root.setLeft(createAdminSidebar("Customers"));

        // 2. Main Scroll Content (No TopBar)
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Customer Accounts");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Live monitor of registered event planners, booking counts, and contact records.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, sub);

        totalCustomersCardVal = new Label("0");
        activeCustomersCardVal = new Label("0");

        HBox cards = new HBox(14,
                createKpiCard("TOTAL REGISTERED CUSTOMERS", totalCustomersCardVal, "fas-users", PRIMARY),
                createKpiCard("ACTIVE EVENT PLANNERS", activeCustomersCardVal, "fas-user-check", "#16A34A")
        );

        // Table Card
        VBox tableBox = new VBox(12);
        tableBox.setPadding(new Insets(18));
        tableBox.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        HBox tableHead = new HBox(12);
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all = new Label("All Customers");
        all.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        all.setTextFill(Color.web(PRIMARY));

        Region ts = new Region();
        HBox.setHgrow(ts, Priority.ALWAYS);

        search.setPromptText("Search customer by name or email...");
        search.setPrefWidth(300);
        search.setStyle("-fx-padding: 6 10; -fx-border-color:" + BORDER + "; -fx-border-radius:6;");

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> search.clear());

        tableHead.getChildren().addAll(all, ts, search, clearBtn);

        createTable();
        table.setItems(data);
        table.setPrefHeight(360);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableBox.getChildren().addAll(tableHead, table);
        content.getChildren().addAll(heading, cards, tableBox);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadCustomersDataAsync();

        customerScene = new Scene(root, 1550, 785);
        return customerScene;
    }

    private void createTable() {
        String[] names = {"Customer", "Email", "Phone", "Events", "Last Event", "Registered", "Status"};
        String[] props = {"name", "email", "phone", "events", "lastEvent", "registered", "status"};

        for (int i = 0; i < props.length; i++) {
            TableColumn<CustomerData, String> c = new TableColumn<>(names[i]);
            c.setCellValueFactory(new PropertyValueFactory<>(props[i]));
            table.getColumns().add(c);
        }

        TableColumn<CustomerData, Void> action = new TableColumn<>("Action");
        action.setPrefWidth(90);
        action.setCellFactory(col -> new TableCell<>() {
            final Button view = new Button("View →");
            {
                view.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;");
                view.setOnAction(e -> {
                    CustomerData selected = getTableView().getItems().get(getIndex());
                    AdminContext.setSelectedCustomerId(selected.getCustomerId());
                    Stage stage = (Stage) view.getScene().getWindow();
                    NavigationManager.navigate("Customer Details", stage);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : view);
            }
        });

        table.getColumns().add(action);
    }

    private void loadCustomersDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                List<CustomerProfile> profiles = controller.getAllCustomerProfiles();
                List<Event> allEvents = controller.getAllEvents();

                Platform.runLater(() -> {
                    data.clear();
                    int activeCount = 0;

                    if (profiles != null) {
                        totalCustomersCardVal.setText(String.valueOf(profiles.size()));

                        for (CustomerProfile cp : profiles) {
                            List<Event> custEvents = allEvents.stream()
                                    .filter(ev -> ev.getCustomerId() != null && ev.getCustomerId().equals(cp.getCustomerId()))
                                    .toList();

                            if (!custEvents.isEmpty()) activeCount++;

                            String eventCountText = custEvents.size() + " Event" + (custEvents.size() == 1 ? "" : "s");
                            String lastEventText = custEvents.isEmpty() ? "—" : custEvents.get(custEvents.size() - 1).getEventName();
                            String registeredText = cp.getCreatedAt() != null ? cp.getCreatedAt().substring(0, Math.min(10, cp.getCreatedAt().length())) : "Registered";
                            String statusText = cp.getOnboardingStatus() != null ? cp.getOnboardingStatus() : "Active";

                            data.add(new CustomerData(cp.getCustomerId(),
                                    cp.getName() != null && !cp.getName().isBlank() ? cp.getName() : "Customer",
                                    cp.getEmail() != null ? cp.getEmail() : "N/A",
                                    cp.getMobileNo() != null ? cp.getMobileNo() : "N/A",
                                    eventCountText,
                                    lastEventText,
                                    registeredText,
                                    statusText
                            ));
                        }
                    }
                    activeCustomersCardVal.setText(String.valueOf(activeCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createKpiCard(String title, Label valLbl, String iconLit, String colorHex) {
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

        card.getChildren().addAll(top, valLbl);
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

    public static class CustomerData {
        private final String customerId, name, email, phone, events, lastEvent, registered, status;

        public CustomerData(String id, String n, String e, String p, String ev, String le, String r, String s) {
            customerId = id; name = n; email = e; phone = p; events = ev; lastEvent = le; registered = r; status = s;
        }

        public String getCustomerId() { return customerId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getEvents() { return events; }
        public String getLastEvent() { return lastEvent; }
        public String getRegistered() { return registered; }
        public String getStatus() { return status; }
    }
}