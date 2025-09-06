package com.jiayan.quitsmoking.entity;

import com.jiayan.quitsmoking.enums.BlockType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识内容块实体类
 */
@Entity
@Table(name = "knowledge_content_blocks")
@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeContentBlock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "article_id", nullable = false)
    private Long articleId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "block_type", length = 20, nullable = false)
    private BlockType blockType;
    
    @Column(name = "content_order", nullable = false)
    private Integer contentOrder;
    
    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "image_alt", length = 200)
    private String imageAlt;
    
    @Column(name = "image_width")
    private Integer imageWidth;
    
    @Column(name = "image_height")
    private Integer imageHeight;
    
    // ========== 关联关系 ==========
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private KnowledgeArticle article;
    
    // ========== 业务方法 ==========
    
    /**
     * 检查是否为文本块
     */
    public boolean isTextBlock() {
        return blockType == BlockType.TEXT;
    }
    
    /**
     * 检查是否为图片块
     */
    public boolean isImageBlock() {
        return blockType == BlockType.IMAGE;
    }
    
    /**
     * 获取内容摘要
     */
    public String getContentSummary() {
        if (isTextBlock()) {
            if (textContent == null || textContent.length() <= 100) {
                return textContent;
            }
            return textContent.substring(0, 100) + "...";
        }
        
        if (isImageBlock()) {
            return imageAlt != null ? imageAlt : "图片";
        }
        
        return "";
    }
    
    /**
     * 获取图片尺寸信息
     */
    public String getImageSizeInfo() {
        if (!isImageBlock() || imageWidth == null || imageHeight == null) {
            return "";
        }
        return imageWidth + "x" + imageHeight;
    }
} 