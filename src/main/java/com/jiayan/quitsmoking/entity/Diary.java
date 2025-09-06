package com.jiayan.quitsmoking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "smoking_diary")
public class Diary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
    
    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "mood_score")
    private Integer moodScore;
    
    @Column(name = "tags")
    private String tags;
    
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;
    
    @Column(name = "word_count")
    private Integer wordCount = 0;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status = "草稿";
    
    @Column(name = "is_forwarded")
    private Boolean isForwarded = false;
    
    @Column(name = "forum_post_id")
    private Long forumPostId;
    
    @Column(name = "forum_section", length = 100)
    private String forumSection;
    
    @Column(name = "forward_time")
    private LocalDateTime forwardTime;
    
    @Column(name = "forum_views")
    private Integer forumViews = 0;
    
    @Column(name = "forum_likes")
    private Integer forumLikes = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "created_ip", length = 45)
    private String createdIp;
    
    @Column(name = "sync_status")
    private Integer syncStatus = 0;
    
    // 构造函数
    public Diary() {
        LocalDateTime now = LocalDateTime.now();
        this.recordDate = now.toLocalDate();
        this.recordTime = now;
        this.createdAt = now;
        this.updatedAt = now;
    }
    
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
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }
    
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
    
    public String getCreatedIp() { return createdIp; }
    public void setCreatedIp(String createdIp) { this.createdIp = createdIp; }
    
    public Integer getSyncStatus() { return syncStatus; }
    public void setSyncStatus(Integer syncStatus) { this.syncStatus = syncStatus; }
} 