package com.server.service;

/**
 * EmailService interface for sending emails to users.
 */
public interface EmailService {
    
    /**
     * Sends a welcome email to a newly registered user.
     * 
     * @param recipientEmail The email address of the recipient
     * @param recipientName The name of the recipient
     */
    void sendWelcomeEmail(String recipientEmail, String recipientName);
}
