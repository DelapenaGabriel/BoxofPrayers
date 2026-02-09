package com.server.dao;

import com.server.model.PostComment;
import java.util.List;

public interface PostCommentDao {
    List<PostComment> getCommentsByPostId(int postId);
    PostComment createComment(PostComment comment);
    PostComment getCommentById(int commentId);
    void deleteComment(int commentId, int userId);
}
