package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import com.example.studyflowframework.service.TaskListService;
import com.example.studyflowframework.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AddTaskController {

    private final TaskListService taskListService;
    private final TaskService     taskService;
    private final UserRepository  userRepo;

    @Autowired
    public AddTaskController(TaskListService tls,
                             TaskService     ts,
                             UserRepository  ur) {
        this.taskListService = tls;
        this.taskService     = ts;
        this.userRepo        = ur;
    }

    /* ---------- formularz ---------- */
    @GetMapping("/addTaskPage")
    public String form(Model m) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User   user  = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        List<TaskList> lists = taskListService.getAllTaskLists(user.getId());
        m.addAttribute("taskLists", lists);
        return "addTaskPage";
    }

    /* ---------- POST ---------- */
    @PostMapping("/addTask")
    public String addTask(@RequestParam String name,
                          @RequestParam String description,
                          @RequestParam String due_date,        // yyyy‑MM‑dd
                          @RequestParam Long   task_list_id,
                          @RequestParam(name="priority",
                                  defaultValue = "low") String prio) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User   user  = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        /* mapowanie priorytetu code → ID */
        short prioId = switch (prio.toLowerCase()) {
            case "medium" -> 2;
            case "high"   -> 3;
            default       -> 1;     // low
        };

        taskService.addTask(
                name,
                description,
                due_date,
                (short) 1,          // status_id = 1  (“todo”)
                task_list_id,
                user.getId(),
                prioId
        );
        return "redirect:/home";
    }
}
