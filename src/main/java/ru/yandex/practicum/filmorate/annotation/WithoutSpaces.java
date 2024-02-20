package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.WithoutSpacesValidator;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WithoutSpacesValidator.class)
public @interface WithoutSpaces {
    String message() default "Login must not be contains spaces";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
