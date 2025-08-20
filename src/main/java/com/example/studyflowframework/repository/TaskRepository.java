package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.Task;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /* Standardowe metody zwracają TYLKO nieusunięte rekordy (działa @Where) */
    List<Task> findByReporterId(Long reporterId);

    List<Task> findByTaskListIdAndReporterIdOrderByDueDateAsc(Long taskListId,
                                                              Long reporterId);

    @Query("""
        SELECT t FROM Task t
        WHERE t.taskListId = :taskListId
          AND t.reporterId = :reporterId
          AND (
              :query = ''
              OR LOWER(t.name)        LIKE LOWER(CONCAT('%',:query,'%'))
              OR LOWER(t.description) LIKE LOWER(CONCAT('%',:query,'%'))
          )
        ORDER BY t.dueDate
    """)
    List<Task> searchTasks(@Param("query")      String query,
                           @Param("taskListId") Long   taskListId,
                           @Param("reporterId") Long   reporterId);

    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.statusId = :statusId WHERE t.id = :taskId")
    void updateTaskStatus(@Param("taskId") Long taskId,
                          @Param("statusId") Short statusId);

    /* ====== KOSZ / PRZYWRACANIE / HARD DELETE ====== */

    /** Lista zadań w koszu (pomija @Where) */
    @Query(value = """
        SELECT * FROM tasks
        WHERE deleted_at IS NOT NULL
          AND (:reporterId IS NULL OR reporter_id = :reporterId)
          AND (:taskListId IS NULL OR task_list_id = :taskListId)
        ORDER BY deleted_at DESC
        """, nativeQuery = true)
    List<Task> listDeleted(@Param("reporterId") Long reporterId,
                           @Param("taskListId") Long taskListId);

    /** Przywrócenie z kosza */
    @Transactional
    @Modifying
    @Query(value = "UPDATE tasks SET deleted_at = NULL WHERE task_id = :id", nativeQuery = true)
    void restore(@Param("id") Long id);

    /** Twarde usunięcie (uwaga: usuwa rekord permanentnie) */
    @Transactional
    @Modifying
    @Query("DELETE FROM Task t WHERE t.id = :id")
    void hardDelete(@Param("id") Long id);
}
