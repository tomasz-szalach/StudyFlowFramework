package com.example.studyflowframework.controller;

import com.example.studyflowframework.config.RedisPublisher; // <-- dopisz import
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final RedisPublisher redisPublisher; // <-- nowy

    @Autowired
    public RegistrationController(UserService userService,
                                  RedisPublisher redisPublisher) {
        this.userService = userService;
        this.redisPublisher = redisPublisher; // <-- zapisz w polu
    }

    @GetMapping("/registrationUser")
    public String showRegistrationForm() {
        return "registration";
    }

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

        // Czy email już istnieje
        if (userService.ifContainsEmail(email)) {
            messages.add("Użytkownik o tym emailu już istnieje!");
            model.addAttribute("messages", messages);
            return "registration";
        }

        // Tworzymy nowego usera (rola USER)
        User user = new User(email, password, "USER");
        userService.saveUser(user);

        // 1) Publikujemy do Redis (np. email usera)
        redisPublisher.publishUserRegistration(email);

        // 2) redirect do login
        return "redirect:/login";
    }
}
