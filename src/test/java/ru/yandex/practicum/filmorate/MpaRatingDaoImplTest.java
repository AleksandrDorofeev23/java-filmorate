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
class MpaRatingDaoImplTest {
    private final MpaDaoImpl mpaRatingDao;

    @Test
    void findGenreByNormalId() {
        Mpa rating = mpaRatingDao.getMpaById(1);
        assertTrue(rating.getName().equals("G"), "name = G");
    }

    @Test
    void findGenreByWrongId() {
        final DataException exception = assertThrows(
                DataException.class,
                () -> mpaRatingDao.getMpaById(-1)
        );

        assertEquals("Рейтинг с идентификатором -1 не найден.", exception.getMessage());
    }

    @Test
    void getGenres() {
        Collection<Mpa> ratings = mpaRatingDao.getAllMpa();
        assertTrue(ratings.size() == 5, "ratings = 5");
    }

}