package com.SecretarioVirtual.main.entities.dtos.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RequestUpdateMailDto(
        @Email
        @NotNull(message = "El correo es obligatorio")
        String email,
        @Email
        @NotNull(message = "El correo es obligatorio")
        String emailNuevo,
        @NotNull(message = "El codigo de verificacion no puede ser nulo")
        String verificationCode
) {}
