package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Widok 'taskList.html' - np. lista wszystkich list zadań w innej formie
 */
@Controller
public class TaskListController {

    private final TaskListService taskListService;

    @Autowired
    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    // GET -> pokaż listę zadań w stylu taskList.html
    @GetMapping("/taskLists")
    public String showTaskLists(Model model) {
        Long userId = 1L; // TODO: z Security
        List<TaskList> lists = taskListService.getAllTaskLists(userId);
        model.addAttribute("taskLists", lists);
        return "taskList"; // -> resources/templates/taskList.html
    }
}
