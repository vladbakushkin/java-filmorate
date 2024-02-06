package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.GeneratorId;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final GeneratorId generatorId = new GeneratorId();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        int id = generatorId.generateNewId();
        user.setId(id);
        users.put(id, user);
        log.debug("Создан пользователь: {}, {}, {}, {}, {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id: " + user.getId() + " не зарегистрирован.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Обновлен пользователь: {}, {}, {}, {}, {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @GetMapping
    public List<User> listUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }


}
