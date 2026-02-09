package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.PostComment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPostCommentDao implements PostCommentDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostCommentDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<PostComment> getCommentsByPostId(int postId) {
        List<PostComment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.display_name, u.profile_image " +
                     "FROM post_comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? " +
                     "ORDER BY c.created_at ASC";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, postId);
            while (results.next()) {
                comments.add(mapRowToComment(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return comments;
    }

    @Override
    public PostComment getCommentById(int commentId) {
        PostComment comment = null;
        String sql = "SELECT c.*, u.display_name, u.profile_image " +
                     "FROM post_comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, commentId);
            if (results.next()) {
                comment = mapRowToComment(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return comment;
    }

    @Override
    public PostComment createComment(PostComment comment) {
        String sql = "INSERT INTO post_comments (post_id, user_id, content) VALUES (?, ?, ?) RETURNING id";
        try {
            Integer newId = jdbcTemplate.queryForObject(sql, Integer.class,
                    comment.getPostId(), comment.getUserId(), comment.getContent());
            return getCommentById(newId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM post_comments WHERE id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sql, commentId, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    private PostComment mapRowToComment(SqlRowSet rs) {
        PostComment comment = new PostComment();
        comment.setId(rs.getInt("id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        
        comment.setUserDisplayName(rs.getString("display_name"));
        comment.setUserProfileImage(rs.getString("profile_image"));
        return comment;
    }
}
