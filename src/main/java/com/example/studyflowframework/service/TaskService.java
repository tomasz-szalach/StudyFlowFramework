package com.example.studyflowframework.service;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serwis do zarządzania encjami Task (dodawanie, pobieranie, wyszukiwanie, toggle, usuwanie).
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public String addTask(String name, String description, String dueDate, String status,
                          Long taskListId, Long userId) {
        Task task = new Task(name, description, dueDate, status, userId, taskListId);
        taskRepository.save(task);
        return "Zadanie zostało dodane pomyślnie.";
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    /**
     * Teraz korzystamy z findByTaskListIdAndUserIdOrderByDueDateAsc
     * (zamiast starego findByTaskListIdAndUserId).
     */
    public List<Task> getTasksByTaskList(Long listId, Long userId) {
        return taskRepository.findByTaskListIdAndUserIdOrderByDueDateAsc(listId, userId);
    }

    /**
     * Wyszukiwanie w obrębie listy z sortowaniem rosnąco po dueDate.
     */
    public List<Task> searchTasksInList(Long listId, Long userId, String query) {
        if (query == null) {
            query = "";
        }
        return taskRepository.searchTasks(query, listId, userId);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, String status) {
        taskRepository.updateTaskStatus(taskId, status);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
