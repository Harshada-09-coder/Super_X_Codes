package com.techtrio.view;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookingDetailsV {

    private Scene bookingDetailsScene;
    private Runnable callBackAction;

    private String uid;
    private String idToken;
    private VendorBooking booking;

    // Color Palette Constants
    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
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
    private static final String RED_TEXT = "#DC2626";

    // Dynamic UI Labels
    private Label statusBadgeHeader;
    private Label bookingIdTag;
    private Label amountHeader;
    private Label payBadgeHeader;
    private Label headerEventName;
    private Label headerOrgName;

    // Event Info Card Labels
    private Label specEventName;
    private Label specEventType;
    private Label specEventDate;
    private Label specEventTime;
    private Label specVenue;
    private Label specLocation;
    private Label specExpectedGuests;

    // Service Info Card
    private Label srvNameLabel;
    private Label srvPackageLabel;

    // Requirements List
    private VBox reqListContainer;

    // Payment Card
    private Label payTotalAmount;
    private Label payAdvance;
    private Label payFinal;

    // Venue Card
    private Label venueNameLabel;
    private Label venueAddressLabel;
    private Label venueCapacityLabel;

    // Summary Card
    private Label summaryStatus;
    private Label summaryId;
    private VBox summaryGridContainer;

    // Organizer Card & Actions
    private Label orgNameLabel;
    private Label orgPhoneLabel;
    private Label orgEmailLabel;
    private VBox organizerActionBox;

    public BookingDetailsV() {
        this(null);
    }

    public BookingDetailsV(VendorBooking booking) {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
        this.booking = booking;
    }

    public Scene getbookingDetailsScene(Stage primaryStage, Runnable callBackAction) {
        this.callBackAction = callBackAction;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

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
                createHeaderAndBreadcrumb(primaryStage, callBackAction),
                createBookingHeaderCard(),
                createTwoColumnMainLayout(primaryStage)
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);
        root.setCenter(mainArea);

        loadDynamicBookingDetailsAsync(primaryStage);

        bookingDetailsScene = new Scene(root, 1550, 785);
        return bookingDetailsScene;
    }

    // ==========================================
    // ASYNC FIRESTORE DATA LOADER
    // ==========================================
    private void loadDynamicBookingDetailsAsync(Stage stage) {
        if (booking == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                EventService eventService = new EventService();
                Event event = (booking.getEventId() != null) ? eventService.getEventById(booking.getEventId()) : null;

                OrganizerController orgController = new OrganizerController();
                OrganizerProfile orgProfile = (booking.getOrganizerId() != null) ? orgController.getOrganizerProfile(booking.getOrganizerId()) : null;

                Platform.runLater(() -> {
                    String rawStatus = booking.getStatus() != null ? booking.getStatus().toUpperCase() : "PENDING";
                    boolean isConfirmed = "CONFIRMED".equals(rawStatus);
                    boolean isPending = "PENDING".equals(rawStatus);

                    // Header Card
                    if (statusBadgeHeader != null) {
                        statusBadgeHeader.setText(rawStatus);
                        statusBadgeHeader.setStyle(isConfirmed ? "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;"
                                : "-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
                    }

                    if (bookingIdTag != null) bookingIdTag.setText("BOOKING ID: " + (booking.getBookingId() != null ? booking.getBookingId() : "BK-2026"));
                    if (amountHeader != null) amountHeader.setText(booking.getAmount() != null ? booking.getAmount() : "₹0");
                    if (payBadgeHeader != null) payBadgeHeader.setText(isConfirmed ? "Paid" : "Pending");

                    if (headerEventName != null) headerEventName.setText(booking.getEventName() != null ? booking.getEventName() : "Event");
                    String orgDisplay = orgProfile != null && orgProfile.getName() != null ? orgProfile.getName() : (booking.getOrganizerName() != null ? booking.getOrganizerName() : "Organizer");
                    String phoneDisplay = orgProfile != null && orgProfile.getMobileNo() != null ? orgProfile.getMobileNo() : (booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "N/A");
                    if (headerOrgName != null) headerOrgName.setText("Organizer: " + orgDisplay + "  •  📞 " + phoneDisplay);

                    // Event Information Card
                    String currentVenueName = (ReviewPageC.EventData.confirmedVenueName != null && !ReviewPageC.EventData.confirmedVenueName.isBlank())
                            ? ReviewPageC.EventData.confirmedVenueName
                            : ((event != null && event.getLocality() != null) ? event.getLocality() : "Grand Convention Center");

                    String currentVenueAddress = (ReviewPageC.EventData.confirmedVenueAddress != null && !ReviewPageC.EventData.confirmedVenueAddress.isBlank())
                            ? ReviewPageC.EventData.confirmedVenueAddress
                            : ((booking.getLocation() != null) ? booking.getLocation() : "Pune, Maharashtra");

                    if (specEventName != null) specEventName.setText(booking.getEventName());
                    if (specEventType != null && event != null) specEventType.setText(event.getEventType() != null ? event.getEventType() : "Corporate Conference");
                    if (specEventDate != null) specEventDate.setText("📅 " + (booking.getEventDate() != null ? booking.getEventDate() : "TBD"));
                    if (specEventTime != null) specEventTime.setText("⏰ " + (event != null && event.getTime() != null ? event.getTime() : "09:00 AM – 06:00 PM"));
                    if (specVenue != null) specVenue.setText("🏢 " + currentVenueName);
                    if (specLocation != null) specLocation.setText("📍 " + currentVenueAddress);
                    if (specExpectedGuests != null) specExpectedGuests.setText("👥 " + booking.getGuests() + " Guests");

                    // Service Info
                    if (srvNameLabel != null) srvNameLabel.setText(booking.getServiceCategory() != null ? booking.getServiceCategory() : "Service");
                    if (srvPackageLabel != null) srvPackageLabel.setText("Premium " + (booking.getServiceCategory() != null ? booking.getServiceCategory() : "Package"));

                    // Event Requirements
                    if (reqListContainer != null && event != null) {
                        reqListContainer.getChildren().clear();
                        if (event.getRequirements() != null && !event.getRequirements().isBlank()) {
                            reqListContainer.getChildren().add(createReqRow("Event Requirements:", event.getRequirements()));
                        }
                        if (event.getNotes() != null && !event.getNotes().isBlank()) {
                            reqListContainer.getChildren().add(createReqRow("Special Notes:", event.getNotes()));
                        }
                        if (reqListContainer.getChildren().isEmpty()) {
                            reqListContainer.getChildren().add(createReqRow("Setup Scope:", "Standard execution for " + booking.getGuests() + " corporate guests."));
                        }
                    }

                    // Payment Info
                    String amt = booking.getAmount() != null ? booking.getAmount() : "₹85,000";
                    if (payTotalAmount != null) payTotalAmount.setText(amt);
                    if (payAdvance != null) payAdvance.setText("Completed");
                    if (payFinal != null) payFinal.setText("Escrow Protected");

                    // Venue Info
                    if (venueNameLabel != null) venueNameLabel.setText("🏢 Venue Name: " + currentVenueName);
                    if (venueAddressLabel != null) venueAddressLabel.setText("📍 Address: " + currentVenueAddress);
                    if (venueCapacityLabel != null) venueCapacityLabel.setText("👥 Capacity: Expected " + booking.getGuests() + " Guests");

                    // Summary Card
                    if (summaryStatus != null) {
                        summaryStatus.setText(rawStatus);
                        summaryStatus.setStyle(isConfirmed ? "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 8px;"
                                : "-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 8px;");
                    }
                    if (summaryId != null) summaryId.setText(booking.getBookingId() != null ? booking.getBookingId() : "BK-2026");

                    if (summaryGridContainer != null) {
                        summaryGridContainer.getChildren().clear();
                        summaryGridContainer.getChildren().addAll(
                                createBulletLabel("• Event: " + booking.getEventName()),
                                createBulletLabel("• Date: " + booking.getEventDate()),
                                createBulletLabel("• Service: " + booking.getServiceCategory()),
                                createBulletLabel("• Guests: " + booking.getGuests() + " Pax"),
                                createBulletLabel("• Amount: " + booking.getAmount()),
                                createBulletLabel("• Organizer: " + orgDisplay)
                        );
                    }

                    // Organizer Card
                    if (orgNameLabel != null) orgNameLabel.setText(orgDisplay);
                    if (orgPhoneLabel != null) orgPhoneLabel.setText("📞 Phone: " + phoneDisplay);
                    if (orgEmailLabel != null) orgEmailLabel.setText("✉ Email: " + (orgProfile != null && orgProfile.getEmail() != null ? orgProfile.getEmail() : (booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "N/A")));

                    // Action Buttons
                    if (organizerActionBox != null) {
                        organizerActionBox.getChildren().clear();

                        if (isPending) {
                            Button acceptBtn = createPrimaryButton("✓ Accept Booking Request");
                            acceptBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8;");
                            acceptBtn.setMaxWidth(Double.MAX_VALUE);
                            acceptBtn.setOnAction(e -> {
                                VendorController controller = new VendorController();
                                controller.acceptBookingRequest(booking.getBookingId());
                                booking.setStatus("CONFIRMED");
                                showToast(stage, "✓ Booking confirmed successfully in Firestore!");
                                loadDynamicBookingDetailsAsync(stage);
                            });

                            Button rejectBtn = createSecondaryButton("✕ Reject Request");
                            rejectBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px;");
                            rejectBtn.setMaxWidth(Double.MAX_VALUE);
                            rejectBtn.setOnAction(e -> {
                                VendorController controller = new VendorController();
                                controller.rejectBookingRequest(booking.getBookingId());
                                booking.setStatus("REJECTED");
                                showToast(stage, "Request rejected.");
                                loadDynamicBookingDetailsAsync(stage);
                            });

                            organizerActionBox.getChildren().addAll(acceptBtn, rejectBtn);
                        } else {
                            final String finalPhone = phoneDisplay;
                            Button contactBtn = createSecondaryButton("Contact Organizer");
                            contactBtn.setMaxWidth(Double.MAX_VALUE);
                            contactBtn.setOnAction(e -> showToast(stage, "Organizer Contact: " + finalPhone));

                            Button cancelBtn = createSecondaryButton("Cancel Booking");
                            cancelBtn.setMaxWidth(Double.MAX_VALUE);
                            cancelBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px;");
                            cancelBtn.setOnAction(e -> openCancelModal(stage));

                            organizerActionBox.getChildren().addAll(contactBtn, cancelBtn);
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
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
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
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> {
            ServiceRequestsV req = new ServiceRequestsV(uid, idToken);
            LandingPage.landingPageStage.setScene(req.getserviceRequestVScene());
        }));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", true, e -> {
            if (callBackAction != null) callBackAction.run();
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
        searchField.setPromptText("🔍 Search bookings, events, organizers...");
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
    private VBox createHeaderAndBreadcrumb(Stage stage, Runnable callBackAction) {
        VBox header = new VBox(6);
        Label breadcrumb = new Label("My Bookings  /  Booking Details");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Booking Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to My Bookings");
        backBtn.setOnAction(e -> {
            if (callBackAction != null) callBackAction.run();
        });

        titleRow.getChildren().addAll(title, spacer, backBtn);

        Label subtitle = new Label("View complete booking information, event requirements and payment details.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. BOOKING HEADER CARD
    // ==========================================
    private VBox createBookingHeaderCard() {
        VBox card = createCardContainer();
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        statusBadgeHeader = new Label("CONFIRMED");
        statusBadgeHeader.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        bookingIdTag = new Label("BOOKING ID: BK-2026-00124");
        bookingIdTag.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.TOP_RIGHT);
        amountHeader = new Label("₹85,000");
        amountHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        payBadgeHeader = new Label("Paid");
        payBadgeHeader.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8px;");
        priceBox.getChildren().addAll(amountHeader, payBadgeHeader);

        topRow.getChildren().addAll(statusBadgeHeader, bookingIdTag, spacer, priceBox);

        HBox mainInfoRow = new HBox(16);
        mainInfoRow.setAlignment(Pos.CENTER_LEFT);

        Label calIcon = new Label("📅");
        calIcon.setStyle("-fx-font-size: 22px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 10; -fx-background-radius: 50%;");

        VBox titleBox = new VBox(2);
        headerEventName = new Label("Loading Event Name...");
        headerEventName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        headerOrgName = new Label("Organizer: Loading...");
        headerOrgName.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(headerEventName, headerOrgName);

        mainInfoRow.getChildren().addAll(calIcon, titleBox);
        card.getChildren().addAll(topRow, new Separator(), mainInfoRow);
        return card;
    }

    // ==========================================
    // 5. TWO-COLUMN MAIN LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEventInformationCard(),
                createServiceInformationCard(),
                createRequirementsCard(),
                createPaymentInformationCard(),
                createTimelineCard(),
                createVenueInformationCard(stage)
        );

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createSummaryCard(),
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
        Label specSetupTime = new Label("⏰ 06:00 AM (Prior)");

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

    private VBox createServiceInformationCard() {
        VBox card = createCardContainer();
        Label title = new Label("Service Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox top = new HBox(16);
        top.setAlignment(Pos.CENTER_LEFT);

        srvNameLabel = new Label("Service");
        srvNameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        VBox b1 = new VBox(2, new Label("SERVICE"), srvNameLabel);
        b1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        srvPackageLabel = new Label("Premium Package");
        srvPackageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        VBox b2 = new VBox(2, new Label("PACKAGE"), srvPackageLabel);
        b2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        top.getChildren().addAll(b1, new Separator(), b2);

        Label reqHeader = new Label("Included Equipment & Staff:");
        reqHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        FlowPane tags = new FlowPane();
        tags.setHgap(8); tags.setVgap(8);

        String[] items = {"🔊 Professional Gear", "🎙 Complete Setup", "💡 Production Lighting", "👨‍💻 Technical Team"};
        for (String item : items) {
            Label tag = new Label(item);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");
            tags.getChildren().add(tag);
        }

        card.getChildren().addAll(title, new Separator(), top, reqHeader, tags);
        return card;
    }

    private VBox createRequirementsCard() {
        VBox card = createCardContainer();
        Label title = new Label("Event Requirements");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        reqListContainer = new VBox(8);
        reqListContainer.getChildren().add(createReqRow("Requirements:", "Complete vendor setup required on site."));

        card.getChildren().addAll(title, new Separator(), reqListContainer);
        return card;
    }

    private VBox createPaymentInformationCard() {
        VBox card = createCardContainer();
        Label title = new Label("Payment Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox pGrid = new HBox(20);
        payTotalAmount = new Label("₹85,000"); payTotalAmount.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        VBox p1 = new VBox(2, new Label("Total Amount"), payTotalAmount);
        p1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        payAdvance = new Label("Paid"); payAdvance.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + GREEN_TEXT + ";");
        VBox p2 = new VBox(2, new Label("Advance Payment"), payAdvance);
        p2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        payFinal = new Label("Escrow Protected"); payFinal.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + GREEN_TEXT + ";");
        VBox p3 = new VBox(2, new Label("Final Payment"), payFinal);
        p3.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        pGrid.getChildren().addAll(p1, new Separator(), p2, new Separator(), p3);

        VBox meta = new VBox(4);
        meta.getChildren().addAll(
                new Label("💳 Payment Method: EventXpert Escrow Payment"),
                new Label("🗓 Payment Status: Verified via Escrow Security")
        );
        meta.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        card.getChildren().addAll(title, new Separator(), pGrid, new Separator(), meta);
        return card;
    }

    private VBox createTimelineCard() {
        VBox card = createCardContainer();
        Label title = new Label("Booking Timeline");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox timeline = new VBox(6);
        timeline.getChildren().addAll(
                createTimelineStep("✓ Request Received", "Recent", true),
                createTimelineStep("✓ Request Processed", "Confirmed", true),
                createTimelineStep("● Event Day Scheduled", "Scheduled", true)
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
    // VENUE CARD WITH DIRECT GOOGLE MAPS NAVIGATION
    // ==========================================
    private VBox createVenueInformationCard(Stage stage) {
        VBox card = createCardContainer();
        HBox top = new HBox();
        Label title = new Label("Venue Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button mapBtn = createSecondaryButton("🗺 View on Map");

        // GOOGLE MAPS ACTION FOR VENDOR
        mapBtn.setOnAction(e -> {
            String venueTarget = (ReviewPageC.EventData.confirmedVenueName != null && !ReviewPageC.EventData.confirmedVenueName.isBlank())
                    ? ReviewPageC.EventData.confirmedVenueName : "Grand Convention Center";

            String addressTarget = (ReviewPageC.EventData.confirmedVenueAddress != null && !ReviewPageC.EventData.confirmedVenueAddress.isBlank())
                    ? ReviewPageC.EventData.confirmedVenueAddress : "Pune, Maharashtra";

            openInGoogleMaps(venueTarget + " " + addressTarget);
        });

        top.getChildren().addAll(title, sp, mapBtn);

        String initialVenue = (ReviewPageC.EventData.confirmedVenueName != null && !ReviewPageC.EventData.confirmedVenueName.isBlank())
                ? ReviewPageC.EventData.confirmedVenueName : "Grand Convention Center";

        String initialAddr = (ReviewPageC.EventData.confirmedVenueAddress != null && !ReviewPageC.EventData.confirmedVenueAddress.isBlank())
                ? ReviewPageC.EventData.confirmedVenueAddress : "Pune, Maharashtra";

        venueNameLabel = new Label("🏢 Venue Name: " + initialVenue);
        venueAddressLabel = new Label("📍 Address: " + initialAddr);
        venueCapacityLabel = new Label("👥 Expected Guests: 250");

        VBox details = new VBox(4, venueNameLabel, venueAddressLabel, venueCapacityLabel);
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        card.getChildren().addAll(top, new Separator(), details);
        return card;
    }

    private void openInGoogleMaps(String locationQuery) {
        try {
            String encodedQuery = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8);
            String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedQuery;

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(mapUrl));
            } else {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", mapUrl});
            }
        } catch (Exception ex) {
            System.err.println("Error opening map: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Location Info");
            alert.setHeaderText(null);
            alert.setContentText("Could not open Google Maps.");
            alert.showAndWait();
        }
    }

    private VBox createSummaryCard() {
        VBox card = createCardContainer();
        Label title = new Label("Booking Summary");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox statusRow = new HBox();
        summaryStatus = new Label("CONFIRMED");
        summaryStatus.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 8px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        summaryId = new Label("BK-2026-00124");
        summaryId.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        statusRow.getChildren().addAll(summaryStatus, sp, summaryId);

        summaryGridContainer = new VBox(6);
        summaryGridContainer.getChildren().addAll(
                new Label("• Event: Loading..."),
                new Label("• Date: Loading...")
        );

        card.getChildren().addAll(title, statusRow, new Separator(), summaryGridContainer);
        return card;
    }

    private VBox createOrganizerCard(Stage stage) {
        VBox card = createCardContainer();
        Label title = new Label("Organizer Information");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        orgNameLabel = new Label("TechVision Events");
        orgNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label orgRole = new Label("👤 Role: Corporate Event Organizer");
        orgPhoneLabel = new Label("📞 Phone: Loading...");
        orgEmailLabel = new Label("✉ Email: Loading...");
        Label orgRating = new Label("⭐ Rating: 4.8 / 5.0");

        VBox details = new VBox(4, orgRole, orgPhoneLabel, orgEmailLabel, orgRating);
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        organizerActionBox = new VBox(8);

        Button contactBtn = createSecondaryButton("Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setOnAction(e -> showToast(stage, "Organizer Contact: " + orgPhoneLabel.getText()));

        Button cancelBtn = createSecondaryButton("Cancel Booking");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle("-fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px;");
        cancelBtn.setOnAction(e -> openCancelModal(stage));

        organizerActionBox.getChildren().addAll(contactBtn, cancelBtn);

        card.getChildren().addAll(title, orgNameLabel, details, new Separator(), organizerActionBox);
        return card;
    }

    private void openCancelModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Cancel Booking?");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Cancel Booking?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + RED_TEXT + ";");

        Label msg = new Label("Are you sure you want to cancel this booking? This action may affect your payment and booking status.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button confirmBtn = createPrimaryButton("Confirm Cancellation");
        confirmBtn.setStyle("-fx-background-color: " + RED_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            if (booking != null) {
                VendorController controller = new VendorController();
                controller.rejectBookingRequest(booking.getBookingId());
                booking.setStatus("CANCELLED");
            }
            showToast(parentStage, "Cancellation request submitted.");
            loadDynamicBookingDetailsAsync(parentStage);
            modal.close();
        });

        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> modal.close());

        btns.getChildren().addAll(confirmBtn, cancelBtn);
        layout.getChildren().addAll(title, msg, new Separator(), btns);

        Scene scene = new Scene(layout, 380, 180);
        modal.setScene(scene);
        modal.show();
    }

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