package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class EventRequestsO {

    private Scene eventRequestsOScene;
    private String uid;
    private String idToken;

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    private static final String HIGH_BG = "#FCE8E6";
    private static final String HIGH_TEXT = "#C5221F";

    private VBox rowsContainer;
    private Label newReqValLbl;
    private Label pendingValLbl;
    private Label acceptedValLbl;
    private Label rejectedValLbl;

    public EventRequestsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public EventRequestsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene geteventRequestsOScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setLeft(createSidebar(callBackAction));
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 30, 30, 30));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        VBox titleBox = new VBox(4);
        Label title = new Label("Event Requests");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Review event requests assigned to you by the Admin and confirm your decision.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        HBox metricCardsRow = createMetricCardsRow();

        HBox mainGrid = new HBox(20);
        VBox leftColumn = new VBox(20);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox filterCard = createFilterBarCard();
        VBox requestsTableCard = createRequestsTableCard();

        leftColumn.getChildren().addAll(filterCard, requestsTableCard);

        VBox rightColumn = createOrganizerInsightsCard();
        rightColumn.setPrefWidth(320);
        rightColumn.setMinWidth(300);

        mainGrid.getChildren().addAll(leftColumn, rightColumn);

        content.getChildren().addAll(titleBox, metricCardsRow, mainGrid);
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        loadOrganizerAssignedRequestsAsync();

        eventRequestsOScene = new Scene(root, 1550, 785);
        return eventRequestsOScene;
    }

    private void loadOrganizerAssignedRequestsAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                OrganizerController controller = new OrganizerController();
                // FETCH ONLY EVENTS ASSIGNED SPECIFICALLY TO THIS ORGANIZER UID
                return controller.getAssignedEvents(uid);
            }
        };

        task.setOnSucceeded(evt -> {
            List<Event> assignedList = task.getValue();
            Platform.runLater(() -> {
                if (rowsContainer != null) rowsContainer.getChildren().clear();

                int newCount = 0;
                int pendingCount = 0;
                int acceptedCount = 0;
                int rejectedCount = 0;

                if (assignedList != null && !assignedList.isEmpty()) {
                    for (Event ev : assignedList) {
                        String oStatus = ev.getOrganizerStatus() != null ? ev.getOrganizerStatus().toUpperCase() : "PENDING";

                        if ("PENDING".equals(oStatus)) {
                            newCount++;
                            pendingCount++;

                            // Render pending request for organizer decision
                            if (rowsContainer != null) {
                                String detailsStr = "📅 " + (ev.getEventDate() != null ? ev.getEventDate() : "TBD") +
                                        "   👥 " + ev.getGuests() + " Guests" +
                                        "   💳 " + (ev.getBudget() != null ? ev.getBudget() : "N/A");

                                rowsContainer.getChildren().add(createRequestRow(
                                        ev.getEventId(),
                                        ev.getEventName() != null ? ev.getEventName() : "Unnamed Event",
                                        ev.getEventType() != null ? ev.getEventType() : "General",
                                        detailsStr,
                                        "High", HIGH_BG, HIGH_TEXT,
                                        "Assigned by Admin", LIGHT_BLUE, PRIMARY_BLUE
                                ));
                            }
                        } else if ("ACCEPTED".equals(oStatus)) {
                            acceptedCount++;
                        } else if ("REJECTED".equals(oStatus)) {
                            rejectedCount++;
                        }
                    }
                }

                if (rowsContainer != null && rowsContainer.getChildren().isEmpty()) {
                    renderEmptyRequestsState();
                }

                if (newReqValLbl != null) newReqValLbl.setText(String.format("%02d", newCount));
                if (pendingValLbl != null) pendingValLbl.setText(String.format("%02d", pendingCount));
                if (acceptedValLbl != null) acceptedValLbl.setText(String.format("%02d", acceptedCount));
                if (rejectedValLbl != null) rejectedValLbl.setText(String.format("%02d", rejectedCount));
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderEmptyRequestsState() {
        VBox emptyBox = new VBox(8);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(35));

        Label icon = new Label("📫"); icon.setStyle("-fx-font-size: 28px;");
        Label title = new Label("No new requests assigned");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label sub = new Label("When the Admin assigns an event request to you, it will appear here.");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        emptyBox.getChildren().addAll(icon, title, sub);
        rowsContainer.getChildren().add(emptyBox);
    }

    private HBox createMetricCardsRow() {
        HBox row = new HBox(16);
        newReqValLbl = new Label("00");
        pendingValLbl = new Label("00");
        acceptedValLbl = new Label("00");
        rejectedValLbl = new Label("00");

        row.getChildren().addAll(
                createKpiCard("NEW REQUESTS", newReqValLbl, "Needs review", "🆕", PRIMARY_BLUE),
                createKpiCard("PENDING", pendingValLbl, "Awaiting decision", "⌛", SECONDARY_TEXT),
                createKpiCard("ACCEPTED", acceptedValLbl, "This month", "✓", "#137333"),
                createKpiCard("REJECTED", rejectedValLbl, "This month", "✕", "#C5221F")
        );
        return row;
    }

    private VBox createKpiCard(String label, Label valLbl, String subtext, String iconStr, String accentColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 14px; -fx-text-fill: " + accentColor + ";");

        topRow.getChildren().addAll(lbl, spacer, icon);
        valLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subLbl = new Label(subtext);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, valLbl, subLbl);
        return card;
    }

    private VBox createFilterBarCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search by event name or customer...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchInput);

        filterRow.getChildren().addAll(searchBox);
        card.getChildren().add(filterRow);
        return card;
    }

    private VBox createRequestsTableCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label cardTitle = new Label("Assigned Event Requests");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 4px;");

        Label col1 = createColumnHeader("EVENT TYPE", 0.30);
        Label col2 = createColumnHeader("DETAILS", 0.32);
        Label col3 = createColumnHeader("PRIORITY", 0.13);
        Label col4 = createColumnHeader("STATUS", 0.15);
        Label col5 = createColumnHeader("ACTION", 0.10);

        tableHeader.getChildren().addAll(col1, col2, col3, col4, col5);
        rowsContainer = new VBox(0);

        card.getChildren().addAll(cardTitle, tableHeader, rowsContainer);
        return card;
    }

    private Label createColumnHeader(String text, double widthPercent) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        return label;
    }

    private HBox createRequestRow(String eventId, String eventName, String eventType, String details,
                                 String priority, String priorityBg, String priorityFg,
                                 String status, String statusBg, String statusFg) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 12, 14, 12));
        row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        VBox typeBox = new VBox(3);
        Label nameLbl = new Label(eventName);
        nameLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label typeLbl = new Label(eventType);
        typeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        typeBox.getChildren().addAll(nameLbl, typeLbl);

        Label detailsLbl = new Label(details);
        detailsLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label priorityBadge = new Label(priority);
        priorityBadge.setStyle("-fx-background-color: " + priorityBg + "; -fx-text-fill: " + priorityFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12px;");

        Label statusBadge = new Label(status);
        statusBadge.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 12px;");

        Button actionBtn = new Button("View Details →");
        actionBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;");
        actionBtn.setOnAction(e -> {
            AdminContext.setSelectedEventId(eventId);
            EventDetailsO reviewPage = new EventDetailsO(uid, idToken);
            LandingPage.landingPageStage.setScene(reviewPage.getScene(LandingPage.landingPageStage, () -> {
                LandingPage.landingPageStage.setScene(eventRequestsOScene);
            }));
        });

        HBox c1 = new HBox(typeBox); HBox c2 = new HBox(detailsLbl); HBox c3 = new HBox(priorityBadge); HBox c4 = new HBox(statusBadge); HBox c5 = new HBox(actionBtn);
        c1.setAlignment(Pos.CENTER_LEFT); c2.setAlignment(Pos.CENTER_LEFT); c3.setAlignment(Pos.CENTER_LEFT); c4.setAlignment(Pos.CENTER_LEFT); c5.setAlignment(Pos.CENTER_LEFT);

        c1.prefWidthProperty().bind(row.widthProperty().multiply(0.30));
        c2.prefWidthProperty().bind(row.widthProperty().multiply(0.32));
        c3.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        c4.prefWidthProperty().bind(row.widthProperty().multiply(0.15));
        c5.prefWidthProperty().bind(row.widthProperty().multiply(0.10));

        row.getChildren().addAll(c1, c2, c3, c4, c5);
        return row;
    }

    private VBox createOrganizerInsightsCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 12px;");

        Label cardTitle = new Label("📈 Organizer Insights");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox itemsBox = new VBox(14);
        itemsBox.getChildren().add(createInsightItem("⚠️ Assigned Requests", "Review your pending requests assigned by Admin."));
        itemsBox.getChildren().add(createInsightItem("❗ Action Required", "Accept or reject requests to confirm event schedule."));

        card.getChildren().addAll(cardTitle, itemsBox);
        return card;
    }

    private VBox createInsightItem(String headerText, String bodyText) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 8px;");

        Label header = new Label(headerText);
        header.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label body = new Label(bodyText);
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 11px; -fx-text-fill: #EAF3FF;");

        box.getChildren().addAll(header, body);
        return box;
    }

    private VBox createSidebar(Runnable backAction) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, () -> openDashboard(backAction)));
        menu.getChildren().add(createNavBtn("Event Requests", true, () -> {}));
        menu.getChildren().add(createNavBtn("My Events", false, this::openMyEvents));
        menu.getChildren().add(createNavBtn("Venue", false, this::openVenue));
        menu.getChildren().add(createNavBtn("Vendors", false, this::openVendors));
        menu.getChildren().add(createNavBtn("Schedule", false, this::openSchedule));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottom = new VBox(6);
        bottom.getChildren().add(createNavBtn("Notifications", false, this::openNotifications));
        bottom.getChildren().add(createNavBtn("Settings", false, this::openSettings));
        bottom.getChildren().add(createNavBtn("Profile", false, this::openProfile));

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer, bottom);
        return sidebar;
    }

    private Button createNavBtn(String title, boolean active, Runnable action) {
        Button b = new Button(title);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(active ? "-fx-background-color: #EAF3FF; -fx-text-fill: #0B4EA2; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6px;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) b.setOnAction(e -> action.run());
        return b;
    }

    private HBox createTopBar() {
        HBox bar = new HBox();
        bar.setPrefHeight(65);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 30, 0, 30));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label titleLbl = new Label("Event Requests Center");
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle userAvatar = new Circle(16, Color.web(BRIGHT_BLUE));
        userAvatar.setStyle("-fx-cursor: hand;");
        userAvatar.setOnMouseClicked(e -> openProfile());

        bar.getChildren().addAll(titleLbl, spacer, userAvatar);
        return bar;
    }

    private void openDashboard(Runnable backAction) {
        if (backAction != null) backAction.run();
        else {
            OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
            LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
        }
    }
    private void openMyEvents() {
        MyEventsO view = new MyEventsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getMyEventsOScene(this::openDashboardNull));
    }
    private void openVenue() {
        VenueManagementO view = new VenueManagementO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::openDashboardNull));
    }
    private void openVendors() {
        VendorManagementO view = new VendorManagementO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(this::openDashboardNull));
    }
    private void openSchedule() {
        ScheduleO view = new ScheduleO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::openDashboardNull));
    }
    private void openNotifications() {
        NotificationsO view = new NotificationsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getNotificationScene());
    }
    private void openSettings() {
        SettingsO view = new SettingsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getSettingScene(this::openDashboardNull));
    }
    private void openProfile() {
        ProfileO view = new ProfileO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getProfileScene());
    }
    private void openDashboardNull() {
        openDashboard(null);
    }
}