package com.example.studyflowframework.controller;

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

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /* ---------- GET: formularz ---------- */
    @Operation(summary = "Wyświetla formularz rejestracji użytkownika")
    @ApiResponse(responseCode = "200", description = "Formularz wyświetlony")
    @GetMapping("/registrationUser")
    public String showRegistrationForm() {
        return "registration";
    }

    /* ---------- POST: przetwarzanie ---------- */
    @Operation(summary = "Przetwarza rejestrację nowego użytkownika")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Sukces → redirect /login"),
            @ApiResponse(responseCode = "200", description = "Błąd walidacji → powrót do formularza")
    })
    @PostMapping("/registrationUser")
    public String processRegistration(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            Model model) {

        List<String> messages = new ArrayList<>();

        /* ①  czy hasła identyczne */
        if (!password.equals(password2)) {
            messages.add("Hasła nie są takie same!");
        }

        /* ②  email unikalny */
        if (userService.ifContainsEmail(email)) {
            messages.add("Użytkownik o tym emailu już istnieje!");
        }

        /* ③  ewentualne błędy → powrót do formularza */
        if (!messages.isEmpty()) {
            model.addAttribute("messages", messages);
            return "registration";
        }

        /* ④  tworzymy konto (rola USER, imię/nazwisko puste) */
        userService.register(email, password, "", "");

        /* ⑤  redirect do logowania */
        return "redirect:/login?registered";
    }
}
