package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;

@Data
@AllArgsConstructor
public class Mpa {

    @Max(value = 5)
    private int id;
    private String name;
}
