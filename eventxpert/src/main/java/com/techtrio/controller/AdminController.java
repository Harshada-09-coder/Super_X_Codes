package com.techtrio.controller;

import com.techtrio.model.AdminProfile;
import com.techtrio.services.AdminProfileService;

public class AdminController {

    private final AdminProfileService service =
            new AdminProfileService();

    // CREATE
    public boolean createAdminProfile(AdminProfile admin) {
        return service.createAdminProfile(admin);
    }

    // READ
    public AdminProfile getAdminProfile(String adminId) {
        return service.getAdminProfile(adminId);
    }

    // UPDATE
    public boolean updateAdminProfile(
            String adminId,
            String name,
            String email,
            String mobileNo,
            String address) {

        return service.updateAdminProfile(
                adminId,
                name,
                email,
                mobileNo,
                address
        );
    }

    // DELETE
    public boolean deleteAdminProfile(String adminId) {
        return service.deleteAdminProfile(adminId);
    }

    public boolean updateTwoFactorStatus(
        String adminId,
        boolean enabled) {

    return service.updateTwoFactorStatus(
            adminId,
            enabled
    );
}
}