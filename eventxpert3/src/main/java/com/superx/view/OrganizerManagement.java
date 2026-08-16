package com.superx.view;

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

public class OrganizerManagement extends Application {

    final String PRIMARY="#002A60", SECONDARY="#8CAFC6",LIGHT="#C2E0F4", BG="#F6F9FC";

    TableView<Organizer> table;
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
        side.setCursor(javafx.scene.Cursor.HAND);

        Label logo=label("EventXpert",30,true);
        logo.setTextFill(Color.WHITE);

        Label admin=new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head=new VBox(2,logo,admin);
        head.setPadding(new Insets(0,12,18,12));

        String[] items={"Dashboard","Event Requests","Events","Calendar","Customers","Organizers","Vendors","Conflict Detection","Reports & Analytics"};

        VBox menu=new VBox(4);

        for(String x:items){
            Label l=new Label(x);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(9,14,9,14));
            l.setMaxWidth(Double.MAX_VALUE);

            if(x.equals("Organizers"))
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

        side.getChildren().addAll(head,menu,gap,
                notifications,settings,profile,support);

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

    ObservableList<Organizer> data=FXCollections.observableArrayList(
            new Organizer("Elite Event Planners","elite@example.com",
                    "+91 98765 43210","45 Events","4.8 ★","Pune","Active"),
            new Organizer("Dream Events","dream@example.com",
                    "+91 98234 56789","32 Events","4.6 ★","Pune","Active"),
            new Organizer("Perfect Moments","perfect@example.com",
                    "+91 97654 32109","51 Events","4.7 ★","Mumbai","Busy"),
            new Organizer("Royal Celebrations","royal@example.com",
                    "+91 98989 12345","28 Events","4.5 ★","Nashik","Active")
           
    );

    void createTable(){
        table=new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<Organizer,String> name=col("Organizer",Organizer::getName);
        TableColumn<Organizer,String> email=col("Email",Organizer::getEmail);
        TableColumn<Organizer,String> phone=col("Phone",Organizer::getPhone);
        TableColumn<Organizer,String> events=col("Events Done",Organizer::getEvents);
        TableColumn<Organizer,String> rating=col("Rating",Organizer::getRating);
        TableColumn<Organizer,String> location=col("Location",Organizer::getLocation);
        TableColumn<Organizer,String> status=col("Status",Organizer::getStatus);

        TableColumn<Organizer,String> action=new TableColumn<>("Actions");
        action.setCellFactory(c->new TableCell<>(){
            Button b=button("View");
            {
                b.setOnAction(e->{
                    Organizer o=getTableView().getItems().get(getIndex());
                    new Alert(Alert.AlertType.INFORMATION,
                            "Organizer: "+o.getName()).showAndWait();
                });
            }
            @Override protected void updateItem(String s,boolean empty){
                super.updateItem(s,empty);
                setGraphic(empty?null:b);
            }
        });

        table.getColumns().addAll(name,email,phone,events,rating,location,status,action);
    }

    TableColumn<Organizer,String> col(String title,
            java.util.function.Function<Organizer,String> f){
        TableColumn<Organizer,String> c=new TableColumn<>(title);
        c.setCellValueFactory(x->
                new javafx.beans.property.SimpleStringProperty(
                        f.apply(x.getValue())));
        return c;
    }

    void filterData(String text){
        String q=text.toLowerCase().trim();
        table.setItems(data.filtered(o->
                q.isEmpty() ||
                o.getName().toLowerCase().contains(q) ||
                o.getEmail().toLowerCase().contains(q) ||
                o.getLocation().toLowerCase().contains(q)));
    }

    @Override
    public void start(Stage stage){

        BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");

        root.setLeft(sidebar());

        VBox content=new VBox(18);
        content.setPadding(new Insets(25));

        //HBox top=new HBox();
       // top.setAlignment(Pos.CENTER_LEFT);


        //search.textProperty().addListener((a,b,c)->filterData(c));

        //top.getChildren().add(search);

        Label title=label("Organizer Management",32,true);
        Label sub=label(
                "Manage and monitor all registered organizers on EventXpert.",
                18,false);

        VBox heading=new VBox(5,title,sub);

        HBox stats=new HBox(15,
                stat("Total Organizers","86","72 Active"),
                stat("Active Organizers","72","Currently active"),
                stat("New Organizers","8","This month"),
                stat("Inactive Organizers","6","Need attention"));
            stats.setStyle("fx-font-size:18");

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        VBox organizerCard=card();

        HBox tableHead=new HBox();
        tableHead.setAlignment(Pos.CENTER_LEFT);

        Label all=label("All Organizers",21,true);

        Region space=new Region();
        HBox.setHgrow(space,Priority.ALWAYS);

        TextField tableSearch=new TextField();
        tableSearch.setPromptText("Search organizers...");
        tableSearch.setPrefWidth(280);
        tableSearch.textProperty().addListener((a,b,c)->filterData(c));

        Button filter=button("☰  Filter");
        filter.setOnAction(e->{
            tableSearch.clear();

        });

        tableHead.getChildren().addAll(all,space,tableSearch,filter);

        createTable();

       

        HBox pagination=new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        Button previous=button("Previous");
        Button next=button("Next");

        pagination.getChildren().addAll(previous,next);

        HBox footer=new HBox();
        Region fspace=new Region();
        HBox.setHgrow(fspace,Priority.ALWAYS);
        footer.getChildren().addAll(fspace,pagination);

        organizerCard.getChildren().addAll(
                tableHead,table,new Separator(),footer);

        content.getChildren().addAll(heading,stats,organizerCard);

        ScrollPane scroll=new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:"+BG+";");

        root.setCenter(scroll);

        Scene scene=new Scene(root,1550,750);

        stage.setTitle("EventXpert - Organizer Management");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.show();
    }

    public static class Organizer{
        String name,email,phone,events,rating,location,status;

        Organizer(String name,String email,String phone,String events,
                  String rating,String location,String status){
            this.name=name;
            this.email=email;
            this.phone=phone;
            this.events=events;
            this.rating=rating;
            this.location=location;
            this.status=status;
        }

        String getName(){return name;}
        String getEmail(){return email;}
        String getPhone(){return phone;}
        String getEvents(){return events;}
        String getRating(){return rating;}
        String getLocation(){return location;}
        String getStatus(){return status;}
    }


}
