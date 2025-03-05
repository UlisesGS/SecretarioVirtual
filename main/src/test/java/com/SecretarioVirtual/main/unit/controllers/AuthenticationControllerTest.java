package com.SecretarioVirtual.main.unit.controllers;

import com.SecretarioVirtual.main.controllers.AuthenticationController;
import com.SecretarioVirtual.main.entities.dtos.security.*;
import com.SecretarioVirtual.main.security.JwtService;
import com.SecretarioVirtual.main.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(value = AuthenticationController.class)
@WithMockUser
public class AuthenticationControllerTest { //DEJO LOS CASOS DE ERROR PARA MAS ADELANTE

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {

        mapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class RegisterUserControllerTest {

        @Test
        public void registerUser_ShouldReturnCreated() throws Exception {
            // Arrange: Preparar las clases de Input y Output
            var requestRegisterDto = new RequestRegisterDto(
                    "Ulises",
                    "Gadea",
                    "ulises@example.com",
                    "Password123!",
                    "123456789",
                    LocalDate.of(2000, 1, 1)
            );

            var responseUserNonVerifiedDto = new ResponseUserNonVerifiedDto(
                    "1a2b3c4d5e",
                    "Ulises",
                    "Gadea",
                    "ulises@example.com",
                    "123456789",
                    LocalDate.of(2000, 1, 1),
                    "ABC123XYZ"
            );

            when(authenticationService.signUp(eq("codigo-registro"), any(RequestRegisterDto.class)))
                    .thenReturn(responseUserNonVerifiedDto);

            // Act: Llamada al método que se quiere probar
            mockMvc.perform(post("/api/autenticacion/registro/codigo-registro")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestRegisterDto)))
                    // Assert: Probar aserciones
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", Matchers.is(responseUserNonVerifiedDto.id())))
                    .andExpect(jsonPath("$.name", Matchers.is(responseUserNonVerifiedDto.name())))
                    .andExpect(jsonPath("$.lastName", Matchers.is(responseUserNonVerifiedDto.lastName())))
                    .andExpect(jsonPath("$.email", Matchers.is(responseUserNonVerifiedDto.email())))
                    .andExpect(jsonPath("$.phone", Matchers.is(responseUserNonVerifiedDto.phone())))
                    .andExpect(jsonPath("$.dateOfBirth", Matchers.is(responseUserNonVerifiedDto.dateOfBirth().toString())))
                    .andExpect(jsonPath("$.verificationCode", Matchers.is(responseUserNonVerifiedDto.verificationCode())));
        }


    }


 /*   @Nested
    class LoginUserControllerTest {

        @Test
        public void loginUser_ShouldReturnOk() throws Exception {
            // Arrange: Crear un objeto RequestLoginDto con datos válidos
            var requestLoginDto = new RequestLoginDto(
                    "securePassword123",
                    "user@example.com"
            );

            // Crear un objeto ResponseUserVerifiedDto con todos los campos
            var userVerified = new ResponseUserVerifiedDto(
                    "12345",                  // ID del usuario
                    "Uliss",                  // Nombre
                    "Gadea",                  // Apellido
                    "user@example.com",       // Email
                    "123456789",              // Teléfono
                    LocalDate.of(2000, 1, 1)  // Fecha de nacimiento
            );

            // Crear un objeto ResponseLoginDto
            var responseLoginDto = new ResponseLoginDto(
                    "12345",               // ID del login
                    "jwt-token-example",   // Token
                    userVerified           // Usuario asociado
            );

            // Simular la lógica del servicio para devolver el ResponseLoginDto
            when(authenticationService.login(any(RequestLoginDto.class))).thenReturn(responseLoginDto);

            // Act & Assert
            mockMvc.perform(post("/api/autenticacion/inicio-sesion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestLoginDto))
                            .header("Authorization", "Bearer " + "jwt-token-example"))  // Asegúrate de agregar el token
                    .andDo(print())  // Imprime la respuesta para depuración
                    .andExpect(status().isOk())  // Verifica que el estado sea 200 OK
                    .andExpect(jsonPath("$.id").value("12345"))  // Verifica el ID del login
                    .andExpect(jsonPath("$.token").value("jwt-token-example"))  // Verifica el token
                    .andExpect(jsonPath("$.user.id").value("12345"))  // Verifica el ID del usuario
                    .andExpect(jsonPath("$.user.name").value("Uliss"))  // Verifica el nombre del usuario
                    .andExpect(jsonPath("$.user.lastName").value("Gadea"))  // Verifica el apellido del usuario
                    .andExpect(jsonPath("$.user.email").value("user@example.com"))  // Verifica el email del usuario
                    .andExpect(jsonPath("$.user.phone").value("123456789"))  // Verifica el teléfono
                    .andExpect(jsonPath("$.user.dateOfBirth").value("2000-01-01"));  // Verifica la fecha de nacimiento

        }
    } */


    @Nested
    class VerifyUserControllerTest {

        @Test
        public void verifyUser_ShouldReturnOk() throws Exception {
            // Arrange: Preparar las clases de Input y Output
            var requestVerifyUserDto = new RequestVerifyUserDto(
                    "popo@gmail.com",
                    "ABC123XYZ"
            );

            var responseUserVerifiedDto = new ResponseUserVerifiedDto(
                    "e6770d7a-1eaa-4517-978c-88b4972455ba",
                    "popo",
                    "popo",
                    "popo@gmail.com",
                    "5491164139373",
                    LocalDate.of(2008, 5, 17)
            );

            when(authenticationService.verifyUser(eq("verificar"), any(RequestVerifyUserDto.class)))
                    .thenReturn(responseUserVerifiedDto);

            // Act: Llamada al método que se quiere probar
            mockMvc.perform(post("/api/autenticacion/verificacion-codigo/verificar")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestVerifyUserDto)))
                    // Assert: Probar aserciones
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", Matchers.is(responseUserVerifiedDto.id())))
                    .andExpect(jsonPath("$.name", Matchers.is(responseUserVerifiedDto.name())))
                    .andExpect(jsonPath("$.lastName", Matchers.is(responseUserVerifiedDto.lastName())))
                    .andExpect(jsonPath("$.email", Matchers.is(responseUserVerifiedDto.email())))
                    .andExpect(jsonPath("$.phone", Matchers.is(responseUserVerifiedDto.phone())))
                    .andExpect(jsonPath("$.dateOfBirth", Matchers.is(responseUserVerifiedDto.dateOfBirth().toString())));
        }
    }


    @Nested
    class ResendVerificationCodeControllerTest {

        @Test
        public void resendVerificationCode_ShouldReturnOk() throws Exception {
            // Arrange: Preparar las clases de Input y Output
            var email = "popo@gmail.com";

            var responseUserNonVerifiedDto = new ResponseUserNonVerifiedDto(
                    "e6770d7a-1eaa-4517-978c-88b4972455ba",
                    "popo",
                    "popo",
                    "popo@gmail.com",
                    "5491164139373",
                    LocalDate.of(2008, 5, 17),
                    "ABC123XYZ"
            );

            when(authenticationService.resendVerificationCode(eq("reenviar-codigo"), eq(email)))
                    .thenReturn(responseUserNonVerifiedDto);

            // Act: Llamada al método que se quiere probar
            mockMvc.perform(get("/api/autenticacion/reenvio-codigo/reenviar-codigo")
                            .with(csrf())
                            .param("email", email))
                    // Assert: Probar aserciones
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", Matchers.is(responseUserNonVerifiedDto.id())))
                    .andExpect(jsonPath("$.name", Matchers.is(responseUserNonVerifiedDto.name())))
                    .andExpect(jsonPath("$.lastName", Matchers.is(responseUserNonVerifiedDto.lastName())))
                    .andExpect(jsonPath("$.email", Matchers.is(responseUserNonVerifiedDto.email())))
                    .andExpect(jsonPath("$.phone", Matchers.is(responseUserNonVerifiedDto.phone())))
                    .andExpect(jsonPath("$.dateOfBirth", Matchers.is(responseUserNonVerifiedDto.dateOfBirth().toString())))
                    .andExpect(jsonPath("$.verificationCode", Matchers.is(responseUserNonVerifiedDto.verificationCode())));
        }
    }


    @Nested
    class ResendVerificationCodeEditControllerTest {

        @Test
        public void resendVerificationCodeEdit_ShouldReturnOk() throws Exception {
            // Arrange: Preparar las clases de Input y Output
            var email = "popo@gmail.com";

            var responseUserNonVerifiedDto = new ResponseUserNonVerifiedDto(
                    "e6770d7a-1eaa-4517-978c-88b4972455ba",
                    "popo",
                    "popo",
                    "popo@gmail.com",
                    "5491164139373",
                    LocalDate.of(2008, 5, 17),
                    "ABC123XYZ"
            );

            when(authenticationService.resendVerificationCode(eq("reenviar-codigo"), eq(email)))
                    .thenReturn(responseUserNonVerifiedDto);

            // Act: Llamada al método que se quiere probar
            mockMvc.perform(get("/api/autenticacion/modificar/reenvio-codigo/reenviar-codigo")
                            .with(csrf())
                            .param("email", email))
                    // Assert: Probar aserciones
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", Matchers.is(responseUserNonVerifiedDto.id())))
                    .andExpect(jsonPath("$.name", Matchers.is(responseUserNonVerifiedDto.name())))
                    .andExpect(jsonPath("$.lastName", Matchers.is(responseUserNonVerifiedDto.lastName())))
                    .andExpect(jsonPath("$.email", Matchers.is(responseUserNonVerifiedDto.email())))
                    .andExpect(jsonPath("$.phone", Matchers.is(responseUserNonVerifiedDto.phone())))
                    .andExpect(jsonPath("$.dateOfBirth", Matchers.is(responseUserNonVerifiedDto.dateOfBirth().toString())))
                    .andExpect(jsonPath("$.verificationCode", Matchers.is(responseUserNonVerifiedDto.verificationCode())));
        }
    }


    @Nested
    class VerifyCodeControllerTest {

        @Test
        public void verifyCode_ShouldReturnOk() throws Exception {
            // Arrange: Preparar las clases de Input y Output
            var verifyUserDto = new RequestVerifyUserDto(
                    "popo@gmail.com",
                    "ABC123XYZ"
            );

            var verificationCodeResponse = "Verification code sent successfully";

            when(authenticationService.sendVerificationEmail(eq("enviar-codigo"), eq(verifyUserDto.email()), eq(verifyUserDto.verificationCode())))
                    .thenReturn(verificationCodeResponse);

            // Act: Llamada al método que se quiere probar
            mockMvc.perform(post("/api/autenticacion/enviar-codigo/enviar-codigo")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(verifyUserDto)))
                    // Assert: Probar aserciones
                    .andExpect(status().isOk())
                    .andExpect(content().string(Matchers.is(verificationCodeResponse)));
        }
    }
}
