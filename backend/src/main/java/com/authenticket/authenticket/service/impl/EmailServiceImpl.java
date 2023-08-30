package com.authenticket.authenticket.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSenderImpl mailSender;
    @Value("${authenticket.smtp-username}")
    private final String smtpUsername;
    @Value("${authenticket.smtp-password}")
    private final String smtpPassword;

    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("authenticket2023@hotmail.com");
            mailSender.setUsername(smtpUsername);
            mailSender.setPassword(smtpPassword);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email (1)", e);
            throw new IllegalStateException("failed to send email");
        } catch (Exception e) {
            LOGGER.error("failed to send email (2)", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
