package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Dodawanie nowej listy zadań (addTaskList.html)
 */
@Controller
@Tag(name = "Task List Management", description = "Operacje związane z zarządzaniem listami zadań")
public class AddTaskListController {

    private final TaskListService taskListService;
    private final UserRepository userRepository;

    @Autowired
    public AddTaskListController(TaskListService taskListService,
                                 UserRepository userRepository) {
        this.taskListService = taskListService;
        this.userRepository = userRepository;
    }

    /**
     * Wyświetla formularz do dodawania nowej listy zadań
     *
     * @return Nazwa widoku formularza
     */
    @Operation(summary = "Wyświetla formularz do dodawania nowej listy zadań")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formularz wyświetlony pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/addTaskList")
    public String showAddTaskListForm() {
        return "addTaskList"; // resources/templates/addTaskList.html
    }

    /**
     * Tworzy nową listę zadań dla zalogowanego użytkownika
     *
     * @param name  Nazwa nowej listy zadań
     * @param model Model do przekazania danych do widoku
     * @return Redirect do strony głównej
     */
    @Operation(summary = "Tworzy nową listę zadań dla zalogowanego użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Lista zadań utworzona, przekierowanie do strony głównej"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
            @ApiResponse(responseCode = "404", description = "Nieznaleziony użytkownik")
    })
    @PostMapping("/createTaskList")
    public String createTaskList(@RequestParam String name, Model model) {
        // 1. Pobierz email z kontekstu
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. Znajdź usera, by mieć userId
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = user.getId();

        // (ew. messages)
        List<String> messages = new ArrayList<>();

        // 3. Tworzymy listę zadań
        taskListService.addTaskList(name, userId);

        // 4. Redirect do /home
        return "redirect:/home";
    }
}
