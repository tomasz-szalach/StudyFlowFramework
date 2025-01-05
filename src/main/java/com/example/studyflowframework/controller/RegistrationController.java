package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registrationUser")
    public String showRegistrationPage() {
        return "registration"; // Widok registration.html
    }

    @PostMapping("/registrationUser")
    public String handleRegistration(User user, Model model) {
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Użytkownik o podanym adresie e-mail już istnieje.");
            return "registration";
        }

        // Logika zapisu użytkownika w bazie
        userRepository.save(user);
        model.addAttribute("success", "Konto zostało utworzone.");
        return "redirect:/";
    }
}
