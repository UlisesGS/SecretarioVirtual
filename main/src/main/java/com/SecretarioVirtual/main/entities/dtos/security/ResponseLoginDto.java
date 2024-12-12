package com.SecretarioVirtual.main.entities.dtos.security;

public record ResponseLoginDto(
        String id,
        String token,
        ResponseUserVerifiedDto user
) {
}