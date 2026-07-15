package com.pm.employeeservice.repository;

import com.pm.employeeservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Integer> {

    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.department_code = :department_code")
    boolean existsByDepartment_code(@Param("department_code") String department_code);

    @Query("SELECT d FROM Department d WHERE d.department_id = :id")
    Optional<Department> findByDepartment_id(@Param("id") BigInteger id);
}
