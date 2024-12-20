package com.SecretarioVirtual.main.entities.dtos.security.User;

import java.time.LocalDate;

public record ResponseUserDto (
        String id,
        String name,
        String lastName,
        String email,
        Long phone,
        LocalDate dateOfBirth
){
}
