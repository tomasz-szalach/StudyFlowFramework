package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.service.TaskService;
import com.example.studyflowframework.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Pokazuje główną stronę (homepage.html) po zalogowaniu.
 */
@Controller
@Tag(name = "Home Management", description = "Operacje związane z wyświetlaniem strony głównej")
public class HomeController {

    private final TaskListService taskListService;
    private final TaskService taskService;
    private final UserRepository userRepository; // Do pobrania userId

    @Autowired
    public HomeController(TaskListService taskListService,
                          TaskService taskService,
                          UserRepository userRepository) {
        this.taskListService = taskListService;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    /**
     * Wyświetla stronę główną dla zalogowanego użytkownika.
     *
     * @param model Model do przekazania danych do widoku.
     * @return Nazwa widoku homepage.html.
     */
    @Operation(summary = "Wyświetla stronę główną po zalogowaniu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Strona główna wyświetlona pomyślnie"),
            @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
    })
    @GetMapping("/home")
    public String showHomePage(Model model) {
        // 1. Pobierz email aktualnie zalogowanego użytkownika
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Znajdź encję User (by dostać userId)
        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Long userId = userEntity.getId();

        // 3. Pobierz listy i zadania tego użytkownika
        List<TaskList> userLists = taskListService.getAllTaskLists(userId);
        List<Task> tasks = taskService.getTasksByUserId(userId);

        // 4. (Opcjonalnie) nazwa pierwszej listy do wyświetlenia
        String activeListName = userLists.isEmpty() ? "Brak list" : userLists.get(0).getName();

        // 5. Wrzucamy do modelu i zwracamy homepage.html
        model.addAttribute("taskLists", userLists);
        model.addAttribute("tasks", tasks);
        model.addAttribute("activeListName", activeListName);

        return "homepage";
    }
}
