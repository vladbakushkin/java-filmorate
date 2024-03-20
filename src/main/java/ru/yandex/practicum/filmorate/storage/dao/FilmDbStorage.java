package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
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

        film.setId(id);

        // TODO: переписать через лямбду
        for (Genre genre : film.getGenres()) {
            String sql = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(sql, id, genre.getId());
        }

        return film;
    }

    @Override
    public void removeFilm(int id) {
        checkFilmInStorage(id);
        String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmInStorage(film.getId());
        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate().toString(),
                film.getDuration().toMinutes(), film.getMpa().getId(), film.getId());

        // TODO: А если жанры не обновляются?
        jdbcTemplate.update("DELETE FROM film_genre WHERE FILM_ID = ?", film.getId());

        // TODO: переписать через лямбду
        for (Genre genre : film.getGenres()) {
            String sqlGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(sqlGenre, film.getId(), genre.getId());
        }

        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.ID, " +
                "       f.NAME, " +
                "       f.DESCRIPTION, " +
                "       f.RELEASE_DATE, " +
                "       f.DURATION, " +
                "       f.MPA_ID " +
                "FROM film f";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilm(int id) {
        checkFilmInStorage(id);
        String sql = "SELECT f.ID, " +
                "       f.NAME, " +
                "       f.DESCRIPTION, " +
                "       f.RELEASE_DATE, " +
                "       f.DURATION, " +
                "       f.MPA_ID " +
                "FROM film f " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
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

    private void checkFilmInStorage(Integer id) {
        String checkSql = "SELECT COUNT(*) FROM film WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);

        if (count == null || count == 0) {
            throw new FilmNotFoundException("Фильма с id \"" + id + "\" нет в хранилище.");
        }
    }
}
