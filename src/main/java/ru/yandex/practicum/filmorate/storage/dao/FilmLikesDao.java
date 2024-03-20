package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public class FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmLikesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.ID, " +
                "       f.NAME, " +
                "       f.DESCRIPTION, " +
                "       f.RELEASE_DATE, " +
                "       f.DURATION, " +
                "       f.MPA_ID " +
                "FROM film f " +
                "JOIN film_likes AS fl ON f.id = fl.film_id " +
                "GROUP BY f.name " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
        film.setDuration(Duration.ofMinutes(rs.getInt("duration")));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        film.setMpa(mpa);

        String sqlGenres = "SELECT fg.GENRE_ID FROM FILM_GENRE fg WHERE fg.FILM_ID = ?";
        Collection<Genre> genres = jdbcTemplate.query(sqlGenres,
                (rsGenre, rowNumGenre) -> new Genre(rsGenre.getInt("genre_id")), film.getId());
        film.setGenres(genres);

        String sqlLikes = "SELECT fl.USER_ID FROM FILM_LIKES fl WHERE fl.FILM_ID = ?";
        Collection<Integer> likes = jdbcTemplate.query(sqlLikes,
                (rsLike, rowNumLike) -> rsLike.getInt("user_id"), film.getId());
        film.setLikes(likes);

        return film;
    }
}
