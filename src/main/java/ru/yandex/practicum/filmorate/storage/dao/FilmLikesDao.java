package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmLikesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.ID, " +
                "f.NAME, " +
                "f.DESCRIPTION, " +
                "f.RELEASE_DATE, " +
                "f.DURATION, " +
                "f.MPA_ID " +
                "FROM FILM f " +
                "LEFT JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID " +
                "GROUP BY f.NAME, f.ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmMapper(new MpaDao(jdbcTemplate), new GenreDao(jdbcTemplate), new FilmLikesDao(jdbcTemplate)), count);
    }

    public List<Integer> findLikesByFilmId(int filmId) {
        String sqlLikes = "SELECT fl.USER_ID FROM FILM_LIKES fl WHERE fl.FILM_ID = ?";
        return jdbcTemplate.query(sqlLikes,
                (rsLike, rowNumLike) -> rsLike.getInt("user_id"), filmId);
    }

    public void updateFilmLikes(int filmId, Collection<Integer> likes) {
        String sql = "MERGE INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for (Integer userId : likes) {
            parameters.add(new Object[]{filmId, userId});
        }
        jdbcTemplate.batchUpdate(sql, parameters);
    }
}
