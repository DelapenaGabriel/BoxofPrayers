package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.PrayerRequest;
import com.server.model.PrayerRequestDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPrayerRequestDao implements PrayerRequestDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcPrayerRequestDao (DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<PrayerRequestDto> getAllPrayerRequest(String category, Boolean isAnswered) {
        List<PrayerRequestDto> requests = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        List<String> whereClauses = new ArrayList<>();

        String sql = """
                        SELECT
                            pr.id,
                            pr.requester_id,
                            pr.name,
                            pr.content,
                            pr.category,
                            pr.is_visible,
                            pr.is_answered,
                            pr.answer_content,
                            pr.created_at,
                            COUNT(p.id) AS prayer_count,
                            u.profile_image
                        FROM prayer_requests pr
                        LEFT JOIN prayers p ON p.prayer_request_id = pr.id
                        LEFT JOIN users u ON u.id = pr.requester_id
                        """;

        if (category != null && !category.isEmpty()) {
            whereClauses.add("pr.category = ?");
            params.add(category);
        }

        if (isAnswered != null) {
            whereClauses.add("pr.is_answered = ?");
            params.add(isAnswered);
        }

        if (!whereClauses.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", whereClauses);
        }

        sql += """
                 GROUP BY pr.id, pr.requester_id, pr.name, pr.content, pr.category, pr.is_visible, pr.is_answered, pr.answer_content, pr.created_at, u.profile_image
                 ORDER BY pr.created_at DESC
               """;

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, params.toArray());
            while(result.next()){
                requests.add(mapRowToPrayerRequestDto(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot Connect To Database", e);
        }

        return requests;
    }

    @Override
    public List<PrayerRequestDto> getAllPrayerRequestByRequesterId(int requesterId) {
        List<PrayerRequestDto> requests = new ArrayList<>();

        String sql = """
                        SELECT
                            pr.id,
                            pr.requester_id,
                            pr.name,
                            pr.content,
                            pr.category,
                            pr.is_visible,
                            pr.is_answered,
                            pr.answer_content,
                            pr.created_at,
                            COUNT(p.id) AS prayer_count
                        FROM prayer_requests pr
                        LEFT JOIN prayers p ON p.prayer_request_id = pr.id
                        WHERE pr.requester_id = ?
                        GROUP BY pr.id, pr.requester_id, pr.name, pr.content, pr.category, pr.is_visible, pr.is_answered, pr.answer_content, pr.created_at
                        ORDER BY pr.created_at DESC
                        """;

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, requesterId);
            while(result.next()){
                requests.add(mapRowToPrayerRequestDto(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot Connect To Database", e);
        }

        return requests;
    }

    @Override
    public PrayerRequest getPrayerRequestById(int id) {
        PrayerRequest prayerRequest = null;

        String sql = "SELECT * FROM prayer_requests WHERE id = ?;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()){
                prayerRequest = mapRowToPrayerRequest(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot Connect To Database", e);
        }
        return prayerRequest;
    }

    @Override
    public PrayerRequest createPrayerRequest(PrayerRequest newPrayerRequest) {
        PrayerRequest prayerRequest = null;
        int newId;

        String sql = "INSERT INTO prayer_requests (requester_id, name, content, category, is_anonymous) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id;";

        try{
            newId = jdbcTemplate.queryForObject(sql, int.class, 
                newPrayerRequest.getRequesterId(), 
                newPrayerRequest.getName(), 
                newPrayerRequest.getContent(), 
                newPrayerRequest.getCategory(),
                newPrayerRequest.getAnonymous());
            prayerRequest = getPrayerRequestById(newId);
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot Connect To Database", e);
        }
        return prayerRequest;
    }

    @Override
    public PrayerRequest updatePrayerRequest(PrayerRequest prayerRequest) {
        String sql = "UPDATE prayer_requests SET name = ?, content = ?, category = ?, is_visible = ?, is_answered = ?, answer_content = ?, is_anonymous = ? WHERE id = ?;";
        try {
            jdbcTemplate.update(sql, 
                prayerRequest.getName(), 
                prayerRequest.getContent(), 
                prayerRequest.getCategory(), 
                prayerRequest.getVisible(), 
                prayerRequest.getAnswered(), 
                prayerRequest.getAnswerContent(),
                prayerRequest.getAnonymous(),
                prayerRequest.getId());
            return getPrayerRequestById(prayerRequest.getId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot Connect To Database", e);
        }
    }

    @Override
    public int deletePrayerRequest(int id) {
        String sqlPrayer = "DELETE FROM prayers WHERE prayer_request_id = ?;";
        String sql = "DELETE FROM prayer_requests WHERE id = ?;";

        try{
            jdbcTemplate.update(sqlPrayer, id);
            return jdbcTemplate.update(sql, id);
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private PrayerRequest mapRowToPrayerRequest(SqlRowSet rowSet){
        PrayerRequest prayerRequest = new PrayerRequest();
        prayerRequest.setId(rowSet.getInt("id"));
        int requestId = rowSet.getInt("requester_id");
        prayerRequest.setRequesterId(rowSet.wasNull()? null: requestId);
        prayerRequest.setName(rowSet.getString("name"));
        prayerRequest.setContent(rowSet.getString("content"));
        if (rowSet.getTimestamp("created_at") != null) {
            prayerRequest.setCreatedAt(rowSet.getTimestamp("created_at").toLocalDateTime());
        }
        prayerRequest.setVisible(rowSet.getBoolean("is_visible"));
        prayerRequest.setCategory(rowSet.getString("category"));
        prayerRequest.setAnswered(rowSet.getBoolean("is_answered"));
        prayerRequest.setAnswerContent(rowSet.getString("answer_content"));
        return prayerRequest;
    }

    private PrayerRequestDto mapRowToPrayerRequestDto (SqlRowSet rowSet){
        PrayerRequestDto prayerRequestDto = new PrayerRequestDto();
        prayerRequestDto.setPrayerCount(rowSet.getInt("prayer_count"));
        prayerRequestDto.setPrayerRequest(mapRowToPrayerRequest(rowSet));
        // Get profile image if available (from JOIN with users table)
        try {
            prayerRequestDto.setUserProfileImage(rowSet.getString("profile_image"));
        } catch (Exception e) {
            // column might not exist in some queries
        }
        return prayerRequestDto;
    }
}

