package com.jiayan.quitsmoking.service.impl;

import com.jiayan.quitsmoking.entity.KnowledgeCategory;
import com.jiayan.quitsmoking.enums.AccessLevel;
import com.jiayan.quitsmoking.repository.KnowledgeCategoryRepository;
import com.jiayan.quitsmoking.repository.KnowledgeArticleRepository;
import com.jiayan.quitsmoking.service.KnowledgeCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeCategoryServiceImpl implements KnowledgeCategoryService {
    
    private final KnowledgeCategoryRepository categoryRepository;
    private final KnowledgeArticleRepository articleRepository;
    
    @Override
    public KnowledgeCategory createCategory(KnowledgeCategory category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public KnowledgeCategory updateCategory(Long categoryId, KnowledgeCategory category) {
        category.setId(categoryId);
        return categoryRepository.save(category);
    }
    
    @Override
    public KnowledgeCategory getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }
    
    @Override
    public KnowledgeCategory getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }
    
    @Override
    public List<KnowledgeCategory> getRootCategories() {
        return categoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
    }
    
    @Override
    public List<KnowledgeCategory> getChildCategories(Long parentId) {
        return categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }
    
    @Override
    public List<KnowledgeCategory> getCategoryPath(Long categoryId) {
        // 简化实现
        KnowledgeCategory category = getCategoryById(categoryId);
        return category != null ? List.of(category) : List.of();
    }
    
    @Override
    public List<KnowledgeCategory> getCategoryTree() {
        return categoryRepository.findAll();
    }
    
    @Override
    public Page<KnowledgeCategory> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    @Override
    public Page<KnowledgeCategory> getCategoriesOrderByHierarchyAndSort(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    @Override
    public Page<KnowledgeCategory> getCategoriesWithFilters(String name, Boolean isActive, Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    @Override
    public List<KnowledgeCategory> getCategoriesByAccessLevel(AccessLevel accessLevel) {
        return categoryRepository.findAll(); // 临时简化
    }
    
    @Override
    public Page<KnowledgeCategory> getCategoriesByAccessLevel(AccessLevel accessLevel, Pageable pageable) {
        return categoryRepository.findAll(pageable); // 临时简化
    }
    
    @Override
    public List<KnowledgeCategory> getAccessibleCategories(String memberLevel, Boolean isPremiumMember) {
        return categoryRepository.findAll();
    }
    
    @Override
    public List<KnowledgeCategory> getPopularCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public boolean isCategoryNameExists(String name) {
        return categoryRepository.findByName(name).isPresent();
    }
    
    @Override
    public boolean canDeleteCategory(Long categoryId) {
        return true;
    }
    
    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
    
    @Override
    public void updateCategoriesStatus(List<Long> categoryIds, Boolean isActive) {
        // 简化实现
    }
    
    @Override
    public void updateCategorySortOrder(Long categoryId, Integer sortOrder) {
        // 简化实现
    }
    
    @Override
    public void moveCategory(Long categoryId, Long newParentId) {
        // 简化实现
    }
    
    @Override
    public long getCategoryCount() {
        return categoryRepository.count();
    }
    
    @Override
    public long getArticleCountByCategory(Long categoryId) {
        try {
            return articleRepository.countByCategoryIdAndStatusAndIsDeletedFalse(categoryId, null);
        } catch (Exception e) {
            log.warn("获取分类文章数量失败: categoryId={}, error={}", categoryId, e.getMessage());
            return 0L;
        }
    }
    
    @Override
    public boolean canUserAccessCategory(Long categoryId, String memberLevel, Boolean isPremiumMember) {
        return true;
    }
}