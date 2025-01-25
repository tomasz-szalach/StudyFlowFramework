package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.service.TaskListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Widok 'taskList.html' - np. lista wszystkich list zadań w innej formie
 */
@Controller
@Tag(name = "Task List Management", description = "Operacje związane z zarządzaniem listami zadań")
public class TaskListController {

    private final TaskListService taskListService;

    @Autowired
    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    /**
     * Wyświetla listę zadań w formie taskList.html
     *
     * @param model Model do przekazania danych do widoku.
     * @return Nazwa widoku taskList.html.
     */
    @Operation(summary = "Wyświetla listę wszystkich list zadań")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista zadań wyświetlona pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/taskLists")
    public String showTaskLists(Model model) {
        Long userId = 1L; // TODO: z Security
        List<TaskList> lists = taskListService.getAllTaskLists(userId);
        model.addAttribute("taskLists", lists);
        return "taskList"; // -> resources/templates/taskList.html
    }
}
