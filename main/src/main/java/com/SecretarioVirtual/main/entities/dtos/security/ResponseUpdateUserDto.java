package com.SecretarioVirtual.main.entities.dtos.security;

public record ResponseUpdateUserDto(
        Long phone,

        String name,

        String lastName
)
{}
