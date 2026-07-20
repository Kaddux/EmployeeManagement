package com.pm.employeeservice.controller;


import com.pm.employeeservice.dto.LoginRequestDTO;
import com.pm.employeeservice.dto.LoginResponseDTO;
import com.pm.employeeservice.dto.SetPasswordRequestDTO;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.pm.employeeservice.mail.VerificationSuccessEvent;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.VerificationToken;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import com.pm.employeeservice.service.AuthService;

import java.util.Optional;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        if(tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<LoginResponseDTO>build();
        }
        String token = tokenOptional.get();

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return authService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @GetMapping("/verify")
    @Transactional
    public ResponseEntity<String> verifyEmployeeEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> tokenOpt = verificationTokenRepository.findByToken(token);

        if(tokenOpt.isEmpty())
            return ResponseEntity.badRequest().body("Invalid Token");

        VerificationToken verificationToken = tokenOpt.get();

        if(verificationToken.isExpired()){
            verificationTokenRepository.delete(verificationToken);
            return ResponseEntity.badRequest().body("Verification Token has expired");
        }

        Employee employee = verificationToken.getEmployee();

        if(employee.isEnabled())
            return ResponseEntity.ok("Employee is already enabled");

        employee.setEnabled(true);
        employeeRepository.save(employee);

        applicationEventPublisher.publishEvent(new VerificationSuccessEvent(this, employee));

        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok().body("Employee has been verified");

    }

    @GetMapping(value = "/set-password", produces = "text/html")
    public String showSetPasswordPage(@RequestParam("token") String token, Model model) {
       model.addAttribute(token);
       return "set-password";
    }

    @PostMapping("/set-password")
    @Transactional
    public ResponseEntity<String> setPassword(@Valid @RequestBody SetPasswordRequestDTO request) {
        try {
            String result = authService.setPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
