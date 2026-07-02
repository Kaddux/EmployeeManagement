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

    @Async
    @EventListener
    public void handleEmployeeActivation(CreateActivationEvent event){
        Employee employee = event.getEmployee();

        String token = UUID.randomUUID().toString();

        verification_tokens verification_token = new verification_tokens();
        verification_token.setToken(token);
        verification_token.setEmployee(employee);
        verification_token.setExpiryDate(LocalDateTime.now().plusSeconds(30));
        verification_token.setCreatedAt(Instant.now());

        tokenRepository.save(verification_token);

        EmployeeActivationEventListener.log.info("Successfully created token for employee {}", employee.getId());

        String verificationUrl = "http://localhost:5002/verify?token=" + token;
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("flamesoul90@gmail.com","Your Company Onboarding");
            helper.setTo(employee.getEmail());
            helper.setSubject("Account Reactivation Mail");

            String htmlContent = "<h3>Welcome " + employee.getUsername() + "!</h3>" +
                    "<p> Your account for is currently deactivated. " +
                    "To regain access, please reactivate your account by clicking the secure link below." +
                    "Please click the link below to activate your account:</p>" +
                    "<a href=\"" + verificationUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;display:inline-block;\">Verify Email</a>" +
                    "<p>This link will expire in 30 seconds.</p>";

            helper.setText(htmlContent, true); // True flag renders HTML
            mailSender.send(mimeMessage);

            log.info("Verification email sent to: {}", employee.getEmail());
        } catch (Exception e) {
            log.warn("Error in sending Email to Employee");
        }
    }

}
