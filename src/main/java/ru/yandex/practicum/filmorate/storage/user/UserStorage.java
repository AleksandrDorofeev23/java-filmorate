package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    Collection<User> getAllUsers();

    User addFriend(int id, int friendId);

    User deleteFriend(int id, int friendId);

}
