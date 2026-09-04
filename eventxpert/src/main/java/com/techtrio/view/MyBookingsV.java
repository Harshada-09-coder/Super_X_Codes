package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public class MyBookingsV {
    private Scene myBookingScene;
    private VendorDashboardV vendorDashboard;

    private String uid;
    private String idToken;

    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
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
    private static final String RED_BG = "#FEF2F2";
    private static final String RED_TEXT = "#DC2626";

    public static class BookingData {
        public String id;
        public String organizer;
        public String eventName;
        public String venue;
        public String dateTime;
        public String service;
        public String guests;
        public String amount;
        public String status;
        public VendorBooking rawBooking;

        public BookingData(String id, String organizer, String eventName, String venue, String dateTime,
                    String service, String guests, String amount, String status, VendorBooking rawBooking) {
            this.id = id;
            this.organizer = organizer;
            this.eventName = eventName;
            this.venue = venue;
            this.dateTime = dateTime;
            this.service = service;
            this.guests = guests;
            this.amount = amount;
            this.status = status;
            this.rawBooking = rawBooking;
        }
    }

    private final List<BookingData> bookingList = new ArrayList<>();
    private VBox tableRowsContainer;
    private String activeTabFilter = "All Bookings";

    // 5 KPI Summary labels (Including Cancelled Card)
    private Label totalBookingsValLbl;
    private Label upcomingValLbl;
    private Label inProgressValLbl;
    private Label completedValLbl;
    private Label cancelledValLbl;

    private HBox tabsRowContainer;
    private TextField searchInput;

    public MyBookingsV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public MyBookingsV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getmyBookingScene() {
        return getmyBookingScene(LandingPage.landingPageStage);
    }

    public Scene getmyBookingScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Standardized Vendor Sidebar
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        root.setLeft(dashHelper.createCommonVendorSidebar("My Bookings"));

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
                tabsRowContainer = createTabsRow(primaryStage),
                createTableCard(primaryStage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadBookingsFromFirestoreAsync(primaryStage);

        myBookingScene = new Scene(root, 1550, 785);
        return myBookingScene;
    }

    private void loadBookingsFromFirestoreAsync(Stage stage) {
        if (uid == null || uid.isBlank()) return;

        Task<List<VendorBooking>> task = new Task<>() {
            @Override
            protected List<VendorBooking> call() {
                VendorController controller = new VendorController();
                return controller.getVendorBookings(uid);
            }
        };

        task.setOnSucceeded(evt -> {
            List<VendorBooking> result = task.getValue();
            Platform.runLater(() -> {
                bookingList.clear();
                if (result != null) {
                    for (VendorBooking b : result) {
                        String rawStatus = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";
                        String uiStatus = "Pending";
                        if ("CONFIRMED".equals(rawStatus) || "ACCEPTED".equals(rawStatus) || "PAID".equals(rawStatus)) uiStatus = "Confirmed";
                        else if ("REJECTED".equals(rawStatus) || "CANCELLED".equals(rawStatus)) uiStatus = "Cancelled";

                        bookingList.add(new BookingData(
                                b.getBookingId() != null ? b.getBookingId() : "",
                                b.getOrganizerName() != null ? b.getOrganizerName() : "Organizer",
                                b.getEventName() != null ? b.getEventName() : "Event",
                                b.getLocation() != null ? b.getLocation() : "Pune",
                                b.getEventDate() != null ? b.getEventDate() : "Upcoming",
                                b.getServiceCategory() != null ? b.getServiceCategory() : "Service",
                                String.valueOf(b.getGuests() > 0 ? b.getGuests() : 100),
                                b.getAmount() != null ? b.getAmount() : "₹ 0",
                                uiStatus,
                                b
                        ));
                    }
                }
                updateSummaryCounts();
                buildTabs(tabsRowContainer, stage);
                renderBookingsTable(stage);
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void updateSummaryCounts() {
        int total = bookingList.size();
        int upcoming = 0;
        int completed = 0;
        int cancelled = 0;

        for (BookingData b : bookingList) {
            if ("Confirmed".equalsIgnoreCase(b.status)) upcoming++;
            else if ("Completed".equalsIgnoreCase(b.status)) completed++;
            else if ("Cancelled".equalsIgnoreCase(b.status)) cancelled++;
        }

        if (totalBookingsValLbl != null) totalBookingsValLbl.setText(String.valueOf(total));
        if (upcomingValLbl != null) upcomingValLbl.setText(String.valueOf(upcoming));
        if (inProgressValLbl != null) inProgressValLbl.setText(String.valueOf(upcoming > 0 ? 1 : 0));
        if (completedValLbl != null) completedValLbl.setText(String.valueOf(completed));
        if (cancelledValLbl != null) cancelledValLbl.setText(String.valueOf(cancelled));
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        Label title = new Label("My Bookings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("View and manage all your assigned, confirmed and cancelled bookings.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    // ⭐ 5 Summary Cards Row including CANCELLED card
    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        totalBookingsValLbl = new Label("0");
        upcomingValLbl = new Label("0");
        inProgressValLbl = new Label("0");
        completedValLbl = new Label("0");
        cancelledValLbl = new Label("0");

        row.getChildren().addAll(
                createSummaryCard("Total Bookings", totalBookingsValLbl, "fas-calendar-alt", PRIMARY_BLUE),
                createSummaryCard("Upcoming", upcomingValLbl, "fas-clock", PRIMARY_BLUE),
                createSummaryCard("In Progress", inProgressValLbl, "fas-spinner", PRIMARY_BLUE),
                createSummaryCard("Completed", completedValLbl, "fas-check-circle", GREEN_TEXT),
                createSummaryCard("Cancelled", cancelledValLbl, "fas-times-circle", RED_TEXT)
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
        searchInput = new TextField();
        searchInput.setPromptText("🔍 Search bookings by event, organizer or service...");
        searchInput.setPrefHeight(36);
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12;");
        searchInput.textProperty().addListener((obs, oldV, newV) -> renderBookingsTable(LandingPage.landingPageStage));

        card.getChildren().add(searchInput);
        return card;
    }

    private HBox createTabsRow(Stage stage) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        buildTabs(row, stage);
        return row;
    }

    private void buildTabs(HBox row, Stage stage) {
        if (row == null) return;
        row.getChildren().clear();

        int allCount = bookingList.size();
        long upcomingCount = bookingList.stream().filter(b -> "Confirmed".equalsIgnoreCase(b.status)).count();
        long cancelledCount = bookingList.stream().filter(b -> "Cancelled".equalsIgnoreCase(b.status)).count();

        String[] tabs = {
                "All Bookings " + allCount,
                "Upcoming " + upcomingCount,
                "Cancelled " + cancelledCount
        };

        for (String tab : tabs) {
            String tabName = tab.substring(0, tab.lastIndexOf(" "));
            Button tabBtn = new Button(tab);

            if (tabName.equalsIgnoreCase(activeTabFilter)) {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 0 0 2 0; -fx-padding: 6 12; -fx-cursor: hand;");
            } else {
                tabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-padding: 6 12; -fx-cursor: hand;");
            }

            tabBtn.setOnAction(e -> {
                activeTabFilter = tabName;
                buildTabs(row, stage);
                renderBookingsTable(stage);
            });

            row.getChildren().add(tabBtn);
        }
    }

    private VBox createTableCard(Stage stage) {
        VBox tableCard = createCardContainer();

        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(12);
        tableHeader.setPadding(new Insets(10, 12, 10, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addTableHeaderLabel(tableHeader, "EVENT & ORGANIZER", 0, 240);
        addTableHeaderLabel(tableHeader, "DATE", 1, 140);
        addTableHeaderLabel(tableHeader, "SERVICE", 2, 140);
        addTableHeaderLabel(tableHeader, "AMOUNT", 3, 100);
        addTableHeaderLabel(tableHeader, "STATUS", 4, 100);
        addTableHeaderLabel(tableHeader, "ACTION", 5, 100);

        tableRowsContainer = new VBox(4);
        tableCard.getChildren().addAll(tableHeader, tableRowsContainer);
        return tableCard;
    }

    private void addTableHeaderLabel(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void renderBookingsTable(Stage stage) {
        if (tableRowsContainer == null) return;
        tableRowsContainer.getChildren().clear();

        List<BookingData> filtered = bookingList.stream().filter(b -> {
            if ("Upcoming".equalsIgnoreCase(activeTabFilter) && !"Confirmed".equalsIgnoreCase(b.status)) return false;
            if ("Cancelled".equalsIgnoreCase(activeTabFilter) && !"Cancelled".equalsIgnoreCase(b.status)) return false;

            if (searchInput != null && !searchInput.getText().isBlank()) {
                String q = searchInput.getText().toLowerCase().trim();
                return b.eventName.toLowerCase().contains(q) || b.service.toLowerCase().contains(q) || b.organizer.toLowerCase().contains(q);
            }
            return true;
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Label empty = new Label("No bookings found.");
            empty.setStyle("-fx-padding: 20; -fx-text-fill: " + SECONDARY_TEXT + ";");
            tableRowsContainer.getChildren().add(empty);
            return;
        }

        for (BookingData b : filtered) {
            GridPane row = new GridPane();
            row.setHgap(12);
            row.setPadding(new Insets(10, 12, 10, 12));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            VBox eventBox = new VBox(2);
            eventBox.setPrefWidth(240);
            Label eventName = new Label(b.eventName);
            eventName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label org = new Label("Organizer: " + b.organizer);
            org.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            eventBox.getChildren().addAll(eventName, org);

            Label dtLbl = new Label(b.dateTime); dtLbl.setPrefWidth(140);
            Label sLbl = new Label(b.service); sLbl.setPrefWidth(140);
            Label aLbl = new Label(b.amount); aLbl.setPrefWidth(100); aLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

            Label statusBadge = new Label(b.status); statusBadge.setPrefWidth(100);
            if (b.status.equalsIgnoreCase("Confirmed")) {
                statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
            } else if (b.status.equalsIgnoreCase("Cancelled")) {
                statusBadge.setStyle("-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
            } else {
                statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 10px;");
            }

            Button viewBtn = new Button("View Details");
            viewBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 4px; -fx-cursor: hand;");
            viewBtn.setOnAction(e -> {
                BookingDetailsV bookingDetailsV = new BookingDetailsV(b.rawBooking);
                LandingPage.landingPageStage.setScene(bookingDetailsV.getbookingDetailsScene(stage, this::backToMyBookingPage));
            });

            row.add(eventBox, 0, 0);
            row.add(dtLbl, 1, 0);
            row.add(sLbl, 2, 0);
            row.add(aLbl, 3, 0);
            row.add(statusBadge, 4, 0);
            row.add(viewBtn, 5, 0);

            tableRowsContainer.getChildren().add(row);
        }
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }

    public void backToMyBookingPage() {
        LandingPage.landingPageStage.setScene(getmyBookingScene(LandingPage.landingPageStage));
    }
}