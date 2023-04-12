package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private Map<Integer, Film> films = new HashMap();
    private int filmId = 0;
    private LocalDate filmsBirthDay = LocalDate.of(1895, 12, 28);

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Получен get запрос /films");
        return films.values();
    }

    @PostMapping("/films")
    public Film post(@Valid @RequestBody Film film) {
        log.info("Получен post запрос /films");
        if (film.getReleaseDate().isBefore(filmsBirthDay)) {
            throw new ValidationException("Неверная дата фильма");
        }
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping("/films")
    public Film put(@Valid @RequestBody Film film) {
        log.info("Получен put запрос /films");
        if (film.getReleaseDate().isBefore(filmsBirthDay)) {
            throw new ValidationException("Неверная дата фильма");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Такого фильма нет");
        }
        return film;
    }

}
