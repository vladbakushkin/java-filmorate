package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotation.WithoutSpaces;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WithoutSpacesValidator implements ConstraintValidator<WithoutSpaces, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || !s.contains(" ");
    }
}
