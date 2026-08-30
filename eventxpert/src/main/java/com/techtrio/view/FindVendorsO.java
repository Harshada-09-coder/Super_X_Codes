package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', sans-serif;");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 30, 30, 30));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        VBox headerBox = createPageHeader(stage);
        VBox categoryHeaderCard = createCategoryHeaderCard();
        VBox resultsContainer = createResultsContainer(stage);

        mainContent.getChildren().addAll(headerBox, categoryHeaderCard, resultsContainer);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadRealVendorsFromFirestoreAsync(stage);

        return new Scene(root, 1550, 785);
    }

    private void loadRealVendorsFromFirestoreAsync(Stage stage) {
        Task<List<VendorProfile>> task = new Task<>() {
            @Override
            protected List<VendorProfile> call() {
                VendorProfileService service = new VendorProfileService();
                return service.getAllVendors();
            }
        };

        task.setOnSucceeded(evt -> {
            realVendorsList = task.getValue();
            Platform.runLater(() -> renderVendorCards(stage));
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("My Events  >  TechVision Annual Summit  >  Vendors  >  Find Vendors");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Find Vendors");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label eventTag = new Label("● TechVision Annual Summit • Pune • 18 May 2026 • 250 Guests");
        eventTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16px; -fx-font-size: 12px;");

        Button backBtn = createSecondaryButton("← Back to Vendor Management");
        backBtn.setOnAction(e -> handleBack(stage));

        titleRow.getChildren().addAll(title, backBtn, spacer, eventTag);

        Label subtitle = new Label("Discover trusted vendors that match your event requirements, budget and location.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    private VBox createCategoryHeaderCard() {
        VBox card = createCardContainer();
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label catLbl = new Label("Category Selected: ");
        catLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label categoryTag = new Label(selectedCategory);
        categoryTag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 6 16; -fx-background-radius: 16px;");

        row.getChildren().addAll(catLbl, categoryTag);
        card.getChildren().add(row);
        return card;
    }

    private VBox createResultsContainer(Stage stage) {
        VBox box = new VBox(16);

        resultsCountLabel = new Label("Loading Firestore Vendors...");
        resultsCountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

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

                    if (vCat.contains(filterCat) || filterCat.contains(vCat)) {
                        return true;
                    }
                    if (filterCat.contains("sound") && (vCat.contains("sound") || vCat.contains("audio") || vCat.contains("av"))) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        resultsCountLabel.setText(filtered.size() + " Vendors Found for Category [" + selectedCategory + "]");

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
        VBox card = createCardContainer();
        card.setPrefWidth(280);

        String bName = v.getBusinessName() != null && !v.getBusinessName().isBlank() ? v.getBusinessName() : (v.getFullName() != null ? v.getFullName() : v.getName());
        String cat = v.getCategory() != null ? v.getCategory() : selectedCategory;

        Label name = new Label(bName != null ? bName : "Vendor");
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: " + NAVY + ";");

        Label categoryLbl = new Label(cat + " • 📍 " + (v.getAddress() != null ? v.getAddress() : "Pune"));
        categoryLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label contactLbl = new Label("📞 " + (v.getPhone() != null && !v.getPhone().isBlank() ? v.getPhone() : (v.getMobileNo() != null ? v.getMobileNo() : "N/A")));
        contactLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        Button requestQuoteBtn = createPrimaryButton("Request Quote & Assign");
        requestQuoteBtn.setMaxWidth(Double.MAX_VALUE);

        requestQuoteBtn.setOnAction(e -> {
            String selectedEvtId = AdminContext.getSelectedEventId();
            if (selectedEvtId == null || selectedEvtId.isBlank()) {
                selectedEvtId = "EVT_DEFAULT";
            }

            final String evtId = selectedEvtId;

            AdminController adminController = new AdminController();
            Event event = adminController.getEventById(evtId);
            String evtName = event != null && event.getEventName() != null ? event.getEventName() : "Corporate Event";
            String evtDate = event != null && event.getEventDate() != null ? event.getEventDate() : "18 May 2026";
            int evtGuests = event != null ? event.getGuests() : 250;
            String evtLoc = event != null && event.getCity() != null ? event.getCity() : "Pune";

            VendorBooking booking = new VendorBooking(
                    "BKG_" + System.currentTimeMillis(),
                    evtId,
                    evtName,
                    evtDate,
                    uid,
                    "Organizer",
                    v.getVendorId(),
                    bName,
                    cat,
                    "₹75,000",
                    evtGuests,
                    evtLoc
            );

            VendorController vendorController = new VendorController();
            boolean success = vendorController.requestVendorBooking(booking);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking request sent to Vendor [" + bName + "] in Firestore!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to send booking request.");
                alert.showAndWait();
            }
        });

        card.getChildren().addAll(name, categoryLbl, contactLbl, new Separator(), requestQuoteBtn);
        return card;
    }

    private void handleBack(Stage stage) {
        if (backAction != null) {
            backAction.run();
        } else {
            VendorManagementO view = new VendorManagementO(uid, idToken);
            LandingPage.landingPageStage.setScene(view.getvendorManagementOScene(() -> goDashboard(stage)));
        }
    }

    private void goDashboard(Stage stage) {
        OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));

        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox menu = new VBox(6);
        menu.getChildren().add(createNavBtn("Dashboard", false, () -> goDashboard(stage)));
        menu.getChildren().add(createNavBtn("Vendors", true, () -> handleBack(stage)));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brand, new Separator(), menu, spacer);
        return sidebar;
    }

    private Button createNavBtn(String title, boolean active, Runnable action) {
        Button b = new Button(title);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(active ? "-fx-background-color: #EAF3FF; -fx-text-fill: #0B4EA2; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6px;"
                : "-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 8 12; -fx-cursor: hand;");
        if (action != null) b.setOnAction(e -> action.run());
        return b;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label searchLabel = new Label("Find Vendors");
        searchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        topBar.getChildren().add(searchLabel);
        return topBar;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 5 11; -fx-cursor: hand;");
        return btn;
    }
}