package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.BibleVerse;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Repository
public class JdbcBibleVerseDao implements BibleVerseDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBibleVerseDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public BibleVerse getDailyVerse() {
        BibleVerse verse = null;
        String sql = "SELECT * FROM bible_verses ORDER BY id";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            // Count total rows manually if needed or just load all IDs.
            // For simplicity and small dataset, let's load all to list and pick index.
            // A more optimized way is SELECT COUNT(*) -> Random Index if huge.
            // But requirement is "Daily Verse" consistent for the day.
            
            // Let's implement logic: 
            // 1. Get count
            // 2. Hash date -> index
            // 3. Select with limit/offset
            
            // Better SQL:
            // SELECT count(*)
            String countSql = "SELECT COUNT(*) FROM bible_verses";
            Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            if (count != null && count > 0) {
                 LocalDate today = LocalDate.now();
                 // Simple hash of date
                 int hash = today.hashCode(); 
                 int index = Math.abs(hash) % count;
                 
                 String fetchSql = "SELECT * FROM bible_verses ORDER BY id LIMIT 1 OFFSET ?";
                 SqlRowSet result = jdbcTemplate.queryForRowSet(fetchSql, index);
                 if (result.next()) {
                     verse = mapRowToBibleVerse(result);
                 }
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error accessing data", e);
        }
        return verse;
    }

    @Override
    public BibleVerse getRandomVerse() {
        BibleVerse verse = null;
        String sql = "SELECT * FROM bible_verses ORDER BY RANDOM() LIMIT 1";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            if (results.next()) {
                verse = mapRowToBibleVerse(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return verse;
    }

    private BibleVerse mapRowToBibleVerse(SqlRowSet rs) {
        BibleVerse verse = new BibleVerse();
        verse.setId(rs.getInt("id"));
        verse.setBook(rs.getString("book"));
        verse.setChapter(rs.getInt("chapter"));
        verse.setVerse(rs.getString("verse"));
        verse.setText(rs.getString("text"));
        verse.setVersion(rs.getString("version"));
        return verse;
    }
}
