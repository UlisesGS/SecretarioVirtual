package com.SecretarioVirtual.main.entities.dtos.appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


public record RequestDatesDto(
        @Future (message = "La fecha del turno debe ser futura.")
        LocalDate startDate,
        @NotNull (message = "La hora de finalización es obligatoria.")
        LocalDate endDate
) {
}
