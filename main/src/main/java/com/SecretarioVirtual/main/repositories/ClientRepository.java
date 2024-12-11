package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client,String> {
}
