package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.*;

@Service
@Getter
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film likeFilm(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        return film;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film deleteLike(int id, int userId) {
        if (userId < 0) {
            throw new DataException("ID пользователя должно быть положительным");
        }
        Film film = filmStorage.getFilmById(id);
        Set<Integer> likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);
        return film;
    }

    public List<Film> findPopularFilms(int count) {
        Collection<Film> films =  filmStorage.getAllFilms();
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
