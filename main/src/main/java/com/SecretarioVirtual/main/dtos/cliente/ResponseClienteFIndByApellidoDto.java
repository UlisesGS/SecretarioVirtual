package com.SecretarioVirtual.main.dtos.cliente;

import java.time.LocalDate;

public record ResponseClienteFIndByApellidoDto(
        String apellido,
        String nombre,
        String email,
        String telefono,
        LocalDate fechaCreacion
) {
}
