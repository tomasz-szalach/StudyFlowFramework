package com.example.studyflowframework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Schema(description = "Model reprezentujący użytkownika")
public class User {

    /* ---------- kolumny ---------- */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", length = 64)
    private String firstName;

    @Column(name = "last_name",  length = 64)
    private String lastName;

    /* === FK → user_roles.role_id === */
    @ManyToOne(fetch = FetchType.LAZY)               // LAZY = bez dodatkowych JOIN‑ów
    @JoinColumn(name = "role_id", nullable = false)  // NOT NULL
    private UserRole role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* ---------- konstruktory ---------- */
    public User() {}                                 // JPA

    public User(String email, String password, UserRole role,
                String firstName, String lastName) {
        this.email      = email;
        this.password   = password;
        this.role       = role;
        this.firstName  = firstName;
        this.lastName   = lastName;
    }

    /* ---------- get / set ---------- */
    public Long          getId()        { return id; }
    public String        getEmail()     { return email; }
    public String        getPassword()  { return password; }
    public String        getFirstName() { return firstName; }
    public String        getLastName()  { return lastName; }
    public UserRole      getRole()      { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setEmail(String e)      { this.email = e; }
    public void setPassword(String p)   { this.password = p; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public void setLastName(String ln)  { this.lastName = ln; }
    public void setRole(UserRole r)     { this.role = r; }
}
