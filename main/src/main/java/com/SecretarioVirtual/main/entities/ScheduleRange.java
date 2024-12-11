package com.SecretarioVirtual.main.entities;

import com.SecretarioVirtual.main.entities.enums.Days;
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
@Entity(name = "rangos_horarios")
public class ScheduleRange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Days day;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private Integer minutes;
}
