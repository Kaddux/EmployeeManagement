package com.pm.employeeservice.service;

import com.pm.employeeservice.dto.LoginRequestDTO;
import com.pm.employeeservice.model.Employee;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.util.JwtUtil;

import java.util.Optional;

@Service
public class AuthService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
