package com.server.model;

public class LeaderboardDto {
    private int id;
    private String name;
    private int prayerCount;
    private String avatar;

    public LeaderboardDto() {}

    public LeaderboardDto(int id, String name, int prayerCount, String avatar) {
        this.id = id;
        this.name = name;
        this.prayerCount = prayerCount;
        this.avatar = avatar;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrayerCount() { return prayerCount; }
    public void setPrayerCount(int prayerCount) { this.prayerCount = prayerCount; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
