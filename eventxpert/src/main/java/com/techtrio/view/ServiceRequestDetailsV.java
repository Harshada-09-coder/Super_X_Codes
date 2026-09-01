package com.techtrio.view;

import com.techtrio.controller.OrganizerController;
import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorBooking;
import com.techtrio.services.EventService;
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

public class ServiceRequestDetailsV {

    private Scene serviceRequestDetailsScene;
    private Runnable callBackAction;

    private String uid;
    private String idToken;
    private VendorBooking booking;

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

    // Dynamic UI State Elements
    private Label statusBadgeHeader;
    private Label priorityBadgeHeader;
    private Label metaInfoLabel;
    private Label headerEventName;
    private Label headerOrgName;
    private Label headerEventTypeTag;

    // Event Info Card Labels
    private Label specEventName;
    private Label specEventType;
    private Label specEventDate;
    private Label specEventTime;
    private Label specVenue;
    private Label specLocation;
    private Label specExpectedGuests;
    private Label specSetupTime;

    // Service Card Labels
    private Label srvCategoryLabel;
    private Label srvBudgetLabel;
    private FlowPane srvTagsPane;

    // Requirements List
    private VBox reqListContainer;

    // Payment Card Labels
    private Label payRequestedBudget;
    private Label payAdvanceAmount;
    private Label payPostEventAmount;

    // Right Summary Labels
    private Label summaryStatusLabel;
    private Label summaryPrioLabel;
    private VBox summaryGridContainer;
    private Button rightAcceptBtn;
    private Button rightRejectBtn;
    private Button topAcceptBtn;
    private HBox bottomActionBar;

    // Organizer Card Labels
    private Label orgCardName;
    private Label orgCardRole;
    private Label orgCardPhone;
    private Label orgCardEmail;

    public ServiceRequestDetailsV() {
        this(null);
    }

    public ServiceRequestDetailsV(VendorBooking booking) {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
        this.booking = booking;
    }

    public Scene getserviceRequestDetailsScene(Stage primaryStage, Runnable callBackAction) {
        this.callBackAction = callBackAction;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        HBox topBar = createTopBar(primaryStage);
        mainArea.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createHeaderAndBreadcrumb(primaryStage),
                createStatusOverviewHeaderCard(),
                createConflictAlertBanner(primaryStage),
                createTwoColumnMainLayout(primaryStage),
                createBottomActionBar(primaryStage)
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);
        root.setCenter(mainArea);

        loadDynamicDetailsAsync(primaryStage);

        serviceRequestDetailsScene = new Scene(root, 1550, 785);
        return serviceRequestDetailsScene;
    }

    // ==========================================
    // ASYNC FIRESTORE DATA LOADER
    // ==========================================
    private void loadDynamicDetailsAsync(Stage stage) {
        if (booking == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                EventService eventService = new EventService();
                Event event = (booking.getEventId() != null) ? eventService.getEventById(booking.getEventId()) : null;

                OrganizerController orgController = new OrganizerController();
                OrganizerProfile orgProfile = (booking.getOrganizerId() != null) ? orgController.getOrganizerProfile(booking.getOrganizerId()) : null;

                Platform.runLater(() -> {
                    String status = booking.getStatus() != null ? booking.getStatus().toUpperCase() : "PENDING";
                    boolean isPending = "PENDING".equals(status) || "NEW".equals(status);
                    boolean isAccepted = "CONFIRMED".equals(status) || "ACCEPTED".equals(status);

                    // Header Card
                    if (statusBadgeHeader != null) {
                        statusBadgeHeader.setText(isAccepted ? "ACCEPTED" : (isPending ? "NEW REQUEST" : status));
                        statusBadgeHeader.setStyle(isAccepted ? "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;"
                                : "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;");
                    }

                    if (metaInfoLabel != null) {
                        metaInfoLabel.setText("Received: Recent  •  ID: " + (booking.getBookingId() != null ? booking.getBookingId() : "SR-2026"));
                    }

                    if (headerEventName != null) headerEventName.setText(booking.getEventName() != null ? booking.getEventName() : "Event Planning");
                    
                    String orgDisplay = orgProfile != null && orgProfile.getName() != null ? orgProfile.getName() : (booking.getOrganizerName() != null ? booking.getOrganizerName() : "Organizer");
                    String phoneDisplay = orgProfile != null && orgProfile.getMobileNo() != null ? orgProfile.getMobileNo() : (booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "N/A");
                    if (headerOrgName != null) headerOrgName.setText("Organizer: " + orgDisplay + "  •  📞 " + phoneDisplay);

                    if (headerEventTypeTag != null && event != null) {
                        headerEventTypeTag.setText(event.getEventType() != null ? event.getEventType() : "Corporate Event");
                    }

                    // Event Information Card
                    if (specEventName != null) specEventName.setText(booking.getEventName());
                    if (specEventType != null && event != null) specEventType.setText(event.getEventType() != null ? event.getEventType() : "Corporate Conference");
                    if (specEventDate != null) specEventDate.setText("📅 " + (booking.getEventDate() != null ? booking.getEventDate() : "TBD"));
                    if (specEventTime != null) specEventTime.setText("⏰ " + (event != null && event.getTime() != null ? event.getTime() : "09:00 AM – 06:00 PM"));
                    if (specVenue != null) specVenue.setText("🏢 " + (event != null && event.getLocality() != null ? event.getLocality() : "Grand Convention Center"));
                    if (specLocation != null) specLocation.setText("📍 " + (booking.getLocation() != null ? booking.getLocation() : "Pune"));
                    if (specExpectedGuests != null) specExpectedGuests.setText("👥 " + booking.getGuests() + " Guests");
                    if (specSetupTime != null) specSetupTime.setText("⏰ 07:30 AM (Pre-event)");

                    // Service Card
                    if (srvCategoryLabel != null) srvCategoryLabel.setText(booking.getServiceCategory() != null ? booking.getServiceCategory() : "General Service");
                    if (srvBudgetLabel != null) srvBudgetLabel.setText(booking.getAmount() != null ? booking.getAmount() : "₹0");

                    // Detailed Requirements
                    if (reqListContainer != null && event != null) {
                        reqListContainer.getChildren().clear();
                        if (event.getRequirements() != null && !event.getRequirements().isBlank()) {
                            reqListContainer.getChildren().add(createReqRow("Event Requirements:", event.getRequirements()));
                        }
                        if (event.getNotes() != null && !event.getNotes().isBlank()) {
                            reqListContainer.getChildren().add(createReqRow("Special Notes:", event.getNotes()));
                        }
                        if (reqListContainer.getChildren().isEmpty()) {
                            reqListContainer.getChildren().add(createReqRow("Service Scope:", "Complete standard setup for " + booking.getServiceCategory() + " for " + booking.getGuests() + " guests."));
                        }
                    }

                    // Payment Card
                    String amt = booking.getAmount() != null ? booking.getAmount() : "₹85,000";
                    if (payRequestedBudget != null) payRequestedBudget.setText(amt);
                    if (payAdvanceAmount != null) payAdvanceAmount.setText("Advance: Released Upon Acceptance");
                    if (payPostEventAmount != null) payPostEventAmount.setText("Balance: Upon Completion");

                    // Right Summary Card
                    if (summaryStatusLabel != null) {
                        summaryStatusLabel.setText("Status: " + status);
                        summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + (isAccepted ? GREEN_TEXT : PRIMARY_BLUE) + ";");
                    }

                    if (summaryGridContainer != null) {
                        summaryGridContainer.getChildren().clear();
                        summaryGridContainer.getChildren().addAll(
                                createBulletLabel("• Event: " + booking.getEventName()),
                                createBulletLabel("• Date: " + booking.getEventDate()),
                                createBulletLabel("• Service: " + booking.getServiceCategory()),
                                createBulletLabel("• Guests: " + booking.getGuests() + " Guests"),
                                createBulletLabel("• Budget: " + booking.getAmount())
                        );
                    }

                    // Organizer Information Card
                    if (orgCardName != null) orgCardName.setText(orgDisplay);
                    if (orgCardRole != null) orgCardRole.setText("👤 Role: Event Organizer");
                    if (orgCardPhone != null) orgCardPhone.setText("📞 Phone: " + phoneDisplay);
                    if (orgCardEmail != null) orgCardEmail.setText("✉ Email: " + (orgProfile != null && orgProfile.getEmail() != null ? orgProfile.getEmail() : (booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "N/A")));

                    // =========================================================================
                    // HIDE BUTTONS IF ALREADY ACCEPTED / CONFIRMED
                    // =========================================================================
                    if (!isPending) {
                        if (topAcceptBtn != null) {
                            topAcceptBtn.setVisible(false);
                            topAcceptBtn.setManaged(false);
                        }
                        if (rightAcceptBtn != null) {
                            rightAcceptBtn.setVisible(false);
                            rightAcceptBtn.setManaged(false);
                        }
                        if (rightRejectBtn != null) {
                            rightRejectBtn.setVisible(false);
                            rightRejectBtn.setManaged(false);
                        }
                        if (bottomActionBar != null) {
                            bottomActionBar.getChildren().removeIf(node -> node instanceof Button && (((Button) node).getText().contains("Accept") || ((Button) node).getText().contains("Reject")));
                        }
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private Label createBulletLabel(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-padding: 1 0;");
        return l;
    }

    // ==========================================
    // 1. SIDEBAR
    // ==========================================
    private VBox createSidebar(Stage stage) {
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

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> {
            VendorDashboardV dash = new VendorDashboardV(uid, idToken);
            LandingPage.landingPageStage.setScene(dash.getvendorDashboardScene());
        }));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", true, e -> {
            if (callBackAction != null) callBackAction.run();
        }));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> {
            MyBookingsV bookings = new MyBookingsV(uid, idToken);
            LandingPage.landingPageStage.setScene(bookings.getmyBookingScene());
        }));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(stage, "Opening Calendar...")));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Finance...")));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));
        sidebar.getChildren().add(createSidebarItem("👤 Settings", false, e -> showToast(stage, "Opening Settings...")));
        sidebar.getChildren().add(createSidebarItem("❓ Support", false, e -> showToast(stage, "Opening Support...")));

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
        Label userSub = new Label("Audio & Visual • ● Available");
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
    private HBox createTopBar(Stage stage) {
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
    // 3. BREADCRUMB & HEADER
    // ==========================================
    private VBox createHeaderAndBreadcrumb(Stage stage) {
        VBox header = new VBox(6);

        Label breadcrumb = new Label("Service Requests  /  Service Request Details");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Service Request Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Requests");
        backBtn.setOnAction(e -> {
            if (callBackAction != null) callBackAction.run();
        });

        topAcceptBtn = createPrimaryButton("Accept Request");
        topAcceptBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        titleRow.getChildren().addAll(title, spacer, backBtn, topAcceptBtn);

        Label subtitle = new Label("Review the event requirements and decide how you want to respond.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. REQUEST STATUS HEADER CARD
    // ==========================================
    private VBox createStatusOverviewHeaderCard() {
        VBox card = createCardContainer();

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        statusBadgeHeader = new Label("NEW REQUEST");
        statusBadgeHeader.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        priorityBadgeHeader = new Label("Priority: Important");
        priorityBadgeHeader.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        metaInfoLabel = new Label("Received: Recent  •  ID: SR-2026-00248");
        metaInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        topRow.getChildren().addAll(statusBadgeHeader, priorityBadgeHeader, spacer, metaInfoLabel);

        HBox mainInfoRow = new HBox(20);
        mainInfoRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        headerEventName = new Label("Loading Event Name...");
        headerEventName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        headerOrgName = new Label("Organizer: Loading...");
        headerOrgName.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(headerEventName, headerOrgName);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        headerEventTypeTag = new Label("Corporate Event");
        headerEventTypeTag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        mainInfoRow.getChildren().addAll(titleBox, spacer2, headerEventTypeTag);

        card.getChildren().addAll(topRow, new Separator(), mainInfoRow);
        return card;
    }

    private HBox createConflictAlertBanner(Stage stage) {
        HBox alertBox = new HBox(12);
        alertBox.setAlignment(Pos.CENTER_LEFT);
        alertBox.setPadding(new Insets(12, 16, 12, 16));
        alertBox.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-border-color: #FCD34D; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label icon = new Label("⚠️"); icon.setStyle("-fx-font-size: 16px;");
        VBox text = new VBox(2);
        Label title = new Label("Potential Scheduling Notice"); title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + WARNING_TEXT + ";");
        Label desc = new Label("You have another booking close to this event date. Please review your availability calendar."); desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + WARNING_TEXT + ";");
        text.getChildren().addAll(title, desc);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button calBtn = createSecondaryButton("View Calendar");
        calBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + WARNING_TEXT + "; -fx-border-color: " + WARNING_TEXT + "; -fx-border-radius: 6px; -fx-padding: 4 10; -fx-font-size: 11px;");
        calBtn.setOnAction(e -> showToast(stage, "Opening Availability Calendar..."));

        alertBox.getChildren().addAll(icon, text, sp, calBtn);
        return alertBox;
    }

    // =========================================================================
    // TWO-COLUMN MAIN LAYOUT (WITH FIXED 360px RIGHT COLUMN TO PREVENT SQUISHING)
    // =========================================================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(22);

        VBox leftCol = new VBox(20);
        leftCol.setMinWidth(0);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEventInformationCard(),
                createRequestedServiceCard(),
                createEventRequirementsCard(),
                createBudgetAndPaymentCard(),
                createTimelineCard()
        );

        VBox rightCol = new VBox(20);
        rightCol.setMinWidth(360);
        rightCol.setPrefWidth(360);
        rightCol.setMaxWidth(380);

        rightCol.getChildren().addAll(
                createRightSummaryCard(stage),
                createOrganizerCard(stage)
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createEventInformationCard() {
        VBox card = createCardContainer();
        Label title = new Label("Event Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);

        specEventName = new Label("Loading...");
        specEventType = new Label("Loading...");
        specEventDate = new Label("📅 Loading...");
        specEventTime = new Label("⏰ Loading...");
        specVenue = new Label("🏢 Loading...");
        specLocation = new Label("📍 Loading...");
        specExpectedGuests = new Label("👥 Loading...");
        specSetupTime = new Label("⏰ 07:30 AM (Pre-event)");

        grid.add(createSpecItemBox("Event Name", specEventName), 0, 0);
        grid.add(createSpecItemBox("Event Type", specEventType), 1, 0);
        grid.add(createSpecItemBox("Event Date", specEventDate), 0, 1);
        grid.add(createSpecItemBox("Event Time", specEventTime), 1, 1);
        grid.add(createSpecItemBox("Venue", specVenue), 0, 2);
        grid.add(createSpecItemBox("Location", specLocation), 1, 2);
        grid.add(createSpecItemBox("Expected Guests", specExpectedGuests), 0, 3);
        grid.add(createSpecItemBox("Setup Time", specSetupTime), 1, 3);

        card.getChildren().addAll(title, new Separator(), grid);
        return card;
    }

    private VBox createRequestedServiceCard() {
        VBox card = createCardContainer();
        Label title = new Label("Requested Service");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox top = new HBox(16);
        top.setAlignment(Pos.CENTER_LEFT);

        srvCategoryLabel = new Label("Loading...");
        srvCategoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        VBox b1 = new VBox(2, new Label("SERVICE CATEGORY"), srvCategoryLabel);
        b1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        srvBudgetLabel = new Label("₹0");
        srvBudgetLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        VBox b2 = new VBox(2, new Label("OFFERED BUDGET"), srvBudgetLabel);
        b2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        top.getChildren().addAll(b1, new Separator(), b2);

        Label reqHeader = new Label("Package Inclusions Required:");
        reqHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        srvTagsPane = new FlowPane();
        srvTagsPane.setHgap(8); srvTagsPane.setVgap(8);

        String[] items = {"🔊 Professional Equipment", "🎙 Wireless Setup", "💡 Stage Support", "👨‍💻 Technical Team"};
        for (String item : items) {
            Label tag = new Label(item);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");
            srvTagsPane.getChildren().add(tag);
        }

        card.getChildren().addAll(title, new Separator(), top, reqHeader, srvTagsPane);
        return card;
    }

    private VBox createEventRequirementsCard() {
        VBox card = createCardContainer();
        Label title = new Label("Detailed Requirements");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        reqListContainer = new VBox(8);
        reqListContainer.getChildren().add(createReqRow("Requirements:", "Standard setup required on site."));

        card.getChildren().addAll(title, new Separator(), reqListContainer);
        return card;
    }

    private VBox createBudgetAndPaymentCard() {
        VBox card = createCardContainer();
        Label title = new Label("Budget & Payment Terms");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox pGrid = new HBox(24);
        payRequestedBudget = new Label("₹85,000"); payRequestedBudget.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        VBox p1 = new VBox(2, new Label("Requested Budget"), payRequestedBudget);
        p1.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        payAdvanceAmount = new Label("Advance: Released"); payAdvanceAmount.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + GREEN_TEXT + ";");
        VBox p2 = new VBox(2, new Label("Payment Terms"), payAdvanceAmount);
        p2.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        payPostEventAmount = new Label("Balance: Upon Completion"); payPostEventAmount.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        VBox p3 = new VBox(2, new Label("Final Settlement"), payPostEventAmount);
        p3.getChildren().get(0).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        pGrid.getChildren().addAll(p1, new Separator(), p2, new Separator(), p3);

        Label terms = new Label("💳 Payment Method: EventXpert Escrow Payment. Advance released upon vendor confirmation.");
        terms.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(title, new Separator(), pGrid, terms);
        return card;
    }

    private VBox createTimelineCard() {
        VBox card = createCardContainer();
        Label title = new Label("Request Timeline");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox timeline = new VBox(6);
        timeline.getChildren().addAll(
                createTimelineStep("✓ Request Received", "Recent", true),
                createTimelineStep("○ Request Reviewed by Vendor", "In Progress", true),
                createTimelineStep("○ Booking Confirmed", "Pending", false)
        );

        card.getChildren().addAll(title, new Separator(), timeline);
        return card;
    }

    private HBox createTimelineStep(String step, String time, boolean isDone) {
        HBox r = new HBox(10);
        r.setAlignment(Pos.CENTER_LEFT);
        Label s = new Label(step);
        s.setStyle("-fx-font-weight: " + (isDone ? "bold" : "normal") + "; -fx-font-size: 12px; -fx-text-fill: " + (isDone ? GREEN_TEXT : SECONDARY_TEXT) + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label t = new Label(time); t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().addAll(s, sp, t);
        return r;
    }

    // ==========================================
    // 5. RIGHT SUMMARY CARD (FIXED WIDTH & FULL READABLE TEXT)
    // ==========================================
    private VBox createRightSummaryCard(Stage stage) {
        VBox card = createCardContainer();
        Label title = new Label("Request Summary");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        summaryStatusLabel = new Label("Status: PENDING");
        summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        summaryPrioLabel = new Label("Important");
        summaryPrioLabel.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 8px;");
        statusRow.getChildren().addAll(summaryStatusLabel, sp, summaryPrioLabel);

        summaryGridContainer = new VBox(8);
        summaryGridContainer.setPadding(new Insets(6, 0, 6, 0));
        summaryGridContainer.getChildren().addAll(
                createBulletLabel("• Event: Loading..."),
                createBulletLabel("• Date: Loading...")
        );

        rightAcceptBtn = createPrimaryButton("Accept Request");
        rightAcceptBtn.setMaxWidth(Double.MAX_VALUE);
        rightAcceptBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        rightRejectBtn = createSecondaryButton("Reject Request");
        rightRejectBtn.setMaxWidth(Double.MAX_VALUE);
        rightRejectBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;");
        rightRejectBtn.setOnAction(e -> openRejectModal(stage));

        card.getChildren().addAll(title, statusRow, new Separator(), summaryGridContainer, new Separator(), rightAcceptBtn, rightRejectBtn);
        return card;
    }

    // ==========================================
    // 6. ORGANIZER INFORMATION CARD (FIXED WIDTH & CLEAN DETAILS)
    // ==========================================
    private VBox createOrganizerCard(Stage stage) {
        VBox card = createCardContainer();
        Label title = new Label("Organizer Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        orgCardName = new Label("Organizer: Loading...");
        orgCardName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        orgCardRole = new Label("👤 Role: Event Organizer");
        orgCardPhone = new Label("📞 Phone: Loading...");
        orgCardEmail = new Label("✉ Email: Loading...");
        Label orgRating = new Label("⭐ Rating: 4.8 / 5.0 (Active Organizer)");

        orgCardRole.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgCardPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgCardEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgRating.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #92400E;");

        VBox details = new VBox(6, orgCardRole, orgCardPhone, orgCardEmail, orgRating);
        details.setPadding(new Insets(4, 0, 4, 0));

        Button contactBtn = createSecondaryButton("Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setOnAction(e -> showToast(stage, "Contact: " + orgCardPhone.getText() + " | " + orgCardEmail.getText()));

        card.getChildren().addAll(title, orgCardName, details, new Separator(), contactBtn);
        return card;
    }

    private HBox createBottomActionBar(Stage stage) {
        bottomActionBar = new HBox(12);
        bottomActionBar.setAlignment(Pos.CENTER_LEFT);
        bottomActionBar.setPadding(new Insets(14, 20, 14, 20));
        bottomActionBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");

        Button backBtn = createSecondaryButton("← Back to Requests");
        backBtn.setOnAction(e -> {
            if (callBackAction != null) callBackAction.run();
        });

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button rejBtn = createSecondaryButton("Reject Request");
        rejBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + BORDER_COLOR + ";");
        rejBtn.setOnAction(e -> openRejectModal(stage));

        Button accBtn = createPrimaryButton("Accept Request");
        accBtn.setOnAction(e -> openAcceptConfirmationModal(stage));

        bottomActionBar.getChildren().addAll(backBtn, sp, rejBtn, accBtn);
        return bottomActionBar;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openAcceptConfirmationModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Accept Service Request");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Accept Service Request?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label msg = new Label("Are you sure you want to accept this request for " + (booking != null ? booking.getEventName() : "this event") + "?");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button confirmBtn = createPrimaryButton("Confirm & Accept");
        confirmBtn.setOnAction(e -> {
            if (booking != null) {
                VendorController controller = new VendorController();
                controller.acceptBookingRequest(booking.getBookingId());
                booking.setStatus("CONFIRMED");
            }

            statusBadgeHeader.setText("ACCEPTED");
            statusBadgeHeader.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
            summaryStatusLabel.setText("Status: ACCEPTED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + GREEN_TEXT + ";");

            if (topAcceptBtn != null) topAcceptBtn.setVisible(false);
            if (rightAcceptBtn != null) rightAcceptBtn.setVisible(false);
            if (rightRejectBtn != null) rightRejectBtn.setVisible(false);

            showToast(parentStage, "✓ Service request accepted successfully in Firestore!");
            modal.close();
        });

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> modal.close());

        btns.getChildren().addAll(confirmBtn, cancelBtn);
        layout.getChildren().addAll(title, msg, new Separator(), btns);

        Scene scene = new Scene(layout, 380, 200);
        modal.setScene(scene);
        modal.show();
    }

    private void openRejectModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
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
            if (booking != null) {
                VendorController controller = new VendorController();
                controller.rejectBookingRequest(booking.getBookingId());
                booking.setStatus("REJECTED");
            }
            statusBadgeHeader.setText("REJECTED");
            statusBadgeHeader.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
            summaryStatusLabel.setText("Status: REJECTED");
            summaryStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + RED_TEXT + ";");
            showToast(parentStage, "Request rejected.");
            modal.close();
        });

        layout.getChildren().addAll(title, new Label("Reason for Rejection:"), reasonCB, new Label("Additional Note:"), msgInput, submitBtn);

        Scene scene = new Scene(layout, 360, 320);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private VBox createSpecItemBox(String label, Label valueLbl) {
        VBox b = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valueLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        b.getChildren().addAll(l, valueLbl);
        return b;
    }

    private HBox createReqRow(String label, String val) {
        HBox r = new HBox(8);
        Label l = new Label(label); l.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().addAll(l, v);
        return r;
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

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (stage != null) alert.initOwner(stage);
        alert.show();
    }
}