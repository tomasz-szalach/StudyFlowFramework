package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import com.example.studyflowframework.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    private final UserRepository userRepo;
    private final TaskService    taskService;

    public AccountController(UserRepository ur, TaskService ts) {
        this.userRepo    = ur;
        this.taskService = ts;
    }

    @GetMapping("/account")
    public String account(Model m) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User   user  = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        long total = taskService.getTasksByUserId(user.getId()).size();
        long done  = taskService.getTasksByUserId(user.getId())
                .stream()
                .filter(t -> "completed".equals(t.getStatus()))
                .count();

        m.addAttribute("user",  user);
        m.addAttribute("total", total);
        m.addAttribute("done",  done);
        return "account";
    }
}
