package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;

    @Test
    public void createUser() {
        User testUser = User.builder()
                .id(1)
                .login("user2")
                .email("user2@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        User user1 = userService.createUser(testUser);
        assertNotEquals(1, user1.getId());
    }

    @Test
    public void createDuplicateUser() {
        userService.createUser(user);
        final DataException exception = assertThrows(
                DataException.class, () -> userService.createUser(user)
        );
        assertEquals("Пользователь уже существует", exception.getMessage());
    }

    @Test
    public void updateUserWithFailData() {
        User testUser = User.builder()
                .login("user3")
                .email("user3@yandex.ru")
                .build();
        final DataException exception = assertThrows(
                DataException.class, () -> userService.updateUser(testUser)
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void findUsers() {
        user.setLogin("user5");
        userService.createUser(user);
        user.setLogin("user6");
        userService.createUser(user);
        Collection<User> users = userService.findAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    public void findUserWithFailId() {
        final DataException exception = assertThrows(
                DataException.class, () -> userService.findUser(-22)
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void updateUser() {
        User testUser = User.builder()
                .login("user8")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        testUser = userService.createUser(testUser);
        User updateUser = User.builder()
                .id(testUser.getId())
                .login("user9")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        updateUser = userService.updateUser(updateUser);
        assertEquals("user9", updateUser.getLogin());
    }

    @Test
    public void updateUserWithFailId() {
        User testUser = User.builder()
                .id(-1)
                .login("user7")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        final DataException exception = assertThrows(
                DataException.class, () -> userService.updateUser(testUser)
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void addFriend() {
        user.setLogin("user11");
        user = userService.createUser(user);
        User friend = User.builder()
                .id(22)
                .login("user12")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        friend = userService.createUser(friend);
        user = userService.addFriend(user.getId(), friend.getId());
        assertEquals(1, user.getFriends().size());
        assertNull(friend.getFriends());
        friend = userService.addFriend(friend.getId(), user.getId());
        assertEquals(1, friend.getFriends().size());
    }

    @Test
    public void addFriendWithFailId() {
        user.setLogin("user10");
        User testUser = userService.createUser(user);
        DataException exception = assertThrows(
                DataException.class, () -> userService.addFriend(testUser.getId(), -1)
        );
        assertEquals("ID друга должно быть положительным", exception.getMessage());
        exception = assertThrows(
                DataException.class, () -> userService.addFriend(-1, testUser.getId())
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void deleteFriend() {
        user.setLogin("user13");
        user = userService.createUser(user);
        User friend = User.builder()
                .id(-5)
                .login("user14")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        friend = userService.createUser(friend);
        user = userService.addFriend(user.getId(), friend.getId());
        friend = userService.addFriend(friend.getId(), user.getId());
        friend = userService.deleteFriend(friend.getId(), user.getId());
        assertTrue(friend.getFriends().isEmpty());
    }

    @Test
    public void getMutualFriends() {
        user.setLogin("user14");
        user = userService.createUser(user);
        User friend = User.builder()
                .id(-1)
                .login("user15")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        friend = userService.createUser(friend);
        User friend2 = User.builder()
                .id(-1)
                .login("testGetCommonFriends")
                .email("testGetCommonFriends@ya.ru")
                .birthday(LocalDate.now())
                .build();
        friend2 = userService.createUser(friend2);
        user = userService.addFriend(user.getId(), friend2.getId());
        friend = userService.addFriend(friend.getId(), friend2.getId());
        Collection<User> commonFriends = userService.getMutualFriends(user.getId(), friend.getId());
        assertEquals(1, commonFriends.size());
    }

    User user = User.builder()
            .login("user")
            .email("user@yandex.ru")
            .birthday(LocalDate.now())
            .build();
}