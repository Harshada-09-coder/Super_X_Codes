package com.techtrio.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.techtrio.view.AdminDashboardA;
import com.techtrio.view.EventRequestA;
import com.techtrio.view.EventsA;
import com.techtrio.view.NotificationsA;
import com.techtrio.view.OrganizerManagementA;
import com.techtrio.view.ProfileA;
import com.techtrio.view.ReportA;
import com.techtrio.view.SettingsA;
import com.techtrio.view.SupportA;
import com.techtrio.view.VendorManagementA;
import com.techtrio.view.CalenderA;
import com.techtrio.view.ConflictDetectionA;
import com.techtrio.view.CustomerA;

public class NavigationManager {

    public static void navigate(String page, Stage stage) {

        switch(page) {

            case "Dashboard":
                stage.setScene(
                    new AdminDashboardA().getadminDashboardScene());
                break;

            case "Event Requests":
                stage.setScene(
                    new EventRequestA().geteventRequestScene());
                break;

            case "Events":
                stage.setScene(
                    new EventsA().geteventsScene());
                break;

            case "Calendar":
                stage.setScene(
                    new CalenderA().getcalenderScene());
                break;

            case "Customers":
                stage.setScene(
                    new CustomerA().getcustomerScene());
                break;

            case "Organizers":
                stage.setScene(
                    new OrganizerManagementA().getorganizerScene());
                break;

            case "Vendors":
                stage.setScene(
                    new VendorManagementA().getvendorScene());
                break;    

            case "Conflict Detection":
                stage.setScene(
                    new ConflictDetectionA().getconflictDetectionScene());
                break;

            case "Report & Analytics":
                stage.setScene(
                    new ReportA().getreportScene());
                break;

            case "Notifications":
                stage.setScene(
                    new NotificationsA().getnotificationsScene());
                break;

            case "Setting":
                stage.setScene(
                    new SettingsA().getsettingScene());
                break;

            case "Profile":
                stage.setScene(
                    new ProfileA().getprofileScene());
                break;

            case "Support":
                stage.setScene(
                    new SupportA().getsupportScene());
                break;
            
            
        }
    }
}
