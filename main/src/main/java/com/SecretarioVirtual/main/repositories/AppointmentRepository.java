package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entities.Appointment;
import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,String> {
    List<Appointment> findAllByClientId(String clientId);
    List<Appointment> findAllByClientIdAndStatus(String clientId, AppointmentStatus status);
    List<Appointment> findAllByClientIdAndIsPaid(String clientId, Boolean isPaid);

    @Query("SELECT a FROM Appointment a WHERE (a.clientId = :clientId)" +
            "AND a.date >= :fromDate AND a.date < :toDate")
    List<Appointment> findAllAppointmentsByClientIdAndDateRange(
            @Param("clientId") String clientId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    List<Appointment> findAllByStatus(AppointmentStatus status);
    List<Appointment> findAllByIsPaid(Boolean isPaid);

    @Query("SELECT a FROM Appointment a WHERE a.date >= :fromDate AND a.date < :toDate")
    List<Appointment> findAllAppointmentsByDateRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

}
