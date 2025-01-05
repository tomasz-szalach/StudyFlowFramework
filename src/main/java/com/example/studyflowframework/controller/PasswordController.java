package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/changePassword")
    public String showChangePasswordPage() {
        return "changePassword"; // Widok changePassword.html
    }

    @PostMapping("/changePassword")
    public String handleChangePassword(String oldPassword, String newPassword, String newPassword2, Model model) {
        // Przykładowa logika zmiany hasła (trzeba dopasować do logiki aplikacji)
        if (!newPassword.equals(newPassword2)) {
            model.addAttribute("error", "Hasła się nie zgadzają.");
            return "changePassword";
        }
        // Logika aktualizacji hasła w bazie
        model.addAttribute("success", "Hasło zostało zmienione.");
        return "redirect:/homePage";
    }
}
