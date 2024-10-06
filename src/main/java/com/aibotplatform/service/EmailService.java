package com.aibotplatform.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("AIbot Support <12211205@mail.sustech.edu.cn>");
            helper.setTo(to);
            helper.setSubject("AIbot Verification Code");

            helper.setText("Your verification code is: " + verificationCode, true);

            mailSender.send(message);

            System.out.println("Verification email sent successfully");

        } catch (MailException e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}