package com.example.studyflowframework.controller;

import com.example.studyflowframework.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AddTaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    // GET -> pokaż formularz
    @GetMapping("/addTaskList")
    public String showAddTaskListForm() {
        return "addTaskList"; // resources/templates/addTaskList.html
    }

    // POST -> obsługa tworzenia
    @PostMapping("/createTaskList")
    public String createTaskList(@RequestParam String name, Model model) {
        Long userId = 1L; // TODO: realnie -> z Security

        List<String> messages = new ArrayList<>();

        // Tutaj można sprawdzić, czy lista o takiej nazwie już istnieje:
        // if ( ... ) { messages.add("Taka lista już jest!"); ... }

        taskListService.addTaskList(name, userId);

        // Po dodaniu listy -> redirect np. do /home
        return "redirect:/home";
    }
}
