package com.jiayan.quitsmoking.dto;

import com.jiayan.quitsmoking.enums.CommentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应DTO
 */
@Data
public class CommentResponse {
    
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 父评论ID
     */
    private Long parentId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 是否有用
     */
    private Boolean isHelpful;
    
    /**
     * 评论状态
     */
    private CommentStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 图片列表
     */
    private List<CommentImageResponse> images;
    
    /**
     * 回复列表
     */
    private List<CommentResponse> replies;
    
    /**
     * 评论图片响应DTO
     */
    @Data
    public static class CommentImageResponse {
        
        /**
         * 图片ID
         */
        private Long id;
        
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
        
        /**
         * 排序顺序
         */
        private Integer sortOrder;
    }
} 