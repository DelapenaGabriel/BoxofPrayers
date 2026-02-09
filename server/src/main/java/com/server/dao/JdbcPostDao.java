package com.server.dao;

import com.server.exception.DaoException;
import com.server.model.Post;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPostDao implements PostDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Post> getAllPosts(int currentUserId, int limit, int offset) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.display_name, u.profile_image, " +
                     "(SELECT COUNT(*) FROM post_reactions pr WHERE pr.post_id = p.id AND pr.type='AMEN') as amen_count, " +
                     "(SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = p.id) as comment_count, " +
                     "(SELECT COUNT(*) > 0 FROM post_reactions pr WHERE pr.post_id = p.id AND pr.user_id = ? AND pr.type='AMEN') as is_liked, " +
                     "op.id as original_id, op.user_id as original_user_id, op.content as original_content, " +
                     "op.created_at as original_created_at, ou.display_name as original_user_display_name, " +
                     "ou.profile_image as original_user_profile_image " +
                     "FROM posts p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN posts op ON p.original_post_id = op.id " +
                     "LEFT JOIN users ou ON op.user_id = ou.id " +
                     "ORDER BY p.created_at DESC " +
                     "LIMIT ? OFFSET ?";
        
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUserId, limit, offset);
            while (results.next()) {
                posts.add(mapRowToPost(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return posts;
    }

    @Override
    public Post getPostById(int postId, int currentUserId) {
        Post post = null;
        String sql = "SELECT p.*, u.display_name, u.profile_image, " +
                     "(SELECT COUNT(*) FROM post_reactions pr WHERE pr.post_id = p.id AND pr.type='AMEN') as amen_count, " +
                     "(SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = p.id) as comment_count, " +
                     "(SELECT COUNT(*) > 0 FROM post_reactions pr WHERE pr.post_id = p.id AND pr.user_id = ? AND pr.type='AMEN') as is_liked, " +
                     "op.id as original_id, op.user_id as original_user_id, op.content as original_content, " +
                     "op.created_at as original_created_at, ou.display_name as original_user_display_name, " +
                     "ou.profile_image as original_user_profile_image " +
                     "FROM posts p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN posts op ON p.original_post_id = op.id " +
                     "LEFT JOIN users ou ON op.user_id = ou.id " +
                     "WHERE p.id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUserId, postId);
            if (results.next()) {
                post = mapRowToPost(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return post;
    }

    @Override
    public Post createPost(Post post) {
        String sql = "INSERT INTO posts (user_id, content, image_url, video_url, link_url, link_title, link_preview_img, original_post_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            Integer newId = jdbcTemplate.queryForObject(sql, Integer.class,
                    post.getUserId(), post.getContent(), post.getImageUrl(),
                    post.getVideoUrl(), post.getLinkUrl(), post.getLinkTitle(), post.getLinkPreviewImg(),
                    post.getOriginalPostId());
            
            return getPostById(newId, post.getUserId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public Post updatePost(Post post, int userId) {
        String sql = "UPDATE posts SET content = ?, image_url = ?, video_url = ?, " +
                     "link_url = ?, link_title = ?, link_preview_img = ? " +
                     "WHERE id = ? AND user_id = ?";
        try {
            int rows = jdbcTemplate.update(sql,
                    post.getContent(), post.getImageUrl(), post.getVideoUrl(),
                    post.getLinkUrl(), post.getLinkTitle(), post.getLinkPreviewImg(),
                    post.getId(), userId);
            
            if (rows == 0) {
                throw new DaoException("Post not found or user not authorized to update");
            }
            
            return getPostById(post.getId(), userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void deletePost(int postId, int userId) {
        String sql = "DELETE FROM posts WHERE id = ? AND user_id = ?";
        try {
             int rows = jdbcTemplate.update(sql, postId, userId);
             if (rows == 0) {
                 // Check if post exists to determine if it was not found or just not owned by user
                 // For now, we'll assume it's acceptable to just return or throw specific exception if needed
             }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    private Post mapRowToPost(SqlRowSet rs) {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setImageUrl(rs.getString("image_url"));
        post.setVideoUrl(rs.getString("video_url"));
        post.setLinkUrl(rs.getString("link_url"));
        post.setLinkTitle(rs.getString("link_title"));
        post.setLinkPreviewImg(rs.getString("link_preview_img"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        
        post.setUserDisplayName(rs.getString("display_name"));
        post.setUserProfileImage(rs.getString("profile_image"));
        
        post.setAmenCount(rs.getInt("amen_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setLikedByCurrentUser(rs.getBoolean("is_liked"));
        
        // Map original post if this is a repost
        if (rs.getObject("original_id") != null) {
            Post originalPost = new Post();
            originalPost.setId(rs.getInt("original_id"));
            originalPost.setUserId(rs.getInt("original_user_id"));
            originalPost.setContent(rs.getString("original_content"));
            originalPost.setCreatedAt(rs.getTimestamp("original_created_at"));
            originalPost.setUserDisplayName(rs.getString("original_user_display_name"));
            originalPost.setUserProfileImage(rs.getString("original_user_profile_image"));
            
            post.setOriginalPost(originalPost);
            post.setOriginalPostId(originalPost.getId());
        }
        
        return post;
    }
}
