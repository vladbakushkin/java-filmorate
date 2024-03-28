package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private FriendshipDao friendshipDao;
    private User user1;
    private User user2;
    private User mutualFriend;

    @BeforeEach
    void setUp() {
        friendshipDao = new FriendshipDao(jdbcTemplate);
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);
        user1 = storage.createUser(new User("user1@email.ru", "vanya123", "Ivan Petrov",
                "1990-01-01"));
        user2 = storage.createUser(new User("user2@email.com", "petya321", "Petr First",
                "1672-02-02"));
        mutualFriend = storage.createUser(new User("friend@email.ru", "friend", "Mutual Friend",
                "2000-10-10"));
    }

    @Test
    void addFriend() {
        // when
        friendshipDao.addFriend(user1.getId(), user2.getId());
        List<User> user1Friends = friendshipDao.getFriends(user1.getId());
        List<User> user2Friends = friendshipDao.getFriends(user2.getId());

        // then
        assertThat(user1Friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user2));

        // then
        assertThat(user2Friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void removeFriend() {
        // given
        friendshipDao.addFriend(user1.getId(), user2.getId());

        // when
        friendshipDao.removeFriend(user1.getId(), user2.getId());
        List<User> user1Friends = friendshipDao.getFriends(user1.getId());

        // then
        assertThat(user1Friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void getFriends() {
        // given
        friendshipDao.addFriend(user1.getId(), user2.getId());
        friendshipDao.addFriend(user2.getId(), user1.getId());

        // when
        List<User> user1Friends = friendshipDao.getFriends(user1.getId());
        List<User> user2Friends = friendshipDao.getFriends(user2.getId());

        // then
        assertThat(user1Friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user2));
        // then
        assertThat(user2Friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user1));
    }

    @Test
    void getMutualFriends() {
        // given
        friendshipDao.addFriend(user1.getId(), mutualFriend.getId());
        friendshipDao.addFriend(user2.getId(), mutualFriend.getId());

        // when
        List<User> mutualFriendsUser1User2 = friendshipDao.getMutualFriends(user1.getId(), user2.getId());

        // then
        assertThat(mutualFriendsUser1User2)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(mutualFriend));
    }
}