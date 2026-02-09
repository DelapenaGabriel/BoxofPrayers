package com.server.service;

import com.server.model.PrayerRequest;
import com.server.model.PrayerRequestDto;

import java.security.Principal;
import java.util.List;

public interface PrayerRequestService {
    List<PrayerRequestDto> getAllPrayerRequestDto(Principal principal, String category, Boolean isAnswered);
    PrayerRequest getPrayerRequestById(int id);
    PrayerRequest createPrayerRequest(PrayerRequest newPrayerRequest, Principal principal);
    PrayerRequest updatePrayerRequest(PrayerRequest prayerRequest, Principal principal);
    int deletePrayerRequest(int id, Principal principal);
}
