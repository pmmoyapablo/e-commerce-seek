package com.seek.users.application.port.out.external;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
