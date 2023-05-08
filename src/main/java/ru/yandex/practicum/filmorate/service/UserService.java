package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Getter
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User addFriend(int id, int friendId) {
        if (friendId < 0) {
            throw new DataException("ID друга должно быть положительным");
        }
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        Set<Integer> friends = user.getFriends();
        friends.add(friendId);
        user.setFriends(friends);
        friends = friend.getFriends();
        friends.add(id);
        friend.setFriends(friends);
        updateUser(friend);
        return updateUser(user);
    }

    public Collection<User> findAllUsers() {
        return userStorage.getAllUsers();
    }

    public User deleteFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        Set<Integer> friends = user.getFriends();
        friends.remove(friendId);
        user.setFriends(friends);
        friends = friend.getFriends();
        friends.remove(id);
        friend.setFriends(friends);
        updateUser(friend);
        return updateUser(user);
    }

    public Collection<User> getFriends(int id) {
        User user = userStorage.getUserById(id);
        Set<Integer> friendsIds = user.getFriends();
        Collection<User> friends = new ArrayList<>();
        for (Integer i : friendsIds) {
            friends.add(userStorage.getUserById(i));
        }
        return friends;
    }

    public Collection<User> getMutualFriends(int id, int otherId) {
        Set<Integer> user1Friends = new HashSet<>(userStorage.getUserById(id).getFriends());
        Set<Integer> user2Friends = new HashSet<>(userStorage.getUserById(otherId).getFriends());
        Collection<User> mutualFriends = new ArrayList<>();
        for (Integer i : user1Friends) {
            if (user2Friends.contains(i)) {
                mutualFriends.add(userStorage.getUserById(i));
            }
        }
        return mutualFriends;
    }

    public User findUser(int id) {
        return userStorage.getUserById(id);
    }

}
