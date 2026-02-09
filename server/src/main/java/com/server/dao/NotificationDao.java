package com.server.dao;

import com.server.model.Notification;
import java.util.List;

public interface NotificationDao {
    List<Notification> getNotificationsByUserId(int userId);
    void createNotification(Notification notification);
    void markAsRead(int notificationId);
    int getUnreadCount(int userId);
}
