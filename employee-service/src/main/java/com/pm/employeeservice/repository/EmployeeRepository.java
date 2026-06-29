package com.pm.employeeservice.repository;

import com.pm.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,UUID> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Optional<Employee> findByName(String name);

    Optional<Employee> findByEmail(String email);

     Optional<Employee> findById(UUID id);

    @Modifying
    @Query(value = "DELETE FROM employee WHERE enabled = false AND id IN " +
            "(SELECT employee_id FROM verification_tokens WHERE expiry_date < :now AND warning_sent = true)",
            nativeQuery = true)
    int bulkPurgeExpiredAccounts(@Param("now") LocalDateTime now);
}
