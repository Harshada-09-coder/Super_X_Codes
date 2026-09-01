package com.techtrio.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
            System.err.println("⚠️ Invalid email address: " + toEmail);
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
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "EventXpert Team"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("🎟 Your Entry Pass for " + eventName);

            // 1. QR Code इमेज मेमरीमध्ये तयार करणे (ZXing)
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrPayload, BarcodeFormat.QR_CODE, 250, 250);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] qrBytes = baos.toByteArray();

            // 2. Multipart Email Body (HTML + Embedded QR Code Image)
            MimeMultipart multipart = new MimeMultipart("related");

            // HTML Pass Template
            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 520px; margin: auto; padding: 24px; border: 1px solid #E2E8F0; border-radius: 12px; background-color: #FFFFFF;'>"
                    + "<div style='background-color: #002A60; padding: 14px; border-radius: 8px; text-align: center;'>"
                    + "<h2 style='color: #FFFFFF; margin: 0;'>EventXpert Entry Pass</h2>"
                    + "</div>"
                    + "<p style='color: #334155; font-size: 14px; margin-top: 18px;'>Hello <b>" + guestName + "</b>,</p>"
                    + "<p style='color: #64748B; font-size: 13px;'>Here is your official verified entry pass for the upcoming event:</p>"
                    + "<div style='background: #F8FAFC; padding: 14px; border-left: 4px solid #002A60; border-radius: 6px; margin: 15px 0;'>"
                    + "<p style='margin: 4px 0; font-size: 13px;'><b>Event:</b> " + eventName + "</p>"
                    + "<p style='margin: 4px 0; font-size: 13px;'><b>Date:</b> " + eventDate + "</p>"
                    + "<p style='margin: 4px 0; font-size: 13px;'><b>Location:</b> " + eventLocation + "</p>"
                    + "</div>"
                    + "<div style='text-align: center; margin: 24px 0;'>"
                    + "<p style='font-size: 12px; color: #475569; font-weight: bold;'>Show this QR code at the entrance for fast check-in:</p>"
                    + "<img src='cid:qrImage' style='width: 220px; height: 220px; border: 1px solid #CBD5E1; border-radius: 8px; padding: 8px; background: white;'/>"
                    + "</div>"
                    + "<p style='font-size: 11px; color: #94A3B8; text-align: center;'>Powered by EventXpert • Seamless Event Management</p>"
                    + "</div>";

            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // QR Code Attachment
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new ByteArrayDataSource(qrBytes, "image/png");
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setHeader("Content-ID", "<qrImage>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imagePart);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("✅ QR Entry Pass successfully sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to send QR email to " + toEmail + ": " + e.getMessage());
            return false;
        }
    }
}