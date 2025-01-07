package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AddTaskController(TaskListService taskListService, TaskService taskService) {
        this.taskListService = taskListService;
        this.taskService = taskService;
    }

    // GET -> pokaż formularz do dodawania zadania
    @GetMapping("/addTaskPage")
    public String showAddTaskPage(Model model) {
        Long userId = 1L; // TODO: realnie -> pobierz ID z kontekstu Security
        // Pobierz listy zadań użytkownika, by wypełnić <select>
        List<TaskList> userLists = taskListService.getAllTaskLists(userId);
        model.addAttribute("taskLists", userLists);
        return "addTaskPage"; // resources/templates/addTaskPage.html
    }

    // POST -> obsługa formularza
    @PostMapping("/addTask")
    public String addTask(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String due_date,
            @RequestParam Long task_list_id
    ) {
        Long userId = 1L; // TODO: pobierz z Security

        // Zakładamy status domyślny "todo"
        taskService.addTask(name, description, due_date, "todo", task_list_id, userId);

        // Po dodaniu zadania przekierowujemy np. na /home
        return "redirect:/home";
    }
}
