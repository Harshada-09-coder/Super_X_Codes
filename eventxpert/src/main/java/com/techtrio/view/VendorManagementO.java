package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.model.Event;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.EventService;
import com.techtrio.services.VendorProfileService;
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

public class VendorManagementO {

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

    // Status Colors
    private static final String GREEN_TEXT = "#137333";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String PURPLE_BG = "#F3E8FF";
    private static final String PURPLE_TEXT = "#6B21A8";

    // Dynamic UI Metric Labels
    private Label totalVendorsValLbl;
    private Label activeVendorsValLbl;
    private Label inactiveVendorsValLbl;
    private Label newVendorsValLbl;
    private Label eventBadge;

    // Dynamic Category Count Labels
    private Label decorCountLbl;
    private Label soundCountLbl;
    private Label photoCountLbl;
    private Label rentalCountLbl;
    private Label invitationCountLbl;
    private Label cateringCountLbl;
    private Label anchorCountLbl;
    private Label cakeCountLbl;

    public VendorManagementO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public VendorManagementO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getvendorManagementOScene(Runnable callBackAction) {
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

        // Header Section
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Vendor Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Find, compare and manage trusted vendors for your event.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        eventBadge = new Label("● All Vendors (Directory Mode)");
        eventBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16px; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, eventBadge);

        // Top Summary KPI Cards Row (4 Dynamic Cards)
        HBox summaryRow = createSummaryKpiRow();

        // Search Filter Bar Card
        VBox filterCard = createFilterBarCard();

        // Main Section Title
        Label servicesTitle = new Label("Vendor Services");
        servicesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        // 8 Dynamic Vendor Services Cards Grid (2x4)
        GridPane servicesGrid = createVendorServicesGrid(callBackAction);

        content.getChildren().addAll(headerRow, summaryRow, filterCard, servicesTitle, servicesGrid);
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        loadDynamicVendorDataFromFirestoreAsync();
        loadDynamicEventContextBadgeAsync();

        return new Scene(root, 1550, 785);
    }

    private void loadDynamicEventContextBadgeAsync() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId != null && !selectedEvtId.isBlank() && !"EVT_DEFAULT".equalsIgnoreCase(selectedEvtId)) {
            Task<Event> task = new Task<>() {
                @Override
                protected Event call() {
                    EventService eventService = new EventService();
                    return eventService.getEventById(selectedEvtId);
                }
            };
            task.setOnSucceeded(e -> {
                Event ev = task.getValue();
                if (ev != null) {
                    Platform.runLater(() -> {
                        if (eventBadge != null) {
                            eventBadge.setText("● " + ev.getEventName() + " (In Planning)");
                        }
                    });
                }
            });
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
    }

    // ==========================================
    // DYNAMIC FIRESTORE DATA LOADER
    // ==========================================
    private void loadDynamicVendorDataFromFirestoreAsync() {
        Task<List<VendorProfile>> task = new Task<>() {
            @Override
            protected List<VendorProfile> call() {
                VendorProfileService service = new VendorProfileService();
                return service.getAllVendors();
            }
        };

        task.setOnSucceeded(evt -> {
            List<VendorProfile> vendors = task.getValue();
            Platform.runLater(() -> {
                if (vendors == null || vendors.isEmpty()) {
                    setAllCountsToZero();
                    return;
                }

                int totalCount = vendors.size();
                int activeCount = 0;
                int inactiveCount = 0;

                int decorCount = 0;
                int soundCount = 0;
                int photoCount = 0;
                int rentalCount = 0;
                int invitationCount = 0;
                int cateringCount = 0;
                int anchorCount = 0;
                int cakeCount = 0;

                for (VendorProfile v : vendors) {
                    String status = v.getOnboardingStatus() != null ? v.getOnboardingStatus().toUpperCase() : "NEW";
                    if ("ACTIVE".equals(status) || "EXISTING".equals(status) || v.isProfileCompleted() || v.isHasProvidedService()) {
                        activeCount++;
                    } else {
                        inactiveCount++;
                    }

                    String cat = v.getCategory() != null ? v.getCategory().toLowerCase().trim() : "";

                    if (cat.contains("decor")) decorCount++;
                    if (cat.contains("sound") || cat.contains("audio") || cat.contains("av")) soundCount++;
                    if (cat.contains("photo") || cat.contains("video")) photoCount++;
                    if (cat.contains("rental") || cat.contains("equipment")) rentalCount++;
                    if (cat.contains("invitation") || cat.contains("print")) invitationCount++;
                    if (cat.contains("cater")) cateringCount++;
                    if (cat.contains("anchor") || cat.contains("mc")) anchorCount++;
                    if (cat.contains("cake") || cat.contains("dessert")) cakeCount++;
                }

                if (totalVendorsValLbl != null) totalVendorsValLbl.setText(String.valueOf(totalCount));
                if (activeVendorsValLbl != null) activeVendorsValLbl.setText(String.valueOf(activeCount));
                if (inactiveVendorsValLbl != null) inactiveVendorsValLbl.setText(String.valueOf(inactiveCount));
                if (newVendorsValLbl != null) newVendorsValLbl.setText(String.valueOf(inactiveCount));

                if (decorCountLbl != null) decorCountLbl.setText("Count: " + decorCount + " Vendors");
                if (soundCountLbl != null) soundCountLbl.setText("Count: " + soundCount + " Vendors");
                if (photoCountLbl != null) photoCountLbl.setText("Count: " + photoCount + " Vendors");
                if (rentalCountLbl != null) rentalCountLbl.setText("Count: " + rentalCount + " Vendors");
                if (invitationCountLbl != null) invitationCountLbl.setText("Count: " + invitationCount + " Vendors");
                if (cateringCountLbl != null) cateringCountLbl.setText("Count: " + cateringCount + " Vendors");
                if (anchorCountLbl != null) anchorCountLbl.setText("Count: " + anchorCount + " Vendors");
                if (cakeCountLbl != null) cakeCountLbl.setText("Count: " + cakeCount + " Vendors");
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void setAllCountsToZero() {
        if (totalVendorsValLbl != null) totalVendorsValLbl.setText("0");
        if (activeVendorsValLbl != null) activeVendorsValLbl.setText("0");
        if (inactiveVendorsValLbl != null) inactiveVendorsValLbl.setText("0");
        if (newVendorsValLbl != null) newVendorsValLbl.setText("0");

        if (decorCountLbl != null) decorCountLbl.setText("Count: 0 Vendors");
        if (soundCountLbl != null) soundCountLbl.setText("Count: 0 Vendors");
        if (photoCountLbl != null) photoCountLbl.setText("Count: 0 Vendors");
        if (rentalCountLbl != null) rentalCountLbl.setText("Count: 0 Vendors");
        if (invitationCountLbl != null) invitationCountLbl.setText("Count: 0 Vendors");
        if (cateringCountLbl != null) cateringCountLbl.setText("Count: 0 Vendors");
        if (anchorCountLbl != null) anchorCountLbl.setText("Count: 0 Vendors");
        if (cakeCountLbl != null) cakeCountLbl.setText("Count: 0 Vendors");
    }

    private HBox createSummaryKpiRow() {
        HBox row = new HBox(16);
        totalVendorsValLbl = new Label("0");
        activeVendorsValLbl = new Label("0");
        inactiveVendorsValLbl = new Label("0");
        newVendorsValLbl = new Label("0");

        row.getChildren().addAll(
                createKpiCard("TOTAL VENDORS", totalVendorsValLbl, "All registered vendors", "👥", LIGHT_BLUE, PRIMARY_BLUE),
                createKpiCard("ACTIVE VENDORS", activeVendorsValLbl, "Currently active vendors", "✓", GREEN_BG, GREEN_TEXT),
                createKpiCard("INACTIVE VENDORS", inactiveVendorsValLbl, "Currently inactive vendors", "👤", RED_BG, RED_TEXT),
                createKpiCard("NEW THIS MONTH", newVendorsValLbl, "New vendors added", "⭐", PURPLE_BG, PURPLE_TEXT)
        );
        return row;
    }

    private VBox createKpiCard(String label, Label valLbl, String subtext, String iconStr, String bgHex, String colorHex) {
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

        StackPane iconCircle = new StackPane();
        Circle c = new Circle(16, Color.web(bgHex));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 12px; -fx-text-fill: " + colorHex + ";");
        iconCircle.getChildren().addAll(c, ic);

        topRow.getChildren().addAll(lbl, spacer, iconCircle);

        valLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subLbl = new Label(subtext);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, valLbl, subLbl);
        return card;
    }

    private VBox createFilterBarCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(14, 16, 14, 16));
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
        searchInput.setPromptText("Search vendors or services...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchInput);

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> searchInput.clear());

        filterRow.getChildren().addAll(searchBox, clearBtn);
        card.getChildren().add(filterRow);
        return card;
    }

    // ==========================================
    // VENDOR SERVICES GRID (2x4 = 8 CARDS)
    // ==========================================
    private GridPane createVendorServicesGrid(Runnable callBackAction) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        decorCountLbl = new Label("Count: 0 Vendors");
        soundCountLbl = new Label("Count: 0 Vendors");
        photoCountLbl = new Label("Count: 0 Vendors");
        rentalCountLbl = new Label("Count: 0 Vendors");
        invitationCountLbl = new Label("Count: 0 Vendors");
        cateringCountLbl = new Label("Count: 0 Vendors");
        anchorCountLbl = new Label("Count: 0 Vendors");
        cakeCountLbl = new Label("Count: 0 Vendors");

        grid.add(createServiceCard("🎨", "Decoration", "Decoration", decorCountLbl, callBackAction), 0, 0);
        grid.add(createServiceCard("🔊", "Sound System", "Sound System", soundCountLbl, callBackAction), 1, 0);
        grid.add(createServiceCard("📷", "Photography & Videography", "Photography & Videography", photoCountLbl, callBackAction), 2, 0);
        grid.add(createServiceCard("🎪", "Rental Equipment", "Rental Equipment", rentalCountLbl, callBackAction), 3, 0);

        grid.add(createServiceCard("✉️", "Invitation & Printing", "Invitation & Printing", invitationCountLbl, callBackAction), 0, 1);
        grid.add(createServiceCard("🍽", "Catering", "Catering", cateringCountLbl, callBackAction), 1, 1);
        grid.add(createServiceCard("🎤", "Anchor", "Anchor", anchorCountLbl, callBackAction), 2, 1);
        grid.add(createServiceCard("🍰", "Cake and Dessert Services", "Cake and Dessert Services", cakeCountLbl, callBackAction), 3, 1);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }

        return grid;
    }

    private VBox createServiceCard(String iconStr, String categoryName, String categoryFilterKey, Label countLbl, Runnable callBackAction) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Circle c = new Circle(20, Color.web(LIGHT_BLUE));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 16px;");
        iconBox.getChildren().addAll(c, ic);

        VBox titleBox = new VBox(2);
        Label nameLbl = new Label(categoryName);
        nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label statusLbl = new Label("Status: Active");
        statusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold;");

        countLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(nameLbl, statusLbl, countLbl);
        topRow.getChildren().addAll(iconBox, titleBox);

        Button findBtn = new Button("Find Vendors");
        findBtn.setMaxWidth(Double.MAX_VALUE);
        findBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");

        findBtn.setOnAction(e -> {
            FindVendorsO findView = new FindVendorsO(uid, idToken, categoryFilterKey);
            LandingPage.landingPageStage.setScene(findView.getScene(LandingPage.landingPageStage, () -> {
                LandingPage.landingPageStage.setScene(getvendorManagementOScene(callBackAction));
            }));
        });

        card.getChildren().addAll(topRow, findBtn);
        return card;
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
        menu.getChildren().add(createNavBtn("Event Requests", false, this::openEventRequests));
        menu.getChildren().add(createNavBtn("My Events", false, this::openMyEvents));
        menu.getChildren().add(createNavBtn("Venue", false, this::openVenue));
        
        // When clicking directly on sidebar Vendors, clear any selected event context
        menu.getChildren().add(createNavBtn("Vendors", true, this::openDirectVendors));
        
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

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(380);
        searchBox.setPadding(new Insets(8, 12, 8, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search events, vendors, attendees...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightNav = new HBox(15);
        rightNav.setAlignment(Pos.CENTER);

        Button bellBtn = new Button("🔔");
        bellBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        bellBtn.setOnAction(e -> openNotifications());

        Button helpBtn = new Button("✉️");
        helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle userAvatar = new Circle(16, Color.web(BRIGHT_BLUE));
        userAvatar.setStyle("-fx-cursor: hand;");
        userAvatar.setOnMouseClicked(e -> openProfile());

        rightNav.getChildren().addAll(bellBtn, helpBtn, userAvatar);

        bar.getChildren().addAll(searchBox, spacer, rightNav);
        return bar;
    }

    private void openDashboard(Runnable backAction) {
        if (backAction != null) backAction.run();
        else {
            OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
            LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
        }
    }
    private void openEventRequests() {
        EventRequestsO view = new EventRequestsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.geteventRequestsOScene(this::openDashboardNull));
    }
    private void openMyEvents() {
        MyEventsO view = new MyEventsO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getMyEventsOScene(this::openDashboardNull));
    }
    private void openDirectVendors() {
        AdminContext.setSelectedEventId(null); // Clear context when coming from direct sidebar
        VendorManagementO view = new VendorManagementO(uid, idToken);
        LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(this::openDashboardNull));
    }
    private void openVenue() {
        VenueManagementO view = new VenueManagementO();
        LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::openDashboardNull));
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