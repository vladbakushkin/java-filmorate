package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.utils.GeneratorId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final GeneratorId generatorId = new GeneratorId();

    @Override
    public Film addFilm(Film film) {
        int id = generatorId.generateNewId();
        film.setId(id);
        films.put(id, film);

        return film;
    }

    @Override
    public void removeFilm(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильма с id \"" + id + "\" нет в хранилище.");
        }
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильма с id \"" + film.getId() + "\" нет в хранилище.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильма с id \"" + id + "\" нет в хранилище.");
        }
        return films.get(id);
    }
}
