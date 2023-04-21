package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap();
    private int filmId = 0;
    private LocalDate filmsBirthDay = LocalDate.of(1895, 12, 28);

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(filmsBirthDay)) {
            throw new ValidationException("Неверная дата фильма");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может превышать 200 символов");
        }
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isBefore(filmsBirthDay)) {
            throw new ValidationException("Неверная дата фильма");
        }
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
