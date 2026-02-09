package com.server.dao;

import com.server.model.Comment;
import java.util.List;

public interface CommentDao {
    List<Comment> getCommentsByRequestId(int requestId);
    Comment createComment(Comment comment);
    List<Comment> getAllComments();
    int deleteComment(int commentId);
    Comment updateComment(int commentId, String content);
    Comment getCommentById(int commentId);
}
