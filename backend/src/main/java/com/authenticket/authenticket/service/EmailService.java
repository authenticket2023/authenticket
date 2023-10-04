package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.file.FileNameRecord;

import java.util.List;

public interface EmailService {
    void send(String to, String email, String subject);
    void send(String to, String subject, String body, List<FileNameRecord> attachments);
    void sendScheduledEmails();
}
