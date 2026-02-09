package com.server.controller;

import com.server.dao.BadgeDao;
import com.server.dao.UserDao;
import com.server.model.Badge;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeDao badgeDao;
    private final UserDao userDao;

    public BadgeController(BadgeDao badgeDao, UserDao userDao) {
        this.badgeDao = badgeDao;
        this.userDao = userDao;
    }

    @GetMapping("/my")
    public List<Badge> getMyBadges(Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        int userId = userDao.getUserByEmail(principal.getName()).getId();
        return badgeDao.getBadgesByUserId(userId);
    }

    @GetMapping("/{userId}")
    public List<Badge> getUserBadges(@PathVariable int userId) {
        return badgeDao.getBadgesByUserId(userId);
    }
}
