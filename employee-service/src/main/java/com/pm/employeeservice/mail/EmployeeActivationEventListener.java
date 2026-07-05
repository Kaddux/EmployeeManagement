package com.pm.employeeservice.mail;

import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verification_tokens;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeActivationEventListener {
    private final JavaMailSender mailSender;
    private final VerificationTokenRepository tokenRepository;
    private MailSenderBody mailSenderBody;

    @Async
    @EventListener
    public void handleEmployeeActivation(CreateActivationEvent event){
        Employee employee = event.getEmployee();

        String token = UUID.randomUUID().toString();

        verification_tokens verification_token = mailSenderBody.verificationBodyMapper(token,employee);
        tokenRepository.save(verification_token);

        EmployeeActivationEventListener.log.info("Successfully created token for employee {}", employee.getId());

        String verificationUrl = "http://localhost:5002/verify?token=" + token;

        String htmlContent = "<h3>Welcome " + employee.getUsername() + "!</h3>" +
                "<p> Your account for is currently deactivated. " +
                "To regain access, please reactivate your account by clicking the secure link below." +
                "Please click the link below to activate your account:</p>" +
                "<a href=\"" + verificationUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;display:inline-block;\">Verify Email</a>" +
                "<p>This link will expire in 30 seconds.</p>";
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mailSenderBody.mailSenderBody(employee,"Account Reactivation Mail",htmlContent,mimeMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
//

            log.info("Verification email sent to: {}", employee.getEmail());
        }
    }
