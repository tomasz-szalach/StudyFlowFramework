package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import com.example.studyflowframework.service.TaskService;
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
 *  - DELETE /api/tasks/{id}       -> usunięcie (już było)
 *
 *  - GET /api/tasks/tasklists/{listId}/tasks
 *  - GET /api/tasks/tasklists/{listId}/search?query=...
 *  - PATCH /api/tasks/{id}/toggle
 */
@RestController
@RequestMapping("/api/tasks")
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
     * GET /api/tasks/{id}
     * Pobiera pojedyncze zadanie (jeśli należy do zalogowanego usera).
     */
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
     * POST /api/tasks
     * Tworzenie nowego zadania.
     */
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
     * PUT /api/tasks/{id}
     * Aktualizacja zadania.
     */
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
     * DELETE /api/tasks/{id}
     * (Już miałeś taką metodę – dodałem tylko typ).
     */
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
     * GET /api/tasks/tasklists/{listId}/tasks
     * Zwraca JSON z zadaniami w obrębie listy i usera,
     * posortowanymi wg dueDate (patrz repository).
     */
    @GetMapping("/tasklists/{listId}/tasks")
    public ResponseEntity<List<Task>> getTasksForList(@PathVariable Long listId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserOrThrow(email);

        List<Task> tasks = taskService.getTasksByTaskList(listId, user.getId());
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/tasks/tasklists/{listId}/search?query=...
     */
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
     * PATCH /api/tasks/{id}/toggle
     * Przełącza status zadania z "todo" na "completed" lub odwrotnie
     */
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
    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
