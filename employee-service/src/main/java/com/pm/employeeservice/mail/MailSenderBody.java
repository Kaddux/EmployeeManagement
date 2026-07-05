package com.pm.employeeservice.mail;

import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verification_tokens;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

//MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//
//            helper.setFrom("flamesoul90@gmail.com","Your Company Onboarding");
//            helper.setTo(employee.getEmail());
//            helper.setSubject("Account Reactivation Mail");
//
//            String htmlContent = "<h3>Welcome " + employee.getUsername() + "!</h3>" +
//                    "<p> Your account for is currently deactivated. " +
//                    "To regain access, please reactivate your account by clicking the secure link below." +
//                    "Please click the link below to activate your account:</p>" +
//                    "<a href=\"" + verificationUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;display:inline-block;\">Verify Email</a>" +
//                    "<p>This link will expire in 30 seconds.</p>";
//
//            helper.setText(htmlContent, true); // True flag renders HTML
//            mailSender.send(mimeMessage);

@Getter
@Setter
@RequiredArgsConstructor
public class MailSenderBody {
    private final JavaMailSender javaMailSender;
    public void mailSenderBody(Employee employee, String subject, String htmlContent, MimeMessage mimeMessage) {

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setFrom("flamesoul90@gmail.com", "Your Company Onboarding");
            helper.setTo(employee.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Error in sending mail");
        }
    }
    public verification_tokens verificationBodyMapper(String token, Employee employee){
        verification_tokens verification_token = new verification_tokens();
        verification_token.setToken(token);
        verification_token.setEmployee(employee);
        verification_token.setCreatedAt(Instant.now());
        verification_token.setExpiryDate(LocalDateTime.now().plusSeconds(30));

        return verification_token;
    }
}
