package com.collins.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

   // @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("notification@collins-dev.com");
            message.setSubject(subject);
            message.setText(body);

            LOGGER.info("Attempting to send email to {}", to);
            mailSender.send(message);

            LOGGER.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            LOGGER.error("Failed to send email to {}", to, e);
        }
    }
}
