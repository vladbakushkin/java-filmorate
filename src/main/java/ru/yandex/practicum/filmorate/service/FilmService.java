package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesDao filmLikesDao;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       FilmLikesDao filmLikesDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikesDao = filmLikesDao;
    }

    public Film addFilm(Film film) {
        final Film filmAdded = filmStorage.addFilm(film);
        log.debug("Добавлен фильм: id={},\n name={},\n description={},\n releaseDate={},\n duration={},\n mpaId={},\n genres={},\n",
                filmAdded.getId(), filmAdded.getName(), filmAdded.getDescription(), filmAdded.getReleaseDate(),
                filmAdded.getDuration(), filmAdded.getMpa().toString(), filmAdded.getGenres().toString());
        return filmAdded;
    }

    public Film updateFilm(Film film) {
        final Film filmUpdated = filmStorage.updateFilm(film);
        log.debug("Обновлен фильм: {}, {}, {}, {}, {}, {}, {}",
                filmUpdated.getId(), filmUpdated.getName(), filmUpdated.getDescription(), filmUpdated.getMpa().getId(),
                filmUpdated.getGenres().toString(), filmUpdated.getReleaseDate(), filmUpdated.getDuration());
        return film;
    }

    public List<Film> findAll() {
        final List<Film> films = filmStorage.getFilms();
        log.debug("Все фильмы: {}", films.toString());
        return films;
    }

    public Film findFilmById(int filmId) {
        final Film film = filmStorage.getFilm(filmId);
        log.debug("Найден фильм: id={},\n name={}, description={}, releaseDate={}, duration={}, mpaId={}, genres={},\n",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa(), film.getGenres().toString());
        return film;
    }

    public Film addLike(int filmId, int userId) {
        final Film film = filmStorage.getFilm(filmId);
        final User user = userStorage.getUser(userId);
        filmLikesDao.addLike(film.getId(), user.getId());
        film.addLike(userId);
        log.debug("Добавлен лайк фильму: {}", film);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        final Film film = filmStorage.getFilm(filmId);
        final User user = userStorage.getUser(userId);
        filmLikesDao.removeLike(film.getId(), user.getId());
        film.removeLike(user.getId());
        log.debug("Удален лайк у фильма: {}", film);
        return film;
    }

    public List<Film> getMostPopularFilms(int count) {

        List<Film> mostPopularFilms = filmLikesDao.getMostPopularFilms(count);

        log.debug("Список фильмов по лайкам: {}",
                mostPopularFilms);

        return mostPopularFilms;
    }
}
