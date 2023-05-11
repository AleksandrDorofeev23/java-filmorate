package ru.yandex.practicum.filmorate.storage.Dao.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenre() {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM genres");
        List<Genre> genres = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            rows.next();
            Genre genre = Genre.builder()
                    .id(rows.getInt("id"))
                    .name(rows.getString("name"))
                    .build();
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE id = ?", id);
        if (rows.next()) {
            return Genre.builder()
                    .id(rows.getInt("id"))
                    .name(rows.getString("name"))
                    .build();

        } else {
            throw new DataException("Такой жанр не найден");
        }
    }

}
