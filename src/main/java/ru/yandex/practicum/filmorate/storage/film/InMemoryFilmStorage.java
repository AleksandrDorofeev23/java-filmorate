package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap();
    private int filmId = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new DataException("Такого фильма нет");
        }
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new DataException("Фильм " + id + " не найден.");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

}
