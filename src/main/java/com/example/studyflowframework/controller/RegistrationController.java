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

    /**
     * GET -> pokaż formularz rejestracji
     * Wyświetla widok "registration.html"
     */
    @GetMapping("/registrationUser")
    public String showRegistrationForm() {
        return "registration"; // resources/templates/registration.html
    }

    /**
     * POST -> przetwórz formularz
     * Parametry: email, password, password2
     * W encji User mamy konstruktor (String email, String password, String role)
     */
    @PostMapping("/registrationUser")
    public String processRegistration(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            Model model
    ) {
        List<String> messages = new ArrayList<>();

        // 1. Walidacja, czy hasła są takie same
        if (!password.equals(password2)) {
            messages.add("Hasła nie są takie same!");
            model.addAttribute("messages", messages);
            return "registration"; // wróć do formularza
        }

        // 2. Sprawdzamy, czy email już istnieje w bazie
        if (userService.ifContainsEmail(email)) {
            messages.add("Użytkownik o tym emailu już istnieje!");
            model.addAttribute("messages", messages);
            return "registration";
        }

        // 3. Zapisz nowego usera w bazie
        //    Konstruktor: User(String email, String password, String role)
        //    Załóżmy, że rejestrujemy domyślnie userów z rolą "USER"
        User user = new User(email, password, "USER");
        userService.saveUser(user);

        // 4. Po pomyślnej rejestracji można przekierować do /login
        return "redirect:/login";
    }
}
