package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
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
import java.util.*;

public class Budget {

private String uid;
private String idToken;
private Event event;
private Runnable backAction;

private static final String NAVY = "#061B41";
private static final String ACTIVE_NAV_BLUE = "#2563EB";
private static final String BG = "#F4F7FC";
private static final String TEXT_DARK = "#101828";
private static final String TEXT_MUTED = "#64748B";
private static final String BORDER = "#E2E8F0";
private static final String GREEN_BG = "#DCFCE7";
private static final String GREEN_TEXT = "#16A34A";
private static final String BLUE_BG = "#E0F2FE";
private static final String BLUE_TEXT = "#0369A1";

private Label lblTotalBudget;
private Label lblAllocated;
private Label lblSpent;
private Label lblRemaining;
private VBox tableRowsContainer;

private ListenerRegistration firestoreListener;

public Budget() {
    this(UserSession.getUid(), UserSession.getIdToken(), null);
}

public Budget(String uid, String idToken, Event event) {
    this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
    this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    this.event = event;
}

public Scene getBudgetScene(Runnable backAction) {
    this.backAction = backAction;

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

    root.setLeft(createCommonSidebar("Budget"));

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

    VBox mainContent = createBudgetContent();
    scrollPane.setContent(mainContent);
    root.setCenter(scrollPane);

    if (event != null) {
        loadVendorExpensesAndRender(event);
    }

    initRealtimeBudgetListener();

    return new Scene(root, 1550, 785);
}

private void initRealtimeBudgetListener() {
    if (event == null || event.getEventId() == null) return;

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
                    loadVendorExpensesAndRender(ev);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}

private void loadVendorExpensesAndRender(Event ev) {
    new Thread(() -> {
        List<VendorBooking> bookings = new ArrayList<>();
        try {
            VendorController vendorController = new VendorController();
            if (ev.getEventId() != null) {
                bookings = vendorController.getEventVendorBookings(ev.getEventId());
            }
        } catch (Exception ignored) {}

        final List<VendorBooking> finalBookings = (bookings != null) ? bookings : new ArrayList<>();
        Platform.runLater(() -> renderDynamicBudgetData(ev, finalBookings));
    }).start();
}

private void renderDynamicBudgetData(Event ev, List<VendorBooking> bookings) {
    if (ev == null) return;

    // १. Approved Budget
    double totalApproved = 50000.0;
    try {
        String clean = ev.getBudget() != null ? ev.getBudget().replaceAll("[^0-9.]", "").trim() : "50000";
        if (!clean.isEmpty()) totalApproved = Double.parseDouble(clean);
    } catch (Exception ignored) {}

    if (totalApproved <= 0 && ev.getTotalBudget() > 0) {
        totalApproved = ev.getTotalBudget();
    }

    // २. Organizer चे budgetRecommendation
    Map<String, Double> allocationMap = new LinkedHashMap<>();
    if (ev.getBudgetRecommendation() != null && !ev.getBudgetRecommendation().isEmpty()) {
        for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
            try {
                allocationMap.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
            } catch (Exception ignored) {}
        }
    }

    if (allocationMap.isEmpty()) {
        allocationMap.put("Venue", (double) Math.round(totalApproved * 0.45));
        allocationMap.put("Contingency & Other", (double) Math.round(totalApproved * 0.15));
        allocationMap.put("Decoration", (double) Math.round(totalApproved * 0.40));
    }

    // ३. Allocated मोजणे
    double totalAllocated = 0.0;
    for (double val : allocationMap.values()) {
        totalAllocated += val;
    }

    // ४. Committed / Spent मोजणे
    double totalSpent = 0.0;

    boolean isVenueConfirmed = "CONFIRMED".equalsIgnoreCase(ev.getVenueStatus());
    double venueCost = 0.0;
    if (isVenueConfirmed) {
        try {
            String clean = ev.getConfirmedVenueCost() != null ? ev.getConfirmedVenueCost().replaceAll("[^0-9.]", "").trim() : "0";
            venueCost = Double.parseDouble(clean);
        } catch (Exception ignored) {}
        if (venueCost == 0.0 && allocationMap.containsKey("Venue")) {
            venueCost = allocationMap.get("Venue");
        }
        totalSpent += venueCost;
    }

    if (bookings != null) {
        for (VendorBooking vb : bookings) {
            if (vb == null) continue;
            String st = vb.getStatus() != null ? vb.getStatus().toUpperCase() : "";
            if (!"REJECTED".equals(st) && !"CANCELLED".equals(st)) {
                try {
                    totalSpent += Double.parseDouble(vb.getAmount().replaceAll("[^0-9.]", "").trim());
                } catch (Exception ignored) {}
            }
        }
    }

    // ⭐ कडक अट: केवळ paidAmount > 0 किंवा paymentStatus == 'PAID' असेल तरच पैसे भरले मानले जातील
    String payStatus = ev.getPaymentStatus() != null ? ev.getPaymentStatus().toUpperCase() : "";
    boolean isCustomerActuallyPaid = (ev.getPaidAmount() > 0) || "PAID".equals(payStatus);

    double contingencyAmt = 0.0;
    for (Map.Entry<String, Double> entry : allocationMap.entrySet()) {
        String k = entry.getKey().toLowerCase();
        if (k.contains("contingency") || k.contains("other") || k.contains("misc")) {
            contingencyAmt = entry.getValue();
            break;
        }
    }

    if (isCustomerActuallyPaid && contingencyAmt > 0) {
        totalSpent += contingencyAmt;
    }

    double remaining = Math.max(0.0, totalApproved - totalSpent);

    lblTotalBudget.setText("₹ " + String.format("%,.0f", totalApproved));
    lblAllocated.setText("₹ " + String.format("%,.0f", totalAllocated));
    lblSpent.setText("₹ " + String.format("%,.0f", totalSpent));
    lblRemaining.setText("₹ " + String.format("%,.0f", remaining));

    tableRowsContainer.getChildren().clear();

    String orgName = (ev.getOrganizerName() != null && !ev.getOrganizerName().isBlank()) 
            ? ev.getOrganizerName() : "Organizer";

    // ६. टेबलमधील ओळी
    for (Map.Entry<String, Double> entry : allocationMap.entrySet()) {
        String category = entry.getKey().trim();
        double allocatedAmt = entry.getValue();

        String assignedName = "Not Assigned";
        double spentAmt = 0.0;
        String status = "Allocated";
        boolean isConfirmedOrPaid = false;
        boolean isActuallyPaid = false;

        String catLower = category.toLowerCase();

        // Contingency & Other
        if (catLower.contains("contingency") || catLower.contains("other") || catLower.contains("misc")) {
            assignedName = orgName;
            spentAmt = isCustomerActuallyPaid ? allocatedAmt : 0.0;
            isConfirmedOrPaid = isCustomerActuallyPaid;
            isActuallyPaid = isCustomerActuallyPaid;
            status = isActuallyPaid ? "Paid" : "Allocated";
        }
        // ⭐ Venue: Customer ने प्रत्यक्ष पैसे भरेपर्यंत 'Confirmed', पैसे भरल्यावरच 'Paid'
        else if (catLower.contains("venue") && isVenueConfirmed) {
            assignedName = (ev.getConfirmedVenueName() != null && !ev.getConfirmedVenueName().isBlank()) 
                    ? ev.getConfirmedVenueName() : "Confirmed Venue";
            spentAmt = venueCost;
            isConfirmedOrPaid = true;
            isActuallyPaid = isCustomerActuallyPaid;
            status = isActuallyPaid ? "Paid" : "Confirmed";
        } 
        // Vendors (Decoration इ.)
        else if (bookings != null && !bookings.isEmpty()) {
            for (VendorBooking vb : bookings) {
                if (vb == null) continue;
                String vCat = vb.getServiceCategory() != null ? vb.getServiceCategory().trim().toLowerCase() : "";

                if (vCat.contains(catLower) || catLower.contains(vCat)) {
                    String bSt = vb.getStatus() != null ? vb.getStatus().toUpperCase() : "";
                    if (!"REJECTED".equals(bSt) && !"CANCELLED".equals(bSt)) {
                        assignedName = (vb.getVendorName() != null && !vb.getVendorName().isBlank()) 
                                ? vb.getVendorName() : "Vendor Partner";
                        try { 
                            spentAmt = Double.parseDouble(vb.getAmount().replaceAll("[^0-9.]", "").trim()); 
                        } catch (Exception ignored) {}

                        boolean isAccepted = "CONFIRMED".equals(bSt) || "ACCEPTED".equals(bSt) || "COMPLETED".equals(bSt);
                        isActuallyPaid = "PAID".equals(bSt) || (isCustomerActuallyPaid && isAccepted);
                        isConfirmedOrPaid = isAccepted || isActuallyPaid;

                        // ⭐ Vendor Request असताना Pending, Accept झाल्यावर Confirmed, आणि Customer ने पैसे भरल्यावरच Paid!
                        if (isActuallyPaid) {
                            status = "Paid";
                        } else if (isAccepted) {
                            status = "COMPLETED".equals(bSt) ? "Completed" : "Confirmed";
                        } else {
                            status = "Pending";
                        }
                        break;
                    }
                }
            }
        }

        double remCategory = Math.max(0.0, allocatedAmt - spentAmt);

        GridPane row = new GridPane();
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label catLbl = new Label(category); 
        catLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        
        Label allocLbl = new Label("₹ " + String.format("%,.0f", allocatedAmt)); 
        allocLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        
        Label assignLbl = new Label(assignedName); 
        assignLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (isConfirmedOrPaid ? "#0D3979" : TEXT_MUTED) + "; -fx-font-weight: " + (isConfirmedOrPaid ? "bold;" : "normal;"));
        
        Label spentLbl = new Label("₹ " + String.format("%,.0f", spentAmt)); 
        spentLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        
        Label remLbl = new Label("₹ " + String.format("%,.0f", remCategory)); 
        remLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        Label stBadge = new Label(status);
        if (isActuallyPaid || "Paid".equalsIgnoreCase(status)) {
            stBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
        } else if (isConfirmedOrPaid) {
            stBadge.setStyle("-fx-background-color: " + BLUE_BG + "; -fx-text-fill: " + BLUE_TEXT + "; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
        } else {
            stBadge.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 3 8; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;");
        }

        row.add(catLbl, 0, 0);
        row.add(allocLbl, 1, 0);
        row.add(assignLbl, 2, 0);
        row.add(spentLbl, 3, 0);
        row.add(remLbl, 4, 0);
        row.add(stBadge, 5, 0);

        setGridConstraints(row);
        tableRowsContainer.getChildren().add(row);
    }
}

private VBox createBudgetContent() {
    VBox page = new VBox(16);
    page.setPadding(new Insets(24, 32, 32, 32));

    Hyperlink backLink = new Hyperlink("← Back to My Events");
    backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
    backLink.setOnAction(e -> handleBack());

    VBox titleBox = new VBox(3);
    Label heading = new Label("Budget");
    heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
    Label sub = new Label("View real-time budget allocation and vendor expenses synced live with Organizer.");
    sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
    titleBox.getChildren().addAll(heading, sub);

    HBox cardsRow = new HBox(16);
    lblTotalBudget = new Label("₹ 0");
    lblAllocated = new Label("₹ 0");
    lblSpent = new Label("₹ 0");
    lblRemaining = new Label("₹ 0");

    cardsRow.getChildren().addAll(
            createStatCard("Total Budget", lblTotalBudget, TEXT_DARK, "fas-wallet", "#0D3979", "#E0E7FF"),
            createStatCard("Allocated", lblAllocated, TEXT_DARK, "fas-chart-pie", "#2563EB", "#DBEAFE"),
            createStatCard("Spent (Confirmed)", lblSpent, "#0B4EA2", "fas-receipt", "#16A34A", "#DCFCE7"),
            createStatCard("Remaining", lblRemaining, TEXT_DARK, "fas-piggy-bank", "#D97706", "#FEF3C7")
    );

    VBox tableCard = new VBox(12);
    tableCard.setPadding(new Insets(18));
    tableCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    Label tblTitle = new Label("Budget Allocation & Vendor Sync");
    tblTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

    GridPane tableHeader = new GridPane();
    tableHeader.setPadding(new Insets(10, 14, 10, 14));
    tableHeader.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px;");

    tableHeader.add(createHeaderLabel("Category / Requirement"), 0, 0);
    tableHeader.add(createHeaderLabel("Allocated Amount"), 1, 0);
    tableHeader.add(createHeaderLabel("Assigned Vendor / Venue"), 2, 0);
    tableHeader.add(createHeaderLabel("Spent / Committed"), 3, 0);
    tableHeader.add(createHeaderLabel("Remaining"), 4, 0);
    tableHeader.add(createHeaderLabel("Status"), 5, 0);

    setGridConstraints(tableHeader);

    tableRowsContainer = new VBox(2);
    tableCard.getChildren().addAll(tblTitle, tableHeader, tableRowsContainer);

    HBox banner = new HBox(10);
    banner.setAlignment(Pos.CENTER_LEFT);
    banner.setPadding(new Insets(12, 16, 12, 16));
    banner.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #BFDBFE; -fx-border-radius: 8px; -fx-background-radius: 8px;");

    FontIcon lockIco = new FontIcon("fas-lock");
    lockIco.setIconSize(13);
    lockIco.setIconColor(Color.web("#0D3979"));
    Label bText = new Label("Live budget sync active. Any venue confirmation or vendor assignment is tracked here automatically.");
    bText.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E293B;");
    banner.getChildren().addAll(lockIco, bText);

    page.getChildren().addAll(backLink, titleBox, cardsRow, tableCard, banner);
    return page;
}

private VBox createStatCard(String title, Label valLbl, String colorHex, String iconLit, String iconColor, String iconBg) {
    VBox card = new VBox(6);
    card.setPadding(new Insets(16));
    card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    HBox.setHgrow(card, Priority.ALWAYS);

    HBox topRow = new HBox(8);
    topRow.setAlignment(Pos.CENTER_LEFT);

    Label t = new Label(title);
    t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold;");

    Region sp = new Region();
    HBox.setHgrow(sp, Priority.ALWAYS);

    StackPane iconStack = new StackPane();
    iconStack.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 20px; -fx-padding: 6px;");
    FontIcon icon = new FontIcon(iconLit);
    icon.setIconSize(13);
    icon.setIconColor(Color.web(iconColor));
    iconStack.getChildren().add(icon);

    topRow.getChildren().addAll(t, sp, iconStack);

    valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

    card.getChildren().addAll(topRow, valLbl);
    return card;
}

private Label createHeaderLabel(String text) {
    Label l = new Label(text);
    l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";");
    return l;
}

private void setGridConstraints(GridPane grid) {
    ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(24);
    ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(16);
    ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(20);
    ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(14);
    ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(13);
    ColumnConstraints c6 = new ColumnConstraints(); c6.setPercentWidth(13);
    grid.getColumnConstraints().addAll(c1, c2, c3, c4, c5, c6);
}

private void detachListener() {
    if (firestoreListener != null) {
        firestoreListener.remove();
        firestoreListener = null;
    }
}

private VBox createCommonSidebar(String activeTab) {
    VBox sidebar = new VBox(4);
    sidebar.setPrefWidth(240);
    sidebar.setMinWidth(240);
    sidebar.setMaxWidth(240);
    sidebar.setPadding(new Insets(20, 14, 20, 14));
    sidebar.setStyle("-fx-background-color:" + NAVY + ";");

    HBox brandBox = new HBox(8);
    brandBox.setAlignment(Pos.CENTER_LEFT);
    brandBox.setPadding(new Insets(0, 8, 16, 8));

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
            createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
            createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), () -> {}),
            createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
            createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
            createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
    );

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    sidebar.getChildren().addAll(brandBox, mainNav, sectionLabel, eventNav, spacer);
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
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px;"));
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

private void goVenue() {
    detachListener();
    Venue view = new Venue(uid, idToken, event);
    LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
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
}