package com.SecretarioVirtual.main.dtos.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestClienteCreacionDto(

        @NotBlank(message = "El nombre no puede ser null")
        String nombre,

        @NotBlank(message = "El apellido no puede ser null")
        String apellido,

        @NotBlank(message = "El email no puede ser null")
        String email,

        @NotBlank(message = "La contraseña no puede ser null")
        @Size(min = 6)//proximamente cumplir con ciertos caracteres
        String contrasenia,

        @NotNull(message = "El telefono no puede ser null")
        Long telefono

) {}
