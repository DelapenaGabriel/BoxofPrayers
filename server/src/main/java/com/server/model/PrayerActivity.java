package com.server.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrayerActivity {
    private LocalDate day;
    private int prayerCount;

    public PrayerActivity() {
    }

    public PrayerActivity(LocalDate day, int prayerCount) {
        this.day = day;
        this.prayerCount = prayerCount;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public int getPrayerCount() {
        return prayerCount;
    }

    public void setPrayerCount(int prayerCount) {
        this.prayerCount = prayerCount;
    }
}
