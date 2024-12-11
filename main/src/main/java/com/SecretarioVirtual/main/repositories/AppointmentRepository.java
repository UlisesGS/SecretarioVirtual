package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,String> {
}
