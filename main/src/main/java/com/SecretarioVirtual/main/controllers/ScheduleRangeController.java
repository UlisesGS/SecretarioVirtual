package com.SecretarioVirtual.main.controllers;

import com.SecretarioVirtual.main.entities.dtos.appointment.ResponseAppointmentDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.RequestScheduleRangeDto;
import com.SecretarioVirtual.main.entities.dtos.scheduleRange.ResponseScheduleRangeDto;
import com.SecretarioVirtual.main.services.ScheduleRangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/todos")
    public ResponseEntity<List<ResponseScheduleRangeDto>> getAllScheduleRanges() {
        List<ResponseScheduleRangeDto> result = scheduleRangeService.getAllScheduleRanges();
        return ResponseEntity.ok(result);
    }

    @GetMapping("todos/dia/{day}")
    public ResponseEntity<List<ResponseScheduleRangeDto>> getAllScheduleRangesByDay(@PathVariable String day) {
        List<ResponseScheduleRangeDto> result = scheduleRangeService.getAllScheduleRangesByDay(day);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("borrar/{id}")
    public ResponseEntity<Boolean> DeleteScheduleRangeByDay(@PathVariable String id) {
        Boolean result = scheduleRangeService.deleteScheduleRangeId(id);
        return ResponseEntity.ok(result);
    }
    
}
