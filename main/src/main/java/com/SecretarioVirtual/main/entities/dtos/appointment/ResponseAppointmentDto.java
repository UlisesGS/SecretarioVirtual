package com.SecretarioVirtual.main.entities.dtos.appointment;

import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;
import java.time.LocalDateTime;


public record ResponseAppointmentDto(
        String id,
        String clientId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        AppointmentStatus status,
        Boolean isPaid
) {
}
