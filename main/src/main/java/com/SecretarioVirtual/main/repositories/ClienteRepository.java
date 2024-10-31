package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
    public Optional<Cliente> findByApellido(String apellido);
}
