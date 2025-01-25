package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.Task;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repozytorium do zarządzania encjami Task.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Pobieranie zadań danego usera (bez listy).
     * (Jeśli chcesz też posortować globalnie, można dopisać order by).
     */
    List<Task> findByUserId(Long userId);

    /**
     * Pobieranie zadań w obrębie konkretnej listy i użytkownika,
     * POSORTOWANE rosnąco po dueDate.
     *
     * W Spring Data można to osiągnąć przez nazewnictwo:
     * findByTaskListIdAndUserIdOrderByDueDateAsc
     */
    List<Task> findByTaskListIdAndUserIdOrderByDueDateAsc(Long taskListId, Long userId);

    /**
     * Wyszukiwanie zadań w obrębie danej listy i użytkownika
     * po fragmencie (query) w name lub description,
     * z uwzględnieniem sortowania rosnąco po dueDate.
     */
    @Query("""
        SELECT t
        FROM Task t
        WHERE t.taskListId = :taskListId
          AND t.userId = :userId
          AND (
            :query = ''
            OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY t.dueDate ASC
    """)
    List<Task> searchTasks(
            @Param("query") String query,
            @Param("taskListId") Long taskListId,
            @Param("userId") Long userId
    );

    /**
     * Aktualizacja statusu zadania (np. toggle).
     */
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :taskId")
    void updateTaskStatus(@Param("taskId") Long taskId, @Param("status") String status);

    /**
     * Usuwanie zadania -> wbudowana metoda deleteById(taskId).
     * Nie musimy pisać custom.
     */
}
