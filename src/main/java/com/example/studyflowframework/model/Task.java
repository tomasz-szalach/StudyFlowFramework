package com.example.studyflowframework.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String dueDate;
    private String status;

    private Long userId;
    private Long taskListId;

    public Task() {
    }

    public Task(String name, String description, String dueDate, String status, Long userId, Long taskListId) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.userId = userId;
        this.taskListId = taskListId;
    }

    // gettery / settery
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getDueDate() {
        return dueDate;
    }
    public String getStatus() {
        return status;
    }
    public Long getUserId() {
        return userId;
    }
    public Long getTaskListId() {
        return taskListId;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public void setTaskListId(Long taskListId) {
        this.taskListId = taskListId;
    }
}
