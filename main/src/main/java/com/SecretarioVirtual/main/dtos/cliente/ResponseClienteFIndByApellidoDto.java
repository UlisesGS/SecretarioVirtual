package com.SecretarioVirtual.main.dtos.cliente;

public record ResponseClienteFIndByApellidoDto(
        String apellido,
        String nombre,
        String email,
        String telefono
) {
}
