package com.techtrio.view;

import java.util.List;
import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.model.VendorProfile;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VendorDetailsA {

    private Scene vendorDetailScene;
    private final String PRIMARY = "#002A60", LIGHT = "#C2E0F4", BG = "#F8FAFC", BORDER = "#E2E8F0";

    private Label avatarLetterLabel;
    private Label vendorNameLabel;
    private Label vendorTypeTag;
    private Label vendorStatusTag;
    private Label registeredDateLabel;

    private Label emailValLabel;
    private Label phoneValLabel;
    private Label addressValLabel;
    private Label websiteValLabel;

    private Label totalEventsServedVal;
    private Label completedEventsVal;
    private Label upcomingEventsVal;
    private Label cancelledEventsVal;

    private Label infoTypeVal;
    private Label infoCatVal;
    private Label infoPriceRangeVal;
    private Label infoCapacityVal;
    private Label infoServiceAreaVal;

    private Label currentStatusVal;
    private Label activeEventsCountVal;
    private Label upcomingBookingsCountVal;

    private TableView<VendorBooking> historyTable;
    private ObservableList<VendorBooking> historyData = FXCollections.observableArrayList();

    public Scene getvendorDetailScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Unified Dashboard Sidebar
        root.setLeft(createAdminSidebar("Vendors"));

        // 2. Main Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Button backBtn = new Button("← Back to Vendors");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0 0 4 0;");
        backBtn.setOnAction(e -> {
            Stage stage = (Stage) backBtn.getScene().getWindow();
            NavigationManager.navigate("Vendors", stage);
        });

        Label title = new Label("Vendor Profile & Performance");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label sub = new Label("Complete overview of vendor service capabilities, ratings and active booking pipeline.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));

        VBox heading = new VBox(4, backBtn, title, sub);

        VBox left = new VBox(16, vendorProfileCard(), contactCard());
        left.setPrefWidth(320);

        totalEventsServedVal = new Label("0");
        completedEventsVal = new Label("0");
        upcomingEventsVal = new Label("0");
        cancelledEventsVal = new Label("0");

        HBox stats = new HBox(12,
                createKpiCard("TOTAL BOOKINGS", totalEventsServedVal, "fas-receipt", PRIMARY),
                createKpiCard("COMPLETED", completedEventsVal, "fas-check-circle", "#16A34A"),
                createKpiCard("UPCOMING / ONGOING", upcomingEventsVal, "fas-clock", "#0284C7"),
                createKpiCard("CANCELLED", cancelledEventsVal, "fas-times-circle", "#DC2626")
        );

        HBox middle = new HBox(16, serviceCard(), availabilityCard());
        HBox.setHgrow(middle.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(middle.getChildren().get(1), Priority.ALWAYS);

        VBox right = new VBox(16, stats, middle, historyCard());
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox main = new HBox(20, left, right);

        content.getChildren().addAll(heading, main);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadVendorDetailsAsync();

        vendorDetailScene = new Scene(root, 1550, 785);
        return vendorDetailScene;
    }

    private VBox vendorProfileCard() {
        VBox v = createCard();
        v.setAlignment(Pos.CENTER);

        avatarLetterLabel = new Label("V");
        avatarLetterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        avatarLetterLabel.setTextFill(Color.web(PRIMARY));
        avatarLetterLabel.setAlignment(Pos.CENTER);
        avatarLetterLabel.setPrefSize(90, 90);
        avatarLetterLabel.setStyle("-fx-background-color: " + LIGHT + "; -fx-background-radius: 50;");

        vendorNameLabel = new Label("Loading...");
        vendorNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        vendorNameLabel.setTextFill(Color.web(PRIMARY));

        vendorTypeTag = new Label("Audio-Visual");
        vendorTypeTag.setStyle("-fx-background-color: #E2E8F0; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px;");

        vendorStatusTag = new Label("● Active");
        vendorStatusTag.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox tags = new HBox(8, vendorTypeTag, vendorStatusTag);
        tags.setAlignment(Pos.CENTER);

        registeredDateLabel = new Label("Registered: Aug 2026");
        registeredDateLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");

        v.getChildren().addAll(avatarLetterLabel, vendorNameLabel, tags, new Separator(), registeredDateLabel);
        return v;
    }

    private VBox contactCard() {
        VBox v = createCard();
        emailValLabel = new Label("✉ Email: Loading...");
        phoneValLabel = new Label("☎ Phone: Loading...");
        addressValLabel = new Label("📍 Location: Loading...");
        websiteValLabel = new Label("🌐 Website: www.eventxpert.com");

        for (Label l : new Label[]{emailValLabel, phoneValLabel, addressValLabel, websiteValLabel}) {
            l.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        }

        Label h = new Label("Contact Information");
        h.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        h.setTextFill(Color.web(PRIMARY));

        v.getChildren().addAll(h, new Separator(), emailValLabel, phoneValLabel, addressValLabel, websiteValLabel);
        return v;
    }

    private VBox serviceCard() {
        VBox v = createCard();
        Label title = new Label("Service Capabilities");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        infoTypeVal = new Label("Audio-Visual");
        infoCatVal = new Label("Event AV");
        infoPriceRangeVal = new Label("₹25k – ₹80k");
        infoCapacityVal = new Label("Up to 500 Guests");
        infoServiceAreaVal = new Label("Pune, Maharashtra");

        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(8);
        g.add(new Label("Type:"), 0, 0); g.add(infoTypeVal, 1, 0);
        g.add(new Label("Category:"), 0, 1); g.add(infoCatVal, 1, 1);
        g.add(new Label("Pricing:"), 0, 2); g.add(infoPriceRangeVal, 1, 2);
        g.add(new Label("Capacity:"), 0, 3); g.add(infoCapacityVal, 1, 3);
        g.add(new Label("Coverage:"), 0, 4); g.add(infoServiceAreaVal, 1, 4);

        v.getChildren().addAll(title, new Separator(), g);
        return v;
    }

    private VBox availabilityCard() {
        VBox v = createCard();
        Label title = new Label("Workload & Rating");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        currentStatusVal = new Label("● Available for Assignment");
        currentStatusVal.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 12px;");

        activeEventsCountVal = new Label("0");
        upcomingBookingsCountVal = new Label("0");

        Label ratingLabel = new Label("Vendor Rating: ★ 4.8 / 5.0 (Verified Service Partner)");
        ratingLabel.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-padding: 6 10; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 11px;");

        v.getChildren().addAll(title, new Separator(), currentStatusVal, ratingLabel);
        return v;
    }

    private VBox historyCard() {
        VBox v = createCard();
        Label title = new Label("Event Assignment History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(PRIMARY));

        historyTable = new TableView<>();
        historyTable.setItems(historyData);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPrefHeight(200);

        TableColumn<VendorBooking, String> nameCol = new TableColumn<>("EVENT NAME");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEventName() != null ? d.getValue().getEventName() : "Event"));

        TableColumn<VendorBooking, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEventDate() != null ? d.getValue().getEventDate() : "Upcoming"));

        TableColumn<VendorBooking, String> custCol = new TableColumn<>("ORGANIZER");
        custCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrganizerName() != null ? d.getValue().getOrganizerName() : "Organizer"));

        TableColumn<VendorBooking, String> amtCol = new TableColumn<>("ALLOCATED AMOUNT");
        amtCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAmount() != null ? d.getValue().getAmount() : "₹ 0"));

        TableColumn<VendorBooking, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus() != null ? d.getValue().getStatus() : "CONFIRMED"));

        historyTable.getColumns().addAll(nameCol, dateCol, custCol, amtCol, statusCol);
        v.getChildren().addAll(title, historyTable);
        return v;
    }

    // ⭐ ACCURATE REAL-TIME VENDOR COMPLETED EVENTS MAPPING
    private void loadVendorDetailsAsync() {
        String selectedVendorId = AdminContext.getSelectedVendorId();
        if (selectedVendorId == null || selectedVendorId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                VendorProfile v = controller.getVendorProfile(selectedVendorId);
                VendorController vendorController = new VendorController();
                List<VendorBooking> bookings = vendorController.getVendorBookings(selectedVendorId);

                if (v != null) {
                    Platform.runLater(() -> {
                        String bName = v.getBusinessName() != null && !v.getBusinessName().isBlank()
                                ? v.getBusinessName()
                                : (v.getFullName() != null ? v.getFullName() : v.getName());

                        if (bName == null || bName.isBlank()) bName = "Vendor";

                        vendorNameLabel.setText(bName);
                        avatarLetterLabel.setText(bName.substring(0, 1).toUpperCase());

                        String cat = v.getCategory() != null ? v.getCategory() : "Audio-Visual";
                        vendorTypeTag.setText(cat);
                        infoTypeVal.setText(cat);
                        infoCatVal.setText(cat);

                        emailValLabel.setText("✉ Email: " + (v.getEmail() != null ? v.getEmail() : "vendor@eventxpert.com"));
                        phoneValLabel.setText("☎ Phone: " + (v.getPhone() != null ? v.getPhone() : (v.getMobileNo() != null ? v.getMobileNo() : "N/A")));
                        addressValLabel.setText("📍 Location: " + (v.getAddress() != null ? v.getAddress() : "Pune, Maharashtra"));
                        websiteValLabel.setText("🌐 Website: www." + bName.toLowerCase().replaceAll("\\s+", "") + ".com");

                        historyData.clear();
                        int completed = 0;
                        int upcoming = 0;
                        int cancelled = 0;

                        if (bookings != null && !bookings.isEmpty()) {
                            for (VendorBooking b : bookings) {
                                String status = b.getStatus() != null ? b.getStatus().trim().toUpperCase() : "CONFIRMED";

                                if ("COMPLETED".equals(status)) {
                                    completed++;
                                } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                                    cancelled++;
                                } else {
                                    upcoming++;
                                }
                                historyData.add(b);
                            }
                        }

                        totalEventsServedVal.setText(String.valueOf(bookings != null ? bookings.size() : 0));
                        completedEventsVal.setText(String.valueOf(completed));
                        upcomingEventsVal.setText(String.valueOf(upcoming));
                        cancelledEventsVal.setText(String.valueOf(cancelled));
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createCard() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        return v;
    }

    private VBox createKpiCard(String title, Label valLbl, String iconLit, String colorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(colorHex));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        t.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(icon, t);

        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valLbl.setTextFill(Color.web(PRIMARY));

        card.getChildren().addAll(top, valLbl);
        return card;
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
}