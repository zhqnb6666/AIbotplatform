package com.aibotplatform.service.impl;

import com.aibotplatform.exception.ApiException;
import com.aibotplatform.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String verificationCode) throws ApiException {
        sendEmail(to,
                "AIbot Verification Code",
                "Your verification code is: " + verificationCode);
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws ApiException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("AIbot Support <12211205@mail.sustech.edu.cn>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (MailException e) {
            throw new ApiException("Failed to send verification email: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}