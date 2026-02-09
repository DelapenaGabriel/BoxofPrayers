package com.server.dao;

import com.server.model.Badge;
import java.util.List;

public interface BadgeDao {
    List<Badge> getBadgesByUserId(int userId);
    void awardBadge(int userId, String criteria);
    boolean hasBadge(int userId, String criteria);
}
