package com.techtrio.view;

import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Conflict;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorProfile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class NotificationsA {

    private Scene notificationsScene;

    final String NAVY = "#062957";
    final String BLUE = "#0D3979";
    final String BG = "#F6F8FB";
    final String BORDER = "#DDE3EA";

    private final AdminController adminController = new AdminController();

    // KPI Counters
    private Label unreadCountLabel = new Label("0");
    private Label totalCountLabel = new Label("0");

    // Containers
    private VBox alertListBox = new VBox(10);
    private List<AdminAlertItem> allAlerts = new ArrayList<>();
    private String selectedFilter = "All";
    private HBox filterBar;

    // Right-hand "Alert Focus Details" panel controls
    private Label focusHeadingLabel;
    private Label focusTimestampLabel;
    private Label focusEventLabel;
    private Label focusTypeLabel;
    private Label focusStatusLabel;
    private Label focusDetailsLabel;
    private Button focusActionButton;

    public Scene getnotificationsScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        /* ================= SIDEBAR ================= */

        VBox side = new VBox(10);
        side.setPrefWidth(258);
        side.setPadding(new Insets(25, 14, 15, 14));
        side.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill:white;-fx-font-size:26px;-fx-font-weight:bold;");

        Label sub = new Label("ADMIN DASHBOARD");
        sub.setStyle("-fx-text-fill:#AAB9CC;-fx-font-size:11px;-fx-font-weight:bold;");

        VBox brandText = new VBox(2, logo, sub);
        HBox brand = new HBox(10, brandText);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 5, 20, 5));

        VBox menu = new VBox(4);

        String[] menus = {
                "▦   Dashboard",
                "◫   Event Requests",
                "▣   Events",
                "▤   Calendar",
                "♧   Customers",
                "♟   Organizers",
                "▤   Vendors",
                "⚠   Conflict Detection",
                "▣   Reports & Analytics"
        };

        for (String text : menus) {
            Label item = new Label(text);
            item.setMaxWidth(Double.MAX_VALUE);
            item.setPrefHeight(38);
            item.setPadding(new Insets(8, 10, 8, 10));
            item.setStyle("-fx-text-fill:#AAB9CC;-fx-font-size:14px;");

            item.setOnMouseClicked(e -> {
                Stage stage = (Stage) item.getScene().getWindow();
                String page = text.replace("▦", "").replace("◫", "").replace("▣", "")
                        .replace("▤", "").replace("♧", "").replace("♟", "").replace("⚠", "").trim();
                NavigationManager.navigate(page, stage);
            });

            menu.getChildren().add(item);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label notificationTab = new Label("🔔   Notifications");
        notificationTab.setMaxWidth(Double.MAX_VALUE);
        notificationTab.setStyle(
                "-fx-background-color:#BFE1FA;" +
                "-fx-text-fill:#062957;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7;" +
                "-fx-padding:10px 10px;"
        );

        Label settings = sideItem("⚙   Settings");
        Label profile = sideItem("👤   Profile");
        Label support = sideItem("?   Support");

        settings.setOnMouseClicked(e -> NavigationManager.navigate("Settings", (Stage) settings.getScene().getWindow()));
        profile.setOnMouseClicked(e -> NavigationManager.navigate("Profile", (Stage) profile.getScene().getWindow()));
        support.setOnMouseClicked(e -> NavigationManager.navigate("Support", (Stage) support.getScene().getWindow()));

        VBox bottom = new VBox(5, notificationTab, profile, settings, support);
        side.getChildren().addAll(brand, menu, spacer, bottom);
        root.setLeft(side);

        /* ================= MAIN CONTENT ================= */

        VBox main = new VBox(16);
        main.setPadding(new Insets(24, 28, 28, 28));

        // Top Header
        Label title = new Label("Notifications & Alerts");
        title.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:#101828;");

        Label subtitle = new Label("Stay updated with your real-time service requests, confirmed bookings and system alerts.");
        subtitle.setStyle("-fx-font-size:14px;-fx-text-fill:#667085;");

        VBox heading = new VBox(3, title, subtitle);

        Button markAllRead = new Button("✓  Mark All as Read");
        markAllRead.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BLUE + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:7 16;" +
                "-fx-cursor:hand;"
        );

        markAllRead.setOnAction(e -> {
            for (AdminAlertItem item : allAlerts) {
                item.isRead = true;
            }
            updateCounters();
            renderAlertsList();
            if (!allAlerts.isEmpty()) {
                selectAlertForFocus(allAlerts.get(0));
            }
        });

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        HBox topBar = new HBox(heading, headerSpace, markAllRead);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // --- 2 Big Top Cards (Exact Vendor UI Style) ---
        HBox topCards = new HBox(16);
        topCards.getChildren().addAll(
                buildTopCard("✉", "Unread Alerts", unreadCountLabel),
                buildTopCard("🔔", "Total Alerts", totalCountLabel)
        );

        // --- Filter Pills ---
        filterBar = new HBox(8);
        String[] filters = {"All", "Unread", "Event Requests", "Bookings", "System"};
        for (String f : filters) {
            Button pill = new Button(f);
            pill.setStyle(filterPillStyle(f.equals("All")));

            pill.setOnAction(e -> {
                for (javafx.scene.Node n : filterBar.getChildren()) {
                    n.setStyle(filterPillStyle(false));
                }
                pill.setStyle(filterPillStyle(true));
                selectedFilter = f;
                renderAlertsList();
            });

            filterBar.getChildren().add(pill);
        }

        // --- Two Column Area: Left List + Right Detail Panel ---
        HBox contentLayout = new HBox(18);

        // Left List (ScrollPane)
        ScrollPane listScroll = new ScrollPane(alertListBox);
        listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;");
        listScroll.setPrefHeight(450);
        HBox.setHgrow(listScroll, Priority.ALWAYS);

        // Right Detail Card ("Alert Focus Details")
        VBox rightFocusCard = buildFocusDetailsCard();
        rightFocusCard.setPrefWidth(380);
        rightFocusCard.setMinWidth(360);
        rightFocusCard.setMaxWidth(400);

        contentLayout.getChildren().addAll(listScroll, rightFocusCard);
        VBox.setVgrow(contentLayout, Priority.ALWAYS);

        main.getChildren().addAll(topBar, topCards, filterBar, contentLayout);

        ScrollPane mainScroll = new ScrollPane(main);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background-color:" + BG + ";");
        root.setCenter(mainScroll);

        // Load Live Data
        loadRealNotifications();

        Scene scene = new Scene(root, 1550, 750);
        notificationsScene = scene;
        return notificationsScene;
    }

    /* ================= 2 TOP KPI CARDS ================= */

    private VBox buildTopCard(String icon, String title, Label countLabel) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:15px;-fx-text-fill:" + BLUE + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#344054;");

        HBox top = new HBox(8, iconLabel, titleLabel);
        top.setAlignment(Pos.CENTER_LEFT);

        countLabel.setStyle("-fx-font-size:32px;-fx-font-weight:bold;-fx-text-fill:#101828;");

        VBox box = new VBox(10, top, countLabel);
        box.setPadding(new Insets(18, 22, 18, 22));
        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;"
        );
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    /* ================= RIGHT ALERT FOCUS DETAILS CARD ================= */

    private VBox buildFocusDetailsCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;"
        );

        Label cardTitle = new Label("Alert Focus Details");
        cardTitle.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#101828;");

        focusHeadingLabel = new Label("Select a notification to view full details");
        focusHeadingLabel.setWrapText(true);
        focusHeadingLabel.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + BLUE + ";");

        focusTimestampLabel = new Label("Timestamp: -");
        focusTimestampLabel.setStyle("-fx-font-size:12px;-fx-text-fill:#667085;");

        Separator sep = new Separator();

        focusEventLabel = new Label("• Event: -");
        focusEventLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#344054;");
        focusEventLabel.setWrapText(true);

        focusTypeLabel = new Label("• Type: -");
        focusTypeLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#344054;");

        focusStatusLabel = new Label("• Status: -");
        focusStatusLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#344054;");

        focusDetailsLabel = new Label("Detailed message will appear here.");
        focusDetailsLabel.setWrapText(true);
        focusDetailsLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#475467;");

        VBox innerDetails = new VBox(8, focusDetailsLabel, focusEventLabel, focusTypeLabel, focusStatusLabel);
        innerDetails.setPadding(new Insets(14));
        innerDetails.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:#EAECF0;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        focusActionButton = new Button("View in Events ↗");
        focusActionButton.setMaxWidth(Double.MAX_VALUE);
        focusActionButton.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-font-size:13px;" +
                "-fx-background-radius:6;" +
                "-fx-padding:9 14;" +
                "-fx-cursor:hand;"
        );
        focusActionButton.setVisible(false);

        card.getChildren().addAll(cardTitle, focusHeadingLabel, focusTimestampLabel, sep, innerDetails, focusActionButton);
        return card;
    }

    /* ================= DYNAMIC DATA FETCHING ================= */

    private void loadRealNotifications() {
        Thread thread = new Thread(() -> {
            try {
                List<Event> events = adminController.getAllEvents();
                List<OrganizerProfile> organizers = adminController.getAllOrganizers();
                List<VendorProfile> vendors = adminController.getAllVendors();
                List<Conflict> conflicts = adminController.getAllConflicts();

                List<AdminAlertItem> items = new ArrayList<>();

                // 1. Events -> Requests & Bookings
                if (events != null) {
                    for (Event ev : events) {
                        String st = (ev.getStatus() != null) ? ev.getStatus().trim().toUpperCase() : "PENDING";
                        String name = (ev.getEventName() != null && !ev.getEventName().isBlank()) ? ev.getEventName() : "Unnamed Event";
                        String date = (ev.getEventDate() != null) ? ev.getEventDate() : "TBD";
                        double budget = extractBudget(ev);

                        if ("PENDING".equals(st) || "NOT_ASSIGNED".equals(ev.getOrganizerStatus())) {
                            items.add(new AdminAlertItem(
                                    "📅",
                                    "New Event Request: " + name,
                                    "Customer submitted new request. Location: " + safe(ev.getCity()) + ". Date: " + date,
                                    "Recent",
                                    "Event Requests",
                                    false,
                                    name,
                                    "Event Request",
                                    "Pending Review",
                                    "Budget: ₹" + Math.round(budget) + " | Venue: " + safe(ev.getVenueType()),
                                    "Event Requests"
                            ));
                        } else {
                            items.add(new AdminAlertItem(
                                    "📅",
                                    "Booking Confirmed: " + name,
                                    "Service assignment for " + safe(ev.getOrganizerName()) + " is active. Date: " + date,
                                    "Recent",
                                    "Bookings",
                                    true,
                                    name,
                                    "Confirmed Booking",
                                    st,
                                    "Event confirmed with Organizer: " + safe(ev.getOrganizerName()) + " at " + safe(ev.getCity()),
                                    "Events"
                            ));
                        }
                    }
                }

                // 2. Conflicts
                if (conflicts != null) {
                    for (Conflict c : conflicts) {
                        items.add(0, new AdminAlertItem(
                                "⚠",
                                "Conflict Alert: " + safe(c.getType()),
                                safe(c.getDescription()) + " Resource: " + safe(c.getResource()),
                                "Immediate",
                                "System",
                                false,
                                "Conflict #" + safe(c.getConflictId()),
                                "Conflict",
                                safe(c.getStatus()),
                                safe(c.getDescription()),
                                "Conflict Detection"
                        ));
                    }
                }

                // 3. Organizers
                if (organizers != null) {
                    for (OrganizerProfile op : organizers) {
                        String orgName = extractString(op, "getName", "getOrganizerName");
                        if (orgName == null) orgName = "Organizer";
                        items.add(new AdminAlertItem(
                                "♟",
                                "Organizer Profile: " + orgName,
                                orgName + " is actively onboarded and verified in the platform.",
                                "Recent",
                                "System",
                                true,
                                "-",
                                "Organizer",
                                "Active",
                                "Registered organizer profile in EventXpert.",
                                "Organizers"
                        ));
                    }
                }

                // 4. Vendors
                if (vendors != null) {
                    for (VendorProfile vp : vendors) {
                        String vName = extractString(vp, "getBusinessName", "getName", "getVendorName");
                        if (vName == null) vName = "Vendor";
                        items.add(new AdminAlertItem(
                                "▤",
                                "Vendor Registration: " + vName,
                                vName + " has registered their service portfolio.",
                                "Recent",
                                "System",
                                true,
                                "-",
                                "Vendor",
                                "Verified",
                                "Active vendor available for event services.",
                                "Vendors"
                        ));
                    }
                }

                Platform.runLater(() -> {
                    allAlerts.clear();
                    allAlerts.addAll(items);
                    updateCounters();
                    renderAlertsList();

                    if (!allAlerts.isEmpty()) {
                        selectAlertForFocus(allAlerts.get(0));
                    }
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
        int unread = 0;
        for (AdminAlertItem item : allAlerts) {
            if (!item.isRead) unread++;
        }
        totalCountLabel.setText(String.valueOf(total));
        unreadCountLabel.setText(String.valueOf(unread));
    }

    /* ================= NOTIFICATION ROW (VENDOR STYLE) ================= */

    private HBox buildAlertRow(AdminAlertItem item) {
        // Left Blue Square Icon Box
        Label icon = new Label(item.icon);
        icon.setMinSize(40, 40);
        icon.setMaxSize(40, 40);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle(
                "-fx-background-color:#EBF5FF;" +
                "-fx-text-fill:" + BLUE + ";" +
                "-fx-font-size:18px;" +
                "-fx-background-radius:8;"
        );

        // Center Content
        Label titleLabel = new Label(item.title);
        titleLabel.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#101828;");

        Label descLabel = new Label(item.subtitle);
        descLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#667085;");
        descLabel.setWrapText(true);

        VBox text = new VBox(3, titleLabel, descLabel);
        HBox.setHgrow(text, Priority.ALWAYS);

        // Right Timestamp
        Label timeLabel = new Label(item.timestamp);
        timeLabel.setStyle("-fx-font-size:12px;-fx-text-fill:#98A2B3;");

        HBox row = new HBox(14, icon, text, timeLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + (item.isRead ? BORDER : "#B2DDFF") + ";" +
                "-fx-border-width:" + (item.isRead ? "1" : "1.5") + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-cursor:hand;"
        );

        row.setOnMouseClicked(e -> {
            item.isRead = true;
            row.setStyle(
                    "-fx-background-color:white;" +
                    "-fx-border-color:" + BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-border-radius:8;" +
                    "-fx-background-radius:8;" +
                    "-fx-cursor:hand;"
            );
            updateCounters();
            selectAlertForFocus(item);
        });

        return row;
    }

    private void selectAlertForFocus(AdminAlertItem item) {
        focusHeadingLabel.setText(item.title);
        focusTimestampLabel.setText("Timestamp: " + item.timestamp);
        focusEventLabel.setText("• Event: " + item.eventName);
        focusTypeLabel.setText("• Type: " + item.eventType);
        focusStatusLabel.setText("• Status: " + item.status);
        focusDetailsLabel.setText(item.details);

        if (item.targetPage != null && !item.targetPage.isBlank()) {
            focusActionButton.setText("Navigate to " + item.targetPage + " ↗");
            focusActionButton.setVisible(true);
            focusActionButton.setOnAction(e -> {
                Stage stage = (Stage) focusActionButton.getScene().getWindow();
                NavigationManager.navigate(item.targetPage, stage);
            });
        } else {
            focusActionButton.setVisible(false);
        }
    }

    /* ================= UTILITY & STYLES ================= */

    private String filterPillStyle(boolean active) {
        if (active) {
            return "-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:20;-fx-padding:6 18;-fx-font-size:13px;-fx-cursor:hand;";
        } else {
            return "-fx-background-color:white;-fx-text-fill:#344054;-fx-border-color:" + BORDER + ";-fx-border-radius:20;-fx-background-radius:20;-fx-padding:6 18;-fx-font-size:13px;-fx-cursor:hand;";
        }
    }

    private Label sideItem(String text) {
        Label item = new Label(text);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setPrefHeight(36);
        item.setPadding(new Insets(8, 10, 8, 10));
        item.setStyle("-fx-text-fill:#AAB9CC;-fx-font-size:14px;");
        return item;
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s.trim();
    }

    private double extractBudget(Event ev) {
        if (ev == null) return 0.0;
        try {
            Method m = ev.getClass().getMethod("getAllocatedBudget");
            Object val = m.invoke(ev);
            if (val instanceof Number) return ((Number) val).doubleValue();
        } catch (Exception ignored) {}
        try {
            Method m = ev.getClass().getMethod("getBudget");
            Object val = m.invoke(ev);
            if (val instanceof Number) return ((Number) val).doubleValue();
            if (val instanceof String) return Double.parseDouble(((String) val).replaceAll("[^0-9.]", "").trim());
        } catch (Exception ignored) {}
        return 0.0;
    }

    private String extractString(Object obj, String... methods) {
        if (obj == null) return null;
        for (String mName : methods) {
            try {
                Method m = obj.getClass().getMethod(mName);
                Object res = m.invoke(obj);
                if (res != null) return res.toString();
            } catch (Exception ignored) {}
        }
        return null;
    }

    /* ================= MODEL ================= */

    private static class AdminAlertItem {
        String icon;
        String title;
        String subtitle;
        String timestamp;
        String category; // Event Requests, Bookings, System
        boolean isRead;

        String eventName;
        String eventType;
        String status;
        String details;
        String targetPage;

        AdminAlertItem(String icon, String title, String subtitle, String timestamp,
                       String category, boolean isRead, String eventName,
                       String eventType, String status, String details, String targetPage) {
            this.icon = icon;
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