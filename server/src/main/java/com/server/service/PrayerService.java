package com.server.service;

import com.server.model.Prayer;

import java.security.Principal;
import java.util.List;

public interface PrayerService {
    List<Prayer> getAllPrayers(Principal principal);
    List<Prayer> getAllPublicPrayers();
    List<Prayer> getAllPrayersByUserId(int userId);
    List<Prayer> getAllPrayersByPrayerRequestId(int prayerRequestId);
    Prayer getPrayerById(int id);
    Prayer createPrayer(Prayer newPrayer, Principal principal);
    int deletePrayer(int id, Principal principal);
}
