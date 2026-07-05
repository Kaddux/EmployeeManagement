package com.pm.employeeservice.mail;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.verification_tokens;

import org.springframework.context.event.EventListener;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.pm.employeeservice.repository.VerificationTokenRepository;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeRegistrationEventListener {
    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository tokenRepository;
    private MailSenderBody mailSenderBody;
    @Async
    @EventListener
    public void handleUserRegistrationEvent(CreateRegistrationEvent event) {
        Employee employee = event.getEmployee();
        String token = UUID.randomUUID().toString();
        verification_tokens verificationToken = mailSenderBody.verificationBodyMapper(token,event.getEmployee());

        tokenRepository.save(verificationToken);

        log.info("Successfully created token for employee {}", employee.getId());

        String verificationUrl = "http://localhost:5002/verify?token=" + token;
        String htmlContent = "<h3>Welcome " + employee.getUsername() + "!</h3>" +
                    "<p>Thank you for registering. Please click the link below to activate your account:</p>" +
                    "<a href=\"" + verificationUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;display:inline-block;\">Verify Email</a>" +
                    "<p>This link will expire in 30 seconds.</p>";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mailSenderBody.mailSenderBody(employee,"Activate your Account",htmlContent,mimeMessage);

        log.info("Verification Email sent to {}",employee.getEmail());
        }
    }

//        try {
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//
//            helper.setFrom("flamesoul90@gmail.com");
//            helper.setTo(employee.getEmail());
//            helper.setSubject("Activate Your Account ✔");
//
//            String htmlContent = "<h3>Welcome " + employee.getUsername() + "!</h3>" +
//                    "<p>Thank you for registering. Please click the link below to activate your account:</p>" +
//                    "<a href=\"" + verificationUrl + "\" style=\"background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;display:inline-block;\">Verify Email</a>" +
//                    "<p>This link will expire in 30 seconds.</p>";
//
//            helper.setText(htmlContent, true); // True flag renders HTML
//            javaMailSender.send(mimeMessage);
//
//            log.info("Verification email sent to: {}", employee.getEmail());
//        } catch (Exception e) {
//            log.warn("Error in sending Email to Employee");
