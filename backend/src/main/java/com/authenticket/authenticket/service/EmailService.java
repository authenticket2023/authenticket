package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.FileNameRecord;
import org.springframework.core.io.InputStreamResource;

import java.util.List;
import java.util.TreeMap;

public interface EmailService {
    void send(String to, String email, String subject);
    void send(String to, String subject, String body, List<FileNameRecord> attachments);
    void sendScheduledEmails();
}
