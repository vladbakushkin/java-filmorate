package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.MinimumDate;
import ru.yandex.practicum.filmorate.annotation.PositiveDuration;
import ru.yandex.practicum.filmorate.serializer.DurationDeserializer;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * Film.
 */
@Data
@RequiredArgsConstructor
public class Film {

    public static final Comparator<Film> COMPARATOR_LIKES_ASC = Comparator.comparing(f -> f.getLikes().size());
    public static final Comparator<Film> COMPARATOR_LIKES_DESC = COMPARATOR_LIKES_ASC.reversed();

    private int id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description should contain no more than 200 characters")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @MinimumDate
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    @PositiveDuration
    private Duration duration;

    private Collection<Integer> likes = new HashSet<>();    // id пользователей лайкнувших фильм

    @Valid
    private Mpa mpa;

    private Collection<Genre> genres = new LinkedHashSet<>();   /* HashSet не проходит тесты в Postman
                                                                 (не совпадает порядок id в коллекции) */

    public Film(String name, String description, Mpa mpa, String releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.mpa = mpa;
        this.releaseDate = LocalDate.parse(releaseDate);
        this.duration = Duration.ofMinutes(duration);
    }

    public Film(String name, String description, String releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate);
        this.duration = Duration.ofMinutes(duration);
    }

    public List<Integer> getLikes() {
        return new ArrayList<>(likes);
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

    public List<Genre> getGenres() {
        return new ArrayList<>(genres);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("mpa_id", mpa.getId());
        values.put("genres", genres);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("likes", likes);
        return values;
    }
}
