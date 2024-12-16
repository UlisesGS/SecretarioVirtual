package com.SecretarioVirtual.main.entities.dtos.scheduleRange;

import com.SecretarioVirtual.main.entities.enums.Days;
import java.time.LocalTime;

public record ResponseScheduleRangeDto(
        String id,
        Days day,
        LocalTime startsAt,
        LocalTime endsAt,
        Integer appointmentDuration,
        Integer breakTime,
        Integer appointmentsAmount
) {
}
