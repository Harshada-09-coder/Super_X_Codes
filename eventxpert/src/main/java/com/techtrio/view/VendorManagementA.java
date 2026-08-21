package com.techtrio.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VendorManagementA extends Application {
    final String PRIMARY="#002A60", SECONDARY="#8CAFC6",LIGHT="#C2E0F4", BG="#F6F9FC";
     

    TableView<Vendor> table;
    TextField search;

    Label label(String text,int size,boolean bold){
        Label l=new Label(text);
        l.setFont(Font.font("Arial",size));
        l.setTextFill(Color.web(PRIMARY));
        if(bold) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    Button button(String text){
        Button b=new Button(text);
        b.setStyle("-fx-background-color:white;-fx-text-fill:"+PRIMARY+
                ";-fx-border-color:"+SECONDARY+";-fx-border-radius:6;"+
                "-fx-background-radius:6;-fx-font-weight:bold;");
        b.setPadding(new Insets(8,14,8,14));
        return b;
    }

    VBox card(){
        VBox v=new VBox(8);
        v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;"+
                "-fx-border-radius:8;-fx-background-radius:8;");
        return v;
    }

    VBox sidebar(){
        VBox side=new VBox(7);
        side.setPrefWidth(285);
        side.setPadding(new Insets(24,12,15,12));
        side.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=label("EventXpert",30,true);
        logo.setTextFill(Color.WHITE);

        Label admin=new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head=new VBox(2,logo,admin);
        head.setPadding(new Insets(0,12,18,12));

        String[] items={"Dashboard","Event Requests","Events","Calendar",
                "Customers","Organizers","Vendors","Conflict Detection",
                "Reports & Analytics"};

        VBox menu=new VBox(4);

        for(String x:items){
            Label l=new Label(x);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(9,14,9,14));
            l.setMaxWidth(Double.MAX_VALUE);

            if(x.equals("Vendors"))
                l.setStyle("-fx-background-color:#1264A3;"+
                        "-fx-background-radius:6;-fx-font-weight:bold;");

            menu.getChildren().add(l);
        }

        Region gap=new Region();
        VBox.setVgrow(gap,Priority.ALWAYS);

        Label notifications=new Label("🔔  Notifications");
        Label settings=new Label("⚙  Settings");
        Label profile=new Label("👤  Profile");
        Label support=new Label("?  Support");

        for(Label l:new Label[]{notifications,settings,profile,support}){
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(7,14,7,14));
        }

        side.getChildren().addAll(
                head,menu,gap,
                notifications,settings,profile,support
        );

        return side;
    }

    VBox stat(String title,String value,String sub){
        VBox v=card();
        v.setPrefHeight(125);
        v.getChildren().addAll(
                label(title.toUpperCase(),14,false),
                label(value,34,true),
                label(sub,14,false)
        );
        return v;
    }

    ObservableList<Vendor> data=FXCollections.observableArrayList(
            new Vendor("Royal Caterers","Catering",
                    "royal@example.com","+91 98765 43210",
                    "Pune","48 Events","4.7 ★","Active"),

            new Vendor("Projector and microphone","Special Requirements",
                    "floral@example.com","+91 98234 56789",
                    "Mumbai","36 Events","4.6 ★","Active"),

            new Vendor("Click Moments","Photography",
                    "click@example.com","+91 97654 32109",
                    "Pune","52 Events","4.8 ★","Active"),

            new Vendor("Sound Pro","Entertainment",
                    "sound@example.com","+91 98989 12345",
                    "Nashik","29 Events","4.4 ★","Busy"),

            new Vendor("Grand Venue","Venue",
                    "venue@example.com","+91 98123 45678",
                    "Pune","41 Events","4.5 ★","Inactive")
    );

    void createTable(){

        table=new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<Vendor,String> name=col("Vendor",Vendor::getName);
        TableColumn<Vendor,String> type=col("Type",Vendor::getType);
        TableColumn<Vendor,String> email=col("Email",Vendor::getEmail);
        TableColumn<Vendor,String> phone=col("Phone",Vendor::getPhone);
        TableColumn<Vendor,String> location=col("Location",Vendor::getLocation);
        TableColumn<Vendor,String> events=col("Events Served",Vendor::getEvents);
        TableColumn<Vendor,String> rating=col("Rating",Vendor::getRating);
        TableColumn<Vendor,String> status=col("Status",Vendor::getStatus);

        TableColumn<Vendor,String> action=new TableColumn<>("Actions");

        action.setCellFactory(c->new TableCell<>(){
            Button b=button("View");

            {
                b.setOnAction(e->{
                    Vendor v=getTableView().getItems().get(getIndex());
                    new Alert(Alert.AlertType.INFORMATION,
                            "Vendor: "+v.getName()).showAndWait();
                });
            }

            @Override
            protected void updateItem(String s,boolean empty){
                super.updateItem(s,empty);
                setGraphic(empty?null:b);
            }
        });

        table.getColumns().addAll(
                name,type,email,phone,location,events,rating,status,action
        );
    }

    TableColumn<Vendor,String> col(
            String title,
            java.util.function.Function<Vendor,String> f){

        TableColumn<Vendor,String> c=new TableColumn<>(title);

        c.setCellValueFactory(x->
                new javafx.beans.property.SimpleStringProperty(
                        f.apply(x.getValue())));

        return c;
    }

    void filterData(String text){

        String q=text.toLowerCase().trim();

        table.setItems(data.filtered(v->
                q.isEmpty() ||
                v.getName().toLowerCase().contains(q) ||
                v.getType().toLowerCase().contains(q) ||
                v.getEmail().toLowerCase().contains(q) ||
                v.getLocation().toLowerCase().contains(q)));
    }

    @Override
    public void start(Stage stage){

        BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");

        root.setLeft(sidebar());

        VBox content=new VBox(18);
        content.setPadding(new Insets(25));

        Label title=label("Vendor Management",32,true);
        Label sub=label(
                "Manage and monitor all registered vendors on EventXpert.",
                18,false);

        VBox heading=new VBox(5,title,sub);

        HBox stats=new HBox(
                15,
                stat("Total Vendors","214","198 Active"),
                stat("Active Vendors","198","Currently active"),
                stat("New Vendors","12","This month"),
                stat("Inactive Vendors","16","Need attention")
        );

        stats.setStyle("-fx-font-size:18px;");

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        VBox vendorCard=card();

        HBox tableHead=new HBox();
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all=label("All Vendors",21,true);

        Region space=new Region();
        HBox.setHgrow(space,Priority.ALWAYS);

        TextField tableSearch=new TextField();
        tableSearch.setPromptText("Search vendors...");
        tableSearch.setPrefWidth(280);

        tableSearch.textProperty().addListener(
                (a,b,c)->filterData(c));

        Button filter=button("☰  Filter");

        filter.setOnAction(e->{
            tableSearch.clear();
        });

        tableHead.getChildren().addAll(
                all,space,tableSearch,filter
        );

        createTable();

        HBox pagination=new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        Button previous=button("Previous");
        Button next=button("Next");

        pagination.getChildren().addAll(previous,next);

        HBox footer=new HBox();

        Region fspace=new Region();
        HBox.setHgrow(fspace,Priority.ALWAYS);

        Label showing=label(
                "Showing 1–10 of 214 vendors",14,false);

        footer.getChildren().addAll(
                showing,fspace,pagination
        );

        vendorCard.getChildren().addAll(
                tableHead,table,new Separator(),footer
        );

        content.getChildren().addAll(
                heading,stats,vendorCard
        );

        ScrollPane scroll=new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
                "-fx-background-color:"+BG+";"
        );

        root.setCenter(scroll);

        Scene scene=new Scene(root,1550,750);

        stage.setTitle("EventXpert - Vendor Management");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.show();
    }

    public static class Vendor{

        String name,type,email,phone,location,
                events,rating,status;

        Vendor(String name,String type,String email,
               String phone,String location,String events,
               String rating,String status){

            this.name=name;
            this.type=type;
            this.email=email;
            this.phone=phone;
            this.location=location;
            this.events=events;
            this.rating=rating;
            this.status=status;
        }

        String getName(){return name;}
        String getType(){return type;}
        String getEmail(){return email;}
        String getPhone(){return phone;}
        String getLocation(){return location;}
        String getEvents(){return events;}
        String getRating(){return rating;}
        String getStatus(){return status;}
    }
}