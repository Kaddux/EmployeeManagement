package com.pm.employeeservice.mail;


import com.pm.employeeservice.model.EmailFailureLog;
import com.pm.employeeservice.service.EmailFailureLogService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verificationTokens;

import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.pm.employeeservice.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeRegistrationEventListener {
    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository tokenRepository;
    private final MailSenderBody mailSenderBody;
    private final EmailFailureLogService emailFailureLogService;
    private final SpringTemplateEngine templateEngine;

    @EventListener
    public void handleUserRegistrationEvent(CreateRegistrationEvent event) {
        Employee employee = event.getEmployee();
        String token = UUID.randomUUID().toString();
        verificationTokens verificationToken = mailSenderBody.verificationBodyMapper(token,event.getEmployee());

        tokenRepository.save(verificationToken);

        log.info("Successfully created token for employee {}", employee.getId());

        String htmlContent = getString(token, employee);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        
        try {
            mailSenderBody.mailSenderBody(employee,"Activate your Account",htmlContent,mimeMessage);
            log.info("Verification Email sent to {}",employee.getEmail());
        } catch (Exception ex) {
            try {
                tokenRepository.delete(verificationToken);
            } catch (Exception delEx) {
                log.warn("Failed to clean up verification token after email failure: {}", delEx.getMessage());
            }

            EmailFailureLog emailFailureLog = new EmailFailureLog();
            emailFailureLog.setEventType("REGISTRATION");
            emailFailureLog.setFailedAt(LocalDateTime.now());
            emailFailureLog.setErrorMessage(ex.getMessage());
            emailFailureLog.setEmail(event.getEmail());
            
            emailFailureLogService.saveLog(emailFailureLog);

            log.warn("Failed to send email to recipient {} because {}",event.getEmail(),ex.getMessage());
            
            throw new RuntimeException("Email delivery failed for " + event.getEmail() + ": " + ex.getMessage());
        }
    }

    private @NonNull String getString(String token, Employee employee) {

        String verificationUrl = "http://localhost:5002/verify?token=" + token;
        Context context = new Context();
        context.setVariable("name", employee.getName());
        context.setVariable("verificationUrl", verificationUrl);
        return templateEngine.process("email-verification", context);
    }
}

