package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import com.example.studyflowframework.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST kontroler do akcji:
 *  - pobierania zadań danej listy,
 *  - wyszukiwania w obrębie listy,
 *  - toggle statusu,
 *  - usuwania zadania.
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

    /**
     * GET /api/tasks/tasklists/{listId}/tasks
     * Zwraca JSON z zadaniami należącymi do zalogowanego usera
     * w obrębie listy 'listId'.
     */
    @GetMapping("/tasklists/{listId}/tasks")
    public ResponseEntity<List<Task>> getTasksForList(@PathVariable Long listId) {
        // Ustalamy userId
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Pobranie zadań
        List<Task> tasks = taskService.getTasksByTaskList(listId, user.getId());
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/tasks/tasklists/{listId}/search?query=...
     * Wyszukiwanie w obrębie listy 'listId' i usera,
     * po fragmencie w name lub description.
     */
    @GetMapping("/tasklists/{listId}/search")
    public ResponseEntity<List<Task>> searchTasksInList(@PathVariable Long listId,
                                                        @RequestParam(defaultValue = "") String query) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        List<Task> tasks = taskService.searchTasksInList(listId, user.getId(), query);
        return ResponseEntity.ok(tasks);
    }

    /**
     * PATCH /api/tasks/{id}/toggle
     * Przełącza status zadania: "todo" <-> "completed".
     * Zwraca nowy status w body.
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<String> toggleStatus(@PathVariable("id") Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Znajdź zadanie
        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        // Przełącz status
        String current = task.getStatus();
        String newStatus = current.equals("completed") ? "todo" : "completed";
        taskService.updateTaskStatus(taskId, newStatus);

        return ResponseEntity.ok(newStatus);
    }

    /**
     * DELETE /api/tasks/{id}
     * Usuwa zadanie, jeśli należy do zalogowanego usera.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
