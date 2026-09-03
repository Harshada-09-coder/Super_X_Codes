package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.VenueController;
import com.techtrio.model.Event;
import com.techtrio.model.VenueModel;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Venue {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";
    private static final String GREEN_BG = "#DCFCE7";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String LIGHT_BLUE = "#EFF6FF";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";

    // Dynamic UI References
    private Label lblVenueStatus;
    private Label lblStatusSub;
    private Label lblVenueName;
    private Label lblVenueAddress;
    private Label lblVenueTypeVal;
    private Label lblAreaVal;
    private Label lblCapacityVal;
    private Label lblCostVal;
    private Label lblDateVal;
    private Label lblPaidStatusVal;
    private Hyperlink mapLink;

    private VBox suggestionsContainer;
    private VBox confirmedDetailsCard;
    private StackPane statusIconCircle;
    private FontIcon statusIcon;
    private Button btnContactOrg;

    private ListenerRegistration firestoreListener;
    private final VenueController venueController = new VenueController();
    private final List<VenueModel> currentSuggestions = new ArrayList<>();
    private VenueModel selectedProposalVenue;

    public Venue() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Venue(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getVenueScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Venue"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createVenueContent();
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        initRealtimeVenueListener();

        return new Scene(root, 1550, 785);
    }

    private void initRealtimeVenueListener() {
        if (event == null || event.getEventId() == null || event.getEventId().isBlank()) return;

        new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference docRef = db.collection("events").document(event.getEventId());

                detachListener();

                firestoreListener = docRef.addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    Event ev = snapshot.toObject(Event.class);
                    if (ev != null) {
                        this.event = ev;
                        Platform.runLater(() -> renderDynamicVenueState(ev));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderDynamicVenueState(Event ev) {
        String status = ev.getVenueStatus() != null ? ev.getVenueStatus().toUpperCase() : "PENDING_SUGGESTION";
        String orgName = (ev.getOrganizerName() != null && !ev.getOrganizerName().isBlank()) ? ev.getOrganizerName() : "Assigned Organizer";

        currentSuggestions.clear();
        if (ev.getSuggestedVenues() != null) {
            currentSuggestions.addAll(ev.getSuggestedVenues());
        }

        if ("CONFIRMED".equals(status)) {
            // CONFIRMED VIEW
            lblVenueStatus.setText("Confirmed");
            lblVenueStatus.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Confirmed on\n" + (ev.getEventDate() != null ? ev.getEventDate() : "Live Schedule") + "\nby " + orgName);
            statusIcon.setIconLiteral("fas-check");
            statusIcon.setIconColor(Color.web(GREEN_TEXT));

            lblVenueName.setText(ev.getConfirmedVenueName() != null ? ev.getConfirmedVenueName() : "Confirmed Event Venue");
            lblVenueAddress.setText(ev.getConfirmedVenueAddress() != null ? ev.getConfirmedVenueAddress() : (ev.getCity() != null ? ev.getCity() : "India"));
            lblVenueTypeVal.setText(ev.getVenueType() != null ? ev.getVenueType() : "Banquet Hall");
            lblAreaVal.setText((ev.getArea() != null ? ev.getArea() + ", " : "") + (ev.getCity() != null ? ev.getCity() : "Pune"));
            lblCapacityVal.setText(ev.getGuests() + " Guests");
            lblCostVal.setText(ev.getConfirmedVenueCost() != null ? ev.getConfirmedVenueCost() : "₹ 1,50,000");
            lblDateVal.setText(ev.getStartDate() != null ? ev.getStartDate() : (ev.getEventDate() != null ? ev.getEventDate() : "TBD"));
            lblPaidStatusVal.setText("Confirmed");

            confirmedDetailsCard.setVisible(true);
            confirmedDetailsCard.setManaged(true);
            suggestionsContainer.setVisible(false);
            suggestionsContainer.setManaged(false);

            btnContactOrg.setText("Request Venue Alteration");
            btnContactOrg.setOnAction(e -> handleRequestVenueChangeDialog());

        } else if ("SUGGESTED".equals(status)) {
            // ORGANIZER SENT SUGGESTIONS
            lblVenueStatus.setText("Suggestions Received");
            lblVenueStatus.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + ACTIVE_NAV_BLUE + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Review and select your preferred venue below.");
            statusIcon.setIconLiteral("fas-bell");
            statusIcon.setIconColor(Color.web(ACTIVE_NAV_BLUE));

            confirmedDetailsCard.setVisible(false);
            confirmedDetailsCard.setManaged(false);
            suggestionsContainer.setVisible(true);
            suggestionsContainer.setManaged(true);

            renderSuggestionCards();

        } else if ("CHANGE_REQUESTED".equals(status)) {
            // CHANGE REQUESTED VIEW
            lblVenueStatus.setText("Change In Review");
            lblVenueStatus.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Your organizer is finding new venues based on your request.");
            statusIcon.setIconLiteral("fas-hourglass-half");
            statusIcon.setIconColor(Color.web(WARNING_TEXT));

            confirmedDetailsCard.setVisible(true);
            confirmedDetailsCard.setManaged(true);
            suggestionsContainer.setVisible(false);
            suggestionsContainer.setManaged(false);

            btnContactOrg.setText("Alteration Note Sent ✓");
            btnContactOrg.setDisable(true);

        } else {
            // PENDING SUGGESTION
            lblVenueStatus.setText("Pending Suggestions");
            lblVenueStatus.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Organizer will propose matching venues shortly.");
            statusIcon.setIconLiteral("fas-clock");
            statusIcon.setIconColor(Color.web(TEXT_MUTED));

            lblVenueName.setText("Awaiting Organizer Curation");
            lblVenueAddress.setText("Target Location: " + (ev.getArea() != null ? ev.getArea() + ", " : "") + (ev.getCity() != null ? ev.getCity() : "Pune"));
            lblVenueTypeVal.setText(ev.getVenueType() != null ? ev.getVenueType() : "Banquet Hall");
            lblAreaVal.setText(ev.getCity() != null ? ev.getCity() : "Pune");
            lblCapacityVal.setText(ev.getGuests() + " Guests");
            lblCostVal.setText(ev.getBudget() != null ? ev.getBudget() : "TBD");
            lblDateVal.setText(ev.getStartDate() != null ? ev.getStartDate() : "Upcoming");
            lblPaidStatusVal.setText("Pending");

            confirmedDetailsCard.setVisible(true);
            confirmedDetailsCard.setManaged(true);
            suggestionsContainer.setVisible(false);
            suggestionsContainer.setManaged(false);

            btnContactOrg.setText("Contact Organizer");
            btnContactOrg.setOnAction(e -> handleRequestVenueChangeDialog());
        }
    }

    private void renderSuggestionCards() {
        suggestionsContainer.getChildren().clear();

        Label prompt = new Label("✨ Your Organizer has curated these venues for you. Select one to confirm:");
        prompt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        suggestionsContainer.getChildren().add(prompt);

        HBox cardsBox = new HBox(16);
        for (VenueModel vm : currentSuggestions) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(16));
            card.setPrefWidth(320);
            card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

            Label name = new Label(vm.getName());
            name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

            Label addr = new Label("📍 " + vm.getAddress());
            addr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

            Label cap = new Label("👥 Capacity: " + vm.getCapacity() + " Guests  |  ⭐ " + vm.getRating() + " / 5.0");
            cap.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");

            Label cost = new Label("Est. Cost: ₹ " + String.format("%,.0f", vm.getPrice()));
            cost.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

            Button selectBtn = new Button("✓ Select & Confirm Venue");
            selectBtn.setMaxWidth(Double.MAX_VALUE);
            selectBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");
            selectBtn.setOnAction(e -> handleConfirmVenueSelection(vm));

            card.getChildren().addAll(name, addr, cap, cost, new Separator(), selectBtn);
            cardsBox.getChildren().add(card);
        }

        suggestionsContainer.getChildren().add(cardsBox);
    }

    private void handleConfirmVenueSelection(VenueModel vm) {
        if (event == null || event.getEventId() == null) return;

        boolean success = venueController.confirmCustomerVenueChoice(event.getEventId(), vm, event.getOrganizerName());
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Venue '" + vm.getName() + "' is now confirmed for your event!", ButtonType.OK);
            alert.setHeaderText("Venue Confirmed");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to confirm venue. Please try again.");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        }
    }

    private void handleRequestVenueChangeDialog() {
        if (event == null || event.getEventId() == null) return;

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Request Venue Change");
        dialog.setHeaderText("Need to change venue? Inform your organizer:");
        if (LandingPage.landingPageStage != null) dialog.initOwner(LandingPage.landingPageStage);

        ButtonType submitBtn = new ButtonType("Send Request", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtn, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefWidth(380);

        Label lblPrompt = new Label("Describe your reason & new preferences (e.g. Higher capacity, outdoor lawn, different area):");
        lblPrompt.setWrapText(true);
        lblPrompt.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        TextArea txtNotes = new TextArea();
        txtNotes.setPromptText("Enter your requirements here...");
        txtNotes.setPrefRowCount(4);

        content.getChildren().addAll(lblPrompt, txtNotes);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> btn == submitBtn ? txtNotes.getText().trim() : null);

        Optional<String> res = dialog.showAndWait();
        res.ifPresent(notes -> {
            if (!notes.isEmpty()) {
                boolean sent = venueController.requestVenueChange(event.getEventId(), notes);
                if (sent) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Venue change request sent to Organizer successfully!", ButtonType.OK);
                    if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
                    a.showAndWait();
                }
            }
        });
    }

    private VBox createVenueContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(3);
        Label heading = new Label("Venue Management");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Manage your event venue details, organizer recommendations, and location setup.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        HBox bodyRow = new HBox(20);

        // 1. Status Card
        VBox statusCard = new VBox(12);
        statusCard.setPadding(new Insets(18));
        statusCard.setPrefWidth(260);
        statusCard.setAlignment(Pos.CENTER);
        statusCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label stHeader = new Label("Venue Status");
        stHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        statusIconCircle = new StackPane();
        Circle c = new Circle(28, Color.web("#DCFCE7"));
        statusIcon = new FontIcon("fas-check");
        statusIcon.setIconSize(20);
        statusIcon.setIconColor(Color.web(GREEN_TEXT));
        statusIconCircle.getChildren().addAll(c, statusIcon);

        lblVenueStatus = new Label("Confirmed");
        lblVenueStatus.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

        lblStatusSub = new Label("Loading live status...");
        lblStatusSub.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-text-alignment: center;");

        statusCard.getChildren().addAll(stHeader, statusIconCircle, lblVenueStatus, lblStatusSub);

        // 2. Confirmed Details Card
        confirmedDetailsCard = new VBox(14);
        confirmedDetailsCard.setPadding(new Insets(18));
        confirmedDetailsCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(confirmedDetailsCard, Priority.ALWAYS);

        Label detTitle = new Label("Venue Information");
        detTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox bannerRow = new HBox(16);
        bannerRow.setAlignment(Pos.CENTER_LEFT);

        StackPane imgPlaceholder = new StackPane();
        imgPlaceholder.setPrefSize(140, 90);
        imgPlaceholder.setStyle("-fx-background-color: #334155; -fx-background-radius: 8px;");
        FontIcon bIcon = new FontIcon("fas-hotel");
        bIcon.setIconSize(32);
        bIcon.setIconColor(Color.web("#94A3B8"));
        imgPlaceholder.getChildren().add(bIcon);

        VBox venueDesc = new VBox(4);
        lblVenueName = new Label("Loading Venue...");
        lblVenueName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        lblVenueAddress = new Label("Loading address...");
        lblVenueAddress.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        mapLink = new Hyperlink("📍 View on Google Maps");
        mapLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-padding: 0;");
        mapLink.setOnAction(e -> openInGoogleMaps(lblVenueName.getText() + " " + lblVenueAddress.getText()));

        venueDesc.getChildren().addAll(lblVenueName, lblVenueAddress, mapLink);
        bannerRow.getChildren().addAll(imgPlaceholder, venueDesc);

        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(14);
        grid.setPadding(new Insets(10, 0, 0, 0));

        lblVenueTypeVal = new Label("-");
        lblAreaVal = new Label("-");
        lblCapacityVal = new Label("-");
        lblCostVal = new Label("-");
        lblDateVal = new Label("-");
        lblPaidStatusVal = new Label("-");

        grid.add(createDetailItem("Venue Type", lblVenueTypeVal), 0, 0);
        grid.add(createDetailItem("Area", lblAreaVal), 1, 0);
        grid.add(createDetailItem("Capacity", lblCapacityVal), 0, 1);
        grid.add(createDetailItem("Venue Cost", lblCostVal), 1, 1);
        grid.add(createDetailItem("Event Date", lblDateVal), 0, 2);
        grid.add(createDetailItem("Booking Status", lblPaidStatusVal), 1, 2);

        confirmedDetailsCard.getChildren().addAll(detTitle, bannerRow, grid);

        // 3. Suggestions View Container (When status is SUGGESTED)
        suggestionsContainer = new VBox(14);
        suggestionsContainer.setPadding(new Insets(18));
        suggestionsContainer.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(suggestionsContainer, Priority.ALWAYS);
        suggestionsContainer.setVisible(false);
        suggestionsContainer.setManaged(false);

        bodyRow.getChildren().addAll(statusCard, confirmedDetailsCard, suggestionsContainer);

        // Help / Request Alteration Banner
        HBox helpBanner = new HBox(12);
        helpBanner.setAlignment(Pos.CENTER_LEFT);
        helpBanner.setPadding(new Insets(12, 16, 12, 16));
        helpBanner.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #BFDBFE; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        FontIcon infoIco = new FontIcon("fas-info-circle");
        infoIco.setIconSize(16);
        infoIco.setIconColor(Color.web(PRIMARY_BLUE));

        Label helpTxt = new Label("Need to change venue?\nContact your organizer to request a venue alteration or additional options.");
        helpTxt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        btnContactOrg = new Button("Request Venue Alteration");
        btnContactOrg.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-border-radius: 6px; -fx-cursor: hand;");
        btnContactOrg.setOnAction(e -> handleRequestVenueChangeDialog());

        helpBanner.getChildren().addAll(infoIco, helpTxt, sp, btnContactOrg);

        page.getChildren().addAll(backLink, titleBox, bodyRow, helpBanner);
        return page;
    }

    private VBox createDetailItem(String label, Label valueLbl) {
        VBox box = new VBox(2);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        valueLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        box.getChildren().addAll(l, valueLbl);
        return box;
    }

    private void openInGoogleMaps(String locationQuery) {
        try {
            String encoded = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8);
            String url = "https://www.google.com/maps/search/?api=1&query=" + encoded;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ignored) {}
    }

    private void detachListener() {
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(4, 8, 12, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        VBox mainNav = new VBox(3);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 12 8 4 8;");

        VBox eventNav = new VBox(3);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), this::goOverview),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), () -> {}),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(3);
        bottomNav.getChildren().addAll(
                createNavItem("Support", "fas-life-ring", false, null),
                createNavItem("Settings", "fas-cog", false, this::goSettings)
        );

        sidebar.getChildren().addAll(brandBox, mainNav, sectionLabel, eventNav, spacer, bottomNav);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleBack() {
        detachListener();
        if (backAction != null) backAction.run();
        else goOverview();
    }
    private void goDashboard() {
        detachListener();
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }
    private void goCreateEvent() {
        detachListener();
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }
    private void goOverview() {
        detachListener();
        MyEventDetails view = new MyEventDetails(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getMyEventDetailsScene(this::handleBack));
    }
    private void goBudget() {
        detachListener();
        Budget view = new Budget(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getBudgetScene(this::handleBack));
    }
    private void goGuest() {
        detachListener();
        Guest view = new Guest(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getGuestScene(this::handleBack));
    }
    private void goPayment() {
        detachListener();
        Payment view = new Payment(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getPaymentScene(this::handleBack));
    }
    private void goRequirement() {
        detachListener();
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }
    private void goSettings() {
        detachListener();
        SettingC s = new SettingC();
        LandingPage.landingPageStage.setScene(s.getSettingScene(this::handleBack));
    }
}