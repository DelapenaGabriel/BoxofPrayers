package com.server.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Implementation of EmailService using SendGrid API.
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.template.welcome}")
    private String welcomeTemplateId;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    @Override
    public void sendWelcomeEmail(String recipientEmail, String recipientName) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(recipientEmail);
            
            Mail mail = new Mail();
            mail.setFrom(from);
            mail.setTemplateId(welcomeTemplateId);
            
            Personalization personalization = new Personalization();
            personalization.addTo(to);
            personalization.addDynamicTemplateData("name", recipientName);
            
            mail.addPersonalization(personalization);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            

            
        } catch (IOException e) {
            // e.printStackTrace(); // TODO: Add proper logging
            // Don't throw exception - we don't want email failures to break registration
        }
    }
}
