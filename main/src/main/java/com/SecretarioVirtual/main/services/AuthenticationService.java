package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.entities.dtos.security.*;
import jakarta.validation.Valid;

public interface AuthenticationService {
    ResponseUserNonVerifiedDto signUp(@Valid RequestRegisterDto requestRegisterDto);

    ResponseLoginDto login(RequestLoginDto requestLoginDto);

    ResponseUserVerifiedDto verifyUser(RequestVerifyUserDto verifyUserDto);

    ResponseUserNonVerifiedDto resendVerificationCode(String email);
}
