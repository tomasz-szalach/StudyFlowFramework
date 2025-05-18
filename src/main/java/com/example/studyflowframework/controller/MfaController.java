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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Wyświetla formularz weryfikacji MFA (Multi-Factor Authentication).
     *
     * @return Widok verify-mfa.html
     */
    @GetMapping
    public String showMfaForm() {
        return "verify-mfa"; // verify-mfa.html w templates
    }

    /**
     * Obsługuje sprawdzanie kodu MFA.
     *
     * @param mfaCode kod wpisany przez użytkownika
     * @param session obiekt sesji
     * @param model   model widoku
     * @return Przekierowanie do /home lub ponowne wyświetlenie formularza z błędem
     */
    @PostMapping
    public String verifyMfa(@RequestParam("mfaCode") String mfaCode,
                            HttpSession session,
                            Model model) {
        String email = (String) session.getAttribute("mfa_email");

        if (email == null) {
            return "redirect:/login";
        }

        String key = "mfa:" + email;
        String expectedCode = (String) redisTemplate.opsForValue().get(key);

        if (expectedCode != null && expectedCode.equals(mfaCode)) {
            session.setAttribute("mfa_verified", true);
            redisTemplate.delete(key);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Niepoprawny kod MFA");
            return "verify-mfa";
        }
    }
}
