package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.Appointment;
import com.SecretarioVirtual.main.entities.dtos.appointment.EditAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.RequestAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.ResponseAppointmentDto;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.exceptions.ValidationException;
import com.SecretarioVirtual.main.mappers.AppointmentMapper;
import com.SecretarioVirtual.main.repositories.AppointmentRepository;
import com.SecretarioVirtual.main.repositories.UserRepository;
import com.SecretarioVirtual.main.services.AppointmentService;
import com.SecretarioVirtual.main.utils.DateFormatter;
import com.SecretarioVirtual.main.validations.Validations;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final Validations validations;
    private final DateFormatter dateFormatter;


    @Transactional
    @Override
    public ResponseAppointmentDto createAppointment(RequestAppointmentDto requestAppointmentDto) {
        validations.selfOrAdminValidation(requestAppointmentDto.clientId());
        String newAppDate = requestAppointmentDto.startDate().toLocalDate().toString();
        var newDateRange = dateFormatter.getDateFromString(newAppDate);
        var scheduledAppointments = appointmentRepository.findAllAppointmentsByDateRange(newDateRange.get(0), newDateRange.get(1));
        for (Appointment existent : scheduledAppointments) {
            if ((requestAppointmentDto.startDate().isAfter(existent.getStartDate())
                    && requestAppointmentDto.startDate().isBefore(existent.getEndDate())) ||
                    (requestAppointmentDto.endDate().isAfter(existent.getStartDate())
                            && requestAppointmentDto.endDate().isBefore(existent.getEndDate())) ||
                    (requestAppointmentDto.endDate().isAfter(existent.getEndDate())
                            && requestAppointmentDto.startDate().isBefore(existent.getStartDate())) ||
                    (requestAppointmentDto.startDate().equals(existent.getStartDate())) ||
                    (requestAppointmentDto.endDate().equals(existent.getEndDate()))) {
                throw new ValidationException("El turno se superpone con otro turno existente");
            }
        }
        var appointment = appointmentMapper.requestDtoToAppointment(requestAppointmentDto);
        //TODO. Pago de la sesión no debe ser siempre true.
        var saved = appointmentRepository.save(appointment);
        var responseDto = appointmentMapper.appointmentToResponseDto(saved);
        return responseDto;
    }

    @Transactional
    @Override
    public ResponseAppointmentDto changeStatusAppointment(EditAppointmentDto editAppointmentDto) {
        var entity = appointmentRepository.findById(editAppointmentDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("El turno no fue encontrado"));
        var enumStatus = validations.statusConvert(editAppointmentDto.status());
        validations.selfOrAdminValidation(entity.getClientId());
        entity.setStatus(enumStatus);
        var responseDto = appointmentMapper.appointmentToResponseDto(entity);
        return responseDto;
    }

    @Transactional
    @Override
    public ResponseAppointmentDto payAppointment(EditAppointmentDto editAppointmentDto) {
        var entity = appointmentRepository.findById(editAppointmentDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("El turno no fue encontrado"));
        validations.selfOrAdminValidation(entity.getClientId());
        //TODO. Integrar MercadoPago y ver si este método queda acá o se mueve de service.
        entity.setIsPaid(editAppointmentDto.isPaid());
        var responseDto = appointmentMapper.appointmentToResponseDto(entity);
        return responseDto;
    }

    @Override
    public ResponseAppointmentDto getAppointmentById(String appointmentId) {
        var entity = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("El turno no fue encontrado"));
        validations.selfOrAdminValidation(entity.getClientId());
        var responseDto = appointmentMapper.appointmentToResponseDto(entity);
        return responseDto;
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatient(String patientId) {
        validations.selfOrAdminValidation(patientId);
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        List<Appointment> entities = appointmentRepository.findAllByClientId(patientId);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatientAndStatus(String patientId, String status) {
        validations.selfOrAdminValidation(patientId);
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        var enumStatus = validations.statusConvert(status);
        List<Appointment> entities = appointmentRepository.findAllByClientIdAndStatus(patientId, enumStatus);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatientAndPaid(String patientId, Boolean paid) {
        validations.selfOrAdminValidation(patientId);
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        List<Appointment> entities = appointmentRepository.findAllByClientIdAndIsPaid(patientId, paid);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatientBetweenDates(String patientId, String fromDate, String toDate) {
        validations.selfOrAdminValidation(patientId);
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        var ldtDate = dateFormatter.getDateFromString(fromDate, toDate);
        List<Appointment> entities = appointmentRepository.findAllAppointmentsByClientIdAndDateRange(patientId, ldtDate.get(0), ldtDate.get(1));
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointments() {
        validations.adminValidation();
        var entities = appointmentRepository.findAll();
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsByStatus(String status) {
        validations.adminValidation();
        var enumStatus = validations.statusConvert(status);
        List<Appointment> entities = appointmentRepository.findAllByStatus(enumStatus);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsByPaid(Boolean paid) {
        validations.adminValidation();
        List<Appointment> entities = appointmentRepository.findAllByIsPaid(paid);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsByDate(String date) {
        validations.adminValidation();
        var ldtDate = dateFormatter.getDateFromString(date);
        List<Appointment> entities = appointmentRepository.findAllAppointmentsByDateRange(ldtDate.get(0), ldtDate.get(1));
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsBetweenDates(String fromDate, String toDate) {
        validations.adminValidation();
        var ldtDate = dateFormatter.getDateFromString(fromDate, toDate);
        List<Appointment> entities = appointmentRepository.findAllAppointmentsByDateRange(ldtDate.get(0), ldtDate.get(1));
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }
}
