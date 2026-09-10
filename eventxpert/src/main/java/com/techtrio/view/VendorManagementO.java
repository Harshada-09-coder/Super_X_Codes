package com.techtrio.view;

import java.util.List;

import com.techtrio.context.AdminContext;
import com.techtrio.model.Event;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.EventService;
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

public class VendorManagementO {

    private String uid;
    private String idToken;
    private Runnable backAction;

    // COLOR PALETTE CONSTANTS
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // Status Colors
    private static final String GREEN_TEXT = "#137333";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String RED_BG = "#FCE8E6";
    private static final String RED_TEXT = "#C5221F";
    private static final String PURPLE_BG = "#F3E8FF";
    private static final String PURPLE_TEXT = "#6B21A8";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";

    // Dynamic UI Metric Labels
    private Label totalVendorsValLbl;
    private Label activeVendorsValLbl;
    private Label inactiveVendorsValLbl;
    private Label newVendorsValLbl;
    private Label eventBadge;
    private HBox venueLockBanner;
    private Label lblVenueLockText;

    // Dynamic Category Count Labels
    private Label decorCountLbl;
    private Label soundCountLbl;
    private Label photoCountLbl;
    private Label rentalCountLbl;
    private Label invitationCountLbl;
    private Label cateringCountLbl;
    private Label anchorCountLbl;
    private Label cakeCountLbl;

    private boolean isVenueConfirmed = true;
    private Event currentEvent;
    private StackPane rootLayer;

    public VendorManagementO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public VendorManagementO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getvendorManagementOScene(Runnable callBackAction) {
        this.backAction = callBackAction;

        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        root.setLeft(createCommonOrganizerSidebar("My Events"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 30, 30, 30));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Breadcrumbs & Navigation
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);
        Label bc1 = new Label("← Back to Event Planning");
        bc1.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> {
            if (callBackAction != null) callBackAction.run();
            else {
                ViewEventO view = new ViewEventO(uid, idToken);
                LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, null));
            }
        });
        breadcrumbs.getChildren().add(bc1);

        // Header Section
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Vendor Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Find, compare and manage trusted service vendors for your event.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        eventBadge = new Label("● All Vendors (Directory Mode)");
        eventBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16px; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, eventBadge);

        // ⭐ Venue Lock Notification Banner
        venueLockBanner = new HBox(10);
        venueLockBanner.setAlignment(Pos.CENTER_LEFT);
        venueLockBanner.setPadding(new Insets(10, 16, 10, 16));
        venueLockBanner.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-border-color: #FCD34D; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        lblVenueLockText = new Label("🔒 Vendor Assignment Locked: Please confirm the Event Venue with the Customer first before assigning service vendors.");
        lblVenueLockText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + WARNING_TEXT + ";");
        venueLockBanner.getChildren().add(lblVenueLockText);
        venueLockBanner.setVisible(false);
        venueLockBanner.setManaged(false);

        // Top Summary KPI Cards Row (4 Dynamic Cards)
        HBox summaryRow = createSummaryKpiRow();

        // Search Filter Bar Card
        VBox filterCard = createFilterBarCard();

        // Main Section Title
        Label servicesTitle = new Label("Vendor Services");
        servicesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        // 8 Dynamic Vendor Services Cards Grid (2x4)
        GridPane servicesGrid = createVendorServicesGrid(callBackAction);

        content.getChildren().addAll(breadcrumbs, headerRow, venueLockBanner, summaryRow, filterCard, servicesTitle, servicesGrid);
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        rootLayer.getChildren().add(root);

        loadDynamicVendorDataFromFirestoreAsync();
        loadDynamicEventContextBadgeAsync(servicesGrid);

        return new Scene(rootLayer, 1550, 785);
    }

    private void loadDynamicEventContextBadgeAsync(GridPane servicesGrid) {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId != null && !selectedEvtId.isBlank() && !"EVT_DEFAULT".equalsIgnoreCase(selectedEvtId)) {
            Task<Event> task = new Task<>() {
                @Override
                protected Event call() {
                    EventService eventService = new EventService();
                    return eventService.getEventById(selectedEvtId);
                }
            };
            task.setOnSucceeded(e -> {
                Event ev = task.getValue();
                currentEvent = ev;
                if (ev != null) {
                    Platform.runLater(() -> {
                        if (eventBadge != null) {
                            eventBadge.setText("● " + ev.getEventName() + " (In Planning)");
                        }

                        String vStatus = ev.getVenueStatus() != null ? ev.getVenueStatus().toUpperCase() : "";
                        isVenueConfirmed = "CONFIRMED".equals(vStatus) && ev.getConfirmedVenueName() != null && !ev.getConfirmedVenueName().startsWith("Pending");

                        if (!isVenueConfirmed) {
                            venueLockBanner.setVisible(true);
                            venueLockBanner.setManaged(true);
                        } else {
                            venueLockBanner.setVisible(false);
                            venueLockBanner.setManaged(false);
                        }
                    });
                }
            });
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
    }

    private void loadDynamicVendorDataFromFirestoreAsync() {
        Task<List<VendorProfile>> task = new Task<>() {
            @Override
            protected List<VendorProfile> call() {
                VendorProfileService service = new VendorProfileService();
                return service.getAllVendors();
            }
        };

        task.setOnSucceeded(evt -> {
            List<VendorProfile> vendors = task.getValue();
            Platform.runLater(() -> {
                if (vendors == null || vendors.isEmpty()) {
                    setAllCountsToZero();
                    return;
                }

                int totalCount = vendors.size();
                int activeCount = 0;
                int inactiveCount = 0;

                int decorCount = 0;
                int soundCount = 0;
                int photoCount = 0;
                int rentalCount = 0;
                int invitationCount = 0;
                int cateringCount = 0;
                int anchorCount = 0;
                int cakeCount = 0;

                for (VendorProfile v : vendors) {
                    String status = v.getOnboardingStatus() != null ? v.getOnboardingStatus().toUpperCase() : "NEW";
                    if ("ACTIVE".equals(status) || "EXISTING".equals(status) || v.isProfileCompleted() || v.isHasProvidedService()) {
                        activeCount++;
                    } else {
                        inactiveCount++;
                    }

                    String cat = v.getCategory() != null ? v.getCategory().toLowerCase().trim() : "";

                    if (cat.contains("decor")) decorCount++;
                    if (cat.contains("sound") || cat.contains("audio") || cat.contains("av")) soundCount++;
                    if (cat.contains("photo") || cat.contains("video")) photoCount++;
                    if (cat.contains("rental") || cat.contains("equipment")) rentalCount++;
                    if (cat.contains("invitation") || cat.contains("print")) invitationCount++;
                    if (cat.contains("cater")) cateringCount++;
                    if (cat.contains("anchor") || cat.contains("mc")) anchorCount++;
                    if (cat.contains("cake") || cat.contains("dessert")) cakeCount++;
                }

                if (totalVendorsValLbl != null) totalVendorsValLbl.setText(String.valueOf(totalCount));
                if (activeVendorsValLbl != null) activeVendorsValLbl.setText(String.valueOf(activeCount));
                if (inactiveVendorsValLbl != null) inactiveVendorsValLbl.setText(String.valueOf(inactiveCount));
                if (newVendorsValLbl != null) newVendorsValLbl.setText(String.valueOf(inactiveCount));

                if (decorCountLbl != null) decorCountLbl.setText("Count: " + decorCount + " Vendors");
                if (soundCountLbl != null) soundCountLbl.setText("Count: " + soundCount + " Vendors");
                if (photoCountLbl != null) photoCountLbl.setText("Count: " + photoCount + " Vendors");
                if (rentalCountLbl != null) rentalCountLbl.setText("Count: " + rentalCount + " Vendors");
                if (invitationCountLbl != null) invitationCountLbl.setText("Count: " + invitationCount + " Vendors");
                if (cateringCountLbl != null) cateringCountLbl.setText("Count: " + cateringCount + " Vendors");
                if (anchorCountLbl != null) anchorCountLbl.setText("Count: " + anchorCount + " Vendors");
                if (cakeCountLbl != null) cakeCountLbl.setText("Count: " + cakeCount + " Vendors");
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void setAllCountsToZero() {
        if (totalVendorsValLbl != null) totalVendorsValLbl.setText("0");
        if (activeVendorsValLbl != null) activeVendorsValLbl.setText("0");
        if (inactiveVendorsValLbl != null) inactiveVendorsValLbl.setText("0");
        if (newVendorsValLbl != null) newVendorsValLbl.setText("0");

        if (decorCountLbl != null) decorCountLbl.setText("Count: 0 Vendors");
        if (soundCountLbl != null) soundCountLbl.setText("Count: 0 Vendors");
        if (photoCountLbl != null) photoCountLbl.setText("Count: 0 Vendors");
        if (rentalCountLbl != null) rentalCountLbl.setText("Count: 0 Vendors");
        if (invitationCountLbl != null) invitationCountLbl.setText("Count: 0 Vendors");
        if (cateringCountLbl != null) cateringCountLbl.setText("Count: 0 Vendors");
        if (anchorCountLbl != null) anchorCountLbl.setText("Count: 0 Vendors");
        if (cakeCountLbl != null) cakeCountLbl.setText("Count: 0 Vendors");
    }

    private HBox createSummaryKpiRow() {
        HBox row = new HBox(16);
        totalVendorsValLbl = new Label("0");
        activeVendorsValLbl = new Label("0");
        inactiveVendorsValLbl = new Label("0");
        newVendorsValLbl = new Label("0");

        row.getChildren().addAll(
                createKpiCard("TOTAL VENDORS", totalVendorsValLbl, "All registered vendors", "👥", LIGHT_BLUE, PRIMARY_BLUE),
                createKpiCard("ACTIVE VENDORS", activeVendorsValLbl, "Currently active vendors", "✓", GREEN_BG, GREEN_TEXT),
                createKpiCard("INACTIVE VENDORS", inactiveVendorsValLbl, "Currently inactive vendors", "👤", RED_BG, RED_TEXT),
                createKpiCard("NEW THIS MONTH", newVendorsValLbl, "New vendors added", "⭐", PURPLE_BG, PURPLE_TEXT)
        );
        return row;
    }

    private VBox createKpiCard(String label, Label valLbl, String subtext, String iconStr, String bgHex, String colorHex) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane iconCircle = new StackPane();
        Circle c = new Circle(16, Color.web(bgHex));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 12px; -fx-text-fill: " + colorHex + ";");
        iconCircle.getChildren().addAll(c, ic);

        topRow.getChildren().addAll(lbl, spacer, iconCircle);

        valLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subLbl = new Label(subtext);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(topRow, valLbl, subLbl);
        return card;
    }

    private VBox createFilterBarCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 12, 6, 12));
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search vendors or services...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchInput);

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> searchInput.clear());

        filterRow.getChildren().addAll(searchBox, clearBtn);
        card.getChildren().add(filterRow);
        return card;
    }

    private GridPane createVendorServicesGrid(Runnable callBackAction) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        decorCountLbl = new Label("Count: 0 Vendors");
        soundCountLbl = new Label("Count: 0 Vendors");
        photoCountLbl = new Label("Count: 0 Vendors");
        rentalCountLbl = new Label("Count: 0 Vendors");
        invitationCountLbl = new Label("Count: 0 Vendors");
        cateringCountLbl = new Label("Count: 0 Vendors");
        anchorCountLbl = new Label("Count: 0 Vendors");
        cakeCountLbl = new Label("Count: 0 Vendors");

        grid.add(createServiceCard("🎨", "Decoration", "Decoration", decorCountLbl, callBackAction), 0, 0);
        grid.add(createServiceCard("🔊", "Sound System", "Sound System", soundCountLbl, callBackAction), 1, 0);
        grid.add(createServiceCard("📷", "Photography & Videography", "Photography & Videography", photoCountLbl, callBackAction), 2, 0);
        grid.add(createServiceCard("🎪", "Rental Equipment", "Rental Equipment", rentalCountLbl, callBackAction), 3, 0);

        grid.add(createServiceCard("✉️", "Invitation & Printing", "Invitation & Printing", invitationCountLbl, callBackAction), 0, 1);
        grid.add(createServiceCard("🍽", "Catering", "Catering", cateringCountLbl, callBackAction), 1, 1);
        grid.add(createServiceCard("🎤", "Anchor", "Anchor", anchorCountLbl, callBackAction), 2, 1);
        grid.add(createServiceCard("🍰", "Cake and Dessert Services", "Cake and Dessert Services", cakeCountLbl, callBackAction), 3, 1);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }

        return grid;
    }

    private VBox createServiceCard(String iconStr, String categoryName, String categoryFilterKey, Label countLbl, Runnable callBackAction) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Circle c = new Circle(20, Color.web(LIGHT_BLUE));
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 16px;");
        iconBox.getChildren().addAll(c, ic);

        VBox titleBox = new VBox(2);
        Label nameLbl = new Label(categoryName);
        nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label statusLbl = new Label("Status: Active");
        statusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold;");

        countLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(nameLbl, statusLbl, countLbl);
        topRow.getChildren().addAll(iconBox, titleBox);

        Button findBtn = new Button("Find Vendors");
        findBtn.setMaxWidth(Double.MAX_VALUE);
        findBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8; -fx-background-radius: 6px; -fx-cursor: hand;");

        findBtn.setOnAction(e -> {
            // ⭐ Attractive Pop-up Trigger (नवीन Stage उघडणार नाही)
            if (!isVenueConfirmed && currentEvent != null) {
                showAttractiveLockModal(
                        "Venue Not Confirmed Yet",
                        "Please confirm the Event Venue with the Customer first before proceeding to assign service vendors for this event."
                );
                return;
            }

            FindVendorsO findView = new FindVendorsO(uid, idToken, categoryFilterKey);
            LandingPage.landingPageStage.setScene(findView.getScene(LandingPage.landingPageStage, () -> {
                LandingPage.landingPageStage.setScene(getvendorManagementOScene(callBackAction));
            }));
        });

        card.getChildren().addAll(topRow, findBtn);
        return card;
    }

    // ✨ इन-स्क्रीन सुंदर व आकर्षक पॉप-अप (No New Stage Window)
    private void showAttractiveLockModal(String title, String message) {
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

        StackPane lockCircle = new StackPane();
        Circle bgCircle = new Circle(28, Color.web("#FEF3C7"));
        Label lockIcon = new Label("🔒");
        lockIcon.setStyle("-fx-font-size: 24px;");
        lockCircle.getChildren().addAll(bgCircle, lockIcon);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label lblMsg = new Label(message);
        lblMsg.setWrapText(true);
        lblMsg.setAlignment(Pos.CENTER);
        lblMsg.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-text-alignment: center; -fx-line-spacing: 3px;");

        Button btnClose = new Button("Understood, I'll Confirm First");
        btnClose.setMaxWidth(Double.MAX_VALUE);
        btnClose.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 18;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );

        btnClose.setOnMouseEntered(e -> btnClose.setStyle("-fx-background-color: " + BRIGHT_BLUE + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8px; -fx-cursor: hand;"));
        btnClose.setOnMouseExited(e -> btnClose.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8px;"));
        btnClose.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(lockCircle, lblTitle, lblMsg, btnClose);
        overlay.getChildren().add(modal);

        rootLayer.getChildren().add(overlay);
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