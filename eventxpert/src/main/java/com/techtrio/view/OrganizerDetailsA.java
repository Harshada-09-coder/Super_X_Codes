package com.techtrio.view;

import java.util.List;

import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.services.EventService;
import com.techtrio.services.OrganizerProfileService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class OrganizerDetailsA {

    private Scene organizerDetailsScene;

    // Static holder for selected organizer
    public static OrganizerProfile selectedOrganizer;

    // Services
    private final EventService eventService = new EventService();
    private final OrganizerProfileService organizerService = new OrganizerProfileService();

    final String PRIMARY="#002A60", SECONDARY="#8CAFC6",
                 LIGHT="#C2E0F4", BG="#F6F9FC";

    Label label(String t,int s,boolean b){
        Label l=new Label(t != null ? t : "");
        l.setFont(Font.font("Arial",s));
        l.setTextFill(Color.web(PRIMARY));
        if(b) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    VBox card(){
        VBox v=new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;"+
                   "-fx-border-radius:8;-fx-background-radius:8;");
        return v;
    }

    Button button(String t){
        Button b=new Button(t);
        b.setStyle("-fx-background-color:"+PRIMARY+";-fx-text-fill:white;"+
                   "-fx-font-weight:bold;-fx-background-radius:6;");
        b.setPadding(new Insets(9,15,9,15));
        return b;
    }

    /* ================= SIDEBAR ================= */

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

        String[] items={
            "Dashboard","Event Requests","Events","Calendar","Customers",
            "Organizers","Vendors","Conflict Detection","Reports & Analytics"
        };

        VBox menu=new VBox(5);

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

        side.getChildren().addAll(
            head,menu,gap,notifications,settings,profile,support
        );

        return side;
    }

    /* ================= ORGANIZER CARD ================= */

    VBox organizerCard(OrganizerProfile org){
        VBox v=card();
        v.setPrefWidth(320);
        v.setAlignment(Pos.CENTER);

        String orgName = "Elite Event Planners";
        String orgId = "ID: ORG-001";
        String statusText = "● Active";
        String regDate = "Aug 12, 2026";
        String initials = "EE";

        if(org != null){
            orgName = (org.getOrganizationName() != null && !org.getOrganizationName().isBlank())
                    ? org.getOrganizationName()
                    : (org.getName() != null ? org.getName() : "Organizer");

            orgId = "ID: " + (org.getOrganizerId() != null ? org.getOrganizerId() : "ORG-001");
            statusText = (org.getOnboardingStatus() != null) ? "● " + org.getOnboardingStatus() : "● Active";
            
            if(org.getCreatedAt() != null && org.getCreatedAt().length() >= 10){
                regDate = org.getCreatedAt().substring(0,10);
            }

            String[] parts = orgName.trim().split("\\s+");
            if(parts.length >= 2){
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else if(orgName.length() >= 2){
                initials = orgName.substring(0,2).toUpperCase();
            } else if(!orgName.isEmpty()){
                initials = orgName.toUpperCase();
            }
        }

        Label avatar=label(initials,30,true);
        avatar.setTextFill(Color.WHITE);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(95,95);
        avatar.setStyle("-fx-background-color:"+PRIMARY+
                        ";-fx-background-radius:60;"+
                        "-fx-font-weight:bold;");

        Label name=label(orgName,20,true);
        Label id=label(orgId,14,false);

        Separator line=new Separator();

        Label status=new Label(statusText);
        status.setTextFill(Color.web(PRIMARY));
        status.setStyle("-fx-background-color:"+LIGHT+
                        ";-fx-background-radius:20;-fx-font-weight:bold;");
        status.setPadding(new Insets(7,16,7,16));

        Label registered=label("Registered: " + regDate,14,false);

        v.getChildren().addAll(
            avatar,name,id,line,status,registered
        );

        return v;
    }

    /* ================= CONTACT ================= */

    VBox contactCard(OrganizerProfile org){
        VBox v=card();
        v.setPrefWidth(320);

        String email = (org != null && org.getEmail() != null) ? org.getEmail() : "info@eventxpert.com";
        String mobile = (org != null && org.getMobileNo() != null) ? org.getMobileNo() : "+91 98765 43210";
        String address = (org != null && org.getAddress() != null) ? org.getAddress() : "Pune, Maharashtra";

        v.getChildren().addAll(
            label("Contact Information",20,true),
            new Separator(),
            label("✉  Email\n    " + email,14,false),
            label("☎  Mobile\n    " + mobile,14,false),
            label("⌖  Address\n    " + address,14,false)
        );

        return v;
    }

    /* ================= STAT ================= */

    VBox stat(String title,String value){
        VBox v=card();
        v.setPrefHeight(92);
        v.getChildren().addAll(
            label(title.toUpperCase(),13,true),
            label(value,28,true)
        );
        return v;
    }

    /* ================= INFO ROW ================= */

    HBox info(String left,String right){
        Label a=label(left,14,false);
        Label b=label(right,14,true);

        Region r=new Region();
        HBox.setHgrow(r,Priority.ALWAYS);

        HBox h=new HBox(a,r,b);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    /* ================= EXPERIENCE ================= */

    VBox experience(OrganizerProfile org){
        VBox v=card();

        String businessType = (org != null && org.getBusinessType() != null) ? org.getBusinessType() : "Corporate";

        v.getChildren().addAll(
            label("Experience & Performance",20,true),
            new Separator(),
            info("Experience","6 Years"),
            info("Rating","4.8 ☆"),
            label("Specialization",14,false)
        );

        HBox tags=new HBox(6);
        String[] tagItems = businessType.split("[,/]");
        if(tagItems.length == 0 || (tagItems.length == 1 && tagItems[0].isBlank())){
            tagItems = new String[]{"Corporate","Educational","Birthday"};
        }

        for(String s: tagItems){
            Label l=new Label(s.trim());
            l.setPadding(new Insets(5,9,5,9));
            l.setStyle("-fx-background-color:#E2E6EA;"+
                       "-fx-background-radius:15;");
            tags.getChildren().add(l);
        }

        v.getChildren().add(tags);
        return v;
    }

    /* ================= AVAILABILITY ================= */

    VBox availability(OrganizerProfile org, int upcomingCount){
        VBox v=card();

        String currentStatus = (org != null && org.getOnboardingStatus() != null) ? "● " + org.getOnboardingStatus() : "● Available";

        v.getChildren().addAll(
            label("Availability & Workload",20,true),
            new Separator(),
            info("Current Status", currentStatus),
            info("Workload", upcomingCount + " Active Events"),
            info("Upcoming Assignments", String.valueOf(upcomingCount)),
            new Separator(),
            info("Next Available","Aug 30, 2026")
        );

        return v;
    }

    /* ================= STATUS ================= */

    Label status(String text,boolean active){
        Label l=new Label(text);
        l.setPadding(new Insets(6,12,6,12));
        l.setTextFill(Color.web(PRIMARY));
        l.setStyle("-fx-background-color:"+
                   (active?LIGHT:"#E2E6EA")+
                   ";-fx-background-radius:20;");
        return l;
    }

    /* ================= EVENT HISTORY ================= */

    VBox eventHistory(List<Event> orgEvents){
        VBox v=card();

        v.getChildren().add(label("Event History",20,true));

        GridPane g=new GridPane();
        g.setHgap(20);
        g.setVgap(18);

        String[] h={
            "EVENT NAME","TYPE","DATE",
            "CUSTOMER","LOCATION","STATUS"
        };

        for(int i=0;i<h.length;i++)
            g.add(label(h[i],12,true),i,0);

        if(orgEvents != null && !orgEvents.isEmpty()){
            int row = 1;
            for(Event ev : orgEvents){
                String name = ev.getEventName() != null ? ev.getEventName() : (ev.getEventType() != null ? ev.getEventType() : "Event");
                String type = ev.getEventType() != null ? ev.getEventType() : "General";
                String date = ev.getEventDate() != null ? ev.getEventDate() : "TBD";
                String cust = ev.getCustomerId() != null ? ev.getCustomerId() : "Customer";
                String loc = ev.getCity() != null ? ev.getCity() : "Pune";
                String st = ev.getStatus() != null ? ev.getStatus() : "Upcoming";

                boolean isUpcoming = !"COMPLETED".equalsIgnoreCase(st) && !"REJECTED".equalsIgnoreCase(st);

                g.add(label(name,14,false),0,row);
                g.add(label(type,14,false),1,row);
                g.add(label(date,14,false),2,row);
                g.add(label(cust,14,false),3,row);
                g.add(label(loc,14,false),4,row);
                g.add(status(st, isUpcoming),5,row);
                row++;
                if(row > 4) break;
            }
        } else {
            g.add(label("Corporate\nSeminar",14,false),0,1);
            g.add(label("Corporate",14,false),1,1);
            g.add(label("Aug 18,\n2026",14,false),2,1);
            g.add(label("INCUBATORS",14,false),3,1);
            g.add(label("Pune",14,false),4,1);
            g.add(status("Upcoming",true),5,1);
        }

        v.getChildren().add(g);
        return v;
    }

    /* ================= ACTIVITY ================= */

    VBox activity(OrganizerProfile org){
        VBox v=card();

        String name = (org != null && org.getOrganizationName() != null) ? org.getOrganizationName() : "Organizer";

        v.getChildren().addAll(
            label("Recent Activity",20,true),
            new Separator(),
            label("•  Assigned to active events\n    Recently",14,false),
            label("•  Profile verified for " + name + "\n    Active",14,false)
        );

        return v;
    }

    /* ================= MAIN ================= */

    public Scene getorganizerDetailsScene(){

        OrganizerProfile org = selectedOrganizer;

        // जर आधीपासून ऑर्गनायझर सिलेक्ट नसेल, तर डेटाबेसमधून पहिला ऑर्गनायझर फेच करा
        if(org == null){
            List<OrganizerProfile> all = organizerService.getAllOrganizers();
            if(all != null && !all.isEmpty()){
                org = all.get(0);
                selectedOrganizer = org;
            }
        }

        List<Event> orgEvents = null;
        if(org != null && org.getOrganizerId() != null){
            orgEvents = eventService.getEventsByOrganizerId(org.getOrganizerId());
        }

        int totalCount = (orgEvents != null) ? orgEvents.size() : 2;
        int completedCount = 0;
        int upcomingCount = 0;
        int cancelledCount = 0;

        if(orgEvents != null && !orgEvents.isEmpty()){
            for(Event ev : orgEvents){
                String s = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "";
                if("COMPLETED".equals(s) || "CONFIRMED".equals(s)){
                    completedCount++;
                } else if("REJECTED".equals(s) || "CANCELLED".equals(s)){
                    cancelledCount++;
                } else {
                    upcomingCount++;
                }
            }
        } else {
            completedCount = 1;
            upcomingCount = 1;
        }

        BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");
        root.setLeft(sidebar());

        VBox content=new VBox(16);
        content.setPadding(new Insets(20,25,25,25));

        Button back=button("←  Back to Organizers");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Organizers", stage);
        });

        Label title=label("Organizer Details",35,true);
        Label sub=label(
            "View organizer information, experience, events and availability.",
            18,false
        );

        VBox heading=new VBox(8,back,title,sub);

        VBox left=new VBox(
            16,organizerCard(org),contactCard(org)
        );
        left.setPrefWidth(320);

        HBox stats=new HBox(
            14,
            stat("Total Events", String.valueOf(totalCount)),
            stat("Completed", String.valueOf(completedCount)),
            stat("Upcoming", String.valueOf(upcomingCount)),
            stat("Cancelled", String.valueOf(cancelledCount))
        );
        stats.setCursor(javafx.scene.Cursor.HAND);

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        HBox details=new HBox(
            16,experience(org),availability(org, upcomingCount)
        );

        VBox right=new VBox(
            16,stats,details,eventHistory(orgEvents),activity(org)
        );

        HBox.setHgrow(right,Priority.ALWAYS);

        HBox main=new HBox(20,left,right);
        HBox.setHgrow(right,Priority.ALWAYS);

        content.getChildren().addAll(heading,main);

        ScrollPane scroll=new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );
        scroll.setStyle(
            "-fx-background-color:"+BG+
            ";-fx-border-color:transparent;"
        );

        root.setCenter(scroll);

        Scene scene=new Scene(root,1550,750);
        organizerDetailsScene = scene;
        return organizerDetailsScene;
    }
}