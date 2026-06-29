package com.pm.employeeservice.mail;

import com.pm.employeeservice.dto.ExpiredTokenProjection;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verification_tokens;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeDeactivationEventListener {
    private final JavaMailSender mailSender;

    @Async
    @EventListener
    public void handleAccountDeletionWarning(EmployeeDeactivationEvent event) {
        ExpiredTokenProjection tokenEntity = event.getVerificationToken();


        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("flamesoul90@gmail.com", "Identity Security");
            helper.setTo(tokenEntity.email());
            helper.setSubject("URGENT: Verify your account within 24 hours or it will be deleted");

            String freshVerificationUrl = "http://localhost:5002/verify?token=" + event.getNewToken();

            String htmlContent = "<h2>Final Verification Notice</h2>" +
                    "<p>Hi " + tokenEntity.employeeName() + ",</p>" +
                    "<p>Your initial registration link has expired. <strong>Your account is now scheduled for permanent deletion.</strong></p>" +
                    "<p>To cancel the deletion process, you must verify your identity within the next 24 hours by clicking the link below:</p>" +
                    "<br><a href='" + freshVerificationUrl + "' style='background-color:#d9534f;color:white;padding:12px 25px;text-decoration:none;display:inline-block;font-weight:bold;border-radius:4px;'>Verify Account Now</a>" +
                    "<p>If you take no action, all your registered information will be wiped entirely from our servers for security compliance.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.warn("Error occurred in sending deletion warning email {}", e.getMessage());
        }
    }
}
