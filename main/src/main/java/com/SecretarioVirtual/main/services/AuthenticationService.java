package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.entities.dtos.security.*;
import jakarta.validation.Valid;

public interface AuthenticationService {
    ResponseUserNonVerifiedDto signUp(String action,@Valid RequestRegisterDto requestRegisterDto);

    ResponseLoginDto login(RequestLoginDto requestLoginDto);

    ResponseUserVerifiedDto verifyUser(String action,RequestVerifyUserDto verifyUserDto);

    ResponseUserNonVerifiedDto resendVerificationCode(String action,String email);

    String sendVerificationEmail(String action, String mail, String verificationCode);
}
