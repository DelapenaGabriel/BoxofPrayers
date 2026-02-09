package com.server.dao;

import com.server.model.PrayerActivity;
import com.server.model.PrayerRequest;
import com.server.model.PrayerRequestDto;

import java.util.List;

public interface PrayerRequestDao {
    List<PrayerRequestDto> getAllPrayerRequest(String category, Boolean isAnswered);
    List<PrayerRequestDto> getAllPrayerRequestByRequesterId(int requesterId);
    PrayerRequest getPrayerRequestById(int id);
    PrayerRequest createPrayerRequest(PrayerRequest newPrayerRequest);
    PrayerRequest updatePrayerRequest(PrayerRequest prayerRequest);
    int deletePrayerRequest(int id);
}
