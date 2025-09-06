package com.jiayan.quitsmoking.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

/**
 * 创建用户评分请求DTO
 */
@Data
public class CreateRatingRequest {
    
    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    /**
     * 评分（1-5分）
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能低于1分")
    @Max(value = 5, message = "评分不能高于5分")
    private Integer rating;
    
    /**
     * 评价内容
     */
    @Size(max = 1000, message = "评价内容长度不能超过1000个字符")
    private String comment;
} 