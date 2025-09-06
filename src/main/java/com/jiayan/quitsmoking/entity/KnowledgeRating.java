package com.jiayan.quitsmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户评分实体类
 */
@Entity
@Table(name = "knowledge_ratings")
@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "article_id", nullable = false)
    private Long articleId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
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
    
    // ========== 业务方法 ==========
    
    /**
     * 检查评分是否有效
     */
    public boolean isValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
    
    /**
     * 获取评分描述
     */
    public String getRatingDescription() {
        if (rating == null) {
            return "未评分";
        }
        
        switch (rating) {
            case 1: return "很差";
            case 2: return "较差";
            case 3: return "一般";
            case 4: return "较好";
            case 5: return "很好";
            default: return "无效评分";
        }
    }
    
    /**
     * 获取评分星级显示
     */
    public String getRatingStars() {
        if (rating == null) {
            return "";
        }
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }
} 