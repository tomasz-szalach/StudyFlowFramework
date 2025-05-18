package com.example.studyflowframework.service;

import com.example.studyflowframework.config.RedisPublisher;
import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serwis do obsługi użytkowników:
 *  • bieżący user, walidacja hasła
 *  • zmiana hasła, zmiana maila
 *  • publikacja kodów weryfikacyjnych (Redis)
 *  • podstawowe CRUD-y
 */
@Service
public class UserService {

    private final UserRepository  repo;
    private final PasswordEncoder encoder;
    private final RedisPublisher  publisher;

    /* --------------------------------------------------------
       KONSTRUKTOR
       -------------------------------------------------------- */
    @Autowired
    public UserService(UserRepository repo,
                       PasswordEncoder encoder,
                       RedisPublisher publisher) {
        this.repo      = repo;
        this.encoder   = encoder;
        this.publisher = publisher;
    }

    /* --------------------------------------------------------
       BIEŻĄCY UŻYTKOWNIK
       -------------------------------------------------------- */
    public User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return repo.findByEmail(email).orElseThrow();   // 401 gdy brak
    }

    public boolean passwordMatches(User user, String rawPwd) {
        return encoder.matches(rawPwd, user.getPassword());
    }

    /* --------------------------------------------------------
       ZMIANA HASŁA / MAILA
       -------------------------------------------------------- */
    @Transactional
    public void updatePassword(Long userId, String rawPwd) {
        repo.updatePassword(userId, encoder.encode(rawPwd));
    }

    @Transactional
    public void changeEmail(Long uid, String newEmail) {
        User u = repo.findById(uid).orElseThrow();
        u.setEmail(newEmail);
        repo.save(u);
    }

    /* --------------------------------------------------------
       PUBLIKACJA KODÓW (Redis → e-mail)
       -------------------------------------------------------- */
    /** Kod MFA / rejestracja – stary wariant (email|code). */
    public void publishEmailVerification(String email, String code) {
        publisher.publishUserRegistration(email + "|" + code);
    }

    /** Kod do zmiany adresu (emailChange|email|code). */
    public void publishEmailChange(String email, String code) {
        publisher.publishUserRegistration("emailChange|" + email + "|" + code);
    }

    /* --------------------------------------------------------
       POZOSTAŁE OPERACJE
       -------------------------------------------------------- */
    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public User getUserByEmailOrThrow(String email) throws Exception {
        return repo.findByEmail(email)
                .orElseThrow(() -> new Exception(
                        "user not found for email " + email));
    }

    @Transactional
    public void saveUser(User user) {
        repo.save(user);
    }

    public boolean ifContainsEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Transactional
    public void deleteUser(Long id) {
        repo.deleteById(id);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }
}
