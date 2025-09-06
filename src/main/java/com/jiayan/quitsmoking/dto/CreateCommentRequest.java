package com.jiayan.quitsmoking.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 创建评论请求DTO
 */
@Data
public class CreateCommentRequest {
    
    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    /**
     * 父评论ID（回复时使用）
     */
    private Long parentId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容长度不能超过2000个字符")
    private String content;
    
    /**
     * 图片列表
     */
    private List<CommentImageRequest> images;
    
    /**
     * 评论图片请求DTO
     */
    @Data
    public static class CommentImageRequest {
        
        /**
         * 图片URL
         */
        @NotBlank(message = "图片URL不能为空")
        private String imageUrl;
        
        /**
         * 图片描述
         */
        @Size(max = 200, message = "图片描述长度不能超过200个字符")
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