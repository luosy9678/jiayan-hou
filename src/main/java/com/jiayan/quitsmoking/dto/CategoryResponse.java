package com.jiayan.quitsmoking.dto;

import com.jiayan.quitsmoking.enums.AccessLevel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识分类响应DTO
 */
@Data
public class CategoryResponse {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
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
    private AccessLevel accessLevel;
    
    /**
     * 是否仅会员可访问
     */
    private Boolean memberOnly;
    
    /**
     * 图标文件名
     */
    private String iconName;
    
    /**
     * 图标URL
     */
    private String iconUrl;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 子分类列表
     */
    private List<CategoryResponse> children;
    
    /**
     * 文章数量
     */
    private Long articleCount;
    
    /**
     * 父分类名称
     */
    private String parentName;
}