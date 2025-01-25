package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.service.TaskService;
import com.example.studyflowframework.repository.UserRepository;
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

    // GET -> pokaż formularz do dodawania zadania
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

    // POST -> obsługa formularza
    @PostMapping("/addTask")
    public String addTask(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String due_date,
            @RequestParam Long task_list_id
    ) {
        // 1. Email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. userId
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = user.getId();

        // 3. Zakładamy status domyślny "todo"
        String status = "todo";

        // 4. Zapis
        taskService.addTask(name, description, due_date, status, task_list_id, userId);

        // 5. redirect
        return "redirect:/home";
    }
}
