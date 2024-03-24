package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private GenreDao genreDao;
    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genreDao = new GenreDao(jdbcTemplate);
        jdbcTemplate.execute("MERGE INTO GENRE (id, name) VALUES (1, 'Комедия')");
        jdbcTemplate.execute("MERGE INTO GENRE (id, name) VALUES (2, 'Драма')");
        jdbcTemplate.execute("MERGE INTO GENRE (id, name) VALUES (3, 'Мультфильм')");
        jdbcTemplate.execute("MERGE INTO GENRE (id, name) VALUES (4, 'Триллер')");
        jdbcTemplate.execute("MERGE INTO GENRE (id, name) VALUES (5, 'Документальный')");
        jdbcTemplate.execute("MERGE INTO GENRE (id, name) VALUES (6, 'Боевик')");

        genres = List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"), new Genre(4, "Триллер"),
                new Genre(5, "Документальный"), new Genre(6, "Боевик"));
    }

    @Test
    void findAll() {
        // when
        List<Genre> genresFromDb = genreDao.findAll();

        // then
        assertThat(genresFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genres);
    }

    @Test
    void findGenreById() {
        // when
        Genre genreFromDb = genreDao.findGenreById(genres.get(0).getId()); // id = 1

        // then
        assertThat(genreFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genres.get(0));
    }

    @Test
    void findGenreByUnknownId() {
        int id = 9999;
        // then
        assertThatThrownBy(() -> genreDao.findGenreById(id))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessage("Genre with id " + id + " not found.");
    }

    @Test
    void findGenresByFilmId() {
        // given
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film film = new Film("film", "film", new Mpa(1, "G"),
                "1990-01-01", 100);
        film.setGenres(genres);

        Film addedFilm = filmDbStorage.addFilm(film);

        // when
        List<Genre> filmGenres = genreDao.findGenresByFilmId(addedFilm.getId());

        // then
        assertThat(filmGenres)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedFilm.getGenres());
    }
}