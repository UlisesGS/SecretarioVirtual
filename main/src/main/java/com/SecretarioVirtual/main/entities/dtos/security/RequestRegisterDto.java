package com.SecretarioVirtual.main.entities.dtos.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RequestRegisterDto(

        @NotBlank(message = "El nombre es obligatorio y no puede estar vacío.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "El nombre solo debe contener letras.")
        String name,

        @NotBlank(message = "El apellido es obligatorio y no puede estar vacío.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "El apellido solo debe contener letras.")
        String lastName,

        @NotBlank(message = "El email es obligatorio y no puede estar vacío.")
        @Email(message = "Formato de email no válido.")
        String email,

        @NotNull(message = "La contraseña es obligatoria.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial (!@#$%^&*()).")
        String password,

        @NotNull(message = "El teléfono es obligatorio y no puede estar vacío.")
        Long phone,

        @NotNull(message = "La fecha de nacimiento es obligatoria.")
        @JsonFormat(pattern="yyyy-MM-dd")
        @Past(message = "La fecha de nacimiento debe estar en el pasado.")
        LocalDate dateOfBirth

) {
}
