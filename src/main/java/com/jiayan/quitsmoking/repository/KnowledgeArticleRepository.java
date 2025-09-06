package com.jiayan.quitsmoking.repository;

import com.jiayan.quitsmoking.entity.KnowledgeArticle;
import com.jiayan.quitsmoking.enums.ArticleStatus;
import com.jiayan.quitsmoking.enums.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 知识文章数据访问层
 */
@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long>, JpaSpecificationExecutor<KnowledgeArticle> {
    
    /**
     * 根据分类ID查找文章
     */
    Page<KnowledgeArticle> findByCategoryIdAndStatusAndIsDeletedFalse(Long categoryId, ArticleStatus status, Pageable pageable);
    
    /**
     * 根据作者ID查找文章
     */
    Page<KnowledgeArticle> findByAuthorIdAndStatusAndIsDeletedFalse(Long authorId, ArticleStatus status, Pageable pageable);
    
    /**
     * 根据审核状态查找文章
     */
    Page<KnowledgeArticle> findByAuditStatus(AuditStatus auditStatus, Pageable pageable);
    
    /**
     * 查找待审核的文章
     */
    Page<KnowledgeArticle> findByAuditStatusAndStatus(AuditStatus auditStatus, ArticleStatus status, Pageable pageable);
    
    /**
     * 查找已发布的文章
     */
    Page<KnowledgeArticle> findByStatusAndIsDeletedFalseOrderByPublishTimeDesc(ArticleStatus status, Pageable pageable);
    
    /**
     * 根据标题搜索文章
     */
    Page<KnowledgeArticle> findByTitleContainingAndStatusAndIsDeletedFalse(String title, ArticleStatus status, Pageable pageable);
    
    /**
     * 查找热门文章（按浏览次数排序）
     */
    Page<KnowledgeArticle> findByStatusAndIsDeletedFalseOrderByViewCountDesc(ArticleStatus status, Pageable pageable);
    
    /**
     * 查找高评分文章（按评分排序）
     */
    Page<KnowledgeArticle> findByStatusAndIsDeletedFalseOrderByRatingScoreDesc(ArticleStatus status, Pageable pageable);
    
    /**
     * 根据分类ID统计文章数量
     */
    long countByCategoryIdAndStatusAndIsDeletedFalse(Long categoryId, ArticleStatus status);
    
    /**
     * 根据作者ID统计文章数量
     */
    long countByAuthorIdAndStatusAndIsDeletedFalse(Long authorId, ArticleStatus status);
    
    /**
     * 统计待审核文章数量
     */
    long countByAuditStatus(AuditStatus auditStatus);
    
    /**
     * 查找指定时间范围内发布的文章
     */
    @Query("SELECT a FROM KnowledgeArticle a WHERE a.status = :status AND a.isDeleted = false " +
           "AND a.publishTime BETWEEN :startTime AND :endTime " +
           "ORDER BY a.publishTime DESC")
    Page<KnowledgeArticle> findByPublishTimeBetween(@Param("status") ArticleStatus status,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   Pageable pageable);
    
    /**
     * 查找用户有权限查看的文章
     */
    @Query("SELECT a FROM KnowledgeArticle a WHERE a.categoryId IN " +
           "(SELECT c.id FROM KnowledgeCategory c WHERE c.isActive = true AND " +
           "(c.accessLevel = 'free' OR " +
           "(c.accessLevel = 'member' AND :memberLevel IN ('member', 'premium')) OR " +
           "(c.accessLevel = 'premium' AND :memberLevel = 'premium'))) " +
           "AND a.status = :status AND a.isDeleted = false " +
           "ORDER BY a.publishTime DESC")
    Page<KnowledgeArticle> findAccessibleArticlesByMemberLevel(@Param("memberLevel") String memberLevel,
                                                              @Param("status") ArticleStatus status,
                                                              Pageable pageable);
    
    /**
     * 更新文章浏览次数
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.viewCount = a.viewCount + 1 WHERE a.id = :articleId")
    void incrementViewCount(@Param("articleId") Long articleId);
    
    /**
     * 更新文章点赞数
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.likeCount = a.likeCount + :increment WHERE a.id = :articleId")
    void updateLikeCount(@Param("articleId") Long articleId, @Param("increment") Integer increment);
    
    /**
     * 更新文章评分
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.ratingScore = :ratingScore, a.ratingCount = :ratingCount WHERE a.id = :articleId")
    void updateRating(@Param("articleId") Long articleId, @Param("ratingScore") Double ratingScore, @Param("ratingCount") Integer ratingCount);
    
    /**
     * 批量更新文章状态
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.status = :status WHERE a.id IN :articleIds")
    void updateStatusByIds(@Param("articleIds") List<Long> articleIds, @Param("status") ArticleStatus status);
    
    /**
     * 批量更新审核状态
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.auditStatus = :auditStatus, a.auditedBy = :auditorId, a.auditedAt = :auditTime WHERE a.id IN :articleIds")
    void updateAuditStatusByIds(@Param("articleIds") List<Long> articleIds,
                               @Param("auditStatus") AuditStatus auditStatus,
                               @Param("auditorId") Long auditorId,
                               @Param("auditTime") LocalDateTime auditTime);
    
    /**
     * 软删除文章
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.isDeleted = true, a.deletedBy = :deletedBy, a.deletedAt = :deletedAt WHERE a.id = :articleId")
    void softDelete(@Param("articleId") Long articleId, @Param("deletedBy") Long deletedBy, @Param("deletedAt") LocalDateTime deletedAt);
    
    /**
     * 恢复软删除的文章
     */
    @Modifying
    @Query("UPDATE KnowledgeArticle a SET a.isDeleted = false, a.deletedBy = null, a.deletedAt = null WHERE a.id = :articleId")
    void restore(@Param("articleId") Long articleId);
    
    /**
     * 查找被禁用的文章
     */
    Page<KnowledgeArticle> findByStatusAndIsDeletedFalse(ArticleStatus status, Pageable pageable);
    
    /**
     * 根据禁用原因查找文章
     */
    @Query("SELECT a FROM KnowledgeArticle a WHERE a.bannedReason LIKE %:reason% AND a.status = 'banned' AND a.isDeleted = false")
    Page<KnowledgeArticle> findByBannedReasonContaining(@Param("reason") String reason, Pageable pageable);
} 