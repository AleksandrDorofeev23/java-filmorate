package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {

    Collection<Mpa> getAllMpa();

    Mpa getMpaById(int id);

}
