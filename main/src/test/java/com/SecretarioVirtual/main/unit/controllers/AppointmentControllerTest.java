package com.SecretarioVirtual.main.unit.controllers;

import com.SecretarioVirtual.main.controllers.AppointmentController;
import com.SecretarioVirtual.main.entities.dtos.appointment.EditAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.RequestAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.ResponseAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;
import com.SecretarioVirtual.main.entities.enums.AppointmentStatus;
import com.SecretarioVirtual.main.entities.enums.Days;
import com.SecretarioVirtual.main.exceptions.InvalidDateFormatException;
import com.SecretarioVirtual.main.exceptions.InvalidUserCredentialsException;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.exceptions.ValidationException;
import com.SecretarioVirtual.main.security.JwtService;
import com.SecretarioVirtual.main.services.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AppointmentController.class)
@WithMockUser
public class AppointmentControllerTest {
    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private JwtService jwtService;
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Nested
    class CreateAppointmentTests {
        @Test
        public void create_appointment_should_be_ok() throws Exception {
            var requestAppointmentDto = new RequestAppointmentDto("Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    "PENDING", false);
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.PENDING, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.createAppointment(any())).thenReturn(responseAppointmentDto);

            mockMvc.perform(post("/api/turno/crear")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestAppointmentDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$.startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$.isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void create_appointment_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            var requestAppointmentDto = new RequestAppointmentDto("Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    "PENDING", false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.createAppointment(any())).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(post("/api/turno/crear")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestAppointmentDto))
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void create_appointment_should_NOT_be_ok_due_to_wrong_date() throws Exception {
            var requestAppointmentDto = new RequestAppointmentDto("Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    "PENDING", false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.createAppointment(any())).thenThrow(new ValidationException(
                    "El turno se superpone con otro turno existente")
            );
            mockMvc.perform(post("/api/turno/crear")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestAppointmentDto))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno se superpone con otro turno existente")));
        }
    }

    @Nested
    class ChangeStatusAppointmentTest {
        @Test
        public void change_status_appointment_should_be_ok() throws Exception {
            var editAppointmentDto = new EditAppointmentDto("Appointment123",
                    AppointmentStatus.ACCEPTED.name(), false);
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.changeStatusAppointment(any())).thenReturn(responseAppointmentDto);

            mockMvc.perform(put("/api/turno/cambiar-estado")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(editAppointmentDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$.startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$.isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void change_status_appointment_should_NOT_be_ok_due_to_not_found() throws Exception {
            var editAppointmentDto = new EditAppointmentDto("Appointment123",
                    AppointmentStatus.ACCEPTED.name(), false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.changeStatusAppointment(any())).thenThrow(new ResourceNotFoundException(
                    "El turno no fue encontrado")
            );
            mockMvc.perform(put("/api/turno/cambiar-estado")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(editAppointmentDto))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }

        @Test
        public void change_status_appointment_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            var editAppointmentDto = new EditAppointmentDto("Appointment123",
                    AppointmentStatus.ACCEPTED.name(), false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.changeStatusAppointment(any())).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(put("/api/turno/cambiar-estado")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(editAppointmentDto))
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

    }

    @Nested
    class PayAppointmentTest {
        @Test
        public void pay_appointment_should_be_ok() throws Exception {
            var editAppointmentDto = new EditAppointmentDto("Appointment123",
                    AppointmentStatus.ACCEPTED.name(), true);
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, true);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.payAppointment(any())).thenReturn(responseAppointmentDto);

            mockMvc.perform(put("/api/turno/pagar")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(editAppointmentDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$.startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$.isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void pay_appointment_should_NOT_be_ok_due_to_not_found() throws Exception {
            var editAppointmentDto = new EditAppointmentDto("Appointment123",
                    AppointmentStatus.ACCEPTED.name(), false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.payAppointment(any())).thenThrow(new ResourceNotFoundException(
                    "El turno no fue encontrado")
            );
            mockMvc.perform(put("/api/turno/pagar")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(editAppointmentDto))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }
    }

    @Nested
    class GetAllAppointmentsTests {
        @Test
        public void get_all_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAllAppointments()).thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_all_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAllAppointments()).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/turno/todos"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }
    }

    @Nested
    class GetAllAppointmentsByStatusTests {
        @Test
        public void get_all_by_status_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAllAppointmentsByStatus("ACCEPTED")).thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/todos/estado/ACCEPTED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_all_by_status_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAllAppointmentsByStatus("ACCEPTED")).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/turno/todos/estado/ACCEPTED"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

    }

    @Nested
    class GetAllAppointmentsByPaidTests {
        @Test
        public void get_all_by_paid_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAllAppointmentsByPaid(false)).thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/todos/pago/false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_all_by_paid_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAllAppointmentsByPaid(false)).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/turno/todos/pago/false"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

    }

    @Nested
    class GetAllAppointmentsByDateTests {
        @Test
        public void get_all_by_date_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAllAppointmentsByDate("2025-01-06")).thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/todos/fecha/2025-01-06"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_all_by_date_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAllAppointmentsByDate("2025-01-06")).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/turno/todos/fecha/2025-01-06"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_all_by_date_should_NOT_be_ok_due_to_invalid_date_format() throws Exception {
            when(appointmentService.getAllAppointmentsByDate("2025-01-06")).thenThrow(new InvalidDateFormatException(
                    "Formato de fecha erroneo. Debe ingresar yyyy-MM-dd")
            );
            mockMvc.perform(get("/api/turno/todos/fecha/2025-01-06"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is(
                            "Formato de fecha erroneo. Debe ingresar yyyy-MM-dd")));
        }
}

    @Nested
    class GetAllAppointmentsBetweenDatesTests {
        @Test
        public void get_all_between_date_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAllAppointmentsBetweenDates("2025-01-06", "2025-01-10"))
                    .thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/todos/2025-01-06/2025-01-10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_all_between_date_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAllAppointmentsBetweenDates("2025-01-06", "2025-01-10"))
                    .thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/turno/todos/2025-01-06/2025-01-10"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_all_between_date_should_NOT_be_ok_due_to_invalid_date_format() throws Exception {
            when(appointmentService.getAllAppointmentsBetweenDates("2025-01-06", "2025-01-10"))
                    .thenThrow(new InvalidDateFormatException(
                    "Formato de fecha erroneo. Debe ingresar yyyy-MM-dd")
            );
            mockMvc.perform(get("/api/turno/todos/2025-01-06/2025-01-10"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is(
                            "Formato de fecha erroneo. Debe ingresar yyyy-MM-dd")));
        }
    }

    @Nested
    class GetAppointmentByIdTests {
        @Test
        public void get_appointment_by_id_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAppointmentById("Appointment123")).thenReturn(responseAppointmentDto);
            mockMvc.perform(get("/api/turno/id/Appointment123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$.startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$.status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$.isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_appointment_by_id_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAppointmentById("Appointment123"))
                    .thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/turno/id/Appointment123"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_appointment_by_id_should_NOT_be_ok_due_to_not_found() throws Exception {
            when(appointmentService.getAppointmentById("Appointment123"))
                    .thenThrow(new ResourceNotFoundException("El turno no fue encontrado")
                    );
            mockMvc.perform(get("/api/turno/id/Appointment123"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }

    }

    @Nested
    class GetAppointmentsByPatientIdTests {
        @Test
        public void get_appointment_by_patient_id_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAppointmentsByPatient("Client123"))
                    .thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/paciente/Client123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_appointment_by_patient_id_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAppointmentsByPatient("Client123"))
                    .thenThrow(new InvalidUserCredentialsException(
                            "El usuario no tiene permiso para esta acción")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_appointment_by_patient_id_should_NOT_be_ok_due_to_not_found() throws Exception {
            when(appointmentService.getAppointmentsByPatient("Client123"))
                    .thenThrow(new ResourceNotFoundException("El turno no fue encontrado")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }

    }

    @Nested
    class GetAppointmentsByPatientIdAndStatusTests {
        @Test
        public void get_appointment_by_patient_id_and_status_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAppointmentsByPatientAndStatus("Client123", "ACCEPTED"))
                    .thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/paciente/Client123/estado/ACCEPTED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_appointment_by_patient_id_and_status_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAppointmentsByPatientAndStatus("Client123", "ACCEPTED"))
                    .thenThrow(new InvalidUserCredentialsException(
                            "El usuario no tiene permiso para esta acción")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123/estado/ACCEPTED"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_appointment_by_patient_id_and_status_should_NOT_be_ok_due_to_not_found() throws Exception {
            when(appointmentService.getAppointmentsByPatientAndStatus("Client123", "ACCEPTED"))
                    .thenThrow(new ResourceNotFoundException("El turno no fue encontrado")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123/estado/ACCEPTED"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }

    }

    @Nested
    class GetAppointmentsByPatientIdAndPaidTests {
        @Test
        public void get_appointment_by_patient_id_and_paid_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAppointmentsByPatientAndPaid("Client123", false))
                    .thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/paciente/Client123/pago/false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_appointment_by_patient_id_and_paid_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAppointmentsByPatientAndPaid("Client123", false))
                    .thenThrow(new InvalidUserCredentialsException(
                            "El usuario no tiene permiso para esta acción")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123/pago/false"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_appointment_by_patient_id_and_paid_should_NOT_be_ok_due_to_not_found() throws Exception {
            when(appointmentService.getAppointmentsByPatientAndPaid("Client123", false))
                    .thenThrow(new ResourceNotFoundException("El turno no fue encontrado")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123/pago/false"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }

    }

    @Nested
    class GetAppointmentsByPatientIdBetweenDatesTests {
        @Test
        public void get_appointment_by_patient_id_between_dates_should_be_ok() throws Exception {
            var responseAppointmentDto = new ResponseAppointmentDto("Appointment123", "Client123",
                    LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(105),
                    AppointmentStatus.ACCEPTED, false);
            mapper.registerModule(new JavaTimeModule());
            when(appointmentService.getAppointmentsByPatientBetweenDates("Client123",
                    "2025-01-06","2025-01-10"))
                    .thenReturn(List.of(responseAppointmentDto));
            mockMvc.perform(get("/api/turno/paciente/Client123/2025-01-06/2025-01-10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clientId", Matchers.is(responseAppointmentDto.clientId())))
                    .andExpect(jsonPath("$[0].startDate", Matchers.startsWith(responseAppointmentDto.startDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].endDate", Matchers.startsWith(responseAppointmentDto.endDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                    .andExpect(jsonPath("$[0].status", Matchers.is(responseAppointmentDto.status().toString())))
                    .andExpect(jsonPath("$[0].isPaid", Matchers.is(responseAppointmentDto.isPaid())));
        }

        @Test
        public void get_appointment_by_patient_id_between_dates_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            when(appointmentService.getAppointmentsByPatientBetweenDates("Client123",
                    "2025-01-06","2025-01-10"))
                    .thenThrow(new InvalidUserCredentialsException(
                            "El usuario no tiene permiso para esta acción")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123/2025-01-06/2025-01-10"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void get_appointment_by_patient_id_between_dates_should_NOT_be_ok_due_to_not_found() throws Exception {
            when(appointmentService.getAppointmentsByPatientBetweenDates("Client123",
                    "2025-01-06","2025-01-10"))
                    .thenThrow(new ResourceNotFoundException("El turno no fue encontrado")
                    );
            mockMvc.perform(get("/api/turno/paciente/Client123/2025-01-06/2025-01-10"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", Matchers.is("El turno no fue encontrado")));
        }

    }
}
