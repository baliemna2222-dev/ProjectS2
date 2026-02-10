package JStream.service;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailService {

    private static final String FROM_EMAIL = "yourgmail@gmail.com"; // your email
    private static final String APP_PASSWORD = "your_app_password"; // Gmail App Password

    public static void sendVerificationCode(String toEmail, String code) {
        String subject = "Your Verification Code";
        String message = "Your verification code is: " + code + "\nIt expires in 15 minutes.";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);
            msg.setText(message);

            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
