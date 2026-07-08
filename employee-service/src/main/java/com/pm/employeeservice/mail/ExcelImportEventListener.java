package com.pm.employeeservice.mail;

import com.pm.employeeservice.dto.NewEmployeeInfo;
import com.pm.employeeservice.model.EmailFailureLog;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verificationTokens;
import com.pm.employeeservice.repository.EmailFailureLogRepository;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExcelImportEventListener {
    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmployeeRepository employeeRepository;
    private final MailSenderBody senderBody;
    private final EmailFailureLogRepository emailFailureLogRepository;

    @Async
    @EventListener
    public void handleExcelImportEvent(ExcelImportEvent event) {
        List<UUID> employeeIds = event.getEmployeeInfo().stream()
                .map(NewEmployeeInfo::getId)
                .collect(Collectors.toList());

        Iterable<Employee> employees = employeeRepository.findAllById(employeeIds);
        Map<UUID, Employee> employeeMap = new HashMap<>();
        employees.forEach(e -> employeeMap.put(e.getId(), e));

        for (NewEmployeeInfo info : event.getEmployeeInfo()) {
            verificationTokens verificationToken = null;
            try {
                Employee employee = employeeMap.get(info.getId());
                if (employee == null) {
                    log.warn("Employee {} not found in DB, skipping email", info.getId());
                    continue;
                }

                String token = UUID.randomUUID().toString();
                verificationToken = senderBody.verificationBodyMapper(token, employee, 72 * 60 * 60);
                verificationTokenRepository.save(verificationToken);

                String verificationUrl = "http://localhost:5002/set-password?token=" + token;
                String htmlContent = "<h3>Welcome " + employee.getName() + "!</h3>" +
                        "<p>You have been added to the system. Please set your password to verify and activate your account:</p>" +
                        "<a href=\"" + verificationUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;display:inline-block;\">Set Password</a>" +
                        "<p>This link will expire in 72 hours.</p>";

                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                senderBody.mailSenderBody(employee, "Set your password to Activate your account", htmlContent, mimeMessage);

                log.info("Set-password email sent to {}", employee.getEmail());
            } catch (Exception ex) {
                try {
                    EmailFailureLog emailFailureLog = new EmailFailureLog();
                    emailFailureLog.setEventType("ACTIVATION");
                    emailFailureLog.setFailedAt(LocalDateTime.now());
                    emailFailureLog.setErrorMessage(ex.getMessage());
                    emailFailureLog.setEmail(info.getEmail());
                    emailFailureLogRepository.save(emailFailureLog);

                    log.warn("Failed to send email to recipient {} because {}",info.getEmail(),ex.getMessage());
                } catch (Exception logEx) {
                    log.warn("Failed to log email error to database: {}", logEx.getMessage());
                }
                if (verificationToken != null) {
                    try {
                        verificationTokenRepository.delete(verificationToken);
                    } catch (Exception delEx) {
                        log.warn("Failed to clean up verification token after email failure: {}", delEx.getMessage());
                    }
                }
                log.warn("Failed to send set-password email to {}: {}", info.getEmail(), ex.getMessage());
            }
        }
    }
}
