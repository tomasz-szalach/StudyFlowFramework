package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.Task;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /* reporterId = dawne userId */
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

    /* UPDATE – ustawiamy statusId (FK) */
    @Transactional @Modifying
    @Query("UPDATE Task t SET t.statusId = :statusId WHERE t.id = :taskId")
    void updateTaskStatus(@Param("taskId") Long  taskId,
                          @Param("statusId") Short statusId);
}
