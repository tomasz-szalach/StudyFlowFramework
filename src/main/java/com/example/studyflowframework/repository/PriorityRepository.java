/* src/main/java/com/example/studyflowframework/repository/PriorityRepository.java */
package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriorityRepository extends JpaRepository<Priority, Short> {

Optional<Priority> findByCode(String code);
}
