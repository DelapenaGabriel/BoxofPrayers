package com.server.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.relational.core.sql.In;

import java.time.LocalDateTime;

public class Prayer {
    private Integer id;
    @NotNull
    private Integer prayerRequestId;
    private Integer userId;
    private LocalDateTime prayedAt;

    public Prayer() {
    }

    public Prayer(Integer id, Integer prayerRequestId, Integer userId, LocalDateTime prayedAt) {
        this.id = id;
        this.prayerRequestId = prayerRequestId;
        this.userId = userId;
        this.prayedAt = prayedAt;
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

    public LocalDateTime getPrayedAt() {
        return prayedAt;
    }

    public void setPrayedAt(LocalDateTime prayedAt) {
        this.prayedAt = prayedAt;
    }
}


