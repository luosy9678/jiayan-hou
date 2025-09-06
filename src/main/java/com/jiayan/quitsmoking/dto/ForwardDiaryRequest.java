package com.jiayan.quitsmoking.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 转发日记到论坛请求DTO
 */
@Data
public class ForwardDiaryRequest {
    
    @NotBlank(message = "版块不能为空")
    @Pattern(regexp = "^(quit_experience|success_story|help_support|health_tips|motivation)$", 
             message = "无效的版块类型")
    private String section;
} 