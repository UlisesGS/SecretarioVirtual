package com.SecretarioVirtual.main.unit.controllers;

import com.SecretarioVirtual.main.controllers.UserController;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.RequestUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateMailDto;
import com.SecretarioVirtual.main.entities.dtos.security.ResponseUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestEmailUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.RequestPasswordUpdateUserDto;
import com.SecretarioVirtual.main.entities.dtos.security.User.ResponseUserDto;
import com.SecretarioVirtual.main.security.JwtService;
import com.SecretarioVirtual.main.services.UserService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
@WithMockUser
public class UserControllerTest {

    @MockBean
    private UserService userService;

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
    class FindAllTest {

        @Test
        public void findAll_ShouldReturnOk() throws Exception {
            // Arrange: Preparar datos para la respuesta
            var user1 = new ResponseUserDto(
                    "e6770d7a-1eaa-4517-978c-88b4972455ba",
                    "Ulises",
                    "Gadea",
                    "ulises@example.com",
                    "123456789",
                    LocalDate.of(2000, 1, 1)
            );

            var user2 = new ResponseUserDto(
                    "b77b0d7a-1eaa-4517-978c-88b4972455bb",
                    "Carlos",
                    "Perez",
                    "carlos@example.com",
                    "987654321",
                    LocalDate.of(1995, 5, 15)
            );

            var users = List.of(user1, user2);

            // Configuración del mock del servicio
            when(userService.getAllUsers()).thenReturn(users);

            // Act: Llamada al endpoint que se quiere probar
            mockMvc.perform(get("/api/usuario/todos-usuarios")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    // Assert: Probar aserciones
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id", Matchers.is(user1.id())))
                    .andExpect(jsonPath("$[0].name", Matchers.is(user1.name())))
                    .andExpect(jsonPath("$[0].lastName", Matchers.is(user1.lastName())))
                    .andExpect(jsonPath("$[0].email", Matchers.is(user1.email())))
                    .andExpect(jsonPath("$[0].phone", Matchers.is(user1.phone())))
                    .andExpect(jsonPath("$[0].dateOfBirth", Matchers.is(user1.dateOfBirth().toString())))
                    .andExpect(jsonPath("$[1].id", Matchers.is(user2.id())))
                    .andExpect(jsonPath("$[1].name", Matchers.is(user2.name())))
                    .andExpect(jsonPath("$[1].lastName", Matchers.is(user2.lastName())))
                    .andExpect(jsonPath("$[1].email", Matchers.is(user2.email())))
                    .andExpect(jsonPath("$[1].phone", Matchers.is(user2.phone())))
                    .andExpect(jsonPath("$[1].dateOfBirth", Matchers.is(user2.dateOfBirth().toString())));
        }
    }


    @Nested
    class FindByEmailTest {

        @Test
        public void findByEmail_ShouldReturnOk() throws Exception {
            // Arrange: Preparar datos para la respuesta
            var requestEmailUserDto = new RequestEmailUserDto("ulises@example.com");

            var responseUserDto = new ResponseUserDto(
                    "e6770d7a-1eaa-4517-978c-88b4972455ba",
                    "Ulises",
                    "Gadea",
                    "ulises@example.com",
                    "123456789",
                    LocalDate.of(2000, 1, 1)
            );

            // Configuración del mock del servicio
            when(userService.getByEmail(eq(requestEmailUserDto))).thenReturn(responseUserDto);

            // Act: Llamada al endpoint que se quiere probar
            mockMvc.perform(get("/api/usuario/buscar-usuario")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestEmailUserDto)))
                    // Assert: Probar aserciones
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", Matchers.is(responseUserDto.id())))
                    .andExpect(jsonPath("$.name", Matchers.is(responseUserDto.name())))
                    .andExpect(jsonPath("$.lastName", Matchers.is(responseUserDto.lastName())))
                    .andExpect(jsonPath("$.email", Matchers.is(responseUserDto.email())))
                    .andExpect(jsonPath("$.phone", Matchers.is(responseUserDto.phone())))
                    .andExpect(jsonPath("$.dateOfBirth", Matchers.is(responseUserDto.dateOfBirth().toString())));
        }
    }


    @Nested
    class UpdateUserTest {

        @Test
        public void updateUser_ShouldReturnOk() throws Exception {
            // Arrange: Preparar datos de entrada (Request) y salida esperada (Response)
            var requestUpdateUserDto = new RequestUpdateUserDto(
                    "Ulises",
                    "Gadea",
                    "123456789",
                    "ulises@example.com",
                    LocalDate.of(2000, 1, 1)
            );

            var responseUpdateUserDto = new ResponseUpdateUserDto(
                    "123456789",
                    "Ulises",
                    "Gadea",
                    LocalDate.of(2000, 1, 1)
            );

            // Configuración del mock del servicio
            when(userService.updateUser(eq(requestUpdateUserDto))).thenReturn(responseUpdateUserDto);

            // Act: Realizar la llamada al endpoint con PUT
            mockMvc.perform(put("/api/usuario/editar-usuario")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestUpdateUserDto)))
                    // Assert: Comprobar las respuestas
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phone", Matchers.is(responseUpdateUserDto.phone())))
                    .andExpect(jsonPath("$.name", Matchers.is(responseUpdateUserDto.name())))
                    .andExpect(jsonPath("$.lastName", Matchers.is(responseUpdateUserDto.lastName())))
                    .andExpect(jsonPath("$.dateOfBirth", Matchers.is(responseUpdateUserDto.dateOfBirth().toString())));
        }
    }


    @Nested
    class UpdatePasswordTest {

        @Test
        public void updatePassword_ShouldReturnOk() throws Exception {
            // Arrange: Preparar datos de entrada (Request)
            var requestPasswordUpdateUserDto = new RequestPasswordUpdateUserDto(
                    "ulises@example.com",
                    "Password123!",
                    "ABC123XYZ"
            );

            // Act: Realizar la llamada al endpoint con PUT
            mockMvc.perform(put("/api/usuario/editar-contraseña")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestPasswordUpdateUserDto)))
                    // Assert: Comprobar que la respuesta es OK (200)
                    .andExpect(status().isOk());
        }
    }


    @Nested
    class UpdateMailTest {

        @Test
        public void updateMail_ShouldReturnOk() throws Exception {
            // Arrange: Preparar datos de entrada (Request)
            var requestUpdateMailDto = new RequestUpdateMailDto(
                    "ulises@example.com",
                    "new-ulises@example.com",
                    "ABC123XYZ"
            );

            // Crear el Response esperado
            var responseUpdateMailDto = new ResponseUpdateMailDto("new-ulises@example.com");

            // Simular el comportamiento del servicio
            when(userService.updateMail(eq(requestUpdateMailDto)))
                    .thenReturn(responseUpdateMailDto);

            // Act: Realizar la llamada al endpoint con PUT
            mockMvc.perform(put("/api/usuario/editar-mail")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestUpdateMailDto)))
                    // Assert: Comprobar que la respuesta es OK (200) y que el email actualizado está en el cuerpo de la respuesta
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", Matchers.is(responseUpdateMailDto.email())));
        }
    }


    @Nested
    class DeleteByEmail {

        @Test
        public void deleteByEmail_ShouldReturnOk() throws Exception {
            // Arrange: Preparar datos de entrada (Request)
            var requestEmailUserDto = new RequestEmailUserDto("ulises@example.com");

            // Simular el comportamiento del servicio
            doNothing().when(userService).deleteByEmail(eq(requestEmailUserDto));

            // Act: Realizar la llamada al endpoint con DELETE
            mockMvc.perform(delete("/api/usuario/eliminar-usuario")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestEmailUserDto)))
                    // Assert: Comprobar que la respuesta es OK (200)
                    .andExpect(status().isOk());
        }
    }
}
