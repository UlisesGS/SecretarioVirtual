package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.TermsConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsConditionsRepository extends JpaRepository<TermsConditions,String> {
}
