package com.example.studyflowframework.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Kontroler obsługujący weryfikację kodu MFA.
 */
@Controller
@RequestMapping("/verify-mfa")
public class MfaController {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public MfaController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /* ---------- GET – formularz ---------- */
    @GetMapping
    public String form() {
        return "verify-mfa";                 // templates/verify-mfa.html
    }

    /* ---------- POST – weryfikacja ---------- */
    @PostMapping
    public String verify(@RequestParam("code") String code,   // <── NAZWA = "code"
                         HttpSession session,
                         Model model) {

        /* a) brak sesji = wracamy do logowania */
        String email = (String) session.getAttribute("mfa_email");
        if (email == null) {
            return "redirect:/login";
        }

        /* b) pobierz oczekiwany kod z Redisa */
        String key          = "mfa:" + email;
        String expectedCode = (String) redisTemplate.opsForValue().get(key);

        if (expectedCode != null && expectedCode.equals(code.trim())) {
            /* ✓ kod poprawny */
            session.setAttribute("mfa_verified", true);   // flaga dla filtra
            redisTemplate.delete(key);                   // jednorazowy → usuń
            return "redirect:/home";
        }

        /* ✗ kod zły */
        model.addAttribute("error", "Niepoprawny kod MFA.");
        return "verify-mfa";
    }

    /* ---------- ponowne wysłanie kodu ---------- */
    @GetMapping("/resend")
    public String resendCode(HttpSession session, Model model){

        String email = (String) session.getAttribute("mfa_email");
        if (email == null) return "redirect:/login";

        // wygeneruj nowy kod, zapisz w Redis (TTL 5 min) – pseudo-kod:
        String code = String.format("%06d",(int)(Math.random()*1_000_000));
        redisTemplate.opsForValue().set("mfa:"+email, code, java.time.Duration.ofMinutes(5));

        // … tu wywołanie publishera, aby poszedł e-mail …
        // publisher.publishUserRegistration(email + "|" + code);

        model.addAttribute("success", "Nowy kod został wysłany.");
        return "verify-mfa";
    }
}
