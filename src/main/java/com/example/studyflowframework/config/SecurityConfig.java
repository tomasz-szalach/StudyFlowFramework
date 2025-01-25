package com.example.studyflowframework.config;

import com.example.studyflowframework.service.CustomUserDetailsService;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
// Możesz użyć new BCryptPasswordEncoder() w produkcji
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Konfiguracja Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * NoOpPasswordEncoder - do testów
     * (W produkcji użyj np. BCryptPasswordEncoder)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Bean CustomUserDetailsService z wstrzykniętym repo i encoderem
     */
    @Bean
    public CustomUserDetailsService customUserDetailsService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new CustomUserDetailsService(userRepository, passwordEncoder);
    }

    /**
     * AuthenticationManager z userDetailsService i encoderem
     */
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

    /**
     * Główna konfiguracja łańcucha filtrów i logowania
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Ścieżki publiczne
                        .requestMatchers("/error", "/login", "/css/**", "/js/**", "/img/**", "/registrationUser")
                        .permitAll()
                        // Reszta wymaga autentykacji
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // Po błędnych danych -> ?error
                        .failureUrl("/login?error")
                        // Po udanym logowaniu -> /home
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                );
        return http.build();
    }
}
