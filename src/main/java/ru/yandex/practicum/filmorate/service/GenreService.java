package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.List;

@Service
public class GenreService {

    private final GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> findAll() {
        return genreDao.findAll();
    }

    public Genre findGenreById(int id) {
        try {
            return genreDao.findGenreById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Такого жанра нет в хранилище");
        }
    }
}
