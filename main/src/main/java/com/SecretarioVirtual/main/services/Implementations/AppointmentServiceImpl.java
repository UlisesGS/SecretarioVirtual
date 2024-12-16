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

    /*
    1. que la sesión nueva empiece antes que el fin de las que existen && Empiece después del inicio de las que existen.
    2. que la sesión nueva termine antes que el fin de las que existen && Termine después del inicio de las que existen.
*/
    @Transactional
    @Override
    public ResponseAppointmentDto createAppointment(RequestAppointmentDto requestAppointmentDto) {
        String newAppDate = requestAppointmentDto.date().toString();
        var newDateRange = dateFormatter.getDateFromString(newAppDate);
        var scheduledAppointments = appointmentRepository.findAllAppointmentsByDateRange(newDateRange.get(0), newDateRange.get(1));
        for (Appointment appointment : scheduledAppointments){
            if (requestAppointmentDto.date().isAfter(appointment.getDate())
                    && requestAppointmentDto.date().isBefore(appointment.getDate().plus(60, ChronoUnit.MINUTES))){
                throw new ValidationException("El turno se superpone con turnos existentes");
            }
            if (requestAppointmentDto.date().plus(60, ChronoUnit.MINUTES).isAfter(appointment.getDate())
                    && requestAppointmentDto.date().plus(60, ChronoUnit.MINUTES).isBefore(appointment.getDate().plus(60, ChronoUnit.MINUTES))){
                throw new ValidationException("El turno se superpone con turnos existentes");
            }
        }
        var appointment = appointmentMapper.requestDtoToAppointment(requestAppointmentDto);
        appointment.setIsPaid(true); //TODO. Pago de la sesión no debe ser siempre true.
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
        entity.setStatus(enumStatus);
        var responseDto = appointmentMapper.appointmentToResponseDto(entity);
        return responseDto;
    }

    @Transactional
    @Override
    public ResponseAppointmentDto payAppointment(EditAppointmentDto editAppointmentDto) {
        var entity = appointmentRepository.findById(editAppointmentDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("El turno no fue encontrado"));
        //TODO. Ver de manejar la excepción con el globalHandler.
        //TODO. Integrar MercadoPago y ver si este método queda acá o se mueve de service.
        entity.setIsPaid(editAppointmentDto.isPaid());
        var responseDto = appointmentMapper.appointmentToResponseDto(entity);
        return responseDto;
    }

    @Override
    public ResponseAppointmentDto getAppointmentById(String appointmentId) {
        var entity = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("El turno no fue encontrado"));
        var responseDto = appointmentMapper.appointmentToResponseDto(entity);
        return responseDto;
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatient(String patientId) {
        //TODO. Self validation + Admin.
        //TODO. Ver de manejar la excepción con el globalHandler.
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        List<Appointment> entities = appointmentRepository.findAllByClientId(patientId);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatientAndStatus(String patientId, String status) {
        //TODO. Self validation + Admin.
        //TODO. Ver de manejar la excepción con el globalHandler.
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        var enumStatus = validations.statusConvert(status);
        List<Appointment> entities = appointmentRepository.findAllByClientIdAndStatus(patientId, enumStatus);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatientAndPaid(String patientId, Boolean paid) {
        //TODO. Self validation + Admin.
        //TODO. Ver de manejar la excepción con el globalHandler.
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        List<Appointment> entities = appointmentRepository.findAllByClientIdAndIsPaid(patientId, paid);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAppointmentsByPatientBetweenDates(String patientId, String fromDate, String toDate) {
        //TODO. Self validation + Admin.
        //TODO. Ver de manejar la excepción con el globalHandler.
        userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no fue encontrado"));
        var ldtDate = dateFormatter.getDateFromString(fromDate, toDate);
        List<Appointment> entities = appointmentRepository.findAllAppointmentsByClientIdAndDateRange(patientId, ldtDate.get(0), ldtDate.get(1));
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointments() {
        //TODO. Validation ONLY Admin.
        var entities = appointmentRepository.findAll();
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsByStatus(String status) {
        //TODO. Validation ONLY Admin.
        var enumStatus = validations.statusConvert(status);
        List<Appointment> entities = appointmentRepository.findAllByStatus(enumStatus);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsByPaid(Boolean paid) {
        //TODO. Validation ONLY Admin.
        List<Appointment> entities = appointmentRepository.findAllByIsPaid(paid);
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsByDate(String date) {
        //TODO. Validation ONLY Admin.
        var ldtDate = dateFormatter.getDateFromString(date);
        List<Appointment> entities = appointmentRepository.findAllAppointmentsByDateRange(ldtDate.get(0), ldtDate.get(1));
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseAppointmentDto> getAllAppointmentsBetweenDates(String fromDate, String toDate) {
        //TODO. Validation ONLY Admin.
        var ldtDate = dateFormatter.getDateFromString(fromDate, toDate);
        List<Appointment> entities = appointmentRepository.findAllAppointmentsByDateRange(ldtDate.get(0), ldtDate.get(1));
        return appointmentMapper.appointmentListToResponseDtoList(entities);
    }
}
