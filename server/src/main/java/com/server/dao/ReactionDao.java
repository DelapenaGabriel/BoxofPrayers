package com.server.dao;

public interface ReactionDao {
    void addReaction(int postId, int userId, String type);
    void removeReaction(int postId, int userId, String type);
}
