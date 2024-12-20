package com.SecretarioVirtual.main.entities.dtos.appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RequestAppointmentDto(
        @NotNull (message = "El id del cliente es obligatorio.")
        String clientId,
        @NotNull (message = "La fecha y hora del turno son obligatorias.")
        @Future (message = "La fecha del turno debe ser futura.")
        LocalDateTime startDate,
        @NotNull (message = "La hora de finalización es obligatoria.")
        LocalDateTime endDate,
        @NotNull (message = "El estado del turno es obligatorio.")
        String status,
        @NotNull (message = "Es obligatorio marcar si el turno está pago o no.")
        Boolean isPaid
) {
}
