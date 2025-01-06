package com.example.studyflowframework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLogin() {
        // Nazwa widoku: szuka pliku "login.html" w folderze resources/templates/
        return "login";
    }
}
