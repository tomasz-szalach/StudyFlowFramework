package com.example.studyflowframework.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Handler sukcesu logowania – uruchamia MFA.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic userRegistrationTopic;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName(); // zakładamy, że email to login

        // 1. Wygeneruj kod MFA
        String mfaCode = String.format("%06d", new Random().nextInt(999999));

        // 2. Zapisz kod do Redis
        redisTemplate.opsForValue().set("mfa:" + email, mfaCode, 5, TimeUnit.MINUTES);

        // 3. Wyślij przez Redis do mailera
        redisTemplate.convertAndSend(userRegistrationTopic.getTopic(), email + "|" + mfaCode);

        // 4. Ustaw sesję
        HttpSession session = request.getSession();
        session.setAttribute("mfa_email", email);
        session.setAttribute("mfa_verified", false);

        // 5. Przekieruj na formularz weryfikacji MFA
        response.sendRedirect("/verify-mfa");
    }
}
