package com.techtrio.util;

import com.techtrio.services.OtpEmailService;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.Random;

public class OtpHelper {

    public static boolean sendAndVerifyOtp(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Customer email is missing.");
            return false;
        }

        // ६ अंकी OTP जनरेट करा
        String generatedOtp = String.valueOf(100000 + new Random().nextInt(900000));

        System.out.println("Sending OTP email to " + userEmail + " via dedicated OtpEmailService...");

        // ⭐ स्वतंत्र OtpEmailService द्वारे ईमेल पाठवणे
        OtpEmailService otpEmailService = new OtpEmailService();
        boolean emailSent = otpEmailService.sendOtpEmail(userEmail, generatedOtp);

        // डायलॉग बॉक्स
        TextInputDialog otpDialog = new TextInputDialog();
        otpDialog.setTitle("Security Verification");

        if (emailSent) {
            otpDialog.setHeaderText("A 6-digit OTP has been sent to your Gmail:\n" + userEmail);
        } else {
            otpDialog.setHeaderText("OTP sent to: " + userEmail + "\n(Delivery in progress / Check terminal: " + generatedOtp + ")");
        }

        otpDialog.setContentText("Enter 6-digit OTP:");

        Optional<String> result = otpDialog.showAndWait();
        if (result.isPresent()) {
            String enteredOtp = result.get().trim();
            if (enteredOtp.equals(generatedOtp)) {
                return true;
            } else {
                showAlert(Alert.AlertType.ERROR, "Verification Failed", "Incorrect OTP! Payment cancelled.");
                return false;
            }
        }
        return false;
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}