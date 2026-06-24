package mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Employee;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeVerifiedEvent {
    private final JavaMailSender mailSender;

    @Async
    @EventListener
    public void handleEmployeeVerifiedEvent(VerificationSuccessEvent event){
        Employee employee = event.getEmployee();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("flamesoul90@gmail.com", "Your Company Onboarding");
            helper.setTo(employee.getEmail());
            helper.setSubject("Welcome to the Team! 🚀 Account Activated");

            String htmlContent = "<h2>Account Activated!</h2>" +
                    "<p>Hi " + employee.getUsername() + ",</p>" +
                    "<p>Your email has been verified successfully. Your account is now fully active.</p>" +
                    "<p>Best regards,<br>The Team</p>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("Email Successfully sent to new Employee {}", employee);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", employee.getEmail(), e);
        }
    }
}
