package com.pm.employeeservice.RepositoryTests;

import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DepartmentRepositoryTests {

    @Autowired
    TestEntityManager em;
    @Autowired DepartmentRepository repository;

    @BeforeEach
    void setUp() {
        Department dept = new Department();
        dept.setDepartment_name("Engineering");
        dept.setDepartment_code("ENG");
        em.persist(dept);
    }

    @Test
    void existsByDepartmentCode_whenExists_returnsTrue() {
        assertTrue(repository.existsByDepartment_code("ENG"));
    }

    @Test
    void existsByDepartmentCode_whenNotExists_returnsFalse() {
        assertFalse(repository.existsByDepartment_code("NONEXISTENT"));
    }

    @Test
    void findById_whenExists_returnsDepartment() {
        var dept = repository.findAll().get(0);
        assertTrue(repository.findById(dept.getDepartment_id()).isPresent());
    }

    @Test
    void deleteById_removesDepartment() {
        var dept = repository.findAll().get(0);
        repository.deleteById(dept.getDepartment_id());
        assertTrue(repository.findById(dept.getDepartment_id()).isEmpty());
    }
}
