package com.techtrio.view;

import com.techtrio.controller.OrganizerController;
import com.techtrio.controller.VendorController;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ServiceRequestDetailsV {

    private Scene serviceRequestDetailsScene;
    private Runnable callBackAction;

    private String uid;
    private String idToken;
    private VendorBooking booking;

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
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";

    // UI Dynamic Labels
    private Label statusBadgeHeader;
    private Label metaInfoLabel;
    private Label headerEventName;

    private Label specEventName;
    private Label specEventType;
    private Label specEventDate;
    private Label specLocation;
    private Label specExpectedGuests;

    private Label srvCategoryLabel;
    private Label srvBudgetLabel;
    private VBox reqListContainer;

    private Label orgCardName;
    private Label orgCardPhone;
    private Label orgCardEmail;

    private Button acceptActionBtn;
    private Button rejectActionBtn;

    // In-Scene Modal Overlay (No popup stage)
    private StackPane modalOverlayPane;

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

        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane mainLayout = new BorderPane();
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        mainLayout.setLeft(dashHelper.createCommonVendorSidebar("Service Requests"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(primaryStage);
        scrollPane.setContent(mainContent);
        mainLayout.setCenter(scrollPane);

        modalOverlayPane = new StackPane();
        modalOverlayPane.setStyle("-fx-background-color: rgba(6, 27, 65, 0.45);");
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlayPane);

        loadDynamicDetailsAsync(primaryStage);

        serviceRequestDetailsScene = new Scene(rootStack, 1550, 785);
        return serviceRequestDetailsScene;
    }

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
                    boolean isAccepted = "CONFIRMED".equals(status) || "ACCEPTED".equals(status) || "PAID".equals(status);
                    boolean isRejected = "REJECTED".equals(status) || "CANCELLED".equals(status);

                    if (statusBadgeHeader != null) {
                        statusBadgeHeader.setText(status);
                        statusBadgeHeader.setStyle(isAccepted ? "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;"
                                : (isRejected ? "-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;"
                                : "-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12px;"));
                    }

                    if (metaInfoLabel != null) metaInfoLabel.setText("Booking ID: " + (booking.getBookingId() != null ? booking.getBookingId() : "N/A"));
                    if (headerEventName != null) headerEventName.setText(booking.getEventName() != null ? booking.getEventName() : "Event Details");

                    String orgDisplay = orgProfile != null && orgProfile.getName() != null ? orgProfile.getName() : (booking.getOrganizerName() != null ? booking.getOrganizerName() : "Event Organizer");
                    String phoneDisplay = orgProfile != null && orgProfile.getMobileNo() != null ? orgProfile.getMobileNo() : (booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "Not Provided");
                    String emailDisplay = orgProfile != null && orgProfile.getEmail() != null ? orgProfile.getEmail() : (booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "Not Provided");

                    if (specEventName != null) specEventName.setText(booking.getEventName());
                    if (specEventType != null && event != null) specEventType.setText(event.getEventType() != null ? event.getEventType() : "General Event");
                    if (specEventDate != null) specEventDate.setText(booking.getEventDate() != null ? booking.getEventDate() : "Upcoming");
                    if (specLocation != null) specLocation.setText(booking.getLocation() != null ? booking.getLocation() : "Pune");
                    if (specExpectedGuests != null) specExpectedGuests.setText(booking.getGuests() + " Expected Guests");

                    if (srvCategoryLabel != null) srvCategoryLabel.setText(booking.getServiceCategory() != null ? booking.getServiceCategory() : "Service");
                    if (srvBudgetLabel != null) srvBudgetLabel.setText(booking.getAmount() != null ? booking.getAmount() : "₹ 0");

                    if (reqListContainer != null && event != null) {
                        reqListContainer.getChildren().clear();
                        if (event.getRequirements() != null && !event.getRequirements().isBlank()) {
                            reqListContainer.getChildren().add(createDetailRow("Requirements:", event.getRequirements()));
                        }
                        if (event.getNotes() != null && !event.getNotes().isBlank()) {
                            reqListContainer.getChildren().add(createDetailRow("Special Instructions:", event.getNotes()));
                        }
                        if (reqListContainer.getChildren().isEmpty()) {
                            reqListContainer.getChildren().add(createDetailRow("Scope:", "Standard vendor service execution for " + booking.getGuests() + " guests."));
                        }
                    }

                    if (orgCardName != null) orgCardName.setText(orgDisplay);
                    if (orgCardPhone != null) orgCardPhone.setText(phoneDisplay);
                    if (orgCardEmail != null) orgCardEmail.setText(emailDisplay);

                    // Hide decision buttons if already processed
                    if (isAccepted || isRejected) {
                        if (acceptActionBtn != null) { acceptActionBtn.setVisible(false); acceptActionBtn.setManaged(false); }
                        if (rejectActionBtn != null) { rejectActionBtn.setVisible(false); rejectActionBtn.setManaged(false); }
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createMainContent(Stage stage) {
        VBox mainBox = new VBox(18);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bcLink = new Label("← Back to Service Requests");
        bcLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        bcLink.setOnMouseClicked(e -> {
            if (callBackAction != null) callBackAction.run();
        });
        breadcrumbs.getChildren().add(bcLink);

        // Header Card
        VBox headerCard = new VBox(10);
        headerCard.setPadding(new Insets(18));
        headerCard.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        statusBadgeHeader = new Label("PENDING");
        statusBadgeHeader.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        metaInfoLabel = new Label("Booking ID: Loading...");
        metaInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        topRow.getChildren().addAll(statusBadgeHeader, sp, metaInfoLabel);

        headerEventName = new Label("Loading Event Name...");
        headerEventName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        headerCard.getChildren().addAll(topRow, headerEventName);

        // Two Column Balanced Layout (Fixed 380px Right Column)
        HBox twoCol = new HBox(20);

        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createEventSpecsCard(),
                createServiceBudgetCard(),
                createRequirementsCard()
        );

        // ⭐ Right Column with Solid 380px Width (NEVER Squished)
        VBox rightCol = new VBox(16);
        rightCol.setMinWidth(380);
        rightCol.setPrefWidth(380);
        rightCol.setMaxWidth(400);

        rightCol.getChildren().addAll(
                createOrganizerCard(),
                createActionDecisionCard(stage)
        );

        twoCol.getChildren().addAll(leftCol, rightCol);
        mainBox.getChildren().addAll(breadcrumbs, headerCard, twoCol);
        return mainBox;
    }

    private VBox createEventSpecsCard() {
        VBox card = createCardBase("fas-calendar-alt", "Event Specifications");
        GridPane grid = new GridPane();
        grid.setHgap(24); grid.setVgap(12);

        specEventName = new Label("Loading...");
        specEventType = new Label("Loading...");
        specEventDate = new Label("Loading...");
        specLocation = new Label("Loading...");
        specExpectedGuests = new Label("Loading...");

        grid.add(createSpecField("Event Name", specEventName), 0, 0);
        grid.add(createSpecField("Event Type", specEventType), 1, 0);
        grid.add(createSpecField("Date", specEventDate), 0, 1);
        grid.add(createSpecField("Location", specLocation), 1, 1);
        grid.add(createSpecField("Guest Count", specExpectedGuests), 0, 2);

        card.getChildren().add(grid);
        return card;
    }

    private VBox createServiceBudgetCard() {
        VBox card = createCardBase("fas-wallet", "Assigned Service & Budget");
        HBox h = new HBox(30);
        h.setAlignment(Pos.CENTER_LEFT);

        srvCategoryLabel = new Label("Loading...");
        srvCategoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        VBox b1 = new VBox(2, new Label("SERVICE CATEGORY"), srvCategoryLabel);
        b1.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        srvBudgetLabel = new Label("₹ 0");
        srvBudgetLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        VBox b2 = new VBox(2, new Label("ALLOCATED BUDGET"), srvBudgetLabel);
        b2.getChildren().get(0).setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        h.getChildren().addAll(b1, new Separator(), b2);
        card.getChildren().add(h);
        return card;
    }

    private VBox createRequirementsCard() {
        VBox card = createCardBase("fas-clipboard-list", "Event Instructions");
        reqListContainer = new VBox(6);
        card.getChildren().add(reqListContainer);
        return card;
    }

    // ⭐ Beautiful & Spacious Organizer Details Card
    private VBox createOrganizerCard() {
        VBox card = createCardBase("fas-user-tie", "Organizer Details");
        card.setSpacing(10);

        orgCardName = new Label("Loading Organizer...");
        orgCardName.setWrapText(true);
        orgCardName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox contactBox = new VBox(8);
        contactBox.setPadding(new Insets(10));
        contactBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");

        // Phone Row
        HBox phoneRow = new HBox(8);
        phoneRow.setAlignment(Pos.CENTER_LEFT);
        FontIcon phoneIcon = new FontIcon("fas-phone-alt");
        phoneIcon.setIconSize(12);
        phoneIcon.setIconColor(Color.web(PRIMARY_BLUE));
        orgCardPhone = new Label("Phone: Loading...");
        orgCardPhone.setWrapText(true);
        orgCardPhone.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        phoneRow.getChildren().addAll(phoneIcon, orgCardPhone);

        // Email Row
        HBox emailRow = new HBox(8);
        emailRow.setAlignment(Pos.CENTER_LEFT);
        FontIcon emailIcon = new FontIcon("fas-envelope");
        emailIcon.setIconSize(12);
        emailIcon.setIconColor(Color.web(PRIMARY_BLUE));
        orgCardEmail = new Label("Email: Loading...");
        orgCardEmail.setWrapText(true);
        orgCardEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        emailRow.getChildren().addAll(emailIcon, orgCardEmail);

        contactBox.getChildren().addAll(phoneRow, emailRow);
        card.getChildren().addAll(orgCardName, contactBox);
        return card;
    }

    // ⭐ Spacious Action / Decision Card with Full-Width Buttons
    private VBox createActionDecisionCard(Stage stage) {
        VBox card = createCardBase("fas-check-circle", "Accept / Decline Action");
        card.setSpacing(10);

        Label prompt = new Label("Please accept or decline this request to update the event schedule.");
        prompt.setWrapText(true);
        prompt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        acceptActionBtn = new Button("✓ Accept & Confirm Assignment");
        acceptActionBtn.setMaxWidth(Double.MAX_VALUE);
        acceptActionBtn.setPrefHeight(38);
        acceptActionBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-cursor: hand;");
        acceptActionBtn.setOnAction(e -> showInAppAcceptConfirmModal(stage));

        rejectActionBtn = new Button("✕ Decline Request");
        rejectActionBtn.setMaxWidth(Double.MAX_VALUE);
        rejectActionBtn.setPrefHeight(36);
        rejectActionBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        rejectActionBtn.setOnAction(e -> {
            if (booking != null) {
                VendorController controller = new VendorController();
                controller.rejectBookingRequest(booking.getBookingId());
                booking.setStatus("REJECTED");
                loadDynamicDetailsAsync(stage);
            }
        });

        card.getChildren().addAll(prompt, acceptActionBtn, rejectActionBtn);
        return card;
    }

    // ⭐ Compact Horizontal Rectangle Pop-up (No Full-Screen Vertical Stretching)
    private void showInAppAcceptConfirmModal(Stage stage) {
        modalOverlayPane.getChildren().clear();
        modalOverlayPane.setAlignment(Pos.CENTER);

        VBox card = new VBox(14);
        card.setMaxWidth(460);
        card.setMaxHeight(Region.USE_PREF_SIZE); // 🎯 Height फिक्स केली (उभी ताणली जाणार नाही)
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.22), 16, 0, 0, 4);");

        StackPane.setAlignment(card, Pos.CENTER);

        HBox head = new HBox(8);
        head.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon("fas-check-circle");
        icon.setIconSize(18);
        icon.setIconColor(Color.web(GREEN_TEXT));
        Label title = new Label("Confirm Assignment Acceptance");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        head.getChildren().addAll(icon, title);

        Label msg = new Label("Are you sure you want to accept this assignment for '" + (booking != null ? booking.getEventName() : "Event") + "'?\n\nAllocated Budget: " + (booking != null ? booking.getAmount() : "₹ 0") + "\nThis will sync live to the Organizer's Expense Tracker.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setPadding(new Insets(6, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 16; -fx-font-size: 12px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeInAppModal());

        Button confirmBtn = new Button("Yes, Confirm");
        confirmBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 6 18; -fx-cursor: hand;");
        confirmBtn.setOnAction(e -> {
            if (booking != null) {
                VendorController controller = new VendorController();
                controller.acceptBookingRequest(booking.getBookingId());
                booking.setStatus("CONFIRMED");
                closeInAppModal();
                loadDynamicDetailsAsync(stage);
            }
        });

        btns.getChildren().addAll(cancelBtn, confirmBtn);
        card.getChildren().addAll(head, new Separator(), msg, btns);

        modalOverlayPane.getChildren().add(card);
        modalOverlayPane.setVisible(true);
        modalOverlayPane.setManaged(true);
    }

    private void closeInAppModal() {
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);
        modalOverlayPane.getChildren().clear();
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

    private HBox createDetailRow(String label, String val) {
        HBox r = new HBox(8);
        Label l = new Label(label); l.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label v = new Label(val); v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        r.getChildren().addAll(l, v);
        return r;
    }
}