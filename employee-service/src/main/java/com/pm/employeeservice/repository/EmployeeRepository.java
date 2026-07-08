package com.pm.employeeservice.repository;

import com.pm.employeeservice.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,UUID> {

    boolean existsByEmailAndEnabled(String email, boolean enabled);

    boolean existsByEmailAndIdNot(String email, UUID id);


    Optional<Employee> findByEmail(String email);

    Employee findEmployeeByEmail(String email);

    Page<Employee> findByEnabledTrue(Pageable pageable);

}
