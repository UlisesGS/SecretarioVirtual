package com.SecretarioVirtual.main.exceptions;

public record ErrorResponse(
        int statusCode,
        String message
) {
}
