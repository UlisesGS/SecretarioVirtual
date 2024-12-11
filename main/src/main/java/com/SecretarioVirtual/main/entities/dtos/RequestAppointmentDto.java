package com.SecretarioVirtual.main.entities.dtos;
import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RequestAppointmentDto(
        @NotNull (message = "El id del cliente es obligatorio.")
        String clientId,
        @NotNull (message = "La fecha y hora del turno son obligatorias.")
        @Future (message = "La fecha del turno debe ser futura.")
        LocalDateTime date,
        @NotNull (message = "El estado del turno es obligatorio.")
        String status,
        @NotNull (message = "Es obligatorio marcar si el turno está pago o no.")
        Boolean isPaid
) {
}
