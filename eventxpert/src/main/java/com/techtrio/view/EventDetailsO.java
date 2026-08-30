package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.CustomerProfile;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EventDetailsO {

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_TEXT = "#137333";
    private static final String RED_TEXT = "#C5221F";
    private static final String YELLOW_BG = "#FEF7E0";
    private static final String YELLOW_TEXT = "#B06000";

    // Dynamic UI Controls
    private Label summaryStatusLabel;
    private VBox actionAreaContainer;
    private VBox timelineContainer;

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

    private Label summaryStatusRowVal;
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
        this.uid = uid;
        this.idToken = idToken;
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar(stage));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        loadRealEventDetailsAsync(stage);

        return new Scene(root, 1550, 785);
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
                    CustomerProfile cust = adminController.getCustomerProfile(ev.getCustomerId());

                    Platform.runLater(() -> {
                        String name = ev.getEventName() != null ? ev.getEventName() : "Event Request";
                        String type = ev.getEventType() != null ? ev.getEventType() : "General";
                        String date = ev.getEventDate() != null ? ev.getEventDate() : "TBD";
                        String time = ev.getTime() != null && !ev.getTime().isBlank() ? ev.getTime() : "09:00 AM – 05:00 PM";
                        String loc = ev.getCity() != null ? ev.getCity() : "Pune, Maharashtra";
                        String budget = ev.getBudget() != null ? ev.getBudget() : "₹5,00,000";

                        if (eventTitleLabel != null) eventTitleLabel.setText(name);
                        if (eventTypeSubLabel != null) eventTypeSubLabel.setText(type + " • Event Request");

                        if (eventNameOverviewVal != null) eventNameOverviewVal.setText(name);
                        if (eventTypeOverviewVal != null) eventTypeOverviewVal.setText(type);

                        if (dateLabel != null) dateLabel.setText("📅 " + date);
                        if (timeLabel != null) timeLabel.setText("⏰ " + time);
                        if (guestsLabel != null) guestsLabel.setText("👥 " + ev.getGuests() + " Guests");
                        if (locationLabel != null) locationLabel.setText("📍 " + loc);

                        // CUSTOMER DATA
                        if (cust != null) {
                            String cName = cust.getName() != null && !cust.getName().isBlank() ? cust.getName() : "Customer";
                            String cEmail = cust.getEmail() != null ? cust.getEmail() : "N/A";
                            String cPhone = cust.getMobileNo() != null ? cust.getMobileNo() : "N/A";

                            if (customerNameLabel != null) customerNameLabel.setText(cName);
                            if (customerContactLabel != null) customerContactLabel.setText("Contact: " + cName + " (" + cEmail + " | " + cPhone + ")");
                        }

                        // REAL-TIME CUSTOMER REQUIREMENTS CHIPS
                        if (requirementsChipsPane != null) {
                            requirementsChipsPane.getChildren().clear();
                            String reqStr = ev.getRequirements();

                            if (reqStr != null && !reqStr.isBlank()) {
                                String[] reqArray = reqStr.split(",");
                                for (String r : reqArray) {
                                    String trimmed = r.trim();
                                    if (!trimmed.isEmpty()) {
                                        requirementsChipsPane.getChildren().add(createRequirementChip(getEmojiForReq(trimmed) + " " + trimmed));
                                    }
                                }
                            } else {
                                requirementsChipsPane.getChildren().addAll(
                                        createRequirementChip("🏛️ Auditorium"),
                                        createRequirementChip("🎭 Stage"),
                                        createRequirementChip("🔊 Sound"),
                                        createRequirementChip("🍽️ Catering")
                                );
                            }
                        }

                        // REAL-TIME NOTES
                        if (notesTextContent != null) {
                            String notes = ev.getNotes() != null && !ev.getNotes().isBlank() ? ev.getNotes() : "No special notes provided by customer.";
                            notesTextContent.setText(notes);
                        }

                        // SUMMARY SIDEBAR
                        if (summaryDateRowVal != null) summaryDateRowVal.setText(date);
                        if (summaryGuestsRowVal != null) summaryGuestsRowVal.setText(String.valueOf(ev.getGuests()));
                        if (summaryLocationRowVal != null) summaryLocationRowVal.setText(loc);
                        if (summaryBudgetRowVal != null) summaryBudgetRowVal.setText(budget);

                        String oStatus = ev.getOrganizerStatus() != null ? ev.getOrganizerStatus().toUpperCase() : "PENDING";
                        if (summaryStatusLabel != null) {
                            summaryStatusLabel.setText("ACCEPTED".equals(oStatus) ? "ACCEPTED" : ("REJECTED".equals(oStatus) ? "REJECTED" : "Pending Review"));
                            if ("ACCEPTED".equals(oStatus) || "CONFIRMED".equals(ev.getStatus())) {
                                summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
                            } else if ("REJECTED".equals(oStatus)) {
                                summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");
                            }
                        }

                        renderActionBox(stage);
                        renderTimeline();
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private String getEmojiForReq(String reqName) {
        String lower = reqName.toLowerCase();
        if (lower.contains("venue") || lower.contains("auditorium")) return "🏛️";
        if (lower.contains("catering") || lower.contains("food")) return "🍽️";
        if (lower.contains("decor") || lower.contains("stage")) return "🎭";
        if (lower.contains("photo") || lower.contains("camera")) return "📷";
        if (lower.contains("sound") || lower.contains("av") || lower.contains("audio")) return "🔊";
        if (lower.contains("projector")) return "📹";
        if (lower.contains("seating")) return "🪑";
        return "☑️";
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search events, vendors, attendees...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Circle circle = new Circle(16, Color.web(PRIMARY_BLUE));

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, helpBtn, circle);
        return topBar;
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setSpacing(6);

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        sidebar.getChildren().addAll(brand, new Separator(),
            createNavItem("Dashboard", false, () -> goDashboard(stage)),
            createNavItem("Event Requests", true, () -> handleBack(stage)),
            createNavItem("My Events", false, () -> {
                MyEventsO view = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getMyEventsOScene(() -> goDashboard(stage)));
            }),
            createNavItem("Venue", false, () -> {
                VenueManagementO view = new VenueManagementO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
            }),
            createNavItem("Vendors", false, () -> {
                VendorManagementO view = new VendorManagementO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(() -> goDashboard(stage)));
            }),
            createNavItem("Schedule", false, () -> {
                ScheduleO view = new ScheduleO();
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
            })
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().add(spacer);
        return sidebar;
    }

    private Button createNavItem(String text, boolean isActive, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(isActive ? "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6px;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createMainContent(Stage stage) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumbs & Header
        HBox breadcrumbRow = new HBox();
        breadcrumbRow.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("Event Requests");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> handleBack(stage));

        Label bcSep = new Label("  /  ");
        bcSep.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bc2 = new Label("Event Review");
        bc2.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region bcSpacer = new Region();
        HBox.setHgrow(bcSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Requests");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> handleBack(stage));

        breadcrumbRow.getChildren().addAll(bc1, bcSep, bc2, bcSpacer, backBtn);

        HBox pageHeader = new HBox(15);
        pageHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        eventTitleLabel = new Label("Loading Event...");
        eventTitleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label newBadge = new Label("New Request");
        newBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-padding: 4 10 4 10; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");

        titleRow.getChildren().addAll(eventTitleLabel, newBadge);

        eventTypeSubLabel = new Label("Event Request");
        eventTypeSubLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(titleRow, eventTypeSubLabel);
        pageHeader.getChildren().add(titleBox);

        HBox columns = new HBox(24);

        VBox leftColumn = new VBox(20);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        leftColumn.getChildren().addAll(
            createEventOverviewCard(),
            createEventRequirementsCard(),
            createPlanningInsightsCard()
        );

        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(360);
        rightColumn.setMinWidth(340);

        rightColumn.getChildren().addAll(
            createRequestSummaryCard(),
            createTimelineCard(),
            createActionCard(stage)
        );

        columns.getChildren().addAll(leftColumn, rightColumn);

        content.getChildren().addAll(breadcrumbRow, pageHeader, columns);
        return content;
    }

    private VBox createEventOverviewCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("ℹ️ Event Overview");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(16);
        grid.setPadding(new Insets(10, 0, 15, 0));

        eventNameOverviewVal = new Label("Loading...");
        eventTypeOverviewVal = new Label("Loading...");
        dateLabel = new Label("📅 Loading...");
        timeLabel = new Label("⏰ Loading...");
        guestsLabel = new Label("👥 Loading...");
        locationLabel = new Label("📍 Loading...");

        grid.add(createDetailItem("Event Name", eventNameOverviewVal), 0, 0);
        grid.add(createDetailItem("Event Type", eventTypeOverviewVal), 1, 0);
        grid.add(createDetailItem("Date", dateLabel), 0, 1);
        grid.add(createDetailItem("Time", timeLabel), 1, 1);
        grid.add(createDetailItem("Expected Guests", guestsLabel), 0, 2);
        grid.add(createDetailItem("Requested Location", locationLabel), 1, 2);

        HBox customerBanner = new HBox(12);
        customerBanner.setAlignment(Pos.CENTER_LEFT);
        customerBanner.setPadding(new Insets(14, 16, 14, 16));
        customerBanner.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        StackPane custAvatar = new StackPane();
        Circle c = new Circle(18, Color.web(LIGHT_BLUE));
        Label cTxt = new Label("C");
        cTxt.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        custAvatar.getChildren().addAll(c, cTxt);

        VBox custInfo = new VBox(2);
        Label custLabel = new Label("Customer");
        custLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        customerNameLabel = new Label("Loading Customer...");
        customerNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px;");
        customerContactLabel = new Label("Loading Contacts...");
        customerContactLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        custInfo.getChildren().addAll(custLabel, customerNameLabel, customerContactLabel);

        customerBanner.getChildren().addAll(custAvatar, custInfo);

        card.getChildren().addAll(cardTitle, grid, customerBanner);
        return card;
    }

    // ==========================================
    // REAL-TIME EVENT REQUIREMENTS CARD
    // ==========================================
    private VBox createEventRequirementsCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("☑ Event Requirements");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        requirementsChipsPane = new FlowPane();
        requirementsChipsPane.setHgap(8);
        requirementsChipsPane.setVgap(8);
        requirementsChipsPane.setPadding(new Insets(8, 0, 12, 0));

        Label addReqLabel = new Label("Additional Requirements");
        addReqLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 6 0 2 0;");

        VBox addReqBox = new VBox();
        addReqBox.setPadding(new Insets(12));
        addReqBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        notesTextContent = new Text("Loading customer notes...");
        notesTextContent.setStyle("-fx-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        notesTextContent.setWrappingWidth(580);

        addReqBox.getChildren().add(notesTextContent);

        card.getChildren().addAll(cardTitle, requirementsChipsPane, addReqLabel, addReqBox);
        return card;
    }

    private VBox createPlanningInsightsCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("Planning Insights");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane insightsGrid = new GridPane();
        insightsGrid.setHgap(12); insightsGrid.setVgap(12);
        insightsGrid.setPadding(new Insets(8, 0, 0, 0));

        insightsGrid.add(createInsightBox("🌧️ Weather Forecast", "Rain expected. Ensure indoor contingencies.", "#E0E0E0", WHITE), 0, 0);
        insightsGrid.add(createInsightBox("📅 Schedule Conflict", "Clear. No overlapping major events.", "#C8E6C9", "#E8F5E9"), 1, 0);
        insightsGrid.add(createInsightBox("📊 Budget Analysis", "Estimated costs within requested limits.", "#C8E6C9", "#E8F5E9"), 0, 1);
        insightsGrid.add(createInsightBox("📍 Venue Availability", "Multiple suitable alternatives available in Pune.", "#FFE0B2", "#FFF3E0"), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        insightsGrid.getColumnConstraints().addAll(col1, col2);

        card.getChildren().addAll(cardTitle, insightsGrid);
        return card;
    }

    private VBox createRequestSummaryCard() {
        VBox card = createCardContainer();
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label cardTitle = new Label("Request Summary");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priorityBadge = new Label(" High Priority ");
        priorityBadge.setStyle("-fx-background-color: " + YELLOW_BG + "; -fx-text-fill: " + YELLOW_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 4px;");

        headerRow.getChildren().addAll(cardTitle, spacer, priorityBadge);

        VBox rows = new VBox(10);
        rows.setPadding(new Insets(12, 0, 12, 0));

        summaryStatusLabel = new Label("Pending Review");
        summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        summaryDateRowVal = new Label("Loading...");
        summaryGuestsRowVal = new Label("Loading...");
        summaryLocationRowVal = new Label("Loading...");
        summaryBudgetRowVal = new Label("Loading...");

        rows.getChildren().addAll(
            createSummaryRow("Status", summaryStatusLabel),
            createSummaryRow("Date", summaryDateRowVal),
            createSummaryRow("Guests", summaryGuestsRowVal),
            createSummaryRow("Location", summaryLocationRowVal),
            createSummaryRow("Est. Budget", summaryBudgetRowVal)
        );

        VBox sysRecBox = new VBox(6);
        sysRecBox.setPadding(new Insets(12));
        sysRecBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        HBox sysTitle = new HBox(6);
        sysTitle.setAlignment(Pos.CENTER_LEFT);
        Label sysIcon = new Label("⚙️");
        Label sysHeader = new Label("System Recommendation");
        sysHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
        sysTitle.getChildren().addAll(sysIcon, sysHeader);

        Label sysDesc = new Label("Highly feasible based on requested parameters and budget.");
        sysDesc.setWrapText(true);
        sysDesc.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px;");

        sysRecBox.getChildren().addAll(sysTitle, sysDesc);

        card.getChildren().addAll(headerRow, rows, sysRecBox);
        return card;
    }

    private VBox createTimelineCard() {
        VBox card = createCardContainer();
        Label cardTitle = new Label("Request Timeline");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        timelineContainer = new VBox(12);
        timelineContainer.setPadding(new Insets(10, 0, 0, 0));

        renderTimeline();

        card.getChildren().addAll(cardTitle, timelineContainer);
        return card;
    }

    private void renderTimeline() {
        if (timelineContainer == null) return;
        timelineContainer.getChildren().clear();

        String oStatus = (currentEvent != null && currentEvent.getOrganizerStatus() != null)
                ? currentEvent.getOrganizerStatus().toUpperCase() : "PENDING";

        timelineContainer.getChildren().addAll(
            createTimelineItem("Assigned by Admin", "Current Event", true),
            createTimelineItem("Organizer Decision", oStatus, !"PENDING".equalsIgnoreCase(oStatus))
        );
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

        if ("PENDING".equalsIgnoreCase(oStatus) || "NOT_ASSIGNED".equalsIgnoreCase(oStatus)) {
            Label actionTitle = new Label("Ready to take this event?");
            actionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

            HBox btnBox = new HBox(10);
            btnBox.setPadding(new Insets(8, 0, 0, 0));

            Button rejectBtn = new Button("Reject");
            rejectBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rejectBtn, Priority.ALWAYS);
            rejectBtn.setStyle("-fx-background-color: white; -fx-border-color: " + RED_TEXT + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-border-radius: 6px; -fx-cursor: hand; -fx-padding: 8 0;");
            rejectBtn.setOnAction(e -> handleRejectAction(stage));

            Button acceptBtn = new Button("Accept Request");
            acceptBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(acceptBtn, Priority.ALWAYS);
            acceptBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 8 0;");
            acceptBtn.setOnAction(e -> handleAcceptAction(stage));

            btnBox.getChildren().addAll(rejectBtn, acceptBtn);
            actionAreaContainer.getChildren().addAll(actionTitle, btnBox);

        } else if ("ACCEPTED".equalsIgnoreCase(oStatus)) {
            VBox approvedBox = new VBox(8);
            approvedBox.setAlignment(Pos.CENTER);

            Label successTitle = new Label("✓ Event Accepted & Confirmed");
            successTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");

            Button planBtn = new Button("Continue Planning →");
            planBtn.setMaxWidth(Double.MAX_VALUE);
            planBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 10 0;");
            planBtn.setOnAction(e -> {
                ViewEventO view = new ViewEventO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getScene(stage, () -> handleBack(stage)));
            });

            approvedBox.getChildren().addAll(successTitle, planBtn);
            actionAreaContainer.getChildren().add(approvedBox);

        } else if ("REJECTED".equalsIgnoreCase(oStatus)) {
            VBox rejectedBox = new VBox(6);
            rejectedBox.setAlignment(Pos.CENTER);

            Label rejTitle = new Label("✕ Request Rejected");
            rejTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

            rejectedBox.getChildren().add(rejTitle);
            actionAreaContainer.getChildren().add(rejectedBox);
        }
    }

    private void handleAcceptAction(Stage stage) {
        if (currentEvent == null || currentEvent.getEventId() == null) return;

        OrganizerController controller = new OrganizerController();
        boolean success = controller.acceptEventAssignment(currentEvent.getEventId());

        if (success) {
            currentEvent.setOrganizerStatus("ACCEPTED");
            currentEvent.setStatus("CONFIRMED");

            if (summaryStatusLabel != null) {
                summaryStatusLabel.setText("ACCEPTED");
                summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
            }

            renderActionBox(stage);
            renderTimeline();

            Alert a = new Alert(Alert.AlertType.INFORMATION, "Event assignment accepted! Status updated to CONFIRMED in Firestore.");
            a.showAndWait();
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Failed to update event status in Firestore.");
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

            if (summaryStatusLabel != null) {
                summaryStatusLabel.setText("REJECTED");
                summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");
            }

            renderActionBox(stage);
            renderTimeline();

            Alert a = new Alert(Alert.AlertType.INFORMATION, "Event assignment rejected.");
            a.showAndWait();
        }
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            EventRequestsO requestsView = new EventRequestsO(uid, idToken);
            LandingPage.landingPageStage.setScene(requestsView.geteventRequestsOScene(() -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        return card;
    }

    private VBox createDetailItem(String label, Label valueLabel) {
        VBox box = new VBox(3);
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valueLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().addAll(l, valueLabel);
        return box;
    }

    private HBox createRequirementChip(String text) {
        HBox chip = new HBox();
        chip.setPadding(new Insets(6, 12, 6, 12));
        chip.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16px; -fx-background-radius: 16px;");
        Label l = new Label(text); l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        chip.getChildren().add(l);
        return chip;
    }

    private VBox createInsightBox(String title, String desc, String borderHex, String bgHex) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + bgHex + "; -fx-border-color: " + borderHex + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label t = new Label(title); t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label d = new Label(desc); d.setWrapText(true); d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(t, d);
        return box;
    }

    private HBox createSummaryRow(String labelText, Label valueLabel) {
        HBox row = new HBox();
        Label l = new Label(labelText); l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        valueLabel.setStyle(valueLabel.getStyle() + "; -fx-font-size: 12px;");
        row.getChildren().addAll(l, spacer, valueLabel);
        return row;
    }

    private HBox createTimelineItem(String title, String status, boolean isDone) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(6, isDone ? Color.web(PRIMARY_BLUE) : Color.web(SECONDARY_TEXT));

        VBox textContent = new VBox(1);
        Label t = new Label(title); t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label d = new Label(status); d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textContent.getChildren().addAll(t, d);
        item.getChildren().addAll(dot, textContent);
        return item;
    }
}