package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void createUser() {
        // given
        User user = new User("user@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01");

        // when
        User createdUser = userDbStorage.createUser(user);
        user.setId(createdUser.getId());

        // then
        assertThat(createdUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void updateUser() {
        // given
        User user = userDbStorage.createUser(new User("user@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01"));
        User newUser = new User("newemail@email.ru", "newlogin", "newName", "1984-09-13");
        newUser.setId(user.getId());

        // when
        User updatedUser = userDbStorage.updateUser(newUser);

        // then
        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void updateUserNotFound() {
        // given
        User user = new User("user@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01");
        user.setId(9999);

        AssertionsForClassTypes.assertThatThrownBy(() -> userDbStorage.updateUser(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователя с id \"" + user.getId() + "\" нет в хранилище.");
    }

    @Test
    void deleteUser() {
        // given
        User user = userDbStorage.createUser(new User("user@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01"));

        // when
        userDbStorage.deleteUser(user.getId());

        // then
        assertThatThrownBy(() -> userDbStorage.getUser(user.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователя с id \"" + user.getId() + "\" нет в хранилище.");
    }

    @Test
    void getUsers() {
        // given
        User user1 = userDbStorage.createUser(new User("user1@email.ru", "user1@email.ru", "user1",
                "1984-09-13"));
        User user2 = userDbStorage.createUser(new User("user2@email.ru", "user2@email.ru", "user2",
                "1984-09-13"));
        List<User> users = List.of(user1, user2);

        // when
        List<User> dbStorageUsers = userDbStorage.getUsers();

        // then
        assertThat(dbStorageUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(users);
    }

    @Test
    void getUser() {
        // given
        User user = userDbStorage.createUser(new User("user@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01"));

        // when
        User userFromDb = userDbStorage.getUser(user.getId());

        // then
        assertThat(userFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }
}