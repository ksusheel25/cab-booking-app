package com.skumar.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(to);
        helper.setSubject("Email Verification");
        helper.setText(createVerificationEmailContent(token), true);
        
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(createPasswordResetEmailContent(token), true);
        
        mailSender.send(message);
    }

    private String createVerificationEmailContent(String token) {
        return String.format("""
            <html>
                <body>
                    <h2>Verify your email</h2>
                    <p>Please click the link below to verify your email address:</p>
                    <a href="http://localhost:8081/api/users/verify-email?token=%s">Verify Email</a>
                    <p>This link will expire in 24 hours.</p>
                </body>
            </html>
            """, token);
    }

    private String createPasswordResetEmailContent(String token) {
        return String.format("""
            <html>
                <body>
                    <h2>Reset Your Password</h2>
                    <p>Please click the link below to reset your password:</p>
                    <a href="http://localhost:8081/api/users/reset-password?token=%s">Reset Password</a>
                    <p>This link will expire in 1 hour.</p>
                    <p>If you did not request this, please ignore this email.</p>
                </body>
            </html>
            """, token);
    }
}