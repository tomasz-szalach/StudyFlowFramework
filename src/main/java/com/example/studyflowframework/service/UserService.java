package com.example.studyflowframework.service;

import com.example.studyflowframework.config.RedisPublisher;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.model.UserRole;
import com.example.studyflowframework.repository.UserRepository;
import com.example.studyflowframework.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository    repo;
    private final UserRoleRepository roleRepo;
    private final PasswordEncoder   encoder;
    private final RedisPublisher    publisher;

    @Autowired
    public UserService(UserRepository repo,
                       UserRoleRepository roleRepo,
                       PasswordEncoder encoder,
                       RedisPublisher publisher) {
        this.repo      = repo;
        this.roleRepo  = roleRepo;
        this.encoder   = encoder;
        this.publisher = publisher;
    }

    /* ---------- bieżący użytkownik ---------- */
    public User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return repo.findByEmail(email).orElseThrow();
    }

    /* ---------- hasła ---------- */
    public boolean passwordMatches(User u, String raw) {
        return encoder.matches(raw, u.getPassword());
    }

    /* ---------- rejestracja ---------- */
    @Transactional
    public User register(String email, String rawPwd,
                         String firstName, String lastName) {

        if (repo.existsByEmail(email))
            throw new RuntimeException("email‑taken");

        UserRole role = roleRepo.findByRoleCode("USER")
                .orElseThrow();

        User u = new User(
                email,
                encoder.encode(rawPwd),
                role,                 // przekazujemy cały obiekt
                firstName,
                lastName
        );
        return repo.save(u);
    }

    /* ---------- zmiany ---------- */
    @Transactional
    public void updatePassword(Long uid, String rawPwd) {
        repo.updatePassword(uid, encoder.encode(rawPwd));
    }

    @Transactional
    public void changeEmail(Long uid, String newEmail) {
        User u = repo.findById(uid).orElseThrow();
        u.setEmail(newEmail);
        repo.save(u);
    }

    /* ---------- Redis ---------- */
    public void publishEmailVerification(String email, String code) {
        publisher.publishUserRegistration(email + "|" + code);
    }
    public void publishEmailChange(String email, String code) {
        publisher.publishUserRegistration("emailChange|" + email + "|" + code);
    }

    /* ---------- util ---------- */
    public boolean ifContainsEmail(String email){ return repo.existsByEmail(email); }
    public List<User> getAllUsers(){ return repo.findAll(); }
}
