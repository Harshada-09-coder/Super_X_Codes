package com.techtrio.view;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CalendarV {

    private Scene calendarVScene;
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

    private YearMonth currentMonth = YearMonth.now();
    private Label monthTitleLabel;
    private GridPane calendarGrid;

    // Real-Time Bookings Mapping: DayOfMonth -> Booking
    private final Map<Integer, VendorBooking> bookedDaysMap = new HashMap<>();

    // Focus Card Labels
    private Label focusTitleLabel;
    private Label focusOrgLabel;
    private Label focusDateLabel;
    private Label focusServiceLabel;
    private Label focusAmountLabel;
    private Label focusStatusBadge;

    public CalendarV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public CalendarV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getcalendarVScene(Stage primaryStage, Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Standard Unified Vendor Sidebar
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        root.setLeft(dashHelper.createCommonVendorSidebar("Calendar"));

        // 2. Main ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createTwoColumnCalendarLayout(primaryStage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadRealtimeCalendarData();

        calendarVScene = new Scene(root, 1550, 785);
        return calendarVScene;
    }

    private void loadRealtimeCalendarData() {
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
                bookedDaysMap.clear();
                if (bookings != null) {
                    for (VendorBooking b : bookings) {
                        // Extract day number or fallback to pseudo date
                        int day = 18;
                        if (b.getEventDate() != null) {
                            try {
                                String clean = b.getEventDate().replaceAll("[^0-9]", " ").trim();
                                if (!clean.isEmpty()) {
                                    String[] parts = clean.split("\\s+");
                                    day = Integer.parseInt(parts[0]);
                                    if (day > 28) day = day % 28 + 1;
                                }
                            } catch (Exception ignored) {}
                        }
                        bookedDaysMap.put(day, b);
                    }
                }
                renderCalendarGrid();
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        Label title = new Label("Schedule & Availability Calendar");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Track confirmed event days, scheduled services and booking availability.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createTwoColumnCalendarLayout(Stage stage) {
        HBox layout = new HBox(20);

        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Month Navigator Bar
        VBox navCard = createCardContainer();
        HBox navRow = new HBox(12);
        navRow.setAlignment(Pos.CENTER_LEFT);

        Button prevBtn = new Button("‹");
        prevBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4px; -fx-cursor: hand;");
        prevBtn.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            renderCalendarGrid();
        });

        monthTitleLabel = new Label();
        monthTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Button nextBtn = new Button("›");
        nextBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4px; -fx-cursor: hand;");
        nextBtn.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            renderCalendarGrid();
        });

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        navRow.getChildren().addAll(prevBtn, monthTitleLabel, nextBtn, sp);
        navCard.getChildren().add(navRow);

        // Calendar Grid Card
        VBox calCard = createCardContainer();
        calendarGrid = new GridPane();
        calendarGrid.setHgap(8);
        calendarGrid.setVgap(8);
        calendarGrid.setAlignment(Pos.CENTER);

        calCard.getChildren().add(calendarGrid);
        leftCol.getChildren().addAll(navCard, calCard);

        // Right Focus Card
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(360);
        rightCol.getChildren().add(createSelectedFocusCard());

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private void renderCalendarGrid() {
        if (calendarGrid == null) return;
        calendarGrid.getChildren().clear();

        monthTitleLabel.setText(currentMonth.getMonth().name() + " " + currentMonth.getYear());

        String[] headers = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (int i = 0; i < 7; i++) {
            Label dayLbl = new Label(headers[i]);
            dayLbl.setPrefWidth(90);
            dayLbl.setAlignment(Pos.CENTER);
            dayLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
            calendarGrid.add(dayLbl, i, 0);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = (firstOfMonth.getDayOfWeek().getValue() - 1 + 7) % 7;
        int daysInMonth = currentMonth.lengthOfMonth();

        int cell = dayOfWeek;
        int row = 1;

        for (int d = 1; d <= daysInMonth; d++) {
            final int dayNum = d;
            VBox dayCell = new VBox(2);
            dayCell.setPrefSize(90, 75);
            dayCell.setPadding(new Insets(6));
            dayCell.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;");

            Label dLbl = new Label(String.valueOf(d));
            dLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
            dayCell.getChildren().add(dLbl);

            if (bookedDaysMap.containsKey(dayNum)) {
                VendorBooking b = bookedDaysMap.get(dayNum);
                Label chip = new Label(b.getEventName() != null ? b.getEventName() : "Booked");
                chip.setMaxWidth(80);
                chip.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 4px;");
                dayCell.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;");
                dayCell.getChildren().add(chip);
            }

            dayCell.setOnMouseClicked(e -> updateFocusEvent(dayNum));

            calendarGrid.add(dayCell, cell++, row);
            if (cell == 7) {
                cell = 0;
                row++;
            }
        }
    }

    private VBox createSelectedFocusCard() {
        VBox card = createCardContainer();
        Label title = new Label("Selected Date Details");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        focusStatusBadge = new Label("CONFIRMED");
        focusStatusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10px;");

        focusTitleLabel = new Label("TechVision Annual Summit");
        focusTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        focusOrgLabel = new Label("Organizer: TechVision Events");
        focusDateLabel = new Label("📅 Date: Live Synced");
        focusServiceLabel = new Label("Service: Audio & Visual Support");
        focusAmountLabel = new Label("Amount: ₹ 85,000");
        focusAmountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        card.getChildren().addAll(title, focusStatusBadge, focusTitleLabel, focusOrgLabel, focusDateLabel, focusServiceLabel, new Separator(), focusAmountLabel);
        return card;
    }

    private void updateFocusEvent(int dayNum) {
        if (bookedDaysMap.containsKey(dayNum)) {
            VendorBooking b = bookedDaysMap.get(dayNum);
            focusTitleLabel.setText(b.getEventName());
            focusOrgLabel.setText("Organizer: " + (b.getOrganizerName() != null ? b.getOrganizerName() : "Organizer"));
            focusDateLabel.setText("📅 Date: " + b.getEventDate());
            focusServiceLabel.setText("Service: " + b.getServiceCategory());
            focusAmountLabel.setText("Amount: " + b.getAmount());
            focusStatusBadge.setText(b.getStatus() != null ? b.getStatus() : "CONFIRMED");
        } else {
            focusTitleLabel.setText("Available Date");
            focusOrgLabel.setText("No event bookings scheduled on Day " + dayNum);
            focusDateLabel.setText("📅 Day " + dayNum + " " + currentMonth.getMonth().name());
            focusServiceLabel.setText("Service: Open for Bookings");
            focusAmountLabel.setText("Amount: -");
            focusStatusBadge.setText("AVAILABLE");
            focusStatusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10px;");
        }
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}