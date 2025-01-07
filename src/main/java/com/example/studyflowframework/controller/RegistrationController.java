package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Obsługa rejestracji (formularz i logika)
 */
@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // GET -> pokaż formularz rejestracji
    @GetMapping("/registrationUser")
    public String showRegistrationForm() {
        return "registration"; // resources/templates/registration.html
    }

    // POST -> przetwórz formularz
    @PostMapping("/registrationUser")
    public String processRegistration(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            Model model
    ) {
        List<String> messages = new ArrayList<>();

        // Przykład walidacji
        if (!password.equals(password2)) {
            messages.add("Hasła nie są takie same!");
            model.addAttribute("messages", messages);
            return "registration"; // wróć do formularza
        }

        // Sprawdzamy czy email istnieje
        if (userService.ifContainsEmail(email)) {
            messages.add("Użytkownik o tym emailu już istnieje!");
            model.addAttribute("messages", messages);
            return "registration";
        }

        // Zapisz nowego usera
        User user = new User(username, email, password, "USER");
        userService.saveUser(user);

        // Po pomyślnej rejestracji można przekierować do /login
        return "redirect:/login";
    }
}
