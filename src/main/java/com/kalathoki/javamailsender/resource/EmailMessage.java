package com.kalathoki.javamailsender.resource;

import lombok.Data;

/**
 * @author:- Paribartan Kalathoki
 * @created on:- 30 Apr, 2023 at 9:49 PM
 */

@Data
public class EmailMessage {

 private String to;
 private String subject;
 private String message;
}
