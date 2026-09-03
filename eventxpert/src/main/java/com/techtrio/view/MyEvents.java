package com.techtrio.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.techtrio.controller.CustomerEventController;
import com.techtrio.model.Event;
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

public class MyEvents {

    private Scene myEventsScene;
    private String uid;
    private String idToken;
    private Runnable callBackAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    private List<Event> masterEventList = new ArrayList<>();
    private VBox eventsListContainer;

    private Label allEventsCountLbl;
    private Label upcomingCountLbl;
    private Label completedCountLbl;
    private Label draftsCountLbl;

    private TextField filterSearch;
    private ComboBox<String> statusCombo;
    private ComboBox<String> typeCombo;
    private ComboBox<String> dateCombo;

    public MyEvents() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public MyEvents(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }

    public Scene getMyEventsScene(Runnable callBackAction) {
        this.callBackAction = callBackAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Sidebar (Left) - Exactly matches Dashboard alignment
        root.setLeft(createSidebar());

        // 2. Main Content (Top bar removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox contentBox = createContentBox();
        scrollPane.setContent(contentBox);
        root.setCenter(scrollPane);

        loadDynamicEventsFromDatabase();

        myEventsScene = new Scene(root, 1550, 785);
        return myEventsScene;
    }

    // ================= 1. SIDEBAR =================
    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(6, 10, 14, 10));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label brandSub = new Label("Plan. Manage. Celebrate.");
        brandSub.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px;");
        brandTexts.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox navList = new VBox(5);
        navList.getChildren().addAll(
                createSidebarItem("Dashboard", "fas-tachometer-alt", false, this::returnToDashboard),
                createSidebarItem("Create Event", "fas-plus-circle", false, this::openCreateEvent),
                createSidebarItem("My Events", "fas-calendar-check", true, () -> {}),
                createSidebarItem("Notifications", "fas-bell", false, this::openNotifications),
                createSidebarItem("Profile", "fas-user", false, this::openProfile),
                createSidebarItem("Settings", "fas-cog", false, this::openSettings)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, navList, spacer);
        return sidebar;
    }

    private Button createSidebarItem(String title, String iconLiteral, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(38);
        btn.setPadding(new Insets(0, 12, 0, 12));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill:" + NAVY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 7px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 7px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 12px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    // ================= 2. MAIN CONTENT =================
    private VBox createContentBox() {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(24, 32, 28, 32));

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("My Events");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Manage and track all your events in one place.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button createEventBtn = new Button(" Create New Event");
        FontIcon plusIcon = new FontIcon("fas-plus");
        plusIcon.setIconSize(12);
        plusIcon.setIconColor(Color.WHITE);
        createEventBtn.setGraphic(plusIcon);
        createEventBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 9 18; -fx-cursor: hand;");
        createEventBtn.setOnAction(e -> openCreateEvent());

        headerBox.getChildren().addAll(titleBox, headerSpacer, createEventBtn);

        allEventsCountLbl = new Label("0");
        upcomingCountLbl = new Label("0");
        completedCountLbl = new Label("0");
        draftsCountLbl = new Label("0");

        HBox statsGrid = new HBox(16);
        statsGrid.getChildren().addAll(
                createStatCard("fas-calendar-alt", "All Events", allEventsCountLbl, "#0284C7"),
                createStatCard("fas-calendar-check", "Upcoming", upcomingCountLbl, "#4F46E5"),
                createStatCard("fas-check-circle", "Completed", completedCountLbl, "#16A34A"),
                createStatCard("fas-clipboard-list", "Drafts / Pending", draftsCountLbl, "#D97706")
        );

        HBox filterBox = new HBox(12);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10, 14, 10, 14));
        filterBox.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        filterSearch = new TextField();
        filterSearch.setPromptText("🔍 Search by event name...");
        filterSearch.setPrefWidth(280);
        filterSearch.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("All Status", "Planning", "Upcoming", "Completed", "Pending");
        statusCombo.setValue("All Status");
        statusCombo.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All Event Types", "Academic Event", "Family Event", "Birthday Event", "Corporate", "Wedding");
        typeCombo.setValue("All Event Types");
        typeCombo.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        dateCombo = new ComboBox<>();
        dateCombo.getItems().addAll("All Dates", "Upcoming Dates", "Past Dates");
        dateCombo.setValue("All Dates");
        dateCombo.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        Hyperlink clearFilter = new Hyperlink("✕ Clear Filters");
        clearFilter.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px;");

        filterBox.getChildren().addAll(filterSearch, statusCombo, typeCombo, dateCombo, clearFilter);

        eventsListContainer = new VBox(14);
        eventsListContainer.getChildren().add(new Label("Loading your events from database..."));

        filterSearch.textProperty().addListener((obs, o, n) -> applyFilters());
        statusCombo.valueProperty().addListener((obs, o, n) -> applyFilters());
        typeCombo.valueProperty().addListener((obs, o, n) -> applyFilters());
        dateCombo.valueProperty().addListener((obs, o, n) -> applyFilters());

        clearFilter.setOnAction(e -> {
            filterSearch.clear();
            statusCombo.setValue("All Status");
            typeCombo.setValue("All Event Types");
            dateCombo.setValue("All Dates");
            applyFilters();
        });

        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10, 0, 10, 0));

        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 20; -fx-background-radius: 7; -fx-cursor: hand;");
        backButton.setOnAction(e -> returnToDashboard());
        bottomBar.getChildren().add(backButton);

        contentBox.getChildren().addAll(headerBox, statsGrid, filterBox, eventsListContainer, bottomBar);
        return contentBox;
    }

    private HBox createStatCard(String iconLit, String title, Label countLbl, String iconColorHex) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setPrefWidth(220);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        StackPane iconHolder = new StackPane();
        Circle c = new Circle(18, Color.web(iconColorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(16);
        fontIcon.setIconColor(Color.web(iconColorHex));
        iconHolder.getChildren().addAll(c, fontIcon);

        VBox textVBox = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        countLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        textVBox.getChildren().addAll(titleLbl, countLbl);
        card.getChildren().addAll(iconHolder, textVBox);
        return card;
    }

    // ================= 3. DYNAMIC DATABASE FETCHING =================
    private void loadDynamicEventsFromDatabase() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                CustomerEventController controller = new CustomerEventController();
                return controller.getEventsByCustomerId(uid);
            }
        };

        task.setOnSucceeded(e -> {
            masterEventList = task.getValue();
            if (masterEventList == null) {
                masterEventList = new ArrayList<>();
            }

            Platform.runLater(() -> {
                updateStatsCards(masterEventList);
                renderEventList(masterEventList);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                if (eventsListContainer != null) {
                    eventsListContainer.getChildren().clear();
                    Label errLbl = new Label("Failed to load events from database.");
                    errLbl.setStyle("-fx-text-fill: #B42318; -fx-font-size: 13px;");
                    eventsListContainer.getChildren().add(errLbl);
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateStatsCards(List<Event> events) {
        if (events == null) return;

        if (allEventsCountLbl != null) allEventsCountLbl.setText(String.valueOf(events.size()));

        long upcoming = events.stream().filter(ev -> ev != null && ("UPCOMING".equalsIgnoreCase(ev.getStatus()) || "ACTIVE".equalsIgnoreCase(ev.getStatus()))).count();
        long completed = events.stream().filter(ev -> ev != null && "COMPLETED".equalsIgnoreCase(ev.getStatus())).count();
        long pending = events.stream().filter(ev -> ev != null && ("PENDING".equalsIgnoreCase(ev.getStatus()) || "PLANNING".equalsIgnoreCase(ev.getStatus()))).count();

        if (upcomingCountLbl != null) upcomingCountLbl.setText(String.valueOf(upcoming));
        if (completedCountLbl != null) completedCountLbl.setText(String.valueOf(completed));
        if (draftsCountLbl != null) draftsCountLbl.setText(String.valueOf(pending));
    }

    private void applyFilters() {
        if (masterEventList == null || eventsListContainer == null) return;

        String query = (filterSearch != null && filterSearch.getText() != null) ? filterSearch.getText().trim().toLowerCase() : "";
        String status = (statusCombo != null && statusCombo.getValue() != null) ? statusCombo.getValue() : "All Status";
        String type = (typeCombo != null && typeCombo.getValue() != null) ? typeCombo.getValue() : "All Event Types";
        String dateFilter = (dateCombo != null && dateCombo.getValue() != null) ? dateCombo.getValue() : "All Dates";
        LocalDate today = LocalDate.now();

        List<Event> filtered = masterEventList.stream().filter(ev -> {
            if (ev == null) return false;

            boolean matchesSearch = query.isEmpty() ||
                    (ev.getEventName() != null && ev.getEventName().toLowerCase().contains(query)) ||
                    (ev.getCity() != null && ev.getCity().toLowerCase().contains(query));

            boolean matchesStatus = "All Status".equals(status) ||
                    (ev.getStatus() != null && ev.getStatus().equalsIgnoreCase(status));

            String evType = ev.getVenueType() != null ? ev.getVenueType() : "";
            boolean matchesType = "All Event Types".equals(type) ||
                    evType.equalsIgnoreCase(type) ||
                    (ev.getEventName() != null && ev.getEventName().toLowerCase().contains(type.toLowerCase()));

            boolean matchesDate = true;
            if (!"All Dates".equals(dateFilter) && ev.getEventDate() != null && !ev.getEventDate().isBlank()) {
                try {
                    LocalDate evDate = LocalDate.parse(ev.getEventDate().trim());
                    if ("Upcoming Dates".equals(dateFilter)) {
                        matchesDate = !evDate.isBefore(today);
                    } else if ("Past Dates".equals(dateFilter)) {
                        matchesDate = evDate.isBefore(today);
                    }
                } catch (Exception ignored) {}
            }

            return matchesSearch && matchesStatus && matchesType && matchesDate;
        }).collect(Collectors.toList());

        renderEventList(filtered);
    }

    private void renderEventList(List<Event> events) {
        if (eventsListContainer == null) return;
        eventsListContainer.getChildren().clear();

        if (events == null || events.isEmpty()) {
            VBox emptyBox = new VBox(6);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40, 20, 40, 20));
            emptyBox.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-background-radius: 12;");

            Label noEvent = new Label("No events found matching your criteria.");
            noEvent.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

            Hyperlink createLnk = new Hyperlink("Click here to create a new event");
            createLnk.setOnAction(e -> openCreateEvent());

            emptyBox.getChildren().addAll(noEvent, createLnk);
            eventsListContainer.getChildren().add(emptyBox);
            return;
        }

        for (Event ev : events) {
            if (ev != null) {
                eventsListContainer.getChildren().add(createDynamicEventCard(ev));
            }
        }
    }

    private HBox createDynamicEventCard(Event ev) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        String iconLit = "fas-calendar-alt";
        String iconBg = "#E0F2FE";
        String iconColor = "#0284C7";

        String typeStr = (ev.getVenueType() != null && !ev.getVenueType().isBlank()) ? ev.getVenueType() : "Event";
        if (typeStr.toLowerCase().contains("academic") || (ev.getEventName() != null && ev.getEventName().toLowerCase().contains("seminar"))) {
            iconLit = "fas-graduation-cap"; iconBg = "#E0F2FE"; iconColor = "#0284C7";
        } else if (typeStr.toLowerCase().contains("family") || typeStr.toLowerCase().contains("wedding")) {
            iconLit = "fas-users"; iconBg = "#EDE9FE"; iconColor = "#7C3AED";
        } else if (typeStr.toLowerCase().contains("birthday")) {
            iconLit = "fas-birthday-cake"; iconBg = "#DCFCE7"; iconColor = "#16A34A";
        }

        StackPane iconHolder = new StackPane();
        iconHolder.setPrefSize(46, 46);
        Circle c = new Circle(20, Color.web(iconBg));
        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(18);
        fontIcon.setIconColor(Color.web(iconColor));
        iconHolder.getChildren().addAll(c, fontIcon);

        VBox detailsBox = new VBox(4);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        String name = (ev.getEventName() != null && !ev.getEventName().isBlank()) ? ev.getEventName() : "Unnamed Event";
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        String status = (ev.getStatus() != null && !ev.getStatus().isBlank()) ? ev.getStatus().toUpperCase() : "PLANNING";
        String statusBg = "#E0F2FE";
        String statusColor = "#0284C7";

        if ("COMPLETED".equals(status)) {
            statusBg = "#DCFCE7"; statusColor = "#16A34A";
        } else if ("UPCOMING".equals(status)) {
            statusBg = "#EEF2FF"; statusColor = "#4F46E5";
        }

        Label badgeLbl = new Label(status);
        badgeLbl.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusColor + "; -fx-padding: 3 8; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;");

        titleRow.getChildren().addAll(nameLbl, badgeLbl);

        Label catLbl = new Label(typeStr);
        catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        HBox metaRow = new HBox(16);
        metaRow.setPadding(new Insets(6, 0, 0, 0));

        String dateStr = (ev.getEventDate() != null && !ev.getEventDate().isBlank()) ? ev.getEventDate() : "Date TBD";
        String locStr = (ev.getCity() != null && !ev.getCity().isBlank()) ? ev.getCity() : "Location Pending";

        metaRow.getChildren().addAll(
                createMetaItem("📅 " + dateStr),
                createMetaItem("⏰ 10:00 AM"),
                createMetaItem("📍 " + locStr),
                createMetaItem("👥 100 Expected")
        );

        detailsBox.getChildren().addAll(titleRow, catLbl, metaRow);

        VBox rightBox = new VBox(2);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        rightBox.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 0 1;");

        Label budgetTitle = new Label("Budget");
        budgetTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        String budgetRaw = ev.getBudget();
        String budgetValStr = "₹50,000";
        if (budgetRaw != null && !budgetRaw.isBlank()) {
            budgetValStr = budgetRaw.startsWith("₹") ? budgetRaw : "₹" + budgetRaw;
        }

        Label budgetVal = new Label(budgetValStr);
        budgetVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Hyperlink actionLink = new Hyperlink("View Details →");
        actionLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 0;");
        actionLink.setOnAction(e -> openEventDetails(ev));

        rightBox.getChildren().addAll(budgetTitle, budgetVal, actionLink);

        card.getChildren().addAll(iconHolder, detailsBox, rightBox);
        return card;
    }

    private Label createMetaItem(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        return lbl;
    }

    private void returnToDashboard() {
        if (callBackAction != null) {
            callBackAction.run();
        } else {
            ActiveCustomerDashboardC dashboard = new ActiveCustomerDashboardC(uid, idToken);
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(dashboard.getactiveCustomerScene(null));
            }
        }
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene(() -> {
                if (LandingPage.landingPageStage != null) {
                    LandingPage.landingPageStage.setScene(getMyEventsScene(callBackAction));
                }
            }));
        }
    }

    private void openEventDetails(Event selectedEvent) {
        MyEventDetails detailsPage = new MyEventDetails(uid, idToken, selectedEvent);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(detailsPage.getMyEventDetailsScene(() -> {
                if (LandingPage.landingPageStage != null) {
                    LandingPage.landingPageStage.setScene(getMyEventsScene(callBackAction));
                }
            }));
        }
    }

    private void openNotifications() {
        Notification notificationPage = new Notification();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(notificationPage.getNotificationScene());
        }
    }

    private void openProfile() {
        ProfilePageC profilePage = new ProfilePageC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
        }
    }

    private void openSettings() {
        SettingC settingPage = new SettingC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
        }
    }
}