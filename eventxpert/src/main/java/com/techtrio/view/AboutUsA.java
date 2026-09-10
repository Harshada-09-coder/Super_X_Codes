package com.techtrio.view;

import com.techtrio.controller.NavigationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/** EventXpert acknowledgement and platform-information page for the admin portal. */
public class AboutUsA {
    private static final String DARK = "#F8FAFC", CARD = "#FFFFFF", BORDER = "#E2E8F0", BORDER_LIGHT = "#0B4EA2";
    private static final String CYAN = "#0B4EA2", GOLD = "#D97706", PURPLE = "#7C3AED", GREEN = "#16A34A", ICE = "#334155", MUTED = "#64748B";

    public Scene getAboutUsScene() {
        BorderPane root = new BorderPane();
        root.setLeft(createSidebar());
        VBox page = new VBox(20, createHero(), createMiddleSection(), createAcknowledgementSection(), createFooter());
        page.setPadding(new Insets(25, 30, 25, 30));
        page.setStyle("-fx-background-color:" + DARK + ";");
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true); scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:" + DARK + "; -fx-background-color:" + DARK + "; -fx-border-color:transparent;");
        root.setCenter(scroll);
        return new Scene(root, 1550, 785);
    }

    private HBox createHero() {
        HBox card = new HBox(30); card.setAlignment(Pos.CENTER); card.setPadding(new Insets(22, 30, 22, 30));
        card.setStyle("-fx-background-color:#002A60; -fx-border-color:" + BORDER_LIGHT + "; -fx-border-radius:22; -fx-background-radius:22;");
        VBox brand = new VBox(6); brand.setAlignment(Pos.CENTER); brand.setPrefWidth(185);
        ImageView logo = loadLogo();
        brand.getChildren().add(logo != null ? logo : avatar("EX", 54, CYAN));
        brand.getChildren().addAll(label("EventXpert", 22, Color.web("#F8FAFC"), true), label("Plan • Manage • Celebrate", 12, Color.web("#7DD3FC"), true));

        VBox thanks = new VBox(5); thanks.setAlignment(Pos.CENTER); HBox.setHgrow(thanks, Priority.ALWAYS);
        Label badge = label("Thank You, Shashi Sir", 17, Color.web(GOLD), true);
        badge.setPadding(new Insets(5, 22, 5, 22));
        badge.setStyle("-fx-text-fill:" + GOLD + "; -fx-border-color:" + GOLD + "; -fx-border-radius:12; -fx-background-radius:12; -fx-background-color:rgba(245,158,11,0.11);");
        Label message = label("Your guidance, encouragement, and belief in our ideas helped us transform\nEventXpert from a concept into a smart, intelligent event platform.", 14, Color.web("#CBD5E1"), false);
        message.setAlignment(Pos.CENTER);
        thanks.getChildren().addAll(label("THANK YOU", 36, Color.web("#F8FAFC"), true), label("For Your Guidance, Support & Inspiration", 14, Color.web("#CBD5E1"), false), badge, message, label("MENTOR   |   GUIDE   |   INSPIRATION", 11, Color.web("#7DD3FC"), true));

        VBox sir = new VBox(8, avatar("", 50, GOLD), label("Shashi Sir", 14, Color.web(GOLD), true)); sir.setAlignment(Pos.CENTER); sir.setPrefWidth(185);
        card.getChildren().addAll(brand, thanks, sir);
        card.lookupAll(".label").forEach(node -> ((Label) node).setStyle("-fx-text-fill:#FFFFFF;"));
        badge.setStyle("-fx-text-fill:" + GOLD + "; -fx-border-color:" + GOLD + "; -fx-border-radius:12; -fx-background-radius:12; -fx-background-color:rgba(245,158,11,0.11);");
        return card;
    }

    private HBox createMiddleSection() {
        VBox about = card(18, BORDER); HBox.setHgrow(about, Priority.ALWAYS); about.setPrefWidth(530);
        about.getChildren().addAll(label("About EventXpert", 20, Color.web(CYAN), true),
                paragraph("EventXpert is a comprehensive, intelligent event planning and execution platform built to streamline modern celebrations. It seamlessly connects clients, organizers, and vendors with automated scheduling, real-time tracking, and AI-driven recommendations.", ICE),
                paragraph("Powered by Google Gemini AI, OptaPlanner optimization, and Razorpay payment integration, EventXpert eliminates event conflicts, optimizes budgets, and brings celebrations to life.", MUTED));
        VBox features = card(18, BORDER); HBox.setHgrow(features, Priority.ALWAYS); features.setPrefWidth(530);
        features.getChildren().add(label("EventXpert Smart Features", 20, Color.web(GREEN), true));
        GridPane grid = new GridPane(); grid.setHgap(16); grid.setVgap(12); grid.setPadding(new Insets(14, 0, 0, 0));
        String[][] items = {{"fas-robot", "Gemini AI Event Planner"}, {"fas-bolt", "Conflict Detection Engine"}, {"fas-wallet", "Smart Budget Optimizer"}, {"fas-qrcode", "QR Guest Check-in"}, {"fas-credit-card", "Razorpay Payment Gateway"}, {"fas-cloud-sun", "Live Weather Forecast"}, {"fas-handshake", "Vendor & Venue Booking"}, {"fas-chart-line", "Live Organizer Dashboard"}};
        for (int i = 0; i < items.length; i++) grid.add(feature(items[i][0], items[i][1]), i % 2, i / 2);
        features.getChildren().add(grid); return new HBox(20, about, features);
    }

    private HBox createAcknowledgementSection() {
        VBox instructors = acknowledgementCard("Thanks To", "Instructors", PURPLE, "Sachin Sir  |  Pramod Sir  |  Akshay Sir", "“For your valuable guidance, continuous support and motivation.”");
        VBox team = acknowledgementCard("Our Team", "Team TechTrio", GOLD, "Team Lead:Sudarshana Tambe\n\nHarshda Kurumkar •  Vedantika Pawar\nKirti Narkhede", "“Different Minds • One Vision • Smarter Events”");
        VBox mentors = acknowledgementCard("Thanks To", "Super Mentors", CYAN, "Shiv Sir  |  Subodh Sir\n\nSumit Dada  |  Mansi Didi  |  Dhanashree Didi", "“For your expert advice, mentorship and constant encouragement.”");
        HBox.setHgrow(instructors, Priority.ALWAYS); HBox.setHgrow(team, Priority.ALWAYS); HBox.setHgrow(mentors, Priority.ALWAYS);
        return new HBox(20, instructors, team, mentors);
    }

    private VBox acknowledgementCard(String tag, String title, String accent, String people, String quote) {
        VBox card = card(18, accent); card.setAlignment(Pos.CENTER); card.setPrefWidth(330);
        Label peopleLabel = label(people, 13, Color.web("#0F172A"), true); peopleLabel.setAlignment(Pos.CENTER);
        Label quoteLabel = label(quote, 12, Color.web(MUTED), false); quoteLabel.setWrapText(true); quoteLabel.setAlignment(Pos.CENTER);
        card.getChildren().addAll(label(tag, 12, Color.web(MUTED), false), label(title, 20, Color.web(accent), true), peopleLabel, quoteLabel); return card;
    }

    private HBox createFooter() {
        HBox footer = new HBox(label("EVENTXPERT  —  Built with passion, learning and teamwork.  •  Plan. Manage. Celebrate.", 12, Color.web(MUTED), false));
        footer.setAlignment(Pos.CENTER); footer.setPadding(new Insets(2, 0, 8, 0)); return footer;
    }

    private HBox feature(String iconName, String text) {
        FontIcon icon = new FontIcon(iconName); icon.setIconSize(14); icon.setIconColor(Color.web(CYAN));
        return new HBox(8, icon, label(text, 13, Color.web(ICE), false));
    }

    private VBox card(int radius, String borderColor) {
        VBox card = new VBox(12); card.setPadding(new Insets(22, 25, 22, 25));
        card.setStyle("-fx-background-color:" + CARD + "; -fx-border-color:" + borderColor + "; -fx-border-radius:" + radius + "; -fx-background-radius:" + radius + ";"); return card;
    }

    private Label paragraph(String text, String color) { Label label = label(text, 13, Color.web(color), false); label.setWrapText(true); return label; }
    private Label label(String text, double size, Color color, boolean bold) { Label label = new Label(text); label.setTextFill(color); label.setFont(Font.font("Segoe UI", bold ? FontWeight.BOLD : FontWeight.NORMAL, size)); label.setWrapText(true); return label; }
    private StackPane avatar(String initials, int size, String color) { Circle circle = new Circle(size, Color.web(color, 0.17)); circle.setStroke(Color.web(color)); circle.setStrokeWidth(2); return new StackPane(circle, label(initials, size * 0.46, Color.web(color), true)); }

    private ImageView loadLogo() {
        try { var stream = getClass().getResourceAsStream(""); if (stream == null) return null; ImageView image = new ImageView(new Image(stream)); image.setFitWidth(94); image.setFitHeight(94); image.setPreserveRatio(true); return image; }
        catch (Exception ignored) { return null; }
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(6); sidebar.setPadding(new Insets(20, 14, 20, 14)); sidebar.setPrefWidth(240); sidebar.setMinWidth(240); sidebar.setStyle("-fx-background-color:#002A60;");
        HBox brand = new HBox(8); brand.setAlignment(Pos.CENTER_LEFT); FontIcon calendar = new FontIcon("fas-calendar-alt"); calendar.setIconSize(20); calendar.setIconColor(Color.web("#38BDF8")); brand.getChildren().addAll(calendar, label("EventXpert", 18, Color.WHITE, true));
        VBox menu = new VBox(3,
                sidebarButton("Dashboard", "fas-tachometer-alt", false),
                sidebarButton("Event Requests", "fas-inbox", false),
                sidebarButton("Events", "fas-calendar-check", false),
                sidebarButton("Calendar", "fas-calendar", false),
                sidebarButton("Customers", "fas-users", false),
                sidebarButton("Organizers", "fas-user-tie", false),
                sidebarButton("Vendors", "fas-truck", false),
                sidebarButton("Conflict Detection", "fas-shield-alt", false),
                sidebarButton("Reports & Analytics", "fas-chart-pie", false));
        Region space = new Region(); VBox.setVgrow(space, Priority.ALWAYS);
        VBox bottomMenu = new VBox(3,
                sidebarButton("Notifications", "fas-bell", false),
                sidebarButton("Profile", "fas-user", false),
                sidebarButton("Settings", "fas-cog", false),
                sidebarButton("Support", "fas-question-circle", false),
                sidebarButton("About Us", "fas-info-circle", true));
        sidebar.getChildren().addAll(brand, menu, space, bottomMenu); return sidebar;
    }

    private Button sidebarButton(String name, String iconName, boolean active) {
        Button button = new Button("  " + name); button.setMaxWidth(Double.MAX_VALUE); button.setAlignment(Pos.CENTER_LEFT); button.setPrefHeight(34);
        FontIcon icon = new FontIcon(iconName); icon.setIconSize(13); icon.setIconColor(active ? Color.web("#002A60") : Color.web("#CBD5E1")); button.setGraphic(icon);
        button.setStyle(active ? "-fx-background-color:#C2E0F4;-fx-text-fill:#002A60;-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;" : "-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
        button.setOnAction(e -> NavigationManager.navigate(name, (Stage) button.getScene().getWindow())); return button;
    }
}
