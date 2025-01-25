package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Model reprezentujący użytkownika.
 */
@Entity
@Table(name = "users")
@Schema(description = "Model reprezentujący użytkownika")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator użytkownika", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Adres e-mail użytkownika", example = "user@example.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Hasło użytkownika", example = "password123")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Rola użytkownika", example = "USER")
    private String role;

    public User() {
    }

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Gettery i Settery

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
