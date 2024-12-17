package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.security.*;
import com.SecretarioVirtual.main.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/autenticacion")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/registro/{action}")
    public ResponseEntity<ResponseUserNonVerifiedDto> registerUser(@PathVariable String action,@Valid @RequestBody RequestRegisterDto requestRegisterDto) {
        ResponseUserNonVerifiedDto unverifiedRegisteredUser = authenticationService.signUp(action,requestRegisterDto);
        return new ResponseEntity<>(unverifiedRegisteredUser, HttpStatus.CREATED);
    }

    @PostMapping("/inicio-sesion")
    public ResponseEntity<ResponseLoginDto> loginUser(@Valid @RequestBody RequestLoginDto requestLoginDto) {
        ResponseLoginDto loginResponse = authenticationService.login(requestLoginDto);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verificacion-codigo/{action}")
    public ResponseEntity<ResponseUserVerifiedDto> verifyUser(@PathVariable String action, @Valid @RequestBody RequestVerifyUserDto verifyUserDto) {
        ResponseUserVerifiedDto verifiedRegisteredUser = authenticationService.verifyUser(action,verifyUserDto);
        return new ResponseEntity<>(verifiedRegisteredUser, HttpStatus.OK);
    }

    //revisar si queda abierta de jwt o no
    @GetMapping("/reenvio-codigo/{action}")
    public ResponseEntity<?> resendVerificationCode(@PathVariable String action,@RequestParam String email) {
        ResponseUserNonVerifiedDto unverifiedRegisteredUser = authenticationService.resendVerificationCode(action,email);
        return new ResponseEntity<>(unverifiedRegisteredUser, HttpStatus.OK);
    }

    @PostMapping("/enviar-codigo/{action}")
    public ResponseEntity<?> verifyCode(@PathVariable String action, @Valid @RequestBody RequestVerifyUserDto verifyUserDto) {
        String code = authenticationService.sendVerificationEmail(action,verifyUserDto.email(),verifyUserDto.verificationCode());
        return new ResponseEntity<>(code, HttpStatus.OK);
    }
}
