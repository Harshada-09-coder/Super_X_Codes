package com.techtrio.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Base64;

public class QRCodeUtil {

    // 1. Secure Random 24-byte Token
    public static String generateSecureQrToken() {
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    // 2. Format QR Payload
    public static String formatPayload(String eventId, String qrToken) {
        return "EVENTXPERT:" + eventId + ":" + qrToken;
    }

    // 3. थेट JavaFX Image मध्ये QR कोड तयार करणे (Screen Preview & Download साठी)
    public static WritableImage generateQrImage(String data, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            WritableImage wr = new WritableImage(width, height);
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pw.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
            return wr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}