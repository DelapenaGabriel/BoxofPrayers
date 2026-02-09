package com.server.controller;

import com.server.dao.UserDao;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @PutMapping("/profile-image")
    public void updateProfileImage(@RequestBody Map<String, String> body, Principal principal) {
        String imageUrl = body.get("imageUrl");
        if (imageUrl == null) return;
        userDao.updateProfileImage(principal.getName(), imageUrl);
    }

    @PutMapping("/users/profile")
    public void updateUserProfile(@RequestBody Map<String, String> body, Principal principal) {
        String name = body.get("name");
        String displayName = body.get("displayName");

        String profileImage = body.get("profileImage");

        // Input Sanitization (Chaos Audit Fix)
        if (name != null && name.length() > 50) throw new IllegalArgumentException("Name too long");
        if (displayName != null && displayName.length() > 50) throw new IllegalArgumentException("Spiritual Name too long");

        com.server.model.User user = userDao.getUserByEmail(principal.getName());
        if (user != null) {
            userDao.updateUserProfile(user.getId(), name, displayName, profileImage);
        }
    }

    @GetMapping("/users/leaderboard")
    public java.util.List<com.server.model.LeaderboardDto> getLeaderboard(@RequestParam(defaultValue = "all") String timeFrame) {
        // Default limit to 10 for now
        return userDao.getTopPrayerWarriors(timeFrame, 10);
    }
}
