package com.SecretarioVirtual.main.entity;

import com.SecretarioVirtual.main.enums.DiaSemana;
import com.SecretarioVirtual.main.validations.interfaces.TimeRange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "disponibilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "dia_de_semana")
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;  // Ej: "Lunes", "Martes"

    @TimeRange(min = "08:00", max = "11:00", message = "La hora de inicio debe estar entre 08:00 y 11:00")
    @Column(name = "hora_de_inicio_mañana")
    private LocalTime horaInicio;

    @TimeRange(min = "09:00", max = "12:00", message = "La hora fin debe estar entre 09:00 y 12:00")
    @Column(name = "hora_fin_mañana") // RECIBIMOS TODOS, Y DE ULTIMA SE DEJA NULL
    private LocalTime horaFin;

    @TimeRange(min = "12:00", max = "17:00", message = "La hora de inicio debe estar entre 12:00 y 17:00")
    @Column(name = "hora_de_inicio_tarde")
    private LocalTime horaInicioTarde;

    @TimeRange(min = "13:00", max = "18:00", message = "La hora de inicio debe estar entre 13:00 y 18:00")
    @Column(name = "hora_fin_tarde")
    private LocalTime horaFinTarde;

    @TimeRange(min = "18:00", max = "21:00", message = "La hora de inicio debe estar entre 18:00 y 21:00")
    @Column(name = "hora_de_inicio_noche")
    private LocalTime horaInicioNoche;

    @TimeRange(min = "19:00", max = "22:00", message = "La hora de inicio debe estar entre 19:00 y 22:00")
    @Column(name = "hora_fin_noche")
    private LocalTime horaFinNoche;

}

