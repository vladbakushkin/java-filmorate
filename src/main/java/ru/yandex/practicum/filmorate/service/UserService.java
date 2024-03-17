package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        User userCreated = userStorage.createUser(user);
        log.debug("Создан пользователь: {}, {}, {}, {}, {}",
                userCreated.getId(), userCreated.getEmail(), userCreated.getLogin(), userCreated.getName(),
                userCreated.getBirthday());
        return userCreated;
    }

    public User updateUser(User user) {
        User userOriginal = userStorage.getUser(user.getId());
        User userUpdated = userStorage.updateUser(user);
        log.debug("Обновлен пользователь (старые данные): {}, {}, {}, {}, {}",
                userOriginal.getId(), userOriginal.getEmail(), userOriginal.getLogin(), userOriginal.getName(),
                userOriginal.getBirthday());
        log.debug("Обновлен пользователь (новые данные): {}, {}, {}, {}, {}",
                userUpdated.getId(), userUpdated.getEmail(), userUpdated.getLogin(), userUpdated.getName(),
                userUpdated.getBirthday());
        return userUpdated;
    }

    public List<User> findAll() {
        final List<User> users = userStorage.getUsers();
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    public User findUserById(int id) {
        final User user = userStorage.getUser(id);
        log.debug("Найден пользователь: {}, {}, {}, {}, {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    public void addFriend(int userId1, int userId2) {
        final User user1 = userStorage.getUser(userId1);
        final User user2 = userStorage.getUser(userId2);

        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
        log.debug("Пользователь id = {} добавил в друзья пользователя id =  {}.",
                user1.getId(), user2.getId());

        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
    }

    public void removeFriend(int userId1, int userId2) {
        final User user1 = userStorage.getUser(userId1);
        final User user2 = userStorage.getUser(userId2);

        user1.removeFriend(user2.getId());
        user2.removeFriend(user1.getId());

        log.debug("Пользователь id = {} удалил из друзей пользователя id =  {}.",
                user1.getId(), user2.getId());

        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
    }

    public List<User> getFriends(int id) {
        final User user = userStorage.getUser(id);

        return userStorage.getUsers().stream()
                .map(User::getId)
                .filter(user.getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(int userId1, int userId2) {

        final User user1 = userStorage.getUser(userId1);
        final User user2 = userStorage.getUser(userId2);

        List<Integer> mutualFriendsId = user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .collect(Collectors.toList());

        return userStorage.getUsers().stream()
                .map(User::getId)
                .filter(mutualFriendsId::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
