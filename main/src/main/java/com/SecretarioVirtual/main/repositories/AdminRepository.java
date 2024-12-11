package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,String> {
}
