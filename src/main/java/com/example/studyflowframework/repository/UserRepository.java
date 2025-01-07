package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Pobierz użytkownika na podstawie email
     */
    Optional<User> findByEmail(String email);

    /**
     * Czy istnieje użytkownik o podanym emailu
     */
    boolean existsByEmail(String email);

    /**
     * Aktualizacja hasła użytkownika
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :newPass WHERE u.id = :id")
    void updatePassword(@Param("id") Long id, @Param("newPass") String newPass);

    /**
     * Usuwanie użytkownika -> wbudowana metoda deleteById(id).
     * Pobieranie wszystkich użytkowników -> findAll().
     */
}
