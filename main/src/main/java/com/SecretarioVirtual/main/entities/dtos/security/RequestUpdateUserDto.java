package com.SecretarioVirtual.main.entities.dtos.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RequestUpdateUserDto(
        @NotBlank(message = "El nombre es obligatorio y no puede estar vacío.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "El nombre solo debe contener letras.")
        String name,
        @NotBlank(message = "El apellido es obligatorio y no puede estar vacío.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "El apellido solo debe contener letras.")
        String lastName,
        @NotNull(message = "El teléfono es obligatorio y no puede estar vacío.")
        Long phone,
        @Email
        @NotNull(message = "El correo es obligatorio")
        String email,
        @NotNull(message = "La fecha de nacimiento es obligatoria.")
        @JsonFormat(pattern="yyyy-MM-dd")
        @Past(message = "La fecha de nacimiento debe estar en el pasado.")
        LocalDate dateOfBirth
) {}
