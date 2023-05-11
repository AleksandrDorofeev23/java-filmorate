package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    private final FilmService filmService;
    private final UserService userService;

    @Test
    public void createFilm() {
        film.setName("Film3");
        Film testFilm = filmService.createFilm(film);
        assertTrue(testFilm.getName().equals(film.getName()));
        assertTrue(testFilm.getDescription().equals(film.getDescription()));
    }

    @Test
    public void createDuplicate() {
        final DataException exception = assertThrows(
                DataException.class, () -> filmService.createFilm(film)
        );
        assertEquals("Фильм  уже существует", exception.getMessage());
    }

    @Test
    public void updateFilmWithFailId() {
        film.setId(-10);
        final DataException exception = assertThrows(
                DataException.class, () -> filmService.updateFilm(film)
        );
        assertEquals("Фильм не найден.", exception.getMessage());
    }

    @Test
    public void updateFilm() {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().id(1).build());
        genres.add(Genre.builder().id(2).build());
        genres.add(Genre.builder().id(3).build());
        film.setMpa(Mpa.builder().id(1).build());
        film.setGenres(genres);
        filmService.updateFilm(film);
        Film expectedFilm = filmService.findFilm(film.getId());
        assertTrue(expectedFilm.getMpa().getId() == 1);
        assertTrue(expectedFilm.getGenres().size() == 3);
    }

    @Test
    public void findFilmWithFailId() {
        final DataException exception = assertThrows(
                DataException.class, () -> filmService.findFilm(-1)
        );
        assertEquals("Фильм не найден.", exception.getMessage());
    }

    @Test
    public void getFilms() {
        film.setName("Film5");
        filmService.createFilm(film);
        Collection<Film> films = filmService.findAllFilms();
        assertFalse(films.isEmpty());
    }

    @Test
    void testAddLike() {
        film.setName("Film2");
        User testUser = User.builder()
                .login("user3")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        film = filmService.createFilm(film);
        testUser = userService.createUser(testUser);
        filmService.likeFilm(film.getId(), testUser.getId());
        film = filmService.findFilm(film.getId());
        assertTrue(film.getLikes().size() == 1);
    }

    @Test
    void deleteLike() {
        film.setName("Film4");
        film = filmService.createFilm(film);
        User testUser = User.builder()
                .login("user22")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        testUser = userService.createUser(testUser);
        filmService.likeFilm(film.getId(), testUser.getId());
        filmService.deleteLike(film.getId(), testUser.getId());
        film = filmService.findFilm(film.getId());
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    public void createFilmWithFailData() {
        Film testFilm = Film.builder()
                .id(-1)
                .name("Film244")
                .description("NewFilm")
                .duration(60)
                .releaseDate(LocalDate.now())
                .mpa(Mpa.builder().name("PG").build())
                .build();
        final DataException mpaException = assertThrows(
                DataException.class, () -> filmService.createFilm(testFilm)
        );
        assertEquals("Рейтинг не найден", mpaException.getMessage());
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().name("Драма").build());
        testFilm.setMpa(Mpa.builder().id(1).build());
        testFilm.setGenres(genres);
        final BadSqlGrammarException genreException = assertThrows(
                BadSqlGrammarException.class, () -> filmService.createFilm(testFilm)
        );
    }

    @Test
    void getPopularFilms() {
        film = filmService.createFilm(film);
        User testUser = User.builder()
                .login("user1")
                .email("user@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        testUser = userService.createUser(testUser);
        filmService.likeFilm(film.getId(), testUser.getId());
        film.setName("Film244");
        filmService.createFilm(film);
        List<Film> films = filmService.findPopularFilms(2);
        assertTrue(films.size() == 2);
    }

    Film film = Film.builder()
            .id(1)
            .name("Film")
            .description("NewFilm")
            .releaseDate(LocalDate.now())
            .duration(60)
            .mpa(Mpa.builder().id(1).build())
            .build();
}