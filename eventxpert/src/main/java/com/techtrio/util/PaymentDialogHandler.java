package com.techtrio.util;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

public class PaymentDialogHandler {

    // १. पहिला डायलॉग: Razorpay पेमेंट स्क्रीन
    public static void startPaymentProcess(JFrame parentFrame, String checkoutUrl, OnPaymentCompletedListener listener) {
        JDialog paymentDialog = new JDialog(parentFrame, "Complete Payment - EventXpert", true);
        paymentDialog.setSize(500, 650);
        paymentDialog.setLocationRelativeTo(parentFrame);
        paymentDialog.setLayout(new BorderLayout());

        JFXPanel jfxPanel = new JFXPanel();
        paymentDialog.add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            jfxPanel.setScene(new Scene(webView));
            webView.getEngine().load(checkoutUrl);

            // URL वर लक्ष ठेवा (payment-success डिटेक्ट करण्यासाठी)
            webView.getEngine().locationProperty().addListener((obs, oldUrl, newUrl) -> {
                if (newUrl != null && newUrl.contains("payment-success")) {
                    // Payment ID URL मधून काढणे (उदा. razorpay_payment_id=pay_xxxx)
                    String paymentId = "Unknown";
                    if (newUrl.contains("razorpay_payment_id=")) {
                        paymentId = newUrl.split("razorpay_payment_id=")[1].split("&")[0];
                    }

                    final String finalPaymentId = paymentId;

                    // Swing Thread वर डायलॉग बंद करून Success डायलॉग दाखवणे
                    SwingUtilities.invokeLater(() -> {
                        paymentDialog.dispose(); // पहिला डायलॉग बंद
                        showSuccessDialog(parentFrame, finalPaymentId, listener); // दुसरा डायलॉग ओपन
                    });
                }
            });
        });

        paymentDialog.setVisible(true);
    }

    // २. दुसरा डायलॉग: Payment Successful पॉपअप ("OK" बटणासह)
    private static void showSuccessDialog(JFrame parentFrame, String paymentId, OnPaymentCompletedListener listener) {
        JDialog successDialog = new JDialog(parentFrame, "Transaction Status", true);
        successDialog.setSize(420, 320);
        successDialog.setLocationRelativeTo(parentFrame);
        successDialog.setLayout(new BorderLayout());
        successDialog.getContentPane().setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

        // हिरवा बॅज
        JLabel lblBadge = new JLabel("✔ Payment Successful");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBadge.setForeground(new Color(34, 139, 34));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // हेडिंग
        JLabel lblTitle = new JLabel("Transaction Completed");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 30, 30));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // मेसेज
        JLabel lblMsg = new JLabel("<html><center>Your payment has been successfully processed.<br><b>Payment ID:</b> " + paymentId + "<br><br>Click below to open Payment Screen.</center></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // हिरवे "OK" बटण
        JButton btnOk = new JButton("✔  OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOk.setBackground(new Color(40, 167, 69));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setPreferredSize(new Dimension(140, 40));
        btnOk.setMaximumSize(new Dimension(140, 40));
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // OK बटण क्लिक केल्यावर:
        btnOk.addActionListener(e -> {
            successDialog.dispose(); // दुसरा डायलॉग बंद
            listener.onSuccess(paymentId); // payment.java ला थेट ट्रिगर करा
        });

        contentPanel.add(lblBadge);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(lblMsg);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(btnOk);

        successDialog.add(contentPanel, BorderLayout.CENTER);
        successDialog.setVisible(true);
    }

    public interface OnPaymentCompletedListener {
        void onSuccess(String paymentId);
    }
}