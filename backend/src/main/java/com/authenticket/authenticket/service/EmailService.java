package com.authenticket.authenticket.service;

public interface EmailService {
    void send(String to, String email, String subject);
}
