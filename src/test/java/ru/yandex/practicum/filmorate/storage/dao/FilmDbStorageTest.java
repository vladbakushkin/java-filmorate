package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private final Mpa mpa = new Mpa(1, "G");
    private FilmDbStorage filmDbStorage;
    private Film film;


    @BeforeEach
    void setUp() {
        film = new Film("name", "description", mpa, "2000-01-01", 100);
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        film.setGenres(genres);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    void addFilm() {
        // when
        Film addedFilm = filmDbStorage.addFilm(film);
        film.setId(addedFilm.getId());

        // then
        assertThat(addedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void removeFilm() {
        // given
        Film addedFilm = filmDbStorage.addFilm(film);

        // when
        filmDbStorage.removeFilm(addedFilm.getId());

        // then
        assertThatThrownBy(() -> filmDbStorage.getFilm(addedFilm.getId()))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessage("Фильма с id \"" + addedFilm.getId() + "\" нет в хранилище.");
    }

    @Test
    void updateFilm() {
        // given
        Film addedFilm = filmDbStorage.addFilm(film);
        mpa.setId(2);
        mpa.setName("PG");
        Film newFilm = new Film("newName", "newDescription", mpa, "2010-11-11", 392);
        newFilm.setId(addedFilm.getId());

        // when
        Film filmUpdated = filmDbStorage.updateFilm(newFilm);

        // then
        assertThat(filmUpdated)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    void updateFilmNotFound() {
        // given
        Film newFilm = new Film("newName", "newDescription", mpa, "2010-11-11", 392);
        newFilm.setId(9999);

        // then
        assertThatThrownBy(() -> filmDbStorage.updateFilm(newFilm))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessage("Фильма с id \"" + newFilm.getId() + "\" нет в хранилище.");
    }

    @Test
    void getFilms() {
        // given
        Film newFilm = filmDbStorage.addFilm(new Film("newName", "newDescription", mpa,
                "2010-11-11", 392));
        Film newFilm2 = filmDbStorage.addFilm(new Film("newName2", "newDescription2", mpa,
                "1930-03-20", 68));
        List<Film> films = List.of(newFilm, newFilm2);

        // when
        List<Film> filmsFromDb = filmDbStorage.getFilms();

        // then
        assertThat(filmsFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(films);
    }

    @Test
    void getFilm() {
        // given
        Film addedFilm = filmDbStorage.addFilm(film);

        // when
        Film filmFromDb = filmDbStorage.getFilm(addedFilm.getId());

        // then
        assertThat(filmFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedFilm);
    }
}