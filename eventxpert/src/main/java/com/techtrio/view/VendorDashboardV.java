package com.techtrio.view;

import java.util.List;

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
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class VendorDashboardV {

    private Scene vendorDashboardScene;
    private String uid;
    private String idToken;

    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
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
    private static final String ORANGE_BTN = "#EA580C";

    private VBox requestsContainerBox;
    private Label newReqValLbl;
    private Label upcomingValLbl;
    private Label totalEarningsValLbl;
    private Label pendingPaymentsValLbl;
    private GridPane upcomingTableGrid;

    public VendorDashboardV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public VendorDashboardV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Vendor");
    }

    public Scene getvendorDashboardScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Common Sidebar (Add service button removed)
        root.setLeft(createCommonVendorSidebar("Dashboard"));

        // Main Content ScrollPane (TopBar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createTopHeroCardsRow(),
                createSummaryCardsRow(),
                createMainSplitSection(),
                createServicesPreviewSection()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadVendorRequestsFromFirestoreAsync();

        vendorDashboardScene = new Scene(root, 1550, 785);
        return vendorDashboardScene;
    }

    public void backToVendorDashboard() {
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(getvendorDashboardScene());
        }
    }

    private void loadVendorRequestsFromFirestoreAsync() {
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
            Platform.runLater(() -> {
                if (requestsContainerBox != null) requestsContainerBox.getChildren().clear();
                if (upcomingTableGrid != null) {
                    upcomingTableGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
                }

                int pendingCount = 0;
                int confirmedCount = 0;
                double totalEarnings = 0.0;
                double pendingPayments = 0.0;
                int tableRow = 1;

                if (bookings != null) {
                    for (VendorBooking b : bookings) {
                        String st = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";
                        
                        double amt = 0.0;
                        try {
                            String clean = b.getAmount() != null ? b.getAmount().replaceAll("[^0-9.]", "") : "0";
                            if (!clean.isEmpty()) amt = Double.parseDouble(clean);
                        } catch (Exception ignored) {}

                        if ("PENDING".equals(st) || "NEW".equals(st)) {
                            pendingCount++;
                            pendingPayments += amt;
                            if (requestsContainerBox != null) {
                                requestsContainerBox.getChildren().add(createRequestBox(b));
                            }
                        } else if ("CONFIRMED".equals(st) || "ACCEPTED".equals(st) || "PAID".equals(st)) {
                            confirmedCount++;
                            totalEarnings += amt;
                            if (upcomingTableGrid != null && tableRow <= 5) {
                                addTableRow(upcomingTableGrid, tableRow++, b.getEventName(), b.getEventDate(), b.getServiceCategory(), b.getAmount(), "Confirmed");
                            }
                        }
                    }
                }

                if (newReqValLbl != null) newReqValLbl.setText(String.valueOf(pendingCount));
                if (upcomingValLbl != null) upcomingValLbl.setText(String.valueOf(confirmedCount));
                if (totalEarningsValLbl != null) totalEarningsValLbl.setText("₹ " + String.format("%,.0f", totalEarnings));
                if (pendingPaymentsValLbl != null) pendingPaymentsValLbl.setText("₹ " + String.format("%,.0f", pendingPayments));

                if (requestsContainerBox != null && requestsContainerBox.getChildren().isEmpty()) {
                    Label empty = new Label("No new service requests currently in Firestore.");
                    empty.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12;");
                    requestsContainerBox.getChildren().add(empty);
                }

                if (upcomingTableGrid != null && tableRow == 1) {
                    addTableRow(upcomingTableGrid, 1, "No upcoming bookings yet", "-", "-", "-", "Pending");
                }
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createRequestBox(VendorBooking b) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        HBox top = new HBox();
        Label t = new Label(b.getEventName() != null ? b.getEventName() : "Event");
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label p = new Label(b.getAmount() != null ? b.getAmount() : "₹ 0");
        p.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        top.getChildren().addAll(t, sp, p);

        Label s = new Label("Category: " + (b.getServiceCategory() != null ? b.getServiceCategory() : "General"));
        s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label d = new Label("Date: " + (b.getEventDate() != null ? b.getEventDate() : "Upcoming") + " • Guests: " + b.getGuests());
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox btns = new HBox(8);
        Button acceptBtn = new Button("Accept");
        acceptBtn.setStyle("-fx-background-color: " + GREEN_TEXT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        acceptBtn.setOnAction(e -> {
            VendorController controller = new VendorController();
            boolean success = controller.acceptBookingRequest(b.getBookingId());
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking Request Accepted & Synced with Organizer Budget!");
                alert.showAndWait();
                loadVendorRequestsFromFirestoreAsync();
            }
        });

        Button rejectBtn = new Button("Reject");
        rejectBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_MAIN + "; -fx-border-color: " + BORDER_COLOR + "; -fx-font-size: 11px; -fx-padding: 5 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        rejectBtn.setOnAction(e -> {
            VendorController controller = new VendorController();
            controller.rejectBookingRequest(b.getBookingId());
            loadVendorRequestsFromFirestoreAsync();
        });

        btns.getChildren().addAll(acceptBtn, rejectBtn);
        box.getChildren().addAll(top, s, d, btns);
        return box;
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        Label greeting = new Label("Vendor Workspace 👋");
        greeting.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Here's what's happening with your bookings, service requests and earnings.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(greeting, subtitle);
        return header;
    }

    private HBox createTopHeroCardsRow() {
        HBox row = new HBox(16);

        VBox nextBookingCard = createCardContainer();
        HBox.setHgrow(nextBookingCard, Priority.ALWAYS);

        HBox topRow1 = new HBox();
        topRow1.setAlignment(Pos.CENTER_LEFT);

        Label statusBadge = new Label("Confirmed");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12px;");

        Label categoryTag = new Label("NEXT UPCOMING BOOKING");
        categoryTag.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 0 0 0 10;");

        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);

        FontIcon iconHome = new FontIcon("fas-calendar-check");
        iconHome.setIconSize(18);
        iconHome.setIconColor(Color.web(PRIMARY_BLUE));

        topRow1.getChildren().addAll(statusBadge, categoryTag, sp1, iconHome);

        Label eventTitle = new Label("Upcoming Live Event Assignment");
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox detailsGrid = new HBox(24);
        VBox d1 = new VBox(2, new Label("📅 Date: Live Synced"), new Label("⏰ Working Hours: Standard Event Time"));
        d1.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        d1.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox d2 = new VBox(2, new Label("📍 Venue: Assigned by Organizer"), new Label("👥 Full Technical Support"));
        d2.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        d2.getChildren().get(1).setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        detailsGrid.getChildren().addAll(d1, d2);

        Button viewBookingBtn = new Button("View My Bookings →");
        viewBookingBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 0; -fx-cursor: hand;");
        viewBookingBtn.setOnAction(e -> openMyBookings());

        nextBookingCard.getChildren().addAll(topRow1, eventTitle, detailsGrid, new Separator(), viewBookingBtn);

        VBox actionCard = createCardContainer();
        actionCard.setPrefWidth(420);
        actionCard.setStyle("-fx-background-color: #FFFBEB; -fx-border-color: #FCD34D; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 18;");

        HBox actionHeader = new HBox(8);
        actionHeader.setAlignment(Pos.CENTER_LEFT);

        FontIcon warnIcon = new FontIcon("fas-exclamation-triangle");
        warnIcon.setIconSize(16);
        warnIcon.setIconColor(Color.web("#92400E"));

        Label actionTitle = new Label("Action Required");
        actionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #92400E;");
        actionHeader.getChildren().addAll(warnIcon, actionTitle);

        Label actionText = new Label("You have service requests assigned by event organizers awaiting confirmation.");
        actionText.setWrapText(true);
        actionText.setStyle("-fx-font-size: 12px; -fx-text-fill: #78350F;");

        Region flexSpace = new Region();
        VBox.setVgrow(flexSpace, Priority.ALWAYS);

        Button reviewBtn = new Button("Review Service Requests →");
        reviewBtn.setStyle("-fx-background-color: " + ORANGE_BTN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 10 18; -fx-background-radius: 8px; -fx-cursor: hand;");
        reviewBtn.setOnAction(e -> openServiceRequests());

        actionCard.getChildren().addAll(actionHeader, actionText, flexSpace, reviewBtn);
        row.getChildren().addAll(nextBookingCard, actionCard);
        return row;
    }

    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        newReqValLbl = new Label("0");
        upcomingValLbl = new Label("0");
        totalEarningsValLbl = new Label("₹ 0");
        pendingPaymentsValLbl = new Label("₹ 0");

        row.getChildren().addAll(
                createStatCard("New Requests", newReqValLbl, "Needs Response", "fas-inbox", PRIMARY_BLUE),
                createStatCard("Confirmed Bookings", upcomingValLbl, "Active Assignments", "fas-calendar-check", GREEN_TEXT),
                createStatCard("Total Earnings", totalEarningsValLbl, "From Paid Bookings", "fas-wallet", PRIMARY_BLUE),
                createStatCard("Pending Requests", pendingPaymentsValLbl, "Unconfirmed Amount", "fas-hourglass-half", WARNING_TEXT)
        );
        return row;
    }

    private VBox createStatCard(String title, Label valLbl, String sub, String iconLit, String colorHex) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(colorHex));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        top.getChildren().addAll(icon, t);

        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, valLbl, s);
        return card;
    }

    private HBox createMainSplitSection() {
        HBox row = new HBox(16);

        VBox tableCard = createCardContainer();
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        Label tTitle = new Label("Upcoming Bookings");
        tTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Hyperlink viewAll = new Hyperlink("View All Bookings →");
        viewAll.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        viewAll.setOnAction(e -> openMyBookings());

        tableHeader.getChildren().addAll(tTitle, sp, viewAll);

        upcomingTableGrid = new GridPane();
        upcomingTableGrid.setHgap(16);
        upcomingTableGrid.setVgap(12);
        upcomingTableGrid.setPadding(new Insets(10, 0, 0, 0));

        addTableHeader(upcomingTableGrid, "EVENT", 0);
        addTableHeader(upcomingTableGrid, "DATE", 1);
        addTableHeader(upcomingTableGrid, "SERVICE", 2);
        addTableHeader(upcomingTableGrid, "AMOUNT", 3);
        addTableHeader(upcomingTableGrid, "STATUS", 4);

        tableCard.getChildren().addAll(tableHeader, upcomingTableGrid);

        VBox requestsCard = createCardContainer();
        requestsCard.setPrefWidth(380);

        HBox reqHeader = new HBox(8);
        reqHeader.setAlignment(Pos.CENTER_LEFT);

        Label rTitle = new Label("Recent Service Requests");
        rTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        reqHeader.getChildren().add(rTitle);

        requestsContainerBox = new VBox(10);
        requestsCard.getChildren().addAll(reqHeader, new Separator(), requestsContainerBox);

        row.getChildren().addAll(tableCard, requestsCard);
        return row;
    }

    private void addTableHeader(GridPane grid, String title, int col) {
        Label l = new Label(title);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void addTableRow(GridPane grid, int row, String event, String date, String service, String amount, String status) {
        Label eLbl = new Label(event); eLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label dLbl = new Label(date); dLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label sLbl = new Label(service); sLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label aLbl = new Label(amount); aLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Label statusBadge = new Label(status);
        if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Accepted")) {
            statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
        }

        grid.add(eLbl, 0, row);
        grid.add(dLbl, 1, row);
        grid.add(sLbl, 2, row);
        grid.add(aLbl, 3, row);
        grid.add(statusBadge, 4, row);
    }

    private VBox createServicesPreviewSection() {
        VBox card = createCardContainer();
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Service Categories Preview");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        header.getChildren().addAll(title, sp);

        FlowPane servicesGrid = new FlowPane();
        servicesGrid.setHgap(10);
        servicesGrid.setVgap(10);

        String[] categories = {
                "Decoration", "Sound System", "Photography & Videography",
                "Rental Equipment", "Invitation & Printing", "Catering",
                "Anchor", "Cake & Dessert Services"
        };

        for (String cat : categories) {
            Label tag = new Label("● " + cat);
            tag.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px; -fx-padding: 8 14; -fx-background-radius: 20px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px;");
            servicesGrid.getChildren().add(tag);
        }

        card.getChildren().addAll(header, new Separator(), servicesGrid);
        return card;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        return card;
    }

    // ==========================================
    // STANDARDIZED VENDOR SIDEBAR (MATCHES ALL SCREENS)
    // ==========================================
    public VBox createCommonVendorSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 6, 14, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("VENDOR PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        logoBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Service Requests", "fas-inbox"},
                {"My Bookings", "fas-calendar-check"},
                {"Services & Packages", "fas-cubes"},
                {"Calendar", "fas-calendar-alt"},
                {"Earnings & Payments", "fas-wallet"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleVendorNav(name)));
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, this::openNotificationsV),
                createSidebarButton("Profile", "fas-user", false, this::openProfileV),
                createSidebarButton("Settings", "fas-cog", false, this::openSettingsV),
                createSidebarButton("Support", "fas-question-circle", false, this::openSupportV)
        );

        sidebar.getChildren().addAll(logoBox, menu, spacer, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String title, String iconLit, boolean isActive, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(isActive ? Color.web(SIDEBAR_NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleVendorNav(String screen) {
        switch (screen) {
            case "Dashboard":
                LandingPage.landingPageStage.setScene(getvendorDashboardScene());
                break;
            case "Service Requests":
                openServiceRequests();
                break;
            case "My Bookings":
                openMyBookings();
                break;
            case "Services & Packages":
                openServicesPackages();
                break;
            case "Calendar":
                openCalenderV();
                break;
            case "Earnings & Payments":
                openEarningsPaymentsV();
                break;
        }
    }

    private void openProfileV() {
        ProfileV profileV = new ProfileV(uid);
        profileV.setVendorDashboard(this);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(profileV.getprofileVScene());
        }
    }

    private void openServiceRequests() {
        ServiceRequestsV serviceRequestsV = new ServiceRequestsV(uid, idToken);
        serviceRequestsV.setVendorDashboard(this);
        Scene serviceScene = serviceRequestsV.getserviceRequestVScene();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(serviceScene);
        }
    }

    private void openMyBookings() {
        MyBookingsV myBookingsV = new MyBookingsV(uid, idToken);
        myBookingsV.setVendorDashboard(this);
        Scene bookingScene = myBookingsV.getmyBookingScene();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(bookingScene);
        }
    }

    private void openServicesPackages() {
        ServicesPackagesV servicesPackagesV = new ServicesPackagesV();
        servicesPackagesV.setVendorDashboard(this);
        Scene packagesScene = servicesPackagesV.getservicesPackagesVScene(null, null);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(packagesScene);
        }
    }

    private void openCalenderV() {
        CalendarV calendarV = new CalendarV();
        calendarV.setVendorDashboard(this);
        Scene calenderScene = calendarV.getcalendarVScene(null, null);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(calenderScene);
        }
    }

    private void openEarningsPaymentsV() {
        EarningsPaymentsV earningsPaymentsV = new EarningsPaymentsV();
        earningsPaymentsV.setVendorDashboard(this);
        Scene earningScene = earningsPaymentsV.getearningsPaymentsScene(null);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(earningScene);
        }
    }

    private void openNotificationsV() {
        NotificationsV notificationsV = new NotificationsV();
        notificationsV.setVendorDashboard(this);
        Scene notificationScene = notificationsV.getnotificationsScene(null);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(notificationScene);
        }
    }

    private void openSettingsV() {
        SettingsV settingsV = new SettingsV();
        settingsV.setVendorDashboard(this);
        Scene settingsVScene = settingsV.getsettingsVScene(null);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(settingsVScene);
        }
    }

    private void openSupportV() {
        SupportV supportV = new SupportV();
        supportV.setVendorDashboard(this);
        Scene supportVScene = supportV.getsupportVScene(null);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(supportVScene);
        }
    }
}