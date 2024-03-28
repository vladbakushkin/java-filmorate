package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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
        return getFilm(id);
    }

    @Override
    public void removeFilm(int id) {
        String sql = "DELETE FROM FILM WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate().toString(),
                film.getDuration().toSeconds(), film.getMpa().getId(), film.getId());
        return getFilm(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM FILM";
        return jdbcTemplate.query(sql, new FilmMapper(new MpaDao(jdbcTemplate), new GenreDao(jdbcTemplate),
                new FilmLikesDao(jdbcTemplate)));
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT * FROM FILM WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new FilmMapper(new MpaDao(jdbcTemplate), new GenreDao(jdbcTemplate),
                new FilmLikesDao(jdbcTemplate)), id);
    }
}
