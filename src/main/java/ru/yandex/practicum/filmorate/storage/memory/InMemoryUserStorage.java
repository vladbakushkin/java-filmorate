package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.GeneratorId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final GeneratorId generatorId = new GeneratorId();

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        int id = generatorId.generateNewId();
        user.setId(id);
        users.put(id, user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id: \"" + user.getId() + "\" не зарегистрирован.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id: \"" + id + "\" не зарегистрирован.");
        }
        users.remove(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id: \"" + id + "\" не зарегистрирован.");
        }
        return users.get(id);
    }
}
