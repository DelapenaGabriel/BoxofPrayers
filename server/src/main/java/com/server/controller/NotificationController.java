package com.server.controller;

import com.server.dao.NotificationDao;
import com.server.dao.UserDao;
import com.server.model.Notification;
import com.server.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationDao notificationDao;
    private final UserDao userDao;

    public NotificationController(NotificationDao notificationDao, UserDao userDao) {
        this.notificationDao = notificationDao;
        this.userDao = userDao;
    }

    @GetMapping("")
    public List<Notification> getMyNotifications(Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        int userId = userDao.getUserByEmail(principal.getName()).getId();
        return notificationDao.getNotificationsByUserId(userId);
    }

    @GetMapping("/unread-count")
    public int getUnreadCount(Principal principal) {
        if (principal == null) return 0;
        int userId = userDao.getUserByEmail(principal.getName()).getId();
        return notificationDao.getUnreadCount(userId);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable int id, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        // Ideally check ownership
        notificationDao.markAsRead(id);
    }
}
