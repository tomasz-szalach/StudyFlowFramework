package com.example.studyflowframework.config;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Odbiorca (Sub) komunikatów z Redis:
 * - Metoda onMessage() jest wywoływana przez listenerAdapter,
 *   zdefiniowany w RedisConfig.
 */
@Service
public class RedisSubscriber {

    // Twoje dane do logowania
    private static final String FROM_EMAIL = "studyflowapplication@gmail.com";
    private static final String APP_PASSWORD = "yydr veqd kofr jxfz";

    /**
     * Odbiera wiadomość z Redis (np. email lub email|kod MFA)
     */
    public void onMessage(String message, String pattern) {
        System.out.println("[REDIS] Odebrano komunikat: " + message);

        try {
            if (message.contains("|")) {
                // MFA: email|kod
                String[] parts = message.split("\\|", 2);
                String to = parts[0];
                String code = parts[1];

                sendEmail(to, "Kod weryfikacyjny MFA",
                        "Twój kod MFA do logowania w StudyFlow to: <b>" + code + "</b><br><br>Kod jest ważny przez 5 minut.");

                System.out.println("[REDIS] Wysłano kod MFA do: " + to);
            } else {
                // Rejestracja: tylko email
                String to = message;

                sendEmail(to, "Witamy w StudyFlow",
                        "Dziękujemy za rejestrację w aplikacji StudyFlow. Miłego korzystania!");

                System.out.println("[REDIS] Wysłano mail powitalny do: " + to);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("[REDIS] Błąd przy wysyłaniu maila");
        }
    }

    /**
     * Metoda wysyłająca wiadomość e-mail
     */
    private void sendEmail(String to, String subject, String htmlBody) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlBody, "text/html; charset=utf-8");

        Transport.send(message);
    }
}
