package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        userStorage.createUser(user);
        log.debug("Создан пользователь: {}, {}, {}, {}, {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    public User updateUser(User user) {
        userStorage.updateUser(user);
        log.debug("Обновлен пользователь: {}, {}, {}, {}, {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
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

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        log.debug("Пользователь id = {} добавил в друзья пользователя id =  {}.",
                user1.getId(), user2.getId());

        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
    }

    public void removeFriend(int userId1, int userId2) {
        final User user1 = userStorage.getUser(userId1);
        final User user2 = userStorage.getUser(userId2);

        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());

        log.debug("Пользователь id = {} удалил из друзей пользователя id =  {}.",
                user1.getId(), user2.getId());

        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
    }

    public List<User> getFriends(int id) {
        User user = userStorage.getUser(id);

        List<User> friends = new ArrayList<>();

        for (User storageUser : userStorage.getUsers()) {
            for (int friendId : user.getFriends()) {
                if (storageUser.getId() == friendId) {
                    friends.add(storageUser);
                }
            }
        }
        return friends;
    }

    public List<User> getMutualFriends(int userId1, int userId2) {

        final User user1 = userStorage.getUser(userId1);
        final User user2 = userStorage.getUser(userId2);
        Set<Integer> friendsUser1 = new HashSet<>(user1.getFriends());
        Set<Integer> friendsUser2 = new HashSet<>(user2.getFriends());

        friendsUser1.retainAll(friendsUser2);

        List<User> mutualFriends = new ArrayList<>();
        for (int friendId : friendsUser1) {
            for (User user : userStorage.getUsers()) {
                if (user.getId() == friendId) {
                    mutualFriends.add(user);
                }
            }
        }
        return mutualFriends;
    }
}
