package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.repository.TaskListRepository;
import com.example.studyflowframework.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TaskController {

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/addTaskPage")
    public String showAddTaskPage(Model model) {
        model.addAttribute("taskLists", taskListRepository.findAll());
        return "addTaskPage"; // Widok addTaskPage.html
    }

    @PostMapping("/addTask")
    public String handleAddTask(Task task, Model model) {
        taskRepository.save(task);
        return "redirect:/homePage"; // Po dodaniu przekierowanie na stronę główną
    }

    @PostMapping("/deleteTask")
    public String deleteTask(Long id) {
        taskRepository.deleteById(id);
        return "redirect:/homePage";
    }
}
