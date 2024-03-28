package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.Collection;

@Service
public class MpaService {

    private final MpaDao mpaDao;

    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Collection<Mpa> findAll() {
        return mpaDao.findAll();
    }

    public Mpa findMpaById(int id) {
        try {
            return mpaDao.findMpaById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException("MPA with id " + id + " not found.");
        }
    }
}
