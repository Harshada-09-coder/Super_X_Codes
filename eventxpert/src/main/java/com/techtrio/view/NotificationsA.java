package com.techtrio.view;

import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Conflict;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorProfile;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class NotificationsA {

    private Scene notificationsScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private final AdminController adminController = new AdminController();

    private Label unreadCountLabel = new Label("0");
    private Label totalCountLabel = new Label("0");

    private VBox alertListBox = new VBox(10);
    private List<AdminAlertItem> allAlerts = new ArrayList<>();
    private String selectedFilter = "All";

    private Label focusHeadingLabel;
    private Label focusTimestampLabel;
    private Label focusEventLabel;
    private Label focusTypeLabel;
    private Label focusStatusLabel;
    private Label focusDetailsLabel;
    private Button focusActionButton;

    public Scene getnotificationsScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Dashboard Matched Sidebar
        root.setLeft(createAdminSidebar("Notifications"));

        // 2. Main Scroll Content (No TopBar)
        VBox main = new VBox(18);
        main.setPadding(new Insets(24, 32, 28, 32));
        main.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("System Notifications & Live Alerts");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Real-time feed of new event requests, customer payments, vendor verifications and system conflicts.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(2, title, subtitle);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        Button markAllRead = new Button("✓ Mark All as Read");
        markAllRead.setStyle("-fx-background-color:white; -fx-border-color:" + PRIMARY + "; -fx-border-radius:6; -fx-text-fill:" + PRIMARY + "; -fx-font-size:12px; -fx-font-weight:bold; -fx-padding:7 14; -fx-cursor:hand;");
        markAllRead.setOnAction(e -> {
            for (AdminAlertItem item : allAlerts) item.isRead = true;
            updateCounters();
            renderAlertsList();
        });

        HBox topBar = new HBox(heading, headerSpace, markAllRead);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // 2 KPI Cards
        HBox topCards = new HBox(14);
        topCards.getChildren().addAll(
                createKpiCard("UNREAD ALERTS", unreadCountLabel, "Action required", "fas-envelope", "#0284C7"),
                createKpiCard("TOTAL NOTIFICATIONS", totalCountLabel, "Logged in session", "fas-bell", PRIMARY)
        );

        // Filter Pills
        HBox filterBar = new HBox(8);
        String[] filters = {"All", "Unread", "Event Requests", "Bookings", "System"};
        for (String f : filters) {
            Button pill = new Button(f);
            pill.setStyle(filterPillStyle(f.equals("All")));
            pill.setOnAction(e -> {
                for (javafx.scene.Node n : filterBar.getChildren()) n.setStyle(filterPillStyle(false));
                pill.setStyle(filterPillStyle(true));
                selectedFilter = f;
                renderAlertsList();
            });
            filterBar.getChildren().add(pill);
        }

        // Two Column Area
        HBox contentLayout = new HBox(18);

        ScrollPane listScroll = new ScrollPane(alertListBox);
        listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
        listScroll.setPrefHeight(450);
        HBox.setHgrow(listScroll, Priority.ALWAYS);

        VBox rightFocusCard = buildFocusDetailsCard();
        rightFocusCard.setPrefWidth(380);
        rightFocusCard.setMinWidth(360);

        contentLayout.getChildren().addAll(listScroll, rightFocusCard);
        main.getChildren().addAll(topBar, topCards, filterBar, contentLayout);

        ScrollPane mainScroll = new ScrollPane(main);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background-color:" + BG + "; -fx-border-color:transparent;");
        root.setCenter(mainScroll);

        loadRealNotifications();

        notificationsScene = new Scene(root, 1550, 785);
        return notificationsScene;
    }

    private VBox buildFocusDetailsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        Label cardTitle = new Label("Alert Focus Details");
        cardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        cardTitle.setTextFill(Color.web(PRIMARY));

        focusHeadingLabel = new Label("Select a notification to view full details");
        focusHeadingLabel.setWrapText(true);
        focusHeadingLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");

        focusTimestampLabel = new Label("Timestamp: Live");
        focusTimestampLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#64748B;");

        Separator sep = new Separator();

        focusEventLabel = new Label("• Event: —");
        focusTypeLabel = new Label("• Type: —");
        focusStatusLabel = new Label("• Status: —");
        focusDetailsLabel = new Label("Detailed message will appear here.");
        focusDetailsLabel.setWrapText(true);

        for (Label l : new Label[]{focusEventLabel, focusTypeLabel, focusStatusLabel, focusDetailsLabel}) {
            l.setStyle("-fx-font-size:12px; -fx-text-fill:#334155;");
        }

        VBox innerDetails = new VBox(8, focusDetailsLabel, focusEventLabel, focusTypeLabel, focusStatusLabel);
        innerDetails.setPadding(new Insets(12));
        innerDetails.setStyle("-fx-background-color:#F8FAFC; -fx-border-color:" + BORDER + "; -fx-border-radius:6; -fx-background-radius:6;");

        focusActionButton = new Button("Open Event Workspace →");
        focusActionButton.setMaxWidth(Double.MAX_VALUE);
        focusActionButton.setStyle("-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:12px; -fx-background-radius:6; -fx-padding:8 14; -fx-cursor:hand;");
        focusActionButton.setVisible(false);

        card.getChildren().addAll(cardTitle, focusHeadingLabel, focusTimestampLabel, sep, innerDetails, focusActionButton);
        return card;
    }

    private void loadRealNotifications() {
        Thread thread = new Thread(() -> {
            try {
                List<Event> events = adminController.getAllEvents();
                List<Conflict> conflicts = adminController.getAllConflicts();
                List<AdminAlertItem> items = new ArrayList<>();

                if (events != null) {
                    for (Event ev : events) {
                        String st = ev.getStatus() != null ? ev.getStatus().trim().toUpperCase() : "PENDING";
                        String name = ev.getEventName() != null ? ev.getEventName() : "Event";
                        String date = ev.getEventDate() != null ? ev.getEventDate() : (ev.getStartDate() != null ? ev.getStartDate() : "Upcoming");

                        if ("PENDING".equals(st)) {
                            items.add(new AdminAlertItem("fas-inbox", "New Event Request: " + name, "Submitted by Customer. City: " + (ev.getCity() != null ? ev.getCity() : "Pune") + " • Date: " + date, "Recent", "Event Requests", false, name, "Customer Request", "Pending Review", "Budget: " + ev.getBudget(), "Event Requests"));
                        } else {
                            items.add(new AdminAlertItem("fas-calendar-check", "Confirmed Event: " + name, "Organizer: " + (ev.getOrganizerName() != null ? ev.getOrganizerName() : "Assigned") + " • Status: " + st, "Recent", "Bookings", true, name, "Confirmed Event", st, "Active planning underway in EventXpert.", "Events"));
                        }
                    }
                }

                if (conflicts != null) {
                    for (Conflict c : conflicts) {
                        items.add(0, new AdminAlertItem("fas-exclamation-triangle", "Conflict Alert: " + (c.getType() != null ? c.getType() : "Overlap"), (c.getDescription() != null ? c.getDescription() : "Resource conflict detected in schedule."), "Immediate", "System", false, "Conflict #" + c.getConflictId(), "Conflict Alert", c.getStatus() != null ? c.getStatus() : "Open", c.getDescription(), "Conflict Detection"));
                    }
                }

                Platform.runLater(() -> {
                    allAlerts.clear();
                    allAlerts.addAll(items);
                    updateCounters();
                    renderAlertsList();
                    if (!allAlerts.isEmpty()) selectAlertForFocus(allAlerts.get(0));
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void renderAlertsList() {
        alertListBox.getChildren().clear();
        for (AdminAlertItem item : allAlerts) {
            if (matchesFilter(item, selectedFilter)) {
                alertListBox.getChildren().add(buildAlertRow(item));
            }
        }
    }

    private boolean matchesFilter(AdminAlertItem item, String filter) {
        if ("All".equalsIgnoreCase(filter)) return true;
        if ("Unread".equalsIgnoreCase(filter)) return !item.isRead;
        if ("Event Requests".equalsIgnoreCase(filter)) return "Event Requests".equalsIgnoreCase(item.category);
        if ("Bookings".equalsIgnoreCase(filter)) return "Bookings".equalsIgnoreCase(item.category);
        if ("System".equalsIgnoreCase(filter)) return "System".equalsIgnoreCase(item.category);
        return true;
    }

    private void updateCounters() {
        int total = allAlerts.size();
        int unread = (int) allAlerts.stream().filter(a -> !a.isRead).count();
        totalCountLabel.setText(String.valueOf(total));
        unreadCountLabel.setText(String.valueOf(unread));
    }

    private HBox buildAlertRow(AdminAlertItem item) {
        FontIcon icon = new FontIcon(item.iconLit);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(PRIMARY));

        StackPane iconCircle = new StackPane(icon);
        iconCircle.setPrefSize(38, 38);
        iconCircle.setStyle("-fx-background-color:#EAF3FF; -fx-background-radius:8;");

        Label titleLabel = new Label(item.title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titleLabel.setTextFill(Color.web(PRIMARY));

        Label descLabel = new Label(item.subtitle);
        descLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#64748B;");
        descLabel.setWrapText(true);

        VBox text = new VBox(2, titleLabel, descLabel);
        HBox.setHgrow(text, Priority.ALWAYS);

        Label timeLabel = new Label(item.timestamp);
        timeLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#94A3B8;");

        HBox row = new HBox(12, iconCircle, text, timeLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color:white; -fx-border-color:" + (item.isRead ? BORDER : PRIMARY) + "; -fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;");

        row.setOnMouseClicked(e -> {
            item.isRead = true;
            row.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:8; -fx-background-radius:8;");
            updateCounters();
            selectAlertForFocus(item);
        });

        return row;
    }

    private void selectAlertForFocus(AdminAlertItem item) {
        focusHeadingLabel.setText(item.title);
        focusTimestampLabel.setText("Timestamp: " + item.timestamp);
        focusEventLabel.setText("• Context: " + item.eventName);
        focusTypeLabel.setText("• Category: " + item.eventType);
        focusStatusLabel.setText("• Status: " + item.status);
        focusDetailsLabel.setText(item.details != null ? item.details : item.subtitle);

        if (item.targetPage != null && !item.targetPage.isBlank()) {
            focusActionButton.setText("Navigate to " + item.targetPage + " →");
            focusActionButton.setVisible(true);
            focusActionButton.setOnAction(e -> {
                Stage stage = (Stage) focusActionButton.getScene().getWindow();
                NavigationManager.navigate(item.targetPage, stage);
            });
        } else {
            focusActionButton.setVisible(false);
        }
    }

    private String filterPillStyle(boolean active) {
        return active
                ? "-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:18; -fx-padding:5 14; -fx-font-size:11px; -fx-cursor:hand;"
                : "-fx-background-color:white; -fx-text-fill:#475569; -fx-border-color:" + BORDER + "; -fx-border-radius:18; -fx-background-radius:18; -fx-padding:5 14; -fx-font-size:11px; -fx-cursor:hand;";
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
                createSidebarButton("Notifications", "fas-bell", activeTab.equals("Notifications")),
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

    private static class AdminAlertItem {
        String iconLit;
        String title;
        String subtitle;
        String timestamp;
        String category;
        boolean isRead;
        String eventName;
        String eventType;
        String status;
        String details;
        String targetPage;

        AdminAlertItem(String iconLit, String title, String subtitle, String timestamp, String category, boolean isRead, String eventName, String eventType, String status, String details, String targetPage) {
            this.iconLit = iconLit;
            this.title = title;
            this.subtitle = subtitle;
            this.timestamp = timestamp;
            this.category = category;
            this.isRead = isRead;
            this.eventName = eventName;
            this.eventType = eventType;
            this.status = status;
            this.details = details;
            this.targetPage = targetPage;
        }
    }
}