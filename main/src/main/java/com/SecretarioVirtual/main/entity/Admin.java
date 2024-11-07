package com.SecretarioVirtual.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "administrador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nombre;

    private String apellido;

    private String alias;

    @Column(unique = true)
    private String email;

    private String contrasenia;

    @Column(unique = true)
    private Long telefono;

    @ManyToMany
    private List<Disponibilidad> disponibilidadLista;
}
