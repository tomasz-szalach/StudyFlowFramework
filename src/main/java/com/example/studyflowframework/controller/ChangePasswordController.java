package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Validated
@RequestMapping("/changePassword")
public class ChangePasswordController {

    private final UserService userService;

    public ChangePasswordController(UserService userService) {
        this.userService = userService;
    }

    /* ---------- GET ---------- */
    @GetMapping
    public String form() { return "changePassword"; }

    /* ---------- POST ---------- */
    @PostMapping
    public String change(@RequestParam("old_pass") @NotBlank String oldPwd,
                         @RequestParam("new_pass") @NotBlank String newPwd,
                         RedirectAttributes ra) {

        /* ①  czy nowe ≠ stare wpisane */
        if (oldPwd.equals(newPwd)) {
            ra.addFlashAttribute("error",
                    "Nowe hasło musi różnić się od aktualnego.");
            return "redirect:/changePassword";
        }

        User u = userService.currentUser();

        /* ②  stare hasło poprawne? */
        if (!userService.passwordMatches(u, oldPwd)) {
            ra.addFlashAttribute("error",
                    "Stare hasło jest niepoprawne.");
            return "redirect:/changePassword";
        }

        /* ③  nowe nie może pokrywać się z zapisanym (gdy ktoś wpisał inne “stare”) */
        if (userService.passwordMatches(u, newPwd)) {
            ra.addFlashAttribute("error",
                    "Nowe hasło musi różnić się od aktualnego.");
            return "redirect:/changePassword";
        }

        userService.updatePassword(u.getId(), newPwd);
        ra.addFlashAttribute("success",
                "Hasło zostało zmienione. Przy następnym logowaniu użyj nowego.");
        return "redirect:/changePassword";
    }
}
