package com.SecretarioVirtual.main.entity;

import com.SecretarioVirtual.main.enums.EstadoPago;
import com.SecretarioVirtual.main.enums.EstadoTurno;
import com.SecretarioVirtual.main.enums.TipoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal monto;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private TipoPago tipo;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @OneToOne
    private Turno turno;


}
