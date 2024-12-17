package com.SecretarioVirtual.main.services.Implementations;

import com.SecretarioVirtual.main.entities.ScheduleRange;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;
import com.SecretarioVirtual.main.exceptions.ResourceNotFoundException;
import com.SecretarioVirtual.main.exceptions.ValidationException;
import com.SecretarioVirtual.main.mappers.ScheduleRangeMapper;
import com.SecretarioVirtual.main.repositories.ScheduleRangeRepository;
import com.SecretarioVirtual.main.services.ScheduleRangeService;
import com.SecretarioVirtual.main.validations.Validations;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleRangeServiceImpl implements ScheduleRangeService {

    private final ScheduleRangeRepository scheduleRangeRepository;
    private final ScheduleRangeMapper scheduleRangeMapper;
    private final Validations validations;

    @Transactional
    @Override
    public ResponseScheduleRangeDto createScheduleRange(RequestScheduleRangeDto requestScheduleRangeDto) {
        validations.adminValidation();
        validations.isSecondDateBefore(requestScheduleRangeDto.startsAt(), requestScheduleRangeDto.endsAt());

        //Busca día y se fija que no haya otro rango existente que se superponga parcialmente o a fechas inicio/fin.
        List<ScheduleRange> existentRanges = scheduleRangeRepository.findScheduleRangeByDay(requestScheduleRangeDto.day());
        for (ScheduleRange existent : existentRanges) {
            if ((requestScheduleRangeDto.startsAt().isAfter(existent.getStartsAt())
                    && requestScheduleRangeDto.startsAt().isBefore(existent.getEndsAt())) ||
                    (requestScheduleRangeDto.endsAt().isAfter(existent.getStartsAt())
                            && requestScheduleRangeDto.endsAt().isBefore(existent.getEndsAt())) ||
                    (requestScheduleRangeDto.endsAt().isAfter(existent.getEndsAt())
                            && requestScheduleRangeDto.startsAt().isBefore(existent.getStartsAt())) ||
                    (requestScheduleRangeDto.startsAt().equals(existent.getStartsAt())) ||
                    (requestScheduleRangeDto.endsAt().equals(existent.getEndsAt()))) {
                throw new ValidationException("El horario se superpone con otro horario existente");
            }
        }

        //Calcula la cantidad de turnos dividiendo la duración del rango por la suma del tiempo sesión y recreo.
        Integer appointmentsAmount = (int) (Duration.between(requestScheduleRangeDto.startsAt(), requestScheduleRangeDto.endsAt())).toMinutes()
                / (requestScheduleRangeDto.appointmentDuration() + requestScheduleRangeDto.breakTime());

        //Con el cálculo hecho, calcula la hora real de fin.
        var realEndTime = requestScheduleRangeDto.startsAt().plusMinutes((
                ((requestScheduleRangeDto.appointmentDuration() + requestScheduleRangeDto.breakTime()) * appointmentsAmount
                ) - requestScheduleRangeDto.breakTime()));

        var entity = scheduleRangeMapper.requestDtoToScheduleRange(requestScheduleRangeDto);
        entity.setEndsAt(realEndTime);
        entity.setAppointmentsAmount(appointmentsAmount);
        var saved = scheduleRangeRepository.save(entity);
        return scheduleRangeMapper.scheduleRangeToResponseDto(saved);
    }

    @Override
    public List<ResponseScheduleRangeDto> getAllScheduleRanges() {
        validations.adminValidation();
        var entities = scheduleRangeRepository.findAll();
        return scheduleRangeMapper.scheduleRangeListToResponseDtoList(entities);
    }

    @Override
    public List<ResponseScheduleRangeDto> getAllScheduleRangesByDay(String day) {
        validations.adminValidation();
        var enumDay = validations.dayConvert(day);
        var entities = scheduleRangeRepository.findScheduleRangeByDay(enumDay);
        return scheduleRangeMapper.scheduleRangeListToResponseDtoList(entities);
    }

    @Override
    public Boolean deleteScheduleRangeId(String id) {
        validations.adminValidation();
        var entity = scheduleRangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el rango horario con el id " + id));
        scheduleRangeRepository.delete(entity);
        return null;
    }
}
