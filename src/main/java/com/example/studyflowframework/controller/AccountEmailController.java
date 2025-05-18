package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/account/change-email")
@Validated
public class AccountEmailController {

    private final UserService userService;

    public AccountEmailController(UserService userService) {
        this.userService = userService;
    }

    /* ---------- GET ---------- */
    @GetMapping
    public String form() {
        return "changeEmail";
    }

    /* ---------- POST – wysłanie kodu ---------- */
    @PostMapping
    public String sendCode(@RequestParam("newEmail")
                           @NotBlank @Email String newEmail,
                           HttpSession session,
                           RedirectAttributes ra) {

        User me = userService.currentUser();

        /* ① taki sam jak obecny */
        if (me.getEmail().equalsIgnoreCase(newEmail)) {
            ra.addFlashAttribute("error",
                    "Podany adres jest już Twoim aktualnym.");
            return "redirect:/account/change-email";
        }

        /* ② zajęty przez innego użytkownika */
        if (userService.ifContainsEmail(newEmail)) {
            ra.addFlashAttribute("error",
                    "Ten adres jest już zajęty.");
            return "redirect:/account/change-email";
        }

        /* 6-cyfrowy kod */
        String code = "%06d".formatted(
                ThreadLocalRandom.current().nextInt(1_000_000));

        /* zapis w sesji */
        session.setAttribute("verifyCode", code);
        session.setAttribute("newEmail",   newEmail);
        session.setAttribute("uid",        me.getId());
        session.setMaxInactiveInterval(300);

        /* mail → Redis */
        userService.publishEmailChange(newEmail, code);

        ra.addFlashAttribute("step", 2);
        ra.addFlashAttribute("newEmail", newEmail);
        ra.addFlashAttribute("info",
                "Kod został wysłany na " + newEmail);
        return "redirect:/account/change-email";
    }

    /* ---------- POST – weryfikacja kodu ---------- */
    @PostMapping("/verify")
    public String verify(@RequestParam("code") String code,
                         HttpSession session,
                         RedirectAttributes ra) {

        String good = (String) session.getAttribute("verifyCode");
        String mail = (String) session.getAttribute("newEmail");
        Long   uid  = (Long)   session.getAttribute("uid");

        if (good == null || mail == null || uid == null) {
            ra.addFlashAttribute("error",
                    "Sesja wygasła – rozpocznij od początku.");
            return "redirect:/account/change-email";
        }

        if (!good.equals(code.trim())) {
            ra.addFlashAttribute("step", 2);
            ra.addFlashAttribute("newEmail", mail);
            ra.addFlashAttribute("error",
                    "Niepoprawny kod weryfikacyjny.");
            return "redirect:/account/change-email";
        }

        userService.changeEmail(uid, mail);
        session.invalidate();
        ra.addFlashAttribute("success",
                "Adres e-mail został zmieniony.");
        return "redirect:/account";
    }
}
