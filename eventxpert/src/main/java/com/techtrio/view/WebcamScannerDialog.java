package com.techtrio.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class WebcamScannerDialog {

    private Webcam webcam = null;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void openCameraScanner(Stage parentStage, Consumer<String> onQrDetected) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Live QR Scanner - EventXpert");

        ImageView cameraView = new ImageView();
        cameraView.setFitWidth(400);
        cameraView.setFitHeight(300);
        cameraView.setStyle("-fx-border-color: #0B4EA2; -fx-border-width: 2px;");

        Label statusLbl = new Label("Align QR code within camera view...");
        statusLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #52677D; -fx-font-weight: bold;");

        Button closeBtn = new Button("Close Camera");
        closeBtn.setStyle("-fx-background-color: #C5221F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 6;");
        closeBtn.setOnAction(e -> {
            stopCamera();
            dialogStage.close();
        });

        VBox root = new VBox(12, statusLbl, cameraView, closeBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #F8FAFC;");

        Scene scene = new Scene(root, 450, 400);
        dialogStage.setScene(scene);

        dialogStage.setOnCloseRequest(e -> stopCamera());

        // Background Thread for Webcam Reading
        new Thread(() -> {
            try {
                webcam = Webcam.getDefault();
                if (webcam == null) {
                    Platform.runLater(() -> statusLbl.setText("❌ No Webcam found on this device!"));
                    return;
                }

                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.open();
                isRunning.set(true);

                while (isRunning.get()) {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        // SwingFXUtils न वापरता Pure JavaFX Conversion
                        WritableImage fxImage = convertToFxImage(image);
                        Platform.runLater(() -> cameraView.setImage(fxImage));

                        // Decode QR Code with ZXing
                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        try {
                            Result result = new MultiFormatReader().decode(bitmap);
                            if (result != null) {
                                String text = result.getText();
                                isRunning.set(false); // Stop scanning on match

                                Platform.runLater(() -> {
                                    statusLbl.setText("✓ QR Detected!");
                                    stopCamera();
                                    dialogStage.close();
                                    onQrDetected.accept(text); // Send back to organizer screen
                                });
                                break;
                            }
                        } catch (NotFoundException ignored) {
                            // Keep scanning until QR appears
                        }
                    }
                    Thread.sleep(80); // ~12 fps
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();

        dialogStage.showAndWait();
    }

    private void stopCamera() {
        isRunning.set(false);
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    /**
     * Pure JavaFX BufferedImage -> WritableImage converter (Zero SwingFX dependence)
     */
    private WritableImage convertToFxImage(BufferedImage bimg) {
        if (bimg == null) return null;
        WritableImage wr = new WritableImage(bimg.getWidth(), bimg.getHeight());
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < bimg.getWidth(); x++) {
            for (int y = 0; y < bimg.getHeight(); y++) {
                pw.setArgb(x, y, bimg.getRGB(x, y));
            }
        }
        return wr;
    }
}