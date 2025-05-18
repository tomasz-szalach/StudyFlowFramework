package com.example.studyflowframework.config;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Odbiorca komunikatów Pub/Sub z Redis.
 *
 * Obsługiwane formaty komunikatu:
 *  •  "email|code"                     – MFA / rejestracja (oryginalnie)
 *  •  "emailChange|email|code"         – zmiana adresu e-mail
 *  •  "plainEmail"                     – mail powitalny
 */
@Service
public class RedisSubscriber {

    /* --- dane logowania do konta wysyłkowego --- */
    private static final String FROM_EMAIL   = "studyflowapplication@gmail.com";
    private static final String APP_PASSWORD = "yydr veqd kofr jxfz";

    /* --- główny handler wywoływany przez listenerAdapter --- */
    public void onMessage(String message, String pattern) {
        System.out.println("[REDIS] Odebrano komunikat: " + message);

        try {
            /* ---------- 1) Zmiana adresu e-mail ---------- */
            if (message.startsWith("emailChange|")) {
                /*  emailChange|email|code  */
                String[] p  = message.split("\\|", 3);
                String to   = p[1];
                String code = p[2];

                sendEmail(
                        to,
                        "Kod weryfikacyjny zmiany adresu e-mail",
                        """
                        Dzień dobry!<br><br>
                        Twój kod do potwierdzenia <b>zmiany adresu e-mail</b> w StudyFlow to:
                        <h2 style="letter-spacing:2px;">%s</h2>
                        Kod jest ważny przez 5 minut.<br><br>
                        Jeśli to nie Ty próbujesz zmienić adres – zignoruj tę wiadomość.
                        """.formatted(code)
                );
                System.out.println("[REDIS] Wysłano kod zmiany e-mail do: " + to);
                return;
            }

            /* ---------- 2) MFA / rejestracja (stare zachowanie) ---------- */
            if (message.contains("|")) {
                /*  email|code  */
                String[] p  = message.split("\\|", 2);
                String to   = p[0];
                String code = p[1];

                sendEmail(
                        to,
                        "Kod weryfikacyjny MFA",
                        """
                        Twój kod MFA do logowania w StudyFlow:
                        <h2 style="letter-spacing:2px;">%s</h2>
                        Kod jest ważny przez 5 minut.
                        """.formatted(code)
                );
                System.out.println("[REDIS] Wysłano kod MFA do: " + to);
                return;
            }

            /* ---------- 3) Mail powitalny ---------- */
            sendEmail(
                    message,
                    "Witamy w StudyFlow",
                    "Dziękujemy za rejestrację w StudyFlow. Miłego korzystania!"
            );
            System.out.println("[REDIS] Wysłano mail powitalny do: " + message);

        } catch (MessagingException e) {
            System.err.println("[REDIS] Błąd przy wysyłaniu maila");
            e.printStackTrace();
        }
    }

    /* --- wysyłka SMTP --- */
    private void sendEmail(String to, String subject, String htmlBody) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth",              "true");
        props.put("mail.smtp.starttls.enable",   "true");
        props.put("mail.smtp.host",              "smtp.gmail.com");
        props.put("mail.smtp.port",              "587");

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                    }
                });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM_EMAIL));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(htmlBody, "text/html; charset=utf-8");

        Transport.send(msg);
    }
}
