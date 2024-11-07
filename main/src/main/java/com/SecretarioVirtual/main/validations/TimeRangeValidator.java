package com.SecretarioVirtual.main.validations;

import com.SecretarioVirtual.main.validations.interfaces.TimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeRangeValidator implements ConstraintValidator<TimeRange, LocalTime> {

    private LocalTime min;
    private LocalTime max;

    @Override
    public void initialize(TimeRange constraintAnnotation) {
        try {
            min = LocalTime.parse(constraintAnnotation.min());
            max = LocalTime.parse(constraintAnnotation.max());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido en la anotación TimeRange");
        }
    }

    @Override
    public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
        // Permitir que el valor sea nulo
        return value == null || (value.equals(min) || value.equals(max) || (value.isAfter(min) && value.isBefore(max)));
    }
}
