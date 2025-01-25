package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import com.example.studyflowframework.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rozszerzony kontroler REST do encji Task.
 *
 * Obsługuje:
 *  - GET  /api/tasks/{id}         -> pojedyncze zadanie
 *  - POST /api/tasks              -> tworzenie nowego
 *  - PUT  /api/tasks/{id}         -> aktualizacja
 *  - DELETE /api/tasks/{id}       -> usunięcie
 *
 *  - GET /api/tasks/tasklists/{listId}/tasks
 *  - GET /api/tasks/tasklists/{listId}/search?query=...
 *  - PATCH /api/tasks/{id}/toggle
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "Operacje związane z zarządzaniem zadaniami")
public class TaskRestController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    @Autowired
    public TaskRestController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    //------------------------------------------
    // Nowe: uniwersalne CRUD endpointy
    //------------------------------------------

    /**
     * Pobiera pojedyncze zadanie (jeśli należy do zalogowanego usera).
     *
     * @param id ID zadania.
     * @return ResponseEntity z zadaniem lub statusem 404.
     */
    @Operation(summary = "Pobiera pojedyncze zadanie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zadanie znalezione"),
            @ApiResponse(responseCode = "404", description = "Zadanie nie znalezione lub nie należy do użytkownika"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getOneTask(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return ResponseEntity.<Task>notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    /**
     * Tworzenie nowego zadania.
     *
     * @param newTask Obiekt Task z danymi nowego zadania.
     * @return ResponseEntity z utworzonym zadaniem i statusem 201.
     */
    @Operation(summary = "Tworzy nowe zadanie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Zadanie utworzone"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task newTask) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        // Nadpisz userId
        newTask.setUserId(user.getId());

        // Ewentualnie sprawdź, czy newTask.getTaskListId() też należy do usera:
        // (to można sprawdzić w serwisie)

        Task saved = taskService.saveTask(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Aktualizacja zadania.
     *
     * @param id      ID zadania do aktualizacji.
     * @param updated Obiekt Task z zaktualizowanymi danymi.
     * @return ResponseEntity z zaktualizowanym zadaniem lub statusem 404.
     */
    @Operation(summary = "Aktualizuje istniejące zadanie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zadanie zaktualizowane"),
            @ApiResponse(responseCode = "404", description = "Zadanie nie znalezione lub nie należy do użytkownika"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @RequestBody Task updated) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        // Znajdź stare zadanie
        Task existing = taskService.getTaskById(id);
        if (existing == null || !existing.getUserId().equals(user.getId())) {
            return ResponseEntity.<Task>notFound().build();
        }

        // Nadpisz pola
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setDueDate(updated.getDueDate());
        existing.setStatus(updated.getStatus());
        existing.setTaskListId(updated.getTaskListId());
        existing.setUserId(user.getId());

        Task saved = taskService.saveTask(existing);
        return ResponseEntity.ok(saved);
    }

    /**
     * Usuwa zadanie.
     *
     * @param taskId ID zadania do usunięcia.
     * @return ResponseEntity z statusem 204 lub 404.
     */
    @Operation(summary = "Usuwa istniejące zadanie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Zadanie usunięte"),
            @ApiResponse(responseCode = "404", description = "Zadanie nie znalezione lub nie należy do użytkownika"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    //------------------------------------------
    // Twoje poprzednie endpointy
    //------------------------------------------

    /**
     * Zwraca listę zadań dla określonej listy zadań i użytkownika.
     *
     * @param listId ID listy zadań.
     * @return ResponseEntity z listą zadań.
     */
    @Operation(summary = "Zwraca listę zadań dla określonej listy zadań i użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zadania zwrócone pomyślnie"),
            @ApiResponse(responseCode = "404", description = "Lista zadań nie znaleziona lub brak zadań"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/tasklists/{listId}/tasks")
    public ResponseEntity<List<Task>> getTasksForList(@PathVariable Long listId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        List<Task> tasks = taskService.getTasksByTaskList(listId, user.getId());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Wyszukuje zadania w określonej liście zadań na podstawie zapytania.
     *
     * @param listId ID listy zadań.
     * @param query  Zapytanie wyszukiwania.
     * @return ResponseEntity z listą znalezionych zadań.
     */
    @Operation(summary = "Wyszukuje zadania w określonej liście zadań na podstawie zapytania")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zadania znalezione pomyślnie"),
            @ApiResponse(responseCode = "404", description = "Lista zadań nie znaleziona lub brak zadań"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/tasklists/{listId}/search")
    public ResponseEntity<List<Task>> searchTasksInList(
            @PathVariable Long listId,
            @RequestParam(defaultValue = "") String query) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        List<Task> tasks = taskService.searchTasksInList(listId, user.getId(), query);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Przełącza status zadania między "todo" a "completed".
     *
     * @param taskId ID zadania.
     * @return ResponseEntity z nowym statusem zadania.
     */
    @Operation(summary = "Przełącza status zadania między 'todo' a 'completed'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status zadania przełączony"),
            @ApiResponse(responseCode = "404", description = "Zadanie nie znalezione lub nie należy do użytkownika"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<String> toggleStatus(@PathVariable("id") Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        String current = task.getStatus();
        String newStatus = current.equals("completed") ? "todo" : "completed";
        taskService.updateTaskStatus(taskId, newStatus);

        return ResponseEntity.ok(newStatus);
    }

    //------------------------------------------
    // Pomocnicza metoda
    //------------------------------------------

    /**
     * Znajduje użytkownika na podstawie emaila lub rzuca wyjątkiem.
     *
     * @param email Email użytkownika.
     * @return Obiekt User.
     */
    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
