package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
public class FilmController {

    private final FilmService filmService;

    private LocalDate filmsBirthDay = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAllFilms() {
        log.info("Получен get запрос /films");
        return filmService.findAllFilms();
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен post запрос /films");
        if (film.getReleaseDate().isBefore(filmsBirthDay)) {
            throw new ValidationException("Неверная дата фильма");
        }
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен put запрос /films");
        if (film.getReleaseDate().isBefore(filmsBirthDay)) {
            throw new ValidationException("Неверная дата фильма");
        }
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен put запрос /films/" + id + "/like/" + userId);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен delete запрос /films/" + id + "/like/" + userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен get запрос /films/popular");
        return filmService.findPopularFilms(count);
    }

    @GetMapping("/films/{id}")
    public Film findFilm(@PathVariable int id) {
        log.info("Получен get запрос /films/" + id);
        return filmService.findFilm(id);
    }

}
