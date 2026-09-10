package com.techtrio.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class EmailService {

    
    private static final String SENDER_EMAIL = "kurumkarharshada8@gmail.com";
    private static final String SENDER_PASSWORD = "envq yjsk eqhp woll";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static boolean sendGuestQrPass(String toEmail, String guestName, String eventName, String eventDate, String eventLocation, String qrPayload) {
        if (toEmail == null || toEmail.isBlank() || !toEmail.contains("@")) {
            System.err.println("⚠️ Invalid email: " + toEmail);
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "EventXpert Support"));
            message.setReplyTo(new Address[]{new InternetAddress(SENDER_EMAIL)});
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Event Entry Pass: " + eventName, "UTF-8");
            message.setSentDate(new Date());

            // Anti-Spam Headers (In-box Delivery)
            message.setHeader("X-Mailer", "EventXpert Notification System");
            message.setHeader("Precedence", "bulk");
            message.setHeader("X-Priority", "3");
            message.setHeader("Importance", "Normal");

            // 1. Generate QR Code in memory
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrPayload, BarcodeFormat.QR_CODE, 240, 240);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] qrBytes = baos.toByteArray();

            // 2. Multipart Body
            MimeMultipart multipart = new MimeMultipart("related");

            // HTML Body
            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = "<!DOCTYPE html><html><body style='font-family: Arial, sans-serif; background-color: #F8FAFC; padding: 20px;'>"
                    + "<div style='max-width: 500px; margin: auto; background-color: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 12px; padding: 24px; text-align: left;'>"
                    + "<div style='background-color: #062A60; padding: 14px; border-radius: 8px; text-align: center; margin-bottom: 20px;'>"
                    + "<h2 style='color: #FFFFFF; margin: 0; font-size: 20px;'>Event Entry Pass</h2>"
                    + "</div>"
                    + "<p style='color: #102A43; font-size: 15px; margin: 0 0 10px 0;'>Hello <b>" + guestName + "</b>,</p>"
                    + "<p style='color: #52677D; font-size: 13px; margin: 0 0 16px 0;'>Here is your official verified entry ticket for <b>" + eventName + "</b>.</p>"
                    + "<div style='background-color: #F8FAFC; border-left: 4px solid #0B4EA2; padding: 14px; border-radius: 6px; margin-bottom: 20px;'>"
                    + "<p style='margin: 4px 0; font-size: 13px; color: #102A43;'><b>Date:</b> " + eventDate + "</p>"
                    + "<p style='margin: 4px 0; font-size: 13px; color: #102A43;'><b>Location:</b> " + eventLocation + "</p>"
                    + "</div>"
                    + "<div style='text-align: center; margin: 20px 0;'>"
                    + "<p style='font-size: 12px; color: #0B4EA2; font-weight: bold; margin-bottom: 10px;'>Present this QR code at the entrance:</p>"
                    + "<img src='cid:qrImage' style='width: 200px; height: 200px; border: 1px solid #CBD5E1; border-radius: 8px; padding: 6px; background: white;'/>"
                    + "</div>"
                    + "<hr style='border: none; border-top: 1px solid #E2E8F0; margin: 20px 0;'/>"
                    + "<p style='font-size: 11px; color: #94A3B8; text-align: center; margin: 0;'>EventXpert • Automated Entry Verification</p>"
                    + "</div></body></html>";

            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // Embedded Image Part
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new ByteArrayDataSource(qrBytes, "image/png");
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setHeader("Content-ID", "<qrImage>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imagePart);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("✅ QR Entry Pass sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to send QR email to " + toEmail + ": " + e.getMessage());
            return false;
        }
    }

    /* 

     private static final String SENDER_EMAIL1 = "kirtinarkhede03@gmail.com"; 
    private static final String SENDER_APP_PASSWORD = "etsh opei mnax stwj"; 
     public boolean sendEmail(String toEmail, String subject, String body) {
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
            message.setFrom(new InternetAddress(SENDER_EMAIL, "EventXpert Support"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            
            // HTML Formatted सुंदर ईमेल
            message.setContent(
                "<div style='font-family:Arial,sans-serif;max-width:550px;margin:auto;padding:25px;border:1px solid #e2e8f0;border-radius:10px;'>"
                + "<h2 style='color:#0D3979;margin-top:0;'>EventXpert Payment Verification</h2>"
                + "<p style='font-size:15px;color:#334155;'>Dear Customer,</p>"
                + "<p style='font-size:15px;color:#334155;'>Your One-Time Password (OTP) to approve your Event Payment is:</p>"
                + "<div style='text-align:center;margin:30px 0;'>"
                + "<span style='font-size:32px;font-weight:bold;letter-spacing:6px;color:#16A34A;background:#DCFCE7;padding:12px 28px;border-radius:8px;display:inline-block;'>" 
                + body 
                + "</span>"
                + "</div>"
                + "<p style='font-size:13px;color:#64748b;'>This OTP is valid for 10 minutes. Please do not share it with anyone.</p>"
                + "<hr style='border:none;border-top:1px solid #f1f5f9;margin:20px 0;'>"
                + "<p style='font-size:12px;color:#94a3b8;margin:0;'>Regards,<br><b>Team EventXpert</b></p>"
                + "</div>", 
                "text/html; charset=utf-8"
            );

            Transport.send(message);
            System.out.println("✅ REAL EMAIL DELIVERED to: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            System.err.println("❌ Login Error: Gmail App Password चुकीचा आहे. कृपया नवीन App Password टाका.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Email Send Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        */

}