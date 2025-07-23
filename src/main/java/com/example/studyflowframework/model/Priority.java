package com.example.studyflowframework.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "priorities")
@Schema(description = "Słownik priorytetów")
public class Priority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "priority_id")
    private Short id;

    @Column(name = "priority_code", unique = true, nullable = false, length = 10)
    private String code;                 // LOW | MEDIUM | HIGH

    @Column(name = "priority_name", nullable = false, length = 32)
    private String name;                 // „Niski” …

    /* --- get / set --- */
    public Short  getId()   { return id;   }
    public String getCode() { return code; }
    public String getName() { return name; }

    public void setId(Short id)       { this.id = id; }
    public void setCode(String code)  { this.code = code; }
    public void setName(String name)  { this.name = name; }
}
