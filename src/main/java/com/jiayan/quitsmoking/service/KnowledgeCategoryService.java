package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.entity.KnowledgeCategory;
import com.jiayan.quitsmoking.enums.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 知识分类服务接口
 */
public interface KnowledgeCategoryService {
    
    /**
     * 创建分类
     */
    KnowledgeCategory createCategory(KnowledgeCategory category);
    
    /**
     * 更新分类
     */
    KnowledgeCategory updateCategory(Long categoryId, KnowledgeCategory category);
    
    /**
     * 根据ID获取分类
     */
    KnowledgeCategory getCategoryById(Long categoryId);
    
    /**
     * 根据名称获取分类
     */
    KnowledgeCategory getCategoryByName(String name);
    
    /**
     * 获取所有顶级分类
     */
    List<KnowledgeCategory> getRootCategories();
    
    /**
     * 获取指定分类的子分类
     */
    List<KnowledgeCategory> getChildCategories(Long parentId);
    
    /**
     * 获取分类的完整层级路径
     */
    List<KnowledgeCategory> getCategoryPath(Long categoryId);
    
    /**
     * 获取分类树结构
     */
    List<KnowledgeCategory> getCategoryTree();
    
    /**
     * 分页获取分类列表
     */
    Page<KnowledgeCategory> getCategories(Pageable pageable);
    
    /**
     * 按层级和排序值分页获取分类列表（用于管理后台）
     * 排序逻辑：小排序值的顶级分类 + 其子类 → 大排序值的顶级分类 + 其子类
     */
    Page<KnowledgeCategory> getCategoriesOrderByHierarchyAndSort(Pageable pageable);
    
    /**
     * 分页获取分类列表（支持筛选）
     */
    Page<KnowledgeCategory> getCategoriesWithFilters(String name, Boolean isActive, Pageable pageable);
    
    /**
     * 根据访问权限获取分类
     */
    List<KnowledgeCategory> getCategoriesByAccessLevel(AccessLevel accessLevel);
    
    /**
     * 根据访问权限分页获取分类
     */
    Page<KnowledgeCategory> getCategoriesByAccessLevel(AccessLevel accessLevel, Pageable pageable);
    
    /**
     * 根据用户会员等级获取可访问的分类
     */
    List<KnowledgeCategory> getAccessibleCategories(String memberLevel, Boolean isPremiumMember);
    
    /**
     * 获取热门分类
     */
    List<KnowledgeCategory> getPopularCategories();
    
    /**
     * 检查分类名称是否存在
     */
    boolean isCategoryNameExists(String name);
    
    /**
     * 检查分类是否可以删除
     */
    boolean canDeleteCategory(Long categoryId);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);
    
    /**
     * 批量更新分类状态
     */
    void updateCategoriesStatus(List<Long> categoryIds, Boolean isActive);
    
    /**
     * 更新分类排序
     */
    void updateCategorySortOrder(Long categoryId, Integer sortOrder);
    
    /**
     * 移动分类到新的父分类
     */
    void moveCategory(Long categoryId, Long newParentId);
    
    /**
     * 获取分类统计信息
     */
    long getCategoryCount();
    
    /**
     * 获取指定分类下的文章数量
     */
    long getArticleCountByCategory(Long categoryId);
    
    /**
     * 验证分类访问权限
     */
    boolean canUserAccessCategory(Long categoryId, String memberLevel, Boolean isPremiumMember);
} 