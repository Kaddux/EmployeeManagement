package com.pm.employeeservice.repository;

import com.pm.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,UUID> {
    boolean existsByEmail(String email, boolean enabled);

    boolean existsByEmailAndIdNot(String email, UUID id);


    Optional<Employee> findByEmail(String email);

    Employee findEmployeeByEmail(String email);




    @Query(value = "SELECT * FROM employee ORDER BY name OFFSET :offset LIMIT :limit",
            nativeQuery = true)
    List<Employee> findEmployeesPaginated(@Param("offset") int offset,
                                          @Param("limit") int limit);
}
