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
    public String showHomePage(Model model) {
        // Przykładowo pobierz ID zalogowanego usera (na razie "na sztywno")
        Long userId = 1L; // TODO: docelowo pobierz z Security

        // Wszystkie listy zadań użytkownika
        List<TaskList> userLists = taskListService.getAllTaskLists(userId);

        // Możesz pobrać zadania dla pierwszej listy lub wszystkie
        // Tylko przykład:
        List<Task> tasks = taskService.getTasksByUserId(userId);

        model.addAttribute("taskLists", userLists);
        model.addAttribute("tasks", tasks);
        model.addAttribute("activeListName", userLists.isEmpty() ? "Brak list" : userLists.get(0).getName());

        return "homepage"; // -> resources/templates/homepage.html
    }
}
