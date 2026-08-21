package com.techtrio.view;

import com.techtrio.controller.NavigationManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ReportA  {
        private Scene reportScene;


    private final String NAVY = "#062B63";
    private final String BG = "#F6F8FB";
    private final String BORDER = "#D6DEE8";
    
    public Scene getreportScene(){

        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        /* ================= SIDEBAR ================= */

        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(300);
        sidebar.setPadding(new Insets(28, 14, 18, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.LIGHTGRAY);
        admin.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        VBox brand = new VBox(2, logo, admin);
        brand.setPadding(new Insets(0, 0, 18, 8));

        

        sidebar.getChildren().addAll(
                brand,
            
                navButton("▦   Dashboard", false),
                navButton("▣   Event Requests", false),
                navButton("▣   Events", false),
                navButton("▦   Calendar", false),
                navButton("♙   Customers", false),
                navButton("♙   Organizers", false),
                navButton("▱   Vendors", false),
                navButton("△   Conflict Detection", false),
                navButton("▣   Reports & Analytics", true)
        );

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
                sideSpacer,
                navButton("🔔   Notifications", false),
                navButton("⚙   Settings", false),
                navButton("👤   Profile", false),
                navButton("?   Support", false)
        );

        root.setLeft(sidebar);

        /* ================= MAIN ================= */

        VBox main = new VBox(18);
        main.setPadding(new Insets(25, 28, 30, 28));

        Label title = new Label("Reports & Analytics");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(NAVY));

        Label subtitle = new Label(
                "View system performance, event trends and business insights."
        );
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.web("#394352"));

        ComboBox<String> month = new ComboBox<>();
        month.getItems().addAll(
                "This Month", "Last Month", "Last 2 Months", "This Year"
        );
        month.setValue("This Month");
        month.setPrefWidth(130);

        Button back = new Button(" Back to Dashboard");
        back.setStyle(
                "-fx-background-color:NAVY;" +
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:" + BG + ";" +
                "-fx-font-weight:bold;"+
                "-fx-font-size:15;"
        );
        

        Button export = new Button("⇩  Export Report");
        export.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-weight:bold;"
        );

        export.setOnAction(e ->
                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Report exported successfully."
                ).showAndWait()
        );

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        HBox heading = new HBox(
                new VBox(3, title, subtitle),
                topSpace,
    
                month,
                export
        );
        heading.setAlignment(Pos.CENTER_LEFT);

        /* ================= KPI CARDS ================= */

        HBox cards = new HBox(12);

        cards.getChildren().addAll(
                card("TOTAL EVENTS", "45", "All events"),
                card("COMPLETED EVENTS", "28", "Successfully completed"),
                card("UPCOMING EVENTS", "12", "Scheduled events"),
                card("TOTAL REVENUE", "₹4,85,000", "This month"),
                card("TOTAL BOOKINGS", "156", "Confirmed bookings")
        );

        /* ================= EVENT OVERVIEW ================= */

        CategoryAxis eventX = new CategoryAxis();
        NumberAxis eventY = new NumberAxis();

        eventX.setLabel("");
        eventY.setLabel("");

        BarChart<String, Number> eventChart =
                new BarChart<>(eventX, eventY);

        eventChart.setLegendVisible(false);
        eventChart.setAnimated(false);
        eventChart.setTitle("");

        XYChart.Series<String, Number> eventSeries =
                new XYChart.Series<>();

        eventSeries.getData().add(
                new XYChart.Data<>("Planning", 5)
        );
        eventSeries.getData().add(
                new XYChart.Data<>("Upcoming", 12)
        );
        eventSeries.getData().add(
                new XYChart.Data<>("Completed", 28)
        );
        eventSeries.getData().add(
                new XYChart.Data<>("Cancelled", 0)
        );

        eventChart.getData().add(eventSeries);
        eventChart.setPrefHeight(250);

        VBox eventBox = panel(
                "Event Overview",
                eventChart
        );

        /* ================= EVENTS BY TYPE ================= */

        PieChart pie = new PieChart();

        pie.getData().addAll(
                new PieChart.Data("Corporate", 40),
                new PieChart.Data("Birthday", 15),
                new PieChart.Data("College", 30),
                new PieChart.Data("Family & Other", 10)
        );

        pie.setLegendVisible(true);
        pie.setLabelsVisible(false);
        pie.setStartAngle(90);
        pie.setPrefHeight(250);

        VBox typeBox = panel(
                "Events by Type",
                pie
        );

        HBox charts = new HBox(18, eventBox, typeBox);
        HBox.setHgrow(eventBox, Priority.ALWAYS);
        HBox.setHgrow(typeBox, Priority.ALWAYS);

        /* ================= REVENUE ================= */

        Label revenueValue = new Label("₹4,85,000");
        revenueValue.setFont(
                Font.font("Arial", FontWeight.BOLD, 17)
        );
        revenueValue.setTextFill(Color.web(NAVY));

        Label avg = new Label("Avg. Budget: ₹32,333");
        avg.setFont(Font.font("Arial", 11));

        VBox revenueRight = new VBox(2, revenueValue, avg);
        revenueRight.setAlignment(Pos.CENTER_RIGHT);

        Label revenueTitle = new Label("Revenue Overview");
        revenueTitle.setFont(
                Font.font("Arial", FontWeight.NORMAL, 15)
        );

        Region rSpace = new Region();
        HBox.setHgrow(rSpace, Priority.ALWAYS);

        HBox revenueHead = new HBox(
                revenueTitle,
                rSpace,
                revenueRight
        );

        TableView<String[]> revenueTable = new TableView<>();
        revenueTable.setPrefHeight(210);

        TableColumn<String[], String> monthCol =
                new TableColumn<>("MONTH");

        TableColumn<String[], String> revenueCol =
                new TableColumn<>("REVENUE");

        TableColumn<String[], String> growthCol =
                new TableColumn<>("GROWTH");

        monthCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[0]
                )
        );

        revenueCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[1]
                )
        );

        growthCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[2]
                )
        );

        monthCol.setPrefWidth(100);
        revenueCol.setPrefWidth(150);
        growthCol.setPrefWidth(100);

        revenueTable.getColumns().addAll(
                monthCol, revenueCol, growthCol
        );

        revenueTable.getItems().addAll(
                new String[]{"Jan", "₹60,000", "-"},
                new String[]{"Feb", "₹75,000", "+25%"},
                new String[]{"Mar", "₹55,000", "-26%"},
                new String[]{"Apr", "₹90,000", "+63%"},
                new String[]{"May", "₹80,000", "-11%"},
                new String[]{"Jun", "₹1,25,000", "+56%"}
        );

        VBox revenueBox = new VBox(
                10,
                revenueHead,
                revenueTable
        );

        revenueBox.setPadding(new Insets(18));
        revenueBox.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        /* ================= CONFLICT SUMMARY ================= */

        VBox conflictBox = new VBox(12);
        conflictBox.setPadding(new Insets(18));
        conflictBox.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        Label conflictTitle = new Label("Conflict Summary");
        conflictTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 15)
        );
        conflictTitle.setTextFill(Color.web(NAVY));

        HBox conflictCards = new HBox(8);

        conflictCards.getChildren().addAll(
                smallCard("Total", "12"),
                smallCard("Critical", "3"),
                smallCard("Pending", "6"),
                smallCard("Resolved", "3")
        );

        conflictBox.getChildren().addAll(
                conflictTitle,
                conflictCards,
                progress("Organizer", "4", 0.40),
                progress("Vendor", "5", 0.55),
                progress("Venue", "2", 0.22),
                progress("Schedule", "1", 0.14)
        );

        HBox revenueRow = new HBox(
                18,
                revenueBox,
                conflictBox
        );

        HBox.setHgrow(revenueBox, Priority.ALWAYS);
        HBox.setHgrow(conflictBox, Priority.ALWAYS);

        /* ================= TOP VENDORS ================= */

        VBox vendorBox = new VBox(10);
        vendorBox.setPadding(new Insets(18));
        vendorBox.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        Label vendorTitle = new Label("Top Performing Vendors");
        vendorTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 15)
        );
        vendorTitle.setTextFill(Color.web(NAVY));

        TableView<String[]> vendorTable = new TableView<>();
        vendorTable.setPrefHeight(170);

        TableColumn<String[], String> vendor =
                new TableColumn<>("VENDOR");

        TableColumn<String[], String> events =
                new TableColumn<>("EVENTS");

        TableColumn<String[], String> rating =
                new TableColumn<>("RATING");

        TableColumn<String[], String> status =
                new TableColumn<>("STATUS");

        vendor.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[0]
                )
        );

        events.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[1]
                )
        );

        rating.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[2]
                )
        );

        status.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue()[3]
                )
        );

        vendor.setPrefWidth(250);
        events.setPrefWidth(120);
        rating.setPrefWidth(120);
        status.setPrefWidth(120);

        vendorTable.getColumns().addAll(
                vendor, events, rating, status
        );

        vendorTable.getItems().addAll(
                new String[]{"Floral Designs", "12", "★ 4.8", "Active"},
                new String[]{"Vision AV Solutions", "9", "★ 4.6", "Active"},
                new String[]{"Elite Catering", "8", "★ 4.5", "Active"}
        );

        vendorBox.getChildren().addAll(
                vendorTitle,
                vendorTable
        );

        /* ================= REPORT BUTTONS ================= */

        

       
      

        main.getChildren().addAll(

                back,
                heading,
                cards,
                charts,
                revenueRow,
                vendorBox
                
        );

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        root.setCenter(scroll);

        Scene scene = new Scene(root, 1550, 750);
        reportScene = scene;
        return reportScene;
    }

        

    /* ================= SIDEBAR BUTTON ================= */
    private Button navButton(String text, boolean active) {

    Button b = new Button(text);

    b.setMaxWidth(Double.MAX_VALUE);
    b.setAlignment(Pos.CENTER_LEFT);
    b.setPadding(new Insets(10, 8, 10, 8));

    b.setFont(Font.font(
            "Arial",
            active ? FontWeight.BOLD : FontWeight.NORMAL,
            13
    ));

    if (active) {

        b.setStyle(
                "-fx-background-color:#BBDCF2;" +
                "-fx-text-fill:#062B63;" +
                "-fx-background-radius:7;"
        );

    } else {

        b.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:white;"
        );
    }

    // Navigation
    b.setOnAction(e -> {

        Stage stage =
                (Stage) b.getScene().getWindow();

        String page = text
                .replace("▦", "")
                .replace("▣", "")
                .replace("♙", "")
                .replace("▱", "")
                .replace("△", "")
                .replace("🔔", "")
                .replace("⚙", "")
                .replace("👤", "")
                .replace("?", "")
                .trim();

        NavigationManager.navigate(page, stage);
    });

    return b;
}
    
    /* ================= KPI CARD ================= */

    private VBox card(
            String title,
            String value,
            String sub
    ) {

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        t.setTextFill(Color.web("#303846"));

        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        v.setTextFill(Color.web(NAVY));

        Label s = new Label(sub);
        s.setFont(Font.font("Arial", 12));
        s.setTextFill(Color.web("#697586"));

        VBox box = new VBox(7, t, v, s);
        box.setPadding(new Insets(16));
        box.setPrefHeight(120);
        box.setMaxWidth(Double.MAX_VALUE);

        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        HBox.setHgrow(box, Priority.ALWAYS);

        return box;
    }

    /* ================= PANEL ================= */

    private VBox panel(
            String title,
            Node content
    ) {

        Label l = new Label(title);
        l.setFont(
                Font.font("Arial", FontWeight.NORMAL, 15)
        );

        VBox box = new VBox(10, l, content);
        box.setPadding(new Insets(18));
        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        HBox.setHgrow(box, Priority.ALWAYS);

        return box;
    }

    /* ================= SMALL CONFLICT CARD ================= */

    private VBox smallCard(
            String title,
            String value
    ) {

        Label t = new Label(title);
        t.setFont(Font.font("Arial", 11));

        Label v = new Label(value);
        v.setFont(
                Font.font("Arial", FontWeight.BOLD, 18)
        );

        VBox box = new VBox(3, t, v);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8));
        box.setPrefWidth(80);

        box.setStyle(
                "-fx-background-color:#F1F4F7;" +
                "-fx-background-radius:5;"
        );

        return box;
    }

    /* ================= PROGRESS ================= */

    private VBox progress(
            String name,
            String value,
            double amount
    ) {

        Label title = new Label(name + "                         " + value);

        ProgressBar bar = new ProgressBar(amount);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle(value);
        bar.setPrefHeight(7);

         bar.setStyle(
        "-fx-accent: #062B63;" +
        "-fx-control-inner-background: #E5EBF1;"
    );

        VBox box = new VBox(8, title, bar);

        return box;
    }

    /* ================= REPORT BUTTON ================= */

    private Button reportButton(String text) {

        Button b = new Button(text);
        b.setPadding(new Insets(8, 14, 8, 14));

        b.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#C8D3DF;" +
                "-fx-border-radius:6;" +
                "-fx-text-fill:#062B63;"
        );

        return b;
    }

    private void showMessage(String message) {
        new Alert(
                Alert.AlertType.INFORMATION,
                message
        ).showAndWait();
    }

}



        
    
    
