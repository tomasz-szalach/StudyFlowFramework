package com.example.studyflowframework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Prosta konfiguracja z InMemoryUserDetailsManager
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/login", "/css/**", "/js/**", "/img/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true) // lub cokolwiek
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Definiujemy użytkownika w pamięci (bez bazy).
     */
    @Bean
    public UserDetailsService userDetailsService() {

        // Tworzysz np. jednego usera: admin@studyflow.pl / password123 (no-op)
        UserDetails admin = User.withUsername("admin@studyflow.pl")
                .password("{noop}password123") // brak hashowania
                .roles("ADMIN") // lub "USER"
                .build();

        // Możesz ewentualnie dodać drugiego usera
        // UserDetails user = User.withUsername("user@studyflow.pl")
        //         .password("{noop}user123")
        //         .roles("USER")
        //         .build();

        return new InMemoryUserDetailsManager(admin
                //, user
        );
    }
}
