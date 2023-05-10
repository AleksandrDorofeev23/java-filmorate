package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Dao.Impl.GenreDaoImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDaoImplTest {

    private final GenreDaoImpl genreDao;

    @Test
    void findGenre() {
        Genre genre1 = genreDao.getGenreById(1);
        Genre genre2 = genreDao.getGenreById(2);
        Genre genre3 = genreDao.getGenreById(3);
        assertTrue(genre1.getName().equals("Комедия"));
        assertTrue(genre2.getName().equals("Драма"));
        assertTrue(genre3.getName().equals("Мультфильм"));
    }

    @Test
    void findAll() {
        Collection<Genre> genres = genreDao.getAllGenre();
        assertTrue(genres.size() == 6);
    }

    @Test
    void findGenreWithFailId() {
        final DataException exception = assertThrows(
                DataException.class, () -> genreDao.getGenreById(10)
        );
        assertEquals("Такой жанр не найден", exception.getMessage());
    }
}
