package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.EditAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.RequestAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.ResponseAppointmentDto;
import com.SecretarioVirtual.main.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<ResponseAppointmentDto> createAppointment(
            @RequestBody @Valid RequestAppointmentDto requestAppointmentDto) {
        ResponseAppointmentDto result = appointmentService.createAppointment(requestAppointmentDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/changeStatus")
    public ResponseEntity<ResponseAppointmentDto> changeStatusAppointment(
            @RequestBody @Valid EditAppointmentDto editAppointmentDto) {
        ResponseAppointmentDto result = appointmentService.changeStatusAppointment(editAppointmentDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/payAppointment")
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

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatient(@PathVariable String patientId) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatientAndStatus(@PathVariable String patientId, @PathVariable String status) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatientAndStatus(patientId, status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patient/{patientId}/paid/{paid}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatientAndPaid(@PathVariable String patientId, @PathVariable Boolean paid) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatientAndPaid(patientId, paid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patient/{patientId}/between-dates")
    public ResponseEntity<List<ResponseAppointmentDto>> getAppointmentsByPatientBetweenDates(
            @PathVariable String patientId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<ResponseAppointmentDto> result = appointmentService.getAppointmentsByPatientBetweenDates(patientId, fromDate, toDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointments() {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointments();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all/status/{status}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsByStatus(@PathVariable String status) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsByStatus(status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all/paid/{paid}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsByPaid(@PathVariable Boolean paid) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsByPaid(paid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("all/date/{date}")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsByDate(@PathVariable String date) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsByDate(date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all/between-dates")
    public ResponseEntity<List<ResponseAppointmentDto>> getAllAppointmentsBetweenDates(
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<ResponseAppointmentDto> result = appointmentService.getAllAppointmentsBetweenDates(fromDate, toDate);
        return ResponseEntity.ok(result);
    }

}
