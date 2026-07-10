package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.model.EmailFailureLog;
import com.pm.employeeservice.repository.EmailFailureLogRepository;
import com.pm.employeeservice.service.EmailFailureLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailFailureLogServiceTests {

    @Mock EmailFailureLogRepository repository;
    @InjectMocks EmailFailureLogService service;

    @Test
    void saveLog_success() {
        EmailFailureLog log = new EmailFailureLog();
        service.saveLog(log);
        verify(repository).save(log);
    }

    @Test
    void saveLog_whenException_doesNotThrow() {
        EmailFailureLog log = new EmailFailureLog();
        doThrow(new RuntimeException("DB down")).when(repository).save(log);
        assertDoesNotThrow(() -> service.saveLog(log));
    }
}
