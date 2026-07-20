package com.pm.employeeservice.RepositoryTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.dto.ExpiredTokenProjection;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.VerificationToken;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VerificationTokenRepositoryTests {

    @Autowired
    TestEntityManager em;
    @Autowired VerificationTokenRepository repository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        Department dept = new Department();
        dept.setDepartment_name("Engineering");
        dept.setDepartment_code("ENG");
        em.persist(dept);

        employee = new Employee();
        employee.setName("John");
        employee.setEmail("john@example.com");
        employee.setPassword("encoded");
        employee.setRole(Role.ROLE_EMPLOYEE);
        employee.setEnabled(false);
        employee.setDate_of_birth(LocalDate.of(1990, 1, 1));
        employee.setDepartment(dept);
        em.persist(employee);
    }

    private VerificationToken createToken(String tokenStr, LocalDateTime expiry, boolean warningSent) {
        VerificationToken t = new VerificationToken();
        t.setToken(tokenStr);
        t.setEmployee(employee);
        t.setExpiryDate(expiry);
        t.setWarning_sent(warningSent);
        t.setCreatedAt(Instant.now());
        em.persist(t);
        return t;
    }

    @Test
    void findByToken_whenExists_returnsToken() {
        createToken("valid-token", LocalDateTime.now().plusDays(1), false);
        Optional<VerificationToken> found = repository.findByToken("valid-token");
        assertTrue(found.isPresent());
    }

    @Test
    void findByToken_whenNotExists_returnsEmpty() {
        assertTrue(repository.findByToken("nonexistent").isEmpty());
    }

    @Test
    void findExpiredTokensForWarning_returnsOnlyUnwarnedExpired() {
        createToken("expired-unwarned", LocalDateTime.now().minusHours(1), false);
        createToken("expired-warned", LocalDateTime.now().minusHours(1), true);
        createToken("not-expired", LocalDateTime.now().plusDays(1), false);

        List<ExpiredTokenProjection> result = repository.findExpiredTokensForWarning(LocalDateTime.now());
        assertEquals(1, result.size());
        assertTrue(result.get(0).token().contains("expired-unwarned"));
    }

    @Test
    void deleteVerificationTokens_deletesOnlyExpiredWarned() {
        createToken("to-delete", LocalDateTime.now().minusHours(1), true);
        createToken("keep-expired", LocalDateTime.now().minusHours(1), false);
        createToken("keep-future", LocalDateTime.now().plusDays(1), true);

        int deleted = repository.deleteVerificationTokens(LocalDateTime.now());
        assertEquals(1, deleted);
        assertTrue(repository.findByToken("keep-expired").isPresent());
        assertTrue(repository.findByToken("keep-future").isPresent());
        assertTrue(repository.findByToken("to-delete").isEmpty());
    }

    @Test
    void existsByEmployeeAndCreatedAtAfter_returnsTrueForRecent() {
        VerificationToken t = createToken("recent", LocalDateTime.now().plusDays(1), false);
        t.setCreatedAt(Instant.now());
        em.persist(t);

        boolean exists = repository.existsByEmployeeAndCreatedAtAfter(
                employee, Instant.now().minusSeconds(30));
        assertTrue(exists);
    }

    @Test
    void existsByEmployeeAndCreatedAtAfter_returnsFalseForOld() {
        VerificationToken t = createToken("old", LocalDateTime.now().plusDays(1), false);
        t.setCreatedAt(Instant.now().minusSeconds(120));
        em.persist(t);

        boolean exists = repository.existsByEmployeeAndCreatedAtAfter(
                employee, Instant.now().minusSeconds(60));
        assertFalse(exists);
    }
}
