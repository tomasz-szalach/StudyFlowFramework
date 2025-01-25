package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Model reprezentujący listę zadań.
 */
@Entity
@Table(name = "task_lists")
@Schema(description = "Model reprezentujący listę zadań")
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator listy zadań", example = "1")
    private Long id;

    @Schema(description = "Nazwa listy zadań", example = "Prace domowe")
    private String name;

    @Schema(description = "ID użytkownika, do którego należy lista zadań", example = "1")
    private Long userId;

    public TaskList() {
    }

    public TaskList(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    public TaskList(Long id, String name, Long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    // Gettery i Settery

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
