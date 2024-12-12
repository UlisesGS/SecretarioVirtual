package com.SecretarioVirtual.main.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidJwtException extends RuntimeException {
    private String message;
}

