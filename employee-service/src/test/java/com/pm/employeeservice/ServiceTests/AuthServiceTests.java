package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.dto.LoginRequestDTO;
import com.pm.employeeservice.mail.VerificationSuccessEvent;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verificationTokens;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import com.pm.employeeservice.service.AuthService;
import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock EmployeeRepository employeeRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock VerificationTokenRepository verificationTokenRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks AuthService authService;

    private Employee enabledEmployee() {
        Employee e = new Employee();
        e.setEmail("user@example.com");
        e.setPassword("encoded-pass");
        e.setEnabled(true);
        e.setRole(Role.ROLE_EMPLOYEE);
        return e;
    }

    private Employee disabledEmployee() {
        Employee e = enabledEmployee();
        e.setEnabled(false);
        return e;
    }

    private verificationTokens validToken(Employee e) {
        verificationTokens t = new verificationTokens();
        t.setEmployee(e);
        t.setExpiryDate(LocalDateTime.now().plusDays(1));
        t.setToken("valid-token");
        return t;
    }

    @Test
    void authenticate_validCredentials_returnsToken() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("correct-pass");

        Employee e = enabledEmployee();
        when(employeeRepository.findByEmail("user@example.com")).thenReturn(Optional.of(e));
        when(passwordEncoder.matches("correct-pass", "encoded-pass")).thenReturn(true);
        when(jwtUtil.generateToken("user@example.com", "ROLE_EMPLOYEE")).thenReturn("jwt-token");

        Optional<String> result = authService.authenticate(dto);
        assertTrue(result.isPresent());
        assertEquals("jwt-token", result.get());
    }

    @Test
    void authenticate_wrongPassword_returnsEmpty() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("wrong-pass");

        when(employeeRepository.findByEmail("user@example.com")).thenReturn(Optional.of(enabledEmployee()));
        when(passwordEncoder.matches("wrong-pass", "encoded-pass")).thenReturn(false);

        assertTrue(authService.authenticate(dto).isEmpty());
    }

    @Test
    void authenticate_disabledAccount_returnsEmpty() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("correct-pass");

        Employee e = disabledEmployee();
        when(employeeRepository.findByEmail("user@example.com")).thenReturn(Optional.of(e));
        when(passwordEncoder.matches("correct-pass", "encoded-pass")).thenReturn(true);

        assertTrue(authService.authenticate(dto).isEmpty());
    }

    @Test
    void authenticate_emailNotFound_returnsEmpty() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("missing@example.com");
        dto.setPassword("any-pass");

        when(employeeRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertTrue(authService.authenticate(dto).isEmpty());
    }

    @Test
    void setPassword_validToken_success() {
        Employee e = disabledEmployee();
        verificationTokens t = validToken(e);
        when(verificationTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(t));
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");

        String result = authService.setPassword("valid-token", "new-pass");

        assertEquals("Password has been set successfully. Your account is now active.", result);
        assertTrue(e.isEnabled());
        assertEquals("encoded-new-pass", e.getPassword());
        verify(verificationTokenRepository).delete(t);
        verify(eventPublisher).publishEvent(any(VerificationSuccessEvent.class));
    }

    @Test
    void setPassword_expiredToken_throws() {
        Employee e = disabledEmployee();
        verificationTokens t = validToken(e);
        t.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(verificationTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(t));

        assertThrows(IllegalArgumentException.class,
                () -> authService.setPassword("expired-token", "new-pass"));
        verify(verificationTokenRepository).delete(t);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void setPassword_alreadyActive_throws() {
        Employee e = enabledEmployee();
        verificationTokens t = validToken(e);
        when(verificationTokenRepository.findByToken("active-token")).thenReturn(Optional.of(t));

        assertThrows(IllegalArgumentException.class,
                () -> authService.setPassword("active-token", "new-pass"));
        verify(verificationTokenRepository).delete(t);
    }

    @Test
    void setPassword_invalidToken_throws() {
        when(verificationTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> authService.setPassword("bad-token", "new-pass"));
    }

    @Test
    void validateToken_valid_returnsTrue() {
        when(jwtUtil.validateToken("good-token")).thenReturn(true);
        assertTrue(authService.validateToken("good-token"));
    }

    @Test
    void validateToken_invalid_returnsFalse() {
        when(jwtUtil.validateToken("bad-token")).thenThrow(new RuntimeException("Invalid"));
        assertFalse(authService.validateToken("bad-token"));
    }
}
