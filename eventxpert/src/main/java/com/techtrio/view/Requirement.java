package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
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

import java.util.ArrayList;
import java.util.List;

public class Requirement {

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

    // Dynamic UI References
    private Label lblCountRequested;
    private Label lblCountAssigned;
    private Label lblCountInProgress;
    private Label lblCountCompleted;
    private Label lblDonutTotal;
    private VBox tableRowsContainer;
    private VBox legendContainer;

    private ListenerRegistration eventListener;
    private ListenerRegistration bookingsListener;

    public Requirement() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Requirement(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getRequirementScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Requirements"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createRequirementContent();
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        initRealtimeRequirementListener();

        return new Scene(root, 1550, 785);
    }

    // ⭐ १. इव्हेंट आणि थेट Vendor Bookings चा Real-Time Snapshot Listener

    private void initRealtimeRequirementListener() {
        if (event == null || event.getEventId() == null) return;

        new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                if (db == null) return;

                String eventId = event.getEventId().trim();

                // ⭐ अचूक 'vendorBookings' कलेक्शनवर Live Listener
                if (bookingsListener != null) bookingsListener.remove();
                bookingsListener = db.collection("vendorBookings")
                        .whereEqualTo("eventId", eventId)
                        .addSnapshotListener((snapshots, error) -> {
                            if (error != null || snapshots == null) return;

                            List<VendorBooking> liveBookings = new ArrayList<>();
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                VendorBooking vb = doc.toObject(VendorBooking.class);
                                if (vb != null) {
                                    if (vb.getBookingId() == null || vb.getBookingId().isBlank()) {
                                        vb.setBookingId(doc.getId());
                                    }
                                    liveBookings.add(vb);
                                }
                            }

                            List<String> reqs = new ArrayList<>();
                            if (event.getRequirements() != null && !event.getRequirements().isBlank()) {
                                for (String r : event.getRequirements().split(",\\s*")) {
                                    if (!r.trim().isEmpty()) reqs.add(r.trim());
                                }
                            }
                            if (reqs.isEmpty()) {
                                reqs.add("Decoration");
                                reqs.add("Sound System");
                                reqs.add("Catering");
                                reqs.add("Photography");
                                reqs.add("Anchor");
                                reqs.add("Cake & Dessert Services");
                                reqs.add("Invitation & Printing");
                                reqs.add("Rental Equipment");
                            }

                            Platform.runLater(() -> renderDynamicRequirementView(reqs, liveBookings));
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

	private void renderDynamicRequirementView(List<String> reqs, List<VendorBooking> bookings) {
        int countReq = 0;
        int countAssigned = 0;
        int countInProgress = 0;
        int countCompleted = 0;

        tableRowsContainer.getChildren().clear();

        double totalEvtBudget = 20000.0;
        try {
            if (event.getBudget() != null) {
                totalEvtBudget = Double.parseDouble(event.getBudget().replaceAll("[^0-9.]", "").trim());
            }
        } catch (Exception ignored) {}

        double costPerService = Math.round(totalEvtBudget / Math.max(1, reqs.size()));

        for (String service : reqs) {
            String status = "Requested";
            String assignedTo = "—";
            String priority = "High";
            double cost = costPerService;
            String iconLit = "fas-concierge-bell";

            String sLower = service.toLowerCase();
            if (sLower.contains("decor")) { iconLit = "fas-paint-brush"; priority = "High"; }
            else if (sLower.contains("cater") || sLower.contains("food")) { iconLit = "fas-utensils"; priority = "High"; }
            else if (sLower.contains("photo") || sLower.contains("video")) { iconLit = "fas-camera"; priority = "Medium"; }
            else if (sLower.contains("sound") || sLower.contains("music") || sLower.contains("dj")) { iconLit = "fas-volume-up"; priority = "Medium"; }
            else if (sLower.contains("cake")) { iconLit = "fas-birthday-cake"; priority = "Medium"; }
            else if (sLower.contains("anchor")) { iconLit = "fas-microphone"; priority = "Medium"; }
            else if (sLower.contains("rent")) { iconLit = "fas-chair"; priority = "Low"; }

            // ⭐ अचूक मॅच: Category किंवा Service नाव जुळवून लाइव्ह स्टेटस घेणे
            if (bookings != null) {
                for (VendorBooking vb : bookings) {
                    if (vb == null) continue;
                    boolean matchesService = (vb.getServiceCategory() != null && vb.getServiceCategory().trim().equalsIgnoreCase(service.trim()))
                            || (vb.getServiceCategory() != null && vb.getServiceCategory().trim().equalsIgnoreCase(service.trim()));

                    if (matchesService) {
                        String bSt = vb.getStatus() != null ? vb.getStatus().trim().toUpperCase() : "";
                        assignedTo = (vb.getVendorName() != null && !vb.getVendorName().isBlank()) ? vb.getVendorName() : "Vendor Partner";
                        try {
                            if (vb.getAmount() != null) {
                                cost = Double.parseDouble(vb.getAmount().replaceAll("[^0-9.]", "").trim());
                            }
                        } catch (Exception ignored) {}

                        if ("COMPLETED".equals(bSt)) {
                            status = "Completed";
                        } else if ("IN_PROGRESS".equals(bSt) || "IN PROGRESS".equals(bSt)) {
                            status = "In Progress";
                        } else {
                            status = "Assigned";
                        }
                        break;
                    }
                }
            }

            if ("Requested".equals(status)) countReq++;
            else if ("Assigned".equals(status)) countAssigned++;
            else if ("In Progress".equals(status)) countInProgress++;
            else if ("Completed".equals(status)) countCompleted++;

            tableRowsContainer.getChildren().add(createServiceRow(service, status, assignedTo, priority, "₹ " + String.format("%,.0f", cost), iconLit));
        }

        int totalCount = reqs.size();
        lblCountRequested.setText(String.valueOf(countReq));
        lblCountAssigned.setText(String.valueOf(countAssigned));
        lblCountInProgress.setText(String.valueOf(countInProgress));
        lblCountCompleted.setText(String.valueOf(countCompleted));
        lblDonutTotal.setText(String.valueOf(totalCount));

        legendContainer.getChildren().clear();
        legendContainer.getChildren().addAll(
                createDonutLegendItem("Requested", countReq + " (" + calculatePercent(countReq, totalCount) + "%)", "#8B5CF6"),
                createDonutLegendItem("Assigned", countAssigned + " (" + calculatePercent(countAssigned, totalCount) + "%)", "#0284C7"),
                createDonutLegendItem("In Progress", countInProgress + " (" + calculatePercent(countInProgress, totalCount) + "%)", "#F59E0B"),
                createDonutLegendItem("Completed", countCompleted + " (" + calculatePercent(countCompleted, totalCount) + "%)", "#16A34A")
        );
    }

    private int calculatePercent(int part, int total) {
        if (total == 0) return 0;
        return (int) Math.round(((double) part / total) * 100);
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(16, 12, 16, 12));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 8, 8, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(18);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        HBox userBox = new HBox(8);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(4, 8, 10, 8));

        Circle userAvatar = new Circle(14, Color.web("#DBEAFE"));
        VBox userDetails = new VBox(0);
        Label userName = new Label("Customer");
        userName.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label userEmail = new Label(UserSession.getUserEmail() != null ? UserSession.getUserEmail() : "user@email.com");
        userEmail.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 9px;");
        userDetails.getChildren().addAll(userName, userEmail);
        userBox.getChildren().addAll(userAvatar, userDetails);

        VBox mainNav = new VBox(2);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 8 3 8;");

        VBox eventNav = new VBox(2);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), this::goOverview),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), () -> {})
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, userBox, mainNav, sectionLabel, eventNav, spacer);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(32);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 11px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 6px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 11px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createRequirementContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(2);
        Label heading = new Label("Requirements");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Manage all your event requirements and live vendor service status");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        HBox kpiRow = new HBox(16);
        lblCountRequested = new Label("0");
        lblCountAssigned = new Label("0");
        lblCountInProgress = new Label("0");
        lblCountCompleted = new Label("0");

        kpiRow.getChildren().addAll(
                createReqKpiPill(lblCountRequested, "Requested", "fas-lightbulb", "#8B5CF6"),
                createReqKpiPill(lblCountAssigned, "Assigned", "fas-user-check", "#0284C7"),
                createReqKpiPill(lblCountInProgress, "In Progress", "fas-clock", "#F59E0B"),
                createReqKpiPill(lblCountCompleted, "Completed", "fas-check-circle", "#16A34A")
        );

        HBox bodyRow = new HBox(20);

        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        Label tblTitle = new Label("Requested Services & Assigned Vendors");
        tblTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        VBox rows = new VBox(6);
        rows.setPadding(new Insets(8, 0, 0, 0));

        HBox tHead = new HBox(10);
        tHead.setPadding(new Insets(6, 12, 6, 12));
        tHead.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px;");
        tHead.getChildren().addAll(
                createTableCell("Service", 160, true),
                createTableCell("Status", 110, true),
                createTableCell("Assigned To", 160, true),
                createTableCell("Priority", 90, true),
                createTableCell("Estimated Cost", 120, true)
        );

        tableRowsContainer = new VBox(2);
        rows.getChildren().addAll(tHead, tableRowsContainer);
        tableCard.getChildren().addAll(tblTitle, rows);

        VBox donutCard = new VBox(12);
        donutCard.setPadding(new Insets(16));
        donutCard.setPrefWidth(380);
        donutCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label donutTitle = new Label("Requirement Status Overview");
        donutTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox donutBody = new HBox(16);
        donutBody.setAlignment(Pos.CENTER_LEFT);

        StackPane donutHolder = new StackPane();
        Circle outC = new Circle(44, Color.web("#EFF6FF"));
        Circle inC = new Circle(30, Color.WHITE);
        VBox centerTxt = new VBox(0);
        centerTxt.setAlignment(Pos.CENTER);
        lblDonutTotal = new Label("0");
        lblDonutTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label totL = new Label("Total");
        totL.setStyle("-fx-font-size: 9px; -fx-text-fill: " + TEXT_MUTED + ";");
        centerTxt.getChildren().addAll(lblDonutTotal, totL);
        donutHolder.getChildren().addAll(outC, inC, centerTxt);

        legendContainer = new VBox(6);

        donutBody.getChildren().addAll(donutHolder, legendContainer);
        donutCard.getChildren().addAll(donutTitle, donutBody);

        bodyRow.getChildren().addAll(tableCard, donutCard);

        page.getChildren().addAll(backLink, titleBox, kpiRow, bodyRow);
        return page;
    }

    private HBox createReqKpiPill(Label countLbl, String label, String iconLit, String colorHex) {
        HBox pill = new HBox(12);
        pill.setAlignment(Pos.CENTER_LEFT);
        pill.setPadding(new Insets(14));
        pill.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(pill, Priority.ALWAYS);

        StackPane circle = new StackPane();
        Circle c = new Circle(18, Color.web(colorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(14);
        icon.setIconColor(Color.web(colorHex));
        circle.getChildren().addAll(c, icon);

        VBox texts = new VBox(1);
        countLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label lLbl = new Label(label);
        lLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        texts.getChildren().addAll(countLbl, lLbl);

        pill.getChildren().addAll(circle, texts);
        return pill;
    }

    private Label createTableCell(String text, double width, boolean isHeader) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setStyle(isHeader ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";" : "-fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        return lbl;
    }

    private HBox createServiceRow(String service, String status, String assignedTo, String priority, String estCost, String iconLit) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        HBox svcBox = new HBox(8);
        svcBox.setPrefWidth(160);
        svcBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon ico = new FontIcon(iconLit);
        ico.setIconSize(13);
        ico.setIconColor(Color.web(PRIMARY_BLUE));
        Label svcLabel = new Label(service);
        svcLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        svcBox.getChildren().addAll(ico, svcLabel);

        Label st = new Label(status);
        st.setPrefWidth(110);
        if ("Completed".equalsIgnoreCase(status)) st.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 11px;");
        else if ("In Progress".equalsIgnoreCase(status)) st.setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 11px;");
        else if ("Assigned".equalsIgnoreCase(status)) st.setStyle("-fx-text-fill: #0284C7; -fx-font-weight: bold; -fx-font-size: 11px;");
        else st.setStyle("-fx-text-fill: #8B5CF6; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label asg = createTableCell(assignedTo, 160, false);

        Label pri = new Label(priority);
        pri.setPrefWidth(90);
        pri.setStyle("High".equals(priority) ? "-fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-font-weight: bold;" : ("Medium".equals(priority) ? "-fx-text-fill: #D97706; -fx-font-size: 11px; -fx-font-weight: bold;" : "-fx-text-fill: #16A34A; -fx-font-size: 11px; -fx-font-weight: bold;"));

        Label cost = createTableCell(estCost, 120, false);
        cost.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");

        row.getChildren().addAll(svcBox, st, asg, pri, cost);
        return row;
    }

    private HBox createDonutLegendItem(String title, String val, String colorHex) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, Color.web(colorHex));
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";");
        row.getChildren().addAll(dot, t, sp, v);
        return row;
    }

    private void detachListeners() {
        if (eventListener != null) {
            eventListener.remove();
            eventListener = null;
        }
        if (bookingsListener != null) {
            bookingsListener.remove();
            bookingsListener = null;
        }
    }

    private void handleBack() {
        detachListeners();
        if (backAction != null) backAction.run();
        else goOverview();
    }
    private void goDashboard() {
        detachListeners();
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }
    private void goCreateEvent() {
        detachListeners();
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }
    private void goOverview() {
        detachListeners();
        MyEventDetails view = new MyEventDetails(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getMyEventDetailsScene(this::handleBack));
    }
    private void goVenue() {
        detachListeners();
        Venue view = new Venue(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
    }
    private void goBudget() {
        detachListeners();
        Budget view = new Budget(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getBudgetScene(this::handleBack));
    }
    private void goGuest() {
        detachListeners();
        Guest view = new Guest(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getGuestScene(this::handleBack));
    }
    private void goPayment() {
        detachListeners();
        Payment view = new Payment(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getPaymentScene(this::handleBack));
    }
}