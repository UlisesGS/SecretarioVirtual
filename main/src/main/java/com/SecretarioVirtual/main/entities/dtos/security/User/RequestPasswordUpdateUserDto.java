package com.SecretarioVirtual.main.entities.dtos.security.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RequestPasswordUpdateUserDto(

        @NotBlank(message = "El email es obligatorio y no puede estar vacío.")
        @Email(message = "Formato de email no válido.")
        String email,
        @NotNull(message = "La contraseña es obligatoria.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial (!@#$%^&*()).")
        String password,
        @NotNull(message = "El codigo de verificacion no puede ser nulo")
        String verificationCode
        //se tiene en cueta tanto para cambiar de contra como para recuperar contra
) {
}
