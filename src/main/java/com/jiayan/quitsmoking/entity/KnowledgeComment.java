package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 */
@Entity
@Table(name = "knowledge_comments")
@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "article_id", nullable = false)
    private Long articleId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "is_helpful")
    private Boolean isHelpful = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CommentStatus status = CommentStatus.ACTIVE;
    
    @Column(name = "hidden_reason", length = 200)
    private String hiddenReason;
    
    @Column(name = "hidden_by")
    private Long hiddenBy;
    
    @Column(name = "hidden_at")
    private LocalDateTime hiddenAt;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_by")
    private Long deletedBy;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ========== 关联关系 ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private KnowledgeArticle article;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private KnowledgeComment parent;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<KnowledgeComment> replies;
    
    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("sortOrder ASC")
    private List<CommentImage> images;
    
    // ========== 业务方法 ==========
    
    /**
     * 检查评论是否可见
     */
    public boolean isVisible() {
        return !isDeleted && status == CommentStatus.ACTIVE;
    }
    
    /**
     * 检查是否为回复评论
     */
    public boolean isReply() {
        return parentId != null;
    }
    
    /**
     * 检查是否为顶级评论
     */
    public boolean isTopLevel() {
        return parentId == null;
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
     * 获取评论摘要
     */
    public String getContentSummary() {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }
    
    /**
     * 检查是否有图片
     */
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
} 