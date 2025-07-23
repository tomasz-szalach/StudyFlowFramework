package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Model reprezentujący listę zadań.
 * Po refaktorze DB: list_id, list_name, owner_id.
 * Zachowujemy stare nazewnictwo pól (id, name, userId), żeby nie przepisywać całej aplikacji.
 */
@Entity
@Table(name = "task_lists")
@Schema(description = "Model reprezentujący listę zadań")
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Long id;

    @Column(name = "list_name")
    private String name;

    /** FK → users.user_id. Stary kod używał nazwy userId, więc ją zostawiamy. */
    @Column(name = "owner_id")
    private Long userId;

    /* ===== Konstruktory ===== */
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

    /* ===== Get / Set ===== */
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** zgodność z wcześniejszym kodem */
    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** zgodność z wcześniejszym kodem */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
