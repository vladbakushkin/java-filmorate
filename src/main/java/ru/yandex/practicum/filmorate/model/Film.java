package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinimumDate;
import ru.yandex.practicum.filmorate.annotation.PositiveDuration;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {

    private int id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description should contain no more than 200 characters")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @MinimumDate
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @PositiveDuration
    private Duration duration;

    private Set<Integer> likes;    // id пользователей лайкнувших фильм

    public Film(String name, String description, String releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate);
        this.duration = Duration.ofMinutes(duration);
        this.likes = new HashSet<>();
    }
}
