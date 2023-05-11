package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Data
@Builder
public class User {
    private Set<Integer> friends;
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name = "";
    @PastOrPresent
    private LocalDate birthday;

}
