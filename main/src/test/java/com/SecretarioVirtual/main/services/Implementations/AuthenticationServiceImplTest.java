package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.User;
import com.SecretarioVirtual.main.entities.dtos.security.*;
import com.SecretarioVirtual.main.exceptions.*;
import com.SecretarioVirtual.main.mappers.UserMapper;
import com.SecretarioVirtual.main.repositories.UserRepository;
import com.SecretarioVirtual.main.security.EmailService;
import com.SecretarioVirtual.main.security.JwtService;
import com.SecretarioVirtual.main.validations.Validations;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    @Mock
    private Validations validations;

    @Spy
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;



    @Nested
    class SignUpTest {

        @Test
        public void signUp_success() {
            // Preparacion de datos de prueba
            String action = "codigo-registro";
            RequestRegisterDto requestRegisterDto = new RequestRegisterDto("John", "Doe", "test@example.com", "password123", "5491164139370", LocalDate.of(2000, 1, 1));
            User user = new User();
            user.setEmail("test@example.com");
            user.setPassword("encodedPassword");
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            User savedUser = new User();
            savedUser.setId("12345");
            savedUser.setName("John");
            savedUser.setLastName("Doe");
            savedUser.setEmail("test@example.com");
            savedUser.setPhone("5491164139370");
            savedUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
            savedUser.setVerificationCode("123456");
            savedUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

            ResponseUserNonVerifiedDto responseDto = new ResponseUserNonVerifiedDto(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getLastName(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getDateOfBirth(),
                    savedUser.getVerificationCode()
            );

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestRegisterDto.email())).thenReturn(Optional.empty());
            when(userRepository.findByPhone(requestRegisterDto.phone())).thenReturn(Optional.empty());
            when(userMapper.registerUserToUser(requestRegisterDto)).thenReturn(user);
            when(passwordEncoder.encode(requestRegisterDto.password())).thenReturn("encodedPassword");
            when(userRepository.save(user)).thenReturn(savedUser);
            when(userMapper.userToUserNonVerifiedDto(savedUser)).thenReturn(responseDto);

            // Ejecutando el metodo
            ResponseUserNonVerifiedDto result = authenticationService.signUp(action, requestRegisterDto);

            // Verificando la respuesta
            assertNotNull(result);
            assertEquals(savedUser.getId(), result.id());
            assertEquals(savedUser.getName(), result.name());
            assertEquals(savedUser.getLastName(), result.lastName());
            assertEquals(savedUser.getEmail(), result.email());
            assertEquals(savedUser.getPhone(), result.phone());
            assertEquals(savedUser.getDateOfBirth(), result.dateOfBirth());
            assertEquals(savedUser.getVerificationCode(), result.verificationCode());

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestRegisterDto.email());
            verify(userRepository, times(1)).findByPhone(requestRegisterDto.phone());
            verify(userRepository, times(1)).save(user);
            verify(userMapper, times(1)).userToUserNonVerifiedDto(savedUser);
        }

        @Test
        public void signUp_ResourceAlreadyExistsException_email_already_exists() {
            // Preparacion de datos de prueba
            RequestRegisterDto requestRegisterDto = new RequestRegisterDto("John", "Doe", "test@example.com", "password123", "5491164139370", LocalDate.of(2000, 1, 1));
            when(userRepository.findByEmail(requestRegisterDto.email())).thenReturn(Optional.of(new User()));

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
                authenticationService.signUp("codigo-registro", requestRegisterDto);
            });

            String expectedMessage = "Ya hay una cuenta asociada con el email " + requestRegisterDto.email() + ".";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestRegisterDto.email());
            verify(userRepository, never()).findByPhone(requestRegisterDto.phone());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        public void signUp_ResourceAlreadyExistsException_phone_already_exists() {
            // Preparacion de datos de prueba
            RequestRegisterDto requestRegisterDto = new RequestRegisterDto("John", "Doe", "test@example.com", "password123", "5491164139370", LocalDate.of(2000, 1, 1));
            when(userRepository.findByEmail(requestRegisterDto.email())).thenReturn(Optional.empty());
            when(userRepository.findByPhone(requestRegisterDto.phone())).thenReturn(Optional.of(new User()));

            // Verificando que la excepcion es lanzada
            Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
                authenticationService.signUp("codigo-registro", requestRegisterDto);
            });

            String expectedMessage = "Ya hay una cuenta asociada con el numero de celular " + requestRegisterDto.phone() + ".";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los metodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestRegisterDto.email());
            verify(userRepository, times(1)).findByPhone(requestRegisterDto.phone());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class LoginTest {

        @Test
        public void login_success() {
            // Preparación de datos de prueba
            RequestLoginDto requestLoginDto = new RequestLoginDto("test@example.com", "password123");
            User user = new User();
            user.setId("12345");
            user.setName("John");
            user.setLastName("Doe");
            user.setEmail("test@example.com");
            user.setPhone("1234567890");
            user.setDateOfBirth(LocalDate.of(1990, 1, 1));
            user.setEnabled(true);

            String jwtToken = "generatedJwtToken";
            ResponseUserVerifiedDto userDto = new ResponseUserVerifiedDto(
                    "12345",
                    "John",
                    "Doe",
                    "test@example.com",
                    "1234567890",
                    LocalDate.of(1990, 1, 1)
            );

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(requestLoginDto.email())).thenReturn(Optional.of(user));
            when(jwtService.generateToken(user)).thenReturn(jwtToken);
            when(userMapper.userToUserVerifiedDto(user)).thenReturn(userDto);

            // No es necesario usar doNothing() aquí, simplemente simula el comportamiento
            doAnswer(invocation -> null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

            // Ejecutando el método
            ResponseLoginDto result = authenticationService.login(requestLoginDto);

            // Verificando la respuesta
            assertNotNull(result);
            assertEquals(user.getId(), result.id());
            assertEquals(jwtToken, result.token());
            assertEquals(userDto, result.user());

            // Verificando que los métodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestLoginDto.email());
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService, times(1)).generateToken(user);
            verify(userMapper, times(1)).userToUserVerifiedDto(user);
        }


        @Test
        public void login_userNotFound_ResourceNotFoundException() {
            // Preparación de datos de prueba
            RequestLoginDto requestLoginDto = new RequestLoginDto("test@example.com", "password123");
            when(userRepository.findByEmail(requestLoginDto.email())).thenReturn(Optional.empty());

            // Verificando que la excepción es lanzada
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
                authenticationService.login(requestLoginDto);
            });

            String expectedMessage = "Usuario no encontrado.";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los métodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestLoginDto.email());
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        public void login_accountNotVerified_InvalidUserCredentialsException() {
            // Preparación de datos de prueba
            RequestLoginDto requestLoginDto = new RequestLoginDto("test@example.com", "password123");

            // Creación del usuario con enabled = false
            User user = new User();
            user.setId("12345");
            user.setEmail("test@example.com");
            user.setEnabled(false); // Asegurarse de que el usuario no esté habilitado

            // Mock del repositorio
            when(userRepository.findByEmail(requestLoginDto.email())).thenReturn(Optional.of(user));

            // Verificación de que la excepción es lanzada
            Exception exception = assertThrows(InvalidUserCredentialsException.class, () -> {
                authenticationService.login(requestLoginDto);
            });

            String expectedMessage = "Cuenta no verificada. Por favor verifique su cuenta.";
            assertTrue(exception.getMessage().contains(expectedMessage));

            // Verificación de interacciones
            verify(userRepository, times(1)).findByEmail(requestLoginDto.email());
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }


        @Test
        public void login_invalidCredentials_InvalidUserCredentialsException() {
            // Preparación de datos de prueba
            RequestLoginDto requestLoginDto = new RequestLoginDto("test@example.com", "wrongPassword");
            User user = new User();
            user.setEnabled(true);
            when(userRepository.findByEmail(requestLoginDto.email())).thenReturn(Optional.of(user));

            doThrow(new BadCredentialsException("Invalid credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

            // Verificando que la excepción es lanzada
            Exception exception = assertThrows(InvalidUserCredentialsException.class, () -> {
                authenticationService.login(requestLoginDto);
            });

            String expectedMessage = "Email y/o contraseña inválidos.";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

            // Verificando que los métodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(requestLoginDto.email());
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }

    @Nested
    class VerifyUserTest {

        @Test
        public void verifyUser_success() {
            // Preparación de datos de prueba
            String action = "verify";
            RequestVerifyUserDto verifyUserDto = new RequestVerifyUserDto("test@example.com", "123456");

            User user = new User();
            user.setId("1");
            user.setName("John");
            user.setLastName("Doe");
            user.setEmail(verifyUserDto.email());
            user.setPhone("123456789");
            user.setDateOfBirth(LocalDate.of(1990, 1, 1));
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            user.setEnabled(false);

            User savedUser = new User();
            savedUser.setId("1");
            savedUser.setName("John");
            savedUser.setLastName("Doe");
            savedUser.setEmail(verifyUserDto.email());
            savedUser.setPhone("123456789");
            savedUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
            savedUser.setEnabled(true);

            ResponseUserVerifiedDto responseDto = new ResponseUserVerifiedDto(
                    "1",
                    "John",
                    "Doe",
                    "test@example.com",
                    "123456789",
                    LocalDate.of(1990, 1, 1)
            );

            // Mock del repositorio y mapper
            when(userRepository.findByEmail(verifyUserDto.email())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(userMapper.userToUserVerifiedDto(savedUser)).thenReturn(responseDto);

            // Ejecución del método
            ResponseUserVerifiedDto result = authenticationService.verifyUser(action, verifyUserDto);

            // Verificación de resultados
            assertNotNull(result);
            assertEquals(responseDto.id(), result.id());
            assertEquals(responseDto.name(), result.name());
            assertEquals(responseDto.lastName(), result.lastName());
            assertEquals(responseDto.email(), result.email());
            assertEquals(responseDto.phone(), result.phone());
            assertEquals(responseDto.dateOfBirth(), result.dateOfBirth());
            assertTrue(savedUser.isEnabled());

            verify(userRepository, times(1)).findByEmail(verifyUserDto.email());
            verify(userRepository, times(1)).save(user);
            verify(userMapper, times(1)).userToUserVerifiedDto(savedUser);
        }


        @Test
        public void verifyUser_userNotFound_throwsResourceNotFoundException() {
            // Preparación de datos de prueba
            String action = "verify";
            RequestVerifyUserDto verifyUserDto = new RequestVerifyUserDto("test@example.com", "123456");

            // Mock del repositorio
            when(userRepository.findByEmail(verifyUserDto.email())).thenReturn(Optional.empty());

            // Verificación de la excepción
            Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
                authenticationService.verifyUser(action, verifyUserDto);
            });

            String expectedMessage = "Usuario no encontrado.";
            assertEquals(expectedMessage, exception.getMessage());

            verify(userRepository, times(1)).findByEmail(verifyUserDto.email());
            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).userToUserVerifiedDto(any(User.class));
        }

        @Test
        public void verifyUser_alreadyVerified_throwsInvalidDataException() {
            // Preparación de datos de prueba
            String action = "verify";
            RequestVerifyUserDto verifyUserDto = new RequestVerifyUserDto("test@example.com", "123456");

            User user = new User();
            user.setEmail(verifyUserDto.email());
            user.setVerificationCode(null); // Indica que ya está verificado

            // Mock del repositorio
            when(userRepository.findByEmail(verifyUserDto.email())).thenReturn(Optional.of(user));

            // Verificación de la excepción
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                authenticationService.verifyUser(action, verifyUserDto);
            });

            String expectedMessage = "Cuenta ya se encuentra verificada.";
            assertEquals(expectedMessage, exception.getMessage());

            verify(userRepository, times(1)).findByEmail(verifyUserDto.email());
            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).userToUserVerifiedDto(any(User.class));
        }

        @Test
        public void verifyUser_verificationCodeExpired_throwsInvalidDataException() {
            // Preparación de datos de prueba
            String action = "verify";
            RequestVerifyUserDto verifyUserDto = new RequestVerifyUserDto("test@example.com", "123456");

            User user = new User();
            user.setEmail(verifyUserDto.email());
            user.setVerificationCode("123456");
            user.setVerificationCodeExpiresAt(LocalDateTime.now().minusHours(1)); // Código expirado

            // Mock del repositorio
            when(userRepository.findByEmail(verifyUserDto.email())).thenReturn(Optional.of(user));

            // Verificación de la excepción
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                authenticationService.verifyUser(action, verifyUserDto);
            });

            String expectedMessage = "Código de verificación vencido.";
            assertEquals(expectedMessage, exception.getMessage());

            verify(userRepository, times(1)).findByEmail(verifyUserDto.email());
            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).userToUserVerifiedDto(any(User.class));
        }

        @Test
        public void verifyUser_incorrectVerificationCode_throwsInvalidDataException() {
            // Preparación de datos de prueba
            String action = "verify";
            RequestVerifyUserDto verifyUserDto = new RequestVerifyUserDto("test@example.com", "wrong-code");

            User user = new User();
            user.setEmail(verifyUserDto.email());
            user.setVerificationCode("123456"); // Código correcto
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));

            // Mock del repositorio
            when(userRepository.findByEmail(verifyUserDto.email())).thenReturn(Optional.of(user));

            // Verificación de la excepción
            Exception exception = assertThrows(InvalidDataException.class, () -> {
                authenticationService.verifyUser(action, verifyUserDto);
            });

            String expectedMessage = "Código de verificación incorrecto.";
            assertEquals(expectedMessage, exception.getMessage());

            verify(userRepository, times(1)).findByEmail(verifyUserDto.email());
            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).userToUserVerifiedDto(any(User.class));
        }
    }

    @Nested
    class ResendVerificationCodeTest{

        @Test
        public void resendVerificationCode_success() {
            // Preparación de datos de prueba
            String action = "codigo-registro";
            String email = "test@example.com";
            User user = new User();
            user.setId("12345");
            user.setName("John");
            user.setLastName("Doe");
            user.setEmail(email);
            user.setPhone("1234567890");
            user.setDateOfBirth(LocalDate.of(1990, 1, 1));
            user.setEnabled(false);  // Usuario no verificado
            user.setVerificationCode(null);  // Sin código de verificación inicial

            String generatedCode = "123456";  // Este será el código de verificación generado
            LocalDateTime verificationCodeExpiresAt = LocalDateTime.now().plusHours(1);

            // Creamos el ResponseUserNonVerifiedDto esperado
            ResponseUserNonVerifiedDto userDto = new ResponseUserNonVerifiedDto(
                    "12345",
                    "John",
                    "Doe",
                    "test@example.com",
                    "1234567890",
                    LocalDate.of(1990, 1, 1),
                    generatedCode
            );

            // Simulando el comportamiento de las dependencias
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.userToUserNonVerifiedDto(user)).thenReturn(userDto);

            // Ejecutando el método
            ResponseUserNonVerifiedDto result = authenticationService.resendVerificationCode(action, email);

            // Verificando la respuesta
            assertNotNull(result);
            assertEquals(user.getId(), result.id());
            assertEquals(user.getEmail(), result.email());
            assertEquals(generatedCode, result.verificationCode()); // Verificando que el código de verificación es el esperado

            // Verificando que el código de verificación y la fecha de expiración fueron generados correctamente
            assertNotNull(user.getVerificationCode());
            assertNotNull(user.getVerificationCodeExpiresAt());
            assertTrue(user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()));

            // Verificando que el método de envío de correo fue llamado
            verify(authenticationService, times(1)).sendVerificationEmail(action, user.getEmail(), user.getVerificationCode());

            // Verificando que los métodos correctos fueron llamados
            verify(userRepository, times(1)).findByEmail(email);
            verify(userRepository, times(1)).save(user);
            verify(userMapper, times(1)).userToUserNonVerifiedDto(user);
        }

        @Test
        void testResendVerificationCode_UserNotFound() {
            // Preparación del test
            User user = new User();
            user.setEmail("test@example.com");

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

            // Ejecución y verificación de la excepción
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                authenticationService.resendVerificationCode("codigo-registro", user.getEmail());
            });

            assertEquals("Usuario no encontrado.", exception.getMessage());
        }

        @Test
        void testResendVerificationCode_AccountAlreadyVerified() {
            // Preparación del test
            User user = new User();
            user.setEmail("test@example.com");
            user.setEnabled(true); // Cuenta ya verificada
            user.setVerificationCode("123456");
            user.setId("user-id");
            user.setName("John");
            user.setLastName("Doe");
            user.setPhone("123-456-7890");
            user.setDateOfBirth(LocalDate.of(1990, 1, 1));

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

            // Ejecución y verificación de la excepción
            InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
                authenticationService.resendVerificationCode("codigo-registro", user.getEmail());
            });

            assertEquals("La cuenta ya se encuentra verificada.", exception.getMessage());
        }




    }

    @Nested
    class SendVerificatioEmailTest{

        @Test
        void testSendVerificationEmail_OK() throws MessagingException {
            // Datos de prueba
            String action = "codigo-registro";
            String mail = "test@example.com";
            String verificationCode = "123456";

            // Simulamos que el método de envío de correo no lanza excepciones
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

            // Llamamos al método
            String result = authenticationService.sendVerificationEmail(action, mail, verificationCode);

            // Verificamos que el código de verificación es el que se pasa
            assertEquals(verificationCode, result);

            // Verificamos que el método sendVerificationEmail fue llamado con los parámetros correctos
            verify(emailService, times(1)).sendVerificationEmail(
                    eq(mail), // Compara el email
                    eq("Verificación de cuenta"), // Compara el subject
                    contains(verificationCode) // Verifica que el mensaje contiene el código de verificación
            );
        }


        @Test
        void testSendVerificationEmail_InvalidAction() {
            // Datos de prueba
            String action = "invalid-action";
            String mail = "test@example.com";
            String verificationCode = "123456";

            // Llamamos al método y verificamos que se lanza la excepción
            InvalidDataException thrown = assertThrows(InvalidDataException.class, () -> {
                authenticationService.sendVerificationEmail(action, mail, verificationCode);
            });

            // Verificamos el mensaje de la excepción
            assertEquals("Action de envio de codigo incorrecto.", thrown.getMessage());
        }

        @Test
        void testSendVerificationEmail_MessagingException() throws MessagingException {
            // Datos de prueba
            String action = "codigo-registro";
            String mail = "test@example.com";
            String verificationCode = "123456";

            // Simulamos que el servicio de correo lanza una excepción
            doThrow(MessagingException.class).when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

            // Llamamos al método y verificamos que se lanza la excepción MailSendingException
            MailSendingException thrown = assertThrows(MailSendingException.class, () -> {
                authenticationService.sendVerificationEmail(action, mail, verificationCode);
            });

            // Verificamos el mensaje de la excepción
            assertEquals("Error para enviar el mail.", thrown.getMessage());
        }
    }
}
