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
