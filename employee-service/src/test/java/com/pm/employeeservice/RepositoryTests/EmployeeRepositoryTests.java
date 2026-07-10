package com.pm.employeeservice.RepositoryTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTests {

    @Autowired
    TestEntityManager em;
    @Autowired EmployeeRepository repository;

    private Department dept;
    private Employee enabledEmp;
    private Employee disabledEmp;

    @BeforeEach
    void setUp() {
        dept = new Department();
        dept.setDepartment_name("Engineering");
        dept.setDepartment_code("ENG");
        em.persist(dept);

        enabledEmp = new Employee();
        enabledEmp.setName("John Enabled");
        enabledEmp.setEmail("enabled@example.com");
        enabledEmp.setPassword("encoded");
        enabledEmp.setRole(Role.ROLE_EMPLOYEE);
        enabledEmp.setEnabled(true);
        enabledEmp.setDate_of_birth(LocalDate.of(1990, 1, 1));
        enabledEmp.setDepartment(dept);
        em.persist(enabledEmp);

        disabledEmp = new Employee();
        disabledEmp.setName("Jane Disabled");
        disabledEmp.setEmail("disabled@example.com");
        disabledEmp.setPassword("encoded");
        disabledEmp.setRole(Role.ROLE_EMPLOYEE);
        disabledEmp.setEnabled(false);
        disabledEmp.setDate_of_birth(LocalDate.of(1992, 5, 10));
        disabledEmp.setDepartment(dept);
        em.persist(disabledEmp);
    }

    @Test
    void findByEmail_whenExists_returnsEmployee() {
        Optional<Employee> found = repository.findByEmail("enabled@example.com");
        assertTrue(found.isPresent());
        assertEquals("John Enabled", found.get().getName());
    }

    @Test
    void findByEmail_whenNotExists_returnsEmpty() {
        assertTrue(repository.findByEmail("nobody@example.com").isEmpty());
    }

    @Test
    void existsByEmailAndEnabled_returnsTrue() {
        assertTrue(repository.existsByEmailAndEnabled("enabled@example.com", true));
        assertTrue(repository.existsByEmailAndEnabled("disabled@example.com", false));
    }

    @Test
    void existsByEmailAndEnabled_returnsFalse() {
        assertFalse(repository.existsByEmailAndEnabled("disabled@example.com", true));
        assertFalse(repository.existsByEmailAndEnabled("enabled@example.com", false));
    }

    @Test
    void findByEnabledTrue_returnsOnlyEnabled() {
        Page<Employee> page = repository.findByEnabledTrue(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("enabled@example.com", page.getContent().get(0).getEmail());
    }

    @Test
    void existsByEmailAndIdNot_returnsTrue() {
        UUID otherId = UUID.randomUUID();
        assertTrue(repository.existsByEmailAndIdNot("enabled@example.com", otherId));
    }

    @Test
    void existsByEmailAndIdNot_whenSameId_returnsFalse() {
        assertFalse(repository.existsByEmailAndIdNot("enabled@example.com", enabledEmp.getId()));
    }

    @Test
    void findEmployeeByEmail_returnsEmployee() {
        Employee found = repository.findEmployeeByEmail("enabled@example.com");
        assertNotNull(found);
        assertEquals("John Enabled", found.getName());
    }

    @Test
    void deleteById_removesEmployee() {
        repository.deleteById(enabledEmp.getId());
        assertTrue(repository.findById(enabledEmp.getId()).isEmpty());
    }
}
