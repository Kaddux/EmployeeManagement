package com.pm.employeeservice.mail;

import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verificationTokens;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
@Component
public class MailSenderBody {
    private final JavaMailSender javaMailSender;
    public void mailSenderBody(Employee employee, String subject, String htmlContent, MimeMessage mimeMessage) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom("flamesoul90@gmail.com", "Your Company Onboarding");
            helper.setTo(employee.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Error in sending mail: " + e.getMessage());
        }
    }
    public verificationTokens verificationBodyMapper(String token, Employee employee){
        return verificationBodyMapper(token, employee, 30);
    }

    public verificationTokens verificationBodyMapper(String token, Employee employee, long expirySeconds){
        verificationTokens verification_token = new verificationTokens();
        verification_token.setToken(token);
        verification_token.setEmployee(employee);
        verification_token.setCreatedAt(Instant.now());
        verification_token.setExpiryDate(LocalDateTime.now().plusSeconds(expirySeconds));

        return verification_token;
    }
}
