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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class OrganizerAssignA {

    private Scene organizerAssignScene;
    public static Event currentSelectedEvent;

    private final EventService eventService = new EventService();
    private final OrganizerProfileService organizerService = new OrganizerProfileService();
    private OrganizerProfile selectedOrganizerObj = null;

    private final String PRIMARY = "#002A60", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";
    private StackPane rootLayer;

    public Scene getorganizerAssignScene() {
        Event ev = currentSelectedEvent;

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. Unified Dashboard Sidebar
        root.setLeft(createAdminSidebar("Event Requests"));

        // 2. Main Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Button back = new Button("← Back to Review");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0 0 4 0;");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Event Request Review", stage);
        });

        Label title = new Label("Organizer Assignment");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        String currentStatus = (ev != null && ev.getStatus() != null) ? ev.getStatus() : "Pending Assignment";
        Label statusBadge = new Label(currentStatus);
        statusBadge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");

        HBox titleLine = new HBox(10, title, statusBadge);
        titleLine.setAlignment(Pos.CENTER_LEFT);

        Label sub = new Label("Select the most suitable verified organizer for this customer request.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox titleBox = new VBox(4, back, titleLine, sub);

        // Summary Card
        VBox summary = createSummaryBox(ev);

        // Recommended Organizers Cards List
        Label recTitle = new Label("Verified Available Organizers");
        recTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        recTitle.setTextFill(Color.web(PRIMARY));

        Label selected = new Label("Selected Organizer: None");
        selected.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-padding: 8 14; -fx-border-color: #BFDBFE; -fx-border-radius: 6; -fx-background-radius: 6;");

        String[] current = {""};
        HBox cards = new HBox(12);

        List<OrganizerProfile> dbOrganizers = organizerService.getAllOrganizers();
        if (dbOrganizers == null || dbOrganizers.isEmpty()) {
            dbOrganizers = eventService.getAllActiveOrganizers();
        }

        if (dbOrganizers != null && !dbOrganizers.isEmpty()) {
            for (int i = 0; i < dbOrganizers.size(); i++) {
                OrganizerProfile org = dbOrganizers.get(i);
                boolean isBest = (i == 0);
                String orgName = org.getOrganizationName() != null ? org.getOrganizationName() : (org.getName() != null ? org.getName() : "Organizer");

                if (isBest) {
                    selectedOrganizerObj = org;
                    selected.setText("Selected Organizer: " + orgName);
                    current[0] = orgName;
                }

                cards.getChildren().add(createOrgCard(org, isBest, selected, current));
            }
        }

        VBox recommended = new VBox(10, recTitle, cards);

        // Assignment Confirmation Box
        VBox confirmBox = createConfirmationBox(ev, selected, current);
        confirmBox.setPrefWidth(340);

        HBox body = new HBox(18, recommended, confirmBox);
        HBox.setHgrow(recommended, Priority.ALWAYS);

        content.getChildren().addAll(titleBox, summary, body);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);
        rootLayer.getChildren().add(root);

        organizerAssignScene = new Scene(rootLayer, 1550, 785);
        return organizerAssignScene;
    }

    private VBox createOrgCard(OrganizerProfile org, boolean best, Label selected, String[] current) {
        String name = (org.getOrganizationName() != null && !org.getOrganizationName().isBlank())
                ? org.getOrganizationName()
                : (org.getName() != null ? org.getName() : "Organizer");

        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setPrefWidth(210);
        card.setStyle("-fx-background-color:white; -fx-border-color:" + (best ? PRIMARY : BORDER) + "; -fx-border-width:" + (best ? "2" : "1") + "; -fx-border-radius:10; -fx-background-radius:10;");

        Label title = new Label(name);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        Label loc = new Label("📍 " + (org.getAddress() != null ? org.getAddress() : "Pune"));
        loc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Label match = new Label(best ? "95% Best Match" : "85% Match");
        match.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;");

        Button select = new Button("Select");
        select.setMaxWidth(Double.MAX_VALUE);
        select.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6; -fx-background-radius: 6; -fx-cursor: hand;");

        select.setOnAction(e -> {
            selected.setText("Selected Organizer: " + name);
            current[0] = name;
            selectedOrganizerObj = org;
        });

        card.getChildren().addAll(title, loc, match, select);
        return card;
    }

    private VBox createConfirmationBox(Event ev, Label selected, String[] current) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        Label t = new Label("Assignment Action");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(PRIMARY));

        TextArea notes = new TextArea();
        notes.setPromptText("Add internal instructions for this organizer...");
        notes.setPrefRowCount(3);

        Button confirmBtn = new Button("Confirm Assignment ✓");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9; -fx-background-radius: 6; -fx-cursor: hand;");

        confirmBtn.setOnAction(e -> {
            String eventId = (ev != null && ev.getEventId() != null) ? ev.getEventId() : "";
            String orgId = (selectedOrganizerObj != null && selectedOrganizerObj.getOrganizerId() != null) ? selectedOrganizerObj.getOrganizerId() : "";

            if (!eventId.isBlank() && !orgId.isBlank()) {
                eventService.assignOrganizerToEvent(eventId, orgId, current[0], "Admin", notes.getText());
            }

            // In-Scene Modal (No Extra Stage Window)
            showInAppModal("Assignment Confirmed!", "Organizer [" + current[0] + "] has been successfully assigned to this event.", () -> {
                Stage stage = (Stage) confirmBtn.getScene().getWindow();
                NavigationManager.navigate("Event Requests", stage);
            });
        });

        box.getChildren().addAll(t, selected, new Label("Assignment Notes:"), notes, confirmBtn);
        return box;
    }

    private VBox createSummaryBox(Event ev) {
        VBox summary = new VBox(8);
        summary.setPadding(new Insets(14, 18, 14, 18));
        summary.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        String eName = ev != null ? ev.getEventName() : "Event";
        String eDate = ev != null ? (ev.getEventDate() != null ? ev.getEventDate() : ev.getStartDate()) : "TBD";
        String eBudget = ev != null ? ev.getBudget() : "N/A";
        int eGuests = ev != null ? ev.getGuests() : 100;

        row.getChildren().addAll(
                new Label("Event: " + eName),
                new Label("Date: " + eDate),
                new Label("Budget: " + eBudget),
                new Label("Guests: " + eGuests)
        );

        summary.getChildren().addAll(new Label("Event Context Summary:"), row);
        return summary;
    }

    private void showInAppModal(String title, String message, Runnable onClose) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        VBox modal = new VBox(14);
        modal.setMaxWidth(380);
        modal.setPadding(new Insets(24));
        modal.setAlignment(Pos.CENTER);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 14px;");
        modal.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));

        Circle c = new Circle(24, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 20px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        t.setTextFill(Color.web(PRIMARY));

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-text-alignment: center;");

        Button okBtn = new Button("Okay, Done");
        okBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        okBtn.setOnAction(e -> {
            rootLayer.getChildren().remove(overlay);
            if (onClose != null) onClose.run();
        });

        modal.getChildren().addAll(iconStack, t, m, okBtn);
        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
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