package com.jiayan.quitsmoking.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户评分响应DTO
 */
@Data
public class RatingResponse {
    
    /**
     * 评分ID
     */
    private Long id;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 文章标题
     */
    private String articleTitle;
    
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
     * 评分（1-5分）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 