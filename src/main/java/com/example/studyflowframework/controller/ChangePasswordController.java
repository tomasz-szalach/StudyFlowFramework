package com.example.studyflowframework.controller;

import com.example.studyflowframework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Zmiana hasła użytkownika
 */
@Controller
public class ChangePasswordController {

    private final UserService userService;

    @Autowired
    public ChangePasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/changePassword")
    public String showChangePasswordForm() {
        // Po prostu zwraca formularz
        return "changePassword"; // resources/templates/changePassword.html
    }

    @PostMapping("/changePassword")
    public String processChangePassword(
            @RequestParam String old_pass,
            @RequestParam String new_pass,
            @RequestParam String new_pass2,
            Model model
    ) {
        List<String> messages = new ArrayList<>();
        Long userId = 1L; // TODO: docelowo pobierz z zalogowanego usera

        // Dla przykładu moglibyśmy pobrać User z bazy i sprawdzić, czy old_pass się zgadza
        // (tu: pominięte w kodzie)
        if (!new_pass.equals(new_pass2)) {
            messages.add("Nowe hasła nie są takie same!");
            model.addAttribute("messages", messages);
            return "changePassword";
        }

        // Zmień hasło w bazie
        userService.updatePassword(userId, new_pass);

        messages.add("Hasło zostało pomyślnie zmienione.");
        model.addAttribute("messages", messages);

        return "changePassword";
    }
}
