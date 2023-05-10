package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Dao.Impl.MpaDaoImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDaoImpllTest {

    private final MpaDaoImpl mpaRatingDao;

    @Test
    void findGenre() {
        Mpa rating1 = mpaRatingDao.getMpaById(1);
        Mpa rating2 = mpaRatingDao.getMpaById(2);
        Mpa rating3 = mpaRatingDao.getMpaById(3);
        assertTrue(rating1.getName().equals("G"));
        assertTrue(rating2.getName().equals("PG"));
        assertTrue(rating3.getName().equals("PG-13"));
    }

    @Test
    void findGenres() {
        Collection<Mpa> ratings = mpaRatingDao.getAllMpa();
        assertTrue(ratings.size() == 5);
    }

    @Test
    void findGenreWithFailId() {
        final DataException exception = assertThrows(
                DataException.class, () -> mpaRatingDao.getMpaById(6)
        );
        assertEquals("Рейтинг не найден", exception.getMessage());
    }

}
