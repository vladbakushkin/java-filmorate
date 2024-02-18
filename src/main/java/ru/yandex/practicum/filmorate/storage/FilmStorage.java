package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {

    Film addFilm(Film film);

    void removeFilm(int id);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int id);
}
