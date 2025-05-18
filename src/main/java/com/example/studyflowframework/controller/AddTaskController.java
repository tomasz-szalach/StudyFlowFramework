package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.service.TaskService;
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

import java.util.List;

/**
 * Dodawanie nowego zadania (addTaskPage.html)
 */
@Controller
@Tag(name = "Task Management", description = "Operacje związane z zarządzaniem zadaniami")
public class AddTaskController {

    private final TaskListService taskListService;
    private final TaskService taskService;
    private final UserRepository userRepository;

    @Autowired
    public AddTaskController(TaskListService taskListService,
                             TaskService taskService,
                             UserRepository userRepository) {
        this.taskListService = taskListService;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    /**
     * Formularz do dodawania zadania
     */
    @Operation(summary = "Wyświetla formularz do dodawania nowego zadania")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formularz wyświetlony pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/addTaskPage")
    public String showAddTaskPage(Model model) {
        // 1. Email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. userId
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = user.getId();

        // 3. listy zadań -> wyświetlane w <select> w addTaskPage
        List<TaskList> userLists = taskListService.getAllTaskLists(userId);
        model.addAttribute("taskLists", userLists);
        return "addTaskPage";
    }

    /**
     * Obsługuje formularz dodawania nowego zadania
     *
     * @param name        Nazwa zadania
     * @param description Opis zadania
     * @param due_date    Data wykonania zadania
     * @param task_list_id ID listy zadań
     * @return Redirect do strony głównej
     */
    @Operation(summary = "Dodaje nowe zadanie do określonej listy zadań")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Zadanie dodane, przekierowanie do strony głównej"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
            @ApiResponse(responseCode = "404", description = "Nieznaleziony użytkownik")
    })
    @PostMapping("/addTask")
    public String addTask(@RequestParam String name,
                          @RequestParam String description,
                          @RequestParam String due_date,
                          @RequestParam Long   task_list_id,
                          @RequestParam(name="priority", defaultValue = "low") String prio) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        taskService.addTask(
                name,
                description,
                due_date,
                "todo",
                task_list_id,
                user.getId(),
                Task.Priority.valueOf(prio.toUpperCase())        // LOW | MEDIUM | HIGH
        );
        return "redirect:/home";
    }
}
