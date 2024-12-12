package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.*;
import com.SecretarioVirtual.main.entities.enums.Role;
import com.SecretarioVirtual.main.exceptions.InvalidDataException;
import com.SecretarioVirtual.main.exceptions.InvalidUserCredentialsException;
import com.SecretarioVirtual.main.exceptions.ResourceAlreadyExistsException;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.mappers.UserMapper;
import com.SecretarioVirtual.main.repositories.UserRepository;
import com.SecretarioVirtual.main.security.EmailService;
import com.SecretarioVirtual.main.security.JwtService;
import com.SecretarioVirtual.main.services.AuthenticationService;
import com.SecretarioVirtual.main.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    @Transactional
    public ResponseUserNonVerifiedDto signUp(@Valid RequestRegisterDto requestRegisterDto) {
        if (userRepository.findByEmail(requestRegisterDto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya hay una cuenta asociada con el email " + requestRegisterDto.email() + ".");
        }

        if (userRepository.findByPhone(requestRegisterDto.phone()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya hay una cuenta asociada con el numero de celular " + requestRegisterDto.phone() + ".");
        }

        /* if (Period.between(requestRegisterDto.dateOfBirth(), LocalDate.now()).getYears() < 18) {
            throw new InvalidDataException("Debes tener al menos 18 años para registrarte.");
        } */

        User user = userMapper.registerUserToUser(requestRegisterDto);
        user.setPassword(passwordEncoder.encode(requestRegisterDto.password()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        user.setRole(Role.CLIENT);
        user.setCredentialsNonExpired(true);
        sendVerificationEmail(user);
        User savedUser = userRepository.save(user);

        return userMapper.userToUserNonVerifiedDto(savedUser);
    }

    @Override
    @Transactional
    public ResponseLoginDto login(RequestLoginDto requestLoginDto) {
        User user = userRepository.findByEmail(requestLoginDto.email()).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        if (!user.isEnabled()) {
            throw new InvalidUserCredentialsException("Cuenta no verificada. Por favor verifique su cuenta.");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLoginDto.email(),
                            requestLoginDto.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidUserCredentialsException("Email y/o contraseña inválidos.");
        }

        String jwtToken = jwtService.generateToken(user);
        ResponseUserVerifiedDto userDto = userMapper.userToUserVerifiedDto(user);

        return new ResponseLoginDto(user.getId(), jwtToken, userDto);
    }


    public ResponseUserVerifiedDto verifyUser(RequestVerifyUserDto verifyUserDto) {
        Optional<User> optionalUser = userRepository.findByEmail(verifyUserDto.email());

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado.");
        }

        User user = optionalUser.get();

        if (user.getVerificationCode() == null) {
            throw new InvalidDataException("Cuenta ya se encuentra verificada.");
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Código de verificación vencido.");
        }

        if (!user.getVerificationCode().equals(verifyUserDto.verificationCode())) {
            throw new InvalidDataException("Código de verificación incorrecto.");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        User savedUser = userRepository.save(user);

        return userMapper.userToUserVerifiedDto(savedUser);
    }


    public ResponseUserNonVerifiedDto resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado.");
        }

        User user = optionalUser.get();

        if (user.isEnabled()) {
            throw new InvalidDataException("La cuenta ya se encuentra verificada.");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
        sendVerificationEmail(user);
        User savedUser = userRepository.save(user);

        return userMapper.userToUserNonVerifiedDto(savedUser);
    }


    private void sendVerificationEmail(User user) {
        String subject = "Verificación de cuenta";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">¡Bienvenido a Secretario Virtual!</h2>"
                + "<p style=\"font-size: 16px;\">Por favor ingresa el siguiente código debajo para continuar:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Código de Verificación:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}