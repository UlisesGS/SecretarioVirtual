package com.SecretarioVirtual.main.entities.dtos.appointment;

import jakarta.validation.constraints.NotNull;

public record EditAppointmentDto(
        @NotNull (message = "El id del turno es obligatorio.")
        String id,
        @NotNull (message = "El estado del turno es obligatorio.")
        String status,
        @NotNull (message = "Es obligatorio marcar si el turno está pago o no.")
        Boolean isPaid
) {
}
