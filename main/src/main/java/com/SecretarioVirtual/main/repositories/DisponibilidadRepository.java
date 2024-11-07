package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Cliente;
import com.SecretarioVirtual.main.entity.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, String>  {

}
