package com.pm.employeeservice.schedules;

import com.pm.employeeservice.dto.ExpiredTokenProjection;
import com.pm.employeeservice.mail.EmployeeDeactivationEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verification_tokens;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCleanupScheduler {
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void purgeUnverifiedAccounts() {
        log.info("Starting purge for unverified accounts: ");

        LocalDateTime now = LocalDateTime.now();

        List<ExpiredTokenProjection> targets = verificationTokenRepository.findExpiredTokensForWarning(now);

        if (!targets.isEmpty()) {
            // 2. Execute ONE single SQL command to update all matching records in the DB
            LocalDateTime freshExpiry = LocalDateTime.now().plusSeconds(30);
            String sql = "UPDATE verification_tokens SET token = ?, expiry_date = ?, warning_sent = true WHERE id = ?";
            List<Object[]> batchArgs = new ArrayList<>();

            for (ExpiredTokenProjection target : targets) {
                // 1. Generate the token in Java so we actually know what it is!
                String newJavaToken = UUID.randomUUID().toString();

                // 2. Queue the parameters for the database batch execution
                batchArgs.add(new Object[]{newJavaToken, freshExpiry, target.token_id()});
                jdbcTemplate.batchUpdate(sql, batchArgs);

                log.info("Bulk updated {} tokens to warning state in database.", batchArgs.size());

                applicationEventPublisher.publishEvent(new EmployeeDeactivationEvent(this, target, newJavaToken));
            }

            int deletedCount = employeeRepository.bulkPurgeExpiredAccounts(now);
            if (deletedCount > 0) {
                log.warn("Bulk purged {} dead unverified accounts from the system.", deletedCount);
            }
        }
    }
}
