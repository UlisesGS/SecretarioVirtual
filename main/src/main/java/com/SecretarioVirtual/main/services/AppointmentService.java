package com.SecretarioVirtual.main.services;

import com.SecretarioVirtual.main.entities.dtos.EditAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.RequestAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.ResponseAppointmentDto;

import java.util.List;

public interface AppointmentService {

    ResponseAppointmentDto createAppointment(RequestAppointmentDto requestAppointmentDto);
    ResponseAppointmentDto changeStatusAppointment(EditAppointmentDto requestAppointmentDto);
    ResponseAppointmentDto payAppointment(EditAppointmentDto requestAppointmentDto);

    ResponseAppointmentDto getAppointmentById(String appointmentId);
    List<ResponseAppointmentDto> getAppointmentsByPatient(String patientId);
    List<ResponseAppointmentDto> getAppointmentsByPatientAndStatus(String patientId, String status);
    List<ResponseAppointmentDto> getAppointmentsByPatientAndPaid(String patientId, Boolean paid);
    List<ResponseAppointmentDto> getAppointmentsByPatientBetweenDates(String patientId, String fromDate, String toDate);

    List<ResponseAppointmentDto> getAllAppointments();
    List<ResponseAppointmentDto> getAllAppointmentsByStatus(String status);
    List<ResponseAppointmentDto> getAllAppointmentsByPaid(Boolean paid);
    List<ResponseAppointmentDto> getAllAppointmentsByDate(String date);
    List<ResponseAppointmentDto> getAllAppointmentsBetweenDates(String fromDate, String toDate);
}
