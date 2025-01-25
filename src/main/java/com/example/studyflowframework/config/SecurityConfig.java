package com.example.studyflowframework.config;

import com.example.studyflowframework.service.CustomUserDetailsService;
import com.example.studyflowframework.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
// lub new BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean PasswordEncoder - do testów NoOp
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Tworzymy CustomUserDetailsService w postaci beana -
     * wstrzykujemy UserRepository i PasswordEncoder w parametrach metody.
     */
    @Bean
    public CustomUserDetailsService customUserDetailsService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new CustomUserDetailsService(userRepository, passwordEncoder);
    }

    /**
     * Składamy AuthenticationManager - używa beana customUserDetailsService i passwordEncoder.
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
     * Filtr łańcuchowy - reguły autoryzacji i logowania.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/login", "/css/**", "/js/**", "/img/**", "/registrationUser")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                );
        return http.build();
    }
}
