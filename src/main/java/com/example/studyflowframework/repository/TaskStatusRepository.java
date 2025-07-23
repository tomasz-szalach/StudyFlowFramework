package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Short> {

    /** Pobiera status na podstawie pola code (np. "TODO", "COMPLETED") */
    Optional<TaskStatus> findByCode(String code);
}
