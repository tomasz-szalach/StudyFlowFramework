package com.example.studyflowframework.service;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.repository.TaskListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskListService {

    private final TaskListRepository repo;

    @Autowired
    public TaskListService(TaskListRepository repo) {
        this.repo = repo;
    }

    /* ───────── dotychczasowe metody ───────── */

    @Transactional
    public String addTaskList(String name, Long userId) {
        repo.save(new TaskList(name, userId));
        return "Lista zadań została dodana pomyślnie.";
    }

    public List<TaskList> getAllTaskLists(Long userId) {
        return repo.findByUserId(userId);
    }

    @Transactional
    public String updateTaskListName(Long id, String newName) {
        repo.updateTaskListName(id, newName);
        return "Nazwa listy zadań została zaktualizowana pomyślnie.";
    }

    @Transactional
    public String deleteTaskList(Long id) {
        repo.deleteById(id);
        return "Lista zadań została usunięta pomyślnie.";
    }

    /* ───────── NOWE – używane przez REST ───────── */

    /** Zwraca zaktualizowaną encję lub EntityNotFoundException */
    @Transactional
    public TaskList rename(Long id, String newName) {
        TaskList list = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskList " + id + " not found"));
        list.setName(newName);
        return repo.save(list);
    }

    /** Usuwa listę lub rzuca EntityNotFoundException */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new EntityNotFoundException("TaskList " + id + " not found");
        repo.deleteById(id);
    }
}
