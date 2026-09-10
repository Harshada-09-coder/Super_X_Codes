package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EventDetailsO {

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_TEXT = "#137333";
    private static final String RED_TEXT = "#C5221F";

    private Label summaryStatusLabel;
    private VBox actionAreaContainer;

    private Label eventTitleLabel;
    private Label eventTypeSubLabel;
    private Label eventNameOverviewVal;
    private Label eventTypeOverviewVal;
    private Label dateLabel;
    private Label timeLabel;
    private Label guestsLabel;
    private Label locationLabel;

    private Label customerNameLabel;
    private Label customerContactLabel;

    private FlowPane requirementsChipsPane;
    private Text notesTextContent;

    private Label summaryDateRowVal;
    private Label summaryGuestsRowVal;
    private Label summaryLocationRowVal;
    private Label summaryBudgetRowVal;

    private String uid;
    private String idToken;
    private Runnable backAction;
    private Event currentEvent;

    public EventDetailsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public EventDetailsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setLeft(createCommonOrganizerSidebar("Event Requests"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);
        loadRealEventDetailsAsync(stage);

        return new Scene(root, 1550, 785);
    }

    private VBox createCommonOrganizerSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard" -> {
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
            }
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> {
                MyEventsO myEvents = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(myEvents.getMyEventsOScene(null));
            }
            case "Venue" -> {
                VenueManagementO venue = new VenueManagementO();
                LandingPage.landingPageStage.setScene(venue.getScene(LandingPage.landingPageStage, null));
            }
            case "Vendors" -> {
                VendorManagementO vendor = new VendorManagementO(uid, idToken);
                LandingPage.landingPageStage.setScene(vendor.getvendorManagementOScene(null));
            }
            case "Schedule" -> {
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
            }
            case "Notifications" -> {
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
            }
            case "Profile" -> {
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
            }
            case "Settings" -> {
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
            }
            case "Support" -> {
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
            }
        }
    }

    private VBox createMainContent(Stage stage) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 30, 30, 30));

        HBox breadcrumbRow = new HBox();
        breadcrumbRow.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("Event Requests  /  ");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));

        Label bc2 = new Label("Event Review");
        bc2.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpacer = new Region();
        HBox.setHgrow(bcSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Requests");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> handleBack(stage));

        breadcrumbRow.getChildren().addAll(bc1, bc2, bcSpacer, backBtn);

        eventTitleLabel = new Label("Loading Event...");
        eventTitleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        eventTypeSubLabel = new Label("Event Request");
        eventTypeSubLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox titleBox = new VBox(2, eventTitleLabel, eventTypeSubLabel);

        HBox columns = new HBox(20);
        VBox leftColumn = new VBox(16);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        leftColumn.getChildren().addAll(
                createEventOverviewCard(),
                createEventRequirementsCard()
        );

        VBox rightColumn = new VBox(16);
        rightColumn.setPrefWidth(360);

        rightColumn.getChildren().addAll(
                createRequestSummaryCard(),
                createActionCard(stage)
        );

        columns.getChildren().addAll(leftColumn, rightColumn);
        content.getChildren().addAll(breadcrumbRow, titleBox, columns);
        return content;
    }

    private VBox createEventOverviewCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("Event Overview (Live Customer Inputs)");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(24); grid.setVgap(12);

        eventNameOverviewVal = new Label("Loading...");
        eventTypeOverviewVal = new Label("Loading...");
        dateLabel = new Label("Loading...");
        timeLabel = new Label("Loading...");
        guestsLabel = new Label("Loading...");
        locationLabel = new Label("Loading...");

        grid.add(createDetailItem("Event Name", eventNameOverviewVal, "fas-tag"), 0, 0);
        grid.add(createDetailItem("Category", eventTypeOverviewVal, "fas-bookmark"), 1, 0);
        grid.add(createDetailItem("Event Duration (Dates)", dateLabel, "fas-calendar-alt"), 0, 1);
        grid.add(createDetailItem("Start - End Time", timeLabel, "fas-clock"), 1, 1);
        grid.add(createDetailItem("Expected Guests", guestsLabel, "fas-users"), 0, 2);
        grid.add(createDetailItem("Location / City", locationLabel, "fas-map-marker-alt"), 1, 2);

        card.getChildren().addAll(cardTitle, grid);
        return card;
    }

    private VBox createEventRequirementsCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("Customer-Selected Requirements & Timefold Plan");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        requirementsChipsPane = new FlowPane();
        requirementsChipsPane.setHgap(8);
        requirementsChipsPane.setVgap(8);

        notesTextContent = new Text("Loading customer instructions and AI plan...");
        notesTextContent.setStyle("-fx-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        notesTextContent.setWrappingWidth(560);

        card.getChildren().addAll(cardTitle, requirementsChipsPane, new Label("Special Notes & AI Optimization:"), notesTextContent);
        return card;
    }

    private VBox createRequestSummaryCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("Request Summary");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        summaryStatusLabel = new Label("Pending Review");
        summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        summaryDateRowVal = new Label("Loading...");
        summaryGuestsRowVal = new Label("Loading...");
        summaryLocationRowVal = new Label("Loading...");
        summaryBudgetRowVal = new Label("Loading...");

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createSummaryRow("Decision Status", summaryStatusLabel),
                createSummaryRow("Dates", summaryDateRowVal),
                createSummaryRow("Guests", summaryGuestsRowVal),
                createSummaryRow("Location", summaryLocationRowVal),
                createSummaryRow("Budget", summaryBudgetRowVal)
        );

        card.getChildren().addAll(cardTitle, rows);
        return card;
    }

    private VBox createActionCard(Stage stage) {
        actionAreaContainer = createCardContainer();
        renderActionBox(stage);
        return actionAreaContainer;
    }

    private void renderActionBox(Stage stage) {
        if (actionAreaContainer == null) return;
        actionAreaContainer.getChildren().clear();

        String oStatus = (currentEvent != null && currentEvent.getOrganizerStatus() != null)
                ? currentEvent.getOrganizerStatus().toUpperCase() : "PENDING";
        String evStatus = (currentEvent != null && currentEvent.getStatus() != null)
                ? currentEvent.getStatus().toUpperCase() : "PENDING";

        if ("PENDING".equalsIgnoreCase(oStatus) || "NOT_ASSIGNED".equalsIgnoreCase(oStatus) || "PENDING".equalsIgnoreCase(evStatus)) {
            Label actionTitle = new Label("Confirm Event Assignment");
            actionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

            HBox btnBox = new HBox(10);
            Button rejectBtn = new Button("Reject");
            rejectBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rejectBtn, Priority.ALWAYS);
            rejectBtn.setStyle("-fx-background-color: white; -fx-border-color: " + RED_TEXT + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-border-radius: 6px; -fx-cursor: hand;");
            rejectBtn.setOnAction(e -> handleRejectAction(stage));

            Button acceptBtn = new Button("Accept Request");
            acceptBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(acceptBtn, Priority.ALWAYS);
            acceptBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> handleAcceptAction(stage));

            btnBox.getChildren().addAll(rejectBtn, acceptBtn);
            actionAreaContainer.getChildren().addAll(actionTitle, btnBox);

        } else if ("ACCEPTED".equalsIgnoreCase(oStatus) || "CONFIRMED".equalsIgnoreCase(evStatus)) {
            VBox approvedBox = new VBox(8);
            approvedBox.setAlignment(Pos.CENTER);

            Label successTitle = new Label("✓ Event Confirmed & Live Synced");
            successTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");

            Button planBtn = new Button("Open Event Planning →");
            planBtn.setMaxWidth(Double.MAX_VALUE);
            planBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
            planBtn.setOnAction(e -> {
                ViewEventO view = new ViewEventO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            });

            approvedBox.getChildren().addAll(successTitle, planBtn);
            actionAreaContainer.getChildren().add(approvedBox);
        }
    }

    private void handleAcceptAction(Stage stage) {
        if (currentEvent == null || currentEvent.getEventId() == null) return;

        OrganizerController controller = new OrganizerController();
        OrganizerProfile profile = controller.getOrganizerProfile(uid);
        String orgName = (profile != null && profile.getName() != null && !profile.getName().isBlank())
                ? profile.getName() : "Organizer";

        boolean success = controller.acceptEventAssignment(currentEvent.getEventId(), uid, orgName);

        if (success) {
            currentEvent.setOrganizerId(uid);
            currentEvent.setOrganizerName(orgName);
            currentEvent.setOrganizerStatus("ACCEPTED");
            currentEvent.setStatus("CONFIRMED");

            summaryStatusLabel.setText("ACCEPTED & CONFIRMED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");

            renderActionBox(stage);

            Alert a = new Alert(Alert.AlertType.INFORMATION, "Event accepted! Status is now CONFIRMED across Customer and Admin screens.");
            a.setHeaderText("Request Confirmed");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Failed to update Firestore.");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
        }
    }

    private void handleRejectAction(Stage stage) {
        if (currentEvent == null || currentEvent.getEventId() == null) return;

        OrganizerController controller = new OrganizerController();
        boolean success = controller.rejectEventAssignment(currentEvent.getEventId());

        if (success) {
            currentEvent.setOrganizerStatus("REJECTED");
            currentEvent.setStatus("REJECTED");

            summaryStatusLabel.setText("REJECTED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

            renderActionBox(stage);
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Event request rejected.");
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait();
        }
    }

    private void loadRealEventDetailsAsync(Stage stage) {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController adminController = new AdminController();
                Event ev = adminController.getEventById(selectedEvtId);
                currentEvent = ev;

                if (ev != null) {
                    Platform.runLater(() -> {
                        eventTitleLabel.setText(ev.getEventName() != null ? ev.getEventName() : "Event Request");
                        eventNameOverviewVal.setText(ev.getEventName() != null ? ev.getEventName() : "N/A");
                        eventTypeOverviewVal.setText(ev.getEventType() != null ? ev.getEventType() : "General");

                        String dateRange = (ev.getStartDate() != null && !ev.getStartDate().isBlank())
                                ? ev.getStartDate() + (ev.getEndDate() != null && !ev.getEndDate().isBlank() && !ev.getStartDate().equals(ev.getEndDate()) ? " to " + ev.getEndDate() : "")
                                : (ev.getEventDate() != null ? ev.getEventDate() : "TBD");

                        dateLabel.setText(dateRange);
                        summaryDateRowVal.setText(dateRange);

                        String timeRange = (ev.getStartTime() != null && !ev.getStartTime().isBlank())
                                ? ev.getStartTime() + (ev.getEndTime() != null && !ev.getEndTime().isBlank() ? " - " + ev.getEndTime() : "")
                                : (ev.getTime() != null ? ev.getTime() : "TBD");

                        timeLabel.setText(timeRange);
                        guestsLabel.setText(ev.getGuests() + " Expected");
                        summaryGuestsRowVal.setText(String.valueOf(ev.getGuests()));
                        locationLabel.setText(ev.getCity() != null ? ev.getCity() : "N/A");
                        summaryLocationRowVal.setText(locationLabel.getText());
                        summaryBudgetRowVal.setText(ev.getBudget() != null ? ev.getBudget() : "N/A");

                        if (requirementsChipsPane != null) {
                            requirementsChipsPane.getChildren().clear();
                            String reqStr = ev.getRequirements();
                            if (reqStr != null && !reqStr.isBlank()) {
                                for (String r : reqStr.split(",\\s*")) {
                                    requirementsChipsPane.getChildren().add(createRequirementChip(r.trim(), "fas-check-circle"));
                                }
                            }
                        }

                        if (notesTextContent != null) {
                            String notes = ev.getNotes() != null && !ev.getNotes().isBlank() ? ev.getNotes() : "Customer notes synchronized.";
                            notesTextContent.setText(notes);
                        }

                        renderActionBox(stage);
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void handleBack(Stage stage) {
        if (backAction != null) backAction.run();
        else {
            EventRequestsO requestsView = new EventRequestsO(uid, idToken);
            LandingPage.landingPageStage.setScene(requestsView.geteventRequestsOScene(null));
        }
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        return card;
    }

    private VBox createDetailItem(String label, Label valueLabel, String iconLiteral) {
        VBox box = new VBox(2);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valueLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().addAll(l, valueLabel);
        return box;
    }

    private HBox createRequirementChip(String text, String iconLiteral) {
        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(4, 10, 4, 10));
        chip.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px;");

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(11);
        icon.setIconColor(Color.web(PRIMARY_BLUE));

        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold;");

        chip.getChildren().addAll(icon, l);
        return chip;
    }

    private HBox createSummaryRow(String labelText, Label valueLabel) {
        HBox row = new HBox();
        Label l = new Label(labelText); l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        valueLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        row.getChildren().addAll(l, spacer, valueLabel);
        return row;
    }
}