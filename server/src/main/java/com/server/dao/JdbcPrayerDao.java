package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.Prayer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPrayerDao implements PrayerDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcPrayerDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<Prayer> getAllPrayers() {
        List<Prayer> prayers = new ArrayList<>();

        String sql = "SELECT * FROM prayers;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while(result.next()){
                prayers.add(mapRowToPrayer(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return prayers;
    }

    @Override
    public List<Prayer> getAllPrayersByUserId(int userId){
        List<Prayer> prayers = new ArrayList<>();

        String sql = "SELECT * FROM prayers WHERE user_id = ?;";

        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()){
                prayers.add(mapRowToPrayer(results));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return prayers;
    }

    @Override
    public List<Prayer> getAllPrayersByPrayerRequestId(int prayerRequestId) {
        List<Prayer> prayers = new ArrayList<>();

        String sql = "SELECT * FROM prayers WHERE prayer_request_id = ?;";

        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, prayerRequestId);
            while (results.next()){
                prayers.add(mapRowToPrayer(results));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return prayers;
    }

    @Override
    public Prayer getPrayerById(int id) {
        Prayer prayer = null;

        String sql = "SELECT * FROM prayers WHERE id = ?;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()){
                prayer = mapRowToPrayer(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return prayer;
    }

    @Override
    public Prayer createPrayer(Prayer newPrayer) {
        Prayer prayer = null;
        int newId;

        String sql = "INSERT INTO prayers (prayer_request_id, user_id, prayed_at) " +
                "VALUES (?, ?, ?) RETURNING id;";

        try{
            newId = jdbcTemplate.queryForObject(sql, int.class, newPrayer.getPrayerRequestId(), newPrayer.getUserId(), newPrayer.getPrayedAt());
            prayer = getPrayerById(newId);
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return prayer;
    }

    @Override
    public int deletePrayer(int id) {
        String sql = "DELETE FROM prayers WHERE id = ?;";

        try{
            return jdbcTemplate.update(sql, id);
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Prayer mapRowToPrayer(SqlRowSet rowSet){
        Prayer prayer = new Prayer();
        prayer.setId(rowSet.getInt("id"));
        prayer.setPrayerRequestId(rowSet.getInt("prayer_request_id"));
        int userId = rowSet.getInt("user_id");
        prayer.setUserId(rowSet.wasNull()? null : userId);
        if (rowSet.getTimestamp("prayed_at") != null){
            prayer.setPrayedAt(rowSet.getTimestamp("prayed_at").toLocalDateTime());
        }
        return prayer;
    }
}
