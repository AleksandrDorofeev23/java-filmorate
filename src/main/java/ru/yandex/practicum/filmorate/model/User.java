package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Set<Integer> friends = new HashSet<>();
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name = "";
    @PastOrPresent
    private LocalDate birthday;
}
