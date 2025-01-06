package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestEmailUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestPasswordUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.ResponseUserDto;
import com.SecretarioVirtual.main.exceptions.InvalidDataException;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.mappers.UserMapper;
import com.SecretarioVirtual.main.repositories.UserRepository;
import com.SecretarioVirtual.main.services.UserService;
import com.SecretarioVirtual.main.validations.Validations;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validations validations;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ResponseUserDto> getAllUsers() {
        validations.adminValidation();
        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            throw new ResourceNotFoundException("No hay datos guardados");
        }
        return userMapper.userListToResponseUserDto(userList);
    }


    @Override
    public ResponseUserDto getByEmail(RequestEmailUserDto requestEmailUserDto) {
        validations.adminValidation();
        User user = userRepository.findByEmail(requestEmailUserDto.email()).orElseThrow(() ->
                new ResourceNotFoundException("El usuario no fue encontrado"));
        return userMapper.userToResponseUserDto(user);
    }


    @Override
    @Transactional
    public ResponseUpdateUserDto updateUser(RequestUpdateUserDto requestUpdateUserDto) {
        User user = userRepository.findByEmail(requestUpdateUserDto.email()).orElseThrow(() ->
                new ResourceNotFoundException("El usuario no fue encontrado"));

        User userDto = userMapper.updateUserDtoToUser(requestUpdateUserDto);

        if(user.getName().equals(userDto.getName()) &&
            user.getLastName().equals(userDto.getLastName()) &&
            user.getPhone().equals(userDto.getPhone()) &&
            user.getDateOfBirth().equals(userDto.getDateOfBirth())){
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
    public ResponseUpdateMailDto updateMail(RequestUpdateMailDto requestUpdateMailDto) {
        User user = userRepository.findByEmail(requestUpdateMailDto.email())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no fue encontrado"));

        if (user.getVerificationCode()==null){
            throw new InvalidDataException("Accion incorrecta.");
        }

        if (user.getEmail().equals(requestUpdateMailDto.newEmail())) {
            throw new InvalidDataException("El nuevo correo electrónico es igual al actual.");
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Código de verificació" +
                    "n vencido.");
        }

        if (!user.getVerificationCode().equals(requestUpdateMailDto.verificationCode())) {
            throw new InvalidDataException("Código de verificación incorrecto.");
        }

        user.setEmail(requestUpdateMailDto.newEmail());
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

        return userMapper.userToResponseUpdateMailDto(user);
    }


    @Override
    @Transactional
    public void updatePassword(RequestPasswordUpdateUserDto requestPasswordUpdateUserDto) {
        validations.selfOrAdminValidationEmail(requestPasswordUpdateUserDto.email());

        User user = userRepository.findByEmail(requestPasswordUpdateUserDto.email())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no fue encontrado"));

        if (user.getVerificationCode()==null){
            throw new InvalidDataException("Accion incorrecta.");
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Código de verificació" +
                    "n vencido.");
        }

        if (!user.getVerificationCode().equals(requestPasswordUpdateUserDto.verificationCode())) {
            throw new InvalidDataException("Código de verificación incorrecto.");
        }


        user.setPassword(passwordEncoder.encode(requestPasswordUpdateUserDto.password()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void deleteByEmail(RequestEmailUserDto requestEmailUserDto) {
        validations.adminValidation();
        userRepository.findByEmail(requestEmailUserDto.email()).orElseThrow(
                () -> new ResourceNotFoundException("El usuario no fue encontrado"));
        userRepository.deleteByEmail(requestEmailUserDto.email());
    }
}
