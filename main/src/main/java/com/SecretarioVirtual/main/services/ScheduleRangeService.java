package com.SecretarioVirtual.main.services;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;

import java.util.List;

public interface ScheduleRangeService {

    ResponseScheduleRangeDto createScheduleRange(RequestScheduleRangeDto requestScheduleRangeDto);
    List<ResponseScheduleRangeDto> getAllScheduleRanges();
    List<ResponseScheduleRangeDto> getAllScheduleRangesByDay(String day);
    Boolean deleteScheduleRangeId(String id);
}
