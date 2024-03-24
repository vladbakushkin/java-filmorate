package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmLikesDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmLikesDao filmLikesDao;
    private FilmDbStorage filmDbStorage;
    private Film film;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        filmLikesDao = new FilmLikesDao(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
        film = filmDbStorage.addFilm(new Film("name", "description", new Mpa(1, "G"), "2000-01-01",
                100));
        user1 = userDbStorage.createUser(new User("user1@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01"));
        user2 = userDbStorage.createUser(new User("user2@email.com", "petya321", "Petr First",
                "1672-02-02"));
    }

    @Test
    void addLike() {
        // when
        filmLikesDao.addLike(film.getId(), user1.getId());

        // then
        assertThat(filmDbStorage.getFilm(film.getId()).getLikes())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user1.getId()));
    }

    @Test
    void removeLike() {
        // given
        filmLikesDao.addLike(film.getId(), user1.getId());
        filmLikesDao.addLike(film.getId(), user2.getId());

        // when
        filmLikesDao.removeLike(film.getId(), user1.getId());

        // then
        assertThat(filmDbStorage.getFilm(film.getId()).getLikes())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user2.getId()));
    }

    @Test
    void getMostPopularFilms() {
        // given
        Film film1 = filmDbStorage.addFilm(new Film("film1", "film1", new Mpa(1, "G"),
                "2000-10-10", 100));
        Film film2 = filmDbStorage.addFilm(new Film("film2", "film2", new Mpa(2, "PG"),
                "1990-01-01", 123));
        filmLikesDao.addLike(film1.getId(), user1.getId());
        filmLikesDao.addLike(film2.getId(), user1.getId());
        filmLikesDao.addLike(film2.getId(), user2.getId());

        // when
        List<Film> mostPopularFilms = filmLikesDao.getMostPopularFilms(10);

        // then
        assertThat(mostPopularFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(filmDbStorage.getFilm(film2.getId()), filmDbStorage.getFilm(film1.getId()),
                        filmDbStorage.getFilm(film.getId())));
    }

    @Test
    void findLikesByFilmId() {
        // given
        filmLikesDao.addLike(film.getId(), user1.getId());
        filmLikesDao.addLike(film.getId(), user2.getId());

        // when
        List<Integer> likesByFilmId = filmLikesDao.findLikesByFilmId(film.getId());

        // then
        assertThat(likesByFilmId)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user1.getId(), user2.getId()));
    }
}