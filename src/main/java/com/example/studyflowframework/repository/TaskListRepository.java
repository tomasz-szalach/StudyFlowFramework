package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {

    /**
     * Pobierz wszystkie listy zadań dla danego użytkownika
     */
    List<TaskList> findByUserId(Long userId);

    /**
     * Aktualizacja nazwy listy zadań
     */
    @Transactional
    @Modifying
    @Query("UPDATE TaskList tl SET tl.name = :newName WHERE tl.id = :listId")
    void updateTaskListName(@Param("listId") Long listId, @Param("newName") String newName);

    /**
     * Usuwanie listy zadań -> wbudowana metoda deleteById(listId).
     */
}
