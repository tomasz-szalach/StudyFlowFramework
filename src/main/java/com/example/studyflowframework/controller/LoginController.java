package com.example.studyflowframework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Kontroler obsługujący logowanie użytkowników.
 */
@Controller
@Tag(name = "Authentication", description = "Operacje związane z logowaniem i autoryzacją")
public class LoginController {

    /**
     * Wyświetla formularz logowania.
     *
     * @return Nazwa widoku login.html.
     */
    @Operation(summary = "Wyświetla formularz logowania")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formularz logowania wyświetlony pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/login")
    public String showLogin() {
        // Nazwa widoku: szuka pliku "login.html" w folderze resources/templates/
        return "login";
    }
}
