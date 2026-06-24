package controller;


import dto.LoginRequestDTO;
import dto.LoginResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mail.EmployeeVerifiedEvent;
import mail.VerificationSuccessEvent;
import model.Employee;
import model.verification_tokens;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.EmployeeRepository;
import repository.VerificationTokenRepository;
import service.AuthService;

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
    public Object login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);


        if(tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
        Optional<verification_tokens> tokenOpt = verificationTokenRepository.findByToken(token);

        if(tokenOpt.isEmpty())
            return ResponseEntity.badRequest().body("Invalid Token");

        verification_tokens verificationToken = tokenOpt.get();

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
}
