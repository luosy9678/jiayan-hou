package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.ArticleStatus;
import com.jiayan.quitsmoking.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识文章实体类
 */
@Entity
@Table(name = "knowledge_articles")
@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeArticle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", length = 200, nullable = false)
    private String title;
    
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @Column(name = "source", length = 500)
    private String source;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;
    
    @Column(name = "rating_score", precision = 3, scale = 2)
    private BigDecimal ratingScore = BigDecimal.ZERO;
    
    @Column(name = "rating_count")
    private Integer ratingCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ArticleStatus status = ArticleStatus.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", length = 20)
    private AuditStatus auditStatus = AuditStatus.PENDING;
    
    @Column(name = "audit_comment", columnDefinition = "TEXT")
    private String auditComment;
    
    @Column(name = "audited_by")
    private Long auditedBy;
    
    @Column(name = "audited_at")
    private LocalDateTime auditedAt;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_by")
    private Long deletedBy;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "banned_reason", length = 500)
    private String bannedReason;
    
    @Column(name = "banned_by")
    private Long bannedBy;
    
    @Column(name = "banned_at")
    private LocalDateTime bannedAt;
    
    @Column(name = "publish_time")
    private LocalDateTime publishTime;
    
    @Column(name = "last_edit_time")
    private LocalDateTime lastEditTime;
    
    @Column(name = "last_edit_by")
    private Long lastEditBy;
    
    @Column(name = "edit_count")
    private Integer editCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ========== 关联关系 ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private KnowledgeCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private User author;
    
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("contentOrder ASC")
    private List<KnowledgeContentBlock> contentBlocks;
    
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<KnowledgeComment> comments;
    
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<KnowledgeRating> ratings;
    
    // ========== 业务方法 ==========
    
    /**
     * 检查文章是否可见
     */
    public boolean isVisible() {
        return !isDeleted && status == ArticleStatus.PUBLISHED;
    }
    
    /**
     * 检查文章是否需要审核
     */
    public boolean needsAudit() {
        return auditStatus == AuditStatus.PENDING;
    }
    
    /**
     * 检查文章是否已发布
     */
    public boolean isPublished() {
        return status == ArticleStatus.PUBLISHED && auditStatus == AuditStatus.APPROVED;
    }
    
    /**
     * 检查文章是否被禁用
     */
    public boolean isBanned() {
        return status == ArticleStatus.BANNED;
    }
    
    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
    
    /**
     * 增加点赞数
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }
    
    /**
     * 减少点赞数
     */
    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    /**
     * 计算平均评分
     */
    public void calculateRatingScore() {
        if (ratingCount != null && ratingCount > 0 && ratingScore != null) {
            // 这里可以添加更复杂的评分计算逻辑
            // 目前使用简单的平均值
        }
    }
    
    /**
     * 获取文章摘要
     */
    public String getSummary() {
        if (content == null || content.length() <= 200) {
            return content;
        }
        return content.substring(0, 200) + "...";
    }
} 