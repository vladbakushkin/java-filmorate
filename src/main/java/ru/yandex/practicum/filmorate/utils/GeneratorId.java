package ru.yandex.practicum.filmorate.utils;

import lombok.Getter;

@Getter
public class GeneratorId {

    private int id = 0;

    public int generateNewId() {
        return ++id;
    }
}
