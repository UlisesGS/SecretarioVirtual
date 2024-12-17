package com.SecretarioVirtual.main.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MailSendingException extends RuntimeException {
    private String message;
}
