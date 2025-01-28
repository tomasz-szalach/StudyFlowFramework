package com.example.studyflowframework.config;

import com.example.studyflowframework.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Odbiorca (Sub) komunikatów z Redis:
 * - Metoda onMessage() jest wywoływana przez listenerAdapter,
 *   zdefiniowany w RedisConfig.
 */
@Service
public class RedisSubscriber {

    @Autowired
    private MailService mailService;

    /**
     * Odbiera wiadomość (czyli np. e-mail użytkownika).
     */
    public void onMessage(String message, String pattern) {
        System.out.println("[REDIS] Odebrano komunikat: " + message);

        try {
            mailService.sendWelcomeEmail(message);
            System.out.println("[REDIS] Wyslano mail powitalny do: " + message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("[REDIS] Błąd przy wysyłaniu maila do: " + message);
        }
    }
}
