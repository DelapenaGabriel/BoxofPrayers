package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.Comment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcCommentDao implements CommentDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Comment> getCommentsByRequestId(int requestId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.id, c.prayer_request_id, c.user_id, c.content, c.created_at, " +
                     "u.display_name, u.profile_image " +
                     "FROM comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.prayer_request_id = ? " +
                     "ORDER BY c.created_at ASC";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, requestId);
            while (results.next()) {
                comments.add(mapRowToComment(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return comments;
    }

    @Override
    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.id, c.prayer_request_id, c.user_id, c.content, c.created_at, " +
                     "u.display_name, u.profile_image " +
                     "FROM comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "ORDER BY c.created_at DESC";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                comments.add(mapRowToComment(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return comments;
    }

    @Override
    public Comment createComment(Comment comment) {
        String sql = "INSERT INTO comments (prayer_request_id, user_id, content) VALUES (?, ?, ?) RETURNING id";
        try {
            Integer newId = jdbcTemplate.queryForObject(sql, Integer.class,
                    comment.getPrayerRequestId(), comment.getUserId(), comment.getContent());
            return getCommentById(newId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public int deleteComment(int commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try {
            return jdbcTemplate.update(sql, commentId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Comment getCommentById(int id) {
        Comment comment = null;
        String sql = "SELECT c.id, c.prayer_request_id, c.user_id, c.content, c.created_at, " +
                     "u.display_name, u.profile_image " +
                     "FROM comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                comment = mapRowToComment(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return comment;
    }

    @Override
    public Comment updateComment(int commentId, String content) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, content, commentId);
            if (rowsAffected == 0) {
                return null;
            }
            return getCommentById(commentId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Comment mapRowToComment(SqlRowSet rs) {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setPrayerRequestId(rs.getInt("prayer_request_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setContent(rs.getString("content"));
        if (rs.getTimestamp("created_at") != null) {
            comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        comment.setUserName(rs.getString("display_name"));
        comment.setUserProfileImage(rs.getString("profile_image"));
        return comment;
    }
}
