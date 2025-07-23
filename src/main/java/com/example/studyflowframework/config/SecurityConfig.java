package com.example.studyflowframework.config;

import com.example.studyflowframework.security.CustomAuthenticationSuccessHandler;
import com.example.studyflowframework.service.CustomUserDetailsService;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Konfiguracja Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /* ───────────────── PasswordEncoder ───────────────── */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // ⚠ produkcyjnie użyj BCryptPasswordEncoder
        return NoOpPasswordEncoder.getInstance();
    }

    /* ───────────────── CustomUserDetailsService ───────────────── */
    @Bean
    public CustomUserDetailsService customUserDetailsService(UserRepository userRepository) {
        // konstruktor ma 1 param
        return new CustomUserDetailsService(userRepository);
    }

    /* ───────────────── AuthenticationManager ───────────────── */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder
    ) throws Exception {

        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);

        return authBuilder.build();
    }

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /* ───────────────── Główny filtr ───────────────── */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/error",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/registrationUser",
                                "/verify-mfa"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                );
        return http.build();
    }
}
