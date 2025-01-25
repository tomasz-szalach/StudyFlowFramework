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

/**
 * REST kontroler do akcji (toggle status, usuwanie) bez przeładowywania strony.
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
     * PATCH /api/tasks/{id}/toggle
     * Przełącza status zadania z "todo" -> "completed" lub odwrotnie.
     * Zwracamy nowy status (String).
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<String> toggleStatus(@PathVariable("id") Long taskId) {
        // 1. Pobierz usera po emailu
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = user.getId();

        // 2. Znajdź zadanie
        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            // Brak lub nie należy do zalogowanego usera
            return ResponseEntity.notFound().build();
        }

        // 3. Przełącz status
        String current = task.getStatus();
        String newStatus = current.equals("completed") ? "todo" : "completed";
        taskService.updateTaskStatus(taskId, newStatus);

        // 4. Zwróć nowy status
        return ResponseEntity.ok(newStatus);
    }

    /**
     * DELETE /api/tasks/{id}
     * Usuwa zadanie użytkownika (bez przeładowania w frontend).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = user.getId();

        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
