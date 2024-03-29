package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    void test1_addFilm() throws Exception {
        Film film = new Film("nisi eiusmod", "Mark Heckler",
                "1967-03-25", 100);

        when(filmService.addFilm(any(Film.class))).thenReturn(film);

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

        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

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

        when(filmService.updateFilm(any(Film.class))).thenThrow(new FilmNotFoundException("Film not found"));

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(updatedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(FilmNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void test8_getAllFilms() throws Exception {
        Film film1 = new Film("film1", "film1",
                "1967-03-25", 100);

        Film film2 = new Film("film2", "film2",
                "1967-03-25", 100);

        List<Film> films = Arrays.asList(film1, film2);

        when(filmService.findAll()).thenReturn(films);

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

    @Test
    void test9_getFilmById1() throws Exception {
        Film film = new Film("film", "film",
                "1967-03-25", 100);

        when(filmService.findFilmById(1)).thenReturn(film);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Film film1 = objectMapper.readValue(responseJson, Film.class);

        assertEquals(film.getName(), film1.getName(), "Неверное название фильма.");
        assertEquals(film.getDescription(), film1.getDescription(), "Неверное описание фильма.");
        assertEquals(film.getReleaseDate(), film1.getReleaseDate(), "Неверная дата релиза фильма.");
        assertEquals(film.getDuration(), film1.getDuration(), "Неверная продолжительность фильма.");
    }

    @Test
    void test10_getFilmUnknownWithId9999() throws Exception {
        Film film = new Film("film", "film",
                "1967-03-25", 100);

        when(filmService.findFilmById(9999)).thenThrow(new FilmNotFoundException("Film not found"));

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test11_addLike() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");

        Film film1 = new Film("film1", "film1", "1967-03-25", 90);
        Film film2 = new Film("film2", "film2", "1967-03-25", 150);
        Film film3 = new Film("film3", "film3", "1967-03-25", 100);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

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

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film3))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film2.addLike(1);
        when(filmService.addLike(2, 1)).thenReturn(film2);
        when(filmService.getMostPopularFilms(1)).thenReturn(List.of(film2));

        mockMvc.perform(
                        put("/films/2/like/1"))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(
                        get("/films/popular?count=1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        List<Film> filmList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(1, filmList.size(), "Неверное количество фильмов в списке.");

        assertEquals(film2.getName(), filmList.get(0).getName(), "Неверное название фильма.");
        assertEquals(film2.getDescription(), filmList.get(0).getDescription(), "Неверное описание фильма.");
        assertEquals(film2.getReleaseDate(), filmList.get(0).getReleaseDate(), "Неверная дата релиза фильма.");
        assertEquals(film2.getDuration(), filmList.get(0).getDuration(), "Неверная продолжительность фильма.");
        assertEquals(1, filmList.get(0).getLikes().size(), "Неверное количество лайков.");
    }

    @Test
    void test12_removeLike() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");

        Film film = new Film("film1", "film1", "1967-03-25", 90);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.addLike(1);
        when(filmService.addLike(1, 1)).thenReturn(film);

        mockMvc.perform(
                        put("/films/1/like/1"))
                .andExpect(status().isOk());

        when(filmService.findFilmById(1)).thenReturn(film);

        MvcResult mvcResult1 = mockMvc.perform(
                        get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson1 = mvcResult1.getResponse().getContentAsString();
        Film film1WithLike = objectMapper.readValue(responseJson1, Film.class);

        assertEquals(1, film1WithLike.getLikes().size(), "Неверное количество лайков.");

        film.removeLike(1);
        when(filmService.removeLike(1, 1)).thenReturn(film);

        mockMvc.perform(
                        delete("/films/1/like/1"))
                .andExpect(status().isOk());

        MvcResult mvcResult2 = mockMvc.perform(
                        get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson2 = mvcResult2.getResponse().getContentAsString();
        Film film1WithoutLike = objectMapper.readValue(responseJson2, Film.class);

        assertEquals(0, film1WithoutLike.getLikes().size(), "Неверное количество фильмов в списке.");
    }

    @Test
    void test13_removeLikeFromUnknownUser() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");

        Film film = new Film("film1", "film1", "1967-03-25", 90);
        when(filmService.removeLike(1, -2)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/films/1/like/-2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test14_getAllPopularFilms() throws Exception {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            users.add(new User("mail@mail.ru", "dolore" + i, "Nick Name" + i, "1946-08-20"));
        }

        for (int i = 0; i < 6; i++) {
            mockMvc.perform(
                            post("/users")
                                    .content(objectMapper.writeValueAsString(users.get(i)))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        List<Film> films = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            films.add(new Film("film" + i, "film" + i, "1967-03-25", 90));
        }

        for (int i = 0; i < 15; i++) {
            mockMvc.perform(
                            post("/films")
                                    .content(objectMapper.writeValueAsString(films.get(i)))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(
                        put("/films/6/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/6/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/6/like/3"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/9/like/4"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/9/like/5"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films/14/like/6"))
                .andExpect(status().isOk());

        films.get(6).addLike(1);
        films.get(6).addLike(2);
        films.get(6).addLike(3);
        films.get(9).addLike(4);
        films.get(9).addLike(5);
        films.get(14).addLike(6);

        when(filmService.getMostPopularFilms(10)).thenReturn(List.of(films.get(6), films.get(9), films.get(14),
                films.get(0), films.get(1), films.get(2), films.get(3), films.get(4), films.get(5), films.get(7)));

        MvcResult mvcResult = mockMvc.perform(
                        get("/films/popular"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        List<Film> filmList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(10, filmList.size(), "Неверное количество фильмов в списке.");

        assertEquals(3, filmList.get(0).getLikes().size(), "Неверное количество лайков.");
        assertEquals(2, filmList.get(1).getLikes().size(), "Неверное количество лайков.");
        assertEquals(1, filmList.get(2).getLikes().size(), "Неверное количество лайков.");
        assertEquals(0, filmList.get(3).getLikes().size(), "Неверное количество лайков.");

        when(filmService.getMostPopularFilms(5)).thenReturn(List.of(films.get(6), films.get(9), films.get(14),
                films.get(0), films.get(1)));

        MvcResult mvcResult2 = mockMvc.perform(
                        get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson2 = mvcResult2.getResponse().getContentAsString();
        List<Film> filmList2 = objectMapper.readValue(responseJson2, new TypeReference<>() {
        });

        assertEquals(5, filmList2.size(), "Неверное количество фильмов в списке.");

        assertEquals(3, filmList2.get(0).getLikes().size(), "Неверное количество лайков.");
        assertEquals(2, filmList2.get(1).getLikes().size(), "Неверное количество лайков.");
        assertEquals(1, filmList2.get(2).getLikes().size(), "Неверное количество лайков.");
        assertEquals(0, filmList2.get(3).getLikes().size(), "Неверное количество лайков.");
        assertEquals(0, filmList2.get(4).getLikes().size(), "Неверное количество лайков.");
    }
}