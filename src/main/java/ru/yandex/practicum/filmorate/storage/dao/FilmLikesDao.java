package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

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
        return jdbcTemplate.query(sql, new FilmMapper(new MpaDao(jdbcTemplate), new GenreDao(jdbcTemplate), new FilmLikesDao(jdbcTemplate)), count);
    }

    public Collection<Integer> findLikesByFilmId(int filmId) {
        String sqlLikes = "SELECT fl.USER_ID FROM FILM_LIKES fl WHERE fl.FILM_ID = ?";
        return jdbcTemplate.query(sqlLikes,
                (rsLike, rowNumLike) -> rsLike.getInt("user_id"), filmId);
    }
}
