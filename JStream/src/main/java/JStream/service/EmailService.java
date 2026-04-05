package JStream.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Random;

public class EmailService {

    // ── Gmail credentials ──────────────────────────────────────
    private static final String SENDER_EMAIL    = "baliemna2222@gmail.com";
    private static final String SENDER_PASSWORD = "ujsbowqjpmfqnpjv"; // App Password (not your real password)

    // ── Store the last generated code ─────────────────────────
    private static String lastGeneratedCode = null;

    // ── Generate a 6-digit code ────────────────────────────────
    public static String generateCode() {
        lastGeneratedCode = String.format("%06d", new Random().nextInt(999999));
        return lastGeneratedCode;
    }

    public static String getLastGeneratedCode() {
        return lastGeneratedCode;
    }

    // ── Send verification email ────────────────────────────────
    public static boolean sendVerificationEmail(String toEmail, String code) {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");
        props.put("mail.smtp.ssl.trust",       "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Verification Code — Raksha");

            // HTML email body
            String htmlBody =
                "<div style='font-family:Arial,sans-serif; background:#0a0f1e;" +
                "padding:40px; border-radius:12px; max-width:500px; margin:auto;'>" +
                "<h2 style='color:#00d4ff; text-align:center;'>Raksha</h2>" +
                "<p style='color:#cccccc; font-size:16px;'>Your verification code is:</p>" +
                "<div style='background:#111827; border:2px solid #00d4ff;" +
                "border-radius:10px; padding:20px; text-align:center; margin:20px 0;'>" +
                "<span style='font-size:36px; font-weight:bold; color:#00d4ff;" +
                "letter-spacing:10px;'>" + code + "</span>" +
                "</div>" +
                "<p style='color:#888888; font-size:13px;'>This code expires in 10 minutes." +
                " If you did not request this, ignore this email.</p>" +
                "</div>";

            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("✅ Email sent to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}