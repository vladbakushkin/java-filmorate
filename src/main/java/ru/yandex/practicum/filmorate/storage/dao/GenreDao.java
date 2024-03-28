package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
    }

    public List<Genre> findGenresByFilmId(int filmId) {
        String sqlGenres = "SELECT fg.GENRE_ID as ID, g.NAME FROM FILM_GENRE fg JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "WHERE fg.FILM_ID = ?";
        return jdbcTemplate.query(sqlGenres, this::mapRowToGenre, filmId);
    }

    public void updateFilmGenres(int filmId, Collection<Genre> genres) {
        String sql = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for (Genre genre : genres) {
            parameters.add(new Object[]{filmId, genre.getId()});
        }
        jdbcTemplate.batchUpdate(sql, parameters);
    }

    public Set<Integer> getMatchGenresId(Collection<Integer> genresId) {
        String inIds = String.join(",", Collections.nCopies(genresId.size(), "?"));
        List<Genre> genreList = jdbcTemplate.query("SELECT * FROM GENRE WHERE ID IN (" + inIds + ")",
                this::mapRowToGenre, genresId.toArray());

        Set<Integer> matchGenresIds = new HashSet<>();
        for (Genre genre : genreList) {
            matchGenresIds.add(genre.getId());
        }
        return matchGenresIds;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
