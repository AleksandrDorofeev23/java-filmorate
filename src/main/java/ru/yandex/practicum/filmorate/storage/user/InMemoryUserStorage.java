package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap();
    private int userId = 0;

    @Override
    public User createUser(User user) {
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            throw new ValidationException("Логин не может содержать пробелы или быть пустым");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new DataException("Такого пользователя нет");
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new DataException("Пользователь " + id + " не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }
}
