package ru.yandex.practicum.filmorate.storage.Dao.Impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Dao.MpaDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        List<Mpa> mpas = new ArrayList<>();
        while (rows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(rows.getInt("id"))
                    .name(rows.getString("name"))
                    .build();
            mpas.add(mpa);
        }
        return mpas;
    }

    @Override
    public Mpa getMpaById(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?", id);
        if (rows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(rows.getInt("id"))
                    .name(rows.getString("name"))
                    .build();
            return mpa;
        } else {
            throw new DataException("Рейтинг не найден");
        }
    }
}
