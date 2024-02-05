package ru.yandex.practicum.filmorate.utils;

import lombok.Getter;

@Getter
public class GeneratorId {

    private static int generatorFilmId = 0;

    private static int generatorUserId = 0;

    public static int incrementAndGetFilmId() {
        return ++generatorFilmId;
    }

    public static int incrementAndGetUserId() {
        return ++generatorUserId;
    }
}
