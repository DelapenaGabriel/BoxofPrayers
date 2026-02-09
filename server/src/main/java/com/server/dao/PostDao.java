package com.server.dao;

import com.server.model.Post;
import java.util.List;

public interface PostDao {
    List<Post> getAllPosts(int userId, int limit, int offset);
    Post getPostById(int postId, int userId);
    Post createPost(Post post);
    Post updatePost(Post post, int userId);
    void deletePost(int postId, int userId);
}
