package ru.yandex.practicum.filmorate.mapper;


import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;

public class FilmMapper implements RowMapper<Film> {

    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final FilmLikesDao filmLikesDao;

    public FilmMapper(MpaDao mpaDao, GenreDao genreDao, FilmLikesDao filmLikesDao) {
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
        this.filmLikesDao = filmLikesDao;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
        film.setDuration(Duration.ofSeconds(rs.getInt("duration")));

        film.setMpa(mpaDao.findMpaById(rs.getInt("mpa_id")));

        film.setGenres(genreDao.findGenresByFilmId(film.getId()));

        film.setLikes(filmLikesDao.findLikesByFilmId(film.getId()));

        return film;
    }
}
