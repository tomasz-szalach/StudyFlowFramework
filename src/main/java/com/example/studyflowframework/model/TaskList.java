package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_lists")
@Schema(description = "Model reprezentujący listę zadań")
@SQLDelete(sql = "UPDATE task_lists SET deleted_at = CURRENT_TIMESTAMP WHERE task_list_id = ?")
@Where(clause = "deleted_at IS NULL")
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_list_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "owner_id")
    private Long userId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public TaskList() {}
    public TaskList(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }
    public TaskList(Long id, String name, Long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getUserId() { return userId; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
