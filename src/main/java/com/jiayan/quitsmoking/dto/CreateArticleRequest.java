package com.jiayan.quitsmoking.dto;

import com.jiayan.quitsmoking.entity.KnowledgeContentBlock;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 创建知识文章请求DTO
 */
@Data
public class CreateArticleRequest {
    
    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    private String title;
    
    /**
     * 文章内容
     */
    @Size(max = 60000, message = "文章内容长度不能超过60000个字符")
    private String content;
    
    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    /**
     * 出处/来源
     */
    @Size(max = 500, message = "出处长度不能超过500个字符")
    private String source;
    
    /**
     * 内容块列表
     */
    private List<ContentBlockRequest> contentBlocks;
    
    /**
     * 内容块请求DTO
     */
    @Data
    public static class ContentBlockRequest {
        
        /**
         * 内容类型
         */
        @NotNull(message = "内容类型不能为空")
        private String blockType;
        
        /**
         * 内容顺序
         */
        @NotNull(message = "内容顺序不能为空")
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