package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

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
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()));
    }

    @Test
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

        assertEquals(2, usersList.size(), "Неверное количество пользователей в списке.");

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

    @Test
    void test9_getUserById1() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        User userFromJson = objectMapper.readValue(responseJson, User.class);

        assertEquals(user.getName(), userFromJson.getName(), "Неверное имя пользователя.");
        assertEquals(user.getEmail(), userFromJson.getEmail(), "Неверный email пользователя.");
        assertEquals(user.getLogin(), userFromJson.getLogin(), "Неверный логин пользователя.");
        assertEquals(user.getBirthday(), userFromJson.getBirthday(), "Неверная дата рождения пользователя.");
    }

    @Test
    void test10_getUserUnknownWithId9999() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test11_userGetEmptyMutualFriends() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User friend = new User("friend@mail.ru", "friend", "friend adipisicing", "1976-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        List<User> usersList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(0, usersList.size(), "Список общих друзей не пуст.");
    }

    @Test
    void test12_addFriend() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User friend = new User("friend@mail.ru", "friend", "friend adipisicing", "1976-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users/1/friends/2"))
                .andExpect(status().isOk());

        MvcResult mvcResult1 = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String friendJson1 = mvcResult1.getResponse().getContentAsString();
        List<User> friends1 = objectMapper.readValue(friendJson1, new TypeReference<>() {
        });

        assertEquals(1, friends1.size(), "Неверное количество друзей в списке.");

        assertEquals(friend.getName(), friends1.get(0).getName(), "Неверное имя пользователя.");
        assertEquals(friend.getEmail(), friends1.get(0).getEmail(), "Неверный email пользователя.");
        assertEquals(friend.getLogin(), friends1.get(0).getLogin(), "Неверный логин пользователя.");
        assertEquals(friend.getBirthday(), friends1.get(0).getBirthday(), "Неверная дата рождения пользователя.");

        MvcResult mvcResult2 = mockMvc.perform(get("/users/2/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String friendJson2 = mvcResult2.getResponse().getContentAsString();
        List<User> friends2 = objectMapper.readValue(friendJson2, new TypeReference<>() {
        });

        assertEquals(1, friends2.size(), "Неверное количество друзей в списке.");

        assertEquals(user.getName(), friends2.get(0).getName(), "Неверное имя пользователя.");
        assertEquals(user.getEmail(), friends2.get(0).getEmail(), "Неверный email пользователя.");
        assertEquals(user.getLogin(), friends2.get(0).getLogin(), "Неверный логин пользователя.");
        assertEquals(user.getBirthday(), friends2.get(0).getBirthday(), "Неверная дата рождения пользователя.");
    }

    @Test
    void test13_addFriendUnknownId() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User friend = new User("friend@mail.ru", "friend", "friend adipisicing", "1976-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users/1/friends/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test14_getMutualFriends() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User friend = new User("friend@mail.ru", "friend", "friend adipisicing", "1976-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User mutualFriend = new User("friend@common.ru", "common", "", "2000-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(mutualFriend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users/1/friends/3"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users/2/friends/3"))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andReturn();

        String friendsJson = mvcResult.getResponse().getContentAsString();
        List<User> friends = objectMapper.readValue(friendsJson, new TypeReference<>() {
        });

        assertEquals(1, friends.size(), "Неверное количество друзей в списке.");

        assertEquals("common", friends.get(0).getName(), "Неверное имя пользователя.");
        assertEquals(mutualFriend.getEmail(), friends.get(0).getEmail(), "Неверный email пользователя.");
        assertEquals(mutualFriend.getLogin(), friends.get(0).getLogin(), "Неверный логин пользователя.");
        assertEquals(mutualFriend.getBirthday(), friends.get(0).getBirthday(), "Неверная дата рождения пользователя.");
    }

    @Test
    void test15_removeFriend() throws Exception {
        User user = new User("mail@mail.ru", "dolore", "Nick Name", "1946-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User friend = new User("friend@mail.ru", "friend", "friend adipisicing", "1976-08-20");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        MvcResult mvcResult1 = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String friendJson1 = mvcResult1.getResponse().getContentAsString();
        List<User> friends1 = objectMapper.readValue(friendJson1, new TypeReference<>() {
        });

        assertEquals(0, friends1.size(), "Неверное количество друзей в списке.");

        MvcResult mvcResult2 = mockMvc.perform(get("/users/2/friends"))
                .andExpect(status().isOk())
                .andReturn();

        String friendJson2 = mvcResult2.getResponse().getContentAsString();
        List<User> friends2 = objectMapper.readValue(friendJson2, new TypeReference<>() {
        });

        assertEquals(0, friends2.size(), "Неверное количество друзей в списке.");
    }
}