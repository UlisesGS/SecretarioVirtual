package com.SecretarioVirtual.main.entities.dtos.security;

import java.time.LocalDate;

public record ResponseUserNonVerifiedDto(
        String id,
        String name,
        String lastName,
        String email,
        Long phone,
        LocalDate dateOfBirth,
        String verificationCode
) {
}
