package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_addFilm() throws Exception {
        Film film = new Film("nisi eiusmod", "Mark Heckler",
                "1967-03-25", 100);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(jsonPath("$.description").value("Mark Heckler"))
                .andExpect(jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(jsonPath("$.duration").value(100));
    }

    @Test
    void test2_addFilmFailName() throws Exception {
        Film film1 = new Film(null, "Description",
                "1900-03-25", 200);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Film film2 = new Film("", "Description",
                "1900-03-25", 200);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Film film3 = new Film(" ", "Description",
                "1900-03-25", 200);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film3))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test3_addFilmFailDescription() throws Exception {
        Film film = new Film("Film name", "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова," +
                " который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия»," +
                " стал кандидатом Коломбани.",
                "1900-03-25", 200);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test4_addFilmFailReleaseDate() throws Exception {
        Film film = new Film("Name", "Description",
                "1890-03-25", 200);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test5_addFilmFailDuration() throws Exception {
        Film film = new Film("Name", "Description",
                "1980-03-25", -200);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test6_updateFilm() throws Exception {
        Film film = new Film("nisi eiusmod", "Mark Heckler",
                "1967-03-25", 100);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));

        Film updatedFilm = new Film("Film Updated", "New film update description",
                "1989-04-17", 190);
        updatedFilm.setId(1);

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(updatedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Film Updated"))
                .andExpect(jsonPath("$.description").value("New film update description"))
                .andExpect(jsonPath("$.releaseDate").value("1989-04-17"))
                .andExpect(jsonPath("$.duration").value(190));
    }

    @Test
    void test7_updateUnknownFilm() throws Exception {
        Film updatedFilm = new Film("Film Updated", "New film update description",
                "1989-04-17", 190);
        updatedFilm.setId(9999);

        Objects.requireNonNull(mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(updatedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void test8_getAllFilms() throws Exception {
        Film film1 = new Film("film1", "film1",
                "1967-03-25", 100);

        Film film2 = new Film("film2", "film2",
                "1967-03-25", 100);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        MvcResult mvcResult = mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        List<Film> filmList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(2, filmList.size(), "Неверное количество фильмов в списке.");

        assertEquals(film1.getName(), filmList.get(0).getName(), "Неверное название фильма.");
        assertEquals(film1.getDescription(), filmList.get(0).getDescription(), "Неверное описание фильма.");
        assertEquals(film1.getReleaseDate(), filmList.get(0).getReleaseDate(), "Неверная дата релиза фильма.");
        assertEquals(film1.getDuration(), filmList.get(0).getDuration(), "Неверная продолжительность фильма.");

        assertEquals(film2.getName(), filmList.get(1).getName(), "Неверное название фильма.");
        assertEquals(film2.getDescription(), filmList.get(1).getDescription(), "Неверное описание фильма.");
        assertEquals(film2.getReleaseDate(), filmList.get(1).getReleaseDate(), "Неверная дата релиза фильма.");
        assertEquals(film2.getDuration(), filmList.get(1).getDuration(), "Неверная продолжительность фильма.");
    }
}