package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.OrganizerController;
import com.techtrio.controller.VendorController;
import com.techtrio.model.AiEventPlan;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorBooking;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;
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

public class FindVendorsO {

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    private String uid;
    private String idToken;
    private Runnable backAction;

    private FlowPane allVendorsPane;
    private Label resultsCountLabel;
    private Label eventTagLabel;
    private Label breadcrumbLabel;
    private StackPane rootLayer;

    private Event currentEvent;
    private List<VendorProfile> realVendorsList = new ArrayList<>();
    private String selectedCategory = "All Categories";

    public FindVendorsO() {
        this(UserSession.getUid(), UserSession.getIdToken(), "All Categories");
    }

    public FindVendorsO(String uid, String idToken) {
        this(uid, idToken, "All Categories");
    }

    public FindVendorsO(String uid, String idToken, String selectedCategory) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.selectedCategory = (selectedCategory != null && !selectedCategory.isBlank()) ? selectedCategory : "All Categories";
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        this.backAction = backAction;

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        root.setLeft(createCommonOrganizerSidebar("My Events"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent(stage);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        rootLayer.getChildren().add(root);

        loadDynamicEventContextAndVendors(stage);

        return new Scene(rootLayer, 1550, 785);
    }

    private void loadDynamicEventContextAndVendors(Stage stage) {
        String selectedEvtId = AdminContext.getSelectedEventId();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                if (selectedEvtId != null && !selectedEvtId.isBlank()) {
                    AdminController adminController = new AdminController();
                    currentEvent = adminController.getEventById(selectedEvtId);
                }

                VendorProfileService service = new VendorProfileService();
                realVendorsList = service.getAllVendors();

                Platform.runLater(() -> {
                    if (currentEvent != null) {
                        String eName = currentEvent.getEventName() != null ? currentEvent.getEventName() : "Event";
                        if (breadcrumbLabel != null) breadcrumbLabel.setText("My Events  /  " + eName + "  /  Find Vendors");
                        if (eventTagLabel != null) eventTagLabel.setText("● " + eName + " • " + (currentEvent.getCity() != null ? currentEvent.getCity() : "Pune") + " • " + currentEvent.getGuests() + " Guests");
                    }
                    renderVendorCards(stage);
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

        Label bcLink = new Label("← Back to Event Workspace");
        bcLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        bcLink.setOnMouseClicked(e -> handleBack(stage));
        breadcrumbs.getChildren().add(bcLink);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("Assign Verified Vendors");
        pageTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        eventTagLabel = new Label("● Live Workspace Sync");
        eventTagLabel.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16px; -fx-font-size: 12px;");

        titleRow.getChildren().addAll(pageTitle, sp, eventTagLabel);

        VBox categoryFilterCard = createCategoryHeaderCard();
        VBox resultsContainer = createResultsContainer(stage);

        mainBox.getChildren().addAll(breadcrumbs, titleRow, categoryFilterCard, resultsContainer);
        return mainBox;
    }

    private VBox createCategoryHeaderCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        FontIcon filterIcon = new FontIcon("fas-filter");
        filterIcon.setIconSize(13);
        filterIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label catLbl = new Label("Requirement Category: ");
        catLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label categoryTag = new Label(selectedCategory);
        categoryTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 4 14; -fx-background-radius: 16px;");

        row.getChildren().addAll(filterIcon, catLbl, categoryTag);
        card.getChildren().add(row);
        return card;
    }

    private VBox createResultsContainer(Stage stage) {
        VBox box = new VBox(16);

        resultsCountLabel = new Label("Loading Firestore Vendors...");
        resultsCountLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        allVendorsPane = new FlowPane();
        allVendorsPane.setHgap(16);
        allVendorsPane.setVgap(16);

        box.getChildren().addAll(resultsCountLabel, allVendorsPane);
        return box;
    }

    private void renderVendorCards(Stage stage) {
        if (allVendorsPane == null) return;
        allVendorsPane.getChildren().clear();

        List<VendorProfile> filtered = realVendorsList.stream()
                .filter(v -> {
                    if (selectedCategory == null || selectedCategory.isBlank() || "All Categories".equalsIgnoreCase(selectedCategory)) {
                        return true;
                    }
                    String vCat = v.getCategory() != null ? v.getCategory().toLowerCase().trim() : "";
                    String filterCat = selectedCategory.toLowerCase().trim();

                    if (vCat.contains(filterCat) || filterCat.contains(vCat)) return true;
                    if (filterCat.contains("sound") && (vCat.contains("sound") || vCat.contains("audio") || vCat.contains("av"))) return true;
                    return false;
                })
                .collect(Collectors.toList());

        resultsCountLabel.setText(filtered.size() + " Verified Vendors Available for [" + selectedCategory + "]");

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            Label emptyLbl = new Label("No registered vendors found in Firestore for category: " + selectedCategory);
            emptyLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            emptyBox.getChildren().add(emptyLbl);
            allVendorsPane.getChildren().add(emptyBox);
            return;
        }

        for (VendorProfile v : filtered) {
            allVendorsPane.getChildren().add(createVendorCard(stage, v));
        }
    }

    private VBox createVendorCard(Stage stage, VendorProfile v) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setPrefWidth(290);
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");

        String bName = v.getBusinessName() != null && !v.getBusinessName().isBlank() ? v.getBusinessName() : (v.getFullName() != null ? v.getFullName() : v.getName());
        String cat = v.getCategory() != null ? v.getCategory() : selectedCategory;

        Label name = new Label(bName != null ? bName : "Vendor Partner");
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: " + NAVY + ";");

        Label categoryLbl = new Label(cat + " • 📍 " + (v.getAddress() != null ? v.getAddress() : "Pune"));
        categoryLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label contactLbl = new Label("📞 " + (v.getPhone() != null && !v.getPhone().isBlank() ? v.getPhone() : (v.getMobileNo() != null ? v.getMobileNo() : "N/A")));
        contactLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Button assignBtn = new Button("Assign & Send Request");
        FontIcon sendIcon = new FontIcon("fas-paper-plane");
        sendIcon.setIconSize(11);
        sendIcon.setIconColor(Color.WHITE);
        assignBtn.setGraphic(sendIcon);
        assignBtn.setMaxWidth(Double.MAX_VALUE);
        assignBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");

        assignBtn.setOnAction(e -> {
            if (currentEvent == null) {
                showInAppModal("Event Required", "Please select an event before assigning vendors.", "⚠️", "#FEF3C7", PRIMARY_BLUE);
                return;
            }

            // ⭐ VENUE LOCK CHECK (Attractive In-Screen Pop-up)
            String vStatus = currentEvent.getVenueStatus() != null ? currentEvent.getVenueStatus().toUpperCase() : "";
            boolean isVenueConfirmed = "CONFIRMED".equals(vStatus) && currentEvent.getConfirmedVenueName() != null && !currentEvent.getConfirmedVenueName().startsWith("Pending");

            if (!isVenueConfirmed) {
                showInAppModal(
                        "Venue Lock Active",
                        "The Venue is not yet confirmed by the Customer. Please confirm the Venue first before assigning vendors.",
                        "🔒",
                        "#FEF3C7",
                        PRIMARY_BLUE
                );
                return;
            }

            OrganizerController orgController = new OrganizerController();
            OrganizerProfile orgProfile = orgController.getOrganizerProfile(uid);

            String organizerName = (orgProfile != null && orgProfile.getName() != null && !orgProfile.getName().isBlank()) 
                    ? orgProfile.getName() 
                    : (currentEvent.getOrganizerName() != null ? currentEvent.getOrganizerName() : "Organizer");
            
            String orgPhone = (orgProfile != null && orgProfile.getMobileNo() != null) ? orgProfile.getMobileNo() : "";
            String orgEmail = (orgProfile != null && orgProfile.getEmail() != null) ? orgProfile.getEmail() : "";

            String evtName = currentEvent.getEventName() != null ? currentEvent.getEventName() : "Event";
            String evtDate = currentEvent.getEventDate() != null ? currentEvent.getEventDate() : (currentEvent.getStartDate() != null ? currentEvent.getStartDate() : "TBD");
            int evtGuests = currentEvent.getGuests() > 0 ? currentEvent.getGuests() : 100;
            
            String evtLoc = (currentEvent.getConfirmedVenueAddress() != null && !currentEvent.getConfirmedVenueAddress().isBlank())
                    ? currentEvent.getConfirmedVenueAddress()
                    : ((currentEvent.getArea() != null ? currentEvent.getArea() + ", " : "") + (currentEvent.getCity() != null ? currentEvent.getCity() : "Pune"));

            double allocatedAmount = getExactAllocatedAmount(currentEvent, cat);
            String formattedAmount = "₹ " + String.format("%,.0f", allocatedAmount);

            VendorBooking booking = new VendorBooking(
                    "BKG_" + System.currentTimeMillis(),
                    currentEvent.getEventId(),
                    evtName,
                    evtDate,
                    uid,
                    organizerName,
                    v.getVendorId(),
                    bName,
                    cat,
                    formattedAmount,
                    evtGuests,
                    evtLoc
            );

            booking.setCustomerName(organizerName);
            booking.setCustomerPhone(orgPhone);
            booking.setCustomerEmail(orgEmail);

            VendorController vendorController = new VendorController();
            boolean success = vendorController.requestVendorBooking(booking);

            if (success) {
                showInAppModal(
                        "Request Dispatched!",
                        "Vendor [" + bName + "] has been assigned for [" + cat + "] with allocated amount " + formattedAmount + "!\nSynced live with Venue: " + evtLoc,
                        "✓",
                        "#DEF7EC",
                        "#0E9F6E"
                );
            } else {
                showInAppModal(
                        "Dispatch Failed",
                        "Failed to dispatch booking request to Firestore. Please check network connection.",
                        "✕",
                        "#FDE8E8",
                        "#F05252"
                );
            }
        });

        card.getChildren().addAll(name, categoryLbl, contactLbl, new Separator(), assignBtn);
        return card;
    }

    // ✨ इन-स्क्रीन सुंदर व आकर्षक पॉप-अप (No New Window/Stage)
    private void showInAppModal(String title, String message, String iconSymbol, String iconBgHex, String btnColorHex) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        VBox modal = new VBox(16);
        modal.setMaxWidth(420);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPadding(new Insets(26));
        modal.setAlignment(Pos.CENTER);
        modal.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 18px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 18px;"
        );
        modal.setEffect(new DropShadow(25, Color.rgb(0, 0, 0, 0.20)));

        StackPane iconCircle = new StackPane();
        Circle bgCircle = new Circle(28, Color.web(iconBgHex));
        Label ic = new Label(iconSymbol);
        ic.setStyle("-fx-font-size: 22px;");
        iconCircle.getChildren().addAll(bgCircle, ic);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true);
        lblMsg.setAlignment(Pos.CENTER);
        lblMsg.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-text-alignment: center; -fx-line-spacing: 3px;");

        Button btnClose = new Button("Okay, Understood");
        btnClose.setMaxWidth(Double.MAX_VALUE);
        btnClose.setStyle(
                "-fx-background-color: " + btnColorHex + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 18;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );

        btnClose.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconCircle, lblTitle, lblMsg, btnClose);
        overlay.getChildren().add(modal);

        rootLayer.getChildren().add(overlay);
    }

    private double getExactAllocatedAmount(Event event, String category) {
        if (event == null) return 0.0;
        String catLower = category != null ? category.toLowerCase().trim() : "";

        if (event.getBudgetRecommendation() != null && !event.getBudgetRecommendation().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getBudgetRecommendation().entrySet()) {
                if (entry.getKey().toLowerCase().trim().contains(catLower) || catLower.contains(entry.getKey().toLowerCase().trim())) {
                    try { return Double.parseDouble(entry.getValue().toString()); } catch (Exception ignored) {}
                }
            }
        }

        if (event.getAiPlanJson() != null && !event.getAiPlanJson().isBlank()) {
            AiEventPlan plan = AiEventPlan.fromJson(event.getAiPlanJson());
            if (plan != null && plan.getBudgetBreakdown() != null) {
                for (AiEventPlan.BudgetItem bi : plan.getBudgetBreakdown()) {
                    if (bi.getCategory() != null && (bi.getCategory().toLowerCase().trim().contains(catLower) || catLower.contains(bi.getCategory().toLowerCase().trim()))) {
                        double total = 15000;
                        try {
                            String clean = event.getBudget().replaceAll("[^0-9.]", "");
                            if (!clean.isEmpty()) total = Double.parseDouble(clean);
                        } catch (Exception ignored) {}
                        return (bi.getPercentage() / 100.0) * total;
                    }
                }
            }
        }

        double total = 15000.0;
        try {
            String clean = event.getBudget().replaceAll("[^0-9.]", "");
            if (!clean.isEmpty()) total = Double.parseDouble(clean);
        } catch (Exception ignored) {}
        return total * 0.20;
    }

    private VBox createCommonOrganizerSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard":
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
                break;
            case "Event Requests":
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
                break;
            case "My Events":
                handleBack(LandingPage.landingPageStage);
                break;
            case "Schedule":
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
                break;
            case "Notifications":
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
                break;
            case "Profile":
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
                break;
            case "Settings":
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
                break;
            case "Support":
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
                break;
        }
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            ViewEventO view = new ViewEventO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getScene(stage, () -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }
}