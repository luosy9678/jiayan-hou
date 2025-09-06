package com.jiayan.quitsmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

/**
 * 评论图片实体类
 */
@Entity
@Table(name = "comment_images")
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "comment_id", nullable = false)
    private Long commentId;
    
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;
    
    @Column(name = "image_alt", length = 200)
    private String imageAlt;
    
    @Column(name = "image_width")
    private Integer imageWidth;
    
    @Column(name = "image_height")
    private Integer imageHeight;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    // ========== 关联关系 ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private KnowledgeComment comment;
    
    // ========== 业务方法 ==========
    
    /**
     * 获取图片尺寸信息
     */
    public String getImageSizeInfo() {
        if (imageWidth == null || imageHeight == null) {
            return "";
        }
        return imageWidth + "x" + imageHeight;
    }
    
    /**
     * 检查是否为有效图片
     */
    public boolean isValidImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }
} 