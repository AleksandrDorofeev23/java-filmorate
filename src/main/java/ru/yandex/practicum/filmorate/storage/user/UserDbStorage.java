package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sql = "SELECT * FROM users WHERE login=?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, user.getLogin());
        if (row.next()) {
            throw new DataException("Пользователь уже существует");
        }
        if (user.getName() == "") {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toValue()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, user.getId());
        if (rows.next()) {
            Set<Integer> friends = user.getFriends();
            user.setFriends(updateFriends(friends, user.getId()));
            sql = "UPDATE users SET email= ?, login = ?, name = ?, birthday = ?;";
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday());
            return user;
        } else {
            throw new DataException("Пользователь не найден");
        }
    }

    private Set<Integer> updateFriends(Set<Integer> friends, int id) {
        String sql = "DELETE FROM friends WHERE user_id = ?;";
        jdbcTemplate.update(sql, id);
        if (friends == null || friends.isEmpty()) {
            return friends;
        }
        for (Integer friend : friends) {
            sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?);";
            jdbcTemplate.update(sql, id, friend);
        }
        return friends;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM users");
        while (filmsRows.next()) {
            users.add(getUserById(filmsRows.getInt("user_id")));
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        if (userRows.next()) {
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(
                    "SELECT friend_id FROM friends WHERE (user_id = ?);", id);
            Set<Integer> friends = new HashSet<>();
            while (friendsRows.next()) {
                friends.add(friendsRows.getInt("friend_id"));
            }
            User user = new User(
                    friends,
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    LocalDate.parse(userRows.getString("birthday"), format)
            );
            return user;
        } else {
            throw new DataException("Пользователь не найден");
        }
    }

    @Override
    public User addFriend(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);
        String sql = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?;";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id, friendId);
        if (rows.next()) {
            boolean isStatusConfirm = rows.getBoolean("status");
            if (isStatusConfirm) {
                throw new DataException("Дружба уже существует.");
            } else {
                throw new DataException("Дружба была отправлена ранее и еще не подтверждена");
            }
        }
        rows = jdbcTemplate.queryForRowSet(sql, friendId, id);
        if (rows.next()) {
            sql = "UPDATE friends SET status = true WHERE user_id = ? AND friend_id = ?;";
            jdbcTemplate.update(sql, friendId, id);
            sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?);";
            jdbcTemplate.update(sql, id, friendId, true);
            return getUserById(id);
        }
        sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, id, friendId);
        return getUserById(id);
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, id, friendId);
        sql = "UPDATE friends SET status = false WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, friendId, id);
        return getUserById(id);
    }

}
