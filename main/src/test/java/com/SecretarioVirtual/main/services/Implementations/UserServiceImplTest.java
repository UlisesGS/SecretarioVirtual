package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestEmailUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestPasswordUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.ResponseUserDto;
import com.SecretarioVirtual.main.entities.enums.Role;
import com.SecretarioVirtual.main.exceptions.InvalidDataException;
import com.SecretarioVirtual.main.exceptions.ResourceAlreadyExistsException;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.mappers.UserMapper;
import com.SecretarioVirtual.main.repositories.UserRepository;
import com.SecretarioVirtual.main.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Validations validations;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    private User user;
    private RequestUpdateUserDto requestUpdateUserDto;
    private RequestUpdateMailDto requestUpdateMailDto;
    private RequestPasswordUpdateUserDto requestPasswordUpdateUserDto;
    private RequestEmailUserDto requestEmailUserDto;


    @BeforeEach
    public void setupTest(){
        user = new User("5f60bec8-29a4-430f-9576-13e07b87df97", "Pepe", "Pepe", "pepe@gmail.com", "5491164139370",
                LocalDate.of(2008, 5, 17), "Contra123456!", null, null,
                true, new ArrayList<>(), new ArrayList<>(), true, Role.CLIENT, new ArrayList<>());
    }


    @Nested
    class GetAllUserTest{

        @Test
        public void get_all_users_user_list() {
            // Preparacion de datos de prueba
            User user1 = new User(null, "John", "Doe", "john.doe@example.com", "1234567890", LocalDate.of(1990, 1, 1), "password123", null, null, true, new ArrayList<>(), new ArrayList<>(), true, Role.CLIENT, new ArrayList<>());
            User user2 = new User(null, "Jane", "Smith", "jane.smith@example.com", "0987654321", LocalDate.of(1985, 5, 20), "password456", null, null, true, new ArrayList<>(), new ArrayList<>(), true, Role.CLIENT, new ArrayList<>());
            List<User> userList = Arrays.asList(user1, user2);

            // Mockeando el comportamiento del repositorio y el mapper
            when(userRepository.findAll()).thenReturn(userList);
            when(userMapper.userListToResponseUserDto(userList)).thenReturn(
                    userList.stream()
                            .map(user -> new ResponseUserDto(user.getId(), user.getName(), user.getLastName(),
                                    user.getEmail(), user.getPhone(), user.getDateOfBirth()))
                            .collect(Collectors.toList()) // Convertir el Stream a List de manera segura
            );

            // Ejecutando el servicio
            List<ResponseUserDto> response = userServiceImpl.getAllUsers();

            // Verificaciones
            assertThat(response).isNotNull();
            assertThat(response.size()).isEqualTo(2);
            assertThat(response.get(0).name()).isEqualTo("John");
            assertThat(response.get(1).name()).isEqualTo("Jane");

            // Verificando que se llama a los metodos correctos
            verify(userRepository, times(1)).findAll();
            verify(userMapper, times(1)).userListToResponseUserDto(userList);
        }

        @Test
        public void get_all_users_exception_no_users_found() {
            // Preparacion de datos de prueba
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Verificando que se lanza la excepcion correcta con el mensaje esperado
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userServiceImpl.getAllUsers();
            });

            // Verificando que el mensaje de la excepcion es el esperado
            assertThat(exception.getMessage()).isEqualTo("No hay datos guardados");

            // Verificando que se llama al metodo correcto
            verify(userRepository, times(1)).findAll();
        }

    }

    @Nested
    class GetByEmailTest{

        @Test
        public void get_by_email_UserDto(){
            // Preparacion de datos de prueba
            RequestEmailUserDto requestEmailUserDto = new RequestEmailUserDto("pepe@gmail.com");

            ResponseUserDto responseUserDto = new ResponseUserDto(user.getId(), user.getName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getDateOfBirth());

            // Simulando que el repositorio devuelve un usuario con el email solicitado
            when(userRepository.findByEmail(requestEmailUserDto.email())).thenReturn(Optional.of(user));
            when(userMapper.userToResponseUserDto(user)).thenReturn(responseUserDto);

            // Ejecutando el metodo y verificando la respuesta
            ResponseUserDto result = userServiceImpl.getByEmail(requestEmailUserDto);

            // Verificando que el resultado no sea nulo
            assertNotNull(result);

            // Verificando que los valores sean los esperados
            assertEquals(user.getId(), result.id());
            assertEquals(user.getName(), result.name());
            assertEquals(user.getLastName(), result.lastName());
            assertEquals(user.getEmail(), result.email());
            assertEquals(user.getPhone(), result.phone());
            assertEquals(user.getDateOfBirth(), result.dateOfBirth());

            // Verificando que se llama al metodo correcto
            verify(userRepository, times(1)).findByEmail(requestEmailUserDto.email());
            verify(userMapper, times(1)).userToResponseUserDto(user);
        }



        @Test
        public void get_by_email_ResourceNotFoundException() {
            // Preparacion de datos de prueba
            RequestEmailUserDto requestEmailUserDto = new RequestEmailUserDto("test@example.com");

            // Simulando que el repositorio no encuentra al usuario por el email
            when(userRepository.findByEmail(requestEmailUserDto.email())).thenReturn(Optional.empty());

            // Verificando que se lanza la excepcion ResourceNotFoundException
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
                userServiceImpl.getByEmail(requestEmailUserDto);
            });

            // Mensaje esperado de la excepcion
            String expectedMessage = "El usuario no fue encontrado";
            String actualMessage = exception.getMessage();

            // Verificando que el mensaje de la excepcion es el correcto
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que se llama al metodo correcto
            verify(userRepository, times(1)).findByEmail(requestEmailUserDto.email());
        }
    }

    @Nested
    class UpdateUserTest{

        @Test
        public void update_user_UserDto() throws Exception {
            // Preparacion de datos de prueba
            RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto("Pepe", "Perez", "5491164139370", "test@example.com", LocalDate.of(1990, 1, 1));
            User existingUser = new User();
            existingUser.setName("Juan");
            existingUser.setLastName("Lopez");
            existingUser.setEmail("test@example.com");
            existingUser.setPhone("5491164139370");
            existingUser.setDateOfBirth(LocalDate.of(1990, 1, 1));

            User updatedUser = new User();
            updatedUser.setName("Pepe");
            updatedUser.setLastName("Perez");
            updatedUser.setEmail("test@example.com");
            updatedUser.setPhone("5491164139370");
            updatedUser.setDateOfBirth(LocalDate.of(1990, 1, 1));

            ResponseUpdateUserDto responseUpdateUserDto = new ResponseUpdateUserDto(updatedUser.getPhone(),updatedUser.getName(), updatedUser.getLastName(), updatedUser.getDateOfBirth());

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestUpdateUserDto.email())).thenReturn(Optional.of(existingUser));
            when(userRepository.findByPhone(updatedUser.getPhone())).thenReturn(Optional.empty());
            when(userMapper.updateUserDtoToUser(requestUpdateUserDto)).thenReturn(updatedUser);
            when(userRepository.save(existingUser)).thenReturn(existingUser);
            when(userMapper.userToResponseUpdateUserDto(existingUser)).thenReturn(responseUpdateUserDto);

            // Ejecutando el metodo
            ResponseUpdateUserDto result = userServiceImpl.updateUser(requestUpdateUserDto);

            // Verificando la respuesta
            assertNotNull(result);
            assertEquals(updatedUser.getName(), result.name());
            assertEquals(updatedUser.getLastName(), result.lastName());
            assertEquals(updatedUser.getPhone(), result.phone());
            assertEquals(updatedUser.getDateOfBirth(), result.dateOfBirth());

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateUserDto.email());
            verify(userRepository, times(1)).findByPhone(updatedUser.getPhone());
            verify(userRepository, times(1)).save(existingUser);
            verify(userMapper, times(1)).userToResponseUpdateUserDto(existingUser);
        }

        @Test
        public void update_user_ResourceNotFoundException_User_Not_Found() {
            // Preparacion de datos de prueba
            RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto("Pepe", "Perez", "5491164139370", "nottest@example.com", LocalDate.of(1990, 1, 1));

            // Simulando que el usuario no es encontrado
            when(userRepository.findByEmail(requestUpdateUserDto.email())).thenReturn(Optional.empty());

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
                userServiceImpl.updateUser(requestUpdateUserDto);
            });

            String expectedMessage = "El usuario no fue encontrado";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateUserDto.email());
        }

        @Test
        public void update_user_ResourceAlreadyExistsException_Phone_Already_Taken() {
            // Preparacion de datos de prueba
            RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto("Pepe", "Perez", "5491164139370", "test@example.com", LocalDate.of(1990, 1, 1));
            User existingUser = new User();
            existingUser.setName("Juan");
            existingUser.setLastName("Lopez");
            existingUser.setEmail("test@example.com");
            existingUser.setPhone("5491164139370");
            existingUser.setDateOfBirth(LocalDate.of(1990, 1, 1));

            // Simulamos que el telefono ya esta registrado
            when(userRepository.findByEmail(requestUpdateUserDto.email())).thenReturn(Optional.of(existingUser));
            when(userMapper.updateUserDtoToUser(requestUpdateUserDto)).thenReturn(existingUser);
            when(userRepository.findByPhone(existingUser.getPhone())).thenReturn(Optional.of(existingUser)); // Simulamos que el teléfono ya está registrado

            // Verificando que la excepción es lanzada
            Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
                userServiceImpl.updateUser(requestUpdateUserDto);
            });

            String expectedMessage = "Ya hay una cuenta asociada con el numero de celular " + requestUpdateUserDto.phone() + ".";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateUserDto.email());
            verify(userRepository, times(1)).findByPhone(requestUpdateUserDto.phone());
        }

        @Test
        public void update_user_InvalidDataException_Data_Is_Same() {
            // Preparacion de datos de prueba
            RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto("Pepe", "Perez", "5491164139371", "test@example.com", LocalDate.of(1990, 1, 1));
            User existingUser = new User();
            existingUser.setId("userId");
            existingUser.setName("Juan");
            existingUser.setLastName("Lopez");
            existingUser.setEmail("test@example.com");
            existingUser.setPhone("5491164139370");
            existingUser.setDateOfBirth(LocalDate.of(1990, 1, 1));

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestUpdateUserDto.email())).thenReturn(Optional.of(existingUser));
            when(userMapper.updateUserDtoToUser(requestUpdateUserDto)).thenReturn(existingUser);
            when(userRepository.findByPhone(existingUser.getPhone())).thenReturn(Optional.empty());

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updateUser(requestUpdateUserDto);
            });

            String expectedMessage = "Los nuevos datos son iguales a los ya guardados.";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateUserDto.email());
        }

    }

    @Nested
    class UpdateMailTest {

        @Test
        public void updateMail_Updated_ResponseUpdateMailDto() {
            // Preparacion de datos de prueba
            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto("test@example.com", "newemail@example.com", "123456");
            User existingUser = new User();
            existingUser.setEmail("test@example.com");
            existingUser.setVerificationCode("123456");
            existingUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));

            User updatedUser = new User();
            updatedUser.setEmail("newemail@example.com");
            updatedUser.setVerificationCode(null);
            updatedUser.setVerificationCodeExpiresAt(null);

            ResponseUpdateMailDto responseUpdateMailDto = new ResponseUpdateMailDto(updatedUser.getEmail());

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestUpdateMailDto.email())).thenReturn(Optional.of(existingUser));
            when(userRepository.findByEmail(requestUpdateMailDto.newEmail())).thenReturn(Optional.empty());
            when(userRepository.save(existingUser)).thenReturn(updatedUser);
            when(userMapper.userToResponseUpdateMailDto(updatedUser)).thenReturn(responseUpdateMailDto);

            // Ejecutando el metodo
            ResponseUpdateMailDto result = userServiceImpl.updateMail(requestUpdateMailDto);

            // Verificando la respuesta
            assertNotNull(result);
            assertEquals(updatedUser.getEmail(), result.email());

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.email());
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.newEmail());
            verify(userRepository, times(1)).save(existingUser);
            verify(userMapper, times(1)).userToResponseUpdateMailDto(updatedUser);
        }


        @Test
        public void updateMail_ResourceNotFoundException_User_Not_Found() {
            // Preparacion de datos de prueba
            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto("nonexistent@example.com", "newemail@example.com", "123456");

            // Simulando que el usuario no es encontrado
            when(userRepository.findByEmail(requestUpdateMailDto.email())).thenReturn(Optional.empty());

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
                userServiceImpl.updateMail(requestUpdateMailDto);
            });

            String expectedMessage = "El usuario no fue encontrado";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.email());
        }

        @Test
        public void updateMail_InvalidDataException_VerificationCode_Is_Null() {
            // Preparacion de datos de prueba
            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto("test@example.com", "newemail@example.com", "123456");
            User existingUser = new User();
            existingUser.setEmail("test@example.com");
            existingUser.setVerificationCode(null);

            // Simulando que el usuario es encontrado pero con codigo de verificación nulo
            when(userRepository.findByEmail(requestUpdateMailDto.email())).thenReturn(Optional.of(existingUser));

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updateMail(requestUpdateMailDto);
            });

            String expectedMessage = "Accion incorrecta.";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.email());
        }

        @Test
        public void updateMail_InvalidDataException_NewEmail_Equals_CurrentEmail() {

            String email = "test@example.com";
            String newEmail = "test@example.com";
            String verificationCode = "123456";

            User user = new User();
            user.setEmail(email);
            user.setVerificationCode(verificationCode);

            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto(email, newEmail, verificationCode);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


            InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updateMail(requestUpdateMailDto);
            });


            assertEquals("El nuevo correo electrónico es igual al actual.", exception.getMessage());

            verify(userRepository, times(1)).findByEmail(email);
        }

        @Test
        public void updateMail_ResourceAlreadyExistsException_NewEmail_Already_Exists() {
            // Preparacion de datos de prueba
            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto("test@example.com", "existingemail@example.com", "123456");
            User existingUser = new User();
            existingUser.setEmail("test@example.com");
            existingUser.setVerificationCode("123456");

            // Simulando que el usuario es encontrado y tiene un codigo valido
            when(userRepository.findByEmail(requestUpdateMailDto.email())).thenReturn(Optional.of(existingUser));

            // Simulando que el nuevo correo ya esta registrado
            when(userRepository.findByEmail(requestUpdateMailDto.newEmail())).thenReturn(Optional.of(new User()));

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
                userServiceImpl.updateMail(requestUpdateMailDto);
            });

            String expectedMessage = "Ya hay una cuenta asociada con el email " + requestUpdateMailDto.newEmail() + ".";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.email());
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.newEmail());
        }

        @Test
        public void updateMail_InvalidDataException_VerificationCode_Expired() {
            // Preparacion de datos de prueba
            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto("test@example.com", "newemail@example.com", "123456");
            User existingUser = new User();
            existingUser.setEmail("test@example.com");
            existingUser.setVerificationCode("123456");
            existingUser.setVerificationCodeExpiresAt(LocalDateTime.now().minusDays(1));

            // Simulando que el usuario es encontrado y el codigo esta vencido
            when(userRepository.findByEmail(requestUpdateMailDto.email())).thenReturn(Optional.of(existingUser));

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updateMail(requestUpdateMailDto);
            });

            String expectedMessage = "Código de verificación vencido.";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.email());
        }

        @Test
        public void updateMail_InvalidDataException_VerificationCode_Incorrect() {
            // Preparacion de datos de prueba
            RequestUpdateMailDto requestUpdateMailDto = new RequestUpdateMailDto("test@example.com", "newemail@example.com", "wrongcode");
            User existingUser = new User();
            existingUser.setEmail("test@example.com");
            existingUser.setVerificationCode("123456");
            existingUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusDays(1));

            // Simulando que el usuario es encontrado pero el codigo es incorrecto
            when(userRepository.findByEmail(requestUpdateMailDto.email())).thenReturn(Optional.of(existingUser));

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updateMail(requestUpdateMailDto);
            });

            String expectedMessage = "Código de verificación incorrecto.";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestUpdateMailDto.email());
        }

    }

    @Nested
    class UpdatePasswordTest{

        @Test
        void updatePassword_success() {
            // Arrange
            RequestPasswordUpdateUserDto requestPasswordUpdateUserDto = new RequestPasswordUpdateUserDto("test@example.com", "newPassword", "123456");

            User existingUser = new User();
            existingUser.setEmail("test@example.com");
            existingUser.setVerificationCode("123456");
            existingUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
            existingUser.setPassword("oldPassword");

            User updatedUser = new User();
            updatedUser.setEmail("test@example.com");
            updatedUser.setPassword("encodedNewPassword");
            updatedUser.setVerificationCode(null);
            updatedUser.setVerificationCodeExpiresAt(null);

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestPasswordUpdateUserDto.email())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(requestPasswordUpdateUserDto.password())).thenReturn("encodedNewPassword");
            when(userRepository.save(existingUser)).thenReturn(updatedUser);

            // Ejecutando el metodo
            userServiceImpl.updatePassword(requestPasswordUpdateUserDto);

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestPasswordUpdateUserDto.email());
            verify(userRepository, times(1)).save(existingUser);
            verify(passwordEncoder, times(1)).encode(requestPasswordUpdateUserDto.password());

            // Verificando los cambios en el usuario
            assertEquals("encodedNewPassword", existingUser.getPassword());
            assertNull(existingUser.getVerificationCode());
            assertNull(existingUser.getVerificationCodeExpiresAt());
        }

        @Test
        void updatePassword_ResourceNotFoundException_User_Not_Found() {
            // Arrange
            String email = "nonexistent@example.com";
            RequestPasswordUpdateUserDto requestDto = new RequestPasswordUpdateUserDto(email, "password", "123456");

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userServiceImpl.updatePassword(requestDto);
            });

            assertEquals("El usuario no fue encontrado", exception.getMessage());
        }

        @Test
        void updatePassword_InvalidDataException_Verification_Code_Is_Null() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setEmail(email);
            user.setVerificationCode(null);

            RequestPasswordUpdateUserDto requestDto = new RequestPasswordUpdateUserDto(email, "password", "123456");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act & Assert
            InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updatePassword(requestDto);
            });

            assertEquals("Accion incorrecta.", exception.getMessage());
        }

        @Test
        void updatePassword_InvalidDataException_Verification_Code_Expired() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setEmail(email);
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));

            RequestPasswordUpdateUserDto requestDto = new RequestPasswordUpdateUserDto(email, "password", "123456");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act & Assert
            InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updatePassword(requestDto);
            });

            assertEquals("Código de verificación vencido.", exception.getMessage());
        }

        @Test
        void updatePassword_InvalidDataException_Verification_Code_Is_Incorrect() {
            // Arrange
            String email = "test@example.com";
            User user = new User();
            user.setEmail(email);
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

            RequestPasswordUpdateUserDto requestDto = new RequestPasswordUpdateUserDto(email, "password", "wrongCode");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // Act & Assert
            InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
                userServiceImpl.updatePassword(requestDto);
            });

            assertEquals("Código de verificación incorrecto.", exception.getMessage());
        }

    }

    @Nested
    class DeleteByEmailTest {

        @Test
        public void deleteByEmail_success() {
            // Preparacion de datos de prueba
            RequestEmailUserDto requestEmailUserDto = new RequestEmailUserDto("test@example.com");
            User existingUser = new User();
            existingUser.setEmail("test@example.com");

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestEmailUserDto.email())).thenReturn(Optional.of(existingUser));

            // Ejecutando el metodo
            userServiceImpl.deleteByEmail(requestEmailUserDto);

            // Verificando que los métodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestEmailUserDto.email());
            verify(userRepository, times(1)).deleteByEmail(requestEmailUserDto.email());
        }

        @Test
        public void deleteByEmail_ResourceNotFoundException() {
            // Preparacion de datos de prueba
            RequestEmailUserDto requestEmailUserDto = new RequestEmailUserDto("nottest@example.com");

            // Simulando que el usuario no es encontrado
            when(userRepository.findByEmail(requestEmailUserDto.email())).thenReturn(Optional.empty());

            // Verificando que la excepción es lanzada
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
                userServiceImpl.deleteByEmail(requestEmailUserDto);
            });

            String expectedMessage = "El usuario no fue encontrado";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestEmailUserDto.email());
            verify(userRepository, never()).deleteByEmail(requestEmailUserDto.email());
        }
    }


}


    /*@Nested
    class CreateDonationTrueTest {

        @Test
        public void create_donation_true_should_be_Ok() throws Exception {

            // Mockeando el comportamiento de los repositorios y validaciones
            when(validations.getAuthenticatedUserAndAccount()).thenReturn(new Validations.UserAccountPair(userDonor, accountDonor));
            when(validations.validateTransactionUserFunds(any())).thenReturn(true);
            when(accountRepository.findAccountByNumberAccount(accountBeneficiary.getAccountNumber())).thenReturn(Optional.of(accountBeneficiary));
            when(userRepository.findById("beneficiaryUserId")).thenReturn(Optional.of(userBeneficiary));
            when(donationMapper.requestDtoToDonation(any(RequestDonationDto.class))).thenReturn(donation);
            donation.setAccountIdDonor(accountDonor.getId());
            donation.setAccountIdBeneficiary(userBeneficiary.getId());
            when(donationRepository.save(any(Donation.class))).thenReturn(donation);
            when(accountRepository.save(any(Account.class))).thenReturn(accountDonor);


            // Ejecutando el servicio
            ResponseDonationDtoTrue res = donationServiceImpl.createDonationTrue(requestDonationDtoTrue);
            assertThat(res).isNotNull();
            assertThat(res.id()).isEqualTo(donation.getId());
            assertThat(res.amount()).isEqualTo(donation.getAmount());
            assertThat(res.donorName()).isEqualTo(userDonor.getName());
            assertThat(res.donorLastName()).isEqualTo(userDonor.getSurname());
            assertThat(res.beneficiaryAccountNumber()).isEqualTo(accountBeneficiary.getAccountNumber());
            assertThat(res.beneficiaryName()).isEqualTo(userBeneficiary.getName());
            assertThat(res.beneficiaryLastName()).isEqualTo(userBeneficiary.getSurname());
            assertThat(res.createdAt()).isEqualTo(donation.getCreatedAt());
            assertThat(res.status()).isEqualTo(donation.getStatus().name());

            // Verificando que se llama a los métodos correctos
            verify(validations, times(1)).getAuthenticatedUserAndAccount();
            verify(validations, times(1)).validateTransactionUserFunds(requestDonationDtoTrue.amount());
            verify(accountRepository, times(1)).findAccountByNumberAccount(requestDonationDtoTrue.numberAccountBeneficiary());
            verify(userRepository, times(1)).findById(accountBeneficiary.getUserId());
            verify(donationRepository).save(any(Donation.class));
            verify(accountRepository).save(accountDonor);


        }
    }*/

