package com.kalathoki.javamailsender.controller;

import com.kalathoki.javamailsender.resource.EmailMessage;
import com.kalathoki.javamailsender.service.EmailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:- Paribartan Kalathoki
 * @created on:- 30 Apr, 2023 at 9:47 PM
 */

@RestController
public class EmailController {

    private final EmailSenderService emailSenderService;

    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/send-mail")
    public ResponseEntity sendEmail(
            @RequestBody EmailMessage emailMessage
    ) {
        // send normal mail using subject and body
        this.emailSenderService.sendEmail(emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getMessage());

        // send mail using html template and image logo
        this.emailSenderService.sendTestMail();
        return ResponseEntity.ok("Successfully sent email.");
    }

}
