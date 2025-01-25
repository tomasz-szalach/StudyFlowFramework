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
 * REST kontroler do akcji (toggle status, usuwanie) + pobierania zadań danej listy.
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
     * Zwraca JSON z zadaniami danej listy (należącymi do zalogowanego usera).
     */
    @GetMapping("/tasklists/{listId}/tasks")
    public ResponseEntity<List<Task>> getTasksForList(@PathVariable Long listId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = user.getId();

        List<Task> tasks = taskService.getTasksByTaskList(listId, userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * PATCH /api/tasks/{id}/toggle
     * Przełącza status zadania z "todo" na "completed" lub odwrotnie.
     * Zwraca nowy status w body.
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<String> toggleStatus(@PathVariable("id") Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        String current = task.getStatus();
        String newStatus = current.equals("completed") ? "todo" : "completed";
        taskService.updateTaskStatus(taskId, newStatus);

        return ResponseEntity.ok(newStatus);
    }

    /**
     * DELETE /api/tasks/{id}
     * Usuwa zadanie z bazy (tylko jeśli należy do zalogowanego usera).
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
