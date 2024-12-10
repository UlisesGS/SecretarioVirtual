package com.SecretarioVirtual.main.entity;


import com.SecretarioVirtual.main.enums.AppointmentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "turnos")
public class Appointment {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String clientId;

    private LocalDateTime date;

    private AppointmentStatus status;

    private Boolean isPaid;
}
