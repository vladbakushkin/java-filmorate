package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(film.toMap()).intValue();
        batchUpdateFilmGenres(id, film.getGenres());
        batchUpdateFilmLikes(id, film.getLikes());
        return getFilm(id);
    }

    @Override
    public void removeFilm(int id) {
        String sql = "DELETE FROM film WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (RuntimeException e) {
            throw new FilmNotFoundException("Фильма с id \"" + id + "\" нет в хранилище.");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE ID = ?";
        try {
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate().toString(),
                    film.getDuration().toSeconds(), film.getMpa().getId(), film.getId());
            batchUpdateFilmGenres(film.getId(), film.getGenres());
            batchUpdateFilmLikes(film.getId(), film.getLikes());
            return getFilm(film.getId());
        } catch (RuntimeException e) {
            throw new FilmNotFoundException("Фильма с id \"" + film.getId() + "\" нет в хранилище.");
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM FILM";
        return jdbcTemplate.query(sql, new FilmMapper(new MpaDao(jdbcTemplate), new GenreDao(jdbcTemplate), new FilmLikesDao(jdbcTemplate)));
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT * FROM FILM WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new FilmMapper(new MpaDao(jdbcTemplate), new GenreDao(jdbcTemplate), new FilmLikesDao(jdbcTemplate)), id);
        } catch (RuntimeException e) {
            throw new FilmNotFoundException("Фильма с id \"" + id + "\" нет в хранилище.");
        }
    }

    private void batchUpdateFilmGenres(int filmId, Collection<Genre> genres) {
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
        String sql = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for (Genre genre : genres) {
            parameters.add(new Object[]{filmId, genre.getId()});
        }
        jdbcTemplate.batchUpdate(sql, parameters);
    }

    private void batchUpdateFilmLikes(int filmId, Collection<Integer> likes) {
        jdbcTemplate.update("DELETE FROM FILM_LIKES WHERE FILM_ID = ?", filmId);
        String sql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for (Integer userId : likes) {
            parameters.add(new Object[]{filmId, userId});
        }
        jdbcTemplate.batchUpdate(sql, parameters);
    }
}
