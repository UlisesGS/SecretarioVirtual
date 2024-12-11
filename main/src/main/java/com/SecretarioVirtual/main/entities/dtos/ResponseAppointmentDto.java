package com.SecretarioVirtual.main.entities.dtos;

import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record ResponseAppointmentDto(
        String id,
        String clientId,
        LocalDateTime date,
        AppointmentStatus status,
        Boolean isPaid
) {
}
