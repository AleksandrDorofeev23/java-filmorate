package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.Dao.MpaDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "SELECT * FROM films WHERE name=? AND release_date=?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, film.getName(), film.getReleaseDate());
        if (row.next()) {
            throw new DataException("Фильм существует");
        }
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDao.getGenreById(genre.getId());
            }
        }
        int mpaId = film.getMpa().getId();
        film.setMpa(mpaDao.getMpaById(mpaId));
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setGenres(updateGenres(genres, film.getId()));
        System.out.println(film.getGenres());

        film.setId(simpleJdbcInsert.executeAndReturnKey(film.getValue()).intValue());
        return film;

    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, film.getId());
        if (rows.next()) {
            sql = "UPDATE films SET name= ?, description = ?, release_date = ?, " +
                    "duration = ?, mpa_id = ? WHERE film_id = ?;";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            List<Genre> genres = film.getGenres();
            film.setGenres(updateGenres(genres, film.getId()));
            Set<Integer> likes = film.getLikes();
            film.setLikes(updateLikes(likes, film.getId()));
            return film;
        } else {
            throw new DataException("Фильм не найден.");
        }
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);

        if (filmRows.next()) {
            sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
            List<Genre> genres = new ArrayList<>();
            while (genreRows.next()) {
                int genreId = genreRows.getInt("genre_id");
                genres.add(genreDao.getGenreById(genreId));
            }

            sql = "SELECT user_id FROM likes WHERE film_id = ?";
            SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, id);
            Set<Integer> likes = new HashSet<>();
            while (likesRows.next()) {
                likes.add(likesRows.getInt("user_id"));
            }

            Film film = new Film(
                    likes,
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    LocalDate.parse(filmRows.getString("release_date"), formatter),
                    filmRows.getInt("duration"),
                    genres,
                    mpaDao.getMpaById(Integer.parseInt(filmRows.getString("mpa_id"))));
            return film;
        } else {
            throw new DataException("Фильм не найден.");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT film_id FROM films");
        while (rows.next()) {
            films.add(getFilmById(rows.getInt("film_id")));
        }
        Collections.sort(films, Comparator.comparingInt(Film::getId));
        return films;
    }

    private List<Genre> updateGenres(List<Genre> genres, Integer id) {
        List<Genre> genresNew = new ArrayList<>();
        String sql = "DELETE FROM film_genres WHERE film_id = ?;";
        jdbcTemplate.update(sql, id);
        if (genres != null && !genres.isEmpty()) {
            genres = genres.stream()
                    .distinct()
                    .collect(Collectors.toList());
            for (Genre genre : genres) {
                sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);";
                jdbcTemplate.update(sql, id, genre.getId());
                genre = genreDao.getGenreById(genre.getId());
                genresNew.add(genre);
            }
        }
        return genresNew;

    }

    public Film deleteLike(int id, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, id, userId);
        return getFilmById(id);
    }

    private Set<Integer> updateLikes(Set<Integer> likes, Integer id) {
        String sql = "DELETE FROM likes WHERE film_id = ?;";
        jdbcTemplate.update(sql, id);
        if (likes == null || likes.isEmpty()) {
            return likes;
        }
        for (Integer like : likes) {
            sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
            jdbcTemplate.update(sql, id, like);
        }
        return likes;
    }

}
