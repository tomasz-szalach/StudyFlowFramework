package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomePageController {

    @Autowired
    private TaskListRepository taskListRepository;

    @GetMapping("/homePage")
    public String showHomePage(Model model) {
        List<TaskList> taskLists = taskListRepository.findAll();
        model.addAttribute("taskLists", taskLists);
        return "homePage"; // Widok homePage.html
    }
}
