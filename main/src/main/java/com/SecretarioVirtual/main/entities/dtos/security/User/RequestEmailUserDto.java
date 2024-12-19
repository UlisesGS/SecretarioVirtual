package com.SecretarioVirtual.main.entities.dtos.security.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestEmailUserDto(
        @NotBlank(message = "El email es obligatorio y no puede estar vacío.")
        @Email(message = "Formato de email no válido.")
        String email
) {
}
