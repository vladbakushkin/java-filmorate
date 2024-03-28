package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private MpaDao mpaDao;
    private List<Mpa> mpaList;

    @BeforeEach
    void setUp() {
        mpaDao = new MpaDao(jdbcTemplate);
        jdbcTemplate.execute("MERGE INTO MPA (id, name) VALUES (1, 'G')");
        jdbcTemplate.execute("MERGE INTO MPA (id, name) VALUES (2, 'PG')");
        jdbcTemplate.execute("MERGE INTO MPA (id, name) VALUES (3, 'PG-13')");
        jdbcTemplate.execute("MERGE INTO MPA (id, name) VALUES (4, 'R')");
        jdbcTemplate.execute("MERGE INTO MPA (id, name) VALUES (5, 'NC-17')");

        mpaList = List.of(new Mpa(1, "G"), new Mpa(2, "PG"), new Mpa(3, "PG-13"),
                new Mpa(4, "R"), new Mpa(5, "NC-17"));
    }

    @Test
    void findAll() {
        // when
        List<Mpa> mpaListFromDb = mpaDao.findAll();

        // then
        assertThat(mpaListFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpaList);
    }

    @Test
    void findMpaById() {
        // given
        Mpa mpa = new Mpa(1, "G");
        jdbcTemplate.execute("MERGE INTO MPA (id, name) VALUES (1, 'G')");

        // when
        Mpa mpaFromDb = mpaDao.findMpaById(mpa.getId());

        // then
        assertThat(mpaFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    void findMpaByUnknownId() {
        int id = 9999;
        // then
        assertThatThrownBy(() -> mpaDao.findMpaById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}