package com.SecretarioVirtual.main.entities.dtos.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ResponseUpdateMailDto(

        String email
) {}