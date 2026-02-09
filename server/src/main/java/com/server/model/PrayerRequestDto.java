package com.server.model;

public class PrayerRequestDto {
    private int prayerCount;
    private PrayerRequest prayerRequest;
    private String userProfileImage;

    public PrayerRequestDto(int prayerCount, PrayerRequest prayerRequest) {
        this.prayerCount = prayerCount;
        this.prayerRequest = prayerRequest;
    }

    public PrayerRequestDto() {
    }

    public int getPrayerCount() {
        return prayerCount;
    }

    public void setPrayerCount(int prayerCount) {
        this.prayerCount = prayerCount;
    }

    public PrayerRequest getPrayerRequest() {
        return prayerRequest;
    }

    public void setPrayerRequest(PrayerRequest prayerRequest) {
        this.prayerRequest = prayerRequest;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}
