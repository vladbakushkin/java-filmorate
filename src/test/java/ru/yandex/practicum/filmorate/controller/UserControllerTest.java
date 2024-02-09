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
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_createUser() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("dolore"))
                .andExpect(jsonPath("$.name").value("Nick Name"))
                .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    @Test
    void test2_createUserFailLogin() throws Exception {
        User user1 = new User("mail@mail.ru", null, "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        User user2 = new User("mail@mail.ru", "", "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        User user3 = new User("mail@mail.ru", " ", "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user3))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        User user4 = new User("mail@mail.ru", "fail fail", "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user4))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test3_userCreateFailEmail() throws Exception {
        User user1 = new User("mail.ru", "doloreullamco", "name", "1980-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        User user2 = new User("this-wrong?email@", "doloreullamco", "name", "1980-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test4_userCreateFailBirthDay() throws Exception {
        User user1 = new User("test@mail.ru", "dolore", "", "2446-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test5_updateUser() throws Exception {
        User newUser = new User("test@mail.ru", "dolore", "est adipisicing", "1989-04-17");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updatedUser = new User("mail@yandex.ru", "doloreUpdate",
                "", "1976-09-20");
        updatedUser.setId(1);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.login").value("doloreUpdate"))
                .andExpect(jsonPath("$.name").value("doloreUpdate"))
                .andExpect(jsonPath("$.birthday").value("1976-09-20"));
    }

    @Test
    void test6_updateUnknownUser() throws Exception {
        User newUser = new User("test@mail.ru", "dolore", "", "1989-04-17");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updatedUser = new User("mail@yandex.ru", "doloreUpdate",
                "est adipisicing", "1976-09-20");
        updatedUser.setId(9999);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void test7_getAllUsers() throws Exception {
        User user1 = new User("one@mail.ru", "1", "name1", "1980-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User("two@mail.ru", "2", "name2", "1980-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        List<User> usersList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(2, usersList.size(), "Неверное количество фильмов в списке.");

        assertEquals(user1.getName(), usersList.get(0).getName(), "Неверное имя пользователя.");
        assertEquals(user1.getEmail(), usersList.get(0).getEmail(), "Неверный email пользователя.");
        assertEquals(user1.getLogin(), usersList.get(0).getLogin(), "Неверный логин пользователя.");
        assertEquals(user1.getBirthday(), usersList.get(0).getBirthday(), "Неверная дата рождения пользователя.");

        assertEquals(user2.getName(), usersList.get(1).getName(), "Неверное имя пользователя.");
        assertEquals(user2.getEmail(), usersList.get(1).getEmail(), "Неверный email пользователя.");
        assertEquals(user2.getLogin(), usersList.get(1).getLogin(), "Неверный логин пользователя.");
        assertEquals(user2.getBirthday(), usersList.get(1).getBirthday(), "Неверная дата рождения пользователя.");
    }

    @Test
    void test8_createUserWithEmptyName() throws Exception {
        User user1 = new User("friend@common.ru", "common", null, "2000-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("friend@common.ru"))
                .andExpect(jsonPath("$.login").value("common"))
                .andExpect(jsonPath("$.name").value("common"))
                .andExpect(jsonPath("$.birthday").value("2000-08-20"));

        User user2 = new User("friend@common.ru", "common", "", "2000-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("friend@common.ru"))
                .andExpect(jsonPath("$.login").value("common"))
                .andExpect(jsonPath("$.name").value("common"))
                .andExpect(jsonPath("$.birthday").value("2000-08-20"));

        User user3 = new User("friend@common.ru", "common", " ", "2000-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user3))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("friend@common.ru"))
                .andExpect(jsonPath("$.login").value("common"))
                .andExpect(jsonPath("$.name").value("common"))
                .andExpect(jsonPath("$.birthday").value("2000-08-20"));
    }
}