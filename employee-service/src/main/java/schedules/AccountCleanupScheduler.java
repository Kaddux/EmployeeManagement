package schedules;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Employee;
import model.verification_tokens;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import repository.EmployeeRepository;
import repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCleanupScheduler {
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmployeeRepository employeeRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void purgeUnverifiedAccounts(){
        log.info("Starting purge for unverified accounts: ");

        LocalDateTime now = LocalDateTime.now();

        List<verification_tokens> expiredTokens = verificationTokenRepository.findAllByExpiryDateBefore(now);

        if(expiredTokens.isEmpty())
            log.info("No expired tokens found");

        log.info("Found {} expired tokens to process.", expiredTokens.size());

        for (verification_tokens token : expiredTokens) {
            Employee employee = token.getEmployee();

            // Double-check safeguard: Only delete if the employee is still unverified
            if (employee != null && !employee.isEnabled()) {
                log.info("Deleting unverified employee: {} (Reason: Token expired)", employee.getEmail());

                // 2. Delete the employee.
                // automatically drops token due to cascade
                employeeRepository.delete(employee);

            }
        }
    }
}
