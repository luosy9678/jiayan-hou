package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.entity.KnowledgeArticle;
import com.jiayan.quitsmoking.entity.KnowledgeContentBlock;
import com.jiayan.quitsmoking.enums.ArticleStatus;
import com.jiayan.quitsmoking.enums.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 知识文章服务接口
 */
public interface KnowledgeArticleService {
    
    /**
     * 创建文章
     */
    KnowledgeArticle createArticle(KnowledgeArticle article, List<KnowledgeContentBlock> contentBlocks);
    
    /**
     * 更新文章
     */
    KnowledgeArticle updateArticle(Long articleId, KnowledgeArticle article, List<KnowledgeContentBlock> contentBlocks);
    
    /**
     * 根据ID获取文章
     */
    KnowledgeArticle getArticleById(Long articleId);
    
    /**
     * 根据ID获取文章（包含内容块）
     */
    KnowledgeArticle getArticleWithContent(Long articleId);
    
    /**
     * 分页获取文章列表
     */
    Page<KnowledgeArticle> getArticles(Pageable pageable);
    
    /**
     * 根据分类ID获取文章
     */
    Page<KnowledgeArticle> getArticlesByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 根据作者ID获取文章
     */
    Page<KnowledgeArticle> getArticlesByAuthor(Long authorId, Pageable pageable);
    
    /**
     * 根据状态获取文章
     */
    Page<KnowledgeArticle> getArticlesByStatus(ArticleStatus status, Pageable pageable);
    
    /**
     * 根据审核状态获取文章
     */
    Page<KnowledgeArticle> getArticlesByAuditStatus(AuditStatus auditStatus, Pageable pageable);
    
    /**
     * 搜索文章
     */
    Page<KnowledgeArticle> searchArticles(String keyword, Pageable pageable);
    
    /**
     * 获取热门文章
     */
    Page<KnowledgeArticle> getPopularArticles(Pageable pageable);
    
    /**
     * 获取高评分文章
     */
    Page<KnowledgeArticle> getHighRatedArticles(Pageable pageable);
    
    /**
     * 获取最新文章
     */
    Page<KnowledgeArticle> getLatestArticles(Pageable pageable);
    
    /**
     * 根据用户权限获取可访问的文章
     */
    Page<KnowledgeArticle> getAccessibleArticles(String memberLevel, Boolean isPremiumMember, Pageable pageable);
    
    /**
     * 提交文章审核
     */
    void submitForAudit(Long articleId, Long authorId);
    
    /**
     * 审核文章
     */
    void auditArticle(Long articleId, AuditStatus auditStatus, String comment, Long auditorId);
    
    /**
     * 发布文章
     */
    void publishArticle(Long articleId, Long publisherId);
    
    /**
     * 禁用文章
     */
    void banArticle(Long articleId, String reason, Long adminId);
    
    /**
     * 恢复文章
     */
    void restoreArticle(Long articleId, Long adminId);
    
    /**
     * 软删除文章
     */
    void softDeleteArticle(Long articleId, Long operatorId, String reason);
    
    /**
     * 恢复软删除的文章
     */
    void restoreSoftDeletedArticle(Long articleId, Long operatorId);
    
    /**
     * 增加文章浏览次数
     */
    void incrementViewCount(Long articleId);
    
    /**
     * 更新文章点赞数
     */
    void updateLikeCount(Long articleId, Integer increment);
    
    /**
     * 更新文章评分
     */
    void updateRating(Long articleId, Double ratingScore, Integer ratingCount);
    
    /**
     * 检查用户是否有权限查看文章
     */
    boolean canUserAccessArticle(Long articleId, String memberLevel, Boolean isPremiumMember);
    
    /**
     * 检查用户是否有权限编辑文章
     */
    boolean canUserEditArticle(Long articleId, Long userId);
    
    /**
     * 获取文章统计信息
     */
    long getArticleCount();
    
    /**
     * 获取待审核文章数量
     */
    long getPendingAuditCount();
    
    /**
     * 获取已发布文章数量
     */
    long getPublishedArticleCount();
    
    /**
     * 批量更新文章状态
     */
    void batchUpdateArticleStatus(List<Long> articleIds, ArticleStatus status);
    
    /**
     * 批量更新审核状态
     */
    void batchUpdateAuditStatus(List<Long> articleIds, AuditStatus auditStatus, Long auditorId);
    
    Page<KnowledgeArticle> getArticlesCombined(String keyword, Long categoryId, ArticleStatus status, AuditStatus auditStatus, Pageable pageable);
} 