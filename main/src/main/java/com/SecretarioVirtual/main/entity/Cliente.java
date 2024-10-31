package com.SecretarioVirtual.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nombre;

    private String apellido;

    @Column(unique = true)
    private String email;

    private String contrasenia;

    @Column(unique = true)
    private Long telefono;

    @Column(name = "cantidad_de_visitas")
    private Integer cantidadVisitas;

    private boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @PrePersist
    public void onPrePersist(){
        this.fechaCreacion=LocalDate.now();
        this.activo=false;
    }


}
