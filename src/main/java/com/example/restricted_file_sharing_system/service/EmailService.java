package com.example.restricted_file_sharing_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom("shuvochandra999@gmail.com");

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: " + to, e);
        }
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        String subject = "Verify your email - Restricted File Sharing System";
        String content = String.format(
                """
                        <div style="font-family: Arial, sans-serif; padding: 20px;">
                            <h2>Welcome to Restricted File Sharing System!</h2>
                            <p>Please click the button below to verify your email address and activate your account:</p>
                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 10px 0;">Verify Email</a>
                            <p>This link will expire in 10 minutes.</p>
                            <p>If you didn't create an account, you can safely ignore this email.</p>
                        </div>
                        """,
                verificationLink);

        sendEmail(to, subject, content);
    }

    public void sendFileSharedNotification(String to, String senderEmail, String fileName, String downloadLink) {
        String subject = "File Shared With You: " + fileName;
        String content = String.format(
                """
                        <div style="font-family: Arial, sans-serif; padding: 20px;">
                            <h3>A file has been shared with you!</h3>
                            <p><strong>%s</strong> has shared the file "<strong>%s</strong>" with you.</p>
                            <p>You can access this file using the link below:</p>
                            <a href="%s" style="background-color: #008CBA; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 10px 0;">Download File</a>
                            <p>Note: You must be logged in to your account to access this file.</p>
                        </div>
                        """,
                senderEmail, fileName, downloadLink);

        sendEmail(to, subject, content);
    }
}
