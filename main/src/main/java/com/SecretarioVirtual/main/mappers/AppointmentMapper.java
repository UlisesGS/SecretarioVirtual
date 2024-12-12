package com.SecretarioVirtual.main.mappers;

import com.SecretarioVirtual.main.entities.Appointment;
import com.SecretarioVirtual.main.entities.dtos.appointment.EditAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.RequestAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.appointment.ResponseAppointmentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    Appointment requestDtoToAppointment (RequestAppointmentDto requestAppointmentDto);
    Appointment editDtoToAppointment (EditAppointmentDto editAppointmentDto);
    ResponseAppointmentDto appointmentToResponseDto (Appointment appointment);
    List<ResponseAppointmentDto> appointmentListToResponseDtoList (List<Appointment> appointmentList);
}
