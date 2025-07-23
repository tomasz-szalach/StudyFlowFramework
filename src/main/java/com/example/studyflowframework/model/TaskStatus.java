package com.example.studyflowframework.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "task_statuses")
@Schema(description = "Słownik statusów zadań")
public class TaskStatus {

    /* ────── pola ────── */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Short id;

    @Column(name = "status_code", unique = true, nullable = false, length = 20)
    private String code;      // TODO | IN_PROGRESS | COMPLETED …

    @Column(name = "status_name", nullable = false, length = 64)
    private String name;      // „Do zrobienia”, „W trakcie”, „Zakończone”

    /* ────── get / set ────── */
    public Short  getId()   { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }

    public void setId(Short id)        { this.id = id; }
    public void setCode(String code)   { this.code = code; }
    public void setName(String name)   { this.name = name; }
}
