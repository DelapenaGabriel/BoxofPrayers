package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.Notification;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcNotificationDao implements NotificationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNotificationDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.*, u.display_name as sender_name, u.profile_image as sender_image " +
                     "FROM notifications n " +
                     "LEFT JOIN users u ON n.sender_id = u.id " +
                     "WHERE n.user_id = ? " +
                     "ORDER BY n.created_at DESC";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()) {
                notifications.add(mapRowToNotification(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return notifications;
    }

    @Override
    public void createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, message, sender_id) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, notification.getUserId(), notification.getMessage(), notification.getSenderId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public void markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try {
            jdbcTemplate.update(sql, notificationId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    private Notification mapRowToNotification(SqlRowSet rs) {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        if (rs.getTimestamp("created_at") != null) {
            n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        n.setSenderId(rs.getInt("sender_id"));
        if (rs.getString("sender_name") != null) {
           n.setSenderName(rs.getString("sender_name"));
        }
        if (rs.getString("sender_image") != null) {
           n.setSenderProfileImage(rs.getString("sender_image"));
        }
        return n;
    }
}
