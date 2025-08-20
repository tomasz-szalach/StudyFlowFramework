package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Short> {

    /** Szuka roli po kodzie, np. "USER", "ADMIN". */
    Optional<UserRole> findByRoleCode(String roleCode);
}
