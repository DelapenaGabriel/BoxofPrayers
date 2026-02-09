package com.server.model;

import java.time.LocalDateTime;

public class PrayerRequest {

    private Integer id;
    private Integer requesterId;
    @jakarta.validation.constraints.NotEmpty
    @jakarta.validation.constraints.Size(max=50)
    private String name;
    @jakarta.validation.constraints.NotEmpty
    private String content;
    private LocalDateTime createdAt;
    private Boolean isVisible;
    private Boolean isAnonymous;


    private String category;
    private Boolean isAnswered;
    private String answerContent;


    public PrayerRequest() {
    }

    public PrayerRequest(Integer id, Integer requesterId, String name, String content, LocalDateTime createdAt, Boolean isVisible, Boolean isAnonymous, String category, Boolean isAnswered, String answerContent) {
        this.id = id;
        this.requesterId = requesterId;
        this.name = name;
        this.content = content;
        this.createdAt = createdAt;
        this.isVisible = isVisible;
        this.isAnonymous = isAnonymous;
        this.category = category;
        this.isAnswered = isAnswered;
        this.answerContent = answerContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
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

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAnswered() {
        return isAnswered;
    }

    public void setAnswered(Boolean answered) {
        isAnswered = answered;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }
}

