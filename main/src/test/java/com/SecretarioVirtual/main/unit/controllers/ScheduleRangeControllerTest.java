package com.SecretarioVirtual.main.unit.controllers;

import com.SecretarioVirtual.main.controllers.ScheduleRangeController;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;
import com.SecretarioVirtual.main.entities.enums.Days;
import com.SecretarioVirtual.main.exceptions.InvalidUserCredentialsException;
import com.SecretarioVirtual.main.exceptions.ValidationException;
import com.SecretarioVirtual.main.security.JwtService;
import com.SecretarioVirtual.main.services.ScheduleRangeService;
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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ScheduleRangeController.class)
@WithMockUser
public class ScheduleRangeControllerTest {
    @MockBean
    private ScheduleRangeService scheduleRangeService;
    @MockBean
    private JwtService jwtService;
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Nested
    class CreateScheduleRangeTests {
        @Test
        public void create_schedule_range_should_be_ok() throws Exception {
            var requestScheduleRangeDto = new RequestScheduleRangeDto(Days.MONDAY,
                    LocalTime.of(8, 00), LocalTime.of(16, 00),
                    45, 5);
            var responseScheduleRangeDto = new ResponseScheduleRangeDto("ABC123", Days.MONDAY,
                    LocalTime.of(8, 00), LocalTime.of(16, 00),
                    45, 5, 9);
            mapper.registerModule(new JavaTimeModule());
            when(scheduleRangeService.createScheduleRange(any())).thenReturn(responseScheduleRangeDto);

            mockMvc.perform(post("/api/horario/crear")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestScheduleRangeDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.day", Matchers.is(responseScheduleRangeDto.day().name())))
                    .andExpect(jsonPath("$.startsAt", Matchers.is(responseScheduleRangeDto.startsAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                    .andExpect(jsonPath("$.endsAt", Matchers.is(responseScheduleRangeDto.endsAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                    .andExpect(jsonPath("$.appointmentDuration", Matchers.is(responseScheduleRangeDto.appointmentDuration())))
                    .andExpect(jsonPath("$.breakTime", Matchers.is(responseScheduleRangeDto.breakTime())));
        }

        @Test
        public void create_schedule_range_should_NOT_be_ok_due_to_invalid_credentials() throws Exception {
            var requestScheduleRangeDto = new RequestScheduleRangeDto(Days.MONDAY,
                    LocalTime.of(8, 00), LocalTime.of(16, 00),
                    45, 5);
            mapper.registerModule(new JavaTimeModule());
            when(scheduleRangeService.createScheduleRange(any())).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(post("/api/horario/crear")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestScheduleRangeDto))
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

        @Test
        public void create_schedule_range_should_NOT_be_ok_due_to_wrong_date() throws Exception {
            var requestScheduleRangeDto = new RequestScheduleRangeDto(Days.MONDAY,
                    LocalTime.of(16, 00), LocalTime.of(8, 00),
                    45, 5);
            mapper.registerModule(new JavaTimeModule());
            when(scheduleRangeService.createScheduleRange(any())).thenThrow(new ValidationException(
                    "La fecha de inicio NO puede ser posterior a la de fin.")
            );
            mockMvc.perform(post("/api/horario/crear")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestScheduleRangeDto))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is("La fecha de inicio NO puede ser posterior a la de fin.")));
        }
    }

    @Nested
    class GetAllScheduleRangeTests {
        @Test
        public void get_all_should_be_ok() throws Exception {
            var responseScheduleRangeDto = new ResponseScheduleRangeDto("ABC123", Days.MONDAY,
                    LocalTime.of(8, 00), LocalTime.of(16, 00),
                    45, 5, 9);
            mapper.registerModule(new JavaTimeModule());
            when(scheduleRangeService.getAllScheduleRanges()).thenReturn(List.of(responseScheduleRangeDto));
            mockMvc.perform(get("/api/horario/todos/dia/MONDAY"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(jsonPath("$[0].day", Matchers.is(responseScheduleRangeDto.day().name())))
                    .andExpect(jsonPath("$[0].startsAt", Matchers.is(responseScheduleRangeDto.startsAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                    .andExpect(jsonPath("$[0].endsAt", Matchers.is(responseScheduleRangeDto.endsAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                    .andExpect(jsonPath("$[0].appointmentDuration", Matchers.is(responseScheduleRangeDto.appointmentDuration())))
                    .andExpect(jsonPath("$[0].breakTime", Matchers.is(responseScheduleRangeDto.breakTime())));
        }

        @Test
        public void get_all_should_NOT_be_ok() throws Exception {
            when(scheduleRangeService.getAllScheduleRanges()).thenThrow(new InvalidUserCredentialsException(
                    "El usuario no tiene permiso para esta acción")
            );
            mockMvc.perform(get("/api/horario/todos"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }
    }

    @Nested
    class GetAllByDayScheduleRangeTests {
        @Test
        public void get_all_by_day_should_be_ok() throws Exception {
            var responseScheduleRangeDto = new ResponseScheduleRangeDto("ABC123", Days.MONDAY,
                    LocalTime.of(8, 00), LocalTime.of(16, 00),
                    45, 5, 9);
            mapper.registerModule(new JavaTimeModule());
            when(scheduleRangeService.getAllScheduleRangesByDay(any()))
                    .thenReturn(List.of(responseScheduleRangeDto));
            mockMvc.perform(get("/api/horario/todos/dia/MONDAY"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasSize(1)))
                    .andExpect(jsonPath("$[0].day", Matchers.is(responseScheduleRangeDto.day().name())))
                    .andExpect(jsonPath("$[0].startsAt", Matchers.is(responseScheduleRangeDto.startsAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                    .andExpect(jsonPath("$[0].endsAt", Matchers.is(responseScheduleRangeDto.endsAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                    .andExpect(jsonPath("$[0].appointmentDuration", Matchers.is(responseScheduleRangeDto.appointmentDuration())))
                    .andExpect(jsonPath("$[0].breakTime", Matchers.is(responseScheduleRangeDto.breakTime())));
        }

        @Test
        public void get_by_status_should_NOT_be_ok_due_NOT_FOUND() throws Exception {
            when(scheduleRangeService.getAllScheduleRangesByDay(Days.MONDAY.name()))
                    .thenThrow(new InvalidUserCredentialsException(
                            "El usuario no tiene permiso para esta acción")
                    );
            mockMvc.perform(get("/api/horario/todos/dia/MONDAY"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

    }

    @Nested
    class DeleteScheduleRangeTests {
        @Test
        public void delete_should_be_ok() throws Exception {
            String id = "ABC123";
            when(scheduleRangeService.deleteScheduleRangeId(id)).thenReturn(true);
            mockMvc.perform(delete("/api/horario/borrar/ABC123")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.is(true)));
        }

        @Test
        public void delete_should_NOT_be_ok() throws Exception {
            String id = "ABC123";
            when(scheduleRangeService.deleteScheduleRangeId(id))
                    .thenThrow(new InvalidUserCredentialsException(
                            "El usuario no tiene permiso para esta acción")
                    );
            mockMvc.perform(delete("/api/horario/borrar/ABC123")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is("El usuario no tiene permiso para esta acción")));
        }

    }
}
