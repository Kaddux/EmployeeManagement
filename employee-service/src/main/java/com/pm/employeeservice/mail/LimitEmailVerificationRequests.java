package com.pm.employeeservice.mail;

import com.pm.employeeservice.Exceptions.EmailRequestsException;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LimitEmailVerificationRequests {

    private final VerificationTokenRepository verificationTokenRepository;

    public void limitEmailVerificationRequested(Employee employee) {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(2));

        boolean recentlySent = verificationTokenRepository.existsByEmployeeAndCreatedAtAfter(employee,cutoff);

        if(recentlySent)
            throw new EmailRequestsException("Too many requests");
    }
}
