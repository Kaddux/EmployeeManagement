package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.Exceptions.EmailRequestsException;
import com.pm.employeeservice.mail.LimitEmailVerificationRequests;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimitEmailVerificationRequestsTests {

    @Mock VerificationTokenRepository verificationTokenRepository;
    @InjectMocks LimitEmailVerificationRequests rateLimiter;

    @Test
    void limitEmailVerificationRequested_recentRequest_throws() {
        Employee emp = new Employee();
        when(verificationTokenRepository.existsByEmployeeAndCreatedAtAfter(any(), any(Instant.class)))
                .thenReturn(true);
        assertThrows(EmailRequestsException.class,
                () -> rateLimiter.limitEmailVerificationRequested(emp));
    }

    @Test
    void limitEmailVerificationRequested_noRecentRequest_passes() {
        Employee emp = new Employee();
        when(verificationTokenRepository.existsByEmployeeAndCreatedAtAfter(any(), any(Instant.class)))
                .thenReturn(false);
        assertDoesNotThrow(() -> rateLimiter.limitEmailVerificationRequested(emp));
    }
}
