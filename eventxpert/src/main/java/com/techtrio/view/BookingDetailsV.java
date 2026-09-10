package com.techtrio.view;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.OrganizerController;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorBooking;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class BookingDetailsV {

    private Scene bookingDetailsScene;
    private Runnable callBackAction;

    private String uid;
    private String idToken;
    private VendorBooking booking;
    private Event event;

    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String ORANGE_TEXT = "#B06000";

    private StackPane rootLayer;

    private Label statusBadgeHeader;
    private Label amountHeader;
    private Label headerEventName;

    private Label specEventName;
    private Label specEventType;
    private Label specEventDate;
    private Label specLocation;
    private Label specExpectedGuests;

    private Label srvNameLabel;
    private Label payTotalAmount;
    private Label venueNameLabel;
    private Label venueAddressLabel;

    private Label orgNameLabel;
    private Label orgPhoneLabel;
    private Label orgEmailLabel;

    private Label liveExecutionStatus;
    private Button btnStartService;
    private Button btnCompleteService;

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

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        root.setLeft(dashHelper.createCommonVendorSidebar("My Bookings"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(primaryStage);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        rootLayer.getChildren().add(root);

        loadDynamicBookingDetailsAsync(primaryStage);

        bookingDetailsScene = new Scene(rootLayer, 1550, 785);
        return bookingDetailsScene;
    }

    private void loadDynamicBookingDetailsAsync(Stage stage) {
        if (booking == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                EventService eventService = new EventService();
                event = (booking.getEventId() != null) ? eventService.getEventById(booking.getEventId()) : null;

                OrganizerController orgController = new OrganizerController();
                OrganizerProfile orgProfile = (booking.getOrganizerId() != null) ? orgController.getOrganizerProfile(booking.getOrganizerId()) : null;

                Platform.runLater(() -> {
                    String rawStatus = booking.getStatus() != null ? booking.getStatus().toUpperCase() : "CONFIRMED";

                    updateStatusBadge(rawStatus);

                    if (amountHeader != null) amountHeader.setText(booking.getAmount() != null ? booking.getAmount() : "₹ 0");

                    if (headerEventName != null) headerEventName.setText(booking.getEventName() != null ? booking.getEventName() : "Event");
                    String orgDisplay = orgProfile != null && orgProfile.getName() != null ? orgProfile.getName() : (booking.getOrganizerName() != null ? booking.getOrganizerName() : "Organizer");
                    String phoneDisplay = orgProfile != null && orgProfile.getMobileNo() != null ? orgProfile.getMobileNo() : (booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "N/A");

                    if (specEventName != null) specEventName.setText(booking.getEventName());
                    if (specEventType != null && event != null) specEventType.setText(event.getEventType() != null ? event.getEventType() : "Event");
                    if (specEventDate != null) specEventDate.setText(booking.getEventDate() != null ? booking.getEventDate() : "Upcoming");
                    if (specLocation != null) specLocation.setText(booking.getLocation() != null ? booking.getLocation() : "Pune");
                    if (specExpectedGuests != null) specExpectedGuests.setText(booking.getGuests() + " Guests");

                    if (srvNameLabel != null) srvNameLabel.setText(booking.getServiceCategory() != null ? booking.getServiceCategory() : "Service");
                    if (payTotalAmount != null) payTotalAmount.setText(booking.getAmount() != null ? booking.getAmount() : "₹ 0");

                    String confirmedVenue = (event != null && event.getConfirmedVenueName() != null && !event.getConfirmedVenueName().startsWith("Pending"))
                            ? event.getConfirmedVenueName()
                            : (booking.getLocation() != null ? booking.getLocation() : "Venue");

                    String confirmedAddr = (event != null && event.getConfirmedVenueAddress() != null && !event.getConfirmedVenueAddress().isBlank())
                            ? event.getConfirmedVenueAddress()
                            : (booking.getLocation() != null ? booking.getLocation() : "Venue Location");

                    if (venueNameLabel != null) venueNameLabel.setText("🏢 Venue: " + confirmedVenue);
                    if (venueAddressLabel != null) venueAddressLabel.setText("📍 Location: " + confirmedAddr);

                    if (orgNameLabel != null) orgNameLabel.setText(orgDisplay);
                    if (orgPhoneLabel != null) orgPhoneLabel.setText("Phone: " + phoneDisplay);
                    if (orgEmailLabel != null) orgEmailLabel.setText("Email: " + (orgProfile != null && orgProfile.getEmail() != null ? orgProfile.getEmail() : "N/A"));

                    updateExecutionButtonsState(rawStatus);
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void updateStatusBadge(String status) {
        if (statusBadgeHeader == null) return;
        statusBadgeHeader.setText(status.toUpperCase());

        if ("COMPLETED".equalsIgnoreCase(status)) {
            statusBadgeHeader.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
        } else if ("IN_PROGRESS".equalsIgnoreCase(status) || "IN PROGRESS".equalsIgnoreCase(status)) {
            statusBadgeHeader.setStyle("-fx-background-color: " + ORANGE_BG + "; -fx-text-fill: " + ORANGE_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
        } else {
            statusBadgeHeader.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
        }
    }

    private void updateExecutionButtonsState(String currentStatus) {
        boolean isCustomerActuallyPaid = event != null && (event.getPaidAmount() > 0 || "PAID".equalsIgnoreCase(event.getStatus()));

        if (!isCustomerActuallyPaid) {
            liveExecutionStatus.setText("🔒 Awaiting Customer Payment. Service execution unlocks once customer confirms payment via Razorpay.");
            liveExecutionStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");
            
            btnStartService.setDisable(true);
            btnCompleteService.setDisable(true);
            btnStartService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
            btnCompleteService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(currentStatus)) {
            liveExecutionStatus.setText("✓ Service has been delivered and marked COMPLETED. Organizer & Customer notified.");
            liveExecutionStatus.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
            btnStartService.setDisable(true);
            btnCompleteService.setDisable(true);
            btnStartService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
            btnCompleteService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
        } else if ("IN_PROGRESS".equalsIgnoreCase(currentStatus) || "IN PROGRESS".equalsIgnoreCase(currentStatus)) {
            liveExecutionStatus.setText("▶ Service is currently IN PROGRESS on event floor.");
            liveExecutionStatus.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE_TEXT + ";");
            btnStartService.setDisable(true);
            btnCompleteService.setDisable(false);
            btnStartService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
            btnCompleteService.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");
        } else {
            liveExecutionStatus.setText("● Payment Secured in Escrow. Ready for event day execution.");
            liveExecutionStatus.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
            btnStartService.setDisable(false);
            btnCompleteService.setDisable(true);
            btnStartService.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");
            btnCompleteService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
        }
    }

private void updateServiceBookingStatus(String newStatus) {
        if (booking == null || booking.getBookingId() == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    String bId = booking.getBookingId().trim();
                    String eId = booking.getEventId();
                    System.out.println("Updating vendorBookings ID: " + bId + " to Status: " + newStatus);

                    // ⭐ अधिकृत VendorBookingService द्वारे 'vendorBookings' कलेक्शन अपडेट करणे
                    com.techtrio.services.VendorBookingService vService = new com.techtrio.services.VendorBookingService();
                    boolean updated = vService.updateBookingStatus(bId, newStatus);
                    System.out.println("Firestore update result: " + updated);

                    // Event ला टच करणे जेणेकरून Organizer आणि Customer स्क्रीन्स लगेच ट्रिगर होतील
                    if (eId != null && !eId.isBlank()) {
                        Firestore db = com.google.firebase.cloud.FirestoreClient.getFirestore();
                        if (db != null) {
                            try {
                                db.collection("events").document(eId.trim())
                                        .update("updatedAt", String.valueOf(System.currentTimeMillis())).get();
                            } catch (Exception ignored) {}
                        }
                    }

                    booking.setStatus(newStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                updateStatusBadge(newStatus);
                updateExecutionButtonsState(newStatus);
                showInAppModal("Success", "Service status marked as " + newStatus);
            });
        });

        new Thread(task).start();
    }

    private void showInAppModal(String title, String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + SIDEBAR_NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(18);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);
        Label bcLink = new Label("← Back to My Bookings");
        bcLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        bcLink.setOnMouseClicked(e -> {
            if (callBackAction != null) callBackAction.run();
        });
        breadcrumbs.getChildren().add(bcLink);

        // Header Card (Booking ID पूर्णपणे काढून टाकला आहे)
        VBox headerCard = new VBox(10);
        headerCard.setPadding(new Insets(16));
        headerCard.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        statusBadgeHeader = new Label("CONFIRMED");
        statusBadgeHeader.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        amountHeader = new Label("₹ 0");
        amountHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        topRow.getChildren().addAll(statusBadgeHeader, sp, amountHeader);

        headerEventName = new Label("Loading Event Name...");
        headerEventName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        headerCard.getChildren().addAll(topRow, headerEventName);

        // Two Columns
        HBox twoCol = new HBox(20);

        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEventSpecsCard(),
                createServicePaymentCard(),
                createVenueCard()
        );

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(360);
        rightCol.getChildren().addAll(
                createOrganizerCard(),
                createServiceExecutionCard()
        );

        twoCol.getChildren().addAll(leftCol, rightCol);
        mainBox.getChildren().addAll(breadcrumbs, headerCard, twoCol);
        return mainBox;
    }

    private VBox createServiceExecutionCard() {
        VBox card = createCardBase("fas-tasks", "On-Floor Service Execution");

        liveExecutionStatus = new Label("Checking payment authorization...");
        liveExecutionStatus.setWrapText(true);
        liveExecutionStatus.setStyle("-fx-font-size: 11.5px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        btnStartService = new Button("▶  Start Service (In Progress)");
        btnStartService.setMaxWidth(Double.MAX_VALUE);
        btnStartService.setDisable(true);
        btnStartService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
        btnStartService.setOnAction(e -> updateServiceBookingStatus("IN_PROGRESS"));

        btnCompleteService = new Button("✓  Mark Service as Completed");
        btnCompleteService.setMaxWidth(Double.MAX_VALUE);
        btnCompleteService.setDisable(true);
        btnCompleteService.setStyle("-fx-background-color: #CBD5E1; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px;");
        btnCompleteService.setOnAction(e -> updateServiceBookingStatus("COMPLETED"));

        card.getChildren().addAll(liveExecutionStatus, new Separator(), btnStartService, btnCompleteService);
        return card;
    }

    private VBox createEventSpecsCard() {
        VBox card = createCardBase("fas-calendar-alt", "Event Information");
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(10);

        specEventName = new Label("Loading...");
        specEventType = new Label("Loading...");
        specEventDate = new Label("Loading...");
        specLocation = new Label("Loading...");
        specExpectedGuests = new Label("Loading...");

        grid.add(createSpecField("Event Name", specEventName), 0, 0);
        grid.add(createSpecField("Event Type", specEventType), 1, 0);
        grid.add(createSpecField("Date", specEventDate), 0, 1);
        grid.add(createSpecField("Location", specLocation), 1, 1);
        grid.add(createSpecField("Guests", specExpectedGuests), 0, 2);

        card.getChildren().add(grid);
        return card;
    }

    private VBox createServicePaymentCard() {
        VBox card = createCardBase("fas-wallet", "Confirmed Service & Financials");
        HBox h = new HBox(30);
        h.setAlignment(Pos.CENTER_LEFT);

        srvNameLabel = new Label("Loading...");
        srvNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        VBox b1 = new VBox(2, new Label("SERVICE"), srvNameLabel);
        b1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        payTotalAmount = new Label("₹ 0");
        payTotalAmount.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        VBox b2 = new VBox(2, new Label("CONFIRMED AMOUNT (PAID / ESCROW)"), payTotalAmount);
        b2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        h.getChildren().addAll(b1, new Separator(), b2);
        card.getChildren().add(h);
        return card;
    }

    private VBox createVenueCard() {
        VBox card = createCardBase("fas-map-marker-alt", "Venue Information");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        venueNameLabel = new Label("🏢 Venue: Loading...");
        venueNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button mapBtn = new Button("View on Map");
        FontIcon mapIcon = new FontIcon("fas-map");
        mapIcon.setIconSize(11);
        mapBtn.setGraphic(mapIcon);
        mapBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 4px; -fx-cursor: hand;");
        mapBtn.setOnAction(e -> {
            String query = venueNameLabel.getText().replace("🏢 Venue: ", "") + " " + venueAddressLabel.getText().replace("📍 Location: ", "");
            openInGoogleMaps(query);
        });

        top.getChildren().addAll(venueNameLabel, sp, mapBtn);
        venueAddressLabel = new Label("📍 Location: Loading...");
        venueAddressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, venueAddressLabel);
        return card;
    }

    private VBox createOrganizerCard() {
        VBox card = createCardBase("fas-user-tie", "Organizer Details");
        orgNameLabel = new Label("Loading...");
        orgNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        orgPhoneLabel = new Label("Phone: Loading...");
        orgPhoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgEmailLabel = new Label("Email: Loading...");
        orgEmailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(orgNameLabel, orgPhoneLabel, orgEmailLabel);
        return card;
    }

    private void openInGoogleMaps(String locationQuery) {
        try {
            String encodedQuery = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8);
            String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedQuery;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(mapUrl));
            }
        } catch (Exception ignored) {}
    }

    private VBox createCardBase(String iconLit, String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox head = new HBox(8);
        head.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");
        head.getChildren().addAll(icon, t);

        card.getChildren().addAll(head, new Separator());
        return card;
    }

    private VBox createSpecField(String label, Label valueLbl) {
        VBox b = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valueLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        b.getChildren().addAll(l, valueLbl);
        return b;
    }
}