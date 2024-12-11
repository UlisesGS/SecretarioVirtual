package com.SecretarioVirtual.main.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "clientes")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String lastName;

    @Email
    private String email;

    private Long phone;

    private LocalDate dateOfBirth;

    private String password;

    @ManyToMany
    private List<TermsConditions> acceptedTermsConditions;

    @OneToMany
    private List<Appointment> appointmentList;

    private Boolean enabled;
}