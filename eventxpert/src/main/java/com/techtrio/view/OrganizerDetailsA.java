package com.techtrio.view;

import java.util.List;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.services.EventService;
import com.techtrio.services.OrganizerProfileService;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class OrganizerDetailsA {

    private Scene organizerDetailsScene;
    public static OrganizerProfile selectedOrganizer;

    private final EventService eventService = new EventService();
    private final OrganizerProfileService organizerService = new OrganizerProfileService();

    private final String PRIMARY = "#002A60", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";

    public Scene getorganizerDetailsScene() {
        OrganizerProfile org = selectedOrganizer;

        if (org == null) {
            List<OrganizerProfile> all = organizerService.getAllOrganizers();
            if (all != null && !all.isEmpty()) {
                org = all.get(0);
                selectedOrganizer = org;
            }
        }

        List<Event> orgEvents = null;
        if (org != null && org.getOrganizerId() != null) {
            orgEvents = eventService.getEventsByOrganizerId(org.getOrganizerId());
        }

        int totalCount = (orgEvents != null) ? orgEvents.size() : 0;
        int completedCount = 0;
        int upcomingCount = 0;

        // ⭐ अचूक मोजणी: COMPLETED स्टेटसवर स्वतंत्र लक्ष
        if (orgEvents != null && !orgEvents.isEmpty()) {
            for (Event ev : orgEvents) {
                String s = ev.getStatus() != null ? ev.getStatus().trim().toUpperCase() : "";
                if ("COMPLETED".equals(s)) {
                    completedCount++;
                } else if ("CONFIRMED".equals(s) || "ACCEPTED".equals(s) || "PAID".equals(s)) {
                    upcomingCount++;
                } else {
                    upcomingCount++;
                }
            }
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Dashboard Sidebar
        root.setLeft(createAdminSidebar("Organizers"));

        // 2. Main Content (No TopBar)
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Button back = new Button("← Back to Organizers");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0 0 4 0;");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Organizers", stage);
        });

        Label title = new Label("Organizer Profile & Record");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("View verified organizer capacity, ongoing event assignments and ratings.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(4, back, title, sub);

        VBox left = new VBox(16, organizerCard(org), contactCard(org));
        left.setPrefWidth(320);

        HBox stats = new HBox(12,
                createKpiCard("ASSIGNED EVENTS", String.valueOf(totalCount), "fas-calendar-alt", PRIMARY),
                createKpiCard("COMPLETED", String.valueOf(completedCount), "fas-check-circle", "#16A34A"),
                createKpiCard("ACTIVE WORKLOAD", String.valueOf(upcomingCount), "fas-clock", "#0284C7")
        );

        VBox right = new VBox(16, stats, eventHistory(orgEvents));
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox main = new HBox(20, left, right);

        content.getChildren().addAll(heading, main);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        organizerDetailsScene = new Scene(root, 1550, 785);
        return organizerDetailsScene;
    }

    private VBox organizerCard(OrganizerProfile org) {
        VBox v = createCard();
        v.setAlignment(Pos.CENTER);

        String orgName = (org != null && org.getName() != null) ? org.getName() : "Organizer";
        String initials = orgName.length() >= 2 ? orgName.substring(0, 2).toUpperCase() : "OR";

        Label avatar = new Label(initials);
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        avatar.setTextFill(Color.web(PRIMARY));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(90, 90);
        avatar.setStyle("-fx-background-color: " + LIGHT + "; -fx-background-radius: 50;");

        Label name = new Label(orgName);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        name.setTextFill(Color.web(PRIMARY));

        Label status = new Label("● Verified Organizer");
        status.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        v.getChildren().addAll(avatar, name, status);
        return v;
    }

    private VBox contactCard(OrganizerProfile org) {
        VBox v = createCard();
        Label title = new Label("Contact Details");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        Label email = new Label("✉ Email: " + (org != null && org.getEmail() != null ? org.getEmail() : "organizer@eventxpert.com"));
        Label mobile = new Label("☎ Phone: " + (org != null && org.getMobileNo() != null ? org.getMobileNo() : "N/A"));
        Label address = new Label("📍 Location: " + (org != null && org.getAddress() != null ? org.getAddress() : "Pune, Maharashtra"));

        for (Label l : new Label[]{email, mobile, address}) {
            l.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        }

        v.getChildren().addAll(title, new Separator(), email, mobile, address);
        return v;
    }

    private VBox eventHistory(List<Event> orgEvents) {
        VBox v = createCard();
        Label title = new Label("Assigned Events History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        GridPane g = new GridPane();
        g.setHgap(20);
        g.setVgap(12);

        String[] h = {"EVENT NAME", "TYPE", "DATE", "LOCATION", "STATUS"};
        for (int i = 0; i < h.length; i++) {
            Label l = new Label(h[i]);
            l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            l.setTextFill(Color.web("#64748B"));
            g.add(l, i, 0);
        }

        if (orgEvents != null && !orgEvents.isEmpty()) {
            int row = 1;
            for (Event ev : orgEvents) {
                String st = ev.getStatus() != null ? ev.getStatus().trim().toUpperCase() : "CONFIRMED";

                g.add(new Label(ev.getEventName() != null ? ev.getEventName() : "Event"), 0, row);
                g.add(new Label(ev.getEventType() != null ? ev.getEventType() : "General"), 1, row);
                g.add(new Label(ev.getEventDate() != null ? ev.getEventDate() : "TBD"), 2, row);
                g.add(new Label(ev.getCity() != null ? ev.getCity() : "Pune"), 3, row);

                Label stBadge = new Label(st);
                if ("COMPLETED".equals(st)) {
                    stBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
                } else {
                    stBadge.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
                }
                g.add(stBadge, 4, row);
                row++;
            }
        }

        v.getChildren().addAll(title, new Separator(), g);
        return v;
    }

    private VBox createCard() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        return v;
    }

    private VBox createKpiCard(String title, String val, String iconLit, String colorHex) {
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

        Label valLbl = new Label(val);
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