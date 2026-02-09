package com.server.service;

import com.server.dao.BadgeDao;
import com.server.dao.NotificationDao;
import com.server.dao.PrayerDao;
import com.server.dao.PrayerRequestDao;
import com.server.dao.UserDao;
import com.server.model.Notification;
import com.server.model.Prayer;
import com.server.model.PrayerRequest;
import com.server.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class RestPrayerService implements PrayerService{

    private final PrayerDao prayerDao;
    private final UserDao userDao;
    private final NotificationDao notificationDao;
    private final BadgeDao badgeDao;
    private final PrayerRequestDao prayerRequestDao;

    public RestPrayerService(PrayerDao prayerDao, UserDao userDao, NotificationDao notificationDao, BadgeDao badgeDao, PrayerRequestDao prayerRequestDao){
        this.prayerDao = prayerDao;
        this.userDao = userDao;
        this.notificationDao = notificationDao;
        this.badgeDao = badgeDao;
        this.prayerRequestDao = prayerRequestDao;
    }

    private User getUser(Principal principal){
        return userDao.getUserByEmail(principal.getName());
    }


    @Override
    public List<Prayer> getAllPrayers(Principal principal) {
        User user;

        if (principal != null){
            user = getUser(principal);
            if (!isAdmin(user)){
                return prayerDao.getAllPrayersByUserId(user.getId());
            }
        }
        return prayerDao.getAllPrayers();

    }

    @Override
    public List<Prayer> getAllPublicPrayers() {
        return prayerDao.getAllPrayers();
    }

    @Override
    public List<Prayer> getAllPrayersByUserId(int userId) {
        return prayerDao.getAllPrayersByUserId(userId);
    }

    @Override
    public List<Prayer> getAllPrayersByPrayerRequestId(int prayerRequestId) {
        return prayerDao.getAllPrayersByPrayerRequestId(prayerRequestId);
    }

    @Override
    public Prayer getPrayerById(int id) {
        return prayerDao.getPrayerById(id);
    }

    @Override
    public Prayer createPrayer(Prayer newPrayer, Principal principal) {
        User user = null;
        if (principal != null){
            user = getUser(principal);
            newPrayer.setUserId(user.getId());
        }
        
        if (newPrayer.getPrayedAt() == null) {
            newPrayer.setPrayedAt(java.time.LocalDateTime.now());
        }

        Prayer created = prayerDao.createPrayer(newPrayer);

        if (user != null) {
            // Badges
            List<Prayer> userPrayers = prayerDao.getAllPrayersByUserId(user.getId());
            int count = userPrayers.size();

            // Volume Milestones
            if (count == 1) badgeDao.awardBadge(user.getId(), "1_prayer");
            if (count == 10) badgeDao.awardBadge(user.getId(), "10_prayers");
            if (count == 25) badgeDao.awardBadge(user.getId(), "25_prayers");
            if (count == 50) badgeDao.awardBadge(user.getId(), "50_prayers");
            if (count == 100) badgeDao.awardBadge(user.getId(), "100_prayers");
            if (count == 250) badgeDao.awardBadge(user.getId(), "250_prayers");
            if (count == 500) badgeDao.awardBadge(user.getId(), "500_prayers");
            if (count == 1000) badgeDao.awardBadge(user.getId(), "1000_prayers");
            if (count == 5000) badgeDao.awardBadge(user.getId(), "5000_prayers");

            // Time & Weekend Logic
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            int hour = now.getHour();
            // Morning Light: 5am - 8am
            if (hour >= 5 && hour < 9) badgeDao.awardBadge(user.getId(), "morning_prayer");
            // Night Watch: 11pm - 3am
            if (hour >= 23 || hour < 3) badgeDao.awardBadge(user.getId(), "night_prayer");

            // Weekend
            java.time.DayOfWeek day = now.getDayOfWeek();
            if (day == java.time.DayOfWeek.SATURDAY || day == java.time.DayOfWeek.SUNDAY) {
                 badgeDao.awardBadge(user.getId(), "weekend_prayer");
            }

            // Streak Logic
            int streak = calculateStreak(userPrayers);
            if (streak >= 3) badgeDao.awardBadge(user.getId(), "3_day_streak");
            if (streak >= 7) badgeDao.awardBadge(user.getId(), "7_day_streak");
            if (streak >= 14) badgeDao.awardBadge(user.getId(), "14_day_streak");
            if (streak >= 30) badgeDao.awardBadge(user.getId(), "30_day_streak");
            if (streak >= 90) badgeDao.awardBadge(user.getId(), "90_day_streak");
            if (streak >= 365) badgeDao.awardBadge(user.getId(), "365_day_streak");

            // Notification
            PrayerRequest prayerRequest = prayerRequestDao.getPrayerRequestById(newPrayer.getPrayerRequestId());
            if (prayerRequest != null && prayerRequest.getRequesterId() != null && !prayerRequest.getRequesterId().equals(user.getId())) {
                String message = user.getDisplayName() + " prayed for your request: " + prayerRequest.getName();
                Notification notification = new Notification();
                notification.setUserId(prayerRequest.getRequesterId());
                notification.setMessage(message);
                notification.setSenderId(user.getId()); // Set the sender ID
                
                notificationDao.createNotification(notification);
            }
        }

        return created;
    }

    @Override
    public int deletePrayer(int id, Principal principal) {
        Prayer prayer = getPrayerById(id);

        User user = getUser(principal);
        if (!isAdmin(user) && prayer.getUserId() != user.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this prayer");
        }
        return prayerDao.deletePrayer(id);
    }

    private boolean isAdmin(User user){
        return user.getRole().equals("ROLE_ADMIN");
    }

    private int calculateStreak(List<Prayer> prayers) {
        if (prayers == null || prayers.isEmpty()) return 0;

        // Extract and sort unique dates (LocalDate)
        java.util.Set<java.time.LocalDate> dates = new java.util.HashSet<>();
        for (Prayer p : prayers) {
            if (p.getPrayedAt() != null) {
                dates.add(p.getPrayedAt().toLocalDate());
            }
        }

        List<java.time.LocalDate> sortedDates = dates.stream()
                .sorted(java.util.Comparator.reverseOrder())
                .collect(java.util.stream.Collectors.toList());

        if (sortedDates.isEmpty()) return 0;

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate yesterday = today.minusDays(1);
        java.time.LocalDate latest = sortedDates.get(0);

        // If latest prayer is older than yesterday, streak is broken
        if (latest.isBefore(yesterday)) {
            return 0;
        }

        int streak = 0;
        // If the latest is today, start counting from 0. If yesterday, also start from 0.
        // We iterate and expect difference of 1 day.

        java.time.LocalDate previous = null;

        for (java.time.LocalDate current : sortedDates) {
            if (previous == null) {
                // First element
                streak = 1;
                previous = current;
                continue;
            }

            if (current.equals(previous.minusDays(1))) {
                streak++;
                previous = current;
            } else {
                break;
            }
        }
        return streak;
    }
}
