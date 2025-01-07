package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Pobieranie zadań dla danego użytkownika
     */
    List<Task> findByUserId(Long userId);

    /**
     * Pobieranie zadań dla danej listy i użytkownika
     */
    List<Task> findByTaskListIdAndUserId(Long taskListId, Long userId);

    /**
     * Wyszukiwanie zadań po słowie kluczowym (query) w nazwie lub opisie,
     * w zadanej liście i przypisanych do użytkownika.
     * Używamy składni JPQL i funkcji LOWER do "case-insensitive".
     */
    @Query("""
        SELECT t FROM Task t
        WHERE 
          (:query = '' OR 
             LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))
          )
          AND t.taskListId = :taskListId
          AND t.userId = :userId
    """)
    List<Task> searchTasks(
            @Param("query") String query,
            @Param("taskListId") Long taskListId,
            @Param("userId") Long userId
    );

    /**
     * Aktualizacja statusu zadania
     */
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :taskId")
    void updateTaskStatus(@Param("taskId") Long taskId, @Param("status") String status);

    /**
     * Usuwanie zadania -> wbudowana metoda deleteById(taskId).
     * Nie musimy pisać własnej metody, wystarczy:
     *   taskRepository.deleteById(taskId)
     */
}
