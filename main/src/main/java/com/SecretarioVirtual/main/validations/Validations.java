package com.SecretarioVirtual.main.validations;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;
import com.SecretarioVirtual.main.entities.enums.Days;
import com.SecretarioVirtual.main.entities.enums.Role;
import com.SecretarioVirtual.main.exceptions.InvalidDataException;
import com.SecretarioVirtual.main.exceptions.InvalidUserCredentialsException;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.exceptions.ValidationException;
import com.SecretarioVirtual.main.repositories.UserRepository;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class Validations {

    private final UserRepository userRepository;

    public boolean selfOrAdminValidation(String userId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User userLogged = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        User userGiven = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (userId == userLogged.getId() || userLogged.getRole().equals("ADMIN")) {
            return true;
        }
        throw new InvalidUserCredentialsException("El usuario no tiene permiso para esta acción");
    }

    public boolean adminValidation() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User userLogged = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (userLogged.getRole().equals(Role.ADMIN)) {
            return true;
        }
        throw new InvalidUserCredentialsException("El usuario no tiene permiso para esta acción");
    }

    public AppointmentStatus statusConvert(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return AppointmentStatus.PENDING;
            case "ACCEPTED":
                return AppointmentStatus.ACCEPTED;
            case "DENIED":
                return AppointmentStatus.DENIED;
            case "CANCELLED":
                return AppointmentStatus.CANCELLED;
            case "DONE":
                return AppointmentStatus.DONE;
            default:
                throw new InvalidDataException("El estado del turno no existe: " + status);
        }
    }

    public Days dayConvert(String day) {
        switch (day.toUpperCase()) {
            case "MONDAY":
                return Days.MONDAY;
            case "TUESDAY":
                return Days.TUESDAY;
            case "WEDNESDAY":
                return Days.WEDNESDAY;
            case "THURSDAY":
                return Days.THURSDAY;
            case "FRIDAY":
                return Days.FRIDAY;
            case "SATURDAY":
                return Days.SATURDAY;
            case "SUNDAY":
                return Days.SUNDAY;
            default:
                throw new InvalidDataException("El día no existe: " + day);
        }
    }

    public boolean isSecondDateBefore(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("La fecha de inicio NO puede ser posterior a la de fin.");
        } else {
            return true;
        }
    }

    public boolean isSecondDateBefore(LocalTime startDate, LocalTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("La fecha de inicio NO puede ser posterior a la de fin.");
        } else {
            return true;
        }
    }
}
