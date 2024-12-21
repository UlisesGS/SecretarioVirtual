package com.SecretarioVirtual.main.entities.dtos.security;

import java.time.LocalDate;

public record ResponseUserVerifiedDto(
        String id,
        String name,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
