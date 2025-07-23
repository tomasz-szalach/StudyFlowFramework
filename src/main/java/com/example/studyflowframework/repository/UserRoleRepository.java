/* UserRoleRepository.java */
package com.example.studyflowframework.repository;

import com.example.studyflowframework.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Short> {
    Optional<UserRole> findByRoleCode(String roleCode);
}
