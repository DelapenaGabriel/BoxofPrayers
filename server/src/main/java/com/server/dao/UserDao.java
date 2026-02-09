package com.server.dao;



import com.server.model.LeaderboardDto;
import com.server.model.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(int id);

    User getUserByEmail(String email);

    User createUser(User user);
    
    void updateProfileImage(String email, String imageUrl);

    void updateUserProfile(int userId, String name, String displayName, String profileImage);

    List<LeaderboardDto> getTopPrayerWarriors(String timeFrame, int limit);
}
