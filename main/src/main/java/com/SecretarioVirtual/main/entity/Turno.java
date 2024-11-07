package com.SecretarioVirtual.main.entity;

import com.SecretarioVirtual.main.enums.EstadoTurno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String dia; // dia de la semana

    private LocalDate fecha;

    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    private EstadoTurno estado;

    @ManyToOne
    private Cliente cliente;

    private BigDecimal monto;

    @PrePersist
    public void onPrePersist(){
        this.estado=EstadoTurno.DISPONIBLE;
    }
}
