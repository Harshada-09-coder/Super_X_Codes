package com.techtrio.view;

import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicesPackagesV {

    private Scene servicesPackagesVScene;
    private VendorDashboardV vendorDashboard;
    private String uid;
    private String idToken;

    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_TEXT = "#16A34A";

    private static class ServiceData {
        String name;
        String desc;
        String price;
        int packages;
        int bookings;
        double rating;
        boolean isActive;
        String iconLiteral;

        ServiceData(String name, String desc, String price, int packages, int bookings, double rating, boolean isActive, String iconLiteral) {
            this.name = name;
            this.desc = desc;
            this.price = price;
            this.packages = packages;
            this.bookings = bookings;
            this.rating = rating;
            this.isActive = isActive;
            this.iconLiteral = iconLiteral;
        }
    }

    private final List<ServiceData> serviceList = new ArrayList<>();
    private GridPane servicesGridContainer;
    private String selectedCategoryFilter = "All Services";
    private StackPane modalOverlayPane;

    public ServicesPackagesV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ServicesPackagesV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getservicesPackagesVScene(Stage primaryStage, Runnable callBackAction) {
        initSampleServices();

        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane mainLayout = new BorderPane();
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        mainLayout.setLeft(dashHelper.createCommonVendorSidebar("Services & Packages"));

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
                createServicesGridSection()
        );

        scrollPane.setContent(mainContent);
        mainLayout.setCenter(scrollPane);

        modalOverlayPane = new StackPane();
        modalOverlayPane.setStyle("-fx-background-color: rgba(6, 27, 65, 0.45);");
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlayPane);

        servicesPackagesVScene = new Scene(rootStack, 1550, 785);
        return servicesPackagesVScene;
    }

    private void initSampleServices() {
        serviceList.clear();
        serviceList.add(new ServiceData("Decoration", "Stage, floral, entrance & theme setups.", "₹ 15,000", 3, 12, 4.9, true, "fas-palette"));
        serviceList.add(new ServiceData("Sound System", "Live audio, DJ, microphones & sound tech.", "₹ 8,000", 3, 18, 4.9, true, "fas-volume-up"));
        serviceList.add(new ServiceData("Photography & Video", "Cinematography, 4K video & candid photo.", "₹ 20,000", 3, 15, 4.8, true, "fas-camera"));
        serviceList.add(new ServiceData("Rental Equipment", "Tables, chairs, stage, lighting & LED walls.", "₹ 2,000", 4, 21, 4.8, true, "fas-couch"));
        serviceList.add(new ServiceData("Invitation & Printing", "Custom cards, digital invites & banners.", "₹ 1,500", 2, 8, 4.7, true, "fas-envelope-open-text"));
        serviceList.add(new ServiceData("Catering", "Buffet, customized multi-cuisine menu options.", "₹ 350 / guest", 3, 14, 4.9, true, "fas-utensils"));
        serviceList.add(new ServiceData("Anchor", "Event hosting & celebrity emcee management.", "₹ 5,000", 2, 7, 4.8, true, "fas-microphone-alt"));
        serviceList.add(new ServiceData("Cake & Desserts", "Custom celebratory cakes & pastry setups.", "₹ 2,500", 2, 9, 4.9, true, "fas-birthday-cake"));
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Services & Packages");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addServiceBtn = new Button("+ Add New Service");
        FontIcon plusIcon = new FontIcon("fas-plus");
        plusIcon.setIconSize(11);
        plusIcon.setIconColor(Color.WHITE);
        addServiceBtn.setGraphic(plusIcon);
        addServiceBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> showInAppAddServiceModal());

        titleRow.getChildren().addAll(title, spacer, addServiceBtn);

        Label subtitle = new Label("Manage listed service categories, standard pricing and package configurations.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        row.getChildren().addAll(
                createSummaryCard("Active Services", "8", "Listed Categories", "fas-cubes", PRIMARY_BLUE),
                createSummaryCard("Active Packages", "16", "Across All Categories", "fas-box-open", PRIMARY_BLUE),
                createSummaryCard("Total Bookings", "38", "Completed & Live", "fas-calendar-check", GREEN_TEXT),
                createSummaryCard("Average Rating", "4.8 ★", "Verified Client Reviews", "fas-star", PRIMARY_BLUE)
        );
        return row;
    }

    private VBox createSummaryCard(String title, String val, String sub, String iconLit, String colorHex) {
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

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, v, s);
        return card;
    }

    private VBox createFilterSection() {
        VBox card = createCardContainer();
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search listed services...");
        searchInput.setPrefHeight(36);
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 0 12;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        ComboBox<String> categoryCB = new ComboBox<>();
        categoryCB.getItems().addAll("All Services", "Decoration", "Sound System", "Photography & Video", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Desserts");
        categoryCB.setValue("All Services");
        categoryCB.setPrefHeight(36);
        categoryCB.setOnAction(e -> {
            selectedCategoryFilter = categoryCB.getValue();
            renderServicesGrid();
        });

        row.getChildren().addAll(searchInput, categoryCB);
        card.getChildren().add(row);
        return card;
    }

    private VBox createServicesGridSection() {
        VBox container = new VBox(16);

        servicesGridContainer = new GridPane();
        servicesGridContainer.setHgap(16);
        servicesGridContainer.setVgap(16);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            servicesGridContainer.getColumnConstraints().add(cc);
        }

        renderServicesGrid();
        container.getChildren().add(servicesGridContainer);
        return container;
    }

    private void renderServicesGrid() {
        servicesGridContainer.getChildren().clear();

        List<ServiceData> filtered = serviceList.stream().filter(s -> {
            if (selectedCategoryFilter.equals("All Services")) return true;
            return s.name.toLowerCase().contains(selectedCategoryFilter.toLowerCase());
        }).collect(Collectors.toList());

        int col = 0;
        int row = 0;

        for (ServiceData s : filtered) {
            servicesGridContainer.add(createServiceCard(s), col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createServiceCard(ServiceData s) {
        VBox card = createCardContainer();
        card.setPadding(new Insets(16));

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(s.iconLiteral);
        icon.setIconSize(18);
        icon.setIconColor(Color.web(PRIMARY_BLUE));

        StackPane iconHolder = new StackPane(icon);
        iconHolder.setPrefSize(36, 36);
        iconHolder.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        VBox titleBox = new VBox(2);
        Label title = new Label(s.name);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label desc = new Label(s.desc);
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, desc);

        topRow.getChildren().addAll(iconHolder, titleBox);

        HBox statsRow = new HBox(12);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        VBox pBox = new VBox(1);
        Label pHead = new Label("STARTING AT");
        pHead.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label pVal = new Label(s.price);
        pVal.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        pBox.getChildren().addAll(pHead, pVal);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label ratingTag = new Label(s.rating + " ★");
        ratingTag.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #92400E;");

        statsRow.getChildren().addAll(pBox, sp, ratingTag);

        Button viewPkgsBtn = new Button("View Packages (" + s.packages + ")");
        viewPkgsBtn.setMaxWidth(Double.MAX_VALUE);
        viewPkgsBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 10; -fx-background-radius: 4px; -fx-cursor: hand;");
        viewPkgsBtn.setOnAction(e -> showInAppPackagesModal(s));

        card.getChildren().addAll(topRow, new Separator(), statsRow, viewPkgsBtn);
        return card;
    }

    // ⭐ In-App Pop-up Overlay for Adding Service
    private void showInAppAddServiceModal() {
        modalOverlayPane.getChildren().clear();

        VBox card = new VBox(12);
        card.setMaxWidth(380);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 16, 0, 0, 4);");

        Label title = new Label("Add New Service Category");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        ComboBox<String> nameCB = new ComboBox<>();
        nameCB.getItems().addAll("Decoration", "Sound System", "Photography & Video", "Rental Equipment", "Invitation & Printing", "Catering", "Anchor", "Cake & Desserts");
        nameCB.setValue("Sound System");
        nameCB.setMaxWidth(Double.MAX_VALUE);

        TextField priceInput = new TextField();
        priceInput.setPromptText("Starting Price (e.g. ₹ 10,000)");

        HBox btns = new HBox(8);
        btns.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 14; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeInAppModal());

        Button saveBtn = new Button("Add Service");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            serviceList.add(new ServiceData(nameCB.getValue(), "Custom verified vendor service package.", priceInput.getText().isEmpty() ? "₹ 10,000" : priceInput.getText(), 2, 0, 5.0, true, "fas-box-open"));
            renderServicesGrid();
            closeInAppModal();
        });

        btns.getChildren().addAll(cancelBtn, saveBtn);
        card.getChildren().addAll(title, new Separator(), new Label("Category:"), nameCB, new Label("Starting Price:"), priceInput, btns);

        modalOverlayPane.getChildren().add(card);
        modalOverlayPane.setVisible(true);
        modalOverlayPane.setManaged(true);
    }

    // ⭐ In-App Pop-up Overlay for Viewing Packages
    private void showInAppPackagesModal(ServiceData s) {
        modalOverlayPane.getChildren().clear();

        VBox card = new VBox(12);
        card.setMaxWidth(400);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 16, 0, 0, 4);");

        Label title = new Label("Packages for " + s.name);
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox p1 = new VBox(2, new Label("Standard Package (" + s.price + ")"), new Label("Includes standard setup and certified equipment."));
        p1.setPadding(new Insets(8));
        p1.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 6px; -fx-border-radius: 6px;");

        VBox p2 = new VBox(2, new Label("Premium Package (₹ 25,000)"), new Label("Complete full-day support with technical team."));
        p2.setPadding(new Insets(8));
        p2.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 6px; -fx-border-radius: 6px;");

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> closeInAppModal());

        HBox btnBox = new HBox(closeBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, new Separator(), p1, p2, btnBox);

        modalOverlayPane.getChildren().add(card);
        modalOverlayPane.setVisible(true);
        modalOverlayPane.setManaged(true);
    }

    private void closeInAppModal() {
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);
        modalOverlayPane.getChildren().clear();
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}