package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotation.PositiveDuration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        return duration.toMinutes() > 0;
    }
}
