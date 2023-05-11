package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.Dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "SELECT * FROM films WHERE name=? AND duration=?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, film.getName(), film.getDuration());
        if (row.next()) {
            throw new DataException("Фильм  уже существует");
        }
        List<Genre> genres = film.getGenres();
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            film.setMpa(mpaDao.getMpaById(mpaId));
        } else {
            film.setMpa(new Mpa(0, null));
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(getValue(film)).intValue());
        film.setGenres(updateGenres(genres, film.getId()));
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
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
            List<Genre> genres;
            List<Integer> genresInt = new ArrayList<>();
            while (genreRows.next()) {
                genresInt.add(genreRows.getInt("genre_id"));
            }
            String ids = genresInt.stream().map(String::valueOf).collect(Collectors.joining(","));
            String sqlGenre = "SELECT * FROM genres WHERE id IN (" + ids + ")";
            genres = jdbcTemplate.query(sqlGenre, FilmDbStorage::getGenre);
            sql = "SELECT user_id FROM likes WHERE film_id = ?";
            SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, id);
            Set<Integer> likes = new HashSet<>();
            while (likesRows.next()) {
                likes.add(likesRows.getInt("user_id"));
            }

            Film film = new Film(
                    likes,
                    rows.getInt("film_id"),
                    rows.getString("name"),
                    rows.getString("description"),
                    LocalDate.parse(rows.getString("release_date"), format),
                    rows.getInt("duration"),
                    genres,
                    mpaDao.getMpaById(Integer.parseInt(rows.getString("mpa_id"))));
            return film;
        } else {
            throw new DataException("Фильм не найден.");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM films " +
                "LEFT OUTER JOIN mpa ON films.mpa_id = mpa.id " +
                "LEFT OUTER JOIN film_genres ON films.film_id = film_genres.film_id " +
                "LEFT OUTER JOIN genres ON genres.id = film_genres.genre_id " +
                "LEFT OUTER JOIN likes ON likes.film_id = films.film_id";
        jdbcTemplate.query(sql, rows -> {
            List<Genre> genres = new ArrayList<>();
            int genreId = rows.getInt("genre_id");
            if (!rows.wasNull()) {
                String name = rows.getString("genres.name");
                Genre genre = new Genre(genreId, name);
                genres.add(genre);
            }
            Set<Integer> likes = new HashSet<>();
            if (!rows.wasNull()) {
                likes.add(rows.getInt("user_id"));
            }
            films.add(new Film(
                    likes,
                    rows.getInt("film_id"),
                    rows.getString("name"),
                    rows.getString("description"),
                    LocalDate.parse(rows.getString("release_date"), format),
                    rows.getInt("duration"),
                    genres,
                    new Mpa(rows.getInt("id"), rows.getString("mpa.name"))));
        });
        Collections.sort(films, Comparator.comparingInt(Film::getId));
        return films;
    }

    private List<Genre> updateGenres(List<Genre> genres, Integer id) {
        List<Genre> genresNew = new ArrayList<>();
        String sql = "DELETE FROM film_genres WHERE film_id = ?;";
        jdbcTemplate.update(sql, id);
        if (genres != null && !genres.isEmpty()) {
            List<Integer> genresInt = genres
                    .stream()
                    .distinct()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            String ids = genresInt.stream().map(String::valueOf).collect(Collectors.joining(","));
            String sqlGenre = "SELECT * FROM genres WHERE id IN (" + ids + ")";
            genresNew = jdbcTemplate.query(sqlGenre, FilmDbStorage::getGenre);
            String repeat = "";
            for (Genre genre : genresNew) {
                repeat += "(" + id + ", " + genre.getId() + "),";
            }
            if (repeat.endsWith(",")) {
                repeat = repeat.substring(0, repeat.length() - 1);
            }
            sqlGenre = "INSERT INTO film_genres (film_id, genre_id) VALUES" + repeat + ";";
            jdbcTemplate.update(sqlGenre);
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
        String repeat = "";
        for (Integer like : likes) {
            repeat += "(" + id + ", " + like + "),";
        }
        if (repeat.endsWith(",")) {
            repeat = repeat.substring(0, repeat.length() - 1);
        }
        sql = "INSERT INTO likes (film_id, user_id) VALUES" + repeat + ";";
        jdbcTemplate.update(sql);

        return likes;
    }

    public Map<String, Object> getValue(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    static Genre getGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("name")
        );
    }

}
