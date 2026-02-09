package com.server.dao;

import com.server.model.Prayer;

import java.util.List;

public interface PrayerDao {
    List<Prayer> getAllPrayers();
    List<Prayer> getAllPrayersByUserId(int userId);
    List<Prayer> getAllPrayersByPrayerRequestId(int prayerRequestId);
    Prayer getPrayerById(int id);
    Prayer createPrayer(Prayer newPrayer);
    int deletePrayer(int id);
}
