package com.SecretarioVirtual.main.entities.dtos.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestLoginDto(
        @NotBlank(message = "La contraseña es obligatoria.")
        String password,
        @NotBlank(message = "El email es obligatorio y no puede estar vacío.")
        @Email(message = "Formato de email no válido.")
        String email
) {
}