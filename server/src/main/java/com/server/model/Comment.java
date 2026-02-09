package com.server.model;

import java.time.LocalDateTime;

public class Comment {
    private Integer id;
    private Integer prayerRequestId;
    private Integer userId;
    private String userName; // Transient, for display
    private String userProfileImage; // Transient, for display
    private String content;
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(Integer id, Integer prayerRequestId, Integer userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.prayerRequestId = prayerRequestId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrayerRequestId() {
        return prayerRequestId;
    }

    public void setPrayerRequestId(Integer prayerRequestId) {
        this.prayerRequestId = prayerRequestId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
