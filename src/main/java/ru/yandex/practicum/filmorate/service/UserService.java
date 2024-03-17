package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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

    public void addFriend(int userId, int friendId) {
        final User user = userStorage.getUser(userId);
        final User friend = userStorage.getUser(friendId);

        friendshipDao.addFriend(user.getId(), friend.getId());

        log.debug("Пользователь id = {} отправил заявку в друзья пользователю id = {}.",
                user.getId(), friend.getId());

        userStorage.updateUser(user);
    }

    public void removeFriend(int userId, int friendId) {
        final User user = userStorage.getUser(userId);
        final User friend = userStorage.getUser(friendId);

        friendshipDao.removeFriend(user.getId(), friend.getId());

        log.debug("Пользователь id = {} удалил из друзей пользователя id =  {}.",
                user.getId(), friend.getId());

        userStorage.updateUser(user);
    }

    public List<User> getFriends(int id) {
        final User user = userStorage.getUser(id);

        List<User> friends = friendshipDao.getFriends(user.getId());

        log.debug("Список друзей пользователя id = {}: {}.",
                user.getId(), friends.toString());

        return friends;
    }

    public List<User> getMutualFriends(int userId1, int userId2) {

        final User user1 = userStorage.getUser(userId1);
        final User user2 = userStorage.getUser(userId2);

        List<User> mutualFriends = friendshipDao.getMutualFriends(user1.getId(), user2.getId());

        log.debug("Список общих друзей пользователей: id = {} и id = {}: {}",
                user1.getId(), user2.getId(), mutualFriends.toString());

        return mutualFriends;
    }
}
