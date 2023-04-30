package com.kalathoki.javamailsender.service;

import java.util.Map;

/**
 * @author:- Paribartan Kalathoki
 * @created on:- 30 Apr, 2023 at 9:40 PM
 */
public interface EmailSenderService {

    void sendEmail(String to, String subject, String message);


    void sendTestMail();


    String createDynamicHtmlViewContent(String template, Map<String, Object> details);

}
