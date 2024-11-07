package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,String> {
}
