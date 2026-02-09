package com.server.model;

import java.sql.Timestamp;

public class Post {
    private Integer id;
    private int userId;
    private String userName;
    private String userDisplayName;
    private String userProfileImage;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private String linkUrl;
    private String linkTitle;
    private String linkPreviewImg;
    private Timestamp createdAt;
    private Integer amenCount;
    private Integer commentCount;
    private boolean isLikedByCurrentUser;
    private Integer originalPostId;
    private Post originalPost;

    // Constructors
    public Post() {}

    public Post(int id, int userId, String content, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Post( int userId, String content, Timestamp createdAt) {
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserDisplayName() { return userDisplayName; }
    public void setUserDisplayName(String userDisplayName) { this.userDisplayName = userDisplayName; }

    public String getUserProfileImage() { return userProfileImage; }
    public void setUserProfileImage(String userProfileImage) { this.userProfileImage = userProfileImage; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public String getLinkTitle() { return linkTitle; }
    public void setLinkTitle(String linkTitle) { this.linkTitle = linkTitle; }

    public String getLinkPreviewImg() { return linkPreviewImg; }
    public void setLinkPreviewImg(String linkPreviewImg) { this.linkPreviewImg = linkPreviewImg; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Integer getAmenCount() { return amenCount; }
    public void setAmenCount(Integer amenCount) { this.amenCount = amenCount; }
    
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { isLikedByCurrentUser = likedByCurrentUser; }

    public Integer getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Integer originalPostId) { this.originalPostId = originalPostId; }

    public Post getOriginalPost() { return originalPost; }
    public void setOriginalPost(Post originalPost) { this.originalPost = originalPost; }
}
