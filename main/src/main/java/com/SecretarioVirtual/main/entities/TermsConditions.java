package com.SecretarioVirtual.main.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "terminos_condiciones")
public class TermsConditions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private LocalDate date;

    private String content;

    private Boolean isPublished;

    @PrePersist
    private void onPrePresist(){
        this.date = LocalDateTime.now().toLocalDate();
    }
}
