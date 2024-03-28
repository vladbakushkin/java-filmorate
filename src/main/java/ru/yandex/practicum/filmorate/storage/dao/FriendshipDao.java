package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, FRIEND_STATUS_CONFIRM) VALUES (?, ?, ?)";
        boolean isConfirmFriendship = false;
        jdbcTemplate.update(sql, userId, friendId, isConfirmFriendship);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        String sql = "SELECT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM USER_ACCOUNT u " +
                "JOIN FRIENDSHIP f ON u.ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sql, new UserMapper(), userId);
    }

    public List<User> getMutualFriends(int userId1, int userId2) {
        String sql = "SELECT u.ID as friend_id, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM USER_ACCOUNT u " +
                "JOIN (SELECT FRIEND_ID FROM friendship WHERE USER_ID = ?) fs1 " +
                "JOIN (SELECT FRIEND_ID FROM friendship WHERE USER_ID = ?) fs2 " +
                "ON fs1.FRIEND_ID = fs2.FRIEND_ID " +
                "WHERE u.ID = fs1.FRIEND_ID";
        return jdbcTemplate.query(sql, new UserMapper(), userId1, userId2);
    }
}
