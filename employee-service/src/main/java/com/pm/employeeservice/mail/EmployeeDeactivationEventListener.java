package com.pm.employeeservice.mail;

import com.pm.employeeservice.dto.ExpiredTokenProjection;
import com.pm.employeeservice.model.EmailFailureLog;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeDeactivationEventListener {
    private final JavaMailSender mailSender;
    private final com.pm.employeeservice.repository.EmailFailureLogRepository emailFailureLogRepository;

    @Async
    @EventListener
    public void handleAccountDeletionWarning(EmployeeDeactivationEvent event) {
        ExpiredTokenProjection tokenEntity = event.getVerificationToken();


        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("flamesoul90@gmail.com", "Identity Security");
            helper.setTo(tokenEntity.email());
            helper.setSubject("URGENT: Verify your account within 24 hours or it will be deactivated");

            String freshVerificationUrl = "http://localhost:5002/verify?token=" + event.getNewToken();

            String htmlContent = "<h2>Final Verification Notice</h2>" +
                    "<p>Hi " + tokenEntity.employeeName() + ",</p>" +
                    "<p>Your initial registration link has expired. <strong>Your account is now scheduled for deactivation.</strong></p>" +
                    "<p>To cancel the deactivation process, you must verify your identity within the next 24 hours by clicking the link below:</p>" +
                    "<br><a href='" + freshVerificationUrl + "' style='background-color:#d9534f;color:white;padding:12px 25px;text-decoration:none;display:inline-block;font-weight:bold;border-radius:4px;'>Verify Account Now</a>" +
                    "<p>If you take no action, all your registered information will be wiped entirely from our servers for security compliance.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            EmailFailureLog emailFailureLog = new EmailFailureLog();
            emailFailureLog.setEventType("ACTIVATION");
            emailFailureLog.setFailedAt(LocalDateTime.now());
            emailFailureLog.setErrorMessage(e.getMessage());
            emailFailureLog.setEmail(tokenEntity.email());
            
            try {
                emailFailureLogRepository.save(emailFailureLog);
            } catch (Exception logEx) {
                log.warn("Failed to log email error to database: {}", logEx.getMessage());
            }

            log.warn("Failed to send email to recipient {} because {}",tokenEntity.email(),e.getMessage());
            log.warn("Error occurred in sending deletion warning email {}", e.getMessage());
        }
    }
}
