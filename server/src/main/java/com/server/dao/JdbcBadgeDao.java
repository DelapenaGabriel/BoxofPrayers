package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.Badge;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcBadgeDao implements BadgeDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBadgeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Badge> getBadgesByUserId(int userId) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT b.*, ub.awarded_at FROM badges b " +
                     "JOIN user_badges ub ON b.id = ub.badge_id " +
                     "WHERE ub.user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()) {
                badges.add(mapRowToBadge(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return badges;
    }

    @Override
    public void awardBadge(int userId, String criteria) {
        String sql = "INSERT INTO user_badges (user_id, badge_id) " +
                     "SELECT ?, id FROM badges WHERE criteria = ? " +
                     "ON CONFLICT DO NOTHING"; 
        try {
            jdbcTemplate.update(sql, userId, criteria);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public boolean hasBadge(int userId, String criteria) {
        String sql = "SELECT COUNT(*) FROM user_badges ub " +
                     "JOIN badges b ON ub.badge_id = b.id " +
                     "WHERE ub.user_id = ? AND b.criteria = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, criteria);
            return count != null && count > 0;
        } catch (CannotGetJdbcConnectionException e) {
            return false;
        }
    }

    private Badge mapRowToBadge(SqlRowSet rs) {
        Badge b = new Badge();
        b.setId(rs.getInt("id"));
        b.setName(rs.getString("name"));
        b.setDescription(rs.getString("description"));
        b.setIconUrl(rs.getString("icon_url"));
        b.setCriteria(rs.getString("criteria"));
        if (rs.getTimestamp("awarded_at") != null) {
            b.setAwardedAt(rs.getTimestamp("awarded_at").toLocalDateTime());
        }
        return b;
    }
}
