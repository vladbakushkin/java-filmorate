package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        log.debug("Добавлен фильм: {}, {}, {}, {}, {}",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        log.debug("Обновлен фильм: {}, {}, {}, {}, {}",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        return film;
    }

    public List<Film> findAll() {
        final List<Film> films = filmStorage.getFilms();
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    public Film findFilmById(int filmId) {
        final Film film = filmStorage.getFilm(filmId);
        log.debug("Найден фильм: {}, {}, {}, {}, {}",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        return film;
    }

    public Film addLike(int filmId, int userId) {
        final Film film = filmStorage.getFilm(filmId);
        final User user = userStorage.getUser(userId);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.debug("Добавлен лайк фильму: {}, все лайки: {}",
                film.getId(), film.getLikes().toString());
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        final Film film = filmStorage.getFilm(filmId);
        final User user = userStorage.getUser(userId);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.debug("Удален лайк у фильма: {}, все лайки: {}",
                film.getId(), film.getLikes().toString());
        return film;
    }

    public List<Film> getMostPopularFilms(int count) {
        Comparator<Film> likesComparatorAsc = Comparator.comparing(f -> f.getLikes().size());
        Comparator<Film> likesComparatorDesc = likesComparatorAsc.reversed();

        List<Film> mostPopularFilms = filmStorage.getFilms().stream()
                .sorted(likesComparatorDesc)
                .limit(count)
                .collect(Collectors.toList());

        log.debug("Список фильмов по лайкам: {}",
                mostPopularFilms);

        return mostPopularFilms;
    }
}
