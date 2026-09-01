package com.techtrio.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.techtrio.controller.CustomerEventController;
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

public class MyEvents {

    private Scene myEventsScene;
    private String uid;
    private String idToken;
    private Runnable callBackAction;

    // डेटाबेसमधून आलेली मुख्य लिस्ट आणि UI कंटेनर
    private List<Event> masterEventList = new ArrayList<>();
    private VBox eventsListContainer;

    // Dynamic Stats Labels
    private Label allEventsCountLbl;
    private Label upcomingCountLbl;
    private Label completedCountLbl;
    private Label draftsCountLbl;

    // Filters Controls
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
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif;");

        // 1. Sidebar (Left)
        root.setLeft(createSidebar());

        // 2. Main Content Area (Navbar + Scrollable Body)
        BorderPane mainContainer = new BorderPane();
        mainContainer.setTop(createTopNavbar());
        mainContainer.setCenter(createMainContent());

        root.setCenter(mainContainer);

        // सर्व UI तयार झाल्यावर डेटाबेस कॉलिंग
        loadDynamicEventsFromDatabase();

        myEventsScene = new Scene(root, 1550, 750);
        return myEventsScene;
    }

    // ================= 1. SIDEBAR =================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.setStyle("-fx-background-color: #061B41;");

        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(24, 20, 20, 20));
        logoBox.setStyle("-fx-border-color: #0d2750; -fx-border-width: 0 0 1 0;");

        Label logoTitle = new Label("EventXpert");
        logoTitle.setStyle("-fx-text-fill: white; -fx-font-size: 23px; -fx-font-weight: bold; -fx-font-family: Arial;");
        Label logoSubtitle = new Label("CUSTOMER PANEL");
        logoSubtitle.setStyle("-fx-text-fill: #B8C9E2; -fx-font-size: 9px; -fx-font-family: Arial;");

        logoBox.getChildren().addAll(logoTitle, logoSubtitle);

        VBox navBox = new VBox(5);
        navBox.setPadding(new Insets(12, 12, 8, 12));

        Button dashboardNav = createNavItem("▦", "Dashboard", false);
        dashboardNav.setOnAction(e -> returnToDashboard());

        Button createEventNav = createNavItem("⊕", "Create Event", false);
        createEventNav.setOnAction(e -> openCreateEvent());

        Button myEventsNav = createNavItem("▣", "My Events", true); // Active

        Button notificationNav = createNavItem("♧", "Notifications", false);
        notificationNav.setOnAction(e -> {
            Notification notificationPage = new Notification();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(notificationPage.getNotificationScene());
            }
        });

        navBox.getChildren().addAll(dashboardNav, createEventNav, myEventsNav, notificationNav);

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(5);
        bottomMenu.setPadding(new Insets(8, 12, 18, 12));

        Button profileNav = createNavItem("◉", "Profile", false);
        profileNav.setOnAction(e -> {
            ProfilePageC profilePage = new ProfilePageC();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
            }
        });

        Button settingsNav = createNavItem("⚙", "Settings", false);
        settingsNav.setOnAction(e -> {
            SettingC settingPage = new SettingC();
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
            }
        });

        Button supportNav = createNavItem("?", "Support", false);

        bottomMenu.getChildren().addAll(profileNav, settingsNav, supportNav);
        sidebar.getChildren().addAll(logoBox, navBox, sidebarSpace, bottomMenu);
        return sidebar;
    }

    private Button createNavItem(String icon, String text, boolean isActive) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(40);
        btn.setPadding(new Insets(0, 11, 0, 13));

        if (isActive) {
            btn.setStyle("-fx-background-color: #BFE5FF; -fx-text-fill: #061B41; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #D1D9E8; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #0c2752; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #D1D9E8; -fx-font-size: 12px; -fx-cursor: hand;"));
        }
        return btn;
    }

    // ================= 2. TOP NAVBAR =================
    private HBox createTopNavbar() {
        HBox navbar = new HBox(12);
        navbar.setPrefHeight(64);
        navbar.setMinHeight(64);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(10, 20, 10, 20));
        navbar.setStyle("-fx-background-color: white; -fx-border-color: #E0E6EF; -fx-border-width: 0 0 1 0;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        searchField.setPrefWidth(330);
        searchField.setPrefHeight(36);
        searchField.setStyle("-fx-background-color: #F7F9FC; -fx-border-color: #D2DAE5; -fx-border-radius: 18; -fx-background-radius: 18; -fx-padding: 0 15; -fx-font-size: 12px;");

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            if (filterSearch != null) {
                filterSearch.setText(newV);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label notifIcon = new Label("♧");
        notifIcon.setStyle("-fx-font-size: 19px; -fx-cursor: hand; -fx-text-fill: #061B41;");

        StackPane avatar = new StackPane();
        Circle avatarCircle = new Circle(18, Color.web("#C9E8FA"));
        Label avatarLetter = new Label("C");
        avatarLetter.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #061B41; -fx-font-family: Arial;");
        avatar.getChildren().addAll(avatarCircle, avatarLetter);

        navbar.getChildren().addAll(searchField, spacer, notifIcon, avatar);
        return navbar;
    }

    // ================= 3. MAIN CONTENT =================
    private ScrollPane createMainContent() {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(24, 32, 28, 32));
        contentBox.setStyle("-fx-background-color: #F5F8FC;");

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("My Events");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-text-fill: #061B41; -fx-font-family: Arial;");
        Label subtitle = new Label("Manage and track all your events in one place.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #596273;");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button createEventBtn = new Button("+  Create New Event");
        createEventBtn.setPrefSize(175, 36);
        createEventBtn.setStyle("-fx-background-color: #061B41; -fx-text-fill: white; -fx-background-radius: 7; -fx-font-family: Arial; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
        createEventBtn.setOnAction(e -> openCreateEvent());

        headerBox.getChildren().addAll(titleBox, headerSpacer, createEventBtn);

        allEventsCountLbl = new Label("0");
        upcomingCountLbl = new Label("0");
        completedCountLbl = new Label("0");
        draftsCountLbl = new Label("0");

        HBox statsGrid = new HBox(16);
        statsGrid.getChildren().addAll(
                createStatCard("📅", "All Events", allEventsCountLbl, "#EEF2FF", "#4F46E5"),
                createStatCard("🗓️", "Upcoming", upcomingCountLbl, "#EEF2FF", "#4F46E5"),
                createStatCard("✓", "Completed", completedCountLbl, "#EEF2FF", "#4F46E5"),
                createStatCard("📝", "Drafts / Pending", draftsCountLbl, "#EEF2FF", "#4F46E5")
        );

        HBox filterBox = new HBox(12);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10, 14, 10, 14));
        filterBox.setStyle("-fx-background-color: white; -fx-border-color: #D7DEE8; -fx-border-radius: 10; -fx-background-radius: 10;");

        filterSearch = new TextField();
        filterSearch.setPromptText("🔍 Search by event name...");
        filterSearch.setPrefWidth(280);
        filterSearch.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6;");

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("All Status", "Planning", "Upcoming", "Completed", "Pending");
        statusCombo.setValue("All Status");
        statusCombo.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6;");

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All Event Types", "Academic Event", "Family Event", "Birthday Event", "Corporate", "Wedding");
        typeCombo.setValue("All Event Types");
        typeCombo.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6;");

        dateCombo = new ComboBox<>();
        dateCombo.getItems().addAll("All Dates", "Upcoming Dates", "Past Dates");
        dateCombo.setValue("All Dates");
        dateCombo.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6;");

        Hyperlink clearFilter = new Hyperlink("✕ Clear Filters");
        clearFilter.setStyle("-fx-text-fill: #0D3979; -fx-font-weight: bold; -fx-font-size: 12px;");

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
        backButton.setStyle("-fx-background-color: #061B41; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 20; -fx-background-radius: 7; -fx-cursor: hand;");
        backButton.setOnAction(e -> returnToDashboard());
        bottomBar.getChildren().add(backButton);

        contentBox.getChildren().addAll(headerBox, statsGrid, filterBox, eventsListContainer, bottomBar);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F5F8FC;");
        return scrollPane;
    }

    // ================= 4. DYNAMIC DATABASE FETCHING =================
    private void loadDynamicEventsFromDatabase() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                // 🎯 CustomerEventController चा वापर
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
            emptyBox.setStyle("-fx-background-color: white; -fx-border-color: #D7DEE8; -fx-border-radius: 12; -fx-background-radius: 12;");

            Label noEvent = new Label("No events found matching your criteria.");
            noEvent.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #17233C;");

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
        card.setStyle("-fx-background-color: white; -fx-border-color: #D7DEE8; -fx-border-radius: 12; -fx-background-radius: 12;");

        String icon = "📅";
        String iconBg = "#E0F2FE";
        String iconColor = "#0284C7";

        String typeStr = (ev.getVenueType() != null && !ev.getVenueType().isBlank()) ? ev.getVenueType() : "Event";
        if (typeStr.toLowerCase().contains("academic") || (ev.getEventName() != null && ev.getEventName().toLowerCase().contains("seminar"))) {
            icon = "🎓"; iconBg = "#E0F2FE"; iconColor = "#0284C7";
        } else if (typeStr.toLowerCase().contains("family")) {
            icon = "👨‍👩‍👧‍👦"; iconBg = "#EDE9FE"; iconColor = "#7C3AED";
        } else if (typeStr.toLowerCase().contains("birthday") || (ev.getEventName() != null && ev.getEventName().toLowerCase().contains("birthday"))) {
            icon = "🎂"; iconBg = "#DCFCE7"; iconColor = "#16A34A";
        }

        Label iconLbl = new Label(icon);
        iconLbl.setPrefSize(46, 46);
        iconLbl.setAlignment(Pos.CENTER);
        iconLbl.setStyle("-fx-background-color: " + iconBg + "; -fx-text-fill: " + iconColor + "; -fx-background-radius: 10; -fx-font-size: 18px;");

        VBox detailsBox = new VBox(4);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        String name = (ev.getEventName() != null && !ev.getEventName().isBlank()) ? ev.getEventName() : "Unnamed Event";
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #17233C;");

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
        catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #667085;");

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
        rightBox.setStyle("-fx-border-color: #D7DEE8; -fx-border-width: 0 0 0 1;");

        Label budgetTitle = new Label("Budget");
        budgetTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        String budgetRaw = ev.getBudget();
        String budgetValStr = "₹50,000";
        if (budgetRaw != null && !budgetRaw.isBlank()) {
            budgetValStr = budgetRaw.startsWith("₹") ? budgetRaw : "₹" + budgetRaw;
        }

        Label budgetVal = new Label(budgetValStr);
        budgetVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #17233C;");

        Hyperlink actionLink = new Hyperlink("View Details →");
        actionLink.setStyle("-fx-text-fill: #0D3979; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 0;");
        actionLink.setOnAction(e -> openEventDetails(ev));

        rightBox.getChildren().addAll(budgetTitle, budgetVal, actionLink);

        card.getChildren().addAll(iconLbl, detailsBox, rightBox);
        return card;
    }

    private Label createMetaItem(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #596273;");
        return lbl;
    }

    private HBox createStatCard(String icon, String title, Label countLbl, String bgIconColor, String iconColor) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setPrefWidth(220);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: white; -fx-border-color: #D7DEE8; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label iconLbl = new Label(icon);
        iconLbl.setPrefSize(40, 40);
        iconLbl.setAlignment(Pos.CENTER);
        iconLbl.setStyle("-fx-background-color: " + bgIconColor + "; -fx-text-fill: " + iconColor + "; -fx-background-radius: 8; -fx-font-size: 16px;");

        VBox textVBox = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #667085;");
        countLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #17233C;");

        textVBox.getChildren().addAll(titleLbl, countLbl);
        card.getChildren().addAll(iconLbl, textVBox);
        return card;
    }

    // ================= 5. NAVIGATION HELPERS =================
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
}