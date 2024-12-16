package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;
import com.SecretarioVirtual.main.services.ScheduleRangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horario")
@RequiredArgsConstructor
public class ScheduleRangeController {

        private final ScheduleRangeService scheduleRangeService;

        @PostMapping("/crear")
        public ResponseEntity<ResponseScheduleRangeDto> createScheduleRange(
                @RequestBody @Valid RequestScheduleRangeDto requestScheduleRangeDto) {
            ResponseScheduleRangeDto result = scheduleRangeService.createScheduleRange(requestScheduleRangeDto);
            return ResponseEntity.ok(result);
        }

        //FindAll,
        //FindByBetweenDates
        //FindByDate
        //Delete.

}
