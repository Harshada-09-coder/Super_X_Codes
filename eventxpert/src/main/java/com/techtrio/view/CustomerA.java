package com.techtrio.view;
import com.techtrio.controller.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CustomerA {
    private Scene customerScene;

    final String PRIMARY = "#002A60", SECONDARY = "#8CAFC6", LIGHT = "#C2E0F4", BG = "#F6F9FC";
    final String WHITE = "#FFFFFF";

    TableView<CustomerData> table = new TableView<>();
    ObservableList<CustomerData> data = FXCollections.observableArrayList();

    TextField search = new TextField();
    ComboBox<String> status = new ComboBox<>(), activity = new ComboBox<>(), date = new ComboBox<>();

    public Scene getcustomerScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // sidebar

        VBox sidebar = new VBox(7);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25, 18, 18, 18));
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        Label logo = lbl("EventXpert", 26, true);
        Label admin = lbl("ADMIN DASHBOARD", 12, true);
        logo.setTextFill(Color.WHITE);
        admin.setTextFill(Color.WHITE);

        VBox logoBox = new VBox(1, logo, admin);
        VBox menu = new VBox(7);

        String[] items = { "Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors",
                "Conflict Detection", "Reports & Analytics" };

        for (String s : items) {
            Label m = lbl(s, 14, true);
            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9, 13, 9, 13));
            if (s.equals("Customer"))
                m.setStyle("-fx-background-color:#084A8F;" + "-fx-text-fill:white;" + "-fx-background-radius:4;");

            m.setOnMouseClicked(e -> {

                Stage stage = (Stage) m.getScene().getWindow();

                NavigationManager.navigate(s, stage);
            });
            menu.getChildren().add(m);
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        Label n = lbl("🔔  Notifications", 13, true);
        Label se = lbl("⚙  Settings", 13, true);
        Label pr = lbl("👤  Profile", 13, true);
        Label sp = lbl("?  Support", 13, true);

        for (Label x : new Label[] { n, se, pr, sp }) {
            x.setTextFill(Color.WHITE);
            x.setPadding(new Insets(7, 10, 7, 10));
        }

        sidebar.getChildren().addAll(logoBox, menu, space, n, se, pr, sp);

        Region hs = new Region();
        HBox.setHgrow(hs, Priority.ALWAYS);

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search events, organizers, vendors...");
        topSearch.setPrefWidth(450);

        HBox header = new HBox(topSearch, new Region(),
                lbl("🔔", 18, false), lbl("?", 18, true), lbl("⚙", 18, false), lbl("👤", 18, false));
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(18);
        header.setPadding(new Insets(12, 25, 12, 25));
        header.setStyle("-fx-background-color:white;");

        Label title = lbl("Customer Management", 30, true);
        title.setTextFill(Color.web(PRIMARY));

        Label sub = lbl(" Manage and monitor all registered customers on EventXpert.", 15, false);
        VBox heading = new VBox(3, title, sub);

        HBox cards = new HBox(20,
                card("Total Customers", "1,248", "+12 this month"),
                card("Active Customers", "1,120", "Currently active"),
                card("New Customers", "48", "This month"),
                card("Inactive Customers", "128", "Need attention"));
        cards.setCursor(javafx.scene.Cursor.HAND);

        search.setPromptText("Search customers...");
        search.setPrefWidth(280);

        setup(status, "All Status", "Active", "Inactive");
        setup(date, "All Dates", "Today", "This Week", "This Month");
        setup(activity, "All Activity", "Has Events", "No Events");

        Button clear = btn("Clear Filters", false);
        clear.setOnAction(e -> {
            search.clear();
            status.setValue("All Status");
            date.setValue("All Dates");
            activity.setValue("All Activity");
        });

        HBox filters = new HBox(12, search, status, date, activity, clear);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(14));
        filters.setStyle("-fx-background-color:white;-fx-border-color:" + SECONDARY +
                ";-fx-border-radius:8;-fx-background-radius:8;");

        Label all = lbl("All Customers", 20, true);
        all.setTextFill(Color.web(PRIMARY));

        TextField tableSearch = new TextField();
        tableSearch.setPromptText("Search customers...");
        tableSearch.setPrefWidth(250);

        Button filter = btn("☰ Filter", false);
        ComboBox<String> sort = new ComboBox<>();
        sort.getItems().addAll("Recent", "Name");
        sort.setValue("Recent");

        Region ts = new Region();
        HBox.setHgrow(ts, Priority.ALWAYS);

        HBox tableHead = new HBox(all, ts, tableSearch, filter,
                lbl("Sort by:", 13, false), sort);
        tableHead.setAlignment(Pos.CENTER_LEFT);
        tableHead.setSpacing(10);
        tableHead.setPadding(new Insets(15));

        createTable();
        loadData();

        table.setItems(data);
        table.setPrefHeight(350);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // PAGINATION
        Label showing = lbl("Showing 1–5 of 1,248 customers", 13, false);

        Region ps = new Region();
        HBox.setHgrow(ps, Priority.ALWAYS);

        Button previous = btn("‹ Previous", false);
        Button next = btn("Next ›", true);

        HBox pagination = new HBox(showing, ps, previous, next);
        pagination.setAlignment(Pos.CENTER_LEFT);
        pagination.setSpacing(10);
        pagination.setPadding(new Insets(12, 15, 15, 15));

        VBox tableBox = new VBox(tableHead, table, pagination);
        tableBox.setStyle("-fx-background-color:white;-fx-border-color:" + SECONDARY +
                ";-fx-border-radius:8;-fx-background-radius:8;");

        VBox content = new VBox(15, heading, cards, filters, tableBox);
        content.setPadding(new Insets(20, 25, 25, 25));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        VBox center = new VBox(header, scroll);

        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene = new Scene(root, 1550, 750);
        customerScene = scene;
        return customerScene;

    }

    void createTable() {

        String[] names = { "Customer", "Email", "Phone", "Events",
                "Last Event", "Registered", "Status", "Actions" };

        String[] props = { "name", "email", "phone", "events",
                "lastEvent", "registered", "status" };

        for (int i = 0; i < props.length; i++) {
            TableColumn<CustomerData, String> c = new TableColumn<>(names[i]);
            c.setCellValueFactory(new PropertyValueFactory<>(props[i]));
            table.getColumns().add(c);
        }

        TableColumn<CustomerData, Void> action = new TableColumn<>("Actions");

        action.setCellFactory(col -> new TableCell<>() {
            Button view = btn("View", false);

            {
                view.setPrefWidth(60);
                view.setOnAction(e -> {
                    Stage stage = (Stage) view.getScene().getWindow();
                    NavigationManager.navigate("Customer Details", stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : view);
            }
        });

        table.getColumns().add(action);
    }

    void loadData() {
        data.addAll(
                new CustomerData("Sarah J.", "sarah.j@example.com", "+91 98765 43210",
                        "2 Events", "College Seminar", "Oct 12, 2023", "Active"),
                new CustomerData("Rahul Sharma", "rahul.sharma@example.com", "+91 98234 56789",
                        "1 Event", "Birthday Celebration", "Oct 08, 2023", "Active"),
                new CustomerData("Priya Mehta", "priya.mehta@example.com", "+91 97654 32109",
                        "3 Events", "Corporate Workshop", "Sep 28, 2023", "Active"),
                new CustomerData("Amit Patil", "amit.patil@example.com", "+91 98989 12345",
                        "0 Events", "—", "Sep 15, 2023", "Inactive"),
                new CustomerData("Neha Kulkarni", "neha.k@example.com", "+91 98123 45678",
                        "2 Events", "Family Reunion", "Aug 30, 2023", "Active"));
    }

    void setup(ComboBox<String> box, String... values) {
        box.getItems().addAll(values);
        box.setValue(values[0]);
        box.setPrefWidth(140);
    }

    Button btn(String text, boolean primary) {
        Button b = new Button(text);
        b.setPadding(new Insets(8, 14, 8, 14));
        b.setStyle("-fx-background-color:" + (primary ? PRIMARY : "white") +
                ";-fx-text-fill:" + (primary ? "white" : PRIMARY) +
                ";-fx-border-color:" + SECONDARY +
                ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-weight:bold;");
        return b;
    }

    VBox card(String title, String value, String sub) {
        Label a = lbl(title, 14, false);
        Label b = lbl(value, 38, true);
        b.setTextFill(Color.web(PRIMARY));

        VBox box = new VBox(4, a, b, lbl(sub, 13, false));
        box.setPadding(new Insets(18));
        box.setPrefHeight(130);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setStyle("-fx-background-color:white;-fx-border-color:#D7DEE7;" +
                "-fx-border-radius:8;-fx-background-radius:8;");
        return box;
    }

    Label lbl(String text, int size, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial",
                bold ? javafx.scene.text.FontWeight.BOLD : javafx.scene.text.FontWeight.NORMAL, size));
        return l;
    }

    public static class CustomerData {
        private final String name, email, phone, events, lastEvent, registered, status;

        CustomerData(String n, String e, String p, String ev, String le, String r, String s) {
            name = n;
            email = e;
            phone = p;
            events = ev;
            lastEvent = le;
            registered = r;
            status = s;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getEvents() {
            return events;
        }

        public String getLastEvent() {
            return lastEvent;
        }

        public String getRegistered() {
            return registered;
        }

        public String getStatus() {
            return status;
        }
    }
}
