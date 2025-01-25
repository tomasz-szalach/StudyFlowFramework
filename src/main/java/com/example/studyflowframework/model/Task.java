package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Model reprezentujący zadanie.
 */
@Entity
@Table(name = "tasks")
@Schema(description = "Model reprezentujący zadanie")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator zadania", example = "1")
    private Long id;

    @Schema(description = "Nazwa zadania", example = "Przygotować prezentację")
    private String name;

    @Schema(description = "Opis zadania", example = "Przygotować prezentację na temat Spring Boot")
    private String description;

    @Schema(description = "Data wykonania zadania", example = "2025-02-15")
    private String dueDate;

    @Schema(description = "Status zadania", example = "todo")
    private String status;

    @Schema(description = "ID użytkownika, do którego należy zadanie", example = "1")
    private Long userId;

    @Schema(description = "ID listy zadań, do której należy zadanie", example = "2")
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

    // Gettery i Settery

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
