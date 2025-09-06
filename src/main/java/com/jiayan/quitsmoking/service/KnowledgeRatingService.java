package com.jiayan.quitsmoking.service;

import com.jiayan.quitsmoking.entity.KnowledgeRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 用户评分服务接口
 */
public interface KnowledgeRatingService {
    
    /**
     * 创建评分
     */
    KnowledgeRating createRating(KnowledgeRating rating);
    
    /**
     * 更新评分
     */
    KnowledgeRating updateRating(Long ratingId, Integer rating, String comment);
    
    /**
     * 根据ID获取评分
     */
    KnowledgeRating getRatingById(Long ratingId);
    
    /**
     * 根据文章ID和用户ID获取评分
     */
    KnowledgeRating getRatingByArticleAndUser(Long articleId, Long userId);
    
    /**
     * 根据文章ID获取评分列表
     */
    Page<KnowledgeRating> getRatingsByArticle(Long articleId, Pageable pageable);
    
    /**
     * 根据用户ID获取评分列表
     */
    Page<KnowledgeRating> getRatingsByUser(Long userId, Pageable pageable);
    
    /**
     * 根据评分值获取评分列表
     */
    List<KnowledgeRating> getRatingsByValue(Integer rating);
    
    /**
     * 根据文章ID获取高评分（4-5分）
     */
    List<KnowledgeRating> getHighRatingsByArticle(Long articleId);
    
    /**
     * 根据文章ID获取低评分（1-2分）
     */
    List<KnowledgeRating> getLowRatingsByArticle(Long articleId);
    
    /**
     * 计算文章的平均评分
     */
    Double calculateAverageRating(Long articleId);
    
    /**
     * 获取文章的评分分布
     */
    Map<Integer, Long> getRatingDistribution(Long articleId);
    
    /**
     * 获取最新评分
     */
    Page<KnowledgeRating> getLatestRatings(Pageable pageable);
    
    /**
     * 获取指定评分范围的最新评分
     */
    Page<KnowledgeRating> getRatingsByRange(Integer minRating, Integer maxRating, Pageable pageable);
    
    /**
     * 获取有评论的评分
     */
    Page<KnowledgeRating> getRatingsWithComments(Pageable pageable);
    
    /**
     * 根据评论内容搜索评分
     */
    Page<KnowledgeRating> searchRatingsByComment(String keyword, Pageable pageable);
    
    /**
     * 删除用户对指定文章的评分
     */
    void deleteRating(Long articleId, Long userId);
    
    /**
     * 检查用户是否已对文章评分
     */
    boolean hasUserRated(Long articleId, Long userId);
    
    /**
     * 获取评分统计信息
     */
    long getRatingCount();
    
    /**
     * 获取指定文章的评分数量
     */
    long getRatingCountByArticle(Long articleId);
    
    /**
     * 获取指定用户的评分数量
     */
    long getRatingCountByUser(Long userId);
    
    /**
     * 获取指定评分值的数量
     */
    long getRatingCountByValue(Integer rating);
    
    /**
     * 获取指定文章指定评分的数量
     */
    long getRatingCountByArticleAndValue(Long articleId, Integer rating);
    
    /**
     * 验证评分值是否有效
     */
    boolean isValidRating(Integer rating);
    
    /**
     * 更新文章的总评分和评分人数
     */
    void updateArticleRatingStats(Long articleId);
    
    /**
     * 获取热门评分文章
     */
    List<Long> getTopRatedArticleIds(int limit);
    
    /**
     * 获取用户评分趋势
     */
    Map<String, Double> getUserRatingTrend(Long userId, int days);
} 