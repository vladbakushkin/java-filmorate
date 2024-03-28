package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_account")
                .usingGeneratedKeyColumns("id");

        int id = insert.executeAndReturnKey(user.toMap()).intValue();
        return getUser(id);
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE user_account SET email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUser(user.getId());
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM user_account WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM user_account";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public User getUser(int id) {
        String sql = "SELECT * FROM user_account WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
    }
}
