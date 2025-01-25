package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Zmiana hasła użytkownika po zalogowaniu.
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
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String processChangePassword(
            @RequestParam String old_pass,
            @RequestParam String new_pass,
            @RequestParam String new_pass2,
            Model model
    ) {
        List<String> messages = new ArrayList<>();

        // 1) Pobierz zalogowanego usera (email) z kontekstu Security
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Nie znaleziono usera: " + email);
        }

        // 2) Sprawdź stare hasło (NoOp: proste .equals)
        //    (jeśli używasz passwordEncoder, to passwordEncoder.matches)
        if (!user.getPassword().equals(old_pass)) {
            messages.add("Stare hasło jest niepoprawne!");
            model.addAttribute("messages", messages);
            return "changePassword";
        }

        // 3) Sprawdź czy new_pass == new_pass2
        if (!new_pass.equals(new_pass2)) {
            messages.add("Nowe hasła nie są takie same!");
            model.addAttribute("messages", messages);
            return "changePassword";
        }

        // (opcjonalnie) walidacja minimalnej długości
        if (new_pass.length() < 5) {
            messages.add("Hasło musi mieć co najmniej 5 znaków!");
            model.addAttribute("messages", messages);
            return "changePassword";
        }

        // 4) Zmień hasło w bazie (NoOp wprost, lub passwordEncoder.encode(new_pass))
        userService.updatePassword(user.getId(), new_pass);

        messages.add("Hasło zostało pomyślnie zmienione. Zaloguj się ponownie nowym hasłem.");
        model.addAttribute("messages", messages);

        // Można zrobić redirect na /login:
        // return "redirect:/login";
        // lub zostać na tej samej stronie i wyświetlić komunikat:
        return "changePassword";
    }
}
