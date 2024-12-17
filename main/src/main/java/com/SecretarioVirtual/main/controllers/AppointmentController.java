package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.appointment.EditAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.RequestAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.ResponseAppointmentDto;
import com.SecretarioVirtual.main.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turno")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/crear")
    public ResponseEntity<ResponseAppointmentDto> createAppointment(
            @RequestBody @Valid RequestAppointmentDto requestAppointmentDto) {
        ResponseAppointmentDto result = appointmentService.createAppointment(requestAppointmentDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/cambiar-estado")
    public ResponseEntity<ResponseAppointmentDto> changeStatusAppointment(
            @RequestBody @Valid EditAppointmentDto editAppointmentDto) {
        ResponseAppointmentDto result = appointmentService.changeStatusAppointment(editAppointmentDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/pagar")
    public ResponseEntity<ResponseAppointmentDto> payAppointment(
            @RequestBody @Valid EditAppointmentDto editAppointmentDto) {
        ResponseAppointmentDto result = appointmentService.payAppointment(editAppointmentDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/id/{appointmentId}")
    public ResponseEntity<ResponseAppointmentDto> getAppointmentById(@PathVariable String appointmentId) {
        ResponseAppointmentDto result = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/paciente/{patientId}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatient(@PathVariable String patientId) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/paciente/{patientId}/estado/{status}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatientAndStatus(@PathVariable String patientId, @PathVariable String status) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatientAndStatus(patientId, status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/paciente/{patientId}/pago/{paid}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatientAndPaid(@PathVariable String patientId, @PathVariable Boolean paid) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatientAndPaid(patientId, paid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/paciente/{patientId}/entre-fechas")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatientBetweenDates(
            @PathVariable String patientId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatientBetweenDates(patientId, fromDate, toDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointments() {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointments();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/todos/estado/{status}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsByStatus(@PathVariable String status) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsByStatus(status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/todos/pago/{paid}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsByPaid(@PathVariable Boolean paid) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsByPaid(paid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("todos/fecha/{date}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsByDate(@PathVariable String date) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsByDate(date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/todos/entre-fechas")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsBetweenDates(
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsBetweenDates(fromDate, toDate);
        return ResponseEntity.ok(result);
    }

}
