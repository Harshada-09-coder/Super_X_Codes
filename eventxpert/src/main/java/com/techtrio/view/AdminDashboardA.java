package com.techtrio.view;

import java.util.List;
import java.util.Map;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorProfile;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminDashboardA {
    private Scene adminDashboardScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BACKGROUND = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private Label totalCustomersVal;
    private Label totalOrganizersVal;
    private Label totalVendorsVal;
    private Label activeEventsVal;
    private Label pendingRequestsVal;
    private Label totalRevenueVal;
    private TableView<String[]> table;

    public Scene getadminDashboardScene() {

        // SIDEBAR
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
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals("Dashboard")));
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

        // MAIN CONTENT
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BACKGROUND + ";");

        Label title = new Label("Admin Dashboard");
        title.setTextFill(Color.web(PRIMARY));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Label subtitle = new Label("Live overview of customer requests, scheduled events, and platform analytics.");
        subtitle.setTextFill(Color.web("#64748B"));
        subtitle.setFont(Font.font("Arial", 13));

        VBox titleBox = new VBox(2, title, subtitle);

        // 6 KPI CARDS
        HBox cards = new HBox(12);
        totalCustomersVal = new Label("0");
        totalOrganizersVal = new Label("0");
        totalVendorsVal = new Label("0");
        activeEventsVal = new Label("0");
        pendingRequestsVal = new Label("0");
        totalRevenueVal = new Label("₹0.00");

        VBox customerCard = createCard("Total Customers", totalCustomersVal, "fas-users", "#0284C7");
        VBox organizerCard = createCard("Total Organizers", totalOrganizersVal, "fas-user-tie", "#7C3AED");
        VBox vendorCard = createCard("Total Vendors", totalVendorsVal, "fas-truck", "#EA580C");
        VBox activeEventsCard = createCard("Active Events", activeEventsVal, "fas-calendar-check", "#16A34A");
        VBox pendingRequestsCard = createCard("Pending Requests", pendingRequestsVal, "fas-inbox", "#DC2626");
        VBox revenueCard = createCard("Platform Revenue", totalRevenueVal, "fas-wallet", "#002A60");

        cards.getChildren().addAll(customerCard, organizerCard, vendorCard, activeEventsCard, pendingRequestsCard, revenueCard);

        customerCard.setOnMouseClicked(e -> NavigationManager.navigate("Customers", (Stage) customerCard.getScene().getWindow()));
        organizerCard.setOnMouseClicked(e -> NavigationManager.navigate("Organizers", (Stage) organizerCard.getScene().getWindow()));
        vendorCard.setOnMouseClicked(e -> NavigationManager.navigate("Vendors", (Stage) vendorCard.getScene().getWindow()));
        activeEventsCard.setOnMouseClicked(e -> NavigationManager.navigate("Events", (Stage) activeEventsCard.getScene().getWindow()));
        pendingRequestsCard.setOnMouseClicked(e -> NavigationManager.navigate("Event Requests", (Stage) pendingRequestsCard.getScene().getWindow()));
        revenueCard.setOnMouseClicked(e -> NavigationManager.navigate("Reports & Analytics", (Stage) revenueCard.getScene().getWindow()));

        // MIDDLE ROW
        VBox requestBox = new VBox(10);
        requestBox.setPadding(new Insets(16));
        requestBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        HBox.setHgrow(requestBox, Priority.ALWAYS);

        Label requestTitle = new Label("Recent Customer Event Requests");
        requestTitle.setTextFill(Color.web(PRIMARY));
        requestTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        String[] columns = {"Customer", "Event Type", "Event Duration", "Time", "Guests", "Budget", "Status"};

        for (int i = 0; i < columns.length; i++) {
            final int index = i;
            TableColumn<String[], String> column = new TableColumn<>(columns[i]);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[index]));
            table.getColumns().add(column);
        }
        table.setPrefHeight(230);
        requestBox.getChildren().addAll(requestTitle, table);

        // QUICK ACTIONS
        VBox quickBox = new VBox(12);
        quickBox.setPadding(new Insets(16));
        quickBox.setPrefWidth(280);
        quickBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        Label quickTitle = new Label("Quick Actions");
        quickTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quickTitle.setTextFill(Color.web(PRIMARY));

        Button reviewBtn = actionButton("Review Requests", "fas-inbox");
        Button custBtn = actionButton("Manage Customers", "fas-users");
        Button orgBtn = actionButton("Manage Organizers", "fas-user-tie");
        Button calBtn = actionButton("View Calendar", "fas-calendar-alt");

        reviewBtn.setOnAction(e -> NavigationManager.navigate("Event Requests", (Stage) reviewBtn.getScene().getWindow()));
        custBtn.setOnAction(e -> NavigationManager.navigate("Customers", (Stage) custBtn.getScene().getWindow()));
        orgBtn.setOnAction(e -> NavigationManager.navigate("Organizers", (Stage) orgBtn.getScene().getWindow()));
        calBtn.setOnAction(e -> NavigationManager.navigate("Calendar", (Stage) calBtn.getScene().getWindow()));

        GridPane actionGrid = new GridPane();
        actionGrid.setHgap(8); actionGrid.setVgap(8);
        actionGrid.add(reviewBtn, 0, 0); actionGrid.add(custBtn, 1, 0);
        actionGrid.add(orgBtn, 0, 1); actionGrid.add(calBtn, 1, 1);

        quickBox.getChildren().addAll(quickTitle, actionGrid);

        HBox middleRow = new HBox(16, requestBox, quickBox);

        // EVENT STATUS & LIVE ACTIVITY
        VBox statusBox = new VBox(10);
        statusBox.setPadding(new Insets(16));
        statusBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        HBox.setHgrow(statusBox, Priority.ALWAYS);

        Label statusTitle = new Label("Event Status Breakdown");
        statusTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        statusTitle.setTextFill(Color.web(PRIMARY));

        statusBox.getChildren().addAll(
                statusTitle,
                createProgressBar("Planning & Review", 45, "#0284C7"),
                createProgressBar("Confirmed / Assigned", 35, "#16A34A"),
                createProgressBar("Completed", 15, "#7C3AED"),
                createProgressBar("Pending Review", 10, "#D97706")
        );

        VBox activityBox = new VBox(10);
        activityBox.setPadding(new Insets(16));
        activityBox.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        HBox.setHgrow(activityBox, Priority.ALWAYS);

        Label activityTitle = new Label("Live System Activity");
        activityTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        activityTitle.setTextFill(Color.web(PRIMARY));

        activityBox.getChildren().addAll(
                activityTitle,
                createActivityItem("● New Event Request submitted by Customer"),
                createActivityItem("● Timefold AI Plan generated for scheduled event"),
                createActivityItem("● Organizer assignment updated"),
                createActivityItem("● Platform commission reconciliation active")
        );

        HBox bottomRow = new HBox(16, statusBox, activityBox);

        content.getChildren().addAll(titleBox, cards, middleRow, bottomRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;-fx-background:" + BACKGROUND + ";-fx-border-color:transparent;");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scrollPane);

        loadDashboardDataAsync();

        adminDashboardScene = new Scene(root, 1550, 785);
        return adminDashboardScene;
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
            btn.setStyle("-fx-background-color:" + LIGHT + ";-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }

    // ⭐ अचूक Admin Revenue: (Contingency मधून उरलेले + Non-Venue Vendors चे ३% फी)
    private void loadDashboardDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();

                List<CustomerProfile> customers = controller.getAllCustomerProfiles();
                List<Event> events = controller.getAllEvents();
                List<OrganizerProfile> organizers = controller.getAllOrganizers();
                List<VendorProfile> vendors = controller.getAllVendors();

                double totalAdminRevenue = 0.0;

                if (events != null) {
                    for (Event ev : events) {
                        double paid = ev.getPaidAmount();
                        if (paid > 0) {
                            // १. Organizer चे १५%
                            double orgShare15 = paid * 0.15;

                            // २. Contingency & Other मधून मिळणारा हिस्सा
                            double contingencyPool = 0.0;
                            double vendor3PercentCut = 0.0;

                            if (ev.getBudgetRecommendation() != null) {
                                for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
                                    String key = entry.getKey().toLowerCase();
                                    try {
                                        double amt = Double.parseDouble(entry.getValue().toString());
                                        if (key.contains("contingency") || key.contains("other") || key.contains("misc")) {
                                            contingencyPool += amt;
                                        } else if (!key.contains("venue")) {
                                            // Venue सोडून इतर व्हेंडर्सकडून ३% फी
                                            vendor3PercentCut += (amt * 0.03);
                                        }
                                    } catch (Exception ignored) {}
                                }
                            }

                            // जर Contingency मधून १५% दिले, तर उरलेली रक्कम Admin कडे राहते
                            double adminContingencyShare = Math.max(0.0, contingencyPool - orgShare15);
                            totalAdminRevenue += (adminContingencyShare + vendor3PercentCut);
                        }
                    }
                }

                final double finalTotalRevenue = totalAdminRevenue;

                Platform.runLater(() -> {
                    totalCustomersVal.setText(String.valueOf(customers != null ? customers.size() : 0));
                    totalOrganizersVal.setText(String.valueOf(organizers != null ? organizers.size() : 0));
                    totalVendorsVal.setText(String.valueOf(vendors != null ? vendors.size() : 0));
                    totalRevenueVal.setText("₹" + String.format("%.2f", finalTotalRevenue));

                    int pendingCount = 0;
                    int activeCount = 0;

                    table.getItems().clear();
                    if (events != null) {
                        for (Event ev : events) {
                            if ("PENDING".equalsIgnoreCase(ev.getStatus())) {
                                pendingCount++;
                            } else {
                                activeCount++;
                            }

                            CustomerProfile cust = (customers != null) ? customers.stream()
                                    .filter(c -> c.getCustomerId() != null && c.getCustomerId().equals(ev.getCustomerId()))
                                    .findFirst().orElse(null) : null;

                            String custName = cust != null ? cust.getName() : "Customer";
                            String dateDisplay = (ev.getStartDate() != null && !ev.getStartDate().isBlank())
                                    ? ev.getStartDate() + (ev.getEndDate() != null && !ev.getEndDate().isBlank() && !ev.getStartDate().equals(ev.getEndDate()) ? " to " + ev.getEndDate() : "")
                                    : (ev.getEventDate() != null ? ev.getEventDate() : "TBD");

                            String timeDisplay = (ev.getStartTime() != null && !ev.getStartTime().isBlank())
                                    ? ev.getStartTime() + (ev.getEndTime() != null && !ev.getEndTime().isBlank() ? " - " + ev.getEndTime() : "")
                                    : (ev.getTime() != null ? ev.getTime() : "TBD");

                            table.getItems().add(new String[]{
                                    custName,
                                    ev.getEventType() != null ? ev.getEventType() : "General",
                                    dateDisplay,
                                    timeDisplay,
                                    String.valueOf(ev.getGuests()),
                                    ev.getBudget() != null ? ev.getBudget() : "N/A",
                                    ev.getStatus() != null ? ev.getStatus() : "PENDING"
                            });
                        }
                    }

                    activeEventsVal.setText(String.valueOf(activeCount));
                    pendingRequestsVal.setText(String.valueOf(pendingCount));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createCard(String title, Label valueLabel, String iconLit, String iconColorHex) {
        VBox card = new VBox(6);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setPrefWidth(200);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = new StackPane();
        Circle c = new Circle(14, Color.web(iconColorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(13);
        fontIcon.setIconColor(Color.web(iconColorHex));
        iconCircle.getChildren().addAll(c, fontIcon);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        titleLabel.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(iconCircle, titleLabel);

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        valueLabel.setTextFill(Color.web(PRIMARY));

        card.getChildren().addAll(top, valueLabel);
        return card;
    }

    private Button actionButton(String text, String iconLit) {
        Button button = new Button(" " + text);
        button.setPrefWidth(125); button.setPrefHeight(50); button.setWrapText(true);
        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(12);
        icon.setIconColor(Color.web(PRIMARY));
        button.setGraphic(icon);
        button.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;-fx-font-weight:bold;-fx-font-size:11px;-fx-text-fill:" + PRIMARY + ";-fx-cursor:hand;");
        return button;
    }

    private VBox createProgressBar(String name, double value, String colorHex) {
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        nameLabel.setTextFill(Color.web("#334155"));

        Label percentageLabel = new Label((int) value + "%");
        percentageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        percentageLabel.setTextFill(Color.web(PRIMARY));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox top = new HBox(nameLabel, space, percentageLabel);

        ProgressBar pb = new ProgressBar(value / 100.0);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(7);
        pb.setStyle("-fx-accent: " + colorHex + ";");

        return new VBox(4, top, pb);
    }

    private Label createActivityItem(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 12));
        l.setTextFill(Color.web("#475569"));
        return l;
    }
}