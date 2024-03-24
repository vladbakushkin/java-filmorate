package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAll() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public Genre findGenreById(int id) {
        String sql = "SELECT * FROM GENRE WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
        } catch (DataAccessException e) {
            throw new GenreNotFoundException("Genre with id " + id + " not found.");
        }
    }

    public List<Genre> findGenresByFilmId(int filmId) {
        String sqlGenres = "SELECT fg.GENRE_ID as ID, g.NAME FROM FILM_GENRE fg JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "WHERE fg.FILM_ID = ?";
        return jdbcTemplate.query(sqlGenres, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
