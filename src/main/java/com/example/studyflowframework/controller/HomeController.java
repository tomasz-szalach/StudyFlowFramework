package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.service.TaskService;
import com.example.studyflowframework.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Pokazuje główną stronę (homepage.html) po zalogowaniu
 */
@Controller
public class HomeController {

    private final TaskListService taskListService;
    private final TaskService taskService;

    @Autowired
    public HomeController(TaskListService taskListService, TaskService taskService) {
        this.taskListService = taskListService;
        this.taskService = taskService;
    }

    @GetMapping("/home")
    public String showHomePage() {
        // Tymczasowo NIC nie pobieramy z bazy
        return "homepage";
    }

}
