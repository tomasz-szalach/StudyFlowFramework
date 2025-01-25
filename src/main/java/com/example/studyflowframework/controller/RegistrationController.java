package com.example.studyflowframework.controller;

import com.example.studyflowframework.config.RedisPublisher;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Kontroler obsługujący rejestrację nowych użytkowników.
 */
@Controller
@Tag(name = "User Registration", description = "Operacje związane z rejestracją nowych użytkowników")
public class RegistrationController {

    private final UserService userService;
    private final RedisPublisher redisPublisher; // Nowy komponent

    @Autowired
    public RegistrationController(UserService userService,
                                  RedisPublisher redisPublisher) {
        this.userService = userService;
        this.redisPublisher = redisPublisher; // Inicjalizacja pola
    }

    /**
     * Wyświetla formularz rejestracji użytkownika.
     *
     * @return Nazwa widoku registration.html.
     */
    @Operation(summary = "Wyświetla formularz rejestracji użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formularz rejestracji wyświetlony pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/registrationUser")
    public String showRegistrationForm() {
        return "registration";
    }

    /**
     * Przetwarza formularz rejestracji użytkownika.
     *
     * @param email     Email użytkownika.
     * @param password  Hasło użytkownika.
     * @param password2 Potwierdzenie hasła użytkownika.
     * @param model     Model do przekazania danych do widoku.
     * @return Widok rejestracji z komunikatami lub redirect do logowania.
     */
    @Operation(summary = "Przetwarza rejestrację nowego użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rejestracja przetworzona, formularz z komunikatami"),
            @ApiResponse(responseCode = "302", description = "Rejestracja zakończona, przekierowanie do logowania"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @PostMapping("/registrationUser")
    public String processRegistration(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            Model model
    ) {
        List<String> messages = new ArrayList<>();

        // Walidacja
        if (!password.equals(password2)) {
            messages.add("Hasła nie są takie same!");
            model.addAttribute("messages", messages);
            return "registration";
        }

        // Sprawdzenie, czy email już istnieje
        if (userService.ifContainsEmail(email)) {
            messages.add("Użytkownik o tym emailu już istnieje!");
            model.addAttribute("messages", messages);
            return "registration";
        }

        // Tworzenie nowego użytkownika z rolą USER
        User user = new User(email, password, "USER");
        userService.saveUser(user);

        // Publikacja rejestracji do Redis
        redisPublisher.publishUserRegistration(email);

        // Redirect do strony logowania
        return "redirect:/login";
    }
}
