package com.techtrio.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class OtpEmailService {

    // ⚠️ तुमचे चालू असलेले Sender Email आणि 16-अंकी Gmail App Password इथे ठेवा
    private static final String SENDER_EMAIL = "kirtinarkhede03@gmail.com"; // किंवा तुमचा ईमेल
    private static final String APP_PASSWORD = "etsh opei mnax stwj";   // तुमचा App Password

    /**
     * Port 587 (STARTTLS) द्वारे थेट Inbox मध्ये जाणारा OTP ईमेल
     */
    public boolean sendOtpEmail(String recipientEmail, String otp) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty() || otp == null) {
            System.err.println("Recipient email or OTP is empty.");
            return false;
        }

        System.out.println("Sending OTP to " + recipientEmail + " via Secure TLS Port 587...");

        // Gmail SMTP Properties (Port 587 is universally supported and bypasses network blocking)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.connectiontimeout", "8000"); // 8 seconds timeout
        props.put("mail.smtp.timeout", "8000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD.replaceAll("\\s+", ""));
            }
        });

        try {
            Message message = new MimeMessage(session);

            // १. ऑफिशियल Sender नाव (यामुळे स्पॅम फिल्टर रोखत नाही)
            message.setFrom(new InternetAddress(SENDER_EMAIL, "EventXpert Support", "UTF-8"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail.trim()));

            // २. स्पष्ट आणि अधिकृत विषय (Subject)
            message.setSubject("EventXpert - Verification Code: " + otp);

            // ३. स्पॅम फिल्टर्ससाठी आवश्यक Headers
            message.setHeader("X-Priority", "1");
            message.setHeader("X-MSMail-Priority", "High");
            message.setHeader("Importance", "High");

            // ४. अधिकृत ट्रॅन्झॅक्शनल HTML टेम्प्लेट (Gmail याला Inbox मध्येच दाखवते)
            String htmlContent = "<!DOCTYPE html><html><head><meta charset='UTF-8'></head>"
                    + "<body style='margin:0; padding:30px; background-color:#F8FAFC; font-family:Segoe UI, Arial, sans-serif;'>"
                    + "<div style='max-width:520px; margin:auto; background:#ffffff; border-radius:12px; padding:35px 30px; box-shadow:0 4px 15px rgba(0,0,0,0.06); border:1px solid #E2E8F0;'>"
                    + "<div style='text-align:center; margin-bottom:25px;'>"
                    + "<h1 style='color:#2563EB; margin:0; font-size:26px; font-weight:bold;'>EventXpert</h1>"
                    + "<p style='color:#64748B; font-size:13px; margin:5px 0 0 0;'>Event Planning & Management Platform</p>"
                    + "</div>"
                    + "<h2 style='color:#0F172A; font-size:18px; margin-bottom:12px;'>Payment Verification Code</h2>"
                    + "<p style='color:#475569; font-size:14px; line-height:1.6; margin:0 0 20px 0;'>"
                    + "We received a request to verify your payment for the event booking. Use the One-Time Password (OTP) below to proceed:"
                    + "</p>"
                    + "<div style='background:#F1F5F9; border:2px dashed #2563EB; border-radius:10px; padding:18px; text-align:center; margin:25px 0;'>"
                    + "<span style='font-size:32px; font-weight:bold; letter-spacing:8px; color:#1E293B; font-family:Consolas, monospace;'>"
                    + otp
                    + "</span>"
                    + "</div>"
                    + "<p style='color:#64748B; font-size:12.5px; line-height:1.5; margin:0 0 10px 0;'>"
                    + "⏱️ This OTP is valid for <b>10 minutes</b>. For your security, never share this code with anyone."
                    + "</p>"
                    + "<p style='color:#94A3B8; font-size:12px; margin:20px 0 0 0; line-height:1.4;'>"
                    + "If you did not initiate this payment, please contact EventXpert support immediately."
                    + "</p>"
                    + "<hr style='border:none; border-top:1px solid #E2E8F0; margin:25px 0;'>"
                    + "<div style='text-align:center; color:#94A3B8; font-size:11px;'>"
                    + "© EventXpert Platform. Automated security notification, please do not reply."
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("✅ OTP Email successfully sent to " + recipientEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email Send Error: " + e.getMessage());
            // Fallback: जर पोर्ट 587 फेल झाला तर पोर्ट 465 वर शेवटचा प्रयत्न
            return sendFallbackSSL(recipientEmail, otp);
        }
    }

    /**
     * Fallback SSL (Port 465) जर पोर्ट 587 उपलब्ध नसेल तर
     */
    private boolean sendFallbackSSL(String recipientEmail, String otp) {
        try {
            Properties sslProps = new Properties();
            sslProps.put("mail.smtp.auth", "true");
            sslProps.put("mail.smtp.ssl.enable", "true");
            sslProps.put("mail.smtp.host", "smtp.gmail.com");
            sslProps.put("mail.smtp.port", "465");
            sslProps.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            sslProps.put("mail.smtp.connectiontimeout", "6000");

            Session session = Session.getInstance(sslProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD.replaceAll("\\s+", ""));
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "EventXpert Support", "UTF-8"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail.trim()));
            message.setSubject("EventXpert - OTP: " + otp);
            message.setContent("<h3>Your Payment OTP is: <b>" + otp + "</b></h3><p>Valid for 10 minutes.</p>", "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("✅ Fallback: Email sent via SSL Port 465.");
            return true;
        } catch (Exception ex) {
            System.err.println("❌ Fallback SSL also failed: " + ex.getMessage());
            return false;
        }
    }

    // Static Overload Method (Direct Call साठी)
    public static boolean sendOtp(String recipientEmail, String otp) {
        return new OtpEmailService().sendOtpEmail(recipientEmail, otp);
    }
}