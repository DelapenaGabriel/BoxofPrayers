package com.server.dao;

import com.server.exception.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcReactionDao implements ReactionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReactionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void addReaction(int postId, int userId, String type) {
        String sql = "INSERT INTO post_reactions (post_id, user_id, type) VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
        try {
            jdbcTemplate.update(sql, postId, userId, type);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void removeReaction(int postId, int userId, String type) {
        String sql = "DELETE FROM post_reactions WHERE post_id = ? AND user_id = ? AND type = ?";
        try {
            jdbcTemplate.update(sql, postId, userId, type);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
