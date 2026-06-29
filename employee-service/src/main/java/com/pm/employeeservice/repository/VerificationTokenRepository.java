package com.pm.employeeservice.repository;

import com.pm.employeeservice.dto.ExpiredTokenProjection;
import com.pm.employeeservice.model.verification_tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<verification_tokens, Long> {
    Optional<verification_tokens> findByToken(String token);

    List<verification_tokens> findAllByExpiryDateBefore(LocalDateTime localDateTime);

    // Projection query — fetches only the 4 fields needed, not full entities
    @Query("SELECT new com.pm.employeeservice.dto.ExpiredTokenProjection(t.id, t.token, e.email, e.name) " +
            "FROM verification_tokens t JOIN t.employee e " +
            "WHERE t.expiryDate < :now AND t.warning_sent = false")
    List<ExpiredTokenProjection> findExpiredTokensForWarning(@Param("now") LocalDateTime now);


    @Modifying
    @Query(value = "DELETE FROM employee WHERE enabled = false AND id IN " +
            "(SELECT employee_id FROM verification_tokens WHERE expiry_date < :now AND warning_sent = true)",
            nativeQuery = true)
    int bulkPurgeExpiredAccounts(@Param("now") LocalDateTime now);
}
