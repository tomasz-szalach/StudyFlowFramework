package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/** Model reprezentujący zadanie. */
@Entity
@Table(name = "tasks")
@Schema(description = "Model reprezentujący zadanie")
public class Task {

    /* ---------- PRIORYTET ---------- */
    public enum Priority {
        @Schema(description = "Priorytet niski")   LOW,
        @Schema(description = "Priorytet średni") MEDIUM,
        @Schema(description = "Priorytet wysoki") HIGH
    }

    /* ---------- POLA ---------- */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator zadania", example = "1")
    private Long id;

    @Schema(description = "Nazwa zadania", example = "Prezentacja z Spring Boot")
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    @Schema(description = "Opis zadania (max 1000 znaków)")
    private String description;

    @Schema(description = "Data wykonania zadania", example = "2025-02-15")
    private String dueDate;

    /** `todo` | `completed` */
    @Schema(description = "Status zadania", example = "todo")
    private String status = "todo";

    @Schema(description = "ID użytkownika", example = "1")
    private Long userId;

    @Schema(description = "ID listy zadań", example = "2")
    private Long taskListId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Priorytet zadania", example = "LOW")
    private Priority priority = Priority.LOW;

    /* ---------- konstruktory ---------- */
    public Task() {}

    public Task(String name, String description, String dueDate,
                String status, Long userId, Long taskListId,
                Priority priority) {
        this.name        = name;
        this.description = description;
        this.dueDate     = dueDate;
        this.status      = status;
        this.userId      = userId;
        this.taskListId  = taskListId;
        this.priority    = priority == null ? Priority.LOW : priority;
    }

    /* stary konstruktor – zostawiony dla kompatybilności */
    public Task(String name, String description, String dueDate,
                String status, Long userId, Long taskListId) {
        this(name, description, dueDate, status, userId, taskListId, Priority.LOW);
    }

    /* ---------- Gettery / Settery ---------- */
    public Long      getId()          { return id; }
    public String    getName()        { return name; }
    public String    getDescription() { return description; }
    public String    getDueDate()     { return dueDate; }
    public String    getStatus()      { return status; }
    public Long      getUserId()      { return userId; }
    public Long      getTaskListId()  { return taskListId; }
    public Priority  getPriority()    { return priority; }

    public void setId(Long id)                  { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String d){ this.description =
            d != null && d.length() > 1000 ? d.substring(0,1000) : d; }
    public void setDueDate(String dueDate)      { this.dueDate = dueDate; }
    public void setStatus(String status)        { this.status = status; }
    public void setUserId(Long userId)          { this.userId = userId; }
    public void setTaskListId(Long taskListId)  { this.taskListId = taskListId; }
    public void setPriority(Priority priority)  {
        this.priority = priority == null ? Priority.LOW : priority;
    }
}
