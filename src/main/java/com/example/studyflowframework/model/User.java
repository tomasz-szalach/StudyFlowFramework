package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Model reprezentujący użytkownika. */
@Entity
@Table(name = "users")
@Schema(description = "Model reprezentujący użytkownika")
public class User {

    /* ───────── pola ───────── */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator użytkownika", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Adres e-mail użytkownika", example = "user@example.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Hasło (hash) użytkownika")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Rola użytkownika", example = "USER")
    private String role;

    /*  NOWE  */
    @CreationTimestamp                      // automatycznie wstawia bieżący czas
    @Column(name = "created_at",
            nullable = false, updatable = false)
    @Schema(description = "Data dołączenia", example = "2025-05-18T15:12:07")
    private LocalDateTime createdAt;

    /* ───────── konstruktory ───────── */
    public User() {}
    public User(String email, String password, String role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /* ───────── get / set ───────── */
    public Long          getId()        { return id; }
    public String        getEmail()     { return email; }
    public String        getPassword()  { return password; }
    public String        getRole()      { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id)           { this.id = id; }
    public void setEmail(String email)   { this.email = email; }
    public void setPassword(String pass) { this.password = pass; }
    public void setRole(String role)     { this.role = role; }
}
