package com.jiayan.quitsmoking.dto;

import com.jiayan.quitsmoking.enums.ArticleStatus;
import com.jiayan.quitsmoking.enums.AuditStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识文章响应DTO
 */
@Data
public class ArticleResponse {
    
    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     */
    private String content;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 作者名称
     */
    private String authorName;
    
    /**
     * 出处/来源
     */
    private String source;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 点踩数
     */
    private Integer dislikeCount;
    
    /**
     * 平均评分
     */
    private BigDecimal ratingScore;
    
    /**
     * 评分人数
     */
    private Integer ratingCount;
    
    /**
     * 文章状态
     */
    private ArticleStatus status;
    
    /**
     * 审核状态
     */
    private AuditStatus auditStatus;
    
    /**
     * 审核意见
     */
    private String auditComment;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 最后编辑时间
     */
    private LocalDateTime lastEditTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 内容块列表
     */
    private List<ContentBlockResponse> contentBlocks;
    
    /**
     * 内容块响应DTO
     */
    @Data
    public static class ContentBlockResponse {
        
        /**
         * 内容块ID
         */
        private Long id;
        
        /**
         * 内容类型
         */
        private String blockType;
        
        /**
         * 内容顺序
         */
        private Integer contentOrder;
        
        /**
         * 文本内容
         */
        private String textContent;
        
        /**
         * 图片URL
         */
        private String imageUrl;
        
        /**
         * 图片描述
         */
        private String imageAlt;
        
        /**
         * 图片宽度
         */
        private Integer imageWidth;
        
        /**
         * 图片高度
         */
        private Integer imageHeight;
    }
} 