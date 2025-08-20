package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.TaskList;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {

    /* Zwraca tylko aktywne listy (deleted_at IS NULL – przez @Where) */
    List<TaskList> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE TaskList tl SET tl.name = :newName WHERE tl.id = :listId")
    void updateTaskListName(@Param("listId") Long listId,
                            @Param("newName") String newName);

    /* ====== KOSZ / PRZYWRACANIE / HARD DELETE ====== */

    @Query(value = """
        SELECT * FROM task_lists
        WHERE deleted_at IS NOT NULL
          AND (:ownerId IS NULL OR owner_id = :ownerId)
        ORDER BY deleted_at DESC
        """, nativeQuery = true)
    List<TaskList> listDeleted(@Param("ownerId") Long ownerId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE task_lists SET deleted_at = NULL WHERE task_list_id = :id", nativeQuery = true)
    void restore(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM TaskList tl WHERE tl.id = :id")
    void hardDelete(@Param("id") Long id);
}
