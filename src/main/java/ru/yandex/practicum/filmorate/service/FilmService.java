package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmLikesDao filmLikesDao;
    private final GenreDao genreDao;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       UserService userService,
                       FilmLikesDao filmLikesDao,
                       GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmLikesDao = filmLikesDao;
        this.genreDao = genreDao;
    }

    public Film addFilm(Film film) {
        final Film filmAdded = filmStorage.addFilm(film);

        updateGenres(filmAdded.getId(), film.getGenres());

        filmLikesDao.updateFilmLikes(filmAdded.getId(), film.getLikes());

        log.debug("Добавлен фильм: id={},\n name={},\n description={},\n releaseDate={},\n duration={},\n mpaId={},\n genres={},\n",
                filmAdded.getId(), filmAdded.getName(), filmAdded.getDescription(), filmAdded.getReleaseDate(),
                filmAdded.getDuration(), filmAdded.getMpa().toString(), filmAdded.getGenres().toString());

        return findFilmById(filmAdded.getId());
    }

    public Film updateFilm(Film film) {
        this.findFilmById(film.getId());
        final Film filmUpdated = filmStorage.updateFilm(film);

        updateGenres(filmUpdated.getId(), film.getGenres());

        filmLikesDao.updateFilmLikes(filmUpdated.getId(), film.getLikes());

        log.debug("Обновлен фильм: {}, {}, {}, {}, {}, {}, {}",
                filmUpdated.getId(), filmUpdated.getName(), filmUpdated.getDescription(), filmUpdated.getMpa().getId(),
                filmUpdated.getGenres().toString(), filmUpdated.getReleaseDate(), filmUpdated.getDuration());
        return findFilmById(filmUpdated.getId());
    }

    public List<Film> findAll() {
        final List<Film> films = filmStorage.getFilms();
        log.debug("Все фильмы: {}", films.toString());
        return films;
    }

    public Film findFilmById(int filmId) {
        try {
            final Film film = filmStorage.getFilm(filmId);
            log.debug("Найден фильм: id={},\n name={}, description={}, releaseDate={}, duration={}, mpaId={}, genres={},\n",
                    film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa(), film.getGenres().toString());
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Фильма с id \"" + filmId + "\" нет в хранилище.");
        }
    }

    public Film addLike(int filmId, int userId) {
        final Film film = this.findFilmById(filmId);
        final User user = userService.findUserById(userId);
        filmLikesDao.addLike(film.getId(), user.getId());
        film.addLike(userId);
        log.debug("Добавлен лайк фильму: {}", film);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        final Film film = this.findFilmById(filmId);
        final User user = userService.findUserById(userId);
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

    private void updateGenres(int filmId, Collection<Genre> genres) {
        Set<Integer> filmGenresIds = new HashSet<>();
        for (Genre genre : genres) {
            filmGenresIds.add(genre.getId());
        }

        Set<Integer> matchGenresIds = genreDao.getMatchGenresId(filmGenresIds);

        if (!filmGenresIds.equals(matchGenresIds)) {
            throw new ValidationException("Некорректный ввод жанров: " + genres);
        }
        genreDao.updateFilmGenres(filmId, genres);
    }
}
