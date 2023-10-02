package com.authenticket.authenticket.service;

import org.springframework.core.io.InputStreamResource;

public interface EmailService {
    void send(String to, String email, String subject);
    void send(String to, String subject, String body, String fileName, InputStreamResource file);
    void sendScheduledEmails();
}
