package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.PositiveDurationValidator;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveDurationValidator.class)
public @interface PositiveDuration {
    String message() default "Duration must not be is negative";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
