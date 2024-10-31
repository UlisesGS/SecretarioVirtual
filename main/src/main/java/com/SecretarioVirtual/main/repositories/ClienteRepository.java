package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
}
