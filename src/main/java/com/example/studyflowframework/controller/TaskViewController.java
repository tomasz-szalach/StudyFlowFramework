package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.service.TaskService;
import com.example.studyflowframework.service.TaskListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Zwykły (nie-REST) kontroler wyświetlający stronę szczegółów / edycji zadania.
 *
 * GET /task/{id}  →  templates/taskDetails.html
 */
@Controller
public class TaskViewController {

    private final TaskService     taskService;
    private final TaskListService taskListService;

    public TaskViewController(TaskService taskService,
                              TaskListService taskListService) {
        this.taskService     = taskService;
        this.taskListService = taskListService;
    }

    @GetMapping("/task/{id}")
    public String taskDetails(@PathVariable Long id, Model model) {

        /* pobierz zadanie; jeśli nie istnieje – wróć do listy */
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/home";
        }

        /* listy zadań użytkownika (do <select>) */
        List<TaskList> lists = taskListService.getAllTaskLists(task.getUserId());

        model.addAttribute("task", task);
        model.addAttribute("taskLists", lists);
        return "taskDetails";          // -> resources/templates/taskDetails.html
    }
}
