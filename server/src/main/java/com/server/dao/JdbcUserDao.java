package com.server.dao;


import com.server.exception.DaoException;
import com.server.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                user = mapRowToUser(results);
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }

    @Override
    public List<User> getUsers() {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                User user = mapRowToUser(results);
                users.add(user);
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return users;
    }

    @Override
    public User getUserByEmail(String email) {

        if (email == null) {
            email = "";
        }
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, email);
            if (results.next()) {
                user = mapRowToUser(results);
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }

    @Override
    public User createUser(User newUser) {

        User user = null;
        String insertUserSql = "INSERT INTO users " +
                "(name, display_name, email, password_hash, role, profile_image) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "RETURNING id";

        if (newUser.getHashedPassword() == null) {
            throw new DaoException("User cannot be created with null password");
        }
        try {
            String passwordHash = new BCryptPasswordEncoder().encode(newUser.getHashedPassword());
            String defaultAvatar = "https://static.vecteezy.com/system/resources/previews/013/360/247/non_2x/default-avatar-photo-icon-social-media-profile-sign-symbol-vector.jpg";

            Integer userId = jdbcTemplate.queryForObject(insertUserSql, int.class, newUser.getName(), newUser.getDisplayName(),
                    newUser.getEmail(), passwordHash, newUser.getRole(), defaultAvatar);
            user =  getUserById(userId);
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return user;
    }

    @Override
    public void updateProfileImage(String email, String imageUrl) {
        String sql = "UPDATE users SET profile_image = ? WHERE email = ?";
        try {
            jdbcTemplate.update(sql, imageUrl, email);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public void updateUserProfile(int userId, String name, String displayName, String profileImage) {
        String sql = "UPDATE users SET name = ?, display_name = ?, profile_image = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, name, displayName, profileImage, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public List<com.server.model.LeaderboardDto> getTopPrayerWarriors(String timeFrame, int limit) {
        List<com.server.model.LeaderboardDto> leaderboard = new ArrayList<>();
        String sql = """
            SELECT 
                u.id, 
                u.display_name, 
                u.profile_image, 
                COUNT(p.id) as prayer_count 
            FROM users u 
            JOIN prayers p ON u.id = p.user_id 
            WHERE 1=1
        """;

        if ("weekly".equalsIgnoreCase(timeFrame)) {
            sql += " AND p.prayed_at >= NOW() - INTERVAL '7 days'";
        } else if ("monthly".equalsIgnoreCase(timeFrame)) {
            sql += " AND p.prayed_at >= NOW() - INTERVAL '30 days'";
        }

        sql += """
             GROUP BY u.id, u.display_name, u.profile_image
             ORDER BY prayer_count DESC
             LIMIT ?
        """;

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, limit);
            while (results.next()) {
                com.server.model.LeaderboardDto entry = new com.server.model.LeaderboardDto();
                entry.setId(results.getInt("id"));
                entry.setName(results.getString("display_name"));
                entry.setPrayerCount(results.getInt("prayer_count"));
                entry.setAvatar(results.getString("profile_image"));
                leaderboard.add(entry);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return leaderboard;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setDisplayName(rs.getString("display_name"));
        user.setEmail(rs.getString("email"));
        user.setHashedPassword(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setProfileImage(rs.getString("profile_image"));
        return user;
    }
}
