package com.SecretarioVirtual.main.mappers;

import com.SecretarioVirtual.main.entities.ScheduleRange;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleRangeMapper {

    ScheduleRange requestDtoToScheduleRange(RequestScheduleRangeDto requestScheduleRangeDto);
    ResponseScheduleRangeDto scheduleRangeToResponseDto(ScheduleRange scheduleRange);
    List<ResponseScheduleRangeDto> scheduleRangeListToResponseDtoList(List<ScheduleRange> scheduleRangeList);
}
