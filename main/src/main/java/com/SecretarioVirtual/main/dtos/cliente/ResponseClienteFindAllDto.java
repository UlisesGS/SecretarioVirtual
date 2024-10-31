package com.SecretarioVirtual.main.dtos.cliente;

public record ResponseClienteFindAllDto(
        String nombre,
        String apellido,
        String telefono,
        String email

) {
}
