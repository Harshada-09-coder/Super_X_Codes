package com.techtrio.view;
import com.techtrio.controller.NavigationManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EventRequestA {
    private Scene eventRequstScene;

   
    private final String PRIMARY = "#002A60";

    private final String LIGHT = "#C2E0F4";

    public Scene geteventRequestScene(){

        VBox sidebar=new VBox(12);
        sidebar.setPrefWidth(235);
        sidebar.setMinWidth(235);
        sidebar.setMaxWidth(235);
        sidebar.setPadding(new Insets(25,15,20,15));
        sidebar.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial",FontWeight.BOLD,24));

        Label admin=new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.web(LIGHT));
        admin.setFont(Font.font("Arial",FontWeight.BOLD,11));

        VBox logoBox=new VBox(2,logo,admin);
        VBox menu=new VBox(7);

        String[] items={"Dashboard","Event Requests","Events","Calendar","Customers","Organizers","Vendors","Conflict Detection","Reports"};

        for(String item:items){
            Label l=new Label(item);
            l.setMaxWidth(Double.MAX_VALUE);
            l.setPadding(new Insets(10,12,10,12));
            l.setFont(Font.font("Arial",FontWeight.BOLD,13));
            l.setTextFill(Color.WHITE);

            if(item.equals("Event Requests"))
                l.setStyle("-fx-background-color:#084A8F;-fx-text-fill:white;-fx-background-radius:4;");
             l.setOnMouseClicked(e -> {

        Stage stage =
            (Stage) l.getScene().getWindow();

        NavigationManager.navigate(item, stage);
    });
           

            menu.getChildren().add(l);
        }

        Region space=new Region();
        VBox.setVgrow(space,Priority.ALWAYS);

        Label notification=new Label("🔔  Notifications");
        Label settings=new Label("⚙  Settings");
        Label profile=new Label("👤  Profile");
        Label support=new Label("? Support ");

        for(Label l:new Label[]{notification,settings,profile,support}){
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",FontWeight.BOLD,13));
            l.setPadding(new Insets(7,10,7,10));
        }

        sidebar.getChildren().addAll(
                logoBox,menu,space,notification,settings,profile,support);

        
        HBox top=new HBox();
        top.setPadding(new Insets(10,25,10,35));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPrefHeight(65);
        top.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:#E5EAF0;-fx-border-width:0 0 1 0;");

        TextField searchTop=new TextField();
        searchTop.setPromptText("Search event requests, organizers, customers...");
        searchTop.setPrefWidth(430);
        searchTop.setPrefHeight(38);

        Region topSpace=new Region();
        HBox.setHgrow(topSpace,Priority.ALWAYS);

        Label bell=new Label("🔔");
        Label question=new Label("?");
        Label gear=new Label("⚙");
        Label user=new Label("👤");

        for(Label l:new Label[]{bell,question,gear,user}){
            l.setFont(Font.font("Arial",18));
            l.setPadding(new Insets(0,8,0,8));
        }

        top.getChildren().addAll(searchTop,topSpace,bell,question,gear,user);

        Label title=new Label("Event Requests");
        title.setFont(Font.font("Arial",FontWeight.BOLD,30));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle=new Label( "Review customer event requests and assign the right organizer.");
        subtitle.setFont(Font.font("Arial",16));
        subtitle.setTextFill(Color.web("#303746"));

        VBox heading=new VBox(5,title,subtitle);

        // Create Event moved here
        Button create=new Button(" + Create");
        create.setPrefHeight(40);
        create.setPadding(new Insets(9,18,9,18));
        create.setStyle(
                "-fx-background-color:"+PRIMARY+";"+
                "-fx-text-fill:white;"+
                "-fx-font-weight:bold;"+
                "-fx-background-radius:7;"
        );

        Region titleSpace=new Region();
        HBox.setHgrow(titleSpace,Priority.ALWAYS);

      
        HBox titleRow=new HBox(15,heading,titleSpace,create);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox headingBox=new VBox(titleRow);
        headingBox.setPadding(new Insets(15,30,15,30));

        HBox cards=new HBox(25);
        cards.setPadding(new Insets(5,30,20,30));

        cards.getChildren().addAll(
                card("","Pending","24 Need Review"),
                card("","Under Review","8 Currently Reviewing"),
                card("","Organizer Assignment","5 Waiting for Organizer"),
                card("","Assigned","12 Organizer Assigned")
        );

        // ================= FILTERS =================
        HBox filters=new HBox(18);
        filters.setPadding(new Insets(20));
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle(
                "-fx-background-color:#FFFFFF;"+
                "-fx-border-color:#D0D8E2;"+
                "-fx-border-radius:10;"+
                "-fx-background-radius:10;"
        );

        TextField search=new TextField();
        search.setPromptText("Search event requests...");
        search.setPrefWidth(255);
        search.setPrefHeight(42);

        ComboBox<String> status=combo("Status","Pending","Under Review","Assigned");
        ComboBox<String> type=combo("Event Types","Educational","Birthday","Corporate","Family");
        ComboBox<String> date=combo("Dates","Today","This Week","This Month");
        ComboBox<String> location=combo("Locations","Pune","Mumbai","Nashik");

     

        Button clear=new Button("Clear Filters");
        clear.setPrefHeight(42);
        clear.setStyle(
                "-fx-background-color:#FFFFFF;"+
                "-fx-border-color:#C8D1DC;"+
                "-fx-border-radius:7;"+
                "-fx-font-weight:bold;"
        );

        clear.setOnAction(e->{
            search.clear();

            status.getSelectionModel().selectFirst();
            type.getSelectionModel().selectFirst();
            date.getSelectionModel().selectFirst();
            location.getSelectionModel().selectFirst();

        });

        filters.getChildren().addAll(search,status,type,date,location,clear);

        // ================= TABLE =================
        TableView<Event> table=new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event,String> customer=col("Customer",Event::getCustomer);
        TableColumn<Event,String> event=col("Event",Event::getEvent);
        TableColumn<Event,String> eventType=col("Type",Event::getType);
        TableColumn<Event,String> eventDate=col("Event Date",Event::getDate);
        TableColumn<Event,String> guests=col("Guests",Event::getGuests);
        TableColumn<Event,String> locationCol=col("Location",Event::getLocation);
        TableColumn<Event,String> budget=col("Budget",Event::getBudget);
        TableColumn<Event,String> statusCol=col("Status",Event::getStatus);

        TableColumn<Event,Void> action=new TableColumn<>("Action");
        action.setPrefWidth(100);

        action.setCellFactory(c->new TableCell<>(){

            Button review=new Button("Review");

            {
                review.setStyle(
                        "-fx-background-color:#FFFFFF;"+
                        "-fx-border-color:"+PRIMARY+";"+
                        "-fx-text-fill:"+PRIMARY+";"+
                        "-fx-border-radius:7;"
                );

                review.setOnAction(e->{
                    Event selected=getTableView().getItems().get(getIndex());
                    showReview(selected);
                });
            }

            @Override
            protected void updateItem(Void item,boolean empty){
                super.updateItem(item,empty);
                setGraphic(empty?null:review);
            }
        });

        table.getColumns().addAll(customer,event,eventType,eventDate,guests,locationCol,budget,statusCol,action);

        table.getItems().addAll(
                new Event("SKNSCOE","College Seminar","Educational",
                        "18 August 2026","200","Pune","₹40,000","Pending"),

                new Event("Rahul K.","Birthday Celebration","Birthday",
                        "27 August 2026","100","Nashik","₹30,000","Under Review"),

                new Event("ABC Corporation","Corporate Workshop","Corporate",
                        "17 August 2026","200","Mumbai","₹60,000"," Organizer Assigned"),

                new Event("Priya M.","Family Reunion","Family",
                        "7 Sept 2026","75","Pune","₹25,000","Pending")
        );

       
        Button previous=new Button("Previous");
        Button next=new Button("Next");

        previous.setPrefWidth(90);
        next.setPrefWidth(70);
        previous.setPrefHeight(35);
        next.setPrefHeight(35);

        previous.setStyle(
                "-fx-background-color:#FFFFFF;"+
                "-fx-border-color:#C9D4E0;"+
                "-fx-border-radius:6;"
        );

        next.setStyle(
                "-fx-background-color:"+PRIMARY+";"+
                "-fx-text-fill:white;"+
                "-fx-font-weight:bold;"+
                "-fx-background-radius:6;"
        );

        previous.setOnAction(e->{
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Pagination");
            a.setHeaderText(null);
            a.setContentText("Previous requests");
            a.showAndWait();
        });

        next.setOnAction(e->{
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Pagination");
            a.setHeaderText(null);
            a.setContentText("Next requests");
            a.showAndWait();
        });

        Region pageSpace=new Region();
        HBox.setHgrow(pageSpace,Priority.ALWAYS);

        HBox pagination=new HBox(10,previous,pageSpace,next);
        pagination.setAlignment(Pos.CENTER_RIGHT);
        pagination.setPadding(new Insets(10,0,5,0));

        // ================= CONTENT =================
        VBox tableBox=new VBox(5,table,pagination);
        tableBox.setPadding(new Insets(20,30,10,30));
        VBox.setVgrow(table,Priority.ALWAYS);

        VBox content=new VBox(headingBox,cards,filters,tableBox);
        content.setStyle("-fx-background-color:#F8FAFC;");
        VBox.setVgrow(tableBox,Priority.ALWAYS);

        BorderPane right=new BorderPane();
        right.setTop(top);
        right.setCenter(content);

        HBox root=new HBox(sidebar,right);
        HBox.setHgrow(right,Priority.ALWAYS);

        ScrollPane scroll=new ScrollPane(root);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Scene scene=new Scene(scroll,1550,750);
        eventRequstScene = scene;

        return eventRequstScene;
    }


    private VBox card(String icon,String title,String value){

        Label i=new Label(icon);
        i.setFont(Font.font("Arial",20));
        i.setTextFill(Color.web(PRIMARY));

        Label t=new Label(title);
        t.setFont(Font.font("Arial",FontWeight.BOLD,21));
        t.setTextFill(Color.web("#111827"));

        Label v=new Label(value);
        v.setFont(Font.font("Arial",15));
        v.setTextFill(Color.web("#303746"));

        VBox box=new VBox(10,i,t,v);
        box.setPadding(new Insets(22));
        box.setPrefHeight(140);
        box.setMinWidth(240);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(
                "-fx-background-color:#FFFFFF;"+
                "-fx-border-color:#C9D4E0;"+
                "-fx-border-radius:10;"+
                "-fx-background-radius:10;"
        );

        HBox.setHgrow(box,Priority.ALWAYS);
        return box;
    }

    
    private ComboBox<String> combo(String first,String... values){

        ComboBox<String> box=new ComboBox<>();
        box.getItems().add(first);
        box.getItems().addAll(values);
        box.setValue(first);
        box.setPrefWidth(200);
        box.setPrefHeight(42);
        return box;
    }

    private TableColumn<Event,String> col(
            String name,
            java.util.function.Function<Event,String> value){

        TableColumn<Event,String> column=new TableColumn<>(name);

        column.setCellValueFactory(
                data->new SimpleStringProperty(
                        value.apply(data.getValue())
                )
        );

        return column;
    }

    private void showReview(Event event){

        Alert alert=new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Review Event Request");
        alert.setHeaderText("Event Request Details");

        alert.setContentText(
                "Customer: "+event.getCustomer()+
                "\nEvent: "+event.getEvent()+
                "\nType: "+event.getType()+
                "\nDate: "+event.getDate()+
                "\nGuests: "+event.getGuests()+
                "\nLocation: "+event.getLocation()+
                "\nBudget: "+event.getBudget()+
                "\nStatus: "+event.getStatus()
        );

        alert.showAndWait();
    }

    public static class Event{

        String customer,event,type,date,guests,location,budget,status;

        Event(String customer,String event,String type,String date,
              String guests,String location,String budget,String status){

            this.customer=customer;
            this.event=event;
            this.type=type;
            this.date=date;
            this.guests=guests;
            this.location=location;
            this.budget=budget;
            this.status=status;
        }

        String getCustomer(){return customer;}
        String getEvent(){return event;}
        String getType(){return type;}
        String getDate(){return date;}
        String getGuests(){return guests;}
        String getLocation(){return location;}
        String getBudget(){return budget;}
        String getStatus(){return status;}

            
    }
}


      
  

   
       