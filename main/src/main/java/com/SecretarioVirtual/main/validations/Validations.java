package com.SecretarioVirtual.main.validations;

import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;
import com.SecretarioVirtual.main.exceptions.InvalidDataException;

import java.time.LocalDateTime;

public class Validations {
    
    //TODO. REQUIERE SECURITY. :D
    /*
    public boolean selfValidationOrAdmin() {
    //Corroborar si user id = context holder id.
        String userNameAuthentication = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userNameAuthentication)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        //Corroborar si user id = ADMIN ID.
        return new UserAccountPair(user, account);
    }*/

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

    public boolean isSecondDateBefore(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            return true;
        } else return false;
    }
}
