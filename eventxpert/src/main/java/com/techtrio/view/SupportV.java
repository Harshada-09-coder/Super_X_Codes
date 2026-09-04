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

public class SupportV {

    private Scene supportVScene;
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
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";

    private static class TicketData {
        String id;
        String subject;
        String category;
        String status;

        TicketData(String id, String subject, String category, String status) {
            this.id = id;
            this.subject = subject;
            this.category = category;
            this.status = status;
        }
    }

    private final List<TicketData> ticketList = new ArrayList<>();
    private VBox ticketsTableContainer;
    private StackPane modalOverlayPane;

    public SupportV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public SupportV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getsupportVScene(Stage primaryStage) {
        initTickets();

        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane mainLayout = new BorderPane();
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        mainLayout.setLeft(dashHelper.createCommonVendorSidebar("Support"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createTwoColumnMainLayout()
        );

        scrollPane.setContent(mainContent);
        mainLayout.setCenter(scrollPane);

        modalOverlayPane = new StackPane();
        modalOverlayPane.setStyle("-fx-background-color: rgba(6, 27, 65, 0.45);");
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlayPane);

        supportVScene = new Scene(rootStack, 1550, 785);
        return supportVScene;
    }

    private void initTickets() {
        ticketList.clear();
        ticketList.add(new TicketData("SUP-00124", "Payout Account Verification", "Payments", "Resolved"));
        ticketList.add(new TicketData("SUP-00128", "Service Request Notification Sync", "System", "Resolved"));
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Vendor Help Desk & Support");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newTicketBtn = new Button("+ Report New Issue");
        newTicketBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        newTicketBtn.setOnAction(e -> showInAppTicketModal());

        titleRow.getChildren().addAll(title, spacer, newTicketBtn);

        Label subtitle = new Label("Find answers, get assistance with payments, bookings and contact EventXpert support.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    private HBox createTwoColumnMainLayout() {
        HBox layout = new HBox(20);

        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createFaqCard(),
                createMyTicketsCard()
        );

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(360);
        rightCol.getChildren().add(createContactCard());

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createFaqCard() {
        VBox card = createCardContainer();
        Label title = new Label("Frequently Asked Questions");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox faqList = new VBox(8);
        faqList.getChildren().addAll(
                createFaqItem("How do service request amounts get determined?", "The budget amount is allocated directly by the event organizer matching the client's budget constraints."),
                createFaqItem("When is payment released for confirmed bookings?", "Payments are held securely in Escrow and released as per the agreed organizer payment milestones.")
        );

        card.getChildren().addAll(title, new Separator(), faqList);
        return card;
    }

    private VBox createFaqItem(String q, String a) {
        VBox b = new VBox(2);
        b.setPadding(new Insets(8));
        b.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label qLbl = new Label("• " + q); qLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label aLbl = new Label(a); aLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        aLbl.setWrapText(true);

        b.getChildren().addAll(qLbl, aLbl);
        return b;
    }

    private VBox createMyTicketsCard() {
        VBox card = createCardContainer();
        Label title = new Label("My Support Tickets");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        ticketsTableContainer = new VBox(4);
        renderTicketsTable();

        card.getChildren().addAll(title, new Separator(), ticketsTableContainer);
        return card;
    }

    private void renderTicketsTable() {
        ticketsTableContainer.getChildren().clear();
        for (TicketData t : ticketList) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            Label id = new Label(t.id); id.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
            id.setPrefWidth(90);
            Label sub = new Label(t.subject); sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            HBox.setHgrow(sub, Priority.ALWAYS);

            Label st = new Label(t.status);
            st.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 6px;");

            row.getChildren().addAll(id, sub, st);
            ticketsTableContainer.getChildren().add(row);
        }
    }

    private VBox createContactCard() {
        VBox card = createCardContainer();
        Label title = new Label("Direct Support Channel");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label em = new Label("✉ Email: support@eventxpert.com");
        em.setStyle("-fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label hr = new Label("⏰ Working Hours: 09:00 AM – 09:00 PM (IST)");
        hr.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(title, new Separator(), em, hr);
        return card;
    }

    // ⭐ In-App Pop-up Overlay for Creating Support Ticket
    private void showInAppTicketModal() {
        modalOverlayPane.getChildren().clear();

        VBox card = new VBox(12);
        card.setMaxWidth(380);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 16, 0, 0, 4);");

        Label title = new Label("Submit Support Issue");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        TextField subInput = new TextField();
        subInput.setPromptText("Enter summary of the issue");

        HBox btns = new HBox(8);
        btns.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 14; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeInAppModal());

        Button submitBtn = new Button("Submit Request");
        submitBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> {
            if (!subInput.getText().trim().isEmpty()) {
                ticketList.add(0, new TicketData("SUP-00" + (130 + ticketList.size()), subInput.getText().trim(), "General", "Open"));
                renderTicketsTable();
                closeInAppModal();
            }
        });

        btns.getChildren().addAll(cancelBtn, submitBtn);
        card.getChildren().addAll(title, new Separator(), new Label("Issue Summary:"), subInput, btns);

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