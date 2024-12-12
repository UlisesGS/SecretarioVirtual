package com.SecretarioVirtual.main.entities;

import com.SecretarioVirtual.main.entities.enums.Days;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "shedules_ranges")
public class ScheduleRange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private Days day;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private Integer minutes;
}
