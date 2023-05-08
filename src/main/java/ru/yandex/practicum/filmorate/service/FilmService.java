package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Getter
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film likeFilm(int id, int userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        updateFilm(film);
        return film;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film deleteLike(int id, int userId) {
        filmStorage.getFilmById(id);
        userStorage.getUserById(userId);
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> findPopularFilms(int count) {
        Collection<Film> films = filmStorage.getAllFilms();
        List<Film> films1 = new ArrayList<>();
        films1.addAll(films);
        Comparator<Film> comparator = Comparator.comparingInt(o -> o.getLikes().size());
        Collections.sort(films1, comparator.reversed());
        if (count >= films.size()) {
            return films1;
        }
        return films1.subList(0, count);
    }

    public Film findFilm(int id) {
        return filmStorage.getFilmById(id);
    }
}
