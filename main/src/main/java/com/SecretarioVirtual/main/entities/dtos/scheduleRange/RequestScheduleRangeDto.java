package com.SecretarioVirtual.main.entities.dtos.scheduleRange;
import com.SecretarioVirtual.main.entities.enums.Days;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record RequestScheduleRangeDto(
        @NotNull (message = "El día no puede ser nulo")
        Days day,
        @NotNull (message = "La hora de inicio no puede ser nula")
        LocalTime startsAt,
        @NotNull (message = "La hora de fin no puede ser nula")
        LocalTime endsAt,
        @NotNull (message = "La duración de la sesión no puede ser nula")
        Integer appointmentDuration,
        @NotNull (message = "El tiempo de recreo no puede ser nulo. Para no calcular recreos, indique 0.")
        Integer breakTime
) {
}
