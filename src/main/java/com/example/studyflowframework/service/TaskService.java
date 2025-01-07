package com.example.studyflowframework.service;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public String addTask(String name, String description, String dueDate, String status, Long taskListId, Long userId) {
        Task task = new Task(name, description, dueDate, status, userId, taskListId);
        taskRepository.save(task);
        return "Zadanie zostało dodane pomyślnie.";
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> getTasksByTaskList(Long listId, Long userId) {
        return taskRepository.findByTaskListIdAndUserId(listId, userId);
    }

    public List<Task> searchTasks(String query, Long taskListId, Long userId) {
        // jeżeli query jest null, dajemy '', żeby zapytanie nie wywaliło błędu
        if (query == null) {
            query = "";
        }
        return taskRepository.searchTasks(query, taskListId, userId);
    }

    public Task getTaskById(Long taskId) {
        // w JPA standardowa metoda to findById
        return taskRepository.findById(taskId).orElse(null);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, String status) {
        // można zrobić przez custom query
        taskRepository.updateTaskStatus(taskId, status);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
