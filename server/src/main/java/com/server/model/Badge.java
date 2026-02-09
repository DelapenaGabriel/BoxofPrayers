package com.server.model;

import java.time.LocalDateTime;

public class Badge {
    private int id;
    private String name;
    private String description;
    private String iconUrl;
    private String criteria;
    private LocalDateTime awardedAt; // Null if just defining badge, populated if user_badge

    public Badge() {}

    public Badge(int id, String name, String description, String iconUrl, String criteria) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
        this.criteria = criteria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public String getCriteria() { return criteria; }
    public void setCriteria(String criteria) { this.criteria = criteria; }
    public LocalDateTime getAwardedAt() { return awardedAt; }
    public void setAwardedAt(LocalDateTime awardedAt) { this.awardedAt = awardedAt; }
}
