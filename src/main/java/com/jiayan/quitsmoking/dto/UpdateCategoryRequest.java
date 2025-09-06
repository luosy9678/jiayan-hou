package com.jiayan.quitsmoking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jiayan.quitsmoking.enums.AccessLevel;
import com.jiayan.quitsmoking.util.AccessLevelDeserializer;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 更新知识分类请求DTO
 */
@Data
public class UpdateCategoryRequest {
    
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;
    
    /**
     * 分类描述
     */
    @Size(max = 1000, message = "分类描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 访问权限级别
     */
    @NotNull(message = "访问权限级别不能为空")
    @JsonDeserialize(using = AccessLevelDeserializer.class)
    private AccessLevel accessLevel;
    
    /**
     * 是否仅会员可访问
     */
    private Boolean memberOnly;
    
    /**
     * 图标文件名
     */
    @Size(max = 100, message = "图标文件名长度不能超过100个字符")
    private String iconName;
}