package com.SecretarioVirtual.main.dtos.cliente;

public record ResponseClienteCreacionDto(
        String nombre,
        String apellido,
        String mensaje
) {}
