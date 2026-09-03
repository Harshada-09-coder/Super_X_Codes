package com.techtrio.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class OtpEmailService {

    // ⭐ Payment OTP साठी कीर्ती यांचे क्रेडेंशियल्स
    private static final String SENDER_EMAIL = "kirtinarkhede03@gmail.com"; 
    private static final String SENDER_APP_PASSWORD = "etsh opei mnax stwj"; 

    public boolean sendOtpEmail(String toEmail, String otpCode) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.ssl.checkserveridentity", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD.replace(" ", ""));
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "EventXpert Payment Security"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("EventXpert Payment OTP: " + otpCode);
            
            // आकर्षक आणि स्पष्ट HTML ईमेल टेम्पलेट
            message.setContent(
                "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;padding:25px;border:1px solid #e2e8f0;border-radius:12px;background:#ffffff;'>"
                + "<div style='text-align:center;border-bottom:2px solid #f1f5f9;padding-bottom:15px;margin-bottom:20px;'>"
                + "<h2 style='color:#0D3979;margin:0;'>EventXpert Payment Authorization</h2>"
                + "</div>"
                + "<p style='font-size:14px;color:#334155;'>Hello,</p>"
                + "<p style='font-size:14px;color:#334155;'>Your One-Time Password (OTP) to verify and authorize your Event Payment is:</p>"
                + "<div style='text-align:center;margin:30px 0;'>"
                + "<span style='font-size:34px;font-weight:bold;letter-spacing:8px;color:#16A34A;background:#DCFCE7;padding:12px 32px;border-radius:8px;display:inline-block;border:1px dashed #16A34A;'>" 
                + otpCode 
                + "</span>"
                + "</div>"
                + "<p style='font-size:13px;color:#64748b;text-align:center;'>This OTP is valid for 10 minutes. Do not share it with anyone.</p>"
                + "<hr style='border:none;border-top:1px solid #f1f5f9;margin:20px 0;'>"
                + "<p style='font-size:11px;color:#94a3b8;text-align:center;margin:0;'>EventXpert • Automated Security Gateway</p>"
                + "</div>", 
                "text/html; charset=utf-8"
            );

            Transport.send(message);
            System.out.println("✅ REAL OTP EMAIL DELIVERED to: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            System.err.println("❌ Authentication Error: Gmail App Password चुकीचा आहे.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Email Send Error: " + e.getMessage());
            return false;
        }
    }
}