package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.repository.UserRepository;
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
public class AddTaskListController {

    private final TaskListService taskListService;
    private final UserRepository userRepository;

    @Autowired
    public AddTaskListController(TaskListService taskListService,
                                 UserRepository userRepository) {
        this.taskListService = taskListService;
        this.userRepository = userRepository;
    }

    // GET -> pokaż formularz
    @GetMapping("/addTaskList")
    public String showAddTaskListForm() {
        return "addTaskList"; // resources/templates/addTaskList.html
    }

    // POST -> obsługa tworzenia
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
