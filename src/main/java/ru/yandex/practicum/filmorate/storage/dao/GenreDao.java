package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> findAll() {
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public Genre findGenreById(int id) {
        String sql = "select * from genre where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
        } catch (RuntimeException e) {
            throw new GenreNotFoundException("Genre with id " + id + " not found.");
        }
    }

    public Collection<Genre> findGenresByFilmId(int filmId) {
        String sqlGenres = "SELECT fg.GENRE_ID as ID, g.NAME FROM FILM_GENRE fg JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "WHERE fg.FILM_ID = ?";
        return jdbcTemplate.query(sqlGenres, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
