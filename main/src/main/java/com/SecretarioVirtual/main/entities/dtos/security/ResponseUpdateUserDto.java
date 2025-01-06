package com.SecretarioVirtual.main.entities.dtos.security;

import java.time.LocalDate;

public record ResponseUpdateUserDto(
        String phone,

        String name,

        String lastName,

        LocalDate dateOfBirth
)
{}
