package com.techtrio.view;

import java.util.List;
import com.techtrio.model.ConflictResult;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.util.StringConverter;

public class EventRequesReviewA {

    private Scene eventRequestReviewScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BACKGROUND = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private Label eventTypeSummary;
    private Label guestsSummary;
    private Label venueSummary;
    private Label locationSummary;
    private Label budgetSummary;
    private Label dateSummary;

    private Label eventNameBox;
    private Label durationBox;
    private Label startTimeBox;
    private Label dateBox;

    private Label customerAvatar;
    private Label customerName;
    private Label customerEmail;
    private Label customerMobile;

    private Label themeReq;
    private Label cateringReq;
    private Label notesReq;

    private TextArea notesArea;

    // ⭐ In-App Modal Overlay Layer (Replaces Dialog / Separate Window)
    private StackPane modalOverlayPane;

    public Scene geteventRequestReviewScene() {

        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color:" + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // SIDEBAR
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
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
        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.web(LIGHT));
        admin.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        brandTexts.getChildren().addAll(logo, admin);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] items = {
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

        for (String[] item : items) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals("Event Requests")));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        // SIDEBAR BOTTOM NAVIGATION
        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false),
                createSidebarButton("Profile", "fas-user", false),
                createSidebarButton("Settings", "fas-cog", false),
                createSidebarButton("Support", "fas-question-circle", false)
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);

        // MAIN CONTENT (TOPBAR REMOVED)
        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(24, 32, 28, 32));
        contentBox.setStyle("-fx-background-color:" + BACKGROUND + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Review Event Request");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));
        Label sub = new Label("Inspect customer requirements and assign the optimal organizer.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#64748B"));
        titleBox.getChildren().addAll(title, sub);

        Region ts = new Region();
        HBox.setHgrow(ts, Priority.ALWAYS);

        Button back = new Button("← Back to Requests");
        back.setStyle("-fx-background-color:white;-fx-border-color:" + PRIMARY + ";-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-font-size:11px;-fx-padding:6 14;-fx-border-radius:6;-fx-cursor:hand;");
        back.setOnAction(e -> {
            EventRequestA requestsView = new EventRequestA();
            LandingPage.landingPageStage.setScene(requestsView.geteventRequestScene());
        });

        titleRow.getChildren().addAll(titleBox, ts, back);

        // SUMMARY BAR
        eventTypeSummary = new Label("Loading...");
        guestsSummary = new Label("Loading...");
        venueSummary = new Label("Loading...");
        locationSummary = new Label("Loading...");
        budgetSummary = new Label("Loading...");
        dateSummary = new Label("Loading...");

        HBox summary = new HBox(12,
                summaryItem("EVENT TYPE", eventTypeSummary),
                summaryItem("GUESTS", guestsSummary),
                summaryItem("VENUE", venueSummary),
                summaryItem("LOCATION", locationSummary),
                summaryItem("BUDGET", budgetSummary),
                summaryItem("EVENT DURATION", dateSummary)
        );
        summary.setPadding(new Insets(10, 16, 10, 16));
        summary.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        // EVENT DETAILS GRID
        VBox eventCard = new VBox(10);
        eventCard.setPadding(new Insets(16));
        eventCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        Label eHeader = new Label("Event Information");
        eHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        eHeader.setTextFill(Color.web(PRIMARY));

        GridPane eg = new GridPane();
        eg.setHgap(16); eg.setVgap(12);

        eventNameBox = new Label("Loading...");
        durationBox = new Label("Loading...");
        startTimeBox = new Label("Loading...");
        dateBox = new Label("Loading...");

        eg.add(box("Event Name", eventNameBox), 0, 0);
        eg.add(box("Event Duration (Dates)", dateBox), 1, 0);
        eg.add(box("Start - End Time", startTimeBox), 0, 1);
        eg.add(box("Total Duration", durationBox), 1, 1);

        eventCard.getChildren().addAll(eHeader, eg);

        // REQUIREMENTS CARD
        VBox reqCard = new VBox(10);
        reqCard.setPadding(new Insets(16));
        reqCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        Label rHeader = new Label("Customer Requirements & Notes");
        rHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        rHeader.setTextFill(Color.web(PRIMARY));

        themeReq = new Label("Loading...");
        cateringReq = new Label("Loading...");
        notesReq = new Label("Loading notes...");
        notesReq.setWrapText(true);

        GridPane rg = new GridPane();
        rg.setHgap(16); rg.setVgap(12);
        rg.add(box("Category", themeReq), 0, 0);
        rg.add(box("Selected Services", cateringReq), 1, 0);
        rg.add(box("Special Instructions", notesReq), 0, 1, 2, 1);

        reqCard.getChildren().addAll(rHeader, rg);

        // CUSTOMER CARD
        VBox customerCard = new VBox(10);
        customerCard.setPadding(new Insets(16));
        customerCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        Label cHeader = new Label("Customer Information");
        cHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        cHeader.setTextFill(Color.web(PRIMARY));

        customerAvatar = new Label("C");
        customerAvatar.setAlignment(Pos.CENTER);
        customerAvatar.setPrefSize(40, 40);
        customerAvatar.setStyle("-fx-background-color:#DBEAFE;-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-background-radius:20;");

        customerName = new Label("Loading...");
        customerName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        customerEmail = new Label("Loading...");
        customerMobile = new Label("Loading...");

        VBox cInfo = new VBox(2, customerName, customerEmail, customerMobile);
        HBox custRow = new HBox(12, customerAvatar, cInfo);
        custRow.setAlignment(Pos.CENTER_LEFT);

        customerCard.getChildren().addAll(cHeader, custRow);

        // ADMIN ACTION CARD
        VBox reviewCard = new VBox(10);
        reviewCard.setPadding(new Insets(16));
        reviewCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        Label actHeader = new Label("Admin Action");
        actHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        actHeader.setTextFill(Color.web(PRIMARY));

        notesArea = new TextArea();
        notesArea.setPromptText("Add assignment instructions for the organizer...");
        notesArea.setWrapText(true);
        notesArea.setPrefRowCount(3);
        notesArea.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

        Button checkConflict = new Button("🔍 Check Scheduling Conflicts");
        checkConflict.setMaxWidth(Double.MAX_VALUE);
        checkConflict.setPrefHeight(36);
        checkConflict.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-border-radius:6;-fx-cursor:hand;");
        checkConflict.setOnAction(e -> checkEventConflict());

        Button approveBtn = new Button("✓  Approve Event");
        approveBtn.setMaxWidth(Double.MAX_VALUE);
        approveBtn.setPrefHeight(36);
        approveBtn.setStyle("-fx-background-color:#16A34A;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;");
        approveBtn.setOnAction(e -> handleApproveEvent());

        Button assign = new Button("Assign Organizer");
        assign.setMaxWidth(Double.MAX_VALUE);
        assign.setPrefHeight(36);
        assign.setStyle("-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;");
        assign.setOnAction(e -> showInAppAssignOrganizerModal());

        Button reject = new Button("Reject Request");
        reject.setMaxWidth(Double.MAX_VALUE);
        reject.setPrefHeight(36);
        reject.setStyle("-fx-background-color:white;-fx-border-color:#DC2626;-fx-text-fill:#DC2626;-fx-font-weight:bold;-fx-border-radius:6;-fx-cursor:hand;");
        reject.setOnAction(e -> {
            String selectedEvtId = AdminContext.getSelectedEventId();
            if (selectedEvtId == null || selectedEvtId.isBlank()) return;

            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to reject this request?", ButtonType.YES, ButtonType.NO);
            if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
            a.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    AdminController controller = new AdminController();
                    controller.updateEventStatus(selectedEvtId, "REJECTED");
                    EventRequestA requestsView = new EventRequestA();
                    LandingPage.landingPageStage.setScene(requestsView.geteventRequestScene());
                }
            });
        });

        reviewCard.getChildren().addAll(actHeader, notesArea, checkConflict, approveBtn, assign, reject);

        VBox leftCol = new VBox(14, eventCard, reqCard);
        VBox rightCol = new VBox(14, customerCard, reviewCard);
        leftCol.setPrefWidth(680);
        rightCol.setPrefWidth(360);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        HBox body = new HBox(16, leftCol, rightCol);
        contentBox.getChildren().addAll(titleRow, summary, body);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BACKGROUND + ";-fx-border-color:transparent;");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(scroll);

        // ⭐ Modal Overlay (Hidden by default)
        modalOverlayPane = new StackPane();
        modalOverlayPane.setStyle("-fx-background-color: rgba(0, 42, 96, 0.45);");
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlayPane);

        loadEventReviewDetailsAsync();

        eventRequestReviewScene = new Scene(rootStack, 1550, 785);
        return eventRequestReviewScene;
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
            btn.setStyle("-fx-background-color:" + LIGHT + ";-fx-text-fill:" + PRIMARY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }

    private void handleApproveEvent() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) {
            showAlert("No event selected for approval.");
            return;
        }

        AdminController controller = new AdminController();
        Event event = controller.getEventById(selectedEvtId);
        if (event == null) {
            showAlert("Event details could not be loaded.");
            return;
        }

        ConflictResult result = controller.checkEventConflict(event);
        if (result != null && result.isConflict()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Conflict Detected");
            alert.setHeaderText("⚠ Event cannot be approved");
            alert.setContentText("Another event already exists with the same venue, date and overlapping time.");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
            return;
        }

        boolean updated = controller.updateEventStatus(selectedEvtId, "APPROVED");
        if (updated) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event approved successfully in Firestore!");
            alert.setTitle("Event Approval");
            if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
            alert.showAndWait();
        } else {
            showAlert("Failed to update event status in Firestore.");
        }
    }

    // ⭐ BEAUTIFUL COMPACT IN-APP MODAL (NO SEPARATE WINDOW STAGE)
    private void showInAppAssignOrganizerModal() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) {
            showAlert("Please select an event to assign an organizer.");
            return;
        }

        AdminController controller = new AdminController();
        List<OrganizerProfile> activeOrganizers = controller.getAllActiveOrganizers();

        if (activeOrganizers == null || activeOrganizers.isEmpty()) {
            showAlert("No active organizers found in Firestore collection 'Organizers'.");
            return;
        }

        modalOverlayPane.getChildren().clear();
        modalOverlayPane.setAlignment(Pos.CENTER);

        VBox card = new VBox(14);
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: " + BORDER + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 18, 0, 0, 4);");

        StackPane.setAlignment(card, Pos.CENTER);

        HBox head = new HBox(8);
        head.setAlignment(Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon("fas-user-tie");
        icon.setIconSize(18);
        icon.setIconColor(Color.web(PRIMARY));
        Label title = new Label("Assign Event Organizer");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");
        head.getChildren().addAll(icon, title);

        Label sub = new Label("Select an active organizer organization from Firestore to assign this event:");
        sub.setWrapText(true);
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        ComboBox<OrganizerProfile> orgComboBox = new ComboBox<>(FXCollections.observableArrayList(activeOrganizers));
        orgComboBox.setMaxWidth(Double.MAX_VALUE);
        orgComboBox.setPrefHeight(38);
        orgComboBox.setValue(activeOrganizers.get(0));

        orgComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(OrganizerProfile org) {
                if (org == null) return "";
                String orgName = (org.getOrganizationName() != null && !org.getOrganizationName().isBlank())
                        ? org.getOrganizationName()
                        : (org.getName() != null ? org.getName() : "Organizer");
                return "🏢 " + orgName + " (" + (org.getEmail() != null ? org.getEmail() : "") + ")";
            }

            @Override
            public OrganizerProfile fromString(String string) {
                return null;
            }
        });

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setPadding(new Insets(6, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6px; -fx-padding: 7 16; -fx-font-size: 12px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeInAppModal());

        Button confirmBtn = new Button("Assign Organizer");
        confirmBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 7 18; -fx-cursor: hand;");

        confirmBtn.setOnAction(e -> {
            OrganizerProfile selectedOrganizer = orgComboBox.getValue();
            if (selectedOrganizer != null) {
                String orgUid = selectedOrganizer.getOrganizerId();
                String orgName = (selectedOrganizer.getOrganizationName() != null && !selectedOrganizer.getOrganizationName().isBlank())
                        ? selectedOrganizer.getOrganizationName()
                        : selectedOrganizer.getName();

                String notes = notesArea != null ? notesArea.getText().trim() : "";

                boolean updated = controller.assignOrganizerToEvent(selectedEvtId, orgUid, orgName, "ADMIN", notes);
                if (updated) {
                    closeInAppModal();
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Event successfully assigned to Organization [" + orgName + "]!");
                    a.setHeaderText("Assignment Successful");
                    if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
                    a.showAndWait();

                    EventRequestA requestsView = new EventRequestA();
                    LandingPage.landingPageStage.setScene(requestsView.geteventRequestScene());
                } else {
                    showAlert("Could not update organizer in Firestore.");
                }
            }
        });

        btns.getChildren().addAll(cancelBtn, confirmBtn);
        card.getChildren().addAll(head, new Separator(), sub, orgComboBox, btns);

        modalOverlayPane.getChildren().add(card);
        modalOverlayPane.setVisible(true);
        modalOverlayPane.setManaged(true);
    }

    private void closeInAppModal() {
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);
        modalOverlayPane.getChildren().clear();
    }

    private void loadEventReviewDetailsAsync() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                Event event = controller.getEventById(selectedEvtId);
                if (event != null) {
                    CustomerProfile cust = controller.getCustomerProfile(event.getCustomerId());

                    Platform.runLater(() -> {
                        eventTypeSummary.setText(event.getEventType() != null ? event.getEventType() : "General");
                        guestsSummary.setText(event.getGuests() + " Guests");
                        venueSummary.setText(event.getVenueType() != null ? event.getVenueType() : "Banquet Hall");
                        locationSummary.setText(event.getCity() != null ? event.getCity() : "N/A");
                        budgetSummary.setText(event.getBudget() != null ? event.getBudget() : "N/A");

                        String dateRange = (event.getStartDate() != null && !event.getStartDate().isBlank())
                                ? event.getStartDate() + (event.getEndDate() != null && !event.getEndDate().isBlank() && !event.getStartDate().equals(event.getEndDate()) ? " to " + event.getEndDate() : "")
                                : (event.getEventDate() != null ? event.getEventDate() : "TBD");

                        dateSummary.setText(dateRange);
                        dateBox.setText(dateRange);

                        String timeRange = (event.getStartTime() != null && !event.getStartTime().isBlank())
                                ? event.getStartTime() + (event.getEndTime() != null && !event.getEndTime().isBlank() ? " - " + event.getEndTime() : "")
                                : (event.getTime() != null ? event.getTime() : "TBD");

                        startTimeBox.setText(timeRange);
                        eventNameBox.setText(event.getEventName() != null ? event.getEventName() : "Unnamed Event");
                        durationBox.setText(event.getStartDate() != null && event.getEndDate() != null && !event.getStartDate().equals(event.getEndDate()) ? "Multi-Day" : "1 Day");

                        themeReq.setText(event.getCategory() != null ? event.getCategory() : "Formal");
                        cateringReq.setText(event.getRequirements() != null ? event.getRequirements() : "None Selected");
                        if (event.getNotes() != null && !event.getNotes().isBlank()) notesReq.setText(event.getNotes());

                        if (cust != null) {
                            String name = cust.getName() != null && !cust.getName().isBlank() ? cust.getName() : "Customer";
                            customerName.setText(name);
                            customerAvatar.setText(name.substring(0, 1).toUpperCase());
                            customerEmail.setText("✉ " + (cust.getEmail() != null ? cust.getEmail() : "N/A"));
                            customerMobile.setText("☎ " + (cust.getMobileNo() != null ? cust.getMobileNo() : "N/A"));
                        }
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void checkEventConflict() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) return;

        AdminController controller = new AdminController();
        Event event = controller.getEventById(selectedEvtId);
        if (event == null) return;

        ConflictResult result = controller.checkEventConflict(event);
        Alert alert = new Alert(result.isConflict() ? Alert.AlertType.WARNING : Alert.AlertType.INFORMATION);
        alert.setTitle("Conflict Detection");
        alert.setHeaderText(result.isConflict() ? "⚠ Conflict Detected!" : "✅ No Conflict Found");
        alert.setContentText(result.isConflict() ? "Another event is scheduled at the same venue and overlapping time." : "This event duration and venue are clear.");
        if (LandingPage.landingPageStage != null) alert.initOwner(LandingPage.landingPageStage);
        alert.showAndWait();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("EventXpert");
        a.setHeaderText(null);
        a.setContentText(msg);
        if (LandingPage.landingPageStage != null) a.initOwner(LandingPage.landingPageStage);
        a.showAndWait();
    }

    private VBox box(String t, Label valueLabel) {
        Label a = new Label(t.toUpperCase());
        a.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        a.setTextFill(Color.web("#64748B"));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        valueLabel.setTextFill(Color.web(PRIMARY));
        VBox x = new VBox(2, a, valueLabel);
        x.setPadding(new Insets(8));
        x.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");
        return x;
    }

    private VBox summaryItem(String t, Label valLabel) {
        Label a = new Label(t);
        a.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        a.setTextFill(Color.web("#64748B"));
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valLabel.setTextFill(Color.web(PRIMARY));
        VBox x = new VBox(2, a, valLabel);
        HBox.setHgrow(x, Priority.ALWAYS);
        return x;
    }
}