package com.pm.employeeservice.mail;

import com.pm.employeeservice.Exceptions.EmailRequestsException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pm.employeeservice.model.Employee;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeVerifiedEvent {
    private final MailSenderBody mailSenderBody;
    private final JavaMailSender javaMailSender;
    private final com.pm.employeeservice.repository.EmailFailureLogRepository emailFailureLogRepository;

    @Async
    @EventListener
    public void handleEmployeeVerifiedEvent(VerificationSuccessEvent event){
        try {
            Employee employee = event.getEmployee();
            MimeMessage message = javaMailSender.createMimeMessage();

            String htmlContent = "<h2>Account Activated!</h2>" +
                    "<p>Hi " + employee.getName() + ",</p>" +
                    "<p>Your email has been verified successfully. Your account is now fully active.</p>" +
                    "<p>Best regards,<br>The Team</p>";

            mailSenderBody.mailSenderBody(employee,"Your Account is now Active",htmlContent,message);
            log.info("Email Successfully sent to new Employee {}", employee);
        } catch (Exception e) {
            log.warn("Error in sending email {}",e.getMessage());
            log.warn("Error in sending mail to user {}",event.getEmployee().getEmail());
            throw new RuntimeException("Failure in sending email");
        }


    }
}
