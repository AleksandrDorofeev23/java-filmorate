package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    @PositiveOrZero
    Integer duration;
}
