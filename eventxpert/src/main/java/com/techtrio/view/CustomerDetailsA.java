package com.techtrio.view;

import java.util.List;
import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
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

public class CustomerDetailsA {

    private Scene customerDetailsScene;
    private final String PRIMARY = "#002A60", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";

    private Label nameLabel;
    private Label emailLabel;
    private Label mobileLabel;
    private Label totalEventsVal;
    private Label upcomingEventsVal;
    private Label completedEventsVal;
    private GridPane historyGrid;

    public Scene getcustomerDetailsScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Dashboard Sidebar
        root.setLeft(createAdminSidebar("Customers"));

        // 2. Main Scroll Content (No TopBar)
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Button back = new Button("← Back to Customers");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0 0 4 0;");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Customers", stage);
        });

        Label title = new Label("Customer Profile & History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Detailed customer information, event booking history and requirements.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(4, back, title, sub);

        VBox left = new VBox(16, profileCard(), contactCard());
        left.setPrefWidth(320);

        totalEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        completedEventsVal = new Label("0");

        HBox stats = new HBox(12,
                createKpiCard("TOTAL EVENTS", totalEventsVal, "fas-calendar-alt", PRIMARY),
                createKpiCard("UPCOMING", upcomingEventsVal, "fas-clock", "#0284C7"),
                createKpiCard("COMPLETED", completedEventsVal, "fas-check-circle", "#16A34A")
        );

        VBox right = new VBox(16, stats, eventHistoryCard());
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox main = new HBox(20, left, right);

        content.getChildren().addAll(heading, main);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadCustomerDetailsAsync();

        customerDetailsScene = new Scene(root, 1550, 785);
        return customerDetailsScene;
    }

    private VBox profileCard() {
        VBox v = createCard();
        v.setAlignment(Pos.CENTER);

        Label avatar = new Label("C");
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        avatar.setTextFill(Color.web(PRIMARY));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(90, 90);
        avatar.setStyle("-fx-background-color: " + LIGHT + "; -fx-background-radius: 50;");

        nameLabel = new Label("Loading...");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web(PRIMARY));

        Label status = new Label("● Active Customer");
        status.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        v.getChildren().addAll(avatar, nameLabel, status);
        return v;
    }

    private VBox contactCard() {
        VBox v = createCard();
        Label title = new Label("Contact Details");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        emailLabel = new Label("✉ Email: Loading...");
        mobileLabel = new Label("☎ Phone: Loading...");

        for (Label l : new Label[]{emailLabel, mobileLabel}) {
            l.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        }

        v.getChildren().addAll(title, new Separator(), emailLabel, mobileLabel);
        return v;
    }

    private VBox eventHistoryCard() {
        VBox v = createCard();
        Label title = new Label("Event Booking Records");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        historyGrid = new GridPane();
        historyGrid.setHgap(20);
        historyGrid.setVgap(12);

        String[] h = {"EVENT NAME", "TYPE", "DATE", "STATUS"};
        for (int i = 0; i < h.length; i++) {
            Label l = new Label(h[i]);
            l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            l.setTextFill(Color.web("#64748B"));
            historyGrid.add(l, i, 0);
        }

        v.getChildren().addAll(title, new Separator(), historyGrid);
        return v;
    }

    // ⭐ ACCURATE REAL-TIME STATUS SYNC FOR COMPLETED EVENTS
    private void loadCustomerDetailsAsync() {
        String selectedCustId = AdminContext.getSelectedCustomerId();
        if (selectedCustId == null || selectedCustId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                CustomerProfile profile = controller.getCustomerProfile(selectedCustId);
                List<Event> events = controller.getEventsByCustomerId(selectedCustId);

                Platform.runLater(() -> {
                    if (profile != null) {
                        String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Customer";
                        nameLabel.setText(name);
                        emailLabel.setText("✉ Email: " + (profile.getEmail() != null ? profile.getEmail() : "N/A"));
                        mobileLabel.setText("☎ Phone: " + (profile.getMobileNo() != null ? profile.getMobileNo() : "N/A"));
                    }

                    int upcoming = 0;
                    int completed = 0;

                    if (events != null) {
                        totalEventsVal.setText(String.valueOf(events.size()));
                        int rowIdx = 1;
                        for (Event ev : events) {
                            String st = ev.getStatus() != null ? ev.getStatus().trim().toUpperCase() : "PENDING";

                            if ("COMPLETED".equals(st)) {
                                completed++;
                            } else {
                                upcoming++;
                            }

                            historyGrid.add(new Label(ev.getEventName() != null ? ev.getEventName() : "Event"), 0, rowIdx);
                            historyGrid.add(new Label(ev.getEventType() != null ? ev.getEventType() : "General"), 1, rowIdx);
                            historyGrid.add(new Label(ev.getEventDate() != null ? ev.getEventDate() : "TBD"), 2, rowIdx);

                            Label stBadge = new Label(st);
                            if ("COMPLETED".equals(st)) {
                                stBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
                            } else {
                                stBadge.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
                            }
                            historyGrid.add(stBadge, 3, rowIdx);
                            rowIdx++;
                        }
                    }

                    upcomingEventsVal.setText(String.valueOf(upcoming));
                    completedEventsVal.setText(String.valueOf(completed));
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createCard() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        return v;
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
}