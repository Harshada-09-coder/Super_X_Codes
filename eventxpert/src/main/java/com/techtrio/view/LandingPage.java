package com.techtrio.view;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class LandingPage extends Application {

    // Color Palette Constants
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    // ⭐ Your Permanent Cloudinary Demo Video URL
    private static final String DEMO_VIDEO_URL = "https://res.cloudinary.com/qkcedgii/video/upload/v1788520817/FinalDemo.mp4";

    private VBox homeSection;
    private VBox featuresSection;
    private VBox howItWorksSection;
    private VBox eventTypesSection;
    private VBox aboutSection;
    private VBox contactSection;

    private ScrollPane scrollPane;

    private Label l1, l2, l3, l4, l5, l6;
    private Label[] navLabels;

    private static final String FONT_FAMILY = "'Poppins', 'Segoe UI', 'Helvetica Neue', Arial, sans-serif";

    public static Stage landingPageStage;
    public Scene landingPageScene;

    @Override
    public void start(Stage primaryStage) {
        landingPageStage = primaryStage;

        // =========================================================================
        // TOPBAR: 1. LEFT LOGO GROUP
        // =========================================================================
        StackPane logoIcon = new StackPane();
        Rectangle iconBg = new Rectangle(36, 36);
        iconBg.setArcWidth(10);
        iconBg.setArcHeight(10);
        iconBg.setFill(Color.web(NAVY));

        SVGPath calendarStarIcon = createSVGIcon("M19,4H18V2H16V4H8V2H6V4H5A2,2 0 0,0 3,6V20A2,2 0 0,0 5,22H19A2,2 0 0,0 21,20V6A2,2 0 0,0 19,4M19,20H5V9H19V20M12,10.5L13.1,12.7L15.5,13.1L13.8,14.8L14.2,17.2L12,16L9.8,17.2L10.2,14.8L8.5,13.1L10.9,12.7L12,10.5Z", WHITE, 0.75);
        logoIcon.getChildren().addAll(iconBg, calendarStarIcon);

        Label heading = new Label("EventXpert");
        heading.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
 
        Label tagline = new Label("Plan. Manage. Celebrate.");
        tagline.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox logoTextGroup = new VBox(-2);
        logoTextGroup.getChildren().addAll(heading, tagline);

        HBox logoGroup = new HBox(10);
        logoGroup.setAlignment(Pos.CENTER_LEFT);
        logoGroup.getChildren().addAll(logoIcon, logoTextGroup);

        // =========================================================================
        // TOPBAR: 2. NAVIGATION LABELS
        // =========================================================================
        l1 = createNavLink("Home");
        l2 = createNavLink("Features");
        l3 = createNavLink("How It Works");
        l4 = createNavLink("Event Types");
        l5 = createNavLink("About Us");
        l6 = createNavLink("Contact");

        navLabels = new Label[]{l1, l2, l3, l4, l5, l6};
        setActiveNavLink(l1);

        HBox navigation = new HBox(22, l1, l2, l3, l4, l5, l6);
        navigation.setAlignment(Pos.CENTER);

        // =========================================================================
        // TOPBAR: 3. RIGHT AUTH BUTTONS
        // =========================================================================
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13.5px; -fx-font-weight: bold; -fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 24; -fx-cursor: hand;");
        loginBtn.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            landingPageStage.setScene(loginPage.getloginPageScene(this::backToLandingPage));
        });

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13.5px; -fx-font-weight: bold; -fx-background-color: " + NAVY + "; -fx-text-fill: " + WHITE + "; -fx-background-radius: 8; -fx-padding: 9 26; -fx-cursor: hand;");
        registerBtn.setOnAction(e -> {
            CommonRegistration commonRegistrationPage = new CommonRegistration();
            landingPageStage.setScene(commonRegistrationPage.getcommonRegistration(this::backToLandingPage));
        });

        HBox authButtonsGroup = new HBox(12, loginBtn, registerBtn);
        authButtonsGroup.setAlignment(Pos.CENTER_RIGHT);

        Region mainSpacer = new Region();
        HBox.setHgrow(mainSpacer, Priority.ALWAYS);

        Region navBtnGap = new Region();
        navBtnGap.setPrefWidth(35);

        HBox headBox = new HBox(logoGroup, mainSpacer, navigation, navBtnGap, authButtonsGroup);
        headBox.setAlignment(Pos.CENTER);
        headBox.setPadding(new Insets(12, 45, 12, 45)); 
        headBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;"); 

        // =========================================================================
        // 1. HOME HERO SECTION
        // =========================================================================
        Label homeTitle = new Label("Plan Smarter.");
        homeTitle.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 54px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + NAVY + ";" +
                "-fx-padding: 0 0 -6 0;"
        );

        Label homeTitle2 = new Label("Celebrate Better.");
        homeTitle2.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 54px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + BRIGHT_BLUE + ";" +
                "-fx-padding: -6 0 0 0;"
        );

        VBox titleContainer = new VBox(0, homeTitle, homeTitle2);

        Label homeText = new Label(
                "EventXpert helps you plan, manage and execute small-scale\n" +
                "personal, college and corporate events with intelligence,\n" +
                "simplicity and perfection."
        );

        homeText.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 17px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-line-spacing: 6px;"
        );

        HBox btnGroup = new HBox(18);
        btnGroup.setAlignment(Pos.CENTER_LEFT);
        btnGroup.setPadding(new Insets(8, 0, 0, 0));

        Button getStartedBtn = new Button("Get Started  →");
        getStartedBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-color: " + NAVY + "; -fx-text-fill: " + WHITE + "; -fx-background-radius: 10; -fx-padding: 13 32; -fx-cursor: hand;");
        getStartedBtn.setOnAction(e -> {
            LoginPage backToLoginPage = new LoginPage();
            LandingPage.landingPageStage.setScene(backToLoginPage.getloginPageScene(this::backToLandingPage));
        });

        // 🎬 WATCH DEMO BUTTON (Plays Cloudinary FinalDemo.mp4 Directly)
        Button watchDemoBtn = new Button("Watch Demo  ◉");
        watchDemoBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12 28; -fx-cursor: hand;");
        watchDemoBtn.setOnAction(e -> {
            playDemoVideoInstant(DEMO_VIDEO_URL, "EventXpert Platform Demo");
        });
        
        btnGroup.getChildren().addAll(getStartedBtn, watchDemoBtn);

        VBox homeSection1 = new VBox(20);
        homeSection1.setAlignment(Pos.CENTER_LEFT);
        homeSection1.setPadding(new Insets(30, 20, 30, 50));
        homeSection1.getChildren().addAll(titleContainer, homeText, btnGroup);
        HBox.setHgrow(homeSection1, Priority.ALWAYS);

        StackPane imgContainer = new StackPane();
        ImageView homeImgView = new ImageView();
        try {
            Image homeImg = new Image(getClass().getResourceAsStream("/assets/images/FrontImg.jpeg"));
            homeImgView.setImage(homeImg);
        } catch (Exception ex) {
            try {
                Image homeImg = new Image("assets/images/FrontImg.jpeg");
                homeImgView.setImage(homeImg);
            } catch (Exception ignored) {}
        }
        homeImgView.setFitHeight(410);
        homeImgView.setFitWidth(680);
        homeImgView.setPreserveRatio(true);

        Rectangle clip = new Rectangle(680, 410);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        homeImgView.setClip(clip);

        DropShadow imgShadow = new DropShadow(18, Color.rgb(6, 42, 96, 0.12));
        imgContainer.setEffect(imgShadow);
        imgContainer.getChildren().add(homeImgView);

        VBox homeSection2 = new VBox(imgContainer);
        homeSection2.setAlignment(Pos.CENTER);
        homeSection2.setPadding(new Insets(20, 50, 20, 20));

        HBox homeSection3 = new HBox(30, homeSection1, homeSection2);
        homeSection3.setAlignment(Pos.CENTER);

        homeSection = new VBox(25);
        homeSection.getChildren().addAll(homeSection3, createFeatureStrip());

        l1.setOnMouseClicked(e -> {
            setActiveNavLink(l1);
            scrollToSection(homeSection);
        });

        // =========================================================================
        // 2. FEATURES SECTION
        // =========================================================================
        featuresSection = createOrbitalFeaturesSection();
        l2.setOnMouseClicked(e -> {
            setActiveNavLink(l2);
            scrollToSection(featuresSection);
        });

        // =========================================================================
        // 3. HOW IT WORKS SECTION
        // =========================================================================
        howItWorksSection = createHowItWorksFlowSection();
        l3.setOnMouseClicked(e -> {
            setActiveNavLink(l3);
            scrollToSection(howItWorksSection);
        });

        // =========================================================================
        // 4. EVENT TYPES SECTION
        // =========================================================================
        eventTypesSection = createEventTypesGridSection();
        l4.setOnMouseClicked(e -> {
            setActiveNavLink(l4);
            scrollToSection(eventTypesSection);
        });

        // =========================================================================
        // 5. ABOUT US SECTION
        // =========================================================================
        aboutSection = createAboutUsSection();
        l5.setOnMouseClicked(e -> {
            setActiveNavLink(l5);
            scrollToSection(aboutSection);
        });

        // =========================================================================
        // 6. CONTACT SECTION
        // =========================================================================
        contactSection = createInteractiveContactSection();
        l6.setOnMouseClicked(e -> {
            setActiveNavLink(l6);
            scrollToSection(contactSection);
        });

        VBox content = new VBox(40);
        content.getChildren().addAll(
                homeSection,
                featuresSection,
                howItWorksSection,
                eventTypesSection,
                aboutSection,
                contactSection
        );

        scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background: " + BACKGROUND + ";");

        // DYNAMIC SCROLL-SPY
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            double v = newVal.doubleValue();
            if (v < 0.15) {
                setActiveNavLink(l1);
            } else if (v < 0.35) {
                setActiveNavLink(l2);
            } else if (v < 0.55) {
                setActiveNavLink(l3);
            } else if (v < 0.75) {
                setActiveNavLink(l4);
            } else if (v < 0.90) {
                setActiveNavLink(l5);
            } else {
                setActiveNavLink(l6);
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(headBox);
        root.setCenter(scrollPane);
        root.setBottom(createFooterBar());
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Scene scene = new Scene(root, 1550, 750);
        landingPageScene = scene;
        landingPageStage.setScene(landingPageScene);
        primaryStage.setTitle("EventXpert");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // =========================================================================
    // IN-APP INSTANT DEMO VIDEO PLAYER
    // =========================================================================
    private void playDemoVideoInstant(String videoUrl, String videoTitle) {
        try {
            System.out.println("Opening Demo Video: " + videoUrl);
            
            // 1. Direct Browser / OS Native Player Launch (Zero Codec / Module Error)
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(videoUrl));
            } else {
                // Windows Fallback
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", videoUrl});
            }

        } catch (Exception ex) {
            System.err.println("Could not open default browser, showing fallback dialog: " + ex.getMessage());
            
            // Fallback Dialog (If Desktop browse is restricted)
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("EventXpert - Watch Demo");
            dialog.setHeaderText("Watch Platform Demo Video");
            if (landingPageStage != null) dialog.initOwner(landingPageStage);

            VBox box = new VBox(12);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(20));

            Label msg = new Label("Click the link below to watch the demo video:");
            msg.setStyle("-fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

            Hyperlink link = new Hyperlink(videoUrl);
            link.setStyle("-fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
            link.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(videoUrl));
                } catch (Exception ignored) {}
            });

            box.getChildren().addAll(msg, link);
            dialog.getDialogPane().setContent(box);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        }
    }

    // =========================================================================
    // FEATURE STRIP
    // =========================================================================
    private HBox createFeatureStrip() {
        HBox outer = new HBox();
        outer.setPadding(new Insets(25, 55, 30, 55));
        outer.setAlignment(Pos.CENTER);

        HBox cardContainer = new HBox(18);
        cardContainer.setPadding(new Insets(20, 26, 20, 26));
        cardContainer.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 16; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16;");
        HBox.setHgrow(cardContainer, Priority.ALWAYS);

        DropShadow shadow = new DropShadow(16, Color.rgb(6, 42, 96, 0.08));
        cardContainer.setEffect(shadow);

        cardContainer.getChildren().addAll(
                createFeatureItem("AI Event Planner", "Smart suggestions for timelines & vendors.", "fas-magic"),
                createFeatureItem("Budget Optimizer", "Constraint allocation within your budget.", "fas-chart-line"),
                createFeatureItem("Emergency Venue", "Find alternative nearby venues instantly.", "fas-map-marker-alt"),
                createFeatureItem("Weather Alerts", "Real-time forecast for better planning.", "fas-cloud-sun"),
                createFeatureItem("QR Check-in", "Seamless guest check-in with QR codes.", "fas-qrcode")
        );

        outer.getChildren().add(cardContainer);
        return outer;
    }

    private VBox createFeatureItem(String title, String desc, String iconLiteral) {
        VBox item = new VBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(item, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBg = new StackPane();
        Circle c = new Circle(16, Color.web(LIGHT_BLUE));
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(14);
        icon.setIconColor(Color.web(PRIMARY_BLUE));
        iconBg.getChildren().addAll(c, icon);

        Label t = new Label(title);
        t.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        top.getChildren().addAll(iconBg, t);

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 10.5px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        item.getChildren().addAll(top, d);
        return item;
    }

    // =========================================================================
    // NAVBAR LABEL STYLING & UNDERLINE HELPERS
    // =========================================================================
    private Label createNavLink(String title) {
        Label l = new Label(title);
        l.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; -fx-cursor: hand; -fx-padding: 4 2;");
        l.setOnMouseEntered(e -> {
            if (!l.getStyle().contains("-fx-border-color")) {
                l.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand; -fx-padding: 4 2;");
            }
        });
        l.setOnMouseExited(e -> {
            if (!l.getStyle().contains("-fx-border-color")) {
                l.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; -fx-cursor: hand; -fx-padding: 4 2;");
            }
        });
        return l;
    }

    private void setActiveNavLink(Label activeLabel) {
        if (navLabels == null) return;
        for (Label lbl : navLabels) {
            if (lbl == activeLabel) {
                lbl.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 0 0 2.5 0; -fx-padding: 4 2 2 2; -fx-cursor: hand;");
            } else {
                lbl.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; -fx-cursor: hand; -fx-padding: 4 2;");
            }
        }
    }

    // =========================================================================
    // CONTACT SECTION
    // =========================================================================
    private VBox createInteractiveContactSection() {
        VBox section = new VBox(20);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(60, 50, 70, 50));
        section.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Label contactTitle = new Label("Let's Plan Your Next Event");
        contactTitle.setStyle(
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + NAVY + ";"
        );

        Label introText = new Label("Have a question or need help planning your event?\nOur team is here to help.");
        introText.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-text-alignment: center;");

        HBox emailBox = new HBox(8);
        emailBox.setAlignment(Pos.CENTER);
        Label emailPrefix = new Label("Email:");
        emailPrefix.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label emailVal = new Label("eventxpert03@gmail.com");
        emailVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");
        emailVal.setOnMouseEntered(e -> emailVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-underline: true; -fx-cursor: hand;"));
        emailVal.setOnMouseExited(e -> emailVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-underline: false; -fx-cursor: hand;"));
        emailBox.getChildren().addAll(emailPrefix, emailVal);

        HBox phoneBox = new HBox(8);
        phoneBox.setAlignment(Pos.CENTER);
        Label phonePrefix = new Label("Phone:");
        phonePrefix.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label phoneVal = new Label("+91 98765 43210");
        phoneVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");
        phoneVal.setOnMouseEntered(e -> phoneVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-underline: true; -fx-cursor: hand;"));
        phoneVal.setOnMouseExited(e -> phoneVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-underline: false; -fx-cursor: hand;"));
        phoneBox.getChildren().addAll(phonePrefix, phoneVal);

        HBox locBox = new HBox(8);
        locBox.setAlignment(Pos.CENTER);
        Label locPrefix = new Label("Location:");
        locPrefix.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label locVal = new Label("Pune, Maharashtra");
        locVal.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 16px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        locBox.getChildren().addAll(locPrefix, locVal);

        VBox contactCard = new VBox(10);
        contactCard.setAlignment(Pos.CENTER);
        contactCard.setPadding(new Insets(14));
        contactCard.getChildren().addAll(emailBox, phoneBox, locBox);

        section.getChildren().addAll(contactTitle, introText, contactCard);
        return section;
    }

    // =========================================================================
    // CIRCULAR FEATURES ORBIT
    // =========================================================================
    private VBox createOrbitalFeaturesSection() {
        VBox section = new VBox(25);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50, 50, 50, 50));

        Label title = new Label("Powerful Features for Smarter Events");
        title.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        StackPane orbitPane = new StackPane();
        orbitPane.setPrefSize(900, 430);
        orbitPane.setMaxSize(900, 430);
        orbitPane.setAlignment(Pos.CENTER);

        Circle outerRing = new Circle(220);
        outerRing.setFill(Color.web("#EBF4FF").deriveColor(0, 1, 1, 0.45));
        outerRing.setStroke(Color.web("#D0E5FF"));
        outerRing.setStrokeWidth(1.5);

        Circle innerRing = new Circle(140);
        innerRing.setFill(Color.web("#DDEEFF").deriveColor(0, 1, 1, 0.4));
        innerRing.setStroke(Color.web("#C6E2FF"));
        innerRing.setStrokeWidth(1.5);

        orbitPane.getChildren().addAll(outerRing, innerRing);

        StackPane feat1 = createCircleFeatureBadge("AI Event Planner", "fas-magic", "#0D3979", 0, -115);
        StackPane feat2 = createCircleFeatureBadge("QR Check-in", "fas-qrcode", "#0284C7", -215, -30);
        StackPane feat3 = createCircleFeatureBadge("Smart Budget\nOptimizer", "fas-chart-line", "#2563EB", 215, -30);
        StackPane feat4 = createCircleFeatureBadge("Weather Alerts", "fas-cloud-sun", "#0284C7", -120, 95);
        StackPane feat5 = createCircleFeatureBadge("Emergency Venue\nFinder", "fas-map-marker-alt", "#0D3979", 120, 95);

        orbitPane.getChildren().addAll(feat1, feat2, feat3, feat4, feat5);
        section.getChildren().addAll(title, orbitPane);
        return section;
    }

    private StackPane createCircleFeatureBadge(String title, String iconLiteral, String themeColorHex, double x, double y) {
        StackPane badge = new StackPane();
        badge.setPrefSize(140, 140);
        badge.setMaxSize(140, 140);
        badge.setTranslateX(x);
        badge.setTranslateY(y);
        badge.setStyle("-fx-cursor: hand;");

        Circle bg = new Circle(64);
        bg.setFill(Color.web("#FFFFFF"));
        bg.setStroke(Color.web(themeColorHex));
        bg.setStrokeWidth(2.5);

        DropShadow shadow = new DropShadow(14, Color.rgb(13, 57, 121, 0.12));
        bg.setEffect(shadow);

        VBox content = new VBox(6);
        content.setAlignment(Pos.CENTER);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(28);
        icon.setIconColor(Color.web(themeColorHex));

        Label text = new Label(title);
        text.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; -fx-text-alignment: center;");
        text.setWrapText(true);

        content.getChildren().addAll(icon, text);
        badge.getChildren().addAll(bg, content);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), badge);
        badge.setOnMouseEntered(e -> {
            st.setToX(1.08);
            st.setToY(1.08);
            st.playFromStart();
            bg.setFill(Color.web("#F0F7FF"));
        });
        badge.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
            bg.setFill(Color.web("#FFFFFF"));
        });

        return badge;
    }

    // =========================================================================
    // HOW IT WORKS FLOW
    // =========================================================================
    private VBox createHowItWorksFlowSection() {
        VBox section = new VBox(30);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50, 50, 50, 50));

        Label title = new Label("How EventXpert Works");
        title.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox cardsRow = new HBox(12);
        cardsRow.setAlignment(Pos.CENTER);

        VBox step1 = createStepCard("01", "Create Your\nEvent", "fas-clipboard-list");
        FontIcon arrow1 = createFlowArrow();
        VBox step2 = createStepCard("02", "Get Matched\nWith an Organizer", "fas-users-cog");
        FontIcon arrow2 = createFlowArrow();
        VBox step3 = createStepCard("03", "Plan & Manage\nYour Event", "fas-chart-pie");
        FontIcon arrow3 = createFlowArrow();
        VBox step4 = createStepCard("04", "Celebrate With\nConfidence", "fas-glass-cheers");

        cardsRow.getChildren().addAll(step1, arrow1, step2, arrow2, step3, arrow3, step4);
        section.getChildren().addAll(title, cardsRow);
        return section;
    }

    private VBox createStepCard(String number, String title, String iconLiteral) {
        VBox card = new VBox(14);
        card.setPrefSize(205, 235);
        card.setMinSize(205, 235);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 16, 20, 16));
        card.setStyle("-fx-background-color: #F3F8FE; -fx-border-color: #CCE3FC; -fx-border-width: 1.5; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;");

        DropShadow shadow = new DropShadow(10, Color.rgb(11, 78, 162, 0.08));
        card.setEffect(shadow);

        Label numLbl = new Label(number);
        numLbl.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(36);
        icon.setIconColor(Color.web(NAVY));

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; -fx-text-alignment: center;");
        titleLbl.setWrapText(true);

        card.getChildren().addAll(numLbl, icon, titleLbl);

        ScaleTransition st = new ScaleTransition(Duration.millis(180), card);
        card.setOnMouseEntered(e -> {
            st.setToX(1.05);
            st.setToY(1.05);
            st.playFromStart();
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + BRIGHT_BLUE + "; -fx-border-width: 1.5; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;");
        });
        card.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
            card.setStyle("-fx-background-color: #F3F8FE; -fx-border-color: #CCE3FC; -fx-border-width: 1.5; -fx-border-radius: 14; -fx-background-radius: 14; -fx-cursor: hand;");
        });

        return card;
    }

    private FontIcon createFlowArrow() {
        FontIcon arrow = new FontIcon("fas-chevron-right");
        arrow.setIconSize(20);
        arrow.setIconColor(Color.web("#38BDF8"));
        return arrow;
    }

    // =========================================================================
    // EVENT TYPES GRID
    // =========================================================================
    private VBox createEventTypesGridSection() {
        VBox section = new VBox(25);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50, 60, 50, 60));

        Label title = new Label("Event Types");
        title.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        StackPane card1 = createEventTypeCard("College & Academic Events", "/assets/images/eduEvent.jpg");
        StackPane card2 = createEventTypeCard("Corporate Events", "/assets/images/copEvent.jpeg");
        StackPane card3 = createEventTypeCard("Conferences & Seminars", "/assets/images/conEvent.jpg");
        StackPane card4 = createEventTypeCard("Birthday & Family Events", "/assets/images/BirEvent.jpg");

        grid.add(card1, 0, 0);
        grid.add(card2, 1, 0);
        grid.add(card3, 0, 1);
        grid.add(card4, 1, 1);

        section.getChildren().addAll(title, grid);
        return section;
    }

    private StackPane createEventTypeCard(String titleText, String imagePath) {
        StackPane card = new StackPane();
        card.setPrefSize(420, 200);
        card.setMaxSize(420, 200);
        card.setStyle("-fx-background-radius: 14; -fx-cursor: hand;");

        ImageView imgView = new ImageView();
        imgView.setFitWidth(420);
        imgView.setFitHeight(200);
        imgView.setPreserveRatio(false);

        Rectangle clip = new Rectangle(420, 200);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imgView.setClip(clip);

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            imgView.setImage(img);
        } catch (Exception ex) {
            try {
                Image img = new Image(imagePath.startsWith("/") ? imagePath.substring(1) : imagePath);
                imgView.setImage(img);
            } catch (Exception e2) {
                Rectangle placeholder = new Rectangle(420, 200, Color.web("#1E293B"));
                placeholder.setArcWidth(20);
                placeholder.setArcHeight(20);
                card.getChildren().add(placeholder);
            }
        }

        Rectangle overlay = new Rectangle(420, 200);
        overlay.setArcWidth(20);
        overlay.setArcHeight(20);
        overlay.setStyle("-fx-fill: linear-gradient(to bottom, rgba(6, 27, 65, 0.25) 0%, rgba(6, 27, 65, 0.85) 100%);");

        Label label = new Label(titleText);
        label.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-text-alignment: center;");
        label.setWrapText(true);

        card.getChildren().addAll(imgView, overlay, label);

        ScaleTransition st = new ScaleTransition(Duration.millis(180), card);
        card.setOnMouseEntered(e -> {
            st.setToX(1.03);
            st.setToY(1.03);
            st.playFromStart();
            overlay.setStyle("-fx-fill: linear-gradient(to bottom, rgba(11, 78, 162, 0.2) 0%, rgba(6, 27, 65, 0.95) 100%);");
        });
        card.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
            overlay.setStyle("-fx-fill: linear-gradient(to bottom, rgba(6, 27, 65, 0.25) 0%, rgba(6, 27, 65, 0.85) 100%);");
        });

        return card;
    }

    // =========================================================================
    // ABOUT US SECTION
    // =========================================================================
    private VBox createAboutUsSection() {
        VBox section = new VBox(25);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50, 60, 50, 60));

        HBox mainRow = new HBox(40);
        mainRow.setAlignment(Pos.CENTER);

        StackPane imgContainer = new StackPane();
        imgContainer.setPrefSize(440, 280);
        imgContainer.setMaxSize(440, 280);

        ImageView aboutImgView = new ImageView();
        aboutImgView.setFitWidth(440);
        aboutImgView.setFitHeight(280);
        aboutImgView.setPreserveRatio(false);

        Rectangle clip = new Rectangle(440, 280);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        aboutImgView.setClip(clip);

        try {
            Image aboutImg = new Image(getClass().getResourceAsStream("/assets/images/AboutEvent.jpg"));
            aboutImgView.setImage(aboutImg);
        } catch (Exception ex) {
            try {
                Image aboutImg = new Image("assets/images/AboutEvent.jpg");
                aboutImgView.setImage(aboutImg);
            } catch (Exception e2) {
                Rectangle ph = new Rectangle(440, 280, Color.web("#1E293B"));
                ph.setArcWidth(24);
                ph.setArcHeight(24);
                imgContainer.getChildren().add(ph);
            }
        }

        DropShadow shadow = new DropShadow(16, Color.rgb(6, 42, 96, 0.15));
        imgContainer.setEffect(shadow);
        imgContainer.getChildren().add(aboutImgView);

        VBox textCol = new VBox(14);
        textCol.setAlignment(Pos.CENTER_LEFT);
        textCol.setMaxWidth(520);

        Label title = new Label("About EventXpert");
        title.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label desc = new Label("EventXpert is a smart event management platform designed to make event planning simpler, organized and reliable.\n\nFrom event requests to venues, vendors, budgets and schedules, EventXpert brings the complete planning process into one intuitive workspace.");
        desc.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 14px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-line-spacing: 4px;");
        desc.setWrapText(true);

        HBox badgeRow = new HBox(12);
        badgeRow.setAlignment(Pos.CENTER_LEFT);
        badgeRow.setPadding(new Insets(8, 0, 0, 0));

        badgeRow.getChildren().addAll(
                createAboutBadge("100% Reliable", "fas-shield-alt"),
                createAboutBadge("Smart AI Tools", "fas-robot"),
                createAboutBadge("Seamless Planning", "fas-cogs")
        );

        textCol.getChildren().addAll(title, desc, badgeRow);
        mainRow.getChildren().addAll(imgContainer, textCol);

        section.getChildren().add(mainRow);
        return section;
    }

    private HBox createAboutBadge(String text, String iconLiteral) {
        HBox badge = new HBox(8);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(8, 12, 8, 12));
        badge.setStyle("-fx-background-color: #EBF4FE; -fx-border-color: #CCE3FC; -fx-border-radius: 10; -fx-background-radius: 10;");

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(14);
        icon.setIconColor(Color.web(PRIMARY_BLUE));

        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        badge.getChildren().addAll(icon, lbl);
        return badge;
    }

    private SVGPath createSVGIcon(String content, String color, double scale) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(Color.web(color));
        return svgPath;
    }

    private void scrollToSection(VBox section) {
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double sectionY = section.getBoundsInParent().getMinY();
        double maxScroll = contentHeight - viewportHeight;

        if (maxScroll > 0) {
            scrollPane.setVvalue(sectionY / maxScroll);
        }
    }

    private HBox createFooterBar() {
        HBox footer = new HBox();
        footer.setPrefHeight(52);
        footer.setPadding(new Insets(0, 50, 0, 50));
        footer.setStyle("-fx-background-color: " + NAVY + ";");
        footer.setAlignment(Pos.CENTER);
        footer.setSpacing(80);

        footer.getChildren().addAll(
                createFooterItem("Secure & Reliable", "Your data is safe with us.", "M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1Z"),
                createFooterItem("24/7 Support", "We're here to help anytime.", "M12,1A9,9 0 0,0 3,10C3,13 4.5,15.6 6.8,17.1L5,22L10.3,20.2C10.8,20.4 11.4,20.5 12,20.5A9,9 0 0,0 21,11.5A9,9 0 0,0 12,1Z"),
                createFooterItem("Easy to Use", "Simple, intuitive, and powerful.", "M23,10C23,9.45 22.55,9 22,9H15.69L16.64,4.43C16.67,4.29 16.68,4.14 16.68,4C16.68,3.59 16.5,3.22 16.24,2.96L15.17,1.89L8.59,8.47C8.22,8.84 8,9.34 8,9.9V19C8,20.1 8.9,21 10,21H18C18.83,21 19.54,20.5 19.84,19.78L22.86,12.73C22.95,12.5 23,12.26 23,12V10M1,21H6V9H1V21Z"),
                createFooterItem("Quality Assured", "We ensure the best for you.", "M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M10,17L18,9L16.59,7.58L10,14.17L7.41,11.59L6,13L10,17Z")
        );

        return footer;
    }

    private HBox createFooterItem(String title, String desc, String svgPathData) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        SVGPath icon = createSVGIcon(svgPathData, BRIGHT_BLUE, 0.75);

        VBox textGroup = new VBox(0);
        Label t = new Label(title);
        t.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");

        Label d = new Label(desc);
        d.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 9.5px; -fx-text-fill: #94A3B8;");

        textGroup.getChildren().addAll(t, d);
        item.getChildren().addAll(icon, textGroup);
        return item;
    }

    public void backToLandingPage() {
        if (landingPageStage != null && landingPageScene != null) {
            landingPageStage.setScene(landingPageScene);
        }
    }
}