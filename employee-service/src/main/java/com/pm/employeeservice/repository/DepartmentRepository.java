package com.pm.employeeservice.repository;

import com.pm.employeeservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department,Integer> {

    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.department_code = :department_code")
    boolean existsByDepartment_code(@Param("department_code") String department_code);
}
