package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Disponibilidad;
import com.SecretarioVirtual.main.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<Turno, String> {
}
