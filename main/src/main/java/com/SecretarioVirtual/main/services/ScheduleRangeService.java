package com.SecretarioVirtual.main.services;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;

public interface ScheduleRangeService {

    ResponseScheduleRangeDto createScheduleRange(RequestScheduleRangeDto requestScheduleRangeDto);
}
