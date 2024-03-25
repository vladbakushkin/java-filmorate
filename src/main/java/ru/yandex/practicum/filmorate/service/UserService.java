package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
    }

    public User createUser(User user) {
        User userCreated = userStorage.createUser(user);
        log.debug("Создан пользователь: {}, {}, {}, {}, {}",
                userCreated.getId(), userCreated.getEmail(), userCreated.getLogin(), userCreated.getName(),
                userCreated.getBirthday());
        return findUserById(userCreated.getId());
    }

    public User updateUser(User user) {
        User userOriginal = this.findUserById(user.getId());
        User userUpdated = userStorage.updateUser(user);
        log.debug("Обновлен пользователь (старые данные): {}, {}, {}, {}, {}",
                userOriginal.getId(), userOriginal.getEmail(), userOriginal.getLogin(), userOriginal.getName(),
                userOriginal.getBirthday());
        log.debug("Обновлен пользователь (новые данные): {}, {}, {}, {}, {}",
                userUpdated.getId(), userUpdated.getEmail(), userUpdated.getLogin(), userUpdated.getName(),
                userUpdated.getBirthday());
        return findUserById(userUpdated.getId());
    }

    public List<User> findAll() {
        final List<User> users = userStorage.getUsers();
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    public User findUserById(int id) {
        try {
            final User user = userStorage.getUser(id);
            log.debug("Найден пользователь: {}, {}, {}, {}, {}",
                    user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Пользователя с id \"" + id + "\" нет в хранилище.");
        }
    }

    public void addFriend(int userId, int friendId) {
        final User user = this.findUserById(userId);
        final User friend = this.findUserById(friendId);

        friendshipDao.addFriend(user.getId(), friend.getId());

        log.debug("Пользователь id = {} отправил заявку в друзья пользователю id = {}.",
                user.getId(), friend.getId());

        userStorage.updateUser(user);
    }

    public void removeFriend(int userId, int friendId) {
        final User user = this.findUserById(userId);
        final User friend = this.findUserById(friendId);

        friendshipDao.removeFriend(user.getId(), friend.getId());

        log.debug("Пользователь id = {} удалил из друзей пользователя id =  {}.",
                user.getId(), friend.getId());

        userStorage.updateUser(user);
    }

    public List<User> getFriends(int id) {
        final User user = this.findUserById(id);

        List<User> friends = friendshipDao.getFriends(user.getId());

        log.debug("Список друзей пользователя id = {}: {}.",
                user.getId(), friends.toString());

        return friends;
    }

    public List<User> getMutualFriends(int userId1, int userId2) {

        final User user1 = this.findUserById(userId1);
        final User user2 = this.findUserById(userId2);

        List<User> mutualFriends = friendshipDao.getMutualFriends(user1.getId(), user2.getId());

        log.debug("Список общих друзей пользователей: id = {} и id = {}: {}",
                user1.getId(), user2.getId(), mutualFriends.toString());

        return mutualFriends;
    }
}
