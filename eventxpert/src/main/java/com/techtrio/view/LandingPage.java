package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;


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

    private VBox homeSection;
    private VBox featuresSection;
    private VBox howItWorksSection;
    private VBox eventTypesSection;
    private VBox aboutSection;
    private VBox contactSection;

    private ScrollPane scrollPane;

    // Font Family Fallback
    private static final String FONT_FAMILY = "'Poppins', 'Segoe UI', 'Helvetica Neue', sans-serif";

    public static Stage landingPageStage;
    public Scene landingPageScene;

    @Override
    public void start(Stage primaryStage) {
        landingPageStage = primaryStage;

        StackPane logoIcon = new StackPane();
        Rectangle iconBg = new Rectangle(36, 36);
        iconBg.setArcWidth(10);
        iconBg.setArcHeight(10);
        iconBg.setFill(Color.web(NAVY));

        SVGPath calendarStarIcon = createSVGIcon("M19,4H18V2H16V4H8V2H6V4H5A2,2 0 0,0 3,6V20A2,2 0 0,0 5,22H19A2,2 0 0,0 21,20V6A2,2 0 0,0 19,4M19,20H5V9H19V20M12,10.5L13.1,12.7L15.5,13.1L13.8,14.8L14.2,17.2L12,16L9.8,17.2L10.2,14.8L8.5,13.1L10.9,12.7L12,10.5Z", WHITE, 0.75);
        logoIcon.getChildren().addAll(iconBg, calendarStarIcon);

        Label heading = new Label("EventXpert");
        heading.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
 
        Label tagline = new Label("Plan. Manage. Celebrate.");
        tagline.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox logoGroup = new HBox(10);

        VBox logoTextGroup = new VBox(-2);

        logoTextGroup.getChildren().addAll(heading, tagline);
        logoGroup.getChildren().addAll(logoIcon, logoTextGroup);

        
        Label l1 = new Label("Home");
        l1.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 21px; -fx-text-fill:  NAVY ; -fx-cursor: hand");

        Label l2 = new Label("Features");
        l2.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 21px;  -fx-text-fill:  NAVY ; -fx-cursor: hand");
      
        Label l3 = new Label("How It Works");
        l3.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 21px;  -fx-text-fill:  NAVY ; -fx-cursor: hand");
      
        Label l4 = new Label("Event Types");
        l4.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 21px;  -fx-text-fill:  NAVY ; -fx-cursor: hand");
      
        Label l5 = new Label("About Us");
        l5.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 21px;  -fx-text-fill:  NAVY ; -fx-cursor: hand");
      
        Label l6 = new Label("Contact");
        l6.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 21px;  -fx-text-fill:  NAVY ; -fx-cursor: hand");

        HBox navigation = new HBox(30, l1, l2, l3, l4, l5, l6);
        navigation.setAlignment(Pos.TOP_CENTER);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 7 20; -fx-cursor: hand;");
        loginBtn.setOnAction(e -> System.out.println("Login clicked"));

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: " + NAVY + "; -fx-text-fill: " + WHITE + "; -fx-background-radius: 8; -fx-padding: 8 22; -fx-cursor: hand;");
        registerBtn.setOnAction(e -> System.out.println("Register clicked"));


        HBox headBox = new HBox(50, logoGroup, navigation, loginBtn, registerBtn);
        headBox.setAlignment(Pos.TOP_CENTER);
        headBox.setPadding(new javafx.geometry.Insets(15, 40, 15, 15)); 
        headBox.setStyle(
        "-fx-background-color: " + LIGHT_BLUE + ";"
); 

        Label homeTitle = new Label("Plan Smarter.");
homeTitle.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 42px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + NAVY + ";"
);

Label homeTitle2 = new Label("Celebrate Better.");
homeTitle2.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 42px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + BRIGHT_BLUE + ";"
);

Label homeText = new Label(
        "EventXpert helps you plan, manage and execute small-scale\n" +
        "personal, college and corporate events with intelligences\n" +
        "implicity and perfection."
);

homeText.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 16px;" +
        "-fx-text-fill: " + SECONDARY_TEXT + ";"
);

HBox btnGroup = new HBox(16);
        btnGroup.setAlignment(Pos.CENTER_LEFT);

        Button getStartedBtn = new Button("Get Started  →");
        getStartedBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13.5px; -fx-font-weight: bold; -fx-background-color: " + NAVY + "; -fx-text-fill: " + WHITE + "; -fx-background-radius: 10; -fx-padding: 11 26; -fx-cursor: hand;");
        getStartedBtn.setOnAction(e -> System.out.println("Get Started clicked"));

        Button watchDemoBtn = new Button("Watch Demo  ◉");
        watchDemoBtn.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 13.5px; -fx-font-weight: bold; -fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10 24; -fx-cursor: hand;");
        watchDemoBtn.setOnAction(e -> System.out.println("Watch Demo clicked"));
        
        btnGroup.getChildren().addAll(getStartedBtn, watchDemoBtn);


VBox homeSection1 = new VBox(10);
        homeSection1.setPrefHeight(700);
        homeSection1.setAlignment(Pos.TOP_LEFT);
        homeSection1.setTranslateX(150);
        homeSection1.setTranslateY(100);

homeSection1.getChildren().addAll(
        homeTitle,
        homeTitle2,
        homeText,
        btnGroup
);

Image homeImg = new Image("assets\\images\\FrontImg.jpeg");
ImageView homeImgView = new ImageView(homeImg);
homeImgView.setFitHeight(300);
homeImgView.setFitWidth(500);
homeImgView.setStyle("-fx-padding: 20px , 20px");
homeImgView.setTranslateX(250);

VBox homeSection2 = new VBox(10);
        homeSection2.setAlignment(Pos.TOP_RIGHT);
        homeSection2.setTranslateX(100);
        homeSection2.setTranslateY(100);

homeSection2.getChildren().addAll(
        homeImgView
);


HBox homeSection3 = new HBox();
homeSection3.getChildren().addAll(
    homeSection1,
    homeSection2
);

        
       
homeSection = new VBox(10);
homeSection.getChildren().addAll(
    homeSection3,
    createFeatureStrip()
);



l1.setOnMouseClicked(e -> {
    scrollToSection(homeSection);
});


featuresSection = new VBox(20);
featuresSection.setPrefHeight(650);
featuresSection.setAlignment(Pos.CENTER);

Label featuresTitle = new Label("Powerful Features for Smarter Events");

featuresTitle.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + NAVY + ";"
);

Label featuresText = new Label(
        "AI Event Planner   •   Smart Budget Optimizer   •   Conflict Detection\n" +
        "Emergency Venue Finder   •   Weather Alerts   •   QR Check-in"
);

featuresText.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 16px;" +
        "-fx-text-fill: " + SECONDARY_TEXT + ";"
);

featuresSection.getChildren().addAll(
        featuresTitle,
        featuresText
);

l2.setOnMouseClicked(e -> {
    scrollToSection(featuresSection);
});

howItWorksSection = new VBox(20);
howItWorksSection.setPrefHeight(650);
howItWorksSection.setAlignment(Pos.CENTER);

Label howTitle = new Label("How EventXpert Works");

howTitle.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + NAVY + ";"
);

Label howText = new Label(
        "01  Create Your Event\n\n" +
        "02  Get Matched With an Organizer\n\n" +
        "03  Plan & Manage Your Event\n\n" +
        "04  Celebrate With Confidence"
);

howText.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 17px;" +
        "-fx-text-fill: " + TEXT_MAIN + ";"
);

howItWorksSection.getChildren().addAll(
        howTitle,
        howText
);

l3.setOnMouseClicked(e -> {
    scrollToSection(howItWorksSection);
});

eventTypesSection = new VBox(20);
eventTypesSection.setPrefHeight(650);
eventTypesSection.setAlignment(Pos.CENTER);

Label eventTypesTitle = new Label("Events We Help You Manage");

eventTypesTitle.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + NAVY + ";"
);

Label eventTypesText = new Label(
        "College & Academic Events\n" +
        "Corporate Events\n" +
        "Conferences & Seminars\n" +
        "Product Launches\n" +
        "Birthday & Personal Events\n" +
        "Social & Community Events"
);

eventTypesText.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 17px;" +
        "-fx-text-fill: " + SECONDARY_TEXT + ";"
);

eventTypesSection.getChildren().addAll(
        eventTypesTitle,
        eventTypesText
);

l4.setOnMouseClicked(e -> {
    scrollToSection(eventTypesSection);
});

aboutSection = new VBox(20);
aboutSection.setPrefHeight(600);
aboutSection.setAlignment(Pos.CENTER);

Label aboutTitle = new Label("About EventXpert");

aboutTitle.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + NAVY + ";"
);

Label aboutText = new Label(
        "EventXpert is a smart event management platform designed " +
        "to make event planning simpler, organized and reliable.\n\n" +

        "From event requests to venues, vendors, budgets and schedules, " +
        "EventXpert brings the complete planning process into one platform."
);

aboutText.setWrapText(true);
aboutText.setMaxWidth(700);

aboutText.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 16px;" +
        "-fx-text-fill: " + SECONDARY_TEXT + ";"
);

aboutSection.getChildren().addAll(
        aboutTitle,
        aboutText
);

l5.setOnMouseClicked(e -> {
    scrollToSection(aboutSection);
});

contactSection = new VBox(20);
contactSection.setPrefHeight(550);
contactSection.setAlignment(Pos.CENTER);

Label contactTitle = new Label("Let's Plan Your Next Event");

contactTitle.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 32px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + NAVY + ";"
);

Label contactText = new Label(
        "Have a question or need help planning your event?\n" +
        "Our team is here to help.\n\n" +
        "Email: support@eventxpert.com\n" +
        "Phone: +91 98765 43210\n" +
        "Location: Pune, Maharashtra"
);

contactText.setStyle(
        "-fx-font-family: " + FONT_FAMILY + ";" +
        "-fx-font-size: 16px;" +
        "-fx-text-fill: " + SECONDARY_TEXT + ";"
);

contactSection.getChildren().addAll(
        contactTitle,
        contactText
);

l6.setOnMouseClicked(e -> {
    scrollToSection(contactSection);
});

VBox content = new VBox();

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
scrollPane.setStyle(
        "-fx-background-color: " + BACKGROUND + ";"
);


        BorderPane root = new BorderPane();

root.setTop(headBox);
root.setCenter(scrollPane);
root.setBottom(createFooterBar());

root.setStyle(
        "-fx-background-color: " + BACKGROUND + ";"
);
         Scene scene = new Scene(root, 1550, 785);
          primaryStage.setTitle("EventXpert");
          primaryStage.setScene(scene);
          primaryStage.setResizable(true);
          primaryStage.show();

    }


    private SVGPath createSVGIcon(String content, String color, double scale) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(Color.web(color));
        return svgPath;
    }

    private void scrollToSection(VBox section) {

    double contentHeight =
            scrollPane.getContent().getBoundsInLocal().getHeight();

    double viewportHeight =
            scrollPane.getViewportBounds().getHeight();

    double sectionY =
            section.getBoundsInParent().getMinY();

    double maxScroll =
            contentHeight - viewportHeight;

    if (maxScroll > 0) {

        scrollPane.setVvalue(
                sectionY / maxScroll
        );
    }
}

private HBox createFooterBar() {
        HBox footer = new HBox();
        footer.setPrefHeight(52);
        footer.setMaxHeight(52);
        footer.setMinHeight(52);
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
      private HBox createFeatureStrip() {
        HBox outer = new HBox();
        outer.setPadding(new Insets(10, 50, 10, 50));

        HBox cardContainer = new HBox(12);
        cardContainer.setPadding(new Insets(16, 18, 16, 18));
        cardContainer.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 16; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16;");
        cardContainer.setTranslateY(-200);
        HBox.setHgrow(cardContainer, Priority.ALWAYS);

        DropShadow shadow = new DropShadow(12, Color.rgb(16, 42, 67, 0.04));
        cardContainer.setEffect(shadow);

        cardContainer.getChildren().addAll(
                createFeatureItem("AI Event Planner", "Smart suggestions for timelines, vendors, and more.", "M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4Z"),
                createFeatureItem("Budget Optimizer", "Get the best event experience within your budget.", "M11.8,10.9C9.53,10.31 8.8,9.7 8.8,8.75C8.8,7.66 9.81,6.9 11.5,6.9C13.28,6.9 13.94,7.75 14,9H16.21C16.14,7.28 15.09,5.7 13,5.19V3H10V5.16C8.06,5.58 6.5,6.84 6.5,8.76C6.5,11.08 8.41,12.23 11.2,12.9C13.7,13.5 14.2,14.38 14.2,15.31C14.2,16 13.6,17.1 11.5,17.1C9.37,17.1 8.53,16.03 8.4,14.5H6.18C6.3,16.89 8.16,18.3 10,18.72V21H13V18.79C15,18.38 16.5,17.2 16.5,15.22C16.5,12.5 14.07,11.5 11.8,10.9Z"),
                createFeatureItem("Conflict Detection", "Detect scheduling, venue, and resource conflicts.", "M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1Z"),
                createFeatureItem("Emergency Venue", "Find nearby alternative venues instantly.", "M12,2A7,7 0 0,0 5,9C5,14.25 12,22 12,22C12,22 19,14.25 19,9A7,7 0 0,0 12,2M12,11.5A2.5,2.5 0 0,1 9.5,9A2.5,2.5 0 0,1 12,6.5A2.5,2.5 0 0,1 14.5,9A2.5,2.5 0 0,1 12,11.5Z"),
                createFeatureItem("Weather Alerts", "Real-time weather updates for better planning.", "M19.35,10.04C18.67,6.59 15.64,4 12,4C9.11,4 6.6,5.64 5.35,8.04C2.34,8.36 0,10.91 0,14A6,6 0 0,0 6,20H19A5,5 0 0,0 24,15C24,12.36 21.95,10.22 19.35,10.04Z"),
                createFeatureItem("QR Check-in", "Seamless guest check-in with QR codes.", "M3,11H11V3H3M5,5H9V9H5M13,3V11H21V3M15,5H19V9H15M3,21H11V13H3M5,15H9V19H5M18,13H21V15H18M13,13H15V15H13M13,17H15V19H13M15,15H18V17H15M18,18H21V21H18M15,19H18V21H15Z")
        );

        outer.getChildren().add(cardContainer);
        return outer;
    }

    private VBox createFeatureItem(String title, String desc, String svgPathData) {
        VBox item = new VBox(8);
        item.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(item, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBg = new StackPane();
        Circle c = new Circle(16, Color.web(LIGHT_BLUE));
        SVGPath icon = createSVGIcon(svgPathData, PRIMARY_BLUE, 0.7);
        iconBg.getChildren().addAll(c, icon);

        Label t = new Label(title);
        t.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        top.getChildren().addAll(iconBg, t);

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-family: " + FONT_FAMILY + "; -fx-font-size: 9.5px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        item.getChildren().addAll(top, d);
        return item;
    }


}
