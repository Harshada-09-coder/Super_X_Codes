package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.VendorController;
import com.techtrio.model.VendorBooking;
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

public class ServiceRequestsV {

    private Scene serviceRequestVScene;
    private VendorDashboardV vendorDashboard;
    private String uid;
    private String idToken;

    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String WARNING_TEXT = "#D97706";
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";

    public static class RequestData {
        public String id;
        public String organizer;
        public String phone;
        public String eventName;
        public String date;
        public String location;
        public String guests;
        public String service;
        public String budget;
        public String status;
        public VendorBooking rawBooking;
        public boolean isEventCompleted; // ⭐ इव्हेंट पूर्ण झाला आहे का तपासण्यासाठी

        public RequestData(String id, String organizer, String phone, String eventName,
                           String date, String location, String guests, String service,
                           String budget, String status, VendorBooking rawBooking, boolean isEventCompleted) {
            this.id = id;
            this.organizer = organizer;
            this.phone = phone;
            this.eventName = eventName;
            this.date = date;
            this.location = location;
            this.guests = guests;
            this.service = service;
            this.budget = budget;
            this.status = status;
            this.rawBooking = rawBooking;
            this.isEventCompleted = isEventCompleted;
        }
    }

    private final List<RequestData> requestList = new ArrayList<>();
    private VBox cardsContainer;
    private String activeTabFilter = "All";

    private Label totalReqValLbl;
    private Label newReqValLbl;
    private Label acceptedValLbl;
    private Label rejectedValLbl;
    private HBox tabsRowContainer;
    private TextField searchInput;

    private StackPane modalOverlayPane;

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
        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane mainLayout = new BorderPane();
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        mainLayout.setLeft(dashHelper.createCommonVendorSidebar("Service Requests"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createSummaryCardsRow(),
                createFilterSection(),
                tabsRowContainer = createTabsRow(),
                createMainSplitSection()
        );

        scrollPane.setContent(mainContent);
        mainLayout.setCenter(scrollPane);

        modalOverlayPane = new StackPane();
        modalOverlayPane.setStyle("-fx-background-color: rgba(6, 27, 65, 0.45);");
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlayPane);

        loadFirestoreRequestsAsync();

        serviceRequestVScene = new Scene(rootStack, 1550, 785);
        return serviceRequestVScene;
    }

    // ⭐ Event Completed तपासणारे लॉजिक
    private void loadFirestoreRequestsAsync() {
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
            new Thread(() -> {
                Firestore db = FirestoreClient.getFirestore();
                List<RequestData> tempList = new ArrayList<>();

                if (bookings != null) {
                    for (VendorBooking b : bookings) {
                        String rawStatus = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";
                        boolean isEventCompleted = false;

                        // इव्हेंटचे मूळ स्टेटस तपासणे
                        if (db != null && b.getEventId() != null && !b.getEventId().isBlank()) {
                            try {
                                DocumentSnapshot evSnap = db.collection("events").document(b.getEventId().trim()).get().get();
                                if (evSnap.exists()) {
                                    String evSt = evSnap.getString("status");
                                    if ("COMPLETED".equalsIgnoreCase(evSt)) {
                                        isEventCompleted = true;
                                    }
                                }
                            } catch (Exception ignored) {}
                        }

                        if ("COMPLETED".equalsIgnoreCase(rawStatus)) {
                            isEventCompleted = true;
                        }

                        String uiStatus;
                        // ⭐ जर इव्हेंट पूर्ण झाला असेल किंवा कन्फर्म असेल तर तो आपोआप "Accepted" मध्ये जाईल!
                        if (isEventCompleted || "CONFIRMED".equals(rawStatus) || "ACCEPTED".equals(rawStatus) || "PAID".equals(rawStatus)) {
                            uiStatus = isEventCompleted ? "Completed" : "Accepted";
                        } else if ("REJECTED".equals(rawStatus) || "CANCELLED".equals(rawStatus)) {
                            uiStatus = "Rejected";
                        } else {
                            uiStatus = "New";
                        }

                        tempList.add(new RequestData(
                                b.getBookingId() != null ? b.getBookingId() : "",
                                b.getOrganizerName() != null ? b.getOrganizerName() : "Organizer",
                                b.getCustomerPhone() != null ? b.getCustomerPhone() : "N/A",
                                b.getEventName() != null ? b.getEventName() : "Event",
                                b.getEventDate() != null ? b.getEventDate() : "Upcoming",
                                b.getLocation() != null ? b.getLocation() : "Pune",
                                String.valueOf(b.getGuests() > 0 ? b.getGuests() : 100),
                                b.getServiceCategory() != null ? b.getServiceCategory() : "Service",
                                b.getAmount() != null ? b.getAmount() : "₹ 0",
                                uiStatus,
                                b,
                                isEventCompleted
                        ));
                    }
                }

                Platform.runLater(() -> {
                    requestList.clear();
                    requestList.addAll(tempList);
                    updateSummaryCounts();
                    buildTabs(tabsRowContainer);
                    renderRequestCards();
                });
            }).start();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void updateSummaryCounts() {
        int total = requestList.size();
        int newCount = (int) requestList.stream().filter(r -> "New".equalsIgnoreCase(r.status)).count();
        int accepted = (int) requestList.stream().filter(r -> "Accepted".equalsIgnoreCase(r.status) || "Completed".equalsIgnoreCase(r.status)).count();
        int rejected = (int) requestList.stream().filter(r -> "Rejected".equalsIgnoreCase(r.status)).count();

        if (totalReqValLbl != null) totalReqValLbl.setText(String.valueOf(total));
        if (newReqValLbl != null) newReqValLbl.setText(String.valueOf(newCount));
        if (acceptedValLbl != null) acceptedValLbl.setText(String.valueOf(accepted));
        if (rejectedValLbl != null) rejectedValLbl.setText(String.valueOf(rejected));
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Service Requests");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button(" ↻ Refresh");
        FontIcon syncIcon = new FontIcon("fas-sync-alt");
        syncIcon.setIconSize(11);
        syncIcon.setIconColor(Color.web(PRIMARY_BLUE));
        refreshBtn.setGraphic(syncIcon);
        refreshBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadFirestoreRequestsAsync());

        titleRow.getChildren().addAll(title, spacer, refreshBtn);

        Label subtitle = new Label("Review and accept service requests assigned to you by event organizers.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        totalReqValLbl = new Label("0");
        newReqValLbl = new Label("0");
        acceptedValLbl = new Label("0");
        rejectedValLbl = new Label("0");

        row.getChildren().addAll(
                createSummaryCard("Total Requests", totalReqValLbl, "fas-inbox", PRIMARY_BLUE),
                createSummaryCard("New Requests", newReqValLbl, "fas-envelope", WARNING_TEXT),
                createSummaryCard("Accepted / Active", acceptedValLbl, "fas-check-circle", GREEN_TEXT),
                createSummaryCard("Declined", rejectedValLbl, "fas-times-circle", RED_TEXT)
        );
        return row;
    }

    private VBox createSummaryCard(String title, Label valLbl, String iconLit, String colorHex) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(12);
        icon.setIconColor(Color.web(colorHex));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        top.getChildren().addAll(icon, t);

        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        card.getChildren().addAll(top, valLbl);
        return card;
    }

    private VBox createFilterSection() {
        VBox card = createCardContainer();
        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        searchInput = new TextField();
        searchInput.setPromptText("🔍 Search by event name, organizer or service...");
        searchInput.setPrefHeight(36);
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchInput.textProperty().addListener((obs, oldV, newV) -> renderRequestCards());

        filterRow.getChildren().add(searchInput);
        card.getChildren().add(filterRow);
        return card;
    }

    private HBox createTabsRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        buildTabs(row);
        return row;
    }

    private void buildTabs(HBox row) {
        if (row == null) return;
        row.getChildren().clear();

        int allCount = requestList.size();
        long newCount = requestList.stream().filter(r -> "New".equalsIgnoreCase(r.status)).count();
        long acceptedCount = requestList.stream().filter(r -> "Accepted".equalsIgnoreCase(r.status) || "Completed".equalsIgnoreCase(r.status)).count();
        long rejectedCount = requestList.stream().filter(r -> "Rejected".equalsIgnoreCase(r.status)).count();

        String[] tabs = {
                "All " + allCount,
                "New " + newCount,
                "Accepted " + acceptedCount,
                "Rejected " + rejectedCount
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
                buildTabs(row);
                renderRequestCards();
            });

            row.getChildren().add(tabBtn);
        }
    }

    private HBox createMainSplitSection() {
        HBox row = new HBox(20);
        cardsContainer = new VBox(14);
        HBox.setHgrow(cardsContainer, Priority.ALWAYS);

        renderRequestCards();
        row.getChildren().add(cardsContainer);
        return row;
    }

    private void renderRequestCards() {
        if (cardsContainer == null) return;
        cardsContainer.getChildren().clear();

        List<RequestData> filtered = requestList.stream().filter(r -> {
            if ("Accepted".equalsIgnoreCase(activeTabFilter)) {
                if (!"Accepted".equalsIgnoreCase(r.status) && !"Completed".equalsIgnoreCase(r.status)) return false;
            } else if (!"All".equalsIgnoreCase(activeTabFilter) && !r.status.equalsIgnoreCase(activeTabFilter)) {
                return false;
            }

            if (searchInput != null && !searchInput.getText().isBlank()) {
                String q = searchInput.getText().toLowerCase().trim();
                return r.eventName.toLowerCase().contains(q) || r.service.toLowerCase().contains(q) || r.organizer.toLowerCase().contains(q);
            }
            return true;
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = createCardContainer();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            Label emptyLbl = new Label("No service requests found for tab [" + activeTabFilter + "]");
            emptyLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            emptyBox.getChildren().add(emptyLbl);
            cardsContainer.getChildren().add(emptyBox);
            return;
        }

        for (RequestData req : filtered) {
            cardsContainer.getChildren().add(createRequestCard(req));
        }
    }

    // ⭐ Event Completed असल्यास Accept/Reject बटणे लपवणारे लॉजिक
    private VBox createRequestCard(RequestData req) {
        VBox card = createCardContainer();

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox orgBox = new VBox(2);
        Label orgName = new Label("👤 " + req.organizer);
        orgName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label orgPhone = new Label("📞 " + req.phone);
        orgPhone.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        orgBox.getChildren().addAll(orgName, orgPhone);
        orgBox.setPrefWidth(160);

        VBox eventBox = new VBox(2);
        Label eName = new Label(req.eventName);
        eName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label eDetails = new Label("📅 " + req.date + " • 📍 " + req.location + " • 👥 " + req.guests + " Guests");
        eDetails.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        eventBox.getChildren().addAll(eName, eDetails);
        HBox.setHgrow(eventBox, Priority.ALWAYS);

        VBox serviceBox = new VBox(2);
        Label sName = new Label("Service: " + req.service);
        sName.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label budget = new Label(req.budget);
        budget.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        serviceBox.getChildren().addAll(sName, budget);
        serviceBox.setPrefWidth(140);

        Label statusBadge = new Label(req.status);
        if ("Completed".equalsIgnoreCase(req.status)) {
            statusBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10px;");
        } else if ("Accepted".equalsIgnoreCase(req.status)) {
            statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10px;");
        } else if ("New".equalsIgnoreCase(req.status)) {
            statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10px;");
        }

        topRow.getChildren().addAll(orgBox, new Separator(), eventBox, new Separator(), serviceBox, statusBadge);

        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 12; -fx-cursor: hand;");
        detailsBtn.setOnAction(e -> {
            ServiceRequestDetailsV detailsView = new ServiceRequestDetailsV(req.rawBooking);
            LandingPage.landingPageStage.setScene(detailsView.getserviceRequestDetailsScene(LandingPage.landingPageStage, this::backToServiceRequestsV));
        });

        // ⭐ केवळ आणि केवळ जर इव्हेंट पूर्ण झालेला नसेल आणि स्टेटस 'New' असेल तरच Accept/Reject दिसेल!
        boolean canShowActions = "New".equalsIgnoreCase(req.status) && !req.isEventCompleted;

        if (canShowActions) {
            Button acceptBtn = new Button("Accept");
            acceptBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 5 14; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> showInAppAcceptModal(req));

            Button rejectBtn = new Button("Reject");
            rejectBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + RED_TEXT + "; -fx-border-color: " + RED_TEXT + "; -fx-border-radius: 6px; -fx-padding: 5 14; -fx-cursor: hand;");
            rejectBtn.setOnAction(e -> {
                VendorController controller = new VendorController();
                controller.rejectBookingRequest(req.id);
                loadFirestoreRequestsAsync();
            });

            btnRow.getChildren().addAll(detailsBtn, acceptBtn, rejectBtn);
        } else {
            // Completed किंवा Accepted झाल्यावर फक्त View Details बटण राहील
            btnRow.getChildren().add(detailsBtn);
        }

        card.getChildren().addAll(topRow, new Separator(), btnRow);
        return card;
    }

    private void showInAppAcceptModal(RequestData req) {
        modalOverlayPane.getChildren().clear();
        modalOverlayPane.setAlignment(Pos.CENTER);

        VBox card = new VBox(14);
        card.setMaxWidth(460);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.22), 16, 0, 0, 4);");

        StackPane.setAlignment(card, Pos.CENTER);

        HBox head = new HBox(8);
        head.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon("fas-check-circle");
        icon.setIconSize(18);
        icon.setIconColor(Color.web(GREEN_TEXT));
        Label title = new Label("Accept Service Request");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        head.getChildren().addAll(icon, title);

        Label msg = new Label("Do you want to confirm assignment for '" + req.eventName + "'?\n\nAllocated Budget: " + req.budget + "\nThis will sync live to the Organizer's Expense Tracker and Customer's live budget.");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setPadding(new Insets(6, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 16; -fx-font-size: 12px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeInAppModal());

        Button confirmBtn = new Button("Confirm & Accept");
        confirmBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 6 18; -fx-cursor: hand;");
        confirmBtn.setOnAction(e -> {
            VendorController controller = new VendorController();
            controller.acceptBookingRequest(req.id);
            closeInAppModal();
            loadFirestoreRequestsAsync();
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

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }

    public void backToServiceRequestsV() {
        LandingPage.landingPageStage.setScene(getserviceRequestVScene());
    }
}