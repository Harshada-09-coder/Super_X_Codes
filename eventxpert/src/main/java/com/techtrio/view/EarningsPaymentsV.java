package com.techtrio.view;

import java.util.ArrayList;
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
import javafx.stage.Stage;

public class EarningsPaymentsV {

    private Scene earningsPaymentsScene;
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

    private Label totalEarningsValLbl;
    private Label thisMonthValLbl;
    private Label pendingPaymentsValLbl;
    private Label receivedValLbl;
    private VBox tableRowsContainer;

    public EarningsPaymentsV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public EarningsPaymentsV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getearningsPaymentsScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Standard Unified Vendor Sidebar
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        root.setLeft(dashHelper.createCommonVendorSidebar("Earnings & Payments"));

        // 2. Main ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createSummaryCardsRow(),
                createTransactionsTableCard()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadRealtimeEarningsData();

        earningsPaymentsScene = new Scene(root, 1550, 785);
        return earningsPaymentsScene;
    }

    private void loadRealtimeEarningsData() {
        Task<List<VendorBooking>> task = new Task<>() {
            @Override
            protected List<VendorBooking> call() {
                VendorController controller = new VendorController();
                return controller.getVendorBookings(uid);
            }
        };

        task.setOnSucceeded(e -> {
            List<VendorBooking> bookings = task.getValue();
            Platform.runLater(() -> {
                double totalEarnings = 0.0;
                double pending = 0.0;

                if (tableRowsContainer != null) tableRowsContainer.getChildren().clear();

                if (bookings != null && !bookings.isEmpty()) {
                    for (VendorBooking b : bookings) {
                        double amt = 0.0;
                        try {
                            String clean = b.getAmount() != null ? b.getAmount().replaceAll("[^0-9.]", "") : "0";
                            if (!clean.isEmpty()) amt = Double.parseDouble(clean);
                        } catch (Exception ignored) {}

                        String st = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";

                        if ("CONFIRMED".equals(st) || "ACCEPTED".equals(st) || "PAID".equals(st)) {
                            totalEarnings += amt;
                            addTransactionRow(b.getEventDate() != null ? b.getEventDate() : "Upcoming", b.getEventName(), "₹ " + String.format("%,.0f", amt), "₹ " + String.format("%,.0f", amt * 0.03), "₹ " + String.format("%,.0f", amt * 0.97), "Paid");
                        } else {
                            pending += amt;
                            addTransactionRow(b.getEventDate() != null ? b.getEventDate() : "Upcoming", b.getEventName(), "₹ " + String.format("%,.0f", amt), "₹ 0", "₹ " + String.format("%,.0f", amt), "Pending");
                        }
                    }
                } else {
                    Label empty = new Label("No financial transactions recorded yet.");
                    empty.setStyle("-fx-padding: 20; -fx-text-fill: " + SECONDARY_TEXT + ";");
                    if (tableRowsContainer != null) tableRowsContainer.getChildren().add(empty);
                }

                if (totalEarningsValLbl != null) totalEarningsValLbl.setText("₹ " + String.format("%,.0f", totalEarnings));
                if (thisMonthValLbl != null) thisMonthValLbl.setText("₹ " + String.format("%,.0f", totalEarnings));
                if (receivedValLbl != null) receivedValLbl.setText("₹ " + String.format("%,.0f", totalEarnings * 0.97));
                if (pendingPaymentsValLbl != null) pendingPaymentsValLbl.setText("₹ " + String.format("%,.0f", pending));
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        Label title = new Label("Earnings & Payments");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Live track your payouts, platform settlements and revenue from confirmed bookings.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        totalEarningsValLbl = new Label("₹ 0");
        thisMonthValLbl = new Label("₹ 0");
        pendingPaymentsValLbl = new Label("₹ 0");
        receivedValLbl = new Label("₹ 0");

        row.getChildren().addAll(
                createSummaryCard("Total Revenue", totalEarningsValLbl, "fas-wallet", PRIMARY_BLUE),
                createSummaryCard("This Month", thisMonthValLbl, "fas-calendar-check", PRIMARY_BLUE),
                createSummaryCard("Net Received", receivedValLbl, "fas-check-circle", GREEN_TEXT),
                createSummaryCard("Pending Approval", pendingPaymentsValLbl, "fas-hourglass-half", WARNING_TEXT)
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

        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        card.getChildren().addAll(top, valLbl);
        return card;
    }

    private VBox createTransactionsTableCard() {
        VBox card = createCardContainer();
        Label title = new Label("Real-Time Transaction History");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(12);
        tableHeader.setPadding(new Insets(8, 12, 8, 12));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addTableHeader(tableHeader, "DATE", 0, 140);
        addTableHeader(tableHeader, "EVENT NAME", 1, 260);
        addTableHeader(tableHeader, "GROSS AMOUNT", 2, 140);
        addTableHeader(tableHeader, "PLATFORM FEE (3%)", 3, 160);
        addTableHeader(tableHeader, "NET PAYOUT", 4, 140);
        addTableHeader(tableHeader, "STATUS", 5, 100);

        tableRowsContainer = new VBox(4);
        card.getChildren().addAll(title, tableHeader, tableRowsContainer);
        return card;
    }

    private void addTableHeader(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void addTransactionRow(String date, String event, String gross, String fee, String net, String status) {
        GridPane row = new GridPane();
        row.setHgap(12);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label d = new Label(date); d.setPrefWidth(140); d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label ev = new Label(event); ev.setPrefWidth(260); ev.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label gr = new Label(gross); gr.setPrefWidth(140); gr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label fe = new Label(fee); fe.setPrefWidth(160); fe.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label nt = new Label(net); nt.setPrefWidth(140); nt.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + GREEN_TEXT + ";");

        Label st = new Label(status); st.setPrefWidth(100);
        if ("Paid".equalsIgnoreCase(status)) {
            st.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 8px;");
        } else {
            st.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 8px;");
        }

        row.add(d, 0, 0);
        row.add(ev, 1, 0);
        row.add(gr, 2, 0);
        row.add(fe, 3, 0);
        row.add(nt, 4, 0);
        row.add(st, 5, 0);

        tableRowsContainer.getChildren().add(row);
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}