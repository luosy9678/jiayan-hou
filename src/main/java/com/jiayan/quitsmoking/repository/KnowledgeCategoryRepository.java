package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.KnowledgeCategory;
import com.jiayan.quitsmoking.enums.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知识分类数据访问层
 */
@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long>, JpaSpecificationExecutor<KnowledgeCategory> {
    
    /**
     * 根据父分类ID查找子分类
     */
    List<KnowledgeCategory> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    /**
     * 查找顶级分类（parentId为null）
     */
    List<KnowledgeCategory> findByParentIdIsNullOrderBySortOrderAsc();
    
    /**
     * 按层级和排序值查找所有分类（用于管理后台）
     * 排序逻辑：小排序值的顶级分类 + 其子类 → 大排序值的顶级分类 + 其子类
     */
    @Query("SELECT c FROM KnowledgeCategory c ORDER BY " +
           "CASE WHEN c.parentId IS NULL THEN 0 ELSE 1 END, " +
           "CASE WHEN c.parentId IS NULL THEN c.sortOrder ELSE " +
           "(SELECT p.sortOrder FROM KnowledgeCategory p WHERE p.id = c.parentId) END, " +
           "CASE WHEN c.parentId IS NULL THEN 0 ELSE c.sortOrder END")
    List<KnowledgeCategory> findAllOrderByHierarchyAndSort();
    
    /**
     * 按层级和排序值查找所有分类（分页，用于管理后台）
     * 排序逻辑：小排序值的顶级分类 + 其子类 → 大排序值的顶级分类 + 其子类
     */
    @Query("SELECT c FROM KnowledgeCategory c ORDER BY " +
           "CASE WHEN c.parentId IS NULL THEN 0 ELSE 1 END, " +
           "CASE WHEN c.parentId IS NULL THEN c.sortOrder ELSE " +
           "(SELECT p.sortOrder FROM KnowledgeCategory p WHERE p.id = c.parentId) END, " +
           "CASE WHEN c.parentId IS NULL THEN 0 ELSE c.sortOrder END")
    Page<KnowledgeCategory> findAllOrderByHierarchyAndSort(Pageable pageable);
    
    /**
     * 根据访问权限和状态查找分类
     */
    List<KnowledgeCategory> findByAccessLevelAndIsActiveOrderBySortOrderAsc(AccessLevel accessLevel, Boolean isActive);
    
    /**
     * 根据访问权限和状态分页查找分类
     */
    Page<KnowledgeCategory> findByAccessLevelAndIsActiveOrderBySortOrderAsc(AccessLevel accessLevel, Boolean isActive, Pageable pageable);
    
    /**
     * 根据会员权限查找分类
     */
    List<KnowledgeCategory> findByMemberOnlyAndIsActiveOrderBySortOrderAsc(Boolean memberOnly, Boolean isActive);
    
    /**
     * 查找所有启用的分类
     */
    List<KnowledgeCategory> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    
    /**
     * 根据名称查找分类
     */
    Optional<KnowledgeCategory> findByName(String name);
    
    /**
     * 根据名称模糊查找分类（分页）
     */
    Page<KnowledgeCategory> findByNameContainingOrderBySortOrderAsc(String name, Pageable pageable);
    
    /**
     * 根据名称和状态模糊查找分类（分页）
     */
    Page<KnowledgeCategory> findByNameContainingAndIsActiveOrderBySortOrderAsc(String name, Boolean isActive, Pageable pageable);
    
    /**
     * 根据状态查找分类（分页）
     */
    Page<KnowledgeCategory> findByIsActiveOrderBySortOrderAsc(Boolean isActive, Pageable pageable);
    
    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据父分类ID统计子分类数量
     */
    long countByParentId(Long parentId);
    
    /**
     * 查找指定分类下的所有子分类（递归）
     */
    @Query("SELECT c FROM KnowledgeCategory c WHERE c.parentId IN " +
           "(SELECT c2.id FROM KnowledgeCategory c2 WHERE c2.parentId = :parentId) " +
           "ORDER BY c.sortOrder ASC")
    List<KnowledgeCategory> findGrandchildrenByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查找分类的完整路径
     */
    @Query("SELECT c FROM KnowledgeCategory c WHERE c.id IN " +
           "(SELECT c2.parentId FROM KnowledgeCategory c2 WHERE c2.id = :categoryId) " +
           "ORDER BY c.sortOrder ASC")
    List<KnowledgeCategory> findAncestorsByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 更新分类排序
     */
    @Modifying
    @Query("UPDATE KnowledgeCategory c SET c.sortOrder = :sortOrder WHERE c.id = :categoryId")
    void updateSortOrder(@Param("categoryId") Long categoryId, @Param("sortOrder") Integer sortOrder);
    
    /**
     * 批量更新分类状态
     */
    @Modifying
    @Query("UPDATE KnowledgeCategory c SET c.isActive = :isActive WHERE c.id IN :categoryIds")
    void updateStatusByIds(@Param("categoryIds") List<Long> categoryIds, @Param("isActive") Boolean isActive);
    
    /**
     * 根据用户会员等级查找可访问的分类
     */
    @Query("SELECT c FROM KnowledgeCategory c WHERE c.isActive = true AND " +
           "(c.accessLevel = 'free' OR " +
           "(c.accessLevel = 'member' AND :memberLevel IN ('member', 'premium')) OR " +
           "(c.accessLevel = 'premium' AND :memberLevel = 'premium')) " +
           "ORDER BY c.sortOrder ASC")
    List<KnowledgeCategory> findAccessibleCategoriesByMemberLevel(@Param("memberLevel") String memberLevel);
    
    /**
     * 查找热门分类（根据文章数量排序）
     */
    @Query("SELECT c FROM KnowledgeCategory c WHERE c.isActive = true " +
           "ORDER BY (SELECT COUNT(a) FROM KnowledgeArticle a WHERE a.categoryId = c.id AND a.status = 'published') DESC")
    List<KnowledgeCategory> findPopularCategories();
} 