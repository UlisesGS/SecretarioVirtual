package com.SecretarioVirtual.main.entities.dtos.security;

public record RequestVerifyUserDto(
        String email,
        String verificationCode
) {
}
