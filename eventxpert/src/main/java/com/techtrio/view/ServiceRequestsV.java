package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.techtrio.controller.VendorController;
import com.techtrio.model.VendorBooking;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServiceRequestsV {
    private Scene serviceRequestVScene;
    private VendorDashboardV vendorDashboard;

    private String uid;
    private String idToken;

    // Color Palette Constants (Exact Original)
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#D97706";
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";

    // Original Data Structure
    public static class RequestData {
        public String id;
        public String organizer;
        public String phone;
        public String eventName;
        public String eventType;
        public String date;
        public String time;
        public String location;
        public String guests;
        public String service;
        public String budget;
        public String priority;
        public String receivedTime;
        public String status; // New, Pending, Accepted, Conflict, Rejected
        public VendorBooking rawBooking;

        public RequestData(String id, String organizer, String phone, String eventName, String eventType,
                    String date, String time, String location, String guests, String service,
                    String budget, String priority, String receivedTime, String status, VendorBooking rawBooking) {
            this.id = id;
            this.organizer = organizer;
            this.phone = phone;
            this.eventName = eventName;
            this.eventType = eventType;
            this.date = date;
            this.time = time;
            this.location = location;
            this.guests = guests;
            this.service = service;
            this.budget = budget;
            this.priority = priority;
            this.receivedTime = receivedTime;
            this.status = status;
            this.rawBooking = rawBooking;
        }
    }

    private final List<RequestData> requestList = new ArrayList<>();
    private VBox cardsContainer;
    private String activeTabFilter = "All";

    // Summary Card Labels
    private Label totalReqValLbl;
    private Label newReqValLbl;
    private Label pendingValLbl;
    private Label acceptedValLbl;
    private Label urgentValLbl;
    private HBox tabsRowContainer;

    // Filter controls
    private TextField searchInput;
    private ComboBox<String> statusCB;
    private ComboBox<String> serviceCB;
    private ComboBox<String> eventTypeCB;
    private ComboBox<String> locationCB;
    private ComboBox<String> dateCB;
    private ComboBox<String> priorityCB;

    public ServiceRequestsV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ServiceRequestsV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getserviceRequestVScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        serviceRequestVScene = new Scene(root, 1550, 785);

        VBox sidebar = createSidebar(serviceRequestVScene);
        root.setLeft(sidebar);

        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        HBox topBar = createTopBar(serviceRequestVScene);
        mainArea.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().add(createPageHeader(serviceRequestVScene));
        mainContent.getChildren().add(createSummaryCardsRow());
        mainContent.getChildren().add(createFilterSection(serviceRequestVScene));
        
        tabsRowContainer = createTabsRow(serviceRequestVScene);
        mainContent.getChildren().add(tabsRowContainer);

        mainContent.getChildren().add(createMainSplitSection(serviceRequestVScene));
        mainContent.getChildren().add(createPaginationRow());

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        loadFirestoreRequestsAsync(serviceRequestVScene);
        return serviceRequestVScene;
    }

    private void loadFirestoreRequestsAsync(Scene scene) {
        if (uid == null || uid.isBlank()) return;

        Task<List<VendorBooking>> task = new Task<>() {
            @Override
            protected List<VendorBooking> call() {
                VendorController controller = new VendorController();
                return controller.getVendorBookings(uid);
            }
        };

        task.setOnSucceeded(evt -> {
            List<VendorBooking> bookings = task.getValue();
            Platform.runLater(() -> {
                requestList.clear();
                if (bookings != null) {
                    for (VendorBooking b : bookings) {
                        String rawStatus = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";
                        String uiStatus = "Pending";
                        if ("PENDING".equals(rawStatus)) uiStatus = "New";
                        else if ("CONFIRMED".equals(rawStatus)) uiStatus = "Accepted";
                        else if ("REJECTED".equals(rawStatus)) uiStatus = "Rejected";

                        requestList.add(new RequestData(
                                b.getBookingId() != null ? b.getBookingId() : "",
                                b.getOrganizerName() != null ? b.getOrganizerName() : "Organizer",
                                b.getCustomerPhone() != null ? b.getCustomerPhone() : "+91 98765 43210",
                                b.getEventName() != null ? b.getEventName() : "Event",
                                "Corporate Conference",
                                b.getEventDate() != null ? b.getEventDate() : "18 May 2026",
                                "09:00 AM - 06:00 PM",
                                b.getLocation() != null ? b.getLocation() : "Grand Convention Center, Pune",
                                String.valueOf(b.getGuests() > 0 ? b.getGuests() : 250),
                                b.getServiceCategory() != null ? b.getServiceCategory() : "Audio & Visual",
                                b.getAmount() != null ? b.getAmount() : "₹85,000",
                                "Important",
                                "Recent",
                                uiStatus,
                                b
                        ));
                    }
                }
                updateSummaryCounts();
                buildTabs(tabsRowContainer, scene);
                renderRequestCards(scene);
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void updateSummaryCounts() {
        int total = requestList.size();
        int newCount = 0;
        int pending = 0;
        int accepted = 0;

        for (RequestData r : requestList) {
            if ("New".equalsIgnoreCase(r.status)) newCount++;
            if ("Pending".equalsIgnoreCase(r.status)) pending++;
            if ("Accepted".equalsIgnoreCase(r.status)) accepted++;
        }

        if (totalReqValLbl != null) totalReqValLbl.setText(String.valueOf(total));
        if (newReqValLbl != null) newReqValLbl.setText(String.valueOf(newCount));
        if (pendingValLbl != null) pendingValLbl.setText(String.valueOf(pending));
        if (acceptedValLbl != null) acceptedValLbl.setText(String.valueOf(accepted));
        if (urgentValLbl != null) urgentValLbl.setText(String.valueOf(newCount > 0 ? 1 : 0));
    }

    // ==========================================
    // 1. SIDEBAR (EXACT ORIGINAL)
    // ==========================================
    private VBox createSidebar(Scene scene) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("E");
        logoIcon.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 4 10; -fx-background-radius: 6px;");

        VBox brandBox = new VBox(0);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label brandSub = new Label("VENDOR PORTAL");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(brandTitle, brandSub);
        logoBox.getChildren().addAll(logoIcon, brandBox);

        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> showToast(scene, "Add Service Clicked"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> {
            if (vendorDashboard != null) {
                vendorDashboard.backToVendorDashboard();
            } else {
                VendorDashboardV dashboard = new VendorDashboardV(uid, idToken);
                LandingPage.landingPageStage.setScene(dashboard.getvendorDashboardScene());
            }
        }));

        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", true, e -> {})); 
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> {
            MyBookingsV myBookingsV = new MyBookingsV(uid, idToken);
            myBookingsV.setVendorDashboard(vendorDashboard);
            LandingPage.landingPageStage.setScene(myBookingsV.getmyBookingScene());
        }));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(scene, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(scene, "Opening Calendar...")));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(scene, "Opening Earnings...")));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));
        sidebar.getChildren().add(createSidebarItem("👤 Settings", false, e -> showToast(scene, "Opening Settings...")));
        sidebar.getChildren().add(createSidebarItem("❓ Support", false, e -> showToast(scene, "Opening Support...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profilePill = new HBox(10);
        profilePill.setAlignment(Pos.CENTER_LEFT);
        profilePill.setPadding(new Insets(10));
        profilePill.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(WHITE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("VisionPro Events");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("Sound System • ● Available");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);
        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        if (isActive) {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        return btn;
    }

    private HBox createSidebarItemWithBadge(String title, String badgeCount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Button btn = createSidebarItem(title, false, e -> {});
        HBox.setHgrow(btn, Priority.ALWAYS);
        Label badge = new Label(badgeCount);
        badge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");
        row.getChildren().addAll(btn, badge);
        return row;
    }

    // ==========================================
    // 2. TOP BAR
    // ==========================================
    private HBox createTopBar(Scene scene) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search requests, events, organizers...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓"); helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button notifBtn = new Button("🔔"); notifBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button settingsBtn = new Button("⚙"); settingsBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("VP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifBtn, settingsBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Scene scene) {
        VBox header = new VBox(6);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Service Requests");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = createSecondaryButton("↻ Refresh");
        refreshBtn.setOnAction(e -> {
            loadFirestoreRequestsAsync(scene);
            showToast(scene, "Requests list refreshed from Firestore.");
        });

        Button calendarBtn = createPrimaryButton("📅 View Calendar");
        calendarBtn.setOnAction(e -> showToast(scene, "Opening Availability Calendar..."));

        titleRow.getChildren().addAll(title, spacer, refreshBtn, calendarBtn);

        Label subtitle = new Label("Review and manage service requests received from event organizers.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUMMARY CARDS ROW (5 EXACT CARDS)
    // ==========================================
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        totalReqValLbl = new Label("24");
        newReqValLbl = new Label("8");
        pendingValLbl = new Label("5");
        acceptedValLbl = new Label("9");
        urgentValLbl = new Label("2");

        row.getChildren().addAll(
                createSummaryCard("Total Requests", totalReqValLbl, "All Requests", PRIMARY_BLUE, "👥"),
                createSummaryCard("New Requests", newReqValLbl, "Needs Review (+3 this week)", GREEN_TEXT, "✉"),
                createSummaryCard("Pending", pendingValLbl, "Awaiting Response", WARNING_TEXT, "⏱"),
                createSummaryCard("Accepted", acceptedValLbl, "Confirmed Services", GREEN_TEXT, "✓"),
                createSummaryCard("Urgent", urgentValLbl, "Requires Attention", RED_TEXT, "⚠️")
        );
        return row;
    }

    private VBox createSummaryCard(String title, Label valLbl, String sub, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px;");
        top.getChildren().addAll(t, sp, ic);

        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, valLbl, s);
        return card;
    }

    // ==========================================
    // 5. FILTER SECTION (EXACT GRID)
    // ==========================================
    private VBox createFilterSection(Scene scene) {
        VBox card = createCardContainer();

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        searchInput = new TextField();
        searchInput.setPromptText("🔍 Search event, organizer, service...");

        statusCB = new ComboBox<>();
        statusCB.getItems().addAll("All Status", "New", "Pending", "Accepted", "Rejected");
        statusCB.setValue("All Status");

        serviceCB = new ComboBox<>();
        serviceCB.getItems().addAll("All Services", "Decoration", "Sound System", "Photography & Videography", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Dessert Services");
        serviceCB.setValue("All Services");

        eventTypeCB = new ComboBox<>();
        eventTypeCB.getItems().addAll("All Event Types", "Corporate Conference", "Corporate Event", "Networking Event", "Corporate Gala");
        eventTypeCB.setValue("All Event Types");

        locationCB = new ComboBox<>();
        locationCB.getItems().addAll("All Locations", "Pune", "Mumbai", "Bangalore");
        locationCB.setValue("All Locations");

        dateCB = new ComboBox<>();
        dateCB.getItems().addAll("All Dates", "Next 7 Days", "Next 30 Days", "May 2026");
        dateCB.setValue("All Dates");

        priorityCB = new ComboBox<>();
        priorityCB.getItems().addAll("All Priorities", "Normal", "Important", "Urgent");
        priorityCB.setValue("All Priorities");

        Button applyBtn = createPrimaryButton("Apply Filters");
        applyBtn.setOnAction(e -> {
            showToast(scene, "Filters applied.");
            renderRequestCards(scene);
        });

        Button clearBtn = createSecondaryButton("Clear");
        clearBtn.setOnAction(e -> {
            searchInput.clear();
            statusCB.setValue("All Status");
            serviceCB.setValue("All Services");
            eventTypeCB.setValue("All Event Types");
            renderRequestCards(scene);
        });

        grid.add(searchInput, 0, 0);
        grid.add(statusCB, 1, 0);
        grid.add(serviceCB, 2, 0);
        grid.add(eventTypeCB, 3, 0);
        grid.add(locationCB, 4, 0);

        grid.add(dateCB, 0, 1);
        grid.add(priorityCB, 1, 1);

        HBox btns = new HBox(8, applyBtn, clearBtn);
        grid.add(btns, 4, 1);

        card.getChildren().add(grid);
        return card;
    }

    // ==========================================
    // 6. REQUEST TABS ROW (6 EXACT TABS)
    // ==========================================
    private HBox createTabsRow(Scene scene) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        buildTabs(row, scene);
        return row;
    }

    private void buildTabs(HBox row, Scene scene) {
        if (row == null) return;
        row.getChildren().clear();

        int allCount = requestList.size();
        long newCount = requestList.stream().filter(r -> "New".equalsIgnoreCase(r.status)).count();
        long pendingCount = requestList.stream().filter(r -> "Pending".equalsIgnoreCase(r.status)).count();
        long acceptedCount = requestList.stream().filter(r -> "Accepted".equalsIgnoreCase(r.status)).count();
        long rejectedCount = requestList.stream().filter(r -> "Rejected".equalsIgnoreCase(r.status)).count();

        String[] tabs = {
                "All " + allCount,
                "New " + newCount,
                "Pending " + pendingCount,
                "Accepted " + acceptedCount,
                "Rejected " + rejectedCount,
                "Expired 0"
        };

        for (String tab : tabs) {
            Button tabBtn = new Button(tab);
            String tabName = tab.split(" ")[0];

            if (tabName.equalsIgnoreCase(activeTabFilter)) {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 0 0 2 0; -fx-padding: 6 12; -fx-cursor: hand;");
            } else {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-padding: 6 12; -fx-cursor: hand;");
            }

            tabBtn.setOnAction(e -> {
                activeTabFilter = tabName;
                buildTabs(row, scene);
                renderRequestCards(scene);
            });

            row.getChildren().add(tabBtn);
        }
    }

    // ==========================================
    // 7. MAIN SPLIT SECTION (CARDS & RIGHT PANELS)
    // ==========================================
    private HBox createMainSplitSection(Scene scene) {
        HBox row = new HBox(20);

        cardsContainer = new VBox(16);
        HBox.setHgrow(cardsContainer, Priority.ALWAYS);

        renderRequestCards(scene);

        VBox rightPanel = new VBox(16);
        rightPanel.setPrefWidth(380);

        rightPanel.getChildren().addAll(
                createQuickInsightsCard(scene),
                createStatusOverviewCard(),
                createImportantAlertsCard(scene)
        );

        row.getChildren().addAll(cardsContainer, rightPanel);
        return row;
    }

    private void renderRequestCards(Scene scene) {
        if (cardsContainer == null) return;
        cardsContainer.getChildren().clear();

        List<RequestData> filtered = requestList.stream().filter(r -> {
            if (!"All".equalsIgnoreCase(activeTabFilter) && !r.status.equalsIgnoreCase(activeTabFilter)) {
                return false;
            }
            if (searchInput != null && !searchInput.getText().isBlank()) {
                String q = searchInput.getText().toLowerCase().trim();
                boolean match = r.eventName.toLowerCase().contains(q) || r.organizer.toLowerCase().contains(q) || r.service.toLowerCase().contains(q);
                if (!match) return false;
            }
            if (statusCB != null && !"All Status".equals(statusCB.getValue()) && !r.status.equalsIgnoreCase(statusCB.getValue())) {
                return false;
            }
            if (serviceCB != null && !"All Services".equals(serviceCB.getValue()) && !r.service.toLowerCase().contains(serviceCB.getValue().toLowerCase())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = createCardContainer();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            emptyBox.getChildren().add(new Label("No service requests found for tab: " + activeTabFilter));
            cardsContainer.getChildren().add(emptyBox);
            return;
        }

        for (RequestData req : filtered) {
            cardsContainer.getChildren().add(createRequestCard(scene, req));
        }
    }

    private VBox createRequestCard(Scene scene, RequestData req) {
        VBox card = createCardContainer();

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox orgBox = new VBox(2);
        Label orgName = new Label("👤 " + req.organizer);
        orgName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label orgPhone = new Label(req.phone);
        orgPhone.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgBox.getChildren().addAll(orgName, orgPhone);
        orgBox.setPrefWidth(160);

        VBox eventBox = new VBox(2);
        Label eName = new Label(req.eventName);
        eName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label eDetails = new Label("📅 " + req.date + " • ⏰ " + req.time + " • 📍 " + req.location + " • 👥 " + req.guests + " Pax");
        eDetails.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        eventBox.getChildren().addAll(eName, eDetails);
        HBox.setHgrow(eventBox, Priority.ALWAYS);

        VBox serviceBox = new VBox(2);
        Label sName = new Label("Service: " + req.service);
        sName.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label budget = new Label("Budget: " + req.budget);
        budget.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        serviceBox.getChildren().addAll(sName, budget);
        serviceBox.setPrefWidth(140);

        VBox metaBox = new VBox(4);
        metaBox.setAlignment(Pos.TOP_RIGHT);
        Label prioLbl = new Label("● " + req.priority);
        prioLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + (req.priority.equals("Urgent") ? RED_TEXT : WARNING_TEXT) + ";");
        Label recLbl = new Label("⏱ " + req.receivedTime);
        recLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label statusBadge = new Label(req.status);
        if (req.status.equals("New")) {
            statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else if (req.status.equals("Accepted")) {
            statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else if (req.status.equals("Conflict") || req.status.equals("Rejected")) {
            statusBadge.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        }

        metaBox.getChildren().addAll(prioLbl, recLbl, statusBadge);
        topRow.getChildren().addAll(orgBox, new Separator(), eventBox, new Separator(), serviceBox, metaBox);

        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button detailsBtn = createSecondaryButton("View Details");
        detailsBtn.setOnAction(e -> {
            ServiceRequestDetailsV serviceRequestDetailsV = new ServiceRequestDetailsV(req.rawBooking);
            LandingPage.landingPageStage.setScene(serviceRequestDetailsV.getserviceRequestDetailsScene(LandingPage.landingPageStage, this::backToServiceRequestsV));
        });

        if (req.status.equals("New") || req.status.equals("Pending")) {
            Button acceptBtn = createPrimaryButton("Accept");
            acceptBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 14; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> openAcceptConfirmationModal(scene, req));

            Button rejectBtn = createSecondaryButton("Reject");
            rejectBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + BORDER_COLOR + ";");
            rejectBtn.setOnAction(e -> openRejectModal(scene, req));

            btnRow.getChildren().addAll(detailsBtn, acceptBtn, rejectBtn);
        } else if (req.status.equals("Conflict")) {
            Button conflictBtn = createSecondaryButton("View Conflict");
            conflictBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + ";");
            conflictBtn.setOnAction(e -> showToast(scene, "Checking calendar conflicts for " + req.date));
            btnRow.getChildren().addAll(conflictBtn, detailsBtn);
        } else {
            Button reviewBtn = createPrimaryButton("Continue Review");
            reviewBtn.setOnAction(e -> showToast(scene, "Continuing review for " + req.eventName));
            btnRow.getChildren().addAll(reviewBtn, detailsBtn);
        }

        card.getChildren().addAll(topRow, new Separator(), btnRow);
        return card;
    }

    // ==========================================
    // RIGHT PANEL CARDS (EXACT ORIGINAL)
    // ==========================================
    private VBox createQuickInsightsCard(Scene scene) {
        VBox card = createCardContainer();
        Label title = new Label("Quick Insights");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createInsightRow("📈 Your Sound System service received 35% more requests this month."),
                createInsightRow("📅 Weekend dates between 18–30 May have high demand."),
                createInsightRow("⏱ Your average response time is excellent (under 15 mins).")
        );

        Button analysisBtn = createSecondaryButton("View Analysis →");
        analysisBtn.setOnAction(e -> showToast(scene, "Opening Vendor Analytics..."));

        card.getChildren().addAll(title, new Separator(), list, analysisBtn);
        return card;
    }

    private HBox createInsightRow(String text) {
        HBox r = new HBox(6);
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().add(l);
        return r;
    }

    private VBox createStatusOverviewCard() {
        VBox card = createCardContainer();
        Label title = new Label("Request Status Overview");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox legend = new VBox(4);
        legend.getChildren().addAll(
                createLegendRow("● New", "8 (33%)", PRIMARY_BLUE),
                createLegendRow("● Pending", "5 (21%)", WARNING_TEXT),
                createLegendRow("● Accepted", "9 (37%)", GREEN_TEXT),
                createLegendRow("● Rejected", "2 (8%)", RED_TEXT)
        );

        card.getChildren().addAll(title, new Separator(), legend);
        return card;
    }

    private HBox createLegendRow(String label, String val, String color) {
        HBox r = new HBox();
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        r.getChildren().addAll(l, sp, v);
        return r;
    }

    private VBox createImportantAlertsCard(Scene scene) {
        VBox card = createCardContainer();
        Label title = new Label("Important Alerts");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox alerts = new VBox(8);
        alerts.getChildren().addAll(
                createAlertRow(scene, "⚠️ 3 requests require your response.", "Review now →"),
                createAlertRow(scene, "⚠️ 15 June has a booking conflict.", "Check calendar →"),
                createAlertRow(scene, "✓ ₹42,000 payment pending.", "View payments →")
        );

        card.getChildren().addAll(title, new Separator(), alerts);
        return card;
    }

    private VBox createAlertRow(Scene scene, String msg, String linkText) {
        VBox box = new VBox(2);
        Label m = new Label(msg); m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label l = new Label(linkText); l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        l.setOnMouseClicked(e -> showToast(scene, "Action: " + linkText));
        box.getChildren().addAll(m, l);
        return box;
    }

    // ==========================================
    // 8. PAGINATION
    // ==========================================
    private HBox createPaginationRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 0, 0, 0));

        Label count = new Label("Showing 1 to 10 of 24 requests");
        count.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button prev = createSecondaryButton("← Previous");
        Button page1 = createPrimaryButton("1");
        Button page2 = createSecondaryButton("2");
        Button page3 = createSecondaryButton("3");
        Button next = createSecondaryButton("Next →");

        HBox pBox = new HBox(4, prev, page1, page2, page3, next);
        row.getChildren().addAll(count, sp, pBox);
        return row;
    }

    // ==========================================
    // MODALS & DIALOGS (WITH FIRESTORE UPDATE)
    // ==========================================
    private void openAcceptConfirmationModal(Scene scene, RequestData req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        if (scene != null && scene.getWindow() != null) modal.initOwner(scene.getWindow());
        modal.setTitle("Accept Service Request");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Accept Service Request?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label msg = new Label("Are you sure you want to accept the " + req.service + " request for " + req.eventName + "?");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button confirmBtn = createPrimaryButton("Accept Request");
        confirmBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            VendorController controller = new VendorController();
            controller.acceptBookingRequest(req.id);
            req.status = "Accepted";
            renderRequestCards(scene);
            showToast(scene, "✓ Request accepted successfully in Firestore!");
            modal.close();
        });

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> modal.close());

        btns.getChildren().addAll(confirmBtn, cancelBtn);
        layout.getChildren().addAll(title, msg, new Separator(), btns);

        Scene scene11 = new Scene(layout, 380, 180);
        modal.setScene(scene11);
        modal.show();
    }

    private void openRejectModal(Scene scene, RequestData req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        if (scene != null && scene.getWindow() != null) modal.initOwner(scene.getWindow());
        modal.setTitle("Reject Service Request");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Reject Service Request");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        ComboBox<String> reasonCB = new ComboBox<>();
        reasonCB.getItems().addAll("Schedule Conflict", "Budget Too Low", "Service Not Available", "Location Not Supported", "Other");
        reasonCB.setValue("Schedule Conflict");

        TextArea msgInput = new TextArea();
        msgInput.setPromptText("Tell the organizer why you are rejecting this request...");
        msgInput.setPrefRowCount(3);

        Button submitBtn = createPrimaryButton("Reject Request");
        submitBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        submitBtn.setOnAction(e -> {
            VendorController controller = new VendorController();
            controller.rejectBookingRequest(req.id);
            req.status = "Rejected";
            renderRequestCards(scene);
            showToast(scene, "Request rejected.");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Reason for Rejection:"), reasonCB, new Label("Additional Note:"), msgInput, submitBtn);

        Scene scene1 = new Scene(layout, 360, 320);
        modal.setScene(scene1);
        modal.show();
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 7 15; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Scene scene, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (scene != null && scene.getWindow() != null) alert.initOwner(scene.getWindow());
        alert.show();
    }

    public void backToServiceRequestsV() {
        LandingPage.landingPageStage.setScene(getserviceRequestVScene());
    }
}