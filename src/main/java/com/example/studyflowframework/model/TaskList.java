package com.example.studyflowframework.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_lists")
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Prosta opcja: trzymasz userId jako Long
    // (W starym PHP: (name, user_id))
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

    // gettery / settery
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
