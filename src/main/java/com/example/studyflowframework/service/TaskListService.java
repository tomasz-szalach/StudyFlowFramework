package com.example.studyflowframework.service;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskListService {

    private final TaskListRepository taskListRepository;

    @Autowired
    public TaskListService(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    @Transactional
    public String addTaskList(String name, Long userId) {
        TaskList tl = new TaskList(name, userId);
        taskListRepository.save(tl);
        return "Lista zadań została dodana pomyślnie.";
    }

    public List<TaskList> getAllTaskLists(Long userId) {
        return taskListRepository.findByUserId(userId);
    }

    @Transactional
    public String updateTaskListName(Long listId, String newName) {
        taskListRepository.updateTaskListName(listId, newName);
        return "Nazwa listy zadań została zaktualizowana pomyślnie.";
    }

    @Transactional
    public String deleteTaskList(Long listId) {
        taskListRepository.deleteById(listId);
        return "Lista zadań została usunięta pomyślnie.";
    }
}
