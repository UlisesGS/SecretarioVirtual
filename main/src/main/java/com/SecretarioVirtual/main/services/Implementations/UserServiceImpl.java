package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.exceptions.InvalidDataException;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.mappers.UserMapper;
import com.SecretarioVirtual.main.repositories.UserRepository;
import com.SecretarioVirtual.main.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ResponseUpdateUserDto updateUser(RequestUpdateUserDto requestUpdateUserDto) {
        User user = userRepository.findByEmail(requestUpdateUserDto.email()).orElseThrow(() ->
                new ResourceNotFoundException("El usuario no fue encontrado"));

        User userDto = userMapper.updateUserDtoToUser(requestUpdateUserDto);

        if(user.getName().equals(userDto.getName()) &&
            user.getLastName().equals(userDto.getLastName()) &&
            user.getPhone().equals(userDto.getPhone())){
            throw new InvalidDataException("Los nuevos datos son iguales a los ya guardados.");
        }

        user.setPhone(requestUpdateUserDto.phone());
        user.setName(requestUpdateUserDto.name());
        user.setDateOfBirth(requestUpdateUserDto.dateOfBirth());
        user.setLastName(requestUpdateUserDto.lastName());
        userRepository.save(user);
        return userMapper.userToResponseUpdateUserDto(user);
    }

    @Override
    @Transactional
    //PREGUNTARLE A GUILLE SI ESTA BIEN RECIBIR ID EN BODY
    public ResponseUpdateMailDto updateMail(RequestUpdateMailDto requestUpdateMailDto) {
        User user = userRepository.findByEmail(requestUpdateMailDto.email())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no fue encontrado"));

        if (user.getEmail().equals(requestUpdateMailDto.emailNuevo())) {
            throw new InvalidDataException("El nuevo correo electrónico es igual al actual.");
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Código de verificación vencido.");
        }

        if (!user.getVerificationCode().equals(requestUpdateMailDto.verificationCode())) {
            throw new InvalidDataException("Código de verificación incorrecto.");
        }

        user.setEmail(requestUpdateMailDto.emailNuevo());
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

        return userMapper.userToResponseUpdateMailDto(user);
    }

}
