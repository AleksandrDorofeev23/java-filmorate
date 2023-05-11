package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {

    Collection<Genre> getAllGenre();

    Genre getGenreById(int id);

}
