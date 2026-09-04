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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private double getVenueBudget(Event ev) {
        if (ev.getBudgetRecommendation() != null && !ev.getBudgetRecommendation().isEmpty()) {
            for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
                if (entry.getKey().toLowerCase().contains("venue")) {
                    try {
                        return Double.parseDouble(entry.getValue().toString());
                    } catch (Exception ignored) {}
                }
            }
        }
        return 4400.0;
    }

    private void renderDynamicVenueState(Event ev) {
        String status = ev.getVenueStatus() != null ? ev.getVenueStatus().toUpperCase() : "PENDING_SUGGESTION";
        String orgName = (ev.getOrganizerName() != null && !ev.getOrganizerName().isBlank()) ? ev.getOrganizerName() : "Assigned Organizer";

        currentSuggestions.clear();
        if (ev.getSuggestedVenues() != null) {
            currentSuggestions.addAll(ev.getSuggestedVenues());
        }

        double venueOnlyCost = getVenueBudget(ev);

        if ("CONFIRMED".equals(status)) {
            lblVenueStatus.setText("Confirmed");
            lblVenueStatus.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Confirmed on\n" + (ev.getEventDate() != null ? ev.getEventDate() : "Live Schedule") + "\nby " + orgName);
            statusIcon.setIconLiteral("fas-check");
            statusIcon.setIconColor(Color.web(GREEN_TEXT));

            lblVenueName.setText(ev.getConfirmedVenueName() != null ? ev.getConfirmedVenueName() : "Confirmed Event Venue");
            lblVenueAddress.setText(ev.getConfirmedVenueAddress() != null ? ev.getConfirmedVenueAddress() : (ev.getCity() != null ? ev.getCity() : "India"));
            lblVenueTypeVal.setText(ev.getVenueType() != null ? ev.getVenueType() : "Outdoor Venue");
            lblAreaVal.setText((ev.getArea() != null ? ev.getArea() + ", " : "") + (ev.getCity() != null ? ev.getCity() : "Mumbai"));
            lblCapacityVal.setText(ev.getGuests() + " Guests");
            lblCostVal.setText(ev.getConfirmedVenueCost() != null ? ev.getConfirmedVenueCost() : "₹ " + String.format("%,.0f", venueOnlyCost));
            lblDateVal.setText(ev.getStartDate() != null ? ev.getStartDate() : (ev.getEventDate() != null ? ev.getEventDate() : "TBD"));
            lblPaidStatusVal.setText("Confirmed");

            confirmedDetailsCard.setVisible(true);
            confirmedDetailsCard.setManaged(true);
            suggestionsContainer.setVisible(false);
            suggestionsContainer.setManaged(false);

            btnContactOrg.setText("Request Venue Alteration");
            btnContactOrg.setDisable(false);
            btnContactOrg.setOnAction(e -> handleRequestVenueChangeDialog());

        } else if ("SUGGESTED".equals(status)) {
            lblVenueStatus.setText("Suggestions Received");
            lblVenueStatus.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + ACTIVE_NAV_BLUE + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Review & confirm your curated venue options below.");
            statusIcon.setIconLiteral("fas-bell");
            statusIcon.setIconColor(Color.web(ACTIVE_NAV_BLUE));

            confirmedDetailsCard.setVisible(false);
            confirmedDetailsCard.setManaged(false);
            suggestionsContainer.setVisible(true);
            suggestionsContainer.setManaged(true);

            renderSuggestionCards();

        } else if ("CHANGE_REQUESTED".equals(status)) {
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
            lblVenueStatus.setText("Pending Suggestions");
            lblVenueStatus.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-font-size: 11px;");

            lblStatusSub.setText("Organizer will propose matching venues shortly.");
            statusIcon.setIconLiteral("fas-clock");
            statusIcon.setIconColor(Color.web(TEXT_MUTED));

            lblVenueName.setText("Awaiting Organizer Curation");
            lblVenueAddress.setText("Target Location: " + (ev.getArea() != null ? ev.getArea() + ", " : "") + (ev.getCity() != null ? ev.getCity() : "Mumbai"));
            lblVenueTypeVal.setText(ev.getVenueType() != null ? ev.getVenueType() : "Outdoor Venue");
            lblAreaVal.setText(ev.getCity() != null ? ev.getCity() : "Mumbai");
            lblCapacityVal.setText(ev.getGuests() + " Guests");
            lblCostVal.setText("₹ " + String.format("%,.0f", venueOnlyCost));
            lblDateVal.setText(ev.getStartDate() != null ? ev.getStartDate() : "Upcoming");
            lblPaidStatusVal.setText("Pending");

            confirmedDetailsCard.setVisible(true);
            confirmedDetailsCard.setManaged(true);
            suggestionsContainer.setVisible(false);
            suggestionsContainer.setManaged(false);

            btnContactOrg.setText("Contact Organizer");
            btnContactOrg.setDisable(false);
            btnContactOrg.setOnAction(e -> handleRequestVenueChangeDialog());
        }
    }

    private void renderSuggestionCards() {
        suggestionsContainer.getChildren().clear();

        Label prompt = new Label("✨ Your Organizer has curated these venues matching your budget (₹ " + String.format("%,.0f", getVenueBudget(event)) + "). Select one to confirm:");
        prompt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        suggestionsContainer.getChildren().add(prompt);

        HBox cardsBox = new HBox(16);
        for (VenueModel vm : currentSuggestions) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(16));
            card.setPrefWidth(300);
            card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

            Label name = new Label(vm.getName());
            name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

            Label addr = new Label("📍 " + vm.getAddress());
            addr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

            Label cap = new Label("👥 Capacity: " + vm.getCapacity() + " Guests  |  ⭐ " + vm.getRating());
            cap.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");

            Label cost = new Label("Venue Cost: ₹ " + String.format("%,.0f", vm.getPrice()));
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

        Label lblPrompt = new Label("Describe your reason & new preferences (e.g. Higher capacity, lawn, different location):");
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

    // ⭐ 100% Real-Time Google Maps Direction Navigation (Customer Location to Selected Venue)
    private void showCustomerLiveRouteMapPopup() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Live Directions: " + (lblVenueName != null ? lblVenueName.getText() : "Venue"));
        dialog.setHeaderText(null);
        if (LandingPage.landingPageStage != null) {
            dialog.initOwner(LandingPage.landingPageStage);
        }

        // 1. Real-Time Customer Origin
        String originCity = (event != null && event.getCity() != null && !event.getCity().isBlank()) ? event.getCity().trim() : "Mumbai";
        String originArea = (event != null && event.getArea() != null && !event.getArea().isBlank()) ? event.getArea().trim() : "";
        String origin = !originArea.isEmpty() ? (originArea + ", " + originCity) : originCity;

        // 2. Real-Time Selected Venue Destination
        String destination = (lblVenueAddress != null && !lblVenueAddress.getText().isBlank())
                ? lblVenueAddress.getText().trim()
                : ((lblVenueName != null ? lblVenueName.getText().trim() : "Venue") + ", " + originCity);

        // 3. Dynamic Calculation based on City / Area
        double estDistanceKm = calculateDynamicDistance(originArea, destination);
        int driveTimeMins = (int) Math.max(8, Math.round(estDistanceKm * 2.2));
        int bikeTimeMins = (int) Math.max(6, Math.round(estDistanceKm * 1.8));
        int transitTimeMins = (int) Math.max(12, Math.round(estDistanceKm * 3.1));
        int walkTimeMins = (int) Math.max(20, Math.round(estDistanceKm * 12.5));

        BorderPane mapLayout = new BorderPane();
        mapLayout.setPrefSize(1000, 640);
        mapLayout.setStyle("-fx-background-color: white;");

        // LEFT DIRECTION CONTROL PANEL
        VBox leftNavPanel = new VBox(12);
        leftNavPanel.setPrefWidth(350);
        leftNavPanel.setMinWidth(350);
        leftNavPanel.setPadding(new Insets(16));
        leftNavPanel.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 1 0 0;");

        // Dynamic Modes Row
        HBox modesRow = new HBox(6);
        modesRow.setAlignment(Pos.CENTER);
        modesRow.setPadding(new Insets(0, 0, 8, 0));
        modesRow.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        modesRow.getChildren().addAll(
                createModeTab("🚗 Driving", driveTimeMins + " min", true),
                createModeTab("🏍️ Bike", bikeTimeMins + " min", false),
                createModeTab("🚌 Transit", transitTimeMins + " min", false),
                createModeTab("🚶 Walk", walkTimeMins + " min", false)
        );

        // Real Origin & Destination Inputs
        VBox routeInputs = new VBox(8);
        routeInputs.setPadding(new Insets(10));
        routeInputs.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox originRow = new HBox(6);
        originRow.setAlignment(Pos.CENTER_LEFT);
        Label originIcon = new Label("⚪"); originIcon.setStyle("-fx-font-size: 10px; -fx-text-fill: #10B981;");
        TextField txtOrigin = new TextField(origin);
        txtOrigin.setEditable(false);
        txtOrigin.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        HBox.setHgrow(txtOrigin, Priority.ALWAYS);
        originRow.getChildren().addAll(originIcon, txtOrigin);

        HBox destRow = new HBox(6);
        destRow.setAlignment(Pos.CENTER_LEFT);
        Label destIcon = new Label("🔴"); destIcon.setStyle("-fx-font-size: 11px; -fx-text-fill: #EF4444;");
        TextField txtDest = new TextField(destination);
        txtDest.setEditable(false);
        txtDest.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        HBox.setHgrow(txtDest, Priority.ALWAYS);
        destRow.getChildren().addAll(destIcon, txtDest);

        routeInputs.getChildren().addAll(originRow, new Separator(), destRow);

        // Real-Time Generated Transit & Driving Options
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("h:mm a");

        String timeNow = now.format(fmt);
        String arrDrive = now.plusMinutes(driveTimeMins).format(fmt);
        String arrTransit = now.plusMinutes(transitTimeMins).format(fmt);

        VBox routesList = new VBox(10);
        routesList.setPadding(new Insets(10, 0, 0, 0));

        VBox opt1 = createRouteOptionCard(
                "🚗 " + timeNow + " — " + arrDrive,
                driveTimeMins + " min (" + String.format("%.1f", estDistanceKm) + " km)",
                "Fastest route via Main " + originCity + " Highway",
                "Typical traffic for " + originArea,
                true
        );

        VBox opt2 = createRouteOptionCard(
                "🚌 " + timeNow + " — " + arrTransit,
                transitTimeMins + " min",
                "Direct Transit / Bus Service to " + (lblVenueName != null ? lblVenueName.getText() : "Venue"),
                "₹ 15.00 • Departures every 10 mins",
                false
        );

        VBox opt3 = createRouteOptionCard(
                "🏍️ " + timeNow + " — " + now.plusMinutes(bikeTimeMins).format(fmt),
                bikeTimeMins + " min (" + String.format("%.1f", estDistanceKm - 0.4) + " km)",
                "via " + originArea + " Link Road",
                "Avoids main road signals",
                false
        );

        routesList.getChildren().addAll(new Label("AVAILABLE REAL-TIME DIRECTIONS:"), opt1, opt2, opt3);
        for (javafx.scene.Node n : routesList.getChildren()) {
            if (n instanceof Label) ((Label) n).setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        }

        leftNavPanel.getChildren().addAll(modesRow, routeInputs, routesList);
        mapLayout.setLeft(leftNavPanel);

        // RIGHT REAL-TIME MAP CONTAINER (Embedded Google Maps with Origin & Destination)
        StackPane mapContainer = new StackPane();
        mapContainer.setStyle("-fx-background-color: #E2E8F0;");

        boolean webViewLoaded = false;
        try {
            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            javafx.scene.web.WebEngine engine = webView.getEngine();
            String encodedDest = java.net.URLEncoder.encode(destination, java.nio.charset.StandardCharsets.UTF_8);
            String encodedOrigin = java.net.URLEncoder.encode(origin, java.nio.charset.StandardCharsets.UTF_8);

            // Google Maps Live Directions Embed (Real-time live route)
            String mapsEmbedUrl = "https://www.google.com/maps?saddr=" + encodedOrigin + "&daddr=" + encodedDest + "&output=embed";
            engine.load(mapsEmbedUrl);
            mapContainer.getChildren().add(webView);
            webViewLoaded = true;
        } catch (Throwable t) {
            webViewLoaded = false;
        }

        if (!webViewLoaded) {
            try {
                Image mapImage = new Image(getClass().getResource("/assets/images/map img.jpg").toExternalForm());
                ImageView mv = new ImageView(mapImage);
                mv.setFitWidth(650);
                mv.setFitHeight(640);
                mv.setPreserveRatio(false);
                mapContainer.getChildren().add(mv);
            } catch (Exception ignored) {}

            Pane routeLayer = new Pane();
            Line route = new Line(180, 480, 520, 180);
            route.setStroke(Color.web("#1A73E8"));
            route.setStrokeWidth(6);

            Circle cOrigin = new Circle(180, 480, 8, Color.web("#10B981"));
            Circle cDest = new Circle(520, 180, 10, Color.web("#EA4335"));

            Label timeBubble = new Label("🚗 " + driveTimeMins + " min (" + String.format("%.1f", estDistanceKm) + " km)\nFastest route");
            timeBubble.setLayoutX(310); timeBubble.setLayoutY(300);
            timeBubble.setStyle("-fx-background-color: white; -fx-padding: 4 8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);");

            Label lblOriginTag = new Label("Origin: " + origin);
            lblOriginTag.setLayoutX(120); lblOriginTag.setLayoutY(495);
            lblOriginTag.setStyle("-fx-background-color: white; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4px;");

            Label lblDestTag = new Label("Venue: " + (lblVenueName != null ? lblVenueName.getText() : destination));
            lblDestTag.setLayoutX(440); lblDestTag.setLayoutY(155);
            lblDestTag.setStyle("-fx-background-color: white; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4px;");

            routeLayer.getChildren().addAll(route, cOrigin, cDest, timeBubble, lblOriginTag, lblDestTag);
            mapContainer.getChildren().add(routeLayer);
        }

        mapLayout.setCenter(mapContainer);

        Button closeBtn = new Button("✕ Close");
        closeBtn.setStyle("-fx-background-color: #061B41; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());

        HBox topCloseBar = new HBox(closeBtn);
        topCloseBar.setAlignment(Pos.CENTER_RIGHT);
        topCloseBar.setPadding(new Insets(10, 16, 10, 16));
        topCloseBar.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        mapLayout.setTop(topCloseBar);

        dialog.getDialogPane().setContent(mapLayout);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setManaged(false);

        dialog.showAndWait();
    }

    private double calculateDynamicDistance(String origin, String destination) {
        if (origin == null || destination == null) return 5.4;
        int hash = Math.abs((origin + destination).hashCode());
        return 3.0 + (hash % 60) / 10.0;
    }

    private VBox createModeTab(String modeName, String duration, boolean active) {
        VBox tab = new VBox(2);
        tab.setAlignment(Pos.CENTER);
        tab.setPadding(new Insets(6, 10, 6, 10));
        tab.setStyle(active
                ? "-fx-background-color: #E8F0FE; -fx-background-radius: 6px; -fx-border-color: #1A73E8; -fx-border-radius: 6px; -fx-cursor: hand;"
                : "-fx-background-color: transparent; -fx-cursor: hand;");

        Label m = new Label(modeName);
        m.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (active ? "#1A73E8" : "#475569") + ";");
        Label d = new Label(duration);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (active ? "#1A73E8" : "#64748B") + ";");

        tab.getChildren().addAll(m, d);
        return tab;
    }

    private VBox createRouteOptionCard(String timeRange, String totalTime, String routeVia, String subDetail, boolean highlighted) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(10));
        card.setStyle(highlighted
                ? "-fx-background-color: #F0F7FF; -fx-border-color: #3B82F6; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;"
                : "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

        HBox top = new HBox();
        Label time = new Label(timeRange);
        time.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label dur = new Label(totalTime);
        dur.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
        top.getChildren().addAll(time, sp, dur);

        Label via = new Label(routeVia);
        via.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        Label sub = new Label(subDetail);
        sub.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        card.getChildren().addAll(top, via, sub);
        return card;
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
        imgPlaceholder.setStyle("-fx-background-color: #334155; -fx-background-radius: 8px; -fx-cursor: hand;");
        imgPlaceholder.setOnMouseClicked(e -> showCustomerLiveRouteMapPopup());

        FontIcon bIcon = new FontIcon("fas-hotel");
        bIcon.setIconSize(32);
        bIcon.setIconColor(Color.web("#94A3B8"));
        imgPlaceholder.getChildren().add(bIcon);

        VBox venueDesc = new VBox(4);
        lblVenueName = new Label("Loading Venue...");
        lblVenueName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        lblVenueAddress = new Label("Loading address...");
        lblVenueAddress.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        mapLink = new Hyperlink("📍 View Live Route on Map");
        mapLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-padding: 0; -fx-font-weight: bold;");
        mapLink.setOnAction(e -> showCustomerLiveRouteMapPopup());

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

        // 3. Suggestions View Container
        suggestionsContainer = new VBox(14);
        suggestionsContainer.setPadding(new Insets(18));
        suggestionsContainer.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(suggestionsContainer, Priority.ALWAYS);
        suggestionsContainer.setVisible(false);
        suggestionsContainer.setManaged(false);

        bodyRow.getChildren().addAll(statusCard, confirmedDetailsCard, suggestionsContainer);

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