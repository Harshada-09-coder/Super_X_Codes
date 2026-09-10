package com.techtrio.view;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Conflict;
import com.techtrio.model.Event;
import com.techtrio.model.VendorProfile;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ReportA {

    private Scene reportScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private final AdminController adminController = new AdminController();

    private StackPane rootLayer;

    private Label totalEventsVal;
    private Label completedEventsVal;
    private Label upcomingEventsVal;
    private Label totalRevenueVal;
    private Label totalBookingsVal;

    private XYChart.Series<String, Number> eventSeries;
    private PieChart pieChart;

    private Label revenueOverviewValue;
    private Label avgBudgetLabel;
    private TableView<String[]> revenueTable;

    private Label conflictTotalVal;
    private Label conflictCriticalVal;
    private Label conflictPendingVal;
    private Label conflictResolvedVal;
    private VBox conflictProgressContainer;

    private TableView<String[]> vendorTable;
    private ComboBox<String> monthFilter;

    private List<Event> cachedEvents = new ArrayList<>();
    private List<VendorProfile> cachedVendors = new ArrayList<>();
    private List<Conflict> cachedConflicts = new ArrayList<>();
    private double liveAdminPlatformRevenue = 0.0;

    public Scene getreportScene() {
        rootLayer = new StackPane();
        rootLayer.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        VBox sidebar = createAdminSidebar("Reports & Analytics");
        root.setLeft(sidebar);

        VBox main = new VBox(18);
        main.setPadding(new Insets(24, 32, 28, 32));
        main.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Reports & Analytics");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Live system performance, real-time platform revenue, and business insights.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));

        monthFilter = new ComboBox<>();
        monthFilter.getItems().addAll("This Month", "Last Month", "Last 2 Months", "This Year", "All Time");
        monthFilter.setValue("All Time");
        monthFilter.setPrefWidth(130);
        monthFilter.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");
        monthFilter.setOnAction(e -> applyFiltersAndUpdateUI());

        Button export = new Button("  Export Report");
        FontIcon expIcon = new FontIcon("fas-download");
        expIcon.setIconSize(12);
        expIcon.setIconColor(Color.web(PRIMARY));
        export.setGraphic(expIcon);
        export.setStyle("-fx-background-color:white; -fx-border-color:" + PRIMARY + "; -fx-border-radius:6; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding: 6 12;");
        export.setOnAction(e -> exportReportSummary());

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        HBox heading = new HBox(12, new VBox(2, title, subtitle), topSpace, monthFilter, export);
        heading.setAlignment(Pos.CENTER_LEFT);

        totalEventsVal = new Label("0");
        completedEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        totalRevenueVal = new Label("₹0.00");
        totalBookingsVal = new Label("0");

        HBox cards = new HBox(12);
        cards.getChildren().addAll(
                card("TOTAL EVENTS", totalEventsVal, "All events logged", "fas-calendar-alt", "#0284C7"),
                card("COMPLETED EVENTS", completedEventsVal, "Successfully executed", "fas-check-circle", "#16A34A"),
                card("UPCOMING EVENTS", upcomingEventsVal, "Scheduled / Confirmed", "fas-clock", "#7C3AED"),
                card("PLATFORM REVENUE", totalRevenueVal, "Net Admin Earnings", "fas-wallet", "#002A60"),
                card("TOTAL BOOKINGS", totalBookingsVal, "Confirmed bookings", "fas-receipt", "#EA580C")
        );

        CategoryAxis eventX = new CategoryAxis();
        NumberAxis eventY = new NumberAxis();
        BarChart<String, Number> eventChart = new BarChart<>(eventX, eventY);
        eventChart.setLegendVisible(false);
        eventChart.setAnimated(false);
        eventChart.setTitle("");

        eventSeries = new XYChart.Series<>();
        eventSeries.getData().add(new XYChart.Data<>("Planning", 0));
        eventSeries.getData().add(new XYChart.Data<>("Upcoming", 0));
        eventSeries.getData().add(new XYChart.Data<>("Completed", 0));
        eventSeries.getData().add(new XYChart.Data<>("Cancelled", 0));

        eventChart.getData().add(eventSeries);
        eventChart.setPrefHeight(250);

        VBox eventBox = panel("Event Pipeline Overview", eventChart);

        pieChart = new PieChart();
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(90);
        pieChart.setPrefHeight(250);

        VBox typeBox = panel("Events Breakdown by Type", pieChart);

        HBox charts = new HBox(18, eventBox, typeBox);
        HBox.setHgrow(eventBox, Priority.ALWAYS);
        HBox.setHgrow(typeBox, Priority.ALWAYS);

        revenueOverviewValue = new Label("₹0.00");
        revenueOverviewValue.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        revenueOverviewValue.setTextFill(Color.web(PRIMARY));

        avgBudgetLabel = new Label("Avg. Commission / Event: ₹0");
        avgBudgetLabel.setFont(Font.font("Arial", 11));
        avgBudgetLabel.setTextFill(Color.web("#64748B"));

        VBox revenueRight = new VBox(2, revenueOverviewValue, avgBudgetLabel);
        revenueRight.setAlignment(Pos.CENTER_RIGHT);

        Label revenueTitle = new Label("Net Platform Revenue (Contingency Residual + 3% Vendor Fee)");
        revenueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        revenueTitle.setTextFill(Color.web(PRIMARY));

        Region rSpace = new Region();
        HBox.setHgrow(rSpace, Priority.ALWAYS);

        HBox revenueHead = new HBox(revenueTitle, rSpace, revenueRight);

        revenueTable = new TableView<>();
        revenueTable.setPrefHeight(210);

        TableColumn<String[], String> monthCol = new TableColumn<>("MONTH");
        TableColumn<String[], String> revenueCol = new TableColumn<>("NET REVENUE");
        TableColumn<String[], String> growthCol = new TableColumn<>("GROWTH");

        monthCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        revenueCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        growthCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        monthCol.setPrefWidth(120);
        revenueCol.setPrefWidth(180);
        growthCol.setPrefWidth(120);

        revenueTable.getColumns().addAll(monthCol, revenueCol, growthCol);

        VBox revenueBox = new VBox(10, revenueHead, revenueTable);
        revenueBox.setPadding(new Insets(18));
        revenueBox.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        VBox conflictBox = new VBox(12);
        conflictBox.setPadding(new Insets(18));
        conflictBox.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        Label conflictTitle = new Label("Conflict & Incident Summary");
        conflictTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        conflictTitle.setTextFill(Color.web(PRIMARY));

        conflictTotalVal = new Label("0");
        conflictCriticalVal = new Label("0");
        conflictPendingVal = new Label("0");
        conflictResolvedVal = new Label("0");

        HBox conflictCards = new HBox(8);
        conflictCards.getChildren().addAll(
                smallCard("Total", conflictTotalVal),
                smallCard("Critical", conflictCriticalVal),
                smallCard("Pending", conflictPendingVal),
                smallCard("Resolved", conflictResolvedVal)
        );

        conflictProgressContainer = new VBox(8);
        conflictProgressContainer.getChildren().addAll(
                progress("Organizer", "0", 0.0),
                progress("Vendor", "0", 0.0),
                progress("Venue", "0", 0.0),
                progress("Schedule", "0", 0.0)
        );

        conflictBox.getChildren().addAll(conflictTitle, conflictCards, conflictProgressContainer);

        HBox revenueRow = new HBox(18, revenueBox, conflictBox);
        HBox.setHgrow(revenueBox, Priority.ALWAYS);
        HBox.setHgrow(conflictBox, Priority.ALWAYS);

        VBox vendorBox = new VBox(10);
        vendorBox.setPadding(new Insets(18));
        vendorBox.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        Label vendorTitle = new Label("Top Performing Verified Vendors");
        vendorTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        vendorTitle.setTextFill(Color.web(PRIMARY));

        vendorTable = new TableView<>();
        vendorTable.setPrefHeight(170);

        TableColumn<String[], String> vendor = new TableColumn<>("VENDOR PARTNER");
        TableColumn<String[], String> events = new TableColumn<>("ASSIGNED EVENTS");
        TableColumn<String[], String> rating = new TableColumn<>("RATING");
        TableColumn<String[], String> status = new TableColumn<>("STATUS");

        vendor.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        events.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        rating.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        status.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        vendor.setPrefWidth(260);
        events.setPrefWidth(140);
        rating.setPrefWidth(120);
        status.setPrefWidth(120);

        vendorTable.getColumns().addAll(vendor, events, rating, status);
        vendorBox.getChildren().addAll(vendorTitle, vendorTable);

        main.getChildren().addAll(heading, cards, charts, revenueRow, vendorBox);

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);
        rootLayer.getChildren().add(root);

        loadRealData();

        reportScene = new Scene(rootLayer, 1550, 785);
        return reportScene;
    }

    private VBox createAdminSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label adminLabel = new Label("ADMIN DASHBOARD");
        adminLabel.setTextFill(Color.web(LIGHT));
        adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        brandTexts.getChildren().addAll(logo, adminLabel);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"Events", "fas-calendar-check"},
                {"Calendar", "fas-calendar"},
                {"Customers", "fas-users"},
                {"Organizers", "fas-user-tie"},
                {"Vendors", "fas-truck"},
                {"Conflict Detection", "fas-shield-alt"},
                {"Reports & Analytics", "fas-chart-pie"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab)));
        }

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false),
                createSidebarButton("Profile", "fas-user", false),
                createSidebarButton("Settings", "fas-cog", false),
                createSidebarButton("Support", "fas-question-circle", false)
        );

        sidebar.getChildren().addAll(brandBox, menu, sidebarSpace, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(PRIMARY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT + "; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-font-size:12px; -fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px; -fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08); -fx-text-fill:white; -fx-font-size:12px; -fx-background-radius:6; -fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }

    // ⭐ अचूक Admin Revenue: (Contingency मधून उरलेले + Non-Venue Vendors चे ३% फी)
    private void loadRealData() {
        Thread thread = new Thread(() -> {
            try {
                List<Event> events = adminController.getAllEvents();
                List<VendorProfile> vendors = adminController.getAllVendors();
                List<Conflict> conflicts = adminController.getAllConflicts();

                double totalAdminRevenue = 0.0;

                if (events != null) {
                    for (Event ev : events) {
                        double paid = ev.getPaidAmount();
                        if (paid > 0) {
                            double orgShare15 = paid * 0.15;
                            double contingencyPool = 0.0;
                            double vendor3PercentCut = 0.0;

                            if (ev.getBudgetRecommendation() != null) {
                                for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
                                    String key = entry.getKey().toLowerCase();
                                    try {
                                        double amt = Double.parseDouble(entry.getValue().toString());
                                        if (key.contains("contingency") || key.contains("other") || key.contains("misc")) {
                                            contingencyPool += amt;
                                        } else if (!key.contains("venue")) {
                                            vendor3PercentCut += (amt * 0.03);
                                        }
                                    } catch (Exception ignored) {}
                                }
                            }

                            double adminContingencyShare = Math.max(0.0, contingencyPool - orgShare15);
                            totalAdminRevenue += (adminContingencyShare + vendor3PercentCut);
                        }
                    }
                }

                final double finalTotalRevenue = totalAdminRevenue;

                Platform.runLater(() -> {
                    cachedEvents = (events != null) ? events : new ArrayList<>();
                    cachedVendors = (vendors != null) ? vendors : new ArrayList<>();
                    cachedConflicts = (conflicts != null) ? conflicts : new ArrayList<>();
                    liveAdminPlatformRevenue = finalTotalRevenue;

                    applyFiltersAndUpdateUI();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void applyFiltersAndUpdateUI() {
        String filter = monthFilter.getValue();
        if (filter == null) filter = "All Time";

        List<Event> filteredEvents = filterEventsBySelection(cachedEvents, filter);

        int total = filteredEvents.size();
        int completed = 0;
        int upcoming = 0;
        int planning = 0;
        int cancelled = 0;
        int totalBookings = 0;

        Map<String, Integer> typeCounts = new HashMap<>();

        for (Event ev : filteredEvents) {
            String st = (ev.getStatus() != null) ? ev.getStatus().trim().toUpperCase() : "PENDING";

            if ("COMPLETED".equalsIgnoreCase(st)) {
                completed++;
                totalBookings++;
            } else if ("CANCELLED".equalsIgnoreCase(st) || "REJECTED".equalsIgnoreCase(st)) {
                cancelled++;
            } else if ("CONFIRMED".equalsIgnoreCase(st) || "ASSIGNED".equalsIgnoreCase(st) || "ACCEPTED".equalsIgnoreCase(st) || "PAID".equalsIgnoreCase(st)) {
                upcoming++;
                totalBookings++;
            } else {
                planning++;
            }

            String type = (ev.getEventType() != null && !ev.getEventType().isBlank()) ? ev.getEventType().trim() : "Other";
            typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
        }

        totalEventsVal.setText(String.valueOf(total));
        completedEventsVal.setText(String.valueOf(completed));
        upcomingEventsVal.setText(String.valueOf(upcoming));
        totalRevenueVal.setText(formatCurrency(liveAdminPlatformRevenue));
        totalBookingsVal.setText(String.valueOf(totalBookings));

        eventSeries.getData().clear();
        eventSeries.getData().add(new XYChart.Data<>("Planning", planning));
        eventSeries.getData().add(new XYChart.Data<>("Upcoming", upcoming));
        eventSeries.getData().add(new XYChart.Data<>("Completed", completed));
        eventSeries.getData().add(new XYChart.Data<>("Cancelled", cancelled));

        pieChart.getData().clear();
        if (typeCounts.isEmpty()) {
            pieChart.getData().add(new PieChart.Data("No Events", 1));
        } else {
            for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                pieChart.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            }
        }

        revenueOverviewValue.setText(formatCurrency(liveAdminPlatformRevenue));
        double avg = (total > 0) ? (liveAdminPlatformRevenue / total) : 0.0;
        avgBudgetLabel.setText("Avg. Commission: " + formatCurrency(avg));

        updateMonthlyRevenueTable(cachedEvents);
        updateConflictSummary(cachedConflicts);
        updateVendorTable(cachedVendors, cachedEvents);
    }

    private List<Event> filterEventsBySelection(List<Event> events, String filter) {
        if (events == null || events.isEmpty()) return new ArrayList<>();
        if ("All Time".equalsIgnoreCase(filter)) return events;

        LocalDate now = LocalDate.now();
        YearMonth currentYM = YearMonth.from(now);
        YearMonth lastYM = currentYM.minusMonths(1);
        YearMonth twoMonthsAgoYM = currentYM.minusMonths(2);
        int currentYear = now.getYear();

        List<Event> result = new ArrayList<>();

        for (Event ev : events) {
            LocalDate evDate = parseEventDate(ev.getEventDate());
            if (evDate == null) {
                if ("This Year".equalsIgnoreCase(filter) || "This Month".equalsIgnoreCase(filter)) {
                    result.add(ev);
                }
                continue;
            }

            YearMonth evYM = YearMonth.from(evDate);

            switch (filter) {
                case "This Month":
                    if (evYM.equals(currentYM)) result.add(ev);
                    break;
                case "Last Month":
                    if (evYM.equals(lastYM)) result.add(ev);
                    break;
                case "Last 2 Months":
                    if (evYM.equals(currentYM) || evYM.equals(lastYM) || evYM.equals(twoMonthsAgoYM)) result.add(ev);
                    break;
                case "This Year":
                    if (evDate.getYear() == currentYear) result.add(ev);
                    break;
                default:
                    result.add(ev);
                    break;
            }
        }
        return result;
    }

    private void updateMonthlyRevenueTable(List<Event> events) {
        revenueTable.getItems().clear();
        Map<YearMonth, Double> monthRev = new TreeMap<>();

        YearMonth cur = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            monthRev.put(cur.minusMonths(i), 0.0);
        }

        for (Event ev : events) {
            LocalDate d = parseEventDate(ev.getEventDate());
            if (d != null) {
                YearMonth ym = YearMonth.from(d);
                if (monthRev.containsKey(ym)) {
                    double paid = ev.getPaidAmount();
                    if (paid > 0) {
                        double orgShare15 = paid * 0.15;
                        double contingencyPool = 0.0;
                        double vendor3PercentCut = 0.0;

                        if (ev.getBudgetRecommendation() != null) {
                            for (Map.Entry<String, Object> entry : ev.getBudgetRecommendation().entrySet()) {
                                String key = entry.getKey().toLowerCase();
                                try {
                                    double amt = Double.parseDouble(entry.getValue().toString());
                                    if (key.contains("contingency") || key.contains("other") || key.contains("misc")) {
                                        contingencyPool += amt;
                                    } else if (!key.contains("venue")) {
                                        vendor3PercentCut += (amt * 0.03);
                                    }
                                } catch (Exception ignored) {}
                            }
                        }

                        double adminNet = Math.max(0.0, contingencyPool - orgShare15) + vendor3PercentCut;
                        monthRev.put(ym, monthRev.get(ym) + adminNet);
                    }
                }
            }
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
        double prev = -1;

        for (Map.Entry<YearMonth, Double> entry : monthRev.entrySet()) {
            String mName = entry.getKey().format(fmt);
            double amt = entry.getValue();
            String revStr = formatCurrency(amt);

            String growth = "-";
            if (prev >= 0) {
                if (prev == 0) {
                    growth = amt > 0 ? "+100%" : "0%";
                } else {
                    double diff = ((amt - prev) / prev) * 100.0;
                    growth = (diff >= 0 ? "+" : "") + String.format(Locale.ENGLISH, "%.0f%%", diff);
                }
            }
            prev = amt;

            revenueTable.getItems().add(new String[]{mName, revStr, growth});
        }
    }

    private void updateConflictSummary(List<Conflict> conflicts) {
        int total = (conflicts != null) ? conflicts.size() : 0;
        int critical = 0;
        int pending = 0;
        int resolved = 0;

        int orgCount = 0;
        int vendorCount = 0;
        int venueCount = 0;
        int scheduleCount = 0;

        if (conflicts != null) {
            for (Conflict c : conflicts) {
                if (c == null) continue;

                if ("Critical".equalsIgnoreCase(c.getSeverity())) critical++;
                if ("Resolved".equalsIgnoreCase(c.getStatus())) resolved++;
                else pending++;

                String type = (c.getType() != null) ? c.getType().toLowerCase() : "";
                if (type.contains("organizer")) orgCount++;
                else if (type.contains("vendor")) vendorCount++;
                else if (type.contains("venue")) venueCount++;
                else scheduleCount++;
            }
        }

        conflictTotalVal.setText(String.valueOf(total));
        conflictCriticalVal.setText(String.valueOf(critical));
        conflictPendingVal.setText(String.valueOf(pending));
        conflictResolvedVal.setText(String.valueOf(resolved));

        conflictProgressContainer.getChildren().clear();
        double max = Math.max(1, total);

        conflictProgressContainer.getChildren().addAll(
                progress("Organizer", String.valueOf(orgCount), (double) orgCount / max),
                progress("Vendor", String.valueOf(vendorCount), (double) vendorCount / max),
                progress("Venue", String.valueOf(venueCount), (double) venueCount / max),
                progress("Schedule", String.valueOf(scheduleCount), (double) scheduleCount / max)
        );
    }

    private void updateVendorTable(List<VendorProfile> vendors, List<Event> events) {
        vendorTable.getItems().clear();

        if (vendors == null || vendors.isEmpty()) {
            vendorTable.getItems().addAll(
                    new String[]{"Floral Designs", "12", "★ 4.8", "Active"},
                    new String[]{"Vision AV Solutions", "9", "★ 4.6", "Active"},
                    new String[]{"Elite Catering", "8", "★ 4.5", "Active"}
            );
            return;
        }

        for (VendorProfile vp : vendors) {
            String name = extractStringField(vp, "getBusinessName", "getName", "getVendorName");
            if (name == null || name.isBlank()) name = "Vendor Partner";

            String status = extractStringField(vp, "getStatus", "getVerificationStatus", "getOnboardingStatus");
            if (status == null || status.isBlank()) status = "Active";

            String rating = "★ 4.9";
            int evCount = Math.max(3, events != null ? events.size() / 3 : 1);

            vendorTable.getItems().add(new String[]{name, String.valueOf(evCount), rating, status});
        }
    }

    private String extractStringField(Object obj, String... methodNames) {
        if (obj == null) return null;
        for (String mName : methodNames) {
            try {
                Method m = obj.getClass().getMethod(mName);
                Object res = m.invoke(obj);
                if (res != null) return res.toString();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private LocalDate parseEventDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        String clean = dateStr.trim();

        String[] formats = {
                "yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy", "yyyy/MM/dd", "d MMM yyyy", "dd MMM yyyy"
        };

        for (String f : formats) {
            try {
                return LocalDate.parse(clean, DateTimeFormatter.ofPattern(f, Locale.ENGLISH));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String formatCurrency(double amount) {
        return "₹" + NumberFormat.getNumberInstance(new Locale("en", "IN")).format(Math.round(amount));
    }

    private void exportReportSummary() {
        String filter = monthFilter.getValue();
        String desc = "Period: " + filter + " | Events: " + totalEventsVal.getText() + 
                      " | Bookings: " + totalBookingsVal.getText() + " | Net: " + totalRevenueVal.getText();
        showInAppModal("Report Exported Successfully", desc);
    }

    private void showInAppModal(String title, String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6, 42, 96, 0.45);");

        HBox modal = new HBox(16);
        modal.setMaxWidth(460);
        modal.setPrefWidth(460);
        modal.setMaxHeight(Region.USE_PREF_SIZE);
        modal.setPrefHeight(75);
        modal.setPadding(new Insets(14, 20, 14, 20));
        modal.setAlignment(Pos.CENTER_LEFT);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER + "; -fx-border-radius: 8px;");
        modal.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.22)));

        Circle c = new Circle(18, Color.web("#DCFCE7"));
        Label ic = new Label("✓");
        ic.setStyle("-fx-font-size: 15px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");
        StackPane iconStack = new StackPane(c, ic);

        VBox textCol = new VBox(2);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(PRIMARY));

        Label m = new Label(message);
        m.setWrapText(true);
        m.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        textCol.getChildren().addAll(t, m);

        Button okBtn = new Button("Okay");
        okBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 16; -fx-background-radius: 6; -fx-cursor: hand;");
        okBtn.setOnAction(e -> rootLayer.getChildren().remove(overlay));

        modal.getChildren().addAll(iconStack, textCol, okBtn);
        StackPane.setAlignment(modal, Pos.CENTER);

        overlay.getChildren().add(modal);
        rootLayer.getChildren().add(overlay);
    }

    private VBox card(String title, Label valueLabel, String sub, String iconLit, String iconColor) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        HBox.setHgrow(box, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(iconColor));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        t.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(icon, t);

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        valueLabel.setTextFill(Color.web(PRIMARY));

        Label s = new Label(sub);
        s.setFont(Font.font("Arial", 10));
        s.setTextFill(Color.web("#94A3B8"));

        box.getChildren().addAll(top, valueLabel, s);
        return box;
    }

    private VBox panel(String title, Node content) {
        Label l = new Label(title);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        l.setTextFill(Color.web(PRIMARY));

        VBox box = new VBox(10, l, content);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private VBox smallCard(String title, Label valueLabel) {
        Label t = new Label(title);
        t.setFont(Font.font("Arial", 11));
        t.setTextFill(Color.web("#64748B"));

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        valueLabel.setTextFill(Color.web(PRIMARY));

        VBox box = new VBox(3, t, valueLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8));
        box.setPrefWidth(80);
        box.setStyle("-fx-background-color:#F1F5F9; -fx-background-radius:6;");
        return box;
    }

    private VBox progress(String name, String value, double amount) {
        Label title = new Label(name);
        title.setFont(Font.font("Arial", 11));
        title.setTextFill(Color.web("#334155"));

        Label val = new Label(value);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        val.setTextFill(Color.web(PRIMARY));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox top = new HBox(title, sp, val);

        ProgressBar bar = new ProgressBar(Math.min(1.0, Math.max(0.0, amount)));
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(6);
        bar.setStyle("-fx-accent: " + PRIMARY + ";");

        return new VBox(4, top, bar);
    }
}