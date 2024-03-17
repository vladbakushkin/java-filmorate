package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friendship (USER_ID, FRIEND_ID, FRIEND_STATUS_CONFIRM) VALUES (?, ?, ?)";
        boolean isConfirmFriendship = false;
        jdbcTemplate.update(sql, userId, friendId, isConfirmFriendship);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM user_account u " +
                "JOIN friendship f ON u.id = f.FRIEND_ID " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    public List<User> getMutualFriends(int userId1, int userId2) {
        String sql = "SELECT u.id as friend_id, u.email, u.login, u.name, u.birthday " +
                "FROM user_account u " +
                "JOIN (SELECT friend_id FROM friendship WHERE user_id = ?) fs1 " +
                "JOIN (SELECT friend_id FROM friendship WHERE user_id = ?) fs2 " +
                "ON fs1.friend_id = fs2.friend_id " +
                "WHERE u.id = fs1.friend_id";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId1, userId2);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getString("name"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("birthday"));
        user.setId(rs.getInt("id"));
        return user;
    }
}
