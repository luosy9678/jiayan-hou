package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 日记详情响应DTO
 */
public class DiaryDetailResponse {
    
    private Long id;
    private Long userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;
    
    private String content;
    private Integer moodScore;
    private List<String> tags;
    private List<DiaryImageInfo> images;
    private Integer wordCount;
    private String status;
    private Boolean isForwarded;
    private Long forumPostId;
    private String forumSection;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime forwardTime;
    
    private Integer forumViews;
    private Integer forumLikes;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public DiaryDetailResponse() {}
    
    // Getter 和 Setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    
    public LocalDateTime getRecordTime() { return recordTime; }
    public void setRecordTime(LocalDateTime recordTime) { this.recordTime = recordTime; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Integer getMoodScore() { return moodScore; }
    public void setMoodScore(Integer moodScore) { this.moodScore = moodScore; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<DiaryImageInfo> getImages() { return images; }
    public void setImages(List<DiaryImageInfo> images) { this.images = images; }
    
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getIsForwarded() { return isForwarded; }
    public void setIsForwarded(Boolean isForwarded) { this.isForwarded = isForwarded; }
    
    public Long getForumPostId() { return forumPostId; }
    public void setForumPostId(Long forumPostId) { this.forumPostId = forumPostId; }
    
    public String getForumSection() { return forumSection; }
    public void setForumSection(String forumSection) { this.forumSection = forumSection; }
    
    public LocalDateTime getForwardTime() { return forwardTime; }
    public void setForwardTime(LocalDateTime forwardTime) { this.forwardTime = forwardTime; }
    
    public Integer getForumViews() { return forumViews; }
    public void setForumViews(Integer forumViews) { this.forumViews = forumViews; }
    
    public Integer getForumLikes() { return forumLikes; }
    public void setForumLikes(Integer forumLikes) { this.forumLikes = forumLikes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * 日记图片信息内部类
     */
    public static class DiaryImageInfo {
        private String fileName;
        private String originalName;
        private String fileUrl;
        private Long fileSize;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime uploadTime;
        
        // 构造函数
        public DiaryImageInfo() {}
        
        // Getter 和 Setter 方法
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public LocalDateTime getUploadTime() { return uploadTime; }
        public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
    }
} 