package com.pm.employeeservice.service;

import com.pm.employeeservice.dto.LoginRequestDTO;
import com.pm.employeeservice.mail.VerificationSuccessEvent;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verificationTokens;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.util.JwtUtil;

import java.util.Optional;

@Service
public class AuthService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       VerificationTokenRepository verificationTokenRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String setPassword(String token, String newPassword) {
        verificationTokens verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("Token has expired");
        }

        Employee employee = verificationToken.getEmployee();

        if (employee.isEnabled()) {
            verificationTokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("Account is already active");
        }
        employee.changePassword(passwordEncoder.encode(newPassword));
        employee.activate();
        employeeRepository.save(employee);

        verificationTokenRepository.delete(verificationToken);

        eventPublisher.publishEvent(new VerificationSuccessEvent(this, employee));

        return "Password has been set successfully. Your account is now active.";
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){
        return employeeRepository.findByEmail(loginRequestDTO.getEmail())
                .filter(e -> passwordEncoder.matches(loginRequestDTO.getPassword(), e.getPassword()))
                .filter(Employee::isEnabled)
                .map(e -> jwtUtil.generateToken(e.getEmail(), String.valueOf(e.getRole())));
    }

    public boolean validateToken(String token) {
        try{
            jwtUtil.validateToken(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
