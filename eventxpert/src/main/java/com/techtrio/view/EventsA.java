package com.techtrio.view;

import com.techtrio.controller.NavigationManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EventsA{
    private Scene eventsScene;

      final String P="#002A60",S="#8CAFC6",L="#C2E0F4";

    Label label(String t,int size,boolean bold){
        Label l=new Label(t);
        l.setFont(Font.font("Arial",bold?FontWeight.BOLD:FontWeight.NORMAL,size));
        return l;
    }

    Button button(String t,boolean primary){
        Button b=new Button(t);
        b.setFont(Font.font("Arial",FontWeight.BOLD,13));
        b.setPrefHeight(38);
        b.setStyle(primary
            ?"-fx-background-color:"+P+";-fx-text-fill:white;-fx-background-radius:7;"
            :"-fx-background-color:white;-fx-text-fill:"+P+";-fx-border-color:#B8C7D8;-fx-border-radius:7;");
        return b;
    }

    VBox card(String title,String value,String icon){
        Label t=label(title,14,false),v=label(value,42,true),i=label(icon,18,true);
        i.setTextFill(Color.web(P));
        StackPane sp=new StackPane(i);
        sp.setPrefSize(38,38);
        sp.setStyle("-fx-background-color:#EAF0F5;-fx-background-radius:20;");
        HBox top=new HBox(t,new Region(),sp);
        HBox.setHgrow(top.getChildren().get(1),Priority.ALWAYS);
        VBox box=new VBox(15,top,v);
        box.setPadding(new Insets(18,20,15,20));
        box.setPrefWidth(245);
        box.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;"
                +"-fx-background-radius:8;-fx-border-radius:8;");
        return box;
    }

    TableView<String[]> table(){
        String[] heads={"Customer","Event Name","Event Type","Event Date",
                "Guests","Location","Organizer","Budget","Status","Action"};

        TableView<String[]> tv=new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tv.setPrefHeight(390);

        double[] widths={115,135,110,115,70,90,135,90,100,75};

        for(int i=0;i<heads.length;i++){
            final int x=i;
            TableColumn<String[],String> c=new TableColumn<>(heads[i]);
            c.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue()[x]));
            c.setPrefWidth(widths[i]);


            if(i==9){
                c.setCellFactory(col->new TableCell<>(){
                    final Button b=button("View",false);

                    { b.setPrefHeight(30); }
                    protected void updateItem(String item,boolean empty){
                        super.updateItem(item,empty);
                        if(empty){setGraphic(null);return;}
                        b.setOnAction(e -> {
                            Stage stage = (Stage) b.getScene().getWindow();

                            NavigationManager.navigate("Event Details", stage);
});
                        setGraphic(b);
                    }
                });
            }
            tv.getColumns().add(c);
        }

        ObservableList<String[]> data=FXCollections.observableArrayList(
            new String[]{"SKNSCOE","College Seminar","Educational","18 Aug 2026","200","Pune","Elite Event Planners","₹40,000","Upcoming","View"},
            new String[]{"Rahul K.","Birthday Celebration","Birthday","27 Aug 2026","100","Nashik","Dream Events","₹30,000","Upcoming","View"},
            new String[]{"ABC Corporation","Corporate Workshop","Corporate","17 Aug 2026","200","Mumbai","Perfect Moments","₹60,000","Planning","View"},
            new String[]{"Priya M.","Family Reunion","Family","07 Sep 2026","75","Pune","Elite Event Planners","₹25,000","Upcoming","View"}
        );
        tv.setItems(data);
        return tv;
    }



    void showView(String[] r){
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Event Details");
        a.setHeaderText(r[1]);
        a.setContentText(
            "Customer: "+r[0]+
            "\nEvent Type: "+r[2]+
            "\nDate: "+r[3]+
            "\nGuests: "+r[4]+
            "\nLocation: "+r[5]+
            "\nOrganizer: "+r[6]+
            "\nBudget: "+r[7]+
            "\nStatus: "+r[8]);
        a.showAndWait();
    }

    public Scene geteventsScene(){
     VBox sidebar=new VBox(7);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25,18,18,18));
        sidebar.setStyle("-fx-background-color:"+P+";");

        Label logo=label("EventXpert",26,true);
        Label admin=label(" ADMIN DASHBOARD",12,true);
        logo.setTextFill(Color.WHITE);admin.setTextFill(Color.WHITE);

        VBox menu=new VBox(7);
        String[] items={"Dashboard","Event Requests","Events","Calendar",
                "Customers","Organizers","Vendors","Conflict Detection",
                "Reports & Analytics"};

        for(String s:items){
            Label m=label(s,14,true);
            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9,13,9,13));
            
            if(s.equals("Events"))
                m.setStyle("-fx-background-color:#07539A;-fx-background-radius:5;");

             m.setOnMouseClicked(e -> {

        Stage stage =
            (Stage) m.getScene().getWindow();

        NavigationManager.navigate(s, stage);
    });

            menu.getChildren().add(m);
        }

        Region space=new Region();
        VBox.setVgrow(space,Priority.ALWAYS);

        Label n=label("🔔  Notifications",13,true);
        Label se=label("⚙  Settings",13,true);
        Label pr=label("👤  Profile",13,true);
        Label sp=label("?  Support",13,true);

        for(Label x:new Label[]{n,se,pr,sp}){
            x.setTextFill(Color.WHITE);
            x.setPadding(new Insets(7,10,7,10));
        }

        sidebar.getChildren().addAll(
            new VBox(1,logo,admin),menu,space,n,se,pr,sp
        );

        /* TOP BAR */
        Region hs=new Region();
        HBox.setHgrow(hs,Priority.ALWAYS);

        HBox header=new HBox(18,hs,
                label("🔔",18,false),
                label("?",18,true),
                label("⚙",18,false),
                label("👤",18,false));

        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(10,25,10,25));
        header.setPrefHeight(60);
        header.setStyle("-fx-background-color:white;-fx-border-color:#E5EAF0;");

        /* TITLE */
        Label title=label("Events",30,true);
        title.setTextFill(Color.web(P));

        Label sub=label("Manage and monitor all scheduled events.",15,false);
        VBox heading=new VBox(3,title,sub);

        Button create=button("+  Create Event",true);
        create.setPrefWidth(145);

        Region hspace=new Region();
        HBox.setHgrow(hspace,Priority.ALWAYS);

        HBox titleRow=new HBox(heading,hspace,create);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        /* SUMMARY */
        HBox cards=new HBox(20,
                card("Total Events","48","▣"),
                card("Upcoming Events","18","◷"),
                card("Ongoing Events","5","●"),
                card("Completed Events","25","✓"));
                
        cards.setCursor(javafx.scene.Cursor.HAND);
    
               
    
        /* FILTERS */
        TextField search=new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(220);

        ComboBox<String> status=new ComboBox<>();
        status.getItems().addAll("Status","Upcoming","Ongoing","Completed","Cancelled");
        status.setValue("Status");

        ComboBox<String> type=new ComboBox<>();
        type.getItems().addAll("Event Types","Corporate","Birthday",
                "Educational","Family");
        type.setValue("Event Types");

        ComboBox<String> date=new ComboBox<>();
        date.getItems().addAll(" Dates","Today","This Week","This Month");
        date.setValue("Dates");

        ComboBox<String> location=new ComboBox<>();
        location.getItems().addAll("Locations","Pune","Mumbai","Nashik");
        location.setValue("Locations");

        for(ComboBox<String> c:new ComboBox[]{status,type,date,location})
            c.setPrefHeight(38);

        Button clear=button("Clear Filters",false);

        clear.setOnAction(e->{
            search.clear();
            status.setValue("Status");
            type.setValue("Event Types");
            date.setValue("Dates");
            location.setValue("Locations");
        });

        HBox filters=new HBox(12,search,status,type,date,location,clear);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(15));
        filters.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;"
                +"-fx-background-radius:8;-fx-border-radius:8;");

        
        Label tableTitle=label("Events",18,true);
        TableView<String[]> table=table();

        Button previous=button("Previous",false);
        Button next=button("Next",true);

        previous.setOnAction(e->new Alert(Alert.AlertType.INFORMATION,
                "Already on first page.").showAndWait());

        next.setOnAction(e->new Alert(Alert.AlertType.INFORMATION,
                "Next page of events.").showAndWait());

        Region ps=new Region();
        HBox.setHgrow(ps,Priority.ALWAYS);

        HBox pagination=new HBox(8,ps,previous,next);
        pagination.setAlignment(Pos.CENTER_LEFT);
        pagination.setPadding(new Insets(10,15,5,15));

        VBox tableBox=new VBox(8,tableTitle,table,pagination);
        tableBox.setPadding(new Insets(15));
        tableBox.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;"
                +"-fx-background-radius:8;-fx-border-radius:8;");

        
        VBox content=new VBox(14,titleRow,cards,filters,tableBox);
        content.setPadding(new Insets(15,25,20,25));
        content.setStyle("-fx-background-color:#F7F9FC;");

        VBox center=new VBox(header,content);

        BorderPane root=new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene=new Scene(root,1550,750);
        eventsScene = scene;
        return eventsScene;
       
    }

    }
    




    
   


   