package com.SecretarioVirtual.main.dtos.cliente;

import java.time.LocalDate;

public record ResponseClienteFindAllDto(
        String nombre,
        String apellido,
        String telefono,
        String email,
        LocalDate fechaCreacion

) {
}
