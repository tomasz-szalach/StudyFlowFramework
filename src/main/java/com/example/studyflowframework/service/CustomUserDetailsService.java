package com.example.studyflowframework.service;

import com.example.studyflowframework.model.User;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // jeśli chcesz hashować przy rejestracji

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Główna metoda wywoływana przez Spring Security podczas logowania:
     * - szuka usera w bazie po emailu
     * - tworzy obiekt UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }

        User userEntity = opt.get();

        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())  // {noop}password123
                .roles(userEntity.getRole())         // ADMIN => ROLE_ADMIN
                .build();
    }

}
